package satisfyu.vinery.block.grape;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import satisfyu.vinery.block.grape.plant.GrapePlantBlock;
import satisfyu.vinery.registry.ObjectRegistry;

public enum GrapeProducerType implements StringRepresentable {
    BUSH("bush", ObjectRegistry.GRAPE_BUSH.get(), ObjectRegistry.GRAPE_BUSH_SEEDS.get()),
    VINES("vine", ObjectRegistry.GRAPE_VINES.get(), ObjectRegistry.GRAPE_VINE_SEEDS.get()),
    POST("post", ObjectRegistry.GRAPEVINE_POST.get(), ObjectRegistry.GRAPE_BUSH_SEEDS.get()),
    LATTICE("lattice", ObjectRegistry.GRAPEVINE_LATTICE.get(), ObjectRegistry.GRAPE_VINE_SEEDS.get());
    
    private final String name;
    private final Block block;
    private final BlockItem seed;
    
    GrapeProducerType(String name, Block block, Item seed) {
        if (!(block instanceof GrapePlantBlock & seed instanceof BlockItem)) throw new AssertionError();
        
        this.name = name;
        this.block = block;
        this.seed = (BlockItem) seed;
    }
    
    public Block getBlock() {
        return block;
    }
    
    public BlockItem getBlockItem() {
        return seed;
    }
    
    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}