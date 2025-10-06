package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuInventoryStorageBlockEntity;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.SingleItemStackHandler;
import me.jddev0.ep.inventory.data.IntegerValueContainerData;
import me.jddev0.ep.machine.tier.ItemSiloTier;
import me.jddev0.ep.screen.ItemSiloMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;

public class ItemSiloBlockEntity
        extends MenuInventoryStorageBlockEntity<SingleItemStackHandler> {
    private final ItemSiloTier tier;

    public ItemSiloBlockEntity(BlockPos blockPos, BlockState blockState, ItemSiloTier tier) {
        super(
                tier.getEntityTypeFromTier(), blockPos, blockState,

                tier.getResourceId(),

                tier.getItemSiloCapacity()
        );

        this.tier = tier;
    }

    public ItemSiloTier getTier() {
        return tier;
    }

    @Override
    protected SingleItemStackHandler initInventoryStorage() {
        return new SingleItemStackHandler(slotCount) {
            @Override
            protected void onFinalCommit() {
                setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new IntegerValueContainerData(itemHandler::getCount, value -> {}),
                new IntegerValueContainerData(() -> itemHandler.getCapacityAsInt(0, itemHandler.getResource()), value -> {})
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ItemSiloMenu(id, inventory, this, this.data);
    }

    public int getRedstoneOutput() {
        return InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
    }

    public @Nullable ResourceHandler<ItemResource> getItemHandlerCapability(@Nullable Direction side) {
        return itemHandler;
    }

    @Override
    public void preRemoveSideEffects(BlockPos worldPosition, BlockState oldState) {
        if(level != null) {
            int count = itemHandler.getCount();
            ItemStack stack = itemHandler.getStackInSlot(0);

            if(count > 0 && !stack.isEmpty()) {
                while(count > 0) {
                    int countItem = Math.min(count, stack.getMaxStackSize());
                    count -= countItem;

                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack.copyWithCount(countItem));
                }
            }
        }
    }
}
