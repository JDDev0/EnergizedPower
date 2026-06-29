package me.jddev0.ep.machine.upgrade;

import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IUpgradableMachine {
    /**
     * This method should only be called on the server-side, because the UpgradeModuleInventory is not synced to the client
     *
     * @return Leftover ItemStack
     */
    ItemStack onShiftClickUpgradeModuleInsertion(Player player, ItemStack stack);

    /**
     * This method should only be called on the server-side, because the UpgradeModuleInventory is not synced to the client
     */
    UpgradeModuleInventory getUpgradeModuleInventory();
}
