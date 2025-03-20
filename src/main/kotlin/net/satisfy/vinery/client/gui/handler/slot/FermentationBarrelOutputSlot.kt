package net.satisfy.vinery.client.gui.handler.slot

import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.satisfy.vinery.block.entity.FermentationBarrelBlockEntity
import kotlin.math.min

class FermentationBarrelOutputSlot(private val player: Player, inventory: Container?, index: Int, x: Int, y: Int) :
    Slot(inventory!!, index, x, y) {
    private var amount = 0

    override fun mayPlace(stack: ItemStack): Boolean = false

    override fun remove(amount: Int): ItemStack {
        if (this.hasItem()) { this.amount += (min(amount.toDouble(), this.item.count.toDouble())).toInt() }
        return super.remove(amount)
    }

    override fun onTake(player: Player, stack: ItemStack) {
        this.checkTakeAchievements(stack)
        super.onTake(player, stack)
    }

    protected override fun onQuickCraft(stack: ItemStack, amount: Int) {
        this.amount += amount
        this.checkTakeAchievements(stack)
    }

    protected override fun checkTakeAchievements(stack: ItemStack) {
        stack.onCraftedBy(player.level(), this.player, this.amount)
        if (player is ServerPlayer && this.container is FermentationBarrelBlockEntity && player.level() is ServerLevel) this.amount = 0
    }
}
