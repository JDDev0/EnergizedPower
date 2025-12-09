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
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.List;
import java.util.Optional;

public final class EPVillager {
    private EPVillager() {}

    public static final PointOfInterestType BASIC_MACHINE_FRAME_POI = registerPOI("basic_machine_frame_poi",
            EPBlocks.BASIC_MACHINE_FRAME);

    public static final RegistryKey<PointOfInterestType> BASIC_MACHINE_FRAME_POI_KEY = poiKey("basic_machine_frame_poi");

    public static final RegistryKey<VillagerProfession> ELECTRICIAN_PROFESSION_KEY = professionKey("electrician");

    public static final VillagerProfession ELECTRICIAN_PROFESSION = registerProfession(ELECTRICIAN_PROFESSION_KEY,
            new VillagerProfession(Text.translatable("entity.minecraft.villager.electrician"), poiType -> poiType.value() == BASIC_MACHINE_FRAME_POI,
                    poiType -> poiType.value() == BASIC_MACHINE_FRAME_POI, ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH));

    private static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(EPAPI.id(name), 1, 1,
            ImmutableSet.copyOf(block.getStateManager().getStates()));
    }

    private static RegistryKey<PointOfInterestType> poiKey(String name) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, EPAPI.id(name));
    }

    private static RegistryKey<VillagerProfession> professionKey(String name) {
        return RegistryKey.of(RegistryKeys.VILLAGER_PROFESSION, EPAPI.id(name));
    }

    private static VillagerProfession registerProfession(RegistryKey<VillagerProfession> professionKey, VillagerProfession profession) {
        return Registry.register(Registries.VILLAGER_PROFESSION, professionKey, profession);
    }

    public static void register() {
        registerTrades();
    }

    private static void registerTrades() {
        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION_KEY, 1, factories -> {
            addOffer(factories,
                    new TradedItem(Items.EMERALD, 6),
                    new TradedItem(Items.BOOK),
                    new ItemStack(EPItems.ENERGIZED_POWER_BOOK),
                    3, 3, .02f);

            addOffer(factories,
                    new TradedItem(Items.COPPER_INGOT, 2),
                    new ItemStack(Items.EMERALD, 1),
                    25, 1, .02f);

            addOffer(factories,
                    new TradedItem(EPItems.SILICON, 3),
                    new ItemStack(Items.EMERALD, 2),
                    15, 2, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD, 6),
                    new ItemStack(EPItems.CABLE_INSULATOR, 16),
                    5, 3, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD, 9),
                    new ItemStack(EPItems.IRON_HAMMER),
                    2, 3, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION_KEY, 2, factories -> {
            addOffer(factories,
                    new TradedItem(Items.EMERALD, 35),
                    new ItemStack(EPBlocks.COPPER_CABLE_ITEM, 6),
                    3, 5, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD, 6),
                    new TradedItem(Items.COPPER_INGOT, 4),
                    new ItemStack(EPItems.BATTERY_2),
                    3, 7, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD, 6),
                    new TradedItem(Items.COPPER_INGOT, 12),
                    new ItemStack(EPItems.ENERGY_ANALYZER),
                    2, 8, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD, 6),
                    new TradedItem(Items.COPPER_INGOT, 12),
                    new ItemStack(EPItems.FLUID_ANALYZER),
                    2, 8, .02f);

            addOffer(factories,
                    new TradedItem(EPItems.COPPER_PLATE, 3),
                    new ItemStack(Items.EMERALD, 8),
                    15, 6, .02f);
            addOffer(factories,
                    new TradedItem(Items.EMERALD, 12),
                    new ItemStack(EPItems.CUTTER),
                    2, 8, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION_KEY, 3, factories -> {
            addOffer(factories,
                    new TradedItem(Items.EMERALD, 21),
                    new TradedItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.COAL_ENGINE_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD, 31),
                    new TradedItem(EPItems.BASIC_SOLAR_CELL, 2),
                    new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_1),
                    3, 10, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD, 33),
                    new TradedItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.FLUID_FILLER_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD, 38),
                    new TradedItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.AUTO_CRAFTER_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD, 46),
                    new TradedItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.CHARGER_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new TradedItem(EPItems.BASIC_SOLAR_CELL, 3),
                    new ItemStack(Items.EMERALD, 9),
                    15, 9, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION_KEY, 4, factories -> {
            addOffer(factories,
                    new TradedItem(Items.EMERALD, 34),
                    new TradedItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.SAWMILL_ITEM),
                    3, 20, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD, 39),
                    new TradedItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.CRUSHER_ITEM),
                    3, 20, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD, 52),
                    new TradedItem(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.COMPRESSOR_ITEM),
                    3, 20, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD, 29),
                    new TradedItem(Items.COPPER_INGOT, 9),
                    new ItemStack(EPItems.BATTERY_4),
                    2, 19, .02f);

            addOffer(factories,
                    new TradedItem(EPItems.SAWDUST, 17),
                    new ItemStack(Items.EMERALD, 4),
                    20, 18, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION_KEY, 5, factories -> {
            addOffer(factories,
                    new TradedItem(Items.EMERALD, 32),
                    new TradedItem(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.THERMAL_GENERATOR_ITEM),
                    1, 30, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD_BLOCK, 9),
                    new TradedItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.ENERGIZER_ITEM),
                    1, 30, .02f);

            addOffer(factories,
                    new TradedItem(Items.EMERALD_BLOCK, 12),
                    new TradedItem(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.LIGHTNING_GENERATOR_ITEM),
                    1, 30, .02f);

            addOffer(factories,
                    new TradedItem(EPItems.ENERGIZED_COPPER_INGOT),
                    new ItemStack(Items.EMERALD, 23),
                    15, 30, .02f);
        });
    }

    private static void addOffer(List<TradeOffers.Factory> factories, TradedItem cost, ItemStack result, int maxUses,
                                 int xp, float priceMultiplier) {
        addOffer(factories, cost, null, result, maxUses, xp, priceMultiplier);
    }

    private static void addOffer(List<TradeOffers.Factory> factories, TradedItem costA, TradedItem costB, ItemStack result,
                                 int maxUses, int xp, float priceMultiplier) {
        factories.add(((world, entity, rand) -> new TradeOffer(costA, Optional.ofNullable(costB), result, maxUses, xp, priceMultiplier)));
    }
}
