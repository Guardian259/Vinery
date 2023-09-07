package satisfyu.vinery.item.modifier;

import com.mojang.blaze3d.shaders.Effect;
import net.minecraft.network.chat.MutableComponent;

public class JuiceModifier extends Modifier {

    private BiomeModifier[] grapes;

    public JuiceModifier(MutableComponent name, BiomeModifier[] biomeModifiers, Effect... effects) {
        super(name, effects);
        this.grapes = biomeModifiers;
    }

}