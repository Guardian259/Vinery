package net.satisfy.vinery.util

import net.minecraft.core.RegistrySetBuilder
import net.minecraft.world.food.Foods
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items.registerItem
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.satisfy.vinery.block.GrapeBush
import net.satisfy.vinery.item.GrapeBushSeedItem
import net.satisfy.vinery.item.GrapeItem


object Registry {
//    val GRAPE_TYPE_TYPES: MutableSet<GrapeType> = HashSet()
//    val NONE: GrapeType = registerGrapeType("none")
//    val RED: GrapeType = registerGrapeType("red")
//    val WHITE: GrapeType = registerGrapeType("white")
//    val SAVANNA_RED: GrapeType = registerGrapeType("savanna_red")
//    val SAVANNA_WHITE: GrapeType = registerGrapeType("savanna_white")
//    val TAIGA_RED: GrapeType = registerGrapeType("taiga_red")
//    val TAIGA_WHITE: GrapeType = registerGrapeType("taiga_white")
//    val JUNGLE_RED: GrapeType = registerGrapeType("jungle_red", true)
//    val JUNGLE_WHITE: GrapeType = registerGrapeType("jungle_white", true)
//
//
//    val RED_GRAPE_BUSH: RegistrySetBuilder<Block> = registerWithoutItem("red_grape_bush") {
//        GrapeBush(
//            BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH),
//            RED
//        )
//    }
//
//    val RED_GRAPE_SEEDS: RegistrySetBuilder<Item> = registerItem("red_grape_seeds") {
//        GrapeBushSeedItem(
//            RED_GRAPE_BUSH.get(),
//            getSettings { settings -> },
//            RED
//        )
//    }
//
//    val RED_GRAPE: RegistrySupplier<Item> = registerItem("red_grape") {
//        GrapeItem(
//            getSettings { settings -> }.food(Foods.SWEET_BERRIES),
//            RED,
//            RED_GRAPE_SEEDS.get()
//        )
//    }
//
//    fun register() {
//    }
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
//
//    private fun getSettings(param: (Any) -> Unit): Item.Properties = getSettings { }
//
//    @JvmOverloads
//    fun registerGrapeType(id: String?, lattice: Boolean = false): GrapeType {
//        val grapeType = GrapeType(id!!, lattice)
//        GRAPE_TYPE_TYPES.add(grapeType)
//        return grapeType
//    }
}