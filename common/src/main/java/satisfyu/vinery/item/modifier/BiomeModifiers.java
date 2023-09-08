package satisfyu.vinery.item.modifier;

import com.mojang.blaze3d.shaders.Effect;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum BiomeModifiers {
    // Augmentation Crop Variants
    SMOKED(Component.translatable("vinery.modifier.smoked"), 2.0f), // When cooked in Smoker
    FROZEN(Component.translatable("vinery.modifier.frozen"), 0f), // Is, Snowing?
    
    // Good Crop Variants
    LUSH(Component.translatable("vinery.modifier.lush"), 0.5f), // Lush Caves
    PLUSH(Component.translatable("vinery.modifier.plush"), 1.0f), // Deserts
    HARDY(Component.translatable("vinery.modifier.hardy"), 0.25f), // Spruce Forest && Tiaga Forests
    RICH(Component.translatable("vinery.modifier.rich"), 0.2f), // Mountains TODO: Determine a better biome as to not conflict with Gleaming
    SUCCULENT(Component.translatable("vinery.modifier.succulent"), 0.8f), // Mangrove Forest
    FRAGRANT(Component.translatable("vinery.modifier.fragrant"), 0.8f), // Flower Forest && Flower Fields
    IMMACULATE(Component.translatable("vinery.modifier.immaculate"), 0.5f), // Cherry Grove
    SUNKISSED(Component.translatable("vinery.modifier.sunkissed"), 0.8f), // Beaches
    JUICY(Component.translatable("vinery.modifier.juicy"), 0.7f), // Forest
    // Bad Crop Variants
    ROTTEN(Component.translatable("vinery.modifier.rotten"), 0.95f), // % Chance in Forests, Swamps, Mangroves, Jungles, Oceans
    SCORCHED(Component.translatable("vinery.modifier.scorched"), 1.5f), // % Chance in Desserts, Savanna, Plains, Beaches, Oceans
    INFESTED(Component.translatable("vinery.modifier.infested"), 0.8f), // % Chance in Swamps, Mangroves, Jungles, Mushroom Forests/Islands
    ACIDIC(Component.translatable("vinery.modifier.acidic"), 1.0f), // % Chance in Savannas
    WITHERED(Component.translatable("vinery.modifier.withered"), 1.5f), // % Chance in Deserts
    
    /* Secret Variants;
     * These will not readily be shown in the crafting recipes,
     * but rather be hinted at through books obtainable through the trader*/
    DARKEND(Component.translatable("vinery.modifier.deep_dark"), 0.4f), // Within the Deep Dark or Next to Sculk Catalyst Blocks
    GLEAMING(Component.translatable("vinery.modifier.gleaming"), 0f); // Next to Budding Amethyst Blocks
    
    BiomeModifiers(MutableComponent name, Float temperature, Effect... effects) {
        new BiomeModifier(name, effects);
    }
}
