package satisfyu.vinery.block.grape;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import satisfyu.vinery.item.grape.GrapeModifier;
import satisfyu.vinery.item.grape.GrapeProperties;
import satisfyu.vinery.item.grape.GrapeType;

import java.util.Optional;

public class GrapeBushBlock extends BushBlock implements GrapePlantBlock, BonemealableBlock {
    public static final IntegerProperty AGE;
    private static final VoxelShape SHAPE;
    
    private Optional<GrapeModifier> grapeModifier;
    private GrapeProperties grapeProperties;
    public GrapeType type;
    private int chance;

    public GrapeBushBlock(Properties settings, GrapeType type, GrapeProperties grapeProperties, GrapeModifier grapeModifier) {
        this(settings, type, grapeProperties, grapeModifier, 5);
    }
    
    public GrapeBushBlock(Properties settings, GrapeType type, GrapeProperties grapeProperties) {
        this(settings, type, grapeProperties, grapeModifier, 5);
    }

    public GrapeBushBlock(Properties settings, GrapeType type, GrapeProperties grapeProperties, GrapeModifier grapeModifier, int chance) {
        super(settings);
        
        this.chance = chance;
        this.type = type;
        this.grapeProperties = grapeProperties;
        this.grapeModifier = Optional.empty();
    }
    
    @Override
    public GrapeProperties getGrapeProperties() {
        return grapeProperties;
    }
    
    @Override
    public Optional<GrapeModifier> getGrapeModifier() {
        return grapeModifier;
    }
    
    @Override
    public GrapeType getType() {
        return type;
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var age = state.getValue(AGE);
        var isFullyGrown = age == 3;
        
        if (age < 3 && player.getItemInHand(hand).is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        }
        
        if (age > 1) {
            var grapeCount = world.random.nextInt(2) + (isFullyGrown ? 1 : 0);
            final var resource = getGrapeType().getItem();
            popResource(world, pos, new ItemStack(resource, grapeCount));
            
            world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlock(pos, state.setValue(AGE, 1), 2);
            
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        
        return super.use(state, world, pos, player, hand, hit);
    }


    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int i = state.getValue(AGE);
        if (i < 3 && random.nextInt(chance) == 0 && canGrowPlace(world, pos, state)) {
            BlockState blockState = state.setValue(AGE, i + 1);
            world.setBlock(pos, blockState, 2);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < 3;
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, boolean bl) {
        return blockState.getValue(AGE) < 3;
    }

    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    public boolean canGrowPlace(LevelReader world, BlockPos blockPos, BlockState blockState) {
        return world.getRawBrightness(blockPos, 0) > 9;
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader world, BlockPos blockPos) {
        return canGrowPlace(world, blockPos, blockState) && this.mayPlaceOn(world.getBlockState(blockPos.below()), world, blockPos);
    }

    @Override
    protected boolean mayPlaceOn(BlockState floor, BlockGetter world, BlockPos pos) {
        return floor.isSolidRender(world, pos);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return new ItemStack(this.type.getSeedItem());
    }

    public ItemStack getGrapeType() {
        return new ItemStack(this.type.getItem());
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        int i = Math.min(3, state.getValue(AGE) + 1);
        world.setBlock(pos, state.setValue(AGE, i), 2);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    static {
        AGE = BlockStateProperties.AGE_3;
        SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    }
}

