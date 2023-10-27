package satisfyu.vinery.block.grape;

import net.minecraft.nbt.CompoundTag;

public class GrapeProperties {
    private GrapeType grapeType;
    private GrapeProducerType producerType;
    private float water; // decrease potency, increase duration
    private float sugar; // increases potency, decreases duration
    private float minerality; // balance multiplier
    private float yeast; // affects fermentation
    
    public GrapeProperties(GrapeType grapeType, GrapeProducerType producerType, float water, float sugar, float minerality, float yeast) {
        this.grapeType = grapeType;
        this.producerType = producerType;
        this.water = Math.max(0.0f, Math.min(15.0f, water));
        this.sugar = Math.max(0.0f, Math.min(15.0f, sugar));
        this.minerality = Math.max(0.0f, Math.min(15.0f, minerality));
        this.yeast = Math.max(0.0f, Math.min(15.0f, yeast));
    }
    
    public GrapeType getGrapeType() {
        return grapeType;
    }
    
    public GrapeProducerType getProducerType() {
        return producerType;
    }
    
    public int getLevel() {
        return Math.round(((water + sugar + minerality + yeast) / 4) / 3);
    }
    
    public float getBalance() {
        return ((water + sugar) / 2) * minerality;
    }
    
    public float getWater() {
        return water;
    }
    
    public float getSugar() {
        return sugar;
    }
    
    public float getYeast() {
        return yeast;
    }
    
    public float getMinerality() {
        return minerality;
    }
    
    public void save(CompoundTag nbt) {
        nbt.putString("type", getProducerType().getSerializedName());
        nbt.putFloat("water", water);
        nbt.putFloat("sugar", sugar);
        nbt.putFloat("minerality", minerality);
        nbt.putFloat("yeast", yeast);
    }
}
