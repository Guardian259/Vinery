package satisfyu.vinery.block.grape.support;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GrapeLatticeBlock extends GrapeSupportBlock {
    private static final VoxelShape LATTICE_SHAPE_N = Block.box(0, 0, 15.0, 16.0, 16.0, 16.0);
    private static final VoxelShape LATTICE_SHAPE_E = Block.box(0, 0, 0, 1.0, 16.0, 16.0);
    private static final VoxelShape LATTICE_SHAPE_S = Block.box(0, 0, 0, 16.0, 16.0, 1.0);
    private static final VoxelShape LATTICE_SHAPE_W = Block.box(15.0, 0, 0, 16.0, 16.0, 16.0);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    
    public GrapeLatticeBlock(Properties settings) {
        super(settings);
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> LATTICE_SHAPE_E;
            case SOUTH -> LATTICE_SHAPE_S;
            case WEST -> LATTICE_SHAPE_W;
            default -> LATTICE_SHAPE_N;
        };
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var blockState = ctx.getClickedFace().getAxis().isHorizontal()
                ? defaultBlockState().setValue(FACING, ctx.getClickedFace())
                : defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
        
        return blockState.canSurvive(ctx.getLevel(), ctx.getClickedPos()) ? blockState : null;
    }
    
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (isMature(state) && state.getValue(AGE) > 0) {
            super.randomTick(state, world, pos, random);
        }
    }
    
    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        VoxelShape shape;
        Direction direction;
        
        switch (state.getValue(FACING).getOpposite()) {
            case EAST -> {
                shape = world.getBlockState(pos.east()).getShape(world, pos.east());
                direction = Direction.WEST;
            }
            
            case SOUTH -> {
                shape = world.getBlockState(pos.south()).getShape(world, pos.south());
                direction = Direction.NORTH;
            }
            
            case WEST -> {
                shape = world.getBlockState(pos.west()).getShape(world, pos.west());
                direction = Direction.EAST;
            }
            
            default -> {
                shape = world.getBlockState(pos.north()).getShape(world, pos.north());
                direction = Direction.SOUTH;
            }
        }
        
        return Block.isFaceFull(shape, direction);
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, BlockGetter world, List<Component> tooltip, TooltipFlag tooltipContext) {
        tooltip.add(Component.translatable("block.vinery.lattice.tooltip").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("item.vinery.lattice2.tooltip"));
        } else {
            tooltip.add(Component.translatable("item.vinery.faucet.tooltip"));
        }
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
}
