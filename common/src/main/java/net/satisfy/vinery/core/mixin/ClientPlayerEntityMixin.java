package net.satisfy.vinery.core.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.satisfy.vinery.core.registry.MobEffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A mixin class modifying {@link LocalPlayer} to enhance jumping mechanics with the Improved Jump Boost effect
 * in a Minecraft mod environment.
 */
@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer {
    /** Tracks the number of mid-air jumps remaining. */
    @Unique
    private int jumpCount = 0;
    /** Indicates if the player jumped in the last tick. */
    @Unique
    private boolean jumpedLastTick = false;

    /**
     * Constructs the mixin instance with client level and game profile.
     * @param clientLevel The client-side level.
     * @param gameProfile The player’s game profile.
     */
    public ClientPlayerEntityMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    /**
     * Injects logic into {@link LocalPlayer#aiStep} to enable multi-jumping with Improved Jump Boost.
     * @param info Callback info for the injection.
     */
    @Inject(method = "aiStep", at = @At("HEAD"))
    private void tickMovement(CallbackInfo info) {
        if(this.hasEffect(MobEffectRegistry.IMPROVED_JUMP_BOOST.get())) {
            LocalPlayer player = (LocalPlayer) (Object) this;
            if (player.onGround() || player.onClimbable()) {
                jumpCount = 1;
            } else if (!jumpedLastTick && jumpCount > 0 && player.getDeltaMovement().y < 0) {
                if (player.input.jumping && !player.getAbilities().flying) {
                    if (canJump(player)) {
                        --jumpCount;
                        player.jumpFromGround();
                    }
                }
            }
            jumpedLastTick = player.input.jumping;
        }
    }

    /**
     * Redirects jump effect check in {@link LocalPlayer#updateAutoJump} to include Improved Jump Boost.
     * @param livingEntity The player entity.
     * @param statusEffect The effect to check.
     * @return True if the player has Jump Boost or Improved Jump Boost.
     */
    @Redirect(method = "updateAutoJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z"))
    public boolean improvedJumpBoost(LocalPlayer livingEntity, MobEffect statusEffect) {
        return livingEntity.hasEffect(MobEffects.JUMP) || livingEntity.hasEffect(MobEffectRegistry.IMPROVED_JUMP_BOOST.get());
    }

    /**
     * Redirects effect instance retrieval in {@link LocalPlayer#updateAutoJump} to prioritize Improved Jump Boost.
     * @param livingEntity The player entity.
     * @param statusEffect The effect to retrieve.
     * @return The {@link MobEffectInstance} for Improved Jump Boost if present, otherwise Jump Boost.
     */
    @Redirect(method = "updateAutoJump", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getEffect(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;"))
    public MobEffectInstance improvedJumpBoostAmplifier(LocalPlayer livingEntity, MobEffect statusEffect) {
        return livingEntity.hasEffect(MobEffectRegistry.IMPROVED_JUMP_BOOST.get()) ?  livingEntity.getEffect(MobEffectRegistry.IMPROVED_JUMP_BOOST.get()) : livingEntity.getEffect(MobEffects.JUMP);
    }

    /**
     * Checks if the player is wearing usable elytra.
     * @param player The player to check.
     * @return True if the chest slot has functional elytra, false otherwise.
     */
    @Unique
    private boolean wearingUsableElytra(LocalPlayer player) {
        ItemStack chestItemStack = player.getItemBySlot(EquipmentSlot.CHEST);
        return chestItemStack.getItem() == Items.ELYTRA && ElytraItem.isFlyEnabled(chestItemStack);
    }

    /**
     * Determines if the player can perform a mid-air jump.
     * @param player The player to evaluate.
     * @return True if jumping is allowed, false if conditions (e.g., elytra, water) prevent it.
     */
    @Unique
    private boolean canJump(LocalPlayer player) {
        return !wearingUsableElytra(player) && !player.isFallFlying() && !player.isPassenger()
                && !player.isInWater() && !player.hasEffect(MobEffects.LEVITATION);
    }
}
