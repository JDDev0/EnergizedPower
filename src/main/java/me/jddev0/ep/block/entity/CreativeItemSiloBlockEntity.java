package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuInventoryStorageBlockEntity;
import me.jddev0.ep.inventory.InfiniteSingleItemStackHandler;
import me.jddev0.ep.screen.CreativeItemSiloMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class CreativeItemSiloBlockEntity
        extends MenuInventoryStorageBlockEntity<InfiniteSingleItemStackHandler> {

    public CreativeItemSiloBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
               EPBlockEntities.CREATIVE_ITEM_SILO_ENTITY.get(), blockPos, blockState,

                "creative_item_silo",

                1
        );
    }

    @Override
    protected InfiniteSingleItemStackHandler initInventoryStorage() {
        return new InfiniteSingleItemStackHandler() {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new CreativeItemSiloMenu(id, inventory, this);
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        return itemHandler;
    }

    @Override
    public void drops(Level level, BlockPos worldPosition) {
        //Do not drop anything
    }
}
