package me.jddev0.ep.block.entity.base;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public abstract class MenuItemStorageBlockEntity<I extends Storage<ItemVariant>>
        extends ItemStorageBlockEntity<I>
        implements ExtendedScreenHandlerFactory {
    protected final String machineName;

    protected final PropertyDelegate data;

    public MenuItemStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                      String machineName,
                                      int slotCount) {
        super(type, blockPos, blockState, slotCount);

        this.machineName = machineName;

        data = initContainerData();
    }

    protected PropertyDelegate initContainerData() {
        return new ArrayPropertyDelegate(0);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower." + machineName);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}