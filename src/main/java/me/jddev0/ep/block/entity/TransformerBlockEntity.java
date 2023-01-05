package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.CableBlock;
import me.jddev0.ep.block.LightningGeneratorBlock;
import me.jddev0.ep.block.TransformerBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ExtractOnlyEnergyStorage;
import me.jddev0.ep.energy.ReceiveAndExtractEnergyStorage;
import me.jddev0.ep.energy.ReceiveExtractEnergyHandler;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransformerBlockEntity extends BlockEntity implements EnergyStoragePacketUpdate {
    private final TransformerBlock.Type type;

    private final ReceiveAndExtractEnergyStorage energyStorage = new ReceiveAndExtractEnergyStorage(0,
            CableBlock.Tier.TIER_ENERGIZED_COPPER.getMaxTransfer(), CableBlock.Tier.TIER_ENERGIZED_COPPER.getMaxTransfer()) {
        @Override
        protected void onChange() {
            setChanged();

            if(level != null && !level.isClientSide())
                ModMessages.sendToAllPlayers(new EnergySyncS2CPacket(energy, capacity, getBlockPos()));
        }
    };
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();
    private final LazyOptional<IEnergyStorage> lazyEnergyStorageSidedReceive = LazyOptional.of(
            () -> new ReceiveExtractEnergyHandler(energyStorage, (maxReceive, simulate) -> true, (maxExtract, simulate) -> false));
    private final LazyOptional<IEnergyStorage> lazyEnergyStorageSidedExtract = LazyOptional.of(
            () -> new ReceiveExtractEnergyHandler(energyStorage, (maxReceive, simulate) -> false, (maxExtract, simulate) -> true));

    public static BlockEntityType<TransformerBlockEntity> getEntityTypeFromType(TransformerBlock.Type type) {
        return switch(type) {
            case TYPE_1_TO_N -> ModBlockEntities.TRANSFORMER_1_TO_N_ENTITY.get();
            case TYPE_N_TO_1 -> ModBlockEntities.TRANSFORMER_N_TO_1_ENTITY.get();
        };
    }

    public TransformerBlockEntity(BlockPos blockPos, BlockState blockState, TransformerBlock.Type type) {
        super(getEntityTypeFromType(type), blockPos, blockState);

        this.type = type;
    }

    public TransformerBlock.Type getTransformerType() {
        return type;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
            if(side == null)
                return lazyEnergyStorage.cast();

            Direction facing = getBlockState().getValue(TransformerBlock.FACING);

            LazyOptional<IEnergyStorage> singleSide = type == TransformerBlock.Type.TYPE_1_TO_N?
                    lazyEnergyStorageSidedReceive:lazyEnergyStorageSidedExtract;

            LazyOptional<IEnergyStorage> multipleSide = type == TransformerBlock.Type.TYPE_1_TO_N?
                    lazyEnergyStorageSidedExtract:lazyEnergyStorageSidedReceive;

            if(facing == side)
                return singleSide.cast();

            return multipleSide.cast();
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

    @Override
    public void setEnergy(int energy) {
        energyStorage.setEnergyWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }
}