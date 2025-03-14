package net.satisfy.vinery.item

import eu.pb4.polymer.core.api.item.PolymerItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class VineryItem(properties: Properties?)  : Item(properties), PolymerItem {
    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayer?): Item = Items.GLASS_BOTTLE
}
