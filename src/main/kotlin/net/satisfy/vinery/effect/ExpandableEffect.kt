package net.satisfy.vinery.effect

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory

class ExpandableEffect(mobEffectCategory: MobEffectCategory?, color: Int) : MobEffect(mobEffectCategory, color) {
    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean = true
}