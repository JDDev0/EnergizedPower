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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidDrainerBlockEntity
        extends ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity
        <ReceiveOnlyEnergyStorage, ItemStackHandler, FluidTank> {
    public static final int MAX_FLUID_DRAINING_PER_TICK = ModConfigs.COMMON_FLUID_DRAINER_FLUID_ITEM_TRANSFER_RATE.getValue();
    public static final int ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_FLUID_DRAINER_ENERGY_CONSUMPTION_PER_TICK.getValue();

    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
                if(i != 0)
                    return false;

                ItemStack stack = itemHandler.getStackInSlot(i);

                LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
                if(!fluidStorageLazyOptional.isPresent())
                    return true;

                IFluidHandlerItem fluidStorage = fluidStorageLazyOptional.orElse(null);
                for(int j = 0;j < fluidStorage.getTanks();j++) {
                    FluidStack fluidStack = fluidStorage.getFluidInTank(j);
                    if(!fluidStack.isEmpty() && ((FluidDrainerBlockEntity.this.fluidStorage.isEmpty() &&
                            FluidDrainerBlockEntity.this.fluidStorage.isFluidValid(fluidStack)) ||
                            fluidStack.isFluidEqual(FluidDrainerBlockEntity.this.fluidStorage.getFluid())))
                        return false;
                }

                return true;
            }));

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
                    return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();

                return super.isItemValid(slot, stack);
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.isSameItem(stack, itemStack) ||
                            (!ItemStack.isSameItemSameTags(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no fluid item
                                    !(stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent() &&
                                            itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()))))
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

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidStorage.cast();
        }else if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.put("recipe.fluid_draining_left", IntTag.valueOf(fluidDrainingLeft));
        nbt.put("recipe.fluid_draining_sum_pending", IntTag.valueOf(fluidDrainingSumPending));
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        fluidDrainingLeft = nbt.getInt("recipe.fluid_draining_left");
        fluidDrainingSumPending = nbt.getInt("recipe.fluid_draining_sum_pending");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, FluidDrainerBlockEntity blockEntity) {
        if(level.isClientSide)
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

            LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
            if(!fluidStorageLazyOptional.isPresent())
                return;

            IFluidHandlerItem fluidStorage = fluidStorageLazyOptional.orElse(null);
            FluidStack firstNonEmptyFluidStack = FluidStack.EMPTY;
            for(int i = 0;i < fluidStorage.getTanks();i++) {
                FluidStack fluidStack = fluidStorage.getFluidInTank(i);
                if(!fluidStack.isEmpty() && firstNonEmptyFluidStack.isEmpty())
                    firstNonEmptyFluidStack = fluidStack;
                if(!fluidStack.isEmpty() && ((blockEntity.fluidStorage.isEmpty() &&
                        blockEntity.fluidStorage.isFluidValid(fluidStack) &&
                        fluidStack.isFluidEqual(firstNonEmptyFluidStack)) ||
                        fluidStack.isFluidEqual(blockEntity.fluidStorage.getFluid()))) {
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

            FluidStack fluidSumDrained = fluidStorage.drain(new FluidStack(fluidStackToDrain.getFluid(), fluidSumDrainable,
                            fluidStackToDrain.getTag()), IFluidHandler.FluidAction.EXECUTE);

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
        if(stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
            LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
            if(!fluidStorageLazyOptional.isPresent())
                return false;

            IFluidHandlerItem fluidStorage = fluidStorageLazyOptional.orElse(null);
            for(int i = 0;i < fluidStorage.getTanks();i++) {
                FluidStack fluidStack = fluidStorage.getFluidInTank(i);
                if(!fluidStack.isEmpty() && ((FluidDrainerBlockEntity.this.fluidStorage.isEmpty() &&
                        FluidDrainerBlockEntity.this.fluidStorage.isFluidValid(fluidStack)) ||
                        fluidStack.isFluidEqual(FluidDrainerBlockEntity.this.fluidStorage.getFluid())))
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