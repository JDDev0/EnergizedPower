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
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
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
                markDirty();
            }
        };
    }

    @Override
    protected NbtCompound writeInventoryStorage(@NotNull NbtCompound nbt, @NotNull RegistryWrapper.WrapperLookup registries) {
        itemHandler.writeNbt(nbt, registries);
        return nbt;
    }

    @Override
    protected void readInventoryStorage(@NotNull NbtCompound nbt, @NotNull RegistryWrapper.WrapperLookup registries) {
        itemHandler.readNbt(nbt, registries);
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new CombinedContainerData(
                new LongValueContainerData(itemHandler::getAmount, value -> {}),
                new LongValueContainerData(itemHandler::getCapacity, value -> {})
        );
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ItemSiloMenu(id, this, inventory, itemHandler, data);
    }

    public int getRedstoneOutput() {
        return InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
    }

    static Storage<ItemVariant> getInventoryStorageForDirection(ItemSiloBlockEntity entity, Direction side) {
        return entity.itemHandler;
    }

    @Override
    public void drops(World level, BlockPos worldPosition) {
        if(world != null) {
            long count = itemHandler.getAmount();
            ItemStack stack = itemHandler.variant.toStack();

            if(count > 0 && !stack.isEmpty()) {
                while(count > 0) {
                    long countItem = Math.min(count, stack.getMaxCount());
                    count -= countItem;

                    ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack.copyWithCount((int)countItem));
                }
            }
        }
    }
}
