package net.satisfy.vinery.effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeMap

class JellieEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0x98D982) {
    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean {
        val i = 50 shr amplifier
        return if (i > 0) {
            duration % i == 0
        } else {
            true
        }
    }

    override fun applyEffectTick(entity: LivingEntity, amplifier: Int) {
        if (entity.health < entity.maxHealth) {
            entity.heal(1.0f)
        }
    }

    override fun removeAttributeModifiers(entity: LivingEntity, attributes: AttributeMap, amplifier: Int) {
        entity.absorptionAmount = entity.absorptionAmount - (4 * (amplifier + 1)).toFloat()
        super.removeAttributeModifiers(entity, attributes, amplifier)
    }

    override fun addAttributeModifiers(entity: LivingEntity, attributes: AttributeMap, amplifier: Int) {
        entity.absorptionAmount = entity.absorptionAmount + (4 * (amplifier + 1)).toFloat()
        super.addAttributeModifiers(entity, attributes, amplifier)
    }
}
