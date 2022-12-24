package me.jddev0.ep.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.jddev0.ep.EnergizedPowerMod;
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

            addOffer(trades, 1,
                    new ItemStack(Items.EMERALD, 12),
                    new ItemStack(ModItems.ENERGIZED_COPPER_INGOT.get(), 1),
                    10, 20, .02f);
            addOffer(trades, 1,
                    new ItemStack(ModItems.SILICON.get(), 9),
                    new ItemStack(Items.EMERALD, 5),
                    15, 10, .02f);

            addOffer(trades, 2,
                    new ItemStack(ModItems.BASIC_SOLAR_CELL.get(), 2),
                    new ItemStack(Items.EMERALD, 3),
                    5, 35, .02f);

            //TODO add trades (for levels 2 - 5)
        }
    }

    private static void addOffer(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, int minLevel,
                                 ItemStack cost, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        trades.get(minLevel).add((trader, rand) -> new MerchantOffer(cost, result, maxUses, xp, priceMultiplier));
    }
}
