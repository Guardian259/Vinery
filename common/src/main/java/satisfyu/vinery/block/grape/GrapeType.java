package satisfyu.vinery.block.grape;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import satisfyu.vinery.registry.ObjectRegistry;

public enum GrapeType implements StringRepresentable {
    RED("red", ObjectRegistry.RED_GRAPE.get()),
    WHITE("white", ObjectRegistry.WHITE_GRAPE.get());
    
    private final String name;
    private final Item grapeItem;
    
    GrapeType(String name, Item grapeItem) {
        this.name = name;
        this.grapeItem = grapeItem;
    }
    
    
    public Item getItem() {
        return grapeItem;
    }
    
    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}

