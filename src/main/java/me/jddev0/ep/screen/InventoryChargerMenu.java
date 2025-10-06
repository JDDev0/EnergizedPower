package me.jddev0.ep.screen;

import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.item.InventoryChargerItem;
import me.jddev0.ep.util.CapabilityUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import org.jetbrains.annotations.NotNull;

public class InventoryChargerMenu extends AbstractContainerMenu {
    private final Container container;
    private final Level level;

    public InventoryChargerMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, new SimpleContainer(InventoryChargerItem.SLOT_COUNT) {
            @Override
            public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < getContainerSize()) {
                    EnergyHandler energyStorage = CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Energy.ITEM, stack);
                    return energyStorage != null && CapabilityUtil.canExtract(energyStorage);
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
    }

    public InventoryChargerMenu(int id, Inventory inv, Container container) {
        super(EPMenuTypes.INVENTORY_CHARGER_MENU.get(), id);

        checkContainerSize(inv, InventoryChargerItem.SLOT_COUNT);
        this.container = container;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        int slotIndex = 0;
        if(container.getContainerSize() >= 5)
            addSlot(new ConstraintInsertSlot(container, slotIndex++, 44, 35));

        if(container.getContainerSize() >= 3)
            addSlot(new ConstraintInsertSlot(container, slotIndex++, 62, 35));

        if(container.getContainerSize() >= 1)
            addSlot(new ConstraintInsertSlot(container, slotIndex++, 80, 35));

        if(container.getContainerSize() >= 3)
            addSlot(new ConstraintInsertSlot(container, slotIndex++, 98, 35));

        if(container.getContainerSize() >= 5)
            addSlot(new ConstraintInsertSlot(container, slotIndex, 116, 35));
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
            if(!moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + container.getContainerSize()) {
            //Tile inventory slot -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.set(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceItem);

        return sourceItemCopy;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
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
