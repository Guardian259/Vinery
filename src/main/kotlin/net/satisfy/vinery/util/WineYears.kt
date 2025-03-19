package net.satisfy.vinery.util

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import kotlin.math.max
import kotlin.math.min

/**
 * Utility for wine aging mechanics in a Minecraft mod, tracking production years and calculating
 * age-based effects using Minecraft's 24000 ticks/day.
 */
object WineYears {
    private const val YEARS_START = 0           // Starting year
    private const val MAX_LEVEL = 5             // Max effect level
    private const val START_DURATION = 1800     // Base effect duration (ticks)
    private const val DURATION_PER_YEAR = 200   // Duration increase per year (ticks)
    const val DAYS_PER_YEAR = 24        // Minecraft days per wine year
    const val YEARS_PER_EFFECT_LEVEL = 6 // Years per effect level increase
    private const val MAX_DURATION = 15000      // Max effect duration (ticks)


    /** Current wine year from world daytime (24000 ticks/day) or [YEARS_START] if null. */
    fun getYear(world: Level?) = world?.let { YEARS_START + (it.dayTime / 24000 / DAYS_PER_YEAR).toInt() } ?: YEARS_START

    /** Effect level (0 to [MAX_LEVEL]) based on age / [YEARS_PER_EFFECT_LEVEL]. */
    fun getEffectLevel(wine: ItemStack, world: Level?) = max(0.0, min(MAX_LEVEL.toDouble(), (getWineAge(wine, world) / YEARS_PER_EFFECT_LEVEL).toDouble())).toInt()

    /** Age in years, or 0 if no "Year" NBT tag exists. */
    fun getWineAge(wine: ItemStack, world: Level?) = if (hasWineYear(wine)) 0 else getYear(world) - getWineYear(wine)

    /** Tags wine with production year in NBT under "Year". */
    fun setWineYear(wine: ItemStack, world: Level?) = wine.getOrCreateTag().putInt("Year", world?.let { getYear(it) } ?: YEARS_START)

    /** Production year from NBT "Year", or 0 if absent. */
    fun getWineYear(wine: ItemStack) = wine.getOrCreateTag().getInt("Year")

    /** Effect duration in ticks, capped at [MAX_DURATION]. */
    fun getEffectDuration(wine: ItemStack, world: Level?) = min((START_DURATION + DURATION_PER_YEAR * getWineAge(wine, world)).toDouble(), MAX_DURATION.toDouble()).toInt()

    /** True if wine lacks "Year" NBT tag. */
    fun hasWineYear(wine: ItemStack) = !wine.getOrCreateTag().contains("Year")
}

