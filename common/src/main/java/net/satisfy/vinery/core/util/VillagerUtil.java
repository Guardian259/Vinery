package net.satisfy.vinery.core.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

/**
 * A utility class providing factories for creating villager trade offers in a Minecraft mod environment.
 */
public class VillagerUtil {
    /**
     * Constructs a new VillagerUtil instance.
     */
    public VillagerUtil() {
    }

    /**
     * A factory for villager trades where the villager sells an item for emeralds.
     * Implements {@link VillagerTrades.ItemListing} to define trade offers.
     */
    public static class SellItemFactory implements VillagerTrades.ItemListing {
        /** The item stack the villager sells. */
        private final ItemStack sell;
        /** The emerald price for the trade. */
        private final int price;
        /** The number of items sold per trade. */
        private final int count;
        /** The maximum number of times the trade can be used. */
        private final int maxUses;
        /** The experience granted to the villager upon trading. */
        private final int experience;
        /** The price multiplier for trade adjustments. */
        private final float multiplier;

        /**
         * Creates a sell trade for a block with default multiplier.
         * @param block The block to sell.
         * @param price The emerald cost.
         * @param count The quantity sold.
         * @param maxUses The maximum trade uses.
         * @param experience The experience awarded.
         */
        public SellItemFactory(Block block, int price, int count, int maxUses, int experience) {
            this(new ItemStack(block), price, count, maxUses, experience);
        }

        /**
         * Creates a sell trade for a block with default max uses (12).
         * @param item The block to sell.
         * @param price The emerald cost.
         * @param count The quantity sold.
         * @param experience The experience awarded.
         */
        public SellItemFactory(Block item, int price, int count, int experience) {
            this((ItemStack)(new ItemStack(item)), price, count, 12, experience);
        }

        /**
         * Creates a sell trade for an item with default max uses (12).
         * @param item The item to sell.
         * @param price The emerald cost.
         * @param count The quantity sold.
         * @param experience The experience awarded.
         */
        public SellItemFactory(Item item, int price, int count, int experience) {
            this((ItemStack)(new ItemStack(item)), price, count, 12, experience);
        }

        /**
         * Creates a sell trade for an item with custom max uses.
         * @param item The item to sell.
         * @param price The emerald cost.
         * @param count The quantity sold.
         * @param maxUses The maximum trade uses.
         * @param experience The experience awarded.
         */
        public SellItemFactory(Item item, int price, int count, int maxUses, int experience) {
            this(new ItemStack(item), price, count, maxUses, experience);
        }

        /**
         * Creates a sell trade for an item stack with default multiplier (0.05).
         * @param stack The item stack to sell.
         * @param price The emerald cost.
         * @param count The quantity sold.
         * @param maxUses The maximum trade uses.
         * @param experience The experience awarded.
         */
        public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience) {
            this(stack, price, count, maxUses, experience, 0.05F);
        }

        /**
         * Creates a sell trade for an item stack with full customization.
         * @param stack The item stack to sell.
         * @param price The emerald cost.
         * @param count The quantity sold.
         * @param maxUses The maximum trade uses.
         * @param experience The experience awarded.
         * @param multiplier The price adjustment multiplier.
         */
        public SellItemFactory(ItemStack stack, int price, int count, int maxUses, int experience, float multiplier) {
            this.sell = stack;
            this.price = price;
            this.count = count;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }

        /**
         * Generates a trade offer for the villager to sell items for emeralds.
         * @param entity The trading entity (typically a villager).
         * @param random A random source for trade generation.
         * @return A {@link MerchantOffer} with the configured trade details.
         */
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.price), new ItemStack(this.sell.getItem(), this.count), this.maxUses, this.experience, this.multiplier);
        }
    }

    /**
     * A factory for villager trades where the villager buys an item for one emerald.
     * Implements {@link VillagerTrades.ItemListing} to define trade offers.
     */
    public static class BuyForOneEmeraldFactory implements VillagerTrades.ItemListing {
        /** The item the villager buys. */
        private final Item buy;
        /** The quantity of the item required for the trade. */
        private final int price;
        /** The maximum number of times the trade can be used. */
        private final int maxUses;
        /** The experience granted to the villager upon trading. */
        private final int experience;
        /** The price multiplier for trade adjustments, defaults to 0.05. */
        private final float multiplier;

        /**
         * Creates a buy trade where the villager purchases an item for one emerald.
         * @param item The item to buy, convertible via {@link ItemLike#asItem()}.
         * @param price The quantity of the item required.
         * @param maxUses The maximum trade uses.
         * @param experience The experience awarded.
         */
        public BuyForOneEmeraldFactory(ItemLike item, int price, int maxUses, int experience) {
            this.buy = item.asItem();
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = 0.05F;
        }

        /**
         * Generates a trade offer for the villager to buy items for one emerald.
         * @param entity The trading entity (typically a villager).
         * @param random A random source for trade generation.
         * @return A {@link MerchantOffer} with the configured trade details.
         */
        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            ItemStack itemStack = new ItemStack(this.buy, this.price);
            return new MerchantOffer(itemStack, new ItemStack(Items.EMERALD), this.maxUses, this.experience, this.multiplier);
        }
    }
}

