package satisfyu.vinery.block.grape.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class GrapeBushBlock extends GrapePlantBlock {
    public GrapeBushBlock(Properties properties) {
        super(properties);
    }
    
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 3;
    }
    
    protected boolean mayPlaceOn(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return (state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND)) && state.isSolidRender(blockGetter, pos);
    }
    
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState blockState2, LevelAccessor world, BlockPos blockPos, BlockPos blockPos2) {
        if (!state.canSurvive(world, blockPos)) return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, blockState2, world, blockPos, blockPos2);
    }
    
    @Override
    public boolean canSurvive(BlockState blockState, LevelReader world, BlockPos blockPos) {
        return world.getRawBrightness(blockPos, 0) > 9 && mayPlaceOn(world.getBlockState(blockPos.below()), world, blockPos);
    }
    
    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.getFluidState().isEmpty();
    }
    
    @Override
    public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
        if (pathComputationType == PathComputationType.AIR && !this.hasCollision) return true;
        return super.isPathfindable(blockState, blockGetter, blockPos, pathComputationType);
    }
}
