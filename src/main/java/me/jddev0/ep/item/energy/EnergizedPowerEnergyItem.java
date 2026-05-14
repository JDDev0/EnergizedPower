package me.jddev0.ep.item.energy;

import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

public class EnergizedPowerEnergyItem extends Item {
    private final long capacity;
    private final long maxReceive;
    private final long maxExtract;

    public EnergizedPowerEnergyItem(Item.Properties props, long capacity, long maxReceive, long maxExtract) {
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        tooltip.add(Component.translatable("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(getEnergy(stack)), EnergyUtils.getEnergyWithPrefix(getEnergyCapacity(stack))).
                withStyle(ChatFormatting.GRAY));
    }
}
