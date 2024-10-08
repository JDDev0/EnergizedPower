package me.jddev0.ep.block.entity;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class EPBlockEntities {
    private EPBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, EPAPI.MOD_ID);

    private static Supplier<BlockEntityType<FluidPipeBlockEntity>> createFluidPipeBlockEntity(String name,
                                                                                              Supplier<FluidPipeBlock> blockSupplier) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of((blockPos, state) -> new FluidPipeBlockEntity(blockPos, state,
                blockSupplier.get().getTier()), blockSupplier.get()).build(null));
    }
    public static final Supplier<BlockEntityType<FluidPipeBlockEntity>> IRON_FLUID_PIPE_ENTITY =
            createFluidPipeBlockEntity("fluid_pipe", EPBlocks.IRON_FLUID_PIPE);
    public static final Supplier<BlockEntityType<FluidPipeBlockEntity>> GOLDEN_FLUID_PIPE_ENTITY =
            createFluidPipeBlockEntity("golden_fluid_pipe", EPBlocks.GOLDEN_FLUID_PIPE);

    private static Supplier<BlockEntityType<FluidTankBlockEntity>> createFluidTankBlockEntity(String name,
                                                                                              Supplier<FluidTankBlock> blockSupplier) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of((blockPos, state) -> new FluidTankBlockEntity(blockPos, state,
                blockSupplier.get().getTier()), blockSupplier.get()).build(null));
    }
    public static final Supplier<BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_SMALL_ENTITY =
            createFluidTankBlockEntity("fluid_tank_small", EPBlocks.FLUID_TANK_SMALL);
    public static final Supplier<BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_MEDIUM_ENTITY =
            createFluidTankBlockEntity("fluid_tank_medium", EPBlocks.FLUID_TANK_MEDIUM);
    public static final Supplier<BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_LARGE_ENTITY =
            createFluidTankBlockEntity("fluid_tank_large", EPBlocks.FLUID_TANK_LARGE);

    public static final Supplier<BlockEntityType<CreativeFluidTankBlockEntity>> CREATIVE_FLUID_TANK_ENTITY =
            BLOCK_ENTITIES.register("creative_fluid_tank", () -> BlockEntityType.Builder.of(CreativeFluidTankBlockEntity::new,
                    EPBlocks.CREATIVE_FLUID_TANK.get()).build(null));

    public static final Supplier<BlockEntityType<ItemConveyorBeltBlockEntity>> ITEM_CONVEYOR_BELT_ENTITY =
            BLOCK_ENTITIES.register("item_conveyor_belt", () -> BlockEntityType.Builder.of(ItemConveyorBeltBlockEntity::new,
                    EPBlocks.ITEM_CONVEYOR_BELT.get()).build(null));

    public static final Supplier<BlockEntityType<ItemConveyorBeltLoaderBlockEntity>> ITEM_CONVEYOR_BELT_LOADER_ENTITY =
            BLOCK_ENTITIES.register("item_conveyor_belt_loader", () -> BlockEntityType.Builder.of(ItemConveyorBeltLoaderBlockEntity::new,
                    EPBlocks.ITEM_CONVEYOR_BELT_LOADER.get()).build(null));

    public static final Supplier<BlockEntityType<ItemConveyorBeltSorterBlockEntity>> ITEM_CONVEYOR_BELT_SORTER_ENTITY =
            BLOCK_ENTITIES.register("item_conveyor_belt_sorter", () -> BlockEntityType.Builder.of(ItemConveyorBeltSorterBlockEntity::new,
                    EPBlocks.ITEM_CONVEYOR_BELT_SORTER.get()).build(null));

    public static final Supplier<BlockEntityType<ItemConveyorBeltSwitchBlockEntity>> ITEM_CONVEYOR_BELT_SWITCH_ENTITY =
            BLOCK_ENTITIES.register("item_conveyor_belt_switch", () -> BlockEntityType.Builder.of(ItemConveyorBeltSwitchBlockEntity::new,
                    EPBlocks.ITEM_CONVEYOR_BELT_SWITCH.get()).build(null));

    public static final Supplier<BlockEntityType<ItemConveyorBeltSplitterBlockEntity>> ITEM_CONVEYOR_BELT_SPLITTER_ENTITY =
            BLOCK_ENTITIES.register("item_conveyor_belt_splitter", () -> BlockEntityType.Builder.of(ItemConveyorBeltSplitterBlockEntity::new,
                    EPBlocks.ITEM_CONVEYOR_BELT_SPLITTER.get()).build(null));

    public static final Supplier<BlockEntityType<ItemConveyorBeltMergerBlockEntity>> ITEM_CONVEYOR_BELT_MERGER_ENTITY =
            BLOCK_ENTITIES.register("item_conveyor_belt_merger", () -> BlockEntityType.Builder.of(ItemConveyorBeltMergerBlockEntity::new,
                    EPBlocks.ITEM_CONVEYOR_BELT_MERGER.get()).build(null));

    public static final Supplier<BlockEntityType<CableBlockEntity>> TIN_CABLE_ENTITY =
            BLOCK_ENTITIES.register("tin_cable", () -> BlockEntityType.Builder.of((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_TIN), EPBlocks.TIN_CABLE.get()).build(null));
    public static final Supplier<BlockEntityType<CableBlockEntity>> COPPER_CABLE_ENTITY =
            BLOCK_ENTITIES.register("copper_cable", () -> BlockEntityType.Builder.of((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_COPPER), EPBlocks.COPPER_CABLE.get()).build(null));
    public static final Supplier<BlockEntityType<CableBlockEntity>> GOLD_CABLE_ENTITY =
            BLOCK_ENTITIES.register("gold_cable", () -> BlockEntityType.Builder.of((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_GOLD), EPBlocks.GOLD_CABLE.get()).build(null));
    public static final Supplier<BlockEntityType<CableBlockEntity>> ENERGIZED_COPPER_CABLE_ENTITY =
            BLOCK_ENTITIES.register("energized_copper_cable", () -> BlockEntityType.Builder.of((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_COPPER), EPBlocks.ENERGIZED_COPPER_CABLE.get()).build(null));
    public static final Supplier<BlockEntityType<CableBlockEntity>> ENERGIZED_GOLD_CABLE_ENTITY =
            BLOCK_ENTITIES.register("energized_gold_cable", () -> BlockEntityType.Builder.of((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_GOLD), EPBlocks.ENERGIZED_GOLD_CABLE.get()).build(null));
    public static final Supplier<BlockEntityType<CableBlockEntity>> ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY =
            BLOCK_ENTITIES.register("energized_crystal_matrix_cable", () -> BlockEntityType.Builder.of((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_CRYSTAL_MATRIX), EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE.get()).build(null));

    public static final Supplier<BlockEntityType<AutoCrafterBlockEntity>> AUTO_CRAFTER_ENTITY =
            BLOCK_ENTITIES.register("auto_crafter", () -> BlockEntityType.Builder.of(AutoCrafterBlockEntity::new,
                    EPBlocks.AUTO_CRAFTER.get()).build(null));

    public static final Supplier<BlockEntityType<AdvancedAutoCrafterBlockEntity>> ADVANCED_AUTO_CRAFTER_ENTITY =
            BLOCK_ENTITIES.register("advanced_auto_crafter", () -> BlockEntityType.Builder.of(AdvancedAutoCrafterBlockEntity::new,
                    EPBlocks.ADVANCED_AUTO_CRAFTER.get()).build(null));

    public static final Supplier<BlockEntityType<PressMoldMakerBlockEntity>> PRESS_MOLD_MAKER_ENTITY =
            BLOCK_ENTITIES.register("press_mold_maker", () -> BlockEntityType.Builder.of(PressMoldMakerBlockEntity::new,
                    EPBlocks.PRESS_MOLD_MAKER.get()).build(null));

    public static final Supplier<BlockEntityType<AlloyFurnaceBlockEntity>> ALLOY_FURNACE_ENTITY =
            BLOCK_ENTITIES.register("alloy_furnace", () -> BlockEntityType.Builder.of(AlloyFurnaceBlockEntity::new,
                    EPBlocks.ALLOY_FURNACE.get()).build(null));

    public static final Supplier<BlockEntityType<CrusherBlockEntity>> CRUSHER_ENTITY =
            BLOCK_ENTITIES.register("crusher", () -> BlockEntityType.Builder.of(CrusherBlockEntity::new,
                    EPBlocks.CRUSHER.get()).build(null));

    public static final Supplier<BlockEntityType<AdvancedCrusherBlockEntity>> ADVANCED_CRUSHER_ENTITY =
            BLOCK_ENTITIES.register("advanced_crusher", () -> BlockEntityType.Builder.of(AdvancedCrusherBlockEntity::new,
                    EPBlocks.ADVANCED_CRUSHER.get()).build(null));

    public static final Supplier<BlockEntityType<PulverizerBlockEntity>> PULVERIZER_ENTITY =
            BLOCK_ENTITIES.register("pulverizer", () -> BlockEntityType.Builder.of(PulverizerBlockEntity::new,
                    EPBlocks.PULVERIZER.get()).build(null));

    public static final Supplier<BlockEntityType<AdvancedPulverizerBlockEntity>> ADVANCED_PULVERIZER_ENTITY =
            BLOCK_ENTITIES.register("advanced_pulverizer", () -> BlockEntityType.Builder.of(AdvancedPulverizerBlockEntity::new,
                    EPBlocks.ADVANCED_PULVERIZER.get()).build(null));

    public static final Supplier<BlockEntityType<SawmillBlockEntity>> SAWMILL_ENTITY =
            BLOCK_ENTITIES.register("sawmill", () -> BlockEntityType.Builder.of(SawmillBlockEntity::new,
                    EPBlocks.SAWMILL.get()).build(null));

    public static final Supplier<BlockEntityType<CompressorBlockEntity>> COMPRESSOR_ENTITY =
            BLOCK_ENTITIES.register("compressor", () -> BlockEntityType.Builder.of(CompressorBlockEntity::new,
                    EPBlocks.COMPRESSOR.get()).build(null));

    public static final Supplier<BlockEntityType<MetalPressBlockEntity>> METAL_PRESS_ENTITY =
            BLOCK_ENTITIES.register("metal_press", () -> BlockEntityType.Builder.of(MetalPressBlockEntity::new,
                    EPBlocks.METAL_PRESS.get()).build(null));

    public static final Supplier<BlockEntityType<AutoPressMoldMakerBlockEntity>> AUTO_PRESS_MOLD_MAKER_ENTITY =
            BLOCK_ENTITIES.register("auto_press_mold_maker", () -> BlockEntityType.Builder.of(AutoPressMoldMakerBlockEntity::new,
                    EPBlocks.AUTO_PRESS_MOLD_MAKER.get()).build(null));

    public static final Supplier<BlockEntityType<AutoStonecutterBlockEntity>> AUTO_STONECUTTER_ENTITY =
            BLOCK_ENTITIES.register("auto_stonecutter", () -> BlockEntityType.Builder.of(AutoStonecutterBlockEntity::new,
                    EPBlocks.AUTO_STONECUTTER.get()).build(null));

    public static final Supplier<BlockEntityType<PlantGrowthChamberBlockEntity>> PLANT_GROWTH_CHAMBER_ENTITY =
            BLOCK_ENTITIES.register("plant_growth_chamber", () -> BlockEntityType.Builder.of(PlantGrowthChamberBlockEntity::new,
                    EPBlocks.PLANT_GROWTH_CHAMBER.get()).build(null));

    public static final Supplier<BlockEntityType<BlockPlacerBlockEntity>> BLOCK_PLACER_ENTITY =
            BLOCK_ENTITIES.register("block_placer", () -> BlockEntityType.Builder.of(BlockPlacerBlockEntity::new,
                    EPBlocks.BLOCK_PLACER.get()).build(null));

    public static final Supplier<BlockEntityType<AssemblingMachineBlockEntity>> ASSEMBLING_MACHINE_ENTITY =
            BLOCK_ENTITIES.register("assembling_machine", () -> BlockEntityType.Builder.of(AssemblingMachineBlockEntity::new,
                    EPBlocks.ASSEMBLING_MACHINE.get()).build(null));

    public static final Supplier<BlockEntityType<InductionSmelterBlockEntity>> INDUCTION_SMELTER_ENTITY =
            BLOCK_ENTITIES.register("induction_smelter", () -> BlockEntityType.Builder.of(InductionSmelterBlockEntity::new,
                    EPBlocks.INDUCTION_SMELTER.get()).build(null));

    public static final Supplier<BlockEntityType<StoneSolidifierBlockEntity>> STONE_SOLIDIFIER_ENTITY =
            BLOCK_ENTITIES.register("stone_solidifier", () -> BlockEntityType.Builder.of(StoneSolidifierBlockEntity::new,
                    EPBlocks.STONE_SOLIDIFIER.get()).build(null));

    public static final Supplier<BlockEntityType<FiltrationPlantBlockEntity>> FILTRATION_PLANT_ENTITY =
            BLOCK_ENTITIES.register("filtration_plant", () -> BlockEntityType.Builder.of(FiltrationPlantBlockEntity::new,
                    EPBlocks.FILTRATION_PLANT.get()).build(null));

    public static final Supplier<BlockEntityType<FluidTransposerBlockEntity>> FLUID_TRANSPOSER_ENTITY =
            BLOCK_ENTITIES.register("fluid_transposer", () -> BlockEntityType.Builder.of(FluidTransposerBlockEntity::new,
                    EPBlocks.FLUID_TRANSPOSER.get()).build(null));

    public static final Supplier<BlockEntityType<FluidFillerBlockEntity>> FLUID_FILLER_ENTITY =
            BLOCK_ENTITIES.register("fluid_filler", () -> BlockEntityType.Builder.of(FluidFillerBlockEntity::new,
                    EPBlocks.FLUID_FILLER.get()).build(null));

    public static final Supplier<BlockEntityType<FluidDrainerBlockEntity>> FLUID_DRAINER_ENTITY =
            BLOCK_ENTITIES.register("fluid_drainer", () -> BlockEntityType.Builder.of(FluidDrainerBlockEntity::new,
                    EPBlocks.FLUID_DRAINER.get()).build(null));

    public static final Supplier<BlockEntityType<FluidPumpBlockEntity>> FLUID_PUMP_ENTITY =
            BLOCK_ENTITIES.register("fluid_pump", () -> BlockEntityType.Builder.of(FluidPumpBlockEntity::new,
                    EPBlocks.FLUID_PUMP.get()).build(null));

    public static final Supplier<BlockEntityType<DrainBlockEntity>> DRAIN_ENTITY =
            BLOCK_ENTITIES.register("drain", () -> BlockEntityType.Builder.of(DrainBlockEntity::new,
                    EPBlocks.DRAIN.get()).build(null));

    public static final Supplier<BlockEntityType<ChargerBlockEntity>> CHARGER_ENTITY =
            BLOCK_ENTITIES.register("charger", () -> BlockEntityType.Builder.of(ChargerBlockEntity::new,
                    EPBlocks.CHARGER.get()).build(null));

    public static final Supplier<BlockEntityType<AdvancedChargerBlockEntity>> ADVANCED_CHARGER_ENTITY =
            BLOCK_ENTITIES.register("advanced_charger", () -> BlockEntityType.Builder.of(AdvancedChargerBlockEntity::new,
                    EPBlocks.ADVANCED_CHARGER.get()).build(null));

    public static final Supplier<BlockEntityType<UnchargerBlockEntity>> UNCHARGER_ENTITY =
            BLOCK_ENTITIES.register("uncharger", () -> BlockEntityType.Builder.of(UnchargerBlockEntity::new,
                    EPBlocks.UNCHARGER.get()).build(null));

    public static final Supplier<BlockEntityType<AdvancedUnchargerBlockEntity>> ADVANCED_UNCHARGER_ENTITY =
            BLOCK_ENTITIES.register("advanced_uncharger", () -> BlockEntityType.Builder.of(AdvancedUnchargerBlockEntity::new,
                    EPBlocks.ADVANCED_UNCHARGER.get()).build(null));

    public static final Supplier<BlockEntityType<MinecartChargerBlockEntity>> MINECART_CHARGER_ENTITY =
            BLOCK_ENTITIES.register("minecart_charger", () -> BlockEntityType.Builder.of(MinecartChargerBlockEntity::new,
                    EPBlocks.MINECART_CHARGER.get()).build(null));

    public static final Supplier<BlockEntityType<AdvancedMinecartChargerBlockEntity>> ADVANCED_MINECART_CHARGER_ENTITY =
            BLOCK_ENTITIES.register("advanced_minecart_charger", () -> BlockEntityType.Builder.of(AdvancedMinecartChargerBlockEntity::new,
                    EPBlocks.ADVANCED_MINECART_CHARGER.get()).build(null));

    public static final Supplier<BlockEntityType<MinecartUnchargerBlockEntity>> MINECART_UNCHARGER_ENTITY =
            BLOCK_ENTITIES.register("minecart_uncharger", () -> BlockEntityType.Builder.of(MinecartUnchargerBlockEntity::new,
                    EPBlocks.MINECART_UNCHARGER.get()).build(null));

    public static final Supplier<BlockEntityType<AdvancedMinecartUnchargerBlockEntity>> ADVANCED_MINECART_UNCHARGER_ENTITY =
            BLOCK_ENTITIES.register("advanced_minecart_uncharger", () -> BlockEntityType.Builder.of(AdvancedMinecartUnchargerBlockEntity::new,
                    EPBlocks.ADVANCED_MINECART_UNCHARGER.get()).build(null));

    private static Supplier<BlockEntityType<SolarPanelBlockEntity>> createSolarPanelBlockEntity(String name,
    Supplier<SolarPanelBlock> blockSupplier) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of((blockPos, state) -> new SolarPanelBlockEntity(blockPos, state,
                        blockSupplier.get().getTier()), blockSupplier.get()).build(null));
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

    public static final Supplier<BlockEntityType<TransformerBlockEntity>> LV_TRANSFORMER_1_TO_N_ENTITY =
            BLOCK_ENTITIES.register("lv_transformer_1_to_n", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_LV,
                                    TransformerBlock.Type.TYPE_1_TO_N),
                    EPBlocks.LV_TRANSFORMER_1_TO_N.get()).build(null));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> LV_TRANSFORMER_3_TO_3_ENTITY =
            BLOCK_ENTITIES.register("lv_transformer_3_to_3", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_LV,
                                    TransformerBlock.Type.TYPE_3_TO_3),
                    EPBlocks.LV_TRANSFORMER_3_TO_3.get()).build(null));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> LV_TRANSFORMER_N_TO_1_ENTITY =
            BLOCK_ENTITIES.register("lv_transformer_n_to_1", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_LV,
                                    TransformerBlock.Type.TYPE_N_TO_1),
                    EPBlocks.LV_TRANSFORMER_N_TO_1.get()).build(null));

    public static final Supplier<BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER_1_TO_N_ENTITY =
            BLOCK_ENTITIES.register("transformer_1_to_n", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV,
                                    TransformerBlock.Type.TYPE_1_TO_N),
                    EPBlocks.MV_TRANSFORMER_1_TO_N.get()).build(null));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER_3_TO_3_ENTITY =
            BLOCK_ENTITIES.register("transformer_3_to_3", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV,
                                    TransformerBlock.Type.TYPE_3_TO_3),
                    EPBlocks.MV_TRANSFORMER_3_TO_3.get()).build(null));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER_N_TO_1_ENTITY =
            BLOCK_ENTITIES.register("transformer_n_to_1", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV,
                                    TransformerBlock.Type.TYPE_N_TO_1),
                    EPBlocks.MV_TRANSFORMER_N_TO_1.get()).build(null));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER_1_TO_N_ENTITY =
            BLOCK_ENTITIES.register("hv_transformer_1_to_n", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV,
                                    TransformerBlock.Type.TYPE_1_TO_N),
                    EPBlocks.HV_TRANSFORMER_1_TO_N.get()).build(null));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER_3_TO_3_ENTITY =
            BLOCK_ENTITIES.register("hv_transformer_3_to_3", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV,
                                    TransformerBlock.Type.TYPE_3_TO_3),
                    EPBlocks.HV_TRANSFORMER_3_TO_3.get()).build(null));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER_N_TO_1_ENTITY =
            BLOCK_ENTITIES.register("hv_transformer_n_to_1", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV,
                                    TransformerBlock.Type.TYPE_N_TO_1),
                    EPBlocks.HV_TRANSFORMER_N_TO_1.get()).build(null));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> EHV_TRANSFORMER_1_TO_N_ENTITY =
            BLOCK_ENTITIES.register("ehv_transformer_1_to_n", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV,
                                    TransformerBlock.Type.TYPE_1_TO_N),
                    EPBlocks.EHV_TRANSFORMER_1_TO_N.get()).build(null));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> EHV_TRANSFORMER_3_TO_3_ENTITY =
            BLOCK_ENTITIES.register("ehv_transformer_3_to_3", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV,
                                    TransformerBlock.Type.TYPE_3_TO_3),
                    EPBlocks.EHV_TRANSFORMER_3_TO_3.get()).build(null));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> EHV_TRANSFORMER_N_TO_1_ENTITY =
            BLOCK_ENTITIES.register("ehv_transformer_n_to_1", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV,
                                    TransformerBlock.Type.TYPE_N_TO_1),
                    EPBlocks.EHV_TRANSFORMER_N_TO_1.get()).build(null));

    public static final Supplier<BlockEntityType<BatteryBoxBlockEntity>> BATTERY_BOX_ENTITY =
            BLOCK_ENTITIES.register("battery_box", () -> BlockEntityType.Builder.of(BatteryBoxBlockEntity::new,
                    EPBlocks.BATTERY_BOX.get()).build(null));

    public static final Supplier<BlockEntityType<AdvancedBatteryBoxBlockEntity>> ADVANCED_BATTERY_BOX_ENTITY =
            BLOCK_ENTITIES.register("advanced_battery_box", () -> BlockEntityType.Builder.of(AdvancedBatteryBoxBlockEntity::new,
                    EPBlocks.ADVANCED_BATTERY_BOX.get()).build(null));

    public static final Supplier<BlockEntityType<CreativeBatteryBoxBlockEntity>> CREATIVE_BATTERY_BOX_ENTITY =
            BLOCK_ENTITIES.register("creative_battery_box", () -> BlockEntityType.Builder.of(CreativeBatteryBoxBlockEntity::new,
                    EPBlocks.CREATIVE_BATTERY_BOX.get()).build(null));

    public static final Supplier<BlockEntityType<CoalEngineBlockEntity>> COAL_ENGINE_ENTITY =
            BLOCK_ENTITIES.register("coal_engine", () -> BlockEntityType.Builder.of(CoalEngineBlockEntity::new,
                    EPBlocks.COAL_ENGINE.get()).build(null));

    public static final Supplier<BlockEntityType<HeatGeneratorBlockEntity>> HEAT_GENERATOR_ENTITY =
            BLOCK_ENTITIES.register("heat_generator", () -> BlockEntityType.Builder.of(HeatGeneratorBlockEntity::new,
                    EPBlocks.HEAT_GENERATOR.get()).build(null));

    public static final Supplier<BlockEntityType<ThermalGeneratorBlockEntity>> THERMAL_GENERATOR_ENTITY =
            BLOCK_ENTITIES.register("thermal_generator", () -> BlockEntityType.Builder.of(ThermalGeneratorBlockEntity::new,
                    EPBlocks.THERMAL_GENERATOR.get()).build(null));

    public static final Supplier<BlockEntityType<PoweredLampBlockEntity>> POWERED_LAMP_ENTITY =
            BLOCK_ENTITIES.register("powered_lamp", () -> BlockEntityType.Builder.of(PoweredLampBlockEntity::new,
                    EPBlocks.POWERED_LAMP.get()).build(null));

    public static final Supplier<BlockEntityType<PoweredFurnaceBlockEntity>> POWERED_FURNACE_ENTITY =
            BLOCK_ENTITIES.register("powered_furnace", () -> BlockEntityType.Builder.of(PoweredFurnaceBlockEntity::new,
                    EPBlocks.POWERED_FURNACE.get()).build(null));

    public static final Supplier<BlockEntityType<AdvancedPoweredFurnaceBlockEntity>> ADVANCED_POWERED_FURNACE_ENTITY =
            BLOCK_ENTITIES.register("advanced_powered_furnace", () -> BlockEntityType.Builder.of(AdvancedPoweredFurnaceBlockEntity::new,
                    EPBlocks.ADVANCED_POWERED_FURNACE.get()).build(null));

    public static final Supplier<BlockEntityType<LightningGeneratorBlockEntity>> LIGHTING_GENERATOR_ENTITY =
            BLOCK_ENTITIES.register("lightning_generator", () -> BlockEntityType.Builder.of(LightningGeneratorBlockEntity::new,
                    EPBlocks.LIGHTNING_GENERATOR.get()).build(null));

    public static final Supplier<BlockEntityType<EnergizerBlockEntity>> ENERGIZER_ENTITY =
            BLOCK_ENTITIES.register("energizer", () -> BlockEntityType.Builder.of(EnergizerBlockEntity::new,
                    EPBlocks.ENERGIZER.get()).build(null));

    public static final Supplier<BlockEntityType<ChargingStationBlockEntity>> CHARGING_STATION_ENTITY =
            BLOCK_ENTITIES.register("charging_station", () -> BlockEntityType.Builder.of(ChargingStationBlockEntity::new,
                    EPBlocks.CHARGING_STATION.get()).build(null));

    public static final Supplier<BlockEntityType<CrystalGrowthChamberBlockEntity>> CRYSTAL_GROWTH_CHAMBER_ENTITY =
            BLOCK_ENTITIES.register("crystal_growth_chamber", () -> BlockEntityType.Builder.of(CrystalGrowthChamberBlockEntity::new,
                    EPBlocks.CRYSTAL_GROWTH_CHAMBER.get()).build(null));

    public static final Supplier<BlockEntityType<WeatherControllerBlockEntity>> WEATHER_CONTROLLER_ENTITY =
            BLOCK_ENTITIES.register("weather_controller", () -> BlockEntityType.Builder.of(WeatherControllerBlockEntity::new,
                    EPBlocks.WEATHER_CONTROLLER.get()).build(null));

    public static final Supplier<BlockEntityType<TimeControllerBlockEntity>> TIME_CONTROLLER_ENTITY =
            BLOCK_ENTITIES.register("time_controller", () -> BlockEntityType.Builder.of(TimeControllerBlockEntity::new,
                    EPBlocks.TIME_CONTROLLER.get()).build(null));

    public static final Supplier<BlockEntityType<TeleporterBlockEntity>> TELEPORTER_ENTITY =
            BLOCK_ENTITIES.register("teleporter", () -> BlockEntityType.Builder.of(TeleporterBlockEntity::new,
                    EPBlocks.TELEPORTER.get()).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
