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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import satisfyu.vinery.item.grape.GrapeItem;
import satisfyu.vinery.item.grape.GrapeModifier;
import satisfyu.vinery.item.grape.GrapeProperties;
import satisfyu.vinery.registry.ObjectRegistry;
import satisfyu.vinery.item.grape.GrapeType;

import java.util.Optional;

public class GrapeVineBlock extends VineBlock implements BonemealableBlock, GrapePlantBlock {
    public static final IntegerProperty AGE;
    public static final BooleanProperty STERILIZED;
    
    private Optional<GrapeModifier> grapeModifier;
    private GrapeProperties grapeProperties;
    public GrapeType type;

    public GrapeVineBlock(Properties settings, GrapeType type) {
        super(settings);
        this.type = type;
        this.registerDefaultState(this.stateDefinition.any().setValue(STERILIZED, false).setValue(UP, false).setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false));
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.OFF_HAND) {
            return super.use(state, world, pos, player, hand, hit);
        }
        if (player.getItemInHand(hand).is(Items.SHEARS)) {
            world.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.setBlockAndUpdate(pos, state.cycle(STERILIZED));
            return InteractionResult.PASS;
        }
        int i = state.getValue(AGE);
        boolean bl = i == 3;
        if (!bl && player.getItemInHand(hand).is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        } else if (i > 1) {
            int x = world.random.nextInt(2);
            
            // Extracted resource item determination from popResource
            final var resource = type == GrapeType.RED ? ObjectRegistry.RED_GRAPE.get() : ObjectRegistry.WHITE_GRAPE.get();
            
            popResource(world, pos, new ItemStack(resource, x + (bl ? 1 : 0)));
            world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setBlock(pos, state.setValue(AGE, 1), 2);
            
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return super.use(state, world, pos, player, hand, hit);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int i = state.getValue(AGE);
        if (i < 3 && random.nextInt(5) == 0 && world.getRawBrightness(pos.above(), 0) >= 9) {
            BlockState blockState = state.setValue(AGE, i + 1);
            world.setBlock(pos, blockState, 2);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
        }
        super.randomTick(state, world, pos, random);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(STERILIZED);
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
        int i = Math.min(3, state.getValue(AGE) + 1);
        world.setBlock(pos, state.setValue(AGE, i), 2);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE, STERILIZED);
    }

    static {
        AGE = BlockStateProperties.AGE_3;
        STERILIZED = BooleanProperty.create("sterilized");
    }
}

