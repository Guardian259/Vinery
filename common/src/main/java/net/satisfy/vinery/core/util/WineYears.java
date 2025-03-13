package net.satisfy.vinery.core.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.satisfy.vinery.platform.PlatformHelper;

/**
 * A utility class for managing wine aging mechanics in a Minecraft mod environment.
 * Tracks wine production years, calculates age-based effect levels and durations,
 * and integrates with platform-specific constants from {@link PlatformHelper}.
 * All time calculations are based on Minecraft's daytime ticks (24000 ticks per day).
 */
public class WineYears {
	/** Starting year for wine age calculations. */
	public static final int YEARS_START = 0;
	/** Maximum wine effect level. */
	public static final int MAX_LEVEL = PlatformHelper.getWineMaxLevel();
	/** Base duration of wine effects in ticks. */
	public static final int START_DURATION = PlatformHelper.getWineStartDuration();
	/** Additional duration per year of aging in ticks. */
	public static final int DURATION_PER_YEAR = PlatformHelper.getWineDurationPerYear();
	/** Minecraft days per wine year. */
	public static final int DAYS_PER_YEAR = PlatformHelper.getWineDaysPerYear();
	/** Years required to increase effect level by 1. */
	public static final int YEARS_PER_EFFECT_LEVEL = PlatformHelper.getWineYearsPerEffectLevel();
	/** Maximum duration of wine effects in ticks. */
	public static final int MAX_DURATION = PlatformHelper.getWineMaxDuration();

	/**
	 * Calculates the current wine year based on the world's daytime in ticks.
	 * Assumes 24000 ticks per Minecraft day and divides by {@link #DAYS_PER_YEAR} to determine years.
	 * @param world The level providing daytime via {@link Level#getDayTime()}, or null.
	 * @return The calculated year as {@link #YEARS_START} + (daytime / 24000 / {@link #DAYS_PER_YEAR}),
	 *         cast to int (truncating fractions), or {@link #YEARS_START} if world is null.
	 */
	public static int getYear(Level world) {
		return world != null ? YEARS_START + (int) (world.getDayTime() / 24000 / DAYS_PER_YEAR) : YEARS_START;
	}

	/**
	 * Computes the effect level of a wine based on its age in years.
	 * Age is divided by {@link #YEARS_PER_EFFECT_LEVEL}, then clamped between 0 and {@link #MAX_LEVEL}.
	 * @param wine The {@link ItemStack} containing wine NBT data, assumed non-null.
	 * @param world The level for age calculation, passed to {@link #getWineAge(ItemStack, Level)}.
	 * @return The effect level as an integer between 0 and {@link #MAX_LEVEL}, inclusive.
	 */
	public static int getEffectLevel(ItemStack wine, Level world) {
		return Math.max(0, Math.min(MAX_LEVEL, getWineAge(wine, world) / YEARS_PER_EFFECT_LEVEL));
	}

	/**
	 * Calculates the age of a wine by subtracting its stored production year from the current year.
	 * Returns 0 if no year is set (i.e., {@link #hasWineYear(ItemStack)} is true).
	 * @param wine The {@link ItemStack} with NBT data, assumed non-null.
	 * @param world The level for current year calculation via {@link #getYear(Level)}.
	 * @return The age in years, or 0 if the wine lacks a "Year" NBT tag.
	 */
	public static int getWineAge(ItemStack wine, Level world) {
		if (hasWineYear(wine)) {
			return 0;
		}
		return getYear(world) - getWineYear(wine);
	}

	/**
	 * Tags a wine item with its production year in its NBT data under the "Year" key.
	 * Creates or updates the NBT tag using {@link ItemStack#getOrCreateTag()}.
	 * @param wine The {@link ItemStack} to tag, assumed non-null.
	 * @param world The level for current year via {@link #getYear(Level)}, or null to use {@link #YEARS_START}.
	 * @implNote Does nothing if {@link Level#getDayTime()} is unavailable when world is null.
	 */
	public static void setWineYear(ItemStack wine, Level world) {
		if (world != null) {
			wine.getOrCreateTag().putInt("Year", getYear(world));
		} else {
			wine.getOrCreateTag().putInt("Year", YEARS_START);
		}
	}

	/**
	 * Retrieves the production year from a wine item's NBT data.
	 * Uses {@link CompoundTag#getInt(String)} with key "Year".
	 * @param wine The {@link ItemStack} with NBT data, assumed non-null.
	 * @return The stored year as an integer, or 0 if the "Year" key is absent.
	 */
	public static int getWineYear(ItemStack wine) {
		CompoundTag nbt = wine.getOrCreateTag();
		return nbt.getInt("Year");
	}

	/**
	 * Computes the effect duration of a wine in ticks based on its age.
	 * Formula: {@link #START_DURATION} + ({@link #DURATION_PER_YEAR} * age), capped at {@link #MAX_DURATION}.
	 * @param wine The {@link ItemStack} for age calculation via {@link #getWineAge(ItemStack, Level)}.
	 * @param world The level for age calculation, assumed non-null for meaningful results.
	 * @return The duration in ticks, guaranteed to be between {@link #START_DURATION} and {@link #MAX_DURATION}.
	 */
	public static int getEffectDuration(ItemStack wine, Level world) {
		int age = getWineAge(wine, world);
		int duration = START_DURATION + (DURATION_PER_YEAR * age);
		return Math.min(duration, MAX_DURATION);
	}

	/**
	 * Checks if a wine item lacks a production year in its NBT data.
	 * Relies on {@link CompoundTag#contains(String)} with key "Year".
	 * @param wine The {@link ItemStack} to inspect, assumed non-null.
	 * @return True if the "Year" key is absent (indicating no year set), false otherwise.
	 * @implNote Returns true for new wines until tagged by {@link #setWineYear(ItemStack, Level)}.
	 */
	public static boolean hasWineYear(ItemStack wine) {
		return !wine.getOrCreateTag().contains("Year");
	}
}

