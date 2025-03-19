package net.satisfy.vinery.util

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import java.util.function.Supplier

/**
 * A class representing the configuration settings for wine items, including food properties
 * and effect details in a Minecraft mod context.
 */
class WineSettings(
    effect: Supplier<MobEffect?>?,
    /** The base duration of the wine's effect in ticks.  */
    val baseDuration: Int, strength: Int
) {
    /**
     * Retrieves the properties of the wine item.
     * @return The immutable [Item.Properties] instance.
     */
    /** The properties defining the wine's item characteristics, including food components.  */
    val properties: Item.Properties
    /**
     * Gets the base duration of the wine's effect.
     * @return The duration in ticks.
     */

    /**
     * Constructs a new WineSettings instance with specified effect, duration, and strength.
     * @param effect A supplier providing the mob effect to apply, or null for no effect.
     * @param duration The base duration of the effect in ticks.
     * @param strength The amplifier level of the effect (0-based).
     */
    init {
        this.properties = Item.Properties()
            .food(Companion.createWineFoodComponent(effect, baseDuration, strength))
    }

    companion object {
        /**
         * Creates a food component for the wine with optional mob effect.
         * @param effect A supplier for the mob effect, or null if none.
         * @param duration The duration of the effect in ticks.
         * @param strength The effect's amplifier level (0-based).
         * @return A configured [FoodProperties] instance.
         */
        fun createWineFoodComponent(effect: Supplier<MobEffect?>?, duration: Int, strength: Int): FoodProperties {
            val builder = FoodProperties.Builder()
                .alwaysEat()
            if (effect != null) {
                effect.get()?.let { MobEffectInstance(it, duration, strength) }?.let { builder.effect(it, 1.0f) }
            }
            return builder.build()
        }
    }
}
