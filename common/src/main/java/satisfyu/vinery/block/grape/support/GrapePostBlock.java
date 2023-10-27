package satisfyu.vinery.block.grape.support;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class GrapePostBlock extends GrapeSupportBlock {
    private static final VoxelShape SHAPE = Block.box(6.0, 0, 6.0, 10.0, 16.0, 10.0);
    
    public GrapePostBlock(Properties settings) {
        super(settings);
    }
    
    public boolean hasBase(BlockGetter world, BlockPos pos) {
        return world.getBlockState(pos.below()).getBlock() == this;
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState;
        blockState = this.defaultBlockState();
        Level world = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        if (blockState.canSurvive(ctx.getLevel(), ctx.getClickedPos())) {
            ItemStack placeStack = Objects.requireNonNull(ctx.getPlayer()).getItemInHand(ctx.getHand());
            if (placeStack != null && (ctx.getPlayer().isCreative() || placeStack.getCount() >= 2) && world.getBlockState(blockPos.below()).getBlock() != this && blockPos.getY() < world.getMaxBuildHeight() - 1 && world.getBlockState(blockPos.above()).canBeReplaced(ctx)) {
                world.setBlock(blockPos.above(), this.defaultBlockState(), 3);
                placeStack.shrink(1);
            }
            return blockState;
        }
        return null;
    }
    
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (isMature(state) && hasBase(world, pos) && state.getValue(AGE) > 0) {
            super.randomTick(state, world, pos, random);
        }
    }
    
    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).isRedstoneConductor(world, pos) || world.getBlockState(pos.below()).getBlock() == this;
    }
    
    @Override
    public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean isClient) {
        return hasBase(world, pos) && super.isValidBonemealTarget(world, pos, state, isClient);
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, BlockGetter world, List<Component> tooltip, TooltipFlag tooltipContext) {
        tooltip.add(Component.translatable("block.vinery.stem.tooltip").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("item.vinery.stem2.tooltip"));
        } else {
            tooltip.add(Component.translatable("item.vinery.faucet.tooltip"));
        }
    }
}
