package me.jddev0.ep.entity;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.screen.MinecartBatteryBoxMenu;
import me.jddev0.ep.util.ByteUtils;
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
    public static final int CAPACITY = 65536;
    public static final int MAX_TRANSFER = 512;

    private static final EntityDataAccessor<Integer> DATA_ID_ENERGY =
            SynchedEntityData.defineId(MinecartBatteryBox.class, EntityDataSerializers.INT);

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch(index) {
                case 0, 1 -> ByteUtils.get2Bytes(MinecartBatteryBox.this.getEnergy(), index);
                case 2, 3 -> ByteUtils.get2Bytes(MinecartBatteryBox.this.getCapacity(), index);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch(index) {
                case 0, 1 -> MinecartBatteryBox.this.setEnergy(ByteUtils.with2Bytes(
                        MinecartBatteryBox.this.getEnergy(), (short)value, index - 2
                ));
                case 2, 3 -> {}
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public MinecartBatteryBox(EntityType<? extends MinecartBatteryBox> entityType, Level level) {
        super(entityType, level);
    }

    public MinecartBatteryBox(Level level, double x, double y, double z) {
        super(ModEntityTypes.BATTERY_BOX_MINECART.get(), level, x, y, z);
    }

    @Override
    protected Item getDropItem() {
        return ModItems.BATTERY_BOX_MINECART.get();
    }

    public BlockState getDefaultDisplayBlockState() {
        return ModBlocks.BATTERY_BOX.get().defaultBlockState();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new MinecartBatteryBoxMenu(id, inventory, this, data);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        entityData.define(DATA_ID_ENERGY, 0);
    }

    @Override
    public int getCapacity() {
        return CAPACITY;
    }

    @Override
    public int getTransferRate() {
        return MAX_TRANSFER;
    }

    @Override
    public int getEnergy() {
        return entityData.get(DATA_ID_ENERGY);
    }

    @Override
    public void setEnergy(int energy) {
        entityData.set(DATA_ID_ENERGY, energy);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.BATTERY_BOX_MINECART.get());
    }
}
