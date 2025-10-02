package me.jddev0.ep.screen;

import me.jddev0.ep.inventory.ConstraintInsertSlot;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import me.jddev0.ep.item.InventoryChargerItem;
import org.jetbrains.annotations.NotNull;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

public class InventoryChargerMenu extends ScreenHandler {
    private final Inventory inv;
    private final World level;

    public InventoryChargerMenu(int id, PlayerInventory inv) {
        this(id, inv, new SimpleInventory(InventoryChargerItem.SLOT_COUNT) {
            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < size()) {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(energyStorage == null)
                        return false;

                    return energyStorage.supportsExtraction();
                }

                return super.isValid(slot, stack);
            }

            @Override
            public int getMaxCountPerStack() {
                return 1;
            }
        });
    }

    public InventoryChargerMenu(int id, PlayerInventory playerInventory, Inventory inv) {
        super(EPMenuTypes.INVENTORY_CHARGER_MENU, id);

        checkSize(inv, InventoryChargerItem.SLOT_COUNT);
        this.inv = inv;
        this.level = playerInventory.player.getEntityWorld();

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        int slotIndex = 0;
        if(inv.size() >= 5)
            addSlot(new ConstraintInsertSlot(inv, slotIndex++, 44, 35));

        if(inv.size() >= 3)
            addSlot(new ConstraintInsertSlot(inv, slotIndex++, 62, 35));

        if(inv.size() >= 1)
            addSlot(new ConstraintInsertSlot(inv, slotIndex++, 80, 35));

        if(inv.size() >= 3)
            addSlot(new ConstraintInsertSlot(inv, slotIndex++, 98, 35));

        if(inv.size() >= 5)
            addSlot(new ConstraintInsertSlot(inv, slotIndex, 116, 35));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into tile inventory
            //Allow only 1 item
            int minFreeSlotIndex = 4 * 9;
            for(;minFreeSlotIndex < 4 * 9 + inv.size();minFreeSlotIndex++)
                if(!getSlot(minFreeSlotIndex).hasStack())
                    break;

            if(minFreeSlotIndex >= 4 * 9 + inv.size() || !insertItem(sourceItem, 4 * 9, 4 * 9 + inv.size(), false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + inv.size()) {
            //Tile inventory slot -> Merge into player inventory
            if(!insertItem(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.setStack(ItemStack.EMPTY);
        else
            sourceSlot.markDirty();

        sourceSlot.onTakeItem(player, sourceItem);

        return sourceItemCopy;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inv.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for(int i = 0;i < 3;i++) {
            for(int j = 0;j < 9;j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for(int i = 0;i < 9;i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
