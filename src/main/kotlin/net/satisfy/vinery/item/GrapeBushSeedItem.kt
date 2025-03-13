package net.satisfy.vinery.item

import eu.pb4.polymer.core.api.item.PolymerItem
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.satisfy.vinery.util.GrapeType

class GrapeBushSeedItem(block: Block?, settings: Properties?, val type: GrapeType) : ItemNameBlockItem(block, settings), PolymerItem {
    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayer?): Item = Items.WHEAT_SEEDS
}