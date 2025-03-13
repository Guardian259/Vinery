package net.satisfy.vinery.core.util;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents a type of grape with associated items and properties in a Minecraft mod context.
 * Implements {@link Comparable} for ordering and {@link StringRepresentable} for serialization.
 */
public class GrapeType implements Comparable<GrapeType>, StringRepresentable {
    /** The unique identifier for this grape type. */
    private final String id;
    /** Indicates if this grape type requires a lattice for growth. */
    private final boolean lattice;
    /** Supplier for the fruit item of this grape type. */
    private Supplier<Item> fruit;
    /** Supplier for the seed item of this grape type. */
    private Supplier<Item> seeds;
    /** Supplier for the bottle item of this grape type. */
    private Supplier<Item> bottle;

    /**
     * Constructs a grape type with the given ID and no lattice requirement.
     * @param id The unique identifier for this grape type.
     */
    public GrapeType(String id) {
        this(id, false);
    }

    /**
     * Constructs a grape type with the given ID and lattice requirement.
     * @param id The unique identifier for this grape type.
     * @param lattice True if this grape type requires a lattice, false otherwise.
     */
    public GrapeType(String id, boolean lattice) {
        this(id,  () -> Items.AIR, () -> Items.AIR, () -> Items.AIR, lattice);
    }

    /**
     * Constructs a grape type with full item suppliers and lattice property.
     * @param id The unique identifier for this grape type.
     * @param fruit Supplier for the fruit item.
     * @param seeds Supplier for the seed item.
     * @param bottle Supplier for the bottle item.
     * @param lattice True if this grape type requires a lattice, false otherwise.
     */
    private GrapeType(String id, Supplier<Item> fruit, Supplier<Item> seeds, Supplier<Item> bottle, boolean lattice) {
        this.id = id;
        this.fruit = fruit;
        this.seeds = seeds;
        this.bottle = bottle;
        this.lattice = lattice;
    }

    /**
     * Returns the serialized name of this grape type.
     * @return The unique identifier ({@link #id}) as a string.
     */
    @Override
    public @NotNull String getSerializedName() {
        return id;
    }

    /**
     * Retrieves the fruit item associated with this grape type.
     * @return The fruit {@link Item} from the supplier.
     */
    public Item getFruit() {
        return this.fruit.get();
    }

    /**
     * Retrieves the seed item associated with this grape type.
     * @return The seed {@link Item} from the supplier.
     */
    public Item getSeeds() {
        return this.seeds.get();
    }

    /**
     * Retrieves the bottle item associated with this grape type.
     * @return The bottle {@link Item} from the supplier.
     */
    public Item getBottle() {
        return bottle.get();
    }

    /**
     * Checks if this grape type requires a lattice for growth.
     * @return True if lattice is required, false otherwise.
     */
    public boolean isLattice() {
        return lattice;
    }

    /**
     * Sets the item suppliers for fruit, seeds, and bottle.
     * @param fruit Supplier for the fruit item.
     * @param seeds Supplier for the seed item.
     * @param bottle Supplier for the bottle item.
     */
    public void setItems(Supplier<Item> fruit, Supplier<Item> seeds, Supplier<Item> bottle) {
        this.fruit = fruit;
        this.seeds = seeds;
        this.bottle = bottle;
    }

    /**
     * Compares this grape type to another. Currently returns 0 (no ordering).
     * @param grapeType The grape type to compare with.
     * @return 0, indicating no specific order.
     */
    @Override
    public int compareTo(@NotNull GrapeType grapeType) {
        return 0;
    }

    /**
     * Computes the hash code based on the grape type’s ID.
     * @return The hash code of the {@link #id}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Checks equality with another object based on the grape type’s ID.
     * @param o The object to compare with.
     * @return True if the object is a {@link GrapeType} with the same {@link #id}, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrapeType grapeType)) return false;
        return Objects.equals(id, grapeType.id);
    }
}