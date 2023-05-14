package me.jddev0.ep.entity;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartBatteryBox extends AbstractMinecart {
    public static final int CAPACITY = 65536;
    public static final int MAX_TRANSFER = 2048;

    private int energy;

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

    @Override
    public Type getMinecartType() {
        return Type.TNT;
    }

    public BlockState getDefaultDisplayBlockState() {
        return ModBlocks.BATTERY_BOX.get().defaultBlockState();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("energy", energy);

        super.addAdditionalSaveData(nbt);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        energy = nbt.getInt("energy");
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.BATTERY_BOX_MINECART.get());
    }
}
