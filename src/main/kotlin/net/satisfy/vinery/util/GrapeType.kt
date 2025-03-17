package net.satisfy.vinery.util

import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import java.util.*
import java.util.function.Supplier

class GrapeType

private constructor(
    private val id: String,

    private var fruit: Supplier<Item>,

    private var seeds: Supplier<Item>,

    private var bottle: Supplier<Item>,

    val isLattice: Boolean
) : Comparable<GrapeType>, StringRepresentable {
    @JvmOverloads
    constructor(id: String, lattice: Boolean = false) : this(
        id,
        Supplier<Item> { Items.AIR },
        Supplier<Item> { Items.AIR },
        Supplier<Item> { Items.AIR },
        lattice
    )

    override fun getSerializedName(): String {
        return id
    }

    fun getFruit(): Item {
        return fruit.get()
    }

    fun getSeeds(): Item {
        return seeds.get()
    }

    fun getBottle(): Item {
        return bottle.get()
    }

    fun setItems(fruit: Supplier<Item>, seeds: Supplier<Item>, bottle: Supplier<Item>) {
        this.fruit = fruit
        this.seeds = seeds
        this.bottle = bottle
    }

    override fun compareTo(other: GrapeType): Int {
        return 0
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GrapeType) return false
        return id == other.id
    }
}