package me.jddev0.ep.config;

import com.mojang.logging.LogUtils;
import me.jddev0.ep.config.value.LongConfigValue;
import net.fabricmc.loader.api.FabricLoader;
import me.jddev0.ep.config.value.IntegerConfigValue;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

public final class ModConfigs {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ModConfigs() {}

    public static final Config COMMON_CONFIG = new Config(getRelativeConfigFile("common.conf"), "Energized Power Common Config");

    //Items
    public static final ConfigValue<Long> COMMON_BATTERY_1_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_1", "Battery (Tier I)", 256
    );
    public static final ConfigValue<Long> COMMON_BATTERY_1_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_1", "Battery (Tier I)", 2
    );

    public static final ConfigValue<Long> COMMON_BATTERY_2_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_2", "Battery (Tier II)", 1024
    );
    public static final ConfigValue<Long> COMMON_BATTERY_2_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_2", "Battery (Tier II)", 8
    );

    public static final ConfigValue<Long> COMMON_BATTERY_3_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_3", "Battery (Tier III)", 2048
    );
    public static final ConfigValue<Long> COMMON_BATTERY_3_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_3", "Battery (Tier III)", 32
    );

    public static final ConfigValue<Long> COMMON_BATTERY_4_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_4", "Battery (Tier IV)", 8192
    );
    public static final ConfigValue<Long> COMMON_BATTERY_4_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_4", "Battery (Tier IV)", 128
    );

    public static final ConfigValue<Long> COMMON_BATTERY_5_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_5", "Battery (Tier V)", 16384
    );
    public static final ConfigValue<Long> COMMON_BATTERY_5_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_5", "Battery (Tier V)", 512
    );

    public static final ConfigValue<Long> COMMON_BATTERY_6_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_6", "Battery (Tier VI)", 65536
    );
    public static final ConfigValue<Long> COMMON_BATTERY_6_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_6", "Battery (Tier VI)", 2048
    );

    public static final ConfigValue<Long> COMMON_BATTERY_7_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_7", "Battery (Tier VII)", 262144
    );
    public static final ConfigValue<Long> COMMON_BATTERY_7_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_7", "Battery (Tier VII)", 8192
    );

    public static final ConfigValue<Long> COMMON_BATTERY_8_CAPACITY = registerEnergyCapacityConfigValue(
            "item.battery_8", "Battery (Tier VIII)", 1048576
    );
    public static final ConfigValue<Long> COMMON_BATTERY_8_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.battery_8", "Battery (Tier VIII)", 32768
    );

    public static final ConfigValue<Long> COMMON_ENERGY_ANALYZER_CAPACITY = registerEnergyCapacityConfigValue(
            "item.energy_analyzer", "Energy Analyzer", 2048
    );
    public static final ConfigValue<Long> COMMON_ENERGY_ANALYZER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.energy_analyzer", "Energy Analyzer", 32
    );
    public static final ConfigValue<Long> COMMON_ENERGY_ANALYZER_ENERGY_CONSUMPTION_PER_USE = registerEnergyConsumptionPerUseConfigValue(
            "item.energy_analyzer", "Energy Analyzer", 8
    );

    public static final ConfigValue<Long> COMMON_FLUID_ANALYZER_CAPACITY = registerEnergyCapacityConfigValue(
            "item.fluid_analyzer", "Fluid Analyzer", 2048
    );
    public static final ConfigValue<Long> COMMON_FLUID_ANALYZER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.fluid_analyzer", "Fluid Analyzer", 32
    );
    public static final ConfigValue<Long> COMMON_FLUID_ANALYZER_ENERGY_CONSUMPTION_PER_USE = registerEnergyConsumptionPerUseConfigValue(
            "item.fluid_analyzer", "Fluid Analyzer", 8
    );

    //Blocks
    public static final ConfigValue<Long> COMMON_BATTERY_BOX_CAPACITY = registerEnergyCapacityConfigValue(
            "block.battery_box", "Battery Box", 65536
    );
    public static final ConfigValue<Long> COMMON_BATTERY_BOX_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.battery_box", "Battery Box", 2048
    );

    public static final ConfigValue<Long> COMMON_ADVANCED_BATTERY_BOX_CAPACITY = registerEnergyCapacityConfigValue(
            "block.advanced_battery_box", "Advanced Battery Box", 8388608
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_BATTERY_BOX_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.advanced_battery_box", "Advanced Battery Box", 262144
    );

    public static final ConfigValue<Long> COMMON_MV_TRANSFORMERS_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.mv_transformers", "MV Transformers", 65536
    );

    public static final ConfigValue<Long> COMMON_HV_TRANSFORMERS_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.hv_transformers", "HV Transformers", 524288
    );

    public static final ConfigValue<Long> COMMON_EHV_TRANSFORMERS_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.ehv_transformers", "EHV Transformers", 2097152
    );

    //Entities
    public static final ConfigValue<Long> COMMON_BATTERY_BOX_MINECART_CAPACITY = registerEnergyCapacityConfigValue(
            "entity.battery_box_minecart", "Battery Box Minecart", 65536
    );
    public static final ConfigValue<Long> COMMON_BATTERY_BOX_MINECART_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "entity.battery_box_minecart", "Battery Box Minecart", 512
    );

    public static final ConfigValue<Long> COMMON_ADVANCED_BATTERY_BOX_MINECART_CAPACITY = registerEnergyCapacityConfigValue(
            "entity.advanced_battery_box_minecart", "Advanced Battery Box Minecart", 8388608
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_BATTERY_BOX_MINECART_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "entity.advanced_battery_box_minecart", "Advanced Battery Box Minecart", 65536
    );


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
        return FabricLoader.getInstance().getConfigDir().resolve("energizedpower/" + fileName).toFile();
    }

    private static ConfigValue<Long> registerEnergyCapacityConfigValue(String baseConfigKey, String itemName, long defaultValue) {
        return COMMON_CONFIG.register(new LongConfigValue(
                baseConfigKey + ".capacity",
                "The energy capacity of " + itemName + " in E",
                defaultValue,
                1L, null
        ));
    }
    private static ConfigValue<Long> registerEnergyTransferRateConfigValue(String baseConfigKey, String itemName, long defaultValue) {
        return COMMON_CONFIG.register(new LongConfigValue(
                baseConfigKey + ".transfer_rate",
                "The energy transfer rate of " + itemName + " in E per tick",
                defaultValue,
                1L, null
        ));
    }
    private static ConfigValue<Long> registerEnergyConsumptionPerUseConfigValue(String baseConfigKey, String itemName, long defaultValue) {
        return COMMON_CONFIG.register(new LongConfigValue(
                baseConfigKey + ".energy_requirement_per_use",
                "The energy consumption of " + itemName + " in E per use",
                defaultValue,
                1L, null
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
