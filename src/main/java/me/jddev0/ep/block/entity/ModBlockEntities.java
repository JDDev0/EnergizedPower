package me.jddev0.ep.block.entity;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.CableBlock;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.SolarPanelBlock;
import me.jddev0.ep.block.TransformerBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.BiFunction;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final BlockEntityType<FluidPipeBlockEntity> FLUID_PIPE_ENTITY = registerFluidStorage(
            createBlockEntity("fluid_pipe", ModBlocks.FLUID_PIPE, FluidPipeBlockEntity::new),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );

    public static final BlockEntityType<CableBlockEntity> COPPER_CABLE_ENTITY = registerEnergyStorage(
            createBlockEntity("copper_cable", ModBlocks.COPPER_CABLE, (blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_COPPER)),
            (blockEntity, direction) -> blockEntity.energyStorage
    );
    public static final BlockEntityType<CableBlockEntity> GOLD_CABLE_ENTITY = registerEnergyStorage(
            createBlockEntity("gold_cable", ModBlocks.GOLD_CABLE, (blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_GOLD)),
            (blockEntity, direction) -> blockEntity.energyStorage
    );
    public static final BlockEntityType<CableBlockEntity> ENERGIZED_COPPER_CABLE_ENTITY = registerEnergyStorage(
            createBlockEntity("energized_copper_cable", ModBlocks.ENERGIZED_COPPER_CABLE, (blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_COPPER)),
            (blockEntity, direction) -> blockEntity.energyStorage
    );
    public static final BlockEntityType<CableBlockEntity> ENERGIZED_GOLD_CABLE_ENTITY = registerEnergyStorage(
            createBlockEntity("energized_gold_cable", ModBlocks.ENERGIZED_GOLD_CABLE, (blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_GOLD)),
            (blockEntity, direction) -> blockEntity.energyStorage
    );
    public static final BlockEntityType<CableBlockEntity> ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY = registerEnergyStorage(
            createBlockEntity("energized_crystal_matrix_cable", ModBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE, (blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_CRYSTAL_MATRIX)),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<AutoCrafterBlockEntity> AUTO_CRAFTER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("auto_crafter", ModBlocks.AUTO_CRAFTER, AutoCrafterBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<CrusherBlockEntity> CRUSHER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("crusher", ModBlocks.CRUSHER, CrusherBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<PulverizerBlockEntity> PULVERIZER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("pulverizer", ModBlocks.PULVERIZER, PulverizerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<SawmillBlockEntity> SAWMILL_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("sawmill", ModBlocks.SAWMILL, SawmillBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("compressor", ModBlocks.COMPRESSOR, CompressorBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<PlantGrowthChamberBlockEntity> PLANT_GROWTH_CHAMBER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                createBlockEntity("plant_growth_chamber", ModBlocks.PLANT_GROWTH_CHAMBER, PlantGrowthChamberBlockEntity::new),
                (blockEntity, side) -> (side == Direction.UP || side == Direction.DOWN)?
                        blockEntity.cachedSidedInventoryStorageTopBottom.apply(side):
                        blockEntity.cachedSidedInventoryStorageSides.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<BlockPlacerBlockEntity> BLOCK_PLACER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("block_placer", ModBlocks.BLOCK_PLACER, BlockPlacerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<FluidFillerBlockEntity> FLUID_FILLER_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    registerInventoryStorage(
                            createBlockEntity("fluid_filler", ModBlocks.FLUID_FILLER, FluidFillerBlockEntity::new),
                            (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
                    ),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<FluidDrainerBlockEntity> FLUID_DRAINER_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    registerInventoryStorage(
                            createBlockEntity("fluid_drainer", ModBlocks.FLUID_DRAINER, FluidDrainerBlockEntity::new),
                            (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
                    ),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<ChargerBlockEntity> CHARGER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                createBlockEntity("charger", ModBlocks.CHARGER, ChargerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<AdvancedChargerBlockEntity> ADVANCED_CHARGER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                createBlockEntity("advanced_charger", ModBlocks.ADVANCED_CHARGER, AdvancedChargerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<UnchargerBlockEntity> UNCHARGER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("uncharger", ModBlocks.UNCHARGER, UnchargerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<AdvancedUnchargerBlockEntity> ADVANCED_UNCHARGER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("advanced_uncharger", ModBlocks.ADVANCED_UNCHARGER, AdvancedUnchargerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<MinecartChargerBlockEntity> MINECART_CHARGER_ENTITY = registerEnergyStorage(
            createBlockEntity("minecart_charger", ModBlocks.MINECART_CHARGER, MinecartChargerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<AdvancedMinecartChargerBlockEntity> ADVANCED_MINECART_CHARGER_ENTITY = registerEnergyStorage(
            createBlockEntity("advanced_minecart_charger", ModBlocks.ADVANCED_MINECART_CHARGER, AdvancedMinecartChargerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<MinecartUnchargerBlockEntity> MINECART_UNCHARGER_ENTITY = registerEnergyStorage(
            createBlockEntity("minecart_uncharger", ModBlocks.MINECART_UNCHARGER, MinecartUnchargerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<AdvancedMinecartUnchargerBlockEntity> ADVANCED_MINECART_UNCHARGER_ENTITY = registerEnergyStorage(
            createBlockEntity("advanced_minecart_uncharger", ModBlocks.ADVANCED_MINECART_UNCHARGER, AdvancedMinecartUnchargerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    private static BlockEntityType<SolarPanelBlockEntity> createSolarPanelBlockEntity(String name, SolarPanelBlock block) {
        return createBlockEntity(name, block, (blockPos, state) -> new SolarPanelBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_1 = registerEnergyStorage(
            createSolarPanelBlockEntity("solar_panel_1", ModBlocks.SOLAR_PANEL_1),
            (blockEntity, direction) -> (direction == null || direction == Direction.DOWN)?blockEntity.energyStorage:null
    );
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_2 = registerEnergyStorage(
            createSolarPanelBlockEntity("solar_panel_2", ModBlocks.SOLAR_PANEL_2),
            (blockEntity, direction) -> (direction == null || direction == Direction.DOWN)?blockEntity.energyStorage:null
    );
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_3 = registerEnergyStorage(
            createSolarPanelBlockEntity("solar_panel_3", ModBlocks.SOLAR_PANEL_3),
            (blockEntity, direction) -> (direction == null || direction == Direction.DOWN)?blockEntity.energyStorage:null
    );
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_4 = registerEnergyStorage(
            createSolarPanelBlockEntity("solar_panel_4", ModBlocks.SOLAR_PANEL_4),
            (blockEntity, direction) -> (direction == null || direction == Direction.DOWN)?blockEntity.energyStorage:null
    );
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_5 = registerEnergyStorage(
            createSolarPanelBlockEntity("solar_panel_5", ModBlocks.SOLAR_PANEL_5),
        (blockEntity, direction) -> (direction == null || direction == Direction.DOWN)?blockEntity.energyStorage:null
    );

    public static final BlockEntityType<TransformerBlockEntity> MV_TRANSFORMER_1_TO_N_ENTITY = registerEnergyStorage(
            createBlockEntity("transformer_1_to_n", ModBlocks.MV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV, TransformerBlock.Type.TYPE_1_TO_N)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> MV_TRANSFORMER_3_TO_3_ENTITY = registerEnergyStorage(
            createBlockEntity("transformer_3_to_3", ModBlocks.MV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV, TransformerBlock.Type.TYPE_3_TO_3)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> MV_TRANSFORMER_N_TO_1_ENTITY = registerEnergyStorage(
            createBlockEntity("transformer_n_to_1", ModBlocks.MV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV, TransformerBlock.Type.TYPE_N_TO_1)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );

    public static final BlockEntityType<TransformerBlockEntity> HV_TRANSFORMER_1_TO_N_ENTITY = registerEnergyStorage(
            createBlockEntity("hv_transformer_1_to_n", ModBlocks.HV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV, TransformerBlock.Type.TYPE_1_TO_N)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> HV_TRANSFORMER_3_TO_3_ENTITY = registerEnergyStorage(
            createBlockEntity("hv_transformer_3_to_3", ModBlocks.HV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV, TransformerBlock.Type.TYPE_3_TO_3)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> HV_TRANSFORMER_N_TO_1_ENTITY = registerEnergyStorage(
            createBlockEntity("hv_transformer_n_to_1", ModBlocks.HV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV, TransformerBlock.Type.TYPE_N_TO_1)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );

    public static final BlockEntityType<TransformerBlockEntity> EHV_TRANSFORMER_1_TO_N_ENTITY = registerEnergyStorage(
            createBlockEntity("ehv_transformer_1_to_n", ModBlocks.EHV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV, TransformerBlock.Type.TYPE_1_TO_N)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> EHV_TRANSFORMER_3_TO_3_ENTITY = registerEnergyStorage(
            createBlockEntity("ehv_transformer_3_to_3", ModBlocks.EHV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV, TransformerBlock.Type.TYPE_3_TO_3)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> EHV_TRANSFORMER_N_TO_1_ENTITY = registerEnergyStorage(
            createBlockEntity("ehv_transformer_n_to_1", ModBlocks.EHV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV, TransformerBlock.Type.TYPE_N_TO_1)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );

    public static final BlockEntityType<BatteryBoxBlockEntity> BATTERY_BOX_ENTITY = registerEnergyStorage(
            createBlockEntity("battery_box", ModBlocks.BATTERY_BOX, BatteryBoxBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<AdvancedBatteryBoxBlockEntity> ADVANCED_BATTERY_BOX_ENTITY = registerEnergyStorage(
            createBlockEntity("advanced_battery_box", ModBlocks.ADVANCED_BATTERY_BOX, AdvancedBatteryBoxBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<CreativeBatteryBoxBlockEntity> CREATIVE_BATTERY_BOX_ENTITY = registerEnergyStorage(
            createBlockEntity("creative_battery_box", ModBlocks.CREATIVE_BATTERY_BOX, CreativeBatteryBoxBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<CoalEngineBlockEntity> COAL_ENGINE_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("coal_engine", ModBlocks.COAL_ENGINE, CoalEngineBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<HeatGeneratorBlockEntity> HEAT_GENERATOR_ENTITY = registerEnergyStorage(
            createBlockEntity("heat_generator", ModBlocks.HEAT_GENERATOR, HeatGeneratorBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<ThermalGeneratorBlockEntity> THERMAL_GENERATOR_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    createBlockEntity("thermal_generator", ModBlocks.THERMAL_GENERATOR, ThermalGeneratorBlockEntity::new),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<PoweredLampBlockEntity> POWERED_LAMP_ENTITY = registerEnergyStorage(
            createBlockEntity("powered_lamp", ModBlocks.POWERED_LAMP, PoweredLampBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<PoweredFurnaceBlockEntity> POWERED_FURNACE_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("powered_furnace", ModBlocks.POWERED_FURNACE, PoweredFurnaceBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<AdvancedPoweredFurnaceBlockEntity> ADVANCED_POWERED_FURNACE_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("advanced_powered_furnace", ModBlocks.ADVANCED_POWERED_FURNACE, AdvancedPoweredFurnaceBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<LightningGeneratorBlockEntity> LIGHTING_GENERATOR_ENTITY = registerEnergyStorage(
            createBlockEntity("lightning_generator", ModBlocks.LIGHTNING_GENERATOR, LightningGeneratorBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<EnergizerBlockEntity> ENERGIZER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("energizer", ModBlocks.ENERGIZER, EnergizerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.cachedSidedInventoryStorage.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<ChargingStationBlockEntity> CHARGING_STATION_ENTITY = registerEnergyStorage(
            createBlockEntity("charging_station", ModBlocks.CHARGING_STATION, ChargingStationBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<WeatherControllerBlockEntity> WEATHER_CONTROLLER_ENTITY = registerEnergyStorage(
            createBlockEntity("weather_controller", ModBlocks.WEATHER_CONTROLLER, WeatherControllerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<TimeControllerBlockEntity> TIME_CONTROLLER_ENTITY = registerEnergyStorage(
            createBlockEntity("time_controller", ModBlocks.TIME_CONTROLLER, TimeControllerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(String name, Block block,
            FabricBlockEntityTypeBuilder.Factory<? extends T> factory) {
        return (BlockEntityType<T>)Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(EnergizedPowerMod.MODID, name),
                FabricBlockEntityTypeBuilder.create(factory, block).build(null));
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerInventoryStorage(BlockEntityType<T> blockEntityType,
           BiFunction<? super T, Direction, @Nullable Storage<ItemVariant>> provider) {
        ItemStorage.SIDED.registerForBlockEntity(provider, blockEntityType);
        return blockEntityType;
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerFluidStorage(BlockEntityType<T> blockEntityType, BiFunction<? super T,
            Direction, @Nullable Storage<FluidVariant>> provider) {
        FluidStorage.SIDED.registerForBlockEntity(provider, blockEntityType);
        return blockEntityType;
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerEnergyStorage(BlockEntityType<T> blockEntityType,
            BiFunction<? super T, Direction, @Nullable EnergyStorage> provider) {
        EnergyStorage.SIDED.registerForBlockEntity(provider, blockEntityType);
        return blockEntityType;
    }

    public static void register() {

    }
}
