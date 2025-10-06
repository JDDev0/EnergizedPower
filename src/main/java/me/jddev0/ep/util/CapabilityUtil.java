package me.jddev0.ep.util;

import me.jddev0.ep.energy.IEnergizedPowerEnergyHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;

public final class CapabilityUtil {
    private CapabilityUtil() {}

    public static <T> T getItemCapabilityReadOnly(ItemCapability<T, ItemAccess> capability, ItemStack itemStack) {
        if(itemStack.isEmpty())
            return null;

        return itemStack.getCapability(capability, ItemAccess.forStack(itemStack));
    }

    //TODO remove if in NeoForge EnergyHandler
    public static boolean canInsert(EnergyHandler handler) {
        return !(handler instanceof IEnergizedPowerEnergyHandler epEnergyHandler) || epEnergyHandler.canInsert();
    }

    //TODO remove if in NeoForge EnergyHandler
    public static boolean canExtract(EnergyHandler handler) {
        return !(handler instanceof IEnergizedPowerEnergyHandler epEnergyHandler) || epEnergyHandler.canExtract();
    }
}
