package net.satisfy.vinery.util

import net.minecraft.world.level.block.state.properties.Property
import java.util.*


class GrapeProperty protected constructor(name: String?) : Property<GrapeType>(name, GrapeType::class.java) {
    private val values: Set<GrapeType> = VineryRegistry.GRAPE_TYPE_TYPES

    override fun getPossibleValues() = this.values

    override fun getName(grapeType: GrapeType): String {
        return grapeType.serializedName
    }

    override fun getValue(string: String): Optional<GrapeType> {
        for (grapeType in values) {
            if (string == grapeType.serializedName) return Optional.of(grapeType)
        }
        return Optional.empty()
    }

    companion object {
        fun create(name: String?) = GrapeProperty(name)
    }
}