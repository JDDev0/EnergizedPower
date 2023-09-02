package me.jddev0.ep.block;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public interface WrenchConfigurable {
    /**
     * @param nextPreviousValue <ul>
     *      <li>false: next value</li>
     *      <li>true: previous value</li>
     * </ul>
     */
    @NotNull ActionResult onUseWrench(ItemUsageContext useOnContext, Direction selectedFace, boolean nextPreviousValue);
}
