package me.jddev0.ep.block.entity.base;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface BasicMenuProvider {
    AbstractContainerMenu createMenu(int id, Inventory inv, BlockEntity blockEntity);
}
