package me.jddev0.ep.item.energy;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class EnergizedPowerEnergyItem extends Item {
    private final Function<ItemStack, IEnergizedPowerEnergyStorage> energyStorageProvider;

    protected static int getEnergy(ItemStack itemStack) {
        IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energyStorage instanceof ItemCapabilityEnergy?energyStorage.getEnergyStored():0;
    }
    protected static void setEnergy(ItemStack itemStack, int energy) {
        IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyStorage instanceof ItemCapabilityEnergy energizedPowerEnergyStorage)
            energizedPowerEnergyStorage.setEnergy(energy);
    }
    protected static int getCapacity(ItemStack itemStack) {
        IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energyStorage instanceof ItemCapabilityEnergy?energyStorage.getMaxEnergyStored():0;
    }

    public EnergizedPowerEnergyItem(Properties props, Supplier<IEnergizedPowerEnergyStorage> energyStorageProvider) {
        this(props, stack -> energyStorageProvider.get());
    }

    public EnergizedPowerEnergyItem(Properties props, Function<ItemStack, IEnergizedPowerEnergyStorage> energyStorageProvider) {
        super(props);

        this.energyStorageProvider = energyStorageProvider;
    }

    public Function<ItemStack, IEnergizedPowerEnergyStorage> getEnergyStorageProvider() {
        return energyStorageProvider;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(getEnergy(stack) * 13.f / getCapacity(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.f, getEnergy(stack) / (float)getCapacity(stack));
        return Mth.hsvToRgb(f * .33f, 1.f, 1.f);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
        components.accept(Component.translatable("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(getEnergy(itemStack)), EnergyUtils.getEnergyWithPrefix(getCapacity(itemStack))).
                withStyle(ChatFormatting.GRAY));
    }
}
