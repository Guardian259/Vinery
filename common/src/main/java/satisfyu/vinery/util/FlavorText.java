package satisfyu.vinery.util;

import java.util.HashMap;

import static net.minecraft.network.chat.Component.translatable;
import static satisfyu.vinery.util.FlavorTextType.BERRY;

public class FlavorText {
    private HashMap<String, FlavorTextType> flavorText = new HashMap<>();

    public FlavorText() {
        // Augmented Crop Variants
        flavorText.put(translatable("flavortext.vinery.smoked").toString(), BERRY); // When around Campfires
        // Good Crop Variants
        flavorText.put(translatable("flavortext.vinery.lush").toString(), BERRY); // Lush Caves
        flavorText.put(translatable("flavortext.vinery.plush").toString(), BERRY); // Plains
        flavorText.put(translatable("flavortext.vinery.hardy").toString(), BERRY); // Spruce Forest && Tiaga Forests
        flavorText.put(translatable("flavortext.vinery.rich").toString(), BERRY); // Mountains
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
        flavorText.put(translatable("flavortext.vinery.frozen").toString(), FlavorTextType.BERRY); // % Chance in Mountains, Tundras

    }

    public void addFlavorTextPair(String string, FlavorTextType type) {flavorText.put(string, type);}
    public void removeFlavorTextPair(String string){flavorText.remove(string);}

}
