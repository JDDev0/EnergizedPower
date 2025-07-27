package me.jddev0.ep.screen;

import me.jddev0.ep.block.entity.ItemConveyorBeltSorterBlockEntity;
import me.jddev0.ep.inventory.PatternSlot;
import me.jddev0.ep.inventory.data.SimpleBooleanValueContainerData;
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

    private final Container patternSlots;

    private final SimpleBooleanValueContainerData[] outputBeltConnectedData = new SimpleBooleanValueContainerData[] {
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData()
    };
    private final SimpleBooleanValueContainerData[] whitelistData = new SimpleBooleanValueContainerData[] {
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData()
    };
    private final SimpleBooleanValueContainerData[] ignoreNBTData = new SimpleBooleanValueContainerData[] {
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData()
    };

    public ItemConveyorBeltSorterMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new SimpleContainer(15), null);
    }

    public ItemConveyorBeltSorterMenu(int id, Inventory inv, BlockEntity blockEntity, Container patternSlots, ContainerData data) {
        super(((ItemConveyorBeltSorterBlockEntity)blockEntity).getTier().getItemConveyorBeltSorterMenuTypeFromTier(), id);

        this.patternSlots = patternSlots;

        this.blockEntity = (ItemConveyorBeltSorterBlockEntity)blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        for(int i = 0;i < 3;i++)
            for(int j = 0;j < 5;j++)
                addSlot(new PatternSlot(patternSlots, j + i * 5, 44 + j * 18, 17 + i * 18, () -> true));

        if(data == null) {
            addDataSlots(outputBeltConnectedData[0]);
            addDataSlots(outputBeltConnectedData[1]);
            addDataSlots(outputBeltConnectedData[2]);
            addDataSlots(whitelistData[0]);
            addDataSlots(whitelistData[1]);
            addDataSlots(whitelistData[2]);
            addDataSlots(ignoreNBTData[0]);
            addDataSlots(ignoreNBTData[1]);
            addDataSlots(ignoreNBTData[2]);
        }else {
            addDataSlots(data);
        }
    }

    public Container getPatternSlots() {
        return patternSlots;
    }

    public boolean isOutputBeltConnected(int index) {
        return outputBeltConnectedData[index].getValue();
    }

    public boolean isWhitelist(int index) {
        return whitelistData[index].getValue();
    }

    public boolean isIgnoreNBT(int index) {
        return ignoreNBTData[index].getValue();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, blockEntity.getTier().getItemConveyorBeltSorterBlockFromTier());
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
