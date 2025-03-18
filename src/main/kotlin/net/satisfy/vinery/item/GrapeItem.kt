package net.satisfy.vinery.item

import eu.pb4.polymer.core.api.item.PolymerItem
import eu.pb4.polymer.resourcepack.api.PolymerModelData
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.util.GrapeType


class GrapeItem(settings: Properties?, val type: GrapeType, private val returnItem: Item, grapeModelName: String) : Item(settings!!), PolymerItem {

    private val grapeModel: PolymerModelData = PolymerResourcePackUtils.requestModel(Items.SWEET_BERRIES, ResourceLocation(MODID, "item/$grapeModelName"))

    override fun finishUsingItem(stack: ItemStack, world: Level, entityLiving: LivingEntity): ItemStack {
        if (!world.isClientSide() && entityLiving is Player) {
            if (stack.item === this) {
                if (world.getRandom().nextFloat() < CHANCE_OF_GETTING_SEEDS) {
                    val returnStack = ItemStack(returnItem)
                    if (!entityLiving.inventory.add(returnStack)) {
                        entityLiving.drop(returnStack, false)
                    }
                }
            }
        }
        return super.finishUsingItem(stack, world, entityLiving)
    }

    companion object {
        private const val CHANCE_OF_GETTING_SEEDS = 0.2
    }

    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayer?): Item = Items.SWEET_BERRIES

    override fun getPolymerCustomModelData(itemStack: ItemStack?, player: ServerPlayer?): Int = grapeModel.value()

}