package net.satisfy.vinery.registry

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
import net.satisfy.vinery.util.WineSettings
import java.util.function.Supplier


/**
 * Registry for Vinery mod objects, managing wine blocks, wine items, and lattice blocks.
 * Wines are loaded from a unified `wines.json` file, defining both block and item properties.
 * Fallback defaults are provided if JSON loading fails. All registrations use Minecraft's
 * built-in registries via a generic `register` utility.
 */
class VineryObjectRegistry {

    /** Properties for wine bottle blocks, copied from glass with no occlusion and instant breaking. */
    private val wineSettings = BlockBehaviour.Properties.copy(Blocks.GLASS).noOcclusion().instabreak()

    /** Grapevine stem block with wood-like properties and random ticking. */
    val GRAPEVINE_STEM = register(
        BuiltInRegistries.BLOCK,
        ResourceLocation(MODID, "grapevine_stem"),
        PaleStemBlock(BlockBehaviour.Properties.of().strength(2.0f).randomTicks().sound(SoundType.WOOD).noOcclusion())
    )

    /**
     * Defines a wine's block and item properties, loaded from `wines.json`.
     *
     * @property name Unique identifier for the wine.
     * @property maxCount Maximum stack count for the block (default: 3).
     * @property effect Mob effect applied by the item, null if no item (default: null).
     * @property duration Effect duration in ticks (default: 1600).
     * @property strength Effect amplifier (default: 0).
     * @property scaleDurationWithAge Whether duration scales with age (default: true).
     */
    data class WineDefinition(
        val name: String,
        val maxCount: Int = 3,
        val effect: Supplier<MobEffect?>? = null,
        val duration: Int = 1600,
        val strength: Int = 0,
        val scaleDurationWithAge: Boolean = true
    ) {
        companion object {
            /**
             * Loads wine definitions from a JSON resource at the specified path.
             *
             * @param resourcePath Path to the JSON file (e.g., "data/vinery/wines.json").
             * @return List of parsed wine definitions.
             * @throws IllegalStateException If the JSON resource is not found.
             */
            fun loadFromJson(resourcePath: String): List<WineDefinition> {
                val gson = Gson()
                val inputStream = VineryObjectRegistry::class.java.classLoader.getResourceAsStream(resourcePath)
                    ?: throw IllegalStateException("Wine definitions JSON not found at $resourcePath")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                val listType = object : TypeToken<List<JsonWine>>() {}.type
                val jsonWines: List<JsonWine> = gson.fromJson(jsonString, listType)
                return jsonWines.map { json ->
                    WineDefinition(
                        name = json.name,
                        maxCount = json.maxCount ?: 3,
                        effect = json.effect?.let { effectId ->
                            Supplier { BuiltInRegistries.MOB_EFFECT.get(ResourceLocation(effectId)) }
                        },
                        duration = json.duration ?: 1600,
                        strength = json.strength ?: 0,
                        scaleDurationWithAge = json.scaleDurationWithAge ?: true
                    )
                }
            }
        }
    }

    /** JSON deserialization helper with nullable fields for optional properties. */
    private data class JsonWine(
        val name: String,
        val maxCount: Int? = null,
        val effect: String? = null,
        val duration: Int? = null,
        val strength: Int? = null,
        val scaleDurationWithAge: Boolean? = null
    )

    /** List of wine definitions loaded from JSON or fallback defaults if loading fails. */
    private val wineDefinitions: List<WineDefinition> = try {
        WineDefinition.loadFromJson("data/$MODID/wines.json")
            .also { log.info("Loaded wines.json from: data/$MODID/wines.json") }
    } catch (e: Exception) {
        log.info("Failed to load wines.json: ${e.message}. Using fallback defaults.")
        listOf(
            //TODO:Remove after building out Custom Mob Effects for Vinery
            WineDefinition("chorus_wine", 1, { MobEffectRegistry.TELEPORT }, 10, 0, false),
            WineDefinition("cherry_wine", 3, { MobEffects.INVISIBILITY }, 1600, 0, true),
            WineDefinition("magnetic_wine", 1, { MobEffectRegistry.MAGNET }, 1600, 0, true),
            WineDefinition("jo_special_mixture", 1, { MobEffectRegistry.CLIMBING_EFFECT }, 1600, 0, true),
            WineDefinition("cristel_wine", 1, { MobEffects.WATER_BREATHING }, 1600, 0, true),
            WineDefinition("glowing_wine", 1, { MobEffects.GLOWING }, 1600, 0, true),
            WineDefinition("creepers_crush", 1, { MobEffectRegistry.CREEPER_EFFECT }, 100, 0, false),
            WineDefinition("mead", 2, { MobEffects.DIG_SPEED }, 1600, 0, true),
            WineDefinition("red_wine", 3, { MobEffects.SLOW_FALLING }, 1600, 0, true),
            WineDefinition("jellie_wine", 1, { MobEffectRegistry.JELLIE }, 1600, 0, true),
            WineDefinition("stal_wine", 3, { MobEffectRegistry.HEALTH_EFFECT }, 1600, 0, true),
            WineDefinition("noir_wine", 3, { MobEffects.JUMP }, 1600, 0, true),
            WineDefinition("bolvar_wine", 3, { MobEffectRegistry.LAVA_WALKER }, 1600, 0, true),
            WineDefinition("solaris_wine", 3, { MobEffects.HEALTH_BOOST }, 1600, 0, true),
            WineDefinition("eiswein", 2, { MobEffectRegistry.ARMOR_EFFECT }, 1600, 0, true),
            WineDefinition("chenet_wine", 2, { MobEffectRegistry.CLIMBING_EFFECT }, 1600, 0, true),
            WineDefinition("kelp_cider", 3, { MobEffectRegistry.WATER_WALKER }, 1600, 0, true),
            WineDefinition("aegis_wine", 2, { MobEffectRegistry.ARMOR_EFFECT }, 1600, 0, true),
            WineDefinition("clark_wine", 3, { MobEffectRegistry.IMPROVED_JUMP_BOOST }, 1600, 0, true),
            WineDefinition("mellohi_wine", 2, { MobEffects.HEAL }, 0, 0, false),
            WineDefinition("strad_wine", 2, { MobEffects.NIGHT_VISION }, 1600, 0, true),
            WineDefinition("apple_cider", 3, { MobEffects.DAMAGE_BOOST }, 1600, 0, true),
            WineDefinition("apple_wine", 3, { MobEffects.DAMAGE_RESISTANCE }, 1600, 0, true),
            WineDefinition("lilitu_wine", 1, { MobEffectRegistry.PARTY_EFFECT }, 1600, 0, true),
            WineDefinition("bottle_mojang_noir", 3, { MobEffectRegistry.EXPERIENCE_EFFECT }, 1600, 0, true),
            WineDefinition("villagers_fright", 3, { MobEffects.BAD_OMEN }, 1600, 0, true)
        )
    }

    /** Registered wine blocks, mapped by name from wine definitions. */
    val registeredWines: Map<String, WineBottleBlock> = wineDefinitions.associate { wine ->
        wine.name to registerWine(wine)
    }

    /**
     * Registers a wine block with the given definition.
     *
     * @param wine Wine definition containing block properties.
     * @return Registered WineBottleBlock instance.
     */
    private fun registerWine(wine: WineDefinition): WineBottleBlock {
        return register(
            BuiltInRegistries.BLOCK,
            ResourceLocation(MODID, wine.name),
            WineBottleBlock(wineSettings, wine.maxCount)
        )
    }

    /** Registered wine items, mapped by name for wines with effects. */
    val registeredWineItems: Map<String, Item> = wineDefinitions
        .filter { it.effect != null }
        .associate { wine ->
            wine.name to registerWineItem(wine)
        }

    /**
     * Registers a wine item with the given definition.
     *
     * @param wine Wine definition containing item properties.
     * @return Registered DrinkBlockItem instance.
     */
    private fun registerWineItem(wine: WineDefinition): Item {
        val settings = createWineSettings(wine.effect!!, wine.duration, wine.strength)
        val wineBlock = registeredWines[wine.name] ?: throw IllegalStateException("No block registered for wine: ${wine.name}")
        return register(
            BuiltInRegistries.ITEM,
            ResourceLocation(MODID, wine.name),
            DrinkBlockItem(
                wineBlock,
                settings.get().properties,
                wine.name,
                settings.get().baseDuration,
                wine.scaleDurationWithAge
            )
        )
    }

    /**
     * Creates wine settings for an item.
     *
     * @param effect Mob effect supplier.
     * @param duration Effect duration in ticks.
     * @param strength Effect amplifier.
     * @return Supplier providing WineSettings instance.
     */
    private fun createWineSettings(effect: Supplier<MobEffect?>, duration: Int, strength: Int): Supplier<WineSettings> {
        return Supplier { WineSettings(effect, duration, strength) }
    }

    init {
        val duplicates = wineDefinitions.groupBy { it.name }.filter { it.value.size > 1 }
        check(duplicates.isEmpty()) { "Duplicate wine names found in JSON: ${duplicates.keys}" }
    }

    /** Lattice blocks mapped by wood type, using plank properties. */
    private val LATTICES: Map<String, Block> = buildMap {
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
            "dark_cherry" to Blocks.MANGROVE_PLANKS
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

    // Convenience accessors for lattices
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

    // Convenience accessors for wine items
    val RED_WINE_ITEM: Item get() = registeredWineItems["red_wine"]!!
    val MEAD_ITEM: Item get() = registeredWineItems["mead"]!!
    val CHORUS_WINE_ITEM: Item get() = registeredWineItems["chorus_wine"]!!

    // Convenience accessors for wine blocks
    val RED_WINE: WineBottleBlock get() = registeredWines["red_wine"]!!
    val CHORUS_WINE: WineBottleBlock get() = registeredWines["chorus_wine"]!!
    val MEAD: WineBottleBlock get() = registeredWines["mead"]!!
}