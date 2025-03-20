package net.satisfy.vinery.client.gui.handler

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.satisfy.vinery.client.gui.handler.slot.ExtendedSlot
import net.satisfy.vinery.client.gui.handler.slot.FermentationBarrelOutputSlot
import net.satisfy.vinery.registry.VineryScreenhandlerTypeRegistry
import net.satisfy.vinery.registry.VineryGrapeRegistry.WINE_BOTTLE

class FermentationBarrelGuiHandler @JvmOverloads constructor(
    syncId: Int,
    playerInventory: Inventory,
    private val inventory: Container = SimpleContainer(6),
    private val data: ContainerData = SimpleContainerData(4)
) : AbstractContainerMenu(VineryScreenhandlerTypeRegistry.FERMENTATION_BARREL_GUI_HANDLER, syncId) {
    private val level: Level = playerInventory.player.level()

    init {
        this.addDataSlots(data)
        this.addBlockEntitySlots(playerInventory)
        this.addPlayerInventory(playerInventory)
    }

    private fun addBlockEntitySlots(playerInventory: Inventory) {
        //TODO: Rework JuiceUtil to make it obsolete and compatible with the new data-driven approach
//        this.addSlot(ExtendedSlot(inventory, 0, 39, 17) { stack: ItemStack -> VineryGrapeRegistry.GRAPE_TYPES.contains(VineryGrapeRegistry.GRAPE_TYPES.find { grapeType -> stack == grapeType.getBottle().defaultInstance }) && canAddJuice(stack) })
//        this.addSlot(ExtendedSlot(inventory, 1, 67, 58) { stack: ItemStack -> this.isIngredient(stack) })
//        this.addSlot(ExtendedSlot(inventory, 2, 85, 58) { stack: ItemStack -> this.isIngredient(stack) })
//        this.addSlot(ExtendedSlot(inventory, 3, 103, 58) { stack: ItemStack -> this.isIngredient(stack) })
        this.addSlot(ExtendedSlot(inventory, WINE_BOTTLE_SLOT, 123, 58) { stack: ItemStack -> stack.`is`(WINE_BOTTLE) })
        this.addSlot(FermentationBarrelOutputSlot(playerInventory.player, inventory, OUTPUT_SLOT_GENERAL, 103, 17))
    }
    //TODO: Rework JuiceUtil to make it obsolete and compatible with the new data-driven approach
//    private fun canAddJuice(stack: ItemStack): Boolean {
//        val newJuiceType: String = JuiceUtil.getJuiceType(stack)
//        val currentJuiceType = juiceType
//        return currentJuiceType.isEmpty() || currentJuiceType == newJuiceType
//    }

    private fun addPlayerInventory(playerInventory: Inventory) {
        for (row in 0..2) {
            for (col in 0..8) {
                this.addSlot(Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18))
            }
        }
        for (col in 0..8) {
            this.addSlot(Slot(playerInventory, col, 8 + col * 18, 142))
        }
    }

    //TODO: Implement the RecipeTypesRegistry with the new data-driven approach
//    private fun isIngredient(stack: ItemStack): Boolean {
//        return level.recipeManager
//            .getAllRecipesFor(RecipeTypesRegistry.FERMENTATION_BARREL_RECIPE_TYPE.get())
//            .stream()
//            .anyMatch(Predicate<T> { recipe: T ->
//                recipe.getIngredients().stream()
//                    .anyMatch(Predicate<Ingredient> { ingredient: Ingredient -> ingredient.test(stack) })
//            })
//    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = slots[index]
        if (slot.hasItem()) {
            val stackInSlot = slot.item
            itemStack = stackInSlot.copy()
            val containerSlots = 6
            if (index < containerSlots) {
                if (!this.moveItemStackTo(stackInSlot, containerSlots, slots.size, true)) { return ItemStack.EMPTY }
            } else {
                //TODO: Rework JuiceUtil to make it obsolete and compatible with the new data-driven approach
//                if (JuiceUtil.isJuice(stackInSlot)) {
//                    if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) { return ItemStack.EMPTY }
//                } else if (stackInSlot.`is`(WINE_BOTTLE)) {
//                    if (!this.moveItemStackTo(stackInSlot, WINE_BOTTLE_SLOT, WINE_BOTTLE_SLOT + 1, false)) { return ItemStack.EMPTY }
//                } else if (isIngredient(stackInSlot)) {
//                    if (!this.moveItemStackTo(stackInSlot, 1, 4, false)) { return ItemStack.EMPTY }
//                } else if (index < slots.size - 9) {
//                    if (!this.moveItemStackTo(
//                            stackInSlot,
//                            slots.size - 9,
//                            slots.size, false
//                        )
//                    ) {
//                        return ItemStack.EMPTY
//                    }
//                } else {
                    if (!this.moveItemStackTo(
                            stackInSlot, containerSlots,
                            slots.size - 9, false
                        )
                    ) {
                        return ItemStack.EMPTY
                    }
                }
                if (stackInSlot.isEmpty) {
                    slot.set(ItemStack.EMPTY)
                } else {
                    slot.setChanged()
                }
                if (stackInSlot.count == itemStack.count) {
                    return ItemStack.EMPTY
                }
                slot.onTake(player, stackInSlot)
            //TODO: Rework JuiceUtil to make it obsolete and compatible with the new data-driven approach
//            }
        }
        return itemStack
    }

    override fun stillValid(player: Player): Boolean {
        return inventory.stillValid(player)
    }

    val juiceType: String
        get() {
            val juiceTypeValue = data[3]
            return getJuiceTypeFromValue(juiceTypeValue)
        }

    private fun getJuiceTypeFromValue(value: Int): String {
        return when (value) {
            0 -> "white_general"
            1 -> "red_general"
            2 -> "white_savanna"
            3 -> "red_savanna"
            4 -> "white_taiga"
            5 -> "red_taiga"
            6 -> "white_jungle"
            7 -> "red_jungle"
            8 -> "apple"
            9 -> "red_crimson"
            10 -> "white_warped"
            else -> ""
        }
    }

    val fluidLevel: Int
        get() = data[2]

    fun getScaledProgress(maxProgress: Int): Int {
        val progress = data[0]
        val totalProgress = data[1]
        if (progress == 0 || totalProgress == 0) {
            return 0
        }
        return (progress.toDouble() / totalProgress * maxProgress).toInt()
    }

    companion object {
        private const val WINE_BOTTLE_SLOT = 4
        private const val OUTPUT_SLOT_GENERAL = 5
    }
}
