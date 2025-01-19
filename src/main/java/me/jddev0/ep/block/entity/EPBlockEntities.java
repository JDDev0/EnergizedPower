package me.jddev0.ep.block.entity;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.BiFunction;

public final class EPBlockEntities {
    private EPBlockEntities() {}

    private static BlockEntityType<FluidPipeBlockEntity> createFluidPipeBlockEntity(String name, FluidPipeBlock block) {
        return createBlockEntity(name, block, (blockPos, state) -> new FluidPipeBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<FluidPipeBlockEntity> IRON_FLUID_PIPE_ENTITY = registerFluidStorage(
            createFluidPipeBlockEntity("fluid_pipe", EPBlocks.IRON_FLUID_PIPE),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );
    public static final BlockEntityType<FluidPipeBlockEntity> GOLDEN_FLUID_PIPE_ENTITY = registerFluidStorage(
            createFluidPipeBlockEntity("golden_fluid_pipe", EPBlocks.GOLDEN_FLUID_PIPE),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );

    private static BlockEntityType<FluidTankBlockEntity> createFluidTankBlockEntity(String name, FluidTankBlock block) {
        return createBlockEntity(name, block, (blockPos, state) -> new FluidTankBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<FluidTankBlockEntity> FLUID_TANK_SMALL_ENTITY = registerFluidStorage(
            createFluidTankBlockEntity("fluid_tank_small", EPBlocks.FLUID_TANK_SMALL),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );
    public static final BlockEntityType<FluidTankBlockEntity> FLUID_TANK_MEDIUM_ENTITY = registerFluidStorage(
            createFluidTankBlockEntity("fluid_tank_medium", EPBlocks.FLUID_TANK_MEDIUM),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );
    public static final BlockEntityType<FluidTankBlockEntity> FLUID_TANK_LARGE_ENTITY = registerFluidStorage(
            createFluidTankBlockEntity("fluid_tank_large", EPBlocks.FLUID_TANK_LARGE),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );

    public static final BlockEntityType<CreativeFluidTankBlockEntity> CREATIVE_FLUID_TANK_ENTITY = registerFluidStorage(
            createBlockEntity("creative_fluid_tank", EPBlocks.CREATIVE_FLUID_TANK, CreativeFluidTankBlockEntity::new),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );

    public static final BlockEntityType<ItemConveyorBeltBlockEntity> ITEM_CONVEYOR_BELT_ENTITY = registerInventoryStorage(
            createBlockEntity("item_conveyor_belt", EPBlocks.ITEM_CONVEYOR_BELT, ItemConveyorBeltBlockEntity::new),
            ItemConveyorBeltBlockEntity::getInventoryStorageForDirection
    );

    public static final BlockEntityType<ItemConveyorBeltLoaderBlockEntity> ITEM_CONVEYOR_BELT_LOADER_ENTITY = registerInventoryStorage(
            createBlockEntity("item_conveyor_belt_loader", EPBlocks.ITEM_CONVEYOR_BELT_LOADER, ItemConveyorBeltLoaderBlockEntity::new),
            (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
    );

    public static final BlockEntityType<ItemConveyorBeltSorterBlockEntity> ITEM_CONVEYOR_BELT_SORTER_ENTITY =
            createBlockEntity("item_conveyor_belt_sorter", EPBlocks.ITEM_CONVEYOR_BELT_SORTER, ItemConveyorBeltSorterBlockEntity::new);

    public static final BlockEntityType<ItemConveyorBeltSwitchBlockEntity> ITEM_CONVEYOR_BELT_SWITCH_ENTITY =
            createBlockEntity("item_conveyor_belt_switch", EPBlocks.ITEM_CONVEYOR_BELT_SWITCH, ItemConveyorBeltSwitchBlockEntity::new);

    public static final BlockEntityType<ItemConveyorBeltSplitterBlockEntity> ITEM_CONVEYOR_BELT_SPLITTER_ENTITY =
            createBlockEntity("item_conveyor_belt_splitter", EPBlocks.ITEM_CONVEYOR_BELT_SPLITTER, ItemConveyorBeltSplitterBlockEntity::new);

    public static final BlockEntityType<ItemConveyorBeltMergerBlockEntity> ITEM_CONVEYOR_BELT_MERGER_ENTITY =
            createBlockEntity("item_conveyor_belt_merger", EPBlocks.ITEM_CONVEYOR_BELT_MERGER, ItemConveyorBeltMergerBlockEntity::new);

    public static final BlockEntityType<CableBlockEntity> TIN_CABLE_ENTITY = registerEnergyStorage(
            createBlockEntity("tin_cable", EPBlocks.TIN_CABLE, (blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_TIN)),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );
    public static final BlockEntityType<CableBlockEntity> COPPER_CABLE_ENTITY = registerEnergyStorage(
            createBlockEntity("copper_cable", EPBlocks.COPPER_CABLE, (blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_COPPER)),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );
    public static final BlockEntityType<CableBlockEntity> GOLD_CABLE_ENTITY = registerEnergyStorage(
            createBlockEntity("gold_cable", EPBlocks.GOLD_CABLE, (blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_GOLD)),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );
    public static final BlockEntityType<CableBlockEntity> ENERGIZED_COPPER_CABLE_ENTITY = registerEnergyStorage(
            createBlockEntity("energized_copper_cable", EPBlocks.ENERGIZED_COPPER_CABLE, (blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_COPPER)),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );
    public static final BlockEntityType<CableBlockEntity> ENERGIZED_GOLD_CABLE_ENTITY = registerEnergyStorage(
            createBlockEntity("energized_gold_cable", EPBlocks.ENERGIZED_GOLD_CABLE, (blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_GOLD)),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );
    public static final BlockEntityType<CableBlockEntity> ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY = registerEnergyStorage(
            createBlockEntity("energized_crystal_matrix_cable", EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE, (blockPos, state) ->
                    new CableBlockEntity(blockPos, state, CableBlock.Tier.TIER_ENERGIZED_CRYSTAL_MATRIX)),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AutoCrafterBlockEntity> AUTO_CRAFTER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("auto_crafter", EPBlocks.AUTO_CRAFTER, AutoCrafterBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AdvancedAutoCrafterBlockEntity> ADVANCED_AUTO_CRAFTER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("advanced_auto_crafter", EPBlocks.ADVANCED_AUTO_CRAFTER, AdvancedAutoCrafterBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<PressMoldMakerBlockEntity> PRESS_MOLD_MAKER_ENTITY = registerInventoryStorage(
            createBlockEntity("press_mold_maker", EPBlocks.PRESS_MOLD_MAKER, PressMoldMakerBlockEntity::new),
            (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
    );

    public static final BlockEntityType<AlloyFurnaceBlockEntity> ALLOY_FURNACE_ENTITY = registerInventoryStorage(
            createBlockEntity("alloy_furnace", EPBlocks.ALLOY_FURNACE, AlloyFurnaceBlockEntity::new),
            AlloyFurnaceBlockEntity::getInventoryStorageForDirection
    );

    public static final BlockEntityType<CrusherBlockEntity> CRUSHER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("crusher", EPBlocks.CRUSHER, CrusherBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AdvancedCrusherBlockEntity> ADVANCED_CRUSHER_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    registerInventoryStorage(
                            createBlockEntity("advanced_crusher", EPBlocks.ADVANCED_CRUSHER, AdvancedCrusherBlockEntity::new),
                            (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
                    ),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<PulverizerBlockEntity> PULVERIZER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("pulverizer", EPBlocks.PULVERIZER, PulverizerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AdvancedPulverizerBlockEntity> ADVANCED_PULVERIZER_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    registerInventoryStorage(
                            createBlockEntity("advanced_pulverizer", EPBlocks.ADVANCED_PULVERIZER, AdvancedPulverizerBlockEntity::new),
                            (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
                    ),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<SawmillBlockEntity> SAWMILL_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("sawmill", EPBlocks.SAWMILL, SawmillBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("compressor", EPBlocks.COMPRESSOR, CompressorBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<MetalPressBlockEntity> METAL_PRESS_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("metal_press", EPBlocks.METAL_PRESS, MetalPressBlockEntity::new),
                    (blockEntity, side) -> (side == Direction.UP)?
                            blockEntity.itemHandlerTopSided.apply(side):
                            blockEntity.itemHandlerOthersSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AutoPressMoldMakerBlockEntity> AUTO_PRESS_MOLD_MAKER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("auto_press_mold_maker", EPBlocks.AUTO_PRESS_MOLD_MAKER, AutoPressMoldMakerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AutoStonecutterBlockEntity> AUTO_STONECUTTER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("auto_stonecutter", EPBlocks.AUTO_STONECUTTER, AutoStonecutterBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<PlantGrowthChamberBlockEntity> PLANT_GROWTH_CHAMBER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                createBlockEntity("plant_growth_chamber", EPBlocks.PLANT_GROWTH_CHAMBER, PlantGrowthChamberBlockEntity::new),
                (blockEntity, side) -> (side == Direction.UP || side == Direction.DOWN)?
                        blockEntity.itemHandlerTopBottomSided.apply(side):
                        blockEntity.itemHandlerSidesSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<BlockPlacerBlockEntity> BLOCK_PLACER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("block_placer", EPBlocks.BLOCK_PLACER, BlockPlacerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AssemblingMachineBlockEntity> ASSEMBLING_MACHINE_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("assembling_machine", EPBlocks.ASSEMBLING_MACHINE, AssemblingMachineBlockEntity::new),
                    AssemblingMachineBlockEntity::getInventoryStorageForDirection
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<InductionSmelterBlockEntity> INDUCTION_SMELTER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("induction_smelter", EPBlocks.INDUCTION_SMELTER, InductionSmelterBlockEntity::new),
                    InductionSmelterBlockEntity::getInventoryStorageForDirection
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<StoneSolidifierBlockEntity> STONE_SOLIDIFIER_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    registerInventoryStorage(
                            createBlockEntity("stone_solidifier", EPBlocks.STONE_SOLIDIFIER, StoneSolidifierBlockEntity::new),
                            (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
                    ),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<FiltrationPlantBlockEntity> FILTRATION_PLANT_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    registerInventoryStorage(
                            createBlockEntity("filtration_plant", EPBlocks.FILTRATION_PLANT, FiltrationPlantBlockEntity::new),
                            (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
                    ),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<FluidTransposerBlockEntity> FLUID_TRANSPOSER_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    registerInventoryStorage(
                            createBlockEntity("fluid_transposer", EPBlocks.FLUID_TRANSPOSER, FluidTransposerBlockEntity::new),
                            (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
                    ),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<FluidFillerBlockEntity> FLUID_FILLER_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    registerInventoryStorage(
                            createBlockEntity("fluid_filler", EPBlocks.FLUID_FILLER, FluidFillerBlockEntity::new),
                            (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
                    ),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<FluidDrainerBlockEntity> FLUID_DRAINER_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    registerInventoryStorage(
                            createBlockEntity("fluid_drainer", EPBlocks.FLUID_DRAINER, FluidDrainerBlockEntity::new),
                            (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
                    ),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<FluidPumpBlockEntity> FLUID_PUMP_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    registerInventoryStorage(
                            createBlockEntity("fluid_pump", EPBlocks.FLUID_PUMP, FluidPumpBlockEntity::new),
                            (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
                    ),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<DrainBlockEntity> DRAIN_ENTITY = registerFluidStorage(
            createBlockEntity("drain", EPBlocks.DRAIN, DrainBlockEntity::new),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );

    public static final BlockEntityType<ChargerBlockEntity> CHARGER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                createBlockEntity("charger", EPBlocks.CHARGER, ChargerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AdvancedChargerBlockEntity> ADVANCED_CHARGER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                createBlockEntity("advanced_charger", EPBlocks.ADVANCED_CHARGER, AdvancedChargerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<UnchargerBlockEntity> UNCHARGER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("uncharger", EPBlocks.UNCHARGER, UnchargerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AdvancedUnchargerBlockEntity> ADVANCED_UNCHARGER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("advanced_uncharger", EPBlocks.ADVANCED_UNCHARGER, AdvancedUnchargerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<MinecartChargerBlockEntity> MINECART_CHARGER_ENTITY = registerEnergyStorage(
            createBlockEntity("minecart_charger", EPBlocks.MINECART_CHARGER, MinecartChargerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AdvancedMinecartChargerBlockEntity> ADVANCED_MINECART_CHARGER_ENTITY = registerEnergyStorage(
            createBlockEntity("advanced_minecart_charger", EPBlocks.ADVANCED_MINECART_CHARGER, AdvancedMinecartChargerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<MinecartUnchargerBlockEntity> MINECART_UNCHARGER_ENTITY = registerEnergyStorage(
            createBlockEntity("minecart_uncharger", EPBlocks.MINECART_UNCHARGER, MinecartUnchargerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AdvancedMinecartUnchargerBlockEntity> ADVANCED_MINECART_UNCHARGER_ENTITY = registerEnergyStorage(
            createBlockEntity("advanced_minecart_uncharger", EPBlocks.ADVANCED_MINECART_UNCHARGER, AdvancedMinecartUnchargerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    private static BlockEntityType<SolarPanelBlockEntity> createSolarPanelBlockEntity(String name, SolarPanelBlock block) {
        return createBlockEntity(name, block, (blockPos, state) -> new SolarPanelBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_1 = registerEnergyStorage(
            createSolarPanelBlockEntity("solar_panel_1", EPBlocks.SOLAR_PANEL_1),
            (blockEntity, direction) -> (direction == null || direction == Direction.DOWN)?blockEntity.limitingEnergyStorage:null
    );
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_2 = registerEnergyStorage(
            createSolarPanelBlockEntity("solar_panel_2", EPBlocks.SOLAR_PANEL_2),
            (blockEntity, direction) -> (direction == null || direction == Direction.DOWN)?blockEntity.limitingEnergyStorage:null
    );
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_3 = registerEnergyStorage(
            createSolarPanelBlockEntity("solar_panel_3", EPBlocks.SOLAR_PANEL_3),
            (blockEntity, direction) -> (direction == null || direction == Direction.DOWN)?blockEntity.limitingEnergyStorage:null
    );
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_4 = registerEnergyStorage(
            createSolarPanelBlockEntity("solar_panel_4", EPBlocks.SOLAR_PANEL_4),
            (blockEntity, direction) -> (direction == null || direction == Direction.DOWN)?blockEntity.limitingEnergyStorage:null
    );
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_5 = registerEnergyStorage(
            createSolarPanelBlockEntity("solar_panel_5", EPBlocks.SOLAR_PANEL_5),
        (blockEntity, direction) -> (direction == null || direction == Direction.DOWN)?blockEntity.limitingEnergyStorage:null
    );
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_6 = registerEnergyStorage(
            createSolarPanelBlockEntity("solar_panel_6", EPBlocks.SOLAR_PANEL_6),
        (blockEntity, direction) -> (direction == null || direction == Direction.DOWN)?blockEntity.limitingEnergyStorage:null
    );

    public static final BlockEntityType<TransformerBlockEntity> LV_TRANSFORMER_1_TO_N_ENTITY = registerEnergyStorage(
            createBlockEntity("lv_transformer_1_to_n", EPBlocks.LV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_LV, TransformerBlock.Type.TYPE_1_TO_N)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> LV_TRANSFORMER_3_TO_3_ENTITY = registerEnergyStorage(
            createBlockEntity("lv_transformer_3_to_3", EPBlocks.LV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_LV, TransformerBlock.Type.TYPE_3_TO_3)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> LV_TRANSFORMER_N_TO_1_ENTITY = registerEnergyStorage(
            createBlockEntity("lv_transformer_n_to_1", EPBlocks.LV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_LV, TransformerBlock.Type.TYPE_N_TO_1)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );

    public static final BlockEntityType<TransformerBlockEntity> MV_TRANSFORMER_1_TO_N_ENTITY = registerEnergyStorage(
            createBlockEntity("transformer_1_to_n", EPBlocks.MV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV, TransformerBlock.Type.TYPE_1_TO_N)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> MV_TRANSFORMER_3_TO_3_ENTITY = registerEnergyStorage(
            createBlockEntity("transformer_3_to_3", EPBlocks.MV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV, TransformerBlock.Type.TYPE_3_TO_3)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> MV_TRANSFORMER_N_TO_1_ENTITY = registerEnergyStorage(
            createBlockEntity("transformer_n_to_1", EPBlocks.MV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_MV, TransformerBlock.Type.TYPE_N_TO_1)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );

    public static final BlockEntityType<TransformerBlockEntity> HV_TRANSFORMER_1_TO_N_ENTITY = registerEnergyStorage(
            createBlockEntity("hv_transformer_1_to_n", EPBlocks.HV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV, TransformerBlock.Type.TYPE_1_TO_N)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> HV_TRANSFORMER_3_TO_3_ENTITY = registerEnergyStorage(
            createBlockEntity("hv_transformer_3_to_3", EPBlocks.HV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV, TransformerBlock.Type.TYPE_3_TO_3)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> HV_TRANSFORMER_N_TO_1_ENTITY = registerEnergyStorage(
            createBlockEntity("hv_transformer_n_to_1", EPBlocks.HV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_HV, TransformerBlock.Type.TYPE_N_TO_1)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );

    public static final BlockEntityType<TransformerBlockEntity> EHV_TRANSFORMER_1_TO_N_ENTITY = registerEnergyStorage(
            createBlockEntity("ehv_transformer_1_to_n", EPBlocks.EHV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV, TransformerBlock.Type.TYPE_1_TO_N)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> EHV_TRANSFORMER_3_TO_3_ENTITY = registerEnergyStorage(
            createBlockEntity("ehv_transformer_3_to_3", EPBlocks.EHV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV, TransformerBlock.Type.TYPE_3_TO_3)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );
    public static final BlockEntityType<TransformerBlockEntity> EHV_TRANSFORMER_N_TO_1_ENTITY = registerEnergyStorage(
            createBlockEntity("ehv_transformer_n_to_1", EPBlocks.EHV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerBlock.Tier.TIER_EHV, TransformerBlock.Type.TYPE_N_TO_1)),
            TransformerBlockEntity::getEnergyStorageForDirection
    );

    public static final BlockEntityType<BatteryBoxBlockEntity> BATTERY_BOX_ENTITY = registerEnergyStorage(
            createBlockEntity("battery_box", EPBlocks.BATTERY_BOX, BatteryBoxBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AdvancedBatteryBoxBlockEntity> ADVANCED_BATTERY_BOX_ENTITY = registerEnergyStorage(
            createBlockEntity("advanced_battery_box", EPBlocks.ADVANCED_BATTERY_BOX, AdvancedBatteryBoxBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<CreativeBatteryBoxBlockEntity> CREATIVE_BATTERY_BOX_ENTITY = registerEnergyStorage(
            createBlockEntity("creative_battery_box", EPBlocks.CREATIVE_BATTERY_BOX, CreativeBatteryBoxBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<CoalEngineBlockEntity> COAL_ENGINE_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("coal_engine", EPBlocks.COAL_ENGINE, CoalEngineBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<HeatGeneratorBlockEntity> HEAT_GENERATOR_ENTITY = registerEnergyStorage(
            createBlockEntity("heat_generator", EPBlocks.HEAT_GENERATOR, HeatGeneratorBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<ThermalGeneratorBlockEntity> THERMAL_GENERATOR_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    createBlockEntity("thermal_generator", EPBlocks.THERMAL_GENERATOR, ThermalGeneratorBlockEntity::new),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<PoweredLampBlockEntity> POWERED_LAMP_ENTITY = registerEnergyStorage(
            createBlockEntity("powered_lamp", EPBlocks.POWERED_LAMP, PoweredLampBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<PoweredFurnaceBlockEntity> POWERED_FURNACE_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("powered_furnace", EPBlocks.POWERED_FURNACE, PoweredFurnaceBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<AdvancedPoweredFurnaceBlockEntity> ADVANCED_POWERED_FURNACE_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("advanced_powered_furnace", EPBlocks.ADVANCED_POWERED_FURNACE, AdvancedPoweredFurnaceBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<LightningGeneratorBlockEntity> LIGHTING_GENERATOR_ENTITY = registerEnergyStorage(
            createBlockEntity("lightning_generator", EPBlocks.LIGHTNING_GENERATOR, LightningGeneratorBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<EnergizerBlockEntity> ENERGIZER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("energizer", EPBlocks.ENERGIZER, EnergizerBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<ChargingStationBlockEntity> CHARGING_STATION_ENTITY = registerEnergyStorage(
            createBlockEntity("charging_station", EPBlocks.CHARGING_STATION, ChargingStationBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<CrystalGrowthChamberBlockEntity> CRYSTAL_GROWTH_CHAMBER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("crystal_growth_chamber", EPBlocks.CRYSTAL_GROWTH_CHAMBER, CrystalGrowthChamberBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<WeatherControllerBlockEntity> WEATHER_CONTROLLER_ENTITY = registerEnergyStorage(
            createBlockEntity("weather_controller", EPBlocks.WEATHER_CONTROLLER, WeatherControllerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<TimeControllerBlockEntity> TIME_CONTROLLER_ENTITY = registerEnergyStorage(
            createBlockEntity("time_controller", EPBlocks.TIME_CONTROLLER, TimeControllerBlockEntity::new),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<TeleporterBlockEntity> TELEPORTER_ENTITY = registerEnergyStorage(
            registerInventoryStorage(
                    createBlockEntity("teleporter", EPBlocks.TELEPORTER, TeleporterBlockEntity::new),
                    (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(String name, Block block,
            FabricBlockEntityTypeBuilder.Factory<? extends T> factory) {
        return (BlockEntityType<T>)Registry.register(Registries.BLOCK_ENTITY_TYPE, EPAPI.id(name),
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
