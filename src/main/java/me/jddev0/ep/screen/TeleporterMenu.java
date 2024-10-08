package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.TeleporterBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;

public class TeleporterMenu extends EnergyStorageMenu<TeleporterBlockEntity> {
    public TeleporterMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv, new SimpleInventory(1) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot == 0) {
                    return stack.isOf(EPItems.TELEPORTER_MATRIX);
                }

                return super.isValid(slot, stack);
            }

            @Override
            public int getMaxCountPerStack() {
                return 1;
            }
        });
    }

    public TeleporterMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv) {
        super(
                EPMenuTypes.TELEPORTER_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.TELEPORTER
        );

        addSlot(new ConstraintInsertSlot(inv, 0, 80, 35));
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
}
