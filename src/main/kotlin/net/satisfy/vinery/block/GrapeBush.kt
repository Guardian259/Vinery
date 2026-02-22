package net.satisfy.vinery.block

import eu.pb4.polymer.blocks.api.BlockModelType
import eu.pb4.polymer.blocks.api.PolymerBlockModel
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock
import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
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
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.Vinery.Companion.config
import net.satisfy.vinery.block.StemBlock.Companion
import net.satisfy.vinery.block.StemBlock.Companion.GRAPE
import net.satisfy.vinery.registry.VineryGrapeRegistry.GrapeTypeDefinition
import net.satisfy.vinery.registry.VineryGrapeRegistry.NONE
import kotlin.math.min

/**
 * A bush block representing grape plants with growth stages and harvestable fruit.
 * Supports bonemeal growth and random ticking for natural progression.
 * Implements [PolymerBlock] to mimic Minecraft's sweet berry bush visually.
 *
 * @param settings Block behavior properties
 * @param type The grape type definition specifying seeds and fruit
 */
open class GrapeBush(settings: Properties?, private val type: GrapeTypeDefinition) : BushBlock(settings!!), BonemealableBlock, PolymerBlock, PolymerTexturedBlock {

    /**
     * Returns the collision shape of the grape bush, a fixed 16x16x16 cube.
     */
    override fun getShape(state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext) = SHAPE

    /**
     * Returns the item stack dropped when the block is broken (grape seeds).
     */
    override fun getCloneItemStack(world: BlockGetter, pos: BlockPos, state: BlockState) = ItemStack(getType().getSeeds())

    /**
     * Handles player interaction with the bush. Harvests fruit if mature (age > 1),
     * resets to age 1 after harvesting. Bone meal is ignored if not fully grown.
     *
     * @return [InteractionResult.SUCCESS] if harvested, otherwise [InteractionResult.PASS] or parent result
     */
    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        val age = state.getValue(AGE)
        return when {
            age < 3 && player.getItemInHand(hand).`is`(Items.BONE_MEAL) -> InteractionResult.PASS
            age > 1 -> {
                val isFullyGrown = age == 3
                val fruitCount = world.random.nextInt(2) + if (isFullyGrown) 1 else 0
                Block.popResource(world, pos, ItemStack(getType().getFruit(), fruitCount))
                world.playSound(
                    null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                    1.0f, 0.8f + world.random.nextFloat() * 0.4f
                )
                world.setBlock(pos, state.setValue(AGE, 1), 2)
                InteractionResult.sidedSuccess(world.isClientSide)
            }
            else -> super.use(state, world, pos, player, hand, hit)
        }
    }

    /**
     * Updates the bush's growth stage randomly based on config-defined chance,
     * if light and placement conditions are met.
     */
    override fun randomTick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val age: Int = state.getValue(AGE)
        val growthChance: Double = config!!.getOrDefault("grapeGrowthChance", 0.5)
        if (age < 3 && random.nextDouble() < growthChance && canGrowPlace(world, pos, state)) {
            val newState: BlockState = state.setValue(AGE, age + 1)
            world.setBlock(pos, newState, 2)
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState))
        }
    }

    /**
     * Indicates if the block should tick randomly (true if age < 3).
     */
    override fun isRandomlyTicking(state: BlockState) = state.getValue(AGE) < 3

    /**
     * Checks if the bush can be fertilized with bonemeal (true if age < 3).
     */
    override fun isValidBonemealTarget(
        levelReader: LevelReader,
        blockPos: BlockPos,
        blockState: BlockState,
        bl: Boolean
    ) = blockState.getValue(AGE) < 3

    /**
     * Determines if bonemeal application succeeds (always true).
     */
    override fun isBonemealSuccess(world: Level, random: RandomSource, pos: BlockPos, state: BlockState) = true

    /**
     * Checks if the bush can grow at its current position based on light level.
     * Defaults to requiring brightness > 9.
     */
    open fun canGrowPlace(world: LevelReader, blockPos: BlockPos, blockState: BlockState?) = world.getRawBrightness(blockPos, 0) > 9

    /**
     * Verifies if the bush can survive at its position, combining growth and placement conditions.
     */
    @Deprecated("Deprecated in Java",
        ReplaceWith("canGrowPlace(world, blockPos, blockState) && this.mayPlaceOn(world.getBlockState(blockPos.below()), world, blockPos)")
    )
    override fun canSurvive(blockState: BlockState, world: LevelReader, blockPos: BlockPos): Boolean {
        return canGrowPlace(world, blockPos, blockState) && this.mayPlaceOn(
            world.getBlockState(blockPos.below()),
            world,
            blockPos
        )
    }

    /**
     * Checks if the bush can be placed on the given floor block (must be solid).
     */
    override fun mayPlaceOn(floor: BlockState, world: BlockGetter, pos: BlockPos) = floor.isSolidRender(world, pos)

    /** Returns the grape type definition for this bush. */
    private fun getType() = this.type

    /** The fruit item stack produced by this bush. */
    private val grapeType: ItemStack
        get() = ItemStack(getType().getFruit())

    /**
     * Applies bonemeal to increment the bush's age up to a maximum of 3.
     */
    override fun performBonemeal(world: ServerLevel, random: RandomSource, pos: BlockPos, state: BlockState) {
        val i = min(3.0, (state.getValue<Int>(AGE) + 1).toDouble()).toInt()
        world.setBlock(pos, state.setValue<Int, Int>(AGE, i), 2)
    }

    /**
     * Adds the AGE property to the block's state definition.
     */
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(AGE)
    }

    /**
     * A grape bush variant requiring high brightness (≥14) to grow.
     */
    class SavannaGrapeBush(settings: BlockBehaviour.Properties?, type: GrapeTypeDefinition) : GrapeBush(settings, type) {
        override fun canGrowPlace(world: LevelReader, blockPos: BlockPos, blockState: BlockState?) = world.getRawBrightness(blockPos, 0) >= 14
    }

    /**
     * A grape bush variant requiring moderate light (>4) and proximity to podzol,
     * coarse dirt, or grass blocks within a 4-block radius.
     */
    class TaigaGrapeBush(settings: BlockBehaviour.Properties?, type: GrapeTypeDefinition) : GrapeBush(settings, type) {
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
            } while (!(world.getBlockState(pos).block === Blocks.PODZOL || world.getBlockState(pos)
                    .block === Blocks.COARSE_DIRT || world.getBlockState(pos).block === Blocks.GRASS_BLOCK)
            )
            return true
        }

        /** Disables pathfinding through this bush. */
        @Deprecated("Deprecated in Java", ReplaceWith("false"))
        override fun isPathfindable(
            arg: BlockState,
            arg2: BlockGetter,
            arg3: BlockPos,
            arg4: PathComputationType
        ) = false
    }

    /** Returns the block used for Polymer visual replacement (sweet berry bush). */
    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.SWEET_BERRY_BUSH

    /** Maps this block’s state to the Polymer block’s state, preserving AGE. */
    override fun getPolymerBlockState(state: BlockState?): BlockState = Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(AGE, state!!.getValue(AGE))

    companion object {
        /** Block state property for growth stage (0-3). */
        val AGE: IntegerProperty = BlockStateProperties.AGE_3

        /** Fixed collision shape of the bush (16x16x16 cube). */
        private val SHAPE: VoxelShape = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    }
}
