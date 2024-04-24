package me.jddev0.ep.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class CableInsulatorItem extends Item {
    public CableInsulatorItem(Item.Settings props) {
        super(props);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.energizedpower.cable_insulator.txt.shift.1").formatted(Formatting.GRAY));
        }else {
            tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }
}
