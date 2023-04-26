package me.jddev0.ep.villager;

import com.google.common.collect.ImmutableSet;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.EnergyAnalyzerItem;
import me.jddev0.ep.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.fabricmc.fabric.mixin.object.builder.VillagerProfessionAccessor;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
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
            VillagerProfessionAccessor.create("electrician", BASIC_MACHINE_FRAME_POI, ImmutableSet.of(), ImmutableSet.of(),
                    SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH));

    private static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(new Identifier(EnergizedPowerMod.MODID, name), 1, 1,
            ImmutableSet.copyOf(block.getStateManager().getStates()));
    }

    private static VillagerProfession registerProfession(String name, VillagerProfession profession) {
        return Registry.register(Registry.VILLAGER_PROFESSION, new Identifier(EnergizedPowerMod.MODID, name), profession);
    }

    public static void register() {
        registerTrades();
    }

    private static void registerTrades() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 1, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(Items.BOOK),
                    new ItemStack(ModItems.ENERGIZED_POWER_BOOK),
                    3, 3, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 1, factories -> {
            addOffer(factories,
                    new ItemStack(Items.COPPER_INGOT, 2),
                    new ItemStack(Items.EMERALD, 1),
                    25, 1, .02f);

            addOffer(factories,
                    new ItemStack(ModItems.SILICON, 3),
                    new ItemStack(Items.EMERALD, 2),
                    15, 2, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 2, factories -> {
            addOffer(factories,
                    new ItemStack(ModItems.BASIC_SOLAR_CELL, 2),
                    new ItemStack(Items.EMERALD, 3),
                    5, 5, .02f);

            {
                ItemStack energyAnalyzer = new ItemStack(ModItems.BATTERY_2);
                energyAnalyzer.getOrCreateNbt().put("energy", NbtInt.of(128));

                addOffer(factories,
                        new ItemStack(Items.EMERALD, 6),
                        new ItemStack(Items.COPPER_INGOT, 4),
                        energyAnalyzer,
                        3, 8, .02f);
            }
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 3, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM, 1),
                    new ItemStack(ModBlocks.AUTO_CRAFTER_ITEM, 1),
                    5, 10, .02f);

            {

                ItemStack energyAnalyzer = new ItemStack(ModItems.ENERGY_ANALYZER);
                energyAnalyzer.getOrCreateNbt().put("energy", NbtLong.of(16 * EnergyAnalyzerItem.ENERGY_CONSUMPTION_PER_USE));

                addOffer(factories,
                        new ItemStack(Items.EMERALD, 16),
                        new ItemStack(Items.COPPER_INGOT, 12),
                        energyAnalyzer,
                        1, 12, .02f);
            }
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 4, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(ModItems.BASIC_SOLAR_CELL, 2),
                    new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_1, 1),
                    3, 19, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 14),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM, 1),
                    new ItemStack(ModBlocks.COAL_ENGINE_ITEM, 1),
                    2, 24, .02f);
        });

        TradeOfferHelper.registerVillagerOffers(ELECTRICIAN_PROFESSION, 5, factories -> {
            addOffer(factories,
                    new ItemStack(Items.EMERALD, 59),
                    new ItemStack(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM, 1),
                    new ItemStack(ModBlocks.LIGHTNING_GENERATOR_ITEM, 1),
                    1, 30, .02f);

            addOffer(factories,
                    new ItemStack(Items.EMERALD, 24),
                    new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_1, 2),
                    new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_2, 1),
                    3, 41, .02f);
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
