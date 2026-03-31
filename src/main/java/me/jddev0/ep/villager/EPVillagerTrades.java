package me.jddev0.ep.villager;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.List;
import java.util.Optional;

public class EPVillagerTrades {
    private EPVillagerTrades() {}

    public static final ResourceKey<VillagerTrade> ELECTRICIAN_1_ENERGIZED_POWER_BOOK = registerKey("electrician/1/energized_power_book");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_1_COPPER_INGOT_EMERALD = registerKey("electrician/1/copper_ingot_emerald");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_1_SILICON_EMERALD = registerKey("electrician/1/silicon_emerald");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_1_CABLE_INSULATOR = registerKey("electrician/1/cable_insulator");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_1_IRON_HAMMER = registerKey("electrician/1/iron_hammer");

    public static final ResourceKey<VillagerTrade> ELECTRICIAN_2_COPPER_CABLE = registerKey("electrician/2/copper_cable");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_2_BATTERY_2 = registerKey("electrician/2/battery_2");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_2_ENERGY_ANALYZER = registerKey("electrician/2/energy_analyzer");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_2_FLUID_ANALYZER = registerKey("electrician/2/fluid_analyzer");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_2_COPPER_PLATE_EMERALD = registerKey("electrician/2/copper_plate_emerald");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_2_CUTTER = registerKey("electrician/2/cutter");

    public static final ResourceKey<VillagerTrade> ELECTRICIAN_3_COAL_ENGINE = registerKey("electrician/3/coal_engine");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_3_SOLAR_PANEL_1 = registerKey("electrician/3/solar_panel_1");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_3_FLUID_FILLER = registerKey("electrician/3/fluid_filler");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_3_AUTO_CRAFTER = registerKey("electrician/3/auto_crafter");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_3_CHARGER = registerKey("electrician/3/charger");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_3_BASIC_SOLAR_CELL_EMERALD = registerKey("electrician/3/basic_solar_cell_emerald");

    public static final ResourceKey<VillagerTrade> ELECTRICIAN_4_SAWMILL = registerKey("electrician/4/sawmill");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_4_CRUSHER = registerKey("electrician/4/crusher");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_4_COMPRESSOR = registerKey("electrician/4/compressor");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_4_BATTERY_4 = registerKey("electrician/4/battery_4");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_4_SAWDUST_EMERALD = registerKey("electrician/4/sawdust_emerald");

    public static final ResourceKey<VillagerTrade> ELECTRICIAN_5_THERMAL_GENERATOR = registerKey("electrician/5/thermal_generator");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_5_ENERGIZER = registerKey("electrician/5/energizer");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_5_LIGHTNING_GENERATOR = registerKey("electrician/5/lightning_generator");
    public static final ResourceKey<VillagerTrade> ELECTRICIAN_5_ENERGIZED_COPPER_EMERALD = registerKey("electrician/5/energized_copper_emerald");

    public static void bootstrap(BootstrapContext<VillagerTrade> context) {
        //Level 1
        register(context, ELECTRICIAN_1_ENERGIZED_POWER_BOOK,
                new TradeCost(Items.EMERALD, 6), new TradeCost(Items.BOOK, 1),
                new ItemStackTemplate(EPItems.ENERGIZED_POWER_BOOK),
                3, 3, .02f);

        register(context, ELECTRICIAN_1_COPPER_INGOT_EMERALD,
                new TradeCost(Items.COPPER_INGOT, 2),
                new ItemStackTemplate(Items.EMERALD, 1),
                25, 1, .02f);

        register(context, ELECTRICIAN_1_SILICON_EMERALD,
                new TradeCost(EPItems.SILICON, 3),
                new ItemStackTemplate(Items.EMERALD, 2),
                15, 2, .02f);

        register(context, ELECTRICIAN_1_CABLE_INSULATOR,
                new TradeCost(Items.EMERALD, 6),
                new ItemStackTemplate(EPItems.CABLE_INSULATOR, 16),
                5, 3, .02f);

        register(context, ELECTRICIAN_1_IRON_HAMMER,
                new TradeCost(Items.EMERALD, 9),
                new ItemStackTemplate(EPItems.IRON_HAMMER),
                2, 3, .02f);

        //Level 2
        register(context, ELECTRICIAN_2_COPPER_CABLE,
                new TradeCost(Items.EMERALD, 35),
                new ItemStackTemplate(EPBlocks.COPPER_CABLE_ITEM, 6),
                3, 5, .02f);

        register(context, ELECTRICIAN_2_BATTERY_2,
                new TradeCost(Items.EMERALD, 6),
                new TradeCost(Items.COPPER_INGOT, 4),
                new ItemStackTemplate(EPItems.BATTERY_2),
                3, 7, .02f);

        register(context, ELECTRICIAN_2_ENERGY_ANALYZER,
                new TradeCost(Items.EMERALD, 6),
                new TradeCost(Items.COPPER_INGOT, 12),
                new ItemStackTemplate(EPItems.ENERGY_ANALYZER),
                2, 8, .02f);

        register(context, ELECTRICIAN_2_FLUID_ANALYZER,
                new TradeCost(Items.EMERALD, 6),
                new TradeCost(Items.COPPER_INGOT, 12),
                new ItemStackTemplate(EPItems.FLUID_ANALYZER),
                2, 8, .02f);

        register(context, ELECTRICIAN_2_COPPER_PLATE_EMERALD,
                new TradeCost(EPItems.COPPER_PLATE, 3),
                new ItemStackTemplate(Items.EMERALD, 8),
                15, 6, .02f);

        register(context, ELECTRICIAN_2_CUTTER,
                new TradeCost(Items.EMERALD, 12),
                new ItemStackTemplate(EPItems.CUTTER),
                2, 8, .02f);

        //Level 3
        register(context, ELECTRICIAN_3_COAL_ENGINE,
                new TradeCost(Items.EMERALD, 21),
                new TradeCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM, 1),
                new ItemStackTemplate(EPBlocks.COAL_ENGINE_ITEM),
                3, 10, .02f);

        register(context, ELECTRICIAN_3_SOLAR_PANEL_1,
                new TradeCost(Items.EMERALD, 31),
                new TradeCost(EPItems.BASIC_SOLAR_CELL, 2),
                new ItemStackTemplate(EPBlocks.SOLAR_PANEL_ITEM_1),
                3, 10, .02f);

        register(context, ELECTRICIAN_3_FLUID_FILLER,
                new TradeCost(Items.EMERALD, 33),
                new TradeCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM, 1),
                new ItemStackTemplate(EPBlocks.FLUID_FILLER_ITEM),
                3, 10, .02f);

        register(context, ELECTRICIAN_3_AUTO_CRAFTER,
                new TradeCost(Items.EMERALD, 38),
                new TradeCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM, 1),
                new ItemStackTemplate(EPBlocks.AUTO_CRAFTER_ITEM),
                3, 10, .02f);

        register(context, ELECTRICIAN_3_CHARGER,
                new TradeCost(Items.EMERALD, 46),
                new TradeCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM, 1),
                new ItemStackTemplate(EPBlocks.CHARGER_ITEM),
                3, 10, .02f);

        register(context, ELECTRICIAN_3_BASIC_SOLAR_CELL_EMERALD,
                new TradeCost(EPItems.BASIC_SOLAR_CELL, 3),
                new ItemStackTemplate(Items.EMERALD, 9),
                15, 9, .02f);

        //Level 4
        register(context, ELECTRICIAN_4_SAWMILL,
                new TradeCost(Items.EMERALD, 34),
                new TradeCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM, 1),
                new ItemStackTemplate(EPBlocks.SAWMILL_ITEM),
                3, 20, .02f);

        register(context, ELECTRICIAN_4_CRUSHER,
                new TradeCost(Items.EMERALD, 39),
                new TradeCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM, 1),
                new ItemStackTemplate(EPBlocks.CRUSHER_ITEM),
                3, 20, .02f);

        register(context, ELECTRICIAN_4_COMPRESSOR,
                new TradeCost(Items.EMERALD, 52),
                new TradeCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM, 1),
                new ItemStackTemplate(EPBlocks.COMPRESSOR_ITEM),
                3, 20, .02f);

        register(context, ELECTRICIAN_4_BATTERY_4,
                new TradeCost(Items.EMERALD, 29),
                new TradeCost(Items.COPPER_INGOT, 9),
                new ItemStackTemplate(EPItems.BATTERY_4),
                2, 19, .02f);

        register(context, ELECTRICIAN_4_SAWDUST_EMERALD,
                new TradeCost(EPItems.SAWDUST, 17),
                new ItemStackTemplate(Items.EMERALD, 4),
                20, 18, .02f);

        //Level 5
        register(context, ELECTRICIAN_5_THERMAL_GENERATOR,
                new TradeCost(Items.EMERALD, 32),
                new TradeCost(EPBlocks.HARDENED_MACHINE_FRAME_ITEM, 1),
                new ItemStackTemplate(EPBlocks.THERMAL_GENERATOR_ITEM),
                1, 30, .02f);

        register(context, ELECTRICIAN_5_ENERGIZER,
                new TradeCost(Items.EMERALD_BLOCK, 9),
                new TradeCost(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM, 1),
                new ItemStackTemplate(EPBlocks.ENERGIZER_ITEM),
                1, 30, .02f);

        register(context, ELECTRICIAN_5_LIGHTNING_GENERATOR,
                new TradeCost(Items.EMERALD_BLOCK, 12),
                new TradeCost(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM, 1),
                new ItemStackTemplate(EPBlocks.LIGHTNING_GENERATOR_ITEM),
                1, 30, .02f);

        register(context, ELECTRICIAN_5_ENERGIZED_COPPER_EMERALD,
                new TradeCost(EPItems.ENERGIZED_COPPER_INGOT, 1),
                new ItemStackTemplate(Items.EMERALD, 23),
                15, 30, .02f);
    }

    public static ResourceKey<VillagerTrade> registerKey(String name) {
        return ResourceKey.create(Registries.VILLAGER_TRADE, EPAPI.id(name));
    }

    private static void register(BootstrapContext<VillagerTrade> context, ResourceKey<VillagerTrade> key,
                                 TradeCost cost, ItemStackTemplate result, int maxUses, int xp, float priceMultiplier) {
        register(context, key, cost, null, result, maxUses, xp, priceMultiplier);
    }

    private static void register(BootstrapContext<VillagerTrade> context, ResourceKey<VillagerTrade> key,
                                 TradeCost costA, TradeCost costB, ItemStackTemplate result, int maxUses, int xp, float priceMultiplier) {
        context.register(key, new VillagerTrade(costA, Optional.ofNullable(costB), result, maxUses, xp, priceMultiplier,
                Optional.empty(), List.of()));
    }
}
