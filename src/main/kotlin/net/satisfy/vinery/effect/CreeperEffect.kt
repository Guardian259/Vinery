package net.satisfy.vinery.effect

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.InstantenousMobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level

class CreeperEffect : InstantenousMobEffect(MobEffectCategory.HARMFUL, 0xFF0000) {
    override fun applyInstantenousEffect(
        source: Entity?,
        attacker: Entity?,
        target: LivingEntity,
        amplifier: Int,
        proximity: Double
    ) {
        explode(source, amplifier)
    }

    override fun applyEffectTick(source: LivingEntity, amplifier: Int) {
        explode(source, amplifier)
    }

    private fun explode(source: Entity?, amplifier: Int) {
        if (source is ServerPlayer && source.gameMode.gameModeForPlayer != GameType.CREATIVE) {
            val world = source.getCommandSenderWorld()
            val x = source.getX()
            val y = source.getY()
            val z = source.getZ()
            world.explode(null, x, y, z, amplifier.toFloat(), Level.ExplosionInteraction.TNT)

            source.hurt(source.level().damageSources().explosion(null), 50.0f)
        }
    }
}
