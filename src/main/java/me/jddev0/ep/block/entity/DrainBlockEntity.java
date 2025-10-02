package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.MenuFluidStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.screen.DrainMenu;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class DrainBlockEntity extends MenuFluidStorageBlockEntity<FluidTank> {
    private int progress;
    private int maxProgress = ModConfigs.COMMON_DRAIN_DRAIN_DURATION.getValue();

    public DrainBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.DRAIN_ENTITY.get(), blockPos, blockState,

                "drain",

                FluidStorageSingleTankMethods.INSTANCE,
                ModConfigs.COMMON_DRAIN_FLUID_TANK_CAPACITY.getValue() * 1000
        );
    }

    @Override
    protected FluidTank initFluidStorage() {
        return new FluidTank(baseTankCapacity) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                syncFluidToPlayers(32);
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncFluidToPlayer(player);

        return new DrainMenu(id, inventory, this, this.data);
    }

    public int getRedstoneOutput() {
        return FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("drain.progress", progress);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        progress = view.getIntOr("drain.progress", 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, DrainBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(hasRecipe(blockEntity)) {
            if(blockEntity.progress < 0 || blockEntity.maxProgress < 0) {
                //Reset progress for invalid values

                blockEntity.resetProgress(blockPos, state);
                setChanged(level, blockPos, state);

                return;
            }

            if(blockEntity.progress < blockEntity.maxProgress)
                blockEntity.progress++;

            if(blockEntity.progress >= blockEntity.maxProgress) {
                BlockPos aboveBlockPos = blockPos.above();
                BlockState aboveBlockState = level.getBlockState(aboveBlockPos);
                if(aboveBlockState.getBlock() instanceof BucketPickup aboveBlock) {
                    ItemStack bucketItemStack = aboveBlock.pickupBlock(null, level, aboveBlockPos, aboveBlockState);

                    if(!bucketItemStack.isEmpty()) {
                        level.gameEvent(null, GameEvent.FLUID_PICKUP, aboveBlockPos);

                        IFluidHandlerItem fluidStorage = bucketItemStack.getCapability(Capabilities.FluidHandler.ITEM);
                        if(fluidStorage != null && fluidStorage.getTanks() == 1) {
                            FluidStack fluidStack = fluidStorage.getFluidInTank(0);

                            if(!fluidStack.isEmpty())
                                blockEntity.fluidStorage.fill(fluidStack.copy(), IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                }

                blockEntity.resetProgress(blockPos, state);
            }

            setChanged(level, blockPos, state);
        }else {
            blockEntity.resetProgress(blockPos, state);
            setChanged(level, blockPos, state);
        }
    }

    private void resetProgress(BlockPos blockPos, BlockState state) {
        progress = 0;
    }

    private static boolean hasRecipe(DrainBlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        BlockPos blockPos = blockEntity.getBlockPos();

        BlockPos aboveBlockPos = blockPos.above();
        BlockState aboveBlockState = level.getBlockState(aboveBlockPos);

        if(!(aboveBlockState.getBlock() instanceof BucketPickup))
            return false;

        FluidState fluidState = level.getFluidState(aboveBlockPos);

        if(fluidState.isEmpty())
            return false;

        return blockEntity.fluidStorage.fill(new FluidStack(fluidState.getType(), 1000), IFluidHandler.FluidAction.SIMULATE) == 1000;
    }
}