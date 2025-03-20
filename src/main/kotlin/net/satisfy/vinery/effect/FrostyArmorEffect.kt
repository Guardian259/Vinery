package net.satisfy.vinery.effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import java.util.*
import kotlin.math.min

class FrostyArmorEffect : MobEffect(MobEffectCategory.NEUTRAL, 0x56CBFD) {
    init {
        this.addAttributeModifier(
            Attributes.MOVEMENT_SPEED,
            MOVEMENT_SPEED_MODIFIER_UUID,
            FROST_MULTIPLIER,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        )
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, DAMAGE_UUID, 4.0, AttributeModifier.Operation.ADDITION)
        this.addAttributeModifier(Attributes.ARMOR, ARMOR_UUID, 6.0, AttributeModifier.Operation.ADDITION)
    }

    override fun getAttributeModifierValue(amplifier: Int, modifier: AttributeModifier): Double {
        if (modifier.id == UUID.fromString(DAMAGE_UUID)) return ((amplifier + 1) * 2.0f).toDouble()
        if (modifier.id == UUID.fromString(ARMOR_UUID)) return ((amplifier + 1) * 4.0f).toDouble()
        if (modifier.id == UUID.fromString(MOVEMENT_SPEED_MODIFIER_UUID)) return FROST_MULTIPLIER
        return (amplifier + 1).toDouble()
    }


    override fun applyEffectTick(living: LivingEntity, amplifier: Int) {
        living.setIsInPowderSnow(true)
        if (amplifier > 0 && living.canFreeze()) {
            living.ticksFrozen =
                min(living.ticksRequiredToFreeze.toDouble(), (living.ticksFrozen + amplifier).toDouble())
                    .toInt()
        }
    }

    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean = true

    companion object {
        private const val ARMOR_UUID = "710D4861-7021-47DE-9F52-62F48D2B61EB"
        private const val DAMAGE_UUID = "CE752B4A-A279-452D-853A-73C26FB4BA46"
        private const val MOVEMENT_SPEED_MODIFIER_UUID = "CE9DBC2A-EE3F-43F5-9DF7-F7F1EE4915A9"

        const val FROST_MULTIPLIER: Double = -0.05
    }
}