package me.jddev0.ep.block;

import me.jddev0.ep.EnergizedPowerMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
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

    public static final Block ITEM_CONVEYOR_BELT = registerBlock("item_conveyor_belt",
            new ItemConveyorBeltBlock(FabricBlockSettings.create().noCollision().
                    strength(2.5f, 3.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ITEM_CONVEYOR_BELT_ITEM = createBlockItem("item_conveyor_belt",
            new ItemConveyorBeltBlock.Item(ITEM_CONVEYOR_BELT, new FabricItemSettings()));

    public static final Block ITEM_CONVEYOR_BELT_LOADER = registerBlock("item_conveyor_belt_loader",
            new ItemConveyorBeltLoaderBlock(FabricBlockSettings.create().mapColor(MapColor.STONE_GRAY).
                    instrument(Instrument.BASEDRUM).requiresTool().strength(3.5f).sounds(BlockSoundGroup.STONE)));
    public static final Item ITEM_CONVEYOR_BELT_LOADER_ITEM = createBlockItem("item_conveyor_belt_loader", ITEM_CONVEYOR_BELT_LOADER);

    public static final FluidPipeBlock FLUID_PIPE = registerBlock("fluid_pipe",
            new FluidPipeBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item FLUID_PIPE_ITEM = createBlockItem("fluid_pipe",
            new FluidPipeBlock.Item(FLUID_PIPE, new FabricItemSettings()));


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

    public static final CableBlock ENERGIZED_CRYSTAL_MATRIX_CABLE = registerBlock("energized_crystal_matrix_cable",
            new CableBlock(CableBlock.Tier.TIER_ENERGIZED_CRYSTAL_MATRIX));
    public static final Item ENERGIZED_CRYSTAL_MATRIX_CABLE_ITEM = createCableBlockItem("energized_crystal_matrix_cable",
            ENERGIZED_CRYSTAL_MATRIX_CABLE);

    private static Item createTransformerBlockItem(String name, TransformerBlock block) {
        return Registry.register(Registries.ITEM, new Identifier(EnergizedPowerMod.MODID, name),
                new TransformerBlock.Item(block, new FabricItemSettings(), block.getTier(), block.getTransformerType()));
    }
    public static final TransformerBlock MV_TRANSFORMER_1_TO_N = registerBlock("transformer_1_to_n",
            new TransformerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerBlock.Tier.TIER_MV, TransformerBlock.Type.TYPE_1_TO_N));
    public static final Item MV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("transformer_1_to_n",
            MV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock MV_TRANSFORMER_3_TO_3 = registerBlock("transformer_3_to_3",
            new TransformerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerBlock.Tier.TIER_MV, TransformerBlock.Type.TYPE_3_TO_3));
    public static final Item MV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("transformer_3_to_3",
            MV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock MV_TRANSFORMER_N_TO_1 = registerBlock("transformer_n_to_1",
            new TransformerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerBlock.Tier.TIER_MV, TransformerBlock.Type.TYPE_N_TO_1));
    public static final Item MV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("transformer_n_to_1",
            MV_TRANSFORMER_N_TO_1);

    public static final TransformerBlock HV_TRANSFORMER_1_TO_N = registerBlock("hv_transformer_1_to_n",
            new TransformerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerBlock.Tier.TIER_HV, TransformerBlock.Type.TYPE_1_TO_N));
    public static final Item HV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("hv_transformer_1_to_n",
            HV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock HV_TRANSFORMER_3_TO_3 = registerBlock("hv_transformer_3_to_3",
            new TransformerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerBlock.Tier.TIER_HV, TransformerBlock.Type.TYPE_3_TO_3));
    public static final Item HV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("hv_transformer_3_to_3",
            HV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock HV_TRANSFORMER_N_TO_1 = registerBlock("hv_transformer_n_to_1",
            new TransformerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerBlock.Tier.TIER_HV, TransformerBlock.Type.TYPE_N_TO_1));
    public static final Item HV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("hv_transformer_n_to_1",
            HV_TRANSFORMER_N_TO_1);

    public static final TransformerBlock EHV_TRANSFORMER_1_TO_N = registerBlock("ehv_transformer_1_to_n",
            new TransformerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerBlock.Tier.TIER_EHV, TransformerBlock.Type.TYPE_1_TO_N));
    public static final Item EHV_TRANSFORMER_1_TO_N_ITEM = createTransformerBlockItem("ehv_transformer_1_to_n",
            EHV_TRANSFORMER_1_TO_N);

    public static final TransformerBlock EHV_TRANSFORMER_3_TO_3 = registerBlock("ehv_transformer_3_to_3",
            new TransformerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerBlock.Tier.TIER_EHV, TransformerBlock.Type.TYPE_3_TO_3));
    public static final Item EHV_TRANSFORMER_3_TO_3_ITEM = createTransformerBlockItem("ehv_transformer_3_to_3",
            EHV_TRANSFORMER_3_TO_3);

    public static final TransformerBlock EHV_TRANSFORMER_N_TO_1 = registerBlock("ehv_transformer_n_to_1",
            new TransformerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL),
                    TransformerBlock.Tier.TIER_EHV, TransformerBlock.Type.TYPE_N_TO_1));
    public static final Item EHV_TRANSFORMER_N_TO_1_ITEM = createTransformerBlockItem("ehv_transformer_n_to_1",
            EHV_TRANSFORMER_N_TO_1);

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

    public static final Block CREATIVE_BATTERY_BOX = registerBlock("creative_battery_box",
            new CreativeBatteryBoxBlock(FabricBlockSettings.create().mapColor(MapColor.PURPLE).
                    requiresTool().strength(-1.f, 3600000.f).dropsNothing()));
    public static final Item CREATIVE_BATTERY_BOX_ITEM = createBlockItem("creative_battery_box",
            new CreativeBatteryBoxBlock.Item(CREATIVE_BATTERY_BOX, new FabricItemSettings()));

    public static final Block CRUSHER = registerBlock("crusher",
            new CrusherBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item CRUSHER_ITEM = createBlockItem("crusher", CRUSHER);

    public static final Block PULVERIZER = registerBlock("pulverizer",
            new PulverizerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item PULVERIZER_ITEM = createBlockItem("pulverizer", PULVERIZER);

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

    public static final Block ADVANCED_CHARGER = registerBlock("advanced_charger",
            new AdvancedChargerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_CHARGER_ITEM = createBlockItem("advanced_charger",
            new AdvancedChargerBlock.Item(ADVANCED_CHARGER, new FabricItemSettings()));

    public static final Block UNCHARGER = registerBlock("uncharger",
            new UnchargerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item UNCHARGER_ITEM = createBlockItem("uncharger", UNCHARGER);

    public static final Block ADVANCED_UNCHARGER = registerBlock("advanced_uncharger",
            new AdvancedUnchargerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_UNCHARGER_ITEM = createBlockItem("advanced_uncharger", ADVANCED_UNCHARGER);

    public static final Block MINECART_CHARGER = registerBlock("minecart_charger",
            new MinecartChargerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item MINECART_CHARGER_ITEM = createBlockItem("minecart_charger",
            new MinecartChargerBlock.Item(MINECART_CHARGER, new FabricItemSettings()));

    public static final Block ADVANCED_MINECART_CHARGER = registerBlock("advanced_minecart_charger",
            new AdvancedMinecartChargerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_MINECART_CHARGER_ITEM = createBlockItem("advanced_minecart_charger",
            new AdvancedMinecartChargerBlock.Item(ADVANCED_MINECART_CHARGER, new FabricItemSettings()));

    public static final Block MINECART_UNCHARGER = registerBlock("minecart_uncharger",
            new MinecartUnchargerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item MINECART_UNCHARGER_ITEM = createBlockItem("minecart_uncharger",
            new MinecartUnchargerBlock.Item(MINECART_UNCHARGER, new FabricItemSettings()));

    public static final Block ADVANCED_MINECART_UNCHARGER = registerBlock("advanced_minecart_uncharger",
            new AdvancedMinecartUnchargerBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item ADVANCED_MINECART_UNCHARGER_ITEM = createBlockItem("advanced_minecart_uncharger",
            new AdvancedMinecartUnchargerBlock.Item(ADVANCED_MINECART_UNCHARGER, new FabricItemSettings()));


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

    public static final Block POWERED_LAMP = registerBlock("powered_lamp",
            new PoweredLampBlock(FabricBlockSettings.create().
                    strength(.3f).sounds(BlockSoundGroup.GLASS).
                    luminance(PoweredLampBlock.LIGHT_EMISSION)));
    public static final Item POWERED_LAMP_ITEM = createBlockItem("powered_lamp", POWERED_LAMP);

    public static final Block POWERED_FURNACE = registerBlock("powered_furnace",
            new PoweredFurnaceBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(PoweredFurnaceBlock.LIGHT_EMISSION)));
    public static final Item POWERED_FURNACE_ITEM = createBlockItem("powered_furnace", POWERED_FURNACE);

    public static final Block ADVANCED_POWERED_FURNACE = registerBlock("advanced_powered_furnace",
            new AdvancedPoweredFurnaceBlock(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL).
                    luminance(AdvancedPoweredFurnaceBlock.LIGHT_EMISSION)));
    public static final Item ADVANCED_POWERED_FURNACE_ITEM = createBlockItem("advanced_powered_furnace", ADVANCED_POWERED_FURNACE);

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

    public static final Block REINFORCED_ADVANCED_MACHINE_FRAME = registerBlock("reinforced_advanced_machine_frame",
            new Block(FabricBlockSettings.create().
                    requiresTool().strength(5.0f, 6.0f).sounds(BlockSoundGroup.METAL)));
    public static final Item REINFORCED_ADVANCED_MACHINE_FRAME_ITEM = createBlockItem("reinforced_advanced_machine_frame", REINFORCED_ADVANCED_MACHINE_FRAME);

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
