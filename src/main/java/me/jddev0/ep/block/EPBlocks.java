package me.jddev0.ep.block;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.machine.tier.*;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EPBlocks {
    private EPBlocks() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EPAPI.MOD_ID);

    private static DeferredItem<Item> createBlockItem(String name, DeferredBlock<? extends Block> blockRegistryObject, Item.Properties props) {
        return EPItems.ITEMS.register(name, () -> new BlockItem(blockRegistryObject.get(), props));
    }
    private static DeferredItem<Item> createBlockItem(String name, DeferredBlock<? extends Block> blockRegistryObject) {
        return createBlockItem(name, blockRegistryObject, new Item.Properties());
    }

    public static final DeferredBlock<Block> SILICON_BLOCK = BLOCKS.register("silicon_block",
            () -> new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> SILICON_BLOCK_ITEM = createBlockItem("silicon_block", SILICON_BLOCK);

    public static final DeferredBlock<Block> TIN_BLOCK = BLOCKS.register("tin_block",
            () -> new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> TIN_BLOCK_ITEM = createBlockItem("tin_block", TIN_BLOCK);

    public static final DeferredBlock<Block> SAWDUST_BLOCK = BLOCKS.register("sawdust_block",
            () -> new SimpleFlammableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).
                    strength(2.0f, 3.0f).sound(SoundType.WOOD), 5, 20));
    public static final DeferredItem<Item> SAWDUST_BLOCK_ITEM = createBlockItem("sawdust_block", SAWDUST_BLOCK);

    public static final DeferredBlock<Block> TIN_ORE = BLOCKS.register("tin_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0f, 3.0f)));
    public static final DeferredItem<Item> TIN_ORE_ITEM = createBlockItem("tin_ore", TIN_ORE);

    public static final DeferredBlock<Block> DEEPSLATE_TIN_ORE = BLOCKS.register("deepslate_tin_ore",
            () -> new DropExperienceBlock(ConstantInt.of(0), BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE)
                    .requiresCorrectToolForDrops().strength(4.5f, 3.0f).sound(SoundType.DEEPSLATE)));
    public static final DeferredItem<Item> DEEPSLATE_TIN_ORE_ITEM = createBlockItem("deepslate_tin_ore", DEEPSLATE_TIN_ORE);

    public static final DeferredBlock<Block> RAW_TIN_BLOCK = BLOCKS.register("raw_tin_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0f, 6.0f)));
    public static final DeferredItem<Item> RAW_TIN_BLOCK_ITEM = createBlockItem("raw_tin_block", RAW_TIN_BLOCK);

    public static final DeferredBlock<ItemConveyorBeltBlock> BASIC_ITEM_CONVEYOR_BELT = BLOCKS.register("item_conveyor_belt",
            () -> new ItemConveyorBeltBlock(ConveyorBeltTier.BASIC, BlockBehaviour.Properties.of().noCollission().
                    strength(2.5f, 3.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> BASIC_ITEM_CONVEYOR_BELT_ITEM = EPItems.ITEMS.register("item_conveyor_belt",
            () -> new ItemConveyorBeltBlock.Item(BASIC_ITEM_CONVEYOR_BELT.get(), new Item.Properties(), ConveyorBeltTier.BASIC));
    public static final DeferredBlock<ItemConveyorBeltBlock> FAST_ITEM_CONVEYOR_BELT = BLOCKS.register("fast_item_conveyor_belt",
            () -> new ItemConveyorBeltBlock(ConveyorBeltTier.FAST, BlockBehaviour.Properties.of().noCollission().
                    strength(2.5f, 3.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> FAST_ITEM_CONVEYOR_BELT_ITEM = EPItems.ITEMS.register("fast_item_conveyor_belt",
            () -> new ItemConveyorBeltBlock.Item(FAST_ITEM_CONVEYOR_BELT.get(), new Item.Properties(), ConveyorBeltTier.FAST));
    public static final DeferredBlock<ItemConveyorBeltBlock> EXPRESS_ITEM_CONVEYOR_BELT = BLOCKS.register("express_item_conveyor_belt",
            () -> new ItemConveyorBeltBlock(ConveyorBeltTier.EXPRESS, BlockBehaviour.Properties.of().noCollission().
                    strength(2.5f, 3.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> EXPRESS_ITEM_CONVEYOR_BELT_ITEM = EPItems.ITEMS.register("express_item_conveyor_belt",
            () -> new ItemConveyorBeltBlock.Item(EXPRESS_ITEM_CONVEYOR_BELT.get(), new Item.Properties(), ConveyorBeltTier.EXPRESS));

    public static final DeferredBlock<ItemConveyorBeltLoaderBlock> BASIC_ITEM_CONVEYOR_BELT_LOADER = BLOCKS.register("item_conveyor_belt_loader",
            () -> new ItemConveyorBeltLoaderBlock(ConveyorBeltTier.BASIC, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM = createBlockItem("item_conveyor_belt_loader", BASIC_ITEM_CONVEYOR_BELT_LOADER);
    public static final DeferredBlock<ItemConveyorBeltLoaderBlock> FAST_ITEM_CONVEYOR_BELT_LOADER = BLOCKS.register("fast_item_conveyor_belt_loader",
            () -> new ItemConveyorBeltLoaderBlock(ConveyorBeltTier.FAST, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM = createBlockItem("fast_item_conveyor_belt_loader", FAST_ITEM_CONVEYOR_BELT_LOADER);
    public static final DeferredBlock<ItemConveyorBeltLoaderBlock> EXPRESS_ITEM_CONVEYOR_BELT_LOADER = BLOCKS.register("express_item_conveyor_belt_loader",
            () -> new ItemConveyorBeltLoaderBlock(ConveyorBeltTier.EXPRESS, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM = createBlockItem("express_item_conveyor_belt_loader", EXPRESS_ITEM_CONVEYOR_BELT_LOADER);

    public static final DeferredBlock<ItemConveyorBeltSorterBlock> BASIC_ITEM_CONVEYOR_BELT_SORTER = BLOCKS.register("item_conveyor_belt_sorter",
            () -> new ItemConveyorBeltSorterBlock(ConveyorBeltTier.BASIC, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM = createBlockItem("item_conveyor_belt_sorter", BASIC_ITEM_CONVEYOR_BELT_SORTER);
    public static final DeferredBlock<ItemConveyorBeltSorterBlock> FAST_ITEM_CONVEYOR_BELT_SORTER = BLOCKS.register("fast_item_conveyor_belt_sorter",
            () -> new ItemConveyorBeltSorterBlock(ConveyorBeltTier.FAST, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM = createBlockItem("fast_item_conveyor_belt_sorter", FAST_ITEM_CONVEYOR_BELT_SORTER);
    public static final DeferredBlock<ItemConveyorBeltSorterBlock> EXPRESS_ITEM_CONVEYOR_BELT_SORTER = BLOCKS.register("express_item_conveyor_belt_sorter",
            () -> new ItemConveyorBeltSorterBlock(ConveyorBeltTier.EXPRESS, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ITEM = createBlockItem("express_item_conveyor_belt_sorter", EXPRESS_ITEM_CONVEYOR_BELT_SORTER);

    public static final DeferredBlock<ItemConveyorBeltSwitchBlock> BASIC_ITEM_CONVEYOR_BELT_SWITCH = BLOCKS.register("item_conveyor_belt_switch",
            () -> new ItemConveyorBeltSwitchBlock(ConveyorBeltTier.BASIC, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM = createBlockItem("item_conveyor_belt_switch", BASIC_ITEM_CONVEYOR_BELT_SWITCH);
    public static final DeferredBlock<ItemConveyorBeltSwitchBlock> FAST_ITEM_CONVEYOR_BELT_SWITCH = BLOCKS.register("fast_item_conveyor_belt_switch",
            () -> new ItemConveyorBeltSwitchBlock(ConveyorBeltTier.FAST, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM = createBlockItem("fast_item_conveyor_belt_switch", FAST_ITEM_CONVEYOR_BELT_SWITCH);
    public static final DeferredBlock<ItemConveyorBeltSwitchBlock> EXPRESS_ITEM_CONVEYOR_BELT_SWITCH = BLOCKS.register("express_item_conveyor_belt_switch",
            () -> new ItemConveyorBeltSwitchBlock(ConveyorBeltTier.EXPRESS, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ITEM = createBlockItem("express_item_conveyor_belt_switch", EXPRESS_ITEM_CONVEYOR_BELT_SWITCH);

    public static final DeferredBlock<ItemConveyorBeltSplitterBlock> BASIC_ITEM_CONVEYOR_BELT_SPLITTER = BLOCKS.register("item_conveyor_belt_splitter",
            () -> new ItemConveyorBeltSplitterBlock(ConveyorBeltTier.BASIC, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM = createBlockItem("item_conveyor_belt_splitter", BASIC_ITEM_CONVEYOR_BELT_SPLITTER);
    public static final DeferredBlock<ItemConveyorBeltSplitterBlock> FAST_ITEM_CONVEYOR_BELT_SPLITTER = BLOCKS.register("fast_item_conveyor_belt_splitter",
            () -> new ItemConveyorBeltSplitterBlock(ConveyorBeltTier.FAST, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM = createBlockItem("fast_item_conveyor_belt_splitter", FAST_ITEM_CONVEYOR_BELT_SPLITTER);
    public static final DeferredBlock<ItemConveyorBeltSplitterBlock> EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER = BLOCKS.register("express_item_conveyor_belt_splitter",
            () -> new ItemConveyorBeltSplitterBlock(ConveyorBeltTier.EXPRESS, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ITEM = createBlockItem("express_item_conveyor_belt_splitter", EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER);

    public static final DeferredBlock<ItemConveyorBeltMergerBlock> BASIC_ITEM_CONVEYOR_BELT_MERGER = BLOCKS.register("item_conveyor_belt_merger",
            () -> new ItemConveyorBeltMergerBlock(ConveyorBeltTier.BASIC, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM = createBlockItem("item_conveyor_belt_merger", BASIC_ITEM_CONVEYOR_BELT_MERGER);
    public static final DeferredBlock<ItemConveyorBeltMergerBlock> FAST_ITEM_CONVEYOR_BELT_MERGER = BLOCKS.register("fast_item_conveyor_belt_merger",
            () -> new ItemConveyorBeltMergerBlock(ConveyorBeltTier.FAST, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM = createBlockItem("fast_item_conveyor_belt_merger", FAST_ITEM_CONVEYOR_BELT_MERGER);
    public static final DeferredBlock<ItemConveyorBeltMergerBlock> EXPRESS_ITEM_CONVEYOR_BELT_MERGER = BLOCKS.register("express_item_conveyor_belt_merger",
            () -> new ItemConveyorBeltMergerBlock(ConveyorBeltTier.EXPRESS, BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ITEM = createBlockItem("express_item_conveyor_belt_merger", EXPRESS_ITEM_CONVEYOR_BELT_MERGER);

    public static final DeferredBlock<FluidPipeBlock> IRON_FLUID_PIPE = BLOCKS.register("fluid_pipe",
            () -> new FluidPipeBlock(FluidPipeTier.IRON));
    public static final DeferredItem<Item> IRON_FLUID_PIPE_ITEM = EPItems.ITEMS.register("fluid_pipe",
            () -> new FluidPipeBlock.Item(IRON_FLUID_PIPE.get(), new Item.Properties(), FluidPipeTier.IRON));

    public static final DeferredBlock<FluidPipeBlock> GOLDEN_FLUID_PIPE = BLOCKS.register("golden_fluid_pipe",
            () -> new FluidPipeBlock(FluidPipeTier.GOLDEN));
    public static final DeferredItem<Item> GOLDEN_FLUID_PIPE_ITEM = EPItems.ITEMS.register("golden_fluid_pipe",
            () -> new FluidPipeBlock.Item(GOLDEN_FLUID_PIPE.get(), new Item.Properties(), FluidPipeTier.GOLDEN));

    public static final DeferredBlock<FluidTankBlock> FLUID_TANK_SMALL = BLOCKS.register("fluid_tank_small",
            () -> new FluidTankBlock(FluidTankTier.SMALL));
    public static final DeferredItem<Item> FLUID_TANK_SMALL_ITEM = EPItems.ITEMS.register("fluid_tank_small",
            () -> new FluidTankBlock.Item(FLUID_TANK_SMALL.get(), new Item.Properties(), FluidTankTier.SMALL));

    public static final DeferredBlock<FluidTankBlock> FLUID_TANK_MEDIUM = BLOCKS.register("fluid_tank_medium",
            () -> new FluidTankBlock(FluidTankTier.MEDIUM));
    public static final DeferredItem<Item> FLUID_TANK_MEDIUM_ITEM = EPItems.ITEMS.register("fluid_tank_medium",
            () -> new FluidTankBlock.Item(FLUID_TANK_MEDIUM.get(), new Item.Properties(), FluidTankTier.MEDIUM));

    public static final DeferredBlock<FluidTankBlock> FLUID_TANK_LARGE = BLOCKS.register("fluid_tank_large",
            () -> new FluidTankBlock(FluidTankTier.LARGE));
    public static final DeferredItem<Item> FLUID_TANK_LARGE_ITEM = EPItems.ITEMS.register("fluid_tank_large",
            () -> new FluidTankBlock.Item(FLUID_TANK_LARGE.get(), new Item.Properties(), FluidTankTier.LARGE));

    public static final DeferredBlock<CreativeFluidTankBlock> CREATIVE_FLUID_TANK = BLOCKS.register("creative_fluid_tank",
            () -> new CreativeFluidTankBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).
                    requiresCorrectToolForDrops().strength(-1.f, 3600000.f).noLootTable()));
    public static final DeferredItem<Item> CREATIVE_FLUID_TANK_ITEM = EPItems.ITEMS.register("creative_fluid_tank",
            () -> new CreativeFluidTankBlock.Item(CREATIVE_FLUID_TANK.get(), new Item.Properties()));

    public static final DeferredBlock<ItemSiloBlock> ITEM_SILO_TINY = BLOCKS.register("item_silo_tiny",
            () -> new ItemSiloBlock(ItemSiloTier.TINY, ItemSiloTier.TINY.getProperties()));
    public static final DeferredItem<Item> ITEM_SILO_TINY_ITEM = EPItems.ITEMS.register("item_silo_tiny",
            () -> new ItemSiloBlock.Item(ITEM_SILO_TINY.get(), new Item.Properties(), ItemSiloTier.TINY));

    public static final DeferredBlock<ItemSiloBlock> ITEM_SILO_SMALL = BLOCKS.register("item_silo_small",
            () -> new ItemSiloBlock(ItemSiloTier.SMALL, ItemSiloTier.SMALL.getProperties()));
    public static final DeferredItem<Item> ITEM_SILO_SMALL_ITEM = EPItems.ITEMS.register("item_silo_small",
            () -> new ItemSiloBlock.Item(ITEM_SILO_SMALL.get(), new Item.Properties(), ItemSiloTier.SMALL));

    public static final DeferredBlock<ItemSiloBlock> ITEM_SILO_MEDIUM = BLOCKS.register("item_silo_medium",
            () -> new ItemSiloBlock(ItemSiloTier.MEDIUM, ItemSiloTier.MEDIUM.getProperties()));
    public static final DeferredItem<Item> ITEM_SILO_MEDIUM_ITEM = EPItems.ITEMS.register("item_silo_medium",
            () -> new ItemSiloBlock.Item(ITEM_SILO_MEDIUM.get(), new Item.Properties(), ItemSiloTier.MEDIUM));

    public static final DeferredBlock<ItemSiloBlock> ITEM_SILO_LARGE = BLOCKS.register("item_silo_large",
            () -> new ItemSiloBlock(ItemSiloTier.LARGE, ItemSiloTier.LARGE.getProperties()));
    public static final DeferredItem<Item> ITEM_SILO_LARGE_ITEM = EPItems.ITEMS.register("item_silo_large",
            () -> new ItemSiloBlock.Item(ITEM_SILO_LARGE.get(), new Item.Properties(), ItemSiloTier.LARGE));

    public static final DeferredBlock<ItemSiloBlock> ITEM_SILO_GIANT = BLOCKS.register("item_silo_giant",
            () -> new ItemSiloBlock(ItemSiloTier.GIANT, ItemSiloTier.GIANT.getProperties()));
    public static final DeferredItem<Item> ITEM_SILO_GIANT_ITEM = EPItems.ITEMS.register("item_silo_giant",
            () -> new ItemSiloBlock.Item(ITEM_SILO_GIANT.get(), new Item.Properties(), ItemSiloTier.GIANT));

    public static final DeferredBlock<CableBlock> TIN_CABLE = BLOCKS.register("tin_cable",
            () -> new CableBlock(CableTier.TIN));
    public static final DeferredItem<Item> TIN_CABLE_ITEM = EPItems.ITEMS.register("tin_cable",
            () -> new CableBlock.Item(TIN_CABLE.get(), new Item.Properties(), CableTier.TIN));

    public static final DeferredBlock<CableBlock> COPPER_CABLE = BLOCKS.register("copper_cable",
            () -> new CableBlock(CableTier.COPPER));
    public static final DeferredItem<Item> COPPER_CABLE_ITEM = EPItems.ITEMS.register("copper_cable",
            () -> new CableBlock.Item(COPPER_CABLE.get(), new Item.Properties(), CableTier.COPPER));

    public static final DeferredBlock<CableBlock> GOLD_CABLE = BLOCKS.register("gold_cable",
            () -> new CableBlock(CableTier.GOLD));
    public static final DeferredItem<Item> GOLD_CABLE_ITEM = EPItems.ITEMS.register("gold_cable",
            () -> new CableBlock.Item(GOLD_CABLE.get(), new Item.Properties(), CableTier.GOLD));

    public static final DeferredBlock<CableBlock> ENERGIZED_COPPER_CABLE = BLOCKS.register("energized_copper_cable",
            () -> new CableBlock(CableTier.ENERGIZED_COPPER));
    public static final DeferredItem<Item> ENERGIZED_COPPER_CABLE_ITEM = EPItems.ITEMS.register("energized_copper_cable",
            () -> new CableBlock.Item(ENERGIZED_COPPER_CABLE.get(), new Item.Properties(), CableTier.ENERGIZED_COPPER));

    public static final DeferredBlock<CableBlock> ENERGIZED_GOLD_CABLE = BLOCKS.register("energized_gold_cable",
            () -> new CableBlock(CableTier.ENERGIZED_GOLD));
    public static final DeferredItem<Item> ENERGIZED_GOLD_CABLE_ITEM = EPItems.ITEMS.register("energized_gold_cable",
            () -> new CableBlock.Item(ENERGIZED_GOLD_CABLE.get(), new Item.Properties(), CableTier.ENERGIZED_GOLD));

    public static final DeferredBlock<CableBlock> ENERGIZED_CRYSTAL_MATRIX_CABLE = BLOCKS.register("energized_crystal_matrix_cable",
            () -> new CableBlock(CableTier.ENERGIZED_CRYSTAL_MATRIX));
    public static final DeferredItem<Item> ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM = EPItems.ITEMS.register("energized_crystal_matrix_cable",
            () -> new CableBlock.Item(ENERGIZED_CRYSTAL_MATRIX_CABLE.get(), new Item.Properties(), CableTier.ENERGIZED_CRYSTAL_MATRIX));

    public static final DeferredBlock<TransformerBlock> LV_TRANSFORMER_1_TO_N = BLOCKS.register("lv_transformer_1_to_n",
            () -> new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.LV, TransformerType.TYPE_1_TO_N));
    public static final DeferredItem<Item> LV_TRANSFORMER_1_TO_N_ITEM = EPItems.ITEMS.register("lv_transformer_1_to_n",
            () -> new TransformerBlock.Item(LV_TRANSFORMER_1_TO_N.get(), new Item.Properties(),
                    TransformerTier.LV, TransformerType.TYPE_1_TO_N));

    public static final DeferredBlock<TransformerBlock> LV_TRANSFORMER_3_TO_3 = BLOCKS.register("lv_transformer_3_to_3",
            () -> new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.LV, TransformerType.TYPE_3_TO_3));
    public static final DeferredItem<Item> LV_TRANSFORMER_3_TO_3_ITEM = EPItems.ITEMS.register("lv_transformer_3_to_3",
            () -> new TransformerBlock.Item(LV_TRANSFORMER_3_TO_3.get(), new Item.Properties(),
                    TransformerTier.LV, TransformerType.TYPE_3_TO_3));

    public static final DeferredBlock<TransformerBlock> LV_TRANSFORMER_N_TO_1 = BLOCKS.register("lv_transformer_n_to_1",
            () -> new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.LV, TransformerType.TYPE_N_TO_1));
    public static final DeferredItem<Item> LV_TRANSFORMER_N_TO_1_ITEM = EPItems.ITEMS.register("lv_transformer_n_to_1",
            () -> new TransformerBlock.Item(LV_TRANSFORMER_N_TO_1.get(), new Item.Properties(),
                    TransformerTier.LV, TransformerType.TYPE_N_TO_1));

    public static final DeferredBlock<TransformerBlock> MV_TRANSFORMER_1_TO_N = BLOCKS.register("transformer_1_to_n",
            () -> new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.MV, TransformerType.TYPE_1_TO_N));
    public static final DeferredItem<Item> MV_TRANSFORMER_1_TO_N_ITEM = EPItems.ITEMS.register("transformer_1_to_n",
            () -> new TransformerBlock.Item(MV_TRANSFORMER_1_TO_N.get(), new Item.Properties(),
                    TransformerTier.MV, TransformerType.TYPE_1_TO_N));

    public static final DeferredBlock<TransformerBlock> MV_TRANSFORMER_3_TO_3 = BLOCKS.register("transformer_3_to_3",
            () -> new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.MV, TransformerType.TYPE_3_TO_3));
    public static final DeferredItem<Item> MV_TRANSFORMER_3_TO_3_ITEM = EPItems.ITEMS.register("transformer_3_to_3",
            () -> new TransformerBlock.Item(MV_TRANSFORMER_3_TO_3.get(), new Item.Properties(),
                    TransformerTier.MV, TransformerType.TYPE_3_TO_3));

    public static final DeferredBlock<TransformerBlock> MV_TRANSFORMER_N_TO_1 = BLOCKS.register("transformer_n_to_1",
            () -> new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.MV, TransformerType.TYPE_N_TO_1));
    public static final DeferredItem<Item> MV_TRANSFORMER_N_TO_1_ITEM = EPItems.ITEMS.register("transformer_n_to_1",
            () -> new TransformerBlock.Item(MV_TRANSFORMER_N_TO_1.get(), new Item.Properties(),
                    TransformerTier.MV, TransformerType.TYPE_N_TO_1));

    public static final DeferredBlock<TransformerBlock> HV_TRANSFORMER_1_TO_N = BLOCKS.register("hv_transformer_1_to_n",
            () -> new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.HV, TransformerType.TYPE_1_TO_N));
    public static final DeferredItem<Item> HV_TRANSFORMER_1_TO_N_ITEM = EPItems.ITEMS.register("hv_transformer_1_to_n",
            () -> new TransformerBlock.Item(HV_TRANSFORMER_1_TO_N.get(), new Item.Properties(),
                    TransformerTier.HV, TransformerType.TYPE_1_TO_N));

    public static final DeferredBlock<TransformerBlock> HV_TRANSFORMER_3_TO_3 = BLOCKS.register("hv_transformer_3_to_3",
            () -> new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.HV, TransformerType.TYPE_3_TO_3));
    public static final DeferredItem<Item> HV_TRANSFORMER_3_TO_3_ITEM = EPItems.ITEMS.register("hv_transformer_3_to_3",
            () -> new TransformerBlock.Item(HV_TRANSFORMER_3_TO_3.get(), new Item.Properties(),
                    TransformerTier.HV, TransformerType.TYPE_3_TO_3));

    public static final DeferredBlock<TransformerBlock> HV_TRANSFORMER_N_TO_1 = BLOCKS.register("hv_transformer_n_to_1",
            () -> new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.HV, TransformerType.TYPE_N_TO_1));
    public static final DeferredItem<Item> HV_TRANSFORMER_N_TO_1_ITEM = EPItems.ITEMS.register("hv_transformer_n_to_1",
            () -> new TransformerBlock.Item(HV_TRANSFORMER_N_TO_1.get(), new Item.Properties(),
                    TransformerTier.HV, TransformerType.TYPE_N_TO_1));

    public static final DeferredBlock<TransformerBlock> EHV_TRANSFORMER_1_TO_N = BLOCKS.register("ehv_transformer_1_to_n",
            () -> new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.EHV, TransformerType.TYPE_1_TO_N));
    public static final DeferredItem<Item> EHV_TRANSFORMER_1_TO_N_ITEM = EPItems.ITEMS.register("ehv_transformer_1_to_n",
            () -> new TransformerBlock.Item(EHV_TRANSFORMER_1_TO_N.get(), new Item.Properties(),
                    TransformerTier.EHV, TransformerType.TYPE_1_TO_N));

    public static final DeferredBlock<TransformerBlock> EHV_TRANSFORMER_3_TO_3 = BLOCKS.register("ehv_transformer_3_to_3",
            () -> new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.EHV, TransformerType.TYPE_3_TO_3));
    public static final DeferredItem<Item> EHV_TRANSFORMER_3_TO_3_ITEM = EPItems.ITEMS.register("ehv_transformer_3_to_3",
            () -> new TransformerBlock.Item(EHV_TRANSFORMER_3_TO_3.get(), new Item.Properties(),
                    TransformerTier.EHV, TransformerType.TYPE_3_TO_3));

    public static final DeferredBlock<TransformerBlock> EHV_TRANSFORMER_N_TO_1 = BLOCKS.register("ehv_transformer_n_to_1",
            () -> new TransformerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL),
                    TransformerTier.EHV, TransformerType.TYPE_N_TO_1));
    public static final DeferredItem<Item> EHV_TRANSFORMER_N_TO_1_ITEM = EPItems.ITEMS.register("ehv_transformer_n_to_1",
            () -> new TransformerBlock.Item(EHV_TRANSFORMER_N_TO_1.get(), new Item.Properties(),
                    TransformerTier.EHV, TransformerType.TYPE_N_TO_1));

    private static DeferredItem<Item> createConfigurableTransformerBlockItem(String name, DeferredBlock<ConfigurableTransformerBlock> block) {
        return EPItems.ITEMS.register(name, () -> new ConfigurableTransformerBlock.Item(block.get(), new Item.Properties(), block.get().getTier()));
    }
    public static final DeferredBlock<ConfigurableTransformerBlock> CONFIGURABLE_LV_TRANSFORMER = BLOCKS.register("configurable_lv_transformer",
            () -> new ConfigurableTransformerBlock(TransformerTier.LV, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> CONFIGURABLE_LV_TRANSFORMER_ITEM = createConfigurableTransformerBlockItem("configurable_lv_transformer",
            CONFIGURABLE_LV_TRANSFORMER);

    public static final DeferredBlock<ConfigurableTransformerBlock> CONFIGURABLE_MV_TRANSFORMER = BLOCKS.register("configurable_mv_transformer",
            () -> new ConfigurableTransformerBlock(TransformerTier.MV, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> CONFIGURABLE_MV_TRANSFORMER_ITEM = createConfigurableTransformerBlockItem("configurable_mv_transformer",
            CONFIGURABLE_MV_TRANSFORMER);

    public static final DeferredBlock<ConfigurableTransformerBlock> CONFIGURABLE_HV_TRANSFORMER = BLOCKS.register("configurable_hv_transformer",
            () -> new ConfigurableTransformerBlock(TransformerTier.HV, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> CONFIGURABLE_HV_TRANSFORMER_ITEM = createConfigurableTransformerBlockItem("configurable_hv_transformer",
            CONFIGURABLE_HV_TRANSFORMER);

    public static final DeferredBlock<ConfigurableTransformerBlock> CONFIGURABLE_EHV_TRANSFORMER = BLOCKS.register("configurable_ehv_transformer",
            () -> new ConfigurableTransformerBlock(TransformerTier.EHV, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> CONFIGURABLE_EHV_TRANSFORMER_ITEM = createConfigurableTransformerBlockItem("configurable_ehv_transformer",
            CONFIGURABLE_EHV_TRANSFORMER);

    public static final DeferredBlock<Block> BATTERY_BOX = BLOCKS.register("battery_box",
            () -> new BatteryBoxBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> BATTERY_BOX_ITEM = EPItems.ITEMS.register("battery_box",
            () -> new BatteryBoxBlock.Item(BATTERY_BOX.get(), new Item.Properties()));

    public static final DeferredBlock<Block> ADVANCED_BATTERY_BOX = BLOCKS.register("advanced_battery_box",
            () -> new AdvancedBatteryBoxBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> ADVANCED_BATTERY_BOX_ITEM = EPItems.ITEMS.register("advanced_battery_box",
            () -> new AdvancedBatteryBoxBlock.Item(ADVANCED_BATTERY_BOX.get(), new Item.Properties()));

    public static final DeferredBlock<Block> CREATIVE_BATTERY_BOX = BLOCKS.register("creative_battery_box",
            () -> new CreativeBatteryBoxBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).
                    requiresCorrectToolForDrops().strength(-1.f, 3600000.f).noLootTable()));
    public static final DeferredItem<Item> CREATIVE_BATTERY_BOX_ITEM = EPItems.ITEMS.register("creative_battery_box",
            () -> new CreativeBatteryBoxBlock.Item(CREATIVE_BATTERY_BOX.get(), new Item.Properties()));

    public static final DeferredBlock<Block> PRESS_MOLD_MAKER = BLOCKS.register("press_mold_maker",
            () -> new PressMoldMakerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0f, 6.0f).sound(SoundType.STONE)));
    public static final DeferredItem<Item> PRESS_MOLD_MAKER_ITEM = createBlockItem("press_mold_maker", PRESS_MOLD_MAKER);

    public static final DeferredBlock<Block> ALLOY_FURNACE = BLOCKS.register("alloy_furnace",
            () -> new AlloyFurnaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0f, 6.0f).sound(SoundType.STONE).
                    lightLevel(AlloyFurnaceBlock.LIGHT_EMISSION)));
    public static final DeferredItem<Item> ALLOY_FURNACE_ITEM = createBlockItem("alloy_furnace", ALLOY_FURNACE);

    public static final DeferredBlock<Block> AUTO_CRAFTER = BLOCKS.register("auto_crafter",
            () -> new AutoCrafterBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> AUTO_CRAFTER_ITEM = EPItems.ITEMS.register("auto_crafter",
            () -> new AutoCrafterBlock.Item(AUTO_CRAFTER.get(), new Item.Properties()));

    public static final DeferredBlock<Block> ADVANCED_AUTO_CRAFTER = BLOCKS.register("advanced_auto_crafter",
            () -> new AdvancedAutoCrafterBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> ADVANCED_AUTO_CRAFTER_ITEM = EPItems.ITEMS.register("advanced_auto_crafter",
            () -> new AdvancedAutoCrafterBlock.Item(ADVANCED_AUTO_CRAFTER.get(), new Item.Properties()));

    public static final DeferredBlock<Block> CRUSHER = BLOCKS.register("crusher",
            () -> new CrusherBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> CRUSHER_ITEM = createBlockItem("crusher", CRUSHER);

    public static final DeferredBlock<Block> ADVANCED_CRUSHER = BLOCKS.register("advanced_crusher",
            () -> new AdvancedCrusherBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> ADVANCED_CRUSHER_ITEM = EPItems.ITEMS.register("advanced_crusher",
            () -> new AdvancedCrusherBlock.Item(ADVANCED_CRUSHER.get(), new Item.Properties()));

    public static final DeferredBlock<Block> PULVERIZER = BLOCKS.register("pulverizer",
            () -> new PulverizerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> PULVERIZER_ITEM = createBlockItem("pulverizer", PULVERIZER);

    public static final DeferredBlock<Block> ADVANCED_PULVERIZER = BLOCKS.register("advanced_pulverizer",
            () -> new AdvancedPulverizerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> ADVANCED_PULVERIZER_ITEM = EPItems.ITEMS.register("advanced_pulverizer",
            () -> new AdvancedPulverizerBlock.Item(ADVANCED_PULVERIZER.get(), new Item.Properties()));

    public static final DeferredBlock<Block> SAWMILL = BLOCKS.register("sawmill",
            () -> new SawmillBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> SAWMILL_ITEM = createBlockItem("sawmill", SAWMILL);

    public static final DeferredBlock<Block> COMPRESSOR = BLOCKS.register("compressor",
            () -> new CompressorBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> COMPRESSOR_ITEM = createBlockItem("compressor", COMPRESSOR);

    public static final DeferredBlock<Block> METAL_PRESS = BLOCKS.register("metal_press",
            () -> new MetalPressBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> METAL_PRESS_ITEM = EPItems.ITEMS.register("metal_press",
            () -> new MetalPressBlock.Item(METAL_PRESS.get(), new Item.Properties()));

    public static final DeferredBlock<Block> AUTO_PRESS_MOLD_MAKER = BLOCKS.register("auto_press_mold_maker",
            () -> new AutoPressMoldMakerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> AUTO_PRESS_MOLD_MAKER_ITEM = createBlockItem("auto_press_mold_maker", AUTO_PRESS_MOLD_MAKER);

    public static final DeferredBlock<Block> AUTO_STONECUTTER = BLOCKS.register("auto_stonecutter",
            () -> new AutoStonecutterBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> AUTO_STONECUTTER_ITEM = createBlockItem("auto_stonecutter", AUTO_STONECUTTER);

    public static final DeferredBlock<Block> PLANT_GROWTH_CHAMBER = BLOCKS.register("plant_growth_chamber",
            () -> new PlantGrowthChamberBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> PLANT_GROWTH_CHAMBER_ITEM = createBlockItem("plant_growth_chamber", PLANT_GROWTH_CHAMBER);

    public static final DeferredBlock<Block> BLOCK_PLACER = BLOCKS.register("block_placer",
            () -> new BlockPlacerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> BLOCK_PLACER_ITEM = createBlockItem("block_placer", BLOCK_PLACER);

    public static final DeferredBlock<Block> ASSEMBLING_MACHINE = BLOCKS.register("assembling_machine",
            () -> new AssemblingMachineBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> ASSEMBLING_MACHINE_ITEM = createBlockItem("assembling_machine", ASSEMBLING_MACHINE);

    public static final DeferredBlock<Block> INDUCTION_SMELTER = BLOCKS.register("induction_smelter",
            () -> new InductionSmelterBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(InductionSmelterBlock.LIGHT_EMISSION)));
    public static final DeferredItem<Item> INDUCTION_SMELTER_ITEM = createBlockItem("induction_smelter", INDUCTION_SMELTER);

    public static final DeferredBlock<Block> FLUID_FILLER = BLOCKS.register("fluid_filler",
            () -> new FluidFillerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> FLUID_FILLER_ITEM = createBlockItem("fluid_filler", FLUID_FILLER);

    public static final DeferredBlock<Block> STONE_LIQUEFIER = BLOCKS.register("stone_liquefier",
            () -> new StoneLiquefierBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> STONE_LIQUEFIER_ITEM = createBlockItem("stone_liquefier", STONE_LIQUEFIER);

    public static final DeferredBlock<Block> STONE_SOLIDIFIER = BLOCKS.register("stone_solidifier",
            () -> new StoneSolidifierBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> STONE_SOLIDIFIER_ITEM = createBlockItem("stone_solidifier", STONE_SOLIDIFIER);

    public static final DeferredBlock<Block> FILTRATION_PLANT = BLOCKS.register("filtration_plant",
            () -> new FiltrationPlantBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> FILTRATION_PLANT_ITEM = EPItems.ITEMS.register("filtration_plant",
            () -> new FiltrationPlantBlock.Item(FILTRATION_PLANT.get(), new Item.Properties()));

    public static final DeferredBlock<Block> FLUID_TRANSPOSER = BLOCKS.register("fluid_transposer",
            () -> new FluidTransposerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> FLUID_TRANSPOSER_ITEM = createBlockItem("fluid_transposer",
            FLUID_TRANSPOSER);

    public static final DeferredBlock<Block> FLUID_DRAINER = BLOCKS.register("fluid_drainer",
            () -> new FluidDrainerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> FLUID_DRAINER_ITEM = createBlockItem("fluid_drainer", FLUID_DRAINER);

    public static final DeferredBlock<Block> FLUID_PUMP = BLOCKS.register("fluid_pump",
            () -> new FluidPumpBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> FLUID_PUMP_ITEM = createBlockItem("fluid_pump", FLUID_PUMP);

    public static final DeferredBlock<Block> ADVANCED_FLUID_PUMP = BLOCKS.register("advanced_fluid_pump",
            () -> new AdvancedFluidPumpBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> ADVANCED_FLUID_PUMP_ITEM = createBlockItem("advanced_fluid_pump", ADVANCED_FLUID_PUMP);

    public static final DeferredBlock<Block> DRAIN = BLOCKS.register("drain",
            () -> new DrainBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> DRAIN_ITEM = EPItems.ITEMS.register("drain",
            () -> new DrainBlock.Item(DRAIN.get(), new Item.Properties()));

    public static final DeferredBlock<Block> CHARGER = BLOCKS.register("charger",
            () -> new ChargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> CHARGER_ITEM = EPItems.ITEMS.register("charger",
            () -> new ChargerBlock.Item(CHARGER.get(), new Item.Properties()));

    public static final DeferredBlock<Block> ADVANCED_CHARGER = BLOCKS.register("advanced_charger",
            () -> new AdvancedChargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> ADVANCED_CHARGER_ITEM = EPItems.ITEMS.register("advanced_charger",
            () -> new AdvancedChargerBlock.Item(ADVANCED_CHARGER.get(), new Item.Properties()));

    public static final DeferredBlock<Block> UNCHARGER = BLOCKS.register("uncharger",
            () -> new UnchargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> UNCHARGER_ITEM = createBlockItem("uncharger", UNCHARGER);

    public static final DeferredBlock<Block> ADVANCED_UNCHARGER = BLOCKS.register("advanced_uncharger",
            () -> new AdvancedUnchargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> ADVANCED_UNCHARGER_ITEM = createBlockItem("advanced_uncharger", ADVANCED_UNCHARGER);

    public static final DeferredBlock<Block> MINECART_CHARGER = BLOCKS.register("minecart_charger",
            () -> new MinecartChargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> MINECART_CHARGER_ITEM = EPItems.ITEMS.register("minecart_charger",
            () -> new MinecartChargerBlock.Item(MINECART_CHARGER.get(), new Item.Properties()));

    public static final DeferredBlock<Block> ADVANCED_MINECART_CHARGER = BLOCKS.register("advanced_minecart_charger",
            () -> new AdvancedMinecartChargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> ADVANCED_MINECART_CHARGER_ITEM = EPItems.ITEMS.register("advanced_minecart_charger",
            () -> new AdvancedMinecartChargerBlock.Item(ADVANCED_MINECART_CHARGER.get(), new Item.Properties()));

    public static final DeferredBlock<Block> MINECART_UNCHARGER = BLOCKS.register("minecart_uncharger",
            () -> new MinecartUnchargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> MINECART_UNCHARGER_ITEM = EPItems.ITEMS.register("minecart_uncharger",
            () -> new MinecartUnchargerBlock.Item(MINECART_UNCHARGER.get(), new Item.Properties()));

    public static final DeferredBlock<Block> ADVANCED_MINECART_UNCHARGER = BLOCKS.register("advanced_minecart_uncharger",
            () -> new AdvancedMinecartUnchargerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> ADVANCED_MINECART_UNCHARGER_ITEM = EPItems.ITEMS.register("advanced_minecart_uncharger",
            () -> new AdvancedMinecartUnchargerBlock.Item(ADVANCED_MINECART_UNCHARGER.get(), new Item.Properties()));


    private static DeferredItem<Item> createSolarPanelBlockItem(String name, DeferredBlock<SolarPanelBlock> blockRegistryObject) {
        return EPItems.ITEMS.register(name, () -> new SolarPanelBlock.Item(blockRegistryObject.get(), new Item.Properties(),
                blockRegistryObject.get().getTier()));
    }
    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL_1 = BLOCKS.register("solar_panel_1",
            () -> new SolarPanelBlock(SolarPanelTier.TIER_1));
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM_1 = createSolarPanelBlockItem("solar_panel_1", SOLAR_PANEL_1);

    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL_2 = BLOCKS.register("solar_panel_2",
            () -> new SolarPanelBlock(SolarPanelTier.TIER_2));
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM_2 = createSolarPanelBlockItem("solar_panel_2", SOLAR_PANEL_2);

    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL_3 = BLOCKS.register("solar_panel_3",
            () -> new SolarPanelBlock(SolarPanelTier.TIER_3));
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM_3 = createSolarPanelBlockItem("solar_panel_3", SOLAR_PANEL_3);

    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL_4 = BLOCKS.register("solar_panel_4",
            () -> new SolarPanelBlock(SolarPanelTier.TIER_4));
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM_4 = createSolarPanelBlockItem("solar_panel_4", SOLAR_PANEL_4);

    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL_5 = BLOCKS.register("solar_panel_5",
            () -> new SolarPanelBlock(SolarPanelTier.TIER_5));
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM_5 = createSolarPanelBlockItem("solar_panel_5", SOLAR_PANEL_5);

    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL_6 = BLOCKS.register("solar_panel_6",
            () -> new SolarPanelBlock(SolarPanelTier.TIER_6));
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM_6 = createSolarPanelBlockItem("solar_panel_6", SOLAR_PANEL_6);

    public static final DeferredBlock<Block> COAL_ENGINE = BLOCKS.register("coal_engine",
            () -> new CoalEngineBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(CoalEngineBlock.LIGHT_EMISSION)));
    public static final DeferredItem<Item> COAL_ENGINE_ITEM = createBlockItem("coal_engine", COAL_ENGINE);

    public static final DeferredBlock<Block> HEAT_GENERATOR = BLOCKS.register("heat_generator",
            () -> new HeatGeneratorBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> HEAT_GENERATOR_ITEM = createBlockItem("heat_generator", HEAT_GENERATOR);

    public static final DeferredBlock<Block> THERMAL_GENERATOR = BLOCKS.register("thermal_generator",
            () -> new ThermalGeneratorBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> THERMAL_GENERATOR_ITEM = createBlockItem("thermal_generator", THERMAL_GENERATOR);

    public static final DeferredBlock<Block> POWERED_LAMP = BLOCKS.register("powered_lamp",
            () -> new PoweredLampBlock(BlockBehaviour.Properties.of().
                    strength(.3f).sound(SoundType.GLASS).
                    lightLevel(PoweredLampBlock.LIGHT_EMISSION)));
    public static final DeferredItem<Item> POWERED_LAMP_ITEM = createBlockItem("powered_lamp", POWERED_LAMP);

    public static final DeferredBlock<Block> POWERED_FURNACE = BLOCKS.register("powered_furnace",
            () -> new PoweredFurnaceBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(PoweredFurnaceBlock.LIGHT_EMISSION)));
    public static final DeferredItem<Item> POWERED_FURNACE_ITEM = createBlockItem("powered_furnace", POWERED_FURNACE);

    public static final DeferredBlock<Block> ADVANCED_POWERED_FURNACE = BLOCKS.register("advanced_powered_furnace",
            () -> new AdvancedPoweredFurnaceBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(AdvancedPoweredFurnaceBlock.LIGHT_EMISSION)));
    public static final DeferredItem<Item> ADVANCED_POWERED_FURNACE_ITEM = createBlockItem("advanced_powered_furnace", ADVANCED_POWERED_FURNACE);

    public static final DeferredBlock<Block> LIGHTNING_GENERATOR = BLOCKS.register("lightning_generator",
            () -> new LightningGeneratorBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(LightningGeneratorBlock.LIGHT_EMISSION)));
    public static final DeferredItem<Item> LIGHTNING_GENERATOR_ITEM = EPItems.ITEMS.register("lightning_generator",
            () -> new LightningGeneratorBlock.Item(LIGHTNING_GENERATOR.get(), new Item.Properties()));

    public static final DeferredBlock<Block> ENERGIZER = BLOCKS.register("energizer",
            () -> new EnergizerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(EnergizerBlock.LIGHT_EMISSION)));
    public static final DeferredItem<Item> ENERGIZER_ITEM = createBlockItem("energizer", ENERGIZER);

    public static final DeferredBlock<Block> CHARGING_STATION = BLOCKS.register("charging_station",
            () -> new ChargingStationBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(ChargingStationBlock.LIGHT_EMISSION)));
    public static final DeferredItem<Item> CHARGING_STATION_ITEM = EPItems.ITEMS.register("charging_station",
            () -> new ChargingStationBlock.Item(CHARGING_STATION.get(), new Item.Properties()));

    public static final DeferredBlock<Block> CRYSTAL_GROWTH_CHAMBER = BLOCKS.register("crystal_growth_chamber",
            () -> new CrystalGrowthChamberBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> CRYSTAL_GROWTH_CHAMBER_ITEM = createBlockItem("crystal_growth_chamber", CRYSTAL_GROWTH_CHAMBER);

    public static final DeferredBlock<Block> WEATHER_CONTROLLER = BLOCKS.register("weather_controller",
            () -> new WeatherControllerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> WEATHER_CONTROLLER_ITEM = createBlockItem("weather_controller", WEATHER_CONTROLLER);

    public static final DeferredBlock<Block> TIME_CONTROLLER = BLOCKS.register("time_controller",
            () -> new TimeControllerBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> TIME_CONTROLLER_ITEM = createBlockItem("time_controller", TIME_CONTROLLER);

    public static final DeferredBlock<Block> TELEPORTER = BLOCKS.register("teleporter",
            () -> new TeleporterBlock(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> TELEPORTER_ITEM = EPItems.ITEMS.register("teleporter",
            () -> new TeleporterBlock.Item(TELEPORTER.get(), new Item.Properties()));

    public static final DeferredBlock<Block> BASIC_MACHINE_FRAME = BLOCKS.register("basic_machine_frame",
            () -> new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> BASIC_MACHINE_FRAME_ITEM = createBlockItem("basic_machine_frame", BASIC_MACHINE_FRAME);

    public static final DeferredBlock<Block> HARDENED_MACHINE_FRAME = BLOCKS.register("hardened_machine_frame",
            () -> new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> HARDENED_MACHINE_FRAME_ITEM = createBlockItem("hardened_machine_frame", HARDENED_MACHINE_FRAME);

    public static final DeferredBlock<Block> ADVANCED_MACHINE_FRAME = BLOCKS.register("advanced_machine_frame",
            () -> new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("advanced_machine_frame", ADVANCED_MACHINE_FRAME);

    public static final DeferredBlock<Block> REINFORCED_ADVANCED_MACHINE_FRAME = BLOCKS.register("reinforced_advanced_machine_frame",
            () -> new Block(BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final DeferredItem<Item> REINFORCED_ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("reinforced_advanced_machine_frame", REINFORCED_ADVANCED_MACHINE_FRAME);

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
