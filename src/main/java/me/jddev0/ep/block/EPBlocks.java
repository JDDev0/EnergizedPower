package me.jddev0.ep.block;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.machine.tier.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

import java.util.function.BiFunction;
import java.util.function.Function;

public final class EPBlocks {
    private EPBlocks() {}

    public static DeferredBlock<Block> registerBlock(String name, Block.Properties props) {
        return registerBlock(name, Block::new, props);
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<Block.Properties, T> factory, Block.Properties props) {
        ResourceLocation blockId = EPAPI.id(name);
        return BLOCKS.register(name, () -> factory.apply(props.setId(ResourceKey.create(Registries.BLOCK, blockId))));
    }

    public static DeferredItem<Item> createBlockItem(String name, DeferredBlock<? extends Block> block) {
        return createBlockItem(name, BlockItem::new, block, new Item.Properties());
    }

    public static DeferredItem<Item> createBlockItem(String name, BiFunction<Block, Item.Properties, Item> factory, DeferredBlock<? extends Block> block) {
        return createBlockItem(name, props -> factory.apply(block.get(), props), new Item.Properties());
    }

    public static DeferredItem<Item> createBlockItem(String name, BiFunction<Block, Item.Properties, Item> factory, DeferredBlock<? extends Block> block, Item.Properties props) {
        return createBlockItem(name, p -> factory.apply(block.get(), p), props);
    }

    public static DeferredItem<Item> createBlockItem(String name, Function<Item.Properties, Item> factory) {
        return createBlockItem(name, factory, new Item.Properties());
    }

    public static DeferredItem<Item> createBlockItem(String name, Function<Item.Properties, Item> factory, Item.Properties props) {
        return EPItems.registerItem(name, factory, props);
    }

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EPAPI.MOD_ID);

    public static final DeferredBlock<Block> SILICON_BLOCK = registerBlock("silicon_block",
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> SILICON_BLOCK_ITEM = createBlockItem("silicon_block", SILICON_BLOCK);

    public static final DeferredBlock<Block> TIN_BLOCK = registerBlock("tin_block",
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> TIN_BLOCK_ITEM = createBlockItem("tin_block", TIN_BLOCK);

    public static final DeferredBlock<Block> SAWDUST_BLOCK = registerBlock("sawdust_block",
            props -> new SimpleFlammableBlock(props, 5, 20),
            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0f, 3.0f).sound(SoundType.WOOD));
    public static final DeferredItem<Item> SAWDUST_BLOCK_ITEM = createBlockItem("sawdust_block", SAWDUST_BLOCK);

    public static final DeferredBlock<Block> TIN_ORE = registerBlock("tin_ore",
            props -> new DropExperienceBlock(ConstantInt.of(0), props),
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).
                    requiresCorrectToolForDrops().strength(3.0f, 3.0f));
    public static final DeferredItem<Item> TIN_ORE_ITEM = createBlockItem("tin_ore", TIN_ORE);

    public static final DeferredBlock<Block> DEEPSLATE_TIN_ORE = registerBlock("deepslate_tin_ore",
            props -> new DropExperienceBlock(ConstantInt.of(0), props),
            BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).requiresCorrectToolForDrops().
                    strength(4.5f, 3.0f).sound(SoundType.DEEPSLATE));
    public static final DeferredItem<Item> DEEPSLATE_TIN_ORE_ITEM = createBlockItem("deepslate_tin_ore", DEEPSLATE_TIN_ORE);

    public static final DeferredBlock<Block> RAW_TIN_BLOCK = registerBlock("raw_tin_block",
            BlockBehaviour.Properties.of().mapColor(MapColor.RAW_IRON)
                    .instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0f, 6.0f));
    public static final DeferredItem<Item> RAW_TIN_BLOCK_ITEM = createBlockItem("raw_tin_block", RAW_TIN_BLOCK);

    public static final DeferredBlock<ItemConveyorBeltBlock> BASIC_ITEM_CONVEYOR_BELT = registerBlock("item_conveyor_belt",
            props -> new ItemConveyorBeltBlock(ConveyorBeltTier.BASIC, props), BlockBehaviour.Properties.of().noCollision().
                    strength(2.5f, 3.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> BASIC_ITEM_CONVEYOR_BELT_ITEM = createBlockItem("item_conveyor_belt",
            (block, props) -> new ItemConveyorBeltBlock.Item(block, props, ConveyorBeltTier.BASIC), BASIC_ITEM_CONVEYOR_BELT);
    public static final DeferredBlock<ItemConveyorBeltBlock> FAST_ITEM_CONVEYOR_BELT = registerBlock("fast_item_conveyor_belt",
            props -> new ItemConveyorBeltBlock(ConveyorBeltTier.FAST, props), BlockBehaviour.Properties.of().noCollision().
                    strength(2.5f, 3.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> FAST_ITEM_CONVEYOR_BELT_ITEM = createBlockItem("fast_item_conveyor_belt",
            (block, props) -> new ItemConveyorBeltBlock.Item(block, props, ConveyorBeltTier.FAST), FAST_ITEM_CONVEYOR_BELT);
    public static final DeferredBlock<ItemConveyorBeltBlock> EXPRESS_ITEM_CONVEYOR_BELT = registerBlock("express_item_conveyor_belt",
            props -> new ItemConveyorBeltBlock(ConveyorBeltTier.EXPRESS, props), BlockBehaviour.Properties.of().noCollision().
                    strength(2.5f, 3.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> EXPRESS_ITEM_CONVEYOR_BELT_ITEM = createBlockItem("express_item_conveyor_belt",
            (block, props) -> new ItemConveyorBeltBlock.Item(block, props, ConveyorBeltTier.EXPRESS), EXPRESS_ITEM_CONVEYOR_BELT);

    public static final DeferredBlock<ItemConveyorBeltLoaderBlock> BASIC_ITEM_CONVEYOR_BELT_LOADER = registerBlock("item_conveyor_belt_loader",
            props -> new ItemConveyorBeltLoaderBlock(ConveyorBeltTier.BASIC, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> BASIC_ITEM_CONVEYOR_BELT_LOADER_ITEM = createBlockItem("item_conveyor_belt_loader", BASIC_ITEM_CONVEYOR_BELT_LOADER);
    public static final DeferredBlock<ItemConveyorBeltLoaderBlock> FAST_ITEM_CONVEYOR_BELT_LOADER = registerBlock("fast_item_conveyor_belt_loader",
            props -> new ItemConveyorBeltLoaderBlock(ConveyorBeltTier.FAST, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> FAST_ITEM_CONVEYOR_BELT_LOADER_ITEM = createBlockItem("fast_item_conveyor_belt_loader", FAST_ITEM_CONVEYOR_BELT_LOADER);
    public static final DeferredBlock<ItemConveyorBeltLoaderBlock> EXPRESS_ITEM_CONVEYOR_BELT_LOADER = registerBlock("express_item_conveyor_belt_loader",
            props -> new ItemConveyorBeltLoaderBlock(ConveyorBeltTier.EXPRESS, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> EXPRESS_ITEM_CONVEYOR_BELT_LOADER_ITEM = createBlockItem("express_item_conveyor_belt_loader", EXPRESS_ITEM_CONVEYOR_BELT_LOADER);

    public static final DeferredBlock<ItemConveyorBeltSorterBlock> BASIC_ITEM_CONVEYOR_BELT_SORTER = registerBlock("item_conveyor_belt_sorter",
            props -> new ItemConveyorBeltSorterBlock(ConveyorBeltTier.BASIC, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> BASIC_ITEM_CONVEYOR_BELT_SORTER_ITEM = createBlockItem("item_conveyor_belt_sorter", BASIC_ITEM_CONVEYOR_BELT_SORTER);
    public static final DeferredBlock<ItemConveyorBeltSorterBlock> FAST_ITEM_CONVEYOR_BELT_SORTER = registerBlock("fast_item_conveyor_belt_sorter",
            props -> new ItemConveyorBeltSorterBlock(ConveyorBeltTier.FAST, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> FAST_ITEM_CONVEYOR_BELT_SORTER_ITEM = createBlockItem("fast_item_conveyor_belt_sorter", FAST_ITEM_CONVEYOR_BELT_SORTER);
    public static final DeferredBlock<ItemConveyorBeltSorterBlock> EXPRESS_ITEM_CONVEYOR_BELT_SORTER = registerBlock("express_item_conveyor_belt_sorter",
            props -> new ItemConveyorBeltSorterBlock(ConveyorBeltTier.EXPRESS, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> EXPRESS_ITEM_CONVEYOR_BELT_SORTER_ITEM = createBlockItem("express_item_conveyor_belt_sorter", EXPRESS_ITEM_CONVEYOR_BELT_SORTER);

    public static final DeferredBlock<ItemConveyorBeltSwitchBlock> BASIC_ITEM_CONVEYOR_BELT_SWITCH = registerBlock("item_conveyor_belt_switch",
            props -> new ItemConveyorBeltSwitchBlock(ConveyorBeltTier.BASIC, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> BASIC_ITEM_CONVEYOR_BELT_SWITCH_ITEM = createBlockItem("item_conveyor_belt_switch", BASIC_ITEM_CONVEYOR_BELT_SWITCH);
    public static final DeferredBlock<ItemConveyorBeltSwitchBlock> FAST_ITEM_CONVEYOR_BELT_SWITCH = registerBlock("fast_item_conveyor_belt_switch",
            props -> new ItemConveyorBeltSwitchBlock(ConveyorBeltTier.FAST, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> FAST_ITEM_CONVEYOR_BELT_SWITCH_ITEM = createBlockItem("fast_item_conveyor_belt_switch", FAST_ITEM_CONVEYOR_BELT_SWITCH);
    public static final DeferredBlock<ItemConveyorBeltSwitchBlock> EXPRESS_ITEM_CONVEYOR_BELT_SWITCH = registerBlock("express_item_conveyor_belt_switch",
            props -> new ItemConveyorBeltSwitchBlock(ConveyorBeltTier.EXPRESS, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_ITEM = createBlockItem("express_item_conveyor_belt_switch", EXPRESS_ITEM_CONVEYOR_BELT_SWITCH);

    public static final DeferredBlock<ItemConveyorBeltSplitterBlock> BASIC_ITEM_CONVEYOR_BELT_SPLITTER = registerBlock("item_conveyor_belt_splitter",
            props -> new ItemConveyorBeltSplitterBlock(ConveyorBeltTier.BASIC, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> BASIC_ITEM_CONVEYOR_BELT_SPLITTER_ITEM = createBlockItem("item_conveyor_belt_splitter", BASIC_ITEM_CONVEYOR_BELT_SPLITTER);
    public static final DeferredBlock<ItemConveyorBeltSplitterBlock> FAST_ITEM_CONVEYOR_BELT_SPLITTER = registerBlock("fast_item_conveyor_belt_splitter",
            props -> new ItemConveyorBeltSplitterBlock(ConveyorBeltTier.FAST, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> FAST_ITEM_CONVEYOR_BELT_SPLITTER_ITEM = createBlockItem("fast_item_conveyor_belt_splitter", FAST_ITEM_CONVEYOR_BELT_SPLITTER);
    public static final DeferredBlock<ItemConveyorBeltSplitterBlock> EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER = registerBlock("express_item_conveyor_belt_splitter",
            props -> new ItemConveyorBeltSplitterBlock(ConveyorBeltTier.EXPRESS, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_ITEM = createBlockItem("express_item_conveyor_belt_splitter", EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER);

    public static final DeferredBlock<ItemConveyorBeltMergerBlock> BASIC_ITEM_CONVEYOR_BELT_MERGER = registerBlock("item_conveyor_belt_merger",
            props -> new ItemConveyorBeltMergerBlock(ConveyorBeltTier.BASIC, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> BASIC_ITEM_CONVEYOR_BELT_MERGER_ITEM = createBlockItem("item_conveyor_belt_merger", BASIC_ITEM_CONVEYOR_BELT_MERGER);
    public static final DeferredBlock<ItemConveyorBeltMergerBlock> FAST_ITEM_CONVEYOR_BELT_MERGER = registerBlock("fast_item_conveyor_belt_merger",
            props -> new ItemConveyorBeltMergerBlock(ConveyorBeltTier.FAST, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> FAST_ITEM_CONVEYOR_BELT_MERGER_ITEM = createBlockItem("fast_item_conveyor_belt_merger", FAST_ITEM_CONVEYOR_BELT_MERGER);
    public static final DeferredBlock<ItemConveyorBeltMergerBlock> EXPRESS_ITEM_CONVEYOR_BELT_MERGER = registerBlock("express_item_conveyor_belt_merger",
            props -> new ItemConveyorBeltMergerBlock(ConveyorBeltTier.EXPRESS, props), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5f).sound(SoundType.STONE));
    public static final DeferredItem<Item> EXPRESS_ITEM_CONVEYOR_BELT_MERGER_ITEM = createBlockItem("express_item_conveyor_belt_merger", EXPRESS_ITEM_CONVEYOR_BELT_MERGER);

    public static final DeferredBlock<FluidPipeBlock> IRON_FLUID_PIPE = registerBlock("fluid_pipe",
            props -> new FluidPipeBlock(FluidPipeTier.IRON, props), FluidPipeTier.IRON.getProperties());
    public static final DeferredItem<Item> IRON_FLUID_PIPE_ITEM = createBlockItem("fluid_pipe",
            props -> new FluidPipeBlock.Item(IRON_FLUID_PIPE.get(), props, FluidPipeTier.IRON));

    public static final DeferredBlock<FluidPipeBlock> GOLDEN_FLUID_PIPE = registerBlock("golden_fluid_pipe",
            props -> new FluidPipeBlock(FluidPipeTier.GOLDEN, props), FluidPipeTier.GOLDEN.getProperties());
    public static final DeferredItem<Item> GOLDEN_FLUID_PIPE_ITEM = createBlockItem("golden_fluid_pipe",
            props -> new FluidPipeBlock.Item(GOLDEN_FLUID_PIPE.get(), props, FluidPipeTier.GOLDEN));

    public static final DeferredBlock<FluidTankBlock> FLUID_TANK_SMALL = registerBlock("fluid_tank_small",
            props -> new FluidTankBlock(FluidTankTier.SMALL, props), FluidTankTier.SMALL.getProperties());
    public static final DeferredItem<Item> FLUID_TANK_SMALL_ITEM = createBlockItem("fluid_tank_small",
            props -> new FluidTankBlock.Item(FLUID_TANK_SMALL.get(), props, FluidTankTier.SMALL));

    public static final DeferredBlock<FluidTankBlock> FLUID_TANK_MEDIUM = registerBlock("fluid_tank_medium",
            props -> new FluidTankBlock(FluidTankTier.MEDIUM, props), FluidTankTier.MEDIUM.getProperties());
    public static final DeferredItem<Item> FLUID_TANK_MEDIUM_ITEM = createBlockItem("fluid_tank_medium",
            props -> new FluidTankBlock.Item(FLUID_TANK_MEDIUM.get(), props, FluidTankTier.MEDIUM));

    public static final DeferredBlock<FluidTankBlock> FLUID_TANK_LARGE = registerBlock("fluid_tank_large",
            props -> new FluidTankBlock(FluidTankTier.LARGE, props), FluidTankTier.LARGE.getProperties());
    public static final DeferredItem<Item> FLUID_TANK_LARGE_ITEM = createBlockItem("fluid_tank_large",
            props -> new FluidTankBlock.Item(FLUID_TANK_LARGE.get(), props, FluidTankTier.LARGE));

    public static final DeferredBlock<CreativeFluidTankBlock> CREATIVE_FLUID_TANK = registerBlock("creative_fluid_tank",
            CreativeFluidTankBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).
                    requiresCorrectToolForDrops().strength(-1.f, 3600000.f).noLootTable());
    public static final DeferredItem<Item> CREATIVE_FLUID_TANK_ITEM = createBlockItem("creative_fluid_tank",
            CreativeFluidTankBlock.Item::new, CREATIVE_FLUID_TANK);

    public static final DeferredBlock<ItemSiloBlock> ITEM_SILO_TINY = registerBlock("item_silo_tiny",
            props -> new ItemSiloBlock(ItemSiloTier.TINY, props), ItemSiloTier.TINY.getProperties());
    public static final DeferredItem<Item> ITEM_SILO_TINY_ITEM = createBlockItem("item_silo_tiny",
            props -> new ItemSiloBlock.Item(ITEM_SILO_TINY.get(), props, ItemSiloTier.TINY));

    public static final DeferredBlock<ItemSiloBlock> ITEM_SILO_SMALL = registerBlock("item_silo_small",
            props -> new ItemSiloBlock(ItemSiloTier.SMALL, props), ItemSiloTier.SMALL.getProperties());
    public static final DeferredItem<Item> ITEM_SILO_SMALL_ITEM = createBlockItem("item_silo_small",
            props -> new ItemSiloBlock.Item(ITEM_SILO_SMALL.get(), props, ItemSiloTier.SMALL));

    public static final DeferredBlock<ItemSiloBlock> ITEM_SILO_MEDIUM = registerBlock("item_silo_medium",
            props -> new ItemSiloBlock(ItemSiloTier.MEDIUM, props), ItemSiloTier.MEDIUM.getProperties());
    public static final DeferredItem<Item> ITEM_SILO_MEDIUM_ITEM = createBlockItem("item_silo_medium",
            props -> new ItemSiloBlock.Item(ITEM_SILO_MEDIUM.get(), props, ItemSiloTier.MEDIUM));

    public static final DeferredBlock<ItemSiloBlock> ITEM_SILO_LARGE = registerBlock("item_silo_large",
            props -> new ItemSiloBlock(ItemSiloTier.LARGE, props), ItemSiloTier.LARGE.getProperties());
    public static final DeferredItem<Item> ITEM_SILO_LARGE_ITEM = createBlockItem("item_silo_large",
            props -> new ItemSiloBlock.Item(ITEM_SILO_LARGE.get(), props, ItemSiloTier.LARGE));

    public static final DeferredBlock<ItemSiloBlock> ITEM_SILO_GIANT = registerBlock("item_silo_giant",
            props -> new ItemSiloBlock(ItemSiloTier.GIANT, props), ItemSiloTier.GIANT.getProperties());
    public static final DeferredItem<Item> ITEM_SILO_GIANT_ITEM = createBlockItem("item_silo_giant",
            props -> new ItemSiloBlock.Item(ITEM_SILO_GIANT.get(), props, ItemSiloTier.GIANT));

    public static final DeferredBlock<CreativeItemSiloBlock> CREATIVE_ITEM_SILO = registerBlock("creative_item_silo",
            CreativeItemSiloBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).
                    requiresCorrectToolForDrops().strength(-1.f, 3600000.f).noLootTable());
    public static final DeferredItem<Item> CREATIVE_ITEM_SILO_ITEM = createBlockItem("creative_item_silo",
            CreativeItemSiloBlock.Item::new, CREATIVE_ITEM_SILO);

    private static DeferredItem<Item> createCableBlockItem(String name, DeferredBlock<CableBlock> block) {
        return createBlockItem(name, props -> new CableBlock.Item(block.get(), props, block.get().getTier()));
    }
    public static final DeferredBlock<CableBlock> TIN_CABLE = registerBlock("tin_cable",
            props -> new CableBlock(CableTier.TIN, props), CableTier.TIN.getProperties());
    public static final DeferredItem<Item> TIN_CABLE_ITEM = createCableBlockItem("tin_cable", TIN_CABLE);

    public static final DeferredBlock<CableBlock> COPPER_CABLE = registerBlock("copper_cable",
            props -> new CableBlock(CableTier.COPPER, props), CableTier.COPPER.getProperties());
    public static final DeferredItem<Item> COPPER_CABLE_ITEM = createCableBlockItem("copper_cable", COPPER_CABLE);

    public static final DeferredBlock<CableBlock> GOLD_CABLE = registerBlock("gold_cable",
            props -> new CableBlock(CableTier.GOLD, props), CableTier.GOLD.getProperties());
    public static final DeferredItem<Item> GOLD_CABLE_ITEM = createCableBlockItem("gold_cable", GOLD_CABLE);

    public static final DeferredBlock<CableBlock> ENERGIZED_COPPER_CABLE = registerBlock("energized_copper_cable",
            props -> new CableBlock(CableTier.ENERGIZED_COPPER, props), CableTier.ENERGIZED_COPPER.getProperties());
    public static final DeferredItem<Item> ENERGIZED_COPPER_CABLE_ITEM = createCableBlockItem("energized_copper_cable", ENERGIZED_COPPER_CABLE);

    public static final DeferredBlock<CableBlock> ENERGIZED_GOLD_CABLE = registerBlock("energized_gold_cable",
            props -> new CableBlock(CableTier.ENERGIZED_GOLD, props), CableTier.ENERGIZED_GOLD.getProperties());
    public static final DeferredItem<Item> ENERGIZED_GOLD_CABLE_ITEM = createCableBlockItem("energized_gold_cable", ENERGIZED_GOLD_CABLE);

    public static final DeferredBlock<CableBlock> ENERGIZED_CRYSTAL_MATRIX_CABLE = registerBlock("energized_crystal_matrix_cable",
            props -> new CableBlock(CableTier.ENERGIZED_CRYSTAL_MATRIX, props), CableTier.ENERGIZED_CRYSTAL_MATRIX.getProperties());
    public static final DeferredItem<Item> ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM = createCableBlockItem("energized_crystal_matrix_cable", ENERGIZED_CRYSTAL_MATRIX_CABLE);

    private static DeferredItem<Item> createTransformerBlockItem(String name, DeferredBlock<TransformerBlock> block) {
        return createBlockItem(name, props -> new TransformerBlock.Item(block.get(), props, block.get().getTier(), block.get().getTransformerType()));
    }
    public static final DeferredBlock<TransformerBlock> LV_TRANSFORMER_1_TO_N = registerBlock("lv_transformer_1_to_n",
            props -> new TransformerBlock(props, TransformerTier.LV, TransformerType.TYPE_1_TO_N),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> LV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("lv_transformer_1_to_n",
            LV_TRANSFORMER_1_TO_N);

    public static final DeferredBlock<TransformerBlock> LV_TRANSFORMER_3_TO_3 = registerBlock("lv_transformer_3_to_3",
            props -> new TransformerBlock(props, TransformerTier.LV, TransformerType.TYPE_3_TO_3),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> LV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("lv_transformer_3_to_3",
            LV_TRANSFORMER_3_TO_3);

    public static final DeferredBlock<TransformerBlock> LV_TRANSFORMER_N_TO_1 = registerBlock("lv_transformer_n_to_1",
            props -> new TransformerBlock(props, TransformerTier.LV, TransformerType.TYPE_N_TO_1),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> LV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("lv_transformer_n_to_1",
            LV_TRANSFORMER_N_TO_1);

    public static final DeferredBlock<TransformerBlock> MV_TRANSFORMER_1_TO_N = registerBlock("transformer_1_to_n",
            props -> new TransformerBlock(props, TransformerTier.MV, TransformerType.TYPE_1_TO_N),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> MV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("transformer_1_to_n",
            MV_TRANSFORMER_1_TO_N);

    public static final DeferredBlock<TransformerBlock> MV_TRANSFORMER_3_TO_3 = registerBlock("transformer_3_to_3",
            props -> new TransformerBlock(props, TransformerTier.MV, TransformerType.TYPE_3_TO_3),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> MV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("transformer_3_to_3",
            MV_TRANSFORMER_3_TO_3);

    public static final DeferredBlock<TransformerBlock> MV_TRANSFORMER_N_TO_1 = registerBlock("transformer_n_to_1",
            props -> new TransformerBlock(props, TransformerTier.MV, TransformerType.TYPE_N_TO_1),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> MV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("transformer_n_to_1",
            MV_TRANSFORMER_N_TO_1);

    public static final DeferredBlock<TransformerBlock> HV_TRANSFORMER_1_TO_N = registerBlock("hv_transformer_1_to_n",
            props -> new TransformerBlock(props, TransformerTier.HV, TransformerType.TYPE_1_TO_N),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> HV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("hv_transformer_1_to_n",
            HV_TRANSFORMER_1_TO_N);

    public static final DeferredBlock<TransformerBlock> HV_TRANSFORMER_3_TO_3 = registerBlock("hv_transformer_3_to_3",
            props -> new TransformerBlock(props, TransformerTier.HV, TransformerType.TYPE_3_TO_3),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> HV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("hv_transformer_3_to_3",
            HV_TRANSFORMER_3_TO_3);

    public static final DeferredBlock<TransformerBlock> HV_TRANSFORMER_N_TO_1 = registerBlock("hv_transformer_n_to_1",
            props -> new TransformerBlock(props, TransformerTier.HV, TransformerType.TYPE_N_TO_1),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> HV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("hv_transformer_n_to_1",
            HV_TRANSFORMER_N_TO_1);

    public static final DeferredBlock<TransformerBlock> EHV_TRANSFORMER_1_TO_N = registerBlock("ehv_transformer_1_to_n",
            props -> new TransformerBlock(props, TransformerTier.EHV, TransformerType.TYPE_1_TO_N),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> EHV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("ehv_transformer_1_to_n",
            EHV_TRANSFORMER_1_TO_N);

    public static final DeferredBlock<TransformerBlock> EHV_TRANSFORMER_3_TO_3 = registerBlock("ehv_transformer_3_to_3",
            props -> new TransformerBlock(props, TransformerTier.EHV, TransformerType.TYPE_3_TO_3),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> EHV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("ehv_transformer_3_to_3",
            EHV_TRANSFORMER_3_TO_3);

    public static final DeferredBlock<TransformerBlock> EHV_TRANSFORMER_N_TO_1 = registerBlock("ehv_transformer_n_to_1",
            props -> new TransformerBlock(props, TransformerTier.EHV, TransformerType.TYPE_N_TO_1),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> EHV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("ehv_transformer_n_to_1",
            EHV_TRANSFORMER_N_TO_1);

    private static DeferredItem<Item> createConfigurableTransformerBlockItem(String name, DeferredBlock<ConfigurableTransformerBlock> block) {
        return createBlockItem(name, props -> new ConfigurableTransformerBlock.Item(block.get(), props, block.get().getTier()));
    }
    public static final DeferredBlock<ConfigurableTransformerBlock> CONFIGURABLE_LV_TRANSFORMER = registerBlock("configurable_lv_transformer",
            props -> new ConfigurableTransformerBlock(TransformerTier.LV, props),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> CONFIGURABLE_LV_TRANSFORMER_ITEM = createConfigurableTransformerBlockItem("configurable_lv_transformer",
            CONFIGURABLE_LV_TRANSFORMER);

    public static final DeferredBlock<ConfigurableTransformerBlock> CONFIGURABLE_MV_TRANSFORMER = registerBlock("configurable_mv_transformer",
            props -> new ConfigurableTransformerBlock(TransformerTier.MV, props),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> CONFIGURABLE_MV_TRANSFORMER_ITEM = createConfigurableTransformerBlockItem("configurable_mv_transformer",
            CONFIGURABLE_MV_TRANSFORMER);

    public static final DeferredBlock<ConfigurableTransformerBlock> CONFIGURABLE_HV_TRANSFORMER = registerBlock("configurable_hv_transformer",
            props -> new ConfigurableTransformerBlock(TransformerTier.HV, props),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> CONFIGURABLE_HV_TRANSFORMER_ITEM = createConfigurableTransformerBlockItem("configurable_hv_transformer",
            CONFIGURABLE_HV_TRANSFORMER);

    public static final DeferredBlock<ConfigurableTransformerBlock> CONFIGURABLE_EHV_TRANSFORMER = registerBlock("configurable_ehv_transformer",
            props -> new ConfigurableTransformerBlock(TransformerTier.EHV, props),
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> CONFIGURABLE_EHV_TRANSFORMER_ITEM = createConfigurableTransformerBlockItem("configurable_ehv_transformer",
            CONFIGURABLE_EHV_TRANSFORMER);

    public static final DeferredBlock<Block> BATTERY_BOX = registerBlock("battery_box",
            BatteryBoxBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> BATTERY_BOX_ITEM = createBlockItem("battery_box",
            BatteryBoxBlock.Item::new, BATTERY_BOX);

    public static final DeferredBlock<Block> ADVANCED_BATTERY_BOX = registerBlock("advanced_battery_box",
            AdvancedBatteryBoxBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> ADVANCED_BATTERY_BOX_ITEM = createBlockItem("advanced_battery_box",
            AdvancedBatteryBoxBlock.Item::new, ADVANCED_BATTERY_BOX);

    public static final DeferredBlock<Block> CREATIVE_BATTERY_BOX = registerBlock("creative_battery_box",
            CreativeBatteryBoxBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).
                    requiresCorrectToolForDrops().strength(-1.f, 3600000.f).noLootTable());
    public static final DeferredItem<Item> CREATIVE_BATTERY_BOX_ITEM = createBlockItem("creative_battery_box",
            CreativeBatteryBoxBlock.Item::new, CREATIVE_BATTERY_BOX);

    public static final DeferredBlock<Block> PRESS_MOLD_MAKER = registerBlock("press_mold_maker",
            PressMoldMakerBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0f, 6.0f).sound(SoundType.STONE));
    public static final DeferredItem<Item> PRESS_MOLD_MAKER_ITEM = createBlockItem("press_mold_maker", PRESS_MOLD_MAKER);

    public static final DeferredBlock<Block> ALLOY_FURNACE = registerBlock("alloy_furnace",
            AlloyFurnaceBlock::new, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).
                    instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0f, 6.0f).sound(SoundType.STONE).
                    lightLevel(AlloyFurnaceBlock.LIGHT_EMISSION));
    public static final DeferredItem<Item> ALLOY_FURNACE_ITEM = createBlockItem("alloy_furnace", ALLOY_FURNACE);

    public static final DeferredBlock<Block> AUTO_CRAFTER = registerBlock("auto_crafter",
            AutoCrafterBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> AUTO_CRAFTER_ITEM = createBlockItem("auto_crafter",
            AutoCrafterBlock.Item::new, AUTO_CRAFTER);

    public static final DeferredBlock<Block> ADVANCED_AUTO_CRAFTER = registerBlock("advanced_auto_crafter",
            AdvancedAutoCrafterBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> ADVANCED_AUTO_CRAFTER_ITEM = createBlockItem("advanced_auto_crafter",
            AdvancedAutoCrafterBlock.Item::new, ADVANCED_AUTO_CRAFTER);

    public static final DeferredBlock<Block> CRUSHER = registerBlock("crusher",
            CrusherBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> CRUSHER_ITEM = createBlockItem("crusher", CRUSHER);

    public static final DeferredBlock<Block> ADVANCED_CRUSHER = registerBlock("advanced_crusher",
            AdvancedCrusherBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> ADVANCED_CRUSHER_ITEM = createBlockItem("advanced_crusher",
            AdvancedCrusherBlock.Item::new, ADVANCED_CRUSHER);

    public static final DeferredBlock<Block> PULVERIZER = registerBlock("pulverizer",
            PulverizerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> PULVERIZER_ITEM = createBlockItem("pulverizer", PULVERIZER);

    public static final DeferredBlock<Block> ADVANCED_PULVERIZER = registerBlock("advanced_pulverizer",
            AdvancedPulverizerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> ADVANCED_PULVERIZER_ITEM = createBlockItem("advanced_pulverizer",
            AdvancedPulverizerBlock.Item::new, ADVANCED_PULVERIZER);

    public static final DeferredBlock<Block> SAWMILL = registerBlock("sawmill",
            SawmillBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> SAWMILL_ITEM = createBlockItem("sawmill", SAWMILL);

    public static final DeferredBlock<Block> COMPRESSOR = registerBlock("compressor",
            CompressorBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> COMPRESSOR_ITEM = createBlockItem("compressor", COMPRESSOR);

    public static final DeferredBlock<Block> METAL_PRESS = registerBlock("metal_press",
            MetalPressBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> METAL_PRESS_ITEM = createBlockItem("metal_press",
            MetalPressBlock.Item::new, METAL_PRESS);

    public static final DeferredBlock<Block> AUTO_PRESS_MOLD_MAKER = registerBlock("auto_press_mold_maker",
            AutoPressMoldMakerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> AUTO_PRESS_MOLD_MAKER_ITEM = createBlockItem("auto_press_mold_maker", AUTO_PRESS_MOLD_MAKER);

    public static final DeferredBlock<Block> AUTO_STONECUTTER = registerBlock("auto_stonecutter",
            AutoStonecutterBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> AUTO_STONECUTTER_ITEM = createBlockItem("auto_stonecutter", AUTO_STONECUTTER);

    public static final DeferredBlock<Block> PLANT_GROWTH_CHAMBER = registerBlock("plant_growth_chamber",
            PlantGrowthChamberBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> PLANT_GROWTH_CHAMBER_ITEM = createBlockItem("plant_growth_chamber", PLANT_GROWTH_CHAMBER);

    public static final DeferredBlock<Block> BLOCK_PLACER = registerBlock("block_placer",
            BlockPlacerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> BLOCK_PLACER_ITEM = createBlockItem("block_placer", BLOCK_PLACER);

    public static final DeferredBlock<Block> ASSEMBLING_MACHINE = registerBlock("assembling_machine",
            AssemblingMachineBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> ASSEMBLING_MACHINE_ITEM = createBlockItem("assembling_machine", ASSEMBLING_MACHINE);

    public static final DeferredBlock<Block> INDUCTION_SMELTER = registerBlock("induction_smelter",
            InductionSmelterBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(InductionSmelterBlock.LIGHT_EMISSION));
    public static final DeferredItem<Item> INDUCTION_SMELTER_ITEM = createBlockItem("induction_smelter", INDUCTION_SMELTER);

    public static final DeferredBlock<Block> FLUID_FILLER = registerBlock("fluid_filler",
            FluidFillerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> FLUID_FILLER_ITEM = createBlockItem("fluid_filler", FLUID_FILLER);

    public static final DeferredBlock<Block> STONE_LIQUEFIER = registerBlock("stone_liquefier",
            StoneLiquefierBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> STONE_LIQUEFIER_ITEM = createBlockItem("stone_liquefier", STONE_LIQUEFIER);

    public static final DeferredBlock<Block> STONE_SOLIDIFIER = registerBlock("stone_solidifier",
            StoneSolidifierBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> STONE_SOLIDIFIER_ITEM = createBlockItem("stone_solidifier", STONE_SOLIDIFIER);

    public static final DeferredBlock<Block> FILTRATION_PLANT = registerBlock("filtration_plant",
            FiltrationPlantBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> FILTRATION_PLANT_ITEM = createBlockItem("filtration_plant",
            FiltrationPlantBlock.Item::new, FILTRATION_PLANT);

    public static final DeferredBlock<Block> FLUID_TRANSPOSER = registerBlock("fluid_transposer",
            FluidTransposerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> FLUID_TRANSPOSER_ITEM = createBlockItem("fluid_transposer",
            FLUID_TRANSPOSER);

    public static final DeferredBlock<Block> FLUID_DRAINER = registerBlock("fluid_drainer",
            FluidDrainerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> FLUID_DRAINER_ITEM = createBlockItem("fluid_drainer", FLUID_DRAINER);

    public static final DeferredBlock<Block> FLUID_PUMP = registerBlock("fluid_pump",
            FluidPumpBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> FLUID_PUMP_ITEM = createBlockItem("fluid_pump", FLUID_PUMP);

    public static final DeferredBlock<Block> ADVANCED_FLUID_PUMP = registerBlock("advanced_fluid_pump",
            AdvancedFluidPumpBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> ADVANCED_FLUID_PUMP_ITEM = createBlockItem("advanced_fluid_pump", ADVANCED_FLUID_PUMP);

    public static final DeferredBlock<Block> DRAIN = registerBlock("drain",
            DrainBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> DRAIN_ITEM = createBlockItem("drain",
            DrainBlock.Item::new, DRAIN);

    public static final DeferredBlock<Block> CHARGER = registerBlock("charger",
            ChargerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> CHARGER_ITEM = createBlockItem("charger",
            ChargerBlock.Item::new, CHARGER);

    public static final DeferredBlock<Block> ADVANCED_CHARGER = registerBlock("advanced_charger",
            AdvancedChargerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> ADVANCED_CHARGER_ITEM = createBlockItem("advanced_charger",
            AdvancedChargerBlock.Item::new, ADVANCED_CHARGER);

    public static final DeferredBlock<Block> UNCHARGER = registerBlock("uncharger",
            UnchargerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> UNCHARGER_ITEM = createBlockItem("uncharger", UNCHARGER);

    public static final DeferredBlock<Block> ADVANCED_UNCHARGER = registerBlock("advanced_uncharger",
            AdvancedUnchargerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> ADVANCED_UNCHARGER_ITEM = createBlockItem("advanced_uncharger", ADVANCED_UNCHARGER);

    public static final DeferredBlock<Block> MINECART_CHARGER = registerBlock("minecart_charger",
            MinecartChargerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> MINECART_CHARGER_ITEM = createBlockItem("minecart_charger",
            MinecartChargerBlock.Item::new, MINECART_CHARGER);

    public static final DeferredBlock<Block> ADVANCED_MINECART_CHARGER = registerBlock("advanced_minecart_charger",
            AdvancedMinecartChargerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> ADVANCED_MINECART_CHARGER_ITEM = createBlockItem("advanced_minecart_charger",
            AdvancedMinecartChargerBlock.Item::new, ADVANCED_MINECART_CHARGER);

    public static final DeferredBlock<Block> MINECART_UNCHARGER = registerBlock("minecart_uncharger",
            MinecartUnchargerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> MINECART_UNCHARGER_ITEM = createBlockItem("minecart_uncharger",
            MinecartUnchargerBlock.Item::new, MINECART_UNCHARGER);

    public static final DeferredBlock<Block> ADVANCED_MINECART_UNCHARGER = registerBlock("advanced_minecart_uncharger",
            AdvancedMinecartUnchargerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> ADVANCED_MINECART_UNCHARGER_ITEM = createBlockItem("advanced_minecart_uncharger",
            AdvancedMinecartUnchargerBlock.Item::new, ADVANCED_MINECART_UNCHARGER);


    private static DeferredItem<Item> createSolarPanelBlockItem(String name, DeferredBlock<SolarPanelBlock> blockRegistryObject) {
        return createBlockItem(name, props -> new SolarPanelBlock.Item(blockRegistryObject.get(), props,
                blockRegistryObject.get().getTier()));
    }
    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL_1 = registerBlock("solar_panel_1",
            props -> new SolarPanelBlock(SolarPanelTier.TIER_1, props), SolarPanelTier.TIER_1.getProperties());
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM_1 = createSolarPanelBlockItem("solar_panel_1", SOLAR_PANEL_1);

    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL_2 = registerBlock("solar_panel_2",
            props -> new SolarPanelBlock(SolarPanelTier.TIER_2, props), SolarPanelTier.TIER_2.getProperties());
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM_2 = createSolarPanelBlockItem("solar_panel_2", SOLAR_PANEL_2);

    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL_3 = registerBlock("solar_panel_3",
            props -> new SolarPanelBlock(SolarPanelTier.TIER_3, props), SolarPanelTier.TIER_3.getProperties());
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM_3 = createSolarPanelBlockItem("solar_panel_3", SOLAR_PANEL_3);

    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL_4 = registerBlock("solar_panel_4",
            props -> new SolarPanelBlock(SolarPanelTier.TIER_4, props), SolarPanelTier.TIER_4.getProperties());
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM_4 = createSolarPanelBlockItem("solar_panel_4", SOLAR_PANEL_4);

    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL_5 = registerBlock("solar_panel_5",
            props -> new SolarPanelBlock(SolarPanelTier.TIER_5, props), SolarPanelTier.TIER_5.getProperties());
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM_5 = createSolarPanelBlockItem("solar_panel_5", SOLAR_PANEL_5);

    public static final DeferredBlock<SolarPanelBlock> SOLAR_PANEL_6 = registerBlock("solar_panel_6",
            props -> new SolarPanelBlock(SolarPanelTier.TIER_6, props), SolarPanelTier.TIER_6.getProperties());
    public static final DeferredItem<Item> SOLAR_PANEL_ITEM_6 = createSolarPanelBlockItem("solar_panel_6", SOLAR_PANEL_6);

    public static final DeferredBlock<Block> COAL_ENGINE = registerBlock("coal_engine",
            CoalEngineBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(CoalEngineBlock.LIGHT_EMISSION));
    public static final DeferredItem<Item> COAL_ENGINE_ITEM = createBlockItem("coal_engine", COAL_ENGINE);

    public static final DeferredBlock<Block> HEAT_GENERATOR = registerBlock("heat_generator",
            HeatGeneratorBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> HEAT_GENERATOR_ITEM = createBlockItem("heat_generator", HEAT_GENERATOR);

    public static final DeferredBlock<Block> THERMAL_GENERATOR = registerBlock("thermal_generator",
            ThermalGeneratorBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> THERMAL_GENERATOR_ITEM = createBlockItem("thermal_generator", THERMAL_GENERATOR);

    public static final DeferredBlock<Block> POWERED_LAMP = registerBlock("powered_lamp",
            PoweredLampBlock::new, BlockBehaviour.Properties.of().
                    strength(.3f).sound(SoundType.GLASS).
                    lightLevel(PoweredLampBlock.LIGHT_EMISSION));
    public static final DeferredItem<Item> POWERED_LAMP_ITEM = createBlockItem("powered_lamp", POWERED_LAMP);

    public static final DeferredBlock<Block> POWERED_FURNACE = registerBlock("powered_furnace",
            PoweredFurnaceBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(PoweredFurnaceBlock.LIGHT_EMISSION));
    public static final DeferredItem<Item> POWERED_FURNACE_ITEM = createBlockItem("powered_furnace", POWERED_FURNACE);

    public static final DeferredBlock<Block> ADVANCED_POWERED_FURNACE = registerBlock("advanced_powered_furnace",
            AdvancedPoweredFurnaceBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(AdvancedPoweredFurnaceBlock.LIGHT_EMISSION));
    public static final DeferredItem<Item> ADVANCED_POWERED_FURNACE_ITEM = createBlockItem("advanced_powered_furnace", ADVANCED_POWERED_FURNACE);

    public static final DeferredBlock<Block> LIGHTNING_GENERATOR = registerBlock("lightning_generator",
            LightningGeneratorBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(LightningGeneratorBlock.LIGHT_EMISSION));
    public static final DeferredItem<Item> LIGHTNING_GENERATOR_ITEM = createBlockItem("lightning_generator",
            LightningGeneratorBlock.Item::new, LIGHTNING_GENERATOR);

    public static final DeferredBlock<Block> ENERGIZER = registerBlock("energizer",
            EnergizerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(EnergizerBlock.LIGHT_EMISSION));
    public static final DeferredItem<Item> ENERGIZER_ITEM = createBlockItem("energizer", ENERGIZER);

    public static final DeferredBlock<Block> CHARGING_STATION = registerBlock("charging_station",
            ChargingStationBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(ChargingStationBlock.LIGHT_EMISSION));
    public static final DeferredItem<Item> CHARGING_STATION_ITEM = createBlockItem("charging_station",
            ChargingStationBlock.Item::new, CHARGING_STATION);

    public static final DeferredBlock<Block> CRYSTAL_GROWTH_CHAMBER = registerBlock("crystal_growth_chamber",
            CrystalGrowthChamberBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> CRYSTAL_GROWTH_CHAMBER_ITEM = createBlockItem("crystal_growth_chamber", CRYSTAL_GROWTH_CHAMBER);

    public static final DeferredBlock<Block> WEATHER_CONTROLLER = registerBlock("weather_controller",
            WeatherControllerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> WEATHER_CONTROLLER_ITEM = createBlockItem("weather_controller", WEATHER_CONTROLLER);

    public static final DeferredBlock<Block> TIME_CONTROLLER = registerBlock("time_controller",
            TimeControllerBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> TIME_CONTROLLER_ITEM = createBlockItem("time_controller", TIME_CONTROLLER);

    public static final DeferredBlock<Block> TELEPORTER = registerBlock("teleporter",
            TeleporterBlock::new, BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> TELEPORTER_ITEM = createBlockItem("teleporter",
            TeleporterBlock.Item::new, TELEPORTER);

    public static final DeferredBlock<Block> BASIC_MACHINE_FRAME = registerBlock("basic_machine_frame",
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> BASIC_MACHINE_FRAME_ITEM = createBlockItem("basic_machine_frame", BASIC_MACHINE_FRAME);

    public static final DeferredBlock<Block> HARDENED_MACHINE_FRAME = registerBlock("hardened_machine_frame",
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> HARDENED_MACHINE_FRAME_ITEM = createBlockItem("hardened_machine_frame", HARDENED_MACHINE_FRAME);

    public static final DeferredBlock<Block> ADVANCED_MACHINE_FRAME = registerBlock("advanced_machine_frame",
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("advanced_machine_frame", ADVANCED_MACHINE_FRAME);

    public static final DeferredBlock<Block> REINFORCED_ADVANCED_MACHINE_FRAME = registerBlock("reinforced_advanced_machine_frame",
            BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL));
    public static final DeferredItem<Item> REINFORCED_ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("reinforced_advanced_machine_frame", REINFORCED_ADVANCED_MACHINE_FRAME);

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
