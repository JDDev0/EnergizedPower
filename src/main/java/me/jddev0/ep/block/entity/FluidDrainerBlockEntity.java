package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FluidDrainerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity;
import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.FluidDrainerMenu;
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
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidDrainerBlockEntity
        extends ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity
        <ReceiveOnlyEnergyStorage, ItemStackHandler, FluidTank> {
    public static final int MAX_FLUID_DRAINING_PER_TICK = ModConfigs.COMMON_FLUID_DRAINER_FLUID_ITEM_TRANSFER_RATE.getValue();
    public static final int ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_FLUID_DRAINER_ENERGY_CONSUMPTION_PER_TICK.getValue();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i != 0)
            return false;

        ItemStack stack = itemHandler.getStackInSlot(i);

        IFluidHandlerItem fluidStorage = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if(fluidStorage == null)
            return true;

        for(int j = 0;j < fluidStorage.getTanks();j++) {
            FluidStack fluidStack = fluidStorage.getFluidInTank(j);
            if(!fluidStack.isEmpty() && ((FluidDrainerBlockEntity.this.fluidStorage.isEmpty() &&
                    FluidDrainerBlockEntity.this.fluidStorage.isFluidValid(fluidStack)) ||
                    FluidStack.isSameFluidSameComponents(fluidStack, FluidDrainerBlockEntity.this.fluidStorage.getFluid())))
                return false;
        }

        return true;
    });

    private int fluidDrainingLeft = -1;
    private int fluidDrainingSumPending = 0;

    public FluidDrainerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.FLUID_DRAINER_ENTITY.get(), blockPos, blockState,

                "fluid_drainer",

                ModConfigs.COMMON_FLUID_DRAINER_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_DRAINER_TRANSFER_RATE.getValue(),

                1,

                FluidStorageSingleTankMethods.INSTANCE,
                ModConfigs.COMMON_FLUID_DRAINER_FLUID_TANK_CAPACITY.getValue() * 1000,

                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected ReceiveOnlyEnergyStorage initEnergyStorage() {
        return new ReceiveOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            public int getMaxReceive() {
                return Math.max(1, (int)Math.ceil(maxReceive * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }

            @Override
            protected void onChange() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                if(slot == 0)
                    return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;

                return super.isItemValid(slot, stack);
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.isSameItem(stack, itemStack) ||
                            (!ItemStack.isSameItemSameComponents(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no fluid item
                                    !(stack.getCapability(Capabilities.FluidHandler.ITEM) != null &&
                                            itemStack.getCapability(Capabilities.FluidHandler.ITEM) != null))))
                        resetProgress();
                }

                super.setStackInSlot(slot, stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
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

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("recipe.fluid_draining_left", fluidDrainingLeft);
        view.putInt("recipe.fluid_draining_sum_pending", fluidDrainingSumPending);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        fluidDrainingLeft = view.getIntOr("recipe.fluid_draining_left", 0);
        fluidDrainingSumPending = view.getIntOr("recipe.fluid_draining_sum_pending", 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, FluidDrainerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(FluidDrainerBlock.POWERED)))
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.itemHandler.getStackInSlot(0);
            int fluidDrainingSum = 0;
            int fluidDrainingLeftSum = 0;

            if(blockEntity.fluidStorage.getCapacity() - blockEntity.fluidStorage.getFluidAmount() - blockEntity.fluidDrainingSumPending <= 0)
                return;

            int energyConsumptionPerTick = Math.max(1, (int)Math.ceil(ENERGY_USAGE_PER_TICK *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.energyStorage.getEnergy() < energyConsumptionPerTick)
                return;

            IFluidHandlerItem fluidStorage = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if(fluidStorage == null)
                return;

            FluidStack firstNonEmptyFluidStack = FluidStack.EMPTY;
            for(int i = 0;i < fluidStorage.getTanks();i++) {
                FluidStack fluidStack = fluidStorage.getFluidInTank(i);
                if(!fluidStack.isEmpty() && firstNonEmptyFluidStack.isEmpty())
                    firstNonEmptyFluidStack = fluidStack;
                if(!fluidStack.isEmpty() && ((blockEntity.fluidStorage.isEmpty() &&
                        blockEntity.fluidStorage.isFluidValid(fluidStack) &&
                        FluidStack.isSameFluidSameComponents(fluidStack, firstNonEmptyFluidStack)) ||
                        FluidStack.isSameFluidSameComponents(fluidStack, blockEntity.fluidStorage.getFluid()))) {
                    fluidDrainingSum += Math.min(blockEntity.fluidStorage.getCapacity() -
                                    blockEntity.fluidStorage.getFluidAmount() - blockEntity.fluidDrainingSumPending -
                                    fluidDrainingSum,
                            Math.min(fluidStack.getAmount(), MAX_FLUID_DRAINING_PER_TICK - fluidDrainingSum));

                    fluidDrainingLeftSum += fluidStack.getAmount();
                }
            }

            if(firstNonEmptyFluidStack.isEmpty() || fluidDrainingSum == 0)
                return;

            blockEntity.fluidDrainingLeft = fluidDrainingLeftSum;
            blockEntity.fluidDrainingSumPending += fluidDrainingSum;

            blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick);

            int fluidSumDrainable = Math.min(blockEntity.fluidStorage.getCapacity() - blockEntity.fluidStorage.getFluidAmount(),
                    blockEntity.fluidDrainingSumPending);

            FluidStack fluidStackToDrain = blockEntity.fluidStorage.isEmpty()?firstNonEmptyFluidStack:blockEntity.getFluid(0);

            FluidStack fluidSumDrained = fluidStorage.drain(new FluidStack(fluidStackToDrain.getFluidHolder(), fluidSumDrainable,
                            fluidStackToDrain.getComponentsPatch()), IFluidHandler.FluidAction.EXECUTE);

            if(fluidSumDrained.isEmpty()) {
                setChanged(level, blockPos, state);

                return;
            }

            blockEntity.itemHandler.setStackInSlot(0, fluidStorage.getContainer());

            blockEntity.fluidStorage.fill(fluidSumDrained, IFluidHandler.FluidAction.EXECUTE);
            blockEntity.fluidDrainingSumPending -= fluidSumDrained.getAmount();
            blockEntity.fluidDrainingLeft = fluidDrainingLeftSum - fluidSumDrained.getAmount();

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
        if(stack.getCapability(Capabilities.FluidHandler.ITEM) != null) {
            IFluidHandlerItem fluidStorage = stack.getCapability(Capabilities.FluidHandler.ITEM);
            if(fluidStorage == null)
                return false;

            for(int i = 0;i < fluidStorage.getTanks();i++) {
                FluidStack fluidStack = fluidStorage.getFluidInTank(i);
                if(!fluidStack.isEmpty() && ((FluidDrainerBlockEntity.this.fluidStorage.isEmpty() &&
                        FluidDrainerBlockEntity.this.fluidStorage.isFluidValid(fluidStack)) ||
                        FluidStack.isSameFluidSameComponents(fluidStack, FluidDrainerBlockEntity.this.fluidStorage.getFluid())))
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