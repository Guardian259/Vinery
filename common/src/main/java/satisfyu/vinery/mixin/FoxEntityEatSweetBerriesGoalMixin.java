package satisfyu.vinery.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import satisfyu.vinery.block.grape.GrapeProducerType;
import satisfyu.vinery.block.grape.plant.GrapeBushBlock;

@Mixin(Fox.FoxEatBerriesGoal.class)
public abstract class FoxEntityEatSweetBerriesGoalMixin extends MoveToBlockGoal {
    @Final
    @Shadow
    Fox fox;   // Synthetic field

    public FoxEntityEatSweetBerriesGoalMixin(PathfinderMob mob, double speed, int range) {
        super(mob, speed, range);
    }

    @Inject(method = "isValidTarget", at = @At("HEAD"), cancellable = true)
    private void isTargetPos(LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof GrapeBushBlock) {
            cir.setReturnValue(state.getValue(GrapeBushBlock.AGE) >= 2);
        }
    }

    @Inject(method = "onReachedTarget", at = @At("TAIL"))
    private void eatGrapes(CallbackInfo ci) {
        final BlockState state = fox.level.getBlockState(this.blockPos);
        if (state.getBlock() instanceof GrapeBushBlock bush) {
            pickGrapes(state, bush.getType());
        }
    }

    private void pickGrapes(BlockState state, GrapeProducerType type) {
        final int age = state.getValue(GrapeBushBlock.AGE);
        state.setValue(GrapeBushBlock.AGE, 1);
        int j = 1 + fox.level.random.nextInt(2) + (age == 3 ? 1 : 0);
        ItemStack itemStack = fox.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack grape = getGrapeFor(type);
        if (itemStack.isEmpty()) {
            fox.setItemSlot(EquipmentSlot.MAINHAND, grape);
            --j;
        }
        if (j > 0) {
            Block.popResource(fox.level, this.blockPos, new ItemStack(grape.getItem(), j));
        }
        fox.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
        fox.level.setBlock(this.blockPos, state.setValue(GrapeBushBlock.AGE, 1), 2);
    }

    private static ItemStack getGrapeFor(GrapeProducerType type) {
        return type.getGrape().getDefaultInstance();
    }
}
