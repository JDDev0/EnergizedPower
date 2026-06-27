package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuLegacyItemContainerStorageBlockEntity;
import me.jddev0.ep.inventory.InfiniteSingleItemStackHandler;
import me.jddev0.ep.screen.CreativeItemSiloMenu;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreativeItemSiloBlockEntity
        extends MenuLegacyItemContainerStorageBlockEntity<InfiniteSingleItemStackHandler> {

    public CreativeItemSiloBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
               EPBlockEntities.CREATIVE_ITEM_SILO_ENTITY, blockPos, blockState,

                "creative_item_silo",

                1
        );
    }

    @Override
    protected InfiniteSingleItemStackHandler initInventoryStorage() {
        return new InfiniteSingleItemStackHandler() {
            @Override
            protected void onFinalCommit() {
                setChanged();
            }
        };
    }

    @Override
    protected void readInventoryStorage(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        itemHandler.readNbt(nbt, registries);
    }

    @Override
    protected CompoundTag writeInventoryStorage(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        itemHandler.writeNbt(nbt, registries);
        return nbt;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new CreativeItemSiloMenu(id, this, inventory, itemHandler);
    }

    static Storage<ItemVariant> getInventoryStorageForDirection(CreativeItemSiloBlockEntity entity, Direction side) {
        return entity.itemHandler;
    }

    @Override
    public void drops(Level level, BlockPos worldPosition) {
        //Do not drop anything
    }
}
