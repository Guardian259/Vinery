package net.satisfy.vinery.item

import eu.pb4.polymer.core.api.item.PolymerItem
import eu.pb4.polymer.resourcepack.api.PolymerModelData
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.registry.VineryGrapeRegistry.GrapeTypeDefinition

/**
 * A seed item for planting grape bushes, tied to a specific grape type.
 * Extends [ItemNameBlockItem] for block placement and [PolymerItem] for custom seed visuals.
 *
 * @param block The block this seed plants
 * @param settings Item properties
 * @param type The grape type this seed represents
 * @param seedsModelName Resource name for the custom seed model
 */
class GrapeBushSeedItem(block: Block?, settings: Properties?, val type: GrapeTypeDefinition, seedsModelName: String) :
    ItemNameBlockItem(block!!, settings!!), PolymerItem {

    /** Custom model data for the seeds, based on wheat seeds. */
    private val seedsModel: PolymerModelData = PolymerResourcePackUtils.requestModel(Items.WHEAT_SEEDS, ResourceLocation(MODID, "item/$seedsModelName"))

    /** Returns the base item for Polymer rendering. */
    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayer?): Item = Items.WHEAT_SEEDS

    /** Returns the custom model data value for this seed type. */
    override fun getPolymerCustomModelData(itemStack: ItemStack?, player: ServerPlayer?): Int = seedsModel.value()
}