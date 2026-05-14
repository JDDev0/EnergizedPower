package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageInfiniteTankMethods;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.InfinityFluidStorage;
import me.jddev0.ep.screen.CreativeFluidTankMenu;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CreativeFluidTankBlockEntity
        extends AbstractFluidTankBlockEntity<InfinityFluidStorage> {
    public CreativeFluidTankBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.CREATIVE_FLUID_TANK_ENTITY, blockPos, blockState,

                "creative_fluid_tank",

                FluidStorageInfiniteTankMethods.INSTANCE,
                Integer.MAX_VALUE
        );
    }

    @Override
    protected InfinityFluidStorage initFluidStorage() {
        return new InfinityFluidStorage() {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncFluidToPlayers(64);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncFluidToPlayer(player);

        return new CreativeFluidTankMenu(id, inventory, this);
    }

    public void setFluidStack(FluidStack fluidStack) {
        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.setFluid(fluidStack, transaction);

            transaction.commit();
        }
    }
}