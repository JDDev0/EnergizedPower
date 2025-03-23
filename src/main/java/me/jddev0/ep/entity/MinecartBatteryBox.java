package me.jddev0.ep.entity;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.screen.MinecartBatteryBoxMenu;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MinecartBatteryBox extends AbstractMinecartBatteryBox {
    public static final long CAPACITY = ModConfigs.COMMON_BATTERY_BOX_MINECART_CAPACITY.getValue();
    public static final long MAX_TRANSFER = ModConfigs.COMMON_BATTERY_BOX_MINECART_TRANSFER_RATE.getValue();

    private static final TrackedData<Long> DATA_ID_ENERGY = DataTracker.registerData(MinecartBatteryBox.class, TrackedDataHandlerRegistry.LONG);

    protected final PropertyDelegate data = new CombinedContainerData(
            new EnergyValueContainerData(MinecartBatteryBox.this::getEnergy, MinecartBatteryBox.this::setEnergy),
            new EnergyValueContainerData(MinecartBatteryBox.this::getCapacity, value -> {})
    );

    public MinecartBatteryBox(EntityType<? extends MinecartBatteryBox> entityType, World level) {
        super(entityType, level);
    }

    public MinecartBatteryBox(World level, double x, double y, double z) {
        super(EPEntityTypes.BATTERY_BOX_MINECART, level, x, y, z);
    }

    @Override
    protected Item getItem() {
        return EPItems.BATTERY_BOX_MINECART;
    }

    public BlockState getDefaultContainedBlock() {
        return EPBlocks.BATTERY_BOX.getDefaultState();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new MinecartBatteryBoxMenu(id, inventory, this, data);
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
        return new ItemStack(EPItems.BATTERY_BOX_MINECART);
    }
}
