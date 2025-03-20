package net.satisfy.vinery.effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import java.util.*

class ImprovedHealthEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0x56CBFD) {
    init {
        this.addAttributeModifier(Attributes.MAX_HEALTH, MAX_HEALTH_UUID, 3.0, AttributeModifier.Operation.ADDITION)
    }


    override fun getAttributeModifierValue(amplifier: Int, modifier: AttributeModifier): Double {
        if (modifier.id == UUID.fromString(MAX_HEALTH_UUID)) return ((amplifier + 1) * 2.0f).toDouble()
        return (amplifier + 1).toDouble()
    }

    companion object {
        private const val MAX_HEALTH_UUID = "9A8F2C6B-AE75-42E1-A837-3A15A04C6C57"
    }
}