package me.jddev0.ep.datagen.adavancement;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.BatteryItem;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class ModAdvancedAdvancements implements ForgeAdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput,
                         ExistingFileHelper existingFileHelper) {
        AdvancementHolder energizedPowerAdvanced = Advancement.Builder.advancement().
                display(
                        ModItems.ENERGIZED_COPPER_INGOT.get(),
                        Component.translatable("advancements.energizedpower.energizedpower_advanced.title"),
                        Component.translatable("advancements.energizedpower.energizedpower_advanced.description"),
                        new ResourceLocation(EnergizedPowerMod.MODID, "textures/block/advanced_machine_frame_top.png"),
                        FrameType.TASK,
                        true,
                        true,
                        false
                ).
                addCriterion("has_the_item",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                                CommonItemTags.INGOTS_ENERGIZED_COPPER
                        ))).
                save(advancementOutput, new ResourceLocation(EnergizedPowerMod.MODID,
                        "main/advanced/energizedpower_advanced"));

        AdvancementHolder advancedAlloyIngot = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                ModItems.ADVANCED_ALLOY_INGOT, "advanced_alloy_ingot", FrameType.TASK,
                CommonItemTags.INGOTS_ADVANCED_ALLOY
        );

        AdvancementHolder advancedAlloyPlate = addAdvancement(
                advancementOutput, existingFileHelper, advancedAlloyIngot,
                ModItems.ADVANCED_ALLOY_PLATE, "advanced_alloy_plate", FrameType.TASK,
                CommonItemTags.PLATES_ADVANCED_ALLOY
        );

        AdvancementHolder energizedCopperCable = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                ModBlocks.ENERGIZED_COPPER_CABLE_ITEM, "energized_copper_cable", FrameType.TASK
        );

        AdvancementHolder advancedSolarCell = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                ModItems.ADVANCED_SOLAR_CELL, "advanced_solar_cell", FrameType.TASK
        );

        AdvancementHolder solarPanel4 = addAdvancement(
                advancementOutput, existingFileHelper, advancedSolarCell,
                ModBlocks.SOLAR_PANEL_ITEM_4, "solar_panel_4", FrameType.TASK
        );

        AdvancementHolder solarPanel5 = addAdvancement(
                advancementOutput, existingFileHelper, solarPanel4,
                ModBlocks.SOLAR_PANEL_ITEM_5, "solar_panel_5", FrameType.TASK
        );

        AdvancementHolder battery6 = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                ModItems.BATTERY_6, "battery_6", FrameType.TASK
        );

        AdvancementHolder battery7 = addAdvancement(
                advancementOutput, existingFileHelper, battery6,
                ModItems.BATTERY_7, "battery_7", FrameType.TASK
        );

        AdvancementHolder battery8 = addAdvancement(
                advancementOutput, existingFileHelper, battery7,
                ModItems.BATTERY_8, "battery_8", FrameType.GOAL
        );

        ItemStack battery8FullyChargedIcon = new ItemStack(ModItems.BATTERY_8.get());
        CompoundTag nbt = battery8FullyChargedIcon.getOrCreateTag();
        nbt.putInt("energy", BatteryItem.Tier.BATTERY_8.getCapacity());
        AdvancementHolder battery8FullyCharged = addAdvancement(
                advancementOutput, existingFileHelper, battery8,
                battery8FullyChargedIcon, "battery_8_fully_charged", FrameType.CHALLENGE,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().
                        of(ModItems.BATTERY_8.get()).
                        hasNbt(nbt.copy()).
                        build())
        );

        AdvancementHolder advancedBatteryBox = addAdvancement(
                advancementOutput, existingFileHelper, battery8,
                ModBlocks.ADVANCED_BATTERY_BOX_ITEM, "advanced_battery_box", FrameType.TASK
        );

        AdvancementHolder advancedBatteryBoxMinecart = addAdvancement(
                advancementOutput, existingFileHelper, advancedBatteryBox,
                ModItems.ADVANCED_BATTERY_BOX_MINECART, "advanced_battery_box_minecart", FrameType.TASK
        );

        AdvancementHolder energizedCopperPlate = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                ModItems.ENERGIZED_COPPER_PLATE, "energized_copper_plate", FrameType.TASK,
                CommonItemTags.PLATES_ENERGIZED_COPPER
        );

        AdvancementHolder energizedCopperWire = addAdvancement(
                advancementOutput, existingFileHelper, energizedCopperPlate,
                ModItems.ENERGIZED_COPPER_WIRE, "energized_copper_wire", FrameType.TASK,
                CommonItemTags.WIRES_ENERGIZED_COPPER
        );

        AdvancementHolder advancedCircuit = addAdvancement(
                advancementOutput, existingFileHelper, energizedCopperWire,
                ModItems.ADVANCED_CIRCUIT, "advanced_circuit", FrameType.TASK
        );

        AdvancementHolder advancedUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, advancedCircuit,
                ModItems.ADVANCED_UPGRADE_MODULE, "advanced_upgrade_module", FrameType.TASK
        );

        AdvancementHolder speedUpgradeUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                ModItems.SPEED_UPGRADE_MODULE_3, "speed_upgrade_module_3", FrameType.TASK
        );

        AdvancementHolder speedUpgradeUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, speedUpgradeUpgradeModule3,
                ModItems.SPEED_UPGRADE_MODULE_4, "speed_upgrade_module_4", FrameType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_3, "energy_efficiency_upgrade_module_3", FrameType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, energyEfficiencyUpgradeModule3,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_4, "energy_efficiency_upgrade_module_4", FrameType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_3, "energy_capacity_upgrade_module_3", FrameType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, energyCapacityUpgradeModule3,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_4, "energy_capacity_upgrade_module_4", FrameType.TASK
        );

        AdvancementHolder rangeUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                ModItems.RANGE_UPGRADE_MODULE_1, "range_upgrade_module_1", FrameType.TASK
        );

        AdvancementHolder rangeUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, rangeUpgradeModule1,
                ModItems.RANGE_UPGRADE_MODULE_2, "range_upgrade_module_2", FrameType.TASK
        );

        AdvancementHolder rangeUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, rangeUpgradeModule2,
                ModItems.RANGE_UPGRADE_MODULE_3, "range_upgrade_module_3", FrameType.TASK
        );

        AdvancementHolder extractionDepthUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_3, "extraction_depth_upgrade_module_3", FrameType.TASK
        );

        AdvancementHolder extractionDepthUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, extractionDepthUpgradeModule3,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_4, "extraction_depth_upgrade_module_4", FrameType.TASK
        );

        AdvancementHolder moonLightUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, advancedUpgradeModule,
                ModItems.MOON_LIGHT_UPGRADE_MODULE_2, "moon_light_upgrade_module_2", FrameType.TASK
        );

        AdvancementHolder advancedMachineFrame = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerAdvanced,
                ModBlocks.ADVANCED_MACHINE_FRAME_ITEM, "advanced_machine_frame", FrameType.TASK
        );

        AdvancementHolder hvTransformers = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM, "hv_transformers", FrameType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                ModBlocks.HV_TRANSFORMER_1_TO_N_ITEM.get(),
                                ModBlocks.HV_TRANSFORMER_3_TO_3_ITEM.get(),
                                ModBlocks.HV_TRANSFORMER_N_TO_1_ITEM.get()
                        ).build()
                )
        );

        AdvancementHolder advancedCrusher = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                ModBlocks.ADVANCED_CRUSHER_ITEM, "advanced_crusher", FrameType.TASK
        );

        AdvancementHolder advancedPulverizer = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                ModBlocks.ADVANCED_PULVERIZER_ITEM, "advanced_pulverizer", FrameType.TASK
        );

        AdvancementHolder lightningGenerator = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                ModBlocks.LIGHTNING_GENERATOR_ITEM, "lightning_generator", FrameType.TASK
        );

        AdvancementHolder crystalGrowthChamber = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                ModBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM, "crystal_growth_chamber", FrameType.TASK
        );

        AdvancementHolder energizer = addAdvancement(
                advancementOutput, existingFileHelper, advancedMachineFrame,
                ModBlocks.ENERGIZER_ITEM, "energizer", FrameType.TASK
        );

        AdvancementHolder energizedGoldIngot = addAdvancement(
                advancementOutput, existingFileHelper, energizer,
                ModItems.ENERGIZED_GOLD_INGOT, "energized_gold_ingot", FrameType.TASK,
                CommonItemTags.INGOTS_ENERGIZED_GOLD
        );

        AdvancementHolder energizedGoldCable = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModBlocks.ENERGIZED_GOLD_CABLE_ITEM, "energized_gold_cable", FrameType.TASK
        );

        AdvancementHolder energizedGoldPlate = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModItems.ENERGIZED_GOLD_PLATE, "energized_gold_plate", FrameType.TASK,
                CommonItemTags.PLATES_ENERGIZED_GOLD
        );

        AdvancementHolder energizedGoldWire = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldPlate,
                ModItems.ENERGIZED_GOLD_WIRE, "energized_gold_wire", FrameType.TASK,
                CommonItemTags.WIRES_ENERGIZED_GOLD
        );

        AdvancementHolder processingUnit = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldWire,
                ModItems.PROCESSING_UNIT, "processing_unit", FrameType.TASK
        );

        AdvancementHolder reinforcedAdvancedUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, processingUnit,
                ModItems.REINFORCED_ADVANCED_UPGRADE_MODULE, "reinforced_advanced_upgrade_module", FrameType.TASK
        );

        AdvancementHolder speedUpgradeUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                ModItems.SPEED_UPGRADE_MODULE_5, "speed_upgrade_module_5", FrameType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_5, "energy_efficiency_upgrade_module_5", FrameType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_5, "energy_capacity_upgrade_module_5", FrameType.TASK
        );

        AdvancementHolder durationUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                ModItems.DURATION_UPGRADE_MODULE_1, "duration_upgrade_module_1", FrameType.TASK
        );

        AdvancementHolder durationUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule1,
                ModItems.DURATION_UPGRADE_MODULE_2, "duration_upgrade_module_2", FrameType.TASK
        );

        AdvancementHolder durationUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule2,
                ModItems.DURATION_UPGRADE_MODULE_3, "duration_upgrade_module_3", FrameType.TASK
        );

        AdvancementHolder durationUpgradeModule4 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule3,
                ModItems.DURATION_UPGRADE_MODULE_4, "duration_upgrade_module_4", FrameType.TASK
        );

        AdvancementHolder durationUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule4,
                ModItems.DURATION_UPGRADE_MODULE_5, "duration_upgrade_module_5", FrameType.TASK
        );

        AdvancementHolder durationUpgradeModule6 = addAdvancement(
                advancementOutput, existingFileHelper, durationUpgradeModule5,
                ModItems.DURATION_UPGRADE_MODULE_6, "duration_upgrade_module_6", FrameType.CHALLENGE
        );

        AdvancementHolder extractionDepthUpgradeModule5 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_5, "extraction_depth_upgrade_module_5", FrameType.TASK
        );

        AdvancementHolder moonLightUpgradeModule3 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedUpgradeModule,
                ModItems.MOON_LIGHT_UPGRADE_MODULE_3, "moon_light_upgrade_module_3", FrameType.TASK
        );

        AdvancementHolder advancedAutoCrafter = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModBlocks.ADVANCED_AUTO_CRAFTER_ITEM, "advanced_auto_crafter", FrameType.TASK
        );

        AdvancementHolder advancedPoweredFurnace = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModBlocks.ADVANCED_POWERED_FURNACE_ITEM, "advanced_powered_furnace", FrameType.TASK
        );

        AdvancementHolder chargingStation = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModBlocks.CHARGING_STATION_ITEM, "charging_station", FrameType.TASK
        );

        AdvancementHolder advancedCharger = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModBlocks.ADVANCED_CHARGER_ITEM, "advanced_charger", FrameType.TASK
        );

        AdvancementHolder advancedMinecartCharger = addAdvancement(
                advancementOutput, existingFileHelper, advancedCharger,
                ModBlocks.ADVANCED_MINECART_CHARGER_ITEM, "advanced_minecart_charger", FrameType.TASK
        );

        AdvancementHolder advancedUncharger = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModBlocks.ADVANCED_UNCHARGER_ITEM, "advanced_uncharger", FrameType.TASK
        );

        AdvancementHolder advancedMinecartUncharger = addAdvancement(
                advancementOutput, existingFileHelper, advancedUncharger,
                ModBlocks.ADVANCED_MINECART_UNCHARGER_ITEM, "advanced_minecart_uncharger", FrameType.TASK
        );

        AdvancementHolder energizedCrystalMatrix = addAdvancement(
                advancementOutput, existingFileHelper, energizedGoldIngot,
                ModItems.ENERGIZED_CRYSTAL_MATRIX, "energized_crystal_matrix", FrameType.TASK
        );

        AdvancementHolder teleporterMatrix = addAdvancement(
                advancementOutput, existingFileHelper, energizedCrystalMatrix,
                ModItems.TELEPORTER_MATRIX, "teleporter_matrix", FrameType.TASK
        );

        AdvancementHolder teleporterProcessingUnit = addAdvancement(
                advancementOutput, existingFileHelper, teleporterMatrix,
                ModItems.TELEPORTER_PROCESSING_UNIT, "teleporter_processing_unit", FrameType.TASK
        );

        AdvancementHolder energizedCrystalMatrixCable = addAdvancement(
                advancementOutput, existingFileHelper, energizedCrystalMatrix,
                ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM, "energized_crystal_matrix_cable", FrameType.TASK
        );

        AdvancementHolder reinforcedAdvancedSolarCell = addAdvancement(
                advancementOutput, existingFileHelper, energizedCrystalMatrix,
                ModItems.REINFORCED_ADVANCED_SOLAR_CELL, "reinforced_advanced_solar_cell", FrameType.TASK
        );

        AdvancementHolder solarPanel6 = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedSolarCell,
                ModBlocks.SOLAR_PANEL_ITEM_6, "solar_panel_6", FrameType.CHALLENGE
        );

        AdvancementHolder reinforcedAdvancedMachineFrame = addAdvancement(
                advancementOutput, existingFileHelper, energizedCrystalMatrix,
                ModBlocks.REINFORCED_ADVANCED_MACHINE_FRAME_ITEM, "reinforced_advanced_machine_frame", FrameType.TASK
        );

        AdvancementHolder ehvTransformers = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedMachineFrame,
                ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM, "ehv_transformers", FrameType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                ModBlocks.EHV_TRANSFORMER_1_TO_N_ITEM.get(),
                                ModBlocks.EHV_TRANSFORMER_3_TO_3_ITEM.get(),
                                ModBlocks.EHV_TRANSFORMER_N_TO_1_ITEM.get()
                        ).build()
                )
        );

        AdvancementHolder weatherController = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedMachineFrame,
                ModBlocks.WEATHER_CONTROLLER_ITEM, "weather_controller", FrameType.TASK
        );

        AdvancementHolder timeController = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedMachineFrame,
                ModBlocks.TIME_CONTROLLER_ITEM, "time_controller", FrameType.TASK
        );

        AdvancementHolder teleporter = addAdvancement(
                advancementOutput, existingFileHelper, reinforcedAdvancedMachineFrame,
                ModBlocks.TELEPORTER_ITEM, "teleporter", FrameType.TASK
        );

        AdvancementHolder inventoryTeleporter = addAdvancement(
                advancementOutput, existingFileHelper, teleporter,
                ModItems.INVENTORY_TELEPORTER, "inventory_teleporter", FrameType.TASK
        );
    }

    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, RegistryObject<Item> icon, String advancementId, FrameType type) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon.get(), advancementId, type);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, FrameType type) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon, advancementId, type, icon);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, RegistryObject<Item> icon, String advancementId, FrameType type,
                                             RegistryObject<Item> trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon.get(), advancementId, type, trigger.get());
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, FrameType type,
                                             ItemLike trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, RegistryObject<Item> icon, String advancementId, FrameType type,
                                             TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon.get(), advancementId, type, trigger);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, FrameType type,
                                             TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        trigger
                )));
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, RegistryObject<Item> icon, String advancementId, FrameType type,
                                             Criterion<?> trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon.get(), advancementId, type, trigger);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, FrameType type,
                                             Criterion<?> trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type, trigger);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemStack icon, String advancementId, FrameType type,
                                             Criterion<?> trigger) {
        return Advancement.Builder.advancement().parent(parent).
                display(
                        icon,
                        Component.translatable("advancements.energizedpower." + advancementId + ".title"),
                        Component.translatable("advancements.energizedpower." + advancementId + ".description"),
                        null,
                        type,
                        true,
                        true,
                        false
                ).
                addCriterion("has_the_item", trigger).
                save(advancementOutput, new ResourceLocation(EnergizedPowerMod.MODID,
                        "main/advanced/" + advancementId));
    }
}
