package net.satisfy.vinery.util

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
import net.satisfy.vinery.block.FermentationBarrelBlock
import net.satisfy.vinery.block.GrapeBush
import net.satisfy.vinery.block.GrapeVineBlock
import net.satisfy.vinery.item.GrapeBushSeedItem
import net.satisfy.vinery.item.GrapeItem
import net.satisfy.vinery.item.GrapejuiceBottleItem
import net.satisfy.vinery.item.VineryItem


object VineryGrapeRegistry {
    /** Set of all registered grape types. */
    val GRAPE_TYPES: MutableSet<GrapeType> = HashSet()

    /** Base wine bottle item, used for grape juice generation */
    val WINE_BOTTLE = register(BuiltInRegistries.ITEM, ResourceLocation(MODID, "wine_bottle"), VineryItem(Item.Properties()))
    val FERMENTATION_BARREL = register(BuiltInRegistries.BLOCK, ResourceLocation(MODID, "fermentation_barrel"), FermentationBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL).noOcclusion()))

    /**
     * Represents a grape variant with a flexible lineage and optional prefix/suffix.
     *
     * @property id Unique identifier for the variant (e.g., "red_jungle").
     * @property lineage Tracks the heritage of the variant (e.g., "red", "red_jungle").
     * @property prefix Optional prefix for the variant (e.g., "wild").
     * @property suffix Optional suffix for the variant (e.g., "elite").
     * @property needsLattice Whether the variant requires a lattice to grow.
     */
    sealed class GrapeVariant(
        val id: String,
        val lineage: String,
        val prefix: String = "",
        val suffix: String = "",
        val needsLattice: Boolean = false
    ) {
        // Predefined variants with initial lineages
        data object NONE : GrapeVariant("none", "none")
        data object RED : GrapeVariant("red", "red")
        data object WHITE : GrapeVariant("white", "white")
        data object SAVANNA_RED : GrapeVariant("savanna_red", "red_savanna")
        data object SAVANNA_WHITE : GrapeVariant("savanna_white", "white_savanna")
        data object TAIGA_RED : GrapeVariant("taiga_red", "red_taiga")
        data object TAIGA_WHITE : GrapeVariant("taiga_white", "white_taiga")
        data object JUNGLE_RED : GrapeVariant("jungle_red", "red_jungle", needsLattice = true)
        data object JUNGLE_WHITE : GrapeVariant("jungle_white", "white_jungle", needsLattice = true)

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
            /**
             * Evolves this variant into a distinct entity, optionally appending a suffix.
             * The lineage is updated to match the current id, and a new suffix may be added.
             */
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

            /** Checks if this variant should evolve based on undefined criteria. */
            private fun shouldEvolve(): Boolean {
                return false // Placeholder for future criteria (e.g., growth success)
            }
        }

        companion object {
            /** List of all predefined grape variants. */
            val predefinedVariants: List<GrapeVariant> = listOf(
                NONE, RED, WHITE, SAVANNA_RED, SAVANNA_WHITE,
                TAIGA_RED, TAIGA_WHITE, JUNGLE_RED, JUNGLE_WHITE
            )

            /** Builds an id by combining base, prefix, and suffix with proper separators. */
            private fun buildId(base: String, prefix: String, suffix: String): String {
                return "${if (prefix.isNotEmpty()) "${prefix}_" else ""}${base}${if (suffix.isNotEmpty()) "_$suffix" else ""}"
            }

            /** Generates an optional prefix (placeholder for future system). */
            private fun generatePrefix(): String {
                return "" // Default until system defined (e.g., biome-based)
            }

            /** Generates an optional suffix (placeholder for future system). */
            private fun generateSuffix(): String {
                return "" // Default until system defined (e.g., random trait)
            }

            /** Generates an optional suffix for evolved variants (placeholder). */
            private fun generateEvolutionSuffix(): String {
                return "" // Default until system defined (e.g., "prime")
            }

            /**
             * Creates a derived variant from a parent lineage with optional prefix/suffix.
             *
             * @param parentLineage The base lineage to extend (e.g., "red").
             * @param modifier The variant-specific modifier (e.g., "desert").
             * @param prefix Optional prefix (defaults to generated value).
             * @param suffix Optional suffix (defaults to generated value).
             * @return A new CustomVariant instance.
             */
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

            /**
             * Combines two variants into a hybrid with optional prefix/suffix.
             *
             * @param parent1 First parent variant.
             * @param parent2 Second parent variant.
             * @param prefix Optional prefix (defaults to generated value).
             * @param suffix Optional suffix (defaults to generated value).
             * @return A new CustomVariant instance representing the hybrid.
             */
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

    /** Map of grape variants to their associated sets. */
    private val grapeSets: MutableMap<GrapeVariant, GrapeSet<Block>> = buildMap<GrapeVariant, GrapeSet<Block>> {
        GrapeVariant.predefinedVariants.forEach { variant ->
            val grapeType = registerGrapeType(variant.id, variant.needsLattice)
            GRAPE_TYPES.add(grapeType)

            if (variant == GrapeVariant.NONE) {
                put(variant, GrapeSet(grapeType))
            } else {
                val bushProperties = BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
                val bushName = if (variant.id == GrapeVariant.RED.id || variant.id == GrapeVariant.WHITE.id) "${variant.id}_grape_bush" else "${variant.id.split("_")[0]}_grape_bush_${variant.id.split("_")[1]}"
                val bush = if (variant.needsLattice) registerGrapeVine(bushName, bushProperties, grapeType) else  registerGrapeBush(bushName, bushProperties, grapeType)
                val seedsName = if (variant.id == GrapeVariant.RED.id || variant.id == GrapeVariant.WHITE.id) "${variant.id}_grape_seeds" else "${variant.id.split("_")[0]}_grape_seeds_${variant.id.split("_")[1]}"
                val seeds = registerGrapeSeeds(seedsName, GrapeBushSeedItem(bush, Item.Properties(), grapeType))
                val grapeName = if (variant.id == GrapeVariant.RED.id || variant.id == GrapeVariant.WHITE.id) "${variant.id}_grape" else "${variant.id.split("_")[0]}_grapes_${variant.id.split("_")[1]}"
                val grape = registerGrapes(grapeName, GrapeItem(Item.Properties().food(Foods.SWEET_BERRIES), grapeType, seeds))
                val juice = register(BuiltInRegistries.ITEM, ResourceLocation(MODID, "${variant.id}_grapejuice"), GrapejuiceBottleItem(Item.Properties().craftRemainder(WINE_BOTTLE.asItem())))
                put(variant, GrapeSet(grapeType, bush, seeds, grape, juice))
            }
        }
    }.toMutableMap()

    // Convenience accessors for predefined variants
    val NONE = grapeSets[GrapeVariant.NONE]!!.type
    val RED = grapeSets[GrapeVariant.RED]!!
    val WHITE = grapeSets[GrapeVariant.WHITE]!!
    val SAVANNA_RED = grapeSets[GrapeVariant.SAVANNA_RED]!!
    val JUNGLE_RED = grapeSets[GrapeVariant.JUNGLE_RED]!!
    val JUNGLE_WHITE = grapeSets[GrapeVariant.JUNGLE_WHITE]!!

    /**
     * Registers a custom grape variant and its associated items in the grape sets.
     *
     * This function creates and registers the grape type, bush, seeds, grape item, and juice item
     * for the given custom variant, then stores them in the [grapeSets] map.
     *
     * @param variant The custom grape variant to register.
     */
    fun registerCustomVariant(variant: GrapeVariant.CustomVariant) {
        val grapeType = registerGrapeType(variant.id, variant.needsLattice)
        GRAPE_TYPES.add(grapeType)

        val bushProperties = BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
        val bush = if (variant.needsLattice) {
            registerGrapeVine("${variant.id}_grape_bush", bushProperties, grapeType)
        } else {
            registerGrapeBush("${variant.id}_grape_bush", bushProperties, grapeType)
        }

        val seeds = registerGrapeSeeds("${variant.id}_grape_seeds",
            GrapeBushSeedItem(bush, Item.Properties(), grapeType))

        val grape = registerGrapes("${variant.id}_grape",
            GrapeItem(Item.Properties().food(Foods.SWEET_BERRIES), grapeType, seeds))

        val juice = register(BuiltInRegistries.ITEM,
            ResourceLocation(MODID, "${variant.id}_grapejuice"),
            GrapejuiceBottleItem(Item.Properties().craftRemainder(WINE_BOTTLE.asItem())))

        grapeSets[variant] = GrapeSet(grapeType, bush, seeds, grape, juice)
    }

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