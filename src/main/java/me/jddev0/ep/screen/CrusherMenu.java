package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.CrusherBlockEntity;
import me.jddev0.ep.energy.EnergyStorageMenuPacketUpdate;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class CrusherMenu extends ScreenHandler implements EnergyStorageMenu, EnergyStorageMenuPacketUpdate {
    private final CrusherBlockEntity blockEntity;
    private final Inventory inv;
    private final World level;
    private final PropertyDelegate data;

    public CrusherMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv, new SimpleInventory(2),
                new ArrayPropertyDelegate(15));
    }

    public CrusherMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                       PropertyDelegate data) {
        super(ModMenuTypes.CRUSHER_MENU, id);

        this.blockEntity = (CrusherBlockEntity)blockEntity;

        this.inv = inv;
        checkSize(this.inv, 2);
        checkDataCount(data, 15);
        this.level = playerInventory.player.world;
        this.inv.onOpen(playerInventory.player);
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addSlot(new ConstraintInsertSlot(this.inv, 0, 48, 35));
        addSlot(new ConstraintInsertSlot(this.inv, 1, 124, 35));

        addProperties(this.data);
    }

    @Override
    public long getEnergy() {
        return ByteUtils.from2ByteChunks((short)data.get(2), (short)data.get(3), (short)data.get(4), (short)data.get(5));
    }

    @Override
    public long getCapacity() {
        return ByteUtils.from2ByteChunks((short)data.get(6), (short)data.get(7), (short)data.get(8), (short)data.get(9));
    }

    @Override
    public long getEnergyIndicatorBarValue() {
        return ByteUtils.from2ByteChunks((short)data.get(10), (short)data.get(11), (short)data.get(12), (short)data.get(13));
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive() {
        return data.get(0) > 0;
    }

    public boolean isCrafting() {
        return data.get(0) > 0 && data.get(14) == 1;
    }

    public int getScaledProgressArrowSize() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        int progressArrowSize = 24;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    @Override
    public int getScaledEnergyMeterPos(int energyMeterHeight) {
        long energy = getEnergy();
        long capacity = getCapacity();

        return (int)((energy == 0 || capacity == 0)?0:Math.max(1, energy * energyMeterHeight / capacity));
    }

    @Override
    public int getScaledEnergyIndicatorBarPos(int energyMeterHeight) {
        long energyRequirement = getEnergyIndicatorBarValue();
        long capacity = getCapacity();

        return (int)((energyRequirement <= 0 || capacity == 0)?0:(Math.min(energyRequirement, capacity - 1) * energyMeterHeight / capacity + 1));
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into tile inventory
            if(!insertItem(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                //"+1" instead of "+2": Do not allow adding to output slot
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 2) {
            //Tile inventory slot -> Merge into player inventory
            if(!insertItem(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.setStack(ItemStack.EMPTY);
        else
            sourceSlot.markDirty();

        sourceSlot.onTakeItem(player, sourceItem);

        return sourceItemCopy;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, ModBlocks.CRUSHER);
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

    @Override
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public void setEnergy(long energy) {
        for(int i = 0;i < 4;i++)
            data.set(i + 2, ByteUtils.get2Bytes(energy, i));
    }

    @Override
    public void setCapacity(long capacity) {
        for(int i = 0;i < 4;i++)
            data.set(i + 6, ByteUtils.get2Bytes(capacity, i));
    }
}
