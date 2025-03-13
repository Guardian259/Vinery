package net.satisfy.vinery.core.mixin;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.satisfy.vinery.core.registry.MobEffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

/**
 * A mixin class modifying {@link ExperienceOrb} to grant bonus experience points when collected
 * by a player with the Experience Effect in a Minecraft mod environment.
 */
@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {

    /**
     * Injects logic into {@link ExperienceOrb#playerTouch} to apply bonus XP based on the Experience Effect.
     * @param player The player collecting the experience orb.
     * @param ci Callback info for the injection.
     */
    @Inject(method = "playerTouch", at = @At("HEAD"))
    public void onPlayerTouch(Player player, CallbackInfo ci) {
        if (player.hasEffect(MobEffectRegistry.EXPERIENCE_EFFECT.get())) {
            int amplifier = Objects.requireNonNull(player.getEffect(MobEffectRegistry.EXPERIENCE_EFFECT.get())).amplifier;

            int multiplier = amplifier + 1;
            ExperienceOrb self = (ExperienceOrb) (Object) this;
            int bonusXp = (int) (self.getValue() * multiplier * 0.25);

            player.giveExperiencePoints(bonusXp);
        }
    }
}

