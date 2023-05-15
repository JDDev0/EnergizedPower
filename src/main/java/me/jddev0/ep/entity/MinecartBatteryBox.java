package me.jddev0.ep.entity;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class MinecartBatteryBox extends AbstractMinecartEntity {
    public static final int CAPACITY = 65536;
    public static final int MAX_TRANSFER = 2048;

    private long energy;

    public MinecartBatteryBox(EntityType<? extends MinecartBatteryBox> entityType, World level) {
        super(entityType, level);
    }

    public MinecartBatteryBox(World level, double x, double y, double z) {
        super(ModEntityTypes.BATTERY_BOX_MINECART, level, x, y, z);
    }

    @Override
    protected Item getItem() {
        return ModItems.BATTERY_BOX_MINECART;
    }

    @Override
    public Type getMinecartType() {
        return Type.TNT;
    }

    public BlockState getDefaultContainedBlock() {
        return ModBlocks.BATTERY_BOX.getDefaultState();
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putLong("energy", energy);

        super.writeCustomDataToNbt(nbt);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        energy = nbt.getLong("energy");
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(ModItems.BATTERY_BOX_MINECART);
    }
}
