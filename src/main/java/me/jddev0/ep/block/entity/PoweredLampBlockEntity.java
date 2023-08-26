package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.PoweredLampBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PoweredLampBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {
    public static final int MAX_RECEIVE = ModConfigs.COMMON_POWERED_LAMP_TRANSFER_RATE.getValue();

    private final ReceiveOnlyEnergyStorage energyStorage;
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    public PoweredLampBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.POWERED_LAMP_ENTITY.get(), blockPos, blockState);

        energyStorage = new ReceiveOnlyEnergyStorage(0, MAX_RECEIVE, MAX_RECEIVE) {
            @Override
            protected void onChange() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToPlayersWithinXBlocks(
                            new EnergySyncS2CPacket(energy, capacity, getBlockPos()),
                            getBlockPos(), level.dimension(), 32
                    );
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

    public static void tick(Level level, BlockPos blockPos, BlockState state, PoweredLampBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        boolean isEmptyFlag = blockEntity.energyStorage.getEnergy() == 0;
        int levelValue = Math.min(Mth.floor((float)blockEntity.energyStorage.getEnergy() / blockEntity.energyStorage.getCapacity() * 14.f) + (isEmptyFlag?0:1), 15);

        if(state.getValue(PoweredLampBlock.LEVEL) != levelValue)
            level.setBlock(blockPos, state.setValue(PoweredLampBlock.LEVEL, levelValue), 3);

        blockEntity.energyStorage.setEnergy(0);
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