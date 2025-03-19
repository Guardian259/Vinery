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
import net.satisfy.vinery.util.VineryGrapeRegistry
import net.satisfy.vinery.util.WineYears
import net.satisfy.vinery.util.WineYears.getEffectDuration
import net.satisfy.vinery.util.WineYears.getEffectLevel
import net.satisfy.vinery.util.WineYears.getWineAge
import net.satisfy.vinery.util.WineYears.hasWineYear
import net.satisfy.vinery.util.WineYears.setWineYear
import org.spongepowered.include.com.google.common.collect.Lists
import java.util.*
import kotlin.math.max

@Suppress("unused")
class DrinkBlockItem(
    block: Block?,
    settings: Properties?,
    itemModelName: String,
    private val baseDuration: Int,
    private val scaleDurationWithAge: Boolean
) : BlockItem(block!!, settings!!), PolymerItem {

    private val itemModel: PolymerModelData = PolymerResourcePackUtils.requestModel(Items.GLASS_BOTTLE, ResourceLocation(
        MODID, "item/$itemModelName")
    )

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.DRINK
    }

    override fun getPlacementState(context: BlockPlaceContext): BlockState? {
        if (!Objects.requireNonNull(context.player)!!.isCrouching) return null
        val blockState = block.getStateForPlacement(context)
        return if (blockState != null && this.canPlace(context, blockState)) blockState else null
    }

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

    override fun appendHoverText(
        stack: ItemStack,
        @Nullable world: Level?,
        tooltip: MutableList<Component>,
        context: TooltipFlag
    ) {
        val effects = if (foodProperties != null) foodProperties!!.effects else Lists.newArrayList()
        if (effects.isEmpty()) {
            tooltip.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY))
        } else {
            for (effectPair in effects) {
                val effectInstance = effectPair.first
                val effect = effectInstance.effect
                val effectName = effect.displayName.string
                val amplifier = max(0.0, getEffectLevel(stack, world).toDouble()).toInt()
                val amplifierRoman = if (amplifier > 0) " " + toRoman(amplifier) else ""
                var durationTicks = if (scaleDurationWithAge) getEffectDuration(stack, world) else baseDuration
                durationTicks = max(0.0, durationTicks.toDouble()).toInt()
                val formattedDuration = formatDuration(durationTicks)
                val tooltipText = "$effectName$amplifierRoman ($formattedDuration)"
                tooltip.add(Component.literal(tooltipText).withStyle(effect.category.tooltipFormatting))
            }
        }
        tooltip.add(Component.empty())
        if (world != null) {
            val age = max(0.0, getWineAge(stack, world).toDouble()).toInt()
            tooltip.add(Component.translatable("tooltip.vinery.age", age).withStyle(ChatFormatting.WHITE))
            tooltip.add(Component.empty())
            val yearsToNextUpgrade = WineYears.YEARS_PER_EFFECT_LEVEL - (age % WineYears.YEARS_PER_EFFECT_LEVEL)
            val daysToNextUpgrade = max(0.0, (yearsToNextUpgrade * WineYears.DAYS_PER_YEAR).toDouble())
                .toInt()
            tooltip.add(
                Component.translatable("tooltip.vinery.next_upgrade", daysToNextUpgrade)
                    .withStyle { style: Style -> style.withColor(TextColor.fromRgb(0x93c47d)) })
        }
    }

    @Suppress("unused")
    override fun finishUsingItem(itemStack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        if (!level.isClientSide) {
            val age = max(0.0, getWineAge(itemStack, level).toDouble()).toInt()
            val duration =
                max(0.0, (if (scaleDurationWithAge) getEffectDuration(itemStack, level) else baseDuration).toDouble())
                    .toInt()
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

    private fun formatDuration(ticks: Int): String {
        val totalSeconds = (max(0.0, ticks.toDouble()) / 20).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        return ItemUtils.startUsingInstantly(level, player, interactionHand)
    }

    override fun onCraftedBy(stack: ItemStack, world: Level, player: Player) {
        super.onCraftedBy(stack, world, player)
        setWineYear(stack, world)
    }

    override fun inventoryTick(stack: ItemStack, world: Level, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)
        if (world != null && hasWineYear(stack)) {
            setWineYear(stack, world)
        }
    }

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

    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayer?): Item = Items.GLASS_BOTTLE

    override fun getPolymerCustomModelData(itemStack: ItemStack?, player: ServerPlayer?): Int = itemModel.value()

}
