package me.jddev0.ep.block;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.tier.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.intprovider.ConstantIntProvider;

public final class EPBlocks {
    private EPBlocks() {}

    public static final Block SILICON_BLOCK = registerBlock("silicon_block",
            new Block(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item SILICON_BLOCK_ITEM = createBlockItem("silicon_block", SILICON_BLOCK);

    public static final Block TIN_BLOCK = registerBlock("tin_block",
            new Block(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item TIN_BLOCK_ITEM = createBlockItem("tin_block", TIN_BLOCK);

    public static final Block SAWDUST_BLOCK = registerBlock("sawdust_block",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).
                    requiresTool().strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)));
    public static final Item SAWDUST_BLOCK_ITEM = createBlockItem("sawdust_block", SAWDUST_BLOCK);

    public static final Block TIN_ORE = registerBlock("tin_ore",
            new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.0f, 3.0f)));
    public static final Item TIN_ORE_ITEM = createBlockItem("tin_ore", TIN_ORE);

    public static final Block DEEPSLATE_TIN_ORE = registerBlock("deepslate_tin_ore",
            new ExperienceDroppingBlock(ConstantIntProvider.create(0), AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY)
                    .requiresTool().strength(4.5f, 3.0f).sounds(BlockSoundGroup.DEEPSLATE)));
    public static final Item DEEPSLATE_TIN_ORE_ITEM = createBlockItem("deepslate_tin_ore", DEEPSLATE_TIN_ORE);

    public static final Block RAW_TIN_BLOCK = registerBlock("raw_tin_block",
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.RAW_IRON_PINK)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(5.0f, 6.0f)));
    public static final Item RAW_TIN_BLOCK_ITEM = createBlockItem("raw_tin_block", RAW_TIN_BLOCK);
    
    public static final ItemConveyorBeltBlock ITEM_CONVEYOR_BELT = registerBlock("item_conveyor_belt",
            new ItemConveyorBeltBlock(AbstractBlock.Settings.create().noCollision().
                    strength(2.5f, 3.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ITEM_CONVEYOR_BELT_ITEM = createBlockItem("item_conveyor_belt",
            new ItemConveyorBeltBlock.Item(ITEM_CONVEYOR_BELT, new Item.Settings()));

    public static final Block ITEM_CONVEYOR_BELT_LOADER = registerBlock("item_conveyor_belt_loader",
            new ItemConveyorBeltLoaderBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE)));
    public static final Item ITEM_CONVEYOR_BELT_LOADER_ITEM = createBlockItem("item_conveyor_belt_loader", ITEM_CONVEYOR_BELT_LOADER);

    public static final Block ITEM_CONVEYOR_BELT_SORTER = registerBlock("item_conveyor_belt_sorter",
            new ItemConveyorBeltSorterBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE)));
    public static final Item ITEM_CONVEYOR_BELT_SORTER_ITEM = createBlockItem("item_conveyor_belt_sorter", ITEM_CONVEYOR_BELT_SORTER);

    public static final Block ITEM_CONVEYOR_BELT_SWITCH = registerBlock("item_conveyor_belt_switch",
            new ItemConveyorBeltSwitchBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE)));
    public static final Item ITEM_CONVEYOR_BELT_SWITCH_ITEM = createBlockItem("item_conveyor_belt_switch", ITEM_CONVEYOR_BELT_SWITCH);

    public static final Block ITEM_CONVEYOR_BELT_SPLITTER = registerBlock("item_conveyor_belt_splitter",
            new ItemConveyorBeltSplitterBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE)));
    public static final Item ITEM_CONVEYOR_BELT_SPLITTER_ITEM = createBlockItem("item_conveyor_belt_splitter", ITEM_CONVEYOR_BELT_SPLITTER);

    public static final Block ITEM_CONVEYOR_BELT_MERGER = registerBlock("item_conveyor_belt_merger",
            new ItemConveyorBeltMergerBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE)));
    public static final Item ITEM_CONVEYOR_BELT_MERGER_ITEM = createBlockItem("item_conveyor_belt_merger", ITEM_CONVEYOR_BELT_MERGER);

    public static final FluidPipeBlock IRON_FLUID_PIPE = registerBlock("fluid_pipe",
            new FluidPipeBlock(FluidPipeTier.IRON));
    public static final Item IRON_FLUID_PIPE_ITEM = createBlockItem("fluid_pipe",
            new FluidPipeBlock.Item(IRON_FLUID_PIPE, new Item.Settings(), FluidPipeTier.IRON));

    public static final FluidPipeBlock GOLDEN_FLUID_PIPE = registerBlock("golden_fluid_pipe",
            new FluidPipeBlock(FluidPipeTier.GOLDEN));
    public static final Item GOLDEN_FLUID_PIPE_ITEM = createBlockItem("golden_fluid_pipe",
            new FluidPipeBlock.Item(GOLDEN_FLUID_PIPE, new Item.Settings(), FluidPipeTier.GOLDEN));

    public static final FluidTankBlock FLUID_TANK_SMALL = registerBlock("fluid_tank_small",
            new FluidTankBlock(FluidTankTier.SMALL));
    public static final Item FLUID_TANK_SMALL_ITEM = createBlockItem("fluid_tank_small",
            new FluidTankBlock.Item(FLUID_TANK_SMALL, new Item.Settings(), FluidTankTier.SMALL));

    public static final FluidTankBlock FLUID_TANK_MEDIUM = registerBlock("fluid_tank_medium",
            new FluidTankBlock(FluidTankTier.MEDIUM));
    public static final Item FLUID_TANK_MEDIUM_ITEM = createBlockItem("fluid_tank_medium",
            new FluidTankBlock.Item(FLUID_TANK_MEDIUM, new Item.Settings(), FluidTankTier.MEDIUM));

    public static final FluidTankBlock FLUID_TANK_LARGE = registerBlock("fluid_tank_large",
            new FluidTankBlock(FluidTankTier.LARGE));
    public static final Item FLUID_TANK_LARGE_ITEM = createBlockItem("fluid_tank_large",
            new FluidTankBlock.Item(FLUID_TANK_LARGE, new Item.Settings(), FluidTankTier.LARGE));

    public static final CreativeFluidTankBlock CREATIVE_FLUID_TANK = registerBlock("creative_fluid_tank",
            new CreativeFluidTankBlock(AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).
                    requiresTool().strength(-1.f, 3600000.f).dropsNothing()));
    public static final Item CREATIVE_FLUID_TANK_ITEM = createBlockItem("creative_fluid_tank",
            new CreativeFluidTankBlock.Item(CREATIVE_FLUID_TANK, new Item.Settings()));

    private static Item createCableBlockItem(String name, CableBlock block) {
        return Registry.register(Registries.ITEM, EPAPI.id(name),
                new CableBlock.Item(block, new Item.Settings(), block.getTier()));
    }
    public static final CableBlock TIN_CABLE = registerBlock("tin_cable",
            new CableBlock(CableTier.TIN));
    public static final Item TIN_CABLE_ITEM = createCableBlockItem("tin_cable", TIN_CABLE);

    public static final CableBlock COPPER_CABLE = registerBlock("copper_cable",
            new CableBlock(CableTier.COPPER));
    public static final Item COPPER_CABLE_ITEM = createCableBlockItem("copper_cable", COPPER_CABLE);

    public static final CableBlock GOLD_CABLE = registerBlock("gold_cable",
            new CableBlock(CableTier.GOLD));
    public static final Item GOLD_CABLE_ITEM = createCableBlockItem("gold_cable", GOLD_CABLE);

    public static final CableBlock ENERGIZED_COPPER_CABLE = registerBlock("energized_copper_cable",
            new CableBlock(CableTier.ENERGIZED_COPPER));
    public static final Item ENERGIZED_COPPER_CABLE_ITEM = createCableBlockItem("energized_copper_cable",
            ENERGIZED_COPPER_CABLE);

    public static final CableBlock ENERGIZED_GOLD_CABLE = registerBlock("energized_gold_cable",
            new CableBlock(CableTier.ENERGIZED_GOLD));
    public static final Item ENERGIZED_GOLD_CABLE_ITEM = createCableBlockItem("energized_gold_cable",
            ENERGIZED_GOLD_CABLE);

    public static final CableBlock ENERGIZED_CRYSTAL_MATRIX_CABLE = registerBlock("energized_crystal_matrix_cable",
            new CableBlock(CableTier.ENERGIZED_CRYSTAL_MATRIX));
    public static final Item ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM = createCableBlockItem("energized_crystal_matrix_cable",
            ENERGIZED_CRYSTAL_MATRIX_CABLE);

    private static Item createTransformerBlockItem(String name, TransformerBlock block) {
        return Registry.register(Registries.ITEM, EPAPI.id(name),
                new TransformerBlock.Item(block, new Item.Settings(), block.getTier(), block.getTransformerType()));
    }
    public static final TransformerBlock LV_TRANSFORMER_1_TO_N = registerBlock("lv_transformer_1_to_n",
            new TransformerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerTier.LV, TransformerType.TYPE_1_TO_N));
    public static final Item LV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("lv_transformer_1_to_n",
            LV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock LV_TRANSFORMER_3_TO_3 = registerBlock("lv_transformer_3_to_3",
            new TransformerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerTier.LV, TransformerType.TYPE_3_TO_3));
    public static final Item LV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("lv_transformer_3_to_3",
            LV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock LV_TRANSFORMER_N_TO_1 = registerBlock("lv_transformer_n_to_1",
            new TransformerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerTier.LV, TransformerType.TYPE_N_TO_1));
    public static final Item LV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("lv_transformer_n_to_1",
            LV_TRANSFORMER_N_TO_1);

    public static final TransformerBlock MV_TRANSFORMER_1_TO_N = registerBlock("transformer_1_to_n",
            new TransformerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerTier.MV, TransformerType.TYPE_1_TO_N));
    public static final Item MV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("transformer_1_to_n",
            MV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock MV_TRANSFORMER_3_TO_3 = registerBlock("transformer_3_to_3",
            new TransformerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerTier.MV, TransformerType.TYPE_3_TO_3));
    public static final Item MV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("transformer_3_to_3",
            MV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock MV_TRANSFORMER_N_TO_1 = registerBlock("transformer_n_to_1",
            new TransformerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerTier.MV, TransformerType.TYPE_N_TO_1));
    public static final Item MV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("transformer_n_to_1",
            MV_TRANSFORMER_N_TO_1);

    public static final TransformerBlock HV_TRANSFORMER_1_TO_N = registerBlock("hv_transformer_1_to_n",
            new TransformerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerTier.HV, TransformerType.TYPE_1_TO_N));
    public static final Item HV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("hv_transformer_1_to_n",
            HV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock HV_TRANSFORMER_3_TO_3 = registerBlock("hv_transformer_3_to_3",
            new TransformerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerTier.HV, TransformerType.TYPE_3_TO_3));
    public static final Item HV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("hv_transformer_3_to_3",
            HV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock HV_TRANSFORMER_N_TO_1 = registerBlock("hv_transformer_n_to_1",
            new TransformerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerTier.HV, TransformerType.TYPE_N_TO_1));
    public static final Item HV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("hv_transformer_n_to_1",
            HV_TRANSFORMER_N_TO_1);

    public static final TransformerBlock EHV_TRANSFORMER_1_TO_N = registerBlock("ehv_transformer_1_to_n",
            new TransformerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerTier.EHV, TransformerType.TYPE_1_TO_N));
    public static final Item EHV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("ehv_transformer_1_to_n",
            EHV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock EHV_TRANSFORMER_3_TO_3 = registerBlock("ehv_transformer_3_to_3",
            new TransformerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerTier.EHV, TransformerType.TYPE_3_TO_3));
    public static final Item EHV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("ehv_transformer_3_to_3",
            EHV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock EHV_TRANSFORMER_N_TO_1 = registerBlock("ehv_transformer_n_to_1",
            new TransformerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerTier.EHV, TransformerType.TYPE_N_TO_1));
    public static final Item EHV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("ehv_transformer_n_to_1",
            EHV_TRANSFORMER_N_TO_1);

    public static final Block PRESS_MOLD_MAKER = registerBlock("press_mold_maker",
            new PressMoldMakerBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0f, 6.0f).sounds(BlockSoundGroup.STONE)));
    public static final Item PRESS_MOLD_MAKER_ITEM = createBlockItem("press_mold_maker", PRESS_MOLD_MAKER);

    public static final Block ALLOY_FURNACE = registerBlock("alloy_furnace",
            new AlloyFurnaceBlock(AbstractBlock.Settings.create().mapColor(MapColor.RED).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0f, 6.0f).sounds(BlockSoundGroup.STONE).
                    luminance(AlloyFurnaceBlock.LIGHT_EMISSION)));
    public static final Item ALLOY_FURNACE_ITEM = createBlockItem("alloy_furnace", ALLOY_FURNACE);

    public static final Block AUTO_CRAFTER = registerBlock("auto_crafter",
            new AutoCrafterBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item AUTO_CRAFTER_ITEM = createBlockItem("auto_crafter",
            new AutoCrafterBlock.Item(AUTO_CRAFTER, new Item.Settings()));

    public static final Block ADVANCED_AUTO_CRAFTER = registerBlock("advanced_auto_crafter",
            new AdvancedAutoCrafterBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_AUTO_CRAFTER_ITEM = createBlockItem("advanced_auto_crafter",
            new AdvancedAutoCrafterBlock.Item(ADVANCED_AUTO_CRAFTER, new Item.Settings()));

    public static final Block BATTERY_BOX = registerBlock("battery_box",
            new BatteryBoxBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item BATTERY_BOX_ITEM = createBlockItem("battery_box",
            new BatteryBoxBlock.Item(BATTERY_BOX, new Item.Settings()));

    public static final Block ADVANCED_BATTERY_BOX = registerBlock("advanced_battery_box",
            new AdvancedBatteryBoxBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_BATTERY_BOX_ITEM = createBlockItem("advanced_battery_box",
            new AdvancedBatteryBoxBlock.Item(ADVANCED_BATTERY_BOX, new Item.Settings()));

    public static final Block CREATIVE_BATTERY_BOX = registerBlock("creative_battery_box",
            new CreativeBatteryBoxBlock(AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).
                    requiresTool().strength(-1.f, 3600000.f).dropsNothing()));
    public static final Item CREATIVE_BATTERY_BOX_ITEM = createBlockItem("creative_battery_box",
            new CreativeBatteryBoxBlock.Item(CREATIVE_BATTERY_BOX, new Item.Settings()));

    public static final Block CRUSHER = registerBlock("crusher",
            new CrusherBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item CRUSHER_ITEM = createBlockItem("crusher", CRUSHER);

    public static final Block ADVANCED_CRUSHER = registerBlock("advanced_crusher",
            new AdvancedCrusherBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_CRUSHER_ITEM = createBlockItem("advanced_crusher",
            new AdvancedCrusherBlock.Item(ADVANCED_CRUSHER, new Item.Settings()));

    public static final Block PULVERIZER = registerBlock("pulverizer",
            new PulverizerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item PULVERIZER_ITEM = createBlockItem("pulverizer", PULVERIZER);

    public static final Block ADVANCED_PULVERIZER = registerBlock("advanced_pulverizer",
            new AdvancedPulverizerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_PULVERIZER_ITEM = createBlockItem("advanced_pulverizer",
            new AdvancedPulverizerBlock.Item(ADVANCED_PULVERIZER, new Item.Settings()));

    public static final Block SAWMILL = registerBlock("sawmill",
            new SawmillBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item SAWMILL_ITEM = createBlockItem("sawmill", SAWMILL);

    public static final Block COMPRESSOR = registerBlock("compressor",
            new CompressorBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item COMPRESSOR_ITEM = createBlockItem("compressor", COMPRESSOR);

    public static final Block METAL_PRESS = registerBlock("metal_press",
            new MetalPressBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item METAL_PRESS_ITEM = createBlockItem("metal_press",
            new MetalPressBlock.Item(METAL_PRESS, new Item.Settings()));

    public static final Block AUTO_PRESS_MOLD_MAKER = registerBlock("auto_press_mold_maker",
            new AutoPressMoldMakerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item AUTO_PRESS_MOLD_MAKER_ITEM = createBlockItem("auto_press_mold_maker", AUTO_PRESS_MOLD_MAKER);

    public static final Block AUTO_STONECUTTER = registerBlock("auto_stonecutter",
            new AutoStonecutterBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item AUTO_STONECUTTER_ITEM = createBlockItem("auto_stonecutter", AUTO_STONECUTTER);

    public static final Block PLANT_GROWTH_CHAMBER = registerBlock("plant_growth_chamber",
            new PlantGrowthChamberBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item PLANT_GROWTH_CHAMBER_ITEM = createBlockItem("plant_growth_chamber", PLANT_GROWTH_CHAMBER);

    public static final Block BLOCK_PLACER = registerBlock("block_placer",
            new BlockPlacerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item BLOCK_PLACER_ITEM = createBlockItem("block_placer", BLOCK_PLACER);

    public static final Block ASSEMBLING_MACHINE = registerBlock("assembling_machine",
            new AssemblingMachineBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ASSEMBLING_MACHINE_ITEM = createBlockItem("assembling_machine", ASSEMBLING_MACHINE);

    public static final Block INDUCTION_SMELTER = registerBlock("induction_smelter",
            new InductionSmelterBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(InductionSmelterBlock.LIGHT_EMISSION)));
    public static final Item INDUCTION_SMELTER_ITEM = createBlockItem("induction_smelter", INDUCTION_SMELTER);

    public static final Block FLUID_FILLER = registerBlock("fluid_filler",
            new FluidFillerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item FLUID_FILLER_ITEM = createBlockItem("fluid_filler", FLUID_FILLER);

    public static final Block STONE_LIQUEFIER = registerBlock("stone_liquefier",
            new StoneLiquefierBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item STONE_LIQUEFIER_ITEM = createBlockItem("stone_liquefier", STONE_LIQUEFIER);

    public static final Block STONE_SOLIDIFIER = registerBlock("stone_solidifier",
            new StoneSolidifierBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item STONE_SOLIDIFIER_ITEM = createBlockItem("stone_solidifier", STONE_SOLIDIFIER);

    public static final Block FILTRATION_PLANT = registerBlock("filtration_plant",
            new FiltrationPlantBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item FILTRATION_PLANT_ITEM = createBlockItem("filtration_plant",
            new FiltrationPlantBlock.Item(FILTRATION_PLANT, new Item.Settings()));

    public static final Block FLUID_TRANSPOSER = registerBlock("fluid_transposer",
            new FluidTransposerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item FLUID_TRANSPOSER_ITEM = createBlockItem("fluid_transposer",
            FLUID_TRANSPOSER);

    public static final Block FLUID_DRAINER = registerBlock("fluid_drainer",
            new FluidDrainerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item FLUID_DRAINER_ITEM = createBlockItem("fluid_drainer", FLUID_DRAINER);

    public static final Block FLUID_PUMP = registerBlock("fluid_pump",
            new FluidPumpBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item FLUID_PUMP_ITEM = createBlockItem("fluid_pump", FLUID_PUMP);

    public static final Block ADVANCED_FLUID_PUMP = registerBlock("advanced_fluid_pump",
            new AdvancedFluidPumpBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_FLUID_PUMP_ITEM = createBlockItem("advanced_fluid_pump", ADVANCED_FLUID_PUMP);

    public static final Block DRAIN = registerBlock("drain",
            new DrainBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item DRAIN_ITEM = createBlockItem("drain",
            new DrainBlock.Item(DRAIN, new Item.Settings()));

    public static final Block CHARGER = registerBlock("charger",
            new ChargerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item CHARGER_ITEM = createBlockItem("charger",
            new ChargerBlock.Item(CHARGER, new Item.Settings()));

    public static final Block ADVANCED_CHARGER = registerBlock("advanced_charger",
            new AdvancedChargerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_CHARGER_ITEM = createBlockItem("advanced_charger",
            new AdvancedChargerBlock.Item(ADVANCED_CHARGER, new Item.Settings()));

    public static final Block UNCHARGER = registerBlock("uncharger",
            new UnchargerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item UNCHARGER_ITEM = createBlockItem("uncharger", UNCHARGER);

    public static final Block ADVANCED_UNCHARGER = registerBlock("advanced_uncharger",
            new AdvancedUnchargerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_UNCHARGER_ITEM = createBlockItem("advanced_uncharger", ADVANCED_UNCHARGER);

    public static final Block MINECART_CHARGER = registerBlock("minecart_charger",
            new MinecartChargerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item MINECART_CHARGER_ITEM = createBlockItem("minecart_charger",
            new MinecartChargerBlock.Item(MINECART_CHARGER, new Item.Settings()));

    public static final Block ADVANCED_MINECART_CHARGER = registerBlock("advanced_minecart_charger",
            new AdvancedMinecartChargerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_MINECART_CHARGER_ITEM = createBlockItem("advanced_minecart_charger",
            new AdvancedMinecartChargerBlock.Item(ADVANCED_MINECART_CHARGER, new Item.Settings()));

    public static final Block MINECART_UNCHARGER = registerBlock("minecart_uncharger",
            new MinecartUnchargerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item MINECART_UNCHARGER_ITEM = createBlockItem("minecart_uncharger",
            new MinecartUnchargerBlock.Item(MINECART_UNCHARGER, new Item.Settings()));

    public static final Block ADVANCED_MINECART_UNCHARGER = registerBlock("advanced_minecart_uncharger",
            new AdvancedMinecartUnchargerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_MINECART_UNCHARGER_ITEM = createBlockItem("advanced_minecart_uncharger",
            new AdvancedMinecartUnchargerBlock.Item(ADVANCED_MINECART_UNCHARGER, new Item.Settings()));


    private static Item createSolarPanelBlockItem(String name, SolarPanelBlock block) {
        return Registry.register(Registries.ITEM, EPAPI.id(name),
                new SolarPanelBlock.Item(block, new Item.Settings(), block.getTier()));
    }
    public static final SolarPanelBlock SOLAR_PANEL_1 = registerBlock("solar_panel_1",
            new SolarPanelBlock(SolarPanelTier.TIER_1));
    public static final Item SOLAR_PANEL_ITEM_1 = createSolarPanelBlockItem("solar_panel_1", SOLAR_PANEL_1);

    public static final SolarPanelBlock SOLAR_PANEL_2 = registerBlock("solar_panel_2",
            new SolarPanelBlock(SolarPanelTier.TIER_2));
    public static final Item SOLAR_PANEL_ITEM_2 = createSolarPanelBlockItem("solar_panel_2", SOLAR_PANEL_2);

    public static final SolarPanelBlock SOLAR_PANEL_3 = registerBlock("solar_panel_3",
            new SolarPanelBlock(SolarPanelTier.TIER_3));
    public static final Item SOLAR_PANEL_ITEM_3 = createSolarPanelBlockItem("solar_panel_3", SOLAR_PANEL_3);

    public static final SolarPanelBlock SOLAR_PANEL_4 = registerBlock("solar_panel_4",
            new SolarPanelBlock(SolarPanelTier.TIER_4));
    public static final Item SOLAR_PANEL_ITEM_4 = createSolarPanelBlockItem("solar_panel_4", SOLAR_PANEL_4);

    public static final SolarPanelBlock SOLAR_PANEL_5 = registerBlock("solar_panel_5",
            new SolarPanelBlock(SolarPanelTier.TIER_5));
    public static final Item SOLAR_PANEL_ITEM_5 = createSolarPanelBlockItem("solar_panel_5", SOLAR_PANEL_5);

    public static final SolarPanelBlock SOLAR_PANEL_6 = registerBlock("solar_panel_6",
            new SolarPanelBlock(SolarPanelTier.TIER_6));
    public static final Item SOLAR_PANEL_ITEM_6 = createSolarPanelBlockItem("solar_panel_6", SOLAR_PANEL_6);

    public static final Block COAL_ENGINE = registerBlock("coal_engine",
            new CoalEngineBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(CoalEngineBlock.LIGHT_EMISSION)));
    public static final Item COAL_ENGINE_ITEM = createBlockItem("coal_engine", COAL_ENGINE);

    public static final Block HEAT_GENERATOR = registerBlock("heat_generator",
            new HeatGeneratorBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item HEAT_GENERATOR_ITEM = createBlockItem("heat_generator", HEAT_GENERATOR);

    public static final Block THERMAL_GENERATOR = registerBlock("thermal_generator",
            new ThermalGeneratorBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item THERMAL_GENERATOR_ITEM = createBlockItem("thermal_generator", THERMAL_GENERATOR);

    public static final Block POWERED_LAMP = registerBlock("powered_lamp",
            new PoweredLampBlock(AbstractBlock.Settings.create().
                    strength(.3f).sounds(BlockSoundGroup.GLASS).
                    luminance(PoweredLampBlock.LIGHT_EMISSION)));
    public static final Item POWERED_LAMP_ITEM = createBlockItem("powered_lamp", POWERED_LAMP);

    public static final Block POWERED_FURNACE = registerBlock("powered_furnace",
            new PoweredFurnaceBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(PoweredFurnaceBlock.LIGHT_EMISSION)));
    public static final Item POWERED_FURNACE_ITEM = createBlockItem("powered_furnace", POWERED_FURNACE);

    public static final Block ADVANCED_POWERED_FURNACE = registerBlock("advanced_powered_furnace",
            new AdvancedPoweredFurnaceBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(AdvancedPoweredFurnaceBlock.LIGHT_EMISSION)));
    public static final Item ADVANCED_POWERED_FURNACE_ITEM = createBlockItem("advanced_powered_furnace", ADVANCED_POWERED_FURNACE);

    public static final Block LIGHTNING_GENERATOR = registerBlock("lightning_generator",
            new LightningGeneratorBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(LightningGeneratorBlock.LIGHT_EMISSION)));
    public static final Item LIGHTNING_GENERATOR_ITEM = createBlockItem("lightning_generator",
            new LightningGeneratorBlock.Item(LIGHTNING_GENERATOR, new Item.Settings()));

    public static final Block ENERGIZER = registerBlock("energizer",
            new EnergizerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(EnergizerBlock.LIGHT_EMISSION)));
    public static final Item ENERGIZER_ITEM = createBlockItem("energizer", ENERGIZER);

    public static final Block CHARGING_STATION = registerBlock("charging_station",
            new ChargingStationBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(ChargingStationBlock.LIGHT_EMISSION)));
    public static final Item CHARGING_STATION_ITEM = createBlockItem("charging_station",
            new ChargingStationBlock.Item(CHARGING_STATION, new Item.Settings()));

    public static final Block CRYSTAL_GROWTH_CHAMBER = registerBlock("crystal_growth_chamber",
            new CrystalGrowthChamberBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item CRYSTAL_GROWTH_CHAMBER_ITEM = createBlockItem("crystal_growth_chamber", CRYSTAL_GROWTH_CHAMBER);

    public static final Block WEATHER_CONTROLLER = registerBlock("weather_controller",
            new WeatherControllerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item WEATHER_CONTROLLER_ITEM = createBlockItem("weather_controller", WEATHER_CONTROLLER);

    public static final Block TIME_CONTROLLER = registerBlock("time_controller",
            new TimeControllerBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item TIME_CONTROLLER_ITEM = createBlockItem("time_controller", TIME_CONTROLLER);

    public static final Block TELEPORTER = registerBlock("teleporter",
            new TeleporterBlock(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item TELEPORTER_ITEM = createBlockItem("teleporter",
            new TeleporterBlock.Item(TELEPORTER, new Item.Settings()));

    public static final Block BASIC_MACHINE_FRAME = registerBlock("basic_machine_frame",
            new Block(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item BASIC_MACHINE_FRAME_ITEM = createBlockItem("basic_machine_frame", BASIC_MACHINE_FRAME);

    public static final Block HARDENED_MACHINE_FRAME = registerBlock("hardened_machine_frame",
            new Block(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item HARDENED_MACHINE_FRAME_ITEM = createBlockItem("hardened_machine_frame", HARDENED_MACHINE_FRAME);

    public static final Block ADVANCED_MACHINE_FRAME = registerBlock("advanced_machine_frame",
            new Block(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("advanced_machine_frame", ADVANCED_MACHINE_FRAME);

    public static final Block REINFORCED_ADVANCED_MACHINE_FRAME = registerBlock("reinforced_advanced_machine_frame",
            new Block(AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item REINFORCED_ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("reinforced_advanced_machine_frame", REINFORCED_ADVANCED_MACHINE_FRAME);

    private static <T extends Block> T registerBlock(String name, T block) {
        return Registry.register(Registries.BLOCK, EPAPI.id(name), block);
    }

    private static Item createBlockItem(String name, Item item) {
        return Registry.register(Registries.ITEM, EPAPI.id(name), item);
    }

    private static Item createBlockItem(String name, Block block, Item.Settings props) {
        return Registry.register(Registries.ITEM, EPAPI.id(name),
                new BlockItem(block, props));
    }

    private static Item createBlockItem(String name, Block block) {
        return createBlockItem(name, block, new Item.Settings());
    }

    public static void register() {

    }
}
