package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuItemStorageBlockEntity;
import me.jddev0.ep.inventory.InfiniteSingleItemStackHandler;
import me.jddev0.ep.screen.CreativeItemSiloMenu;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
                markDirty();
            }
        };
    }

    @Override
    protected void readInventoryStorage(ReadView view) {
        itemHandler.readData(view);
    }

    @Override
    protected void writeInventoryStorage(WriteView view) {
        itemHandler.writeData(view);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new CreativeItemSiloMenu(id, this, inventory, itemHandler);
    }

    static Storage<ItemVariant> getInventoryStorageForDirection(CreativeItemSiloBlockEntity entity, Direction side) {
        return entity.itemHandler;
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        //Do not drop anything
    }
}
