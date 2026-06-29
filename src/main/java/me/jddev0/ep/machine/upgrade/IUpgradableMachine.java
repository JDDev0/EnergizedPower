package me.jddev0.ep.machine.upgrade;

import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IUpgradableMachine {
    /**
     * @return Leftover ItemStack
     */
    ItemStack onShiftClickUpgradeModuleInsertion(Player player, ItemStack stack);

    UpgradeModuleInventory getUpgradeModuleInventory();
}
