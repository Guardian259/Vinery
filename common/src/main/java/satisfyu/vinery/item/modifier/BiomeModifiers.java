package satisfyu.vinery.item.modifier;

import com.mojang.blaze3d.shaders.Effect;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum BiomeModifiers {
    // Augmentation Crop Variants
    SMOKED(Component.translatable("flavortext.vinery.smoked"),2.0f), // When cooked in Smoker
    FROZEN(Component.translatable("flavortext.vinery.frozen"),0f), // Is, Snowing?

    // Good Crop Variants
    LUSH(Component.translatable("flavortext.vinery.lush"), 0.5f), // Lush Caves
    PLUSH(Component.translatable("flavortext.vinery.plush"), 1.0f), // Deserts
    HARDY(Component.translatable("flavortext.vinery.hardy"),0.25f), // Spruce Forest && Tiaga Forests
    RICH(Component.translatable("flavortext.vinery.rich"), 0.2f), // Mountains TODO: Determine a better biome as to not conflict with Gleaming
    SUCCULENT(Component.translatable("flavortext.vinery.succulent"), 0.8f), // Mangrove Forest
    FRAGRANT(Component.translatable("flavortext.vinery.fragrant"), 0.8f), // Flower Forest && Flower Fields
    IMMACULATE(Component.translatable("flavortext.vinery.immaculate"), 0.5f), // Cherry Grove
    SUNKISSED(Component.translatable("flavortext.vinery.sunkissed"), 0.8f), // Beaches
    JUICY(Component.translatable("flavortext.vinery.juicy"), 0.7f), // Forest
    // Bad Crop Variants
    ROTTEN(Component.translatable("flavortext.vinery.rotten"), 0.95f), // % Chance in Forests, Swamps, Mangroves, Jungles, Oceans
    SCORCHED(Component.translatable("flavortext.vinery.scorched"), 1.5f), // % Chance in Desserts, Savanna, Plains, Beaches, Oceans
    INFESTED(Component.translatable("flavortext.vinery.infested"), 0.8f), // % Chance in Swamps, Mangroves, Jungles, Mushroom Forests/Islands
    ACIDIC(Component.translatable("flavortext.vinery.acidic"), 1.0f), // % Chance in Savannas
    WITHERED(Component.translatable("flavortext.vinery.withered"), 1.5f), // % Chance in Deserts

    /* Secret Variants;
     * These will not readily be shown in the crafting recipes,
     * but rather be hinted at through books obtainable through the trader*/
    DARKEND(Component.translatable("flavortext.vinery.deep_dark"), 0.4f), // Within the Deep Dark or Next to Sculk Catalyst Blocks
    GLEAMING(Component.translatable("flavortext.vinery.gleaming"), 0f); // Next to Budding Amethyst Blocks

    BiomeModifiers(MutableComponent name, Float temperature, Effect... effects) {
        new BiomeModifier(name, effects);
    }
}
