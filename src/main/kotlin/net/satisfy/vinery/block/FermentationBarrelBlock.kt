package net.satisfy.vinery.block

import eu.pb4.polymer.core.api.block.PolymerBlock
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition


class FermentationBarrelBlock(properties: Properties) : HorizontalDirectionalBlock(properties), PolymerBlock {

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState = defaultBlockState().setValue(FACING, ctx.horizontalDirection)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) { builder.add(FACING) }

    override fun getPolymerBlock(p0: BlockState?): Block = Blocks.BARREL
}