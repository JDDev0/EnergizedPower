package me.jddev0.ep.item;

import me.jddev0.ep.energy.InfinityEnergyStorage;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class CreativeBatteryItem extends EnergizedPowerEnergyItem {
    public CreativeBatteryItem(Properties props) {
        super(props, InfinityEnergyStorage::new);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return 13;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 16733695; //ChatFormatting.LIGHT_PURPLE
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, components, tooltipFlag);

        if(Minecraft.getInstance().hasShiftDown()) {
            components.accept(Component.translatable("tooltip.energizedpower.capacity.txt",
                            Component.translatable("tooltip.energizedpower.infinite.txt").
                                    withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC)).
                    withStyle(ChatFormatting.GRAY));
            components.accept(Component.translatable("tooltip.energizedpower.transfer_rate.txt",
                            Component.translatable("tooltip.energizedpower.infinite.txt").
                                    withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC)).
                    withStyle(ChatFormatting.GRAY));
        }else {
            components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }
}
