package net.satisfy.vinery.core.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.vinery.core.registry.ObjectRegistry;
import net.satisfy.vinery.core.registry.TagRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for managing juice items in a Minecraft mod, categorizing them by type and region
 * using tags and item mappings.
 */
public class JuiceUtil {
    /** Mapping of red juice tags to their region identifiers. */
    public static final Map<TagKey<Item>, String> RED_JUICE_TAGS = new HashMap<>();
    /** Mapping of white juice tags to their region identifiers. */
    public static final Map<TagKey<Item>, String> WHITE_JUICE_TAGS = new HashMap<>();
    /** Mapping of apple juice items to their type identifier. */
    public static final Map<Item, String> APPLE_JUICES = new HashMap<>();

    static {
        addRedJuice(TagRegistry.RED_GRAPEJUICE, "general");
        addRedJuice(TagRegistry.RED_SAVANNA_GRAPEJUICE, "savanna");
        addRedJuice(TagRegistry.RED_TAIGA_GRAPEJUICE, "taiga");
        addRedJuice(TagRegistry.RED_JUNGLE_GRAPEJUICE, "jungle");
        addRedJuice(TagRegistry.CRIMSON_GRAPEJUICE, "crimson");

        addWhiteJuice(TagRegistry.WHITE_GRAPEJUICE, "general");
        addWhiteJuice(TagRegistry.WHITE_SAVANNA_GRAPEJUICE, "savanna");
        addWhiteJuice(TagRegistry.WHITE_TAIGA_GRAPEJUICE, "taiga");
        addWhiteJuice(TagRegistry.WHITE_JUNGLE_GRAPEJUICE, "jungle");
        addWhiteJuice(TagRegistry.WARPED_GRAPEJUICE, "warped");

        addAppleJuice(ObjectRegistry.APPLE_JUICE.get());
    }

    /**
     * Adds a red juice tag and its associated region to the red juice mapping.
     * @param tag The tag key identifying the red juice.
     * @param region The region identifier (e.g., "general", "savanna").
     */
    private static void addRedJuice(TagKey<Item> tag, String region) {
        RED_JUICE_TAGS.put(tag, region);
    }

    /**
     * Adds a white juice tag and its associated region to the white juice mapping.
     * @param tag The tag key identifying the white juice.
     * @param region The region identifier (e.g., "general", "savanna").
     */
    private static void addWhiteJuice(TagKey<Item> tag, String region) {
        WHITE_JUICE_TAGS.put(tag, region);
    }

    /**
     * Adds an apple juice item to the apple juice mapping with a fixed "apple" identifier.
     * @param item The apple juice item to register.
     */
    private static void addAppleJuice(Item item) {
        APPLE_JUICES.put(item, "apple");
    }

    /**
     * Checks if an item stack is a recognized juice type (red, white, or apple).
     * @param stack The item stack to evaluate.
     * @return True if the stack is a juice, false if empty or not a juice.
     */
    public static boolean isJuice(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return isRedJuice(stack) || isWhiteJuice(stack) || isAppleJuice(stack);
    }

    /**
     * Determines if an item stack is a red juice based on its tags.
     * @param stack The item stack to check.
     * @return True if the stack matches a red juice tag, false otherwise.
     */
    private static boolean isRedJuice(ItemStack stack) {
        for (TagKey<Item> tag : RED_JUICE_TAGS.keySet()) {
            if (stack.is(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if an item stack is a white juice based on its tags.
     * @param stack The item stack to check.
     * @return True if the stack matches a white juice tag, false otherwise.
     */
    private static boolean isWhiteJuice(ItemStack stack) {
        for (TagKey<Item> tag : WHITE_JUICE_TAGS.keySet()) {
            if (stack.is(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if an item stack is an apple juice based on its item.
     * @param stack The item stack to check.
     * @return True if the stack’s item is in the apple juice mapping, false otherwise.
     */
    private static boolean isAppleJuice(ItemStack stack) {
        return APPLE_JUICES.containsKey(stack.getItem());
    }

    /**
     * Retrieves the juice type and region as a string (e.g., "red_general", "white_savanna", "apple").
     * @param stack The item stack to identify.
     * @return The juice type string, or an empty string if not a juice.
     */
    public static String getJuiceType(ItemStack stack) {
        if (!isJuice(stack)) {
            return "";
        }

        for (Map.Entry<TagKey<Item>, String> entry : RED_JUICE_TAGS.entrySet()) {
            if (stack.is(entry.getKey())) {
                return "red_" + entry.getValue();
            }
        }

        for (Map.Entry<TagKey<Item>, String> entry : WHITE_JUICE_TAGS.entrySet()) {
            if (stack.is(entry.getKey())) {
                return "white_" + entry.getValue();
            }
        }

        String region = APPLE_JUICES.get(stack.getItem());
        if (region != null) {
            return region;
        }

        return "";
    }
}
