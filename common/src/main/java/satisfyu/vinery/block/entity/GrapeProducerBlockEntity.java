package satisfyu.vinery.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import satisfyu.vinery.block.grape.GrapeProperties;
import satisfyu.vinery.block.grape.GrapeProducerType;
import satisfyu.vinery.registry.VineryBlockEntityTypes;

public class GrapeProducerBlockEntity extends BlockEntity {
    private final GrapeProducerType type;
    private GrapeProperties grapeProperties;
    
    public GrapeProducerBlockEntity(GrapeProducerType type, BlockPos blockPos, BlockState blockState) {
        super(VineryBlockEntityTypes.GRAPE_BUSH_ENTITY.get(), blockPos, blockState);
        this.type = type;
    }
    
    public GrapeProperties getGrapeProperties() {
        return grapeProperties;
    }
    
    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putString("type", grapeProperties.getProducerType().getSerializedName());
        nbt.putFloat("water", grapeProperties.getWater());
        nbt.putFloat("sugar", grapeProperties.getSugar());
        nbt.putFloat("minerality", grapeProperties.getMinerality());
        nbt.putFloat("yeast", grapeProperties.getYeast());
    }
    
    @Override
    public void load(CompoundTag nbt) {
        GrapeProducerType grapeType;
        if (nbt.contains("grape_type"))
            grapeType = GrapeProducerType.valueOf(nbt.getString("grape_type"));
        else
            grapeType = getLevel().random.nextBoolean() ? GrapeProducerType.BUSH : GrapeProducerType.VINES;
        
        var water = nbt.contains("water") ? nbt.getFloat("water") : 0.0f;
        var sugar = nbt.contains("sugar") ? nbt.getFloat("sugar") : 0.0f;
        var minerality = nbt.contains("minerality") ? nbt.getFloat("minerality") : 0.0f;
        var yeast = nbt.contains("yeast") ? nbt.getFloat("yeast") : 0.0f;
        
        grapeProperties = new GrapeProperties(grapeType, water, sugar, minerality, yeast);
    }
}
