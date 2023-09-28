package satisfyu.vinery.block.grape.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import satisfyu.vinery.block.entity.GrapeProducerBlockEntity;
import satisfyu.vinery.block.grape.GrapeProperties;

public abstract class GrapePlantBlock extends BaseEntityBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    
    protected GrapePlantBlock(Properties properties) {
        super(properties);
    }
    
    public GrapeProperties getGrapeProperties(Level world, BlockPos pos) {
        var blockEntity = (GrapeProducerBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null) throw new AssertionError();
        
        return blockEntity.getGrapeProperties();
    }
    
    public boolean isMature(BlockState state) {
        return state.getValue(AGE) >= 3;
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GrapeProducerBlockEntity(blockPos, blockState);
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        var age = state.getValue(AGE);
        if (!isMature(state) && player.getItemInHand(hand).is(Items.BONE_MEAL)) return InteractionResult.PASS;
        
        if (age > 1) {
            var blockEntity = (GrapeProducerBlockEntity) world.getBlockEntity(pos);
            if (blockEntity == null) throw new AssertionError();
            
            var grapeCount = world.random.nextInt(2) + (isMature(state) ? 1 : 0);
            var grapeType = blockEntity.getGrapeProperties().getProducerType();
            var grapeStack = new ItemStack(grapeType.getGrape(), grapeCount);
            var grapeNbt = new CompoundTag();
            blockEntity.saveAdditional(grapeNbt);
            grapeStack.setTag(grapeNbt);
            
            var name = Component.literal(grapeType + world.getBiome(pos).unwrapKey().get().location().getPath());
            grapeStack.setHoverName(name);
            
            popResource(world, pos, grapeStack);
            world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlock(pos, state.setValue(AGE, 1), 2);
            
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        
        return InteractionResult.PASS;
    }
    
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        var age = state.getValue(AGE);
        if (age < 3 && random.nextInt(5) == 0 && world.getRawBrightness(pos, 0) > 9) {
            BlockState blockState = state.setValue(AGE, age + 1);
            world.setBlock(pos, blockState, 2);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
        }
    }
    
    @Override
    public boolean isValidBonemealTarget(BlockGetter blockGetter, BlockPos blockPos, BlockState state, boolean bl) {
        return state.getValue(AGE) < 3;
    }
    
    @Override
    public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }
    
    @Override
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        var age = state.getValue(AGE);
        if (age < 3) world.setBlock(pos, state.setValue(AGE, age + 1), 2);
    }
}
