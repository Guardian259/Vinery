package net.satisfy.vinery.block

import eu.pb4.polymer.blocks.api.BlockModelType
import eu.pb4.polymer.blocks.api.PolymerBlockModel
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils
import eu.pb4.polymer.core.api.block.PolymerBlock
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
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
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.item.GrapeBushSeedItem
import net.satisfy.vinery.util.GeneralUtil
import java.util.*
import java.util.function.Consumer

/**
 * A lattice block supporting grape growth with directional and connection states.
 * Extends [StemBlock] with custom placement, interaction, and shape logic.
 * Uses [PolymerBlock] to mimic oak stairs visually.
 *
 * @param properties Block behavior properties
 */
class LatticeBlock(modelName: String, properties: Properties?) : StemBlock(properties), PolymerBlock {

    private var polymerBlockState: BlockState

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(SUPPORT, true)
                .setValue(BOTTOM, false)
                .setValue(TYPE, GeneralUtil.LineConnectingType.NONE)
        )
        this.polymerBlockState = PolymerBlockResourceUtils.requestBlock(
            BlockModelType.TRANSPARENT_BLOCK,
            PolymerBlockModel.of(ResourceLocation(MODID, "block/$modelName"))
        )!!
    }

    /**
     * Determines initial state based on placement context, adjusting facing and connection.
     */
    @Nullable
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val level = context.level
        var facing = context.horizontalDirection.opposite
        val clickedPos = context.clickedPos
        val clickedFace = context.clickedFace
        val clickedFacingPos = clickedPos.relative(clickedFace.opposite)
        val clickedFacingState = level.getBlockState(clickedFacingPos)

        if (context.player?.isCrouching == false && clickedFacingState.block is LatticeBlock) {
            val clickedFacingFace = clickedFacingState.getValue(FACING)
            if (clickedFacingFace !== clickedFace && clickedFacingFace.opposite !== clickedFace) facing = clickedFacingFace
        }
        val bottom = clickedFace === Direction.DOWN || clickedFace === Direction.UP
        var state = defaultBlockState().setValue(FACING, facing).setValue(BOTTOM, bottom)
        if (!bottom) state = getConnection(state, level, clickedPos)
        return state
    }

    /** Returns the shape based on facing direction or bottom state. */
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        if (state.getValue(BOTTOM)) return FLOOR
        return when (state.getValue(FACING)) {
            Direction.WEST -> WEST
            Direction.EAST -> EAST
            Direction.SOUTH -> SOUTH
            else -> NORTH
        }
    }

    /**
     * Handles interaction: axe toggles support, shears harvest, seeds plant lattice grapes, hand harvests mature grapes.
     *
     * @return [InteractionResult.SUCCESS] if action performed, else superclass result
     */
    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult? {
        if (hand == InteractionHand.OFF_HAND) return super.use(state, world, pos, player, hand, hit)

        val itemInHand = player.getItemInHand(hand)
        val age = state.getValue(AGE)
        val hitDirection = hit.direction

        return when {
            !world.isClientSide && itemInHand.item is AxeItem -> {
                val newState = state.setValue(SUPPORT, !state.getValue(SUPPORT))
                world.setBlock(pos, getConnection(newState, world, pos), 3)
                InteractionResult.SUCCESS
            }
            age > 0 && itemInHand.item === Items.SHEARS -> {
                itemInHand.hurtAndBreak(1, player) { it.broadcastBreakEvent(hand) }
                if (age > 2) dropGrapes(world, state, pos, hitDirection)
                dropGrapeSeeds(world, state, pos, hitDirection)
                world.setBlock(pos, state.setValue(AGE, 0), 3)
                world.playSound(player, pos, BREAK_SOUND_EVENT, SoundSource.AMBIENT, 1.0f, 1.0f)
                InteractionResult.SUCCESS
            }
            age == 0 && itemInHand.item is GrapeBushSeedItem && (itemInHand.item as GrapeBushSeedItem).type.isLattice -> {
                val seed = itemInHand.item as GrapeBushSeedItem
                world.setBlock(pos, withAge(state, 1, seed.type), 3)
                if (!player.isCreative) itemInHand.shrink(1)
                world.playSound(player, pos, PLACE_SOUND_EVENT, SoundSource.AMBIENT, 1.0f, 1.0f)
                InteractionResult.SUCCESS
            }
            age > 2 -> {
                itemInHand.hurtAndBreak(1, player) { it.broadcastBreakEvent(hand) }
                dropGrapes(world, state, pos, hitDirection)
                world.setBlock(pos, state.setValue(AGE, 1), 3)
                world.playSound(player, pos, BREAK_SOUND_EVENT, SoundSource.AMBIENT, 1.0f, 1.0f)
                InteractionResult.SUCCESS
            }
            else -> super.use(state, world, pos, player, hand, hit)
        }
    }

    /** Grows the lattice randomly (98% chance to skip) if not mature. */
    override fun randomTick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val rand = Random()
        if (rand.nextInt(100) >= 98 || isMature(state)) return
        val age = state.getValue(AGE)
        val newState = withAge(state, age + 1, state.getValue(GRAPE))
        world.setBlock(pos, newState, UPDATE_CLIENTS)
        super.randomTick(state, world, pos, random)
    }

    /** Drops items and destroys block if it can’t survive on tick. */
    override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (!state.canSurvive(world, pos)) {
            if (state.getValue(AGE) > 0) dropGrapeSeeds(world, state, pos, null)
            if (state.getValue(AGE) > 2) dropGrapes(world, state, pos, null)
            world.destroyBlock(pos, true)
        }
    }

    /** Indicates if the block ticks randomly (not mature, age 1-3). */
    override fun isRandomlyTicking(state: BlockState): Boolean {
        val age = state.getValue(AGE)
        return !isMature(state) && age in 1..3
    }

    /** Updates state based on survival and connections. */
    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (!state.canSurvive(world, pos)) world.scheduleTick(pos, this, 1)
        return getConnection(state, world, pos)
    }

    /** Checks if bonemeal can be applied (not mature, age > 0). */
    override fun isValidBonemealTarget(
        levelReader: LevelReader,
        blockPos: BlockPos,
        state: BlockState,
        bl: Boolean
    ): Boolean = !isMature(state) && state.getValue(AGE) > 0

    /** Updates connection type based on adjacent lattice blocks. */
    private fun getConnection(state: BlockState, level: LevelAccessor, currentPos: BlockPos): BlockState {
        val facing = state.getValue(FACING)
        val stateL = level.getBlockState(currentPos.relative(facing.clockWise))
        val stateR = level.getBlockState(currentPos.relative(facing.counterClockWise))
        val sideL = stateL.block is LatticeBlock && (stateL.getValue(FACING) == facing || stateL.getValue(FACING) == facing.clockWise)
        val sideR = stateR.block is LatticeBlock && (stateR.getValue(FACING) == facing || stateR.getValue(FACING) == facing.counterClockWise)
        val type = when {
            sideL && sideR -> GeneralUtil.LineConnectingType.MIDDLE
            sideR -> GeneralUtil.LineConnectingType.LEFT
            sideL -> GeneralUtil.LineConnectingType.RIGHT
            else -> GeneralUtil.LineConnectingType.NONE
        }
        return state.setValue(TYPE, type)
    }

    /** Adds FACING, TYPE, SUPPORT, and BOTTOM to the block state definition. */
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(FACING, TYPE, SUPPORT, BOTTOM)
    }

    /** Rotates the facing direction. */
    override fun rotate(state: BlockState, rotation: Rotation): BlockState =
        state.setValue(FACING, rotation.rotate(state.getValue(FACING)))

    /** Mirrors the facing direction. */
    override fun mirror(state: BlockState, mirror: Mirror): BlockState =
        state.rotate(mirror.getRotation(state.getValue(FACING)))

    /** Returns the block used for Polymer visual replacement (oak stairs). */
    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.OAK_STAIRS

    /** Returns the custom model data value for this lattice block type. */
    override fun getPolymerBlockState(state: BlockState?) = this.polymerBlockState

    companion object {
        /** Indicates if the lattice provides support. */
        val SUPPORT: BooleanProperty = BooleanProperty.create("support")

        /** Indicates if the lattice is on the floor. */
        val BOTTOM: BooleanProperty = BooleanProperty.create("bottom")

        /** Shape for east-facing lattice. */
        private val EAST: VoxelShape = box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0)

        /** Shape for west-facing lattice. */
        private val WEST: VoxelShape = box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0)

        /** Shape for south-facing lattice. */
        private val SOUTH: VoxelShape = box(0.0, 0.0, 0.01, 16.0, 16.0, 2.0)

        /** Shape for north-facing lattice. */
        private val NORTH: VoxelShape = box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0)

        /** Shape for floor lattice. */
        private val FLOOR: VoxelShape = box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0)

        /** Facing direction of the lattice. */
        val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING

        /** Connection type to adjacent lattices. */
        val TYPE: EnumProperty<GeneralUtil.LineConnectingType> = GeneralUtil.LINE_CONNECTING_TYPE

        /** Sound event for breaking the lattice. */
        private val BREAK_SOUND_EVENT: SoundEvent = SoundEvents.SWEET_BERRY_BUSH_BREAK

        /** Sound event for placing the lattice. */
        private val PLACE_SOUND_EVENT: SoundEvent = SoundEvents.SWEET_BERRY_BUSH_PLACE
    }
}