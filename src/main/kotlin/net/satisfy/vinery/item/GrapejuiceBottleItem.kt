package net.satisfy.vinery.item

import eu.pb4.polymer.core.api.item.PolymerItem
import eu.pb4.polymer.resourcepack.api.PolymerModelData
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.registry.VineryGrapeRegistry

/**
 * A consumable grape juice bottle that returns an empty wine bottle after use.
 * Implements [PolymerItem] to use a custom model based on a glass bottle.
 * Also removes poison as a minor effect when consumed.
 *
 * @param properties Item properties
 * @param juiceModelName Resource name for the custom juice model
 */
class GrapejuiceBottleItem(properties: Properties?, juiceModelName: String) : Item(properties!!), PolymerItem {

    /** Custom model data for the juice bottle, based on a glass bottle. */
    private val juiceModel: PolymerModelData = PolymerResourcePackUtils.requestModel(Items.GLASS_BOTTLE, ResourceLocation(MODID, "item/$juiceModelName"))

    /**
     * Consumes the juice: triggers stats, returns an empty wine bottle (non-creative only), and removes poison as a minor effect.
     *
     * @return Empty stack if consumed fully, else the original stack
     */
    override fun finishUsingItem(itemStack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        if (livingEntity is ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(livingEntity, itemStack)
            livingEntity.awardStat(Stats.ITEM_USED.get(this))
        }

        if (!level.isClientSide) livingEntity.removeEffect(MobEffects.POISON)

        if (livingEntity is Player && !livingEntity.abilities.instabuild) {
            itemStack.shrink(1)
            val bottle = ItemStack(VineryGrapeRegistry.WINE_BOTTLE.asItem())
            if (!livingEntity.inventory.add(bottle)) livingEntity.drop(bottle, false)
        }

        return if (itemStack.isEmpty) ItemStack.EMPTY else itemStack
    }

    /** Returns the duration of use (40 ticks). */
    override fun getUseDuration(itemStack: ItemStack): Int = 40

    /** Returns the drinking animation. */
    override fun getUseAnimation(itemStack: ItemStack): UseAnim = UseAnim.DRINK

    /** Returns the sound played while drinking (honey drink sound). */
    override fun getDrinkingSound(): SoundEvent = SoundEvents.HONEY_DRINK

    /** Returns the sound played while consuming (honey drink sound). */
    override fun getEatingSound(): SoundEvent = SoundEvents.HONEY_DRINK

    /**
     * Initiates drinking the juice instantly.
     *
     * @return Result of starting to use the item
     */
    override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResultHolder<ItemStack>? =
        ItemUtils.startUsingInstantly(level, player, interactionHand)

    /** Returns the base item for Polymer rendering (glass bottle). */
    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayer?): Item = Items.GLASS_BOTTLE

    /** Returns the custom model data value for this juice type. */
    override fun getPolymerCustomModelData(itemStack: ItemStack?, player: ServerPlayer?): Int = juiceModel.value()
}