package net.satisfy.vinery.effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import java.util.*

class LuckEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0x56CBFD) {
    init {
        this.addAttributeModifier(Attributes.LUCK, LUCK_UUID, 4.0, AttributeModifier.Operation.ADDITION)
    }


    override fun getAttributeModifierValue(amplifier: Int, modifier: AttributeModifier): Double {
        if (modifier.id == UUID.fromString(LUCK_UUID)) return ((amplifier + 1) * 2.0f).toDouble()
        return (amplifier + 1).toDouble()
    }

    companion object {
        private const val LUCK_UUID = "7C8A6C79-4A74-4591-8F91-71F21E0A7EAD"
    }
}