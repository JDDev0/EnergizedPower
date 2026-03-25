package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.MenuFluidStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.screen.DrainMenu;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.CauldronFluidContent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class DrainBlockEntity extends MenuFluidStorageBlockEntity<SimpleFluidStorage> {
    private int progress;
    private int maxProgress = ModConfigs.COMMON_DRAIN_DRAIN_DURATION.getValue();

    public DrainBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.DRAIN_ENTITY, blockPos, blockState,

                "drain",

                FluidStorageSingleTankMethods.INSTANCE,
                FluidUtils.convertMilliBucketsToDroplets(ModConfigs.COMMON_DRAIN_FLUID_TANK_CAPACITY.getValue() * 1000)
        );
    }

    @Override
    protected SimpleFluidStorage initFluidStorage() {
        return new SimpleFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
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

        return new DrainMenu(id, this, inventory, this.data);
    }

    public int getRedstoneOutput() {
        return FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
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

                        Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(bucketItemStack, ContainerItemContext.withConstant(bucketItemStack));
                        if(fluidStorage != null) {
                            Iterator<StorageView<FluidVariant>> iterator = fluidStorage.iterator();
                            if(iterator.hasNext()) {
                                StorageView<FluidVariant> fluidView = iterator.next();
                                if(!iterator.hasNext()) {
                                    FluidVariant fluidVariant = fluidView.getResource();
                                    if(!fluidVariant.isBlank()) {
                                        try(Transaction transaction = Transaction.openOuter()) {
                                            blockEntity.fluidStorage.insert(fluidVariant, fluidView.getAmount(), transaction);
                                            transaction.commit();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else {
                    CauldronFluidContent cauldronFluidContent = CauldronFluidContent.getForBlock(aboveBlockState.getBlock());
                    if(cauldronFluidContent != null) {
                        Fluid cauldronFluid = cauldronFluidContent.fluid;
                        if(cauldronFluid != Fluids.EMPTY && (blockEntity.fluidStorage.isEmpty() ||
                                blockEntity.fluidStorage.getFluid().getFluid() == cauldronFluid)) {
                            long cauldronAmountPerLevel = cauldronFluidContent.amountPerLevel;
                            IntegerProperty cauldronLevelProp = cauldronFluidContent.levelProperty;

                            long amountInsertable = blockEntity.fluidStorage.capacity - blockEntity.fluidStorage.amount;

                            long cauldronAmount;
                            if(cauldronLevelProp != null && aboveBlockState.hasProperty(cauldronLevelProp))
                                cauldronAmount = cauldronAmountPerLevel * aboveBlockState.getValue(cauldronLevelProp);
                            else
                                cauldronAmount = cauldronAmountPerLevel;

                            if(cauldronAmount > 0 && cauldronAmount <= amountInsertable) {
                                long inserted;
                                try(Transaction transaction = Transaction.openOuter()) {
                                    inserted = blockEntity.fluidStorage.insert(FluidVariant.of(cauldronFluid), cauldronAmount, transaction);
                                    if(inserted == cauldronAmount) {
                                        transaction.commit();
                                    }
                                }

                                if(inserted == cauldronAmount) {
                                    level.setBlock(aboveBlockPos, Blocks.CAULDRON.defaultBlockState(), 3);
                                }
                            }
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

        if((aboveBlockState.getBlock() instanceof BucketPickup)) {
            FluidState fluidState = level.getFluidState(aboveBlockPos);
            if(!fluidState.isEmpty()) {
                try(Transaction transaction = Transaction.openOuter()) {
                    return blockEntity.fluidStorage.insert(FluidVariant.of(fluidState.getType()), FluidConstants.BLOCK,
                            transaction) == FluidConstants.BLOCK;
                }
            }
        }

        CauldronFluidContent cauldronFluidContent = CauldronFluidContent.getForBlock(aboveBlockState.getBlock());
        if(cauldronFluidContent == null)
            return false;

        Fluid cauldronFluid = cauldronFluidContent.fluid;
        if(cauldronFluid == Fluids.EMPTY ||
                (!blockEntity.fluidStorage.isEmpty() && blockEntity.fluidStorage.getFluid().getFluid() != cauldronFluid))
            return false;

        long cauldronAmountPerLevel = cauldronFluidContent.amountPerLevel;
        IntegerProperty cauldronLevelProp = cauldronFluidContent.levelProperty;

        long amountInsertable = blockEntity.fluidStorage.capacity - blockEntity.fluidStorage.amount;

        long cauldronAmount;
        if(cauldronLevelProp != null && aboveBlockState.hasProperty(cauldronLevelProp))
            cauldronAmount = cauldronAmountPerLevel * aboveBlockState.getValue(cauldronLevelProp);
        else
            cauldronAmount = cauldronAmountPerLevel;

        return cauldronAmount > 0 && cauldronAmount <= amountInsertable;
    }
}