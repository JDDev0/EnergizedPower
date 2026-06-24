package me.jddev0.ep.block.entity;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.*;
import me.jddev0.ep.machine.tier.CableTier;
import me.jddev0.ep.machine.tier.TransformerTier;
import me.jddev0.ep.machine.tier.TransformerType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.BiFunction;

public final class EPBlockEntities {
    private EPBlockEntities() {}

    private static BlockEntityType<FluidPipeBlockEntity> createFluidPipeBlockEntity(String name, FluidPipeBlock block) {
        return createBlockEntity(name, block, (blockPos, state) -> new FluidPipeBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<FluidPipeBlockEntity> COPPER_FLUID_PIPE_ENTITY = registerFluidStorage(
            createFluidPipeBlockEntity("copper_fluid_pipe", EPBlocks.COPPER_FLUID_PIPE),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );
    public static final BlockEntityType<FluidPipeBlockEntity> IRON_FLUID_PIPE_ENTITY = registerFluidStorage(
            createFluidPipeBlockEntity("fluid_pipe", EPBlocks.IRON_FLUID_PIPE),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );
    public static final BlockEntityType<FluidPipeBlockEntity> GOLDEN_FLUID_PIPE_ENTITY = registerFluidStorage(
            createFluidPipeBlockEntity("golden_fluid_pipe", EPBlocks.GOLDEN_FLUID_PIPE),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );
    public static final BlockEntityType<FluidPipeBlockEntity> STEEL_FLUID_PIPE_ENTITY = registerFluidStorage(
            createFluidPipeBlockEntity("steel_fluid_pipe", EPBlocks.STEEL_FLUID_PIPE),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );
    public static final BlockEntityType<FluidPipeBlockEntity> PRESSURIZED_FLUID_PIPE_ENTITY = registerFluidStorage(
            createFluidPipeBlockEntity("pressurized_fluid_pipe", EPBlocks.PRESSURIZED_FLUID_PIPE),
            (blockEntity, direction) -> blockEntity.fluidStorage
    );

    private static BlockEntityType<FluidTankBlockEntity> createFluidTankBlockEntity(String name, FluidTankBlock block) {
        return createBlockEntity(name, block, (blockPos, state) -> new FluidTankBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<FluidTankBlockEntity> FLUID_TANK_SMALL_ENTITY =
            createFluidTankBlockEntity("fluid_tank_small", EPBlocks.FLUID_TANK_SMALL);
    public static final BlockEntityType<FluidTankBlockEntity> FLUID_TANK_MEDIUM_ENTITY =
            createFluidTankBlockEntity("fluid_tank_medium", EPBlocks.FLUID_TANK_MEDIUM);
    public static final BlockEntityType<FluidTankBlockEntity> FLUID_TANK_LARGE_ENTITY =
            createFluidTankBlockEntity("fluid_tank_large", EPBlocks.FLUID_TANK_LARGE);

    public static final BlockEntityType<CreativeFluidTankBlockEntity> CREATIVE_FLUID_TANK_ENTITY =
            createBlockEntity("creative_fluid_tank", EPBlocks.CREATIVE_FLUID_TANK, CreativeFluidTankBlockEntity::new);

    private static BlockEntityType<ItemSiloBlockEntity> createItemSiloBlockEntity(
            String name,
            ItemSiloBlock block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemSiloBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<ItemSiloBlockEntity> ITEM_SILO_TINY_ENTITY =
            createItemSiloBlockEntity("item_silo_tiny", EPBlocks.ITEM_SILO_TINY);
    public static final BlockEntityType<ItemSiloBlockEntity> ITEM_SILO_SMALL_ENTITY =
            createItemSiloBlockEntity("item_silo_small", EPBlocks.ITEM_SILO_SMALL);
    public static final BlockEntityType<ItemSiloBlockEntity> ITEM_SILO_MEDIUM_ENTITY =
            createItemSiloBlockEntity("item_silo_medium", EPBlocks.ITEM_SILO_MEDIUM);
    public static final BlockEntityType<ItemSiloBlockEntity> ITEM_SILO_LARGE_ENTITY =
            createItemSiloBlockEntity("item_silo_large", EPBlocks.ITEM_SILO_LARGE);
    public static final BlockEntityType<ItemSiloBlockEntity> ITEM_SILO_GIANT_ENTITY =
            createItemSiloBlockEntity("item_silo_giant", EPBlocks.ITEM_SILO_GIANT);

    public static final BlockEntityType<CreativeItemSiloBlockEntity> CREATIVE_ITEM_SILO_ENTITY = createBlockEntity(
            "creative_item_silo", EPBlocks.CREATIVE_ITEM_SILO, CreativeItemSiloBlockEntity::new);

    private static BlockEntityType<ItemConveyorBeltBlockEntity> createItemConveyorBeltBlockEntity(
            String name,
            ItemConveyorBeltBlock block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemConveyorBeltBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<ItemConveyorBeltBlockEntity> BASIC_ITEM_CONVEYOR_BELT_ENTITY =
            createItemConveyorBeltBlockEntity("item_conveyor_belt", EPBlocks.BASIC_ITEM_CONVEYOR_BELT);
    public static final BlockEntityType<ItemConveyorBeltBlockEntity> FAST_ITEM_CONVEYOR_BELT_ENTITY =
            createItemConveyorBeltBlockEntity("fast_item_conveyor_belt", EPBlocks.FAST_ITEM_CONVEYOR_BELT);
    public static final BlockEntityType<ItemConveyorBeltBlockEntity> EXPRESS_ITEM_CONVEYOR_BELT_ENTITY =
            createItemConveyorBeltBlockEntity("express_item_conveyor_belt", EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT);

    private static BlockEntityType<ItemConveyorBeltLoaderBlockEntity> createItemConveyorBeltLoaderBlockEntity(
            String name,
            ItemConveyorBeltLoaderBlock block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemConveyorBeltLoaderBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<ItemConveyorBeltLoaderBlockEntity> BASIC_ITEM_CONVEYOR_BELT_LOADER_ENTITY =
            createItemConveyorBeltLoaderBlockEntity("item_conveyor_belt_loader", EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER);
    public static final BlockEntityType<ItemConveyorBeltLoaderBlockEntity> FAST_ITEM_CONVEYOR_BELT_LOADER_ENTITY =
            createItemConveyorBeltLoaderBlockEntity("fast_item_conveyor_belt_loader", EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER);
    public static final BlockEntityType<ItemConveyorBeltLoaderBlockEntity> EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ENTITY =
            createItemConveyorBeltLoaderBlockEntity("express_item_conveyor_belt_loader", EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER);

    private static BlockEntityType<ItemConveyorBeltSorterBlockEntity> createItemConveyorBeltSorterBlockEntity(
            String name,
            ItemConveyorBeltSorterBlock block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemConveyorBeltSorterBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<ItemConveyorBeltSorterBlockEntity> BASIC_ITEM_CONVEYOR_BELT_SORTER_ENTITY =
            createItemConveyorBeltSorterBlockEntity("item_conveyor_belt_sorter", EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER);
    public static final BlockEntityType<ItemConveyorBeltSorterBlockEntity> FAST_ITEM_CONVEYOR_BELT_SORTER_ENTITY =
            createItemConveyorBeltSorterBlockEntity("fast_item_conveyor_belt_sorter", EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER);
    public static final BlockEntityType<ItemConveyorBeltSorterBlockEntity> EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ENTITY =
            createItemConveyorBeltSorterBlockEntity("express_item_conveyor_belt_sorter", EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER);

    private static BlockEntityType<ItemConveyorBeltSwitchBlockEntity> createItemConveyorBeltSwitchBlockEntity(
            String name,
            ItemConveyorBeltSwitchBlock block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemConveyorBeltSwitchBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<ItemConveyorBeltSwitchBlockEntity> BASIC_ITEM_CONVEYOR_BELT_SWITCH_ENTITY =
            createItemConveyorBeltSwitchBlockEntity("item_conveyor_belt_switch", EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH);
    public static final BlockEntityType<ItemConveyorBeltSwitchBlockEntity> FAST_ITEM_CONVEYOR_BELT_SWITCH_ENTITY =
            createItemConveyorBeltSwitchBlockEntity("fast_item_conveyor_belt_switch", EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH);
    public static final BlockEntityType<ItemConveyorBeltSwitchBlockEntity> EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ENTITY =
            createItemConveyorBeltSwitchBlockEntity("express_item_conveyor_belt_switch", EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH);

    private static BlockEntityType<ItemConveyorBeltSplitterBlockEntity> createItemConveyorBeltSplitterBlockEntity(
            String name,
            ItemConveyorBeltSplitterBlock block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemConveyorBeltSplitterBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<ItemConveyorBeltSplitterBlockEntity> BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY =
            createItemConveyorBeltSplitterBlockEntity("item_conveyor_belt_splitter", EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER);
    public static final BlockEntityType<ItemConveyorBeltSplitterBlockEntity> FAST_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY =
            createItemConveyorBeltSplitterBlockEntity("fast_conveyor_belt_splitter", EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER);
    public static final BlockEntityType<ItemConveyorBeltSplitterBlockEntity> EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ENTITY =
            createItemConveyorBeltSplitterBlockEntity("express_conveyor_belt_splitter", EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER);

    private static BlockEntityType<ItemConveyorBeltMergerBlockEntity> createItemConveyorBeltMergerBlockEntity(
            String name,
            ItemConveyorBeltMergerBlock block
    ) {
        return createBlockEntity(name, block, (blockPos, state) -> new ItemConveyorBeltMergerBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<ItemConveyorBeltMergerBlockEntity> BASIC_ITEM_CONVEYOR_BELT_MERGER_ENTITY =
            createItemConveyorBeltMergerBlockEntity("item_conveyor_belt_merger", EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER);
    public static final BlockEntityType<ItemConveyorBeltMergerBlockEntity> FAST_ITEM_CONVEYOR_BELT_MERGER_ENTITY =
            createItemConveyorBeltMergerBlockEntity("fast_item_conveyor_belt_merger", EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER);
    public static final BlockEntityType<ItemConveyorBeltMergerBlockEntity> EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ENTITY =
            createItemConveyorBeltMergerBlockEntity("express_item_conveyor_belt_merger", EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER);

    public static final BlockEntityType<CableBlockEntity> TIN_CABLE_ENTITY = createBlockEntity("tin_cable",
            EPBlocks.TIN_CABLE, (blockPos, state) -> new CableBlockEntity(blockPos, state, CableTier.TIN));
    public static final BlockEntityType<CableBlockEntity> COPPER_CABLE_ENTITY = createBlockEntity("copper_cable",
            EPBlocks.COPPER_CABLE, (blockPos, state) -> new CableBlockEntity(blockPos, state, CableTier.COPPER));
    public static final BlockEntityType<CableBlockEntity> GOLD_CABLE_ENTITY = createBlockEntity("gold_cable",
            EPBlocks.GOLD_CABLE, (blockPos, state) -> new CableBlockEntity(blockPos, state, CableTier.GOLD));
    public static final BlockEntityType<CableBlockEntity> ENERGIZED_COPPER_CABLE_ENTITY = createBlockEntity("energized_copper_cable",
            EPBlocks.ENERGIZED_COPPER_CABLE, (blockPos, state) -> new CableBlockEntity(blockPos, state, CableTier.ENERGIZED_COPPER));
    public static final BlockEntityType<CableBlockEntity> ENERGIZED_GOLD_CABLE_ENTITY = createBlockEntity("energized_gold_cable",
            EPBlocks.ENERGIZED_GOLD_CABLE, (blockPos, state) -> new CableBlockEntity(blockPos, state, CableTier.ENERGIZED_GOLD));
    public static final BlockEntityType<CableBlockEntity> ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY = createBlockEntity("energized_crystal_matrix_cable",
            EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE, (blockPos, state) -> new CableBlockEntity(blockPos, state, CableTier.ENERGIZED_CRYSTAL_MATRIX));

    public static final BlockEntityType<AutoCrafterBlockEntity> AUTO_CRAFTER_ENTITY = createBlockEntity("auto_crafter",
            EPBlocks.AUTO_CRAFTER, AutoCrafterBlockEntity::new);

    public static final BlockEntityType<AdvancedAutoCrafterBlockEntity> ADVANCED_AUTO_CRAFTER_ENTITY = createBlockEntity("advanced_auto_crafter",
            EPBlocks.ADVANCED_AUTO_CRAFTER, AdvancedAutoCrafterBlockEntity::new);

    public static final BlockEntityType<PressMoldMakerBlockEntity> PRESS_MOLD_MAKER_ENTITY = createBlockEntity("press_mold_maker",
            EPBlocks.PRESS_MOLD_MAKER, PressMoldMakerBlockEntity::new);

    public static final BlockEntityType<AlloyFurnaceBlockEntity> ALLOY_FURNACE_ENTITY = createBlockEntity("alloy_furnace",
            EPBlocks.ALLOY_FURNACE, AlloyFurnaceBlockEntity::new);

    public static final BlockEntityType<CrusherBlockEntity> CRUSHER_ENTITY = createBlockEntity("crusher",
            EPBlocks.CRUSHER, CrusherBlockEntity::new);

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

    public static final BlockEntityType<PulverizerBlockEntity> PULVERIZER_ENTITY = createBlockEntity("pulverizer",
            EPBlocks.PULVERIZER, PulverizerBlockEntity::new);

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

    public static final BlockEntityType<SawmillBlockEntity> SAWMILL_ENTITY = createBlockEntity("sawmill",
            EPBlocks.SAWMILL, SawmillBlockEntity::new);

    public static final BlockEntityType<CompressorBlockEntity> COMPRESSOR_ENTITY = createBlockEntity("compressor",
            EPBlocks.COMPRESSOR, CompressorBlockEntity::new);

    public static final BlockEntityType<MetalPressBlockEntity> METAL_PRESS_ENTITY = createBlockEntity("metal_press",
            EPBlocks.METAL_PRESS, MetalPressBlockEntity::new);

    public static final BlockEntityType<AutoPressMoldMakerBlockEntity> AUTO_PRESS_MOLD_MAKER_ENTITY = createBlockEntity("auto_press_mold_maker",
            EPBlocks.AUTO_PRESS_MOLD_MAKER, AutoPressMoldMakerBlockEntity::new);

    public static final BlockEntityType<AutoStonecutterBlockEntity> AUTO_STONECUTTER_ENTITY = createBlockEntity("auto_stonecutter",
            EPBlocks.AUTO_STONECUTTER, AutoStonecutterBlockEntity::new);

    public static final BlockEntityType<PlantGrowthChamberBlockEntity> PLANT_GROWTH_CHAMBER_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    registerInventoryStorage(
                            createBlockEntity("plant_growth_chamber", EPBlocks.PLANT_GROWTH_CHAMBER, PlantGrowthChamberBlockEntity::new),
                            (blockEntity, side) -> side == Direction.UP?
                                    blockEntity.itemHandlerTopSided.apply(side):(side == Direction.DOWN?
                                    blockEntity.itemHandlerBottomSided.apply(side):
                                    blockEntity.itemHandlerSidesSided.apply(side))
                    ),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<BlockPlacerBlockEntity> BLOCK_PLACER_ENTITY = createBlockEntity("block_placer",
            EPBlocks.BLOCK_PLACER, BlockPlacerBlockEntity::new);

    public static final BlockEntityType<AssemblingMachineBlockEntity> ASSEMBLING_MACHINE_ENTITY = createBlockEntity("assembling_machine",
            EPBlocks.ASSEMBLING_MACHINE, AssemblingMachineBlockEntity::new);

    public static final BlockEntityType<InductionSmelterBlockEntity> INDUCTION_SMELTER_ENTITY = createBlockEntity("induction_smelter",
            EPBlocks.INDUCTION_SMELTER, InductionSmelterBlockEntity::new);

    public static final BlockEntityType<FluidFreezerBlockEntity> FLUID_FREEZER_ENTITY = createBlockEntity("fluid_freezer",
            EPBlocks.FLUID_FREEZER, FluidFreezerBlockEntity::new);

    public static final BlockEntityType<StoneLiquefierBlockEntity> STONE_LIQUEFIER_ENTITY = registerEnergyStorage(
            registerFluidStorage(
                    registerInventoryStorage(
                            createBlockEntity("stone_liquefier", EPBlocks.STONE_LIQUEFIER, StoneLiquefierBlockEntity::new),
                            (blockEntity, side) -> blockEntity.itemHandlerSided.apply(side)
                    ),
                    (blockEntity, direction) -> blockEntity.fluidStorage
            ),
            (blockEntity, direction) -> blockEntity.limitingEnergyStorage
    );

    public static final BlockEntityType<StoneSolidifierBlockEntity> STONE_SOLIDIFIER_ENTITY = createBlockEntity("stone_solidifier",
            EPBlocks.STONE_SOLIDIFIER, StoneSolidifierBlockEntity::new);

    public static final BlockEntityType<FiltrationPlantBlockEntity> FILTRATION_PLANT_ENTITY = createBlockEntity("filtration_plant",
            EPBlocks.FILTRATION_PLANT, FiltrationPlantBlockEntity::new);

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

    public static final BlockEntityType<FluidFillerBlockEntity> FLUID_FILLER_ENTITY = createBlockEntity("fluid_filler",
            EPBlocks.FLUID_FILLER, FluidFillerBlockEntity::new);

    public static final BlockEntityType<FluidDrainerBlockEntity> FLUID_DRAINER_ENTITY = createBlockEntity("fluid_drainer",
            EPBlocks.FLUID_DRAINER, FluidDrainerBlockEntity::new);

    public static final BlockEntityType<FluidPumpBlockEntity> FLUID_PUMP_ENTITY = createBlockEntity("fluid_pump",
            EPBlocks.FLUID_PUMP, FluidPumpBlockEntity::new);

    public static final BlockEntityType<AdvancedFluidPumpBlockEntity> ADVANCED_FLUID_PUMP_ENTITY = createBlockEntity(
            "advanced_fluid_pump", EPBlocks.ADVANCED_FLUID_PUMP, AdvancedFluidPumpBlockEntity::new);

    public static final BlockEntityType<DrainBlockEntity> DRAIN_ENTITY = createBlockEntity("drain",
            EPBlocks.DRAIN, DrainBlockEntity::new);

    public static final BlockEntityType<ChargerBlockEntity> CHARGER_ENTITY = createBlockEntity("charger",
            EPBlocks.CHARGER, ChargerBlockEntity::new);

    public static final BlockEntityType<AdvancedChargerBlockEntity> ADVANCED_CHARGER_ENTITY = createBlockEntity("advanced_charger",
            EPBlocks.ADVANCED_CHARGER, AdvancedChargerBlockEntity::new);

    public static final BlockEntityType<UnchargerBlockEntity> UNCHARGER_ENTITY = createBlockEntity("uncharger",
            EPBlocks.UNCHARGER, UnchargerBlockEntity::new);

    public static final BlockEntityType<AdvancedUnchargerBlockEntity> ADVANCED_UNCHARGER_ENTITY = createBlockEntity("advanced_uncharger",
            EPBlocks.ADVANCED_UNCHARGER, AdvancedUnchargerBlockEntity::new);

    public static final BlockEntityType<MinecartChargerBlockEntity> MINECART_CHARGER_ENTITY = createBlockEntity("minecart_charger",
            EPBlocks.MINECART_CHARGER, MinecartChargerBlockEntity::new);

    public static final BlockEntityType<AdvancedMinecartChargerBlockEntity> ADVANCED_MINECART_CHARGER_ENTITY = createBlockEntity("advanced_minecart_charger",
            EPBlocks.ADVANCED_MINECART_CHARGER, AdvancedMinecartChargerBlockEntity::new);

    public static final BlockEntityType<MinecartUnchargerBlockEntity> MINECART_UNCHARGER_ENTITY = createBlockEntity("minecart_uncharger",
            EPBlocks.MINECART_UNCHARGER, MinecartUnchargerBlockEntity::new);

    public static final BlockEntityType<AdvancedMinecartUnchargerBlockEntity> ADVANCED_MINECART_UNCHARGER_ENTITY = createBlockEntity("advanced_minecart_uncharger",
            EPBlocks.ADVANCED_MINECART_UNCHARGER, AdvancedMinecartUnchargerBlockEntity::new);

    private static BlockEntityType<SolarPanelBlockEntity> createSolarPanelBlockEntity(String name, SolarPanelBlock block) {
        return createBlockEntity(name, block, (blockPos, state) -> new SolarPanelBlockEntity(blockPos, state, block.getTier()));
    }
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_1 =
            createSolarPanelBlockEntity("solar_panel_1", EPBlocks.SOLAR_PANEL_1);
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_2 =
            createSolarPanelBlockEntity("solar_panel_2", EPBlocks.SOLAR_PANEL_2);
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_3 =
            createSolarPanelBlockEntity("solar_panel_3", EPBlocks.SOLAR_PANEL_3);
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_4 =
            createSolarPanelBlockEntity("solar_panel_4", EPBlocks.SOLAR_PANEL_4);
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_5 =
            createSolarPanelBlockEntity("solar_panel_5", EPBlocks.SOLAR_PANEL_5);
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_6 =
            createSolarPanelBlockEntity("solar_panel_6", EPBlocks.SOLAR_PANEL_6);
    public static final BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL_ENTITY_7 =
            createSolarPanelBlockEntity("solar_panel_7", EPBlocks.SOLAR_PANEL_7);

    public static final BlockEntityType<TransformerBlockEntity> LV_TRANSFORMER_1_TO_N_ENTITY = createBlockEntity("lv_transformer_1_to_n",
            EPBlocks.LV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.LV, TransformerType.TYPE_1_TO_N));
    public static final BlockEntityType<TransformerBlockEntity> LV_TRANSFORMER_3_TO_3_ENTITY = createBlockEntity("lv_transformer_3_to_3",
            EPBlocks.LV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.LV, TransformerType.TYPE_3_TO_3));
    public static final BlockEntityType<TransformerBlockEntity> LV_TRANSFORMER_N_TO_1_ENTITY = createBlockEntity("lv_transformer_n_to_1",
            EPBlocks.LV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.LV, TransformerType.TYPE_N_TO_1));

    public static final BlockEntityType<TransformerBlockEntity> MV_TRANSFORMER_1_TO_N_ENTITY = createBlockEntity("transformer_1_to_n",
            EPBlocks.MV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.MV, TransformerType.TYPE_1_TO_N));
    public static final BlockEntityType<TransformerBlockEntity> MV_TRANSFORMER_3_TO_3_ENTITY = createBlockEntity("transformer_3_to_3",
            EPBlocks.MV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.MV, TransformerType.TYPE_3_TO_3));
    public static final BlockEntityType<TransformerBlockEntity> MV_TRANSFORMER_N_TO_1_ENTITY = createBlockEntity("transformer_n_to_1",
            EPBlocks.MV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.MV, TransformerType.TYPE_N_TO_1));

    public static final BlockEntityType<TransformerBlockEntity> HV_TRANSFORMER_1_TO_N_ENTITY = createBlockEntity("hv_transformer_1_to_n",
            EPBlocks.HV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.HV, TransformerType.TYPE_1_TO_N));
    public static final BlockEntityType<TransformerBlockEntity> HV_TRANSFORMER_3_TO_3_ENTITY = createBlockEntity("hv_transformer_3_to_3",
            EPBlocks.HV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.HV, TransformerType.TYPE_3_TO_3));
    public static final BlockEntityType<TransformerBlockEntity> HV_TRANSFORMER_N_TO_1_ENTITY = createBlockEntity("hv_transformer_n_to_1",
            EPBlocks.HV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.HV, TransformerType.TYPE_N_TO_1));

    public static final BlockEntityType<TransformerBlockEntity> EHV_TRANSFORMER_1_TO_N_ENTITY = createBlockEntity("ehv_transformer_1_to_n",
            EPBlocks.EHV_TRANSFORMER_1_TO_N, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.EHV, TransformerType.TYPE_1_TO_N));
    public static final BlockEntityType<TransformerBlockEntity> EHV_TRANSFORMER_3_TO_3_ENTITY = createBlockEntity("ehv_transformer_3_to_3",
            EPBlocks.EHV_TRANSFORMER_3_TO_3, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.EHV, TransformerType.TYPE_3_TO_3));
    public static final BlockEntityType<TransformerBlockEntity> EHV_TRANSFORMER_N_TO_1_ENTITY = createBlockEntity("ehv_transformer_n_to_1",
            EPBlocks.EHV_TRANSFORMER_N_TO_1, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.EHV, TransformerType.TYPE_N_TO_1));

    public static final BlockEntityType<TransformerBlockEntity> CONFIGURABLE_LV_TRANSFORMER_ENTITY = createBlockEntity("configurable_lv_transformer",
            EPBlocks.CONFIGURABLE_LV_TRANSFORMER, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.LV, TransformerType.CONFIGURABLE));
    public static final BlockEntityType<TransformerBlockEntity> CONFIGURABLE_MV_TRANSFORMER_ENTITY = createBlockEntity("configurable_mv_transformer",
            EPBlocks.CONFIGURABLE_MV_TRANSFORMER, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.MV, TransformerType.CONFIGURABLE));
    public static final BlockEntityType<TransformerBlockEntity> CONFIGURABLE_HV_TRANSFORMER_ENTITY = createBlockEntity("configurable_hv_transformer",
            EPBlocks.CONFIGURABLE_HV_TRANSFORMER, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.HV, TransformerType.CONFIGURABLE));
    public static final BlockEntityType<TransformerBlockEntity> CONFIGURABLE_EHV_TRANSFORMER_ENTITY = createBlockEntity("configurable_ehv_transformer",
            EPBlocks.CONFIGURABLE_EHV_TRANSFORMER, (blockPos, state) ->
                    new TransformerBlockEntity(blockPos, state, TransformerTier.EHV, TransformerType.CONFIGURABLE));

    public static final BlockEntityType<BatteryBoxBlockEntity> BATTERY_BOX_ENTITY = createBlockEntity("battery_box",
            EPBlocks.BATTERY_BOX, BatteryBoxBlockEntity::new);

    public static final BlockEntityType<AdvancedBatteryBoxBlockEntity> ADVANCED_BATTERY_BOX_ENTITY = createBlockEntity("advanced_battery_box",
            EPBlocks.ADVANCED_BATTERY_BOX, AdvancedBatteryBoxBlockEntity::new);

    public static final BlockEntityType<CreativeBatteryBoxBlockEntity> CREATIVE_BATTERY_BOX_ENTITY = createBlockEntity("creative_battery_box",
            EPBlocks.CREATIVE_BATTERY_BOX, CreativeBatteryBoxBlockEntity::new);

    public static final BlockEntityType<CoalEngineBlockEntity> COAL_ENGINE_ENTITY = createBlockEntity("coal_engine",
            EPBlocks.COAL_ENGINE, CoalEngineBlockEntity::new);

    public static final BlockEntityType<HeatGeneratorBlockEntity> HEAT_GENERATOR_ENTITY = createBlockEntity("heat_generator",
            EPBlocks.HEAT_GENERATOR, HeatGeneratorBlockEntity::new);

    public static final BlockEntityType<ThermalGeneratorBlockEntity> THERMAL_GENERATOR_ENTITY = createBlockEntity("thermal_generator",
            EPBlocks.THERMAL_GENERATOR, ThermalGeneratorBlockEntity::new);

    public static final BlockEntityType<PoweredLampBlockEntity> POWERED_LAMP_ENTITY = createBlockEntity("powered_lamp",
            EPBlocks.POWERED_LAMP, PoweredLampBlockEntity::new);

    public static final BlockEntityType<PoweredFurnaceBlockEntity> POWERED_FURNACE_ENTITY = createBlockEntity("powered_furnace",
            EPBlocks.POWERED_FURNACE, PoweredFurnaceBlockEntity::new);

    public static final BlockEntityType<AdvancedPoweredFurnaceBlockEntity> ADVANCED_POWERED_FURNACE_ENTITY = createBlockEntity("advanced_powered_furnace",
            EPBlocks.ADVANCED_POWERED_FURNACE, AdvancedPoweredFurnaceBlockEntity::new);

    public static final BlockEntityType<LightningGeneratorBlockEntity> LIGHTING_GENERATOR_ENTITY = createBlockEntity("lightning_generator",
            EPBlocks.LIGHTNING_GENERATOR, LightningGeneratorBlockEntity::new);

    public static final BlockEntityType<EnergizerBlockEntity> ENERGIZER_ENTITY = createBlockEntity("energizer",
            EPBlocks.ENERGIZER, EnergizerBlockEntity::new);

    public static final BlockEntityType<ChargingStationBlockEntity> CHARGING_STATION_ENTITY = createBlockEntity("charging_station",
            EPBlocks.CHARGING_STATION, ChargingStationBlockEntity::new);

    public static final BlockEntityType<CrystalGrowthChamberBlockEntity> CRYSTAL_GROWTH_CHAMBER_ENTITY = createBlockEntity("crystal_growth_chamber",
            EPBlocks.CRYSTAL_GROWTH_CHAMBER, CrystalGrowthChamberBlockEntity::new);

    public static final BlockEntityType<WeatherControllerBlockEntity> WEATHER_CONTROLLER_ENTITY = createBlockEntity("weather_controller",
            EPBlocks.WEATHER_CONTROLLER, WeatherControllerBlockEntity::new);

    public static final BlockEntityType<TimeControllerBlockEntity> TIME_CONTROLLER_ENTITY = createBlockEntity("time_controller",
            EPBlocks.TIME_CONTROLLER, TimeControllerBlockEntity::new);

    public static final BlockEntityType<TeleporterBlockEntity> TELEPORTER_ENTITY = createBlockEntity("teleporter",
            EPBlocks.TELEPORTER, TeleporterBlockEntity::new);

    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> BlockEntityType<T> createBlockEntity(String name, Block block,
            FabricBlockEntityTypeBuilder.Factory<? extends T> factory) {
        return (BlockEntityType<T>)Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, EPAPI.id(name),
                FabricBlockEntityTypeBuilder.create(factory, block).build(null));
    }

    @Deprecated
    private static <T extends BlockEntity> BlockEntityType<T> registerInventoryStorage(BlockEntityType<T> blockEntityType,
           BiFunction<? super T, Direction, @Nullable Storage<ItemVariant>> provider) {
        ItemStorage.SIDED.registerForBlockEntity(provider, blockEntityType);
        return blockEntityType;
    }

    @Deprecated
    private static <T extends BlockEntity> BlockEntityType<T> registerFluidStorage(BlockEntityType<T> blockEntityType, BiFunction<? super T,
            Direction, @Nullable Storage<FluidVariant>> provider) {
        FluidStorage.SIDED.registerForBlockEntity(provider, blockEntityType);
        return blockEntityType;
    }

    @Deprecated
    private static <T extends BlockEntity> BlockEntityType<T> registerEnergyStorage(BlockEntityType<T> blockEntityType,
            BiFunction<? super T, Direction, @Nullable EnergyStorage> provider) {
        EnergyStorage.SIDED.registerForBlockEntity(provider, blockEntityType);
        return blockEntityType;
    }

    public static void register() {
        registerCapabilities();
    }

    private static void registerCapabilities() {
        RegisterCapabilitiesEvent event = new RegisterCapabilitiesEvent();

        //event.registerBlockEntity(Capabilities.Fluid.BLOCK,
        //        COPPER_FLUID_PIPE_ENTITY, FluidPipeBlockEntity::getFluidHandlerCapability);
        //event.registerBlockEntity(Capabilities.Fluid.BLOCK,
        //        IRON_FLUID_PIPE_ENTITY, FluidPipeBlockEntity::getFluidHandlerCapability);
        //event.registerBlockEntity(Capabilities.Fluid.BLOCK,
        //        GOLDEN_FLUID_PIPE_ENTITY, FluidPipeBlockEntity::getFluidHandlerCapability);
        //event.registerBlockEntity(Capabilities.Fluid.BLOCK,
        //        STEEL_FLUID_PIPE_ENTITY, FluidPipeBlockEntity::getFluidHandlerCapability);
        //event.registerBlockEntity(Capabilities.Fluid.BLOCK,
        //        PRESSURIZED_FLUID_PIPE_ENTITY, FluidPipeBlockEntity::getFluidHandlerCapability);

        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_TANK_SMALL_ENTITY, FluidTankBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_TANK_MEDIUM_ENTITY, FluidTankBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_TANK_LARGE_ENTITY, FluidTankBlockEntity::getFluidHandlerCapability);

        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                CREATIVE_FLUID_TANK_ENTITY, CreativeFluidTankBlockEntity::getFluidHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ITEM_SILO_TINY_ENTITY, ItemSiloBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ITEM_SILO_SMALL_ENTITY, ItemSiloBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ITEM_SILO_MEDIUM_ENTITY, ItemSiloBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ITEM_SILO_LARGE_ENTITY, ItemSiloBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ITEM_SILO_GIANT_ENTITY, ItemSiloBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                CREATIVE_ITEM_SILO_ENTITY, CreativeItemSiloBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                BASIC_ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FAST_ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                EXPRESS_ITEM_CONVEYOR_BELT_ENTITY, ItemConveyorBeltBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                BASIC_ITEM_CONVEYOR_BELT_LOADER_ENTITY, ItemConveyorBeltLoaderBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FAST_ITEM_CONVEYOR_BELT_LOADER_ENTITY, ItemConveyorBeltLoaderBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK,
                EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ENTITY, ItemConveyorBeltLoaderBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                TIN_CABLE_ENTITY, CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                COPPER_CABLE_ENTITY, CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                GOLD_CABLE_ENTITY, CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ENERGIZED_COPPER_CABLE_ENTITY, CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ENERGIZED_GOLD_CABLE_ENTITY, CableBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY, CableBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                AUTO_CRAFTER_ENTITY, AutoCrafterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                AUTO_CRAFTER_ENTITY, AutoCrafterBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ADVANCED_AUTO_CRAFTER_ENTITY, AdvancedAutoCrafterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_AUTO_CRAFTER_ENTITY, AdvancedAutoCrafterBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                PRESS_MOLD_MAKER_ENTITY, PressMoldMakerBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ALLOY_FURNACE_ENTITY, AlloyFurnaceBlockEntity::getItemHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                CRUSHER_ENTITY, CrusherBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CRUSHER_ENTITY, CrusherBlockEntity::getEnergyStorageCapability);

        //event.registerBlockEntity(Capabilities.Item.BLOCK,
        //        ADVANCED_CRUSHER_ENTITY, AdvancedCrusherBlockEntity::getItemHandlerCapability);
        //event.registerBlockEntity(Capabilities.Fluid.BLOCK,
        //        ADVANCED_CRUSHER_ENTITY, AdvancedCrusherBlockEntity::getFluidHandlerCapability);
        //event.registerBlockEntity(Capabilities.Energy.BLOCK,
        //        ADVANCED_CRUSHER_ENTITY, AdvancedCrusherBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                PULVERIZER_ENTITY, PulverizerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                PULVERIZER_ENTITY, PulverizerBlockEntity::getEnergyStorageCapability);

        //event.registerBlockEntity(Capabilities.Item.BLOCK,
        //        ADVANCED_PULVERIZER_ENTITY, AdvancedPulverizerBlockEntity::getItemHandlerCapability);
        //event.registerBlockEntity(Capabilities.Fluid.BLOCK,
        //        ADVANCED_PULVERIZER_ENTITY, AdvancedPulverizerBlockEntity::getFluidHandlerCapability);
        //event.registerBlockEntity(Capabilities.Energy.BLOCK,
        //        ADVANCED_PULVERIZER_ENTITY, AdvancedPulverizerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                SAWMILL_ENTITY, SawmillBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SAWMILL_ENTITY, SawmillBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                COMPRESSOR_ENTITY, CompressorBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                COMPRESSOR_ENTITY, CompressorBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                METAL_PRESS_ENTITY, MetalPressBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                METAL_PRESS_ENTITY, MetalPressBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                AUTO_PRESS_MOLD_MAKER_ENTITY, AutoPressMoldMakerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                AUTO_PRESS_MOLD_MAKER_ENTITY, AutoPressMoldMakerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                AUTO_STONECUTTER_ENTITY, AutoStonecutterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                AUTO_STONECUTTER_ENTITY, AutoStonecutterBlockEntity::getEnergyStorageCapability);

        //event.registerBlockEntity(Capabilities.Item.BLOCK,
        //        PLANT_GROWTH_CHAMBER_ENTITY, PlantGrowthChamberBlockEntity::getItemHandlerCapability);
        //event.registerBlockEntity(Capabilities.Fluid.BLOCK,
        //        PLANT_GROWTH_CHAMBER_ENTITY, PlantGrowthChamberBlockEntity::getFluidHandlerCapability);
        //event.registerBlockEntity(Capabilities.Energy.BLOCK,
        //        PLANT_GROWTH_CHAMBER_ENTITY, PlantGrowthChamberBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                BLOCK_PLACER_ENTITY, BlockPlacerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                BLOCK_PLACER_ENTITY, BlockPlacerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ASSEMBLING_MACHINE_ENTITY, AssemblingMachineBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ASSEMBLING_MACHINE_ENTITY, AssemblingMachineBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                INDUCTION_SMELTER_ENTITY, InductionSmelterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                INDUCTION_SMELTER_ENTITY, InductionSmelterBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FLUID_FREEZER_ENTITY, FluidFreezerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_FREEZER_ENTITY, FluidFreezerBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                FLUID_FREEZER_ENTITY, FluidFreezerBlockEntity::getEnergyStorageCapability);

        //event.registerBlockEntity(Capabilities.Item.BLOCK,
        //        STONE_LIQUEFIER_ENTITY, StoneLiquefierBlockEntity::getItemHandlerCapability);
        //event.registerBlockEntity(Capabilities.Fluid.BLOCK,
        //        STONE_LIQUEFIER_ENTITY, StoneLiquefierBlockEntity::getFluidHandlerCapability);
        //event.registerBlockEntity(Capabilities.Energy.BLOCK,
        //        STONE_LIQUEFIER_ENTITY, StoneLiquefierBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                STONE_SOLIDIFIER_ENTITY, StoneSolidifierBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                STONE_SOLIDIFIER_ENTITY, StoneSolidifierBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                STONE_SOLIDIFIER_ENTITY, StoneSolidifierBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FILTRATION_PLANT_ENTITY, FiltrationPlantBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FILTRATION_PLANT_ENTITY, FiltrationPlantBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                FILTRATION_PLANT_ENTITY, FiltrationPlantBlockEntity::getEnergyStorageCapability);

        //event.registerBlockEntity(Capabilities.Item.BLOCK,
        //        FLUID_TRANSPOSER_ENTITY, FluidTransposerBlockEntity::getItemHandlerCapability);
        //event.registerBlockEntity(Capabilities.Fluid.BLOCK,
        //        FLUID_TRANSPOSER_ENTITY, FluidTransposerBlockEntity::getFluidHandlerCapability);
        //event.registerBlockEntity(Capabilities.Energy.BLOCK,
        //        FLUID_TRANSPOSER_ENTITY, FluidTransposerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FLUID_FILLER_ENTITY, FluidFillerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_FILLER_ENTITY, FluidFillerBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                FLUID_FILLER_ENTITY, FluidFillerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FLUID_DRAINER_ENTITY, FluidDrainerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_DRAINER_ENTITY, FluidDrainerBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                FLUID_DRAINER_ENTITY, FluidDrainerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                FLUID_PUMP_ENTITY, FluidPumpBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                FLUID_PUMP_ENTITY, FluidPumpBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                FLUID_PUMP_ENTITY, FluidPumpBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ADVANCED_FLUID_PUMP_ENTITY, AdvancedFluidPumpBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                ADVANCED_FLUID_PUMP_ENTITY, AdvancedFluidPumpBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_FLUID_PUMP_ENTITY, AdvancedFluidPumpBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                DRAIN_ENTITY, DrainBlockEntity::getFluidHandlerCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                CHARGER_ENTITY, ChargerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CHARGER_ENTITY, ChargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                UNCHARGER_ENTITY, UnchargerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                UNCHARGER_ENTITY, UnchargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ADVANCED_CHARGER_ENTITY, AdvancedChargerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_CHARGER_ENTITY, AdvancedChargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ADVANCED_UNCHARGER_ENTITY, AdvancedUnchargerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_UNCHARGER_ENTITY, AdvancedUnchargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                MINECART_CHARGER_ENTITY, MinecartChargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                MINECART_UNCHARGER_ENTITY, MinecartUnchargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_MINECART_CHARGER_ENTITY, AdvancedMinecartChargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_MINECART_UNCHARGER_ENTITY, AdvancedMinecartUnchargerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_1, SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_2, SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_3, SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_4, SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_5, SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_6, SolarPanelBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                SOLAR_PANEL_ENTITY_7, SolarPanelBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                LV_TRANSFORMER_1_TO_N_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                LV_TRANSFORMER_3_TO_3_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                LV_TRANSFORMER_N_TO_1_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                MV_TRANSFORMER_1_TO_N_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                MV_TRANSFORMER_3_TO_3_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                MV_TRANSFORMER_N_TO_1_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                HV_TRANSFORMER_1_TO_N_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                HV_TRANSFORMER_3_TO_3_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                HV_TRANSFORMER_N_TO_1_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                EHV_TRANSFORMER_1_TO_N_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                EHV_TRANSFORMER_3_TO_3_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                EHV_TRANSFORMER_N_TO_1_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CONFIGURABLE_LV_TRANSFORMER_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CONFIGURABLE_MV_TRANSFORMER_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CONFIGURABLE_HV_TRANSFORMER_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CONFIGURABLE_EHV_TRANSFORMER_ENTITY, TransformerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                BATTERY_BOX_ENTITY, BatteryBoxBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_BATTERY_BOX_ENTITY, AdvancedBatteryBoxBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CREATIVE_BATTERY_BOX_ENTITY, CreativeBatteryBoxBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                COAL_ENGINE_ENTITY, CoalEngineBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                COAL_ENGINE_ENTITY, CoalEngineBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                HEAT_GENERATOR_ENTITY, HeatGeneratorBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Fluid.BLOCK,
                THERMAL_GENERATOR_ENTITY, ThermalGeneratorBlockEntity::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                THERMAL_GENERATOR_ENTITY, ThermalGeneratorBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                POWERED_LAMP_ENTITY, PoweredLampBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                POWERED_FURNACE_ENTITY, PoweredFurnaceBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                POWERED_FURNACE_ENTITY, PoweredFurnaceBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ADVANCED_POWERED_FURNACE_ENTITY, AdvancedPoweredFurnaceBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ADVANCED_POWERED_FURNACE_ENTITY, AdvancedPoweredFurnaceBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                LIGHTING_GENERATOR_ENTITY, LightningGeneratorBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                ENERGIZER_ENTITY, EnergizerBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                ENERGIZER_ENTITY, EnergizerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CHARGING_STATION_ENTITY, ChargingStationBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                CRYSTAL_GROWTH_CHAMBER_ENTITY, CrystalGrowthChamberBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                CRYSTAL_GROWTH_CHAMBER_ENTITY, CrystalGrowthChamberBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                WEATHER_CONTROLLER_ENTITY, WeatherControllerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                TIME_CONTROLLER_ENTITY, TimeControllerBlockEntity::getEnergyStorageCapability);

        event.registerBlockEntity(Capabilities.Item.BLOCK,
                TELEPORTER_ENTITY, TeleporterBlockEntity::getItemHandlerCapability);
        event.registerBlockEntity(Capabilities.Energy.BLOCK,
                TELEPORTER_ENTITY, TeleporterBlockEntity::getEnergyStorageCapability);
    }

    /**
     * Registration adapter to match NeoForge
     */
    private static final class RegisterCapabilitiesEvent {
        private RegisterCapabilitiesEvent() {}

        private <T, C extends @Nullable Object, BE extends BlockEntity> void registerBlockEntity(
                Capability<T, C> cap, BlockEntityType<BE> blockEntityType, ICapabilityProvider<? super BE, C, T> provider) {
            if(cap == Capabilities.Item.BLOCK) {
                ItemStorage.SIDED.registerForBlockEntity((be, ctx) -> Capabilities.Item.BLOCK.wrapProvider(provider).getCapability(be, ctx), blockEntityType);
            }else if(cap == Capabilities.Fluid.BLOCK) {
                FluidStorage.SIDED.registerForBlockEntity((be, ctx) -> Capabilities.Fluid.BLOCK.wrapProvider(provider).getCapability(be, ctx), blockEntityType);
            }else if(cap == Capabilities.Energy.BLOCK) {
                EnergyStorage.SIDED.registerForBlockEntity((be, ctx) -> Capabilities.Energy.BLOCK.wrapProvider(provider).getCapability(be, ctx), blockEntityType);
            }else {
                throw new RuntimeException("Unknown capability: " + cap.getClass().getName());
            }
        }
    }

    /**
     * Dummy class to match NeoForge
     */
    private static final class Capability<T, C extends @Nullable Object> {
        @SuppressWarnings("unchecked")
        private <O> ICapabilityProvider<O, C, T> wrapProvider(ICapabilityProvider<O, ?, ?> provider) {
            return (ICapabilityProvider<O, C, T>)provider;
        }
    }

    /**
     * Dummy class to match NeoForge
     */
    @FunctionalInterface
    private interface ICapabilityProvider<O, C extends @Nullable Object, T> {
        @Nullable
        T getCapability(O object, C context);
    }

    /**
     * Dummy class to match NeoForge
     */
    private static final class Capabilities {
        private Capabilities() {}

        private static final class Item {
            private Item() {}

            private static final Capability<Storage<ItemVariant>, @Nullable Direction> BLOCK = new Capability<>();
        }

        private static final class Fluid {
            private Fluid() {}

            private static final Capability<Storage<FluidVariant>, @Nullable Direction> BLOCK = new Capability<>();
        }

        private static final class Energy {
            private Energy() {}

            private static final Capability<EnergyStorage, @Nullable Direction> BLOCK = new Capability<>();
        }
    }
}
