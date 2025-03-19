package net.satisfy.vinery.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * A class representing the configuration settings for wine items, including food properties
 * and effect details in a Minecraft mod context.
 */
public class WineSettings {
    /** The properties defining the wine's item characteristics, including food components. */
    private final Item.Properties properties;
    /** The base duration of the wine's effect in ticks. */
    private final int baseDuration;

    /**
     * Constructs a new WineSettings instance with specified effect, duration, and strength.
     * @param effect A supplier providing the mob effect to apply, or null for no effect.
     * @param duration The base duration of the effect in ticks.
     * @param strength The amplifier level of the effect (0-based).
     */
    public WineSettings(Supplier<MobEffect> effect, int duration, int strength) {
        this.baseDuration = duration;
        this.properties = new Item.Properties()
                .food(createWineFoodComponent(effect, duration, strength));
    }

    /**
     * Retrieves the properties of the wine item.
     * @return The immutable {@link Item.Properties} instance.
     */
    public Item.Properties getProperties() {
        return properties;
    }

    /**
     * Gets the base duration of the wine's effect.
     * @return The duration in ticks.
     */
    public int getBaseDuration() {
        return baseDuration;
    }

    /**
     * Creates a food component for the wine with optional mob effect.
     * @param effect A supplier for the mob effect, or null if none.
     * @param duration The duration of the effect in ticks.
     * @param strength The effect's amplifier level (0-based).
     * @return A configured {@link FoodProperties} instance.
     */
    private FoodProperties createWineFoodComponent(Supplier<MobEffect> effect, int duration, int strength) {
        FoodProperties.Builder builder = new FoodProperties.Builder()
                .alwaysEat();
        if (effect != null) {
            builder.effect(new MobEffectInstance(effect.get(), duration, strength), 1.0f);
        }
        return builder.build();
    }
}
