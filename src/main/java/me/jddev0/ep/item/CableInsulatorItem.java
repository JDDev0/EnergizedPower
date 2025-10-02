package me.jddev0.ep.item;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Consumer;

public class CableInsulatorItem extends Item {
    public CableInsulatorItem(Item.Settings props) {
        super(props);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
        if(MinecraftClient.getInstance().isShiftPressed()) {
            tooltip.accept(Text.translatable("tooltip.energizedpower.cable_insulator.txt.shift.1").formatted(Formatting.GRAY));
        }else {
            tooltip.accept(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }
}
