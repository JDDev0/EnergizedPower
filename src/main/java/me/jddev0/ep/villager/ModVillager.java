package me.jddev0.ep.villager;

import com.google.common.collect.ImmutableSet;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.EnergyAnalyzerItem;
import me.jddev0.ep.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.List;

public final class ModVillager {
    private ModVillager() {}

    public static final PointOfInterestType BASIC_MACHINE_FRAME_POI = registerPOI("basic_machine_frame_poi",
            ModBlocks.BASIC_MACHINE_FRAME);

    public static final VillagerProfession ELECTRICIAN_PROFESSION = registerProfession("electrician",
            new VillagerProfession("electrician", poiType -> poiType.value() == BASIC_MACHINE_FRAME_POI,
                    poiType -> poiType.value() == BASIC_MACHINE_FRAME_POI, ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH));

    private static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(new Identifier(EnergizedPowerMod.MODID, name), 1, 1,
            ImmutableSet.copyOf(block.getStateManager().getStates()));
    }

    private static VillagerProfession registerProfession(String name, VillagerProfession profession) {
        return Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(EnergizedPowerMod.MODID, name), profession);
    }

    public static void register() {
        registerTrades();
    }

    private static void registerTrades() {
        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 1, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(Items.BOOK),
                    new ItemStack(ModItems.ENERGIZED_POWER_BOOK),
                    3, 3, .02f);

            addOffer(factories,
                    new ItemStack(Items.COPPER_INGOT, 2),
                    new ItemStack(Items.EMERALD, 1),
                    25, 1, .02f);

            addOffer(factories,
                    new ItemStack(ModItems.SILICON, 3),
                    new ItemStack(Items.EMERALD, 2),
                    15, 2, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(ModItems.CABLE_INSULATOR, 16),
                    5, 3, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 9),
                    new ItemStack(ModItems.IRON_HAMMER),
                    2, 3, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 2, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 35),
                    new ItemStack(ModBlocks.COPPER_CABLE_ITEM, 6),
                    3, 5, .02f);

            {

                ItemStack energyItem = new ItemStack(ModItems.BATTERY_2);
                energyItem.getOrCreateNbt().put("energy", NbtLong.of(128));

                addOffer(factories,
                        new ItemStack(Items.EMERALD, 6),
                        new ItemStack(Items.COPPER_INGOT, 4),
                        energyItem,
                        3, 7, .02f);
            }

            {
                ItemStack energyItem = new ItemStack(ModItems.ENERGY_ANALYZER);
                energyItem.getOrCreateNbt().put("energy", NbtLong.of(128));

                addOffer(factories,
                        new ItemStack(Items.EMERALD, 6),
                        new ItemStack(Items.COPPER_INGOT, 12),
                        energyItem,
                        2, 8, .02f);
            }

            addOffer(factories,
                    new ItemStack(ModItems.COPPER_PLATE, 3),
                    new ItemStack(Items.EMERALD, 8),
                    15, 6, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 3, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 21),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(ModBlocks.COAL_ENGINE_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 31),
                    new ItemStack(ModItems.BASIC_SOLAR_CELL, 2),
                    new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_1),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 33),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(ModBlocks.FLUID_FILLER_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 38),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(ModBlocks.AUTO_CRAFTER_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 46),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(ModBlocks.CHARGER_ITEM),
                    3, 10, .02f);

            addOffer(factories,
                    new ItemStack(ModItems.BASIC_SOLAR_CELL, 3),
                    new ItemStack(Items.EMERALD, 9),
                    15, 9, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 4, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 34),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(ModBlocks.SAWMILL_ITEM),
                    3, 20, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 39),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(ModBlocks.CRUSHER_ITEM),
                    3, 20, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 52),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(ModBlocks.COMPRESSOR_ITEM),
                    3, 20, .02f);
            {

                ItemStack energyItem = new ItemStack(ModItems.BATTERY_4);
                energyItem.getOrCreateNbt().put("energy", NbtLong.of(512));

                addOffer(factories,
                        new ItemStack(Items.EMERALD, 29),
                        new ItemStack(Items.COPPER_INGOT, 9),
                        energyItem,
                        2, 19, .02f);
            }

            addOffer(factories,
                    new ItemStack(ModItems.SAWDUST, 17),
                    new ItemStack(Items.EMERALD, 4),
                    20, 18, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 5, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 32),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM),
                    new ItemStack(ModBlocks.THERMAL_GENERATOR_ITEM),
                    1, 30, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD_BLOCK, 9),
                    new ItemStack(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                    new ItemStack(ModBlocks.ENERGIZER_ITEM),
                    1, 30, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD_BLOCK, 12),
                    new ItemStack(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM),
                    new ItemStack(ModBlocks.LIGHTNING_GENERATOR_ITEM),
                    1, 30, .02f);

            addOffer(factories,
                    new ItemStack(ModItems.ENERGIZED_COPPER_INGOT),
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
