package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.ItemConveyorBeltSorterBlockEntity;
import me.jddev0.ep.inventory.PatternSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemConveyorBeltSorterMenu extends AbstractContainerMenu {
    private final ItemConveyorBeltSorterBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    private final Container patternSlots;

    public ItemConveyorBeltSorterMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new SimpleContainer(15), new SimpleContainerData(9));
    }

    public ItemConveyorBeltSorterMenu(int id, Inventory inv, BlockEntity blockEntity, Container patternSlots, ContainerData data) {
        super(ModMenuTypes.ITEM_CONVEYOR_BELT_SORTER_MENU.get(), id);

        this.patternSlots = patternSlots;

        checkContainerDataCount(data, 9);
        this.blockEntity = (ItemConveyorBeltSorterBlockEntity)blockEntity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        for(int i = 0;i < 3;i++)
            for(int j = 0;j < 5;j++)
                addSlot(new PatternSlot(patternSlots, j + i * 5, 44 + j * 18, 17 + i * 18, () -> true));

        addDataSlots(this.data);
    }

    public Container getPatternSlots() {
        return patternSlots;
    }

    public boolean isOutputBeltConnected(int index) {
        return data.get(index) != 0;
    }

    public boolean isWhitelist(int index) {
        return data.get(index + 3) != 0;
    }

    public boolean isIgnoreNBT(int index) {
        return data.get(index + 6) != 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.ITEM_CONVEYOR_BELT_SORTER.get());
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
