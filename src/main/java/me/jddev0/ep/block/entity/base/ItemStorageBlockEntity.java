package me.jddev0.ep.block.entity.base;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    protected abstract void writeInventoryStorage(ValueOutput view);
    protected abstract void readInventoryStorage(ValueInput view);

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        writeInventoryStorage(view.child("inventory"));
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        readInventoryStorage(view.childOrEmpty("inventory"));
    }

    @Override
    public abstract void preRemoveSideEffects(BlockPos pos, BlockState oldState);
}
