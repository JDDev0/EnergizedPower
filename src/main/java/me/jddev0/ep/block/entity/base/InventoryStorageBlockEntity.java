package me.jddev0.ep.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class InventoryStorageBlockEntity<I extends ItemStackHandler>
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
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        itemHandler.serialize(view.child("inventory"));
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        itemHandler.deserialize(view.childOrEmpty("inventory"));
    }

    @Override
    public void preRemoveSideEffects(BlockPos worldPosition, BlockState oldState) {
        if(level != null) {
            SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
            for(int i = 0;i < itemHandler.getSlots();i++)
                inventory.setItem(i, itemHandler.getStackInSlot(i));

            Containers.dropContents(level, worldPosition, inventory);
        }
    }
}
