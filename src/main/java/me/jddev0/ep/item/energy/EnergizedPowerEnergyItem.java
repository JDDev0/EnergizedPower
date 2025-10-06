package me.jddev0.ep.item.energy;

import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class EnergizedPowerEnergyItem extends Item {
    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;

    public EnergizedPowerEnergyItem(Properties props, int capacity, int maxReceive, int maxExtract) {
        super(props);

        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    protected int getEnergy(ItemStack itemStack) {
        return getStoredEnergyUnchecked(itemStack);
    }

    protected void setEnergy(ItemStack itemStack, int energy) {
        setStoredEnergyUnchecked(itemStack, energy);
    }

    public int getEnergyCapacity(ItemStack itemStack) {
        return capacity;
    }

    public int getEnergyMaxInput(ItemStack itemStack) {
        return maxReceive;
    }

    public int getEnergyMaxOutput(ItemStack itemStack) {
        return maxExtract;
    }

    public static int getStoredEnergyUnchecked(ItemStack stack) {
        return stack.getOrDefault(EPDataComponentTypes.ENERGY, 0);
    }

    public static void setStoredEnergyUnchecked(ItemStack stack, int newAmount) {
        stack.set(EPDataComponentTypes.ENERGY, newAmount);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(getEnergy(stack) * 13.f / getEnergyCapacity(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.f, getEnergy(stack) / (float)getEnergyCapacity(stack));
        return Mth.hsvToRgb(f * .33f, 1.f, 1.f);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
        components.accept(Component.translatable("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(getEnergy(itemStack)), EnergyUtils.getEnergyWithPrefix(getEnergyCapacity(itemStack))).
                withStyle(ChatFormatting.GRAY));
    }
}
