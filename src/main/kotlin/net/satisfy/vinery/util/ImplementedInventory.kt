package net.satisfy.vinery.util

import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.world.ContainerHelper
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.satisfy.vinery.util.ImplementedInventory
import kotlin.Boolean
import kotlin.Int
import kotlin.IntArray
import kotlin.invoke

@FunctionalInterface
interface ImplementedInventory : WorldlyContainer {
    val items: NonNullList<ItemStack?>

    override fun setChanged() {}

    override fun getContainerSize(): Int = items.size

    override fun isEmpty(): Boolean {
        for (i in 0 until this.containerSize) {
            val stack = this.getItem(i)
            if (!stack.isEmpty) return false
        }
        return true
    }

    override fun getItem(slot: Int): ItemStack = items[slot] ?: ItemStack.EMPTY

    override fun removeItem(slot: Int, count: Int): ItemStack {
        val result = ContainerHelper.removeItem(this.items, slot, count)
        if (!result.isEmpty) {
            this.setChanged()
        }
        return result
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack = ContainerHelper.takeItem(this.items, slot)

    override fun setItem(slot: Int, stack: ItemStack) {
        items[slot] = stack
        if (stack.count > this.maxStackSize) {
            stack.count = this.maxStackSize
        }
        this.setChanged()
    }

    override fun clearContent() = items.clear()

    override fun getSlotsForFace(side: Direction): IntArray {
        val result = IntArray(items.size)
        var i = 0
        while (i < result.size) {
            result[i] = i++
        }
        return result
    }

    override fun canPlaceItemThroughFace(slot: Int, stack: ItemStack, side: Direction?): Boolean = true

    override fun canTakeItemThroughFace(slot: Int, stack: ItemStack, side: Direction): Boolean = true

    override fun stillValid(player: Player): Boolean = true

    companion object {
        fun of(items: NonNullList<ItemStack?>?): ImplementedInventory? = if (items == null) null else object : ImplementedInventory { override val items: NonNullList<ItemStack?> = items }
        fun ofSize(size: Int): ImplementedInventory? = of(NonNullList.withSize(size, ItemStack.EMPTY))
    }
}
