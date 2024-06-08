package me.jddev0.ep.integration.cctweaked;

import dan200.computercraft.api.ComputerCraftAPI;

public final class EnergizedPowerCCTweakedIntegration {
    private EnergizedPowerCCTweakedIntegration() {}

    public static void register() {
        ComputerCraftAPI.registerGenericSource(new RedstoneModeGenericPeripheral());
        ComputerCraftAPI.registerGenericSource(new ComparatorModeGenericPeripheral());
        ComputerCraftAPI.registerGenericSource(new WeatherControllerGenericPeripheral());
        ComputerCraftAPI.registerGenericSource(new TimeControllerGenericPeripheral());
    }
}
