package me.jddev0.ep.item;

import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreativeBatteryItem extends Item {
    public CreativeBatteryItem(FabricItemSettings props) {
        super(props);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return 13;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 16733695; //ChatFormatting.LIGHT_PURPLE
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World level, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.energizedpower.energy_meter.content.txt",
                        EnergyUtils.getEnergyWithPrefix(Long.MAX_VALUE), EnergyUtils.getEnergyWithPrefix(Long.MAX_VALUE)).
                formatted(Formatting.GRAY));

        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.energizedpower.capacity.txt",
                            Text.translatable("tooltip.energizedpower.infinite.txt").
                                    formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC)).
                    formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.energizedpower.transfer_rate.txt",
                            Text.translatable("tooltip.energizedpower.infinite.txt").
                                    formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC)).
                    formatted(Formatting.GRAY));
        }else {
            tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }
}
