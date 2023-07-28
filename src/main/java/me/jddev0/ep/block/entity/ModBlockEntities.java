package me.jddev0.ep.block.entity;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.CableBlock;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.SolarPanelBlock;
import me.jddev0.ep.block.TransformerBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EnergizedPowerMod.MODID);

    public static final RegistryObject<BlockEntityType<CableBlockEntity>> COPPER_CABLE_ENTITY =
            BLOCK_ENTITIES.register("copper_cable", () -> BlockEntityType.Builder.of((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_COPPER), ModBlocks.COPPER_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CableBlockEntity>> GOLD_CABLE_ENTITY =
            BLOCK_ENTITIES.register("gold_cable", () -> BlockEntityType.Builder.of((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_GOLD), ModBlocks.GOLD_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CableBlockEntity>> ENERGIZED_COPPER_CABLE_ENTITY =
            BLOCK_ENTITIES.register("energized_copper_cable", () -> BlockEntityType.Builder.of((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_COPPER), ModBlocks.ENERGIZED_COPPER_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CableBlockEntity>> ENERGIZED_GOLD_CABLE_ENTITY =
            BLOCK_ENTITIES.register("energized_gold_cable", () -> BlockEntityType.Builder.of((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_GOLD), ModBlocks.ENERGIZED_GOLD_CABLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<CableBlockEntity>> ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY =
            BLOCK_ENTITIES.register("energized_crystal_matrix_cable", () -> BlockEntityType.Builder.of((blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_CRYSTAL_MATRIX), ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<AutoCrafterBlockEntity>> AUTO_CRAFTER_ENTITY =
            BLOCK_ENTITIES.register("auto_crafter", () -> BlockEntityType.Builder.of(AutoCrafterBlockEntity::new,
                    ModBlocks.AUTO_CRAFTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CrusherBlockEntity>> CRUSHER_ENTITY =
            BLOCK_ENTITIES.register("crusher", () -> BlockEntityType.Builder.of(CrusherBlockEntity::new,
                    ModBlocks.CRUSHER.get()).build(null));

    public static final RegistryObject<BlockEntityType<SawmillBlockEntity>> SAWMILL_ENTITY =
            BLOCK_ENTITIES.register("sawmill", () -> BlockEntityType.Builder.of(SawmillBlockEntity::new,
                    ModBlocks.SAWMILL.get()).build(null));

    public static final RegistryObject<BlockEntityType<CompressorBlockEntity>> COMPRESSOR_ENTITY =
            BLOCK_ENTITIES.register("compressor", () -> BlockEntityType.Builder.of(CompressorBlockEntity::new,
                    ModBlocks.COMPRESSOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<PlantGrowthChamberBlockEntity>> PLANT_GROWTH_CHAMBER_ENTITY =
            BLOCK_ENTITIES.register("plant_growth_chamber", () -> BlockEntityType.Builder.of(PlantGrowthChamberBlockEntity::new,
                    ModBlocks.PLANT_GROWTH_CHAMBER.get()).build(null));

    public static final RegistryObject<BlockEntityType<BlockPlacerBlockEntity>> BLOCK_PLACER_ENTITY =
            BLOCK_ENTITIES.register("block_placer", () -> BlockEntityType.Builder.of(BlockPlacerBlockEntity::new,
                    ModBlocks.BLOCK_PLACER.get()).build(null));

    public static final RegistryObject<BlockEntityType<FluidFillerBlockEntity>> FLUID_FILLER_ENTITY =
            BLOCK_ENTITIES.register("fluid_filler", () -> BlockEntityType.Builder.of(FluidFillerBlockEntity::new,
                    ModBlocks.FLUID_FILLER.get()).build(null));

    public static final RegistryObject<BlockEntityType<FluidDrainerBlockEntity>> FLUID_DRAINER_ENTITY =
            BLOCK_ENTITIES.register("fluid_drainer", () -> BlockEntityType.Builder.of(FluidDrainerBlockEntity::new,
                    ModBlocks.FLUID_DRAINER.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChargerBlockEntity>> CHARGER_ENTITY =
            BLOCK_ENTITIES.register("charger", () -> BlockEntityType.Builder.of(ChargerBlockEntity::new,
                    ModBlocks.CHARGER.get()).build(null));

    public static final RegistryObject<BlockEntityType<UnchargerBlockEntity>> UNCHARGER_ENTITY =
            BLOCK_ENTITIES.register("uncharger", () -> BlockEntityType.Builder.of(UnchargerBlockEntity::new,
                    ModBlocks.UNCHARGER.get()).build(null));

    public static final RegistryObject<BlockEntityType<MinecartChargerBlockEntity>> MINECART_CHARGER_ENTITY =
            BLOCK_ENTITIES.register("minecart_charger", () -> BlockEntityType.Builder.of(MinecartChargerBlockEntity::new,
                    ModBlocks.MINECART_CHARGER.get()).build(null));

    public static final RegistryObject<BlockEntityType<MinecartUnchargerBlockEntity>> MINECART_UNCHARGER_ENTITY =
            BLOCK_ENTITIES.register("minecart_uncharger", () -> BlockEntityType.Builder.of(MinecartUnchargerBlockEntity::new,
                    ModBlocks.MINECART_UNCHARGER.get()).build(null));

    private static RegistryObject<BlockEntityType<SolarPanelBlockEntity>> createSolarPanelBlockEntity(String name,
    RegistryObject<SolarPanelBlock> blockRegistryObject) {
        return BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of((blockPos, state) -> new SolarPanelBlockEntity(blockPos, state,
                        blockRegistryObject.get().getTier()), blockRegistryObject.get()).build(null));
    }
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_1 =
            createSolarPanelBlockEntity("solar_panel_1", ModBlocks.SOLAR_PANEL_1);
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_2 =
            createSolarPanelBlockEntity("solar_panel_2", ModBlocks.SOLAR_PANEL_2);
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_3 =
            createSolarPanelBlockEntity("solar_panel_3", ModBlocks.SOLAR_PANEL_3);
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_4 =
            createSolarPanelBlockEntity("solar_panel_4", ModBlocks.SOLAR_PANEL_4);
    public static final RegistryObject<BlockEntityType<SolarPanelBlockEntity>> SOLAR_PANEL_ENTITY_5 =
            createSolarPanelBlockEntity("solar_panel_5", ModBlocks.SOLAR_PANEL_5);

    public static final RegistryObject<BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER_1_TO_N_ENTITY =
            BLOCK_ENTITIES.register("transformer_1_to_n", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV,
                                    TransformerBlock.Type.TYPE_1_TO_N),
                    ModBlocks.MV_TRANSFORMER_1_TO_N.get()).build(null));
    public static final RegistryObject<BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER_3_TO_3_ENTITY =
            BLOCK_ENTITIES.register("transformer_3_to_3", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV,
                                    TransformerBlock.Type.TYPE_3_TO_3),
                    ModBlocks.MV_TRANSFORMER_3_TO_3.get()).build(null));
    public static final RegistryObject<BlockEntityType<TransformerBlockEntity>> MV_TRANSFORMER_N_TO_1_ENTITY =
            BLOCK_ENTITIES.register("transformer_n_to_1", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV,
                                    TransformerBlock.Type.TYPE_N_TO_1),
                    ModBlocks.MV_TRANSFORMER_N_TO_1.get()).build(null));
    public static final RegistryObject<BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER_1_TO_N_ENTITY =
            BLOCK_ENTITIES.register("hv_transformer_1_to_n", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV,
                                    TransformerBlock.Type.TYPE_1_TO_N),
                    ModBlocks.HV_TRANSFORMER_1_TO_N.get()).build(null));
    public static final RegistryObject<BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER_3_TO_3_ENTITY =
            BLOCK_ENTITIES.register("hv_transformer_3_to_3", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV,
                                    TransformerBlock.Type.TYPE_3_TO_3),
                    ModBlocks.HV_TRANSFORMER_3_TO_3.get()).build(null));
    public static final RegistryObject<BlockEntityType<TransformerBlockEntity>> HV_TRANSFORMER_N_TO_1_ENTITY =
            BLOCK_ENTITIES.register("hv_transformer_n_to_1", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV,
                                    TransformerBlock.Type.TYPE_N_TO_1),
                    ModBlocks.HV_TRANSFORMER_N_TO_1.get()).build(null));
    public static final RegistryObject<BlockEntityType<TransformerBlockEntity>> EHV_TRANSFORMER_1_TO_N_ENTITY =
            BLOCK_ENTITIES.register("ehv_transformer_1_to_n", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV,
                                    TransformerBlock.Type.TYPE_1_TO_N),
                    ModBlocks.EHV_TRANSFORMER_1_TO_N.get()).build(null));
    public static final RegistryObject<BlockEntityType<TransformerBlockEntity>> EHV_TRANSFORMER_3_TO_3_ENTITY =
            BLOCK_ENTITIES.register("ehv_transformer_3_to_3", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV,
                                    TransformerBlock.Type.TYPE_3_TO_3),
                    ModBlocks.EHV_TRANSFORMER_3_TO_3.get()).build(null));
    public static final RegistryObject<BlockEntityType<TransformerBlockEntity>> EHV_TRANSFORMER_N_TO_1_ENTITY =
            BLOCK_ENTITIES.register("ehv_transformer_n_to_1", () -> BlockEntityType.Builder.of((blockPos, state) ->
                            new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV,
                                    TransformerBlock.Type.TYPE_N_TO_1),
                    ModBlocks.EHV_TRANSFORMER_N_TO_1.get()).build(null));

    public static final RegistryObject<BlockEntityType<BatteryBoxBlockEntity>> BATTERY_BOX_ENTITY =
            BLOCK_ENTITIES.register("battery_box", () -> BlockEntityType.Builder.of(BatteryBoxBlockEntity::new,
                    ModBlocks.BATTERY_BOX.get()).build(null));

    public static final RegistryObject<BlockEntityType<AdvancedBatteryBoxBlockEntity>> ADVANCED_BATTERY_BOX_ENTITY =
            BLOCK_ENTITIES.register("advanced_battery_box", () -> BlockEntityType.Builder.of(AdvancedBatteryBoxBlockEntity::new,
                    ModBlocks.ADVANCED_BATTERY_BOX.get()).build(null));

    public static final RegistryObject<BlockEntityType<CoalEngineBlockEntity>> COAL_ENGINE_ENTITY =
            BLOCK_ENTITIES.register("coal_engine", () -> BlockEntityType.Builder.of(CoalEngineBlockEntity::new,
                    ModBlocks.COAL_ENGINE.get()).build(null));

    public static final RegistryObject<BlockEntityType<HeatGeneratorBlockEntity>> HEAT_GENERATOR_ENTITY =
            BLOCK_ENTITIES.register("heat_generator", () -> BlockEntityType.Builder.of(HeatGeneratorBlockEntity::new,
                    ModBlocks.HEAT_GENERATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<ThermalGeneratorBlockEntity>> THERMAL_GENERATOR_ENTITY =
            BLOCK_ENTITIES.register("thermal_generator", () -> BlockEntityType.Builder.of(ThermalGeneratorBlockEntity::new,
                    ModBlocks.THERMAL_GENERATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<PoweredFurnaceBlockEntity>> POWERED_FURNACE_ENTITY =
            BLOCK_ENTITIES.register("powered_furnace", () -> BlockEntityType.Builder.of(PoweredFurnaceBlockEntity::new,
                    ModBlocks.POWERED_FURNACE.get()).build(null));

    public static final RegistryObject<BlockEntityType<LightningGeneratorBlockEntity>> LIGHTING_GENERATOR_ENTITY =
            BLOCK_ENTITIES.register("lightning_generator", () -> BlockEntityType.Builder.of(LightningGeneratorBlockEntity::new,
                    ModBlocks.LIGHTNING_GENERATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<EnergizerBlockEntity>> ENERGIZER_ENTITY =
            BLOCK_ENTITIES.register("energizer", () -> BlockEntityType.Builder.of(EnergizerBlockEntity::new,
                    ModBlocks.ENERGIZER.get()).build(null));

    public static final RegistryObject<BlockEntityType<ChargingStationBlockEntity>> CHARGING_STATION_ENTITY =
            BLOCK_ENTITIES.register("charging_station", () -> BlockEntityType.Builder.of(ChargingStationBlockEntity::new,
                    ModBlocks.CHARGING_STATION.get()).build(null));

    public static final RegistryObject<BlockEntityType<WeatherControllerBlockEntity>> WEATHER_CONTROLLER_ENTITY =
            BLOCK_ENTITIES.register("weather_controller", () -> BlockEntityType.Builder.of(WeatherControllerBlockEntity::new,
                    ModBlocks.WEATHER_CONTROLLER.get()).build(null));

    public static final RegistryObject<BlockEntityType<TimeControllerBlockEntity>> TIME_CONTROLLER_ENTITY =
            BLOCK_ENTITIES.register("time_controller", () -> BlockEntityType.Builder.of(TimeControllerBlockEntity::new,
                    ModBlocks.TIME_CONTROLLER.get()).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
