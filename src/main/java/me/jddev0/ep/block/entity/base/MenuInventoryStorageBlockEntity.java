package me.jddev0.ep.block.entity.base;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MenuInventoryStorageBlockEntity<I extends SimpleContainer>
        extends InventoryStorageBlockEntity<I>
        implements ExtendedMenuProvider<BlockPos> {
    protected final String machineName;

    protected final ContainerData data;

    public MenuInventoryStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                      String machineName,
                                                      int slotCount) {
        super(type, blockPos, blockState, slotCount);

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