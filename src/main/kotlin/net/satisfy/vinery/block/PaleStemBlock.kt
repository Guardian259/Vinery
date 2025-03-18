package net.satisfy.vinery.block

import eu.pb4.polymer.core.api.block.PolymerBlock
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.satisfy.vinery.item.GrapeBushSeedItem
import net.satisfy.vinery.util.VineryGrapeRegistry
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.math.max

@SuppressWarnings("deprecation")
class PaleStemBlock(settings: Properties?) : StemBlock(settings), PolymerBlock {
    init {
        this.registerDefaultState(defaultBlockState().setValue(GRAPE, VineryGrapeRegistry.NONE).setValue(AGE, 0))
    }

    @NotNull
    override fun getShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return PALE_SHAPE
    }

    @Nullable
    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        val blockState: BlockState = defaultBlockState()
        val world: Level = ctx.level
        val blockPos: BlockPos = ctx.clickedPos
        if (blockState.canSurvive(ctx.level, ctx.clickedPos)) {
            return blockState
        }
        return null
    }

    override fun setPlacedBy(
        level: Level,
        blockPos: BlockPos,
        blockState: BlockState,
        livingEntity: LivingEntity?,
        itemStack: ItemStack
    ) {
        if (livingEntity is Player) {
            if ((livingEntity.isCreative || itemStack.count >= 2) && level.getBlockState(
                    blockPos.below()
                ).block !== this && blockPos.y < level.maxBuildHeight - 1 && level.getBlockState(
                    blockPos.above()
                ).canBeReplaced()
            ) {
                level.setBlock(blockPos.above(), this.defaultBlockState(), 3)
                itemStack.shrink(1)
            }
        }
    }


    @NotNull
    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult? {
        if (hand === InteractionHand.OFF_HAND) {
            return super.use(state, world, pos, player, hand, hit)
        }

        val age: Int = state.getValue(AGE)
        if (age > 0 && player.getItemInHand(hand).item === Items.SHEARS) {
            if (age > 2) {
                dropGrapes(world, state, pos, hit.direction)
            }
            dropGrapeSeeds(world, state, pos, hit.direction)
            world.setBlock(pos, withAge(state, max(0.0, (age - 1).toDouble()).toInt(), state.getValue(GRAPE)), 3)
            world.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_BREAK, SoundSource.AMBIENT, 1.0f, 1.0f)
            return InteractionResult.sidedSuccess(world.isClientSide)
        }


        val stack: ItemStack = player.getItemInHand(hand)
        if (stack.item is GrapeBushSeedItem && hasTrunk(world, pos)) {
            val seed = (stack.item as GrapeBushSeedItem)
            if (age == 0) {
                if (!seed.type.isLattice) {
                    world.setBlock(pos, withAge(state, 1, seed.type), 3)
                    if (!player.isCreative) {
                        stack.shrink(1)
                    }
                    world.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PLACE, SoundSource.AMBIENT, 1.0f, 1.0f)
                    return InteractionResult.SUCCESS
                }
            }
        }

        return super.use(state, world, pos, player, hand, hit)
    }

    override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (!state.canSurvive(world, pos)) {
            if (state.getValue(AGE) > 0) {
                dropGrapeSeeds(world, state, pos, null)
            }
            if (state.getValue(AGE) > 2) {
                dropGrapes(world, state, pos, null)
            }
            world.destroyBlock(pos, true)
        }
    }

    override fun randomTick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val rand: Random = Random()
        if (rand.nextInt(100) >= 98) return
        if (!isMature(state) && hasTrunk(world, pos) && state.getValue(AGE) > 0) {
            var i: Int = 0
            if (world.getRawBrightness(pos, 0) >= 9 && (state.getValue(AGE).also { i = it }) < 4) {
                world.setBlock(pos, this.withAge(state, i + 1, state.getValue(GRAPE)), Block.UPDATE_CLIENTS)
            }
        }
        super.randomTick(state, world, pos, random)
    }


    override fun canSurvive(state: BlockState?, world: LevelReader, pos: BlockPos): Boolean {
        return world.getBlockState(pos.below()).isRedstoneConductor(world, pos) || world.getBlockState(pos.below())
            .block === this
    }

    @NotNull
    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState? {
        if (!state.canSurvive(world, pos)) {
            world.scheduleTick(pos, this, 1)
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos)
    }

    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.OAK_FENCE

    companion object {
        private val PALE_SHAPE: VoxelShape = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0)
    }
}
