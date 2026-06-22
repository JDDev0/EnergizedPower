package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.fluid.IEnergizedPowerFluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MenuFluidEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, F extends IEnergizedPowerFluidStorage>
        extends FluidEnergyStorageBlockEntity<E, F>
        implements MenuProvider {
    protected final String machineName;

    protected final ContainerData data;

    public MenuFluidEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                             String machineName,
                                             int baseEnergyCapacity, int baseEnergyTransferRate,
                                             int baseTankCapacity) {
        super(type, blockPos, blockState, baseEnergyCapacity, baseEnergyTransferRate, baseTankCapacity);

        this.machineName = machineName;

        data = initContainerData();
    }

    protected ContainerData initContainerData() {
        return new SimpleContainerData(0);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower." + machineName);
    }
}