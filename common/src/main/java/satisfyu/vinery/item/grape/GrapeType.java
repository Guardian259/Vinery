package satisfyu.vinery.item.grape;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import satisfyu.vinery.registry.ObjectRegistry;

public enum GrapeType implements StringRepresentable {
    RED(ObjectRegistry.RED_GRAPE.get(), ObjectRegistry.RED_GRAPE_SEEDS.get()),
    WHITE(ObjectRegistry.WHITE_GRAPE.get(), ObjectRegistry.WHITE_GRAPE_SEEDS.get());
    
    private Item grape;
    private Item seed;
    
    GrapeType(Item grape, Item seed) {
        this.grape = grape;
        this.seed = seed;
    }

    public Item getItem() {
        return grape;
    }

    public Item getSeedItem() {
        return seed;
    }
    
    public boolean isPaleType() {
        throw new AssertionError("TODO");
    }
    
    @Override
    public String getSerializedName() {
        return toString().toLowerCase();
    }
}