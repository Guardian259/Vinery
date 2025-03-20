package net.satisfy.vinery.registry

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry.register
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.client.gui.handler.FermentationBarrelGuiHandler

object VineryScreenhandlerTypeRegistry {

    val FERMENTATION_BARREL_GUI_HANDLER: MenuType<FermentationBarrelGuiHandler> = register(
        BuiltInRegistries.MENU,
        ResourceLocation(MODID, "fermentation_barrel_gui_handler"),
        MenuType({ syncId: Int, playerInventory: Inventory -> FermentationBarrelGuiHandler(syncId, playerInventory) }, FeatureFlags.VANILLA_SET)
    )

}