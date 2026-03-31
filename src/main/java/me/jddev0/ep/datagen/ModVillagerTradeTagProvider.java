package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.villager.EPVillagerTradeTags;
import me.jddev0.ep.villager.EPVillagerTrades;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.concurrent.CompletableFuture;

public class ModVillagerTradeTagProvider extends KeyTagProvider<VillagerTrade> {
    public ModVillagerTradeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.VILLAGER_TRADE, lookupProvider, EPAPI.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag(EPVillagerTradeTags.ELECTRICIAN_LEVEL_1).
                add(EPVillagerTrades.ELECTRICIAN_1_ENERGIZED_POWER_BOOK,
                        EPVillagerTrades.ELECTRICIAN_1_COPPER_INGOT_EMERALD,
                        EPVillagerTrades.ELECTRICIAN_1_SILICON_EMERALD,
                        EPVillagerTrades.ELECTRICIAN_1_CABLE_INSULATOR,
                        EPVillagerTrades.ELECTRICIAN_1_IRON_HAMMER);

        tag(EPVillagerTradeTags.ELECTRICIAN_LEVEL_2).
                add(EPVillagerTrades.ELECTRICIAN_2_COPPER_CABLE,
                        EPVillagerTrades.ELECTRICIAN_2_BATTERY_2,
                        EPVillagerTrades.ELECTRICIAN_2_ENERGY_ANALYZER,
                        EPVillagerTrades.ELECTRICIAN_2_FLUID_ANALYZER,
                        EPVillagerTrades.ELECTRICIAN_2_COPPER_PLATE_EMERALD,
                        EPVillagerTrades.ELECTRICIAN_2_CUTTER);

        tag(EPVillagerTradeTags.ELECTRICIAN_LEVEL_3).
                add(EPVillagerTrades.ELECTRICIAN_3_COAL_ENGINE,
                        EPVillagerTrades.ELECTRICIAN_3_SOLAR_PANEL_1,
                        EPVillagerTrades.ELECTRICIAN_3_FLUID_FILLER,
                        EPVillagerTrades.ELECTRICIAN_3_AUTO_CRAFTER,
                        EPVillagerTrades.ELECTRICIAN_3_CHARGER,
                        EPVillagerTrades.ELECTRICIAN_3_BASIC_SOLAR_CELL_EMERALD);

        tag(EPVillagerTradeTags.ELECTRICIAN_LEVEL_4).
                add(EPVillagerTrades.ELECTRICIAN_4_SAWMILL,
                        EPVillagerTrades.ELECTRICIAN_4_CRUSHER,
                        EPVillagerTrades.ELECTRICIAN_4_COMPRESSOR,
                        EPVillagerTrades.ELECTRICIAN_4_BATTERY_4,
                        EPVillagerTrades.ELECTRICIAN_4_SAWDUST_EMERALD);

        tag(EPVillagerTradeTags.ELECTRICIAN_LEVEL_5).
                add(EPVillagerTrades.ELECTRICIAN_5_THERMAL_GENERATOR,
                        EPVillagerTrades.ELECTRICIAN_5_ENERGIZER,
                        EPVillagerTrades.ELECTRICIAN_5_LIGHTNING_GENERATOR,
                        EPVillagerTrades.ELECTRICIAN_5_ENERGIZED_COPPER_EMERALD);
    }
}
