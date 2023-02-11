package me.jddev0.ep.item;

import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryItem extends EnergizedPowerEnergyItem {
    private final Tier tier;

    public BatteryItem(Tier tier) {
        super(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).maxCount(1), tier.getCapacity(), tier.getMaxTransfer(), tier.getMaxTransfer());

        this.tier = tier;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World level, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(itemStack, level, tooltip, context);

        if(Screen.hasShiftDown()) {
            tooltip.add(new TranslatableText("tooltip.energizedpower.battery.txt.shift.1",
                            EnergyUtils.getEnergyWithPrefix(tier.getMaxTransfer())).formatted(Formatting.GRAY));
        }else {
            tooltip.add(new TranslatableText("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    public enum Tier {
        BATTERY_1("battery_1", 256, 2),
        BATTERY_2("battery_2", 1024, 8),
        BATTERY_3("battery_3", 2048, 32),
        BATTERY_4("battery_4", 8192, 128),
        BATTERY_5("battery_5", 16384, 512),
        BATTERY_6("battery_6", 65536, 2048),
        BATTERY_7("battery_7", 262144, 8192),
        BATTERY_8("battery_8", 1048576, 32768);

        private final String resourceId;
        private final long capacity;
        private final long maxTransfer;

        Tier(String resourceId, long capacity, long maxTransfer) {
            this.resourceId = resourceId;
            this.capacity = capacity;
            this.maxTransfer = maxTransfer;
        }

        public String getResourceId() {
            return resourceId;
        }

        public long getCapacity() {
            return capacity;
        }

        public long getMaxTransfer() {
            return maxTransfer;
        }
    }
}
