package net.satisfy.vinery.util

import net.minecraft.core.Registry.register
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.block.LatticeBlock
import net.satisfy.vinery.block.PaleStemBlock

class VineryObjectRegistry {
    /** Grapevine stem block with wood-like properties. */
    val GRAPEVINE_STEM = register(
        BuiltInRegistries.BLOCK,
        ResourceLocation(MODID, "grapevine_stem"),
        PaleStemBlock(BlockBehaviour.Properties.of().strength(2.0f).randomTicks().sound(SoundType.WOOD).noOcclusion())
    )

    /**
     * Map of lattice types to their registered blocks.
     *
     * Contains lattice blocks for various wood types (e.g., "oak", "spruce"), each registered with properties
     * derived from their corresponding plank blocks. Lattices are lightweight, non-occluding blocks with a strength
     * of 2.0 (breaking) and 3.0 (explosion resistance), using the sound type of their plank source.
     */
    private val LATTICES: Map<String, Block> = buildMap {
        /**
         * List of lattice types, mapping names to their plank block sources for sound properties.
         * Each entry defines a lattice variant registered under "${name}_lattice".
         */
        val latticeTypes = listOf(
            "oak" to Blocks.OAK_PLANKS,
            "spruce" to Blocks.SPRUCE_PLANKS,
            "cherry" to Blocks.CHERRY_PLANKS,
            "birch" to Blocks.BIRCH_PLANKS,
            "dark_oak" to Blocks.DARK_OAK_PLANKS,
            "acacia" to Blocks.ACACIA_PLANKS,
            "bamboo" to Blocks.BAMBOO_PLANKS,
            "jungle" to Blocks.JUNGLE_PLANKS,
            "mangrove" to Blocks.MANGROVE_PLANKS,
            "dark_cherry" to Blocks.MANGROVE_PLANKS // Adjust sound if dark cherry has a unique plank
        )

        latticeTypes.forEach { (name, plankBlock) ->
            put(
                name,
                register(
                    BuiltInRegistries.BLOCK,
                    ResourceLocation(MODID, "${name}_lattice"),
                    LatticeBlock(
                        BlockBehaviour.Properties.of()
                            .strength(2.0f, 3.0f)
                            .sound(plankBlock.getSoundType(plankBlock.defaultBlockState()))
                            .noOcclusion()
                    )
                )
            )
        }
    }

    // Convenience accessors (optional, if you need direct references)
    val OAK_LATTICE get() = LATTICES["oak"]!!
    val SPRUCE_LATTICE get() = LATTICES["spruce"]!!
    val CHERRY_LATTICE get() = LATTICES["cherry"]!!
    val BIRCH_LATTICE get() = LATTICES["birch"]!!
    val DARK_OAK_LATTICE get() = LATTICES["dark_oak"]!!
    val ACACIA_LATTICE get() = LATTICES["acacia"]!!
    val BAMBOO_LATTICE get() = LATTICES["bamboo"]!!
    val JUNGLE_LATTICE get() = LATTICES["jungle"]!!
    val MANGROVE_LATTICE get() = LATTICES["mangrove"]!!
    val DARK_CHERRY_LATTICE get() = LATTICES["dark_cherry"]!!
}