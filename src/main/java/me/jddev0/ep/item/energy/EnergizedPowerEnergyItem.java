package me.jddev0.ep.item.energy;

import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtLong;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.List;

public class EnergizedPowerEnergyItem extends Item implements SimpleEnergyItem {
    private final long capacity;
    private final long maxReceive;
    private final long maxExtract;


    public EnergizedPowerEnergyItem(FabricItemSettings props, long capacity, long maxReceive, long maxExtract) {
        super(props);

        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public void appendStacks(ItemGroup category, DefaultedList<ItemStack> items) {
        if(isIn(category)) {
            items.add(new ItemStack(this));

            ItemStack itemStackFullyCharged = new ItemStack(this);
            itemStackFullyCharged.getOrCreateNbt().put("energy", NbtLong.of(capacity));

            items.add(itemStackFullyCharged);
        }
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
    public void appendTooltip(ItemStack itemStack, @Nullable World level, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(getEnergy(itemStack)), EnergyUtils.getEnergyWithPrefix(getEnergyCapacity(itemStack))).
                formatted(Formatting.GRAY));
    }
}
