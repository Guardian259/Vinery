package satisfyu.vinery.block.grape.plant;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

public class GrapeVineBlock extends GrapePlantBlock {
    public static final BooleanProperty STERILIZED;
    public static final BooleanProperty UP;
    public static final BooleanProperty NORTH;
    public static final BooleanProperty EAST;
    public static final BooleanProperty SOUTH;
    public static final BooleanProperty WEST;
    public static final Map<Direction, BooleanProperty> DIRECTIONAL_PROPERTIES;
    
    private static final VoxelShape UP_AABB;
    private static final VoxelShape WEST_AABB;
    private static final VoxelShape EAST_AABB;
    private static final VoxelShape NORTH_AABB;
    private static final VoxelShape SOUTH_AABB;
    
    private final Map<BlockState, VoxelShape> shapesCache;
    
    static {
        STERILIZED = BooleanProperty.create("sterilized");
        UP = PipeBlock.UP;
        NORTH = PipeBlock.NORTH;
        EAST = PipeBlock.EAST;
        SOUTH = PipeBlock.SOUTH;
        WEST = PipeBlock.WEST;
        DIRECTIONAL_PROPERTIES = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(entry -> entry.getKey() != Direction.DOWN).collect(Util.toMap());
        UP_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
        WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
        EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
        SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    }
    
    public GrapeVineBlock(Properties properties) {
        super(properties);
        shapesCache = getShapeForEachState(GrapeVineBlock::calculateShape);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (hand == InteractionHand.OFF_HAND) return super.use(state, world, pos, player, hand, blockHitResult);
        
        if (player.getItemInHand(hand).is(Items.SHEARS)) {
            world.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.setBlockAndUpdate(pos, state.cycle(STERILIZED));
            return InteractionResult.PASS;
        }
        
        return super.use(state, world, pos, player, hand, blockHitResult);
    }
    
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(STERILIZED);
    }
    
    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return shapesCache.get(blockState);
    }
    
    @Override
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN)
            return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
        
        var updatedState = getUpdatedState(blockState, levelAccessor, blockPos);
        if (!hasFaces(updatedState)) return Blocks.AIR.defaultBlockState();
        return updatedState;
    }
    
    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return true;
    }
    
    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        var newBlockState = getUpdatedState(blockState, levelReader, blockPos);
        return hasFaces(newBlockState);
    }
    
    @Override
    public void randomTick(BlockState blockState, ServerLevel world, BlockPos blockPos, RandomSource random) {
        super.randomTick(blockState, world, blockPos, random);
        if (random.nextInt(4) == 0) return;
        
        var direction = Direction.getRandom(random);
        var directionBlockPos = blockPos.relative(direction);
        var directionBlockState = world.getBlockState(directionBlockPos);
        var directionClockwise = direction.getClockWise();
        var directionCounterClockwise = direction.getCounterClockWise();
        
        if (direction.getAxis().isHorizontal() && !blockState.getValue(DIRECTIONAL_PROPERTIES.get(direction))) {
            if (canSpread(world, blockPos)) return;
            
            if (directionBlockState.isAir()) {
                var growClockwise = blockState.getValue(DIRECTIONAL_PROPERTIES.get(directionClockwise));
                var growCounterClockwise = blockState.getValue(DIRECTIONAL_PROPERTIES.get(directionCounterClockwise));
                var clockwiseBlockPos = directionBlockPos.relative(directionClockwise);
                var counterClockwiseBlockPos = directionBlockPos.relative(directionCounterClockwise);
                
                if (growClockwise && isAcceptableNeighbour(world, clockwiseBlockPos, directionClockwise)) {
                    world.setBlock(directionBlockPos, defaultBlockState().setValue(DIRECTIONAL_PROPERTIES.get(directionClockwise), true), 2);
                } else if (growCounterClockwise && isAcceptableNeighbour(world, counterClockwiseBlockPos, directionCounterClockwise)) {
                    world.setBlock(directionBlockPos, defaultBlockState().setValue(DIRECTIONAL_PROPERTIES.get(directionCounterClockwise), true), 2);
                } else {
                    var oppositeDirection = direction.getOpposite();
                    
                    if (growClockwise && world.isEmptyBlock(clockwiseBlockPos) && isAcceptableNeighbour(world, blockPos.relative(directionClockwise), oppositeDirection)) {
                        world.setBlock(clockwiseBlockPos, defaultBlockState().setValue(DIRECTIONAL_PROPERTIES.get(oppositeDirection), true), 2);
                    } else if (growCounterClockwise && world.isEmptyBlock(counterClockwiseBlockPos) && isAcceptableNeighbour(world, blockPos.relative(directionCounterClockwise), oppositeDirection)) {
                        world.setBlock(counterClockwiseBlockPos, defaultBlockState().setValue(DIRECTIONAL_PROPERTIES.get(oppositeDirection), true), 2);
                    } else if (random.nextFloat() < 0.05f && isAcceptableNeighbour(world, directionBlockPos.above(), Direction.UP)) {
                        world.setBlock(directionBlockPos, defaultBlockState().setValue(UP, true), 2);
                    }
                }
            } else if (isAcceptableNeighbour(world, directionBlockPos, direction)) {
                world.setBlock(blockPos, blockState.setValue(DIRECTIONAL_PROPERTIES.get(direction), true), 2);
            }
            
            return;
        }
        
        if (direction == Direction.UP && blockPos.getY() < world.getMaxBuildHeight() - 1) {
            if (canSupportAtFace(world, blockPos, direction)) {
                world.setBlock(blockPos, blockState.setValue(UP, true), 2);
                return;
            }
            
            var aboveBlockPos = blockPos.above();
            if (world.isEmptyBlock(aboveBlockPos) || !canSpread(world, blockPos)) return;
            
            var clockwiseBlockState = blockState;
            for (var horizontalDirection : Direction.Plane.HORIZONTAL) {
                if (!random.nextBoolean() && isAcceptableNeighbour(world, aboveBlockPos.relative(horizontalDirection), directionClockwise)) {
                    continue;
                }
                
                clockwiseBlockState = clockwiseBlockState.setValue(DIRECTIONAL_PROPERTIES.get(horizontalDirection), false);
            }
            
            if (hasHorizontalConnection(clockwiseBlockState)) {
                world.setBlock(aboveBlockPos, clockwiseBlockState, 2);
            }
            
            return;
        }
        
        if (blockPos.getY() > world.getMinBuildHeight()) {
            directionBlockPos = blockPos.below();
            directionBlockState = world.getBlockState(directionBlockPos);
            if (directionBlockState.isAir() || directionBlockState.is(this)) {
                BlockState blockState4 = directionBlockState.isAir() ? this.defaultBlockState() : directionBlockState;
                BlockState blockState5 = copyRandomFaces(blockState, blockState4, random);
                if (blockState4 != blockState5 && this.hasHorizontalConnection(blockState5)) {
                    world.setBlock(directionBlockPos, blockState5, 2);
                }
            }
        }
    }
    
    @Override
    public boolean canBeReplaced(BlockState newBlockState, BlockPlaceContext blockPlaceContext) {
        var currentBlockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
        if (currentBlockState.is(this)) return countFaces(currentBlockState) < DIRECTIONAL_PROPERTIES.size();
        return super.canBeReplaced(newBlockState, blockPlaceContext);
    }
    
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        var blockState = blockPlaceContext.getLevel().getBlockState(blockPlaceContext.getClickedPos());
        if (!blockState.is(this)) return null;
        
        for (Direction direction : blockPlaceContext.getNearestLookingDirections()) {
            if (direction == Direction.DOWN) continue;
            
            var directionProperty = DIRECTIONAL_PROPERTIES.get(direction);
            if (blockState.getValue(directionProperty) || !canSupportAtFace(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos(), direction)) {
                continue;
            }
            
            return blockState.setValue(directionProperty, true);
        }
        
        return blockState;
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STERILIZED, UP, NORTH, EAST, SOUTH, WEST);
    }
    
    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180 ->
                    blockState.setValue(NORTH, blockState.getValue(SOUTH)).setValue(EAST, blockState.getValue(WEST)).setValue(SOUTH, blockState.getValue(NORTH)).setValue(WEST, blockState.getValue(EAST));
            case COUNTERCLOCKWISE_90 ->
                    blockState.setValue(NORTH, blockState.getValue(EAST)).setValue(EAST, blockState.getValue(SOUTH)).setValue(SOUTH, blockState.getValue(WEST)).setValue(WEST, blockState.getValue(NORTH));
            case CLOCKWISE_90 ->
                    blockState.setValue(NORTH, blockState.getValue(WEST)).setValue(EAST, blockState.getValue(NORTH)).setValue(SOUTH, blockState.getValue(EAST)).setValue(WEST, blockState.getValue(SOUTH));
        }
        
        return blockState;
    }
    
    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT ->
                    blockState.setValue(NORTH, blockState.getValue(SOUTH)).setValue(SOUTH, blockState.getValue(NORTH));
            case FRONT_BACK ->
                    blockState.setValue(EAST, blockState.getValue(WEST)).setValue(WEST, blockState.getValue(EAST));
        }
        
        return blockState;
    }
    
    private boolean hasHorizontalConnection(BlockState blockState) {
        return blockState.getValue(NORTH) || blockState.getValue(EAST) || blockState.getValue(SOUTH) || blockState.getValue(WEST);
    }
    
    private BlockState copyRandomFaces(BlockState blockState, BlockState blockState2, RandomSource randomSource) {
        Iterator var4 = Direction.Plane.HORIZONTAL.iterator();
        
        while (var4.hasNext()) {
            Direction direction = (Direction) var4.next();
            if (randomSource.nextBoolean()) {
                BooleanProperty booleanProperty = DIRECTIONAL_PROPERTIES.get(direction);
                if (blockState.getValue(booleanProperty)) {
                    blockState2 = blockState2.setValue(booleanProperty, true);
                }
            }
        }
        
        return blockState2;
    }
    
    private boolean hasFaces(BlockState blockState) {
        return this.countFaces(blockState) > 0;
    }
    
    private long countFaces(BlockState blockState) {
        return DIRECTIONAL_PROPERTIES.values().stream().filter(blockState::getValue).count();
    }
    
    private BlockState getUpdatedState(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        var aboveBlockPos = blockPos.above();
        
        if (blockState.getValue(UP)) {
            blockState = blockState.setValue(UP, isAcceptableNeighbour(blockGetter, aboveBlockPos, Direction.DOWN));
        }
        
        var newBlockState = blockGetter.getBlockState(aboveBlockPos);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            var directionalProperty = DIRECTIONAL_PROPERTIES.get(direction);
            if (!blockState.getValue(directionalProperty)) continue;
            
            boolean propertyValue = canSupportAtFace(blockGetter, blockPos, direction);
            if (!propertyValue) propertyValue = newBlockState.is(this) && newBlockState.getValue(directionalProperty);
            blockState = blockState.setValue(directionalProperty, propertyValue);
        }
        
        return blockState;
    }
    
    private boolean canSupportAtFace(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        if (direction == Direction.DOWN) return false;
        if (isAcceptableNeighbour(blockGetter, blockPos.relative(direction), direction)) return true;
        
        if (direction.getAxis() != Direction.Axis.Y) {
            var directionProperty = DIRECTIONAL_PROPERTIES.get(direction);
            var aboveBlockState = blockGetter.getBlockState(blockPos.above());
            return aboveBlockState.is(this) && aboveBlockState.getValue(directionProperty);
        }
        
        return false;
    }
    
    private boolean canSpread(BlockGetter blockGetter, BlockPos blockPos) {
        var area = BlockPos.betweenClosed(
                blockPos.getX() - 4,
                blockPos.getY() - 1,
                blockPos.getZ() - 4,
                blockPos.getX() + 4,
                blockPos.getY() + 1,
                blockPos.getZ() + 4
        );
        
        int nearbyAttempts = 5;
        for (BlockPos pos : area) {
            if (!blockGetter.getBlockState(pos).is(this) || --nearbyAttempts > 0) continue;
            return false;
        }
        
        return true;
    }
    
    public static boolean isAcceptableNeighbour(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return MultifaceBlock.canAttachTo(blockGetter, direction, blockPos, blockGetter.getBlockState(blockPos));
    }
    
    private static VoxelShape calculateShape(BlockState blockState) {
        VoxelShape voxelShape = Shapes.empty();
        if (blockState.getValue(UP)) voxelShape = UP_AABB;
        if (blockState.getValue(NORTH)) voxelShape = Shapes.or(voxelShape, NORTH_AABB);
        if (blockState.getValue(SOUTH)) voxelShape = Shapes.or(voxelShape, SOUTH_AABB);
        if (blockState.getValue(EAST)) voxelShape = Shapes.or(voxelShape, EAST_AABB);
        if (blockState.getValue(WEST)) voxelShape = Shapes.or(voxelShape, WEST_AABB);
        return voxelShape.isEmpty() ? Shapes.block() : voxelShape;
    }
}
