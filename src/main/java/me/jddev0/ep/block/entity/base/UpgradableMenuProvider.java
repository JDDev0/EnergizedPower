package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;

@FunctionalInterface
public interface UpgradableMenuProvider {
    ScreenHandler createMenu(int id, BlockEntity blockEntity, PlayerInventory inv, Inventory inventory,
                             UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data);
}
