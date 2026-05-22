package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public abstract class InventoryEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends ItemStackHandler>
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
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.put("inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        itemHandler.deserializeNBT(registries, nbt.getCompound("inventory"));
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);
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

        outer:
        for(Direction direction:Direction.values()) {
            //TODO check performance and cache
            IItemHandler itemStackStorageSelf = level.getCapability(Capabilities.ItemHandler.BLOCK, worldPosition, level.getBlockState(worldPosition),
                this, direction);

            if(itemStackStorageSelf == null)
                continue;


            BlockPos outputBlockPos = worldPosition.relative(direction);
            BlockEntity outputBlockEntity = level.getBlockEntity(outputBlockPos);

            //TODO check performance and cache
            IItemHandler itemStackStorageOutput = level.getCapability(Capabilities.ItemHandler.BLOCK, outputBlockPos, level.getBlockState(outputBlockPos),
                    outputBlockEntity, direction.getOpposite());

            if(itemStackStorageOutput == null)
                continue;

            //Try to output for every slot of self inventory on current direction
            for(int i = 0;i < itemStackStorageSelf.getSlots();i++) {
                ItemStack itemToExtract;
                if(itemStackStorageSelf.getStackInSlot(i).isEmpty())
                    continue;

                itemToExtract = itemStackStorageSelf.getStackInSlot(i);
                itemToExtract = itemStackStorageSelf.extractItem(i, itemToExtract.getCount(), true);
                if(itemToExtract.isEmpty())
                    continue;

                //Item found for extraction -> try to insert and extract for real if successful
                for(int j = 0;j < itemStackStorageOutput.getSlots();j++) {
                    ItemStack remaining = itemStackStorageOutput.insertItem(j, itemToExtract.copy(), false);
                    int amount = itemToExtract.getCount() - remaining.getCount();
                    if(amount > 0) {
                        itemStackStorageSelf.extractItem(i, amount, false);

                        itemAmountLeft -= amount;
                        break;
                    }
                }

                if(itemAmountLeft <= 0) {
                    break outer;
                }
            }
        }
    }
}
