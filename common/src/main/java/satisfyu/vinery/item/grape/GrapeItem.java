package satisfyu.vinery.item.grape;


import com.mojang.blaze3d.shaders.Effect;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;

public class GrapeItem extends Item {
    private static final double CHANCE_OF_GETTING_SEEDS = 0.2;
    
    private GrapeType type;
    private GrapeProperties properties;
    private Optional<GrapeModifier> modifier;
    
    public GrapeItem(Properties settings, GrapeType type) {
        super(settings);
        this.type = type;
        this.modifier = Optional.empty();
    }
    
    public GrapeItem(Properties settings, GrapeType type, GrapeProperties properties) {
        this(settings, type);
        this.properties = properties;
    }
    
    public GrapeItem(Properties settings, GrapeType type, GrapeProperties properties, GrapeModifier modifier) {
        this(settings, type, properties);
        this.modifier = Optional.of(modifier);
    }
    
    public GrapeProperties getProperties() {
        return properties;
    }
    
    public Optional<GrapeModifier> getModifier() {
        return modifier;
    }
    
    public GrapeType getType() {
        return type;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("item.vinery.grape.tooltip." + getDescriptionId()).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
        if (!world.isClientSide() && entityLiving instanceof Player player) {
            if (stack.getItem() == this) {
                if (world.getRandom().nextFloat() < CHANCE_OF_GETTING_SEEDS) {
                    ItemStack returnStack = new ItemStack(seedItem);
                    if (!player.getInventory().add(returnStack)) {
                        player.drop(returnStack, false);
                    }
                }
            }
        }
        return super.finishUsingItem(stack, world, entityLiving);
    }
    

}
