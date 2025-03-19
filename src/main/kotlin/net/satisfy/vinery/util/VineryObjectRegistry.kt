package net.satisfy.vinery.util

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import net.minecraft.core.Registry.register
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.Vinery.Companion.log
import net.satisfy.vinery.block.LatticeBlock
import net.satisfy.vinery.block.PaleStemBlock
import net.satisfy.vinery.block.WineBottleBlock
import net.satisfy.vinery.item.DrinkBlockItem
import java.io.File
import java.util.function.Supplier


class VineryObjectRegistry {

    // Common properties for all wine bottles
    private val wineSettings = BlockBehaviour.Properties.copy(Blocks.GLASS).noOcclusion().instabreak()

    /** Grapevine stem block with wood-like properties. */
    val GRAPEVINE_STEM = register(
        BuiltInRegistries.BLOCK,
        ResourceLocation(MODID, "grapevine_stem"),
        PaleStemBlock(BlockBehaviour.Properties.of().strength(2.0f).randomTicks().sound(SoundType.WOOD).noOcclusion())
    )

    /**
     * Defines the properties of a wine bottle block, matching the structure expected in the `wines.json` file.
     *
     * @property name The unique identifier for the wine (e.g., "red_wine").
     * @property maxCount The potency or maximum stack count of the wine, defaults to 3 if not specified.
     */
    data class WineBLockDefinition(
        val name: String,
        val maxCount: Int = 3 // Default maxCount if not specified in JSON
    ) {
        companion object {
            /**
             * Loads wine block definitions from a JSON file.
             *
             * The JSON file should contain an array of objects with `name` (required) and `maxCount` (optional) fields.
             * Example:
             * ```
             * [
             *   {"name": "red_wine", "maxCount": 3},
             *   {"name": "chorus_wine"}
             * ]
             * ```
             *
             * @param filePath The filesystem path to the JSON file (e.g., "data/vinery/wines.json").
             * @return A list of [WineBlockDefinition] instances parsed from the JSON.
             * @throws IllegalStateException If the JSON file is not found at the specified path.
             */
            fun loadFromJson(filePath: String): List<WineBLockDefinition> {
                val gson = Gson()
                val file = File(filePath)
                if (!file.exists()) {
                    throw IllegalStateException("Wine definitions JSON file not found at $filePath")
                }
                val jsonString = file.readText()
                val listType = object : TypeToken<List<WineBLockDefinition>>() {}.type
                return gson.fromJson(jsonString, listType)
            }
        }
    }


    /**
     * Collection of wine types loaded from the JSON resource or fallback defaults.
     * Attempts to load from "data/[MODID]/wines.json"; if that fails, uses a predefined fallback list.
     */
    private val wineTypes: List<WineBLockDefinition> = try {
        WineBLockDefinition.loadFromJson("data/${MODID}/wines.json")
            .also { log.info("Load wines.json from: data/${MODID}/wines.json...") }
    } catch (e: Exception) {
        log.info("Failed to load wines.json: ${e.message}. Using fallback defaults.")
        listOf( // Fallback to avoid crashing
            WineBLockDefinition("chorus_wine", 1),
            WineBLockDefinition("cherry_wine"),
            WineBLockDefinition("magnetic_wine", 1),
            WineBLockDefinition("jo_special_mixture", 1),
            WineBLockDefinition("cristel_wine", 1),
            WineBLockDefinition("glowing_wine", 1),
            WineBLockDefinition("creepers_crush", 1),
            WineBLockDefinition("mead", 2),
            WineBLockDefinition("red_wine"),
            WineBLockDefinition("jellie_wine", 1),
            WineBLockDefinition("stal_wine"),
            WineBLockDefinition("noir_wine"),
            WineBLockDefinition("bolvar_wine"),
            WineBLockDefinition("solaris_wine"),
            WineBLockDefinition("eiswein", 2),
            WineBLockDefinition("chenet_wine", 2),
            WineBLockDefinition("kelp_cider"),
            WineBLockDefinition("aegis_wine", 2),
            WineBLockDefinition("clark_wine"),
            WineBLockDefinition("mellohi_wine", 2),
            WineBLockDefinition("strad_wine", 2),
            WineBLockDefinition("apple_cider", 2),
            WineBLockDefinition("apple_wine"),
            WineBLockDefinition("lilitu_wine", 1),
            WineBLockDefinition("bottle_mojang_noir"),
            WineBLockDefinition("villagers_fright")
        )
    }

    /**
     * Map of registered wine blocks, keyed by their [WineBlockDefinition.name] for easy access.
     * Each wine is registered as a [WineBottleBlock] instance during initialization.
     */
    val registeredWines: Map<String, WineBottleBlock> = wineTypes.associate { wine ->
        wine.name to registerWine(wine)
    }

    /**
     * Registers a wine block with the Minecraft registry.
     *
     * @param wine The [WineBlockDefinition] containing the wine’s name and properties.
     * @return The registered [WineBottleBlock] instance.
     */
    private fun registerWine(wine: WineBLockDefinition): WineBottleBlock {
        return register(
            BuiltInRegistries.BLOCK,
            ResourceLocation(MODID, wine.name),
            WineBottleBlock(wineSettings, wine.maxCount)
        )
    }

    //#=================================================================================================================

    /**
     * Defines the properties of a wine item, including its effect and duration settings.
     */
    data class WineItemDefinition(
        val name: String,
        val wineBlock: Block, // Reference to the associated WineBottleBlock
        val effect: Supplier<MobEffect?>,
        val duration: Int = 1600, // Default duration for most wines
        val strength: Int = 0, // Default strength
        val scaleDurationWithAge: Boolean = true // Whether duration scales with age
    )

    // List of all wine item definitions to register
    private val wineItemDefinitions = listOf(
        WineItemDefinition("apple_cider", registeredWines["apple_cider"]!!, { MobEffects.DAMAGE_BOOST }, 1600, 0, true),
        WineItemDefinition("apple_wine", registeredWines["apple_wine"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true),
        WineItemDefinition("mead", registeredWines["mead"]!!, { MobEffects.DIG_SPEED }, 1600, 0, true),
        WineItemDefinition("glowing_wine", registeredWines["glowing_wine"]!!, { MobEffects.GLOWING }, 1600, 0, true),
        WineItemDefinition("solaris_wine", registeredWines["solaris_wine"]!!, { MobEffects.HEALTH_BOOST }, 1600, 0, true),
        WineItemDefinition("kelp_cider", registeredWines["kelp_cider"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true), // MobEffectRegistry.WATER_WALKER.get()
        WineItemDefinition("eiswein", registeredWines["eiswein"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true), // MobEffectRegistry.FROSTY_ARMOR_EFFECT.get()
        WineItemDefinition("aegis_wine", registeredWines["aegis_wine"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true), // MobEffectRegistry.ARMOR_EFFECT.get()
        WineItemDefinition("villagers_fright", registeredWines["villagers_fright"]!!, { MobEffects.BAD_OMEN }, 1600, 0, true),
        WineItemDefinition("clark_wine", registeredWines["clark_wine"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true), // MobEffectRegistry.IMPROVED_JUMP_BOOST.get()
        WineItemDefinition("jellie_wine", registeredWines["jellie_wine"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true), // MobEffectRegistry.JELLIE.get()
        WineItemDefinition("noir_wine", registeredWines["noir_wine"]!!, { MobEffects.JUMP }, 1600, 0, true),
        WineItemDefinition("red_wine", registeredWines["red_wine"]!!, { MobEffects.SLOW_FALLING }, 1600, 0, true),
        WineItemDefinition("strad_wine", registeredWines["strad_wine"]!!, { MobEffects.NIGHT_VISION }, 1600, 0, true),
        WineItemDefinition("cherry_wine", registeredWines["cherry_wine"]!!, { MobEffects.INVISIBILITY }, 1600, 0, true),
        WineItemDefinition("cristel_wine", registeredWines["cristel_wine"]!!, { MobEffects.WATER_BREATHING }, 1600, 0, true),
        WineItemDefinition("lilitu_wine", registeredWines["lilitu_wine"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true), // MobEffectRegistry.PARTY_EFFECT.get()
        WineItemDefinition("jo_special_mixture", registeredWines["jo_special_mixture"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true), // MobEffectRegistry.CLIMBING_EFFECT.get()
        WineItemDefinition("bolvar_wine", registeredWines["bolvar_wine"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true), // MobEffectRegistry.LAVA_WALKER.get()
        WineItemDefinition("magnetic_wine", registeredWines["magnetic_wine"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true), // MobEffectRegistry.MAGNET.get()
        WineItemDefinition("stal_wine", registeredWines["stal_wine"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true), // MobEffectRegistry.HEALTH_EFFECT.get()
        WineItemDefinition("chenet_wine", registeredWines["chenet_wine"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true), // MobEffectRegistry.CLIMBING_EFFECT.get()
        WineItemDefinition("bottle_mojang_noir", registeredWines["bottle_mojang_noir"]!!, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true), // MobEffectRegistry.EXPERIENCE_EFFECT.get()
        WineItemDefinition("chorus_wine", registeredWines["chorus_wine"]!!, { MobEffects.DAMAGE_RESISTANCE }, 10, 0, false), // MobEffectRegistry.TELEPORT.get()
        WineItemDefinition("creepers_crush", registeredWines["creepers_crush"]!!, { MobEffects.DAMAGE_RESISTANCE }, 100, 0, false), // MobEffectRegistry.CREEPER_EFFECT.get()
        WineItemDefinition("mellohi_wine", registeredWines["mellohi_wine"]!!, { MobEffects.HEAL }, 0, 0, false)
    )

    // Map to store registered wine items for easy access
    val registeredWineItems: Map<String, Item> = wineItemDefinitions.associate { wine ->
        wine.name to registerWineItem(wine)
    }

    /**
     * Registers a wine item with the specified properties.
     */
    private fun registerWineItem(wine: WineItemDefinition): Item {
        val settings = createWineSettings(wine.effect, wine.duration, wine.strength)
        return register(
            BuiltInRegistries.ITEM,
            ResourceLocation(MODID, wine.name),
            DrinkBlockItem(
                wine.wineBlock,
                settings.get().properties,
                wine.name,
                settings.get().baseDuration,
                wine.scaleDurationWithAge
            )
        )
    }

    init {
        /**
         * Validates that there are no duplicate wine names in [wineTypes].
         * Throws an exception if duplicates are found, ensuring unique registration.
         */
        val wineBlockDuplicates = wineTypes.groupBy { it.name }.filter { it.value.size > 1 }
        check(wineBlockDuplicates.isEmpty()) { "Duplicate wine names found in JSON: ${wineBlockDuplicates.keys}" }
        // Validate no duplicate names
        val wineItemDuplicates = wineItemDefinitions.groupBy { it.name }.filter { it.value.size > 1 }
        check(wineItemDuplicates.isEmpty()) { "Duplicate wine item names found: ${wineItemDuplicates.keys}" }
    }

    /**
     * Creates wine settings with the given effect, duration, and strength.
     */
    private fun createWineSettings(effect: Supplier<MobEffect?>?, duration: Int, strength: Int): Supplier<WineSettings> {
        return Supplier { WineSettings(effect, duration, strength) }
    }

    /**
     * Map of lattice types to their registered blocks.
     *
     * Contains lattice blocks for various wood types (e.g., "oak", "spruce"), each registered with properties
     * derived from their corresponding plank blocks. Lattices are lightweight, non-occluding blocks with a strength
     * of 2.0 (breaking) and 3.0 (explosion resistance), using the sound type of their plank source.
     */
    private val LATTICES: Map<String, Block> = buildMap {
        /**
         * List of lattice types, mapping names to their plank block sources for sound properties.
         * Each entry defines a lattice variant registered under "${name}_lattice".
         */
        val latticeTypes = listOf(
            "oak" to Blocks.OAK_PLANKS,
            "spruce" to Blocks.SPRUCE_PLANKS,
            "cherry" to Blocks.CHERRY_PLANKS,
            "birch" to Blocks.BIRCH_PLANKS,
            "dark_oak" to Blocks.DARK_OAK_PLANKS,
            "acacia" to Blocks.ACACIA_PLANKS,
            "bamboo" to Blocks.BAMBOO_PLANKS,
            "jungle" to Blocks.JUNGLE_PLANKS,
            "mangrove" to Blocks.MANGROVE_PLANKS,
            "dark_cherry" to Blocks.MANGROVE_PLANKS // Adjust sound if dark cherry has a unique plank
        )

        latticeTypes.forEach { (name, plankBlock) ->
            put(
                name,
                register(
                    BuiltInRegistries.BLOCK,
                    ResourceLocation(MODID, "${name}_lattice"),
                    LatticeBlock(
                        BlockBehaviour.Properties.of()
                            .strength(2.0f, 3.0f)
                            .sound(plankBlock.getSoundType(plankBlock.defaultBlockState()))
                            .noOcclusion()
                    )
                )
            )
        }
    }

    // Convenience accessors (optional, if you need direct references)
    val OAK_LATTICE get() = LATTICES["oak"]!!
    val SPRUCE_LATTICE get() = LATTICES["spruce"]!!
    val CHERRY_LATTICE get() = LATTICES["cherry"]!!
    val BIRCH_LATTICE get() = LATTICES["birch"]!!
    val DARK_OAK_LATTICE get() = LATTICES["dark_oak"]!!
    val ACACIA_LATTICE get() = LATTICES["acacia"]!!
    val BAMBOO_LATTICE get() = LATTICES["bamboo"]!!
    val JUNGLE_LATTICE get() = LATTICES["jungle"]!!
    val MANGROVE_LATTICE get() = LATTICES["mangrove"]!!
    val DARK_CHERRY_LATTICE get() = LATTICES["dark_cherry"]!!

    // Convenience accessors (optional)
    val RED_WINE_ITEM: Item get() = registeredWineItems["red_wine"]!!
    val MEAD_ITEM: Item get() = registeredWineItems["mead"]!!
    val CHORUS_WINE_ITEM: Item get() = registeredWineItems["chorus_wine"]!!
    // Add more as needed

    // Convenience accessors for specific wines (optional)
    val RED_WINE: WineBottleBlock get() = registeredWines["red_wine"]!!
    val CHORUS_WINE: WineBottleBlock get() = registeredWines["chorus_wine"]!!
    val MEAD: WineBottleBlock get() = registeredWines["mead"]!!
    // Add more as needed

}