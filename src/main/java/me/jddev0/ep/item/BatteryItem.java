package me.jddev0.ep.item;

import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.machine.tier.BatteryTier;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

public class BatteryItem extends EnergizedPowerEnergyItem {
    private final BatteryTier tier;

    public BatteryItem(BatteryTier tier) {
        super(new Properties().stacksTo(1), tier.getCapacity(), tier.getMaxTransfer(), tier.getMaxTransfer());

        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, tooltip, type);

        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.energizedpower.battery.txt.shift.1",
                            EnergyUtils.getEnergyWithPrefix(tier.getMaxTransfer())).withStyle(ChatFormatting.GRAY));
        }else {
            tooltip.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

}
