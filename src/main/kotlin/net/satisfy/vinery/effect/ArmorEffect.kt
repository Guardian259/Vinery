package net.satisfy.vinery.effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import java.util.*

class ArmorEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0x56CBFD) {
    init {
        this.addAttributeModifier(Attributes.ARMOR, ARMOR_UUID, 4.0, AttributeModifier.Operation.ADDITION)
        this.addAttributeModifier(
            Attributes.ARMOR_TOUGHNESS,
            ARMOR_TOUGHNESS_UUID,
            6.0,
            AttributeModifier.Operation.ADDITION
        )
    }

    override fun getAttributeModifierValue(amplifier: Int, modifier: AttributeModifier): Double {
        if (modifier.id == UUID.fromString(ARMOR_UUID)) return ((amplifier + 1) * 4.0f).toDouble()
        if (modifier.id == UUID.fromString(ARMOR_TOUGHNESS_UUID)) return ((amplifier + 1) * 2.0f).toDouble()
        return (amplifier + 1).toDouble()
    }

    companion object {
        private const val ARMOR_UUID = "710D4861-7021-47DE-9F52-62F48D2B61EB"
        private const val ARMOR_TOUGHNESS_UUID = "B5A8D51B-47EC-47FD-9886-7EBDFE81EBA7"
    }
}