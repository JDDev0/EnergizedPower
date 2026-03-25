package me.jddev0.ep.screen.base;

import me.jddev0.ep.block.entity.base.EnergyStorageBlockEntity;
import me.jddev0.ep.inventory.UpgradeModuleViewContainerData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class UpgradableEnergyStorageMenu<T extends EnergyStorageBlockEntity<?>>
        extends EnergyStorageMenu<T> {
    protected final UpgradeModuleViewContainerData upgradeModuleViewContainerData;
    protected final UpgradeModuleInventory upgradeModuleInventory;

    protected UpgradableEnergyStorageMenu(@Nullable MenuType<?> menuType, int id, Inventory playerInventory,
                                          BlockEntity blockEntity, Block blockType,
                                          UpgradeModuleInventory upgradeModuleInventory, int upgradeModuleCount) {
        super(menuType, id, playerInventory, blockEntity, blockType);

        checkContainerSize(upgradeModuleInventory, upgradeModuleCount);

        this.upgradeModuleViewContainerData = new UpgradeModuleViewContainerData();
        this.upgradeModuleInventory = upgradeModuleInventory;

        addDataSlots(upgradeModuleViewContainerData);
    }

    protected UpgradableEnergyStorageMenu(@Nullable MenuType<?> menuType, int id, Inventory playerInventory,
                                          BlockEntity blockEntity, Block blockType,
                                          int playerInventoryX, int playerInventoryY,
                                          UpgradeModuleInventory upgradeModuleInventory, int upgradeModuleCount) {
        super(menuType, id, playerInventory, blockEntity, blockType, playerInventoryX, playerInventoryY);

        checkContainerSize(upgradeModuleInventory, upgradeModuleCount);

        this.upgradeModuleViewContainerData = new UpgradeModuleViewContainerData();
        this.upgradeModuleInventory = upgradeModuleInventory;

        addDataSlots(upgradeModuleViewContainerData);
    }

    @Override
    public boolean isInUpgradeModuleView() {
        return upgradeModuleViewContainerData.isInUpgradeModuleView();
    }

    @Override
    public boolean clickMenuButton(Player player, int index) {
        if(index == 0) {
            upgradeModuleViewContainerData.toggleInUpgradeModuleView();

            broadcastChanges();
        }

        return false;
    }
}
