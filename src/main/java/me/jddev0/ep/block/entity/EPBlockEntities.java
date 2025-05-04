package me.jddev0.ep.block.entity;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class EPBlockEntities {
    private EPBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, EPAPI.MOD_ID);

    private static Supplier<BlockEntityType<FluidPipeBlockEntity>> createFluidPipeBlockEntity(String name,
                                                                                              Supplier<FluidPipeBlock> blockSupplier) {
        return BLOCK_ENTITIES.register(name, () -> new BlockEntityType<>((blockPos, state) -> new FluidPipeBlockEntity(blockPos, state,
                blockSupplier.get().getTier()), blockSupplier.get()));
    }
    public static final Supplier<BlockEntityType<FluidPipeBlockEntity>> IRON_FLUID_PIPE_ENTITY =
            createFluidPipeBlockEntity("fluid_pipe", EPBlocks.IRON_FLUID_PIPE);
    public static final Supplier<BlockEntityType<FluidPipeBlockEntity>> GOLDEN_FLUID_PIPE_ENTITY =
            createFluidPipeBlockEntity("golden_fluid_pipe", EPBlocks.GOLDEN_FLUID_PIPE);

    private static Supplier<BlockEntityType<FluidTankBlockEntity>> createFluidTankBlockEntity(String name,
                                                                                              Supplier<FluidTankBlock> blockSupplier) {
        return BLOCK_ENTITIES.register(name, () -> new BlockEntityType<>((blockPos, state) -> new FluidTankBlockEntity(blockPos, state,
                blockSupplier.get().getTier()), blockSupplier.get()));
    }
    public static final Supplier<BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_SMALL_ENTITY =
            createFluidTankBlockEntity("fluid_tank_small", EPBlocks.FLUID_TANK_SMALL);
    public static final Supplier<BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_MEDIUM_ENTITY =
            createFluidTankBlockEntity("fluid_tank_medium", EPBlocks.FLUID_TANK_MEDIUM);
    public static final Supplier<BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_LARGE_ENTITY =
            createFluidTankBlockEntity("fluid_tank_large", EPBlocks.FLUID_TANK_LARGE);

    public static final Supplier<BlockEntityType<CreativeFluidTankBlockEntity>> CREATIVE_FLUID_TANK_ENTITY =
            BLOCK_ENTITIES.register("creative_fluid_tank", () -> new BlockEntityType<>(CreativeFluidTankBlockEntity::new,
                    EPBlocks.CREATIVE_FLUID_TANK.get()));

    public static final Supplier<BlockEntityType<ItemConveyorBeltBlockEntity>> ITEM_CONVEYOR_BELT_ENTITY =
            BLOCK_ENTITIES.register("item_conveyor_belt", () -> new BlockEntityType<>(ItemConveyorBeltBlockEntity::new,
                    EPBlocks.ITEM_CONVEYOR_BELT.get()));

    public static final Supplier<BlockEntityType<ItemConveyorBeltLoaderBlockEntity>> ITEM_CONVEYOR_BELT_LOADER_ENTITY =
            BLOCK_ENTITIES.register("item_conveyor_belt_loader", () -> new BlockEntityType<>(ItemConveyorBeltLoaderBlockEntity::new,
                    EPBlocks.ITEM_CONVEYOR_BELT_LOADER.get()));

    public static final Supplier<BlockEntityType<ItemConveyorBeltSorterBlockEntity>> ITEM_CONVEYOR_BELT_SORTER_ENTITY =
            BLOCK_ENTITIES.register("item_conveyor_belt_sorter", () -> new BlockEntityType<>(ItemConveyorBeltSorterBlockEntity::new,
                    EPBlocks.ITEM_CONVEYOR_BELT_SORTER.get()));

    public static final Supplier<BlockEntityType<ItemConveyorBeltSwitchBlockEntity>> ITEM_CONVEYOR_BELT_SWITCH_ENTITY =
            BLOCK_ENTITIES.register("item_conveyor_belt_switch", () -> new BlockEntityType<>(ItemConveyorBeltSwitchBlockEntity::new,
                    EPBlocks.ITEM_CONVEYOR_BELT_SWITCH.get()));

    public static final Supplier<BlockEntityType<ItemConveyorBeltSplitterBlockEntity>> ITEM_CONVEYOR_BELT_SPLITTER_ENTITY =
            BLOCK_ENTITIES.register("item_conveyor_belt_splitter", () -> new BlockEntityType<>(ItemConveyorBeltSplitterBlockEntity::new,
                    EPBlocks.ITEM_CONVEYOR_BELT_SPLITTER.get()));

    public static final Supplier<BlockEntityType<ItemConveyorBeltMergerBlockEntity>> ITEM_CONVEYOR_BELT_MERGER_ENTITY =
            BLOCK_ENTITIES.register("item_conveyor_belt_merger", () -> new BlockEntityType<>(ItemConveyorBeltMergerBlockEntity::new,
                    EPBlocks.ITEM_CONVEYOR_BELT_MERGER.get()));

    public static final Supplier<BlockEntityType<CableBlockEntity>> TIN_CABLE_ENTITY =
            BLOCK_ENTITIES.register("tin_cable", () -> new BlockEntityType<>((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_TIN), EPBlocks.TIN_CABLE.get()));
    public static final Supplier<BlockEntityType<CableBlockEntity>> COPPER_CABLE_ENTITY =
            BLOCK_ENTITIES.register("copper_cable", () -> new BlockEntityType<>((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_COPPER), EPBlocks.COPPER_CABLE.get()));
    public static final Supplier<BlockEntityType<CableBlockEntity>> GOLD_CABLE_ENTITY =
            BLOCK_ENTITIES.register("gold_cable", () -> new BlockEntityType<>((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_GOLD), EPBlocks.GOLD_CABLE.get()));
    public static final Supplier<BlockEntityType<CableBlockEntity>> ENERGIZED_COPPER_CABLE_ENTITY =
            BLOCK_ENTITIES.register("energized_copper_cable", () -> new BlockEntityType<>((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_COPPER), EPBlocks.ENERGIZED_COPPER_CABLE.get()));
    public static final Supplier<BlockEntityType<CableBlockEntity>> ENERGIZED_GOLD_CABLE_ENTITY =
            BLOCK_ENTITIES.register("energized_gold_cable", () -> new BlockEntityType<>((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_GOLD), EPBlocks.ENERGIZED_GOLD_CABLE.get()));
    public static final Supplier<BlockEntityType<CableBlockEntity>> ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY =
            BLOCK_ENTITIES.register("energized_crystal_matrix_cable", () -> new BlockEntityType<>((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_CRYSTAL_MATRIX), EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE.get()));

    public static final Supplier<BlockEntityType<AutoCrafterBlockEntity>> AUTO_CRAFTER_ENTITY =
            BLOCK_ENTITIES.register("auto_crafter", () -> new BlockEntityType<>(AutoCrafterBlockEntity::new,
                    EPBlocks.AUTO_CRAFTER.get()));

    public static final Supplier<BlockEntityType<AdvancedAutoCrafterBlockEntity>> ADVANCED_AUTO_CRAFTER_ENTITY =
            BLOCK_ENTITIES.register("advanced_auto_crafter", () -> new BlockEntityType<>(AdvancedAutoCrafterBlockEntity::new,
                    EPBlocks.ADVANCED_AUTO_CRAFTER.get()));

    public static final Supplier<BlockEntityType<PressMoldMakerBlockEntity>> PRESS_MOLD_MAKER_ENTITY =
            BLOCK_ENTITIES.register("press_mold_maker", () -> new BlockEntityType<>(PressMoldMakerBlockEntity::new,
                    EPBlocks.PRESS_MOLD_MAKER.get()));

    public static final Supplier<BlockEntityType<AlloyFurnaceBlockEntity>> ALLOY_FURNACE_ENTITY =
            BLOCK_ENTITIES.register("alloy_furnace", () -> new BlockEntityType<>(AlloyFurnaceBlockEntity::new,
                    EPBlocks.ALLOY_FURNACE.get()));

    public static final Supplier<BlockEntityType<CrusherBlockEntity>> CRUSHER_ENTITY =
            BLOCK_ENTITIES.register("crusher", () -> new BlockEntityType<>(CrusherBlockEntity::new,
                    EPBlocks.CRUSHER.get()));

    public static final Supplier<BlockEntityType<AdvancedCrusherBlockEntity>> ADVANCED_CRUSHER_ENTITY =
            BLOCK_ENTITIES.register("advanced_crusher", () -> new BlockEntityType<>(AdvancedCrusherBlockEntity::new,
                    EPBlocks.ADVANCED_CRUSHER.get()));

    public static final Supplier<BlockEntityType<PulverizerBlockEntity>> PULVERIZER_ENTITY =
            BLOCK_ENTITIES.register("pulverizer", () -> new BlockEntityType<>(PulverizerBlockEntity::new,
                    EPBlocks.PULVERIZER.get()));

    public static final Supplier<BlockEntityType<AdvancedPulverizerBlockEntity>> ADVANCED_PULVERIZER_ENTITY =
            BLOCK_ENTITIES.register("advanced_pulverizer", () -> new BlockEntityType<>(AdvancedPulverizerBlockEntity::new,
                    EPBlocks.ADVANCED_PULVERIZER.get()));

    public static final Supplier<BlockEntityType<SawmillBlockEntity>> SAWMILL_ENTITY =
            BLOCK_ENTITIES.register("sawmill", () -> new BlockEntityType<>(SawmillBlockEntity::new,
                    EPBlocks.SAWMILL.get()));

    public static final Supplier<BlockEntityType<CompressorBlockEntity>> COMPRESSOR_ENTITY =
            BLOCK_ENTITIES.register("compressor", () -> new BlockEntityType<>(CompressorBlockEntity::new,
                    EPBlocks.COMPRESSOR.get()));

    public static final Supplier<BlockEntityType<MetalPressBlockEntity>> METAL_PRESS_ENTITY =
            BLOCK_ENTITIES.register("metal_press", () -> new BlockEntityType<>(MetalPressBlockEntity::new,
                    EPBlocks.METAL_PRESS.get()));

    public static final Supplier<BlockEntityType<AutoPressMoldMakerBlockEntity>> AUTO_PRESS_MOLD_MAKER_ENTITY =
            BLOCK_ENTITIES.register("auto_press_mold_maker", () -> new BlockEntityType<>(AutoPressMoldMakerBlockEntity::new,
                    EPBlocks.AUTO_PRESS_MOLD_MAKER.get()));

    public static final Supplier<BlockEntityType<AutoStonecutterBlockEntity>> AUTO_STONECUTTER_ENTITY =
            BLOCK_ENTITIES.register("auto_stonecutter", () -> new BlockEntityType<>(AutoStonecutterBlockEntity::new,
                    EPBlocks.AUTO_STONECUTTER.get()));

    public static final Supplier<BlockEntityType<PlantGrowthChamberBlockEntity>> PLANT_GROWTH_CHAMBER_ENTITY =
            BLOCK_ENTITIES.register("plant_growth_chamber", () -> new BlockEntityType<>(PlantGrowthChamberBlockEntity::new,
                    EPBlocks.PLANT_GROWTH_CHAMBER.get()));

    public static final Supplier<BlockEntityType<BlockPlacerBlockEntity>> BLOCK_PLACER_ENTITY =
            BLOCK_ENTITIES.register("block_placer", () -> new BlockEntityType<>(BlockPlacerBlockEntity::new,
                    EPBlocks.BLOCK_PLACER.get()));

    public static final Supplier<BlockEntityType<AssemblingMachineBlockEntity>> ASSEMBLING_MACHINE_ENTITY =
            BLOCK_ENTITIES.register("assembling_machine", () -> new BlockEntityType<>(AssemblingMachineBlockEntity::new,
                    EPBlocks.ASSEMBLING_MACHINE.get()));

    public static final Supplier<BlockEntityType<InductionSmelterBlockEntity>> INDUCTION_SMELTER_ENTITY =
            BLOCK_ENTITIES.register("induction_smelter", () -> new BlockEntityType<>(InductionSmelterBlockEntity::new,
                    EPBlocks.INDUCTION_SMELTER.get()));

    public static final Supplier<BlockEntityType<StoneLiquefierBlockEntity>> STONE_LIQUEFIER_ENTITY =
            BLOCK_ENTITIES.register("stone_liquefier", () -> new BlockEntityType<>(StoneLiquefierBlockEntity::new,
                    EPBlocks.STONE_LIQUEFIER.get()));

    public static final Supplier<BlockEntityType<StoneSolidifierBlockEntity>> STONE_SOLIDIFIER_ENTITY =
            BLOCK_ENTITIES.register("stone_solidifier", () -> new BlockEntityType<>(StoneSolidifierBlockEntity::new,
                    EPBlocks.STONE_SOLIDIFIER.get()));

    public static final Supplier<BlockEntityType<FiltrationPlantBlockEntity>> FILTRATION_PLANT_ENTITY =
            BLOCK_ENTITIES.register("filtration_plant", () -> new BlockEntityType<>(FiltrationPlantBlockEntity::new,
                    EPBlocks.FILTRATION_PLANT.get()));

    public static final Supplier<BlockEntityType<FluidTransposerBlockEntity>> FLUID_TRANSPOSER_ENTITY =
            BLOCK_ENTITIES.register("fluid_transposer", () -> new BlockEntityType<>(FluidTransposerBlockEntity::new,
                    EPBlocks.FLUID_TRANSPOSER.get()));

    public static final Supplier<BlockEntityType<FluidFillerBlockEntity>> FLUID_FILLER_ENTITY =
            BLOCK_ENTITIES.register("fluid_filler", () -> new BlockEntityType<>(FluidFillerBlockEntity::new,
                    EPBlocks.FLUID_FILLER.get()));

    public static final Supplier<BlockEntityType<FluidDrainerBlockEntity>> FLUID_DRAINER_ENTITY =
            BLOCK_ENTITIES.register("fluid_drainer", () -> new BlockEntityType<>(FluidDrainerBlockEntity::new,
                    EPBlocks.FLUID_DRAINER.get()));

    public static final Supplier<BlockEntityType<FluidPumpBlockEntity>> FLUID_PUMP_ENTITY =
            BLOCK_ENTITIES.register("fluid_pump", () -> new BlockEntityType<>(FluidPumpBlockEntity::new,
                    EPBlocks.FLUID_PUMP.get()));

    public static final Supplier<BlockEntityType<DrainBlockEntity>> DRAIN_ENTITY =
            BLOCK_ENTITIES.register("drain", () -> new BlockEntityType<>(DrainBlockEntity::new,
                    EPBlocks.DRAIN.get()));

    public static final Supplier<BlockEntityType<ChargerBlockEntity>> CHARGER_ENTITY =
            BLOCK_ENTITIES.register("charger", () -> new BlockEntityType<>(ChargerBlockEntity::new,
                    EPBlocks.CHARGER.get()));

    public static final Supplier<BlockEntityType<AdvancedChargerBlockEntity>> ADVANCED_CHARGER_ENTITY =
            BLOCK_ENTITIES.register("advanced_charger", () -> new BlockEntityType<>(AdvancedChargerBlockEntity::new,
                    EPBlocks.ADVANCED_CHARGER.get()));

    public static final Supplier<BlockEntityType<UnchargerBlockEntity>> UNCHARGER_ENTITY =
            BLOCK_ENTITIES.register("uncharger", () -> new BlockEntityType<>(UnchargerBlockEntity::new,
                    EPBlocks.UNCHARGER.get()));

    public static final Supplier<BlockEntityType<AdvancedUnchargerBlockEntity>> ADVANCED_UNCHARGER_ENTITY =
            BLOCK_ENTITIES.register("advanced_uncharger", () -> new BlockEntityType<>(AdvancedUnchargerBlockEntity::new,
                    EPBlocks.ADVANCED_UNCHARGER.get()));

    public static final Supplier<BlockEntityType<MinecartChargerBlockEntity>> MINECART_CHARGER_ENTITY =
            BLOCK_ENTITIES.register("minecart_charger", () -> new BlockEntityType<>(MinecartChargerBlockEntity::new,
                    EPBlocks.MINECART_CHARGER.get()));

    public static final Supplier<BlockEntityType<AdvancedMinecartChargerBlockEntity>> ADVANCED_MINECART_CHARGER_ENTITY =
            BLOCK_ENTITIES.register("advanced_minecart_charger", () -> new BlockEntityType<>(AdvancedMinecartChargerBlockEntity::new,
                    EPBlocks.ADVANCED_MINECART_CHARGER.get()));

    public static final Supplier<BlockEntityType<MinecartUnchargerBlockEntity>> MINECART_UNCHARGER_ENTITY =
            BLOCK_ENTITIES.register("minecart_uncharger", () -> new BlockEntityType<>(MinecartUnchargerBlockEntity::new,
                    EPBlocks.MINECART_UNCHARGER.get()));

    public static final Supplier<BlockEntityType<AdvancedMinecartUnchargerBlockEntity>> ADVANCED_MINECART_UNCHARGER_ENTITY =
            BLOCK_ENTITIES.register("advanced_minecart_uncharger", () -> new BlockEntityType<>(AdvancedMinecartUnchargerBlockEntity::new,
                    EPBlocks.ADVANCED_MINECART_UNCHARGER.get()));

    private static Supplier<BlockEntityType<SolarPanelBlockEntity>> createSolarPanelBlockEntity(String name,
    Supplier<SolarPanelBlock> blockSupplier) {
        return BLOCK_ENTITIES.register(name, () -> new BlockEntityType<>((blockPos, state) -> new SolarPanelBlockEntity(blockPos, state,
                        blockSupplier.get().getTier()), blockSupplier.get()));
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
            BLOCK_ENTITIES.register("lv_transformer_1_to_n", () -> new BlockEntityType<>((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_LV,
                                    TransformerBlock.Type.TYPE_1_TO_N),
                    EPBlocks.LV_TRANSFORMER_1_TO_N.get()));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> LV_TRANSFORMER_3_TO_3_ENTITY =
            BLOCK_ENTITIES.register("lv_transformer_3_to_3", () -> new BlockEntityType<>((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_LV,
                                    TransformerBlock.Type.TYPE_3_TO_3),
                    EPBlocks.LV_TRANSFORMER_3_TO_3.get()));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> LV_TRANSFORMER_N_TO_1_ENTITY =
            BLOCK_ENTITIES.register("lv_transformer_n_to_1", () -> new BlockEntityType<>((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_LV,
                                    TransformerBlock.Type.TYPE_N_TO_1),
                    EPBlocks.LV_TRANSFORMER_N_TO_1.get()));

    public static final Supplier<BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER_1_TO_N_ENTITY =
            BLOCK_ENTITIES.register("transformer_1_to_n", () -> new BlockEntityType<>((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV,
                                    TransformerBlock.Type.TYPE_1_TO_N),
                    EPBlocks.MV_TRANSFORMER_1_TO_N.get()));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER_3_TO_3_ENTITY =
            BLOCK_ENTITIES.register("transformer_3_to_3", () -> new BlockEntityType<>((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV,
                                    TransformerBlock.Type.TYPE_3_TO_3),
                    EPBlocks.MV_TRANSFORMER_3_TO_3.get()));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER_N_TO_1_ENTITY =
            BLOCK_ENTITIES.register("transformer_n_to_1", () -> new BlockEntityType<>((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV,
                                    TransformerBlock.Type.TYPE_N_TO_1),
                    EPBlocks.MV_TRANSFORMER_N_TO_1.get()));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER_1_TO_N_ENTITY =
            BLOCK_ENTITIES.register("hv_transformer_1_to_n", () -> new BlockEntityType<>((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV,
                                    TransformerBlock.Type.TYPE_1_TO_N),
                    EPBlocks.HV_TRANSFORMER_1_TO_N.get()));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER_3_TO_3_ENTITY =
            BLOCK_ENTITIES.register("hv_transformer_3_to_3", () -> new BlockEntityType<>((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV,
                                    TransformerBlock.Type.TYPE_3_TO_3),
                    EPBlocks.HV_TRANSFORMER_3_TO_3.get()));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER_N_TO_1_ENTITY =
            BLOCK_ENTITIES.register("hv_transformer_n_to_1", () -> new BlockEntityType<>((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV,
                                    TransformerBlock.Type.TYPE_N_TO_1),
                    EPBlocks.HV_TRANSFORMER_N_TO_1.get()));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> EHV_TRANSFORMER_1_TO_N_ENTITY =
            BLOCK_ENTITIES.register("ehv_transformer_1_to_n", () -> new BlockEntityType<>((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV,
                                    TransformerBlock.Type.TYPE_1_TO_N),
                    EPBlocks.EHV_TRANSFORMER_1_TO_N.get()));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> EHV_TRANSFORMER_3_TO_3_ENTITY =
            BLOCK_ENTITIES.register("ehv_transformer_3_to_3", () -> new BlockEntityType<>((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV,
                                    TransformerBlock.Type.TYPE_3_TO_3),
                    EPBlocks.EHV_TRANSFORMER_3_TO_3.get()));
    public static final Supplier<BlockEntityType<TransformerBlockEntity>> EHV_TRANSFORMER_N_TO_1_ENTITY =
            BLOCK_ENTITIES.register("ehv_transformer_n_to_1", () -> new BlockEntityType<>((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV,
                                    TransformerBlock.Type.TYPE_N_TO_1),
                    EPBlocks.EHV_TRANSFORMER_N_TO_1.get()));

    public static final Supplier<BlockEntityType<BatteryBoxBlockEntity>> BATTERY_BOX_ENTITY =
            BLOCK_ENTITIES.register("battery_box", () -> new BlockEntityType<>(BatteryBoxBlockEntity::new,
                    EPBlocks.BATTERY_BOX.get()));

    public static final Supplier<BlockEntityType<AdvancedBatteryBoxBlockEntity>> ADVANCED_BATTERY_BOX_ENTITY =
            BLOCK_ENTITIES.register("advanced_battery_box", () -> new BlockEntityType<>(AdvancedBatteryBoxBlockEntity::new,
                    EPBlocks.ADVANCED_BATTERY_BOX.get()));

    public static final Supplier<BlockEntityType<CreativeBatteryBoxBlockEntity>> CREATIVE_BATTERY_BOX_ENTITY =
            BLOCK_ENTITIES.register("creative_battery_box", () -> new BlockEntityType<>(CreativeBatteryBoxBlockEntity::new,
                    EPBlocks.CREATIVE_BATTERY_BOX.get()));

    public static final Supplier<BlockEntityType<CoalEngineBlockEntity>> COAL_ENGINE_ENTITY =
            BLOCK_ENTITIES.register("coal_engine", () -> new BlockEntityType<>(CoalEngineBlockEntity::new,
                    EPBlocks.COAL_ENGINE.get()));

    public static final Supplier<BlockEntityType<HeatGeneratorBlockEntity>> HEAT_GENERATOR_ENTITY =
            BLOCK_ENTITIES.register("heat_generator", () -> new BlockEntityType<>(HeatGeneratorBlockEntity::new,
                    EPBlocks.HEAT_GENERATOR.get()));

    public static final Supplier<BlockEntityType<ThermalGeneratorBlockEntity>> THERMAL_GENERATOR_ENTITY =
            BLOCK_ENTITIES.register("thermal_generator", () -> new BlockEntityType<>(ThermalGeneratorBlockEntity::new,
                    EPBlocks.THERMAL_GENERATOR.get()));

    public static final Supplier<BlockEntityType<PoweredLampBlockEntity>> POWERED_LAMP_ENTITY =
            BLOCK_ENTITIES.register("powered_lamp", () -> new BlockEntityType<>(PoweredLampBlockEntity::new,
                    EPBlocks.POWERED_LAMP.get()));

    public static final Supplier<BlockEntityType<PoweredFurnaceBlockEntity>> POWERED_FURNACE_ENTITY =
            BLOCK_ENTITIES.register("powered_furnace", () -> new BlockEntityType<>(PoweredFurnaceBlockEntity::new,
                    EPBlocks.POWERED_FURNACE.get()));

    public static final Supplier<BlockEntityType<AdvancedPoweredFurnaceBlockEntity>> ADVANCED_POWERED_FURNACE_ENTITY =
            BLOCK_ENTITIES.register("advanced_powered_furnace", () -> new BlockEntityType<>(AdvancedPoweredFurnaceBlockEntity::new,
                    EPBlocks.ADVANCED_POWERED_FURNACE.get()));

    public static final Supplier<BlockEntityType<LightningGeneratorBlockEntity>> LIGHTING_GENERATOR_ENTITY =
            BLOCK_ENTITIES.register("lightning_generator", () -> new BlockEntityType<>(LightningGeneratorBlockEntity::new,
                    EPBlocks.LIGHTNING_GENERATOR.get()));

    public static final Supplier<BlockEntityType<EnergizerBlockEntity>> ENERGIZER_ENTITY =
            BLOCK_ENTITIES.register("energizer", () -> new BlockEntityType<>(EnergizerBlockEntity::new,
                    EPBlocks.ENERGIZER.get()));

    public static final Supplier<BlockEntityType<ChargingStationBlockEntity>> CHARGING_STATION_ENTITY =
            BLOCK_ENTITIES.register("charging_station", () -> new BlockEntityType<>(ChargingStationBlockEntity::new,
                    EPBlocks.CHARGING_STATION.get()));

    public static final Supplier<BlockEntityType<CrystalGrowthChamberBlockEntity>> CRYSTAL_GROWTH_CHAMBER_ENTITY =
            BLOCK_ENTITIES.register("crystal_growth_chamber", () -> new BlockEntityType<>(CrystalGrowthChamberBlockEntity::new,
                    EPBlocks.CRYSTAL_GROWTH_CHAMBER.get()));

    public static final Supplier<BlockEntityType<WeatherControllerBlockEntity>> WEATHER_CONTROLLER_ENTITY =
            BLOCK_ENTITIES.register("weather_controller", () -> new BlockEntityType<>(WeatherControllerBlockEntity::new,
                    EPBlocks.WEATHER_CONTROLLER.get()));

    public static final Supplier<BlockEntityType<TimeControllerBlockEntity>> TIME_CONTROLLER_ENTITY =
            BLOCK_ENTITIES.register("time_controller", () -> new BlockEntityType<>(TimeControllerBlockEntity::new,
                    EPBlocks.TIME_CONTROLLER.get()));

    public static final Supplier<BlockEntityType<TeleporterBlockEntity>> TELEPORTER_ENTITY =
            BLOCK_ENTITIES.register("teleporter", () -> new BlockEntityType<>(TeleporterBlockEntity::new,
                    EPBlocks.TELEPORTER.get()));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                IRON_FLUID_PIPE_ENTITY.get(), FluidPipeBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                GOLDEN_FLUID_PIPE_ENTITY.get(), FluidPipeBlockEntity::getFluidHandlerCapability);

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                FLUID_TANK_SMALL_ENTITY.get(), FluidTankBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                FLUID_TANK_MEDIUM_ENTITY.get(), FluidTankBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                FLUID_TANK_LARGE_ENTITY.get(), FluidTankBlockEntity::getFluidHandlerCapability);

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                CREATIVE_FLUID_TANK_ENTITY.get(), CreativeFluidTankBlockEntity::getFluidHandlerCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ITEM_CONVEYOR_BELT_ENTITY.get(), ItemConveyorBeltBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ITEM_CONVEYOR_BELT_LOADER_ENTITY.get(), ItemConveyorBeltLoaderBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                TIN_CABLE_ENTITY.get(), CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                COPPER_CABLE_ENTITY.get(), CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                GOLD_CABLE_ENTITY.get(), CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ENERGIZED_COPPER_CABLE_ENTITY.get(), CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ENERGIZED_GOLD_CABLE_ENTITY.get(), CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY.get(), CableBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                AUTO_CRAFTER_ENTITY.get(), AutoCrafterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                AUTO_CRAFTER_ENTITY.get(), AutoCrafterBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ADVANCED_AUTO_CRAFTER_ENTITY.get(), AdvancedAutoCrafterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ADVANCED_AUTO_CRAFTER_ENTITY.get(), AdvancedAutoCrafterBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                PRESS_MOLD_MAKER_ENTITY.get(), PressMoldMakerBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ALLOY_FURNACE_ENTITY.get(), AlloyFurnaceBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                CRUSHER_ENTITY.get(), CrusherBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                CRUSHER_ENTITY.get(), CrusherBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ADVANCED_CRUSHER_ENTITY.get(), AdvancedCrusherBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                ADVANCED_CRUSHER_ENTITY.get(), AdvancedCrusherBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ADVANCED_CRUSHER_ENTITY.get(), AdvancedCrusherBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                PULVERIZER_ENTITY.get(), PulverizerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                PULVERIZER_ENTITY.get(), PulverizerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ADVANCED_PULVERIZER_ENTITY.get(), AdvancedPulverizerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                ADVANCED_PULVERIZER_ENTITY.get(), AdvancedPulverizerBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ADVANCED_PULVERIZER_ENTITY.get(), AdvancedPulverizerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                SAWMILL_ENTITY.get(), SawmillBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                SAWMILL_ENTITY.get(), SawmillBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                COMPRESSOR_ENTITY.get(), CompressorBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                COMPRESSOR_ENTITY.get(), CompressorBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                METAL_PRESS_ENTITY.get(), MetalPressBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                METAL_PRESS_ENTITY.get(), MetalPressBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                AUTO_PRESS_MOLD_MAKER_ENTITY.get(), AutoPressMoldMakerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                AUTO_PRESS_MOLD_MAKER_ENTITY.get(), AutoPressMoldMakerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                AUTO_STONECUTTER_ENTITY.get(), AutoStonecutterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                AUTO_STONECUTTER_ENTITY.get(), AutoStonecutterBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                PLANT_GROWTH_CHAMBER_ENTITY.get(), PlantGrowthChamberBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                PLANT_GROWTH_CHAMBER_ENTITY.get(), PlantGrowthChamberBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                BLOCK_PLACER_ENTITY.get(), BlockPlacerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                BLOCK_PLACER_ENTITY.get(), BlockPlacerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ASSEMBLING_MACHINE_ENTITY.get(), AssemblingMachineBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ASSEMBLING_MACHINE_ENTITY.get(), AssemblingMachineBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                INDUCTION_SMELTER_ENTITY.get(), InductionSmelterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                INDUCTION_SMELTER_ENTITY.get(), InductionSmelterBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                STONE_LIQUEFIER_ENTITY.get(), StoneLiquefierBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                STONE_LIQUEFIER_ENTITY.get(), StoneLiquefierBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                STONE_LIQUEFIER_ENTITY.get(), StoneLiquefierBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                STONE_SOLIDIFIER_ENTITY.get(), StoneSolidifierBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                STONE_SOLIDIFIER_ENTITY.get(), StoneSolidifierBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                STONE_SOLIDIFIER_ENTITY.get(), StoneSolidifierBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                FILTRATION_PLANT_ENTITY.get(), FiltrationPlantBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                FILTRATION_PLANT_ENTITY.get(), FiltrationPlantBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                FILTRATION_PLANT_ENTITY.get(), FiltrationPlantBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                FLUID_TRANSPOSER_ENTITY.get(), FluidTransposerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                FLUID_TRANSPOSER_ENTITY.get(), FluidTransposerBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                FLUID_TRANSPOSER_ENTITY.get(), FluidTransposerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                FLUID_FILLER_ENTITY.get(), FluidFillerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                FLUID_FILLER_ENTITY.get(), FluidFillerBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                FLUID_FILLER_ENTITY.get(), FluidFillerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                FLUID_DRAINER_ENTITY.get(), FluidDrainerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                FLUID_DRAINER_ENTITY.get(), FluidDrainerBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                FLUID_DRAINER_ENTITY.get(), FluidDrainerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                FLUID_PUMP_ENTITY.get(), FluidPumpBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                FLUID_PUMP_ENTITY.get(), FluidPumpBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                FLUID_PUMP_ENTITY.get(), FluidPumpBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                DRAIN_ENTITY.get(), DrainBlockEntity::getFluidHandlerCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                CHARGER_ENTITY.get(), ChargerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                CHARGER_ENTITY.get(), ChargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                UNCHARGER_ENTITY.get(), UnchargerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                UNCHARGER_ENTITY.get(), UnchargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ADVANCED_CHARGER_ENTITY.get(), AdvancedChargerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ADVANCED_CHARGER_ENTITY.get(), AdvancedChargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ADVANCED_UNCHARGER_ENTITY.get(), AdvancedUnchargerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ADVANCED_UNCHARGER_ENTITY.get(), AdvancedUnchargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                MINECART_CHARGER_ENTITY.get(), MinecartChargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                MINECART_UNCHARGER_ENTITY.get(), MinecartUnchargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ADVANCED_MINECART_CHARGER_ENTITY.get(), AdvancedMinecartChargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ADVANCED_MINECART_UNCHARGER_ENTITY.get(), AdvancedMinecartUnchargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                SOLAR_PANEL_ENTITY_1.get(), SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                SOLAR_PANEL_ENTITY_2.get(), SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                SOLAR_PANEL_ENTITY_3.get(), SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                SOLAR_PANEL_ENTITY_4.get(), SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                SOLAR_PANEL_ENTITY_5.get(), SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                SOLAR_PANEL_ENTITY_6.get(), SolarPanelBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                LV_TRANSFORMER_1_TO_N_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                LV_TRANSFORMER_3_TO_3_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                LV_TRANSFORMER_N_TO_1_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                MV_TRANSFORMER_1_TO_N_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                MV_TRANSFORMER_3_TO_3_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                MV_TRANSFORMER_N_TO_1_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                HV_TRANSFORMER_1_TO_N_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                HV_TRANSFORMER_3_TO_3_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                HV_TRANSFORMER_N_TO_1_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                EHV_TRANSFORMER_1_TO_N_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                EHV_TRANSFORMER_3_TO_3_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                EHV_TRANSFORMER_N_TO_1_ENTITY.get(), TransformerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                BATTERY_BOX_ENTITY.get(), BatteryBoxBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ADVANCED_BATTERY_BOX_ENTITY.get(), AdvancedBatteryBoxBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                CREATIVE_BATTERY_BOX_ENTITY.get(), CreativeBatteryBoxBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                COAL_ENGINE_ENTITY.get(), CoalEngineBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                COAL_ENGINE_ENTITY.get(), CoalEngineBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                HEAT_GENERATOR_ENTITY.get(), HeatGeneratorBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                THERMAL_GENERATOR_ENTITY.get(), ThermalGeneratorBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                THERMAL_GENERATOR_ENTITY.get(), ThermalGeneratorBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                POWERED_LAMP_ENTITY.get(), PoweredLampBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                POWERED_FURNACE_ENTITY.get(), PoweredFurnaceBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                POWERED_FURNACE_ENTITY.get(), PoweredFurnaceBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ADVANCED_POWERED_FURNACE_ENTITY.get(), AdvancedPoweredFurnaceBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ADVANCED_POWERED_FURNACE_ENTITY.get(), AdvancedPoweredFurnaceBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                LIGHTING_GENERATOR_ENTITY.get(), LightningGeneratorBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                ENERGIZER_ENTITY.get(), EnergizerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                ENERGIZER_ENTITY.get(), EnergizerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                CHARGING_STATION_ENTITY.get(), ChargingStationBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                CRYSTAL_GROWTH_CHAMBER_ENTITY.get(), CrystalGrowthChamberBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                CRYSTAL_GROWTH_CHAMBER_ENTITY.get(), CrystalGrowthChamberBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                WEATHER_CONTROLLER_ENTITY.get(), WeatherControllerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                TIME_CONTROLLER_ENTITY.get(), TimeControllerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                TELEPORTER_ENTITY.get(), TeleporterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK,
                TELEPORTER_ENTITY.get(), TeleporterBlockEntity::getEnergyStorageCapability);
    }
}
