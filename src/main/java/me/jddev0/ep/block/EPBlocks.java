package me.jddev0.ep.block;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.tier.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public final class EPBlocks {
    private EPBlocks() {}

    public static final Block SILICON_BLOCK = registerBlock("silicon_block",
            new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item SILICON_BLOCK_ITEM = createBlockItem("silicon_block", SILICON_BLOCK);

    public static final Block TIN_BLOCK = registerBlock("tin_block",
            new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item TIN_BLOCK_ITEM = createBlockItem("tin_block", TIN_BLOCK);

    public static final Block STEEL_BLOCK = registerBlock("steel_block",
            new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item STEEL_BLOCK_ITEM = createBlockItem("steel_block", STEEL_BLOCK);

    public static final Block ADVANCED_ALLOY_BLOCK = registerBlock("advanced_alloy_block",
            new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item ADVANCED_ALLOY_BLOCK_ITEM = createBlockItem("advanced_alloy_block", ADVANCED_ALLOY_BLOCK);

    public static final Block SAWDUST_BLOCK = registerBlock("sawdust_block",
            new Block(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).
                    requiresCorrectToolForDrops().strength(2.0f, 3.0f).sound(SoundType.WOOD)));
    public static final Item SAWDUST_BLOCK_ITEM = createBlockItem("sawdust_block", SAWDUST_BLOCK);

    public static final Block TIN_ORE = registerBlock("tin_ore",
            new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0f, 3.0f)));
    public static final Item TIN_ORE_ITEM = createBlockItem("tin_ore", TIN_ORE);

    public static final Block DEEPSLATE_TIN_ORE = registerBlock("deepslate_tin_ore",
            new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops().strength(4.5f, 3.0f).sound(SoundType.DEEPSLATE)));
    public static final Item DEEPSLATE_TIN_ORE_ITEM = createBlockItem("deepslate_tin_ore", DEEPSLATE_TIN_ORE);

    public static final Block RAW_TIN_BLOCK = registerBlock("raw_tin_block",
            new Block(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0f, 6.0f)));
    public static final Item RAW_TIN_BLOCK_ITEM = createBlockItem("raw_tin_block", RAW_TIN_BLOCK);

    public static final ItemConveyorBeltBlock BASIC_ITEM_CONVEYOR_BELT = registerBlock("item_conveyor_belt",
            new ItemConveyorBeltBlock(ConveyorBeltTier.BASIC, BlockBehaviour.Properties.of().noCollission().
                    strength(2.5f, 3.0f).sound(SoundType.METAL)));
    public static final Item BASIC_ITEM_CONVEYOR_BELT_ITEM = createBlockItem("item_conveyor_belt",
            new ItemConveyorBeltBlock.Item(BASIC_ITEM_CONVEYOR_BELT, new Item.Properties(), ConveyorBeltTier.BASIC));
    public static final ItemConveyorBeltBlock FAST_ITEM_CONVEYOR_BELT = registerBlock("fast_item_conveyor_belt",
            new ItemConveyorBeltBlock(ConveyorBeltTier.FAST, BlockBehaviour.Properties.of().noCollission().
                    strength(2.5f, 3.0f).sound(SoundType.METAL)));
    public static final Item FAST_ITEM_CONVEYOR_BELT_ITEM = createBlockItem("fast_item_conveyor_belt",
            new ItemConveyorBeltBlock.Item(FAST_ITEM_CONVEYOR_BELT, new Item.Properties(), ConveyorBeltTier.FAST));
    public static final ItemConveyorBeltBlock EXPRESS_ITEM_CONVEYOR_BELT = registerBlock("express_item_conveyor_belt",
            new ItemConveyorBeltBlock(ConveyorBeltTier.EXPRESS, BlockBehaviour.Properties.of().noCollission().
                    strength(2.5f, 3.0f).sound(SoundType.METAL)));
    public static final Item EXPRESS_ITEM_CONVEYOR_BELT_ITEM = createBlockItem("express_item_conveyor_belt",
            new ItemConveyorBeltBlock.Item(EXPRESS_ITEM_CONVEYOR_BELT, new Item.Properties(), ConveyorBeltTier.EXPRESS));

    public static final ItemConveyorBeltLoaderBlock BASIC_ITEM_CONVEYOR_BELT_LOADER = registerBlock("item_conveyor_belt_loader",
            new ItemConveyorBeltLoaderBlock(ConveyorBeltTier.BASIC, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM = createBlockItem("item_conveyor_belt_loader", BASIC_ITEM_CONVEYOR_BELT_LOADER);
    public static final ItemConveyorBeltLoaderBlock FAST_ITEM_CONVEYOR_BELT_LOADER = registerBlock("fast_item_conveyor_belt_loader",
            new ItemConveyorBeltLoaderBlock(ConveyorBeltTier.FAST, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM = createBlockItem("fast_item_conveyor_belt_loader", FAST_ITEM_CONVEYOR_BELT_LOADER);
    public static final ItemConveyorBeltLoaderBlock EXPRESS_ITEM_CONVEYOR_BELT_LOADER = registerBlock("express_item_conveyor_belt_loader",
            new ItemConveyorBeltLoaderBlock(ConveyorBeltTier.EXPRESS, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM = createBlockItem("express_item_conveyor_belt_loader", EXPRESS_ITEM_CONVEYOR_BELT_LOADER);

    public static final ItemConveyorBeltSorterBlock BASIC_ITEM_CONVEYOR_BELT_SORTER = registerBlock("item_conveyor_belt_sorter",
            new ItemConveyorBeltSorterBlock(ConveyorBeltTier.BASIC, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM = createBlockItem("item_conveyor_belt_sorter", BASIC_ITEM_CONVEYOR_BELT_SORTER);
    public static final ItemConveyorBeltSorterBlock FAST_ITEM_CONVEYOR_BELT_SORTER = registerBlock("fast_item_conveyor_belt_sorter",
            new ItemConveyorBeltSorterBlock(ConveyorBeltTier.FAST, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM = createBlockItem("fast_item_conveyor_belt_sorter", FAST_ITEM_CONVEYOR_BELT_SORTER);
    public static final ItemConveyorBeltSorterBlock EXPRESS_ITEM_CONVEYOR_BELT_SORTER = registerBlock("express_item_conveyor_belt_sorter",
            new ItemConveyorBeltSorterBlock(ConveyorBeltTier.EXPRESS, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ITEM = createBlockItem("express_item_conveyor_belt_sorter", EXPRESS_ITEM_CONVEYOR_BELT_SORTER);

    public static final ItemConveyorBeltSwitchBlock BASIC_ITEM_CONVEYOR_BELT_SWITCH = registerBlock("item_conveyor_belt_switch",
            new ItemConveyorBeltSwitchBlock(ConveyorBeltTier.BASIC, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM = createBlockItem("item_conveyor_belt_switch", BASIC_ITEM_CONVEYOR_BELT_SWITCH);
    public static final ItemConveyorBeltSwitchBlock FAST_ITEM_CONVEYOR_BELT_SWITCH = registerBlock("fast_item_conveyor_belt_switch",
            new ItemConveyorBeltSwitchBlock(ConveyorBeltTier.FAST, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM = createBlockItem("fast_item_conveyor_belt_switch", FAST_ITEM_CONVEYOR_BELT_SWITCH);
    public static final ItemConveyorBeltSwitchBlock EXPRESS_ITEM_CONVEYOR_BELT_SWITCH = registerBlock("express_item_conveyor_belt_switch",
            new ItemConveyorBeltSwitchBlock(ConveyorBeltTier.EXPRESS, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ITEM = createBlockItem("express_item_conveyor_belt_switch", EXPRESS_ITEM_CONVEYOR_BELT_SWITCH);

    public static final ItemConveyorBeltSplitterBlock BASIC_ITEM_CONVEYOR_BELT_SPLITTER = registerBlock("item_conveyor_belt_splitter",
            new ItemConveyorBeltSplitterBlock(ConveyorBeltTier.BASIC, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM = createBlockItem("item_conveyor_belt_splitter", BASIC_ITEM_CONVEYOR_BELT_SPLITTER);
    public static final ItemConveyorBeltSplitterBlock FAST_ITEM_CONVEYOR_BELT_SPLITTER = registerBlock("fast_item_conveyor_belt_splitter",
            new ItemConveyorBeltSplitterBlock(ConveyorBeltTier.FAST, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM = createBlockItem("fast_item_conveyor_belt_splitter", FAST_ITEM_CONVEYOR_BELT_SPLITTER);
    public static final ItemConveyorBeltSplitterBlock EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER = registerBlock("express_item_conveyor_belt_splitter",
            new ItemConveyorBeltSplitterBlock(ConveyorBeltTier.EXPRESS, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ITEM = createBlockItem("express_item_conveyor_belt_splitter", EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER);

    public static final ItemConveyorBeltMergerBlock BASIC_ITEM_CONVEYOR_BELT_MERGER = registerBlock("item_conveyor_belt_merger",
            new ItemConveyorBeltMergerBlock(ConveyorBeltTier.BASIC, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM = createBlockItem("item_conveyor_belt_merger", BASIC_ITEM_CONVEYOR_BELT_MERGER);
    public static final ItemConveyorBeltMergerBlock FAST_ITEM_CONVEYOR_BELT_MERGER = registerBlock("fast_item_conveyor_belt_merger",
            new ItemConveyorBeltMergerBlock(ConveyorBeltTier.FAST, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM = createBlockItem("fast_item_conveyor_belt_merger", FAST_ITEM_CONVEYOR_BELT_MERGER);
    public static final ItemConveyorBeltMergerBlock EXPRESS_ITEM_CONVEYOR_BELT_MERGER = registerBlock("express_item_conveyor_belt_merger",
            new ItemConveyorBeltMergerBlock(ConveyorBeltTier.EXPRESS, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final Item EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ITEM = createBlockItem("express_item_conveyor_belt_merger", EXPRESS_ITEM_CONVEYOR_BELT_MERGER);

    public static final FluidPipeBlock COPPER_FLUID_PIPE = registerBlock("copper_fluid_pipe",
            new FluidPipeBlock(FluidPipeTier.COPPER));
    public static final Item COPPER_FLUID_PIPE_ITEM = createBlockItem("copper_fluid_pipe",
            new FluidPipeBlock.Item(COPPER_FLUID_PIPE, new Item.Properties(), FluidPipeTier.COPPER));

    public static final FluidPipeBlock IRON_FLUID_PIPE = registerBlock("fluid_pipe",
            new FluidPipeBlock(FluidPipeTier.IRON));
    public static final Item IRON_FLUID_PIPE_ITEM = createBlockItem("fluid_pipe",
            new FluidPipeBlock.Item(IRON_FLUID_PIPE, new Item.Properties(), FluidPipeTier.IRON));

    public static final FluidPipeBlock GOLDEN_FLUID_PIPE = registerBlock("golden_fluid_pipe",
            new FluidPipeBlock(FluidPipeTier.GOLDEN));
    public static final Item GOLDEN_FLUID_PIPE_ITEM = createBlockItem("golden_fluid_pipe",
            new FluidPipeBlock.Item(GOLDEN_FLUID_PIPE, new Item.Properties(), FluidPipeTier.GOLDEN));

    public static final FluidPipeBlock STEEL_FLUID_PIPE = registerBlock("steel_fluid_pipe",
            new FluidPipeBlock(FluidPipeTier.STEEL));
    public static final Item STEEL_FLUID_PIPE_ITEM = createBlockItem("steel_fluid_pipe",
            new FluidPipeBlock.Item(STEEL_FLUID_PIPE, new Item.Properties(), FluidPipeTier.STEEL));

    public static final FluidPipeBlock PRESSURIZED_FLUID_PIPE = registerBlock("pressurized_fluid_pipe",
            new FluidPipeBlock(FluidPipeTier.PRESSURIZED));
    public static final Item PRESSURIZED_FLUID_PIPE_ITEM = createBlockItem("pressurized_fluid_pipe",
            new FluidPipeBlock.Item(PRESSURIZED_FLUID_PIPE, new Item.Properties(), FluidPipeTier.PRESSURIZED));

    public static final FluidTankBlock FLUID_TANK_SMALL = registerBlock("fluid_tank_small",
            new FluidTankBlock(FluidTankTier.SMALL));
    public static final Item FLUID_TANK_SMALL_ITEM = createBlockItem("fluid_tank_small",
            new FluidTankBlock.Item(FLUID_TANK_SMALL, new Item.Properties(), FluidTankTier.SMALL));

    public static final FluidTankBlock FLUID_TANK_MEDIUM = registerBlock("fluid_tank_medium",
            new FluidTankBlock(FluidTankTier.MEDIUM));
    public static final Item FLUID_TANK_MEDIUM_ITEM = createBlockItem("fluid_tank_medium",
            new FluidTankBlock.Item(FLUID_TANK_MEDIUM, new Item.Properties(), FluidTankTier.MEDIUM));

    public static final FluidTankBlock FLUID_TANK_LARGE = registerBlock("fluid_tank_large",
            new FluidTankBlock(FluidTankTier.LARGE));
    public static final Item FLUID_TANK_LARGE_ITEM = createBlockItem("fluid_tank_large",
            new FluidTankBlock.Item(FLUID_TANK_LARGE, new Item.Properties(), FluidTankTier.LARGE));

    public static final CreativeFluidTankBlock CREATIVE_FLUID_TANK = registerBlock("creative_fluid_tank",
            new CreativeFluidTankBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).
                    requiresCorrectToolForDrops().strength(-1.f, 3600000.f).noLootTable()));
    public static final Item CREATIVE_FLUID_TANK_ITEM = createBlockItem("creative_fluid_tank",
            new CreativeFluidTankBlock.Item(CREATIVE_FLUID_TANK, new Item.Properties()));

    public static final ItemSiloBlock ITEM_SILO_TINY = registerBlock("item_silo_tiny",
            new ItemSiloBlock(ItemSiloTier.TINY, ItemSiloTier.TINY.getProperties()));
    public static final Item ITEM_SILO_TINY_ITEM = createBlockItem("item_silo_tiny",
            new ItemSiloBlock.Item(ITEM_SILO_TINY, new Item.Properties(), ItemSiloTier.TINY));

    public static final ItemSiloBlock ITEM_SILO_SMALL = registerBlock("item_silo_small",
            new ItemSiloBlock(ItemSiloTier.SMALL, ItemSiloTier.SMALL.getProperties()));
    public static final Item ITEM_SILO_SMALL_ITEM = createBlockItem("item_silo_small",
            new ItemSiloBlock.Item(ITEM_SILO_SMALL, new Item.Properties(), ItemSiloTier.SMALL));

    public static final ItemSiloBlock ITEM_SILO_MEDIUM = registerBlock("item_silo_medium",
            new ItemSiloBlock(ItemSiloTier.MEDIUM, ItemSiloTier.MEDIUM.getProperties()));
    public static final Item ITEM_SILO_MEDIUM_ITEM = createBlockItem("item_silo_medium",
            new ItemSiloBlock.Item(ITEM_SILO_MEDIUM, new Item.Properties(), ItemSiloTier.MEDIUM));

    public static final ItemSiloBlock ITEM_SILO_LARGE = registerBlock("item_silo_large",
            new ItemSiloBlock(ItemSiloTier.LARGE, ItemSiloTier.LARGE.getProperties()));
    public static final Item ITEM_SILO_LARGE_ITEM = createBlockItem("item_silo_large",
            new ItemSiloBlock.Item(ITEM_SILO_LARGE, new Item.Properties(), ItemSiloTier.LARGE));

    public static final ItemSiloBlock ITEM_SILO_GIANT = registerBlock("item_silo_giant",
            new ItemSiloBlock(ItemSiloTier.GIANT, ItemSiloTier.GIANT.getProperties()));
    public static final Item ITEM_SILO_GIANT_ITEM = createBlockItem("item_silo_giant",
            new ItemSiloBlock.Item(ITEM_SILO_GIANT, new Item.Properties(), ItemSiloTier.GIANT));

    public static final CreativeItemSiloBlock CREATIVE_ITEM_SILO = registerBlock("creative_item_silo",
            new CreativeItemSiloBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).
                    requiresCorrectToolForDrops().strength(-1.f, 3600000.f).noLootTable()));
    public static final Item CREATIVE_ITEM_SILO_ITEM = createBlockItem("creative_item_silo",
            new CreativeItemSiloBlock.Item(CREATIVE_ITEM_SILO, new Item.Properties()));

    private static Item createCableBlockItem(String name, CableBlock block) {
        return Registry.register(BuiltInRegistries.ITEM, EPAPI.id(name),
                new CableBlock.Item(block, new Item.Properties(), block.getTier()));
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
        return Registry.register(BuiltInRegistries.ITEM, EPAPI.id(name),
                new TransformerBlock.Item(block, new Item.Properties(), block.getTier(), block.getTransformerType()));
    }
    public static final TransformerBlock LV_TRANSFORMER_1_TO_N = registerBlock("lv_transformer_1_to_n",
            new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.LV, TransformerType.TYPE_1_TO_N));
    public static final Item LV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("lv_transformer_1_to_n",
            LV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock LV_TRANSFORMER_3_TO_3 = registerBlock("lv_transformer_3_to_3",
            new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.LV, TransformerType.TYPE_3_TO_3));
    public static final Item LV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("lv_transformer_3_to_3",
            LV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock LV_TRANSFORMER_N_TO_1 = registerBlock("lv_transformer_n_to_1",
            new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.LV, TransformerType.TYPE_N_TO_1));
    public static final Item LV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("lv_transformer_n_to_1",
            LV_TRANSFORMER_N_TO_1);

    public static final TransformerBlock MV_TRANSFORMER_1_TO_N = registerBlock("transformer_1_to_n",
            new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.MV, TransformerType.TYPE_1_TO_N));
    public static final Item MV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("transformer_1_to_n",
            MV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock MV_TRANSFORMER_3_TO_3 = registerBlock("transformer_3_to_3",
            new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.MV, TransformerType.TYPE_3_TO_3));
    public static final Item MV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("transformer_3_to_3",
            MV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock MV_TRANSFORMER_N_TO_1 = registerBlock("transformer_n_to_1",
            new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.MV, TransformerType.TYPE_N_TO_1));
    public static final Item MV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("transformer_n_to_1",
            MV_TRANSFORMER_N_TO_1);

    public static final TransformerBlock HV_TRANSFORMER_1_TO_N = registerBlock("hv_transformer_1_to_n",
            new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.HV, TransformerType.TYPE_1_TO_N));
    public static final Item HV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("hv_transformer_1_to_n",
            HV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock HV_TRANSFORMER_3_TO_3 = registerBlock("hv_transformer_3_to_3",
            new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.HV, TransformerType.TYPE_3_TO_3));
    public static final Item HV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("hv_transformer_3_to_3",
            HV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock HV_TRANSFORMER_N_TO_1 = registerBlock("hv_transformer_n_to_1",
            new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.HV, TransformerType.TYPE_N_TO_1));
    public static final Item HV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("hv_transformer_n_to_1",
            HV_TRANSFORMER_N_TO_1);

    public static final TransformerBlock EHV_TRANSFORMER_1_TO_N = registerBlock("ehv_transformer_1_to_n",
            new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.EHV, TransformerType.TYPE_1_TO_N));
    public static final Item EHV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("ehv_transformer_1_to_n",
            EHV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock EHV_TRANSFORMER_3_TO_3 = registerBlock("ehv_transformer_3_to_3",
            new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.EHV, TransformerType.TYPE_3_TO_3));
    public static final Item EHV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("ehv_transformer_3_to_3",
            EHV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock EHV_TRANSFORMER_N_TO_1 = registerBlock("ehv_transformer_n_to_1",
            new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.EHV, TransformerType.TYPE_N_TO_1));
    public static final Item EHV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("ehv_transformer_n_to_1",
            EHV_TRANSFORMER_N_TO_1);

    private static Item createConfigurableTransformerBlockItem(String name, ConfigurableTransformerBlock block) {
        return Registry.register(BuiltInRegistries.ITEM, EPAPI.id(name), new ConfigurableTransformerBlock.Item(block, new Item.Properties(), block.getTier()));
    }
    public static final ConfigurableTransformerBlock CONFIGURABLE_LV_TRANSFORMER = registerBlock("configurable_lv_transformer",
            new ConfigurableTransformerBlock(TransformerTier.LV, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item CONFIGURABLE_LV_TRANSFORMER_ITEM = createConfigurableTransformerBlockItem("configurable_lv_transformer",
            CONFIGURABLE_LV_TRANSFORMER);

    public static final ConfigurableTransformerBlock CONFIGURABLE_MV_TRANSFORMER = registerBlock("configurable_mv_transformer",
            new ConfigurableTransformerBlock(TransformerTier.MV, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item CONFIGURABLE_MV_TRANSFORMER_ITEM = createConfigurableTransformerBlockItem("configurable_mv_transformer",
            CONFIGURABLE_MV_TRANSFORMER);

    public static final ConfigurableTransformerBlock CONFIGURABLE_HV_TRANSFORMER = registerBlock("configurable_hv_transformer",
            new ConfigurableTransformerBlock(TransformerTier.HV, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item CONFIGURABLE_HV_TRANSFORMER_ITEM = createConfigurableTransformerBlockItem("configurable_hv_transformer",
            CONFIGURABLE_HV_TRANSFORMER);

    public static final ConfigurableTransformerBlock CONFIGURABLE_EHV_TRANSFORMER = registerBlock("configurable_ehv_transformer",
            new ConfigurableTransformerBlock(TransformerTier.EHV, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item CONFIGURABLE_EHV_TRANSFORMER_ITEM = createConfigurableTransformerBlockItem("configurable_ehv_transformer",
            CONFIGURABLE_EHV_TRANSFORMER);

    public static final Block PRESS_MOLD_MAKER = registerBlock("press_mold_maker",
            new PressMoldMakerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0f, 6.0f).sound(SoundType.STONE)));
    public static final Item PRESS_MOLD_MAKER_ITEM = createBlockItem("press_mold_maker", PRESS_MOLD_MAKER);

    public static final Block ALLOY_FURNACE = registerBlock("alloy_furnace",
            new AlloyFurnaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0f, 6.0f).sound(SoundType.STONE).
                    lightLevel(AlloyFurnaceBlock.LIGHT_EMISSION)));
    public static final Item ALLOY_FURNACE_ITEM = createBlockItem("alloy_furnace", ALLOY_FURNACE);

    public static final Block AUTO_CRAFTER = registerBlock("auto_crafter",
            new AutoCrafterBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item AUTO_CRAFTER_ITEM = createBlockItem("auto_crafter",
            new AutoCrafterBlock.Item(AUTO_CRAFTER, new Item.Properties()));

    public static final Block ADVANCED_AUTO_CRAFTER = registerBlock("advanced_auto_crafter",
            new AdvancedAutoCrafterBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item ADVANCED_AUTO_CRAFTER_ITEM = createBlockItem("advanced_auto_crafter",
            new AdvancedAutoCrafterBlock.Item(ADVANCED_AUTO_CRAFTER, new Item.Properties()));

    public static final Block BATTERY_BOX = registerBlock("battery_box",
            new BatteryBoxBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item BATTERY_BOX_ITEM = createBlockItem("battery_box",
            new BatteryBoxBlock.Item(BATTERY_BOX, new Item.Properties()));

    public static final Block ADVANCED_BATTERY_BOX = registerBlock("advanced_battery_box",
            new AdvancedBatteryBoxBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item ADVANCED_BATTERY_BOX_ITEM = createBlockItem("advanced_battery_box",
            new AdvancedBatteryBoxBlock.Item(ADVANCED_BATTERY_BOX, new Item.Properties()));

    public static final Block CREATIVE_BATTERY_BOX = registerBlock("creative_battery_box",
            new CreativeBatteryBoxBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).
                    requiresCorrectToolForDrops().strength(-1.f, 3600000.f).noLootTable()));
    public static final Item CREATIVE_BATTERY_BOX_ITEM = createBlockItem("creative_battery_box",
            new CreativeBatteryBoxBlock.Item(CREATIVE_BATTERY_BOX, new Item.Properties()));

    public static final Block CRUSHER = registerBlock("crusher",
            new CrusherBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item CRUSHER_ITEM = createBlockItem("crusher", CRUSHER);

    public static final Block ADVANCED_CRUSHER = registerBlock("advanced_crusher",
            new AdvancedCrusherBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item ADVANCED_CRUSHER_ITEM = createBlockItem("advanced_crusher",
            new AdvancedCrusherBlock.Item(ADVANCED_CRUSHER, new Item.Properties()));

    public static final Block PULVERIZER = registerBlock("pulverizer",
            new PulverizerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item PULVERIZER_ITEM = createBlockItem("pulverizer", PULVERIZER);

    public static final Block ADVANCED_PULVERIZER = registerBlock("advanced_pulverizer",
            new AdvancedPulverizerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item ADVANCED_PULVERIZER_ITEM = createBlockItem("advanced_pulverizer",
            new AdvancedPulverizerBlock.Item(ADVANCED_PULVERIZER, new Item.Properties()));

    public static final Block SAWMILL = registerBlock("sawmill",
            new SawmillBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item SAWMILL_ITEM = createBlockItem("sawmill", SAWMILL);

    public static final Block COMPRESSOR = registerBlock("compressor",
            new CompressorBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item COMPRESSOR_ITEM = createBlockItem("compressor", COMPRESSOR);

    public static final Block METAL_PRESS = registerBlock("metal_press",
            new MetalPressBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item METAL_PRESS_ITEM = createBlockItem("metal_press",
            new MetalPressBlock.Item(METAL_PRESS, new Item.Properties()));

    public static final Block AUTO_PRESS_MOLD_MAKER = registerBlock("auto_press_mold_maker",
            new AutoPressMoldMakerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item AUTO_PRESS_MOLD_MAKER_ITEM = createBlockItem("auto_press_mold_maker", AUTO_PRESS_MOLD_MAKER);

    public static final Block AUTO_STONECUTTER = registerBlock("auto_stonecutter",
            new AutoStonecutterBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item AUTO_STONECUTTER_ITEM = createBlockItem("auto_stonecutter", AUTO_STONECUTTER);

    public static final Block PLANT_GROWTH_CHAMBER = registerBlock("plant_growth_chamber",
            new PlantGrowthChamberBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item PLANT_GROWTH_CHAMBER_ITEM = createBlockItem("plant_growth_chamber", PLANT_GROWTH_CHAMBER);

    public static final Block BLOCK_PLACER = registerBlock("block_placer",
            new BlockPlacerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item BLOCK_PLACER_ITEM = createBlockItem("block_placer", BLOCK_PLACER);

    public static final Block ASSEMBLING_MACHINE = registerBlock("assembling_machine",
            new AssemblingMachineBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item ASSEMBLING_MACHINE_ITEM = createBlockItem("assembling_machine", ASSEMBLING_MACHINE);

    public static final Block INDUCTION_SMELTER = registerBlock("induction_smelter",
            new InductionSmelterBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(InductionSmelterBlock.LIGHT_EMISSION)));
    public static final Item INDUCTION_SMELTER_ITEM = createBlockItem("induction_smelter", INDUCTION_SMELTER);

    public static final Block FLUID_FILLER = registerBlock("fluid_filler",
            new FluidFillerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item FLUID_FILLER_ITEM = createBlockItem("fluid_filler", FLUID_FILLER);

    public static final Block FLUID_FREEZER = registerBlock("fluid_freezer",
            new FluidFreezerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item FLUID_FREEZER_ITEM = createBlockItem("fluid_freezer", FLUID_FREEZER);

    public static final Block STONE_LIQUEFIER = registerBlock("stone_liquefier",
            new StoneLiquefierBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item STONE_LIQUEFIER_ITEM = createBlockItem("stone_liquefier", STONE_LIQUEFIER);

    public static final Block STONE_SOLIDIFIER = registerBlock("stone_solidifier",
            new StoneSolidifierBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item STONE_SOLIDIFIER_ITEM = createBlockItem("stone_solidifier", STONE_SOLIDIFIER);

    public static final Block FILTRATION_PLANT = registerBlock("filtration_plant",
            new FiltrationPlantBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item FILTRATION_PLANT_ITEM = createBlockItem("filtration_plant",
            new FiltrationPlantBlock.Item(FILTRATION_PLANT, new Item.Properties()));

    public static final Block FLUID_TRANSPOSER = registerBlock("fluid_transposer",
            new FluidTransposerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item FLUID_TRANSPOSER_ITEM = createBlockItem("fluid_transposer",
            FLUID_TRANSPOSER);

    public static final Block FLUID_DRAINER = registerBlock("fluid_drainer",
            new FluidDrainerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item FLUID_DRAINER_ITEM = createBlockItem("fluid_drainer", FLUID_DRAINER);

    public static final Block FLUID_PUMP = registerBlock("fluid_pump",
            new FluidPumpBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item FLUID_PUMP_ITEM = createBlockItem("fluid_pump", FLUID_PUMP);

    public static final Block ADVANCED_FLUID_PUMP = registerBlock("advanced_fluid_pump",
            new AdvancedFluidPumpBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item ADVANCED_FLUID_PUMP_ITEM = createBlockItem("advanced_fluid_pump", ADVANCED_FLUID_PUMP);

    public static final Block DRAIN = registerBlock("drain",
            new DrainBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item DRAIN_ITEM = createBlockItem("drain",
            new DrainBlock.Item(DRAIN, new Item.Properties()));

    public static final Block CHARGER = registerBlock("charger",
            new ChargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item CHARGER_ITEM = createBlockItem("charger",
            new ChargerBlock.Item(CHARGER, new Item.Properties()));

    public static final Block ADVANCED_CHARGER = registerBlock("advanced_charger",
            new AdvancedChargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item ADVANCED_CHARGER_ITEM = createBlockItem("advanced_charger",
            new AdvancedChargerBlock.Item(ADVANCED_CHARGER, new Item.Properties()));

    public static final Block UNCHARGER = registerBlock("uncharger",
            new UnchargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item UNCHARGER_ITEM = createBlockItem("uncharger", UNCHARGER);

    public static final Block ADVANCED_UNCHARGER = registerBlock("advanced_uncharger",
            new AdvancedUnchargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item ADVANCED_UNCHARGER_ITEM = createBlockItem("advanced_uncharger", ADVANCED_UNCHARGER);

    public static final Block MINECART_CHARGER = registerBlock("minecart_charger",
            new MinecartChargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item MINECART_CHARGER_ITEM = createBlockItem("minecart_charger",
            new MinecartChargerBlock.Item(MINECART_CHARGER, new Item.Properties()));

    public static final Block ADVANCED_MINECART_CHARGER = registerBlock("advanced_minecart_charger",
            new AdvancedMinecartChargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item ADVANCED_MINECART_CHARGER_ITEM = createBlockItem("advanced_minecart_charger",
            new AdvancedMinecartChargerBlock.Item(ADVANCED_MINECART_CHARGER, new Item.Properties()));

    public static final Block MINECART_UNCHARGER = registerBlock("minecart_uncharger",
            new MinecartUnchargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item MINECART_UNCHARGER_ITEM = createBlockItem("minecart_uncharger",
            new MinecartUnchargerBlock.Item(MINECART_UNCHARGER, new Item.Properties()));

    public static final Block ADVANCED_MINECART_UNCHARGER = registerBlock("advanced_minecart_uncharger",
            new AdvancedMinecartUnchargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item ADVANCED_MINECART_UNCHARGER_ITEM = createBlockItem("advanced_minecart_uncharger",
            new AdvancedMinecartUnchargerBlock.Item(ADVANCED_MINECART_UNCHARGER, new Item.Properties()));


    private static Item createSolarPanelBlockItem(String name, SolarPanelBlock block) {
        return Registry.register(BuiltInRegistries.ITEM, EPAPI.id(name),
                new SolarPanelBlock.Item(block, new Item.Properties(), block.getTier()));
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

    public static final SolarPanelBlock SOLAR_PANEL_7 = registerBlock("solar_panel_7",
            new SolarPanelBlock(SolarPanelTier.TIER_7));
    public static final Item SOLAR_PANEL_ITEM_7 = createSolarPanelBlockItem("solar_panel_7", SOLAR_PANEL_7);

    public static final Block COAL_ENGINE = registerBlock("coal_engine",
            new CoalEngineBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(CoalEngineBlock.LIGHT_EMISSION)));
    public static final Item COAL_ENGINE_ITEM = createBlockItem("coal_engine", COAL_ENGINE);

    public static final Block HEAT_GENERATOR = registerBlock("heat_generator",
            new HeatGeneratorBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item HEAT_GENERATOR_ITEM = createBlockItem("heat_generator", HEAT_GENERATOR);

    public static final Block THERMAL_GENERATOR = registerBlock("thermal_generator",
            new ThermalGeneratorBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item THERMAL_GENERATOR_ITEM = createBlockItem("thermal_generator", THERMAL_GENERATOR);

    public static final Block POWERED_LAMP = registerBlock("powered_lamp",
            new PoweredLampBlock(BlockBehaviour.Properties.of().
                    strength(.3f).sound(SoundType.GLASS).
                    lightLevel(PoweredLampBlock.LIGHT_EMISSION)));
    public static final Item POWERED_LAMP_ITEM = createBlockItem("powered_lamp", POWERED_LAMP);

    public static final Block POWERED_FURNACE = registerBlock("powered_furnace",
            new PoweredFurnaceBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(PoweredFurnaceBlock.LIGHT_EMISSION)));
    public static final Item POWERED_FURNACE_ITEM = createBlockItem("powered_furnace", POWERED_FURNACE);

    public static final Block ADVANCED_POWERED_FURNACE = registerBlock("advanced_powered_furnace",
            new AdvancedPoweredFurnaceBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(AdvancedPoweredFurnaceBlock.LIGHT_EMISSION)));
    public static final Item ADVANCED_POWERED_FURNACE_ITEM = createBlockItem("advanced_powered_furnace", ADVANCED_POWERED_FURNACE);

    public static final Block LIGHTNING_GENERATOR = registerBlock("lightning_generator",
            new LightningGeneratorBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(LightningGeneratorBlock.LIGHT_EMISSION)));
    public static final Item LIGHTNING_GENERATOR_ITEM = createBlockItem("lightning_generator",
            new LightningGeneratorBlock.Item(LIGHTNING_GENERATOR, new Item.Properties()));

    public static final Block ENERGIZER = registerBlock("energizer",
            new EnergizerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(EnergizerBlock.LIGHT_EMISSION)));
    public static final Item ENERGIZER_ITEM = createBlockItem("energizer", ENERGIZER);

    public static final Block CHARGING_STATION = registerBlock("charging_station",
            new ChargingStationBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(ChargingStationBlock.LIGHT_EMISSION)));
    public static final Item CHARGING_STATION_ITEM = createBlockItem("charging_station",
            new ChargingStationBlock.Item(CHARGING_STATION, new Item.Properties()));

    public static final Block CRYSTAL_GROWTH_CHAMBER = registerBlock("crystal_growth_chamber",
            new CrystalGrowthChamberBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item CRYSTAL_GROWTH_CHAMBER_ITEM = createBlockItem("crystal_growth_chamber", CRYSTAL_GROWTH_CHAMBER);

    public static final Block WEATHER_CONTROLLER = registerBlock("weather_controller",
            new WeatherControllerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item WEATHER_CONTROLLER_ITEM = createBlockItem("weather_controller", WEATHER_CONTROLLER);

    public static final Block TIME_CONTROLLER = registerBlock("time_controller",
            new TimeControllerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item TIME_CONTROLLER_ITEM = createBlockItem("time_controller", TIME_CONTROLLER);

    public static final Block TELEPORTER = registerBlock("teleporter",
            new TeleporterBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item TELEPORTER_ITEM = createBlockItem("teleporter",
            new TeleporterBlock.Item(TELEPORTER, new Item.Properties()));

    public static final Block BASIC_MACHINE_FRAME = registerBlock("basic_machine_frame",
            new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item BASIC_MACHINE_FRAME_ITEM = createBlockItem("basic_machine_frame", BASIC_MACHINE_FRAME);

    public static final Block HARDENED_MACHINE_FRAME = registerBlock("hardened_machine_frame",
            new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item HARDENED_MACHINE_FRAME_ITEM = createBlockItem("hardened_machine_frame", HARDENED_MACHINE_FRAME);

    public static final Block ADVANCED_MACHINE_FRAME = registerBlock("advanced_machine_frame",
            new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("advanced_machine_frame", ADVANCED_MACHINE_FRAME);

    public static final Block REINFORCED_ADVANCED_MACHINE_FRAME = registerBlock("reinforced_advanced_machine_frame",
            new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final Item REINFORCED_ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("reinforced_advanced_machine_frame", REINFORCED_ADVANCED_MACHINE_FRAME);

    private static <T extends Block> T registerBlock(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, EPAPI.id(name), block);
    }

    private static Item createBlockItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, EPAPI.id(name), item);
    }

    private static Item createBlockItem(String name, Block block, Item.Properties props) {
        return Registry.register(BuiltInRegistries.ITEM, EPAPI.id(name),
                new BlockItem(block, props));
    }

    private static Item createBlockItem(String name, Block block) {
        return createBlockItem(name, block, new Item.Properties());
    }

    public static void register() {

    }
}
