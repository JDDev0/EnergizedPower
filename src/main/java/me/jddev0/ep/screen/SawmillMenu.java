package me.jddev0.ep.screen;

import me.jddev0.ep.block.entity.SawmillBlockEntity;
import me.jddev0.ep.energy.EnergyStorageMenuPacketUpdate;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
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
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class SawmillMenu extends ScreenHandler implements EnergyStorageMenuPacketUpdate {
    private final SawmillBlockEntity blockEntity;
    private final Inventory inv;
    private final World level;
    private final PropertyDelegate data;

    public SawmillMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.world.getBlockEntity(buf.readBlockPos()), inv, new SimpleInventory(3),
                new ArrayPropertyDelegate(6));
    }

    public SawmillMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv, PropertyDelegate data) {
        super(ModMenuTypes.SAWMILL_MENU, id);

        this.blockEntity = (SawmillBlockEntity)blockEntity;

        this.inv = inv;
        checkSize(this.inv, 3);
        this.level = playerInventory.player.world;
        this.inv.onOpen(playerInventory.player);
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addSlot(new ConstraintInsertSlot(this.inv, 0, 43, 35));
        addSlot(new ConstraintInsertSlot(this.inv, 1, 107, 35));
        addSlot(new ConstraintInsertSlot(this.inv, 2, 134, 35));

        addProperties(this.data);
    }

    int getEnergy() {
        return data.get(2);
    }

    int getCapacity() {
        return data.get(3);
    }

    int getEnergyRequirement() {
        return data.get(4);
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive() {
        return data.get(0) > 0;
    }

    public boolean isCrafting() {
        return data.get(0) > 0 && data.get(5) == 1;
    }

    public int getScaledProgressArrowSize() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        int progressArrowSize = 24;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    public int getScaledEnergyMeterPos() {
        int energy = getEnergy();
        int capacity = getCapacity();
        int energyBarSize = 52;

        return (energy == 0 || capacity == 0)?0:Math.max(1, energy * energyBarSize / capacity);
    }

    public int getEnergyRequirementBarPos() {
        int energyRequirement = getEnergyRequirement();
        int capacity = getCapacity();
        int energyBarSize = 52;

        return (energyRequirement <= 0 || capacity == 0)?0:(Math.min(energyRequirement, capacity - 1) * energyBarSize / capacity + 1);
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
                //"+1" instead of "+3": Do not allow adding to output slots
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 3) {
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
        return inv.canPlayerUse(player);
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
        data.set(2, (int)energy);
    }

    @Override
    public void setCapacity(long capacity) {
        data.set(3, (int)capacity);
    }
}
