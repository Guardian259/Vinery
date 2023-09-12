package satisfyu.vinery.item.grape;

public class GrapeProperties {
    private float water; // decrease potency, increase duration
    private float sugar; // increases potency, decreases duration
    private float minerality; // balance multiplier
    private float yeast; // affects fermentation
    private GrapeModifier modifier; // ... ?
    
    public GrapeProperties(float water, float sugar, float minerality, float yeast) {
        this.water = Math.max(0.0f, Math.min(15.0f, water));
        this.sugar = Math.max(0.0f, Math.min(15.0f, sugar));
        this.minerality = Math.max(0.0f, Math.min(15.0f, minerality));
        this.yeast = Math.max(0.0f, Math.min(15.0f, yeast));
    }
    
    public GrapeProperties(GrapeProperties properties, GrapeModifier modifier) {
        this(properties.water, properties.sugar, properties.minerality, properties.yeast);
        this.modifier = modifier;
    }
    
    public GrapeProperties() {
        this(0.0f, 0.0f, 0.0f, 0.f);
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
    
    public GrapeModifier getModifier() {
        return modifier;
    }
}
