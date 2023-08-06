package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveAndExtractEnergyStorage;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BatteryItem extends EnergizedPowerEnergyItem {
    private final Tier tier;

    public BatteryItem(Tier tier) {
        super(new Item.Properties().tab(ModCreativeModeTab.ENERGIZED_POWER_TAB).stacksTo(1),
                () -> new ReceiveAndExtractEnergyStorage(0, tier.getCapacity(), tier.getMaxTransfer()));

        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, components, tooltipFlag);

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.energizedpower.battery.txt.shift.1",
                            EnergyUtils.getEnergyWithPrefix(tier.getMaxTransfer())).withStyle(ChatFormatting.GRAY));
        }else {
            components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
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
        private final int capacity;
        private final int maxTransfer;

        Tier(String resourceId, int capacity, int maxTransfer) {
            this.resourceId = resourceId;
            this.capacity = capacity;
            this.maxTransfer = maxTransfer;
        }

        public String getResourceId() {
            return resourceId;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getMaxTransfer() {
            return maxTransfer;
        }
    }
}
