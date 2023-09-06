package satisfyu.vinery.item.modifier;

import com.mojang.blaze3d.shaders.Effect;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum BiomeModifiers {
    // Augmentation Crop Variants
    SMOKED(Component.translatable("flavortext.vinery.smoked")), // When cooked in Smoker
    FROZEN(Component.translatable("flavortext.vinery.frozen")), // Is, Snowing?

    // Good Crop Variants
    LUSH(Component.translatable("flavortext.vinery.lush")), // Lush Caves
    PLUSH(Component.translatable("flavortext.vinery.plush")), // Plains
    HARDY(Component.translatable("flavortext.vinery.hardy")), // Spruce Forest && Tiaga Forests
    RICH(Component.translatable("flavortext.vinery.rich")), // Mountains TODO: Determine a better biome as to not conflict with Gleaming
    SUCCULENT(Component.translatable("flavortext.vinery.succulent")), // Mangrove Forest
    FRAGRANT(Component.translatable("flavortext.vinery.fragrant")), // Flower Forest && Flower Fields
    IMMACULATE(Component.translatable("flavortext.vinery.immaculate")), // Cherry Grove
    SUNKISSED(Component.translatable("flavortext.vinery.sunkissed")), // Beaches
    JUICY(Component.translatable("flavortext.vinery.juicy")), // Forest && Spruce Forest
    // Bad Crop Variants
    ROTTEN(Component.translatable("flavortext.vinery.rotten")), // % Chance in Forests, Swamps, Mangroves, Jungles, Oceans
    SCORCHED(Component.translatable("flavortext.vinery.scorched")), // % Chance in Desserts, Savanna, Plains, Beaches, Oceans
    INFESTED(Component.translatable("flavortext.vinery.infested")), // % Chance in Swamps, Mangroves, Jungles, Mushroom Forests/Islands
    ACIDIC(Component.translatable("flavortext.vinery.acidic")), // % Chance in Deserts, Savannas
    WITHERED(Component.translatable("flavortext.vinery.withered")), // % Chance in Deserts, Savannas

    /* Secret Variants;
     * These will not readily be shown in the crafting recipes,
     * but rather be hinted at through books obtainable through the trader*/
    DARKEND(Component.translatable("flavortext.vinery.deep_dark")), // Within the Deep Dark or Next to Sculk Catalyst Blocks
    GLEAMING(Component.translatable("flavortext.vinery.gleaming")); // Next to Budding Amethyst Blocks

    BiomeModifiers(MutableComponent name, Effect... effects) {
        new BiomeModifier(name, effects);
    }
}
