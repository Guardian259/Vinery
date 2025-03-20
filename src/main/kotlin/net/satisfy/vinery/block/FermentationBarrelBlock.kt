package net.satisfy.vinery.block

import eu.pb4.polymer.core.api.block.PolymerBlock
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.satisfy.vinery.block.entity.FermentationBarrelBlockEntity


class FermentationBarrelBlock(properties: Properties) : HorizontalDirectionalBlock(properties), EntityBlock, PolymerBlock {

    @Deprecated("Deprecated in Java")
    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult? {
        if (world.isClientSide) return InteractionResult.SUCCESS //TODO:Confirm this is still needed since polymer will make this fully server-sided

        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is FermentationBarrelBlockEntity) {
            if (player.isShiftKeyDown) {
                if (blockEntity.getFluidLevel() > 0) {
                    blockEntity.setFluidLevel(0)
                    blockEntity.setJuiceType("")
                    world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f)
                    world.sendBlockUpdated(pos, state, state, 3)
                    return InteractionResult.SUCCESS
                }
            } else {
                player.openMenu(blockEntity)
                return InteractionResult.CONSUME
            }
        }
        return super.use(state, world, pos, player, hand, hit)
    }

    @Deprecated("Deprecated in Java")
    override fun onRemove(state: BlockState, world: Level, pos: BlockPos, newState: BlockState, moved: Boolean) {
        if (!state.`is`(newState.block)) {
            val blockEntity: BlockEntity? = world.getBlockEntity(pos)
            if (blockEntity is FermentationBarrelBlockEntity) {
                if (world is ServerLevel)
                    Containers.dropContents(world, pos, blockEntity as Container)
                world.updateNeighbourForOutputSignal(pos, this)
            }
            super.onRemove(state, world, pos, newState, moved)
        }
    }

    @Nullable
    private fun <E : BlockEntity?, A : BlockEntity?> createTicker(
        givenType: BlockEntityType<A>, expectedType: BlockEntityType<E>, ticker: BlockEntityTicker<in E>
    ): BlockEntityTicker<A>? {
        return if (expectedType === givenType) ticker as BlockEntityTicker<A> else null
    }

    @Nullable
    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return null //TODO: Reimplement FermentationBarrelBlockEntity.tick
//        return if (level.isClientSide) null
//        else createTicker(blockEntityType, VineryEntityRegistry.FERMENTATION_BARREL_ENTITY,
//            BlockEntityTicker<E> { world: Level?, pos: BlockPos?, state1: BlockState?, blockEntity: E? ->
//                FermentationBarrelBlockEntity.tick(
//                    world,
//                    pos,
//                    blockEntity
//                )
//            })
    }
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity = FermentationBarrelBlockEntity(blockPos, blockState)

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState = defaultBlockState().setValue(FACING, ctx.horizontalDirection)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) { builder.add(FACING) }

    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.BARREL
}