package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ChargingStationBlock;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChargingStationBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {
    private final ReceiveOnlyEnergyStorage energyStorage;
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    public ChargingStationBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CHARGING_STATION_ENTITY.get(), blockPos, blockState);

        energyStorage = new ReceiveOnlyEnergyStorage(0, 262144, 16384) {
            @Override
            protected void onChange() {
                setChanged();

                ModMessages.sendToAllPlayers(new EnergySyncS2CPacket(energy, capacity, getBlockPos()));
            }
        };
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


    public static void tick(Level level, BlockPos blockPos, BlockState state, ChargingStationBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        List<Player> players = level.getEntitiesOfClass(Player.class, AABB.of(BoundingBox.fromCorners(
                new Vec3i(blockPos.getX() - 1, blockPos.getY() - 1, blockPos.getZ() - 1),
                new Vec3i(blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1))));

        int energyPerTick = Math.min(blockEntity.energyStorage.getMaxReceive(), blockEntity.energyStorage.getEnergy());
        int energyPerTickLeft = energyPerTick;

        outer:
        for(Player player:players) {
            if(player.isDeadOrDying())
                continue;

            Inventory inventory = player.getInventory();
            for(int i = 0;i < inventory.getContainerSize();i++) {
                ItemStack itemStack = inventory.getItem(i);

                LazyOptional<IEnergyStorage> energyStorageLazyOptional = itemStack.getCapability(ForgeCapabilities.ENERGY);
                if(!energyStorageLazyOptional.isPresent())
                    continue;

                IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
                if(!energyStorage.canReceive())
                    continue;

                energyPerTickLeft -= energyStorage.receiveEnergy(energyPerTickLeft, false);
                if(energyPerTickLeft == 0)
                    break outer;
            }
        }

        if(energyPerTickLeft == energyPerTick) {
            if(!level.getBlockState(blockPos).hasProperty(ChargingStationBlock.CHARGING) || level.getBlockState(blockPos).getValue(ChargingStationBlock.CHARGING))
                level.setBlock(blockPos, state.setValue(ChargingStationBlock.CHARGING, Boolean.FALSE), 3);
        }else {
            if(!level.getBlockState(blockPos).hasProperty(ChargingStationBlock.CHARGING) || !level.getBlockState(blockPos).getValue(ChargingStationBlock.CHARGING))
                level.setBlock(blockPos, state.setValue(ChargingStationBlock.CHARGING, Boolean.TRUE), 3);

            blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyPerTick + energyPerTickLeft);
        }
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