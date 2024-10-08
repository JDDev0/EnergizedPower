package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class ModBasicsAdvancements implements AdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider lookupProvider, Consumer<AdvancementHolder> advancementOutput,
                         ExistingFileHelper existingFileHelper) {
        AdvancementHolder energizedPowerBasics = Advancement.Builder.advancement().
                display(
                        Items.COPPER_INGOT,
                        Component.translatable("advancements.energizedpower.energizedpower_basics.title"),
                        Component.translatable("advancements.energizedpower.energizedpower_basics.description"),
                        EPAPI.id("textures/block/basic_machine_frame_top.png"),
                        FrameType.TASK,
                        true,
                        false,
                        false
                ).
                addCriterion("has_the_item",
                        InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                                Tags.Items.INGOTS_COPPER
                        ))).
                save(advancementOutput, EPAPI.id(        "main/basics/energizedpower_basics"), existingFileHelper);

        AdvancementHolder energizedPowerBook = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EPItems.ENERGIZED_POWER_BOOK, "energized_power_book", FrameType.TASK
        );

        AdvancementHolder pressMoldMaker = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EPBlocks.PRESS_MOLD_MAKER_ITEM, "press_mold_maker", FrameType.TASK
        );

        AdvancementHolder rawPressMolds = addAdvancement(
                advancementOutput, existingFileHelper, pressMoldMaker,
                EPItems.RAW_GEAR_PRESS_MOLD, "raw_press_molds", FrameType.TASK,
                EnergizedPowerItemTags.RAW_METAL_PRESS_MOLDS
        );

        AdvancementHolder pressMolds = addAdvancement(
                advancementOutput, existingFileHelper, rawPressMolds,
                EPItems.GEAR_PRESS_MOLD, "press_molds", FrameType.TASK,
                EnergizedPowerItemTags.METAL_PRESS_MOLDS
        );

        AdvancementHolder alloyFurnace = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EPBlocks.ALLOY_FURNACE_ITEM, "alloy_furnace", FrameType.TASK
        );

        AdvancementHolder steelIngot = addAdvancement(
                advancementOutput, existingFileHelper, alloyFurnace,
                EPItems.STEEL_INGOT, "steel_ingot", FrameType.TASK,
                CommonItemTags.INGOTS_STEEL
        );

        AdvancementHolder redstoneAlloyIngot = addAdvancement(
                advancementOutput, existingFileHelper, alloyFurnace,
                EPItems.REDSTONE_ALLOY_INGOT, "redstone_alloy_ingot", FrameType.TASK,
                CommonItemTags.INGOTS_REDSTONE_ALLOY
        );

        AdvancementHolder wrench = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EPItems.WRENCH, "wrench", FrameType.TASK
        );

        AdvancementHolder hammer = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EPItems.IRON_HAMMER, "hammer", FrameType.TASK,
                CommonItemTags.TOOLS_HAMMERS
        );

        AdvancementHolder tinPlate = addAdvancement(
                advancementOutput, existingFileHelper, hammer,
                EPItems.TIN_PLATE, "tin_plate", FrameType.TASK,
                CommonItemTags.PLATES_TIN
        );

        AdvancementHolder copperPlate = addAdvancement(
                advancementOutput, existingFileHelper, hammer,
                EPItems.COPPER_PLATE, "copper_plate", FrameType.TASK,
                CommonItemTags.PLATES_COPPER
        );

        AdvancementHolder goldPlate = addAdvancement(
                advancementOutput, existingFileHelper, hammer,
                EPItems.GOLD_PLATE, "gold_plate", FrameType.TASK,
                CommonItemTags.PLATES_GOLD
        );

        AdvancementHolder ironPlate = addAdvancement(
                advancementOutput, existingFileHelper, hammer,
                EPItems.IRON_PLATE, "iron_plate", FrameType.TASK,
                CommonItemTags.PLATES_IRON
        );

        AdvancementHolder cutter = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                EPItems.CUTTER, "cutter", FrameType.TASK,
                CommonItemTags.TOOLS_CUTTERS
        );

        AdvancementHolder tinWire = addAdvancement(
                advancementOutput, existingFileHelper, cutter,
                EPItems.TIN_WIRE, "tin_wire", FrameType.TASK,
                CommonItemTags.WIRES_TIN
        );

        AdvancementHolder goldWire = addAdvancement(
                advancementOutput, existingFileHelper, cutter,
                EPItems.GOLD_WIRE, "gold_wire", FrameType.TASK,
                CommonItemTags.WIRES_GOLD
        );

        AdvancementHolder copperWire = addAdvancement(
                advancementOutput, existingFileHelper, cutter,
                EPItems.COPPER_WIRE, "copper_wire", FrameType.TASK,
                CommonItemTags.WIRES_COPPER
        );

        AdvancementHolder basicCircuit = addAdvancement(
                advancementOutput, existingFileHelper, copperWire,
                EPItems.BASIC_CIRCUIT, "basic_circuit", FrameType.TASK
        );

        AdvancementHolder basicUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, basicCircuit,
                EPItems.BASIC_UPGRADE_MODULE, "basic_upgrade_module", FrameType.TASK
        );

        AdvancementHolder speedUpgradeUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EPItems.SPEED_UPGRADE_MODULE_1, "speed_upgrade_module_1", FrameType.TASK
        );

        AdvancementHolder speedUpgradeUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, speedUpgradeUpgradeModule1,
                EPItems.SPEED_UPGRADE_MODULE_2, "speed_upgrade_module_2", FrameType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1, "energy_efficiency_upgrade_module_1", FrameType.TASK
        );

        AdvancementHolder energyEfficiencyUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, energyEfficiencyUpgradeModule1,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2, "energy_efficiency_upgrade_module_2", FrameType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1, "energy_capacity_upgrade_module_1", FrameType.TASK
        );

        AdvancementHolder energyCapacityUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, energyCapacityUpgradeModule1,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2, "energy_capacity_upgrade_module_2", FrameType.TASK
        );

        AdvancementHolder extractionDepthUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1, "extraction_depth_upgrade_module_1", FrameType.TASK
        );

        AdvancementHolder extractionDepthUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, extractionDepthUpgradeModule1,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2, "extraction_depth_upgrade_module_2", FrameType.TASK
        );

        AdvancementHolder blastFurnaceUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EPItems.BLAST_FURNACE_UPGRADE_MODULE, "blast_furnace_upgrade_module", FrameType.TASK
        );

        AdvancementHolder smokerUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EPItems.SMOKER_UPGRADE_MODULE, "smoker_upgrade_module", FrameType.TASK
        );

        AdvancementHolder moonLightUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                EPItems.MOON_LIGHT_UPGRADE_MODULE_1, "moon_light_upgrade_module_1", FrameType.TASK
        );

        AdvancementHolder itemConveyorBelt = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                EPBlocks.ITEM_CONVEYOR_BELT_ITEM, "item_conveyor_belt", FrameType.TASK
        );

        AdvancementHolder itemConveyorBeltLoader = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBelt,
                EPBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM, "item_conveyor_belt_loader", FrameType.TASK
        );

        AdvancementHolder itemConveyorBeltSorter = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBeltLoader,
                EPBlocks.ITEM_CONVEYOR_BELT_SORTER_ITEM, "item_conveyor_belt_sorter", FrameType.TASK
        );

        AdvancementHolder itemConveyorBeltSwitch = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBeltLoader,
                EPBlocks.ITEM_CONVEYOR_BELT_SWITCH_ITEM, "item_conveyor_belt_switch", FrameType.TASK
        );

        AdvancementHolder itemConveyorBeltSplitter = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBeltLoader,
                EPBlocks.ITEM_CONVEYOR_BELT_SPLITTER_ITEM, "item_conveyor_belt_splitter", FrameType.TASK
        );

        AdvancementHolder itemConveyorBeltMerger = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBeltLoader,
                EPBlocks.ITEM_CONVEYOR_BELT_MERGER_ITEM, "item_conveyor_belt_merger", FrameType.TASK
        );

        AdvancementHolder ironFluidPipe = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                EPBlocks.IRON_FLUID_PIPE_ITEM, "iron_fluid_pipe", FrameType.TASK
        );

        AdvancementHolder goldenFluidPipe = addAdvancement(
                advancementOutput, existingFileHelper, ironFluidPipe,
                EPBlocks.GOLDEN_FLUID_PIPE_ITEM, "golden_fluid_pipe", FrameType.TASK
        );

        AdvancementHolder fluidTankSmall = addAdvancement(
                advancementOutput, existingFileHelper, ironFluidPipe,
                EPBlocks.FLUID_TANK_SMALL_ITEM, "fluid_tank_small", FrameType.TASK
        );

        AdvancementHolder fluidTankMedium = addAdvancement(
                advancementOutput, existingFileHelper, fluidTankSmall,
                EPBlocks.FLUID_TANK_MEDIUM_ITEM, "fluid_tank_medium", FrameType.TASK
        );

        AdvancementHolder fluidTankLarge = addAdvancement(
                advancementOutput, existingFileHelper, fluidTankMedium,
                EPBlocks.FLUID_TANK_LARGE_ITEM, "fluid_tank_large", FrameType.TASK
        );

        AdvancementHolder drain = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                EPBlocks.DRAIN_ITEM, "drain", FrameType.TASK
        );

        AdvancementHolder cableInsulator = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EPItems.CABLE_INSULATOR, "cable_insulator", FrameType.TASK
        );

        AdvancementHolder tinCable = addAdvancement(
                advancementOutput, existingFileHelper, cableInsulator,
                EPBlocks.TIN_CABLE_ITEM, "tin_cable", FrameType.TASK
        );

        AdvancementHolder copperCable = addAdvancement(
                advancementOutput, existingFileHelper, tinCable,
                EPBlocks.COPPER_CABLE_ITEM, "copper_cable", FrameType.TASK
        );

        AdvancementHolder goldCable = addAdvancement(
                advancementOutput, existingFileHelper, copperCable,
                EPBlocks.GOLD_CABLE_ITEM, "gold_cable", FrameType.TASK
        );

        AdvancementHolder lvTransformers = addAdvancement(
                advancementOutput, existingFileHelper, tinCable,
                EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM, "lv_transformers", FrameType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM,
                                EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM,
                                EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM
                        ).build()
                )
        );

        AdvancementHolder silicon = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                EPItems.SILICON, "silicon", FrameType.TASK,
                CommonItemTags.SILICON
        );

        AdvancementHolder poweredLamp = addAdvancement(
                advancementOutput, existingFileHelper, silicon,
                EPBlocks.POWERED_LAMP_ITEM, "powered_lamp", FrameType.TASK
        );

        AdvancementHolder basicSolarCell = addAdvancement(
                advancementOutput, existingFileHelper, silicon,
                EPItems.BASIC_SOLAR_CELL, "basic_solar_cell", FrameType.TASK
        );

        AdvancementHolder solarPanel1 = addAdvancement(
                advancementOutput, existingFileHelper, basicSolarCell,
                EPBlocks.SOLAR_PANEL_ITEM_1, "solar_panel_1", FrameType.TASK
        );

        AdvancementHolder solarPanel2 = addAdvancement(
                advancementOutput, existingFileHelper, solarPanel1,
                EPBlocks.SOLAR_PANEL_ITEM_2, "solar_panel_2", FrameType.TASK
        );

        AdvancementHolder solarPanel3 = addAdvancement(
                advancementOutput, existingFileHelper, solarPanel2,
                EPBlocks.SOLAR_PANEL_ITEM_3, "solar_panel_3", FrameType.TASK
        );

        AdvancementHolder basicMachineFrame = addAdvancement(
                advancementOutput, existingFileHelper, silicon,
                EPBlocks.BASIC_MACHINE_FRAME_ITEM, "basic_machine_frame", FrameType.TASK
        );

        AdvancementHolder autoCrafter = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.AUTO_CRAFTER_ITEM, "auto_crafter", FrameType.TASK
        );

        AdvancementHolder crusher = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.CRUSHER_ITEM, "crusher", FrameType.TASK
        );

        AdvancementHolder sawmill = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.SAWMILL_ITEM, "sawmill", FrameType.TASK
        );

        AdvancementHolder compressor = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.COMPRESSOR_ITEM, "compressor", FrameType.TASK
        );

        AdvancementHolder autoStonecutter = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.AUTO_STONECUTTER_ITEM, "auto_stonecutter", FrameType.TASK
        );

        AdvancementHolder plantGrowthChamber = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.PLANT_GROWTH_CHAMBER_ITEM, "plant_growth_chamber", FrameType.TASK
        );

        AdvancementHolder blockPlacer = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.BLOCK_PLACER_ITEM, "block_placer", FrameType.TASK
        );

        AdvancementHolder fluidFiller = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.FLUID_FILLER_ITEM, "fluid_filler", FrameType.TASK
        );

        AdvancementHolder fluidDrainer = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.FLUID_DRAINER_ITEM, "fluid_drainer", FrameType.TASK
        );

        AdvancementHolder fluidPump = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.FLUID_PUMP_ITEM, "fluid_pump", FrameType.TASK
        );

        AdvancementHolder poweredFurnace = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.POWERED_FURNACE_ITEM, "powered_furnace", FrameType.TASK
        );

        AdvancementHolder pulverizer = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.PULVERIZER_ITEM, "pulverizer", FrameType.TASK
        );

        AdvancementHolder charcoalFilter = addAdvancement(
                advancementOutput, existingFileHelper, pulverizer,
                EPItems.CHARCOAL_FILTER, "charcoal_filter", FrameType.TASK
        );

        AdvancementHolder coalEngine = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.COAL_ENGINE_ITEM, "coal_engine", FrameType.TASK
        );

        ItemStack inventoryCoalEngineIcon = new ItemStack(EPItems.INVENTORY_COAL_ENGINE.get());
        CompoundTag nbt = inventoryCoalEngineIcon.getOrCreateTag();
        nbt.putBoolean("active", true);
        nbt.putBoolean("working", true);
        AdvancementHolder inventoryCoalEngine = addAdvancement(
                advancementOutput, existingFileHelper, coalEngine,
                inventoryCoalEngineIcon, "inventory_coal_engine", FrameType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(EPItems.INVENTORY_COAL_ENGINE)
        );

        AdvancementHolder heatGenerator = addAdvancement(
                advancementOutput, existingFileHelper, coalEngine,
                EPBlocks.HEAT_GENERATOR_ITEM, "heat_generator", FrameType.TASK
        );

        AdvancementHolder charger = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.CHARGER_ITEM, "charger", FrameType.TASK
        );

        AdvancementHolder minecartCharger = addAdvancement(
                advancementOutput, existingFileHelper, charger,
                EPBlocks.MINECART_CHARGER_ITEM, "minecart_charger", FrameType.TASK
        );

        AdvancementHolder uncharger = addAdvancement(
                advancementOutput, existingFileHelper, charger,
                EPBlocks.UNCHARGER_ITEM, "uncharger", FrameType.TASK
        );

        AdvancementHolder minecartUncharger = addAdvancement(
                advancementOutput, existingFileHelper, uncharger,
                EPBlocks.MINECART_UNCHARGER_ITEM, "minecart_uncharger", FrameType.TASK
        );

        AdvancementHolder inventoryCharger = addAdvancement(
                advancementOutput, existingFileHelper, charger,
                EPItems.INVENTORY_CHARGER, "inventory_charger", FrameType.TASK
        );

        AdvancementHolder battery1 = addAdvancement(
                advancementOutput, existingFileHelper, charger,
                EPItems.BATTERY_1, "battery_1", FrameType.TASK
        );

        AdvancementHolder battery2 = addAdvancement(
                advancementOutput, existingFileHelper, battery1,
                EPItems.BATTERY_2, "battery_2", FrameType.TASK
        );

        AdvancementHolder battery3 = addAdvancement(
                advancementOutput, existingFileHelper, battery2,
                EPItems.BATTERY_3, "battery_3", FrameType.TASK
        );

        AdvancementHolder energyAnalyzer = addAdvancement(
                advancementOutput, existingFileHelper, battery3,
                EPItems.ENERGY_ANALYZER, "energy_analyzer", FrameType.TASK
        );

        AdvancementHolder fluidAnalyzer = addAdvancement(
                advancementOutput, existingFileHelper, battery3,
                EPItems.FLUID_ANALYZER, "fluid_analyzer", FrameType.TASK
        );

        AdvancementHolder battery4 = addAdvancement(
                advancementOutput, existingFileHelper, battery3,
                EPItems.BATTERY_4, "battery_4", FrameType.TASK
        );

        AdvancementHolder battery5 = addAdvancement(
                advancementOutput, existingFileHelper, battery4,
                EPItems.BATTERY_5, "battery_5", FrameType.TASK
        );

        AdvancementHolder batteryBox = addAdvancement(
                advancementOutput, existingFileHelper, battery5,
                EPBlocks.BATTERY_BOX_ITEM, "battery_box", FrameType.TASK
        );

        AdvancementHolder batteryBoxMinecart = addAdvancement(
                advancementOutput, existingFileHelper, batteryBox,
                EPItems.BATTERY_BOX_MINECART, "battery_box_minecart", FrameType.TASK
        );

        AdvancementHolder metalPress = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                EPBlocks.METAL_PRESS_ITEM, "metal_press", FrameType.TASK
        );

        AdvancementHolder autoPressMoldMaker = addAdvancement(
                advancementOutput, existingFileHelper, metalPress,
                EPBlocks.AUTO_PRESS_MOLD_MAKER, "auto_press_mold_maker", FrameType.TASK
        );

        AdvancementHolder hardenedMachineFrame = addAdvancement(
                advancementOutput, existingFileHelper, metalPress,
                EPBlocks.HARDENED_MACHINE_FRAME_ITEM, "hardened_machine_frame", FrameType.TASK
        );

        AdvancementHolder mvTransformers = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EPBlocks.MV_TRANSFORMER_1_TO_N, "mv_transformers", FrameType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM,
                                EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM,
                                EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM
                        ).build()
                )
        );

        AdvancementHolder assemblingMachine = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EPBlocks.ASSEMBLING_MACHINE_ITEM, "assembling_machine", FrameType.TASK
        );

        AdvancementHolder inductionSmelter = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EPBlocks.INDUCTION_SMELTER_ITEM, "induction_smelter", FrameType.TASK
        );

        AdvancementHolder stoneSolidifier = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EPBlocks.STONE_SOLIDIFIER_ITEM, "stone_solidifier", FrameType.TASK
        );

        AdvancementHolder fluidTransposer = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EPBlocks.FLUID_TRANSPOSER_ITEM, "fluid_transposer", FrameType.TASK
        );

        AdvancementHolder filtrationPlant = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EPBlocks.FILTRATION_PLANT_ITEM, "filtration_plant", FrameType.TASK
        );

        AdvancementHolder thermalGenerator = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                EPBlocks.THERMAL_GENERATOR_ITEM, "thermal_generator", FrameType.TASK
        );
    }

    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, FrameType type) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon, advancementId, type, icon);
    }
    private AdvancementHolder addAdvancement(Consumer<AdvancementHolder> advancementOutput, ExistingFileHelper existingFileHelper,
                                             AdvancementHolder parent, ItemLike icon, String advancementId, FrameType type,
                                             ItemLike trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
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
                save(advancementOutput, EPAPI.id("main/basics/" + advancementId), existingFileHelper);
    }
}
