package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FluidFillerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity;
import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.FluidFillerMenu;
import me.jddev0.ep.util.CapabilityUtil;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidFillerBlockEntity
        extends ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity
        <EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler, SimpleFluidStorage> {
    public static final int MAX_FLUID_FILLING_PER_TICK = ModConfigs.COMMON_FLUID_FILLER_FLUID_ITEM_TRANSFER_RATE.getValue();
    public static final int ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_FLUID_FILLER_ENERGY_CONSUMPTION_PER_TICK.getValue();

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i != 0)
            return false;

        ItemStack stack = itemHandler.getStackInSlot(i);

        ResourceHandler<FluidResource> fluidStorage = stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forHandlerIndexStrict(itemHandler, i));
        if(fluidStorage == null)
            return true;

        for(int j = 0;j < fluidStorage.size();j++) {
            FluidResource fluidResource = fluidStorage.getResource(j);
            FluidResource fluidResourceToInsert = fluidResource.isEmpty()?FluidFillerBlockEntity.this.fluidStorage.getResource(0):fluidResource;
            if(fluidStorage.getCapacityAsInt(j, fluidResourceToInsert) > fluidStorage.getAmountAsInt(j) && (FluidFillerBlockEntity.this.fluidStorage.isEmpty() ||
                    (fluidResource.isEmpty() && fluidStorage.isValid(j, FluidFillerBlockEntity.this.fluidStorage.getResource(0))) ||
                    fluidResource.matches(FluidFillerBlockEntity.this.fluidStorage.getFluid())))
                return false;
        }

        return true;
    });

    private int fluidFillingLeft = -1;
    private int fluidFillingSumPending = 0;

    public FluidFillerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.FLUID_FILLER_ENTITY.get(), blockPos, blockState,

                "fluid_filler",

                ModConfigs.COMMON_FLUID_FILLER_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_FILLER_TRANSFER_RATE.getValue(),

                1,

                FluidStorageSingleTankMethods.INSTANCE,
                ModConfigs.COMMON_FLUID_FILLER_FLUID_TANK_CAPACITY.getValue() * 1000,

                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity, baseEnergyCapacity, baseEnergyCapacity) {
            @Override
            public long getCapacityAsLong() {
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
            public int getMaxInsert() {
                return Math.max(1, (int)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public int getCapacity(int index, ItemResource resource) {
                return 1;
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemResource resource) {
                ItemStack stack = resource.toStack();

                if(slot == 0)
                    return CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Fluid.ITEM, stack) != null;

                return super.isValid(slot, resource);
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() && (!ItemStack.isSameItem(stack, previousItemStack) ||
                            (!ItemStack.isSameItemSameComponents(stack, previousItemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no fluid item
                                    !(CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Fluid.ITEM, stack) != null &&
                                            CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Fluid.ITEM, previousItemStack) != null))))
                        resetProgress();
                }

                setChanged();
            }
        };
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

    public @Nullable ResourceHandler<ItemResource> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable ResourceHandler<FluidResource> getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable EnergyHandler getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("recipe.fluid_filling_left", fluidFillingLeft);
        view.putInt("recipe.fluid_filling_sum_pending", fluidFillingSumPending);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        fluidFillingLeft = view.getIntOr("recipe.fluid_filling_left", 0);
        fluidFillingSumPending = view.getIntOr("recipe.fluid_filling_sum_pending", 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, FluidFillerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(FluidFillerBlock.POWERED)))
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.itemHandler.getStackInSlot(0);

            int fluidFillingSum = 0;
            int fluidFillingLeftSum = 0;

            if(blockEntity.fluidStorage.getAmountAsInt(0) - blockEntity.fluidFillingSumPending <= 0)
                return;

            int energyConsumptionPerTick = Math.max(1, (int)Math.ceil(ENERGY_USAGE_PER_TICK *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.energyStorage.getAmountAsInt() < energyConsumptionPerTick)
                return;

            ResourceHandler<FluidResource> fluidStorage = stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forHandlerIndexStrict(blockEntity.itemHandler, 0));
            if(fluidStorage == null)
                return;

            for(int i = 0;i < fluidStorage.size();i++) {
                FluidResource fluidResource = fluidStorage.getResource(i);
                FluidResource fluidResourceToInsert = fluidResource.isEmpty()?blockEntity.fluidStorage.getResource(0):fluidResource;
                if(fluidStorage.getCapacityAsInt(i, fluidResourceToInsert) > fluidStorage.getAmountAsInt(i) && (blockEntity.fluidStorage.isEmpty() ||
                        (fluidResource.isEmpty() && fluidStorage.isValid(i, blockEntity.fluidStorage.getResource(0))) ||
                        fluidResource.matches(blockEntity.fluidStorage.getFluid()))) {
                    fluidFillingSum += Math.min(blockEntity.fluidStorage.getAmountAsInt(0) -
                                    blockEntity.fluidFillingSumPending - fluidFillingSum,
                            Math.min(fluidStorage.getCapacityAsInt(i, fluidResourceToInsert) - fluidStorage.getAmountAsInt(i),
                                    MAX_FLUID_FILLING_PER_TICK - fluidFillingSum));

                    fluidFillingLeftSum += fluidStorage.getCapacityAsInt(i, fluidResourceToInsert) - fluidStorage.getAmountAsInt(i);
                }
            }

            if(fluidFillingSum == 0)
                return;

            blockEntity.fluidFillingLeft = fluidFillingLeftSum;
            blockEntity.fluidFillingSumPending += fluidFillingSum;

            try(Transaction transaction = Transaction.open(null)) {
                blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);

                int fluidSumFillable = Math.min(blockEntity.fluidStorage.getAmountAsInt(0),
                        blockEntity.fluidFillingSumPending);

                FluidResource fluidResourceToFill = blockEntity.fluidStorage.getResource(0);

                int fluidSumFilled = fluidStorage.insert(fluidResourceToFill, fluidSumFillable, transaction);

                if(fluidSumFilled > 0) {
                    blockEntity.fluidStorage.extract(fluidResourceToFill, fluidSumFilled, transaction);
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
        ResourceHandler<FluidResource> fluidStorage = stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forHandlerIndexStrict(itemHandler, 0));
        if(fluidStorage != null) {
            for(int i = 0;i < fluidStorage.size();i++) {
                FluidResource fluidResource = fluidStorage.getResource(i);
                FluidResource fluidResourceToInsert = fluidResource.isEmpty()?FluidFillerBlockEntity.this.fluidStorage.getResource(0):fluidResource;
                if(fluidStorage.getCapacityAsInt(i, fluidResourceToInsert) > fluidStorage.getAmountAsInt(i) && (FluidFillerBlockEntity.this.fluidStorage.isEmpty() ||
                        (fluidResource.isEmpty() && fluidStorage.isValid(i, FluidFillerBlockEntity.this.fluidStorage.getResource(0))) ||
                        fluidResource.matches(FluidFillerBlockEntity.this.fluidStorage.getFluid())))
                    return true;
            }
        }

        return false;
    }

    @Override
    protected void updateUpgradeModules() {
        resetProgress();

        super.updateUpgradeModules();
    }
}