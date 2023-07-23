package me.jddev0.ep.block;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    private ModBlocks() {}

    public static final Block SILICON_BLOCK = registerBlock("silicon_block",
            new Block(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item SILICON_BLOCK_ITEM = createBlockItem("silicon_block", SILICON_BLOCK);

    public static final Block SAWDUST_BLOCK = registerBlock("sawdust_block",
            new Block(FabricBlockSettings.create().mapColor(MapColor.OAK_TAN).
                    requiresTool().strength(2.0f, 3.0f).sounds(BlockSoundGroup.WOOD)));
    public static final Item SAWDUST_BLOCK_ITEM = createBlockItem("sawdust_block", SAWDUST_BLOCK);


    private static Item createCableBlockItem(String name, CableBlock block) {
        return Registry.register(Registries.ITEM, new Identifier(EnergizedPowerMod.MODID, name),
                new CableBlock.Item(block, new FabricItemSettings(), block.getTier()));
    }
    public static final CableBlock COPPER_CABLE = registerBlock("copper_cable",
            new CableBlock(CableBlock.Tier.TIER_COPPER));
    public static final Item COPPER_CABLE_ITEM = createCableBlockItem("copper_cable", COPPER_CABLE);

    public static final CableBlock GOLD_CABLE = registerBlock("gold_cable",
            new CableBlock(CableBlock.Tier.TIER_GOLD));
    public static final Item GOLD_CABLE_ITEM = createCableBlockItem("gold_cable", GOLD_CABLE);

    public static final CableBlock ENERGIZED_COPPER_CABLE = registerBlock("energized_copper_cable",
            new CableBlock(CableBlock.Tier.TIER_ENERGIZED_COPPER));
    public static final Item ENERGIZED_COPPER_CABLE_ITEM = createCableBlockItem("energized_copper_cable",
            ENERGIZED_COPPER_CABLE);

    public static final CableBlock ENERGIZED_GOLD_CABLE = registerBlock("energized_gold_cable",
            new CableBlock(CableBlock.Tier.TIER_ENERGIZED_GOLD));
    public static final Item ENERGIZED_GOLD_CABLE_ITEM = createCableBlockItem("energized_gold_cable",
            ENERGIZED_GOLD_CABLE);

    private static Item createTransformerBlockItem(String name, TransformerBlock block) {
        return Registry.register(Registries.ITEM, new Identifier(EnergizedPowerMod.MODID, name),
                new TransformerBlock.Item(block, new FabricItemSettings(), block.getTransformerType()));
    }
    public static final TransformerBlock TRANSFORMER_1_TO_N = registerBlock("transformer_1_to_n",
            new TransformerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerBlock.Type.TYPE_1_TO_N));
    public static final Item TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("transformer_1_to_n",
            TRANSFORMER_1_TO_N);

    public static final TransformerBlock TRANSFORMER_3_TO_3 = registerBlock("transformer_3_to_3",
            new TransformerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerBlock.Type.TYPE_3_TO_3));
    public static final Item TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("transformer_3_to_3",
            TRANSFORMER_3_TO_3);

    public static final TransformerBlock TRANSFORMER_N_TO_1 = registerBlock("transformer_n_to_1",
            new TransformerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerBlock.Type.TYPE_N_TO_1));
    public static final Item TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("transformer_n_to_1",
            TRANSFORMER_N_TO_1);

    public static final Block AUTO_CRAFTER = registerBlock("auto_crafter",
            new AutoCrafterBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item AUTO_CRAFTER_ITEM = createBlockItem("auto_crafter",
            new AutoCrafterBlock.Item(AUTO_CRAFTER, new FabricItemSettings()));

    public static final Block BATTERY_BOX = registerBlock("battery_box",
            new BatteryBoxBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item BATTERY_BOX_ITEM = createBlockItem("battery_box",
            new BatteryBoxBlock.Item(BATTERY_BOX, new FabricItemSettings()));

    public static final Block ADVANCED_BATTERY_BOX = registerBlock("advanced_battery_box",
            new AdvancedBatteryBoxBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_BATTERY_BOX_ITEM = createBlockItem("advanced_battery_box",
            new AdvancedBatteryBoxBlock.Item(ADVANCED_BATTERY_BOX, new FabricItemSettings()));

    public static final Block CRUSHER = registerBlock("crusher",
            new CrusherBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item CRUSHER_ITEM = createBlockItem("crusher", CRUSHER);

    public static final Block SAWMILL = registerBlock("sawmill",
            new SawmillBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item SAWMILL_ITEM = createBlockItem("sawmill", SAWMILL);

    public static final Block COMPRESSOR = registerBlock("compressor",
            new CompressorBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item COMPRESSOR_ITEM = createBlockItem("compressor", COMPRESSOR);

    public static final Block PLANT_GROWTH_CHAMBER = registerBlock("plant_growth_chamber",
            new PlantGrowthChamberBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item PLANT_GROWTH_CHAMBER_ITEM = createBlockItem("plant_growth_chamber", PLANT_GROWTH_CHAMBER);

    public static final Block BLOCK_PLACER = registerBlock("block_placer",
            new BlockPlacerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item BLOCK_PLACER_ITEM = createBlockItem("block_placer", BLOCK_PLACER);

    public static final Block FLUID_FILLER = registerBlock("fluid_filler",
            new FluidFillerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item FLUID_FILLER_ITEM = createBlockItem("fluid_filler", FLUID_FILLER);

    public static final Block FLUID_DRAINER = registerBlock("fluid_drainer",
            new FluidDrainerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item FLUID_DRAINER_ITEM = createBlockItem("fluid_drainer", FLUID_DRAINER);

    public static final Block CHARGER = registerBlock("charger",
            new ChargerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item CHARGER_ITEM = createBlockItem("charger",
            new ChargerBlock.Item(CHARGER, new FabricItemSettings()));

    public static final Block UNCHARGER = registerBlock("uncharger",
            new UnchargerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item UNCHARGER_ITEM = createBlockItem("uncharger", UNCHARGER);

    public static final Block MINECART_CHARGER = registerBlock("minecart_charger",
            new MinecartChargerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item MINECART_CHARGER_ITEM = createBlockItem("minecart_charger", MINECART_CHARGER);

    public static final Block MINECART_UNCHARGER = registerBlock("minecart_uncharger",
            new MinecartUnchargerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item MINECART_UNCHARGER_ITEM = createBlockItem("minecart_uncharger", MINECART_UNCHARGER);


    private static Item createSolarPanelBlockItem(String name, SolarPanelBlock block) {
        return Registry.register(Registries.ITEM, new Identifier(EnergizedPowerMod.MODID, name),
                new SolarPanelBlock.Item(block, new FabricItemSettings(), block.getTier()));
    }
    public static final SolarPanelBlock SOLAR_PANEL_1 = registerBlock("solar_panel_1",
            new SolarPanelBlock(SolarPanelBlock.Tier.TIER_1));
    public static final Item SOLAR_PANEL_ITEM_1 = createSolarPanelBlockItem("solar_panel_1", SOLAR_PANEL_1);

    public static final SolarPanelBlock SOLAR_PANEL_2 = registerBlock("solar_panel_2",
            new SolarPanelBlock(SolarPanelBlock.Tier.TIER_2));
    public static final Item SOLAR_PANEL_ITEM_2 = createSolarPanelBlockItem("solar_panel_2", SOLAR_PANEL_2);

    public static final SolarPanelBlock SOLAR_PANEL_3 = registerBlock("solar_panel_3",
            new SolarPanelBlock(SolarPanelBlock.Tier.TIER_3));
    public static final Item SOLAR_PANEL_ITEM_3 = createSolarPanelBlockItem("solar_panel_3", SOLAR_PANEL_3);

    public static final SolarPanelBlock SOLAR_PANEL_4 = registerBlock("solar_panel_4",
            new SolarPanelBlock(SolarPanelBlock.Tier.TIER_4));
    public static final Item SOLAR_PANEL_ITEM_4 = createSolarPanelBlockItem("solar_panel_4", SOLAR_PANEL_4);

    public static final SolarPanelBlock SOLAR_PANEL_5 = registerBlock("solar_panel_5",
            new SolarPanelBlock(SolarPanelBlock.Tier.TIER_5));
    public static final Item SOLAR_PANEL_ITEM_5 = createSolarPanelBlockItem("solar_panel_5", SOLAR_PANEL_5);

    public static final Block COAL_ENGINE = registerBlock("coal_engine",
            new CoalEngineBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(CoalEngineBlock.LIGHT_EMISSION)));
    public static final Item COAL_ENGINE_ITEM = createBlockItem("coal_engine", COAL_ENGINE);

    public static final Block HEAT_GENERATOR = registerBlock("heat_generator",
            new HeatGeneratorBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item HEAT_GENERATOR_ITEM = createBlockItem("heat_generator", HEAT_GENERATOR);

    public static final Block THERMAL_GENERATOR = registerBlock("thermal_generator",
            new ThermalGeneratorBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item THERMAL_GENERATOR_ITEM = createBlockItem("thermal_generator", THERMAL_GENERATOR);

    public static final Block POWERED_FURNACE = registerBlock("powered_furnace",
            new PoweredFurnaceBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(PoweredFurnaceBlock.LIGHT_EMISSION)));
    public static final Item POWERED_FURNACE_ITEM = createBlockItem("powered_furnace", POWERED_FURNACE);

    public static final Block LIGHTNING_GENERATOR = registerBlock("lightning_generator",
            new LightningGeneratorBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(LightningGeneratorBlock.LIGHT_EMISSION)));
    public static final Item LIGHTNING_GENERATOR_ITEM = createBlockItem("lightning_generator",
            new LightningGeneratorBlock.Item(LIGHTNING_GENERATOR, new FabricItemSettings()));

    public static final Block ENERGIZER = registerBlock("energizer",
            new EnergizerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(EnergizerBlock.LIGHT_EMISSION)));
    public static final Item ENERGIZER_ITEM = createBlockItem("energizer", ENERGIZER);

    public static final Block CHARGING_STATION = registerBlock("charging_station",
            new ChargingStationBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(ChargingStationBlock.LIGHT_EMISSION)));
    public static final Item CHARGING_STATION_ITEM = createBlockItem("charging_station",
            new ChargingStationBlock.Item(CHARGING_STATION, new FabricItemSettings()));

    public static final Block WEATHER_CONTROLLER = registerBlock("weather_controller",
            new WeatherControllerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item WEATHER_CONTROLLER_ITEM = createBlockItem("weather_controller", WEATHER_CONTROLLER);

    public static final Block TIME_CONTROLLER = registerBlock("time_controller",
            new TimeControllerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item TIME_CONTROLLER_ITEM = createBlockItem("time_controller", TIME_CONTROLLER);

    public static final Block BASIC_MACHINE_FRAME = registerBlock("basic_machine_frame",
            new Block(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item BASIC_MACHINE_FRAME_ITEM = createBlockItem("basic_machine_frame", BASIC_MACHINE_FRAME);

    public static final Block ADVANCED_MACHINE_FRAME = registerBlock("advanced_machine_frame",
            new Block(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("advanced_machine_frame", ADVANCED_MACHINE_FRAME);

    private static <T extends Block> T registerBlock(String name, T block) {
        return Registry.register(Registries.BLOCK, new Identifier(EnergizedPowerMod.MODID, name), block);
    }

    private static Item createBlockItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(EnergizedPowerMod.MODID, name), item);
    }

    private static Item createBlockItem(String name, Block block, FabricItemSettings props) {
        return Registry.register(Registries.ITEM, new Identifier(EnergizedPowerMod.MODID, name),
                new BlockItem(block, props));
    }

    private static Item createBlockItem(String name, Block block) {
        return createBlockItem(name, block, new FabricItemSettings());
    }

    public static void register() {

    }
}
