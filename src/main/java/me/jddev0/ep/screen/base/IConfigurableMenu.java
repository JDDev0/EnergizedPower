package me.jddev0.ep.screen.base;

import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import net.minecraft.block.entity.BlockEntity;

public interface IConfigurableMenu {
    BlockEntity getBlockEntity();

    RedstoneMode getRedstoneMode();
    ComparatorMode getComparatorMode();
}
