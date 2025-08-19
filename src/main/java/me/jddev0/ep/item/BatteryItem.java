package me.jddev0.ep.item;

import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.machine.tier.BatteryTier;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryItem extends EnergizedPowerEnergyItem {
    private final BatteryTier tier;

    public BatteryItem(BatteryTier tier) {
        super(new FabricItemSettings().maxCount(1), tier.getCapacity(), tier.getMaxTransfer(), tier.getMaxTransfer());

        this.tier = tier;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World level, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(itemStack, level, tooltip, context);

        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.energizedpower.battery.txt.shift.1",
                            EnergyUtils.getEnergyWithPrefix(tier.getMaxTransfer())).formatted(Formatting.GRAY));
        }else {
            tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

}
