package net.satisfy.vinery.block

import eu.pb4.polymer.blocks.api.BlockModelType
import eu.pb4.polymer.blocks.api.PolymerBlockModel
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock
import eu.pb4.polymer.core.api.block.PolymerBlock
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
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
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.item.GrapeBushSeedItem
import net.satisfy.vinery.registry.VineryGrapeRegistry.NONE
import org.jetbrains.annotations.NotNull
import java.util.*


/**
 * A grapevine stem block with growth stages, supporting planting, harvesting, and extension upward.
 * Extends [StemBlock] with custom interaction and survival mechanics.
 * Uses [PolymerBlock] to mimic an oak fence visually.
 *
 * @param settings Block behavior properties
 */
@SuppressWarnings("deprecation")
class PaleStemBlock(modelName: String, settings: Properties?) : StemBlock(settings), PolymerBlock, PolymerTexturedBlock {

    private var polymerBlockState: BlockState

    init {
        this.registerDefaultState(defaultBlockState().setValue(GRAPE, NONE).setValue(AGE, 0))
        this.polymerBlockState = PolymerBlockResourceUtils.requestBlock(
            BlockModelType.TRANSPARENT_BLOCK,
            PolymerBlockModel.of(ResourceLocation(MODID, "block/$modelName"))
        )!!
    }

    /** Returns the fixed collision shape of the stem (4x16x4 centered box). */
    @NotNull
    override fun getShape(
        state: BlockState,
        world: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = PALE_SHAPE

    /**
     * Determines the initial state for placement, returning null if it can't survive.
     */
    @Nullable
    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        val blockState = defaultBlockState()
        return if (blockState.canSurvive(ctx.level, ctx.clickedPos)) blockState else null
    }

    /**
     * Extends the stem upward when placed by a player with sufficient items, if space allows.
     */
    override fun setPlacedBy(
        level: Level,
        blockPos: BlockPos,
        blockState: BlockState,
        livingEntity: LivingEntity?,
        itemStack: ItemStack
    ) {
        if (livingEntity !is Player) return

        val player = livingEntity
        val canExtend = (player.isCreative || itemStack.count >= 2) &&
                level.getBlockState(blockPos.below()).block !== this &&
                blockPos.y < level.maxBuildHeight - 1 &&
                level.getBlockState(blockPos.above()).canBeReplaced()

        if (canExtend) {
            level.setBlock(blockPos.above(), defaultBlockState(), 3)
            itemStack.shrink(1)
        }
    }

    /**
     * Handles interaction: shears harvest grapes/seeds and reduce age, seeds plant grape type if age 0.
     *
     * @return [InteractionResult.SUCCESS] if action performed, [InteractionResult.PASS] otherwise
     */
    @NotNull
    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult? {
        if (hand === InteractionHand.OFF_HAND) return super.use(state, world, pos, player, hand, hit)

        val age = state.getValue(AGE)
        val itemInHand = player.getItemInHand(hand)
        return when {
            age > 0 && itemInHand.item === Items.SHEARS -> {
                if (age > 2) dropGrapes(world, state, pos, hit.direction)
                dropGrapeSeeds(world, state, pos, hit.direction)
                world.setBlock(pos, withAge(state, age - 1, state.getValue(GRAPE)), 3)
                world.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_BREAK, SoundSource.AMBIENT, 1.0f, 1.0f)
                InteractionResult.sidedSuccess(world.isClientSide)
            }
            itemInHand.item is GrapeBushSeedItem && hasTrunk(world, pos) && age == 0 -> {
                val seed = itemInHand.item as GrapeBushSeedItem
                if (!seed.type.isLattice) {
                    world.setBlock(pos, withAge(state, 1, seed.type), 3)
                    if (!player.isCreative) itemInHand.shrink(1)
                    world.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PLACE, SoundSource.AMBIENT, 1.0f, 1.0f)
                    InteractionResult.SUCCESS
                } else {
                    super.use(state, world, pos, player, hand, hit)
                }
            }
            else -> super.use(state, world, pos, player, hand, hit)
        }
    }

    /**
     * Checks survival on tick; drops items and destroys block if unsupported.
     */
    override fun tick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        if (!state.canSurvive(world, pos)) {
            if (state.getValue(AGE) > 0) dropGrapeSeeds(world, state, pos, null)
            if (state.getValue(AGE) > 2) dropGrapes(world, state, pos, null)
            world.destroyBlock(pos, true)
        }
    }

    /**
     * Grows the stem randomly (2% chance) if not mature, has a trunk, and light level ≥ 9.
     */
    override fun randomTick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val rand = Random()
        if (rand.nextInt(100) >= 98) return
        if (!isMature(state) && hasTrunk(world, pos) && state.getValue(AGE) > 0) {
            val age = state.getValue(AGE)
            if (world.getRawBrightness(pos, 0) >= 9 && age < 4) {
                world.setBlock(pos, this.withAge(state, age + 1, state.getValue(GRAPE)), Block.UPDATE_CLIENTS)
            }
        }
        super.randomTick(state, world, pos, random)
    }

    /**
     * Checks if the stem can survive (requires solid ground or another stem below).
     */
    override fun canSurvive(state: BlockState?, world: LevelReader, pos: BlockPos): Boolean {
        val belowState = world.getBlockState(pos.below())
        return belowState.isRedstoneConductor(world, pos) || belowState.block === this
    }

    /**
     * Schedules a tick if survival conditions change.
     */
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

    /** Returns the block used for Polymer visual replacement (oak fence). */
    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.OAK_FENCE

    /** Returns the custom model data value for this pale stem block. */
    override fun getPolymerBlockState(state: BlockState?) = this.polymerBlockState

    companion object {
        /** Fixed collision shape of the stem (4x16x4 centered box). */
        private val PALE_SHAPE: VoxelShape = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0)
    }
}