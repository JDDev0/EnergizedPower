package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.component.ModDataComponentTypes;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.component.ComponentChanges;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModBasicsAdvancements extends FabricAdvancementProvider {
    public ModBasicsAdvancements(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        super(dataOutput, lookupProvider);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup lookupProvider, Consumer<AdvancementEntry> advancementOutput) {
        AdvancementEntry energizedPowerBasics = Advancement.Builder.create().
                display(
                        Items.COPPER_INGOT,
                        Text.translatable("advancements.energizedpower.energizedpower_basics.title"),
                        Text.translatable("advancements.energizedpower.energizedpower_basics.description"),
                        Identifier.of(EnergizedPowerMod.MODID, "textures/block/basic_machine_frame_top.png"),
                        AdvancementFrame.TASK,
                        true,
                        false,
                        false
                ).
                criterion("has_the_item",
                        InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(
                                ConventionalItemTags.COPPER_INGOTS
                        ))).
                build(advancementOutput, EnergizedPowerMod.MODID + ":main/basics/energizedpower_basics");

        AdvancementEntry energizedPowerBook = addAdvancement(
                advancementOutput, energizedPowerBasics,
                ModItems.ENERGIZED_POWER_BOOK, "energized_power_book", AdvancementFrame.TASK
        );

        AdvancementEntry pressMoldMaker = addAdvancement(
                advancementOutput, energizedPowerBasics,
                ModBlocks.PRESS_MOLD_MAKER_ITEM, "press_mold_maker", AdvancementFrame.TASK
        );

        AdvancementEntry rawPressMolds = addAdvancement(
                advancementOutput, pressMoldMaker,
                ModItems.RAW_GEAR_PRESS_MOLD, "raw_press_molds", AdvancementFrame.TASK,
                EnergizedPowerItemTags.RAW_METAL_PRESS_MOLDS
        );

        AdvancementEntry pressMolds = addAdvancement(
                advancementOutput, rawPressMolds,
                ModItems.GEAR_PRESS_MOLD, "press_molds", AdvancementFrame.TASK,
                EnergizedPowerItemTags.METAL_PRESS_MOLDS
        );

        AdvancementEntry alloyFurnace = addAdvancement(
                advancementOutput, energizedPowerBasics,
                ModBlocks.ALLOY_FURNACE_ITEM, "alloy_furnace", AdvancementFrame.TASK
        );

        AdvancementEntry steelIngot = addAdvancement(
                advancementOutput, alloyFurnace,
                ModItems.STEEL_INGOT, "steel_ingot", AdvancementFrame.TASK,
                CommonItemTags.INGOTS_STEEL
        );

        AdvancementEntry redstoneAlloyIngot = addAdvancement(
                advancementOutput, alloyFurnace,
                ModItems.REDSTONE_ALLOY_INGOT, "redstone_alloy_ingot", AdvancementFrame.TASK,
                CommonItemTags.INGOTS_REDSTONE_ALLOY
        );

        AdvancementEntry wrench = addAdvancement(
                advancementOutput, energizedPowerBasics,
                ModItems.WRENCH, "wrench", AdvancementFrame.TASK
        );

        AdvancementEntry hammer = addAdvancement(
                advancementOutput, energizedPowerBasics,
                ModItems.IRON_HAMMER, "hammer", AdvancementFrame.TASK,
                CommonItemTags.TOOLS_HAMMERS
        );

        AdvancementEntry tinPlate = addAdvancement(
                advancementOutput, hammer,
                ModItems.TIN_PLATE, "tin_plate", AdvancementFrame.TASK,
                CommonItemTags.PLATES_TIN
        );

        AdvancementEntry copperPlate = addAdvancement(
                advancementOutput, hammer,
                ModItems.COPPER_PLATE, "copper_plate", AdvancementFrame.TASK,
                CommonItemTags.PLATES_COPPER
        );

        AdvancementEntry goldPlate = addAdvancement(
                advancementOutput, hammer,
                ModItems.GOLD_PLATE, "gold_plate", AdvancementFrame.TASK,
                CommonItemTags.PLATES_GOLD
        );

        AdvancementEntry ironPlate = addAdvancement(
                advancementOutput, hammer,
                ModItems.IRON_PLATE, "iron_plate", AdvancementFrame.TASK,
                CommonItemTags.PLATES_IRON
        );

        AdvancementEntry cutter = addAdvancement(
                advancementOutput, ironPlate,
                ModItems.CUTTER, "cutter", AdvancementFrame.TASK,
                CommonItemTags.TOOLS_CUTTERS
        );

        AdvancementEntry tinWire = addAdvancement(
                advancementOutput, cutter,
                ModItems.TIN_WIRE, "tin_wire", AdvancementFrame.TASK,
                CommonItemTags.WIRES_TIN
        );

        AdvancementEntry goldWire = addAdvancement(
                advancementOutput, cutter,
                ModItems.GOLD_WIRE, "gold_wire", AdvancementFrame.TASK,
                CommonItemTags.WIRES_GOLD
        );

        AdvancementEntry copperWire = addAdvancement(
                advancementOutput, cutter,
                ModItems.COPPER_WIRE, "copper_wire", AdvancementFrame.TASK,
                CommonItemTags.WIRES_COPPER
        );

        AdvancementEntry basicCircuit = addAdvancement(
                advancementOutput, copperWire,
                ModItems.BASIC_CIRCUIT, "basic_circuit", AdvancementFrame.TASK
        );

        AdvancementEntry basicUpgradeModule = addAdvancement(
                advancementOutput, basicCircuit,
                ModItems.BASIC_UPGRADE_MODULE, "basic_upgrade_module", AdvancementFrame.TASK
        );

        AdvancementEntry speedUpgradeUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                ModItems.SPEED_UPGRADE_MODULE_1, "speed_upgrade_module_1", AdvancementFrame.TASK
        );

        AdvancementEntry speedUpgradeUpgradeModule2 = addAdvancement(
                advancementOutput, speedUpgradeUpgradeModule1,
                ModItems.SPEED_UPGRADE_MODULE_2, "speed_upgrade_module_2", AdvancementFrame.TASK
        );

        AdvancementEntry energyEfficiencyUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1, "energy_efficiency_upgrade_module_1", AdvancementFrame.TASK
        );

        AdvancementEntry energyEfficiencyUpgradeModule2 = addAdvancement(
                advancementOutput, energyEfficiencyUpgradeModule1,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2, "energy_efficiency_upgrade_module_2", AdvancementFrame.TASK
        );

        AdvancementEntry energyCapacityUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_1, "energy_capacity_upgrade_module_1", AdvancementFrame.TASK
        );

        AdvancementEntry energyCapacityUpgradeModule2 = addAdvancement(
                advancementOutput, energyCapacityUpgradeModule1,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_2, "energy_capacity_upgrade_module_2", AdvancementFrame.TASK
        );

        AdvancementEntry extractionDepthUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1, "extraction_depth_upgrade_module_1", AdvancementFrame.TASK
        );

        AdvancementEntry extractionDepthUpgradeModule2 = addAdvancement(
                advancementOutput, extractionDepthUpgradeModule1,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2, "extraction_depth_upgrade_module_2", AdvancementFrame.TASK
        );

        AdvancementEntry blastFurnaceUpgradeModule = addAdvancement(
                advancementOutput, basicUpgradeModule,
                ModItems.BLAST_FURNACE_UPGRADE_MODULE, "blast_furnace_upgrade_module", AdvancementFrame.TASK
        );

        AdvancementEntry smokerUpgradeModule = addAdvancement(
                advancementOutput, basicUpgradeModule,
                ModItems.SMOKER_UPGRADE_MODULE, "smoker_upgrade_module", AdvancementFrame.TASK
        );

        AdvancementEntry moonLightUpgradeModule1 = addAdvancement(
                advancementOutput, basicUpgradeModule,
                ModItems.MOON_LIGHT_UPGRADE_MODULE_1, "moon_light_upgrade_module_1", AdvancementFrame.TASK
        );

        AdvancementEntry itemConveyorBelt = addAdvancement(
                advancementOutput, ironPlate,
                ModBlocks.ITEM_CONVEYOR_BELT_ITEM, "item_conveyor_belt", AdvancementFrame.TASK
        );

        AdvancementEntry itemConveyorBeltLoader = addAdvancement(
                advancementOutput, itemConveyorBelt,
                ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM, "item_conveyor_belt_loader", AdvancementFrame.TASK
        );

        AdvancementEntry itemConveyorBeltSorter = addAdvancement(
                advancementOutput, itemConveyorBeltLoader,
                ModBlocks.ITEM_CONVEYOR_BELT_SORTER_ITEM, "item_conveyor_belt_sorter", AdvancementFrame.TASK
        );

        AdvancementEntry itemConveyorBeltSwitch = addAdvancement(
                advancementOutput, itemConveyorBeltLoader,
                ModBlocks.ITEM_CONVEYOR_BELT_SWITCH_ITEM, "item_conveyor_belt_switch", AdvancementFrame.TASK
        );

        AdvancementEntry itemConveyorBeltSplitter = addAdvancement(
                advancementOutput, itemConveyorBeltLoader,
                ModBlocks.ITEM_CONVEYOR_BELT_SPLITTER_ITEM, "item_conveyor_belt_splitter", AdvancementFrame.TASK
        );

        AdvancementEntry itemConveyorBeltMerger = addAdvancement(
                advancementOutput, itemConveyorBeltLoader,
                ModBlocks.ITEM_CONVEYOR_BELT_MERGER_ITEM, "item_conveyor_belt_merger", AdvancementFrame.TASK
        );

        AdvancementEntry ironFluidPipe = addAdvancement(
                advancementOutput, ironPlate,
                ModBlocks.IRON_FLUID_PIPE_ITEM, "iron_fluid_pipe", AdvancementFrame.TASK
        );

        AdvancementEntry goldenFluidPipe = addAdvancement(
                advancementOutput, ironFluidPipe,
                ModBlocks.GOLDEN_FLUID_PIPE_ITEM, "golden_fluid_pipe", AdvancementFrame.TASK
        );

        AdvancementEntry fluidTankSmall = addAdvancement(
                advancementOutput, ironFluidPipe,
                ModBlocks.FLUID_TANK_SMALL_ITEM, "fluid_tank_small", AdvancementFrame.TASK
        );

        AdvancementEntry fluidTankMedium = addAdvancement(
                advancementOutput, fluidTankSmall,
                ModBlocks.FLUID_TANK_MEDIUM_ITEM, "fluid_tank_medium", AdvancementFrame.TASK
        );

        AdvancementEntry fluidTankLarge = addAdvancement(
                advancementOutput, fluidTankMedium,
                ModBlocks.FLUID_TANK_LARGE_ITEM, "fluid_tank_large", AdvancementFrame.TASK
        );

        AdvancementEntry drain = addAdvancement(
                advancementOutput, ironPlate,
                ModBlocks.DRAIN_ITEM, "drain", AdvancementFrame.TASK
        );

        AdvancementEntry cableInsulator = addAdvancement(
                advancementOutput, energizedPowerBasics,
                ModItems.CABLE_INSULATOR, "cable_insulator", AdvancementFrame.TASK
        );

        AdvancementEntry tinCable = addAdvancement(
                advancementOutput, cableInsulator,
                ModBlocks.TIN_CABLE_ITEM, "tin_cable", AdvancementFrame.TASK
        );

        AdvancementEntry copperCable = addAdvancement(
                advancementOutput, tinCable,
                ModBlocks.COPPER_CABLE_ITEM, "copper_cable", AdvancementFrame.TASK
        );

        AdvancementEntry goldCable = addAdvancement(
                advancementOutput, copperCable,
                ModBlocks.GOLD_CABLE_ITEM, "gold_cable", AdvancementFrame.TASK
        );

        AdvancementEntry lvTransformers = addAdvancement(
                advancementOutput, tinCable,
                ModBlocks.LV_TRANSFORMER_1_TO_N_ITEM, "lv_transformers", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().items(
                                ModBlocks.LV_TRANSFORMER_1_TO_N_ITEM,
                                ModBlocks.LV_TRANSFORMER_3_TO_3_ITEM,
                                ModBlocks.LV_TRANSFORMER_N_TO_1_ITEM
                        ).build()
                )
        );

        AdvancementEntry silicon = addAdvancement(
                advancementOutput, energizedPowerBasics,
                ModItems.SILICON, "silicon", AdvancementFrame.TASK,
                CommonItemTags.SILICON
        );

        AdvancementEntry poweredLamp = addAdvancement(
                advancementOutput, silicon,
                ModBlocks.POWERED_LAMP_ITEM, "powered_lamp", AdvancementFrame.TASK
        );

        AdvancementEntry basicSolarCell = addAdvancement(
                advancementOutput, silicon,
                ModItems.BASIC_SOLAR_CELL, "basic_solar_cell", AdvancementFrame.TASK
        );

        AdvancementEntry solarPanel1 = addAdvancement(
                advancementOutput, basicSolarCell,
                ModBlocks.SOLAR_PANEL_ITEM_1, "solar_panel_1", AdvancementFrame.TASK
        );

        AdvancementEntry solarPanel2 = addAdvancement(
                advancementOutput, solarPanel1,
                ModBlocks.SOLAR_PANEL_ITEM_2, "solar_panel_2", AdvancementFrame.TASK
        );

        AdvancementEntry solarPanel3 = addAdvancement(
                advancementOutput, solarPanel2,
                ModBlocks.SOLAR_PANEL_ITEM_3, "solar_panel_3", AdvancementFrame.TASK
        );

        AdvancementEntry basicMachineFrame = addAdvancement(
                advancementOutput, silicon,
                ModBlocks.BASIC_MACHINE_FRAME_ITEM, "basic_machine_frame", AdvancementFrame.TASK
        );

        AdvancementEntry autoCrafter = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.AUTO_CRAFTER_ITEM, "auto_crafter", AdvancementFrame.TASK
        );

        AdvancementEntry crusher = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.CRUSHER_ITEM, "crusher", AdvancementFrame.TASK
        );

        AdvancementEntry sawmill = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.SAWMILL_ITEM, "sawmill", AdvancementFrame.TASK
        );

        AdvancementEntry compressor = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.COMPRESSOR_ITEM, "compressor", AdvancementFrame.TASK
        );

        AdvancementEntry autoStonecutter = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.AUTO_STONECUTTER_ITEM, "auto_stonecutter", AdvancementFrame.TASK
        );

        AdvancementEntry plantGrowthChamber = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.PLANT_GROWTH_CHAMBER_ITEM, "plant_growth_chamber", AdvancementFrame.TASK
        );

        AdvancementEntry blockPlacer = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.BLOCK_PLACER_ITEM, "block_placer", AdvancementFrame.TASK
        );

        AdvancementEntry fluidFiller = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.FLUID_FILLER_ITEM, "fluid_filler", AdvancementFrame.TASK
        );

        AdvancementEntry fluidDrainer = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.FLUID_DRAINER_ITEM, "fluid_drainer", AdvancementFrame.TASK
        );

        AdvancementEntry fluidPump = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.FLUID_PUMP_ITEM, "fluid_pump", AdvancementFrame.TASK
        );

        AdvancementEntry poweredFurnace = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.POWERED_FURNACE_ITEM, "powered_furnace", AdvancementFrame.TASK
        );

        AdvancementEntry pulverizer = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.PULVERIZER_ITEM, "pulverizer", AdvancementFrame.TASK
        );

        AdvancementEntry charcoalFilter = addAdvancement(
                advancementOutput, pulverizer,
                ModItems.CHARCOAL_FILTER, "charcoal_filter", AdvancementFrame.TASK
        );

        AdvancementEntry coalEngine = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.COAL_ENGINE_ITEM, "coal_engine", AdvancementFrame.TASK
        );

        ItemStack inventoryCoalEngineIcon = new ItemStack(ModItems.INVENTORY_COAL_ENGINE);
        inventoryCoalEngineIcon.applyChanges(ComponentChanges.builder().
                add(ModDataComponentTypes.ACTIVE, true).
                add(ModDataComponentTypes.WORKING, true).
                build());
        AdvancementEntry inventoryCoalEngine = addAdvancement(
                advancementOutput, coalEngine,
                inventoryCoalEngineIcon, "inventory_coal_engine", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(ModItems.INVENTORY_COAL_ENGINE)
        );

        AdvancementEntry heatGenerator = addAdvancement(
                advancementOutput, coalEngine,
                ModBlocks.HEAT_GENERATOR_ITEM, "heat_generator", AdvancementFrame.TASK
        );

        AdvancementEntry charger = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.CHARGER_ITEM, "charger", AdvancementFrame.TASK
        );

        AdvancementEntry minecartCharger = addAdvancement(
                advancementOutput, charger,
                ModBlocks.MINECART_CHARGER_ITEM, "minecart_charger", AdvancementFrame.TASK
        );

        AdvancementEntry uncharger = addAdvancement(
                advancementOutput, charger,
                ModBlocks.UNCHARGER_ITEM, "uncharger", AdvancementFrame.TASK
        );

        AdvancementEntry minecartUncharger = addAdvancement(
                advancementOutput, uncharger,
                ModBlocks.MINECART_UNCHARGER_ITEM, "minecart_uncharger", AdvancementFrame.TASK
        );

        AdvancementEntry inventoryCharger = addAdvancement(
                advancementOutput, charger,
                ModItems.INVENTORY_CHARGER, "inventory_charger", AdvancementFrame.TASK
        );

        AdvancementEntry battery1 = addAdvancement(
                advancementOutput, charger,
                ModItems.BATTERY_1, "battery_1", AdvancementFrame.TASK
        );

        AdvancementEntry battery2 = addAdvancement(
                advancementOutput, battery1,
                ModItems.BATTERY_2, "battery_2", AdvancementFrame.TASK
        );

        AdvancementEntry battery3 = addAdvancement(
                advancementOutput, battery2,
                ModItems.BATTERY_3, "battery_3", AdvancementFrame.TASK
        );

        AdvancementEntry energyAnalyzer = addAdvancement(
                advancementOutput, battery3,
                ModItems.ENERGY_ANALYZER, "energy_analyzer", AdvancementFrame.TASK
        );

        AdvancementEntry fluidAnalyzer = addAdvancement(
                advancementOutput, battery3,
                ModItems.FLUID_ANALYZER, "fluid_analyzer", AdvancementFrame.TASK
        );

        AdvancementEntry battery4 = addAdvancement(
                advancementOutput, battery3,
                ModItems.BATTERY_4, "battery_4", AdvancementFrame.TASK
        );

        AdvancementEntry battery5 = addAdvancement(
                advancementOutput, battery4,
                ModItems.BATTERY_5, "battery_5", AdvancementFrame.TASK
        );

        AdvancementEntry batteryBox = addAdvancement(
                advancementOutput, battery5,
                ModBlocks.BATTERY_BOX_ITEM, "battery_box", AdvancementFrame.TASK
        );

        AdvancementEntry batteryBoxMinecart = addAdvancement(
                advancementOutput, batteryBox,
                ModItems.BATTERY_BOX_MINECART, "battery_box_minecart", AdvancementFrame.TASK
        );

        AdvancementEntry metalPress = addAdvancement(
                advancementOutput, basicMachineFrame,
                ModBlocks.METAL_PRESS_ITEM, "metal_press", AdvancementFrame.TASK
        );

        AdvancementEntry autoPressMoldMaker = addAdvancement(
                advancementOutput, metalPress,
                ModBlocks.AUTO_PRESS_MOLD_MAKER, "auto_press_mold_maker", AdvancementFrame.TASK
        );

        AdvancementEntry hardenedMachineFrame = addAdvancement(
                advancementOutput, metalPress,
                ModBlocks.HARDENED_MACHINE_FRAME_ITEM, "hardened_machine_frame", AdvancementFrame.TASK
        );

        AdvancementEntry mvTransformers = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                ModBlocks.MV_TRANSFORMER_1_TO_N, "mv_transformers", AdvancementFrame.TASK,
                InventoryChangedCriterion.Conditions.items(
                        ItemPredicate.Builder.create().items(
                                ModBlocks.MV_TRANSFORMER_1_TO_N_ITEM,
                                ModBlocks.MV_TRANSFORMER_3_TO_3_ITEM,
                                ModBlocks.MV_TRANSFORMER_N_TO_1_ITEM
                        ).build()
                )
        );

        AdvancementEntry assemblingMachine = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                ModBlocks.ASSEMBLING_MACHINE_ITEM, "assembling_machine", AdvancementFrame.TASK
        );

        AdvancementEntry inductionSmelter = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                ModBlocks.INDUCTION_SMELTER_ITEM, "induction_smelter", AdvancementFrame.TASK
        );

        AdvancementEntry stoneSolidifier = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                ModBlocks.STONE_SOLIDIFIER_ITEM, "stone_solidifier", AdvancementFrame.TASK
        );

        AdvancementEntry fluidTransposer = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                ModBlocks.FLUID_TRANSPOSER_ITEM, "fluid_transposer", AdvancementFrame.TASK
        );

        AdvancementEntry filtrationPlant = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                ModBlocks.FILTRATION_PLANT_ITEM, "filtration_plant", AdvancementFrame.TASK
        );

        AdvancementEntry thermalGenerator = addAdvancement(
                advancementOutput, hardenedMachineFrame,
                ModBlocks.THERMAL_GENERATOR_ITEM, "thermal_generator", AdvancementFrame.TASK
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
                build(advancementOutput, EnergizedPowerMod.MODID + ":main/basics/" + advancementId);
    }

    @Override
    public String getName() {
        return "Advancements (Basics)";
    }
}
