package me.jddev0.ep.screen;

import me.jddev0.ep.inventory.ConstraintInsertSlot;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import me.jddev0.ep.item.InventoryChargerItem;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

public class InventoryChargerMenu extends AbstractContainerMenu {
    private final Container inv;
    private final Level level;

    public InventoryChargerMenu(int id, Inventory inv) {
        this(id, inv, new SimpleContainer(InventoryChargerItem.SLOT_COUNT) {
            @Override
            public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < getContainerSize()) {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(energyStorage == null)
                        return false;

                    return energyStorage.supportsExtraction();
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
    }

    public InventoryChargerMenu(int id, Inventory playerInventory, Container inv) {
        super(EPMenuTypes.INVENTORY_CHARGER_MENU, id);

        checkContainerSize(inv, InventoryChargerItem.SLOT_COUNT);
        this.inv = inv;
        this.level = playerInventory.player.level();

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        int slotIndex = 0;
        if(inv.getContainerSize() >= 5)
            addSlot(new ConstraintInsertSlot(inv, slotIndex++, 44, 35));

        if(inv.getContainerSize() >= 3)
            addSlot(new ConstraintInsertSlot(inv, slotIndex++, 62, 35));

        if(inv.getContainerSize() >= 1)
            addSlot(new ConstraintInsertSlot(inv, slotIndex++, 80, 35));

        if(inv.getContainerSize() >= 3)
            addSlot(new ConstraintInsertSlot(inv, slotIndex++, 98, 35));

        if(inv.getContainerSize() >= 5)
            addSlot(new ConstraintInsertSlot(inv, slotIndex, 116, 35));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getItem();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into tile inventory
            //Allow only 1 item
            int minFreeSlotIndex = 4 * 9;
            for(;minFreeSlotIndex < 4 * 9 + inv.getContainerSize();minFreeSlotIndex++)
                if(!getSlot(minFreeSlotIndex).hasItem())
                    break;

            if(minFreeSlotIndex >= 4 * 9 + inv.getContainerSize() || !moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + inv.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + inv.getContainerSize()) {
            //Tile inventory slot -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.setByPlayer(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceItem);

        return sourceItemCopy;
    }

    @Override
    public boolean stillValid(Player player) {
        return inv.stillValid(player);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for(int i = 0;i < 3;i++) {
            for(int j = 0;j < 9;j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for(int i = 0;i < 9;i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
