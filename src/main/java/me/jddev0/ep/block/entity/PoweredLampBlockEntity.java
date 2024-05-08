package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.PoweredLampBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PoweredLampBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {
    public static final int MAX_RECEIVE = ModConfigs.COMMON_POWERED_LAMP_TRANSFER_RATE.getValue();

    private final ReceiveOnlyEnergyStorage energyStorage;

    public PoweredLampBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.POWERED_LAMP_ENTITY.get(), blockPos, blockState);

        energyStorage = new ReceiveOnlyEnergyStorage(0, MAX_RECEIVE, MAX_RECEIVE) {
            @Override
            protected void onChange() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToPlayersWithinXBlocks(
                            new EnergySyncS2CPacket(getEnergy(), getCapacity(), getBlockPos()),
                            getBlockPos(), (ServerLevel)level, 32
                    );
            }
        };
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        nbt.put("energy", energyStorage.saveNBT());

        super.saveAdditional(nbt, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

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