package me.jddev0.ep.datagen.advancement;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class ModBasicsAdvancements implements ForgeAdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider lookupProvider, Consumer<Advancement> advancementOutput,
                         ExistingFileHelper existingFileHelper) {
        Advancement energizedPowerBasics = Advancement.Builder.advancement().
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
                        ).build())).
                save(advancementOutput, EPAPI.id("main/basics/energizedpower_basics"), existingFileHelper);

        Advancement energizedPowerBook = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                ModItems.ENERGIZED_POWER_BOOK, "energized_power_book", FrameType.TASK
        );

        Advancement pressMoldMaker = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                ModBlocks.PRESS_MOLD_MAKER_ITEM, "press_mold_maker", FrameType.TASK
        );

        Advancement rawPressMolds = addAdvancement(
                advancementOutput, existingFileHelper, pressMoldMaker,
                ModItems.RAW_GEAR_PRESS_MOLD, "raw_press_molds", FrameType.TASK,
                EnergizedPowerItemTags.RAW_METAL_PRESS_MOLDS
        );

        Advancement pressMolds = addAdvancement(
                advancementOutput, existingFileHelper, rawPressMolds,
                ModItems.GEAR_PRESS_MOLD, "press_molds", FrameType.TASK,
                EnergizedPowerItemTags.METAL_PRESS_MOLDS
        );

        Advancement alloyFurnace = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                ModBlocks.ALLOY_FURNACE_ITEM, "alloy_furnace", FrameType.TASK
        );

        Advancement steelIngot = addAdvancement(
                advancementOutput, existingFileHelper, alloyFurnace,
                ModItems.STEEL_INGOT, "steel_ingot", FrameType.TASK,
                CommonItemTags.INGOTS_STEEL
        );

        Advancement redstoneAlloyIngot = addAdvancement(
                advancementOutput, existingFileHelper, alloyFurnace,
                ModItems.REDSTONE_ALLOY_INGOT, "redstone_alloy_ingot", FrameType.TASK,
                CommonItemTags.INGOTS_REDSTONE_ALLOY
        );

        Advancement wrench = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                ModItems.WRENCH, "wrench", FrameType.TASK
        );

        Advancement hammer = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                ModItems.IRON_HAMMER, "hammer", FrameType.TASK,
                CommonItemTags.TOOLS_HAMMERS
        );

        Advancement tinPlate = addAdvancement(
                advancementOutput, existingFileHelper, hammer,
                ModItems.TIN_PLATE, "tin_plate", FrameType.TASK,
                CommonItemTags.PLATES_TIN
        );

        Advancement copperPlate = addAdvancement(
                advancementOutput, existingFileHelper, hammer,
                ModItems.COPPER_PLATE, "copper_plate", FrameType.TASK,
                CommonItemTags.PLATES_COPPER
        );

        Advancement goldPlate = addAdvancement(
                advancementOutput, existingFileHelper, hammer,
                ModItems.GOLD_PLATE, "gold_plate", FrameType.TASK,
                CommonItemTags.PLATES_GOLD
        );

        Advancement ironPlate = addAdvancement(
                advancementOutput, existingFileHelper, hammer,
                ModItems.IRON_PLATE, "iron_plate", FrameType.TASK,
                CommonItemTags.PLATES_IRON
        );

        Advancement cutter = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                ModItems.CUTTER, "cutter", FrameType.TASK,
                CommonItemTags.TOOLS_CUTTERS
        );

        Advancement tinWire = addAdvancement(
                advancementOutput, existingFileHelper, cutter,
                ModItems.TIN_WIRE, "tin_wire", FrameType.TASK,
                CommonItemTags.WIRES_TIN
        );

        Advancement goldWire = addAdvancement(
                advancementOutput, existingFileHelper, cutter,
                ModItems.GOLD_WIRE, "gold_wire", FrameType.TASK,
                CommonItemTags.WIRES_GOLD
        );

        Advancement copperWire = addAdvancement(
                advancementOutput, existingFileHelper, cutter,
                ModItems.COPPER_WIRE, "copper_wire", FrameType.TASK,
                CommonItemTags.WIRES_COPPER
        );

        Advancement basicCircuit = addAdvancement(
                advancementOutput, existingFileHelper, copperWire,
                ModItems.BASIC_CIRCUIT, "basic_circuit", FrameType.TASK
        );

        Advancement basicUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, basicCircuit,
                ModItems.BASIC_UPGRADE_MODULE, "basic_upgrade_module", FrameType.TASK
        );

        Advancement speedUpgradeUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                ModItems.SPEED_UPGRADE_MODULE_1, "speed_upgrade_module_1", FrameType.TASK
        );

        Advancement speedUpgradeUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, speedUpgradeUpgradeModule1,
                ModItems.SPEED_UPGRADE_MODULE_2, "speed_upgrade_module_2", FrameType.TASK
        );

        Advancement energyEfficiencyUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_1, "energy_efficiency_upgrade_module_1", FrameType.TASK
        );

        Advancement energyEfficiencyUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, energyEfficiencyUpgradeModule1,
                ModItems.ENERGY_EFFICIENCY_UPGRADE_MODULE_2, "energy_efficiency_upgrade_module_2", FrameType.TASK
        );

        Advancement energyCapacityUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_1, "energy_capacity_upgrade_module_1", FrameType.TASK
        );

        Advancement energyCapacityUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, energyCapacityUpgradeModule1,
                ModItems.ENERGY_CAPACITY_UPGRADE_MODULE_2, "energy_capacity_upgrade_module_2", FrameType.TASK
        );

        Advancement extractionDepthUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_1, "extraction_depth_upgrade_module_1", FrameType.TASK
        );

        Advancement extractionDepthUpgradeModule2 = addAdvancement(
                advancementOutput, existingFileHelper, extractionDepthUpgradeModule1,
                ModItems.EXTRACTION_DEPTH_UPGRADE_MODULE_2, "extraction_depth_upgrade_module_2", FrameType.TASK
        );

        Advancement blastFurnaceUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                ModItems.BLAST_FURNACE_UPGRADE_MODULE, "blast_furnace_upgrade_module", FrameType.TASK
        );

        Advancement smokerUpgradeModule = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                ModItems.SMOKER_UPGRADE_MODULE, "smoker_upgrade_module", FrameType.TASK
        );

        Advancement moonLightUpgradeModule1 = addAdvancement(
                advancementOutput, existingFileHelper, basicUpgradeModule,
                ModItems.MOON_LIGHT_UPGRADE_MODULE_1, "moon_light_upgrade_module_1", FrameType.TASK
        );

        Advancement itemConveyorBelt = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                ModBlocks.ITEM_CONVEYOR_BELT_ITEM, "item_conveyor_belt", FrameType.TASK
        );

        Advancement itemConveyorBeltLoader = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBelt,
                ModBlocks.ITEM_CONVEYOR_BELT_LOADER_ITEM, "item_conveyor_belt_loader", FrameType.TASK
        );

        Advancement itemConveyorBeltSorter = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBeltLoader,
                ModBlocks.ITEM_CONVEYOR_BELT_SORTER_ITEM, "item_conveyor_belt_sorter", FrameType.TASK
        );

        Advancement itemConveyorBeltSwitch = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBeltLoader,
                ModBlocks.ITEM_CONVEYOR_BELT_SWITCH_ITEM, "item_conveyor_belt_switch", FrameType.TASK
        );

        Advancement itemConveyorBeltSplitter = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBeltLoader,
                ModBlocks.ITEM_CONVEYOR_BELT_SPLITTER_ITEM, "item_conveyor_belt_splitter", FrameType.TASK
        );

        Advancement itemConveyorBeltMerger = addAdvancement(
                advancementOutput, existingFileHelper, itemConveyorBeltLoader,
                ModBlocks.ITEM_CONVEYOR_BELT_MERGER_ITEM, "item_conveyor_belt_merger", FrameType.TASK
        );

        Advancement ironFluidPipe = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                ModBlocks.IRON_FLUID_PIPE_ITEM, "iron_fluid_pipe", FrameType.TASK
        );

        Advancement goldenFluidPipe = addAdvancement(
                advancementOutput, existingFileHelper, ironFluidPipe,
                ModBlocks.GOLDEN_FLUID_PIPE_ITEM, "golden_fluid_pipe", FrameType.TASK
        );

        Advancement fluidTankSmall = addAdvancement(
                advancementOutput, existingFileHelper, ironFluidPipe,
                ModBlocks.FLUID_TANK_SMALL_ITEM, "fluid_tank_small", FrameType.TASK
        );

        Advancement fluidTankMedium = addAdvancement(
                advancementOutput, existingFileHelper, fluidTankSmall,
                ModBlocks.FLUID_TANK_MEDIUM_ITEM, "fluid_tank_medium", FrameType.TASK
        );

        Advancement fluidTankLarge = addAdvancement(
                advancementOutput, existingFileHelper, fluidTankMedium,
                ModBlocks.FLUID_TANK_LARGE_ITEM, "fluid_tank_large", FrameType.TASK
        );

        Advancement drain = addAdvancement(
                advancementOutput, existingFileHelper, ironPlate,
                ModBlocks.DRAIN_ITEM, "drain", FrameType.TASK
        );

        Advancement cableInsulator = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                ModItems.CABLE_INSULATOR, "cable_insulator", FrameType.TASK
        );

        Advancement tinCable = addAdvancement(
                advancementOutput, existingFileHelper, cableInsulator,
                ModBlocks.TIN_CABLE_ITEM, "tin_cable", FrameType.TASK
        );

        Advancement copperCable = addAdvancement(
                advancementOutput, existingFileHelper, tinCable,
                ModBlocks.COPPER_CABLE_ITEM, "copper_cable", FrameType.TASK
        );

        Advancement goldCable = addAdvancement(
                advancementOutput, existingFileHelper, copperCable,
                ModBlocks.GOLD_CABLE_ITEM, "gold_cable", FrameType.TASK
        );

        Advancement lvTransformers = addAdvancement(
                advancementOutput, existingFileHelper, tinCable,
                ModBlocks.LV_TRANSFORMER_1_TO_N_ITEM, "lv_transformers", FrameType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                ModBlocks.LV_TRANSFORMER_1_TO_N_ITEM.get(),
                                ModBlocks.LV_TRANSFORMER_3_TO_3_ITEM.get(),
                                ModBlocks.LV_TRANSFORMER_N_TO_1_ITEM.get()
                        ).build()
                )
        );

        Advancement silicon = addAdvancement(
                advancementOutput, existingFileHelper, energizedPowerBasics,
                ModItems.SILICON, "silicon", FrameType.TASK,
                CommonItemTags.SILICON
        );

        Advancement poweredLamp = addAdvancement(
                advancementOutput, existingFileHelper, silicon,
                ModBlocks.POWERED_LAMP_ITEM, "powered_lamp", FrameType.TASK
        );

        Advancement basicSolarCell = addAdvancement(
                advancementOutput, existingFileHelper, silicon,
                ModItems.BASIC_SOLAR_CELL, "basic_solar_cell", FrameType.TASK
        );

        Advancement solarPanel1 = addAdvancement(
                advancementOutput, existingFileHelper, basicSolarCell,
                ModBlocks.SOLAR_PANEL_ITEM_1, "solar_panel_1", FrameType.TASK
        );

        Advancement solarPanel2 = addAdvancement(
                advancementOutput, existingFileHelper, solarPanel1,
                ModBlocks.SOLAR_PANEL_ITEM_2, "solar_panel_2", FrameType.TASK
        );

        Advancement solarPanel3 = addAdvancement(
                advancementOutput, existingFileHelper, solarPanel2,
                ModBlocks.SOLAR_PANEL_ITEM_3, "solar_panel_3", FrameType.TASK
        );

        Advancement basicMachineFrame = addAdvancement(
                advancementOutput, existingFileHelper, silicon,
                ModBlocks.BASIC_MACHINE_FRAME_ITEM, "basic_machine_frame", FrameType.TASK
        );

        Advancement autoCrafter = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.AUTO_CRAFTER_ITEM, "auto_crafter", FrameType.TASK
        );

        Advancement crusher = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.CRUSHER_ITEM, "crusher", FrameType.TASK
        );

        Advancement sawmill = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.SAWMILL_ITEM, "sawmill", FrameType.TASK
        );

        Advancement compressor = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.COMPRESSOR_ITEM, "compressor", FrameType.TASK
        );

        Advancement autoStonecutter = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.AUTO_STONECUTTER_ITEM, "auto_stonecutter", FrameType.TASK
        );

        Advancement plantGrowthChamber = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.PLANT_GROWTH_CHAMBER_ITEM, "plant_growth_chamber", FrameType.TASK
        );

        Advancement blockPlacer = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.BLOCK_PLACER_ITEM, "block_placer", FrameType.TASK
        );

        Advancement fluidFiller = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.FLUID_FILLER_ITEM, "fluid_filler", FrameType.TASK
        );

        Advancement fluidDrainer = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.FLUID_DRAINER_ITEM, "fluid_drainer", FrameType.TASK
        );

        Advancement fluidPump = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.FLUID_PUMP_ITEM, "fluid_pump", FrameType.TASK
        );

        Advancement poweredFurnace = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.POWERED_FURNACE_ITEM, "powered_furnace", FrameType.TASK
        );

        Advancement pulverizer = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.PULVERIZER_ITEM, "pulverizer", FrameType.TASK
        );

        Advancement charcoalFilter = addAdvancement(
                advancementOutput, existingFileHelper, pulverizer,
                ModItems.CHARCOAL_FILTER, "charcoal_filter", FrameType.TASK
        );

        Advancement coalEngine = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.COAL_ENGINE_ITEM, "coal_engine", FrameType.TASK
        );

        ItemStack inventoryCoalEngineIcon = new ItemStack(ModItems.INVENTORY_COAL_ENGINE.get());
        CompoundTag nbt = inventoryCoalEngineIcon.getOrCreateTag();
        nbt.putBoolean("active", true);
        nbt.putBoolean("working", true);
        Advancement inventoryCoalEngine = addAdvancement(
                advancementOutput, existingFileHelper, coalEngine,
                inventoryCoalEngineIcon, "inventory_coal_engine", FrameType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.INVENTORY_COAL_ENGINE.get())
        );

        Advancement heatGenerator = addAdvancement(
                advancementOutput, existingFileHelper, coalEngine,
                ModBlocks.HEAT_GENERATOR_ITEM, "heat_generator", FrameType.TASK
        );

        Advancement charger = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.CHARGER_ITEM, "charger", FrameType.TASK
        );

        Advancement minecartCharger = addAdvancement(
                advancementOutput, existingFileHelper, charger,
                ModBlocks.MINECART_CHARGER_ITEM, "minecart_charger", FrameType.TASK
        );

        Advancement uncharger = addAdvancement(
                advancementOutput, existingFileHelper, charger,
                ModBlocks.UNCHARGER_ITEM, "uncharger", FrameType.TASK
        );

        Advancement minecartUncharger = addAdvancement(
                advancementOutput, existingFileHelper, uncharger,
                ModBlocks.MINECART_UNCHARGER_ITEM, "minecart_uncharger", FrameType.TASK
        );

        Advancement inventoryCharger = addAdvancement(
                advancementOutput, existingFileHelper, charger,
                ModItems.INVENTORY_CHARGER, "inventory_charger", FrameType.TASK
        );

        Advancement battery1 = addAdvancement(
                advancementOutput, existingFileHelper, charger,
                ModItems.BATTERY_1, "battery_1", FrameType.TASK
        );

        Advancement battery2 = addAdvancement(
                advancementOutput, existingFileHelper, battery1,
                ModItems.BATTERY_2, "battery_2", FrameType.TASK
        );

        Advancement battery3 = addAdvancement(
                advancementOutput, existingFileHelper, battery2,
                ModItems.BATTERY_3, "battery_3", FrameType.TASK
        );

        Advancement energyAnalyzer = addAdvancement(
                advancementOutput, existingFileHelper, battery3,
                ModItems.ENERGY_ANALYZER, "energy_analyzer", FrameType.TASK
        );

        Advancement fluidAnalyzer = addAdvancement(
                advancementOutput, existingFileHelper, battery3,
                ModItems.FLUID_ANALYZER, "fluid_analyzer", FrameType.TASK
        );

        Advancement battery4 = addAdvancement(
                advancementOutput, existingFileHelper, battery3,
                ModItems.BATTERY_4, "battery_4", FrameType.TASK
        );

        Advancement battery5 = addAdvancement(
                advancementOutput, existingFileHelper, battery4,
                ModItems.BATTERY_5, "battery_5", FrameType.TASK
        );

        Advancement batteryBox = addAdvancement(
                advancementOutput, existingFileHelper, battery5,
                ModBlocks.BATTERY_BOX_ITEM, "battery_box", FrameType.TASK
        );

        Advancement batteryBoxMinecart = addAdvancement(
                advancementOutput, existingFileHelper, batteryBox,
                ModItems.BATTERY_BOX_MINECART, "battery_box_minecart", FrameType.TASK
        );

        Advancement metalPress = addAdvancement(
                advancementOutput, existingFileHelper, basicMachineFrame,
                ModBlocks.METAL_PRESS_ITEM, "metal_press", FrameType.TASK
        );

        Advancement autoPressMoldMaker = addAdvancement(
                advancementOutput, existingFileHelper, metalPress,
                ModBlocks.AUTO_PRESS_MOLD_MAKER_ITEM, "auto_press_mold_maker", FrameType.TASK
        );

        Advancement hardenedMachineFrame = addAdvancement(
                advancementOutput, existingFileHelper, metalPress,
                ModBlocks.HARDENED_MACHINE_FRAME_ITEM, "hardened_machine_frame", FrameType.TASK
        );

        Advancement mvTransformers = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                ModBlocks.MV_TRANSFORMER_1_TO_N_ITEM, "mv_transformers", FrameType.TASK,
                InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item().of(
                                ModBlocks.MV_TRANSFORMER_1_TO_N_ITEM.get(),
                                ModBlocks.MV_TRANSFORMER_3_TO_3_ITEM.get(),
                                ModBlocks.MV_TRANSFORMER_N_TO_1_ITEM.get()
                        ).build()
                )
        );

        Advancement assemblingMachine = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                ModBlocks.ASSEMBLING_MACHINE_ITEM, "assembling_machine", FrameType.TASK
        );

        Advancement inductionSmelter = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                ModBlocks.INDUCTION_SMELTER_ITEM, "induction_smelter", FrameType.TASK
        );

        Advancement stoneSolidifier = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                ModBlocks.STONE_SOLIDIFIER_ITEM, "stone_solidifier", FrameType.TASK
        );

        Advancement fluidTransposer = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                ModBlocks.FLUID_TRANSPOSER_ITEM, "fluid_transposer", FrameType.TASK
        );

        Advancement filtrationPlant = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                ModBlocks.FILTRATION_PLANT_ITEM, "filtration_plant", FrameType.TASK
        );

        Advancement thermalGenerator = addAdvancement(
                advancementOutput, existingFileHelper, hardenedMachineFrame,
                ModBlocks.THERMAL_GENERATOR_ITEM, "thermal_generator", FrameType.TASK
        );
    }

    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, ExistingFileHelper existingFileHelper,
                                             Advancement parent, RegistryObject<Item> icon, String advancementId, FrameType type) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon.get(), advancementId, type);
    }
    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, ExistingFileHelper existingFileHelper,
                                             Advancement parent, ItemLike icon, String advancementId, FrameType type) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon, advancementId, type, icon);
    }
    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, ExistingFileHelper existingFileHelper,
                                             Advancement parent, RegistryObject<Item> icon, String advancementId, FrameType type,
                                             RegistryObject<Item> trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon.get(), advancementId, type, trigger.get());
    }
    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, ExistingFileHelper existingFileHelper,
                                             Advancement parent, ItemLike icon, String advancementId, FrameType type,
                                             ItemLike trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(trigger));
    }
    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, ExistingFileHelper existingFileHelper,
                                             Advancement parent, RegistryObject<Item> icon, String advancementId, FrameType type,
                                             TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon.get(), advancementId, type, trigger);
    }
    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, ExistingFileHelper existingFileHelper,
                                             Advancement parent, ItemLike icon, String advancementId, FrameType type,
                                             TagKey<Item> trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type,
                InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(
                        trigger
                ).build()));
    }
    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, ExistingFileHelper existingFileHelper,
                                             Advancement parent, RegistryObject<Item> icon, String advancementId, FrameType type,
                                             AbstractCriterionTriggerInstance trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, icon.get(), advancementId, type, trigger);
    }
    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, ExistingFileHelper existingFileHelper,
                                             Advancement parent, ItemLike icon, String advancementId, FrameType type,
                                             AbstractCriterionTriggerInstance trigger) {
        return addAdvancement(advancementOutput, existingFileHelper, parent, new ItemStack(icon), advancementId, type, trigger);
    }
    private Advancement addAdvancement(Consumer<Advancement> advancementOutput, ExistingFileHelper existingFileHelper,
                                             Advancement parent, ItemStack icon, String advancementId, FrameType type,
                                             AbstractCriterionTriggerInstance trigger) {
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
