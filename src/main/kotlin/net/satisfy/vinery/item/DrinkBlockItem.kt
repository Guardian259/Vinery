package net.satisfy.vinery.item

import eu.pb4.polymer.core.api.item.PolymerItem
import eu.pb4.polymer.resourcepack.api.PolymerModelData
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.util.GeneralUtil
import net.satisfy.vinery.registry.VineryGrapeRegistry
import net.satisfy.vinery.util.WineYears
import net.satisfy.vinery.util.WineYears.getEffectDuration
import net.satisfy.vinery.util.WineYears.getEffectLevel
import net.satisfy.vinery.util.WineYears.getWineAge
import net.satisfy.vinery.util.WineYears.hasWineYear
import net.satisfy.vinery.util.WineYears.setWineYear
import org.spongepowered.include.com.google.common.collect.Lists
import java.util.*
import kotlin.math.max

/**
 * A drinkable block item (e.g., wine) that applies effects based on age and returns a wine bottle.
 * Extends [BlockItem] for placement and [PolymerItem] for custom glass bottle visuals.
 *
 * @param block The block this item places
 * @param settings Item properties
 * @param itemModelName Resource name for the custom model
 * @param baseDuration Base effect duration in ticks
 * @param scaleDurationWithAge Whether duration scales with wine age
 */
@Suppress("unused")
class DrinkBlockItem(
    block: Block?,
    settings: Properties?,
    itemModelName: String,
    private val baseDuration: Int,
    private val scaleDurationWithAge: Boolean
) : BlockItem(block!!, settings!!), PolymerItem {

    /** Custom model data for the item, based on a glass bottle. */
    private val itemModel: PolymerModelData = PolymerResourcePackUtils.requestModel(Items.GLASS_BOTTLE, ResourceLocation(
        MODID, "item/$itemModelName")
    )

    /** Returns the drinking animation. */
    override fun getUseAnimation(stack: ItemStack): UseAnim = UseAnim.DRINK

    /**
     * Returns the block state for placement if the player is crouching and placement is valid.
     * Returns null otherwise.
     */
    override fun getPlacementState(context: BlockPlaceContext): BlockState? {
        if (!context.player!!.isCrouching) return null
        val blockState = block.getStateForPlacement(context)
        return if (blockState != null && canPlace(context, blockState)) blockState else null
    }

    //TODO:Complete Reimplementation
//    override fun updateCustomBlockEntityTag(
//        blockPos: BlockPos,
//        level: Level,
//        @Nullable player: Player?,
//        itemStack: ItemStack,
//        blockState: BlockState
//    ): Boolean {
//        if (level.getBlockEntity(blockPos) is StorageBlockEntity) {
//            wineEntity.setStack(0, itemStack.copyWithCount(1))
//        }
//        return super.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState)
//    }

    /**
     * Adds tooltip info: effects with duration/amplifier, wine age, and days to next upgrade.
     */
    override fun appendHoverText(
        stack: ItemStack,
        world: Level?,
        tooltip: MutableList<Component>,
        context: TooltipFlag
    ) {
        val effects = foodProperties?.effects ?: Lists.newArrayList()
        if (effects.isEmpty()) {
            tooltip.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY))
        } else {
            // List each effect with name, amplifier, and duration
            effects.forEach { effectPair ->
                val effect = effectPair.first.effect
                val amplifier = getEffectLevel(stack, world)
                val amplifierRoman = if (amplifier > 0) " ${toRoman(amplifier)}" else ""
                val durationTicks = if (scaleDurationWithAge) getEffectDuration(stack, world) else baseDuration
                val tooltipText = "${effect.displayName.string}$amplifierRoman (${formatDuration(durationTicks)})"
                tooltip.add(Component.literal(tooltipText).withStyle(effect.category.tooltipFormatting))
            }
        }
        tooltip.add(Component.empty())
        // Append age and next upgrade info if world is available
        world?.let {
            val age = getWineAge(stack, it)
            tooltip.add(Component.translatable("tooltip.vinery.age", age).withStyle(ChatFormatting.WHITE))
            tooltip.add(Component.empty())
            val yearsToNextUpgrade = WineYears.YEARS_PER_EFFECT_LEVEL - (age % WineYears.YEARS_PER_EFFECT_LEVEL)
            val daysToNextUpgrade = yearsToNextUpgrade * WineYears.DAYS_PER_YEAR
            tooltip.add(
                Component.translatable("tooltip.vinery.next_upgrade", daysToNextUpgrade)
                    .withStyle { style -> style.withColor(TextColor.fromRgb(0x93c47d)) }
            )
        }
    }

    /**
     * Consumes the item: applies age-based effects on server, shrinks stack, and returns a wine bottle.
     */
    @Suppress("unused")
    override fun finishUsingItem(itemStack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        if (!level.isClientSide) {
            val age = max(0.0, getWineAge(itemStack, level).toDouble()).toInt()
            val duration = max(0.0, (if (scaleDurationWithAge) getEffectDuration(itemStack, level) else baseDuration).toDouble()).toInt()
            val amplifier = max(0.0, getEffectLevel(itemStack, level).toDouble()).toInt()
            val effects = Objects.requireNonNull(foodProperties)!!.effects
            for (effectPair in effects) {
                val effect = effectPair.first.effect
                livingEntity.addEffect(MobEffectInstance(effect, duration, amplifier))
            }
        }
        itemStack.shrink(1)
        return GeneralUtil.convertStackAfterFinishUsing(
            livingEntity,
            itemStack,
            VineryGrapeRegistry.WINE_BOTTLE,
            this
        )
    }

    /** Formats duration in ticks to "mm:ss" format. */
    private fun formatDuration(ticks: Int): String {
        val totalSeconds = (max(0.0, ticks.toDouble()) / 20).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    /** Initiates drinking the item instantly. */
    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> = ItemUtils.startUsingInstantly(level, player, interactionHand)

    /** Sets the wine year when crafted. */
    override fun onCraftedBy(stack: ItemStack, world: Level, player: Player) {
        super.onCraftedBy(stack, world, player)
        setWineYear(stack, world)
    }

    /** Updates the wine year in inventory if already set. */
    override fun inventoryTick(stack: ItemStack, world: Level, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)
        if (world != null && hasWineYear(stack)) setWineYear(stack, world)
    }

    /** Converts amplifier level to Roman numerals (I-X). */
    private fun toRoman(number: Int): String {
        return when (number) {
            0 -> "I"
            1 -> "II"
            2 -> "III"
            3 -> "IV"
            4 -> "V"
            5 -> "VI"
            6 -> "VII"
            7 -> "VIII"
            8 -> "IX"
            9 -> "X"
            else -> number.toString()
        }
    }

    /** Returns the base item for Polymer rendering (glass bottle). */
    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayer?): Item = Items.GLASS_BOTTLE

    /** Returns the custom model data value for this item. */
    override fun getPolymerCustomModelData(itemStack: ItemStack?, player: ServerPlayer?): Int = itemModel.value()
}