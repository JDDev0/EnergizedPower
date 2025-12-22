package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Consumer;

public class ModBasicsAdvancements implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput) {
        AdvancementHolder energizedPowerBasics = Advancement.Builder.advancement().
                display(
                        Items.COPPER_INGOT,
                        Component.translatable("advancements.energizedpower.energizedpower_basics.title"),
                        Component.translatable("advancements.energizedpower.energizedpower_basics.description"),
                        EPAPI.id("block/basic_machine_frame_top"),
                        AdvancementType.TASK,
                        true,
                        false,
                        false
                ).
                addCriterion("has_the_item",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                                lookupProvider.lookupOrThrow(Registries.ITEM),
                                Tags.Items.INGOTS_COPPER
                        ))).
                save(advancementOutput, EPAPI.id("main/basics/energizedpower_basics"));

        AdvancementHolder energizedPowerBook = addAdvancement(
                advancementOutput, energizedPowerBasics,
                EPItems.ENERGIZED_POWER_BOOK, "energized_power_book", AdvancementType.TASK
        );

        AdvancementHolder pressMoldMaker = addAdvancement(
                advancementOutput, energizedPowerBasics,
                EPBlocks.PRESS_MOLD_MAKER_ITEM, "press_mold_maker", AdvancementType.TASK
        );

        AdvancementHolder rawPressMolds = addAdvancement(
                lookupProvider,
                advancementOutput, pressMoldMaker,
                EPItems.RAW_GEAR_PRESS_MOLD, "raw_press_molds", AdvancementType.TASK,
                EnergizedPowerItemTags.RAW_METAL_PRESS_MOLDS
        );

        AdvancementHolder pressMolds = addAdvancement(
                lookupProvider,
                advancementOutput, rawPressMolds,
                EPItems.GEAR_PRESS_MOLD, "press_molds", AdvancementType.TASK,
                EnergizedPowerItemTags.METAL_PRESS_MOLDS
        );

        AdvancementHolder alloyFurnace = addAdvancement(
                advancementOutput, energizedPowerBasics,
                EPBlocks.ALLOY_FURNACE_ITEM, "alloy_furnace", AdvancementType.TASK
        );

        AdvancementHolder steelIngot = addAdvancement(
                lookupProvider,
                advancementOutput, alloyFurnace,
                EPItems.STEEL_INGOT, "steel_ingot", AdvancementType.TASK,
                CommonItemTags.INGOTS_STEEL
        );

        AdvancementHolder fastItemConveyorBelt = addAdvancement(
                advancementOutput, steelIngot,
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_ITEM, "fast_item_conveyor_belt", AdvancementType.TASK
        );

        AdvancementHolder fastItemConveyorBeltLoader = addAdvancement(
                advancementOutput, fastItemConveyorBelt,
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM, "fast_item_conveyor_belt_loader", AdvancementType.TASK
        );

        AdvancementHolder fastItemConveyorBeltSorter = addAdvancement(
                advancementOutput, fastItemConveyorBelt,
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM, "fast_item_conveyor_belt_sorter", AdvancementType.TASK
        );

        AdvancementHolder fastItemConveyorBeltSwitch = addAdvancement(
                advancementOutput, fastItemConveyorBelt,
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM, "fast_item_conveyor_belt_switch", AdvancementType.TASK
        );

        AdvancementHolder fastItemConveyorBeltSplitter = addAdvancement(
                advancementOutput, fastItemConveyorBelt,
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM, "fast_item_conveyor_belt_splitter", AdvancementType.TASK
        );

        AdvancementHolder fastItemConveyorBeltMerger = addAdvancement(
                advancementOutput, fastItemConveyorBelt,
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM, "fast_item_conveyor_belt_merger", AdvancementType.TASK
        );

        AdvancementHolder redstoneAlloyIngot = addAdvancement(
                lookupProvider,
                advancementOutput, alloyFurnace,
                EPItems.REDSTONE_ALLOY_INGOT, "redstone_alloy_ingot", AdvancementType.TASK,
                CommonItemTags.INGOTS_REDSTONE_ALLOY
        );

        AdvancementHolder wrench = addAdvancement(
                advancementOutput, energizedPowerBasics,
                EPItems.WRENCH, "wrench", AdvancementType.TASK
        );

        AdvancementHolder hammer = addAdvancement(
                lookupProvider,
                advancementOutput, energizedPowerBasics,
                EPItems.IRON_HAMMER, "hammer", AdvancementType.TASK,
                CommonItemTags.TOOLS_HAMMERS
        );

        AdvancementHolder tinPlate = addAdvancement(
                lookupProvider,
                advancementOutput, hammer,
                EPItems.TIN_PLATE, "tin_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_TIN
        );

        AdvancementHolder copperPlate = addAdvancement(
                lookupProvider,
                advancementOutput, hammer,
                EPItems.COPPER_PLATE, "copper_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_COPPER
        );

        AdvancementHolder goldPlate = addAdvancement(
                lookupProvider,
                advancementOutput, hammer,
                EPItems.GOLD_PLATE, "gold_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_GOLD
        );

        AdvancementHolder ironPlate = addAdvancement(
                lookupProvider,
                advancementOutput, hammer,
                EPItems.IRON_PLATE, "iron_plate", AdvancementType.TASK,
                CommonItemTags.PLATES_IRON
        );

        AdvancementHolder cutter = addAdvancement(
                lookupProvider,
                advancementOutput, ironPlate,
                EPItems.CUTTER, "cutter", AdvancementType.TASK,
                CommonItemTags.TOOLS_CUTTERS
        );

        AdvancementHolder tinWire = addAdvancement(
                lookupProvider,
                advancementOutput, cutter,
                EPItems.TIN_WIRE, "tin_wire", AdvancementType.TASK,
                CommonItemTags.WIRES_TIN
        );

        AdvancementHolder goldWire = addAdvancement(
                lookupProvider,
                advancementOutput, cutter,
                EPItems.GOLD_WIRE, "gold_wire", AdvancementType.TASK,
                CommonItemTags.WIRES_GOLD
        );

        AdvancementHolder copperWire = addAdvancement(
                lookupProvider,
                advancementOutput, cutter,
                EPItems.COPPER_WIRE, "copper_wire", AdvancementType.TASK,
                CommonItemTags.WIRES_COPPER
        );

        AdvancementHolder basicCircuit = addAdvancement(
                advancementOutput, copperWire,
                EPItems.BASIC_CIRCUIT, "basic_circuit", AdvancementType.TASK
        );

        AdvancementHolder basicUpgradeModule = addAdvancement(
                advancementOutput, basicCircuit,
                EPItems.BASIC_UPGRADE_MODULE, "basic_upgrade_module", AdvancementType.TASK
        );

        AdvancementHolder speedUpgradeUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.SPEED_UPGRADE_MODULE_1, "speed_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder speedUpgradeUpgradeModule2 = addAdvancement(
                advancementOutput, speedUpgradeUpgradeModule1,
                EPItems.SPEED_UPGRADE_MODULE_2, "speed_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1, "energy_efficiency_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule2 = addAdvancement(
                advancementOutput, energyEfficiencyUpgradeModule1,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2, "energy_efficiency_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1, "energy_capacity_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule2 = addAdvancement(
                advancementOutput, energyCapacityUpgradeModule1,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2, "energy_capacity_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder extractionDepthUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1, "extraction_depth_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder extractionDepthUpgradeModule2 = addAdvancement(
                advancementOutput, extractionDepthUpgradeModule1,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2, "extraction_depth_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder extractionRangeUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_1, "extraction_range_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder extractionRangeUpgradeModule2 = addAdvancement(
                advancementOutput, extractionRangeUpgradeModule1,
                EPItems.EXTRACTION_RANGE_UPGRADE_MODULE_2, "extraction_range_upgrade_module_2", AdvancementType.TASK
        );

        AdvancementHolder blastFurnaceUpgradeModule = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.BLAST_FURNACE_UPGRADE_MODULE, "blast_furnace_upgrade_module", AdvancementType.TASK
        );

        AdvancementHolder smokerUpgradeModule = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.SMOKER_UPGRADE_MODULE, "smoker_upgrade_module", AdvancementType.TASK
        );

        AdvancementHolder moonLightUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.MOON_LIGHT_UPGRADE_MODULE_1, "moon_light_upgrade_module_1", AdvancementType.TASK
        );

        AdvancementHolder itemConveyorBelt = addAdvancement(
                advancementOutput, ironPlate,
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_ITEM, "item_conveyor_belt", AdvancementType.TASK
        );

        AdvancementHolder itemConveyorBeltLoader = addAdvancement(
                advancementOutput, itemConveyorBelt,
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM, "item_conveyor_belt_loader", AdvancementType.TASK
        );

        AdvancementHolder itemConveyorBeltSorter = addAdvancement(
                advancementOutput, itemConveyorBeltLoader,
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM, "item_conveyor_belt_sorter", AdvancementType.TASK
        );

        AdvancementHolder itemConveyorBeltSwitch = addAdvancement(
                advancementOutput, itemConveyorBeltLoader,
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM, "item_conveyor_belt_switch", AdvancementType.TASK
        );

        AdvancementHolder itemConveyorBeltSplitter = addAdvancement(
                advancementOutput, itemConveyorBeltLoader,
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM, "item_conveyor_belt_splitter", AdvancementType.TASK
        );

        AdvancementHolder itemConveyorBeltMerger = addAdvancement(
                advancementOutput, itemConveyorBeltLoader,
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM, "item_conveyor_belt_merger", AdvancementType.TASK
        );

        AdvancementHolder itemSiloTiny = addAdvancement(
                advancementOutput, ironPlate,
                EPBlocks.ITEM_SILO_TINY_ITEM, "item_silo_tiny", AdvancementType.TASK
        );

        AdvancementHolder itemSiloSmall = addAdvancement(
                advancementOutput, itemSiloTiny,
                EPBlocks.ITEM_SILO_SMALL_ITEM, "item_silo_small", AdvancementType.TASK
        );

        AdvancementHolder itemSiloMedium = addAdvancement(
                advancementOutput, itemSiloSmall,
                EPBlocks.ITEM_SILO_MEDIUM_ITEM, "item_silo_medium", AdvancementType.TASK
        );

        AdvancementHolder itemSiloLarge = addAdvancement(
                advancementOutput, itemSiloMedium,
                EPBlocks.ITEM_SILO_LARGE_ITEM, "item_silo_large", AdvancementType.TASK
        );

        AdvancementHolder itemSiloGiant = addAdvancement(
                advancementOutput, itemSiloLarge,
                EPBlocks.ITEM_SILO_GIANT_ITEM, "item_silo_giant", AdvancementType.TASK
        );

        AdvancementHolder ironFluidPipe = addAdvancement(
                advancementOutput, ironPlate,
                EPBlocks.IRON_FLUID_PIPE_ITEM, "iron_fluid_pipe", AdvancementType.TASK
        );

        AdvancementHolder goldenFluidPipe = addAdvancement(
                advancementOutput, ironFluidPipe,
                EPBlocks.GOLDEN_FLUID_PIPE_ITEM, "golden_fluid_pipe", AdvancementType.TASK
        );

        AdvancementHolder fluidTankSmall = addAdvancement(
                advancementOutput, ironFluidPipe,
                EPBlocks.FLUID_TANK_SMALL_ITEM, "fluid_tank_small", AdvancementType.TASK
        );

        AdvancementHolder fluidTankMedium = addAdvancement(
                advancementOutput, fluidTankSmall,
                EPBlocks.FLUID_TANK_MEDIUM_ITEM, "fluid_tank_medium", AdvancementType.TASK
        );

        AdvancementHolder fluidTankLarge = addAdvancement(
                advancementOutput, fluidTankMedium,
                EPBlocks.FLUID_TANK_LARGE_ITEM, "fluid_tank_large", AdvancementType.TASK
        );

        AdvancementHolder drain = addAdvancement(
                advancementOutput, ironPlate,
                EPBlocks.DRAIN_ITEM, "drain", AdvancementType.TASK
        );

        AdvancementHolder cableInsulator = addAdvancement(
                advancementOutput, energizedPowerBasics,
                EPItems.CABLE_INSULATOR, "cable_insulator", AdvancementType.TASK
        );

        AdvancementHolder tinCable = addAdvancement(
                advancementOutput, cableInsulator,
                EPBlocks.TIN_CABLE_ITEM, "tin_cable", AdvancementType.TASK
        );

        AdvancementHolder copperCable = addAdvancement(
                advancementOutput, tinCable,
                EPBlocks.COPPER_CABLE_ITEM, "copper_cable", AdvancementType.TASK
        );

        AdvancementHolder goldCable = addAdvancement(
                advancementOutput, copperCable,
                EPBlocks.GOLD_CABLE_ITEM, "gold_cable", AdvancementType.TASK
        );

        AdvancementHolder lvTransformers = addAdvancement(
                advancementOutput, tinCable,
                EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM, "lv_transformers", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                lookupProvider.lookupOrThrow(Registries.ITEM),
                                EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM,
                                EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM,
                                EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM,
                                EPBlocks.CONFIGURABLE_LV_TRANSFORMER_ITEM
                        ).build()
                )
        );

        AdvancementHolder silicon = addAdvancement(
                lookupProvider,
                advancementOutput, energizedPowerBasics,
                EPItems.SILICON, "silicon", AdvancementType.TASK,
                CommonItemTags.SILICON
        );

        AdvancementHolder poweredLamp = addAdvancement(
                advancementOutput, silicon,
                EPBlocks.POWERED_LAMP_ITEM, "powered_lamp", AdvancementType.TASK
        );

        AdvancementHolder basicSolarCell = addAdvancement(
                advancementOutput, silicon,
                EPItems.BASIC_SOLAR_CELL, "basic_solar_cell", AdvancementType.TASK
        );

        AdvancementHolder solarPanel1 = addAdvancement(
                advancementOutput, basicSolarCell,
                EPBlocks.SOLAR_PANEL_ITEM_1, "solar_panel_1", AdvancementType.TASK
        );

        AdvancementHolder solarPanel2 = addAdvancement(
                advancementOutput, solarPanel1,
                EPBlocks.SOLAR_PANEL_ITEM_2, "solar_panel_2", AdvancementType.TASK
        );

        AdvancementHolder solarPanel3 = addAdvancement(
                advancementOutput, solarPanel2,
                EPBlocks.SOLAR_PANEL_ITEM_3, "solar_panel_3", AdvancementType.TASK
        );

        AdvancementHolder basicMachineFrame = addAdvancement(
                advancementOutput, silicon,
                EPBlocks.BASIC_MACHINE_FRAME_ITEM, "basic_machine_frame", AdvancementType.TASK
        );

        AdvancementHolder autoCrafter = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.AUTO_CRAFTER_ITEM, "auto_crafter", AdvancementType.TASK
        );

        AdvancementHolder crusher = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.CRUSHER_ITEM, "crusher", AdvancementType.TASK
        );

        AdvancementHolder sawmill = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.SAWMILL_ITEM, "sawmill", AdvancementType.TASK
        );

        AdvancementHolder compressor = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.COMPRESSOR_ITEM, "compressor", AdvancementType.TASK
        );

        AdvancementHolder autoStonecutter = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.AUTO_STONECUTTER_ITEM, "auto_stonecutter", AdvancementType.TASK
        );

        AdvancementHolder plantGrowthChamber = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.PLANT_GROWTH_CHAMBER_ITEM, "plant_growth_chamber", AdvancementType.TASK
        );

        AdvancementHolder blockPlacer = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.BLOCK_PLACER_ITEM, "block_placer", AdvancementType.TASK
        );

        AdvancementHolder fluidFiller = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.FLUID_FILLER_ITEM, "fluid_filler", AdvancementType.TASK
        );

        AdvancementHolder fluidDrainer = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.FLUID_DRAINER_ITEM, "fluid_drainer", AdvancementType.TASK
        );

        AdvancementHolder fluidPump = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.FLUID_PUMP_ITEM, "fluid_pump", AdvancementType.TASK
        );

        AdvancementHolder poweredFurnace = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.POWERED_FURNACE_ITEM, "powered_furnace", AdvancementType.TASK
        );

        AdvancementHolder pulverizer = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.PULVERIZER_ITEM, "pulverizer", AdvancementType.TASK
        );

        AdvancementHolder charcoalFilter = addAdvancement(
                advancementOutput, pulverizer,
                EPItems.CHARCOAL_FILTER, "charcoal_filter", AdvancementType.TASK
        );

        AdvancementHolder coalEngine = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.COAL_ENGINE_ITEM, "coal_engine", AdvancementType.TASK
        );

        ItemStack inventoryCoalEngineIcon = new ItemStack(EPItems.INVENTORY_COAL_ENGINE.get());
        inventoryCoalEngineIcon.applyComponentsAndValidate(DataComponentPatch.builder().
                set(EPDataComponentTypes.ACTIVE.get(), true).
                set(EPDataComponentTypes.WORKING.get(), true).
                build());
        AdvancementHolder inventoryCoalEngine = addAdvancement(
                advancementOutput, coalEngine,
                inventoryCoalEngineIcon, "inventory_coal_engine", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(EPItems.INVENTORY_COAL_ENGINE)
        );

        AdvancementHolder heatGenerator = addAdvancement(
                advancementOutput, coalEngine,
                EPBlocks.HEAT_GENERATOR_ITEM, "heat_generator", AdvancementType.TASK
        );

        AdvancementHolder charger = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.CHARGER_ITEM, "charger", AdvancementType.TASK
        );

        AdvancementHolder minecartCharger = addAdvancement(
                advancementOutput, charger,
                EPBlocks.MINECART_CHARGER_ITEM, "minecart_charger", AdvancementType.TASK
        );

        AdvancementHolder uncharger = addAdvancement(
                advancementOutput, charger,
                EPBlocks.UNCHARGER_ITEM, "uncharger", AdvancementType.TASK
        );

        AdvancementHolder minecartUncharger = addAdvancement(
                advancementOutput, uncharger,
                EPBlocks.MINECART_UNCHARGER_ITEM, "minecart_uncharger", AdvancementType.TASK
        );

        AdvancementHolder inventoryCharger = addAdvancement(
                advancementOutput, charger,
                EPItems.INVENTORY_CHARGER, "inventory_charger", AdvancementType.TASK
        );

        AdvancementHolder battery1 = addAdvancement(
                advancementOutput, charger,
                EPItems.BATTERY_1, "battery_1", AdvancementType.TASK
        );

        AdvancementHolder battery2 = addAdvancement(
                advancementOutput, battery1,
                EPItems.BATTERY_2, "battery_2", AdvancementType.TASK
        );

        AdvancementHolder battery3 = addAdvancement(
                advancementOutput, battery2,
                EPItems.BATTERY_3, "battery_3", AdvancementType.TASK
        );

        AdvancementHolder energyAnalyzer = addAdvancement(
                advancementOutput, battery3,
                EPItems.ENERGY_ANALYZER, "energy_analyzer", AdvancementType.TASK
        );

        AdvancementHolder fluidAnalyzer = addAdvancement(
                advancementOutput, battery3,
                EPItems.FLUID_ANALYZER, "fluid_analyzer", AdvancementType.TASK
        );

        AdvancementHolder battery4 = addAdvancement(
                advancementOutput, battery3,
                EPItems.BATTERY_4, "battery_4", AdvancementType.TASK
        );

        AdvancementHolder battery5 = addAdvancement(
                advancementOutput, battery4,
                EPItems.BATTERY_5, "battery_5", AdvancementType.TASK
        );

        AdvancementHolder batteryBox = addAdvancement(
                advancementOutput, battery5,
                EPBlocks.BATTERY_BOX_ITEM, "battery_box", AdvancementType.TASK
        );

        AdvancementHolder batteryBoxMinecart = addAdvancement(
                advancementOutput, batteryBox,
                EPItems.BATTERY_BOX_MINECART, "battery_box_minecart", AdvancementType.TASK
        );

        AdvancementHolder metalPress = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.METAL_PRESS_ITEM, "metal_press", AdvancementType.TASK
        );

        AdvancementHolder expressItemConveyorBelt = addAdvancement(
                advancementOutput, metalPress,
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_ITEM, "express_item_conveyor_belt", AdvancementType.TASK
        );

        AdvancementHolder expressItemConveyorBeltLoader = addAdvancement(
                advancementOutput, expressItemConveyorBelt,
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM, "express_item_conveyor_belt_loader", AdvancementType.TASK
        );

        AdvancementHolder expressItemConveyorBeltSorter = addAdvancement(
                advancementOutput, expressItemConveyorBelt,
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ITEM, "express_item_conveyor_belt_sorter", AdvancementType.TASK
        );

        AdvancementHolder expressItemConveyorBeltSwitch = addAdvancement(
                advancementOutput, expressItemConveyorBelt,
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ITEM, "express_item_conveyor_belt_switch", AdvancementType.TASK
        );

        AdvancementHolder expressItemConveyorBeltSplitter = addAdvancement(
                advancementOutput, expressItemConveyorBelt,
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ITEM, "express_item_conveyor_belt_splitter", AdvancementType.TASK
        );

        AdvancementHolder expressItemConveyorBeltMerger = addAdvancement(
                advancementOutput, expressItemConveyorBelt,
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ITEM, "express_item_conveyor_belt_merger", AdvancementType.TASK
        );

        AdvancementHolder autoPressMoldMaker = addAdvancement(
                advancementOutput, metalPress,
                EPBlocks.AUTO_PRESS_MOLD_MAKER, "auto_press_mold_maker", AdvancementType.TASK
        );

        AdvancementHolder hardenedMachineFrame = addAdvancement(
                advancementOutput, metalPress,
                EPBlocks.HARDENED_MACHINE_FRAME_ITEM, "hardened_machine_frame", AdvancementType.TASK
        );

        AdvancementHolder mvTransformers = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.MV_TRANSFORMER_1_TO_N, "mv_transformers", AdvancementType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                lookupProvider.lookupOrThrow(Registries.ITEM),
                                EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM,
                                EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM,
                                EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM,
                                EPBlocks.CONFIGURABLE_MV_TRANSFORMER_ITEM
                        ).build()
                )
        );

        AdvancementHolder assemblingMachine = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.ASSEMBLING_MACHINE_ITEM, "assembling_machine", AdvancementType.TASK
        );

        AdvancementHolder inductionSmelter = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.INDUCTION_SMELTER_ITEM, "induction_smelter", AdvancementType.TASK
        );

        AdvancementHolder stoneLiquefier = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.STONE_LIQUEFIER_ITEM, "stone_liquefier", AdvancementType.TASK
        );

        AdvancementHolder stoneSolidifier = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.STONE_SOLIDIFIER_ITEM, "stone_solidifier", AdvancementType.TASK
        );

        AdvancementHolder fluidTransposer = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.FLUID_TRANSPOSER_ITEM, "fluid_transposer", AdvancementType.TASK
        );

        AdvancementHolder filtrationPlant = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.FILTRATION_PLANT_ITEM, "filtration_plant", AdvancementType.TASK
        );

        AdvancementHolder thermalGenerator = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.THERMAL_GENERATOR_ITEM, "thermal_generator", AdvancementType.TASK
        );
    }

    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type) {
        return addAdvancement(advancementOutput, parent, icon, advancementId, type, icon);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             ItemLike trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    private AdvancementHolder addAdvancement(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        lookupProvider.lookupOrThrow(Registries.ITEM),
                        trigger
                )));
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, AdvancementType type,
                                             Criterion<?> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon), advancementId, type, trigger);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput,
                                             AdvancementHolder parent, ItemStack icon, String advancementId, AdvancementType type,
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
                save(advancementOutput, EPAPI.id("main/basics/" + advancementId));
    }
}
