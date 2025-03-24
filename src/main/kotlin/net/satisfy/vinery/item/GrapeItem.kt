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
import net.satisfy.vinery.registry.VineryGrapeRegistry.GrapeTypeDefinition

/**
 * An edible grape item with a chance to drop seeds upon consumption.
 * Implements [PolymerItem] to use a custom model based on sweet berries.
 *
 * @param settings Item properties
 * @param type Grape type definition
 * @param returnItem Item dropped as seeds (e.g., grape seeds)
 * @param grapeModelName Resource name for the custom grape model
 */
class GrapeItem(settings: Properties?, val type: GrapeTypeDefinition, private val returnItem: Item, grapeModelName: String) : Item(settings!!), PolymerItem {

    /** Custom model data for the grape, based on sweet berries. */
    private val grapeModel: PolymerModelData = PolymerResourcePackUtils.requestModel(Items.SWEET_BERRIES, ResourceLocation(MODID, "item/$grapeModelName"))

    /**
     * Handles item consumption. On the server, grants a 20% chance to drop seeds into the player's
     * inventory or as an entity if the inventory is full.
     *
     * @return The resulting stack after consumption (from superclass)
     */
    override fun finishUsingItem(stack: ItemStack, world: Level, entityLiving: LivingEntity): ItemStack {
        if (world.isClientSide || entityLiving !is Player || stack.item !== this) {
            return super.finishUsingItem(stack, world, entityLiving)
        }
        if (world.random.nextFloat() < CHANCE_OF_GETTING_SEEDS) {
            val seeds = ItemStack(returnItem)
            if (!entityLiving.inventory.add(seeds)) {
                entityLiving.drop(seeds, false)
            }
        }
        return super.finishUsingItem(stack, world, entityLiving)
    }

    companion object {
        /** Probability of dropping seeds when consumed (20%). */
        private const val CHANCE_OF_GETTING_SEEDS = 0.2
    }

    /** Returns the base item for Polymer rendering (sweet berries). */
    override fun getPolymerItem(p0: ItemStack?, p1: ServerPlayer?): Item = Items.SWEET_BERRIES

    /** Returns the custom model data value for this grape type. */
    override fun getPolymerCustomModelData(itemStack: ItemStack?, player: ServerPlayer?): Int = grapeModel.value()
}