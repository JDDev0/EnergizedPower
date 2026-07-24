package me.jddev0.ep.entity;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.data.CombinedContainerData;
import me.jddev0.ep.inventory.data.EnergyValueContainerData;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.screen.MinecartEliteBatteryBoxMenu;
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

public class MinecartEliteBatteryBox extends AbstractMinecartBatteryBox {
    public static final long CAPACITY = ModConfigs.COMMON_ELITE_BATTERY_BOX_MINECART_CAPACITY.getValue();
    public static final long MAX_TRANSFER = ModConfigs.COMMON_ELITE_BATTERY_BOX_MINECART_TRANSFER_RATE.getValue();

    private static final EntityDataAccessor<Long> DATA_ID_ENERGY =
            SynchedEntityData.defineId(MinecartEliteBatteryBox.class, EntityDataSerializers.LONG);

    protected final ContainerData data = new CombinedContainerData(
            new EnergyValueContainerData(MinecartEliteBatteryBox.this::getEnergy, MinecartEliteBatteryBox.this::setEnergy),
            new EnergyValueContainerData(MinecartEliteBatteryBox.this::getCapacity, value -> {})
    );

    public MinecartEliteBatteryBox(EntityType<? extends MinecartEliteBatteryBox> entityType, Level level) {
        super(entityType, level);
    }

    public MinecartEliteBatteryBox(Level level, double x, double y, double z) {
        super(EPEntityTypes.ELITE_BATTERY_BOX_MINECART, level, x, y, z);
    }

    @Override
    protected Item getDropItem() {
        return EPItems.ELITE_BATTERY_BOX_MINECART;
    }

    public BlockState getDefaultDisplayBlockState() {
        return EPBlocks.ELITE_BATTERY_BOX.defaultBlockState();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new MinecartEliteBatteryBoxMenu(id, inventory, this, data);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builer) {
        super.defineSynchedData(builer);

        builer.define(DATA_ID_ENERGY, 0L);
    }

    @Override
    public long getCapacity() {
        return CAPACITY;
    }

    @Override
    public long getTransferRate() {
        return MAX_TRANSFER;
    }

    @Override
    public long getEnergy() {
        return entityData.get(DATA_ID_ENERGY);
    }

    @Override
    public void setEnergy(long energy) {
        entityData.set(DATA_ID_ENERGY, energy);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(EPItems.ELITE_BATTERY_BOX_MINECART);
    }
}
