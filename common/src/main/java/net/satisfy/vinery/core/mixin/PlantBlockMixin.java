package net.satisfy.vinery.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.satisfy.vinery.core.block.SpreadableGrassSlabBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A mixin class modifying {@link BushBlock} to restrict placement on certain dirt-based slabs
 * in a Minecraft mod environment.
 */
@Mixin(BushBlock.class)
public class PlantBlockMixin extends Block {

    /**
     * Constructs the mixin instance with block properties.
     * @param properties The properties defining this block.
     */
    public PlantBlockMixin(Properties properties) {
        super(properties);
    }

    /**
     * Injects logic into {@link BushBlock#mayPlaceOn} to prevent placement on bottom dirt slabs.
     * @param floor The block state below the placement position.
     * @param blockGetter The block getter for world access.
     * @param blockPos The position being checked.
     * @param cir Callback info with return value, cancellable.
     */
    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    private void injected(BlockState floor, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if(floor.is(BlockTags.DIRT)){
            if(floor.getBlock() instanceof SpreadableGrassSlabBlock && floor.getValue(SlabBlock.TYPE) == SlabType.BOTTOM)
                cir.setReturnValue(false);
        }
    }
}