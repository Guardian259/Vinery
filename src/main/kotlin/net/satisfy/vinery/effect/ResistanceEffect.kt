package net.satisfy.vinery.effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import java.util.*

class ResistanceEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0x56CBFD) {
    init {
        this.addAttributeModifier(
            Attributes.KNOCKBACK_RESISTANCE,
            KNOCKBACK_RESISTANCE_UUID,
            4.0,
            AttributeModifier.Operation.ADDITION
        )
        this.addAttributeModifier(
            Attributes.ARMOR_TOUGHNESS,
            ARMOR_TOUGHNESS_UUID,
            6.0,
            AttributeModifier.Operation.ADDITION
        )
    }

    override fun getAttributeModifierValue(amplifier: Int, modifier: AttributeModifier): Double {
        if (modifier.id == UUID.fromString(KNOCKBACK_RESISTANCE_UUID)) return ((amplifier + 1) * 2.0f).toDouble()
        if (modifier.id == UUID.fromString(ARMOR_TOUGHNESS_UUID)) return ((amplifier + 1) * 2.0f).toDouble()
        return (amplifier + 1).toDouble()
    }

    companion object {
        private const val KNOCKBACK_RESISTANCE_UUID = "8E5D432F-91E5-4C0A-B556-3D4376F25F11"
        private const val ARMOR_TOUGHNESS_UUID = "B5A8D51B-47EC-47FD-9886-7EBDFE81EBA7"
    }
}