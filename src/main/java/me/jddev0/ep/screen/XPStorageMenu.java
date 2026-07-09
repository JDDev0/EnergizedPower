package me.jddev0.ep.screen;

import me.jddev0.ep.block.entity.XPStorageBlockEntity;
import me.jddev0.ep.inventory.data.SimpleXPValueContainerData;
import me.jddev0.ep.machine.tier.XPStorageTier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class XPStorageMenu extends AbstractContainerMenu {
    private final XPStorageBlockEntity blockEntity;
    private final Level level;

    private final SimpleXPValueContainerData xpAmountData = new SimpleXPValueContainerData();
    private final SimpleXPValueContainerData xpCapacityData = new SimpleXPValueContainerData();

    public XPStorageMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv, inv.player.level().getBlockEntity(pos), null);
    }

    public XPStorageMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(((XPStorageBlockEntity)blockEntity).getTier().getMenuTypeFromTier(), id);

        this.blockEntity = (XPStorageBlockEntity)blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        if(data == null) {
            addDataSlots(xpAmountData);
            addDataSlots(xpCapacityData);
        }else {
            addDataSlots(data);
        }
    }

    public XPStorageTier getTier() {
        return blockEntity.getTier();
    }

    public long getXPAmount() {
        return xpAmountData.getValue();
    }

    public long getXPCapacity() {
        return xpCapacityData.getValue();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, getTier().getBlockFromTier());
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
