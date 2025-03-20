package net.satisfy.vinery.block.entity

import net.darktree.simpleconfig.SimpleConfig
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.core.RegistryAccess
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.ContainerHelper
import net.minecraft.world.Containers
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.satisfy.vinery.Vinery.Companion.config
import net.satisfy.vinery.registry.VineryEntityRegistry.FERMENTATION_BARREL_ENTITY
import net.satisfy.vinery.registry.VineryGrapeRegistry.WINE_BOTTLE
import net.satisfy.vinery.util.ImplementedInventory
import net.satisfy.vinery.util.WineYears
import java.util.*
import java.util.function.Predicate
import kotlin.math.min

class FermentationBarrelBlockEntity(pos: BlockPos?, state: BlockState?) :
    BlockEntity(FERMENTATION_BARREL_ENTITY, pos!!, state!!), ImplementedInventory, MenuProvider {
    override var items: NonNullList<ItemStack?>
        private set

    private var fermentationTime = 0
    private var fluidLevel = 0
    private var juiceType = ""

    private val propertyDelegate: ContainerData = object : ContainerData {
        override fun get(index: Int): Int {
            return when (index) {
                0 -> this@FermentationBarrelBlockEntity.fermentationTime
                1 -> Objects.requireNonNull<SimpleConfig?>(config).getOrDefault("totalFermentationTime", 6000)
                2 -> this@FermentationBarrelBlockEntity.fluidLevel
//                3 -> this.juiceTypeValue
                else -> 0
            }
        }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> this@FermentationBarrelBlockEntity.fermentationTime = value
                1 -> this@FermentationBarrelBlockEntity.updateTotalFermentationTime()
                2 -> this@FermentationBarrelBlockEntity.setFluidLevel(value)
                3 -> this@FermentationBarrelBlockEntity.juiceType = getJuiceTypeFromValue(value)
                else -> {}
            }
        }

        override fun getCount(): Int {
            return 4
        }
    }

    init {
        this.items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY)
    }

    fun updateTotalFermentationTime() {
        setChanged()
    }

    fun getFluidLevel(): Int {
        return fluidLevel
    }

    fun setFluidLevel(fluidLevel: Int) {
        this.fluidLevel = fluidLevel
        setChanged()
        if (level != null && !level!!.isClientSide) {
            level!!.sendBlockUpdated(this.worldPosition, this.blockState, this.blockState, 3)
        }
    }

    fun getJuiceType(): String {
        return juiceType
    }

    fun setJuiceType(juiceType: String) {
        this.juiceType = juiceType
        setChanged()
    }

    private val juiceTypeValue: Int
        get() = when (juiceType) {
            "white_general" -> 0
            "red_general" -> 1
            "white_savanna" -> 2
            "red_savanna" -> 3
            "white_taiga" -> 4
            "red_taiga" -> 5
            "white_jungle" -> 6
            "red_jungle" -> 7
            "apple" -> 8
            "red_crimson" -> 9
            "white_warped" -> 10
            else -> -1
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

    override fun load(nbt: CompoundTag) {
        super.load(nbt)
        this.items = NonNullList.withSize(this.containerSize, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(nbt, this.items)
        this.fermentationTime = nbt.getInt("FermentationTime")
        this.fluidLevel = nbt.getInt("FluidLevel")
        this.juiceType = nbt.getString("JuiceType")
    }

    public override fun saveAdditional(nbt: CompoundTag) {
        super.saveAdditional(nbt)
        ContainerHelper.saveAllItems(nbt, this.items)
        nbt.putInt("FermentationTime", this.fermentationTime)
        nbt.putInt("FluidLevel", this.fluidLevel)
        nbt.putString("JuiceType", this.juiceType)
    }

    override fun setChanged() { TODO("Not yet implemented") }

//    private fun canCraft(recipe: FermentationBarrelRecipe?, access: RegistryAccess): Boolean {
//        if (recipe == null || recipe.getResultItem(access).isEmpty()) {
//            return false
//        } else if (areIngredientsEmpty()) {
//            return false
//        } else if (this.fluidLevel < recipe.getJuiceAmount()) {
//            return false
//        } else if (juiceType != recipe.getJuiceType()) {
//            return false
//        } else {
//            if (recipe.isWineBottleRequired()) {
//                val wineBottle = this.getItem(WINE_BOTTLE_SLOT)
//                if (wineBottle.isEmpty || !wineBottle.`is`(WINE_BOTTLE)) {
//                    return false
//                }
//            }
//
//            val recipeOutput: ItemStack = recipe.getResultItem(access)
//            if (recipeOutput.`is`(WINE_BOTTLE)) {
//                val existingWineBottle = this.getItem(WINE_BOTTLE_SLOT)
//                return if (existingWineBottle.isEmpty) {
//                    true
//                } else existingWineBottle.`is`(recipeOutput.item) && existingWineBottle.count + recipeOutput.count <= existingWineBottle.maxStackSize
//            } else {
//                val existingOutput = this.getItem(OUTPUT_SLOT_GENERAL)
//                return if (existingOutput.isEmpty) {
//                    true
//                } else existingOutput.`is`(recipeOutput.item) && existingOutput.count + recipeOutput.count <= existingOutput.maxStackSize
//            }
//        }
//    }

    private fun areIngredientsEmpty(): Boolean {
        for (i in 1..3) {
            if (!getItem(i).isEmpty) {
                return false
            }
        }
        return true
    }

//    private fun craft(recipe: FermentationBarrelRecipe, access: RegistryAccess) {
//        if (!canCraft(recipe, access)) {
//            return
//        }
//
//        val recipeOutput: ItemStack = recipe.getResultItem(access).copy()
//
//        val existingOutput = this.getItem(OUTPUT_SLOT_GENERAL)
//        if (existingOutput.isEmpty) {
//            this.setItem(OUTPUT_SLOT_GENERAL, recipeOutput)
//        } else if (existingOutput.`is`(recipeOutput.item) && existingOutput.count + recipeOutput.count <= existingOutput.maxStackSize) {
//            existingOutput.grow(recipeOutput.count)
//            this.setItem(OUTPUT_SLOT_GENERAL, existingOutput)
//        } else {
//            checkNotNull(this.level)
//            Containers.dropItemStack(
//                this.level,
//                worldPosition.x + 0.5, (worldPosition.y + 1).toDouble(),
//                worldPosition.z + 0.5, recipeOutput
//            )
//        }
//
//        if (recipe.isWineBottleRequired()) {
//            val wineBottle = this.getItem(WINE_BOTTLE_SLOT)
//            if (!wineBottle.isEmpty && wineBottle.count > 0) {
//                wineBottle.shrink(1)
//                this.setItem(WINE_BOTTLE_SLOT, wineBottle)
//            }
//        }
//
//        val newFluidLevel: Int = this.fluidLevel - recipe.getJuiceAmount()
//        this.setFluidLevel(newFluidLevel)
//
//        for (ingredient in recipe.getIngredients()) {
//            for (i in 1..3) {
//                val slotStack = this.getItem(i)
//                if (ingredient.test(slotStack)) {
//                    slotStack.shrink(1)
//                    if (slotStack.isEmpty) {
//                        this.setItem(i, ItemStack.EMPTY)
//                    } else {
//                        this.setItem(i, slotStack)
//                    }
//                    break
//                }
//            }
//        }
//
//        WineYears.setWineYear(recipeOutput, this.level)
//    }

    override fun setItem(slot: Int, stack: ItemStack) {
        val stackInSlot = items[slot]
        val sameItem = !stack.isEmpty && ItemStack.matches(stack, stackInSlot)

        items[slot] = stack

        if (stack.count > this.getMaxStackSize()) {
            stack.count = this.getMaxStackSize()
        }

        if (!sameItem && isIngredientSlot(slot)) {
            if (areIngredientsEmpty()) {
                this.fermentationTime = 0
                setChanged()
            }
        }
    }

    private fun isIngredientSlot(slot: Int): Boolean {
        return slot >= 1 && slot <= 3
    }

    override fun stillValid(player: Player): Boolean {
        checkNotNull(this.level)
        return level!!.getBlockEntity(this.worldPosition) === this &&
                player.distanceToSqr(
                    worldPosition.x + 0.5,
                    worldPosition.y + 0.5,
                    worldPosition.z + 0.5
                ) <= 64.0
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        val tag = CompoundTag()
        this.saveAdditional(tag)
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(): CompoundTag {
        val tag = CompoundTag()
        this.saveAdditional(tag)
        return tag
    }

    override fun createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu? {
        TODO("Not yet implemented")
    }

    override fun getDisplayName(): Component {
        return Component.translatable(blockState.block.descriptionId)
    }

//    override fun createMenu(syncId: Int, inv: Inventory, player: Player): AbstractContainerMenu? = FermentationBarrelGuiHandler(syncId, inv, this, this.propertyDelegate)


    override fun getItem(slot: Int): ItemStack = items[slot]

    override fun removeItem(slot: Int, count: Int): ItemStack = ContainerHelper.removeItem(this.items, slot, count)

    override fun removeItemNoUpdate(slot: Int): ItemStack = ContainerHelper.takeItem(this.items, slot)

    override fun clearContent() { items.clear() }

//    private fun isIngredient(stack: ItemStack): Boolean {
//        if (level == null) return false
//        return level!!.recipeManager
//            .getAllRecipesFor(RecipeTypesRegistry.FERMENTATION_BARREL_RECIPE_TYPE.get())
//            .stream()
//            .anyMatch(Predicate<T> { recipe: T ->
//                recipe.getIngredients().stream()
//                    .anyMatch(Predicate<Ingredient> { ingredient: Ingredient -> ingredient.test(stack) })
//            })
//    }

    override fun getSlotsForFace(side: Direction): IntArray {
        if (side == Direction.UP) {
            return intArrayOf(GRAPEJUICE_INPUT_SLOT, WINE_BOTTLE_SLOT)
        } else if (side == Direction.DOWN) {
            return intArrayOf(OUTPUT_SLOT_GENERAL)
        } else if (side.axis.isHorizontal) {
            return intArrayOf(OUTPUT_SLOT_GENERAL, WINE_BOTTLE_SLOT, 1, 2, 3)
        }
        return intArrayOf()
    }

//    override fun canPlaceItemThroughFace(slot: Int, stack: ItemStack, side: Direction?): Boolean {
//        if (side == Direction.UP) {
//            if (slot == GRAPEJUICE_INPUT_SLOT && JuiceUtil.isJuice(stack)) {
//                return hasSpace(slot, stack)
//            } else if (slot == WINE_BOTTLE_SLOT && stack.`is`(WINE_BOTTLE)) {
//                return hasSpace(slot, stack)
//            }
//        } else {
//            checkNotNull(side)
//            if (side.axis.isHorizontal) {
//                if ((slot in 1..3) && isIngredient(stack)) {
//                    return hasSpace(slot, stack)
//                } else if (slot == WINE_BOTTLE_SLOT && stack.`is`(WINE_BOTTLE)) {
//                    return hasSpace(slot, stack)
//                }
//            }
//        }
//        return false
//    }

    private fun hasSpace(index: Int, stack: ItemStack): Boolean {
        val slotStack = getItem(index)
        if (slotStack.isEmpty) {
            return true
        }
        if (ItemStack.isSameItemSameTags(slotStack, stack)) {
            return slotStack.count + stack.count <= slotStack.maxStackSize
        }
        return false
    }

    companion object {
        private const val INVENTORY_SIZE = 6
        const val GRAPEJUICE_INPUT_SLOT: Int = 0
        const val OUTPUT_SLOT_GENERAL: Int = 5
        const val WINE_BOTTLE_SLOT: Int = 4

//        fun tick(world: Level, pos: BlockPos, blockEntity: FermentationBarrelBlockEntity) {
//            if (world.isClientSide) return
//
//            if (blockEntity.fluidLevel == 0) {
//                blockEntity.setJuiceType("")
//            }
//
//            val access = world.registryAccess()
//
//            val recipe: FermentationBarrelRecipe = world.recipeManager
//                .getRecipeFor(RecipeTypesRegistry.FERMENTATION_BARREL_RECIPE_TYPE.get(), blockEntity, world)
//                .orElse(null)
//
//            if (blockEntity.canCraft(recipe, access)) {
//                blockEntity.fermentationTime++
//
//                if (blockEntity.fermentationTime >= Objects.requireNonNull(config)
//                        .getOrDefault("totalFermentationTime", 6000)
//                ) {
//                    blockEntity.fermentationTime = 0
//                    blockEntity.craft(recipe, access)
//                }
//            } else {
//                blockEntity.fermentationTime = 0
//            }
//
//            val stack = blockEntity.getItem(GRAPEJUICE_INPUT_SLOT)
//            if (JuiceUtil.isJuice(stack)) {
//                val newJuiceType: String = JuiceUtil.getJuiceType(stack)
//
//                if (blockEntity.fluidLevel == 0 || blockEntity.juiceType == newJuiceType) {
//                    blockEntity.setJuiceType(newJuiceType)
//                    val currentLevel = blockEntity.getFluidLevel()
//                    val maxFluidLevel: Int = PlatformHelper.getMaxFluidLevel()
//                    val juiceCount = stack.count
//                    val juicesToConsume = min(juiceCount.toDouble(), 4.0).toInt()
//                    val fluidIncrease: Int = juicesToConsume * PlatformHelper.getMaxFluidIncrease()
//
//                    val newFluidLevel = min((currentLevel + fluidIncrease).toDouble(), maxFluidLevel.toDouble())
//                        .toInt()
//                    val actualFluidIncrease = newFluidLevel - currentLevel
//
//                    val actualJuicesConsumed: Int = actualFluidIncrease / PlatformHelper.getMaxFluidIncrease()
//
//                    if (actualJuicesConsumed > 0) {
//                        blockEntity.setFluidLevel(newFluidLevel)
//
//                        stack.shrink(actualJuicesConsumed)
//                        if (stack.isEmpty) blockEntity.setItem(GRAPEJUICE_INPUT_SLOT, ItemStack.EMPTY) else blockEntity.setItem(GRAPEJUICE_INPUT_SLOT, stack)
//
//                        val wineBottleStack: ItemStack = ItemStack(WINE_BOTTLE, actualJuicesConsumed)
//
//                        val existingOutput = blockEntity.getItem(WINE_BOTTLE_SLOT)
//                        if (existingOutput.isEmpty) {
//                            blockEntity.setItem(WINE_BOTTLE_SLOT, wineBottleStack)
//                        } else if (existingOutput.`is`(wineBottleStack.item) && existingOutput.count + wineBottleStack.count <= existingOutput.maxStackSize) {
//                            existingOutput.grow(wineBottleStack.count)
//                            blockEntity.setItem(WINE_BOTTLE_SLOT, existingOutput)
//                        } else {
//                            Containers.dropItemStack(
//                                world,
//                                pos.x + 0.5,
//                                (pos.y + 1).toDouble(),
//                                pos.z + 0.5,
//                                wineBottleStack
//                            )
//                        }
//                    }
//                }
//            }
//        }
    }
}
