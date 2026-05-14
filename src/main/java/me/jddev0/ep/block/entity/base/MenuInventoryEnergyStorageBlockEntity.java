package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MenuInventoryEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends SimpleContainer>
        extends InventoryEnergyStorageBlockEntity<E, I>
        implements ExtendedScreenHandlerFactory<BlockPos> {
    protected final String machineName;

    protected final ContainerData data;

    public MenuInventoryEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                       String machineName,
                                       long baseEnergyCapacity, long baseEnergyTransferRate,
                                       int slotCount) {
        super(type, blockPos, blockState, baseEnergyCapacity, baseEnergyTransferRate, slotCount);

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

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return worldPosition;
    }
}