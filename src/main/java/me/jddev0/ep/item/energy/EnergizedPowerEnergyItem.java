package me.jddev0.ep.item.energy;

import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Consumer;

public class EnergizedPowerEnergyItem extends Item {
    private final long capacity;
    private final long maxReceive;
    private final long maxExtract;

    public EnergizedPowerEnergyItem(Item.Settings props, long capacity, long maxReceive, long maxExtract) {
        super(props);

        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    protected long getEnergy(ItemStack itemStack) {
        return getStoredEnergyUnchecked(itemStack);
    }

    protected void setEnergy(ItemStack itemStack, long energy) {
        setStoredEnergyUnchecked(itemStack, energy);
    }

    public long getEnergyCapacity(ItemStack itemStack) {
        return capacity;
    }

    public long getEnergyMaxInput(ItemStack itemStack) {
        return maxReceive;
    }

    public long getEnergyMaxOutput(ItemStack itemStack) {
        return maxExtract;
    }

    public static long getStoredEnergyUnchecked(ItemStack stack) {
        return stack.getOrDefault(EPDataComponentTypes.ENERGY, 0L);
    }

    public static void setStoredEnergyUnchecked(ItemStack stack, long newAmount) {
        stack.set(EPDataComponentTypes.ENERGY, newAmount);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round(getEnergy(stack) * 13.f / getEnergyCapacity(stack));
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        float f = Math.max(0.f, getEnergy(stack) / (float)getEnergyCapacity(stack));
        return MathHelper.hsvToRgb(f * .33f, 1.f, 1.f);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
        tooltip.accept(Text.translatable("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(getEnergy(stack)), EnergyUtils.getEnergyWithPrefix(getEnergyCapacity(stack))).
                formatted(Formatting.GRAY));
    }
}
