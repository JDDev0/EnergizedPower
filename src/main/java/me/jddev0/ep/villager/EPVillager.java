package me.jddev0.ep.villager;

import com.google.common.collect.ImmutableSet;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.List;

public final class EPVillager {
    private EPVillager() {}

    public static final PointOfInterestType BASIC_MACHINE_FRAME_POI = registerPOI("basic_machine_frame_poi",
            EPBlocks.BASIC_MACHINE_FRAME);

    public static final VillagerProfession ELECTRICIAN_PROFESSION = registerProfession("electrician",
            new VillagerProfession("electrician", poiType -> poiType.value() == BASIC_MACHINE_FRAME_POI,
                    poiType -> poiType.value() == BASIC_MACHINE_FRAME_POI, ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH));

    private static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(EPAPI.id(name), 1, 1,
            ImmutableSet.copyOf(block.getStateManager().getStates()));
    }

    private static VillagerProfession registerProfession(String name, VillagerProfession profession) {
        return Registry.register(Registry.VILLAGER_PROFESSION, EPAPI.id(name), profession);
    }

    public static void register() {
        registerTrades();
    }

    private static void registerTrades() {
        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 1, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(Items.BOOK),
                    new ItemStack(EPItems.ENERGIZED_POWER_BOOK),
                    3, 3, .02f);

            addOffer(factories,
                    new ItemStack(Items.COPPER_INGOT, 2),
                    new ItemStack(Items.EMERALD, 1),
                    25, 1, .02f);

            addOffer(factories,
                    new ItemStack(EPItems.SILICON, 3),
                    new ItemStack(Items.EMERALD, 2),
                    15, 2, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(EPItems.CABLE_INSULATOR, 16),
                    5, 3, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 9),
                    new ItemStack(EPItems.IRON_HAMMER),
                    2, 3, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 2, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 35),
                    new ItemStack(EPBlocks.COPPER_CABLE_ITEM, 6),
                    3, 5, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(Items.COPPER_INGOT, 4),
                    new ItemStack(EPItems.BATTERY_2),
                    3, 7, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(Items.COPPER_INGOT, 12),
                    new ItemStack(EPItems.ENERGY_ANALYZER),
                    2, 8, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(Items.COPPER_INGOT, 12),
                    new ItemStack(EPItems.FLUID_ANALYZER),
                    2, 8, .02f);

            addOffer(factories,
                    new ItemStack(EPItems.COPPER_PLATE, 3),
                    new ItemStack(Items.EMERALD, 8),
                    15, 6, .02f);
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 12),
                    new ItemStack(EPItems.CUTTER),
                    2, 8, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 3, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 21),
                    new ItemStack(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.COAL_ENGINE_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 31),
                    new ItemStack(EPItems.BASIC_SOLAR_CELL, 2),
                    new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_1),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 33),
                    new ItemStack(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.FLUID_FILLER_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 38),
                    new ItemStack(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.AUTO_CRAFTER_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 46),
                    new ItemStack(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.CHARGER_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemStack(EPItems.BASIC_SOLAR_CELL, 3),
                    new ItemStack(Items.EMERALD, 9),
                    15, 9, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 4, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 34),
                    new ItemStack(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.SAWMILL_ITEM),
                    3, 20, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 39),
                    new ItemStack(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.CRUSHER_ITEM),
                    3, 20, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 52),
                    new ItemStack(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.COMPRESSOR_ITEM),
                    3, 20, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 29),
                    new ItemStack(Items.COPPER_INGOT, 9),
                    new ItemStack(EPItems.BATTERY_4),
                    2, 19, .02f);

            addOffer(factories,
                    new ItemStack(EPItems.SAWDUST, 17),
                    new ItemStack(Items.EMERALD, 4),
                    20, 18, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 5, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 32),
                    new ItemStack(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.THERMAL_GENERATOR_ITEM),
                    1, 30, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD_BLOCK, 9),
                    new ItemStack(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.ENERGIZER_ITEM),
                    1, 30, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD_BLOCK, 12),
                    new ItemStack(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.LIGHTNING_GENERATOR_ITEM),
                    1, 30, .02f);

            addOffer(factories,
                    new ItemStack(EPItems.ENERGIZED_COPPER_INGOT),
                    new ItemStack(Items.EMERALD, 23),
                    15, 30, .02f);
        });
    }

    private static void addOffer(List<TradeOffers.Factory> factories, ItemStack cost, ItemStack result, int maxUses,
                                 int xp, float priceMultiplier) {
        factories.add(((entity, rand) -> new TradeOffer(cost, result, maxUses, xp, priceMultiplier)));
    }

    private static void addOffer(List<TradeOffers.Factory> factories, ItemStack costA, ItemStack costB, ItemStack result,
                                 int maxUses, int xp, float priceMultiplier) {
        factories.add(((entity, rand) -> new TradeOffer(costA, costB, result, maxUses, xp, priceMultiplier)));
    }
}
