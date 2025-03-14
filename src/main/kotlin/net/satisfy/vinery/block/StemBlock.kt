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
import net.minecraft.world.item.Item
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
import net.satisfy.vinery.util.GeneralUtil
import net.satisfy.vinery.util.GrapeProperty
import net.satisfy.vinery.util.GrapeType
import net.satisfy.vinery.util.VineryRegistry
import org.jetbrains.annotations.NotNull

abstract class StemBlock(settings: Properties?) : Block(settings), BonemealableBlock, PolymerBlock {
    fun dropGrapes(world: Level, state: BlockState, pos: BlockPos?, direction: Direction?) {
        val x: Int = 1 + world.random.nextInt(if (this.isMature(state)) 2 else 1)
        val bonus = if (this.isMature(state)) 2 else 1
        val grape: Item = state.getValue(GRAPE).getFruit()
        val stack: ItemStack = ItemStack(grape, x + bonus)

        if (direction == null) popResource(world, pos, stack)
        else GeneralUtil.popResourceFromFace(world, pos!!, direction, stack)

        world.playSound(
            null,
            pos,
            SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
            SoundSource.BLOCKS,
            1.0f,
            0.8f + world.random.nextFloat() * 0.4f
        )
    }

    fun dropGrapeSeeds(world: Level?, state: BlockState, pos: BlockPos?, direction: Direction?) {
        val grape: Item = state.getValue(GRAPE).getSeeds()
        val stack: ItemStack = ItemStack(grape)

        if (direction == null) popResource(world, pos, stack)
        else GeneralUtil.popResourceFromFace(world!!, pos!!, direction, stack)
    }

    @Suppress("deprecation")
    @NotNull
    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos?,
        player: Player?,
        hand: InteractionHand?,
        hit: BlockHitResult
    ): InteractionResult {
        val age: Int = state.getValue(AGE)
        if (age > 3) {
            dropGrapes(world, state, pos, hit.getDirection())
            world.setBlock(pos, state.setValue(AGE, 2), 2)
            return InteractionResult.sidedSuccess(world.isClientSide)
        } else {
            return InteractionResult.PASS
        }
    }

    override fun playerWillDestroy(world: Level, pos: BlockPos?, state: BlockState, player: Player?) {
        if (state.getValue(AGE) > 2) {
            dropGrapes(world, state, pos, null)
        }
        super.playerWillDestroy(world, pos, state, player)
    }

    fun hasTrunk(world: Level, pos: BlockPos): Boolean {
        return world.getBlockState(pos.below()).getBlock() === this
    }

    private fun boneMealGrow(world: Level, state: BlockState, pos: BlockPos) {
        var j: Int
        var age: Int = state.getValue(AGE) + Mth.nextInt(world.getRandom(), 1, 2)
        if (age > (4.also { j = it })) {
            age = j
        }
        world.setBlock(pos, this.withAge(state, age, state.getValue(GRAPE)), Block.UPDATE_CLIENTS)
    }

    init {
        this.registerDefaultState(this.defaultBlockState().setValue(GRAPE, VineryRegistry.NONE).setValue(AGE, 0))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(AGE, GRAPE)
    }

    fun isMature(state: BlockState): Boolean {
        return state.getValue(AGE) >= 4
    }

    override fun isValidBonemealTarget(levelReader: LevelReader, blockPos: BlockPos, state: BlockState, bl: Boolean): Boolean {
        return !isMature(state) && levelReader.getBlockState(blockPos.below())
            .getBlock() === this && state.getValue(AGE) > 0
    }

    override fun isBonemealSuccess(world: Level?, random: RandomSource?, pos: BlockPos?, state: BlockState?): Boolean {
        return true
    }

    override fun performBonemeal(world: ServerLevel, random: RandomSource?, pos: BlockPos, state: BlockState) {
        boneMealGrow(world, state, pos)
    }

    fun withAge(state: BlockState, age: Int, type: GrapeType?): BlockState {
        return state.setValue(AGE, age).setValue(GRAPE, type!!)
    }

    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.OAK_FENCE

    companion object {
        val GRAPE: GrapeProperty = GrapeProperty.create("grape")
        val AGE: IntegerProperty = BlockStateProperties.AGE_4
    }
}
