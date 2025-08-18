package me.jddev0.ep.item;

import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.machine.tier.BatteryTier;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class BatteryItem extends EnergizedPowerEnergyItem {
    private final BatteryTier tier;

    public BatteryItem(BatteryTier tier) {
        super(new Settings().maxCount(1), tier.getCapacity(), tier.getMaxTransfer(), tier.getMaxTransfer());

        this.tier = tier;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.energizedpower.battery.txt.shift.1",
                            EnergyUtils.getEnergyWithPrefix(tier.getMaxTransfer())).formatted(Formatting.GRAY));
        }else {
            tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

}
