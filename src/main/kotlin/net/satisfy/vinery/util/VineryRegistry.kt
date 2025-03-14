package net.satisfy.vinery.util

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.core.Registry.register
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.food.Foods
import net.minecraft.world.item.*
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.Vinery.Companion.log
import net.satisfy.vinery.block.GrapeBush
import net.satisfy.vinery.item.GrapeBushSeedItem
import net.satisfy.vinery.item.GrapeItem
import java.util.*


/**
 * Singleton object responsible for registering and managing types, items, and blocks.
 */
object VineryRegistry {
    val GRAPE_TYPE_TYPES: MutableSet<GrapeType> = HashSet()
    val NONE: GrapeType = registerGrapeType("none")
    val RED: GrapeType = registerGrapeType("red")
    val WHITE: GrapeType = registerGrapeType("white")
    val SAVANNA_RED: GrapeType = registerGrapeType("savanna_red")
    val SAVANNA_WHITE: GrapeType = registerGrapeType("savanna_white")
    val TAIGA_RED: GrapeType = registerGrapeType("taiga_red")
    val TAIGA_WHITE: GrapeType = registerGrapeType("taiga_white")
    val JUNGLE_RED: GrapeType = registerGrapeType("jungle_red", true)
    val JUNGLE_WHITE: GrapeType = registerGrapeType("jungle_white", true)

    /**
     * Map of grape types to their associated bush, seed, and fruit components.
     */
    private val grapeComponents: MutableMap<GrapeType, GrapeComponents> = HashMap()

    private val VINERY_ITEM_GROUP_KEY: ResourceKey<CreativeModeTab> = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation(MODID, "vinery"))
    //TODO: Fix Creative Tab Generation
//    private val VINERY_ITEM_GROUP: CreativeModeTab = FabricItemGroup.builder()
//        .icon { ItemStack(Items.GLASS_BOTTLE) }
//        .title(Component.translatable("vinery"))
//        .build()

    private val VINERY_ITEM_GROUP: CreativeModeTab = register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        VINERY_ITEM_GROUP_KEY,
        FabricItemGroup.builder()
            .icon { ItemStack(Items.GLASS_BOTTLE) }
            .title(Component.translatable("itemgroup.vinery"))
            .build()
    )

    /**
     * Registers a grape type with the specified ID and no lattice requirement.
     *
     * @param id The unique identifier for the grape type.
     * @return The registered [GrapeType].
     */
    private fun registerGrapeType(id: String?): GrapeType = registerGrapeType(id, false)

    /**
     * Registers a grape type with the specified ID and lattice requirement.
     *
     * @param id The unique identifier for the grape type.
     * @param lattice Whether the grape type requires lattice support.
     * @return The registered [GrapeType].
     */
    private fun registerGrapeType(id: String?, lattice: Boolean): GrapeType {
        val grapeType = GrapeType(id!!, lattice)
        GRAPE_TYPE_TYPES.add(grapeType)
        return grapeType
    }

    /**
     * Initializes the registry by registering all grape types in [GRAPE_TYPE_TYPES] except [NONE].
     * Logs the registration process for each grape type.
     */
    init { GRAPE_TYPE_TYPES.forEach { grapeType -> if(grapeType != NONE) {
        log.info("Vinery Registering ${grapeType.getSerializedName()} berries, seeds, and bushes...")
        registerGrape(grapeType)
        log.info("Vinery Registering ${grapeType.getSerializedName()} berries, seeds, and bushes, Success!!!")
        //TODO: Fix Creative Tab Generation
//        log.info("Vinery Registering Creative Mode Tab...")
//        register(BuiltInRegistries.CREATIVE_MODE_TAB, VINERY_ITEM_GROUP_KEY, VINERY_ITEM_GROUP)
//        log.info("Vinery Registering Creative Mode Tab, Success!!!")
//        modifyEntriesEvent(VINERY_ITEM_GROUP_KEY).register(addItemsToTabGroup(VINERY_ITEM_GROUP))
    } } }

    /**
     * Registers an item in the specified registry with the given resource location.
     *
     * @param registry The registry to add the item to (e.g., [BuiltInRegistries.ITEM]).
     * @param location The [ResourceLocation] identifying the item.
     * @param item The [Item] to register.
     * @return The registered [Item].
     */
    private fun registerItem(item: Item, name: String?): Item = register(BuiltInRegistries.ITEM, ResourceLocation(MODID, name), item)

    /**
     * Registers a block in the specified registry with the given resource location.
     *
     * @param registry The registry to add the block to (e.g., [BuiltInRegistries.BLOCK]).
     * @param location The [ResourceLocation] identifying the block.
     * @param block The [Block] to register.
     * @return The registered [Block].
     */
    private fun registerBlock(block: Block, name: String?): Block = register(BuiltInRegistries.BLOCK, ResourceLocation(MODID, name), block)

    /**
     * Registers a grape type by creating and registering its associated bush, seeds, and fruit items.
     * Stores the components in [grapeComponents] with names derived from the grape type’s serialized
     * name in lowercase. The bush copies properties from sweet berry bushes, seeds are linked to
     * the bush, and the fruit uses sweet berry food properties.
     *
     * @param type The [GrapeType] to register, typically from [GRAPE_TYPE_TYPES].
     */
    private fun registerGrape(type: GrapeType) {
        val bush = registerBlock(GrapeBush(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH), type), "${type.serializedName.lowercase(Locale.getDefault())}_grape_bush")
        val seed = registerItem(GrapeBushSeedItem(bush, Item.Properties(), type), "${type.serializedName.lowercase(Locale.getDefault())}_grape_seeds")
        val fruit = registerItem(GrapeItem(Item.Properties().food(Foods.SWEET_BERRIES), type, seed), "${type.serializedName.lowercase(Locale.getDefault())}_grape")
        grapeComponents[type] = GrapeComponents(bush, seed, fruit)
    }

    /**
     * Adds grape-related items to a Fabric item group in a specific order.
     * Prepends all fruit items first, followed by all seed items, then all bush items
     * from the grapeComponents map to the provided entries.
     *
     * @param entries The FabricItemGroupEntries collection to add items to
     */
    private fun addItemsToTabGroup(entries: CreativeModeTab) {
        grapeComponents.entries.forEach { entry -> entries.displayItems.add(entry.value.fruit.asItem().defaultInstance) }
        grapeComponents.entries.forEach { entry -> entries.displayItems.add(entry.value.seed.asItem().defaultInstance) }
        grapeComponents.entries.forEach { entry -> entries.displayItems.add(entry.value.bush.asItem().defaultInstance) }
    }
}

/**
 * A data class representing the components associated with a grape type in the Vinery mod.
 * Holds references to the grape bush block, seed item, and fruit item for a specific [GrapeType].
 *
 * @property bush The [Block] representing the grape bush.
 * @property seed The [Item] representing the grape seeds, linked to the bush.
 * @property fruit The [Item] representing the grape fruit, tied to the seed and bush.
 */
data class GrapeComponents(val bush: Block, val seed: Item, val fruit: Item)