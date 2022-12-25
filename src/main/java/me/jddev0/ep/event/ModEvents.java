package me.jddev0.ep.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.villager.ModVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = EnergizedPowerMod.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if(event.getType() == ModVillager.ELECTRICIAN_PROFESSION.get()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            //Level 1
            addOffer(trades, 1,
                    new ItemStack(Items.COPPER_INGOT, 2),
                    new ItemStack(Items.EMERALD, 1),
                    25, 1, .02f);
            addOffer(trades, 1,
                    new ItemStack(ModItems.SILICON.get(), 9),
                    new ItemStack(Items.EMERALD, 4),
                    15, 2, .02f);

            //Level 2
            addOffer(trades, 2,
                    new ItemStack(ModItems.BASIC_SOLAR_CELL.get(), 2),
                    new ItemStack(Items.EMERALD, 3),
                    5, 5, .02f);
            addOffer(trades, 2,
                    new ItemStack(Items.EMERALD, 9),
                    new ItemStack(ModItems.ENERGIZED_COPPER_INGOT.get(), 1),
                    10, 8, .02f);

            //Level 3
            addOffer(trades, 3,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM.get(), 1),
                    new ItemStack(ModBlocks.AUTO_CRAFTER_ITEM.get(), 1),
                    5, 10, .02f);
            addOffer(trades, 3,
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM.get(), 1),
                    new ItemStack(ModBlocks.COAL_ENGINE_ITEM.get(), 1),
                    5, 14, .02f);

            //Level 4
            addOffer(trades, 4,
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(ModItems.BASIC_SOLAR_CELL.get(), 2),
                    new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_1.get(), 1),
                    5, 19, .02f);
            addOffer(trades, 4,
                    new ItemStack(Items.EMERALD, 37),
                    new ItemStack(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM.get(), 1),
                    new ItemStack(ModBlocks.ENERGIZER_ITEM.get(), 1),
                    2, 26, .02f);

            //Level 5
            addOffer(trades, 5,
                    new ItemStack(Items.EMERALD, 49),
                    new ItemStack(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM.get(), 1),
                    new ItemStack(ModBlocks.LIGHTNING_GENERATOR_ITEM.get(), 1),
                    2, 30, .02f);
            addOffer(trades, 5,
                    new ItemStack(Items.EMERALD, 14),
                    new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_1.get(), 2),
                    new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_2.get(), 1),
                    5, 41, .02f);
        }
    }

    private static void addOffer(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, int minLevel,
                                 ItemStack cost, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        trades.get(minLevel).add((trader, rand) -> new MerchantOffer(cost, result, maxUses, xp, priceMultiplier));
    }

    private static void addOffer(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, int minLevel,
                                 ItemStack costA, ItemStack costB, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        trades.get(minLevel).add((trader, rand) -> new MerchantOffer(costA, costB, result, maxUses, xp, priceMultiplier));
    }
}
