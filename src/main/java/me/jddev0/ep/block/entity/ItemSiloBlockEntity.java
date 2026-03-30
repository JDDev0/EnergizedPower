package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuItemStorageBlockEntity;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.SingleItemStackHandler;
import me.jddev0.ep.inventory.data.LongValueContainerData;
import me.jddev0.ep.machine.tier.ItemSiloTier;
import me.jddev0.ep.screen.ItemSiloMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class ItemSiloBlockEntity
        extends MenuItemStorageBlockEntity<SingleItemStackHandler> {
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
    protected void readInventoryStorage(ValueInput view) {
        itemHandler.readValue(view);
    }

    @Override
    protected void writeInventoryStorage(ValueOutput view) {
        itemHandler.writeValue(view);
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new LongValueContainerData(itemHandler::getAmount, value -> {}),
                new LongValueContainerData(itemHandler::getCapacity, value -> {})
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ItemSiloMenu(id, this, inventory, itemHandler, data);
    }

    public int getRedstoneOutput() {
        return InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
    }

    static Storage<ItemVariant> getInventoryStorageForDirection(ItemSiloBlockEntity entity, Direction side) {
        return entity.itemHandler;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        if(level != null) {
            long count = itemHandler.getAmount();
            ItemStack stack = itemHandler.variant.toStack();

            if(count > 0 && !stack.isEmpty()) {
                while(count > 0) {
                    long countItem = Math.min(count, stack.getMaxStackSize());
                    count -= countItem;

                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack.copyWithCount((int)countItem));
                }
            }
        }
    }
}
