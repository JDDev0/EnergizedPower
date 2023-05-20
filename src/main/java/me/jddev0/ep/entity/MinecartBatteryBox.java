package me.jddev0.ep.entity;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.screen.MinecartBatteryBoxMenu;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MinecartBatteryBox extends AbstractMinecart implements Container, MenuProvider {
    private static final EntityDataAccessor<Integer> DATA_ID_ENERGY =
            SynchedEntityData.defineId(MinecartBatteryBox.class, EntityDataSerializers.INT);

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch(index) {
                case 0, 1 -> ByteUtils.get2Bytes(MinecartBatteryBox.this.getEnergy(), index);
                case 2, 3 -> ByteUtils.get2Bytes(CAPACITY, index);
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

    public static final int CAPACITY = 65536;
    public static final int MAX_TRANSFER = 512;

    public MinecartBatteryBox(EntityType<? extends MinecartBatteryBox> entityType, Level level) {
        super(entityType, level);
    }

    public MinecartBatteryBox(Level level, double x, double y, double z) {
        super(ModEntityTypes.BATTERY_BOX_MINECART.get(), level, x, y, z);
    }

    @Override
    public void destroy(DamageSource damageSource) {
        super.destroy(damageSource);

        if(this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
            this.spawnAtLocation(ModBlocks.BATTERY_BOX_ITEM.get());
    }

    @Override
    public Type getMinecartType() {
        return Type.CHEST;
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
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        player.openMenu(this);
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean stillValid(Player player) {
        return !isRemoved() && position().closerThan(player.position(), 8.);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        entityData.define(DATA_ID_ENERGY, 0);
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

    public int getEnergy() {
        return entityData.get(DATA_ID_ENERGY);
    }

    public void setEnergy(int energy) {
        entityData.set(DATA_ID_ENERGY, energy);
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
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.BATTERY_BOX_MINECART.get());
    }
}
