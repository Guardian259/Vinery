package net.satisfy.vinery.effect

import net.minecraft.util.Mth
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3

class ClimbingEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0xCC3300) {
    override fun applyEffectTick(entity: LivingEntity, amplifier: Int) {
        if (entity.horizontalCollision) {
            entity.fallDistance = 0.0f

            val velocity = 0.15f

            val motion: Vec3 = entity.getDeltaMovement()

            val motionX: Double = Mth.clamp(motion.x, -velocity.toDouble(), velocity.toDouble())
            var motionY = 0.2
            val motionZ: Double = Mth.clamp(motion.z, -velocity.toDouble(), velocity.toDouble())
            if (entity.isSuppressingSlidingDownLadder()) {
                motionY = 0.0
            }

            entity.setDeltaMovement(motionX, motionY, motionZ)
        }
    }

    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean = true
}