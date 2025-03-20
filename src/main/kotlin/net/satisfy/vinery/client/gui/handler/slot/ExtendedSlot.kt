package net.satisfy.vinery.client.gui.handler.slot

import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

class ExtendedSlot(inventory: Container?, index: Int, x: Int, y: Int, private val filter: Predicate<ItemStack>) :
    Slot(inventory!!, index, x, y) {
    override fun mayPlace(stack: ItemStack): Boolean = filter.test(stack)
}
