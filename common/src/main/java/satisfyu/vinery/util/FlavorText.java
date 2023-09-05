package satisfyu.vinery.util;

import oshi.util.tuples.Pair;

import java.util.HashMap;

import static net.minecraft.network.chat.Component.translatable;
import static satisfyu.vinery.util.FlavorTextType.*;
// TODO: Create Wrapper class for entries & offload the bulk of the entry data into it.
public class FlavorText {
    private static HashMap<String, FlavorTextType> flavorText = new HashMap<>();

    public FlavorText() {
        // Augmented Crop Variants
        flavorText.put(translatable("flavortext.vinery.smoked").toString(), GRAPE_MODIFIER); // When cooked in Smoker
        flavorText.put(translatable("flavortext.vinery.frozen").toString(), GRAPE_MODIFIER); // Is, Snowing?

        // Good Crop Variants
        flavorText.put(translatable("flavortext.vinery.lush").toString(), BERRY); // Lush Caves
        flavorText.put(translatable("flavortext.vinery.plush").toString(), BERRY); // Plains
        flavorText.put(translatable("flavortext.vinery.hardy").toString(), BERRY); // Spruce Forest && Tiaga Forests
        flavorText.put(translatable("flavortext.vinery.rich").toString(), BERRY); // Mountains TODO: Determine a better biome as to not conflict with Gleaming
        flavorText.put(translatable("flavortext.vinery.succulent").toString(), BERRY); // Mangrove Forest
        flavorText.put(translatable("flavortext.vinery.fragrant").toString(), BERRY); // Flower Forest && Flower Fields
        flavorText.put(translatable("flavortext.vinery.immaculate").toString(), BERRY); // Cherry Grove
        flavorText.put(translatable("flavortext.vinery.sunkissed").toString(), BERRY); // Beaches
        flavorText.put(translatable("flavortext.vinery.gleaming").toString(), BERRY); // Mountains
        flavorText.put(translatable("flavortext.vinery.juicy").toString(), BERRY); // Forest && Spruce Forest
        // Bad Crop Variants
        flavorText.put(translatable("flavortext.vinery.rotten").toString(), FlavorTextType.BERRY); // % Chance in Forests, Swamps, Mangroves, Jungles, Oceans
        flavorText.put(translatable("flavortext.vinery.scorched").toString(), FlavorTextType.BERRY); // % Chance in Desserts, Savanna, Plains, Beaches, Oceans
        flavorText.put(translatable("flavortext.vinery.infested").toString(), FlavorTextType.BERRY); // % Chance in Swamps, Mangroves, Jungles, Mushroom Forests/Islands
        //flavorText.put(translatable("flavortext.vinery.acidic").toString(), FlavorTextType.BERRY); // % Chance in Deserts, Savannas
        flavorText.put(translatable("flavortext.vinery.withered").toString(), FlavorTextType.BERRY); // % Chance in Deserts, Savannas

    }

    public void addFlavorTextPair(String string, FlavorTextType type) {flavorText.put(string, type);}
    public void removeFlavorTextPair(String string){flavorText.remove(string);}

    public static Pair<FlavorTextType, String> getFlavorTextFromBiomeTraits(Float temperature, Float rainfall) {
        return new Pair<>(flavorText.get(""), ""); // TODO: Replace empty strings with actual references
    }
}
