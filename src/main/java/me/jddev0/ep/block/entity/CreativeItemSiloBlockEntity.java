package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuItemStorageBlockEntity;
import me.jddev0.ep.inventory.InfiniteSingleItemStackHandler;
import me.jddev0.ep.screen.CreativeItemSiloMenu;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class CreativeItemSiloBlockEntity
        extends MenuItemStorageBlockEntity<InfiniteSingleItemStackHandler> {

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
    protected void readInventoryStorage(ValueInput view) {
        itemHandler.readValue(view);
    }

    @Override
    protected void writeInventoryStorage(ValueOutput view) {
        itemHandler.writeValue(view);
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
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        //Do not drop anything
    }
}
