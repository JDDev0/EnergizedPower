package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FluidDrainerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.InputOutputFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.ComparatorModeValueContainerData;
import me.jddev0.ep.inventory.data.FluidValueContainerData;
import me.jddev0.ep.inventory.data.RedstoneModeValueContainerData;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.FluidDrainerMenu;
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

public class FluidDrainerBlockEntity
        extends ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity
        <EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler, EnergizedPowerFluidStorage> {
    /**
     * MAX_FLUID_DRAINING_PER_TICK is in Milli Buckets
     */
    public static final long MAX_FLUID_DRAINING_PER_TICK = ModConfigs.COMMON_FLUID_DRAINER_FLUID_ITEM_TRANSFER_RATE.getValue();
    public static final long ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_FLUID_DRAINER_ENERGY_CONSUMPTION_PER_TICK.getValue();

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

        if(!fluidStorage.supportsExtraction())
            return true;

        for(StorageView<FluidVariant> fluidView:fluidStorage) {
            FluidVariant fluidVariant = fluidView.getResource();
            if(!fluidVariant.isBlank() && (FluidDrainerBlockEntity.this.fluidStorage.getFluid(0).isEmpty() ||
                    fluidVariant.equals(FluidDrainerBlockEntity.this.fluidStorage.getResource(0))))
                return false;
        }

        return true;
    });
    private final InputOutputFluidStorage fluidStorageSided = new InputOutputFluidStorage(fluidStorage, (i, stack) -> false, i -> true);

    private long fluidDrainingLeft = -1;
    private long fluidDrainingSumPending = 0;

    public FluidDrainerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.FLUID_DRAINER_ENTITY, blockPos, blockState,

                "fluid_drainer",

                ModConfigs.COMMON_FLUID_DRAINER_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_DRAINER_TRANSFER_RATE.getValue(),

                1,

                FluidUtils.convertMilliBucketsToDroplets(ModConfigs.COMMON_FLUID_DRAINER_FLUID_TANK_CAPACITY.getValue() * 1000),

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
                new FluidValueContainerData(() -> fluidDrainingLeft, value -> {}),
                new FluidValueContainerData(() -> fluidDrainingSumPending, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);

        return new FluidDrainerMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable Storage<FluidVariant> getFluidHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return fluidStorage;

        return fluidStorageSided;
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        FluidUtils.writeFluidAmountInMilliBucketsWithLeftover(fluidDrainingLeft,
                "recipe.fluid_draining_left", "recipe.fluid_draining_left_leftover_droplets", view);
        FluidUtils.writeFluidAmountInMilliBucketsWithLeftover(fluidDrainingSumPending,
                "recipe.fluid_draining_sum_pending", "recipe.fluid_draining_sum_pending_leftover_droplets", view);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        fluidDrainingLeft = FluidUtils.readFluidAmountInMilliBucketsWithLeftover("recipe.fluid_draining_left",
                "recipe.fluid_draining_left_leftover_droplets", view);
        fluidDrainingSumPending = FluidUtils.readFluidAmountInMilliBucketsWithLeftover("recipe.fluid_draining_sum_pending",
                "recipe.fluid_draining_sum_pending_leftover_droplets", view);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, FluidDrainerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(FluidDrainerBlock.POWERED)))
            return;

        blockEntity.pullItemsFromInputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_PULLING));

        tickRecipe(level, blockPos, state, blockEntity);

        blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, FluidDrainerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.itemHandler.getStackInSlot(0);
            long fluidDrainingSum = 0;
            long fluidDrainingLeftSum = 0;

            if(blockEntity.fluidStorage.getTankCapacity(0) - blockEntity.fluidStorage.getAmount(0) - blockEntity.fluidDrainingSumPending <= 0)
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

            FluidVariant firstNonEmptyFluidVariant = FluidVariant.blank();
            for(StorageView<FluidVariant> fluidView:fluidStorage) {
                FluidVariant fluidVariant = fluidView.getResource();
                if(!fluidVariant.isBlank() && firstNonEmptyFluidVariant.isBlank())
                    firstNonEmptyFluidVariant = fluidVariant;
                if(!fluidVariant.isBlank() && ((blockEntity.fluidStorage.getFluid(0).isEmpty() &&
                        fluidVariant.equals(firstNonEmptyFluidVariant) ||
                        fluidVariant.equals(blockEntity.fluidStorage.getResource(0))))) {
                    fluidDrainingSum += Math.min(blockEntity.fluidStorage.getTankCapacity(0) -
                                    blockEntity.fluidStorage.getAmount(0) - blockEntity.fluidDrainingSumPending - fluidDrainingSum,
                            Math.min(fluidView.getAmount(), FluidUtils.convertMilliBucketsToDroplets(MAX_FLUID_DRAINING_PER_TICK) -
                                    fluidDrainingSum));

                    fluidDrainingLeftSum += fluidView.getAmount();
                }
            }

            if(firstNonEmptyFluidVariant.isBlank() || fluidDrainingSum == 0)
                return;

            blockEntity.fluidDrainingLeft = fluidDrainingLeftSum;
            blockEntity.fluidDrainingSumPending += fluidDrainingSum;

            try(Transaction transaction = Transaction.openOuter()) {
                blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);

                long fluidSumDrainable = Math.min(blockEntity.fluidStorage.getTankCapacity(0) - blockEntity.fluidStorage.getAmount(0),
                        blockEntity.fluidDrainingSumPending);

                FluidVariant fluidVariantToDrain = blockEntity.fluidStorage.getFluid(0).isEmpty()?firstNonEmptyFluidVariant:
                        blockEntity.fluidStorage.getResource(0);

                long fluidSumDrained = fluidStorage.extract(fluidVariantToDrain, fluidSumDrainable, transaction);

                if(fluidSumDrained > 0) {
                    blockEntity.fluidStorage.insert(fluidVariantToDrain, fluidSumDrained, transaction);
                    blockEntity.fluidDrainingSumPending -= fluidSumDrained;
                    blockEntity.fluidDrainingLeft = fluidDrainingLeftSum - fluidSumDrained;
                }

                transaction.commit();
            }

            if(blockEntity.fluidDrainingLeft <= 0)
                blockEntity.resetProgress();

            setChanged(level, blockPos, state);
        }else {
            blockEntity.resetProgress();
            setChanged(level, blockPos, state);
        }
    }

    private void resetProgress() {
        fluidDrainingLeft = -1;
        fluidDrainingSumPending = 0;
    }

    private boolean hasRecipe() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if(ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null) {
            Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(stack, ContainerItemContext.
                    ofSingleSlot(itemHandler.getSlot(0)));
            if(fluidStorage == null)
                return false;
            for(StorageView<FluidVariant> fluidView:fluidStorage) {
                FluidVariant fluidVariant = fluidView.getResource();
                if(!fluidVariant.isBlank() && (FluidDrainerBlockEntity.this.fluidStorage.getFluid(0).isEmpty() ||
                        fluidVariant.equals(FluidDrainerBlockEntity.this.fluidStorage.getResource(0))))
                    return true;
            }
        }

        return false;
    }
}