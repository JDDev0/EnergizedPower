package me.jddev0.ep.screen;

import me.jddev0.ep.block.entity.ItemSiloBlockEntity;
import me.jddev0.ep.inventory.ItemCapabilityMenuHelper;
import me.jddev0.ep.inventory.SingleItemStackHandler;
import me.jddev0.ep.inventory.ViewOnlySlot;
import me.jddev0.ep.inventory.data.SimpleIntegerValueContainerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemSiloMenu extends AbstractContainerMenu {
    private final SimpleIntegerValueContainerData countData = new SimpleIntegerValueContainerData();
    private final SimpleIntegerValueContainerData maxCountData = new SimpleIntegerValueContainerData();

    private final ItemSiloBlockEntity blockEntity;
    private final Level level;

    public ItemSiloMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), null);
    }

    public ItemSiloMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(((ItemSiloBlockEntity)blockEntity).getTier().getMenuTypeFromTier(), id);

        this.blockEntity = (ItemSiloBlockEntity)blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        ItemCapabilityMenuHelper.getCapabilityItemHandler(this.level, this.blockEntity).ifPresent(itemHandler -> {
            if(itemHandler instanceof SingleItemStackHandler singleItemStackHandler) {
                addSlot(new ViewOnlySlot(singleItemStackHandler, 0, 80, 35));
            }
        });

        if(data == null) {
            addDataSlots(countData);
            addDataSlots(maxCountData);
        }else {
            addDataSlots(data);
        }
    }

    public int getCount() {
        return countData.getValue();
    }

    public int getMaxCount() {
        return maxCountData.getValue();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, blockEntity.getTier().getBlockFromTier());
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

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
