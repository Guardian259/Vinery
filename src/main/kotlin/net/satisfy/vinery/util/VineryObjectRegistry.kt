package net.satisfy.vinery.util

import net.minecraft.core.Registry.register
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.satisfy.vinery.Vinery.Companion.MODID
import net.satisfy.vinery.block.LatticeBlock
import net.satisfy.vinery.block.PaleStemBlock
import net.satisfy.vinery.item.DrinkBlockItem
import java.util.function.Supplier


class VineryObjectRegistry {
    /** Grapevine stem block with wood-like properties. */
    val GRAPEVINE_STEM = register(
        BuiltInRegistries.BLOCK,
        ResourceLocation(MODID, "grapevine_stem"),
        PaleStemBlock(BlockBehaviour.Properties.of().strength(2.0f).randomTicks().sound(SoundType.WOOD).noOcclusion())
    )

    //TODO: THESE MAY BE REMOVED

    val CHORUS_WINE: String = "chorus_wine"
    val CHERRY_WINE: String = "cherry_wine"
    val MAGNETIC_WINE: String = "magnetic_wine"
    val JO_SPECIAL_MIXTURE: String = "jo_special_mixture"
    val CRISTEL_WINE: String = "cristel_wine"
    val GLOWING_WINE: String = "glowing_wine"
    val CREEPERS_CRUSH: String = "creepers_crush"
    val MEAD: String = "mead"
    val RED_WINE: String = "red_wine"
    val JELLIE_WINE: String = "jellie_wine"
    val STAL_WINE: String = "stal_wine"
    val NOIR_WINE: String = "noir_wine"
    val BOLVAR_WINE: String = "bolvar_wine"
    val SOLARIS_WINE: String = "solaris_wine"
    val EISWEIN: String = "eiswein"
    val CHENET_WINE: String = "chenet_wine"
    val KELP_CIDER: String = "kelp_cider"
    val AEGIS_WINE: String = "aegis_wine"
    val CLARK_WINE: String = "clark_wine"
    val MELLOHI_WINE: String = "mellohi_wine"
    val STRAD_WINE: String = "strad_wine"
    val APPLE_CIDER: String = "apple_cider"
    val APPLE_WINE: String = "apple_wine"
    val LILITU_WINE: String = "lilitu_wine"
    val BOTTLE_MOJANG_NOIR: String = "bottle_mojang_noir"
    val VILLAGERS_FRIGHT: String = "villagers_fright"


//    val APPLE_CIDER_ITEM: RegistrySupplier<Item> =
//        registerWineItem("apple_cider", APPLE_CIDER, { createWineSettings({ MobEffects.DAMAGE_BOOST }, 1600, 0) }, true)
//
//    val APPLE_WINE_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "apple_wine",
//        APPLE_WINE,
//        { createWineSettings({ MobEffects.DAMAGE_RESISTANCE }, 1600, 0) },
//        true
//    )
//
//    val MEAD_ITEM: RegistrySupplier<Item> =
//        registerWineItem("mead", MEAD, { createWineSettings({ MobEffects.DIG_SPEED }, 1600, 0) }, true)
//
//    val GLOWING_WINE_ITEM: RegistrySupplier<Item> =
//        registerWineItem("glowing_wine", GLOWING_WINE, { createWineSettings({ MobEffects.GLOWING }, 1600, 0) }, true)
//
//    val SOLARIS_WINE_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "solaris_wine",
//        SOLARIS_WINE,
//        { createWineSettings({ MobEffects.HEALTH_BOOST }, 1600, 0) },
//        true
//    )
//
//    val KELP_CIDER_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "kelp_cider",
//        KELP_CIDER,
//        { createWineSettings({ MobEffectRegistry.WATER_WALKER.get() }, 1600, 0) },
//        true
//    )
//
//    val EISWEIN_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "eiswein",
//        EISWEIN,
//        { createWineSettings({ MobEffectRegistry.FROSTY_ARMOR_EFFECT.get() }, 1600, 0) },
//        true
//    )
//
//    val AEGIS_WINE_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "aegis_wine",
//        AEGIS_WINE,
//        { createWineSettings({ MobEffectRegistry.ARMOR_EFFECT.get() }, 1600, 0) },
//        true
//    )
//
//    val VILLAGERS_FRIGHT_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "villagers_fright",
//        VILLAGERS_FRIGHT,
//        { createWineSettings({ MobEffects.BAD_OMEN }, 1600, 0) },
//        true
//    )
//
//    val CLARK_WINE_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "clark_wine",
//        CLARK_WINE,
//        { createWineSettings({ MobEffectRegistry.IMPROVED_JUMP_BOOST.get() }, 1600, 0) },
//        true
//    )
//
//    val JELLIE_WINE_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "jellie_wine",
//        JELLIE_WINE,
//        { createWineSettings({ MobEffectRegistry.JELLIE.get() }, 1600, 0) },
//        true
//    )
//
//    val NOIR_WINE_ITEM: RegistrySupplier<Item> =
//        registerWineItem("noir_wine", NOIR_WINE, { createWineSettings({ MobEffects.JUMP }, 1600, 0) }, true)
//
//    val drinkBlockSettings = createWineSettings({ MobEffects.SLOW_FALLING }, 1600, 0) };
//    val RED_WINE_ITEM = register(BuiltInRegistries.ITEM, ResourceLocation(MODID, "grapevine_stem"), DrinkBlockItem(
//        wineBlock.get(),
//        drinkBlockSettings.get().properties,
//        name,
//        drinkBlockSettings.get().baseDuration,
//        true
//    ))
//    val RED_WINE_ITEM: Item =
//        registerWineItem("red_wine", RED_WINE, { createWineSettings({ MobEffects.SLOW_FALLING }, 1600, 0) }, true)
//
//    private fun registerWineItem(
//        name: String,
//        wineBlock: Supplier<Block>,
//        wineSettings: Supplier<WineSettings>,
//        scaleDurationWithAge: Boolean
//    ): RegistrySupplier<Item> {
//        return registerItem(name) {
//            DrinkBlockItem(
//                wineBlock.get(),
//                wineSettings.get().properties,
//                name,
//                wineSettings.get().baseDuration,
//                scaleDurationWithAge.toInt()
//            )
//        }
//    }

    private fun createWineSettings(effect: Supplier<MobEffect>, duration: Int, strength: Int) = WineSettings(effect, duration, strength)

//    val STRAD_WINE_ITEM: RegistrySupplier<Item> =
//        registerWineItem("strad_wine", STRAD_WINE, { createWineSettings({ MobEffects.NIGHT_VISION }, 1600, 0) }, true)
//
//    val CHERRY_WINE_ITEM: RegistrySupplier<Item> =
//        registerWineItem("cherry_wine", CHERRY_WINE, { createWineSettings({ MobEffects.INVISIBILITY }, 1600, 0) }, true)
//
//    val CRISTEL_WINE_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "cristel_wine",
//        CRISTEL_WINE,
//        { createWineSettings({ MobEffects.WATER_BREATHING }, 1600, 0) },
//        true
//    )
//
//    val LILITU_WINE_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "lilitu_wine",
//        LILITU_WINE,
//        { createWineSettings({ MobEffectRegistry.PARTY_EFFECT.get() }, 1600, 0) },
//        true
//    )
//
//    val JO_SPECIAL_MIXTURE_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "jo_special_mixture",
//        JO_SPECIAL_MIXTURE,
//        { createWineSettings({ MobEffectRegistry.CLIMBING_EFFECT.get() }, 1600, 0) },
//        true
//    )
//
//    val BOLVAR_WINE_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "bolvar_wine",
//        BOLVAR_WINE,
//        { createWineSettings({ MobEffectRegistry.LAVA_WALKER.get() }, 1600, 0) },
//        true
//    )
//
//    val MAGNETIC_WINE_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "magnetic_wine",
//        MAGNETIC_WINE,
//        { createWineSettings({ MobEffectRegistry.MAGNET.get() }, 1600, 0) },
//        true
//    )
//
//    val STAL_WINE_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "stal_wine",
//        STAL_WINE,
//        { createWineSettings({ MobEffectRegistry.HEALTH_EFFECT.get() }, 1600, 0) },
//        true
//    )
//
//    val CHENET_WINE_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "chenet_wine",
//        CHENET_WINE,
//        { createWineSettings({ MobEffectRegistry.CLIMBING_EFFECT.get() }, 1600, 0) },
//        true
//    )
//
//    val BOTTLE_MOJANG_NOIR_ITEM: RegistrySupplier<Item> = registerWineItem(
//        "bottle_mojang_noir",
//        BOTTLE_MOJANG_NOIR,
//        { createWineSettings({ MobEffectRegistry.EXPERIENCE_EFFECT.get() }, 1600, 0) },
//        true
//    )
//
//    val CHORUS_WINE_ITEM: RegistrySupplier<Item> =
//        registerFixedDurationWineItem("chorus_wine", CHORUS_WINE, 10, { MobEffectRegistry.TELEPORT.get() }, 0)
//
//    val CREEPERS_CRUSH_ITEM: RegistrySupplier<Item> = registerFixedDurationWineItem(
//        "creepers_crush",
//        CREEPERS_CRUSH,
//        100,
//        { MobEffectRegistry.CREEPER_EFFECT.get() },
//        0
//    )
//
//    val MELLOHI_WINE_ITEM: RegistrySupplier<Item> =
//        registerFixedDurationWineItem("mellohi_wine", MELLOHI_WINE, 0, { MobEffects.HEAL }, 0)

    //TODO: THESE MAY BE REMOVED

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