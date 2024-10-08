package me.jddev0.ep.entity;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.entity.data.ModTrackedDataHandlers;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.screen.MinecartAdvancedBatteryBoxMenu;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MinecartAdvancedBatteryBox extends AbstractMinecartBatteryBox {
    public static final long CAPACITY = ModConfigs.COMMON_ADVANCED_BATTERY_BOX_MINECART_CAPACITY.getValue();
    public static final long MAX_TRANSFER = ModConfigs.COMMON_ADVANCED_BATTERY_BOX_MINECART_TRANSFER_RATE.getValue();

    private static final TrackedData<Long> DATA_ID_ENERGY = DataTracker.registerData(MinecartAdvancedBatteryBox.class, ModTrackedDataHandlers.LONG);

    protected final PropertyDelegate data = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch(index) {
                case 0, 1, 2, 3 -> ByteUtils.get2Bytes(MinecartAdvancedBatteryBox.this.getEnergy(), index);
                case 4, 5, 6, 7 -> ByteUtils.get2Bytes(MinecartAdvancedBatteryBox.this.getCapacity(), index - 4);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch(index) {
                case 0, 1, 2, 3 -> MinecartAdvancedBatteryBox.this.setEnergy(ByteUtils.with2Bytes(
                        MinecartAdvancedBatteryBox.this.getEnergy(), (short)value, index));
                case 4, 5, 6, 7 -> {}
            }
        }

        @Override
        public int size() {
            return 8;
        }
    };

    public MinecartAdvancedBatteryBox(EntityType<? extends MinecartAdvancedBatteryBox> entityType, World level) {
        super(entityType, level);
    }

    public MinecartAdvancedBatteryBox(World level, double x, double y, double z) {
        super(EPEntityTypes.ADVANCED_BATTERY_BOX_MINECART, level, x, y, z);
    }

    @Override
    protected Item getItem() {
        return EPItems.ADVANCED_BATTERY_BOX_MINECART;
    }

    public BlockState getDefaultContainedBlock() {
        return EPBlocks.ADVANCED_BATTERY_BOX.getDefaultState();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new MinecartAdvancedBatteryBoxMenu(id, inventory, this, data);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        dataTracker.startTracking(DATA_ID_ENERGY, 0L);
    }

    @Override
    public long getCapacity() {
        return CAPACITY;
    }

    @Override
    public long getTransferRate() {
        return MAX_TRANSFER;
    }

    public long getEnergy() {
        return dataTracker.get(DATA_ID_ENERGY);
    }

    public void setEnergy(long energy) {
        dataTracker.set(DATA_ID_ENERGY, energy);
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(EPItems.ADVANCED_BATTERY_BOX_MINECART);
    }
}
