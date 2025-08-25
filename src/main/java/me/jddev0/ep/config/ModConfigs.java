package me.jddev0.ep.config;

import com.mojang.logging.LogUtils;
import me.jddev0.ep.config.value.DoubleConfigValue;
import me.jddev0.ep.block.CableBlock;
import me.jddev0.ep.config.validation.ValueValidators;
import me.jddev0.ep.config.value.BooleanConfigValue;
import me.jddev0.ep.config.value.EnumConfigValue;
import me.jddev0.ep.config.value.FloatConfigValue;
import me.jddev0.ep.config.value.LongConfigValue;
import net.fabricmc.loader.api.FabricLoader;
import me.jddev0.ep.config.value.IntegerConfigValue;
import me.jddev0.ep.config.value.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ModConfigs {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ModConfigs() {}

    public static final Config COMMON_CONFIG = new Config(getRelativeConfigFile("common.conf"),
            "Energized Power Common Config (IMPORTANT: Not all values are synced from the server to the client.)");

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

    public static final ConfigValue<Long> COMMON_INVENTORY_COAL_ENGINE_CAPACITY = registerEnergyCapacityConfigValue(
            "item.inventory_coal_engine", "Inventory Coal Engine", 2048
    );
    public static final ConfigValue<Long> COMMON_INVENTORY_COAL_ENGINE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.inventory_coal_engine", "Inventory Coal Engine", 256
    );
    public static final ConfigValue<Double> COMMON_INVENTORY_COAL_ENGINE_ENERGY_PRODUCTION_MULTIPLIER = registerEnergyProductionMultiplierConfigValue(
            "item.inventory_coal_engine", "Inventory Coal Engine"
    );

    public static final ConfigValue<Integer> COMMON_INVENTORY_CHARGER_SLOT_COUNT = COMMON_CONFIG.register(new IntegerConfigValue(
            "item.inventory_charger.slot_count",
            "The slot count of the Inventory Charger.\n" +
                    "WARNING: If set to a lower value than previously, items which are stored in an Inventory Charger could vanish!",
            3,
            ValueValidators.elementOfCollection(List.of(1, 3, 5))
    ));
    public static final ConfigValue<Boolean> COMMON_INVENTORY_CHARGER_TRANSFER_RATE_LIMIT_ENABLED = COMMON_CONFIG.register(new BooleanConfigValue(
            "item.inventory_charger.transfer_rate_limit_enabled",
            "If set to true the transfer rate limit of the Inventory Charger will be enabled",
            false
    ));
    public static final ConfigValue<Long> COMMON_INVENTORY_CHARGER_TRANSFER_RATE_LIMIT = COMMON_CONFIG.register(new LongConfigValue(
            "item.inventory_charger.transfer_rate_limit",
            "The maximal transfer rate of the Inventory Charger in E per tick if the transfer rate limit is enabled",
            16384L,
            1L, null
    ));

    public static final ConfigValue<Long> COMMON_INVENTORY_TELEPORTER_CAPACITY = registerEnergyCapacityConfigValue(
            "item.inventory_teleporter", "Inventory Teleporter", 8388608
    );
    public static final ConfigValue<Long> COMMON_INVENTORY_TELEPORTER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "item.inventory_teleporter", "Inventory Teleporter", 65536
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_SPEED_1_EFFECT = registerSpeedModuleEffectValue(
            1, "I",
            1.2
    );
    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_SPEED_1_ENERGY_CONSUMPTION_EFFECT = registerSpeedModuleEnergyConsumptionEffectValue(
            1, "I",
            1.4
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_SPEED_2_EFFECT = registerSpeedModuleEffectValue(
            2, "II",
            1.5
    );
    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_SPEED_2_ENERGY_CONSUMPTION_EFFECT = registerSpeedModuleEnergyConsumptionEffectValue(
            2, "II",
            2.
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_SPEED_3_EFFECT = registerSpeedModuleEffectValue(
            3, "III",
            2.
    );
    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_SPEED_3_ENERGY_CONSUMPTION_EFFECT = registerSpeedModuleEnergyConsumptionEffectValue(
            3, "III",
            2.5
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_SPEED_4_EFFECT = registerSpeedModuleEffectValue(
            4, "IV",
            3.5
    );
    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_SPEED_4_ENERGY_CONSUMPTION_EFFECT = registerSpeedModuleEnergyConsumptionEffectValue(
            4, "IV",
            5
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_SPEED_5_EFFECT = registerSpeedModuleEffectValue(
            5, "V",
            6
    );
    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_SPEED_5_ENERGY_CONSUMPTION_EFFECT = registerSpeedModuleEnergyConsumptionEffectValue(
            5, "V",
            10
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_EFFICIENCY_1_EFFECT = registerEnergyEfficiencyModuleEffectValue(
            1, "I",
            .9
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_EFFICIENCY_2_EFFECT = registerEnergyEfficiencyModuleEffectValue(
            2, "II",
            .75
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_EFFICIENCY_3_EFFECT = registerEnergyEfficiencyModuleEffectValue(
            3, "III",
            .6
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_EFFICIENCY_4_EFFECT = registerEnergyEfficiencyModuleEffectValue(
            4, "IV",
            .4
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_EFFICIENCY_5_EFFECT = registerEnergyEfficiencyModuleEffectValue(
            8, "V",
            .2
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_1_EFFECT = registerEnergyCapacityModuleEffectValue(
            1, "I",
            1.5
    );
    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_1_ENERGY_TRANSFER_RATE_EFFECT = registerEnergyCapacityModuleEnergyTransferRateEffectValue(
            1, "I",
            1.5
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_2_EFFECT = registerEnergyCapacityModuleEffectValue(
            2, "II",
            2.5
    );
    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_2_ENERGY_TRANSFER_RATE_EFFECT = registerEnergyCapacityModuleEnergyTransferRateEffectValue(
            2, "II",
            2.5
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_3_EFFECT = registerEnergyCapacityModuleEffectValue(
            3, "III",
            4
    );
    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_3_ENERGY_TRANSFER_RATE_EFFECT = registerEnergyCapacityModuleEnergyTransferRateEffectValue(
            3, "III",
            4
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_4_EFFECT = registerEnergyCapacityModuleEffectValue(
            4, "IV",
            7.5
    );
    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_4_ENERGY_TRANSFER_RATE_EFFECT = registerEnergyCapacityModuleEnergyTransferRateEffectValue(
            4, "IV",
            7.5
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_5_EFFECT = registerEnergyCapacityModuleEffectValue(
            5, "V",
            10
    );
    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_ENERGY_CAPACITY_5_ENERGY_TRANSFER_RATE_EFFECT = registerEnergyCapacityModuleEnergyTransferRateEffectValue(
            5, "V",
            10
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_DURATION_1_EFFECT = registerDurationUpgradeModuleEffectValue(
            1, "I",
            4.
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_DURATION_2_EFFECT = registerDurationUpgradeModuleEffectValue(
            2, "II",
            10.
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_DURATION_3_EFFECT = registerDurationUpgradeModuleEffectValue(
            3, "III",
            20.
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_DURATION_4_EFFECT = registerDurationUpgradeModuleEffectValue(
            4, "VI",
            50.
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_DURATION_5_EFFECT = registerDurationUpgradeModuleEffectValue(
            5, "V",
            100.
    );

    public static final ConfigValue<Long> COMMON_UPGRADE_MODULE_DURATION_6_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "item.duration_upgrade_module_6.energy_consumption_per_tick",
            "Duration Upgrade Module (Tier VI)",
            131072
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_DURATION_6_ENERGY_TRANSFER_RATE = COMMON_CONFIG.register(new DoubleConfigValue(
            "item.duration_upgrade_module_6.energy_transfer_rate.effect_value",
            "The upgrade module effect (Energy transfer rate multiplier) of the Duration Upgrade Module (Tier VI)",
            8.,
            1., null
    ));

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_RANGE_1_EFFECT = registerRangeUpgradeModuleEffectValue(
            1, "I",
            1.25
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_RANGE_2_EFFECT = registerRangeUpgradeModuleEffectValue(
            2, "II",
            1.5
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_RANGE_3_EFFECT = registerRangeUpgradeModuleEffectValue(
            3, "III",
            1.75
    );

    public static final ConfigValue<Integer> COMMON_UPGRADE_MODULE_EXTRACTION_DEPTH_1_EFFECT = registerExtractionDepthUpgradeModuleEffectValue(
            1, "I",
            16
    );

    public static final ConfigValue<Integer> COMMON_UPGRADE_MODULE_EXTRACTION_DEPTH_2_EFFECT = registerExtractionDepthUpgradeModuleEffectValue(
            2, "II",
            32
    );

    public static final ConfigValue<Integer> COMMON_UPGRADE_MODULE_EXTRACTION_DEPTH_3_EFFECT = registerExtractionDepthUpgradeModuleEffectValue(
            3, "III",
            64
    );

    public static final ConfigValue<Integer> COMMON_UPGRADE_MODULE_EXTRACTION_DEPTH_4_EFFECT = registerExtractionDepthUpgradeModuleEffectValue(
            4, "IV",
            128
    );

    public static final ConfigValue<Integer> COMMON_UPGRADE_MODULE_EXTRACTION_DEPTH_5_EFFECT = registerExtractionDepthUpgradeModuleEffectValue(
            5, "V",
            192
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_EXTRACTION_RANGE_1_EFFECT = registerExtractionRangeUpgradeModuleEffectValue(
            1, "I",
            1.25
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_EXTRACTION_RANGE_2_EFFECT = registerExtractionRangeUpgradeModuleEffectValue(
            2, "II",
            1.5
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_EXTRACTION_RANGE_3_EFFECT = registerExtractionRangeUpgradeModuleEffectValue(
            3, "III",
            2
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_EXTRACTION_RANGE_4_EFFECT = registerExtractionRangeUpgradeModuleEffectValue(
            4, "IV",
            2.5
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_EXTRACTION_RANGE_5_EFFECT = registerExtractionRangeUpgradeModuleEffectValue(
            5, "V",
            3
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_MOON_LIGHT_1_EFFECT = registerMoonLightUpgradeModuleEffectValue(
            1, "I",
            .125
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_MOON_LIGHT_2_EFFECT = registerMoonLightUpgradeModuleEffectValue(
            2, "II",
            .33
    );

    public static final ConfigValue<Double> COMMON_UPGRADE_MODULE_MOON_LIGHT_3_EFFECT = registerMoonLightUpgradeModuleEffectValue(
            3, "III",
            .66
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

    public static final ConfigValue<Long> COMMON_LV_TRANSFORMERS_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.lv_transformers", "LV Transformers", 8192
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

    public static final ConfigValue<CableBlock.EnergyExtractionMode> COMMON_CABLES_ENERGY_EXTRACTION_MODE = COMMON_CONFIG.register(new EnumConfigValue<>(
            "block.cables.energy_extraction_mode",
            "The energy extraction mode defines how cables extract energy.\n" +
                    "- PUSH: Producers must push energy into cables.\n" +
                    "- PULL: Cables pull energy from producers.\n" +
                    "- BOTH: Both systems are used.\n" +
                    "=> Set to PULL for the behavior of cables before version 2.1.1.\n" +
                    "=> Set to BOTH for compatibility with any E energy producers.",
            CableBlock.EnergyExtractionMode.BOTH,
            CableBlock.EnergyExtractionMode.values(), CableBlock.EnergyExtractionMode::valueOf
    ));

    public static final ConfigValue<Long> COMMON_TIN_CABLE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.tin_cable", "Tin Cable", 128
    );

    public static final ConfigValue<Long> COMMON_COPPER_CABLE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.copper_cable", "Copper Cable", 1024
    );

    public static final ConfigValue<Long> COMMON_GOLD_CABLE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.gold_cable", "Gold Cable", 16384
    );

    public static final ConfigValue<Long> COMMON_ENERGIZED_COPPER_CABLE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.energized_copper_cable", "Energized Copper Cable", 131072
    );

    public static final ConfigValue<Long> COMMON_ENERGIZED_GOLD_CABLE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.energized_gold_cable", "Energized Gold Cable", 524288
    );

    public static final ConfigValue<Long> COMMON_ENERGIZED_CRYSTAL_MATRIX_CABLE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.energized_crystal_matrix_cable", "Energized Crystal Matrix Cable", 2097152
    );

    public static final ConfigValue<Long> COMMON_WEATHER_CONTROLLER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.weather_controller", "Weather Controller", 8388608
    );
    public static final ConfigValue<Long> COMMON_WEATHER_CONTROLLER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.weather_controller", "Weather Controller", 32768
    );
    public static final ConfigValue<Integer> COMMON_WEATHER_CONTROLLER_CONTROL_DURATION = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.weather_controller.control_duration",
            "The amount of time in ticks the Weather Controller can maintain the desired weather state",
            20 * 60 * 16 /* 16 minutes */,
            20 * 5 /* 5 seconds */, null
    ));

    public static final ConfigValue<Long> COMMON_LIGHTNING_GENERATOR_CAPACITY = registerEnergyCapacityConfigValue(
            "block.lightning_generator", "Lightning Generator", 1000000
    );
    public static final ConfigValue<Long> COMMON_LIGHTNING_GENERATOR_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.lightning_generator", "Lightning Generator", 65536
    );

    public static final ConfigValue<Long> COMMON_CHARGING_STATION_CAPACITY = registerEnergyCapacityConfigValue(
            "block.charging_station", "Charging Station", 262144
    );
    public static final ConfigValue<Long> COMMON_CHARGING_STATION_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.charging_station", "Charging Station", 16384
    );
    public static final ConfigValue<Integer> COMMON_CHARGING_STATION_MAX_CHARGING_DISTANCE = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.charging_station.max_charging_distance",
            "The maximal distance the Charging Station can operate within",
            7,
            1, 25
    ));

    public static final ConfigValue<Long> COMMON_CRYSTAL_GROWTH_CHAMBER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.crystal_growth_chamber", "Crystal Growth Chamber", 32768
    );
    public static final ConfigValue<Long> COMMON_CRYSTAL_GROWTH_CHAMBER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.crystal_growth_chamber", "Crystal Growth Chamber", 2048
    );
    public static final ConfigValue<Long> COMMON_CRYSTAL_GROWTH_CHAMBER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.crystal_growth_chamber", "Crystal Growth Chamber", 1024
    );
    public static final ConfigValue<Float> COMMON_CRYSTAL_GROWTH_CHAMBER_RECIPE_DURATION_MULTIPLIER = registerRecipeDurationMultiplierConfigValue(
            "block.crystal_growth_chamber", "Crystal Growth Chamber"
    );

    public static final ConfigValue<Long> COMMON_TIME_CONTROLLER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.time_controller", "Time Controller", 8388608
    );
    public static final ConfigValue<Long> COMMON_TIME_CONTROLLER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.time_controller", "Time Controller", 32768
    );

    public static final ConfigValue<Long> COMMON_TELEPORTER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.teleporter", "Teleporter", 8388608
    );
    public static final ConfigValue<Long> COMMON_TELEPORTER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.teleporter", "Teleporter", 65536
    );
    public static final ConfigValue<Boolean> COMMON_TELEPORTER_INTRA_DIMENSIONAL_ENABLED = COMMON_CONFIG.register(new BooleanConfigValue(
            "block.teleporter.intra_dimensional_enabled",
            "Intra dimensional teleportation (= within a dimension) is not allowed if set to false.",
            true
    ));
    public static final ConfigValue<Boolean> COMMON_TELEPORTER_INTER_DIMENSIONAL_ENABLED = COMMON_CONFIG.register(new BooleanConfigValue(
            "block.teleporter.inter_dimensional_enabled",
            "Inter dimensional teleportation (= across multiple dimension) is not allowed if set to false.",
            true
    ));
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_TELEPORTER_DIMENSION_BLACKLIST = COMMON_CONFIG.register(new IdentifierListConfigValue(
            "block.teleporter.dimension_blacklist",
            "Teleportation within, from, and to dimensions in this list are not allowed.",
            new ArrayList<>(0)
    ));
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_TELEPORTER_INTRA_DIMENSIONAL_BLACKLIST = COMMON_CONFIG.register(new IdentifierListConfigValue(
            "block.teleporter.intra_dimensional_blacklist",
            "Teleportation within dimensions in this list are not allowed.\nTeleportation to and from dimensions in this list are still allowed.",
            new ArrayList<>(0)
    ));
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_TELEPORTER_INTER_DIMENSIONAL_FROM_BLACKLIST = COMMON_CONFIG.register(new IdentifierListConfigValue(
            "block.teleporter.inter_dimension_from_blacklist",
            "Teleportation from dimensions in this list are not allowed.\nTeleportation within and to dimensions in this list are still allowed.",
            new ArrayList<>(0)
    ));
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_TELEPORTER_INTER_DIMENSIONAL_TO_BLACKLIST = COMMON_CONFIG.register(new IdentifierListConfigValue(
            "block.teleporter.inter_dimension_to_blacklist",
            "Teleportation to dimensions in this list are not allowed.\nTeleportation within and from dimensions in this list are still allowed.",
            new ArrayList<>(0)
    ));
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_TELEPORTER_DIMENSION_TYPE_BLACKLIST = COMMON_CONFIG.register(new IdentifierListConfigValue(
            "block.teleporter.dimension_type_blacklist",
            "Teleportation within, from, and to dimension types in this list are not allowed.",
            new ArrayList<>(0)
    ));
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_TELEPORTER_INTRA_DIMENSIONAL_TYPE_BLACKLIST = COMMON_CONFIG.register(new IdentifierListConfigValue(
            "block.teleporter.intra_dimensional_type_blacklist",
            "Teleportation within dimension types in this list are not allowed.\nTeleportation to and from dimension types in this list are still allowed.",
            new ArrayList<>(0)
    ));
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_TELEPORTER_INTER_DIMENSIONAL_FROM_TYPE_BLACKLIST = COMMON_CONFIG.register(new IdentifierListConfigValue(
            "block.teleporter.inter_dimension_from_type_blacklist",
            "Teleportation from dimension types in this list are not allowed.\nTeleportation within and to dimension types in this list are still allowed.",
            new ArrayList<>(0)
    ));
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_TELEPORTER_INTER_DIMENSIONAL_TO_TYPE_BLACKLIST = COMMON_CONFIG.register(new IdentifierListConfigValue(
            "block.teleporter.inter_dimension_to_type_blacklist",
            "Teleportation to dimension types in this list are not allowed.\nTeleportation within and from dimension types in this list are still allowed.",
            new ArrayList<>(0)
    ));

    public static final ConfigValue<Float> COMMON_ALLOY_FURNACE_RECIPE_DURATION_MULTIPLIER = registerRecipeDurationMultiplierConfigValue(
            "block.alloy_furnace", "Alloy Furnace"
    );

    public static final ConfigValue<Long> COMMON_COAL_ENGINE_CAPACITY = registerEnergyCapacityConfigValue(
            "block.coal_engine", "Coal Engine", 2048
    );
    public static final ConfigValue<Long> COMMON_COAL_ENGINE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.coal_engine", "Coal Engine", 256
    );
    public static final ConfigValue<Double> COMMON_COAL_ENGINE_ENERGY_PRODUCTION_MULTIPLIER = registerEnergyProductionMultiplierConfigValue(
            "block.coal_engine", "Coal Engine"
    );

    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_1_CAPACITY = registerEnergyCapacityConfigValue(
            "block.solar_panel_1", "Solar Panel (Tier I)", 2 * 20 * 2
    );
    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_1_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.solar_panel_1", "Solar Panel (Tier I)", 2 * 4
    );
    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_1_ENERGY_PEAK_PRODUCTION = registerEnergyPeakProductionConfigValue(
            "block.solar_panel_1", "Solar Panel (Tier I)", 2
    );

    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_2_CAPACITY = registerEnergyCapacityConfigValue(
            "block.solar_panel_2", "Solar Panel (Tier II)", 16 * 20 * 2
    );
    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_2_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.solar_panel_2", "Solar Panel (Tier II)", 16 * 4
    );
    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_2_ENERGY_PEAK_PRODUCTION = registerEnergyPeakProductionConfigValue(
            "block.solar_panel_2", "Solar Panel (Tier II)", 16
    );

    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_3_CAPACITY = registerEnergyCapacityConfigValue(
            "block.solar_panel_3", "Solar Panel (Tier III)", 128 * 20 * 2
    );
    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_3_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.solar_panel_3", "Solar Panel (Tier III)", 128 * 4
    );
    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_3_ENERGY_PEAK_PRODUCTION = registerEnergyPeakProductionConfigValue(
            "block.solar_panel_3", "Solar Panel (Tier III)", 128
    );

    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_4_CAPACITY = registerEnergyCapacityConfigValue(
            "block.solar_panel_4", "Solar Panel (Tier IV)", 2048 * 20 * 2
    );
    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_4_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.solar_panel_4", "Solar Panel (Tier IV)", 2048 * 4
    );
    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_4_ENERGY_PEAK_PRODUCTION = registerEnergyPeakProductionConfigValue(
            "block.solar_panel_4", "Solar Panel (Tier IV)", 2048
    );

    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_5_CAPACITY = registerEnergyCapacityConfigValue(
            "block.solar_panel_5", "Solar Panel (Tier V)", 16384 * 20
    );
    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_5_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.solar_panel_5", "Solar Panel (Tier V)", 16384 * 8
    );
    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_5_ENERGY_PEAK_PRODUCTION = registerEnergyPeakProductionConfigValue(
            "block.solar_panel_5", "Solar Panel (Tier V)", 16384
    );

    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_6_CAPACITY = registerEnergyCapacityConfigValue(
            "block.solar_panel_6", "Solar Panel (Tier VI)", 131072 * 8
    );
    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_6_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.solar_panel_6", "Solar Panel (Tier VI)", 131072 * 4
    );
    public static final ConfigValue<Long> COMMON_SOLAR_PANEL_6_ENERGY_PEAK_PRODUCTION = registerEnergyPeakProductionConfigValue(
            "block.solar_panel_6", "Solar Panel (Tier VI)", 131072
    );

    public static final ConfigValue<Long> COMMON_MINECART_CHARGER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.minecart_charger", "Minecart Charger", 16384
    );
    public static final ConfigValue<Long> COMMON_MINECART_CHARGER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.minecart_charger", "Minecart Charger", 512
    );

    public static final ConfigValue<Long> COMMON_MINECART_UNCHARGER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.minecart_uncharger", "Minecart Uncharger", 16384
    );
    public static final ConfigValue<Long> COMMON_MINECART_UNCHARGER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.minecart_uncharger", "Minecart Uncharger", 512
    );

    public static final ConfigValue<Long> COMMON_ADVANCED_MINECART_CHARGER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.advanced_minecart_charger", "Advanced Minecart Charger", 524288
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_MINECART_CHARGER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.advanced_minecart_charger", "Advanced Minecart Charger", 65536
    );

    public static final ConfigValue<Long> COMMON_ADVANCED_MINECART_UNCHARGER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.advanced_minecart_uncharger", "Advanced Minecart Uncharger", 524288
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_MINECART_UNCHARGER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.advanced_minecart_uncharger", "Advanced Minecart Uncharger", 65536
    );

    public static final ConfigValue<Long> COMMON_CRUSHER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.crusher", "Crusher", 2048
    );
    public static final ConfigValue<Long> COMMON_CRUSHER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.crusher", "Crusher", 128
    );
    public static final ConfigValue<Long> COMMON_CRUSHER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.crusher", "Crusher", 8
    );
    public static final ConfigValue<Integer> COMMON_CRUSHER_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.crusher", "Crusher", 100
    );

    public static final ConfigValue<Long> COMMON_ADVANCED_CRUSHER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.advanced_crusher", "Advanced Crusher", 4096
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_CRUSHER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.advanced_crusher", "Advanced Crusher", 256
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_CRUSHER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.advanced_crusher", "Advanced Crusher", 32
    );
    public static final ConfigValue<Integer> COMMON_ADVANCED_CRUSHER_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.advanced_crusher", "Advanced Crusher", 25
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_CRUSHER_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.advanced_crusher", "Advanced Crusher", 8
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_CRUSHER_WATER_USAGE_PER_RECIPE = COMMON_CONFIG.register(new LongConfigValue(
            "block.advanced_crusher.water_usage_per_recipe",
            "The amount of Water in mB (milli Buckets) which will be converted to Dirty Water per completed recipe in the Advanced Crusher.",
            10L,
            1L, null
    ));

    public static final ConfigValue<Long> COMMON_PULVERIZER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.pulverizer", "Pulverizer", 2048
    );
    public static final ConfigValue<Long> COMMON_PULVERIZER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.pulverizer", "Pulverizer", 128
    );
    public static final ConfigValue<Long> COMMON_PULVERIZER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.pulverizer", "Pulverizer", 8
    );
    public static final ConfigValue<Integer> COMMON_PULVERIZER_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.pulverizer", "Pulverizer", 100
    );

    public static final ConfigValue<Long> COMMON_ADVANCED_PULVERIZER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.advanced_pulverizer", "Advanced Pulverizer", 4096
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_PULVERIZER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.advanced_pulverizer", "Advanced Pulverizer", 256
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_PULVERIZER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.advanced_pulverizer", "Advanced Pulverizer", 32
    );
    public static final ConfigValue<Integer> COMMON_ADVANCED_PULVERIZER_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.advanced_pulverizer", "Advanced Pulverizer", 25
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_PULVERIZER_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.advanced_pulverizer", "Advanced Pulverizer", 8
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_PULVERIZER_WATER_USAGE_PER_RECIPE = COMMON_CONFIG.register(new LongConfigValue(
            "block.advanced_pulverizer.water_usage_per_recipe",
            "The amount of Water in mB (milli Buckets) which will be converted to Dirty Water per completed recipe in the Advanced Pulverizer.",
            10L,
            1L, null
    ));

    public static final ConfigValue<Long> COMMON_SAWMILL_CAPACITY = registerEnergyCapacityConfigValue(
            "block.sawmill", "Sawmill", 2048
    );
    public static final ConfigValue<Long> COMMON_SAWMILL_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.sawmill", "Sawmill", 128
    );
    public static final ConfigValue<Long> COMMON_SAWMILL_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.sawmill", "Sawmill", 8
    );
    public static final ConfigValue<Integer> COMMON_SAWMILL_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.sawmill", "Sawmill", 100
    );

    public static final ConfigValue<Long> COMMON_COMPRESSOR_CAPACITY = registerEnergyCapacityConfigValue(
            "block.compressor", "Compressor", 2048
    );
    public static final ConfigValue<Long> COMMON_COMPRESSOR_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.compressor", "Compressor", 256
    );
    public static final ConfigValue<Long> COMMON_COMPRESSOR_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.compressor", "Compressor", 16
    );
    public static final ConfigValue<Integer> COMMON_COMPRESSOR_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.compressor", "Compressor", 100
    );

    public static final ConfigValue<Long> COMMON_METAL_PRESS_CAPACITY = registerEnergyCapacityConfigValue(
            "block.metal_press", "Metal Press", 2048
    );
    public static final ConfigValue<Long> COMMON_METAL_PRESS_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.metal_press", "Metal Press", 256
    );
    public static final ConfigValue<Long> COMMON_METAL_PRESS_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.metal_press", "Metal Press", 16
    );
    public static final ConfigValue<Integer> COMMON_METAL_PRESS_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.metal_press", "Metal Press", 250
    );

    public static final ConfigValue<Long> COMMON_AUTO_PRESS_MOLD_MAKER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.auto_press_mold_maker", "Auto Press Mold Maker", 2048
    );
    public static final ConfigValue<Long> COMMON_AUTO_PRESS_MOLD_MAKER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.auto_press_mold_maker", "Auto Press Mold Maker", 128
    );
    public static final ConfigValue<Long> COMMON_AUTO_PRESS_MOLD_MAKER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.auto_press_mold_maker", "Auto Press Mold Maker", 8
    );
    public static final ConfigValue<Integer> COMMON_AUTO_PRESS_MOLD_MAKER_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.auto_press_mold_maker", "Auto Press Mold Maker", 100
    );

    public static final ConfigValue<Long> COMMON_AUTO_STONECUTTER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.auto_stonecutter", "Auto Stonecutter", 2048
    );
    public static final ConfigValue<Long> COMMON_AUTO_STONECUTTER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.auto_stonecutter", "Auto Stonecutter", 128
    );
    public static final ConfigValue<Long> COMMON_AUTO_STONECUTTER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.auto_stonecutter", "Auto Stonecutter", 8
    );
    public static final ConfigValue<Integer> COMMON_AUTO_STONECUTTER_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.auto_stonecutter", "Auto Stonecutter", 100
    );

    public static final ConfigValue<Long> COMMON_ENERGIZER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.energizer", "Energizer", 65536
    );
    public static final ConfigValue<Long> COMMON_ENERGIZER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.energizer", "Energizer", 16384
    );
    public static final ConfigValue<Double> COMMON_ENERGIZER_ENERGY_CONSUMPTION_MULTIPLIER = registerEnergyConsumptionMultiplierConfigValue(
            "block.energizer", "Energizer"
    );
    public static final ConfigValue<Integer> COMMON_ENERGIZER_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.energizer", "Energizer", 100
    );

    public static final ConfigValue<Long> COMMON_AUTO_CRAFTER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.auto_crafter", "Auto Crafter", 2048
    );
    public static final ConfigValue<Long> COMMON_AUTO_CRAFTER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.auto_crafter", "Auto Crafter", 256
    );
    public static final ConfigValue<Long> COMMON_AUTO_CRAFTER_ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT = COMMON_CONFIG.register(new LongConfigValue(
            "block.auto_crafter.energy_consumption_per_tick_per_ingredient",
            "The energy consumption of the Auto Crafter if active in E per tick per ingredient",
            2L,
            1L, null
    ));
    public static final ConfigValue<Integer> COMMON_AUTO_CRAFTER_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.auto_crafter", "Auto Crafter", 100
    );
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_AUTO_CRAFTER_RECIPE_BLACKLIST = registerRecipeBlacklistValue(
            "block.auto_crafter", "Auto Crafter", new ArrayList<>(0)
    );

    public static final ConfigValue<Long> COMMON_ADVANCED_AUTO_CRAFTER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.advanced_auto_crafter", "Advanced Auto Crafter", 16384
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_AUTO_CRAFTER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.advanced_auto_crafter", "Advanced Auto Crafter", 1024
    );
    public static final ConfigValue<Integer> COMMON_ADVANCED_AUTO_CRAFTER_ENERGY_CONSUMPTION_PER_TICK_PER_INGREDIENT = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.advanced_auto_crafter.energy_consumption_per_tick_per_ingredient",
            "The energy consumption of the Advanced Auto Crafter if active in FE per tick per ingredient",
            16,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_ADVANCED_AUTO_CRAFTER_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.advanced_auto_crafter", "Advanced Auto Crafter", 50
    );
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_ADVANCED_AUTO_CRAFTER_RECIPE_BLACKLIST = registerRecipeBlacklistValue(
            "block.advanced_auto_crafter", "Advanced Auto Crafter", new ArrayList<>(0)
    );

    public static final ConfigValue<Long> COMMON_HEAT_GENERATOR_CAPACITY = registerEnergyCapacityConfigValue(
            "block.heat_generator", "Heat Generator", 10000
    );
    public static final ConfigValue<Long> COMMON_HEAT_GENERATOR_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.heat_generator", "Heat Generator", 500
    );
    public static final ConfigValue<Double> COMMON_HEAT_GENERATOR_ENERGY_PRODUCTION_MULTIPLIER = registerEnergyProductionMultiplierConfigValue(
            "block.heat_generator", "Heat Generator"
    );

    public static final ConfigValue<Long> COMMON_THERMAL_GENERATOR_CAPACITY = registerEnergyCapacityConfigValue(
            "block.thermal_generator", "Thermal Generator", 40960
    );
    public static final ConfigValue<Long> COMMON_THERMAL_GENERATOR_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.thermal_generator", "Thermal Generator", 2048
    );
    public static final ConfigValue<Double> COMMON_THERMAL_GENERATOR_ENERGY_PRODUCTION_MULTIPLIER = registerEnergyProductionMultiplierConfigValue(
            "block.thermal_generator", "Thermal Generator"
    );
    public static final ConfigValue<Long> COMMON_THERMAL_GENERATOR_FLUID_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.thermal_generator", "Thermal Generator", 8
    );

    public static final ConfigValue<Long> COMMON_BLOCK_PLACER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.block_placer", "Block Placer", 2048
    );
    public static final ConfigValue<Long> COMMON_BLOCK_PLACER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.block_placer", "Block Placer", 128
    );
    public static final ConfigValue<Long> COMMON_BLOCK_PLACER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.block_placer", "Block Placer", 32
    );
    public static final ConfigValue<Integer> COMMON_BLOCK_PLACER_PLACEMENT_DURATION = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.block_placer.placement_duration",
            "The time the Block Placer requires to place a block in ticks",
            20,
            1, null
    ));
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_BLOCK_PLACER_PLACEMENT_BLACKLIST = COMMON_CONFIG.register(new IdentifierListConfigValue(
            "block.block_placer.placement_blacklist",
            "Blocks in this list can not be placed by the Block Placer.",
            new ArrayList<>(0)
    ));

    public static final ConfigValue<Long> COMMON_ASSEMBLING_MACHINE_CAPACITY = registerEnergyCapacityConfigValue(
            "block.assembling_machine", "Assembling Machine", 8192
    );
    public static final ConfigValue<Long> COMMON_ASSEMBLING_MACHINE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.assembling_machine", "Assembling Machine", 512
    );
    public static final ConfigValue<Long> COMMON_ASSEMBLING_MACHINE_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.assembling_machine", "Assembling Machine", 256
    );
    public static final ConfigValue<Integer> COMMON_ASSEMBLING_MACHINE_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.assembling_machine", "Assembling Machine", 200
    );

    public static final ConfigValue<Long> COMMON_INDUCTION_SMELTER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.induction_smelter", "Induction Smelter", 8192
    );
    public static final ConfigValue<Long> COMMON_INDUCTION_SMELTER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.induction_smelter", "Induction Smelter", 256
    );
    public static final ConfigValue<Long> COMMON_INDUCTION_SMELTER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.induction_smelter", "Induction Smelter", 64
    );
    public static final ConfigValue<Float> COMMON_INDUCTION_SMELTER_RECIPE_DURATION_MULTIPLIER = COMMON_CONFIG.register(new FloatConfigValue(
            "block.induction_smelter.recipe_duration_multiplier",
            "The multiplier by which the time a recipe of the Induction Smelter requires is multiplied by.\n" +
                    "=> If set to 2 the Induction Smelter will be as fast as the Alloy Furnace.",
            1.f,
            0.f, null
    ));

    public static final ConfigValue<Long> COMMON_POWERED_LAMP_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.powered_lamp", "Powered Lamp", 1
    );

    public static final ConfigValue<Long> COMMON_POWERED_FURNACE_CAPACITY = registerEnergyCapacityConfigValue(
            "block.powered_furnace", "Powered Furnace", 2048
    );
    public static final ConfigValue<Long> COMMON_POWERED_FURNACE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.powered_furnace", "Powered Furnace", 128
    );
    public static final ConfigValue<Long> COMMON_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.powered_furnace", "Powered Furnace", 2
    );
    public static final ConfigValue<Float> COMMON_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER = COMMON_CONFIG.register(new FloatConfigValue(
            "block.powered_furnace.recipe_duration_multiplier",
            "The multiplier by which the time a recipe of the Powered Furnace requires is multiplied by.\n" +
                    "=> If set to 2 the Powered Furnace will be as fast as the normal Furnace.",
            1.f,
            0.f, null
    ));
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_POWERED_FURNACE_RECIPE_BLACKLIST = registerRecipeBlacklistValue(
            "block.powered_furnace", "Powered Furnace", new ArrayList<>(0)
    );

    public static final ConfigValue<Long> COMMON_ADVANCED_POWERED_FURNACE_CAPACITY = registerEnergyCapacityConfigValue(
            "block.advanced_powered_furnace", "Advanced Powered Furnace", 16384
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_POWERED_FURNACE_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.advanced_powered_furnace", "Advanced Powered Furnace", 1024
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_INPUT_PER_TICK = COMMON_CONFIG.register(new LongConfigValue(
            "block.advanced_powered_furnace.energy_consumption_per_input_per_tick",
            "The energy consumption per input of the Advanced Powered Furnace in E per tick.",
            256L,
            1L, null
    ));
    public static final ConfigValue<Float> COMMON_ADVANCED_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER = COMMON_CONFIG.register(new FloatConfigValue(
            "block.advanced_powered_furnace.recipe_duration_multiplier",
            "The multiplier by which the time a recipe of the Advanced Powered Furnace requires is multiplied by.\n" +
                    "=> If set to 6 the Advanced Powered Furnace will be as fast as the normal Furnace.",
            1.f,
            0.f, null
    ));
    public static final ConfigValue<List<@NotNull Identifier>> COMMON_ADVANCED_POWERED_FURNACE_RECIPE_BLACKLIST = registerRecipeBlacklistValue(
            "block.advanced_powered_furnace", "Advanced Powered Furnace", new ArrayList<>(0)
    );

    public static final ConfigValue<Long> COMMON_PLANT_GROWTH_CHAMBER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.plant_growth_chamber", "Plant Growth Chamber", 4096
    );
    public static final ConfigValue<Long> COMMON_PLANT_GROWTH_CHAMBER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.plant_growth_chamber", "Plant Growth Chamber", 256
    );
    public static final ConfigValue<Long> COMMON_PLANT_GROWTH_CHAMBER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.plant_growth_chamber", "Plant Growth Chamber", 32
    );
    public static final ConfigValue<Float> COMMON_PLANT_GROWTH_CHAMBER_RECIPE_DURATION_MULTIPLIER = registerRecipeDurationMultiplierConfigValue(
            "block.plant_growth_chamber", "Plant Growth Chamber"
    );

    public static final ConfigValue<Long> COMMON_STONE_LIQUEFIER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.stone_liquefier", "Stone Liquefier", 4096
    );
    public static final ConfigValue<Long> COMMON_STONE_LIQUEFIER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.stone_liquefier", "Stone Liquefier", 256
    );
    public static final ConfigValue<Long> COMMON_STONE_LIQUEFIER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.stone_liquefier", "Stone Liquefier", 128
    );
    public static final ConfigValue<Long> COMMON_STONE_LIQUEFIER_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.stone_liquefier", "Stone Liquefier", 8
    );
    public static final ConfigValue<Integer> COMMON_STONE_LIQUEFIER_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.stone_liquefier", "Stone Liquefier", 50
    );

    public static final ConfigValue<Long> COMMON_STONE_SOLIDIFIER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.stone_solidifier", "Stone Solidifier", 4096
    );
    public static final ConfigValue<Long> COMMON_STONE_SOLIDIFIER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.stone_solidifier", "Stone Solidifier", 256
    );
    public static final ConfigValue<Long> COMMON_STONE_SOLIDIFIER_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.stone_solidifier", "Stone Solidifier", 128
    );
    public static final ConfigValue<Long> COMMON_STONE_SOLIDIFIER_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.stone_solidifier", "Stone Solidifier", 8
    );
    public static final ConfigValue<Integer> COMMON_STONE_SOLIDIFIER_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.stone_solidifier", "Stone Solidifier", 25
    );

    public static final ConfigValue<Long> COMMON_FILTRATION_PLANT_CAPACITY = registerEnergyCapacityConfigValue(
            "block.filtration_plant", "Filtration Plant", 4096
    );
    public static final ConfigValue<Long> COMMON_FILTRATION_PLANT_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.filtration_plant", "Filtration Plant", 256
    );
    public static final ConfigValue<Long> COMMON_FILTRATION_PLANT_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.filtration_plant", "Filtration Plant", 128
    );
    public static final ConfigValue<Long> COMMON_FILTRATION_PLANT_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.filtration_plant", "Filtration Plant", 8
    );
    public static final ConfigValue<Integer> COMMON_FILTRATION_PLANT_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.filtration_plant", "Filtration Plant", 1000
    );
    public static final ConfigValue<Long> COMMON_FILTRATION_PLANT_DIRTY_WATER_USAGE_PER_RECIPE = COMMON_CONFIG.register(new LongConfigValue(
            "block.filtration_plant.water_usage_per_recipe",
            "The amount of Dirty Water in mB (milli Buckets) which will be converted to Water per completed recipe in the Filtration Plant.",
            100L,
            1L, null
    ));

    public static final ConfigValue<Long> COMMON_FLUID_TRANSPOSER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.fluid_transposer", "Fluid Transposer", 4096
    );
    public static final ConfigValue<Long> COMMON_FLUID_TRANSPOSER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.fluid_transposer", "Fluid Transposer", 256
    );
    public static final ConfigValue<Long> COMMON_FLUID_TRANSPOSER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.fluid_transposer", "Fluid Transposer", 128
    );
    public static final ConfigValue<Long> COMMON_FLUID_TRANSPOSER_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.fluid_transposer", "Fluid Transposer", 8
    );
    public static final ConfigValue<Integer> COMMON_FLUID_TRANSPOSER_RECIPE_DURATION = registerRecipeDurationConfigValue(
            "block.fluid_transposer", "Fluid Transposer", 200
    );

    public static final ConfigValue<Long> COMMON_FLUID_FILLER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.fluid_filler", "Fluid Filler", 2048
    );
    public static final ConfigValue<Long> COMMON_FLUID_FILLER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.fluid_filler", "Fluid Filler", 128
    );
    public static final ConfigValue<Long> COMMON_FLUID_FILLER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.fluid_filler", "Fluid Filler", 64
    );
    public static final ConfigValue<Long> COMMON_FLUID_FILLER_FLUID_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.fluid_filler", "Fluid Filler", 8
    );
    public static final ConfigValue<Long> COMMON_FLUID_FILLER_FLUID_ITEM_TRANSFER_RATE = COMMON_CONFIG.register(new LongConfigValue(
            "block.fluid_filler.fluid_item_transfer_rate",
            "The transfer rate an item can be filled up at by the Fluid Filler in mB (milli Buckets)",
            100L,
            1L, null
    ));

    public static final ConfigValue<Long> COMMON_FLUID_DRAINER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.fluid_drainer", "Fluid Drainer", 2048
    );
    public static final ConfigValue<Long> COMMON_FLUID_DRAINER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.fluid_drainer", "Fluid Drainer", 128
    );
    public static final ConfigValue<Long> COMMON_FLUID_DRAINER_ENERGY_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.fluid_drainer", "Fluid Drainer", 64
    );
    public static final ConfigValue<Long> COMMON_FLUID_DRAINER_FLUID_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.fluid_drainer", "Fluid Drainer", 8
    );
    public static final ConfigValue<Long> COMMON_FLUID_DRAINER_FLUID_ITEM_TRANSFER_RATE = COMMON_CONFIG.register(new LongConfigValue(
            "block.fluid_drainer.fluid_item_transfer_rate",
            "The transfer rate an item can be drained at by the Fluid Drainer in mB (milli Buckets)",
            100L,
            1L, null
    ));

    public static final ConfigValue<Long> COMMON_FLUID_PUMP_CAPACITY = registerEnergyCapacityConfigValue(
            "block.fluid_pump", "Fluid Pump", 2048
    );
    public static final ConfigValue<Long> COMMON_FLUID_PUMP_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.fluid_pump", "Fluid Pump", 128
    );
    public static final ConfigValue<Long> COMMON_FLUID_PUMP_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.fluid_pump", "Fluid Pump", 32
    );
    public static final ConfigValue<Long> COMMON_FLUID_PUMP_FLUID_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.fluid_pump", "Fluid Pump", 16
    );
    public static final ConfigValue<Integer> COMMON_FLUID_PUMP_NEXT_BLOCK_COOLDOWN = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.fluid_pump.next_block_cooldown",
            "The time the Fluid Pump requires to check the next block in ticks",
            20,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_FLUID_PUMP_EXTRACTION_DURATION = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.fluid_pump.extraction_duration",
            "The time the Fluid Pump requires to extract fluids from a block in ticks",
            100,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_FLUID_PUMP_EXTRACTION_RANGE = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.fluid_pump.extraction_range",
            "The horizontal extraction range of the Fluid Pump in blocks",
            5,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_FLUID_PUMP_EXTRACTION_DEPTH = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.fluid_pump.extraction_depth",
            "The vertical extraction depth of the Fluid Pump in blocks",
            64,
            1, null
    ));

    public static final ConfigValue<Long> COMMON_ADVANCED_FLUID_PUMP_CAPACITY = registerEnergyCapacityConfigValue(
            "block.advanced_fluid_pump", "Advanced Fluid Pump", 16384
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_FLUID_PUMP_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.advanced_fluid_pump", "Advanced Fluid Pump", 1024
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_FLUID_PUMP_CONSUMPTION_PER_TICK = registerEnergyConsumptionPerTickConfigValue(
            "block.advanced_fluid_pump", "Advanced Fluid Pump", 256
    );
    public static final ConfigValue<Long> COMMON_ADVANCED_FLUID_PUMP_FLUID_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.advanced_fluid_pump", "Advanced Fluid Pump", 128
    );
    public static final ConfigValue<Integer> COMMON_ADVANCED_FLUID_PUMP_NEXT_BLOCK_COOLDOWN = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.advanced_fluid_pump.next_block_cooldown",
            "The time the Advanced Fluid Pump requires to check the next block in ticks",
            4,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_ADVANCED_FLUID_PUMP_EXTRACTION_DURATION = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.advanced_fluid_pump.extraction_duration",
            "The time the Advanced Fluid Pump requires to extract fluids from a block in ticks",
            20,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_ADVANCED_FLUID_PUMP_EXTRACTION_RANGE = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.advanced_fluid_pump.extraction_range",
            "The horizontal extraction range of the Advanced Fluid Pump in blocks",
            10,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_ADVANCED_FLUID_PUMP_EXTRACTION_DEPTH = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.advanced_fluid_pump.extraction_depth",
            "The vertical extraction depth of the Advanced Fluid Pump in blocks",
            128,
            1, null
    ));

    public static final ConfigValue<Long> COMMON_DRAIN_FLUID_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.drain", "Drain", 2
    );
    public static final ConfigValue<Integer> COMMON_DRAIN_DRAIN_DURATION = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.drain.drain_duration",
            "The amount of ticks the Drain requires to drain 1000 mB (milli Buckets) from the fluid source block above.",
            20,
            1, null
    ));

    public static final ConfigValue<Long> COMMON_IRON_FLUID_PIPE_FLUID_TRANSFER_RATE = COMMON_CONFIG.register(new LongConfigValue(
            "block.iron_fluid_pipe.fluid_transfer_rate",
            "The transfer rate per tank and face of an Iron Fluid Pipe face in the extraction state in mB (milli Buckets)",
            100L,
            1L, null
    ));

    public static final ConfigValue<Long> COMMON_GOLDEN_FLUID_PIPE_FLUID_TRANSFER_RATE = COMMON_CONFIG.register(new LongConfigValue(
            "block.golden_fluid_pipe.fluid_transfer_rate",
            "The transfer rate per tank and face of a Golden Fluid Pipe face in the extraction state in mB (milli Buckets)",
            1000L,
            1L, null
    ));

    public static final ConfigValue<Long> COMMON_FLUID_TANK_SMALL_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.fluid_tank_small", "Fluid Tank (Small)", 8
    );

    public static final ConfigValue<Long> COMMON_FLUID_TANK_MEDIUM_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.fluid_tank_medium", "Fluid Tank (Medium)", 32
    );

    public static final ConfigValue<Long> COMMON_FLUID_TANK_LARGE_TANK_CAPACITY = registerFluidTankCapacityConfigValue(
            "block.fluid_tank_large", "Fluid Tank (Large)", 128
    );

    public static final ConfigValue<Integer> COMMON_ITEM_SILO_TINY_CAPACITY = registerItemSiloCapacityConfigValue(
            "block.item_silo_tiny", "Item Silo (Tiny)", 50
    );

    public static final ConfigValue<Integer> COMMON_ITEM_SILO_SMALL_CAPACITY = registerItemSiloCapacityConfigValue(
            "block.item_silo_small", "Item Silo (Small)", 100
    );

    public static final ConfigValue<Integer> COMMON_ITEM_SILO_MEDIUM_CAPACITY = registerItemSiloCapacityConfigValue(
            "block.item_silo_medium", "Item Silo (Medium)", 250
    );

    public static final ConfigValue<Integer> COMMON_ITEM_SILO_LARGE_CAPACITY = registerItemSiloCapacityConfigValue(
            "block.item_silo_large", "Item Silo (Large)", 500
    );

    public static final ConfigValue<Integer> COMMON_ITEM_SILO_GIANT_CAPACITY = registerItemSiloCapacityConfigValue(
            "block.item_silo_giant", "Item Silo (Giant)", 1000
    );

    public static final ConfigValue<Long> COMMON_CHARGER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.charger", "Charger", 8192
    );
    public static final ConfigValue<Long> COMMON_CHARGER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.charger", "Charger", 512
    );
    public static final ConfigValue<Float> COMMON_CHARGER_CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER = COMMON_CONFIG.register(new FloatConfigValue(
            "block.charger.charger_recipe_energy_consumption_multiplier",
            "The multiplier by which the energy consumption of charger recipes in the Charger is multiplied by.",
            1.f,
            0.f, null
    ));

    public static final ConfigValue<Long> COMMON_ADVANCED_CHARGER_CAPACITY_PER_SLOT = COMMON_CONFIG.register(new LongConfigValue(
            "block.advanced_charger.capacity_per_slot",
            "The energy capacity per slot of the Advanced Charger in FE.\n" +
                    "=> The energy capacity of the block is three times this value.",
            65536L,
            1L, null
    ));
    public static final ConfigValue<Long> COMMON_ADVANCED_CHARGER_TRANSFER_RATE_PER_SLOT = COMMON_CONFIG.register(new LongConfigValue(
            "block.advanced_charger.transfer_rate_per_slot",
            "The energy transfer rate per slot of the Advanced Charger in FE per tick.\n" +
                    "=> The energy transfer rate of the block is three times this value.",
            8192L,
            1L, null
    ));
    public static final ConfigValue<Float> COMMON_ADVANCED_CHARGER_CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER = COMMON_CONFIG.register(new FloatConfigValue(
            "block.advanced_charger.charger_recipe_energy_consumption_multiplier",
            "The multiplier by which the energy consumption of charger recipes in the Charger is multiplied by.",
            1.f,
            0.f, null
    ));

    public static final ConfigValue<Long> COMMON_UNCHARGER_CAPACITY = registerEnergyCapacityConfigValue(
            "block.uncharger", "Uncharger", 8192
    );
    public static final ConfigValue<Long> COMMON_UNCHARGER_TRANSFER_RATE = registerEnergyTransferRateConfigValue(
            "block.uncharger", "Uncharger", 512
    );

    public static final ConfigValue<Long> COMMON_ADVANCED_UNCHARGER_CAPACITY_PER_SLOT = COMMON_CONFIG.register(new LongConfigValue(
            "block.advanced_uncharger.capacity_per_slot",
            "The energy capacity per slot of the Advanced Uncharger in FE.\n" +
                    "=> The energy capacity of the block is three times this value.",
            65536L,
            1L, null
    ));
    public static final ConfigValue<Long> COMMON_ADVANCED_UNCHARGER_TRANSFER_RATE_PER_SLOT = COMMON_CONFIG.register(new LongConfigValue(
            "block.advanced_uncharger.transfer_rate_per_slot",
            "The energy transfer rate per slot of the Advanced Uncharger in FE per tick.\n" +
                    "=> The energy transfer rate of the block is three times this value.",
            8192L,
            1L, null
    ));

    public static final ConfigValue<Integer> COMMON_BASIC_ITEM_CONVEYOR_BELT_TICKS_PER_STEP = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.item_conveyor_belt.ticks_per_step",
            "The time required for the Basic Item Conveyor Belt to transport an item one step in ticks per step",
            10,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_FAST_ITEM_CONVEYOR_BELT_TICKS_PER_STEP = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.fast_item_conveyor_belt.ticks_per_step",
            "The time required for the Fast Item Conveyor Belt to transport an item one step in ticks per step",
            6,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_EXPRESS_ITEM_CONVEYOR_BELT_TICKS_PER_STEP = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.express_item_conveyor_belt.ticks_per_step",
            "The time required for the Express Item Conveyor Belt to transport an item one step in ticks per step",
            2,
            1, null
    ));

    public static final ConfigValue<Integer> COMMON_BASIC_ITEM_CONVEYOR_BELT_LOADER_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.item_conveyor_belt_loader.ticks_per_item",
            "The time required for the Basic Item Conveyor Belt Loader to extract a single item in ticks",
            10,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_FAST_ITEM_CONVEYOR_BELT_LOADER_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.fast_item_conveyor_belt_loader.ticks_per_item",
            "The time required for the Fast Item Conveyor Belt Loader to extract a single item in ticks",
            6,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_EXPRESS_ITEM_CONVEYOR_BELT_LOADER_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.express_item_conveyor_belt_loader.ticks_per_item",
            "The time required for the Express Item Conveyor Belt Loader to extract a single item in ticks",
            2,
            1, null
    ));

    public static final ConfigValue<Integer> COMMON_BASIC_ITEM_CONVEYOR_BELT_SORTER_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.item_conveyor_belt_sorter.ticks_per_item",
            "The time required for the Basic Item Conveyor Belt Sorter to process a single item in ticks",
            10,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_FAST_ITEM_CONVEYOR_BELT_SORTER_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.fast_item_conveyor_belt_sorter.ticks_per_item",
            "The time required for the Fast Item Conveyor Belt Sorter to process a single item in ticks",
            6,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_EXPRESS_ITEM_CONVEYOR_BELT_SORTER_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.express_item_conveyor_belt_sorter.ticks_per_item",
            "The time required for the Express Item Conveyor Belt Sorter to process a single item in ticks",
            2,
            1, null
    ));

    public static final ConfigValue<Integer> COMMON_BASIC_ITEM_CONVEYOR_BELT_SWITCH_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.item_conveyor_belt_switch.ticks_per_item",
            "The time required for the Basic Item Conveyor Belt Switch to process a single item in ticks",
            10,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_FAST_ITEM_CONVEYOR_BELT_SWITCH_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.fast_item_conveyor_belt_switch.ticks_per_item",
            "The time required for the Fast Item Conveyor Belt Switch to process a single item in ticks",
            6,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.express_item_conveyor_belt_switch.ticks_per_item",
            "The time required for the Express Item Conveyor Belt Switch to process a single item in ticks",
            2,
            1, null
    ));

    public static final ConfigValue<Integer> COMMON_BASIC_ITEM_CONVEYOR_BELT_SPLITTER_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.item_conveyor_belt_splitter.ticks_per_item",
            "The time required for the Basic Item Conveyor Belt Splitter to process a single item in ticks",
            10,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_FAST_ITEM_CONVEYOR_BELT_SPLITTER_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.fast_item_conveyor_belt_splitter.ticks_per_item",
            "The time required for the Fast Item Conveyor Belt Splitter to process a single item in ticks",
            6,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.express_item_conveyor_belt_splitter.ticks_per_item",
            "The time required for the Express Item Conveyor Belt Splitter to process a single item in ticks",
            2,
            1, null
    ));

    public static final ConfigValue<Integer> COMMON_BASIC_ITEM_CONVEYOR_BELT_MERGER_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.item_conveyor_belt_merger.ticks_per_item",
            "The time required for the Basic Item Conveyor Belt Merger to process a single item in ticks",
            10,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_FAST_ITEM_CONVEYOR_BELT_MERGER_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.fast_item_conveyor_belt_merger.ticks_per_item",
            "The time required for the Fast Item Conveyor Belt Merger to process a single item in ticks",
            6,
            1, null
    ));
    public static final ConfigValue<Integer> COMMON_EXPRESS_ITEM_CONVEYOR_BELT_MERGER_TICKS_PER_ITEM = COMMON_CONFIG.register(new IntegerConfigValue(
            "block.express_item_conveyor_belt_merger.ticks_per_item",
            "The time required for the Express Item Conveyor Belt Merger to process a single item in ticks",
            2,
            1, null
    ));

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

    //World
    public static final ConfigValue<Integer> COMMON_ELECTRICIAN_BUILDING_1_PLACEMENT_WEIGHT = COMMON_CONFIG.register(new IntegerConfigValue(
            "world.village.electrician_building_1.placement_weight",
            "Determines how frequent the Electrician Building 1 will be placed in villages.\n" +
                    "=> If set to 0 the Electrician Building 1 will never be placed.",
            5,
            0, 100
    ));

    //Misc
    public static final ConfigValue<Integer> COMMON_OFF_STATE_TIMEOUT = COMMON_CONFIG.register(new IntegerConfigValue(
            "off_state_timeout",
            "Sets the timeout a machine needs to be off for to change the block state to off in ticks (Prevent light flickering)",
            20,
            5, 200
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

    public static final ConfigValue<Boolean> CLIENT_FLUID_PUMP_RELATIVE_TARGET_COORDINATES = CLIENT_CONFIG.register(new BooleanConfigValue(
            "block.fluid_pump.relative_target_coordinates",
            "If set to true relative target coordinates will be shown instead of absolute coordinates in the Fluid Pump",
            true
    ));

    private static File getRelativeConfigFile(String fileName) {
        return FabricLoader.getInstance().getConfigDir().resolve("energizedpower/" + fileName).toFile();
    }

    private static ConfigValue<Long> registerEnergyCapacityConfigValue(String baseConfigKey, String itemName, long defaultValue) {
        return COMMON_CONFIG.register(new LongConfigValue(
                baseConfigKey + ".capacity",
                "The energy capacity of the " + itemName + " in E",
                defaultValue,
                1L, null
        ));
    }
    private static ConfigValue<Long> registerEnergyTransferRateConfigValue(String baseConfigKey, String itemName, long defaultValue) {
        return COMMON_CONFIG.register(new LongConfigValue(
                baseConfigKey + ".transfer_rate",
                "The energy transfer rate of the " + itemName + " in E per tick",
                defaultValue,
                1L, null
        ));
    }
    private static ConfigValue<Long> registerEnergyPeakProductionConfigValue(String baseConfigKey, String itemName, long defaultValue) {
        return COMMON_CONFIG.register(new LongConfigValue(
                baseConfigKey + ".energy_peak_production",
                "The peak energy production of the " + itemName + " for ideal conditions (e.g. at noon with clear sky for solar panels) in E per tick",
                defaultValue,
                1L, null
        ));
    }
    private static ConfigValue<Long> registerEnergyConsumptionPerTickConfigValue(String baseConfigKey, String itemName, long defaultValue) {
        return COMMON_CONFIG.register(new LongConfigValue(
                baseConfigKey + ".energy_consumption_per_tick",
                "The energy consumption of the " + itemName + " if active in E per tick",
                defaultValue,
                1L, null
        ));
    }
    private static ConfigValue<Double> registerEnergyConsumptionMultiplierConfigValue(String baseConfigKey, String itemName) {
        return COMMON_CONFIG.register(new DoubleConfigValue(
                baseConfigKey + ".energy_consumption_multiplier",
                "The multiplier by which the energy consumption of the " + itemName + " is multiplied by",
                1.,
                0., null
        ));
    }
    private static ConfigValue<Double> registerEnergyProductionMultiplierConfigValue(String baseConfigKey, String itemName) {
        return COMMON_CONFIG.register(new DoubleConfigValue(
                baseConfigKey + ".energy_production_multiplier",
                "The multiplier by which the energy production of the " + itemName + " is multiplied by",
                1.,
                0., null
        ));
    }
    private static ConfigValue<Long> registerEnergyConsumptionPerUseConfigValue(String baseConfigKey, String itemName, long defaultValue) {
        return COMMON_CONFIG.register(new LongConfigValue(
                baseConfigKey + ".energy_consumption_per_use",
                "The energy consumption of the " + itemName + " in E per use",
                defaultValue,
                1L, null
        ));
    }

    private static ConfigValue<Integer> registerItemSiloCapacityConfigValue(String baseConfigKey, String itemName, int defaultValue) {
        return COMMON_CONFIG.register(new IntegerConfigValue(
                baseConfigKey + ".item_silo_capacity",
                "The item silo capacity of the " + itemName + " in stacks (= 64 for most items)",
                defaultValue,
                1, null
        ));
    }

    private static ConfigValue<Long> registerFluidTankCapacityConfigValue(String baseConfigKey, String itemName, long defaultValue) {
        return COMMON_CONFIG.register(new LongConfigValue(
                baseConfigKey + ".fluid_tank_capacity",
                "The fluid tank capacity of the " + itemName + " in buckets (= 1000 mB)",
                defaultValue,
                1L, null
        ));
    }

    private static ConfigValue<Integer> registerRecipeDurationConfigValue(String baseConfigKey, String itemName, int defaultValue) {
        return COMMON_CONFIG.register(new IntegerConfigValue(
                baseConfigKey + ".recipe_duration",
                "The time a recipe of the " + itemName + " requires in ticks",
                defaultValue,
                1, null
        ));
    }
    private static ConfigValue<Float> registerRecipeDurationMultiplierConfigValue(String baseConfigKey, String itemName) {
        return COMMON_CONFIG.register(new FloatConfigValue(
                baseConfigKey + ".recipe_duration_multiplier",
                "The multiplier by which the time a recipe of the " + itemName + " requires is multiplied by",
                1.f,
                0.f, null
        ));
    }

    private static ConfigValue<List<@NotNull Identifier>> registerRecipeBlacklistValue(String baseConfigKey, String itemName,
                                                                                       @NotNull List<@NotNull Identifier> defaultValue) {
        return COMMON_CONFIG.register(new IdentifierListConfigValue(
                baseConfigKey + ".recipe_blacklist",
                "The recipe blacklist for the " + itemName + ".\n" +
                        "The blacklist is a list of recipe ids which can not be crafted in the " + itemName,
                defaultValue
        ));
    }

    private static ConfigValue<Double> registerSpeedModuleEffectValue(int tier, String tierRomanNumerals, double defaultValue) {
        return COMMON_CONFIG.register(new DoubleConfigValue(
                "item.speed_upgrade_module_" + tier + ".effect_value",
                "The upgrade module effect (Production speed multiplier) of the Speed Upgrade Module (Tier " +
                        tierRomanNumerals + ")",
                defaultValue,
                1., null
        ));
    }
    private static ConfigValue<Double> registerSpeedModuleEnergyConsumptionEffectValue(int tier, String tierRomanNumerals,
                                                                                       double defaultValue) {
        return COMMON_CONFIG.register(new DoubleConfigValue(
                "item.speed_upgrade_module_" + tier + ".effect_value.energy_consumption",
                "The upgrade module effect (Energy Consumption per tick multiplier) of the Speed Upgrade Module (Tier " +
                        tierRomanNumerals + ")",
                defaultValue,
                1., null
        ));
    }

    private static ConfigValue<Double> registerEnergyEfficiencyModuleEffectValue(int tier, String tierRomanNumerals,
                                                                                 double defaultValue) {
        return COMMON_CONFIG.register(new DoubleConfigValue(
                "item.energy_efficiency_upgrade_module_" + tier + ".effect_value",
                "The upgrade module effect (Energy Consumption per tick multiplier) of the Energy Efficiency Upgrade Module (Tier " +
                        tierRomanNumerals + ")",
                defaultValue,
                0., 1.
        ));
    }

    private static ConfigValue<Double> registerEnergyCapacityModuleEffectValue(int tier, String tierRomanNumerals, double defaultValue) {
        return COMMON_CONFIG.register(new DoubleConfigValue(
                "item.energy_capacity_upgrade_module_" + tier + ".effect_value",
                "The upgrade module effect (Energy capacity multiplier) of the Energy Capacity Upgrade Module (Tier " +
                        tierRomanNumerals + ")",
                defaultValue,
                1., null
        ));
    }
    private static ConfigValue<Double> registerEnergyCapacityModuleEnergyTransferRateEffectValue(int tier, String tierRomanNumerals,
                                                                                                 double defaultValue) {
        return COMMON_CONFIG.register(new DoubleConfigValue(
                "item.energy_capacity_upgrade_module_" + tier + ".energy_transfer_rate.effect_value",
                "The upgrade module effect (Energy transfer rate multiplier) of the Energy Capacity Upgrade Module (Tier " +
                        tierRomanNumerals + ")",
                defaultValue,
                1., null
        ));
    }

    private static ConfigValue<Double> registerDurationUpgradeModuleEffectValue(int tier, String tierRomanNumerals, double defaultValue) {
        return COMMON_CONFIG.register(new DoubleConfigValue(
                "item.duration_upgrade_module_" + tier + ".effect_value",
                "The upgrade module effect (Duration multiplier) of the Duration Upgrade Module (Tier " +
                        tierRomanNumerals + ")",
                defaultValue,
                1., null
        ));
    }

    private static ConfigValue<Double> registerRangeUpgradeModuleEffectValue(int tier, String tierRomanNumerals, double defaultValue) {
        return COMMON_CONFIG.register(new DoubleConfigValue(
                "item.range_upgrade_module_" + tier + ".effect_value",
                "The upgrade module effect (Range multiplier) of the Range Upgrade Module (Tier " +
                        tierRomanNumerals + ")",
                defaultValue,
                1., null
        ));
    }

    private static ConfigValue<Integer> registerExtractionDepthUpgradeModuleEffectValue(int tier, String tierRomanNumerals, int defaultValue) {
        return COMMON_CONFIG.register(new IntegerConfigValue(
                "item.extraction_depth_upgrade_module_" + tier + ".effect_value",
                "The upgrade module effect (Addition Extraction Depth in Blocks) of the Extraction Depth Upgrade Module (Tier " +
                        tierRomanNumerals + ")",
                defaultValue,
                1, null
        ));
    }

    private static ConfigValue<Double> registerExtractionRangeUpgradeModuleEffectValue(int tier, String tierRomanNumerals, double defaultValue) {
        return COMMON_CONFIG.register(new DoubleConfigValue(
                "item.extraction_range_upgrade_module_" + tier + ".effect_value",
                "The upgrade module effect (Range multiplier) of the Extraction Range Upgrade Module (Tier " +
                        tierRomanNumerals + ")",
                defaultValue,
                1., null
        ));
    }

    private static ConfigValue<Double> registerMoonLightUpgradeModuleEffectValue(int tier, String tierRomanNumerals, double defaultValue) {
        return COMMON_CONFIG.register(new DoubleConfigValue(
                "item.moon_light_upgrade_module_" + tier + ".effect_value",
                "The upgrade module effect (Multiplier of peak energy production during nighttime relative to the " +
                        "peak energy production during daytime) of the Moon Light Upgrade Module (Tier " +
                        tierRomanNumerals + ")",
                defaultValue,
                0., null
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
