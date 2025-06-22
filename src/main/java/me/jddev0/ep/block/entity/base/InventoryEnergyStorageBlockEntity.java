package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;

public abstract class InventoryEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends SimpleInventory>
        extends EnergyStorageBlockEntity<E> {
    protected final int slotCount;
    protected final I itemHandler;

    public InventoryEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                             long baseEnergyCapacity, long baseEnergyTransferRate,
                                             int slotCount) {
        super(type, blockPos, blockState, baseEnergyCapacity, baseEnergyTransferRate);

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
