package me.jddev0.ep.block.entity.base;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;

public abstract class InventoryStorageBlockEntity<I extends SimpleInventory>
        extends BlockEntity {
    protected final int slotCount;
    protected final I itemHandler;

    public InventoryStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                       int slotCount) {
        super(type, blockPos, blockState);

        this.slotCount = slotCount;
        itemHandler = initInventoryStorage();
    }

    protected abstract I initInventoryStorage();

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        Inventories.writeData(view.get("inventory"), itemHandler.heldStacks);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        Inventories.readData(view.getReadView("inventory"), itemHandler.heldStacks);
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        if(world != null)
            ItemScatterer.spawn(world, pos, itemHandler);
    }
}
