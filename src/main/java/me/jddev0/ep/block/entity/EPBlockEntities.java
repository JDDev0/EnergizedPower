package me.jddev0.ep.block.entity;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.*;
import me.jddev0.ep.machine.tier.CableTier;
import me.jddev0.ep.machine.tier.TransformerTier;
import me.jddev0.ep.machine.tier.TransformerType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class EPBlockEntities {
    private EPBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, EPAPI.MOD_ID);

    private static Supplier<BlockEntityType<FluidPipeBlockEntity>> createFluidPipeBlockEntity(String name, Supplier<FluidPipeBlock> block) {
        return createBlockEntity(name, block, (blockPos, state) -> new FluidPipeBlockEntity(blockPos, state, block.get().getTier()));
    }
    public static final Supplier<BlockEntityType<FluidPipeBlockEntity>> COPPER_FLUID_PIPE_ENTITY =
            createFluidPipeBlockEntity("copper_fluid_pipe", EPBlocks.COPPER_FLUID_PIPE);
    public static final Supplier<BlockEntityType<FluidPipeBlockEntity>> IRON_FLUID_PIPE_ENTITY =
            createFluidPipeBlockEntity("fluid_pipe", EPBlocks.IRON_FLUID_PIPE);
    public static final Supplier<BlockEntityType<FluidPipeBlockEntity>> GOLDEN_FLUID_PIPE_ENTITY =
            createFluidPipeBlockEntity("golden_fluid_pipe", EPBlocks.GOLDEN_FLUID_PIPE);
    public static final Supplier<BlockEntityType<FluidPipeBlockEntity>> STEEL_FLUID_PIPE_ENTITY =
            createFluidPipeBlockEntity("steel_fluid_pipe", EPBlocks.STEEL_FLUID_PIPE);
    public static final Supplier<BlockEntityType<FluidPipeBlockEntity>> PRESSURIZED_FLUID_PIPE_ENTITY =
            createFluidPipeBlockEntity("pressurized_fluid_pipe", EPBlocks.PRESSURIZED_FLUID_PIPE);

    private static Supplier<BlockEntityType<FluidTankBlockEntity>> createFluidTankBlockEntity(String name, Supplier<FluidTankBlock> block) {
        return createBlockEntity(name, block, (blockPos, state) -> new FluidTankBlockEntity(blockPos, state, block.get().getTier()));
    }
    public static final Supplier<BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_SMALL_ENTITY =
            createFluidTankBlockEntity("fluid_tank_small", EPBlocks.FLUID_TANK_SMALL);
    public static final Supplier<BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_MEDIUM_ENTITY =
            createFluidTankBlockEntity("fluid_tank_medium", EPBlocks.FLUID_TANK_MEDIUM);
    public static final Supplier<BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_LARGE_ENTITY =
            createFluidTankBlockEntity("fluid_tank_large", EPBlocks.FLUID_TANK_LARGE);

    public static final Supplier<BlockEntityType<CreativeFluidTankBlockEntity>> CREATIVE_FLUID_TANK_ENTITY =
            createBlockEntity("creative_fluid_tank", EPBlocks.CREATIVE_FLUID_TANK, CreativeFluidTankBlockEntity::new);

    private static Supplier<BlockEntityType<ItemSiloBlockEntity>> createItemSiloBlockEntity(
            String name,
            Supplier<ItemSiloBlock> block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemSiloBlockEntity(blockPos, state, block.get().getTier()));
    }
    public static final Supplier<BlockEntityType<ItemSiloBlockEntity>> ITEM_SILO_TINY_ENTITY =
            createItemSiloBlockEntity("item_silo_tiny", EPBlocks.ITEM_SILO_TINY);
    public static final Supplier<BlockEntityType<ItemSiloBlockEntity>> ITEM_SILO_SMALL_ENTITY =
            createItemSiloBlockEntity("item_silo_small", EPBlocks.ITEM_SILO_SMALL);
    public static final Supplier<BlockEntityType<ItemSiloBlockEntity>> ITEM_SILO_MEDIUM_ENTITY =
            createItemSiloBlockEntity("item_silo_medium", EPBlocks.ITEM_SILO_MEDIUM);
    public static final Supplier<BlockEntityType<ItemSiloBlockEntity>> ITEM_SILO_LARGE_ENTITY =
            createItemSiloBlockEntity("item_silo_large", EPBlocks.ITEM_SILO_LARGE);
    public static final Supplier<BlockEntityType<ItemSiloBlockEntity>> ITEM_SILO_GIANT_ENTITY =
            createItemSiloBlockEntity("item_silo_giant", EPBlocks.ITEM_SILO_GIANT);

    public static final Supplier<BlockEntityType<CreativeItemSiloBlockEntity>> CREATIVE_ITEM_SILO_ENTITY = createBlockEntity(
            "creative_item_silo", EPBlocks.CREATIVE_ITEM_SILO, CreativeItemSiloBlockEntity::new);

    private static Supplier<BlockEntityType<ItemConveyorBeltBlockEntity>> createItemConveyorBeltBlockEntity(
            String name,
            Supplier<ItemConveyorBeltBlock> block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemConveyorBeltBlockEntity(blockPos, state, block.get().getTier()));
    }
    public static final Supplier<BlockEntityType<ItemConveyorBeltBlockEntity>> BASIC_ITEM_CONVEYOR_BELT_ENTITY =
            createItemConveyorBeltBlockEntity("item_conveyor_belt", EPBlocks.BASIC_ITEM_CONVEYOR_BELT);
    public static final Supplier<BlockEntityType<ItemConveyorBeltBlockEntity>> FAST_ITEM_CONVEYOR_BELT_ENTITY =
            createItemConveyorBeltBlockEntity("fast_item_conveyor_belt", EPBlocks.FAST_ITEM_CONVEYOR_BELT);
    public static final Supplier<BlockEntityType<ItemConveyorBeltBlockEntity>> EXPRESS_ITEM_CONVEYOR_BELT_ENTITY =
            createItemConveyorBeltBlockEntity("express_item_conveyor_belt", EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT);

    private static Supplier<BlockEntityType<ItemConveyorBeltLoaderBlockEntity>> createItemConveyorBeltLoaderBlockEntity(
            String name,
            Supplier<ItemConveyorBeltLoaderBlock> block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemConveyorBeltLoaderBlockEntity(blockPos, state, block.get().getTier()));
    }
    public static final Supplier<BlockEntityType<ItemConveyorBeltLoaderBlockEntity>> BASIC_ITEM_CONVEYOR_BELT_LOADER_ENTITY =
            createItemConveyorBeltLoaderBlockEntity("item_conveyor_belt_loader", EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER);
    public static final Supplier<BlockEntityType<ItemConveyorBeltLoaderBlockEntity>> FAST_ITEM_CONVEYOR_BELT_LOADER_ENTITY =
            createItemConveyorBeltLoaderBlockEntity("fast_item_conveyor_belt_loader", EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER);
    public static final Supplier<BlockEntityType<ItemConveyorBeltLoaderBlockEntity>> EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ENTITY =
            createItemConveyorBeltLoaderBlockEntity("express_item_conveyor_belt_loader", EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER);

    private static Supplier<BlockEntityType<ItemConveyorBeltSorterBlockEntity>> createItemConveyorBeltSorterBlockEntity(
            String name,
            Supplier<ItemConveyorBeltSorterBlock> block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemConveyorBeltSorterBlockEntity(blockPos, state, block.get().getTier()));
    }
    public static final Supplier<BlockEntityType<ItemConveyorBeltSorterBlockEntity>> BASIC_ITEM_CONVEYOR_BELT_SORTER_ENTITY =
            createItemConveyorBeltSorterBlockEntity("item_conveyor_belt_sorter", EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER);
    public static final Supplier<BlockEntityType<ItemConveyorBeltSorterBlockEntity>> FAST_ITEM_CONVEYOR_BELT_SORTER_ENTITY =
            createItemConveyorBeltSorterBlockEntity("fast_item_conveyor_belt_sorter", EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER);
    public static final Supplier<BlockEntityType<ItemConveyorBeltSorterBlockEntity>> EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ENTITY =
            createItemConveyorBeltSorterBlockEntity("express_item_conveyor_belt_sorter", EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER);

    private static Supplier<BlockEntityType<ItemConveyorBeltSwitchBlockEntity>> createItemConveyorBeltSwitchBlockEntity(
            String name,
            Supplier<ItemConveyorBeltSwitchBlock> block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemConveyorBeltSwitchBlockEntity(blockPos, state, block.get().getTier()));
    }
    public static final Supplier<BlockEntityType<ItemConveyorBeltSwitchBlockEntity>> BASIC_ITEM_CONVEYOR_BELT_SWITCH_ENTITY =
            createItemConveyorBeltSwitchBlockEntity("item_conveyor_belt_switch", EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH);
    public static final Supplier<BlockEntityType<ItemConveyorBeltSwitchBlockEntity>> FAST_ITEM_CONVEYOR_BELT_SWITCH_ENTITY =
            createItemConveyorBeltSwitchBlockEntity("fast_item_conveyor_belt_switch", EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH);
    public static final Supplier<BlockEntityType<ItemConveyorBeltSwitchBlockEntity>> EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ENTITY =
            createItemConveyorBeltSwitchBlockEntity("express_item_conveyor_belt_switch", EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH);

    private static Supplier<BlockEntityType<ItemConveyorBeltSplitterBlockEntity>> createItemConveyorBeltSplitterBlockEntity(
            String name,
            Supplier<ItemConveyorBeltSplitterBlock> block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemConveyorBeltSplitterBlockEntity(blockPos, state, block.get().getTier()));
    }
    public static final Supplier<BlockEntityType<ItemConveyorBeltSplitterBlockEntity>> BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY =
            createItemConveyorBeltSplitterBlockEntity("item_conveyor_belt_splitter", EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER);
    public static final Supplier<BlockEntityType<ItemConveyorBeltSplitterBlockEntity>> FAST_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY =
            createItemConveyorBeltSplitterBlockEntity("fast_conveyor_belt_splitter", EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER);
    public static final Supplier<BlockEntityType<ItemConveyorBeltSplitterBlockEntity>> EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY =
            createItemConveyorBeltSplitterBlockEntity("express_conveyor_belt_splitter", EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER);

    private static Supplier<BlockEntityType<ItemConveyorBeltMergerBlockEntity>> createItemConveyorBeltMergerBlockEntity(
            String name,
            Supplier<ItemConveyorBeltMergerBlock> block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemConveyorBeltMergerBlockEntity(blockPos, state, block.get().getTier()));
    }
    public static final Supplier<BlockEntityType<ItemConveyorBeltMergerBlockEntity>> BASIC_ITEM_CONVEYOR_BELT_MERGER_ENTITY =
            createItemConveyorBeltMergerBlockEntity("item_conveyor_belt_merger", EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER);
    public static final Supplier<BlockEntityType<ItemConveyorBeltMergerBlockEntity>> FAST_ITEM_CONVEYOR_BELT_MERGER_ENTITY =
            createItemConveyorBeltMergerBlockEntity("fast_item_conveyor_belt_merger", EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER);
    public static final Supplier<BlockEntityType<ItemConveyorBeltMergerBlockEntity>> EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ENTITY =
            createItemConveyorBeltMergerBlockEntity("express_item_conveyor_belt_merger", EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER);

    public static final Supplier<BlockEntityType<CableBlockEntity>> TIN_CABLE_ENTITY = createBlockEntity("tin_cable",
            EPBlocks.TIN_CABLE, (blockPos, state) -> new CableBlockEntity(blockPos, state, CableTier.TIN));
    public static final Supplier<BlockEntityType<CableBlockEntity>> COPPER_CABLE_ENTITY = createBlockEntity("copper_cable",
            EPBlocks.COPPER_CABLE, (blockPos, state) -> new CableBlockEntity(blockPos, state, CableTier.COPPER));
    public static final Supplier<BlockEntityType<CableBlockEntity>> GOLD_CABLE_ENTITY = createBlockEntity("gold_cable",
            EPBlocks.GOLD_CABLE, (blockPos, state) -> new CableBlockEntity(blockPos, state, CableTier.GOLD));
    public static final Supplier<BlockEntityType<CableBlockEntity>> ENERGIZED_COPPER_CABLE_ENTITY = createBlockEntity("energized_copper_cable",
            EPBlocks.ENERGIZED_COPPER_CABLE, (blockPos, state) -> new CableBlockEntity(blockPos, state, CableTier.ENERGIZED_COPPER));
    public static final Supplier<BlockEntityType<CableBlockEntity>> ENERGIZED_GOLD_CABLE_ENTITY = createBlockEntity("energized_gold_cable",
            EPBlocks.ENERGIZED_GOLD_CABLE, (blockPos, state) -> new CableBlockEntity(blockPos, state, CableTier.ENERGIZED_GOLD));
    public static final Supplier<BlockEntityType<CableBlockEntity>> ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY = createBlockEntity("energized_crystal_matrix_cable",
            EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE, (blockPos, state) -> new CableBlockEntity(blockPos, state, CableTier.ENERGIZED_CRYSTAL_MATRIX));

    public static final Supplier<BlockEntityType<AutoCrafterBlockEntity>> AUTO_CRAFTER_ENTITY = createBlockEntity("auto_crafter",
            EPBlocks.AUTO_CRAFTER, AutoCrafterBlockEntity::new);

    public static final Supplier<BlockEntityType<AdvancedAutoCrafterBlockEntity>> ADVANCED_AUTO_CRAFTER_ENTITY = createBlockEntity("advanced_auto_crafter",
            EPBlocks.ADVANCED_AUTO_CRAFTER, AdvancedAutoCrafterBlockEntity::new);

    public static final Supplier<BlockEntityType<PressMoldMakerBlockEntity>> PRESS_MOLD_MAKER_ENTITY = createBlockEntity("press_mold_maker",
            EPBlocks.PRESS_MOLD_MAKER, PressMoldMakerBlockEntity::new);

    public static final Supplier<BlockEntityType<AlloyFurnaceBlockEntity>> ALLOY_FURNACE_ENTITY = createBlockEntity("alloy_furnace",
            EPBlocks.ALLOY_FURNACE, AlloyFurnaceBlockEntity::new);

    public static final Supplier<BlockEntityType<CrusherBlockEntity>> CRUSHER_ENTITY = createBlockEntity("crusher",
            EPBlocks.CRUSHER, CrusherBlockEntity::new);

    public static final Supplier<BlockEntityType<AdvancedCrusherBlockEntity>> ADVANCED_CRUSHER_ENTITY = createBlockEntity("advanced_crusher",
            EPBlocks.ADVANCED_CRUSHER, AdvancedCrusherBlockEntity::new);

    public static final Supplier<BlockEntityType<PulverizerBlockEntity>> PULVERIZER_ENTITY = createBlockEntity("pulverizer",
            EPBlocks.PULVERIZER, PulverizerBlockEntity::new);

    public static final Supplier<BlockEntityType<AdvancedPulverizerBlockEntity>> ADVANCED_PULVERIZER_ENTITY = createBlockEntity("advanced_pulverizer",
            EPBlocks.ADVANCED_PULVERIZER, AdvancedPulverizerBlockEntity::new);

    public static final Supplier<BlockEntityType<SawmillBlockEntity>> SAWMILL_ENTITY = createBlockEntity("sawmill",
            EPBlocks.SAWMILL, SawmillBlockEntity::new);

    public static final Supplier<BlockEntityType<CompressorBlockEntity>> COMPRESSOR_ENTITY = createBlockEntity("compressor",
            EPBlocks.COMPRESSOR, CompressorBlockEntity::new);

    public static final Supplier<BlockEntityType<MetalPressBlockEntity>> METAL_PRESS_ENTITY = createBlockEntity("metal_press",
            EPBlocks.METAL_PRESS, MetalPressBlockEntity::new);

    public static final Supplier<BlockEntityType<AutoPressMoldMakerBlockEntity>> AUTO_PRESS_MOLD_MAKER_ENTITY = createBlockEntity("auto_press_mold_maker",
            EPBlocks.AUTO_PRESS_MOLD_MAKER, AutoPressMoldMakerBlockEntity::new);

    public static final Supplier<BlockEntityType<AutoStonecutterBlockEntity>> AUTO_STONECUTTER_ENTITY = createBlockEntity("auto_stonecutter",
            EPBlocks.AUTO_STONECUTTER, AutoStonecutterBlockEntity::new);

    public static final Supplier<BlockEntityType<PlantGrowthChamberBlockEntity>> PLANT_GROWTH_CHAMBER_ENTITY = createBlockEntity("plant_growth_chamber",
            EPBlocks.PLANT_GROWTH_CHAMBER, PlantGrowthChamberBlockEntity::new);

    public static final Supplier<BlockEntityType<BlockPlacerBlockEntity>> BLOCK_PLACER_ENTITY = createBlockEntity("block_placer",
            EPBlocks.BLOCK_PLACER, BlockPlacerBlockEntity::new);

    public static final Supplier<BlockEntityType<AssemblingMachineBlockEntity>> ASSEMBLING_MACHINE_ENTITY = createBlockEntity("assembling_machine",
            EPBlocks.ASSEMBLING_MACHINE, AssemblingMachineBlockEntity::new);

    public static final Supplier<BlockEntityType<InductionSmelterBlockEntity>> INDUCTION_SMELTER_ENTITY = createBlockEntity("induction_smelter",
            EPBlocks.INDUCTION_SMELTER, InductionSmelterBlockEntity::new);

    public static final Supplier<BlockEntityType<FluidFreezerBlockEntity>> FLUID_FREEZER_ENTITY = createBlockEntity("fluid_freezer",
            EPBlocks.FLUID_FREEZER, FluidFreezerBlockEntity::new);

    public static final Supplier<BlockEntityType<StoneLiquefierBlockEntity>> STONE_LIQUEFIER_ENTITY = createBlockEntity("stone_liquefier",
            EPBlocks.STONE_LIQUEFIER, StoneLiquefierBlockEntity::new);

    public static final Supplier<BlockEntityType<StoneSolidifierBlockEntity>> STONE_SOLIDIFIER_ENTITY = createBlockEntity("stone_solidifier",
            EPBlocks.STONE_SOLIDIFIER, StoneSolidifierBlockEntity::new);

    public static final Supplier<BlockEntityType<FiltrationPlantBlockEntity>> FILTRATION_PLANT_ENTITY = createBlockEntity("filtration_plant",
            EPBlocks.FILTRATION_PLANT, FiltrationPlantBlockEntity::new);

    public static final Supplier<BlockEntityType<FluidTransposerBlockEntity>> FLUID_TRANSPOSER_ENTITY = createBlockEntity("fluid_transposer",
            EPBlocks.FLUID_TRANSPOSER, FluidTransposerBlockEntity::new);

    public static final Supplier<BlockEntityType<FluidFillerBlockEntity>> FLUID_FILLER_ENTITY = createBlockEntity("fluid_filler",
            EPBlocks.FLUID_FILLER, FluidFillerBlockEntity::new);

    public static final Supplier<BlockEntityType<FluidDrainerBlockEntity>> FLUID_DRAINER_ENTITY = createBlockEntity("fluid_drainer",
            EPBlocks.FLUID_DRAINER, FluidDrainerBlockEntity::new);

    public static final Supplier<BlockEntityType<FluidPumpBlockEntity>> FLUID_PUMP_ENTITY = createBlockEntity("fluid_pump",
            EPBlocks.FLUID_PUMP, FluidPumpBlockEntity::new);

    public static final Supplier<BlockEntityType<AdvancedFluidPumpBlockEntity>> ADVANCED_FLUID_PUMP_ENTITY = createBlockEntity(
            "advanced_fluid_pump", EPBlocks.ADVANCED_FLUID_PUMP, AdvancedFluidPumpBlockEntity::new);

    public static final Supplier<BlockEntityType<DrainBlockEntity>> DRAIN_ENTITY = createBlockEntity("drain",
            EPBlocks.DRAIN, DrainBlockEntity::new);

    public static final Supplier<BlockEntityType<XPDrainBlockEntity>> XP_DRAIN_ENTITY = createBlockEntity("xp_drain",
            EPBlocks.XP_DRAIN, XPDrainBlockEntity::new);

    public static final Supplier<BlockEntityType<ChargerBlockEntity>> CHARGER_ENTITY = createBlockEntity("charger",
            EPBlocks.CHARGER, ChargerBlockEntity::new);

    public static final Supplier<BlockEntityType<AdvancedChargerBlockEntity>> ADVANCED_CHARGER_ENTITY = createBlockEntity("advanced_charger",
            EPBlocks.ADVANCED_CHARGER, AdvancedChargerBlockEntity::new);

    public static final Supplier<BlockEntityType<UnchargerBlockEntity>> UNCHARGER_ENTITY = createBlockEntity("uncharger",
            EPBlocks.UNCHARGER, UnchargerBlockEntity::new);

    public static final Supplier<BlockEntityType<AdvancedUnchargerBlockEntity>> ADVANCED_UNCHARGER_ENTITY = createBlockEntity("advanced_uncharger",
            EPBlocks.ADVANCED_UNCHARGER, AdvancedUnchargerBlockEntity::new);

    public static final Supplier<BlockEntityType<MinecartChargerBlockEntity>> MINECART_CHARGER_ENTITY = createBlockEntity("minecart_charger",
            EPBlocks.MINECART_CHARGER, MinecartChargerBlockEntity::new);

    public static final Supplier<BlockEntityType<AdvancedMinecartChargerBlockEntity>> ADVANCED_MINECART_CHARGER_ENTITY = createBlockEntity("advanced_minecart_charger",
            EPBlocks.ADVANCED_MINECART_CHARGER, AdvancedMinecartChargerBlockEntity::new);

    public static final Supplier<BlockEntityType<MinecartUnchargerBlockEntity>> MINECART_UNCHARGER_ENTITY = createBlockEntity("minecart_uncharger",
            EPBlocks.MINECART_UNCHARGER, MinecartUnchargerBlockEntity::new);

    public static final Supplier<BlockEntityType<AdvancedMinecartUnchargerBlockEntity>> ADVANCED_MINECART_UNCHARGER_ENTITY = createBlockEntity("advanced_minecart_uncharger",
            EPBlocks.ADVANCED_MINECART_UNCHARGER, AdvancedMinecartUnchargerBlockEntity::new);

    private static Supplier<BlockEntityType<SolarPanelBlockEntity>> createSolarPanelBlockEntity(String name, Supplier<SolarPanelBlock> block) {
        return createBlockEntity(name, block, (blockPos, state) -> new SolarPanelBlockEntity(blockPos, state, block.get().getTier()));
    }
    public static final Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_1 =
            createSolarPanelBlockEntity("solar_panel_1", EPBlocks.SOLAR_PANEL_1);
    public static final Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_2 =
            createSolarPanelBlockEntity("solar_panel_2", EPBlocks.SOLAR_PANEL_2);
    public static final Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_3 =
            createSolarPanelBlockEntity("solar_panel_3", EPBlocks.SOLAR_PANEL_3);
    public static final Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_4 =
            createSolarPanelBlockEntity("solar_panel_4", EPBlocks.SOLAR_PANEL_4);
    public static final Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_5 =
            createSolarPanelBlockEntity("solar_panel_5", EPBlocks.SOLAR_PANEL_5);
    public static final Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_6 =
            createSolarPanelBlockEntity("solar_panel_6", EPBlocks.SOLAR_PANEL_6);
    public static final Supplier<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_7 =
            createSolarPanelBlockEntity("solar_panel_7", EPBlocks.SOLAR_PANEL_7);

    public static final Supplier<BlockEntityType<TransformerBlockEntity>> LV_TRANSFORMER_1_TO_N_ENTITY = createBlockEntity("lv_transformer_1_to_n",
            EPBlocks.LV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.LV, TransformerType.TYPE_1_TO_N));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> LV_TRANSFORMER_3_TO_3_ENTITY = createBlockEntity("lv_transformer_3_to_3",
            EPBlocks.LV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.LV, TransformerType.TYPE_3_TO_3));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> LV_TRANSFORMER_N_TO_1_ENTITY = createBlockEntity("lv_transformer_n_to_1",
            EPBlocks.LV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.LV, TransformerType.TYPE_N_TO_1));

    public static final Supplier<BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER_1_TO_N_ENTITY = createBlockEntity("transformer_1_to_n",
            EPBlocks.MV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.MV, TransformerType.TYPE_1_TO_N));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER_3_TO_3_ENTITY = createBlockEntity("transformer_3_to_3",
            EPBlocks.MV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.MV, TransformerType.TYPE_3_TO_3));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER_N_TO_1_ENTITY = createBlockEntity("transformer_n_to_1",
            EPBlocks.MV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.MV, TransformerType.TYPE_N_TO_1));

    public static final Supplier<BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER_1_TO_N_ENTITY = createBlockEntity("hv_transformer_1_to_n",
            EPBlocks.HV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.HV, TransformerType.TYPE_1_TO_N));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER_3_TO_3_ENTITY = createBlockEntity("hv_transformer_3_to_3",
            EPBlocks.HV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.HV, TransformerType.TYPE_3_TO_3));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER_N_TO_1_ENTITY = createBlockEntity("hv_transformer_n_to_1",
            EPBlocks.HV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.HV, TransformerType.TYPE_N_TO_1));

    public static final Supplier<BlockEntityType<TransformerBlockEntity>> EHV_TRANSFORMER_1_TO_N_ENTITY = createBlockEntity("ehv_transformer_1_to_n",
            EPBlocks.EHV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.EHV, TransformerType.TYPE_1_TO_N));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> EHV_TRANSFORMER_3_TO_3_ENTITY = createBlockEntity("ehv_transformer_3_to_3",
            EPBlocks.EHV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.EHV, TransformerType.TYPE_3_TO_3));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> EHV_TRANSFORMER_N_TO_1_ENTITY = createBlockEntity("ehv_transformer_n_to_1",
            EPBlocks.EHV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.EHV, TransformerType.TYPE_N_TO_1));

    public static final Supplier<BlockEntityType<TransformerBlockEntity>> CONFIGURABLE_LV_TRANSFORMER_ENTITY = createBlockEntity("configurable_lv_transformer",
            EPBlocks.CONFIGURABLE_LV_TRANSFORMER, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.LV, TransformerType.CONFIGURABLE));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> CONFIGURABLE_MV_TRANSFORMER_ENTITY = createBlockEntity("configurable_mv_transformer",
            EPBlocks.CONFIGURABLE_MV_TRANSFORMER, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.MV, TransformerType.CONFIGURABLE));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> CONFIGURABLE_HV_TRANSFORMER_ENTITY = createBlockEntity("configurable_hv_transformer",
            EPBlocks.CONFIGURABLE_HV_TRANSFORMER, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.HV, TransformerType.CONFIGURABLE));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> CONFIGURABLE_EHV_TRANSFORMER_ENTITY = createBlockEntity("configurable_ehv_transformer",
            EPBlocks.CONFIGURABLE_EHV_TRANSFORMER, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.EHV, TransformerType.CONFIGURABLE));

    public static final Supplier<BlockEntityType<BatteryBoxBlockEntity>> BATTERY_BOX_ENTITY = createBlockEntity("battery_box",
            EPBlocks.BATTERY_BOX, BatteryBoxBlockEntity::new);

    public static final Supplier<BlockEntityType<AdvancedBatteryBoxBlockEntity>> ADVANCED_BATTERY_BOX_ENTITY = createBlockEntity("advanced_battery_box",
            EPBlocks.ADVANCED_BATTERY_BOX, AdvancedBatteryBoxBlockEntity::new);

    public static final Supplier<BlockEntityType<CreativeBatteryBoxBlockEntity>> CREATIVE_BATTERY_BOX_ENTITY = createBlockEntity("creative_battery_box",
            EPBlocks.CREATIVE_BATTERY_BOX, CreativeBatteryBoxBlockEntity::new);

    public static final Supplier<BlockEntityType<CoalEngineBlockEntity>> COAL_ENGINE_ENTITY = createBlockEntity("coal_engine",
            EPBlocks.COAL_ENGINE, CoalEngineBlockEntity::new);

    public static final Supplier<BlockEntityType<HeatGeneratorBlockEntity>> HEAT_GENERATOR_ENTITY = createBlockEntity("heat_generator",
            EPBlocks.HEAT_GENERATOR, HeatGeneratorBlockEntity::new);

    public static final Supplier<BlockEntityType<ThermalGeneratorBlockEntity>> THERMAL_GENERATOR_ENTITY = createBlockEntity("thermal_generator",
            EPBlocks.THERMAL_GENERATOR, ThermalGeneratorBlockEntity::new);

    public static final Supplier<BlockEntityType<PoweredLampBlockEntity>> POWERED_LAMP_ENTITY = createBlockEntity("powered_lamp",
            EPBlocks.POWERED_LAMP, PoweredLampBlockEntity::new);

    public static final Supplier<BlockEntityType<PoweredFurnaceBlockEntity>> POWERED_FURNACE_ENTITY = createBlockEntity("powered_furnace",
            EPBlocks.POWERED_FURNACE, PoweredFurnaceBlockEntity::new);

    public static final Supplier<BlockEntityType<AdvancedPoweredFurnaceBlockEntity>> ADVANCED_POWERED_FURNACE_ENTITY = createBlockEntity("advanced_powered_furnace",
            EPBlocks.ADVANCED_POWERED_FURNACE, AdvancedPoweredFurnaceBlockEntity::new);

    public static final Supplier<BlockEntityType<LightningGeneratorBlockEntity>> LIGHTING_GENERATOR_ENTITY = createBlockEntity("lightning_generator",
            EPBlocks.LIGHTNING_GENERATOR, LightningGeneratorBlockEntity::new);

    public static final Supplier<BlockEntityType<EnergizerBlockEntity>> ENERGIZER_ENTITY = createBlockEntity("energizer",
            EPBlocks.ENERGIZER, EnergizerBlockEntity::new);

    public static final Supplier<BlockEntityType<ChargingStationBlockEntity>> CHARGING_STATION_ENTITY = createBlockEntity("charging_station",
            EPBlocks.CHARGING_STATION, ChargingStationBlockEntity::new);

    public static final Supplier<BlockEntityType<CrystalGrowthChamberBlockEntity>> CRYSTAL_GROWTH_CHAMBER_ENTITY = createBlockEntity("crystal_growth_chamber",
            EPBlocks.CRYSTAL_GROWTH_CHAMBER, CrystalGrowthChamberBlockEntity::new);

    public static final Supplier<BlockEntityType<WeatherControllerBlockEntity>> WEATHER_CONTROLLER_ENTITY = createBlockEntity("weather_controller",
            EPBlocks.WEATHER_CONTROLLER, WeatherControllerBlockEntity::new);

    public static final Supplier<BlockEntityType<TimeControllerBlockEntity>> TIME_CONTROLLER_ENTITY = createBlockEntity("time_controller",
            EPBlocks.TIME_CONTROLLER, TimeControllerBlockEntity::new);

    public static final Supplier<BlockEntityType<TeleporterBlockEntity>> TELEPORTER_ENTITY = createBlockEntity("teleporter",
            EPBlocks.TELEPORTER, TeleporterBlockEntity::new);

    private static <T extends BlockEntity> Supplier<BlockEntityType<T>> createBlockEntity(
            String name, Supplier<? extends Block> block, BlockEntityType.BlockEntitySupplier<? extends T> factory) {
        return BLOCK_ENTITIES.register(name, () -> new BlockEntityType<>(factory, block.get()));
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                COPPER_FLUID_PIPE_ENTITY.get(), FluidPipeBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                IRON_FLUID_PIPE_ENTITY.get(), FluidPipeBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                GOLDEN_FLUID_PIPE_ENTITY.get(), FluidPipeBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                STEEL_FLUID_PIPE_ENTITY.get(), FluidPipeBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                PRESSURIZED_FLUID_PIPE_ENTITY.get(), FluidPipeBlockEntity::getFluidHandlerCapability);

        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_TANK_SMALL_ENTITY.get(), FluidTankBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_TANK_MEDIUM_ENTITY.get(), FluidTankBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_TANK_LARGE_ENTITY.get(), FluidTankBlockEntity::getFluidHandlerCapability);

        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                CREATIVE_FLUID_TANK_ENTITY.get(), CreativeFluidTankBlockEntity::getFluidHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ITEM_SILO_TINY_ENTITY.get(), ItemSiloBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ITEM_SILO_SMALL_ENTITY.get(), ItemSiloBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ITEM_SILO_MEDIUM_ENTITY.get(), ItemSiloBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ITEM_SILO_LARGE_ENTITY.get(), ItemSiloBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ITEM_SILO_GIANT_ENTITY.get(), ItemSiloBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                CREATIVE_ITEM_SILO_ENTITY.get(), CreativeItemSiloBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                BASIC_ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FAST_ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                EXPRESS_ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                BASIC_ITEM_CONVEYOR_BELT_LOADER_ENTITY.get(), ItemConveyorBeltLoaderBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FAST_ITEM_CONVEYOR_BELT_LOADER_ENTITY.get(), ItemConveyorBeltLoaderBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ENTITY.get(), ItemConveyorBeltLoaderBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                TIN_CABLE_ENTITY.get(), CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                COPPER_CABLE_ENTITY.get(), CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                GOLD_CABLE_ENTITY.get(), CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ENERGIZED_COPPER_CABLE_ENTITY.get(), CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ENERGIZED_GOLD_CABLE_ENTITY.get(), CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY.get(), CableBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                AUTO_CRAFTER_ENTITY.get(), AutoCrafterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                AUTO_CRAFTER_ENTITY.get(), AutoCrafterBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ADVANCED_AUTO_CRAFTER_ENTITY.get(), AdvancedAutoCrafterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_AUTO_CRAFTER_ENTITY.get(), AdvancedAutoCrafterBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                PRESS_MOLD_MAKER_ENTITY.get(), PressMoldMakerBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ALLOY_FURNACE_ENTITY.get(), AlloyFurnaceBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                CRUSHER_ENTITY.get(), CrusherBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CRUSHER_ENTITY.get(), CrusherBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ADVANCED_CRUSHER_ENTITY.get(), AdvancedCrusherBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                ADVANCED_CRUSHER_ENTITY.get(), AdvancedCrusherBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_CRUSHER_ENTITY.get(), AdvancedCrusherBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                PULVERIZER_ENTITY.get(), PulverizerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                PULVERIZER_ENTITY.get(), PulverizerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ADVANCED_PULVERIZER_ENTITY.get(), AdvancedPulverizerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                ADVANCED_PULVERIZER_ENTITY.get(), AdvancedPulverizerBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_PULVERIZER_ENTITY.get(), AdvancedPulverizerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                SAWMILL_ENTITY.get(), SawmillBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SAWMILL_ENTITY.get(), SawmillBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                COMPRESSOR_ENTITY.get(), CompressorBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                COMPRESSOR_ENTITY.get(), CompressorBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                METAL_PRESS_ENTITY.get(), MetalPressBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                METAL_PRESS_ENTITY.get(), MetalPressBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                AUTO_PRESS_MOLD_MAKER_ENTITY.get(), AutoPressMoldMakerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                AUTO_PRESS_MOLD_MAKER_ENTITY.get(), AutoPressMoldMakerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                AUTO_STONECUTTER_ENTITY.get(), AutoStonecutterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                AUTO_STONECUTTER_ENTITY.get(), AutoStonecutterBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                PLANT_GROWTH_CHAMBER_ENTITY.get(), PlantGrowthChamberBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                PLANT_GROWTH_CHAMBER_ENTITY.get(), PlantGrowthChamberBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                PLANT_GROWTH_CHAMBER_ENTITY.get(), PlantGrowthChamberBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                BLOCK_PLACER_ENTITY.get(), BlockPlacerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                BLOCK_PLACER_ENTITY.get(), BlockPlacerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ASSEMBLING_MACHINE_ENTITY.get(), AssemblingMachineBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ASSEMBLING_MACHINE_ENTITY.get(), AssemblingMachineBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                INDUCTION_SMELTER_ENTITY.get(), InductionSmelterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                INDUCTION_SMELTER_ENTITY.get(), InductionSmelterBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FLUID_FREEZER_ENTITY.get(), FluidFreezerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_FREEZER_ENTITY.get(), FluidFreezerBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                FLUID_FREEZER_ENTITY.get(), FluidFreezerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                STONE_LIQUEFIER_ENTITY.get(), StoneLiquefierBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                STONE_LIQUEFIER_ENTITY.get(), StoneLiquefierBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                STONE_LIQUEFIER_ENTITY.get(), StoneLiquefierBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                STONE_SOLIDIFIER_ENTITY.get(), StoneSolidifierBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                STONE_SOLIDIFIER_ENTITY.get(), StoneSolidifierBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                STONE_SOLIDIFIER_ENTITY.get(), StoneSolidifierBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FILTRATION_PLANT_ENTITY.get(), FiltrationPlantBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FILTRATION_PLANT_ENTITY.get(), FiltrationPlantBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                FILTRATION_PLANT_ENTITY.get(), FiltrationPlantBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FLUID_TRANSPOSER_ENTITY.get(), FluidTransposerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_TRANSPOSER_ENTITY.get(), FluidTransposerBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                FLUID_TRANSPOSER_ENTITY.get(), FluidTransposerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FLUID_FILLER_ENTITY.get(), FluidFillerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_FILLER_ENTITY.get(), FluidFillerBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                FLUID_FILLER_ENTITY.get(), FluidFillerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FLUID_DRAINER_ENTITY.get(), FluidDrainerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_DRAINER_ENTITY.get(), FluidDrainerBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                FLUID_DRAINER_ENTITY.get(), FluidDrainerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FLUID_PUMP_ENTITY.get(), FluidPumpBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_PUMP_ENTITY.get(), FluidPumpBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                FLUID_PUMP_ENTITY.get(), FluidPumpBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ADVANCED_FLUID_PUMP_ENTITY.get(), AdvancedFluidPumpBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                ADVANCED_FLUID_PUMP_ENTITY.get(), AdvancedFluidPumpBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_FLUID_PUMP_ENTITY.get(), AdvancedFluidPumpBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                DRAIN_ENTITY.get(), DrainBlockEntity::getFluidHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                CHARGER_ENTITY.get(), ChargerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CHARGER_ENTITY.get(), ChargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                UNCHARGER_ENTITY.get(), UnchargerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                UNCHARGER_ENTITY.get(), UnchargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ADVANCED_CHARGER_ENTITY.get(), AdvancedChargerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_CHARGER_ENTITY.get(), AdvancedChargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ADVANCED_UNCHARGER_ENTITY.get(), AdvancedUnchargerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_UNCHARGER_ENTITY.get(), AdvancedUnchargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                MINECART_CHARGER_ENTITY.get(), MinecartChargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                MINECART_UNCHARGER_ENTITY.get(), MinecartUnchargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_MINECART_CHARGER_ENTITY.get(), AdvancedMinecartChargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_MINECART_UNCHARGER_ENTITY.get(), AdvancedMinecartUnchargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_1.get(), SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_2.get(), SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_3.get(), SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_4.get(), SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_5.get(), SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_6.get(), SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_7.get(), SolarPanelBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                LV_TRANSFORMER_1_TO_N_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                LV_TRANSFORMER_3_TO_3_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                LV_TRANSFORMER_N_TO_1_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                MV_TRANSFORMER_1_TO_N_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                MV_TRANSFORMER_3_TO_3_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                MV_TRANSFORMER_N_TO_1_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                HV_TRANSFORMER_1_TO_N_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                HV_TRANSFORMER_3_TO_3_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                HV_TRANSFORMER_N_TO_1_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                EHV_TRANSFORMER_1_TO_N_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                EHV_TRANSFORMER_3_TO_3_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                EHV_TRANSFORMER_N_TO_1_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CONFIGURABLE_LV_TRANSFORMER_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CONFIGURABLE_MV_TRANSFORMER_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CONFIGURABLE_HV_TRANSFORMER_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CONFIGURABLE_EHV_TRANSFORMER_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                BATTERY_BOX_ENTITY.get(), BatteryBoxBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_BATTERY_BOX_ENTITY.get(), AdvancedBatteryBoxBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CREATIVE_BATTERY_BOX_ENTITY.get(), CreativeBatteryBoxBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                COAL_ENGINE_ENTITY.get(), CoalEngineBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                COAL_ENGINE_ENTITY.get(), CoalEngineBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                HEAT_GENERATOR_ENTITY.get(), HeatGeneratorBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                THERMAL_GENERATOR_ENTITY.get(), ThermalGeneratorBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                THERMAL_GENERATOR_ENTITY.get(), ThermalGeneratorBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                POWERED_LAMP_ENTITY.get(), PoweredLampBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                POWERED_FURNACE_ENTITY.get(), PoweredFurnaceBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                POWERED_FURNACE_ENTITY.get(), PoweredFurnaceBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ADVANCED_POWERED_FURNACE_ENTITY.get(), AdvancedPoweredFurnaceBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_POWERED_FURNACE_ENTITY.get(), AdvancedPoweredFurnaceBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                LIGHTING_GENERATOR_ENTITY.get(), LightningGeneratorBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ENERGIZER_ENTITY.get(), EnergizerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ENERGIZER_ENTITY.get(), EnergizerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CHARGING_STATION_ENTITY.get(), ChargingStationBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                CRYSTAL_GROWTH_CHAMBER_ENTITY.get(), CrystalGrowthChamberBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CRYSTAL_GROWTH_CHAMBER_ENTITY.get(), CrystalGrowthChamberBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                WEATHER_CONTROLLER_ENTITY.get(), WeatherControllerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                TIME_CONTROLLER_ENTITY.get(), TimeControllerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                TELEPORTER_ENTITY.get(), TeleporterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                TELEPORTER_ENTITY.get(), TeleporterBlockEntity::getEnergyStorageCapability);
    }
}
