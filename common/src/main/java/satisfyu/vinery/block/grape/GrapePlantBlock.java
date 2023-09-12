package satisfyu.vinery.block.grape;

import satisfyu.vinery.item.grape.GrapeModifier;
import satisfyu.vinery.item.grape.GrapeProperties;
import satisfyu.vinery.item.grape.GrapeType;

import java.util.Optional;

public interface GrapePlantBlock {
    GrapeProperties getGrapeProperties();
    Optional<GrapeModifier> getGrapeModifier();
    GrapeType getType();
}
