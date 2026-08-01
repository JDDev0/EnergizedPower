package me.jddev0.ep.block.entity;

import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface AutoCrafterMenuProvider {
    AbstractContainerMenu createMenu(int id, Inventory inv, BlockEntity blockEntity,
                                     UpgradeModuleInventory upgradeModuleInventory,
                                     Container[] patternSlots, Container[] patternResultSlots,
                                     ContainerData data);
}
