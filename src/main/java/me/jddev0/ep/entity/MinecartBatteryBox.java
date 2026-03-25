package me.jddev0.ep.entity;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.screen.MinecartBatteryBoxMenu;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MinecartBatteryBox extends AbstractMinecartBatteryBox {
    public static final long CAPACITY = ModConfigs.COMMON_BATTERY_BOX_MINECART_CAPACITY.getValue();
    public static final long MAX_TRANSFER = ModConfigs.COMMON_BATTERY_BOX_MINECART_TRANSFER_RATE.getValue();

    private static final EntityDataAccessor<Long> DATA_ID_ENERGY = SynchedEntityData.defineId(MinecartBatteryBox.class, EntityDataSerializers.LONG);

    protected final ContainerData data = new CombinedContainerData(
            new EnergyValueContainerData(MinecartBatteryBox.this::getEnergy, MinecartBatteryBox.this::setEnergy),
            new EnergyValueContainerData(MinecartBatteryBox.this::getCapacity, value -> {})
    );

    public MinecartBatteryBox(EntityType<? extends MinecartBatteryBox> entityType, Level level) {
        super(entityType, level);
    }

    public MinecartBatteryBox(Level level, double x, double y, double z) {
        super(EPEntityTypes.BATTERY_BOX_MINECART, level, x, y, z);
    }

    @Override
    protected Item getDropItem() {
        return EPItems.BATTERY_BOX_MINECART;
    }

    public BlockState getDefaultDisplayBlockState() {
        return EPBlocks.BATTERY_BOX.defaultBlockState();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new MinecartBatteryBoxMenu(id, inventory, this, data);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);

        builder.define(DATA_ID_ENERGY, 0L);
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
        return entityData.get(DATA_ID_ENERGY);
    }

    public void setEnergy(long energy) {
        entityData.set(DATA_ID_ENERGY, energy);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(EPItems.BATTERY_BOX_MINECART);
    }
}
