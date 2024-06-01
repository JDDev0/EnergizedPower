package me.jddev0.ep.screen.base;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractEnergizedPowerScreenHandler extends ScreenHandler {
    protected AbstractEnergizedPowerScreenHandler(@Nullable ScreenHandlerType<?> type, int id) {
        super(type, id);
    }

    protected boolean insertMaxCount1Item(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        if(fromLast) {
            for(int i = endIndex - 1;i >= startIndex;i--)
                if(!getSlot(i).hasStack() && insertItem(stack, i, i + 1, fromLast))
                    return true;

            return false;
        }

        for(int i = startIndex;i < endIndex;i++)
            if(!getSlot(i).hasStack() && insertItem(stack, i, i + 1, fromLast))
                return true;

        return false;
    }
}
