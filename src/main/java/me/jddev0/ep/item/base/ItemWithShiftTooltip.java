package me.jddev0.ep.item.base;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemWithShiftTooltip extends Item {
    private final List<Component> shiftTooltip;

    public ItemWithShiftTooltip(Properties properties, Component... shiftTooltip) {
        super(properties);

        this.shiftTooltip = List.of(shiftTooltip);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag flag) {
        if(Screen.hasShiftDown()) {
            components.addAll(shiftTooltip);
        }else {
            components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }
}
