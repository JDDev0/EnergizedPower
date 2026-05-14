package me.jddev0.ep.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CableInsulatorItem extends Item {
    public CableInsulatorItem(Item.Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.energizedpower.cable_insulator.txt.shift.1").withStyle(ChatFormatting.GRAY));
        }else {
            tooltip.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }
}
