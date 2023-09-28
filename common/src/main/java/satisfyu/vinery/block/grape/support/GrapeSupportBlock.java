package satisfyu.vinery.block.grape.support;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import satisfyu.vinery.block.entity.GrapeProducerBlockEntity;
import satisfyu.vinery.block.grape.GrapeProperties;
import satisfyu.vinery.item.GrapeSeedItem;

public abstract class GrapeSupportBlock extends BaseEntityBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;

    public GrapeSupportBlock(Properties settings) {
        super(settings);
        registerDefaultState(this.defaultBlockState().setValue(AGE, 0));
    }
    
    public GrapeProperties getGrapeProperties(Level world, BlockPos pos) {
        var blockEntity = (GrapeProducerBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null) throw new AssertionError();
        
        return blockEntity.getGrapeProperties();
    }
    
    public boolean isMature(BlockState state) {
        return state.getValue(AGE) >= 4;
    }

    public void dropGrapes(Level world, BlockState state, BlockPos pos) {
        var grapeCount = 1 + world.random.nextInt(this.isMature(state) ? 2 : 1);
        var maturityBonus = this.isMature(state) ? 2 : 1;
        
        var blockEntity = (GrapeProducerBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null) throw new AssertionError();
        
        var grapeItem = blockEntity.getGrapeProperties().getGrapeType().getItem();
        var itemNbt = new CompoundTag();
        blockEntity.saveAdditional(itemNbt);
        
        popResource(world, pos, new ItemStack(grapeItem, grapeCount + maturityBonus));
        world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
    }

    public void dropSeeds(Level world, BlockState state, BlockPos pos) {
        var blockEntity = (GrapeProducerBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null) throw new AssertionError();
        
        var seedItem = blockEntity.getGrapeProperties().getProducerType().getBlockItem();
        var itemNbt = new CompoundTag();
        blockEntity.saveAdditional(itemNbt);
        
        var seedStack = new ItemStack(seedItem);
        seedStack.setTag(itemNbt);
        
        popResource(world, pos, seedStack);
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GrapeProducerBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        var age = state.getValue(AGE);
        
        if (hand == InteractionHand.OFF_HAND) {
            if (age > 3) {
                dropGrapes(world, state, pos);
                world.setBlock(pos, state.setValue(AGE, 2), 2);
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
            else {
                return InteractionResult.PASS;
            }
        }
        
        if (age > 0 && player.getItemInHand(hand).getItem() == Items.SHEARS) {
            if (age > 2) dropGrapes(world, state, pos);
            
            dropSeeds(world, state, pos);
            world.setBlock(pos, state.setValue(AGE, 0), 3);
            world.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_BREAK, SoundSource.AMBIENT, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        }
        
        final ItemStack stack = player.getItemInHand(hand);
        if (stack.tag instanceof GrapeSeedItem seed) {
            if (age == 0) {
                if (true) { //
                    world.setBlock(pos, state.setValue(AGE, 1), 3);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    world.playSound(player, pos, SoundEvents.SWEET_BERRY_BUSH_PLACE, SoundSource.AMBIENT, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        
        return super.use(state, world, pos, player, hand, hit);
    }
    
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        var age = state.getValue(AGE);
        if (world.getRawBrightness(pos, 0) >= 9 && age < 4) {
            world.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_CLIENTS);
        }
        
        super.randomTick(state, world, pos, random);
    }
    
    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(world, pos)) {
            if (state.getValue(AGE) > 0) {
                dropSeeds(world, state, pos);
            }
            if (state.getValue(AGE) > 2) {
                dropGrapes(world, state, pos);
            }
            world.destroyBlock(pos, true);
        }
    }
    
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(world, pos)) {
            world.scheduleTick(pos, this, 1);
        }
        
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (state.getValue(AGE) > 2) {
            dropGrapes(world, state, pos);
        }
        
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
    
    @Override
    public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean isClient) {
        return !isMature(state) && state.getValue(AGE) > 0;
    }
    
    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        var age = state.getValue(AGE);
        if (age == AGE.getPossibleValues().size()) return;
        
        age += random.nextFloat() < 0.3f ? 2 : 1;
        age = Math.min(age, 4);
        world.setBlock(pos, state.setValue(AGE, age), Block.UPDATE_CLIENTS);
    }
}
