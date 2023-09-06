package satisfyu.vinery.item.modifier;

import com.mojang.blaze3d.shaders.Effect;
import net.minecraft.network.chat.MutableComponent;

public class BiomeModifier extends Modifier {
    public BiomeModifier(MutableComponent name, Effect... effects) {
        super(name, effects);
    }
}
