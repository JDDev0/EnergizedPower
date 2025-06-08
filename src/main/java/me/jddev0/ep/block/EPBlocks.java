package me.jddev0.ep.block;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.item.EPItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class EPBlocks {
    private EPBlocks() {}

    public static Block registerBlock(String name, AbstractBlock.Settings props) {
        return registerBlock(name, Block::new, props);
    }

    public static <T extends Block> T registerBlock(String name, Function<AbstractBlock.Settings, T> factory, AbstractBlock.Settings props) {
        Identifier blockId = EPAPI.id(name);
        return Registry.register(Registries.BLOCK, blockId, factory.apply(props.
                registryKey(RegistryKey.of(RegistryKeys.BLOCK, blockId))));
    }

    public static Item createBlockItem(String name, Block block) {
        return createBlockItem(name, BlockItem::new, block, new Item.Settings());
    }

    public static Item createBlockItem(String name, BiFunction<Block, Item.Settings, Item> factory, Block block) {
        return createBlockItem(name, props -> factory.apply(block, props), new Item.Settings());
    }

    public static Item createBlockItem(String name, BiFunction<Block, Item.Settings, Item> factory, Block block, Item.Settings props) {
        return createBlockItem(name, p -> factory.apply(block, p), props);
    }

    public static Item createBlockItem(String name, Function<Item.Settings, Item> factory) {
        return createBlockItem(name, factory, new Item.Settings());
    }

    public static Item createBlockItem(String name, Function<Item.Settings, Item> factory, Item.Settings props) {
        return EPItems.registerItem(name, factory, props);
    }

    public static final Block SILICON_BLOCK = registerBlock("silicon_block",
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item SILICON_BLOCK_ITEM = createBlockItem("silicon_block", SILICON_BLOCK);

    public static final Block TIN_BLOCK = registerBlock("tin_block",
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item TIN_BLOCK_ITEM = createBlockItem("tin_block", TIN_BLOCK);

    public static final Block SAWDUST_BLOCK = registerBlock("sawdust_block",
            AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).requiresTool().
                    strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD));
    public static final Item SAWDUST_BLOCK_ITEM = createBlockItem("sawdust_block", SAWDUST_BLOCK);

    public static final Block TIN_ORE = registerBlock("tin_ore",
            props -> new ExperienceDroppingBlock(ConstantIntProvider.create(0), props),
            AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.0f, 3.0f));
    public static final Item TIN_ORE_ITEM = createBlockItem("tin_ore", TIN_ORE);

    public static final Block DEEPSLATE_TIN_ORE = registerBlock("deepslate_tin_ore",
            props -> new ExperienceDroppingBlock(ConstantIntProvider.create(0), props),
            AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY)
                    .requiresTool().strength(4.5f, 3.0f).sounds(BlockSoundGroup.DEEPSLATE));
    public static final Item DEEPSLATE_TIN_ORE_ITEM = createBlockItem("deepslate_tin_ore", DEEPSLATE_TIN_ORE);

    public static final Block RAW_TIN_BLOCK = registerBlock("raw_tin_block",
            AbstractBlock.Settings.create().mapColor(MapColor.RAW_IRON_PINK)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(5.0f, 6.0f));
    public static final Item RAW_TIN_BLOCK_ITEM = createBlockItem("raw_tin_block", RAW_TIN_BLOCK);
    
    public static final ItemConveyorBeltBlock ITEM_CONVEYOR_BELT = registerBlock("item_conveyor_belt",
            ItemConveyorBeltBlock::new, AbstractBlock.Settings.create().noCollision().
                    strength(2.5f, 3.0f).sounds(BlockSoundGroup.METAL));
    public static final Item ITEM_CONVEYOR_BELT_ITEM = createBlockItem("item_conveyor_belt",
            ItemConveyorBeltBlock.Item::new, ITEM_CONVEYOR_BELT);

    public static final Block ITEM_CONVEYOR_BELT_LOADER = registerBlock("item_conveyor_belt_loader",
            ItemConveyorBeltLoaderBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE));
    public static final Item ITEM_CONVEYOR_BELT_LOADER_ITEM = createBlockItem("item_conveyor_belt_loader", ITEM_CONVEYOR_BELT_LOADER);

    public static final Block ITEM_CONVEYOR_BELT_SORTER = registerBlock("item_conveyor_belt_sorter",
            ItemConveyorBeltSorterBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE));
    public static final Item ITEM_CONVEYOR_BELT_SORTER_ITEM = createBlockItem("item_conveyor_belt_sorter", ITEM_CONVEYOR_BELT_SORTER);

    public static final Block ITEM_CONVEYOR_BELT_SWITCH = registerBlock("item_conveyor_belt_switch",
            ItemConveyorBeltSwitchBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE));
    public static final Item ITEM_CONVEYOR_BELT_SWITCH_ITEM = createBlockItem("item_conveyor_belt_switch", ITEM_CONVEYOR_BELT_SWITCH);

    public static final Block ITEM_CONVEYOR_BELT_SPLITTER = registerBlock("item_conveyor_belt_splitter",
            ItemConveyorBeltSplitterBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE));
    public static final Item ITEM_CONVEYOR_BELT_SPLITTER_ITEM = createBlockItem("item_conveyor_belt_splitter", ITEM_CONVEYOR_BELT_SPLITTER);

    public static final Block ITEM_CONVEYOR_BELT_MERGER = registerBlock("item_conveyor_belt_merger",
            ItemConveyorBeltMergerBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE));
    public static final Item ITEM_CONVEYOR_BELT_MERGER_ITEM = createBlockItem("item_conveyor_belt_merger", ITEM_CONVEYOR_BELT_MERGER);

    public static final FluidPipeBlock IRON_FLUID_PIPE = registerBlock("fluid_pipe",
            props -> new FluidPipeBlock(FluidPipeBlock.Tier.IRON, props), FluidPipeBlock.Tier.IRON.getProperties());
    public static final Item IRON_FLUID_PIPE_ITEM = createBlockItem("fluid_pipe",
            props -> new FluidPipeBlock.Item(IRON_FLUID_PIPE, props, FluidPipeBlock.Tier.IRON));

    public static final FluidPipeBlock GOLDEN_FLUID_PIPE = registerBlock("golden_fluid_pipe",
            props -> new FluidPipeBlock(FluidPipeBlock.Tier.GOLDEN, props), FluidPipeBlock.Tier.GOLDEN.getProperties());
    public static final Item GOLDEN_FLUID_PIPE_ITEM = createBlockItem("golden_fluid_pipe",
            props -> new FluidPipeBlock.Item(GOLDEN_FLUID_PIPE, props, FluidPipeBlock.Tier.GOLDEN));

    public static final FluidTankBlock FLUID_TANK_SMALL = registerBlock("fluid_tank_small",
            props -> new FluidTankBlock(FluidTankBlock.Tier.SMALL, props), FluidTankBlock.Tier.SMALL.getProperties());
    public static final Item FLUID_TANK_SMALL_ITEM = createBlockItem("fluid_tank_small",
            props -> new FluidTankBlock.Item(FLUID_TANK_SMALL, props, FluidTankBlock.Tier.SMALL));

    public static final FluidTankBlock FLUID_TANK_MEDIUM = registerBlock("fluid_tank_medium",
            props -> new FluidTankBlock(FluidTankBlock.Tier.MEDIUM, props), FluidTankBlock.Tier.MEDIUM.getProperties());
    public static final Item FLUID_TANK_MEDIUM_ITEM = createBlockItem("fluid_tank_medium",
            props -> new FluidTankBlock.Item(FLUID_TANK_MEDIUM, props, FluidTankBlock.Tier.MEDIUM));

    public static final FluidTankBlock FLUID_TANK_LARGE = registerBlock("fluid_tank_large",
            props -> new FluidTankBlock(FluidTankBlock.Tier.LARGE, props), FluidTankBlock.Tier.LARGE.getProperties());
    public static final Item FLUID_TANK_LARGE_ITEM = createBlockItem("fluid_tank_large",
            props -> new FluidTankBlock.Item(FLUID_TANK_LARGE, props, FluidTankBlock.Tier.LARGE));

    public static final CreativeFluidTankBlock CREATIVE_FLUID_TANK = registerBlock("creative_fluid_tank",
            CreativeFluidTankBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).
                    requiresTool().strength(-1.f, 3600000.f).dropsNothing());
    public static final Item CREATIVE_FLUID_TANK_ITEM = createBlockItem("creative_fluid_tank",
            CreativeFluidTankBlock.Item::new, CREATIVE_FLUID_TANK);

    private static Item createCableBlockItem(String name, CableBlock block) {
        return createBlockItem(name, props -> new CableBlock.Item(block, props, block.getTier()));
    }
    public static final CableBlock TIN_CABLE = registerBlock("tin_cable",
            props -> new CableBlock(CableBlock.Tier.TIER_TIN, props), CableBlock.Tier.TIER_TIN.getProperties());
    public static final Item TIN_CABLE_ITEM = createCableBlockItem("tin_cable", TIN_CABLE);

    public static final CableBlock COPPER_CABLE = registerBlock("copper_cable",
            props -> new CableBlock(CableBlock.Tier.TIER_COPPER, props), CableBlock.Tier.TIER_COPPER.getProperties());
    public static final Item COPPER_CABLE_ITEM = createCableBlockItem("copper_cable", COPPER_CABLE);

    public static final CableBlock GOLD_CABLE = registerBlock("gold_cable",
            props -> new CableBlock(CableBlock.Tier.TIER_GOLD, props), CableBlock.Tier.TIER_GOLD.getProperties());
    public static final Item GOLD_CABLE_ITEM = createCableBlockItem("gold_cable", GOLD_CABLE);

    public static final CableBlock ENERGIZED_COPPER_CABLE = registerBlock("energized_copper_cable",
            props -> new CableBlock(CableBlock.Tier.TIER_ENERGIZED_COPPER, props), CableBlock.Tier.TIER_ENERGIZED_COPPER.getProperties());
    public static final Item ENERGIZED_COPPER_CABLE_ITEM = createCableBlockItem("energized_copper_cable",
            ENERGIZED_COPPER_CABLE);

    public static final CableBlock ENERGIZED_GOLD_CABLE = registerBlock("energized_gold_cable",
            props -> new CableBlock(CableBlock.Tier.TIER_ENERGIZED_GOLD, props), CableBlock.Tier.TIER_ENERGIZED_GOLD.getProperties());
    public static final Item ENERGIZED_GOLD_CABLE_ITEM = createCableBlockItem("energized_gold_cable",
            ENERGIZED_GOLD_CABLE);

    public static final CableBlock ENERGIZED_CRYSTAL_MATRIX_CABLE = registerBlock("energized_crystal_matrix_cable",
            props -> new CableBlock(CableBlock.Tier.TIER_ENERGIZED_CRYSTAL_MATRIX, props), CableBlock.Tier.TIER_ENERGIZED_CRYSTAL_MATRIX.getProperties());
    public static final Item ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM = createCableBlockItem("energized_crystal_matrix_cable",
            ENERGIZED_CRYSTAL_MATRIX_CABLE);

    private static Item createTransformerBlockItem(String name, TransformerBlock block) {
        return createBlockItem(name, props -> new TransformerBlock.Item(block, props, block.getTier(), block.getTransformerType()));
    }
    public static final TransformerBlock LV_TRANSFORMER_1_TO_N = registerBlock("lv_transformer_1_to_n",
            props -> new TransformerBlock(props, TransformerBlock.Tier.TIER_LV, TransformerBlock.Type.TYPE_1_TO_N),
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item LV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("lv_transformer_1_to_n",
            LV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock LV_TRANSFORMER_3_TO_3 = registerBlock("lv_transformer_3_to_3",
            props -> new TransformerBlock(props, TransformerBlock.Tier.TIER_LV, TransformerBlock.Type.TYPE_3_TO_3),
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item LV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("lv_transformer_3_to_3",
            LV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock LV_TRANSFORMER_N_TO_1 = registerBlock("lv_transformer_n_to_1",
            props -> new TransformerBlock(props, TransformerBlock.Tier.TIER_LV, TransformerBlock.Type.TYPE_N_TO_1),
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item LV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("lv_transformer_n_to_1",
            LV_TRANSFORMER_N_TO_1);

    public static final TransformerBlock MV_TRANSFORMER_1_TO_N = registerBlock("transformer_1_to_n",
            props -> new TransformerBlock(props, TransformerBlock.Tier.TIER_MV, TransformerBlock.Type.TYPE_1_TO_N),
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item MV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("transformer_1_to_n",
            MV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock MV_TRANSFORMER_3_TO_3 = registerBlock("transformer_3_to_3",
            props -> new TransformerBlock(props, TransformerBlock.Tier.TIER_MV, TransformerBlock.Type.TYPE_3_TO_3),
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item MV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("transformer_3_to_3",
            MV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock MV_TRANSFORMER_N_TO_1 = registerBlock("transformer_n_to_1",
            props -> new TransformerBlock(props, TransformerBlock.Tier.TIER_MV, TransformerBlock.Type.TYPE_N_TO_1),
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item MV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("transformer_n_to_1",
            MV_TRANSFORMER_N_TO_1);

    public static final TransformerBlock HV_TRANSFORMER_1_TO_N = registerBlock("hv_transformer_1_to_n",
            props -> new TransformerBlock(props, TransformerBlock.Tier.TIER_HV, TransformerBlock.Type.TYPE_1_TO_N),
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item HV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("hv_transformer_1_to_n",
            HV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock HV_TRANSFORMER_3_TO_3 = registerBlock("hv_transformer_3_to_3",
            props -> new TransformerBlock(props, TransformerBlock.Tier.TIER_HV, TransformerBlock.Type.TYPE_3_TO_3),
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item HV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("hv_transformer_3_to_3",
            HV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock HV_TRANSFORMER_N_TO_1 = registerBlock("hv_transformer_n_to_1",
            props -> new TransformerBlock(props, TransformerBlock.Tier.TIER_HV, TransformerBlock.Type.TYPE_N_TO_1),
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item HV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("hv_transformer_n_to_1",
            HV_TRANSFORMER_N_TO_1);

    public static final TransformerBlock EHV_TRANSFORMER_1_TO_N = registerBlock("ehv_transformer_1_to_n",
            props -> new TransformerBlock(props, TransformerBlock.Tier.TIER_EHV, TransformerBlock.Type.TYPE_1_TO_N),
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item EHV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("ehv_transformer_1_to_n",
            EHV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock EHV_TRANSFORMER_3_TO_3 = registerBlock("ehv_transformer_3_to_3",
            props -> new TransformerBlock(props, TransformerBlock.Tier.TIER_EHV, TransformerBlock.Type.TYPE_3_TO_3),
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item EHV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("ehv_transformer_3_to_3",
            EHV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock EHV_TRANSFORMER_N_TO_1 = registerBlock("ehv_transformer_n_to_1",
            props -> new TransformerBlock(props, TransformerBlock.Tier.TIER_EHV, TransformerBlock.Type.TYPE_N_TO_1),
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item EHV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("ehv_transformer_n_to_1",
            EHV_TRANSFORMER_N_TO_1);

    public static final Block PRESS_MOLD_MAKER = registerBlock("press_mold_maker",
            PressMoldMakerBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.RED).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0f, 6.0f).sounds(BlockSoundGroup.STONE));
    public static final Item PRESS_MOLD_MAKER_ITEM = createBlockItem("press_mold_maker", PRESS_MOLD_MAKER);

    public static final Block ALLOY_FURNACE = registerBlock("alloy_furnace",
            AlloyFurnaceBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.RED).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresTool().strength(2.0f, 6.0f).sounds(BlockSoundGroup.STONE).
                    luminance(AlloyFurnaceBlock.LIGHT_EMISSION));
    public static final Item ALLOY_FURNACE_ITEM = createBlockItem("alloy_furnace", ALLOY_FURNACE);

    public static final Block AUTO_CRAFTER = registerBlock("auto_crafter",
            AutoCrafterBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item AUTO_CRAFTER_ITEM = createBlockItem("auto_crafter",
            AutoCrafterBlock.Item::new, AUTO_CRAFTER);

    public static final Block ADVANCED_AUTO_CRAFTER = registerBlock("advanced_auto_crafter",
            AdvancedAutoCrafterBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item ADVANCED_AUTO_CRAFTER_ITEM = createBlockItem("advanced_auto_crafter",
            AdvancedAutoCrafterBlock.Item::new, ADVANCED_AUTO_CRAFTER);

    public static final Block BATTERY_BOX = registerBlock("battery_box",
            BatteryBoxBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item BATTERY_BOX_ITEM = createBlockItem("battery_box",
            BatteryBoxBlock.Item::new, BATTERY_BOX);

    public static final Block ADVANCED_BATTERY_BOX = registerBlock("advanced_battery_box",
            AdvancedBatteryBoxBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item ADVANCED_BATTERY_BOX_ITEM = createBlockItem("advanced_battery_box",
            AdvancedBatteryBoxBlock.Item::new, ADVANCED_BATTERY_BOX);

    public static final Block CREATIVE_BATTERY_BOX = registerBlock("creative_battery_box",
            CreativeBatteryBoxBlock::new, AbstractBlock.Settings.create().mapColor(MapColor.PURPLE).
                    requiresTool().strength(-1.f, 3600000.f).dropsNothing());
    public static final Item CREATIVE_BATTERY_BOX_ITEM = createBlockItem("creative_battery_box",
            CreativeBatteryBoxBlock.Item::new, CREATIVE_BATTERY_BOX);

    public static final Block CRUSHER = registerBlock("crusher",
            CrusherBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item CRUSHER_ITEM = createBlockItem("crusher", CRUSHER);

    public static final Block ADVANCED_CRUSHER = registerBlock("advanced_crusher",
            AdvancedCrusherBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item ADVANCED_CRUSHER_ITEM = createBlockItem("advanced_crusher",
            AdvancedCrusherBlock.Item::new, ADVANCED_CRUSHER);

    public static final Block PULVERIZER = registerBlock("pulverizer",
            PulverizerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item PULVERIZER_ITEM = createBlockItem("pulverizer", PULVERIZER);

    public static final Block ADVANCED_PULVERIZER = registerBlock("advanced_pulverizer",
            AdvancedPulverizerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item ADVANCED_PULVERIZER_ITEM = createBlockItem("advanced_pulverizer",
            AdvancedPulverizerBlock.Item::new, ADVANCED_PULVERIZER);

    public static final Block SAWMILL = registerBlock("sawmill",
            SawmillBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item SAWMILL_ITEM = createBlockItem("sawmill", SAWMILL);

    public static final Block COMPRESSOR = registerBlock("compressor",
            CompressorBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item COMPRESSOR_ITEM = createBlockItem("compressor", COMPRESSOR);

    public static final Block METAL_PRESS = registerBlock("metal_press",
            MetalPressBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item METAL_PRESS_ITEM = createBlockItem("metal_press",
            MetalPressBlock.Item::new, METAL_PRESS);

    public static final Block AUTO_PRESS_MOLD_MAKER = registerBlock("auto_press_mold_maker",
            AutoPressMoldMakerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item AUTO_PRESS_MOLD_MAKER_ITEM = createBlockItem("auto_press_mold_maker", AUTO_PRESS_MOLD_MAKER);

    public static final Block AUTO_STONECUTTER = registerBlock("auto_stonecutter",
            AutoStonecutterBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item AUTO_STONECUTTER_ITEM = createBlockItem("auto_stonecutter", AUTO_STONECUTTER);

    public static final Block PLANT_GROWTH_CHAMBER = registerBlock("plant_growth_chamber",
            PlantGrowthChamberBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item PLANT_GROWTH_CHAMBER_ITEM = createBlockItem("plant_growth_chamber", PLANT_GROWTH_CHAMBER);

    public static final Block BLOCK_PLACER = registerBlock("block_placer",
            BlockPlacerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item BLOCK_PLACER_ITEM = createBlockItem("block_placer", BLOCK_PLACER);

    public static final Block ASSEMBLING_MACHINE = registerBlock("assembling_machine",
            AssemblingMachineBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item ASSEMBLING_MACHINE_ITEM = createBlockItem("assembling_machine", ASSEMBLING_MACHINE);

    public static final Block INDUCTION_SMELTER = registerBlock("induction_smelter",
            InductionSmelterBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(InductionSmelterBlock.LIGHT_EMISSION));
    public static final Item INDUCTION_SMELTER_ITEM = createBlockItem("induction_smelter", INDUCTION_SMELTER);

    public static final Block FLUID_FILLER = registerBlock("fluid_filler",
            FluidFillerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item FLUID_FILLER_ITEM = createBlockItem("fluid_filler", FLUID_FILLER);

    public static final Block STONE_LIQUEFIER = registerBlock("stone_liquefier",
            StoneLiquefierBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item STONE_LIQUEFIER_ITEM = createBlockItem("stone_liquefier", STONE_LIQUEFIER);

    public static final Block STONE_SOLIDIFIER = registerBlock("stone_solidifier",
            StoneSolidifierBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item STONE_SOLIDIFIER_ITEM = createBlockItem("stone_solidifier", STONE_SOLIDIFIER);

    public static final Block FILTRATION_PLANT = registerBlock("filtration_plant",
            FiltrationPlantBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item FILTRATION_PLANT_ITEM = createBlockItem("filtration_plant",
            FiltrationPlantBlock.Item::new, FILTRATION_PLANT);

    public static final Block FLUID_TRANSPOSER = registerBlock("fluid_transposer",
            FluidTransposerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item FLUID_TRANSPOSER_ITEM = createBlockItem("fluid_transposer",
            FLUID_TRANSPOSER);

    public static final Block FLUID_DRAINER = registerBlock("fluid_drainer",
            FluidDrainerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item FLUID_DRAINER_ITEM = createBlockItem("fluid_drainer", FLUID_DRAINER);

    public static final Block FLUID_PUMP = registerBlock("fluid_pump",
            FluidPumpBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item FLUID_PUMP_ITEM = createBlockItem("fluid_pump", FLUID_PUMP);

    public static final Block ADVANCED_FLUID_PUMP = registerBlock("advanced_fluid_pump",
            AdvancedFluidPumpBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item ADVANCED_FLUID_PUMP_ITEM = createBlockItem("advanced_fluid_pump", ADVANCED_FLUID_PUMP);

    public static final Block DRAIN = registerBlock("drain",
            DrainBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item DRAIN_ITEM = createBlockItem("drain",
            DrainBlock.Item::new, DRAIN);

    public static final Block CHARGER = registerBlock("charger",
            ChargerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item CHARGER_ITEM = createBlockItem("charger",
            ChargerBlock.Item::new, CHARGER);

    public static final Block ADVANCED_CHARGER = registerBlock("advanced_charger",
            AdvancedChargerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item ADVANCED_CHARGER_ITEM = createBlockItem("advanced_charger",
            AdvancedChargerBlock.Item::new, ADVANCED_CHARGER);

    public static final Block UNCHARGER = registerBlock("uncharger",
            UnchargerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item UNCHARGER_ITEM = createBlockItem("uncharger", UNCHARGER);

    public static final Block ADVANCED_UNCHARGER = registerBlock("advanced_uncharger",
            AdvancedUnchargerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item ADVANCED_UNCHARGER_ITEM = createBlockItem("advanced_uncharger", ADVANCED_UNCHARGER);

    public static final Block MINECART_CHARGER = registerBlock("minecart_charger",
            MinecartChargerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item MINECART_CHARGER_ITEM = createBlockItem("minecart_charger",
            MinecartChargerBlock.Item::new, MINECART_CHARGER);

    public static final Block ADVANCED_MINECART_CHARGER = registerBlock("advanced_minecart_charger",
            AdvancedMinecartChargerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item ADVANCED_MINECART_CHARGER_ITEM = createBlockItem("advanced_minecart_charger",
            AdvancedMinecartChargerBlock.Item::new, ADVANCED_MINECART_CHARGER);

    public static final Block MINECART_UNCHARGER = registerBlock("minecart_uncharger",
            MinecartUnchargerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item MINECART_UNCHARGER_ITEM = createBlockItem("minecart_uncharger",
            MinecartUnchargerBlock.Item::new, MINECART_UNCHARGER);

    public static final Block ADVANCED_MINECART_UNCHARGER = registerBlock("advanced_minecart_uncharger",
            AdvancedMinecartUnchargerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item ADVANCED_MINECART_UNCHARGER_ITEM = createBlockItem("advanced_minecart_uncharger",
            AdvancedMinecartUnchargerBlock.Item::new, ADVANCED_MINECART_UNCHARGER);


    private static Item createSolarPanelBlockItem(String name, SolarPanelBlock block) {
        return createBlockItem(name, props -> new SolarPanelBlock.Item(block, props, block.getTier()));
    }
    public static final SolarPanelBlock SOLAR_PANEL_1 = registerBlock("solar_panel_1",
            props -> new SolarPanelBlock(SolarPanelBlock.Tier.TIER_1, props), SolarPanelBlock.Tier.TIER_1.getProperties());
    public static final Item SOLAR_PANEL_ITEM_1 = createSolarPanelBlockItem("solar_panel_1", SOLAR_PANEL_1);

    public static final SolarPanelBlock SOLAR_PANEL_2 = registerBlock("solar_panel_2",
            props -> new SolarPanelBlock(SolarPanelBlock.Tier.TIER_2, props), SolarPanelBlock.Tier.TIER_2.getProperties());
    public static final Item SOLAR_PANEL_ITEM_2 = createSolarPanelBlockItem("solar_panel_2", SOLAR_PANEL_2);

    public static final SolarPanelBlock SOLAR_PANEL_3 = registerBlock("solar_panel_3",
            props -> new SolarPanelBlock(SolarPanelBlock.Tier.TIER_3, props), SolarPanelBlock.Tier.TIER_3.getProperties());
    public static final Item SOLAR_PANEL_ITEM_3 = createSolarPanelBlockItem("solar_panel_3", SOLAR_PANEL_3);

    public static final SolarPanelBlock SOLAR_PANEL_4 = registerBlock("solar_panel_4",
            props -> new SolarPanelBlock(SolarPanelBlock.Tier.TIER_4, props), SolarPanelBlock.Tier.TIER_4.getProperties());
    public static final Item SOLAR_PANEL_ITEM_4 = createSolarPanelBlockItem("solar_panel_4", SOLAR_PANEL_4);

    public static final SolarPanelBlock SOLAR_PANEL_5 = registerBlock("solar_panel_5",
            props -> new SolarPanelBlock(SolarPanelBlock.Tier.TIER_5, props), SolarPanelBlock.Tier.TIER_5.getProperties());
    public static final Item SOLAR_PANEL_ITEM_5 = createSolarPanelBlockItem("solar_panel_5", SOLAR_PANEL_5);

    public static final SolarPanelBlock SOLAR_PANEL_6 = registerBlock("solar_panel_6",
            props -> new SolarPanelBlock(SolarPanelBlock.Tier.TIER_6, props), SolarPanelBlock.Tier.TIER_6.getProperties());
    public static final Item SOLAR_PANEL_ITEM_6 = createSolarPanelBlockItem("solar_panel_6", SOLAR_PANEL_6);

    public static final Block COAL_ENGINE = registerBlock("coal_engine",
            CoalEngineBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(CoalEngineBlock.LIGHT_EMISSION));
    public static final Item COAL_ENGINE_ITEM = createBlockItem("coal_engine", COAL_ENGINE);

    public static final Block HEAT_GENERATOR = registerBlock("heat_generator",
            HeatGeneratorBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item HEAT_GENERATOR_ITEM = createBlockItem("heat_generator", HEAT_GENERATOR);

    public static final Block THERMAL_GENERATOR = registerBlock("thermal_generator",
            ThermalGeneratorBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item THERMAL_GENERATOR_ITEM = createBlockItem("thermal_generator", THERMAL_GENERATOR);

    public static final Block POWERED_LAMP = registerBlock("powered_lamp",
            PoweredLampBlock::new, AbstractBlock.Settings.create().
                    strength(.3f).sounds(BlockSoundGroup.GLASS).
                    luminance(PoweredLampBlock.LIGHT_EMISSION));
    public static final Item POWERED_LAMP_ITEM = createBlockItem("powered_lamp", POWERED_LAMP);

    public static final Block POWERED_FURNACE = registerBlock("powered_furnace",
            PoweredFurnaceBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(PoweredFurnaceBlock.LIGHT_EMISSION));
    public static final Item POWERED_FURNACE_ITEM = createBlockItem("powered_furnace", POWERED_FURNACE);

    public static final Block ADVANCED_POWERED_FURNACE = registerBlock("advanced_powered_furnace",
            AdvancedPoweredFurnaceBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(AdvancedPoweredFurnaceBlock.LIGHT_EMISSION));
    public static final Item ADVANCED_POWERED_FURNACE_ITEM = createBlockItem("advanced_powered_furnace", ADVANCED_POWERED_FURNACE);

    public static final Block LIGHTNING_GENERATOR = registerBlock("lightning_generator",
            LightningGeneratorBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(LightningGeneratorBlock.LIGHT_EMISSION));
    public static final Item LIGHTNING_GENERATOR_ITEM = createBlockItem("lightning_generator",
            LightningGeneratorBlock.Item::new, LIGHTNING_GENERATOR);

    public static final Block ENERGIZER = registerBlock("energizer",
            EnergizerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(EnergizerBlock.LIGHT_EMISSION));
    public static final Item ENERGIZER_ITEM = createBlockItem("energizer", ENERGIZER);

    public static final Block CHARGING_STATION = registerBlock("charging_station",
            ChargingStationBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(ChargingStationBlock.LIGHT_EMISSION));
    public static final Item CHARGING_STATION_ITEM = createBlockItem("charging_station",
            ChargingStationBlock.Item::new, CHARGING_STATION);

    public static final Block CRYSTAL_GROWTH_CHAMBER = registerBlock("crystal_growth_chamber",
            CrystalGrowthChamberBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item CRYSTAL_GROWTH_CHAMBER_ITEM = createBlockItem("crystal_growth_chamber", CRYSTAL_GROWTH_CHAMBER);

    public static final Block WEATHER_CONTROLLER = registerBlock("weather_controller",
            WeatherControllerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item WEATHER_CONTROLLER_ITEM = createBlockItem("weather_controller", WEATHER_CONTROLLER);

    public static final Block TIME_CONTROLLER = registerBlock("time_controller",
            TimeControllerBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item TIME_CONTROLLER_ITEM = createBlockItem("time_controller", TIME_CONTROLLER);

    public static final Block TELEPORTER = registerBlock("teleporter",
            TeleporterBlock::new, AbstractBlock.Settings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item TELEPORTER_ITEM = createBlockItem("teleporter",
            TeleporterBlock.Item::new, TELEPORTER);

    public static final Block BASIC_MACHINE_FRAME = registerBlock("basic_machine_frame",
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item BASIC_MACHINE_FRAME_ITEM = createBlockItem("basic_machine_frame", BASIC_MACHINE_FRAME);

    public static final Block HARDENED_MACHINE_FRAME = registerBlock("hardened_machine_frame",
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item HARDENED_MACHINE_FRAME_ITEM = createBlockItem("hardened_machine_frame", HARDENED_MACHINE_FRAME);

    public static final Block ADVANCED_MACHINE_FRAME = registerBlock("advanced_machine_frame",
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("advanced_machine_frame", ADVANCED_MACHINE_FRAME);

    public static final Block REINFORCED_ADVANCED_MACHINE_FRAME = registerBlock("reinforced_advanced_machine_frame",
            AbstractBlock.Settings.create().requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL));
    public static final Item REINFORCED_ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("reinforced_advanced_machine_frame", REINFORCED_ADVANCED_MACHINE_FRAME);

    public static void register() {

    }
}
