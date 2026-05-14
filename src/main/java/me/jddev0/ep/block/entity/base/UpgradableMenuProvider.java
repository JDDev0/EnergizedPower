package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;

@FunctionalInterface
public interface UpgradableMenuProvider {
    AbstractContainerMenu createMenu(int id, BlockEntity blockEntity, Inventory inv, Container inventory,
                             UpgradeModuleInventory upgradeModuleInventory, ContainerData data);
}
