package me.jddev0.ep.block;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EnergizedPowerMod.MODID);

    private static RegistryObject<Item> createBlockItem(String name, RegistryObject<Block> blockRegistryObject, Item.Properties props) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(blockRegistryObject.get(), props));
    }
    private static RegistryObject<Item> createBlockItem(String name, RegistryObject<Block> blockRegistryObject) {
        return createBlockItem(name, blockRegistryObject, new Item.Properties());
    }

    public static final RegistryObject<Block> SILICON_BLOCK = BLOCKS.register("silicon_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL).
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final RegistryObject<Item> SILICON_BLOCK_ITEM = createBlockItem("silicon_block", SILICON_BLOCK);

    public static final RegistryObject<Block> AUTO_CRAFTER = BLOCKS.register("auto_crafter",
            () -> new AutoCrafterBlock(BlockBehaviour.Properties.of(Material.METAL).
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final RegistryObject<Item> AUTO_CRAFTER_ITEM = createBlockItem("auto_crafter", AUTO_CRAFTER);


    private static RegistryObject<Item> createSolarPanelBlockItem(String name, RegistryObject<SolarPanelBlock> blockRegistryObject) {
        return ModItems.ITEMS.register(name, () -> new SolarPanelBlock.Item(blockRegistryObject.get(), new Item.Properties(),
                blockRegistryObject.get().getTier()));
    }
    public static final RegistryObject<SolarPanelBlock> SOLAR_PANEL_1 = BLOCKS.register("solar_panel_1",
            () -> new SolarPanelBlock(SolarPanelBlock.Tier.TIER_1));
    public static final RegistryObject<Item> SOLAR_PANEL_ITEM_1 = createSolarPanelBlockItem("solar_panel_1", SOLAR_PANEL_1);

    public static final RegistryObject<SolarPanelBlock> SOLAR_PANEL_2 = BLOCKS.register("solar_panel_2",
            () -> new SolarPanelBlock(SolarPanelBlock.Tier.TIER_2));
    public static final RegistryObject<Item> SOLAR_PANEL_ITEM_2 = createSolarPanelBlockItem("solar_panel_2", SOLAR_PANEL_2);

    public static final RegistryObject<SolarPanelBlock> SOLAR_PANEL_3 = BLOCKS.register("solar_panel_3",
            () -> new SolarPanelBlock(SolarPanelBlock.Tier.TIER_3));
    public static final RegistryObject<Item> SOLAR_PANEL_ITEM_3 = createSolarPanelBlockItem("solar_panel_3", SOLAR_PANEL_3);

    public static final RegistryObject<SolarPanelBlock> SOLAR_PANEL_4 = BLOCKS.register("solar_panel_4",
            () -> new SolarPanelBlock(SolarPanelBlock.Tier.TIER_4));
    public static final RegistryObject<Item> SOLAR_PANEL_ITEM_4 = createSolarPanelBlockItem("solar_panel_4", SOLAR_PANEL_4);

    public static final RegistryObject<SolarPanelBlock> SOLAR_PANEL_5 = BLOCKS.register("solar_panel_5",
            () -> new SolarPanelBlock(SolarPanelBlock.Tier.TIER_5));
    public static final RegistryObject<Item> SOLAR_PANEL_ITEM_5 = createSolarPanelBlockItem("solar_panel_5", SOLAR_PANEL_5);

    public static final RegistryObject<Block> COAL_ENGINE = BLOCKS.register("coal_engine",
            () -> new CoalEngineBlock(BlockBehaviour.Properties.of(Material.METAL).
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL).
                    lightLevel(CoalEngineBlock.LIGHT_EMISSION)));
    public static final RegistryObject<Item> COAL_ENGINE_ITEM = createBlockItem("coal_engine", COAL_ENGINE);

    public static final RegistryObject<Block> LIGHTNING_GENERATOR = BLOCKS.register("lightning_generator",
            () -> new LightningGeneratorBlock(BlockBehaviour.Properties.of(Material.METAL).
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final RegistryObject<Item> LIGHTNING_GENERATOR_ITEM = ModItems.ITEMS.register("lightning_generator",
            () -> new LightningGeneratorBlock.Item(LIGHTNING_GENERATOR.get(), new Item.Properties()));

    public static final RegistryObject<Block> ENERGIZER = BLOCKS.register("energizer",
            () -> new EnergizerBlock(BlockBehaviour.Properties.of(Material.METAL).
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final RegistryObject<Item> ENERGIZER_ITEM = createBlockItem("energizer", ENERGIZER);

    public static final RegistryObject<Block> BASIC_MACHINE_FRAME = BLOCKS.register("basic_machine_frame",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL).
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final RegistryObject<Item> BASIC_MACHINE_FRAME_ITEM = createBlockItem("basic_machine_frame", BASIC_MACHINE_FRAME);

    public static final RegistryObject<Block> ADVANCED_MACHINE_FRAME = BLOCKS.register("advanced_machine_frame",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL).
                    requiresCorrectToolForDrops().strength(5.0f, 6.0f).sound(SoundType.METAL)));
    public static final RegistryObject<Item> ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("advanced_machine_frame", ADVANCED_MACHINE_FRAME);

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
