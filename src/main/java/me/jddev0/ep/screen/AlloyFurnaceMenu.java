package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AlloyFurnaceBlockEntity;
import me.jddev0.ep.inventory.ItemCapabilityMenuHelper;
import me.jddev0.ep.inventory.data.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class AlloyFurnaceMenu extends AbstractContainerMenu {
    private final AlloyFurnaceBlockEntity blockEntity;
    private final Level level;

    private final SimpleProgressValueContainerData progressData = new SimpleProgressValueContainerData();
    private final SimpleProgressValueContainerData maxProgressData = new SimpleProgressValueContainerData();
    private final SimpleProgressValueContainerData litDurationData = new SimpleProgressValueContainerData();
    private final SimpleProgressValueContainerData maxLitDurationData = new SimpleProgressValueContainerData();

    public AlloyFurnaceMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), null);
    }

    public AlloyFurnaceMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(EPMenuTypes.ALLOY_FURNACE_MENU.get(), id);

        this.blockEntity = (AlloyFurnaceBlockEntity)blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        ItemCapabilityMenuHelper.getEnergizedPowerItemStackHandlerCapability(this.level, this.blockEntity).ifPresent(itemHandler -> {
            addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, 0, 14, 20));
            addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, 1, 35, 17));
            addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, 2, 56, 20));
            addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, 3, 35, 53));
            addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, 4, 116, 35));
            addSlot(new ResourceHandlerSlot(itemHandler, itemHandler::set, 5, 143, 35));
        });

        if(data == null) {
            addDataSlots(progressData);
            addDataSlots(maxProgressData);
            addDataSlots(litDurationData);
            addDataSlots(maxLitDurationData);
        }else {
            addDataSlots(data);
        }
    }

    public boolean isCraftingActive() {
        return progressData.getValue() > 0;
    }

    public int getScaledProgressArrowSize() {
        int progress = progressData.getValue();
        int maxProgress = maxProgressData.getValue();
        int progressArrowSize = 24;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    public boolean isBurningFuel() {
        return litDurationData.getValue() > 0;
    }

    public int getScaledProgressFlameSize() {
        int progress = litDurationData.getValue();
        int maxProgress = maxLitDurationData.getValue();
        int progressFlameSize = 14;

        return (maxProgress == 0 || progress == 0)?0:(progress * progressFlameSize / maxProgress);
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
            if(!moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 4, false)) {
                //"+4" instead of "+6": Do not allow adding to output slot
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 6) {
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
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, EPBlocks.ALLOY_FURNACE.get());
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
