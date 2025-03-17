package net.satisfy.vinery

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils
import net.darktree.simpleconfig.SimpleConfig
import net.fabricmc.api.ModInitializer
import net.satisfy.vinery.util.VineryGrapeRegistry
import net.satisfy.vinery.util.VineryObjectRegistry
//import net.satisfy.vinery.util.VineryRegistry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


class Vinery : ModInitializer {

    override fun onInitialize() {
        modInit()
    }

    private fun modInit() {
        log.info("Vinery Initializing...")
        PolymerResourcePackUtils.addModAssets(MODID)
        config = SimpleConfig.of("vinery").provider { filename: String -> this.getDefaultConfig(filename) }
            .request()
        VineryGrapeRegistry
        VineryObjectRegistry()
        log.info("Vinery Initializing Complete, Success!")
    }

    companion object {
        const val MODID: String = "vinery"
        val log: Logger = LogManager.getLogger(MODID)
        @JvmStatic
        var config: SimpleConfig? = null
            private set
    }

    //TODO: Convert the Existing Cloth Config System to Simple Config
    private fun getDefaultConfig(filename: String): String {
        return """"""
    }
}