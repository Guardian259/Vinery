package net.satisfy.vinery.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.core.Registry.register
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.food.Foods
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.Vinery.Companion.log
import net.satisfy.vinery.block.FermentationBarrelBlock
import net.satisfy.vinery.block.GrapeBush
import net.satisfy.vinery.block.GrapeVineBlock
import net.satisfy.vinery.item.GrapeBushSeedItem
import net.satisfy.vinery.item.GrapeItem
import net.satisfy.vinery.item.GrapejuiceBottleItem
import net.satisfy.vinery.item.VineryItem


/**
 * Registry for grape-related objects in the Vinery mod, managing grape types and their associated items.
 * Grape variants are loaded from `grapes.json`, with fallbacks for predefined types if loading fails.
 */
object VineryGrapeRegistry {
    /** Set of all registered grape types. */
    val GRAPE_TYPES: MutableSet<GrapeType> = HashSet()

    /** Base wine bottle item, used for grape juice generation. */
    val WINE_BOTTLE: VineryItem = register(BuiltInRegistries.ITEM, ResourceLocation(MODID, "wine_bottle"), VineryItem(Item.Properties(), "wine_bottle"))

    /** Fermentation barrel block, copied from barrel properties with no occlusion. */
    val FERMENTATION_BARREL: FermentationBarrelBlock = register(BuiltInRegistries.BLOCK, ResourceLocation(MODID, "fermentation_barrel"), FermentationBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL).noOcclusion()))

    /**
     * Represents a grape variant with properties loaded from JSON or defined programmatically.
     *
     * @property id Unique identifier for the variant (e.g., "red_jungle").
     * @property lineage Tracks the heritage of the variant (e.g., "red", "red_jungle").
     * @property prefix Optional prefix for the variant (e.g., "wild").
     * @property suffix Optional suffix for the variant (e.g., "elite").
     * @property needsLattice Whether the variant requires a lattice to grow.
     */
    open class GrapeVariant(
        val id: String,
        val lineage: String,
        val prefix: String = "",
        val suffix: String = "",
        val needsLattice: Boolean = false
    ) {
        /**
         * A dynamic variant that can evolve and have its lineage modified.
         *
         * @property customId Base identifier, mutable to support evolution.
         * @property customLineage Heritage tracker, mutable for evolution.
         * @property customPrefix Optional prefix (immutable).
         * @property customSuffix Optional suffix, mutable for evolution updates.
         * @property customNeedsLattice Whether this variant requires a lattice.
         */
        data class CustomVariant(
            var customId: String,
            var customLineage: String,
            val customPrefix: String = "",
            var customSuffix: String = "",
            val customNeedsLattice: Boolean
        ) : GrapeVariant(
            id = buildId(customId, customPrefix, customSuffix),
            lineage = customLineage,
            prefix = customPrefix,
            suffix = customSuffix,
            needsLattice = customNeedsLattice
        ) {
            /** Evolves this variant into a distinct entity, optionally appending a suffix. */
            fun evolveToDistinct() {
                if (shouldEvolve()) {
                    val newSuffix = generateEvolutionSuffix()
                    customLineage = customId
                    if (newSuffix.isNotEmpty()) {
                        customId = buildId(customId, customPrefix, newSuffix)
                        customSuffix = newSuffix
                    }
                }
            }

            /** Checks if this variant should evolve (placeholder). */
            private fun shouldEvolve(): Boolean = false // Future criteria TBD
        }

        companion object {
            /** Loads grape variants from a JSON resource or returns fallback defaults. */
            fun loadVariants(): List<GrapeVariant> {
                return try {
                    val gson = Gson()
                    val inputStream = VineryGrapeRegistry::class.java.classLoader.getResourceAsStream("data/$MODID/grapes.json")
                        ?: throw IllegalStateException("Grape variants JSON not found at data/$MODID/grapes.json")
                    val jsonString = inputStream.bufferedReader().use { it.readText() }
                    val listType = object : TypeToken<List<JsonGrapeVariant>>() {}.type
                    val jsonVariants: List<JsonGrapeVariant> = gson.fromJson(jsonString, listType)
                    log.info("Loaded grapes.json from: data/$MODID/grapes.json")
                    jsonVariants.map { json ->
                        GrapeVariant(
                            id = json.id,
                            lineage = json.lineage,
                            prefix = json.prefix ?: "",
                            suffix = json.suffix ?: "",
                            needsLattice = json.needsLattice ?: false
                        )
                    }
                } catch (e: Exception) {
                    log.info("Failed to load grapes.json: ${e.message}. Using fallback defaults.")
                    listOf(
                        GrapeVariant("none", "none", "", "", false),
                        GrapeVariant("red", "red", "", "", false),
                        GrapeVariant("white", "white", "", "", false),
                        GrapeVariant("savanna_red", "red_savanna", "", "", false),
                        GrapeVariant("savanna_white", "white_savanna", "", "", false),
                        GrapeVariant("taiga_red", "red_taiga", "", "", false),
                        GrapeVariant("taiga_white", "white_taiga", "", "", false),
                        GrapeVariant("jungle_red", "red_jungle", "", "", true),
                        GrapeVariant("jungle_white", "white_jungle", "", "", true)
                    )
                }
            }

            /** JSON deserialization helper with nullable fields. */
            private data class JsonGrapeVariant(
                val id: String,
                val lineage: String,
                val prefix: String? = null,
                val suffix: String? = null,
                val needsLattice: Boolean? = null
            )

            /** Builds an id by combining base, prefix, and suffix with separators. */
            private fun buildId(base: String, prefix: String, suffix: String): String =
                "${if (prefix.isNotEmpty()) "${prefix}_" else ""}${base}${if (suffix.isNotEmpty()) "_$suffix" else ""}"

            /** Placeholder for generating an optional prefix. */
            private fun generatePrefix(): String = "" // TBD (e.g., biome-based)

            /** Placeholder for generating an optional suffix. */
            private fun generateSuffix(): String = "" // TBD (e.g., random trait)

            /** Placeholder for generating an evolution suffix. */
            private fun generateEvolutionSuffix(): String = "" // TBD (e.g., "prime")

            /** Creates a derived variant from a parent lineage. */
            fun createVariant(
                parentLineage: String,
                modifier: String,
                prefix: String = generatePrefix(),
                suffix: String = generateSuffix()
            ): GrapeVariant {
                val baseId = "${parentLineage}_$modifier"
                val newLineage = "${parentLineage}_$modifier"
                val needsLattice = modifier in listOf("jungle", "tropical", "vine")
                return CustomVariant(baseId, newLineage, prefix, suffix, needsLattice)
            }

            /** Combines two variants into a hybrid. */
            fun crossBreed(
                parent1: GrapeVariant,
                parent2: GrapeVariant,
                prefix: String = generatePrefix(),
                suffix: String = generateSuffix()
            ): GrapeVariant {
                val baseId = "${parent1.lineage}_${parent2.lineage}_hybrid"
                val newLineage = "${parent1.lineage}_${parent2.lineage}"
                val needsLattice = parent1.needsLattice || parent2.needsLattice
                return CustomVariant(baseId, newLineage, prefix, suffix, needsLattice)
            }
        }
    }

    /**
     * Data class representing a set of items associated with a grape variant.
     *
     * @param type The grape type associated with this set.
     * @param bush The bush block (if any).
     * @param seeds The seed item (if any).
     * @param grape The grape item (if any).
     * @param juice The juice item (if any).
     */
    data class GrapeSet<Block>(
        val type: GrapeType,
        val bush: Block? = null,
        val seeds: Item? = null,
        val grape: Item? = null,
        val juice: Item? = null
    )

    /** Map of grape variants to their associated sets, populated from JSON or fallback. */
    private val grapeSets: MutableMap<GrapeVariant, GrapeSet<Block>> = buildMap<GrapeVariant, GrapeSet<Block>> {
        GrapeVariant.loadVariants().forEach { variant ->
            val grapeType = registerGrapeType(variant.id, variant.needsLattice)
            GRAPE_TYPES.add(grapeType)

            if (variant.id == "none") {
                put(variant, GrapeSet(grapeType))
            } else {
                val bushProperties = BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
                val bushName = if (variant.id == "red" || variant.id == "white") "${variant.id}_grape_bush" else "${variant.id.split("_")[0]}_grape_bush_${variant.id.split("_")[1]}"
                val bush = if (variant.needsLattice) registerGrapeVine(bushName, bushProperties, grapeType) else registerGrapeBush(bushName, bushProperties, grapeType)
                val seedsName = if (variant.id == "red" || variant.id == "white") "${variant.id}_grape_seeds" else "${variant.id.split("_")[0]}_grape_seeds_${variant.id.split("_")[1]}"
                val seeds = registerGrapeSeeds(seedsName, GrapeBushSeedItem(bush, Item.Properties(), grapeType, seedsName))
                val grapeName = if (variant.id == "red" || variant.id == "white") "${variant.id}_grape" else "${variant.id.split("_")[0]}_grapes_${variant.id.split("_")[1]}"
                val grape = registerGrapes(grapeName, GrapeItem(Item.Properties().food(Foods.SWEET_BERRIES), grapeType, seeds, grapeName))
                val grapeJuice = if (variant.id == "red" || variant.id == "white") "${variant.id}_grapejuice" else "${variant.id.split("_")[1]}_${variant.id.split("_")[0]}_grapejuice"
                val juice = register(BuiltInRegistries.ITEM, ResourceLocation(MODID, grapeJuice), GrapejuiceBottleItem(Item.Properties().craftRemainder(WINE_BOTTLE.asItem()), grapeJuice))
                grapeType.setItems({ grape }, { seeds }, { juice })
                put(variant, GrapeSet(grapeType, bush, seeds, grape, juice))
            }
        }
    }.toMutableMap()

    // Convenience accessors for predefined variants (using lazy initialization)
    val NONE by lazy { grapeSets.entries.find { it.key.id == "none" }!!.value.type }
    val RED by lazy { grapeSets.entries.find { it.key.id == "red" }!!.value }
    val WHITE by lazy { grapeSets.entries.find { it.key.id == "white" }!!.value }
    val SAVANNA_RED by lazy { grapeSets.entries.find { it.key.id == "savanna_red" }!!.value }
    //TODO: MOVE AWAY FROM STRICTLY ACCESSED VARIANTS
    val JUNGLE_RED by lazy { grapeSets.entries.find { it.key.id == "jungle_red" }!!.value }
    val JUNGLE_WHITE by lazy { grapeSets.entries.find { it.key.id == "jungle_white" }!!.value }

    //TODO:Will currently break with the fixed grapeSets system which sends the element name to the classes. determine if this should be removed or reworked
    /**
     * Registers a custom grape variant and its associated items in the grape sets.
     *
     * This function creates and registers the grape type, bush, seeds, grape item, and juice item
     * for the given custom variant, then stores them in the [grapeSets] map.
     *
     * @param variant The custom grape variant to register.
     */
//    fun registerCustomVariant(variant: GrapeVariant.CustomVariant) {
//        val grapeType = registerGrapeType(variant.id, variant.needsLattice)
//        GRAPE_TYPES.add(grapeType)
//
//        val bushProperties = BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
//        val bush = if (variant.needsLattice) {
//            registerGrapeVine("${variant.id}_grape_bush", bushProperties, grapeType)
//        } else {
//            registerGrapeBush("${variant.id}_grape_bush", bushProperties, grapeType)
//        }
//
//        val seeds = registerGrapeSeeds("${variant.id}_grape_seeds",
//            GrapeBushSeedItem(bush, Item.Properties(), grapeType))
//
//        val grape = registerGrapes("${variant.id}_grape",
//            GrapeItem(Item.Properties().food(Foods.SWEET_BERRIES), grapeType, seeds))
//
//        val juice = register(BuiltInRegistries.ITEM,
//            ResourceLocation(MODID, "${variant.id}_grapejuice"),
//            GrapejuiceBottleItem(Item.Properties().craftRemainder(WINE_BOTTLE.asItem())))
//
//        grapeSets[variant] = GrapeSet(grapeType, bush, seeds, grape, juice)
//    }

    // Registration functions
    private fun registerGrapes(name: String, item: Item) =
        register(BuiltInRegistries.ITEM, ResourceLocation(MODID, name), item)

    private fun registerGrapeSeeds(name: String, item: Item) =
        register(BuiltInRegistries.ITEM, ResourceLocation(MODID, name), item)

    private fun registerGrapeBush(name: String, properties: BlockBehaviour.Properties, type: GrapeType) =
        register(BuiltInRegistries.BLOCK, ResourceLocation(MODID, name), GrapeBush(properties, type))

    private fun registerGrapeVine(name: String, properties: BlockBehaviour.Properties, type: GrapeType) =
        register(BuiltInRegistries.BLOCK, ResourceLocation(MODID, name), GrapeVineBlock(properties, type))

    private fun registerGrapeType(id: String, lattice: Boolean): GrapeType = GrapeType(id, lattice)

    private fun registerBlock(name: String, block: Block): Block = register(BuiltInRegistries.BLOCK, ResourceLocation(MODID, name), block)

    private fun registerBlockItem(name: String, item: BlockItem): BlockItem = register(BuiltInRegistries.ITEM, ResourceLocation(MODID, name), item)
}