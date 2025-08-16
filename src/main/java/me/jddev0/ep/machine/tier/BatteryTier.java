package me.jddev0.ep.machine.tier;

import me.jddev0.ep.config.ModConfigs;

public enum BatteryTier {
    BATTERY_1("battery_1", ModConfigs.COMMON_BATTERY_1_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_1_TRANSFER_RATE.getValue()),
    BATTERY_2("battery_2", ModConfigs.COMMON_BATTERY_2_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_2_TRANSFER_RATE.getValue()),
    BATTERY_3("battery_3", ModConfigs.COMMON_BATTERY_3_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_3_TRANSFER_RATE.getValue()),
    BATTERY_4("battery_4", ModConfigs.COMMON_BATTERY_4_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_4_TRANSFER_RATE.getValue()),
    BATTERY_5("battery_5", ModConfigs.COMMON_BATTERY_5_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_5_TRANSFER_RATE.getValue()),
    BATTERY_6("battery_6", ModConfigs.COMMON_BATTERY_6_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_6_TRANSFER_RATE.getValue()),
    BATTERY_7("battery_7", ModConfigs.COMMON_BATTERY_7_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_7_TRANSFER_RATE.getValue()),
    BATTERY_8("battery_8", ModConfigs.COMMON_BATTERY_8_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_8_TRANSFER_RATE.getValue());

    private final String resourceId;
    private final int capacity;
    private final int maxTransfer;

    BatteryTier(String resourceId, int capacity, int maxTransfer) {
        this.resourceId = resourceId;
        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }
}
