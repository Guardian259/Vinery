package net.satisfy.vinery.item

import eu.pb4.polymer.core.api.item.PolymerItem
import eu.pb4.polymer.resourcepack.api.PolymerModelData
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.satisfy.vinery.Vinery
import net.satisfy.vinery.Vinery.Companion.MODID

class VineryItem(properties: Properties?, itemModelName: String)  : Item(properties!!), PolymerItem {

    private val itemModel: PolymerModelData = PolymerResourcePackUtils.requestModel(Items.GLASS_BOTTLE, ResourceLocation(MODID, "item/$itemModelName"))

    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayer?): Item = Items.GLASS_BOTTLE

    override fun getPolymerCustomModelData(itemStack: ItemStack?, player: ServerPlayer?): Int = itemModel.value()
}
