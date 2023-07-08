package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.MinecartChargerBlock;
import me.jddev0.ep.block.MinecartUnchargerBlock;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.entity.MinecartBatteryBox;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.screen.MinecartChargerMenu;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MinecartChargerBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate {
    public static final int MAX_TRANSFER = 512;

    private final ReceiveOnlyEnergyStorage energyStorage;
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    protected final ContainerData data;

    private boolean hasMinecartOld = true; //Default true (Force first update)
    private boolean hasMinecart = false; //Default false (Force first update)

    public MinecartChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.MINECART_CHARGER_ENTITY.get(), blockPos, blockState);

        energyStorage = new ReceiveOnlyEnergyStorage(0, 16384, MAX_TRANSFER) {
            @Override
            protected void onChange() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToAllPlayers(new EnergySyncS2CPacket(energy, capacity, getBlockPos()));
            }
        };
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(MinecartChargerBlockEntity.this.energyStorage.getEnergy(), index);
                    case 2, 3 -> ByteUtils.get2Bytes(MinecartChargerBlockEntity.this.energyStorage.getCapacity(), index - 2);
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> MinecartChargerBlockEntity.this.energyStorage.setEnergyWithoutUpdate(ByteUtils.with2Bytes(
                            MinecartChargerBlockEntity.this.energyStorage.getEnergy(), (short)value, index
                    ));
                    case 2, 3 -> MinecartChargerBlockEntity.this.energyStorage.setCapacityWithoutUpdate(ByteUtils.with2Bytes(
                            MinecartChargerBlockEntity.this.energyStorage.getCapacity(), (short)value, index - 2
                    ));
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.minecart_charger");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(),
                getBlockPos()), (ServerPlayer)player);

        return new MinecartChargerMenu(id, inventory, this, this.data);
    }

    public int getRedstoneOutput() {
        BlockPos blockPosFacing = getBlockPos().relative(getBlockState().getValue(MinecartUnchargerBlock.FACING));
        List<MinecartBatteryBox> minecarts = level.getEntities(EntityTypeTest.forClass(MinecartBatteryBox.class),
                new AABB(blockPosFacing.getX(), blockPosFacing.getY(),
                        blockPosFacing.getZ(), blockPosFacing.getX() + 1,
                        blockPosFacing.getY() + 1, blockPosFacing.getZ() + 1),
                EntitySelector.ENTITY_STILL_ALIVE);
        if(minecarts.isEmpty())
            return 0;

        MinecartBatteryBox minecart = minecarts.get(0);

        int minecartEnergy = minecart.getEnergy();
        boolean isEmptyFlag = minecartEnergy == 0;

        return Math.min(Mth.floor((float)minecartEnergy / MinecartBatteryBox.CAPACITY * 14.f) + (isEmptyFlag?0:1), 15);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyEnergyStorage.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("energy", energyStorage.saveNBT());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        energyStorage.loadNBT(nbt.get("energy"));
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, MinecartChargerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(blockEntity.hasMinecartOld != blockEntity.hasMinecart)
            setChanged(level, blockPos, state);

        blockEntity.hasMinecartOld = blockEntity.hasMinecart;

        BlockPos blockPosFacing = blockEntity.getBlockPos().relative(blockEntity.getBlockState().getValue(MinecartChargerBlock.FACING));
        List<MinecartBatteryBox> minecarts = level.getEntities(EntityTypeTest.forClass(MinecartBatteryBox.class),
                new AABB(blockPosFacing.getX(), blockPosFacing.getY(),
                        blockPosFacing.getZ(), blockPosFacing.getX() + 1,
                        blockPosFacing.getY() + 1, blockPosFacing.getZ() + 1),
                EntitySelector.ENTITY_STILL_ALIVE);
        blockEntity.hasMinecart = !minecarts.isEmpty();
        if(!blockEntity.hasMinecart)
            return;

        MinecartBatteryBox minecart = minecarts.get(0);
        int transferred = Math.min(Math.min(blockEntity.energyStorage.getEnergy(), MAX_TRANSFER),
                Math.min(MinecartBatteryBox.MAX_TRANSFER, MinecartBatteryBox.CAPACITY - minecart.getEnergy()));
        minecart.setEnergy(minecart.getEnergy() + transferred);

        blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - transferred);
    }

    public int getEnergy() {
        return energyStorage.getEnergy();
    }

    public int getCapacity() {
        return energyStorage.getCapacity();
    }

    @Override
    public void setEnergy(int energy) {
        energyStorage.setEnergyWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }
}