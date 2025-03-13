package net.satisfy.vinery.fabric.config

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.annotation.ConfigEntry

@Config(name = "vinery")
@Config.Gui.Background("vinery:textures/block/dark_cherry_planks.png")
class VineryFabricConfig : ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    var blocks: BlocksSettings = BlocksSettings()

    @ConfigEntry.Gui.CollapsibleObject
    var items: ItemsSettings = ItemsSettings()


    @ConfigEntry.Gui.CollapsibleObject
    var villager: VillagerSettings = VillagerSettings()

    class BlocksSettings {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 10000)
        var totalFermentationTime: Int = 6000

        @ConfigEntry.BoundedDiscrete(min = 100, max = 10000)
        var maxFluidLevel: Int = 100

        @ConfigEntry.BoundedDiscrete(min = 1, max = 10000)
        var maxFluidIncrease: Int = 25

        @ConfigEntry.BoundedDiscrete(min = 1, max = 10000)
        var applePressMashingTime: Int = 600

        @ConfigEntry.BoundedDiscrete(min = 1, max = 10000)
        var applePressFermentationTime: Int = 800

        @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
        var cherryGrowthChance: Double = 0.4

        @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
        var appleGrowthChance: Double = 0.4

        @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
        var grapeGrowthChance: Double = 0.5
    }

    class ItemsSettings {
        @ConfigEntry.Gui.CollapsibleObject
        var wine: WineSettings = WineSettings()

        @ConfigEntry.Gui.CollapsibleObject
        var banner: BannerSettings = BannerSettings()

        @ConfigEntry.Gui.CollapsibleObject
        var basket: BasketSettings = BasketSettings()

        class WineSettings {
            @ConfigEntry.BoundedDiscrete(min = 1, max = 100000)
            var startDuration: Int = 1800

            @ConfigEntry.BoundedDiscrete(min = 1, max = 100000)
            var maxDuration: Int = 15000

            @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
            var maxLevel: Int = 5

            @ConfigEntry.BoundedDiscrete(min = 1, max = 10000)
            var durationPerYear: Int = 200

            @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
            var daysPerYear: Int = 24

            @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
            var yearsPerEffectLevel: Int = 6
        }
    }

    class BannerSettings {
        var giveEffect: Boolean = true

        var showTooltip: Boolean = true

        val isShowTooltipEnabled: Boolean
            get() = giveEffect && showTooltip
    }

    @ConfigEntry.Gui.CollapsibleObject
    var trader: TraderSettings = TraderSettings()

    class TraderSettings {
        @ConfigEntry.BoundedDiscrete(min = 0, max = 1)
        var spawnChance: Double = 0.5
        var spawnWithMules: Boolean = true

        @ConfigEntry.BoundedDiscrete(min = 0, max = 72000)
        var spawnDelay: Int = 48000
    }

    class VillagerSettings {
        @ConfigEntry.Gui.CollapsibleObject
        var level1: TradeLevelSettings = TradeLevelSettings(1)

        @ConfigEntry.Gui.CollapsibleObject
        var level2: TradeLevelSettings = TradeLevelSettings(2)

        @ConfigEntry.Gui.CollapsibleObject
        var level3: TradeLevelSettings = TradeLevelSettings(3)

        @ConfigEntry.Gui.CollapsibleObject
        var level4: TradeLevelSettings = TradeLevelSettings(4)

        @ConfigEntry.Gui.CollapsibleObject
        var level5: TradeLevelSettings = TradeLevelSettings(5)

        class TradeLevelSettings(var level: Int) {
            var trades: MutableList<TradeEntry> = ArrayList()

            init {
                if (level == 1) {
                    trades.add(TradeEntry("vinery:red_grape", TradeType.BUY, 15, 1, 4, 5))
                    trades.add(TradeEntry("vinery:white_grape", TradeType.BUY, 15, 1, 4, 5))
                    trades.add(TradeEntry("vinery:red_grape_seeds", TradeType.SELL, 2, 1, 1, 5))
                    trades.add(TradeEntry("vinery:white_grape_seeds", TradeType.SELL, 2, 1, 1, 5))
                } else if (level == 2) {
                    trades.add(TradeEntry("vinery:wine_bottle", TradeType.SELL, 1, 1, 2, 7))
                } else if (level == 3) {
                    trades.add(TradeEntry("vinery:flower_box", TradeType.SELL, 3, 1, 1, 10))
                    trades.add(TradeEntry("vinery:white_grape_bag", TradeType.SELL, 7, 1, 1, 10))
                    trades.add(TradeEntry("vinery:red_grape_bag", TradeType.SELL, 7, 1, 1, 10))
                } else if (level == 4) {
                    trades.add(TradeEntry("vinery:basket", TradeType.SELL, 4, 1, 1, 10))
                    trades.add(TradeEntry("vinery:flower_pot_big", TradeType.SELL, 5, 1, 1, 10))
                    trades.add(TradeEntry("vinery:window", TradeType.SELL, 12, 1, 1, 10))
                    trades.add(TradeEntry("vinery:dark_cherry_beam", TradeType.SELL, 6, 1, 1, 10))
                    trades.add(TradeEntry("vinery:taiga_red_grape_seeds", TradeType.SELL, 2, 1, 1, 5))
                    trades.add(TradeEntry("vinery:taiga_white_grape_seeds", TradeType.SELL, 2, 1, 1, 5))
                } else if (level == 5) {
                    trades.add(TradeEntry("vinery:wine_box", TradeType.SELL, 10, 1, 1, 10))
                    trades.add(TradeEntry("vinery:lilitu_wine", TradeType.SELL, 4, 1, 1, 10))
                    trades.add(TradeEntry("vinery:calendar", TradeType.SELL, 12, 1, 1, 15))
                }
            }
        }

        class TradeEntry {
            var item: String = "vinery:white_grape"
            var type: TradeType = TradeType.BUY
            var price: Int = 1
            var count: Int = 1
            var maxUses: Int = 1
            var experience: Int = 1

            constructor()

            constructor(item: String, type: TradeType, price: Int, count: Int, maxUses: Int, experience: Int) {
                this.item = item
                this.type = type
                this.price = price
                this.count = count
                this.maxUses = maxUses
                this.experience = experience
            }
        }

        enum class TradeType {
            BUY,
            SELL
        }
    }

    class BasketSettings {
        @ConfigEntry.Gui.CollapsibleObject
        @ConfigEntry.Category("Items/Basket")
        var blacklist: BasketBlacklistSettings = BasketBlacklistSettings()

        class BasketBlacklistSettings {
            var basketBlacklist: MutableList<String> = ArrayList()

            init {
                basketBlacklist.add("minecraft:shulker_box")
                basketBlacklist.add("vinery:basket")
            }
        }
    }
}
