package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuInventoryStorageBlockEntity;
import me.jddev0.ep.inventory.InfiniteSingleItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.screen.CreativeItemSiloMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreativeItemSiloBlockEntity
        extends MenuInventoryStorageBlockEntity<InfiniteSingleItemStackHandler> {
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> true));

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

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }

        return super.getCapability(cap, side);
    }
    @Override
    public void drops(Level level, BlockPos worldPosition) {
        //Do not drop anything
    }
}
