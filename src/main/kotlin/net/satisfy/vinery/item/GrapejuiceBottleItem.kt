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
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.util.VineryGrapeRegistry


class GrapejuiceBottleItem(properties: Properties?, juiceModelName: String) : Item(properties!!), PolymerItem {

    private val juiceModel: PolymerModelData = PolymerResourcePackUtils.requestModel(Items.GLASS_BOTTLE, ResourceLocation(MODID, "item/$juiceModelName"))

    override fun finishUsingItem(itemStack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        if (livingEntity is ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(livingEntity, itemStack)
            livingEntity.awardStat(Stats.ITEM_USED.get(this))
        }

        if (!level.isClientSide) {
            livingEntity.removeEffect(MobEffects.POISON)
        }

        if (livingEntity is Player && !livingEntity.abilities.instabuild) {
            itemStack.shrink(1)
            val itemStack2: ItemStack = ItemStack(VineryGrapeRegistry.WINE_BOTTLE.asItem())
            if (!livingEntity.inventory.add(itemStack2)) {
                livingEntity.drop(itemStack2, false)
            }
        }

        return if (itemStack.isEmpty) ItemStack.EMPTY else itemStack
    }


    override fun getUseDuration(itemStack: ItemStack): Int = 40

    override fun getUseAnimation(itemStack: ItemStack): UseAnim = UseAnim.DRINK

    override fun getDrinkingSound(): SoundEvent = SoundEvents.HONEY_DRINK

    override fun getEatingSound(): SoundEvent = SoundEvents.HONEY_DRINK

    override fun use(level: Level, player: Player, interactionHand: InteractionHand): InteractionResultHolder<ItemStack>? = ItemUtils.startUsingInstantly(level, player, interactionHand)

    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayer?): Item = Items.GLASS_BOTTLE

    override fun getPolymerCustomModelData(itemStack: ItemStack?, player: ServerPlayer?): Int = juiceModel.value()
}