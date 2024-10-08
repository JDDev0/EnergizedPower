package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuEnergyStorageBlockEntity;
import me.jddev0.ep.energy.InfinityEnergyStorage;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.screen.CreativeBatteryBoxMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreativeBatteryBoxBlockEntity extends MenuEnergyStorageBlockEntity<InfinityEnergyStorage>
        implements CheckboxUpdate {
    private boolean energyProduction = true;
    private boolean energyConsumption;

    public CreativeBatteryBoxBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.CREATIVE_BATTERY_BOX_ENTITY.get(), blockPos, blockState,

                "creative_battery_box",

                Integer.MAX_VALUE, Integer.MAX_VALUE
        );
    }

    @Override
    protected InfinityEnergyStorage initEnergyStorage() {
        return new InfinityEnergyStorage() {
            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                return energyProduction?super.extractEnergy(maxExtract, simulate):0;
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return energyConsumption?super.receiveEnergy(maxReceive, simulate):0;
            }

            @Override
            protected void onChange() {
                setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> energyProduction?1:0;
                    case 1 -> energyConsumption?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> CreativeBatteryBoxBlockEntity.this.energyProduction = value != 0;
                    case 1 -> CreativeBatteryBoxBlockEntity.this.energyConsumption = value != 0;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new CreativeBatteryBoxMenu(id, inventory, this, data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.putBoolean("energy_production", energyProduction);
        nbt.putBoolean("energy_consumption", energyConsumption);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        energyProduction = !nbt.contains("energy_production") || nbt.getBoolean("energy_production");
        energyConsumption = nbt.getBoolean("energy_consumption");
    }

    public void setEnergyProduction(boolean energyProduction) {
        this.energyProduction = energyProduction;
        setChanged(level, getBlockPos(), getBlockState());
    }

    public void setEnergyConsumption(boolean energyConsumption) {
        this.energyConsumption = energyConsumption;
        setChanged(level, getBlockPos(), getBlockState());
    }

    @Override
    public void setCheckbox(int checkboxId, boolean checked) {
        switch(checkboxId) {
            //Energy Production
            case 0 -> setEnergyProduction(checked);

            //Energy Consumption
            case 1 -> setEnergyConsumption(checked);
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, CreativeBatteryBoxBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        transferInfiniteEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferInfiniteEnergy(Level level, BlockPos blockPos, BlockState state, CreativeBatteryBoxBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(!blockEntity.energyProduction)
            return;

        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            LazyOptional<IEnergyStorage> energyStorageLazyOptional = testBlockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
            if(!energyStorageLazyOptional.isPresent())
                continue;

            IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
            if(!energyStorage.canReceive())
                continue;

            int received = energyStorage.receiveEnergy(energyStorage.getMaxEnergyStored(), true);
            energyStorage.receiveEnergy(received, false);
        }
    }
}