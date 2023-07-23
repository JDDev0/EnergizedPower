package me.jddev0.ep.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class AbstractMinecartBatteryBox extends AbstractMinecart implements Container, MenuProvider {
    public AbstractMinecartBatteryBox(EntityType<? extends AbstractMinecartBatteryBox> entityType, Level level) {
        super(entityType, level);
    }

    public AbstractMinecartBatteryBox(EntityType<? extends AbstractMinecartBatteryBox> entityType, Level level,
                                      double x, double y, double z) {
        super(entityType, level, x, y, z);
    }

    @Override
    public Type getMinecartType() {
        return Type.CHEST;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        player.openMenu(this);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("energy", getEnergy());

        super.addAdditionalSaveData(nbt);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        setEnergy(nbt.getInt("energy"));
    }

    @Override
    public boolean stillValid(Player player) {
        return !isRemoved() && position().closerThan(player.position(), 8.);
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {

    }

    @Override
    public void setChanged() {

    }

    @Override
    public void clearContent() {

    }

    public abstract int getCapacity();

    public abstract int getTransferRate();

    public abstract int getEnergy();

    public abstract void setEnergy(int energy);
}
