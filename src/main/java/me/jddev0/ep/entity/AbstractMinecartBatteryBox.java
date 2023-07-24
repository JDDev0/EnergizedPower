package me.jddev0.ep.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class AbstractMinecartBatteryBox extends AbstractMinecartEntity implements Inventory, NamedScreenHandlerFactory {
    public AbstractMinecartBatteryBox(EntityType<? extends AbstractMinecartBatteryBox> entityType, World level) {
        super(entityType, level);
    }

    public AbstractMinecartBatteryBox(EntityType<? extends AbstractMinecartBatteryBox> entityType, World level,
                                      double x, double y, double z) {
        super(entityType, level, x, y, z);
    }

    @Override
    public Type getMinecartType() {
        return Type.CHEST;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand interactionHand) {
        player.openHandledScreen(this);

        return player.getWorld().isClient?ActionResult.SUCCESS:ActionResult.CONSUME;
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putLong("energy", getEnergy());

        super.writeCustomDataToNbt(nbt);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        setEnergy(nbt.getLong("energy"));
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return !isRemoved() && getPos().isInRange(player.getPos(), 8.);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

    }

    @Override
    public void markDirty() {

    }

    @Override
    public void clear() {

    }

    public abstract long getCapacity();

    public abstract long getTransferRate();

    public abstract long getEnergy();

    public abstract void setEnergy(long energy);
}
