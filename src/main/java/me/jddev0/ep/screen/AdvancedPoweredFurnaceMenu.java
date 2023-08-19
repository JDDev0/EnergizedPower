package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.AdvancedPoweredFurnaceBlockEntity;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class AdvancedPoweredFurnaceMenu extends AbstractContainerMenu implements EnergyStorageConsumerIndicatorBarMenu {
    private final AdvancedPoweredFurnaceBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public AdvancedPoweredFurnaceMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new SimpleContainerData(21));
    }

    public AdvancedPoweredFurnaceMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.ADVANCED_POWERED_FURNACE_MENU.get(), id);

        checkContainerDataCount(data, 21);
        this.blockEntity = (AdvancedPoweredFurnaceBlockEntity)blockEntity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            addSlot(new SlotItemHandler(itemHandler, 0, 44, 17));
            addSlot(new SlotItemHandler(itemHandler, 1, 98, 17));
            addSlot(new SlotItemHandler(itemHandler, 2, 152, 17));
            addSlot(new SlotItemHandler(itemHandler, 3, 44, 53));
            addSlot(new SlotItemHandler(itemHandler, 4, 98, 53));
            addSlot(new SlotItemHandler(itemHandler, 5, 152, 53));
        });

        addDataSlots(this.data);
    }

    @Override
    public int getEnergy() {
        return blockEntity.getEnergy();
    }

    @Override
    public int getCapacity() {
        return blockEntity.getCapacity();
    }

    @Override
    public int getEnergyIndicatorBarValue() {
        int energyIndicatorBarValueSum = -1;

        for(int i = 0;i < 6;i += 2) {
            int value = ByteUtils.from2ByteChunks((short)data.get(i), (short)data.get(i + 1));

            //Prevent overflow
            if(energyIndicatorBarValueSum + value != (long)energyIndicatorBarValueSum + value)
                return Integer.MAX_VALUE;

            if(value > -1) {
                if(energyIndicatorBarValueSum == -1)
                    energyIndicatorBarValueSum++;

                energyIndicatorBarValueSum += value;
            }
        }

        return energyIndicatorBarValueSum;
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive(int index) {
        return ByteUtils.from2ByteChunks((short)data.get(6 + 4 * index), (short)data.get(7 + 4 * index)) > 0;
    }

    public boolean isCrafting(int index) {
        return ByteUtils.from2ByteChunks((short)data.get(8 + 4 * index), (short)data.get(9 + 4 * index)) > 0 && data.get(18 + index) == 1;
    }

    public int getScaledProgressArrowSize(int index) {
        int progress = ByteUtils.from2ByteChunks((short)data.get(6 + 4 * index), (short)data.get(7 + 4 * index));
        int maxProgress = ByteUtils.from2ByteChunks((short)data.get(8 + 4 * index), (short)data.get(9 + 4 * index));
        int progressArrowSize = 17;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
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
            if(!moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 3, false)) {
                //"+3" instead of "+6": Do not allow adding to output slots
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
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.ADVANCED_POWERED_FURNACE.get());
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
