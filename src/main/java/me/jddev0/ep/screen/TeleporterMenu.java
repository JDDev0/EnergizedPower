package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.TeleporterBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.screen.base.EnergyStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TeleporterMenu extends EnergyStorageMenu<TeleporterBlockEntity> {
    public TeleporterMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv.player.level().getBlockEntity(pos), inv, new SimpleContainer(1) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                if(slot == 0) {
                    return stack.is(EPItems.TELEPORTER_MATRIX);
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
    }

    public TeleporterMenu(int id, BlockEntity blockEntity, Inventory playerInventory, Container inv) {
        super(
                EPMenuTypes.TELEPORTER_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.TELEPORTER
        );

        addSlot(new ConstraintInsertSlot(inv, 0, 80, 35));
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
            if(slots.get(4 * 9).hasItem() || !moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1) {
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
}
