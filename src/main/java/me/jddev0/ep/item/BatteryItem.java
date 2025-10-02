package me.jddev0.ep.item;

import me.jddev0.ep.energy.ReceiveAndExtractEnergyStorage;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.machine.tier.BatteryTier;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class BatteryItem extends EnergizedPowerEnergyItem {
    private final BatteryTier tier;

    public BatteryItem(Properties props, BatteryTier tier) {
        super(props.stacksTo(1), () -> new ReceiveAndExtractEnergyStorage(0, tier.getCapacity(), tier.getMaxTransfer()));

        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, components, tooltipFlag);

        if(Minecraft.getInstance().hasShiftDown()) {
            components.accept(Component.translatable("tooltip.energizedpower.battery.txt.shift.1",
                            EnergyUtils.getEnergyWithPrefix(tier.getMaxTransfer())).withStyle(ChatFormatting.GRAY));
        }else {
            components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }
}
