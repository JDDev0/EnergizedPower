package me.jddev0.ep.item;

import me.jddev0.ep.energy.ReceiveAndExtractEnergyStorage;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
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
            components.add(new TranslatableComponent("tooltip.energizedpower.battery.txt.shift.1",
                            EnergyUtils.getEnergyWithPrefix(tier.getMaxTransfer())).withStyle(ChatFormatting.GRAY));
        }else {
            components.add(new TranslatableComponent("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
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
