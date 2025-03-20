package net.satisfy.vinery.effect

import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.effect.InstantenousMobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.Vec3

class TeleportEffect : InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0xFF69B4) {
    override fun applyInstantenousEffect(
        source: Entity?,
        attacker: Entity?,
        target: LivingEntity,
        amplifier: Int,
        proximity: Double
    ) {
        teleport(source)
    }

    override fun applyEffectTick(source: LivingEntity, i: Int) {
        teleport(source)
    }

    private fun teleport(source: Entity?) {
        if (source !is Player) return

        val world = source.level()
        val targetVec = source.position()
        val lookVec = source.getLookAngle()
        var target: BlockPos? = null
        var i = 12.0
        while (i >= 2) {
            val v3d = targetVec.add(lookVec.multiply(i, i, i))
            target = BlockPos(Math.round(v3d.x).toInt(), Math.round(v3d.y).toInt(), Math.round(v3d.z).toInt())
            if (!fullBlockAt(world, target) && !fullBlockAt(world, target.above())) {
                break
            } else {
                target = null
            }
            i -= 0.5
        }
        if (target != null) {
            if (!source.level().isClientSide) {
                val teleportVec = Vec3(target.x.toDouble(), target.y.toDouble(), target.z.toDouble())
                source.teleportToWithTicket(teleportVec.x + 0.5, teleportVec.y, teleportVec.z + 0.5)
            }
            source.fallDistance = 0f
            source.playSound(SoundEvents.ENDER_EYE_DEATH, 1f, 1f)
        }
    }

    private fun oldTeleport(player: Player) {
        val lookVec = player.lookAngle
        val teleportPos = player.position().add(lookVec.x * 30, lookVec.y * 30, lookVec.z * 30)
        player.teleportToWithTicket(teleportPos.x, teleportPos.y, teleportPos.z)
    }

    companion object {
        private fun fullBlockAt(world: Level, target: BlockPos): Boolean = Block.isShapeFullBlock(world.getBlockState(target).getCollisionShape(world, target))
    }
}
