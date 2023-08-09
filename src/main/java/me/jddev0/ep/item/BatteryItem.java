package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
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
    private final Tier tier;

    public BatteryItem(Tier tier) {
        super(new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).maxCount(1), tier.getCapacity(), tier.getMaxTransfer(), tier.getMaxTransfer());

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

    public enum Tier {
        BATTERY_1("battery_1", ModConfigs.COMMON_BATTERY_1_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_1_TRANSFER_RATE.getValue()),
        BATTERY_2("battery_2", ModConfigs.COMMON_BATTERY_2_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_2_TRANSFER_RATE.getValue()),
        BATTERY_3("battery_3", ModConfigs.COMMON_BATTERY_3_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_3_TRANSFER_RATE.getValue()),
        BATTERY_4("battery_4", ModConfigs.COMMON_BATTERY_4_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_4_TRANSFER_RATE.getValue()),
        BATTERY_5("battery_5", ModConfigs.COMMON_BATTERY_5_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_5_TRANSFER_RATE.getValue()),
        BATTERY_6("battery_6", ModConfigs.COMMON_BATTERY_6_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_6_TRANSFER_RATE.getValue()),
        BATTERY_7("battery_7", ModConfigs.COMMON_BATTERY_7_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_7_TRANSFER_RATE.getValue()),
        BATTERY_8("battery_8", ModConfigs.COMMON_BATTERY_8_CAPACITY.getValue(), ModConfigs.COMMON_BATTERY_8_TRANSFER_RATE.getValue());

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
