package net.satisfy.vinery.util

import net.minecraft.core.Registry.register
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.food.Foods
import net.minecraft.world.item.Item
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

    //TODO:Finish Registration
//
//    fun addGrapeAttributes() {
//        RED.setItems(ObjectRegistry.RED_GRAPE, ObjectRegistry.RED_GRAPE_SEEDS, ObjectRegistry.RED_GRAPEJUICE)
//        WHITE.setItems(ObjectRegistry.WHITE_GRAPE, ObjectRegistry.WHITE_GRAPE_SEEDS, ObjectRegistry.WHITE_GRAPEJUICE)
//        SAVANNA_RED.setItems(
//            ObjectRegistry.SAVANNA_RED_GRAPE,
//            ObjectRegistry.SAVANNA_RED_GRAPE_SEEDS,
//            ObjectRegistry.RED_SAVANNA_GRAPEJUICE
//        )
//        SAVANNA_WHITE.setItems(
//            ObjectRegistry.SAVANNA_WHITE_GRAPE,
//            ObjectRegistry.SAVANNA_WHITE_GRAPE_SEEDS,
//            ObjectRegistry.WHITE_SAVANNA_GRAPEJUICE
//        )
//        TAIGA_RED.setItems(
//            ObjectRegistry.TAIGA_RED_GRAPE,
//            ObjectRegistry.TAIGA_RED_GRAPE_SEEDS,
//            ObjectRegistry.RED_TAIGA_GRAPEJUICE
//        )
//        TAIGA_WHITE.setItems(
//            ObjectRegistry.TAIGA_WHITE_GRAPE,
//            ObjectRegistry.TAIGA_WHITE_GRAPE_SEEDS,
//            ObjectRegistry.WHITE_TAIGA_GRAPEJUICE
//        )
//        JUNGLE_RED.setItems(
//            ObjectRegistry.JUNGLE_RED_GRAPE,
//            ObjectRegistry.JUNGLE_RED_GRAPE_SEEDS,
//            ObjectRegistry.RED_JUNGLE_GRAPEJUICE
//        )
//        JUNGLE_WHITE.setItems(
//            ObjectRegistry.JUNGLE_WHITE_GRAPE,
//            ObjectRegistry.JUNGLE_WHITE_GRAPE_SEEDS,
//            ObjectRegistry.WHITE_JUNGLE_GRAPEJUICE
//        )
//    }

    /**
     * Initializes the registry by registering all grape types in [GRAPE_TYPE_TYPES] except [NONE].
     * Logs the registration process for each grape type.
     */
    init { GRAPE_TYPE_TYPES.forEach { grapeType -> if(grapeType != NONE) {
        log.info("Vinery Registering ${grapeType.getSerializedName()} berries, seeds, and bushes...")
        registerGrape(grapeType)
        log.info("Vinery Registering ${grapeType.getSerializedName()} berries, seeds, and bushes, Success!!!")
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
     *
     * @param type The [GrapeType] to register, typically from [GRAPE_TYPE_TYPES].
     */
    private fun registerGrape(type: GrapeType) {
        val grapeBush = registerBlock(GrapeBush(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH), type), "${type.serializedName.lowercase(Locale.getDefault())}_grape_bush")
        val grapeSeeds = registerItem(GrapeBushSeedItem(grapeBush, Item.Properties(), type), "${type.serializedName.lowercase(Locale.getDefault())}_grape_seeds")
        val grape = registerItem(GrapeItem(Item.Properties().food(Foods.SWEET_BERRIES), type, grapeSeeds), "${type.serializedName.lowercase(Locale.getDefault())}_grape")
    }
}