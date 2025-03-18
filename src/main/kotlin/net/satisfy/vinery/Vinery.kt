package net.satisfy.vinery

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils
import net.darktree.simpleconfig.SimpleConfig
import net.fabricmc.api.ModInitializer
import net.satisfy.vinery.util.VineryGrapeRegistry
import net.satisfy.vinery.util.VineryObjectRegistry
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

    private fun getDefaultConfig(filename: String): String {
        return """
            #============================================================
            #=====================|Block Settings|=======================
            #============================================================
            #totalFermentationTime (min = 1, max = 10000)
            totalFermentationTime=6000
            #maxFluidLevel (min = 1, max = 10000)
            maxFluidLevel=100
            #maxFluidIncrease (min = 1, max = 10000)
            maxFluidIncrease=25
            #applePressMashingTime (min = 1, max = 10000)
            applePressMashingTime=600
            #applePressFermentationTime (min = 1, max = 10000)
            applePressFermentationTime=800
            #cherryGrowthChance (min = 0, max = 1)
            cherryGrowthChance=0.4
            #appleGrowthChance (min = 0, max = 1)
            appleGrowthChance=0.4
            #grapeGrowthChance (min = 0, max = 1)
            grapeGrowthChance=0.5
            #============================================================
            #=====================|Wine Settings|========================
            #============================================================
            #startDuration (min = 1, max = 100000)
            startDuration=1800
            #maxDuration (min = 1, max = 100000)
            maxDuration=15000
            #maxLevel (min = 1, max = 10)
            maxLevel=5
            #durationPerYear (min = 1, max = 10000)
            durationPerYear=200
            #daysPerYear (min = 1, max = 100)
            daysPerYear=24
            #yearsPerEffectLevel (min = 1, max = 100)
            yearsPerEffectLevel=6
            #============================================================
            #====================|Trader Settings|=======================
            #============================================================
            #spawnChance (min = 0, max = 1)
            spawnChance=0.5
            spawnWithMules=true
            #spawnDelay (min = 0, max = 72000)
            spawnDelay=48000
        """.trimIndent()
    }
}