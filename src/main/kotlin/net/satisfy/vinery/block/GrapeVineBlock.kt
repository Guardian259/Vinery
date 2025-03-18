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
import net.satisfy.vinery.util.GrapeType
import net.satisfy.vinery.util.VineryGrapeRegistry
import kotlin.math.min

class GrapeVineBlock(settings: Properties?, val type: GrapeType) : VineBlock(settings!!), BonemealableBlock, PolymerBlock {
    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (hand == InteractionHand.OFF_HAND) {
            return super.use(state, world, pos, player, hand, hit)
        }
        if (player.getItemInHand(hand).`is`(Items.SHEARS)) {
            world.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f)
            world.setBlockAndUpdate(pos, state.cycle(STERILIZED))
            return InteractionResult.PASS
        }
        val i = state.getValue(AGE)
        val bl = i == 3
        if (!bl && player.getItemInHand(hand).`is`(Items.BONE_MEAL)) {
            return InteractionResult.PASS
        } else if (i > 1) {
            val x = world.random.nextInt(2)
            (if (this.type === VineryGrapeRegistry.JUNGLE_RED.type)
                VineryGrapeRegistry.JUNGLE_RED.grape
            else
                VineryGrapeRegistry.JUNGLE_WHITE.grape)?.let {
                    ItemStack(it, x + (if (bl) 1 else 0)) }?.let {
                        popResource(world, pos, it) }
            world.playSound(
                null,
                pos,
                SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                SoundSource.BLOCKS,
                1.0f,
                0.8f + world.random.nextFloat() * 0.4f
            )
            world.setBlock(pos, state.setValue(AGE, 1), 2)
            return InteractionResult.sidedSuccess(world.isClientSide)
        } else {
            return super.use(state, world, pos, player, hand, hit)
        }
    }

    override fun randomTick(state: BlockState, world: ServerLevel, pos: BlockPos, random: RandomSource) {
        val i = state.getValue(AGE)
        if (i < 3 && random.nextInt(5) == 0 && world.getRawBrightness(pos.above(), 0) >= 9) {
            val blockState = state.setValue(AGE, i + 1)
            world.setBlock(pos, blockState, 2)
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState))
        }
        super.randomTick(state, world, pos, random)
    }

    override fun isRandomlyTicking(state: BlockState): Boolean {
        return !state.getValue(STERILIZED)
    }


    override fun isValidBonemealTarget(
        levelReader: LevelReader,
        blockPos: BlockPos,
        blockState: BlockState,
        bl: Boolean
    ): Boolean {
        return blockState.getValue(AGE) < 3
    }

    override fun isBonemealSuccess(world: Level, random: RandomSource, pos: BlockPos, state: BlockState): Boolean {
        return true
    }

    override fun performBonemeal(world: ServerLevel, random: RandomSource, pos: BlockPos, state: BlockState) {
        val i = min(3.0, (state.getValue(AGE) + 1).toDouble())
            .toInt()
        world.setBlock(pos, state.setValue(AGE, i), 2)
    }

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
        val AGE: IntegerProperty = BlockStateProperties.AGE_3
        val STERILIZED: BooleanProperty = BooleanProperty.create("sterilized")
    }

    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.VINE
}
