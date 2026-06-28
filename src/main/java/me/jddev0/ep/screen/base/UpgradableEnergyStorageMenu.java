package me.jddev0.ep.screen.base;

import me.jddev0.ep.block.entity.base.EnergyStorageBlockEntity;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.UpgradeModuleViewContainerData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
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

    protected boolean moveUpgradeModuleItemStackTo(
            ItemStack sourceItemStack,
            int startSlot, int endSlot,
            Player player, int playerStartSlot, int playerEndSlot,
            boolean backwards
    ) {
        if(!(sourceItemStack.getItem() instanceof UpgradeModuleItem sourceUpgradeModuleItem)) {
            return false;
        }

        boolean anythingChanged = false;

        int destSlot = backwards?endSlot - 1:startSlot;
        while(!sourceItemStack.isEmpty() && (backwards?destSlot >= startSlot:destSlot < endSlot)) {
            Slot targetSlot = slots.get(destSlot);
            ItemStack targetItemStack = targetSlot.getItem();
            if(targetSlot.container instanceof UpgradeModuleInventory umi && sourceUpgradeModuleItem.getMainUpgradeModuleModifier() == umi.
                    getUpgradeModifierSlots()[targetSlot.getContainerSlot()] && !ItemStack.isSameItemSameComponents(sourceItemStack, targetItemStack)) {
                if(targetItemStack.isEmpty()) {
                    anythingChanged |= moveItemStackTo(sourceItemStack, destSlot, destSlot + 1, backwards);
                }else if(targetItemStack.getItem() instanceof UpgradeModuleItem targetUpgradeModuleItem && (
                        targetUpgradeModuleItem.shouldIgnoreTierValueForItemSwapping() ||
                                sourceUpgradeModuleItem.getUpgradeModuleTier() > targetUpgradeModuleItem.getUpgradeModuleTier())) {
                    if(moveItemStackTo(targetItemStack, playerStartSlot, playerEndSlot, backwards)) {
                        anythingChanged = true;

                        if(targetItemStack.getCount() == 0)
                            targetSlot.set(ItemStack.EMPTY);
                        else
                            targetSlot.setChanged();

                        targetSlot.onTake(player, targetItemStack);

                        moveItemStackTo(sourceItemStack, destSlot, destSlot + 1, backwards);
                    }
                }
            }

            if(backwards) {
                destSlot--;
            }else {
                destSlot++;
            }
        }

        return anythingChanged;
    }
}
