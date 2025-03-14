package net.satisfy.vinery.block

import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.BonemealableBlock
import net.minecraft.world.level.block.BushBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.satisfy.vinery.util.GrapeType
import kotlin.math.min

open class GrapeBush(settings: Properties?, private val type: GrapeType) : BushBlock(settings!!), BonemealableBlock, PolymerBlock {

    override fun getShape(state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPE
    }

    override fun getCloneItemStack(world: BlockGetter, pos: BlockPos, state: BlockState): ItemStack {
        return ItemStack(getType().getSeeds())
    }

    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        val i: Int = state.getValue<Int>(AGE)
        val bl = i == 3
        if (!bl && player.getItemInHand(hand).`is`(Items.BONE_MEAL)) {
            return InteractionResult.PASS
        } else if (i > 1) {
            val x = world.random.nextInt(2)
            Block.popResource(world, pos, ItemStack(grapeType.getItem(), x + (if (bl) 1 else 0)))
            world.playSound(
                null,
                pos,
                SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                SoundSource.BLOCKS,
                1.0f,
                0.8f + world.random.nextFloat() * 0.4f
            )
            world.setBlock(pos, state.setValue<Int, Int>(AGE, 1), 2)
            return InteractionResult.sidedSuccess(world.isClientSide)
        } else {
            return super.use(state, world, pos, player, hand, hit)
        }
    }

    override fun randomTick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val age: Int = state.getValue<Int>(AGE)
        val growthChance: Double = 0.5; //TODO: Reintegrate into Config
        if (age < 3 && random.nextDouble() < growthChance && canGrowPlace(world, pos, state)) {
            val newState: BlockState = state.setValue<Int, Int>(AGE, age + 1)
            world.setBlock(pos, newState, 2)
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState))
        }
    }

    override fun isRandomlyTicking(state: BlockState): Boolean {
        return state.getValue<Int>(AGE) < 3
    }

    override fun isValidBonemealTarget(
        levelReader: LevelReader,
        blockPos: BlockPos,
        blockState: BlockState,
        bl: Boolean
    ): Boolean {
        return blockState.getValue<Int>(AGE) < 3
    }

    override fun isBonemealSuccess(world: Level, random: RandomSource, pos: BlockPos, state: BlockState): Boolean {
        return true
    }

    open fun canGrowPlace(world: LevelReader, blockPos: BlockPos, blockState: BlockState?): Boolean {
        return world.getRawBrightness(blockPos, 0) > 9
    }

    override fun canSurvive(blockState: BlockState, world: LevelReader, blockPos: BlockPos): Boolean {
        return canGrowPlace(world, blockPos, blockState) && this.mayPlaceOn(
            world.getBlockState(blockPos.below()),
            world,
            blockPos
        )
    }

    protected override fun mayPlaceOn(floor: BlockState, world: BlockGetter, pos: BlockPos): Boolean {
        return floor.isSolidRender(world, pos)
    }

    fun getType(): GrapeType {
        return this.type
    }

    val grapeType: ItemStack
        get() = ItemStack(getType().getFruit())


    override fun performBonemeal(world: ServerLevel, random: RandomSource, pos: BlockPos, state: BlockState) {
        val i = min(3.0, (state.getValue<Int>(AGE) + 1).toDouble()).toInt()
        world.setBlock(pos, state.setValue<Int, Int>(AGE, i), 2)
    }

    protected override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(AGE)
    }

    class SavannaGrapeBush(settings: BlockBehaviour.Properties?, type: GrapeType) : GrapeBush(settings, type) {
        override fun canGrowPlace(world: LevelReader, blockPos: BlockPos, blockState: BlockState?): Boolean {
            return world.getRawBrightness(blockPos, 0) >= 14
        }
    }

    class TaigaGrapeBush(settings: BlockBehaviour.Properties?, type: GrapeType) : GrapeBush(settings, type) {
        override fun canGrowPlace(world: LevelReader, blockPos: BlockPos, blockState: BlockState?): Boolean {
            if (world.getRawBrightness(blockPos, 0) <= 4) {
                return false
            }
            val size = 4
            val var2: Iterator<BlockPos> =
                BlockPos.betweenClosed(blockPos.offset(-size, -2, -size), blockPos.offset(size, 1, size)).iterator()

            var pos: BlockPos
            do {
                if (!var2.hasNext()) {
                    return false
                }

                pos = var2.next()
            } while (!(world.getBlockState(pos).getBlock() === Blocks.PODZOL || world.getBlockState(pos)
                    .getBlock() === Blocks.COARSE_DIRT || world.getBlockState(pos).getBlock() === Blocks.GRASS_BLOCK)
            )

            return true
        }

        override fun isPathfindable(
            arg: BlockState,
            arg2: BlockGetter,
            arg3: BlockPos,
            arg4: PathComputationType
        ): Boolean {
            return false
        }
    }

    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.SWEET_BERRY_BUSH

    companion object {
        val AGE: IntegerProperty = BlockStateProperties.AGE_3
        private val SHAPE: VoxelShape = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    }
}
