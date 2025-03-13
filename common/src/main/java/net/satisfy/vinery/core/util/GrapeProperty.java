package net.satisfy.vinery.core.util;

import net.minecraft.world.level.block.state.properties.Property;
import net.satisfy.vinery.core.registry.GrapeTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * A property class representing grape types in a Minecraft mod, extending {@link Property} for
 * block state management with {@link GrapeType} values.
 */
public class GrapeProperty extends Property<GrapeType> {
    /** The set of all possible grape types, sourced from the registry. */
    private final Set<GrapeType> values;

    /**
     * Constructs a new grape property with the given name.
     * @param name The name of this property.
     */
    protected GrapeProperty(String name) {
        super(name, GrapeType.class);
        this.values = GrapeTypeRegistry.GRAPE_TYPE_TYPES;
    }

    /**
     * Creates a new grape property instance with the specified name.
     * @param name The name of the property.
     * @return A new {@link GrapeProperty} instance.
     */
    public static GrapeProperty create(String name) {
        return new GrapeProperty(name);
    }

    /**
     * Retrieves all possible grape type values for this property.
     * @return An unmodifiable collection of {@link GrapeType} values.
     */
    @Override
    public @NotNull Collection<GrapeType> getPossibleValues() {
        return this.values;
    }

    /**
     * Gets the serialized name of a given grape type.
     * @param grapeType The grape type to name.
     * @return The serialized name from {@link GrapeType#getSerializedName()}.
     */
    @Override
    public @NotNull String getName(GrapeType grapeType) {
        return grapeType.getSerializedName();
    }

    /**
     * Parses a string to find a matching grape type value.
     * @param string The string to parse, typically a serialized name.
     * @return An {@link Optional} containing the matching {@link GrapeType}, or empty if no match.
     */
    @Override
    public @NotNull Optional<GrapeType> getValue(String string) {
        for (GrapeType grapeType : values) {
            if (string.equals(grapeType.getSerializedName()))
                return Optional.of(grapeType);
        }
        return Optional.empty();
    }
}