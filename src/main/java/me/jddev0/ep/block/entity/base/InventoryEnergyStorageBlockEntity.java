package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class InventoryEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends SimpleContainer>
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
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        ContainerHelper.saveAllItems(view.child("inventory"), itemHandler.items);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        ContainerHelper.loadAllItems(view.childOrEmpty("inventory"), itemHandler.items);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        if(level != null)
            Containers.dropContents(level, pos, itemHandler);
    }

    private double itemAmountLeftoverDecimal = 0.;
    protected void pushItemsToOutputs(double itemsPerTick) {
        if(level == null || itemsPerTick <= 0)
            return;

        itemAmountLeftoverDecimal += itemsPerTick;
        long itemAmountLeft = (long)itemAmountLeftoverDecimal;

        //Only keep decimal part
        itemAmountLeftoverDecimal -= itemAmountLeft;

        if(itemAmountLeft <= 0) {
            return;
        }

        outer:
        for(Direction direction:Direction.values()) {
            //TODO check performance and cache
            Storage<ItemVariant> itemStackStorageSelf = ItemStorage.SIDED.find(level, worldPosition, level.getBlockState(worldPosition),
                this, direction);

            if(itemStackStorageSelf == null)
                continue;


            BlockPos outputBlockPos = worldPosition.relative(direction);
            BlockEntity outputBlockEntity = level.getBlockEntity(outputBlockPos);

            //TODO check performance and cache
            Storage<ItemVariant> itemStackStorageOutput = ItemStorage.SIDED.find(level, outputBlockPos, level.getBlockState(outputBlockPos),
                    outputBlockEntity, direction.getOpposite());

            if(itemStackStorageOutput == null)
                continue;

            //Try to output for every slot of self inventory on current direction
            for(StorageView<ItemVariant> itemViewSelf:itemStackStorageSelf) {
                ItemVariant itemToExtract;
                long amountToExtract;
                try(Transaction transaction = Transaction.openOuter()) {
                    if(itemViewSelf.isResourceBlank())
                        continue;

                    itemToExtract = itemViewSelf.getResource();
                    amountToExtract = itemStackStorageSelf.extract(itemToExtract, itemAmountLeft, transaction);
                    if(amountToExtract <= 0)
                        continue;
                }
                if(itemToExtract.isBlank())
                    continue;

                //Item found for extraction -> try to insert and extract for real if successful
                try(Transaction transaction = Transaction.openOuter()) {
                    long amount = itemStackStorageOutput.insert(itemToExtract, amountToExtract, transaction);
                    if(amount > 0) {
                        itemStackStorageSelf.extract(itemToExtract, amount, transaction);

                        itemAmountLeft -= amount;
                    }

                    transaction.commit();
                }

                if(itemAmountLeft <= 0) {
                    break outer;
                }
            }
        }
    }

    private double itemAmountPullingLeftoverDecimal = 0.;
    protected void pullItemsFromInputs(double itemsPerTick) {
        if(level == null || itemsPerTick <= 0)
            return;

        itemAmountPullingLeftoverDecimal += itemsPerTick;
        long itemAmountLeft = (long)itemAmountPullingLeftoverDecimal;

        //Only keep decimal part
        itemAmountPullingLeftoverDecimal -= itemAmountLeft;

        if(itemAmountLeft <= 0) {
            return;
        }

        outer:
        for(Direction direction:Direction.values()) {
            //TODO check performance and cache
            Storage<ItemVariant> itemStackStorageSelf = ItemStorage.SIDED.find(level, worldPosition, level.getBlockState(worldPosition),
                    this, direction);

            if(itemStackStorageSelf == null)
                continue;


            BlockPos outputBlockPos = worldPosition.relative(direction);
            BlockEntity outputBlockEntity = level.getBlockEntity(outputBlockPos);

            //TODO check performance and cache
            Storage<ItemVariant> itemStackStorageInput = ItemStorage.SIDED.find(level, outputBlockPos, level.getBlockState(outputBlockPos),
                    outputBlockEntity, direction.getOpposite());

            if(itemStackStorageInput == null)
                continue;

            //Try to extract for every slot of input inventory on current direction
            for(StorageView<ItemVariant> itemViewInput:itemStackStorageInput) {
                ItemVariant itemToExtract;
                long amountToExtract;
                try(Transaction transaction = Transaction.openOuter()) {
                    if(itemViewInput.isResourceBlank())
                        continue;

                    itemToExtract = itemViewInput.getResource();
                    amountToExtract = itemStackStorageInput.extract(itemToExtract, itemAmountLeft, transaction);
                    if(amountToExtract <= 0)
                        continue;
                }
                if(itemToExtract.isBlank())
                    continue;

                //Item found for extraction -> try to insert and extract for real if successful
                try(Transaction transaction = Transaction.openOuter()) {
                    long amount = itemStackStorageSelf.insert(itemToExtract, amountToExtract, transaction);
                    if(amount > 0) {
                        itemStackStorageInput.extract(itemToExtract, amount, transaction);

                        itemAmountLeft -= amount;
                    }

                    transaction.commit();
                }

                if(itemAmountLeft <= 0) {
                    break outer;
                }
            }
        }
    }
}
