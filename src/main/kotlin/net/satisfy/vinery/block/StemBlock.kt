package net.satisfy.vinery.block

import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.BonemealableBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.BlockHitResult
import net.satisfy.vinery.registry.VineryGrapeRegistry
import org.jetbrains.annotations.NotNull

/**
 * Abstract block representing a grapevine stem with growth stages and harvestable grapes.
 * Supports bonemeal growth and drops grapes/seeds when mature or destroyed.
 * Implements [PolymerBlock] to mimic an oak fence visually.
 *
 * @param settings Block behavior properties
 */
abstract class StemBlock(settings: Properties?) : Block(settings!!), BonemealableBlock, PolymerBlock {

    /**
     * Drops grapes based on maturity (1-3 if mature, 1-2 if not) in the specified direction or at the block position.
     */
    fun dropGrapes(world: Level, state: BlockState, pos: BlockPos?, direction: Direction?) {
        val isMature = isMature(state)
        val grapeCount = 1 + world.random.nextInt(if (isMature) 2 else 1) + if (isMature) 2 else 1
        val stack = ItemStack(state.getValue(GRAPE).getFruit(), grapeCount)

        if (direction == null) popResource(world, pos!!, stack)
        else popResourceFromFace(world, pos!!, direction, stack)

        world.playSound(
            null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
            1.0f, 0.8f + world.random.nextFloat() * 0.4f
        )
    }

    /**
     * Drops a single grape seed in the specified direction or at the block position.
     */
    fun dropGrapeSeeds(world: Level?, state: BlockState, pos: BlockPos?, direction: Direction?) {
        val grape = state.getValue(GRAPE).getSeeds()
        val stack = ItemStack(grape)

        if (direction == null) popResource(world!!, pos!!, stack)
        else popResourceFromFace(world!!, pos!!, direction, stack)
    }

    /**
     * Handles player interaction. Harvests grapes if age > 3, resetting age to 2.
     *
     * @return [InteractionResult.SUCCESS] if harvested, [InteractionResult.PASS] otherwise
     */
    @Deprecated("Deprecated in Java")
    @NotNull
    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult? {
        val age: Int = state.getValue(AGE)
        if (age > 3) {
            dropGrapes(world, state, pos, hit.direction)
            world.setBlock(pos, state.setValue(AGE, 2), 2)
            return InteractionResult.sidedSuccess(world.isClientSide)
        } else {
            return InteractionResult.PASS
        }
    }

    /**
     * Drops grapes when the block is destroyed by a player if age > 2.
     */
    override fun playerWillDestroy(world: Level, pos: BlockPos, state: BlockState, player: Player) {
        if (state.getValue(AGE) > 2) dropGrapes(world, state, pos, null)
        super.playerWillDestroy(world, pos, state, player)
    }

    /**
     * Checks if the block below is also a stem block (indicating a trunk).
     */
    fun hasTrunk(world: Level, pos: BlockPos) = world.getBlockState(pos.below()).block === this

    /**
     * Increments age by 1-2 with bonemeal, capping at 4.
     */
    private fun boneMealGrow(world: Level, state: BlockState, pos: BlockPos) {
        var j: Int
        var age: Int = state.getValue(AGE) + Mth.nextInt(world.getRandom(), 1, 2)
        if (age > (4.also { j = it })) {
            age = j
        }
        world.setBlock(pos, this.withAge(state, age, state.getValue(GRAPE)), UPDATE_CLIENTS)
    }

    init { this.registerDefaultState(this.defaultBlockState().setValue(GRAPE, VineryGrapeRegistry.NONE).setValue(AGE, 0)) }

    /**
     * Adds AGE and GRAPE properties to the block's state definition.
     */
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) { builder.add(AGE, GRAPE) }

    /**
     * Returns true if the stem is mature (age ≥ 4).
     */
    fun isMature(state: BlockState): Boolean {
        return state.getValue(AGE) >= 4
    }

    /**
     * Checks if bonemeal can be applied (not mature, has trunk, age > 0).
     */
    override fun isValidBonemealTarget(levelReader: LevelReader, blockPos: BlockPos, state: BlockState, bl: Boolean) =
        !isMature(state) && levelReader.getBlockState(blockPos.below())
            .block === this && state.getValue(AGE) > 0

    /**
     * Confirms bonemeal application success (always true).
     */
    override fun isBonemealSuccess(world: Level, random: RandomSource, pos: BlockPos, state: BlockState) = true

    /**
     * Applies bonemeal to grow the stem.
     */
    override fun performBonemeal(world: ServerLevel, random: RandomSource, pos: BlockPos, state: BlockState) {
        boneMealGrow(world, state, pos)
    }

    /**
     * Updates the block state with a new age and grape type.
     */
    fun withAge(state: BlockState, age: Int, type: VineryGrapeRegistry.GrapeTypeDefinition?): BlockState = state.setValue(AGE, age).setValue(GRAPE, type!!)

    /** Returns the block used for Polymer visual replacement (oak fence). */
    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.OAK_FENCE

    companion object {
        /** Property for grape type. */
        val GRAPE: VineryGrapeRegistry.GrapeProperty = VineryGrapeRegistry.GrapeProperty

        /** Growth stage property (0-4). */
        val AGE: IntegerProperty = BlockStateProperties.AGE_4
    }
}