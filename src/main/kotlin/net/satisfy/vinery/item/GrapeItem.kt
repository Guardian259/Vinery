package net.satisfy.vinery.item

import eu.pb4.polymer.core.api.item.PolymerItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.satisfy.vinery.util.GrapeType


class GrapeItem(settings: Properties?, val type: GrapeType, private val returnItem: Item) : Item(settings!!), PolymerItem {
    override fun finishUsingItem(stack: ItemStack, world: Level, entityLiving: LivingEntity): ItemStack {
        if (!world.isClientSide() && entityLiving is Player) {
            if (stack.item === this) {
                if (world.getRandom().nextFloat() < CHANCE_OF_GETTING_SEEDS) {
                    val returnStack = ItemStack(returnItem)
                    if (!entityLiving.inventory.add(returnStack)) {
                        entityLiving.drop(returnStack, false)
                    }
                }
            }
        }
        return super.finishUsingItem(stack, world, entityLiving)
    }

    companion object {
        private const val CHANCE_OF_GETTING_SEEDS = 0.2
    }

    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayer?): Item = Items.SWEET_BERRIES
}