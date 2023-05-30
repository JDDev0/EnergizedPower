package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.energy.EnergyStorageMenuPacketUpdate;
import me.jddev0.ep.inventory.PatternResultSlot;
import me.jddev0.ep.inventory.PatternSlot;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class AutoCrafterMenu extends AbstractContainerMenu implements EnergyStorageConsumerIndicatorBarMenu, EnergyStorageMenuPacketUpdate {
    private final AutoCrafterBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    private final Container patternSlots;

    private final Container patternResultSlots;

    public AutoCrafterMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level.getBlockEntity(buffer.readBlockPos()), new SimpleContainer(9), new SimpleContainer(1), new SimpleContainerData(11));
    }

    public AutoCrafterMenu(int id, Inventory inv, BlockEntity blockEntity, Container patternSlots, Container patternResultSlots, ContainerData data) {
        super(ModMenuTypes.AUTO_CRAFTER_MENU.get(), id);

        this.patternSlots = patternSlots;
        this.patternResultSlots = patternResultSlots;

        checkContainerSize(inv, 18 + 3*3 + 1);
        checkContainerDataCount(data, 11);
        this.blockEntity = (AutoCrafterBlockEntity)blockEntity;
        this.level = inv.player.level;
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
            addSlot(new SlotItemHandler(itemHandler, 0, 8, 75));
            addSlot(new SlotItemHandler(itemHandler, 1, 26, 75));
            addSlot(new SlotItemHandler(itemHandler, 2, 44, 75));
            addSlot(new SlotItemHandler(itemHandler, 3, 62, 75));
            addSlot(new SlotItemHandler(itemHandler, 4, 80, 75));
            addSlot(new SlotItemHandler(itemHandler, 5, 98, 75));
            addSlot(new SlotItemHandler(itemHandler, 6, 116, 75));
            addSlot(new SlotItemHandler(itemHandler, 7, 134, 75));
            addSlot(new SlotItemHandler(itemHandler, 8, 152, 75));
            addSlot(new SlotItemHandler(itemHandler, 9, 8, 93));
            addSlot(new SlotItemHandler(itemHandler, 10, 26, 93));
            addSlot(new SlotItemHandler(itemHandler, 11, 44, 93));
            addSlot(new SlotItemHandler(itemHandler, 12, 62, 93));
            addSlot(new SlotItemHandler(itemHandler, 13, 80, 93));
            addSlot(new SlotItemHandler(itemHandler, 14, 98, 93));
            addSlot(new SlotItemHandler(itemHandler, 15, 116, 93));
            addSlot(new SlotItemHandler(itemHandler, 16, 134, 93));
            addSlot(new SlotItemHandler(itemHandler, 17, 152, 93));
        });

        for(int i = 0;i < 3;i++)
            for(int j = 0;j < 3;j++)
                addSlot(new PatternSlot(patternSlots, j + i * 3, 30 + j * 18, 17 + i * 18, () -> true));

        addSlot(new PatternResultSlot(patternResultSlots, 0, 124, 35, () -> true));

        addDataSlots(this.data);
    }

    public Container getPatternSlots() {
        return patternSlots;
    }

    @Override
    public int getEnergy() {
        return ByteUtils.from2ByteChunks((short)data.get(2), (short)data.get(3));
    }

    @Override
    public int getCapacity() {
        return ByteUtils.from2ByteChunks((short)data.get(4), (short)data.get(5));
    }

    @Override
    public int getEnergyIndicatorBarValue() {
        return ByteUtils.from2ByteChunks((short)data.get(6), (short)data.get(7));
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive() {
        return data.get(0) > 0;
    }

    public boolean isCrafting() {
        return data.get(0) > 0 && data.get(8) == 1;
    }

    public int getScaledProgressArrowSize() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        int progressArrowSize = 24;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    public boolean isIgnoreNBT() {
        return data.get(9) != 0;
    }

    public boolean isSecondaryExtractMode() {
        return data.get(10) != 0;
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
            //"+ 18": Ignore 3x3 crafting grid and result slot
            if(!moveItemStackTo(sourceItem, 4 * 9 + 3, 4 * 9 + 18, false)) {
                //"+3" instead of nothing: Do not allow adding to first 3 output item only slot
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 18) {
            //Tile inventory slot -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 18 + 3*3 + 1) {
            return ItemStack.EMPTY;
        }else{
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
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.AUTO_CRAFTER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for(int i = 0;i < 3;i++) {
            for(int j = 0;j < 9;j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 124 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for(int i = 0;i < 9;i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 182));
        }
    }

    @Override
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public void setEnergy(int energy) {
        for(int i = 0;i < 2;i++)
            data.set(i + 2, ByteUtils.get2Bytes(energy, i));
    }

    @Override
    public void setCapacity(int capacity) {
        for(int i = 0;i < 2;i++)
            data.set(i + 4, ByteUtils.get2Bytes(capacity, i));
    }
}
