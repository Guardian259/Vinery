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
import net.satisfy.vinery.util.GrapeType

class GrapeBushSeedItem(block: Block?, settings: Properties?, val type: GrapeType, seedsModelName: String) : ItemNameBlockItem(block!!, settings!!), PolymerItem {

    private val seedsModel: PolymerModelData = PolymerResourcePackUtils.requestModel(Items.SWEET_BERRIES, ResourceLocation(MODID, "item/$seedsModelName"))

    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayer?): Item = Items.WHEAT_SEEDS

    override fun getPolymerCustomModelData(itemStack: ItemStack?, player: ServerPlayer?): Int = seedsModel.value()

}