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
