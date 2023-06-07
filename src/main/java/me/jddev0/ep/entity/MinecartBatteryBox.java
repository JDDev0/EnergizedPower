package me.jddev0.ep.entity;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.screen.MinecartBatteryBoxMenu;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MinecartBatteryBox extends AbstractMinecartEntity implements Inventory, NamedScreenHandlerFactory {
    public static final long CAPACITY = 65536;
    public static final long MAX_TRANSFER = 512;

    private static final TrackedData<Long> DATA_ID_ENERGY = DataTracker.registerData(MinecartBatteryBox.class, TrackedDataHandlerRegistry.LONG);

    protected final PropertyDelegate data = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch(index) {
                case 0, 1, 2, 3 -> ByteUtils.get2Bytes(MinecartBatteryBox.this.getEnergy(), index);
                case 4, 5, 6, 7 -> ByteUtils.get2Bytes(CAPACITY, index - 4);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch(index) {
                case 0, 1, 2, 3 -> MinecartBatteryBox.this.setEnergy(ByteUtils.with2Bytes(
                        MinecartBatteryBox.this.getEnergy(), (short)value, index));
                case 4, 5, 6, 7 -> {}
            }
        }

        @Override
        public int size() {
            return 8;
        }
    };

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
        return Type.CHEST;
    }

    public BlockState getDefaultContainedBlock() {
        return ModBlocks.BATTERY_BOX.getDefaultState();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new MinecartBatteryBoxMenu(id, inventory, this, data);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand interactionHand) {
        player.openHandledScreen(this);

        return player.getWorld().isClient?ActionResult.SUCCESS:ActionResult.CONSUME;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return !isRemoved() && getPos().isInRange(player.getPos(), 8.);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        dataTracker.startTracking(DATA_ID_ENERGY, 0L);
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

    public long getEnergy() {
        return dataTracker.get(DATA_ID_ENERGY);
    }

    public void setEnergy(long energy) {
        dataTracker.set(DATA_ID_ENERGY, energy);
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
    public ItemStack getPickBlockStack() {
        return new ItemStack(ModItems.BATTERY_BOX_MINECART);
    }
}
