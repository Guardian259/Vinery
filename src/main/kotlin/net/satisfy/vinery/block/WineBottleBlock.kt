package net.satisfy.vinery.block

import com.mojang.datafixers.util.Pair
import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.satisfy.vinery.item.DrinkBlockItem

@Suppress("deprecation")
class WineBottleBlock(settings: Properties?, private val maxCount: Int): Block(settings!!), /*StorageBlock(settings)*/ PolymerBlock {
//    init {
//        this.registerDefaultState(this.defaultBlockState().setValue(FAKE_MODEL, true))
//    }

    override fun use(
        state: BlockState?,
        world: Level,
        pos: BlockPos?,
        player: Player,
        hand: InteractionHand?,
        hit: BlockHitResult?
    ): InteractionResult {
        val stack = player.getItemInHand(hand!!)
        val blockEntity = world.getBlockEntity(pos!!)

//        if (blockEntity is StorageBlockEntity) {
//            val inventory: NonNullList<ItemStack> = blockEntity.getInventory()
//
//            if (canInsertStack(stack) && willFitStack(stack, inventory)) {
//                val posInE = getFirstEmptySlot(inventory)
//                if (posInE == Int.MIN_VALUE) return InteractionResult.PASS
//                if (!world.isClientSide()) {
//                    blockEntity.setStack(posInE, stack.split(1))
//                    if (player.isCreative) {
//                        stack.grow(1)
//                    }
//                    world.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f)
//                }
//                return InteractionResult.sidedSuccess(world.isClientSide())
//            } else if (stack.isEmpty && !isEmpty(inventory)) {
//                val posInE = getLastFullSlot(inventory)
//                if (posInE == Int.MIN_VALUE) return InteractionResult.PASS
//                if (!world.isClientSide()) {
//                    val wine: ItemStack = blockEntity.removeStack(posInE)
//                    if (!player.inventory.add(wine)) {
//                        player.drop(wine, false)
//                    }
//                    if (isEmpty(inventory)) {
//                        world.destroyBlock(pos, false)
//                    }
//                    world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f)
//                }
//                return InteractionResult.sidedSuccess(world.isClientSide())
//            }
//        }
        return InteractionResult.PASS
    }

    fun isEmpty(inventory: NonNullList<ItemStack>): Boolean {
        for (stack in inventory) {
            if (!stack.isEmpty) return false
        }
        return true
    }

    fun getFirstEmptySlot(inventory: NonNullList<ItemStack>): Int {
        for (stack in inventory) {
            if (stack.isEmpty) return inventory.indexOf(stack)
        }
        return Int.MIN_VALUE
    }

    fun getLastFullSlot(inventory: NonNullList<ItemStack>): Int {
        for (i in inventory.indices.reversed()) {
            if (!inventory[i].isEmpty) return i
        }
        return Int.MIN_VALUE
    }


    override fun getShape(state: BlockState?, world: BlockGetter?, pos: BlockPos?, context: CollisionContext?): VoxelShape {
        return SHAPE
    }

//    protected fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
//        super.createBlockStateDefinition(builder)
//        builder.add(FAKE_MODEL)
//    }

//    fun updateShape(
//        blockState: BlockState,
//        direction: Direction,
//        blockState2: BlockState?,
//        levelAccessor: LevelAccessor,
//        blockPos: BlockPos?,
//        blockPos2: BlockPos?
//    ): BlockState {
//        if (direction == Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos)) {
//            levelAccessor.destroyBlock(blockPos, true)
//        }
//        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2)
//    }

    fun size(): Int {
        return maxCount
    }

//    fun type(): ResourceLocation {
//        return StorageTypeRegistry.WINE_BOTTLE
//    }

//    fun canInsertStack(stack: ItemStack): Boolean {
//        return stack.`is`(TagRegistry.SMALL_BOTTLE)
//    }

//    fun willFitStack(itemStack: ItemStack, inventory: NonNullList<ItemStack>): Boolean {
//        val p = getFilledAmountAndBiggest(inventory)
//        val biggest = p.second
//        val count = p.first
//        val stackCount = getCount(itemStack)
//        if (biggest == Int.MAX_VALUE) return true
//
//        return stackCount > count && count < biggest
//    }

    fun getSection(aFloat: Float?, aFloat1: Float?): Int {
        return 0
    }

    fun unAllowedDirections(): Array<Direction?> {
        return arrayOfNulls(0)
    }

    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.PLAYER_HEAD

    companion object {
        private val SHAPE: VoxelShape = Shapes.box(0.125, 0.0, 0.125, 0.875, 0.875, 0.875)

        val FAKE_MODEL: BooleanProperty = BooleanProperty.create("fake_model")

//        fun getFilledAmountAndBiggest(inventory: NonNullList<ItemStack>): Pair<Int, Int> {
//            var count = 0
//            var biggest = Int.MAX_VALUE
//            for (stack in inventory) {
//                if (!stack.isEmpty) {
//                    count++
//                    if (stack.item is DrinkBlockItem && item.getBlock() is WineBottleBlock && wine.maxCount < biggest) {
//                        biggest = wine.maxCount
//                    }
//                }
//            }
//            return Pair(count, biggest)
//        }
//
//        fun getCount(itemStack: ItemStack): Int {
//            if (itemStack.item is DrinkBlockItem && item.getBlock() is WineBottleBlock) {
//                return wine.maxCount
//            }
//            return Int.MIN_VALUE
//        }
    }
}
