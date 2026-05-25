package me.jddev0.ep.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ItemDrop {
    void drops(Level level, BlockPos worldPosition);
}
