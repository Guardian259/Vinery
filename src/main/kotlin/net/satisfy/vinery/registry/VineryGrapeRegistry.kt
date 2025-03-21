package net.satisfy.vinery.registry

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.core.Registry.register
import net.minecraft.core.registries.BuiltInRegistries.ITEM as ITEM
import net.minecraft.core.registries.BuiltInRegistries.BLOCK as BLOCK
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable
import net.minecraft.world.food.Foods
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.Property
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.Vinery.Companion.log
import net.satisfy.vinery.block.GrapeBush
import net.satisfy.vinery.block.GrapeVineBlock
import net.satisfy.vinery.item.GrapeBushSeedItem
import net.satisfy.vinery.item.GrapeItem
import net.satisfy.vinery.item.GrapejuiceBottleItem
import net.satisfy.vinery.item.VineryItem
import java.util.*
import java.util.function.Supplier

/**
 * Registry for grape-related objects in the Vinery mod, managing grape types and their associated items.
 * Grape variants are loaded from `grape_variants.json`, with fallbacks for predefined types if loading fails.
 * This object integrates grape type definitions, block state properties, and variant definitions for a centralized system.
 */
object VineryGrapeRegistry {
    // Constants and Public Fields
    /** Set of all registered grape types. */
    val GRAPE_TYPES: MutableSet<GrapeTypeDefinition> = HashSet()

    /** Base wine bottle item, used for grape juice generation. */
    val WINE_BOTTLE: VineryItem = register(
        ITEM,
        ResourceLocation(MODID, "wine_bottle"),
        VineryItem(Item.Properties(), "wine_bottle")
    )

    /** Convenience accessor for the "none" grape type, using lazy initialization. */
    val NONE by lazy { grapeSets.entries.find { it.key.id == "none" }!!.value.type }

    // Inner Classes and Types
    /**
     * Defines a grape type with its identifier, associated items, and lattice requirement.
     * Replaces the standalone GrapeType class, centralizing type management within the registry.
     *
     * @property id Unique identifier for the grape type (e.g., "red").
     * @property fruit Supplier for the grape item (defaults to AIR).
     * @property seeds Supplier for the seed item (defaults to AIR).
     * @property bottle Supplier for the juice bottle item (defaults to AIR).
     * @property isLattice Whether this grape type requires a lattice to grow.
     */
    data class GrapeTypeDefinition(
        private val id: String,
        private var fruit: Supplier<Item> = Supplier { Items.AIR },
        private var seeds: Supplier<Item> = Supplier { Items.AIR },
        private var bottle: Supplier<Item> = Supplier { Items.AIR },
        val isLattice: Boolean = false
    ) : StringRepresentable, Comparable<GrapeTypeDefinition> {
        /** Secondary constructor to mimic original GrapeType behavior with minimal parameters. */
        constructor(id: String, isLattice: Boolean = false) : this(
            id,
            { Items.AIR },
            { Items.AIR },
            { Items.AIR },
            isLattice
        )

        /** Returns the serialized name of this grape type (its ID). */
        override fun getSerializedName(): String = id

        /** Retrieves the associated grape item. */
        fun getFruit(): Item = fruit.get()

        /** Retrieves the associated seed item. */
        fun getSeeds(): Item = seeds.get()

        /** Retrieves the associated juice bottle item. */
        fun getBottle(): Item = bottle.get()

        /**
         * Sets the suppliers for the grape, seed, and juice items.
         *
         * @param fruit Supplier for the grape item.
         * @param seeds Supplier for the seed item.
         * @param bottle Supplier for the juice bottle item.
         */
        fun setItems(fruit: Supplier<Item>, seeds: Supplier<Item>, bottle: Supplier<Item>) {
            this.fruit = fruit
            this.seeds = seeds
            this.bottle = bottle
        }

        /** Generates a hash code based on the ID. */
        override fun hashCode(): Int = Objects.hash(id)

        /** Compares this grape type to another based on ID for block state property compatibility. */
        override fun compareTo(other: GrapeTypeDefinition): Int = id.compareTo(other.id)

        /** Checks equality based on ID. */
        override fun equals(other: Any?): Boolean = other is GrapeTypeDefinition && id == other.id
    }

    /**
     * Block state property for grape types, integrated into the registry.
     * Provides a way to associate grape types with block states using the registered types in GRAPE_TYPES.
     */
    object GrapeProperty : Property<GrapeTypeDefinition>("grape_type", GrapeTypeDefinition::class.java) {
        /** Returns the set of possible grape type values. */
        override fun getPossibleValues(): Set<GrapeTypeDefinition> = GRAPE_TYPES

        /** Gets the serialized name of a grape type for block state representation. */
        override fun getName(value: GrapeTypeDefinition): String = value.serializedName

        /**
         * Parses a string to find a matching grape type, if it exists.
         *
         * @param string The serialized name to parse.
         * @return An Optional containing the matching GrapeTypeDefinition, or empty if not found.
         */
        override fun getValue(string: String): Optional<GrapeTypeDefinition> =
            GRAPE_TYPES.find { it.serializedName == string }?.let { Optional.of(it) } ?: Optional.empty()
    }

    /**
     * Represents a grape variant with properties loaded from JSON or defined programmatically.
     *
     * @property id Unique identifier for the variant (e.g., "red_jungle").
     * @property lineage Tracks the heritage of the variant (e.g., "red", "red_jungle").
     * @property prefix Optional prefix for the variant (e.g., "wild").
     * @property suffix Optional suffix for the variant (e.g., "elite").
     * @property needsLattice Whether the variant requires a lattice to grow.
     */
    data class VariantDefinition(
        val id: String,
        val lineage: String,
        val prefix: String = "",
        val suffix: String = "",
        val needsLattice: Boolean = false
    )

    /** Helper class for parsing JSON variants. */
    private data class JsonVariant(
        val id: String,
        val lineage: String,
        val prefix: String? = null,
        val suffix: String? = null,
        val needsLattice: Boolean? = null
    )

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
        val type: GrapeTypeDefinition,
        val bush: Block? = null,
        val seeds: Item? = null,
        val grape: Item? = null,
        val juice: Item? = null
    )

    // Data Initialization
    /** List of grape variants loaded from JSON or fallback defaults if loading fails. */
    private val variants: List<VariantDefinition> = try {
        val gson = Gson()
        val inputStream = VineryGrapeRegistry::class.java.classLoader.getResourceAsStream("data/$MODID/grape_variants.json")
            ?: throw IllegalStateException("Grape variants JSON not found at data/$MODID/grape_variants.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<JsonVariant>>() {}.type
        gson.fromJson<List<JsonVariant>>(jsonString, listType).map { json ->
            VariantDefinition(
                id = json.id,
                lineage = json.lineage,
                prefix = json.prefix ?: "",
                suffix = json.suffix ?: "",
                needsLattice = json.needsLattice ?: false
            )
        }.also { log.info("Loaded grape variants from: data/$MODID/grape_variants.json") }
    } catch (e: Exception) {
        log.info("Failed to load grape_variants.json: ${e.message}. Using fallback defaults.")
        listOf(
            VariantDefinition("none", "none"),
            VariantDefinition("red", "red"),
            VariantDefinition("white", "white"),
            VariantDefinition("savanna_red", "red_savanna"),
            VariantDefinition("savanna_white", "white_savanna"),
            VariantDefinition("taiga_red", "red_taiga"),
            VariantDefinition("taiga_white", "white_taiga"),
            VariantDefinition("jungle_red", "red_jungle", needsLattice = true),
            VariantDefinition("jungle_white", "white_jungle", needsLattice = true)
        )
    }

    /** Map of grape variants to their associated sets, populated from JSON or fallback. */
    private val grapeSets: MutableMap<VariantDefinition, GrapeSet<Block>> = buildMap<VariantDefinition, GrapeSet<Block>> {
        variants.forEach { variant ->
            val grapeType = GrapeTypeDefinition(variant.id, variant.needsLattice)
            GRAPE_TYPES.add(grapeType)
            if (variant.id == "none") {
                put(variant, GrapeSet(grapeType))
            } else {
                val bushProperties = BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
                val bush = if (variant.needsLattice) registerGrapeVine(getName(variant, ItemType.BUSH), bushProperties, grapeType) else registerGrapeBush(getName(variant, ItemType.BUSH), bushProperties, grapeType)
                val seeds = registerGrapeSeeds(getName(variant, ItemType.SEEDS), GrapeBushSeedItem(bush, Item.Properties(), grapeType, getName(variant, ItemType.SEEDS)))
                val grape = registerGrapes(getName(variant, ItemType.GRAPE), GrapeItem(Item.Properties().food(Foods.SWEET_BERRIES), grapeType, seeds, getName(variant, ItemType.GRAPE)))
                val juice = register(ITEM, ResourceLocation(MODID, getName(variant, ItemType.JUICE)), GrapejuiceBottleItem(Item.Properties().craftRemainder(WINE_BOTTLE.asItem()), getName(variant, ItemType.JUICE)))
                grapeType.setItems({ grape }, { seeds }, { juice })
                put(variant, GrapeSet(grapeType, bush, seeds, grape, juice))
            }
        }
    }.toMutableMap()

    // Utility Functions
    /** Enum for item types to consolidate naming logic. */
    private enum class ItemType { BUSH, SEEDS, GRAPE, JUICE }

    /**
     * Generates a name for a grape-related item based on the variant and item type.
     *
     * @param variant The variant to generate a name for.
     * @param itemType The type of item (bush, seeds, grape, or juice).
     * @return The generated name.
     */
    private fun getName(variant: VariantDefinition, itemType: ItemType): String {
        val base = if (variant.id == "red" || variant.id == "white") variant.id else variant.id.split("_")[0]
        val suffix = if (variant.id == "red" || variant.id == "white") "" else "_${variant.id.split("_")[1]}"
        return when (itemType) {
            ItemType.BUSH -> "${base}_grape_bush$suffix"
            ItemType.SEEDS -> "${base}_grape_seeds$suffix"
            ItemType.GRAPE -> "${base}_grapes$suffix"
            ItemType.JUICE -> if (variant.id == "red" || variant.id == "white") "${base}_grapejuice" else "${variant.id.split("_")[1]}_${base}_grapejuice"
        }
    }

    // Public API Functions
    /**
     * Retrieves the GrapeSet associated with a given GrapeTypeDefinition, if it exists.
     *
     * @param type The GrapeTypeDefinition to look up.
     * @return The corresponding GrapeSet<Block>, or null if not found.
     */
    fun retrieveGrapeSetByType(type: GrapeTypeDefinition): GrapeSet<Block>? =
        grapeSets.entries.find { it.value.type == type }?.value

    /**
     * Retrieves the GrapeSet associated with a given GrapeTypeDefinition, or a default if not found.
     *
     * @param type The GrapeTypeDefinition to look up.
     * @return The corresponding GrapeSet<Block>, or the "none" set if not found.
     */
    fun getGrapeSetOrDefault(type: GrapeTypeDefinition): GrapeSet<Block> =
        retrieveGrapeSetByType(type) ?: grapeSets.entries.find { it.key.id == "none" }!!.value

    //TODO:Will currently break with the fixed grapeSets system which sends the element name to the classes. determine if this should be removed or reworked
    /**
     * Registers a custom grape variant and its associated items in the grape sets.
     *
     * This function creates and registers the grape type, bush, seeds, grape item, and juice item
     * for the given custom variant, then stores them in the [grapeSets] map.
     *
     * @param variant The custom grape variant to register.
     */
    // fun registerCustomVariant(variant: VariantDefinition) {
    // val grapeType = GrapeTypeDefinition(variant.id, variant.needsLattice)
    // GRAPE_TYPES.add(grapeType)
    //
    // val bushProperties = BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
    // val bush = if (variant.needsLattice) {
    // registerGrapeVine(getName(variant, ItemType.BUSH), bushProperties, grapeType)
    // } else {
    // registerGrapeBush(getName(variant, ItemType.BUSH), bushProperties, grapeType)
    // }
    //
    // val seeds = registerGrapeSeeds(
    // getName(variant, ItemType.SEEDS),
    // GrapeBushSeedItem(bush, Item.Properties(), grapeType, getName(variant, ItemType.SEEDS))
    // )
    //
    // val grape = registerGrapes(
    // getName(variant, ItemType.GRAPE),
    // GrapeItem(Item.Properties().food(Foods.SWEET_BERRIES), grapeType, seeds, getName(variant, ItemType.GRAPE))
    // )
    //
    // val juice = register(
    // ITEM,
    // ResourceLocation(MODID, getName(variant, ItemType.JUICE)),
    // GrapejuiceBottleItem(Item.Properties().craftRemainder(WINE_BOTTLE.asItem()), getName(variant, ItemType.JUICE))
    // )
    //
    // grapeType.setItems({ grape }, { seeds }, { juice })
    // grapeSets[variant] = GrapeSet(grapeType, bush, seeds, grape, juice)
    // }

    // Registration Functions
    private fun registerGrapes(name: String, item: Item) = register(ITEM, ResourceLocation(MODID, name), item)
    private fun registerGrapeSeeds(name: String, item: Item) = register(ITEM, ResourceLocation(MODID, name), item)
    private fun registerGrapeBush(name: String, properties: BlockBehaviour.Properties, type: GrapeTypeDefinition) =
        register(BLOCK, ResourceLocation(MODID, name), GrapeBush(properties, type))
    private fun registerGrapeVine(name: String, properties: BlockBehaviour.Properties, type: GrapeTypeDefinition) =
        register(BLOCK, ResourceLocation(MODID, name), GrapeVineBlock(properties, type))
    private fun registerBlock(name: String, block: Block): Block = register(BLOCK, ResourceLocation(MODID, name), block)
    private fun registerBlockItem(name: String, item: BlockItem): BlockItem = register(ITEM, ResourceLocation(MODID, name), item)
}