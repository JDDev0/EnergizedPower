package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FluidFillerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.ComparatorModeValueContainerData;
import me.jddev0.ep.inventory.data.FluidValueContainerData;
import me.jddev0.ep.inventory.data.RedstoneModeValueContainerData;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.FluidFillerMenu;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import team.reborn.energy.api.EnergyStorage;

public class FluidFillerBlockEntity
        extends ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity
        <EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler, EnergizedPowerFluidStorage> {
    /**
     * MAX_FLUID_DRAINING_PER_TICK is in Milli Buckets
     */
    public static final long MAX_FLUID_FILLING_PER_TICK = ModConfigs.COMMON_FLUID_FILLER_FLUID_ITEM_TRANSFER_RATE.getValue();
    public static final long ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_FLUID_FILLER_ENERGY_CONSUMPTION_PER_TICK.getValue();

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i != 0)
            return false;

        ItemStack stack = itemHandler.getStackInSlot(i);

        if(ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) == null)
            return true;

        Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(stack, ContainerItemContext.
                ofSingleSlot(itemHandler.getSlot(i)));
        if(fluidStorage == null)
            return true;

        if(!fluidStorage.supportsInsertion())
            return true;

        boolean isFluidValid = false;
        if(!FluidFillerBlockEntity.this.fluidStorage.getFluid(0).isEmpty()) {
            //Get current transaction for simulation only [Transaction is necessary to find out if a fluid variant is valid]
            try(Transaction transaction = Transaction.openNested(Transaction.getCurrentUnsafe())) {
                long inserted = fluidStorage.insert(FluidFillerBlockEntity.this.fluidStorage.getResource(0),
                        Long.MAX_VALUE, transaction);
                isFluidValid = inserted > 0;

                transaction.abort();
            }
        }

        for(StorageView<FluidVariant> fluidView:fluidStorage) {
            FluidVariant fluidVariant = fluidView.getResource();

            if(fluidView.getCapacity() > fluidView.getAmount() && (FluidFillerBlockEntity.this.fluidStorage.getFluid(0).isEmpty() ||
                    (fluidVariant.isBlank() && isFluidValid) ||
                    fluidVariant.equals(FluidFillerBlockEntity.this.fluidStorage.getResource(0))))
                return false;
        }

        return true;
    });

    private long fluidFillingLeft = -1;
    private long fluidFillingSumPending = 0;

    public FluidFillerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.FLUID_FILLER_ENTITY, blockPos, blockState,

                "fluid_filler",

                ModConfigs.COMMON_FLUID_FILLER_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_FILLER_TRANSFER_RATE.getValue(),

                1,

                FluidUtils.convertMilliBucketsToDroplets(ModConfigs.COMMON_FLUID_FILLER_FLUID_TANK_CAPACITY.getValue() * 1000),

                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity) {
            @Override
            public long getCapacity() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, 0) {
            @Override
            public long getMaxInsert() {
                return Math.max(1, (long)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public long getCapacity(int index, ItemVariant resource) {
                return 1;
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemVariant resource) {
                ItemStack stack = resource.toStack();

                if(slot == 0)
                    return ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null;

                return super.isValid(slot, resource);
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0) {
                    ItemStack stack = getStackInSlot(slot);
                    if(!stack.isEmpty() && !previousItemStack.isEmpty() &&
                            (!ItemStack.isSameItem(stack, previousItemStack) || (!ItemStack.isSameItemSameComponents(stack, previousItemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no fluid item
                                    !(ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null &&
                                            ContainerItemContext.withConstant(previousItemStack).find(FluidStorage.ITEM) != null))))
                        resetProgress();
                }

                setChanged();
            }
        };
    }

    @Override
    protected EnergizedPowerFluidStorage initFluidStorage() {
        return new EnergizedPowerFluidStorage(baseTankCapacity) {
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
                new FluidValueContainerData(() -> fluidFillingLeft, value -> {}),
                new FluidValueContainerData(() -> fluidFillingSumPending, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);

        return new FluidFillerMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable Storage<FluidVariant> getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        FluidUtils.writeFluidAmountInMilliBucketsWithLeftover(fluidFillingLeft,
                "recipe.fluid_filling_left", "recipe.fluid_filling_left_leftover_droplets", view);
        FluidUtils.writeFluidAmountInMilliBucketsWithLeftover(fluidFillingSumPending,
                "recipe.fluid_filling_sum_pending", "recipe.fluid_filling_sum_pending_leftover_droplets", view);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        fluidFillingLeft = FluidUtils.readFluidAmountInMilliBucketsWithLeftover("recipe.fluid_filling_left",
                "recipe.fluid_filling_left_leftover_droplets", view);
        fluidFillingSumPending = FluidUtils.readFluidAmountInMilliBucketsWithLeftover("recipe.fluid_filling_sum_pending",
                "recipe.fluid_filling_sum_pending_leftover_droplets", view);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, FluidFillerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(FluidFillerBlock.POWERED)))
            return;

        blockEntity.pullItemsFromInputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_PULLING));

        tickRecipe(level, blockPos, state, blockEntity);

        blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, FluidFillerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.itemHandler.getStackInSlot(0);
            long fluidFillingSum = 0;
            long fluidFillingLeftSum = 0;

            if(blockEntity.fluidStorage.getAmount(0) - blockEntity.fluidFillingSumPending <= 0)
                return;

            long energyConsumptionPerTick = Math.max(1, (long)Math.ceil(ENERGY_USAGE_PER_TICK *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.energyStorage.getAmount() < energyConsumptionPerTick)
                return;

            if(ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) == null)
                return;

            Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(stack, ContainerItemContext.
                    ofSingleSlot(blockEntity.itemHandler.getSlot(0)));
            if(fluidStorage == null)
                return;

            boolean isFluidValid = false;
            if(!blockEntity.fluidStorage.getFluid(0).isEmpty()) {
                try(Transaction transaction = Transaction.openOuter()) {
                    long inserted = fluidStorage.insert(blockEntity.fluidStorage.getResource(0),
                            Long.MAX_VALUE, transaction);
                    isFluidValid = inserted > 0;

                    transaction.abort();
                }
            }

            for(StorageView<FluidVariant> fluidView:fluidStorage) {
                FluidVariant fluidVariant = fluidView.getResource();
                if(fluidView.getCapacity() > fluidView.getAmount() && ((blockEntity.fluidStorage.getFluid(0).isEmpty() ||
                        (fluidVariant.isBlank() && isFluidValid)) ||
                        fluidVariant.equals(blockEntity.fluidStorage.getResource(0)))) {
                    fluidFillingSum += Math.min(blockEntity.fluidStorage.getAmount(0) -
                                    blockEntity.fluidFillingSumPending - fluidFillingSum,
                            Math.min(fluidView.getCapacity() - fluidView.getAmount(),
                                    FluidUtils.convertMilliBucketsToDroplets(MAX_FLUID_FILLING_PER_TICK) - fluidFillingSum));

                    fluidFillingLeftSum += fluidView.getCapacity() - fluidView.getAmount();
                }
            }

            if(fluidFillingSum == 0)
                return;

            blockEntity.fluidFillingLeft = fluidFillingLeftSum;
            blockEntity.fluidFillingSumPending += fluidFillingSum;

            try(Transaction transaction = Transaction.openOuter()) {
                blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);

                long fluidSumFillable = Math.min(blockEntity.fluidStorage.getAmount(0),
                        blockEntity.fluidFillingSumPending);

                FluidVariant fluidVariantToFill = blockEntity.fluidStorage.getResource(0);

                long fluidSumFilled = fluidStorage.insert(fluidVariantToFill, fluidSumFillable, transaction);

                if(fluidSumFilled > 0) {
                    blockEntity.fluidStorage.extract(fluidVariantToFill, fluidSumFilled, transaction);
                    blockEntity.fluidFillingSumPending -= fluidSumFilled;
                    blockEntity.fluidFillingLeft = fluidFillingLeftSum - fluidSumFilled;
                }

                transaction.commit();
            }

            if(blockEntity.fluidFillingLeft <= 0)
                blockEntity.resetProgress();

            setChanged(level, blockPos, state);
        }else {
            blockEntity.resetProgress();
            setChanged(level, blockPos, state);
        }
    }

    private void resetProgress() {
        fluidFillingLeft = -1;
        fluidFillingSumPending = 0;
    }

    private boolean hasRecipe() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if(ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null) {
            Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(stack, ContainerItemContext.
                    ofSingleSlot(itemHandler.getSlot(0)));
            if(fluidStorage == null)
                return false;

            boolean isFluidValid = false;
            if(!FluidFillerBlockEntity.this.fluidStorage.getFluid(0).isEmpty()) {
                try(Transaction transaction = Transaction.openOuter()) {
                    long inserted = fluidStorage.insert(FluidFillerBlockEntity.this.fluidStorage.getResource(0),
                            Long.MAX_VALUE, transaction);
                    isFluidValid = inserted > 0;

                    transaction.abort();
                }
            }

            for(StorageView<FluidVariant> fluidView:fluidStorage) {
                FluidVariant fluidVariant = fluidView.getResource();
                if(fluidView.getCapacity() > fluidView.getAmount() && (FluidFillerBlockEntity.this.fluidStorage.getFluid(0).isEmpty() ||
                        (fluidVariant.isBlank() && isFluidValid) ||
                        fluidVariant.equals(FluidFillerBlockEntity.this.fluidStorage.getResource(0))))
                    return true;
            }
        }

        return false;
    }
}