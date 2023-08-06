package me.jddev0.ep.config;

import com.mojang.logging.LogUtils;
import me.jddev0.ep.config.value.IntegerConfigValue;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

public final class ModConfigs {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ModConfigs() {}

    public static final Config COMMON_CONFIG = new Config(getRelativeConfigFile("common.conf"), "Energized Power Common Config");

    //Items
    public static final ConfigValue<Integer> COMMON_BATTERY_1_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_1", "Battery (Tier I)", 256
    );
    public static final ConfigValue<Integer> COMMON_BATTERY_1_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_1", "Battery (Tier I)", 2
    );

    public static final ConfigValue<Integer> COMMON_BATTERY_2_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_2", "Battery (Tier II)", 1024
    );
    public static final ConfigValue<Integer> COMMON_BATTERY_2_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_2", "Battery (Tier II)", 8
    );

    public static final ConfigValue<Integer> COMMON_BATTERY_3_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_3", "Battery (Tier III)", 2048
    );
    public static final ConfigValue<Integer> COMMON_BATTERY_3_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_3", "Battery (Tier III)", 32
    );

    public static final ConfigValue<Integer> COMMON_BATTERY_4_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_4", "Battery (Tier IV)", 8192
    );
    public static final ConfigValue<Integer> COMMON_BATTERY_4_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_4", "Battery (Tier IV)", 128
    );

    public static final ConfigValue<Integer> COMMON_BATTERY_5_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_5", "Battery (Tier V)", 16384
    );
    public static final ConfigValue<Integer> COMMON_BATTERY_5_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_5", "Battery (Tier V)", 512
    );

    public static final ConfigValue<Integer> COMMON_BATTERY_6_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_6", "Battery (Tier VI)", 65536
    );
    public static final ConfigValue<Integer> COMMON_BATTERY_6_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_6", "Battery (Tier VI)", 2048
    );

    public static final ConfigValue<Integer> COMMON_BATTERY_7_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_7", "Battery (Tier VII)", 262144
    );
    public static final ConfigValue<Integer> COMMON_BATTERY_7_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_7", "Battery (Tier VII)", 8192
    );

    public static final ConfigValue<Integer> COMMON_BATTERY_8_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_8", "Battery (Tier VIII)", 1048576
    );
    public static final ConfigValue<Integer> COMMON_BATTERY_8_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_8", "Battery (Tier VIII)", 32768
    );

    public static final ConfigValue<Integer> COMMON_ENERGY_ANALYZER_CAPACITY = registerEnergyCapacityConfigValue(
            "item.energy_analyzer", "Energy Analyzer", 2048
    );
    public static final ConfigValue<Integer> COMMON_ENERGY_ANALYZER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.energy_analyzer", "Energy Analyzer", 32
    );
    public static final ConfigValue<Integer> COMMON_ENERGY_ANALYZER_ENERGY_CONSUMPTION_PER_USE = registerEnergyConsumptionPerUseConfigValue(
            "item.energy_analyzer", "Energy Analyzer", 8
    );

    public static final ConfigValue<Integer> COMMON_FLUID_ANALYZER_CAPACITY = registerEnergyCapacityConfigValue(
            "item.fluid_analyzer", "Fluid Analyzer", 2048
    );
    public static final ConfigValue<Integer> COMMON_FLUID_ANALYZER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.fluid_analyzer", "Fluid Analyzer", 32
    );
    public static final ConfigValue<Integer> COMMON_FLUID_ANALYZER_ENERGY_CONSUMPTION_PER_USE = registerEnergyConsumptionPerUseConfigValue(
            "item.fluid_analyzer", "Fluid Analyzer", 8
    );

    //Blocks
    public static final ConfigValue<Integer> COMMON_BATTERY_BOX_CAPACITY = registerEnergyCapacityConfigValue(
            "block.battery_box", "Battery Box", 65536
    );
    public static final ConfigValue<Integer> COMMON_BATTERY_BOX_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.battery_box", "Battery Box", 2048
    );

    public static final ConfigValue<Integer> COMMON_ADVANCED_BATTERY_BOX_CAPACITY = registerEnergyCapacityConfigValue(
            "block.advanced_battery_box", "Advanced Battery Box", 8388608
    );
    public static final ConfigValue<Integer> COMMON_ADVANCED_BATTERY_BOX_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.advanced_battery_box", "Advanced Battery Box", 262144
    );

    public static final ConfigValue<Integer> COMMON_MV_TRANSFORMERS_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.mv_transformers", "MV Transformers", 65536
    );

    public static final ConfigValue<Integer> COMMON_HV_TRANSFORMERS_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.hv_transformers", "HV Transformers", 524288
    );

    public static final ConfigValue<Integer> COMMON_EHV_TRANSFORMERS_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.ehv_transformers", "EHV Transformers", 2097152
    );

    public static final ConfigValue<Integer> COMMON_COPPER_CABLE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.copper_cable", "Copper Cable", 1024
    );

    public static final ConfigValue<Integer> COMMON_GOLD_CABLE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.gold_cable", "Gold Cable", 16384
    );

    public static final ConfigValue<Integer> COMMON_ENERGIZED_COPPER_CABLE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.energized_copper_cable", "Energized Copper Cable", 131072
    );

    public static final ConfigValue<Integer> COMMON_ENERGIZED_GOLD_CABLE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.energized_gold_cable", "Energized Gold Cable", 524288
    );

    public static final ConfigValue<Integer> COMMON_ENERGIZED_CRYSTAL_MATRIX_CABLE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.energized_crystal_matrix_cable", "Energized Crystal Matrix Cable", 2097152
    );

    public static final ConfigValue<Integer> COMMON_WEATHER_CONTROLLER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.weather_controller", "Weather Controller", 8388608
    );
    public static final ConfigValue<Integer> COMMON_WEATHER_CONTROLLER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.weather_controller", "Weather Controller", 32768
    );
    public static final ConfigValue<Integer> COMMON_WEATHER_CONTROLLER_CONTROL_DURATION = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.weather_controller.control_duration",
            "The amount of time in ticks the Weather Controller can maintain the desired weather state",
            20 * 60 * 16 /* 16 minutes */,
            20 * 5 /* 5 seconds */, null
    ));

    public static final ConfigValue<Integer> COMMON_LIGHTNING_GENERATOR_CAPACITY = registerEnergyCapacityConfigValue(
            "block.lightning_generator", "Lightning Generator", 1000000
    );
    public static final ConfigValue<Integer> COMMON_LIGHTNING_GENERATOR_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.lightning_generator", "Lightning Generator", 65536
    );

    public static final ConfigValue<Integer> COMMON_CHARGING_STATION_CAPACITY = registerEnergyCapacityConfigValue(
            "block.charging_station", "Charging Station", 262144
    );
    public static final ConfigValue<Integer> COMMON_CHARGING_STATION_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.charging_station", "Charging Station", 16384
    );
    public static final ConfigValue<Integer> COMMON_CHARGING_STATION_MAX_CHARGING_DISTANCE = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.charging_station.max_charging_distance",
            "The maximal distance the Charging Station can operate within",
            7,
            1, 25
    ));

    public static final ConfigValue<Integer> COMMON_TIME_CONTROLLER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.time_controller", "Time Controller", 8388608
    );
    public static final ConfigValue<Integer> COMMON_TIME_CONTROLLER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.time_controller", "Time Controller", 32768
    );

    //Entities
    public static final ConfigValue<Integer> COMMON_BATTERY_BOX_MINECART_CAPACITY = registerEnergyCapacityConfigValue(
            "entity.battery_box_minecart", "Battery Box Minecart", 65536
    );
    public static final ConfigValue<Integer> COMMON_BATTERY_BOX_MINECART_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "entity.battery_box_minecart", "Battery Box Minecart", 512
    );

    public static final ConfigValue<Integer> COMMON_ADVANCED_BATTERY_BOX_MINECART_CAPACITY = registerEnergyCapacityConfigValue(
            "entity.advanced_battery_box_minecart", "Advanced Battery Box Minecart", 8388608
    );
    public static final ConfigValue<Integer> COMMON_ADVANCED_BATTERY_BOX_MINECART_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "entity.advanced_battery_box_minecart", "Advanced Battery Box Minecart", 65536
    );

    //World
    public static final ConfigValue<Integer> COMMON_ELECTRICIAN_BUILDING_1_PLACEMENT_WEIGHT = COMMON_CONFIG.register(new IntegerConfigValue(
            "world.village.electrician_building_1.placement_weight",
            "Determines how frequent the Electrician Building 1 will be placed in villages. If set to 0 the Electrician Building 1 will never be placed.",
            5,
            0, 100
    ));


    public static final Config SERVER_CONFIG = new Config(getRelativeConfigFile("server.conf"), "Energized Power Server Config");
    //TODO server config values


    public static final Config CLIENT_CONFIG = new Config(getRelativeConfigFile("client.conf"), "Energized Power Client Config");

    public static final ConfigValue<Integer> CLIENT_ENERGIZED_POWER_BOOK_IMAGE_CYCLE_DELAY = CLIENT_CONFIG.register(
            new IntegerConfigValue(
                    "energized_power_book.image_cycle_delay",
                    "The tick amount to wait between two images in the Energized Power Book",
                    50,

                    5 /* 250 ms */, 1200 /* 1 minute */
            )
    );

    private static File getRelativeConfigFile(String fileName) {
        return FMLPaths.CONFIGDIR.get().resolve("energizedpower/" + fileName).toFile();
    }

    private static ConfigValue<Integer> registerEnergyCapacityConfigValue(String baseConfigKey, String itemName, int defaultValue) {
        return COMMON_CONFIG.register(new IntegerConfigValue(
                baseConfigKey + ".capacity",
                "The energy capacity of " + itemName + " in FE",
                defaultValue,
                1, null
        ));
    }
    private static ConfigValue<Integer> registerEnergyTransferRateConfigValue(String baseConfigKey, String itemName, int defaultValue) {
        return COMMON_CONFIG.register(new IntegerConfigValue(
                baseConfigKey + ".transfer_rate",
                "The energy transfer rate of " + itemName + " in FE per tick",
                defaultValue,
                1, null
        ));
    }
    private static ConfigValue<Integer> registerEnergyConsumptionPerUseConfigValue(String baseConfigKey, String itemName, int defaultValue) {
        return COMMON_CONFIG.register(new IntegerConfigValue(
                baseConfigKey + ".energy_consumption_per_use",
                "The energy consumption of " + itemName + " in FE per use",
                defaultValue,
                1, null
        ));
    }

    public static void registerConfigs(boolean isServer) {
        if(!COMMON_CONFIG.isLoaded()) {
            try {
                COMMON_CONFIG.read();

                LOGGER.info("Energized Power common config was successfully loaded");
            }catch(IOException|ConfigValidationException e) {
                LOGGER.error("Energized Power common config could not be read", e);
            }
        }

        if(isServer) {
            if(!SERVER_CONFIG.isLoaded()) {
                try {
                    SERVER_CONFIG.read();

                    LOGGER.info("Energized Power server config was successfully loaded");
                }catch(IOException|ConfigValidationException e) {
                    LOGGER.error("Energized Power server config could not be read", e);
                }
            }
        }else {
            if(!CLIENT_CONFIG.isLoaded()) {
                try {
                    CLIENT_CONFIG.read();

                    LOGGER.info("Energized Power client config was successfully loaded");
                }catch(IOException|ConfigValidationException e) {
                    LOGGER.error("Energized Power client config could not be read", e);
                }
            }
        }
    }
}
