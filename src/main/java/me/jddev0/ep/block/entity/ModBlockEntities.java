package me.jddev0.ep.block.entity;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.CableBlock;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.SolarPanelBlock;
import me.jddev0.ep.block.TransformerBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.BiFunction;

public final class ModBlockEntities {
    private ModBlockEntities() {}

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

    public static final BlockEntityType<AutoCrafterBlockEntity> AUTO_CRAFTER_ENTITY = registerEnergyStorage(
            createBlockEntity("auto_crafter", ModBlocks.AUTO_CRAFTER, AutoCrafterBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<CrusherBlockEntity> CRUSHER_ENTITY = registerEnergyStorage(
            createBlockEntity("crusher", ModBlocks.CRUSHER, CrusherBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<SawmillBlockEntity> SAWMILL_ENTITY = registerEnergyStorage(
            createBlockEntity("sawmill", ModBlocks.SAWMILL, SawmillBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<BlockPlacerBlockEntity> BLOCK_PLACER_ENTITY = registerEnergyStorage(
            createBlockEntity("block_placer", ModBlocks.BLOCK_PLACER, BlockPlacerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<ChargerBlockEntity> CHARGER_ENTITY = registerEnergyStorage(
            createBlockEntity("charger", ModBlocks.CHARGER, ChargerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<UnchargerBlockEntity> UNCHARGER_ENTITY = registerEnergyStorage(
            createBlockEntity("uncharger", ModBlocks.UNCHARGER, UnchargerBlockEntity::new),
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

    public static final BlockEntityType<TransformerBlockEntity> TRANSFORMER_1_TO_N_ENTITY = registerEnergyStorage(
            createBlockEntity("transformer_1_to_n", ModBlocks.TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Type.TYPE_1_TO_N)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> TRANSFORMER_N_TO_1_ENTITY = registerEnergyStorage(
            createBlockEntity("transformer_n_to_1", ModBlocks.TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Type.TYPE_N_TO_1)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );

    public static final BlockEntityType<CoalEngineBlockEntity> COAL_ENGINE_ENTITY = registerEnergyStorage(
            createBlockEntity("coal_engine", ModBlocks.COAL_ENGINE, CoalEngineBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<LightningGeneratorBlockEntity> LIGHTING_GENERATOR_ENTITY = registerEnergyStorage(
            createBlockEntity("lightning_generator", ModBlocks.LIGHTNING_GENERATOR, LightningGeneratorBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<EnergizerBlockEntity> ENERGIZER_ENTITY = registerEnergyStorage(
            createBlockEntity("energizer", ModBlocks.ENERGIZER, EnergizerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    public static final BlockEntityType<ChargingStationBlockEntity> CHARGING_STATION_ENTITY = registerEnergyStorage(
            createBlockEntity("charging_station", ModBlocks.CHARGING_STATION, ChargingStationBlockEntity::new),
            (blockEntity, direction) -> blockEntity.energyStorage
    );

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(String name, Block block,
            FabricBlockEntityTypeBuilder.Factory<? extends T> factory) {
        return (BlockEntityType<T>)Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(EnergizedPowerMod.MODID, name),
                FabricBlockEntityTypeBuilder.create(factory, block).build(null));
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerEnergyStorage(BlockEntityType<T> blockEntityType,
            BiFunction<? super T, Direction, @Nullable EnergyStorage> provider) {
        EnergyStorage.SIDED.registerForBlockEntity(provider, blockEntityType);
        return blockEntityType;
    }

    public static void register() {

    }
}
