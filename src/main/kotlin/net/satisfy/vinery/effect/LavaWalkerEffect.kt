package net.satisfy.vinery.effect

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.FluidTags
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import kotlin.math.max

class LavaWalkerEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0xCC3300) {
    override fun applyEffectTick(pLivingEntity: LivingEntity, pAmplifier: Int) {
        if (!(pLivingEntity is Player && pLivingEntity.isSpectator())) {
            val pos = pLivingEntity.position()
            val movement = pLivingEntity.deltaMovement
            val futurePos = pos.add(movement)
            val onPos = pLivingEntity.onPos
            val futureBlockPos = BlockPos(futurePos.x.toInt(), futurePos.y.toInt(), futurePos.z.toInt())
            if (pLivingEntity.isInLava) {
                pLivingEntity.deltaMovement = movement.add(0.0, 0.1, 0.0)
            } else if (pLivingEntity.level().getFluidState(onPos).`is`(FluidTags.LAVA)) {
                if (pLivingEntity.level() is ServerLevel) {
                    (pLivingEntity.level() as ServerLevel).sendParticles(
                        ParticleTypes.LAVA,
                        pos.x(),
                        pos.y() + 0.1,
                        pos.z(),
                        10,
                        0.2,
                        0.1,
                        0.2,
                        1.5
                    )
                }
                pLivingEntity.setDeltaMovement(movement.x(), max(movement.y(), 0.0), movement.z())
                pLivingEntity.setOnGround(true)
            } else if (pLivingEntity.level().getFluidState(futureBlockPos)
                    .`is`(FluidTags.LAVA) && movement.y() > -0.8
            ) {
                if (pLivingEntity.level() is ServerLevel) {
                    (pLivingEntity.level() as ServerLevel).sendParticles<SimpleParticleType>(
                        ParticleTypes.LAVA,
                        pos.x(),
                        pos.y() + 0.1,
                        pos.z(),
                        10,
                        0.2,
                        0.1,
                        0.2,
                        1.5
                    )
                }
                pLivingEntity.setDeltaMovement(movement.x(), max(movement.y(), movement.y() * 0.5), movement.z())
            }
            super.applyEffectTick(pLivingEntity, pAmplifier)
        }
    }

    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean = true
}