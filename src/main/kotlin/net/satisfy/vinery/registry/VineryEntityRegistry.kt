package net.satisfy.vinery.registry

import net.minecraft.core.BlockPos
import net.minecraft.core.Registry.register
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.block.entity.FermentationBarrelBlockEntity


object VineryEntityRegistry {
    val FERMENTATION_BARREL_ENTITY: BlockEntityType<FermentationBarrelBlockEntity> = register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation(MODID, "fermentation_barrel"), BlockEntityType.Builder.of({ pos: BlockPos?, state: BlockState? -> FermentationBarrelBlockEntity(pos, state) }, VineryObjectRegistry.FERMENTATION_BARREL).build(null))
}