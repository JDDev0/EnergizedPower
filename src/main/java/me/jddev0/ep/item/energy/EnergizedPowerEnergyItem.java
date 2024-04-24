package me.jddev0.ep.item.energy;

import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.List;

public class EnergizedPowerEnergyItem extends Item implements SimpleEnergyItem {
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
        return getStoredEnergy(itemStack);
    }
    protected void setEnergy(ItemStack itemStack, long energy) {
        SimpleEnergyItem.setStoredEnergyUnchecked(itemStack, energy);
    }

    @Override
    public long getEnergyCapacity(ItemStack itemStack) {
        return capacity;
    }

    @Override
    public long getEnergyMaxInput(ItemStack stack) {
        return maxReceive;
    }

    @Override
    public long getEnergyMaxOutput(ItemStack stack) {
        return maxExtract;
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
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(getEnergy(stack)), EnergyUtils.getEnergyWithPrefix(getEnergyCapacity(stack))).
                formatted(Formatting.GRAY));
    }
}
