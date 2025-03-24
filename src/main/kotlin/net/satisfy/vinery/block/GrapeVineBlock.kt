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
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.BonemealableBlock
import net.minecraft.world.level.block.VineBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.registry.VineryGrapeRegistry.GrapeTypeDefinition
import net.satisfy.vinery.registry.VineryGrapeRegistry.retrieveGrapeSetByType
import kotlin.math.min

/**
 * A vine block representing grapevines with growth stages and harvestable fruit.
 * Supports sterilization with shears, bonemeal growth, and random ticking.
 * Implements [PolymerBlock] to mimic Minecraft's vine visually.
 *
 * @param settings Block behavior properties
 * @param type The grape type definition specifying fruit
 */
class GrapeVineBlock(settings: Properties?, val type: GrapeTypeDefinition) : VineBlock(settings!!), BonemealableBlock, PolymerBlock, PolymerTexturedBlock {

    private var polymerBlockState: BlockState

    init {
        this.polymerBlockState = PolymerBlockResourceUtils.requestBlock(
            BlockModelType.TRANSPARENT_BLOCK,
            PolymerBlockModel.of(ResourceLocation(MODID, "block/${type.serializedName}")) //TODO:Resource Location Mismatch
        )!!
    }

    /**
     * Handles player interaction. Sterilizes with shears, harvests fruit if mature (age > 1),
     * resets to age 1 after harvesting. Bone meal is ignored if not fully grown.
     *
     * @return [InteractionResult.SUCCESS] if harvested, [InteractionResult.PASS] if sterilized or bone meal used, else parent result
     */
    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (hand == InteractionHand.OFF_HAND) return super.use(state, world, pos, player, hand, hit)

        val heldItem = player.getItemInHand(hand)
        return when {
            heldItem.`is`(Items.SHEARS) -> {
                world.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f)
                world.setBlockAndUpdate(pos, state.cycle(STERILIZED))
                InteractionResult.PASS
            }
            else -> {
                val age = state.getValue(AGE)
                val isFullyGrown = age == 3
                when {
                    !isFullyGrown && heldItem.`is`(Items.BONE_MEAL) -> InteractionResult.PASS
                    age > 1 -> {
                        val fruitCount = world.random.nextInt(2) + if (isFullyGrown) 1 else 0
                        retrieveGrapeSetByType(type)?.grape?.let { grape ->
                            popResource(world, pos, ItemStack(grape, fruitCount))
                        }
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
        }
    }

    /**
     * Updates growth stage randomly (1/5 chance) if not sterilized, age < 3, and light level ≥ 9 above.
     */
    override fun randomTick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val i = state.getValue(AGE)
        if (i < 3 && random.nextInt(5) == 0 && world.getRawBrightness(pos.above(), 0) >= 9) {
            val blockState = state.setValue(AGE, i + 1)
            world.setBlock(pos, blockState, 2)
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState))
        }
        super.randomTick(state, world, pos, random)
    }

    /**
     * Indicates if the block ticks randomly (true if not sterilized).
     */
    override fun isRandomlyTicking(state: BlockState) = !state.getValue(STERILIZED)

    /**
     * Checks if bonemeal can be applied (true if age < 3).
     */
    override fun isValidBonemealTarget(
        levelReader: LevelReader,
        blockPos: BlockPos,
        blockState: BlockState,
        bl: Boolean
    ) = blockState.getValue(AGE) < 3

    /**
     * Confirms bonemeal application success (always true).
     */
    override fun isBonemealSuccess(world: Level, random: RandomSource, pos: BlockPos, state: BlockState) = true

    /**
     * Increments age with bonemeal, up to a maximum of 3.
     */
    override fun performBonemeal(world: ServerLevel, random: RandomSource, pos: BlockPos, state: BlockState) {
        val i = min(3.0, (state.getValue(AGE) + 1).toDouble()).toInt()
        world.setBlock(pos, state.setValue(AGE, i), 2)
    }

    /**
     * Adds AGE and STERILIZED properties to the block's state definition.
     */
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(AGE, STERILIZED)
    }

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(STERILIZED, false).setValue(UP, false).setValue(NORTH, false).setValue(
                EAST, false
            ).setValue(SOUTH, false).setValue(WEST, false)
        )
    }

    companion object {
        /** Growth stage property (0-3). */
        val AGE: IntegerProperty = BlockStateProperties.AGE_3

        /** Indicates if the vine is sterilized (prevents growth). */
        val STERILIZED: BooleanProperty = BooleanProperty.create("sterilized")
    }

    /** Returns the block used for Polymer visual replacement (vanilla vine). */
    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.VINE

    /** Returns the custom model data value for this grapvine block type. */
    override fun getPolymerBlockState(state: BlockState?) = this.polymerBlockState

}
