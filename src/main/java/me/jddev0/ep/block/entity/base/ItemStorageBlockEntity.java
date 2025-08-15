package me.jddev0.ep.block.entity.base;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

public abstract class ItemStorageBlockEntity<I extends Storage<ItemVariant>>
        extends BlockEntity {
    protected final int slotCount;
    protected final I itemHandler;

    public ItemStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                  int slotCount) {
        super(type, blockPos, blockState);

        this.slotCount = slotCount;
        itemHandler = initInventoryStorage();
    }

    protected abstract I initInventoryStorage();
    protected abstract void writeInventoryStorage(WriteView view);
    protected abstract void readInventoryStorage(ReadView view);

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        writeInventoryStorage(view.get("inventory"));
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        readInventoryStorage(view.getReadView("inventory"));
    }

    @Override
    public abstract void onBlockReplaced(BlockPos pos, BlockState oldState);
}
