package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ModBasicsAdvancements extends FabricAdvancementProvider {
    public ModBasicsAdvancements(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateAdvancement(Consumer<AdvancementEntry> advancementOutput) {
        AdvancementEntry energizedPowerBasics = Advancement.Builder.create().
                display(
                        Items.COPPER_INGOT,
                        Text.translatable("advancements.energizedpower.energizedpower_basics.title"),
                        Text.translatable("advancements.energizedpower.energizedpower_basics.description"),
                        EPAPI.id("textures/block/basic_machine_frame_top.png"),
                        AdvancementFrame.TASK,
                        true,
                        false,
                        false
                ).
                criterion("has_the_item",
                        InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                                ConventionalItemTags.COPPER_INGOTS
                        ))).
                build(advancementOutput, EPAPI.MOD_ID + ":main/basics/energizedpower_basics");

        AdvancementEntry energizedPowerBook = addAdvancement(
                advancementOutput, energizedPowerBasics,
                EPItems.ENERGIZED_POWER_BOOK, "energized_power_book", AdvancementFrame.TASK
        );

        AdvancementEntry pressMoldMaker = addAdvancement(
                advancementOutput, energizedPowerBasics,
                EPBlocks.PRESS_MOLD_MAKER_ITEM, "press_mold_maker", AdvancementFrame.TASK
        );

        AdvancementEntry rawPressMolds = addAdvancement(
                advancementOutput, pressMoldMaker,
                EPItems.RAW_GEAR_PRESS_MOLD, "raw_press_molds", AdvancementFrame.TASK,
                EnergizedPowerItemTags.RAW_METAL_PRESS_MOLDS
        );

        AdvancementEntry pressMolds = addAdvancement(
                advancementOutput, rawPressMolds,
                EPItems.GEAR_PRESS_MOLD, "press_molds", AdvancementFrame.TASK,
                EnergizedPowerItemTags.METAL_PRESS_MOLDS
        );

        AdvancementEntry alloyFurnace = addAdvancement(
                advancementOutput, energizedPowerBasics,
                EPBlocks.ALLOY_FURNACE_ITEM, "alloy_furnace", AdvancementFrame.TASK
        );

        AdvancementEntry steelIngot = addAdvancement(
                advancementOutput, alloyFurnace,
                EPItems.STEEL_INGOT, "steel_ingot", AdvancementFrame.TASK,
                CommonItemTags.STEEL_INGOTS
        );

        AdvancementEntry redstoneAlloyIngot = addAdvancement(
                advancementOutput, alloyFurnace,
                EPItems.REDSTONE_ALLOY_INGOT, "redstone_alloy_ingot", AdvancementFrame.TASK,
                CommonItemTags.REDSTONE_ALLOY_INGOTS
        );

        AdvancementEntry wrench = addAdvancement(
                advancementOutput, energizedPowerBasics,
                EPItems.WRENCH, "wrench", AdvancementFrame.TASK
        );

        AdvancementEntry hammer = addAdvancement(
                advancementOutput, energizedPowerBasics,
                EPItems.IRON_HAMMER, "hammer", AdvancementFrame.TASK,
                CommonItemTags.HAMMERS
        );

        AdvancementEntry tinPlate = addAdvancement(
                advancementOutput, hammer,
                EPItems.TIN_PLATE, "tin_plate", AdvancementFrame.TASK,
                CommonItemTags.TIN_PLATES
        );

        AdvancementEntry copperPlate = addAdvancement(
                advancementOutput, hammer,
                EPItems.COPPER_PLATE, "copper_plate", AdvancementFrame.TASK,
                CommonItemTags.COPPER_PLATES
        );

        AdvancementEntry goldPlate = addAdvancement(
                advancementOutput, hammer,
                EPItems.GOLD_PLATE, "gold_plate", AdvancementFrame.TASK,
                CommonItemTags.GOLD_PLATES
        );

        AdvancementEntry ironPlate = addAdvancement(
                advancementOutput, hammer,
                EPItems.IRON_PLATE, "iron_plate", AdvancementFrame.TASK,
                CommonItemTags.IRON_PLATES
        );

        AdvancementEntry cutter = addAdvancement(
                advancementOutput, ironPlate,
                EPItems.CUTTER, "cutter", AdvancementFrame.TASK,
                CommonItemTags.CUTTERS
        );

        AdvancementEntry tinWire = addAdvancement(
                advancementOutput, cutter,
                EPItems.TIN_WIRE, "tin_wire", AdvancementFrame.TASK,
                CommonItemTags.TIN_WIRES
        );

        AdvancementEntry goldWire = addAdvancement(
                advancementOutput, cutter,
                EPItems.GOLD_WIRE, "gold_wire", AdvancementFrame.TASK,
                CommonItemTags.GOLD_WIRES
        );

        AdvancementEntry copperWire = addAdvancement(
                advancementOutput, cutter,
                EPItems.COPPER_WIRE, "copper_wire", AdvancementFrame.TASK,
                CommonItemTags.COPPER_WIRES
        );

        AdvancementEntry basicCircuit = addAdvancement(
                advancementOutput, copperWire,
                EPItems.BASIC_CIRCUIT, "basic_circuit", AdvancementFrame.TASK
        );

        AdvancementEntry basicUpgradeModule = addAdvancement(
                advancementOutput, basicCircuit,
                EPItems.BASIC_UPGRADE_MODULE, "basic_upgrade_module", AdvancementFrame.TASK
        );

        AdvancementEntry speedUpgradeUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.SPEED_UPGRADE_MODULE_1, "speed_upgrade_module_1", AdvancementFrame.TASK
        );

        AdvancementEntry speedUpgradeUpgradeModule2 = addAdvancement(
                advancementOutput, speedUpgradeUpgradeModule1,
                EPItems.SPEED_UPGRADE_MODULE_2, "speed_upgrade_module_2", AdvancementFrame.TASK
        );

        AdvancementEntry energyEfficiencyUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1, "energy_efficiency_upgrade_module_1", AdvancementFrame.TASK
        );

        AdvancementEntry energyEfficiencyUpgradeModule2 = addAdvancement(
                advancementOutput, energyEfficiencyUpgradeModule1,
                EPItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2, "energy_efficiency_upgrade_module_2", AdvancementFrame.TASK
        );

        AdvancementEntry energyCapacityUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_1, "energy_capacity_upgrade_module_1", AdvancementFrame.TASK
        );

        AdvancementEntry energyCapacityUpgradeModule2 = addAdvancement(
                advancementOutput, energyCapacityUpgradeModule1,
                EPItems.ENERGY_CAPACITY_UPGRADE_MODULE_2, "energy_capacity_upgrade_module_2", AdvancementFrame.TASK
        );

        AdvancementEntry extractionDepthUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1, "extraction_depth_upgrade_module_1", AdvancementFrame.TASK
        );

        AdvancementEntry extractionDepthUpgradeModule2 = addAdvancement(
                advancementOutput, extractionDepthUpgradeModule1,
                EPItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2, "extraction_depth_upgrade_module_2", AdvancementFrame.TASK
        );

        AdvancementEntry blastFurnaceUpgradeModule = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.BLAST_FURNACE_UPGRADE_MODULE, "blast_furnace_upgrade_module", AdvancementFrame.TASK
        );

        AdvancementEntry smokerUpgradeModule = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.SMOKER_UPGRADE_MODULE, "smoker_upgrade_module", AdvancementFrame.TASK
        );

        AdvancementEntry moonLightUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                EPItems.MOON_LIGHT_UPGRADE_MODULE_1, "moon_light_upgrade_module_1", AdvancementFrame.TASK
        );

        AdvancementEntry itemConveyorBelt = addAdvancement(
                advancementOutput, ironPlate,
                EPBlocks.ITEM_CONVEYOR_BELT_ITEM, "item_conveyor_belt", AdvancementFrame.TASK
        );

        AdvancementEntry itemConveyorBeltLoader = addAdvancement(
                advancementOutput, itemConveyorBelt,
                EPBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM, "item_conveyor_belt_loader", AdvancementFrame.TASK
        );

        AdvancementEntry itemConveyorBeltSorter = addAdvancement(
                advancementOutput, itemConveyorBeltLoader,
                EPBlocks.ITEM_CONVEYOR_BELT_SORTER_ITEM, "item_conveyor_belt_sorter", AdvancementFrame.TASK
        );

        AdvancementEntry itemConveyorBeltSwitch = addAdvancement(
                advancementOutput, itemConveyorBeltLoader,
                EPBlocks.ITEM_CONVEYOR_BELT_SWITCH_ITEM, "item_conveyor_belt_switch", AdvancementFrame.TASK
        );

        AdvancementEntry itemConveyorBeltSplitter = addAdvancement(
                advancementOutput, itemConveyorBeltLoader,
                EPBlocks.ITEM_CONVEYOR_BELT_SPLITTER_ITEM, "item_conveyor_belt_splitter", AdvancementFrame.TASK
        );

        AdvancementEntry itemConveyorBeltMerger = addAdvancement(
                advancementOutput, itemConveyorBeltLoader,
                EPBlocks.ITEM_CONVEYOR_BELT_MERGER_ITEM, "item_conveyor_belt_merger", AdvancementFrame.TASK
        );

        AdvancementEntry ironFluidPipe = addAdvancement(
                advancementOutput, ironPlate,
                EPBlocks.IRON_FLUID_PIPE_ITEM, "iron_fluid_pipe", AdvancementFrame.TASK
        );

        AdvancementEntry goldenFluidPipe = addAdvancement(
                advancementOutput, ironFluidPipe,
                EPBlocks.GOLDEN_FLUID_PIPE_ITEM, "golden_fluid_pipe", AdvancementFrame.TASK
        );

        AdvancementEntry fluidTankSmall = addAdvancement(
                advancementOutput, ironFluidPipe,
                EPBlocks.FLUID_TANK_SMALL_ITEM, "fluid_tank_small", AdvancementFrame.TASK
        );

        AdvancementEntry fluidTankMedium = addAdvancement(
                advancementOutput, fluidTankSmall,
                EPBlocks.FLUID_TANK_MEDIUM_ITEM, "fluid_tank_medium", AdvancementFrame.TASK
        );

        AdvancementEntry fluidTankLarge = addAdvancement(
                advancementOutput, fluidTankMedium,
                EPBlocks.FLUID_TANK_LARGE_ITEM, "fluid_tank_large", AdvancementFrame.TASK
        );

        AdvancementEntry drain = addAdvancement(
                advancementOutput, ironPlate,
                EPBlocks.DRAIN_ITEM, "drain", AdvancementFrame.TASK
        );

        AdvancementEntry cableInsulator = addAdvancement(
                advancementOutput, energizedPowerBasics,
                EPItems.CABLE_INSULATOR, "cable_insulator", AdvancementFrame.TASK
        );

        AdvancementEntry tinCable = addAdvancement(
                advancementOutput, cableInsulator,
                EPBlocks.TIN_CABLE_ITEM, "tin_cable", AdvancementFrame.TASK
        );

        AdvancementEntry copperCable = addAdvancement(
                advancementOutput, tinCable,
                EPBlocks.COPPER_CABLE_ITEM, "copper_cable", AdvancementFrame.TASK
        );

        AdvancementEntry goldCable = addAdvancement(
                advancementOutput, copperCable,
                EPBlocks.GOLD_CABLE_ITEM, "gold_cable", AdvancementFrame.TASK
        );

        AdvancementEntry lvTransformers = addAdvancement(
                advancementOutput, tinCable,
                EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM, "lv_transformers", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().items(
                                EPBlocks.LV_TRANSFORMER_1_TO_N_ITEM,
                                EPBlocks.LV_TRANSFORMER_3_TO_3_ITEM,
                                EPBlocks.LV_TRANSFORMER_N_TO_1_ITEM
                        ).build()
                )
        );

        AdvancementEntry silicon = addAdvancement(
                advancementOutput, energizedPowerBasics,
                EPItems.SILICON, "silicon", AdvancementFrame.TASK,
                CommonItemTags.SILICON
        );

        AdvancementEntry poweredLamp = addAdvancement(
                advancementOutput, silicon,
                EPBlocks.POWERED_LAMP_ITEM, "powered_lamp", AdvancementFrame.TASK
        );

        AdvancementEntry basicSolarCell = addAdvancement(
                advancementOutput, silicon,
                EPItems.BASIC_SOLAR_CELL, "basic_solar_cell", AdvancementFrame.TASK
        );

        AdvancementEntry solarPanel1 = addAdvancement(
                advancementOutput, basicSolarCell,
                EPBlocks.SOLAR_PANEL_ITEM_1, "solar_panel_1", AdvancementFrame.TASK
        );

        AdvancementEntry solarPanel2 = addAdvancement(
                advancementOutput, solarPanel1,
                EPBlocks.SOLAR_PANEL_ITEM_2, "solar_panel_2", AdvancementFrame.TASK
        );

        AdvancementEntry solarPanel3 = addAdvancement(
                advancementOutput, solarPanel2,
                EPBlocks.SOLAR_PANEL_ITEM_3, "solar_panel_3", AdvancementFrame.TASK
        );

        AdvancementEntry basicMachineFrame = addAdvancement(
                advancementOutput, silicon,
                EPBlocks.BASIC_MACHINE_FRAME_ITEM, "basic_machine_frame", AdvancementFrame.TASK
        );

        AdvancementEntry autoCrafter = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.AUTO_CRAFTER_ITEM, "auto_crafter", AdvancementFrame.TASK
        );

        AdvancementEntry crusher = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.CRUSHER_ITEM, "crusher", AdvancementFrame.TASK
        );

        AdvancementEntry sawmill = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.SAWMILL_ITEM, "sawmill", AdvancementFrame.TASK
        );

        AdvancementEntry compressor = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.COMPRESSOR_ITEM, "compressor", AdvancementFrame.TASK
        );

        AdvancementEntry autoStonecutter = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.AUTO_STONECUTTER_ITEM, "auto_stonecutter", AdvancementFrame.TASK
        );

        AdvancementEntry plantGrowthChamber = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.PLANT_GROWTH_CHAMBER_ITEM, "plant_growth_chamber", AdvancementFrame.TASK
        );

        AdvancementEntry blockPlacer = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.BLOCK_PLACER_ITEM, "block_placer", AdvancementFrame.TASK
        );

        AdvancementEntry fluidFiller = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.FLUID_FILLER_ITEM, "fluid_filler", AdvancementFrame.TASK
        );

        AdvancementEntry fluidDrainer = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.FLUID_DRAINER_ITEM, "fluid_drainer", AdvancementFrame.TASK
        );

        AdvancementEntry fluidPump = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.FLUID_PUMP_ITEM, "fluid_pump", AdvancementFrame.TASK
        );

        AdvancementEntry poweredFurnace = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.POWERED_FURNACE_ITEM, "powered_furnace", AdvancementFrame.TASK
        );

        AdvancementEntry pulverizer = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.PULVERIZER_ITEM, "pulverizer", AdvancementFrame.TASK
        );

        AdvancementEntry charcoalFilter = addAdvancement(
                advancementOutput, pulverizer,
                EPItems.CHARCOAL_FILTER, "charcoal_filter", AdvancementFrame.TASK
        );

        AdvancementEntry coalEngine = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.COAL_ENGINE_ITEM, "coal_engine", AdvancementFrame.TASK
        );

        ItemStack inventoryCoalEngineIcon = new ItemStack(EPItems.INVENTORY_COAL_ENGINE);
        NbtCompound nbt = inventoryCoalEngineIcon.getOrCreateNbt();
        nbt.putBoolean("active", true);
        nbt.putBoolean("working", true);
        AdvancementEntry inventoryCoalEngine = addAdvancement(
                advancementOutput, coalEngine,
                inventoryCoalEngineIcon, "inventory_coal_engine", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(EPItems.INVENTORY_COAL_ENGINE)
        );

        AdvancementEntry heatGenerator = addAdvancement(
                advancementOutput, coalEngine,
                EPBlocks.HEAT_GENERATOR_ITEM, "heat_generator", AdvancementFrame.TASK
        );

        AdvancementEntry charger = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.CHARGER_ITEM, "charger", AdvancementFrame.TASK
        );

        AdvancementEntry minecartCharger = addAdvancement(
                advancementOutput, charger,
                EPBlocks.MINECART_CHARGER_ITEM, "minecart_charger", AdvancementFrame.TASK
        );

        AdvancementEntry uncharger = addAdvancement(
                advancementOutput, charger,
                EPBlocks.UNCHARGER_ITEM, "uncharger", AdvancementFrame.TASK
        );

        AdvancementEntry minecartUncharger = addAdvancement(
                advancementOutput, uncharger,
                EPBlocks.MINECART_UNCHARGER_ITEM, "minecart_uncharger", AdvancementFrame.TASK
        );

        AdvancementEntry inventoryCharger = addAdvancement(
                advancementOutput, charger,
                EPItems.INVENTORY_CHARGER, "inventory_charger", AdvancementFrame.TASK
        );

        AdvancementEntry battery1 = addAdvancement(
                advancementOutput, charger,
                EPItems.BATTERY_1, "battery_1", AdvancementFrame.TASK
        );

        AdvancementEntry battery2 = addAdvancement(
                advancementOutput, battery1,
                EPItems.BATTERY_2, "battery_2", AdvancementFrame.TASK
        );

        AdvancementEntry battery3 = addAdvancement(
                advancementOutput, battery2,
                EPItems.BATTERY_3, "battery_3", AdvancementFrame.TASK
        );

        AdvancementEntry energyAnalyzer = addAdvancement(
                advancementOutput, battery3,
                EPItems.ENERGY_ANALYZER, "energy_analyzer", AdvancementFrame.TASK
        );

        AdvancementEntry fluidAnalyzer = addAdvancement(
                advancementOutput, battery3,
                EPItems.FLUID_ANALYZER, "fluid_analyzer", AdvancementFrame.TASK
        );

        AdvancementEntry battery4 = addAdvancement(
                advancementOutput, battery3,
                EPItems.BATTERY_4, "battery_4", AdvancementFrame.TASK
        );

        AdvancementEntry battery5 = addAdvancement(
                advancementOutput, battery4,
                EPItems.BATTERY_5, "battery_5", AdvancementFrame.TASK
        );

        AdvancementEntry batteryBox = addAdvancement(
                advancementOutput, battery5,
                EPBlocks.BATTERY_BOX_ITEM, "battery_box", AdvancementFrame.TASK
        );

        AdvancementEntry batteryBoxMinecart = addAdvancement(
                advancementOutput, batteryBox,
                EPItems.BATTERY_BOX_MINECART, "battery_box_minecart", AdvancementFrame.TASK
        );

        AdvancementEntry metalPress = addAdvancement(
                advancementOutput, basicMachineFrame,
                EPBlocks.METAL_PRESS_ITEM, "metal_press", AdvancementFrame.TASK
        );

        AdvancementEntry autoPressMoldMaker = addAdvancement(
                advancementOutput, metalPress,
                EPBlocks.AUTO_PRESS_MOLD_MAKER, "auto_press_mold_maker", AdvancementFrame.TASK
        );

        AdvancementEntry hardenedMachineFrame = addAdvancement(
                advancementOutput, metalPress,
                EPBlocks.HARDENED_MACHINE_FRAME_ITEM, "hardened_machine_frame", AdvancementFrame.TASK
        );

        AdvancementEntry mvTransformers = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.MV_TRANSFORMER_1_TO_N, "mv_transformers", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().items(
                                EPBlocks.MV_TRANSFORMER_1_TO_N_ITEM,
                                EPBlocks.MV_TRANSFORMER_3_TO_3_ITEM,
                                EPBlocks.MV_TRANSFORMER_N_TO_1_ITEM
                        ).build()
                )
        );

        AdvancementEntry assemblingMachine = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.ASSEMBLING_MACHINE_ITEM, "assembling_machine", AdvancementFrame.TASK
        );

        AdvancementEntry inductionSmelter = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.INDUCTION_SMELTER_ITEM, "induction_smelter", AdvancementFrame.TASK
        );

        AdvancementEntry stoneSolidifier = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.STONE_SOLIDIFIER_ITEM, "stone_solidifier", AdvancementFrame.TASK
        );

        AdvancementEntry fluidTransposer = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.FLUID_TRANSPOSER_ITEM, "fluid_transposer", AdvancementFrame.TASK
        );

        AdvancementEntry filtrationPlant = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.FILTRATION_PLANT_ITEM, "filtration_plant", AdvancementFrame.TASK
        );

        AdvancementEntry thermalGenerator = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                EPBlocks.THERMAL_GENERATOR_ITEM, "thermal_generator", AdvancementFrame.TASK
        );
    }

    private AdvancementEntry addAdvancement(Consumer<AdvancementEntry> advancementOutput, AdvancementEntry parent,
                                            ItemConvertible icon, String advancementId, AdvancementFrame type) {
        return addAdvancement(advancementOutput, parent, icon, advancementId, type, icon);
    }
    private AdvancementEntry addAdvancement(Consumer<AdvancementEntry> advancementOutput, AdvancementEntry parent,
                                            ItemConvertible icon, String advancementId, AdvancementFrame type,
                                            ItemConvertible trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon), advancementId, type,
                InventoryChangedCriterion.Conditions.items(trigger));
    }
    private AdvancementEntry addAdvancement(Consumer<AdvancementEntry> advancementOutput, AdvancementEntry parent,
                                            ItemConvertible icon, String advancementId, AdvancementFrame type,
                                            TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon), advancementId, type,
                InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                        trigger
                )));
    }
    private AdvancementEntry addAdvancement(Consumer<AdvancementEntry> advancementOutput, AdvancementEntry parent,
                                            ItemConvertible icon, String advancementId, AdvancementFrame type,
                                            AdvancementCriterion<?> trigger) {
        return addAdvancement(advancementOutput, parent, new ItemStack(icon), advancementId, type, trigger);
    }
    private AdvancementEntry addAdvancement(Consumer<AdvancementEntry> advancementOutput, AdvancementEntry parent,
                                            ItemStack icon, String advancementId, AdvancementFrame type,
                                            AdvancementCriterion<?> trigger) {
        return Advancement.Builder.create().parent(parent).
                display(
                        icon,
                        Text.translatable("advancements.energizedpower." + advancementId + ".title"),
                        Text.translatable("advancements.energizedpower." + advancementId + ".description"),
                        null,
                        type,
                        true,
                        true,
                        false
                ).
                criterion("has_the_item", trigger).
                build(advancementOutput, EPAPI.MOD_ID + ":main/basics/" + advancementId);
    }

    @Override
    public String getName() {
        return "Advancements (Basics)";
    }
}