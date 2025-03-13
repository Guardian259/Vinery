package net.satisfy.vinery

import net.darktree.simpleconfig.SimpleConfig
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


class Vinery : ModInitializer {

    override fun onInitialize() {
        modInit()
    }

    private fun modInit() {
        log.info("Vinery Initializing...")
        config = SimpleConfig.of("vinery").provider { filename: String -> this.getDefaultConfig(filename) }
            .request()
        log.info("Vinery Initializing Complete, Success!")
    }

    companion object {
        const val MODID: String = "vinery"
        val log: Logger = LogManager.getLogger(MODID)
        @JvmStatic
        var config: SimpleConfig? = null
            private set
        @JvmStatic
        fun id(name: String?): ResourceLocation = ResourceLocation.of(MODID, name) //TODO: Correct ResourceLocation.of
    }

    //TODO: Convert the Existing Cloth Config System to Simple Config
    fun getDefaultConfig(filename: String): String {
        return """"""
    }
}