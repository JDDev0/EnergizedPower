package me.jddev0.ep.villager;

import com.google.common.collect.ImmutableSet;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import java.util.List;
import java.util.Optional;

public final class EPVillager {
    private EPVillager() {}

    public static final PoiType BASIC_MACHINE_FRAME_POI = registerPOI("basic_machine_frame_poi",
            EPBlocks.BASIC_MACHINE_FRAME);

    public static final ResourceKey<PoiType> BASIC_MACHINE_FRAME_POI_KEY = poiKey("basic_machine_frame_poi");

    public static final ResourceKey<VillagerProfession> ELECTRICIAN_PROFESSION_KEY = professionKey("electrician");

    public static final VillagerProfession ELECTRICIAN_PROFESSION = registerProfession(ELECTRICIAN_PROFESSION_KEY,
            new VillagerProfession(Component.translatable("entity.minecraft.villager.electrician"), poiType -> poiType.value() == BASIC_MACHINE_FRAME_POI,
                    poiType -> poiType.value() == BASIC_MACHINE_FRAME_POI, ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_TOOLSMITH));

    private static PoiType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(EPAPI.id(name), 1, 1,
            ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates()));
    }

    private static ResourceKey<PoiType> poiKey(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, EPAPI.id(name));
    }

    private static ResourceKey<VillagerProfession> professionKey(String name) {
        return ResourceKey.create(Registries.VILLAGER_PROFESSION, EPAPI.id(name));
    }

    private static VillagerProfession registerProfession(ResourceKey<VillagerProfession> professionKey, VillagerProfession profession) {
        return Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, professionKey, profession);
    }

    public static void register() {
        registerTrades();
    }

    private static void registerTrades() {
        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION_KEY, 1, factories -> {
            addOffer(factories,
                    new ItemCost(Items.EMERALD, 6),
                    new ItemCost(Items.BOOK),
                    new ItemStack(EPItems.ENERGIZED_POWER_BOOK),
                    3, 3, .02f);

            addOffer(factories,
                    new ItemCost(Items.COPPER_INGOT, 2),
                    new ItemStack(Items.EMERALD, 1),
                    25, 1, .02f);

            addOffer(factories,
                    new ItemCost(EPItems.SILICON, 3),
                    new ItemStack(Items.EMERALD, 2),
                    15, 2, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD, 6),
                    new ItemStack(EPItems.CABLE_INSULATOR, 16),
                    5, 3, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD, 9),
                    new ItemStack(EPItems.IRON_HAMMER),
                    2, 3, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION_KEY, 2, factories -> {
            addOffer(factories,
                    new ItemCost(Items.EMERALD, 35),
                    new ItemStack(EPBlocks.COPPER_CABLE_ITEM, 6),
                    3, 5, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD, 6),
                    new ItemCost(Items.COPPER_INGOT, 4),
                    new ItemStack(EPItems.BATTERY_2),
                    3, 7, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD, 6),
                    new ItemCost(Items.COPPER_INGOT, 12),
                    new ItemStack(EPItems.ENERGY_ANALYZER),
                    2, 8, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD, 6),
                    new ItemCost(Items.COPPER_INGOT, 12),
                    new ItemStack(EPItems.FLUID_ANALYZER),
                    2, 8, .02f);

            addOffer(factories,
                    new ItemCost(EPItems.COPPER_PLATE, 3),
                    new ItemStack(Items.EMERALD, 8),
                    15, 6, .02f);
            addOffer(factories,
                    new ItemCost(Items.EMERALD, 12),
                    new ItemStack(EPItems.CUTTER),
                    2, 8, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION_KEY, 3, factories -> {
            addOffer(factories,
                    new ItemCost(Items.EMERALD, 21),
                    new ItemCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.COAL_ENGINE_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD, 31),
                    new ItemCost(EPItems.BASIC_SOLAR_CELL, 2),
                    new ItemStack(EPBlocks.SOLAR_PANEL_ITEM_1),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD, 33),
                    new ItemCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.FLUID_FILLER_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD, 38),
                    new ItemCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.AUTO_CRAFTER_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD, 46),
                    new ItemCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.CHARGER_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemCost(EPItems.BASIC_SOLAR_CELL, 3),
                    new ItemStack(Items.EMERALD, 9),
                    15, 9, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION_KEY, 4, factories -> {
            addOffer(factories,
                    new ItemCost(Items.EMERALD, 34),
                    new ItemCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.SAWMILL_ITEM),
                    3, 20, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD, 39),
                    new ItemCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.CRUSHER_ITEM),
                    3, 20, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD, 52),
                    new ItemCost(EPBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.COMPRESSOR_ITEM),
                    3, 20, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD, 29),
                    new ItemCost(Items.COPPER_INGOT, 9),
                    new ItemStack(EPItems.BATTERY_4),
                    2, 19, .02f);

            addOffer(factories,
                    new ItemCost(EPItems.SAWDUST, 17),
                    new ItemStack(Items.EMERALD, 4),
                    20, 18, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION_KEY, 5, factories -> {
            addOffer(factories,
                    new ItemCost(Items.EMERALD, 32),
                    new ItemCost(EPBlocks.HARDENED_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.THERMAL_GENERATOR_ITEM),
                    1, 30, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD_BLOCK, 9),
                    new ItemCost(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.ENERGIZER_ITEM),
                    1, 30, .02f);

            addOffer(factories,
                    new ItemCost(Items.EMERALD_BLOCK, 12),
                    new ItemCost(EPBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                    new ItemStack(EPBlocks.LIGHTNING_GENERATOR_ITEM),
                    1, 30, .02f);

            addOffer(factories,
                    new ItemCost(EPItems.ENERGIZED_COPPER_INGOT),
                    new ItemStack(Items.EMERALD, 23),
                    15, 30, .02f);
        });
    }

    private static void addOffer(List<VillagerTrades.ItemListing> factories, ItemCost cost, ItemStack result, int maxUses,
                                 int xp, float priceMultiplier) {
        addOffer(factories, cost, null, result, maxUses, xp, priceMultiplier);
    }

    private static void addOffer(List<VillagerTrades.ItemListing> factories, ItemCost costA, ItemCost costB, ItemStack result,
                                 int maxUses, int xp, float priceMultiplier) {
        factories.add(((world, entity, rand) -> new MerchantOffer(costA, Optional.ofNullable(costB), result, maxUses, xp, priceMultiplier)));
    }
}
