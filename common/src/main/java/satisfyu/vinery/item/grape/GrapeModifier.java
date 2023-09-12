package satisfyu.vinery.item.grape;

import com.mojang.blaze3d.shaders.Effect;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

import static net.minecraft.network.chat.Component.translatable;

public abstract class GrapeModifier {
    private final MutableComponent name;
    private final List<Effect> effects;

    public GrapeModifier(MutableComponent name, Effect... effects) {
        this.name = name;
        this.effects = List.of(effects);
    }
    public MutableComponent getName() {
        return name;
    }
    public List<Effect> getEffects() {
        return effects;
    }
}