package me.jddev0.ep.block.entity.base;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MenuItemStorageBlockEntity<I extends Storage<ItemVariant>>
        extends ItemStorageBlockEntity<I>
        implements ExtendedScreenHandlerFactory<BlockPos> {
    protected final String machineName;

    protected final ContainerData data;

    public MenuItemStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
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