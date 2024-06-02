package me.jddev0.ep.screen;

import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.screen.base.AbstractEnergizedPowerScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class InventoryTeleporterMenu extends AbstractEnergizedPowerScreenHandler {
    private final Inventory inv;
    private final World level;

    public InventoryTeleporterMenu(int id, PlayerInventory inv) {
        this(id, inv, new SimpleInventory(1) {
            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < size()) {
                    return stack.isOf(ModItems.TELEPORTER_MATRIX);
                }

                return super.isValid(slot, stack);
            }

            @Override
            public int getMaxCountPerStack() {
                return 1;
            }
        });
    }

    public InventoryTeleporterMenu(int id, PlayerInventory playerInventory, SimpleInventory inv) {
        super(ModMenuTypes.INVENTORY_TELEPORTER_MENU, id);

        checkSize(inv, 1);
        this.inv = inv;
        this.level = playerInventory.player.getWorld();

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addSlot(new ConstraintInsertSlot(inv, 0, 80, 35));
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into tile inventory
            //Allow only 1 item
            if(!insertMaxCount1Item(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1) {
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
