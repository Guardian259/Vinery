package net.satisfy.vinery.effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import kotlin.math.min

class MagnetEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0xB80070) {
    override fun applyEffectTick(entity: LivingEntity, amplifier: Int) {
        if (entity is Player && !entity.isShiftKeyDown()) {
            val entities = entity.getCommandSenderWorld().getEntities(
                entity,
                entity.getBoundingBox().inflate((5 + amplifier).toDouble())
            ) { p: Entity? -> p is ItemEntity }
            for (entityNearby in entities) {
                if (entity.inventory.freeSlot == -1) {
                    val vec3 = entity.getEyePosition().subtract(entityNearby.position())

                    val amp = amplifier + 1

                    entityNearby.setPosRaw(
                        entityNearby.x,
                        entityNearby.y + vec3.y * 0.015 * min(amp.toDouble(), 3.0),
                        entityNearby.z
                    )
                    if (entity.level().isClientSide) {
                        entityNearby.yOld = entityNearby.y
                    }
                    entityNearby.deltaMovement = entityNearby.deltaMovement.scale(0.95)
                        .add(vec3.normalize().yRot(0.2f).scale(0.10 * amp.toDouble()))
                } else {
                    entityNearby.playerTouch(entity)
                }
            }
        }
        super.applyEffectTick(entity, amplifier)
    }

    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean = true
}