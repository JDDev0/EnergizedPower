package me.jddev0.ep.screen.base;

import me.jddev0.ep.block.entity.base.EnergyStorageBlockEntity;
import me.jddev0.ep.inventory.UpgradeModuleViewContainerData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;

public abstract class UpgradableEnergyStorageMenu<T extends EnergyStorageBlockEntity<?>>
        extends EnergyStorageMenu<T> {
    protected final UpgradeModuleViewContainerData upgradeModuleViewContainerData;
    protected final UpgradeModuleInventory upgradeModuleInventory;

    protected UpgradableEnergyStorageMenu(@Nullable ScreenHandlerType<?> menuType, int id, PlayerInventory playerInventory,
                                          BlockEntity blockEntity, Block blockType,
                                          UpgradeModuleInventory upgradeModuleInventory, int upgradeModuleCount) {
        super(menuType, id, playerInventory, blockEntity, blockType);

        checkSize(upgradeModuleInventory, upgradeModuleCount);

        this.upgradeModuleViewContainerData = new UpgradeModuleViewContainerData();
        this.upgradeModuleInventory = upgradeModuleInventory;

        addProperties(upgradeModuleViewContainerData);
    }

    protected UpgradableEnergyStorageMenu(@Nullable ScreenHandlerType<?> menuType, int id, PlayerInventory playerInventory,
                                          BlockEntity blockEntity, Block blockType,
                                          int playerInventoryX, int playerInventoryY,
                                          UpgradeModuleInventory upgradeModuleInventory, int upgradeModuleCount) {
        super(menuType, id, playerInventory, blockEntity, blockType, playerInventoryX, playerInventoryY);

        checkSize(upgradeModuleInventory, upgradeModuleCount);

        this.upgradeModuleViewContainerData = new UpgradeModuleViewContainerData();
        this.upgradeModuleInventory = upgradeModuleInventory;

        addProperties(upgradeModuleViewContainerData);
    }

    @Override
    public boolean isInUpgradeModuleView() {
        return upgradeModuleViewContainerData.isInUpgradeModuleView();
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int index) {
        if(index == 0) {
            upgradeModuleViewContainerData.toggleInUpgradeModuleView();

            sendContentUpdates();
        }

        return false;
    }
}
