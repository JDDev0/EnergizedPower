package me.jddev0.ep.block.entity.base;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MenuFluidStorageBlockEntity<F extends Storage<FluidVariant>>
        extends FluidStorageBlockEntity<F>
        implements ExtendedMenuProvider<BlockPos> {
    protected final String machineName;

    protected final ContainerData data;

    public MenuFluidStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                        String machineName,
                                        FluidStorageMethods<F> fluidStorageMethods, long baseTankCapacity) {
        super(type, blockPos, blockState, fluidStorageMethods, baseTankCapacity);

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