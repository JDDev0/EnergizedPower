package me.jddev0.ep.integration.cctweaked;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.PeripheralType;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.configuration.IComparatorModeHandler;
import me.jddev0.ep.machine.configuration.ComparatorMode;

public class ComparatorModeGenericPeripheral implements GenericPeripheral {
    @Override
    public PeripheralType getType() {
        return PeripheralType.ofAdditional(id());
    }

    @Override
    public String id() {
        return EPAPI.MOD_ID + ":comparator_mode";
    }

    @LuaFunction(mainThread = true)
    public final String[] getAvailableComparatorModes(IComparatorModeHandler comparatorModeHandler) {
        ComparatorMode[] modes = comparatorModeHandler.getAvailableComparatorModes();
        String[] serializedModes = new String[modes.length];
        for(int i = 0;i < modes.length;i++)
            serializedModes[i] = modes[i].getSerializedName();

        return serializedModes;
    }

    @LuaFunction(mainThread = true)
    public final String getComparatorMode(IComparatorModeHandler comparatorModeHandler) {
        return comparatorModeHandler.getComparatorMode().getSerializedName();
    }

    @LuaFunction(mainThread = true)
    public final boolean setComparatorMode(IComparatorModeHandler comparatorModeHandler, ComparatorMode comparatorMode) {
        return comparatorModeHandler.setComparatorMode(comparatorMode);
    }
}
