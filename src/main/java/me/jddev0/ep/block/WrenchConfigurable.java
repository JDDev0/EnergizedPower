package me.jddev0.ep.block;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public interface WrenchConfigurable {
    /**
     * @param nextPreviousValue <ul>
     *      <li>false: next value</li>
     *      <li>true: previous value</li>
     * </ul>
     */
    @NotNull InteractionResult onUseWrench(UseOnContext useOnContext, Direction selectedFace, boolean nextPreviousValue);
}
