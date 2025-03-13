package net.satisfy.vinery.core.mixin;

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
import net.satisfy.vinery.core.block.GrapeBush;
import net.satisfy.vinery.core.util.GrapeType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A mixin class modifying {@link Fox.FoxEatBerriesGoal} to allow foxes to target and eat grapes from
 * {@link GrapeBush} blocks in a Minecraft mod environment.
 */
@Mixin(Fox.FoxEatBerriesGoal.class)
public abstract class FoxEntityEatSweetBerriesGoalMixin extends MoveToBlockGoal {
    /** The fox entity associated with this goal. */
    @Final
    @Shadow
    Fox field_17975;

    /**
     * Constructs the mixin instance with the specified mob, speed, and range.
     * @param mob The pathfinding mob (fox).
     * @param speed The movement speed toward the target.
     * @param range The range to search for targets.
     */
    public FoxEntityEatSweetBerriesGoalMixin(PathfinderMob mob, double speed, int range) {
        super(mob, speed, range);
    }

    /**
     * Injects logic into {@link Fox.FoxEatBerriesGoal#isValidTarget} to validate grape bushes as targets.
     * @param world The world reader for block state access.
     * @param pos The block position to check.
     * @param cir Callback info with return value, cancellable.
     */
    @Inject(method = "isValidTarget", at = @At("HEAD"), cancellable = true)
    private void isTargetPos(LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof GrapeBush) {
            cir.setReturnValue(state.getValue(GrapeBush.AGE) >= 2);
        }
    }

    /**
     * Injects logic into {@link Fox.FoxEatBerriesGoal#onReachedTarget} to handle grape picking.
     * @param ci Callback info for the injection.
     */
    @Inject(method = "onReachedTarget", at = @At("TAIL"))
    private void eatGrapes(CallbackInfo ci) {
        final BlockState state = field_17975.level().getBlockState(this.blockPos);
        if (state.getBlock() instanceof GrapeBush bush) {
            pickGrapes(state, bush.getType());
        }
    }

    /**
     * Handles the fox picking grapes from a grape bush, updating state and dropping items.
     * @param state The current block state of the grape bush.
     * @param type The grape type associated with the bush.
     */
    @Unique
    private void pickGrapes(BlockState state, GrapeType type) {
        final int age = state.getValue(GrapeBush.AGE);
        state.setValue(GrapeBush.AGE, 1);
        int j = 1 + field_17975.level().random.nextInt(2) + (age == 3 ? 1 : 0);
        ItemStack itemStack = field_17975.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack grape = getGrapeFor(type);
        if (itemStack.isEmpty()) {
            field_17975.setItemSlot(EquipmentSlot.MAINHAND, grape);
            --j;
        }
        if (j > 0) {
            Block.popResource(field_17975.level(), this.blockPos, new ItemStack(grape.getItem(), j));
        }
        field_17975.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
        field_17975.level().setBlock(this.blockPos, state.setValue(GrapeBush.AGE, 1), 2);
    }

    /**
     * Retrieves the grape item stack for a given grape type.
     * @param type The grape type to get the fruit for.
     * @return The default {@link ItemStack} for the grape type’s fruit.
     */
    @Unique
    private static ItemStack getGrapeFor(GrapeType type) {
        return type.getFruit().getDefaultInstance();
    }
}