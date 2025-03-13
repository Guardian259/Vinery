package net.satisfy.vinery.core.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.satisfy.vinery.core.registry.ArmorRegistry;
import net.satisfy.vinery.core.item.WinemakerBootsItem;
import net.satisfy.vinery.core.item.WinemakerChestItem;
import net.satisfy.vinery.core.item.WinemakerHelmetItem;
import net.satisfy.vinery.core.item.WinemakerLegsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * A mixin class modifying the behavior of {@link BoneMealItem} to enhance its functionality
 * when used by a player wearing a full winemaker armor set in a Minecraft mod environment.
 */
@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin {

    /**
     * Injects additional logic into the {@link BoneMealItem#useOn} method to refund bonemeal
     * and damage winemaker armor when the full set bonus is active.
     * @param context The context of the item use, including level, player, and hand.
     * @param cir Callback info containing the return value of the original method.
     */
    @Inject(method = "useOn", at = @At("RETURN"))
    public void useOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(context.getLevel() instanceof ServerLevel)) {
            return;
        }

        ArmorRegistry.checkArmorSet(Objects.requireNonNull(context.getPlayer()));
        if (cir.getReturnValue() != InteractionResult.CONSUME || !ArmorRegistry.setBonusActive) {
            return;
        }

        Player player = context.getPlayer();
        if (player == null) return;

        ItemStack helmet = player.getInventory().getArmor(3);
        ItemStack chestplate = player.getInventory().getArmor(2);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack boots = player.getInventory().getArmor(0);

        if (helmet.getItem() instanceof WinemakerHelmetItem &&
                chestplate.getItem() instanceof WinemakerChestItem &&
                leggings.getItem() instanceof WinemakerLegsItem &&
                boots.getItem() instanceof WinemakerBootsItem) {

            ItemStack heldItem = context.getItemInHand();
            if (!heldItem.isEmpty()) {
                heldItem.grow(1);
            }

            for (int i = 0; i < 4; i++) {
                ItemStack armorPiece = player.getInventory().getArmor(i);
                if (!armorPiece.isEmpty()) {
                    armorPiece.hurtAndBreak(2, player, (p) -> p.broadcastBreakEvent(context.getHand()));
                }
            }
        }
    }
}
