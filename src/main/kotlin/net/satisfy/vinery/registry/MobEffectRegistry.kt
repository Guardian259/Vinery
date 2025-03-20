package net.satisfy.vinery.registry

import net.minecraft.core.Registry.register
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.effect.*

object MobEffectRegistry {
    val ARMOR_EFFECT = effect("armor_effect", ArmorEffect())
    val HEALTH_EFFECT = effect("health_effect", ImprovedHealthEffect())
    val LUCK_EFFECT = effect("luck_effect", LuckEffect())
    val RESISTANCE_EFFECT = effect("resistance_effect", ResistanceEffect())
    val EXPERIENCE_EFFECT = effect("experience_effect", ExpandableEffect(MobEffectCategory.BENEFICIAL, 0x00FF00))
    val IMPROVED_JUMP_BOOST = effect("double_jump", ExpandableEffect(MobEffectCategory.BENEFICIAL, 0x00FF00))
    val PARTY_EFFECT = effect("party_effect", ExpandableEffect(MobEffectCategory.BENEFICIAL, 0xFF0000))
    val TELEPORT = effect("teleport", TeleportEffect())
    val CREEPER_EFFECT = effect("creeper_effect", CreeperEffect())
    val CLIMBING_EFFECT = effect("climbing_effect", ClimbingEffect())
    val FROSTY_ARMOR_EFFECT = effect("frosty_armor", FrostyArmorEffect())
    val JELLIE = effect("jellie", JellieEffect())
    val LAVA_WALKER = effect("lava_walker", LavaWalkerEffect())
    val MAGNET = effect("magnet", MagnetEffect())
    val WATER_WALKER = effect("water_walker", WaterWalkerEffect())

    private fun effect(name: String, effect: MobEffect): MobEffect =
        register(BuiltInRegistries.MOB_EFFECT, ResourceLocation(MODID, name), effect)
}
