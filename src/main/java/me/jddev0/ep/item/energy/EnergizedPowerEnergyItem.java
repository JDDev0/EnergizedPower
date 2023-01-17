package me.jddev0.ep.item.energy;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class EnergizedPowerEnergyItem extends Item {
    private final Supplier<IEnergizedPowerEnergyStorage> energyStorageProvider;
    protected static int getEnergy(ItemStack itemStack) {
        return itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(null).getEnergyStored();
    }
    protected static void setEnergy(ItemStack itemStack, int energy) {
        ((ItemCapabilityEnergy)itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(null)).setEnergy(energy);
    }
    protected static int getCapacity(ItemStack itemStack) {
        return itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(null).getMaxEnergyStored();
    }

    public EnergizedPowerEnergyItem(Properties props, Supplier<IEnergizedPowerEnergyStorage> energyStorageProvider) {
        super(props);

        this.energyStorageProvider = energyStorageProvider;
    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
        if(allowdedIn(category)) {
            items.add(new ItemStack(this));

            ItemStack itemStackFullyCharged = new ItemStack(this);
            itemStackFullyCharged.getOrCreateTag().put("energy", IntTag.valueOf(energyStorageProvider.get().getCapacity()));

            items.add(itemStackFullyCharged);
        }
    }

    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    public int getBarWidth(ItemStack stack) {
        return Math.round(getEnergy(stack) * 13.f / getCapacity(stack));
    }

    public int getBarColor(ItemStack stack) {
        float f = Math.max(0.f, getEnergy(stack) / (float)getCapacity(stack));
        return Mth.hsvToRgb(f * .33f, 1.f, 1.f);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        components.add(new TranslatableComponent("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(getEnergy(itemStack)), EnergyUtils.getEnergyWithPrefix(getCapacity(itemStack))).
                withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ItemCapabilityEnergy(stack, stack.getTag(), energyStorageProvider.get());
    }
}
