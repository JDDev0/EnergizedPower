package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.ChargerBlockEntity;
import me.jddev0.ep.inventory.ItemCapabilityMenuHelper;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ChargerMenu extends AbstractContainerMenu implements EnergyStorageConsumerIndicatorBarMenu {
    private final ChargerBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public ChargerMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new SimpleContainerData(4));
    }

    public ChargerMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.CHARGER_MENU.get(), id);

        checkContainerDataCount(data, 4);
        this.blockEntity = (ChargerBlockEntity)blockEntity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        ItemCapabilityMenuHelper.getCapabilityItemHandler(this.level, this.blockEntity).ifPresent(itemHandler -> {
            addSlot(new SlotItemHandler(itemHandler, 0, 80, 35));
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
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1));
    }

    public RedstoneMode getRedstoneMode() {
        return RedstoneMode.fromIndex(data.get(2));
    }

    public ComparatorMode getComparatorMode() {
        return ComparatorMode.fromIndex(data.get(3));
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
            if(!moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
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
            sourceSlot.set(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceItem);

        return sourceItemCopy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.CHARGER.get());
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
