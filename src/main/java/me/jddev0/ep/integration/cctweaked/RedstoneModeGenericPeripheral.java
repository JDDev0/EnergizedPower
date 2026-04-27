package me.jddev0.ep.integration.cctweaked;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import dan200.computercraft.api.peripheral.PeripheralType;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.configuration.IRedstoneModeHandler;
import me.jddev0.ep.machine.configuration.RedstoneMode;

public class RedstoneModeGenericPeripheral implements GenericPeripheral {
    @Override
    public PeripheralType getType() {
        return PeripheralType.ofAdditional(id());
    }

    @Override
    public String id() {
        return EPAPI.MOD_ID + ":redstone_mode";
    }

    @LuaFunction(mainThread = true)
    public final String[] getAvailableRedstoneModes(IRedstoneModeHandler redstoneModeHandler) {
        RedstoneMode[] modes = redstoneModeHandler.getAvailableRedstoneModes();
        String[] serializedModes = new String[modes.length];
        for(int i = 0;i < modes.length;i++)
            serializedModes[i] = modes[i].getSerializedName();

        return serializedModes;
    }

    @LuaFunction(mainThread = true)
    public final String getRedstoneMode(IRedstoneModeHandler redstoneModeHandler) {
        return redstoneModeHandler.getRedstoneMode().getSerializedName();
    }

    @LuaFunction(mainThread = true)
    public final boolean setRedstoneMode(IRedstoneModeHandler redstoneModeHandler, RedstoneMode redstoneMode) {
        return redstoneModeHandler.setRedstoneMode(redstoneMode);
    }
}
