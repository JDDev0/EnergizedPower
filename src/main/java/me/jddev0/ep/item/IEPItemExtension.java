package me.jddev0.ep.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public interface IEPItemExtension {
    default InteractionResult onItemUseFirst(UseOnContext useOnContext) {
        return InteractionResult.PASS;
    }
}
