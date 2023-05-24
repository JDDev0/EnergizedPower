package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.HeatGeneratorBlockEntity;
import me.jddev0.ep.energy.EnergyStorageMenuPacketUpdate;
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

public class HeatGeneratorMenu extends ScreenHandler implements EnergyStorageMenu, EnergyStorageMenuPacketUpdate {
    private final HeatGeneratorBlockEntity blockEntity;
    private final Inventory inv;
    private final World level;
    private final PropertyDelegate data;

    public HeatGeneratorMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv, new SimpleInventory(0),
                new ArrayPropertyDelegate(8));
    }

    public HeatGeneratorMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv, PropertyDelegate data) {
        super(ModMenuTypes.HEAT_GENERATOR_MENU, id);

        this.blockEntity = (HeatGeneratorBlockEntity)blockEntity;

        this.inv = inv;
        checkSize(this.inv, 0);
        checkDataCount(data, 8);
        this.level = playerInventory.player.world;
        this.inv.onOpen(playerInventory.player);
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(this.data);
    }

    @Override
    public long getEnergy() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1), (short)data.get(2), (short)data.get(3));
    }

    @Override
    public long getCapacity() {
        return ByteUtils.from2ByteChunks((short)data.get(4), (short)data.get(5), (short)data.get(6), (short)data.get(7));
    }

    @Override
    public int getScaledEnergyMeterPos(int energyBarHeight) {
        long energy = getEnergy();
        long capacity = getCapacity();

        return (int)((energy == 0 || capacity == 0)?0:Math.max(1, energy * energyBarHeight / capacity));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, ModBlocks.HEAT_GENERATOR);
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
            data.set(i, ByteUtils.get2Bytes(energy, i));
    }

    @Override
    public void setCapacity(long capacity) {
        for(int i = 0;i < 4;i++)
            data.set(i + 4, ByteUtils.get2Bytes(capacity, i));
    }
}
