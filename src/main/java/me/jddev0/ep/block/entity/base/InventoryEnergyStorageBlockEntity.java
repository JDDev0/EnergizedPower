package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.inventory.IEnergizedPowerItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public abstract class InventoryEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends IEnergizedPowerItemStackHandler>
        extends EnergyStorageBlockEntity<E> {
    protected final int slotCount;
    protected final I itemHandler;

    public InventoryEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                             int baseEnergyCapacity, int baseEnergyTransferRate,
                                             int slotCount) {
        super(type, blockPos, blockState, baseEnergyCapacity, baseEnergyTransferRate);

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
            SimpleContainer inventory = new SimpleContainer(itemHandler.size());
            for(int i = 0;i < itemHandler.size();i++)
                inventory.setItem(i, itemHandler.getStackInSlot(i));

            Containers.dropContents(level, worldPosition, inventory);
        }
    }

    private double itemAmountLeftoverDecimal = 0.;
    protected void pushItemsToOutputs(double itemsPerTick) {
        if(level == null || itemsPerTick <= 0)
            return;

        itemAmountLeftoverDecimal += itemsPerTick;
        int itemAmountLeft = (int)itemAmountLeftoverDecimal;

        //Only keep decimal part
        itemAmountLeftoverDecimal -= itemAmountLeft;

        if(itemAmountLeft <= 0) {
            return;
        }

        for(Direction direction:Direction.values()) {
            //TODO check performance and cache
            ResourceHandler<ItemResource> itemStackStorageSelf = level.getCapability(Capabilities.Item.BLOCK, worldPosition, level.getBlockState(worldPosition),
                this, direction);

            if(itemStackStorageSelf == null)
                continue;


            BlockPos outputBlockPos = worldPosition.relative(direction);
            BlockEntity outputBlockEntity = level.getBlockEntity(outputBlockPos);

            //TODO check performance and cache
            ResourceHandler<ItemResource> itemStackStorageOutput = level.getCapability(Capabilities.Item.BLOCK, outputBlockPos, level.getBlockState(outputBlockPos),
                    outputBlockEntity, direction.getOpposite());

            if(itemStackStorageOutput == null)
                continue;

            //Try to output for every slot of self inventory on current direction
            for(int i = 0;i < itemStackStorageSelf.size();i++) {
                ItemResource itemToExtract;
                int amountToExtract;
                try(Transaction transaction = Transaction.open(null)) {
                    if(itemStackStorageSelf.getResource(i).isEmpty())
                        continue;

                    itemToExtract = itemStackStorageSelf.getResource(i);
                    amountToExtract = itemStackStorageSelf.extract(itemToExtract, itemAmountLeft, transaction);
                    if(amountToExtract <= 0)
                        continue;
                }
                if(itemToExtract.isEmpty())
                    continue;

                //Item found for extraction -> try to insert and extract for real if successful
                try(Transaction transaction = Transaction.open(null)) {
                    int amount = itemStackStorageOutput.insert(itemToExtract, amountToExtract, transaction);
                    if(amount > 0) {
                        itemStackStorageSelf.extract(itemToExtract, amount, transaction);

                        itemAmountLeft -= amount;
                    }

                    transaction.commit();
                }

                if(itemAmountLeft <= 0) {
                    break;
                }
            }
        }
    }
}
