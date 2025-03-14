package net.satisfy.vinery.block

import eu.pb4.polymer.core.api.block.PolymerBlock
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.satisfy.vinery.item.GrapeBushSeedItem
import net.satisfy.vinery.util.GeneralUtil
import java.util.*
import java.util.function.Consumer


class LatticeBlock(properties: Properties?) : StemBlock(properties), PolymerBlock {
    init {
        registerDefaultState(
            defaultBlockState()
                .setValue<Direction, Direction>(FACING, Direction.NORTH)
                .setValue<Boolean, Boolean>(SUPPORT, true)
                .setValue<Boolean, Boolean>(BOTTOM, false)
                .setValue(TYPE, GeneralUtil.LineConnectingType.NONE)
        )
    }

    @Nullable
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val level: Level = context.level
        var facing: Direction = context.horizontalDirection.opposite

        val clickedPos = context.clickedPos
        val clickedFace: Direction = context.clickedFace
        val clickedFacingPos = clickedPos.relative(clickedFace.getOpposite())
        val clickedFacingState: BlockState = level.getBlockState(clickedFacingPos)

        if (context.player != null && !context.player!!.isCrouching && clickedFacingState.block is LatticeBlock) {
            val clickedFacingFace: Direction = clickedFacingState.getValue(FACING)
            if (clickedFacingFace !== clickedFace && clickedFacingFace.getOpposite() !== clickedFace) facing =
                clickedFacingFace
        }
        val bottom = clickedFace === Direction.DOWN || clickedFace === Direction.UP
        var state = defaultBlockState().setValue(FACING, facing).setValue(BOTTOM, bottom)
        if (!bottom) {
            state = getConnection(defaultBlockState().setValue(FACING, facing), level, clickedPos)
        }
        return state
    }


    @Suppress("deprecation")
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        if (java.lang.Boolean.TRUE == state.getValue(BOTTOM)) return FLOOR
        return when (state.getValue(FACING)) {
            Direction.WEST -> WEST
            Direction.EAST -> EAST
            Direction.SOUTH -> SOUTH
            else -> NORTH
        }
    }

    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos?,
        player: Player?,
        hand: InteractionHand?,
        hit: BlockHitResult
    ): InteractionResult {
        if (!world.isClientSide && player!!.getItemInHand(hand).getItem() is AxeItem) {
            val newState = state.setValue(SUPPORT, !state.getValue(SUPPORT))
            val updateState = getConnection(newState, world, pos!!)
            world.setBlock(pos, updateState, 3)
            return InteractionResult.SUCCESS
        }
        val stack: ItemStack = player!!.getItemInHand(hand)
        val age = state.getValue(AGE)

        if (hand == InteractionHand.OFF_HAND) {
            return super.use(state, world, pos, player, hand, hit)
        }

        val hitDirection: Direction = hit.direction
        if (age > 0 && stack.item === Items.SHEARS) {
            stack.hurtAndBreak(1, player,
                Consumer { player2: LivingEntity -> player2.broadcastBreakEvent(player.getUsedItemHand()) })
            if (age > 2) {
                dropGrapes(world, state, pos, hitDirection)
            }
            dropGrapeSeeds(world, state, pos, hitDirection)
            world.setBlock(pos, state.setValue(AGE, 0), 3)
            world.playSound(player, pos, BREAK_SOUND_EVENT, SoundSource.AMBIENT, 1.0f, 1.0f)
            return InteractionResult.SUCCESS
        } else if (stack.item is GrapeBushSeedItem && (stack.item as GrapeBushSeedItem).type.isLattice && age == 0) {
            world.setBlock(pos, withAge(state, 1, (stack.item as GrapeBushSeedItem).type), 3)
            if (!player.isCreative()) {
                stack.shrink(1)
            }
            world.playSound(player, pos, PLACE_SOUND_EVENT, SoundSource.AMBIENT, 1.0f, 1.0f)
            return InteractionResult.SUCCESS
        } else if (age > 2) {
            stack.hurtAndBreak(1, player,
                Consumer { player2: LivingEntity -> player2.broadcastBreakEvent(player.getUsedItemHand()) })
            dropGrapes(world, state, pos, hitDirection)
            world.setBlock(pos, state.setValue(AGE, 1), 3)
            world.playSound(player, pos, BREAK_SOUND_EVENT, SoundSource.AMBIENT, 1.0f, 1.0f)
            return InteractionResult.SUCCESS
        }

        return super.use(state, world, pos, player, hand, hit)
    }

    @Suppress("deprecation")
    override fun randomTick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val rand: Random = Random()
        if (rand.nextInt(100) >= 98 || isMature(state)) return
        val age = state.getValue(AGE)
        val newState = this.withAge(state, age + 1, state.getValue(GRAPE))
        world.setBlock(pos, newState, UPDATE_CLIENTS)
        super.randomTick(state, world, pos, random)
    }

    @Suppress("deprecation")
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

    override fun isRandomlyTicking(state: BlockState): Boolean {
        val age = state.getValue(AGE)


        return (!isMature(state) && age > 0 && age < 4)
    }

    @Suppress("deprecation")
    override fun updateShape(
        state: BlockState,
        direction: Direction?,
        neighborState: BlockState?,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos?
    ): BlockState {
        if (!state.canSurvive(world, pos)) {
            world.scheduleTick(pos, this, 1)
        }
        return getConnection(state, world, pos)
    }

    override fun isValidBonemealTarget(
        levelReader: LevelReader,
        blockPos: BlockPos,
        state: BlockState,
        bl: Boolean
    ): Boolean {
        return !isMature(state) && state.getValue(AGE) > 0
    }

    fun getConnection(state: BlockState, level: LevelAccessor, currentPos: BlockPos): BlockState {
        val facing: Direction = state.getValue(FACING)

        val stateL = level.getBlockState(currentPos.relative(facing.getClockWise()))
        val stateR = level.getBlockState(currentPos.relative(facing.getCounterClockWise()))

        val sideL =
            (stateL.block is LatticeBlock && (stateL.getValue(FACING) == facing || stateL.getValue(FACING) == facing.getClockWise()))
        val sideR =
            (stateR.block is LatticeBlock && (stateR.getValue(FACING) == facing || stateR.getValue(FACING) == facing.getCounterClockWise()))
        val type: GeneralUtil.LineConnectingType = if (sideL && sideR) GeneralUtil.LineConnectingType.MIDDLE
        else (if (sideR) GeneralUtil.LineConnectingType.LEFT
        else (if (sideL) GeneralUtil.LineConnectingType.RIGHT
        else GeneralUtil.LineConnectingType.NONE))

        return state.setValue(TYPE, type)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(FACING, TYPE, SUPPORT, BOTTOM)
    }

    @Suppress("deprecation")
    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)))
    }

    @Suppress("deprecation")
    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        return state.rotate(mirror.getRotation(state.getValue(FACING)))
    }

    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.LADDER

    companion object {
        val SUPPORT: BooleanProperty = BooleanProperty.create("support")
        val BOTTOM: BooleanProperty = BooleanProperty.create("bottom")

        protected val EAST: VoxelShape = box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0)
        protected val WEST: VoxelShape = box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0)
        protected val SOUTH: VoxelShape = box(0.0, 0.0, 0.01, 16.0, 16.0, 2.0)
        protected val NORTH: VoxelShape = box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0)
        protected val FLOOR: VoxelShape = box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0)


        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
        val TYPE: EnumProperty<GeneralUtil.LineConnectingType> = GeneralUtil.LINE_CONNECTING_TYPE

        private val BREAK_SOUND_EVENT: SoundEvent = SoundEvents.SWEET_BERRY_BUSH_BREAK
        private val PLACE_SOUND_EVENT: SoundEvent = SoundEvents.SWEET_BERRY_BUSH_PLACE
    }
}