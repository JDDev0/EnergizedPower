package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FluidFillerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity;
import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.FluidFillerMenu;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidFillerBlockEntity
        extends ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity
        <ReceiveOnlyEnergyStorage, ItemStackHandler, FluidTank>
        implements MenuProvider {
    public static final int MAX_FLUID_FILLING_PER_TICK = ModConfigs.COMMON_FLUID_FILLER_FLUID_ITEM_TRANSFER_RATE.getValue();
    public static final int ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_FLUID_FILLER_ENERGY_CONSUMPTION_PER_TICK.getValue();

    private final LazyOptional<IEnergyStorage> lazyEnergyStorage;

    private final LazyOptional<IItemHandler> lazyItemHandler;
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
                if(i != 0)
                    return false;

                ItemStack stack = itemHandler.getStackInSlot(i);

                LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = stack.getCapability(Capabilities.FLUID_HANDLER_ITEM);
                if(!fluidStorageLazyOptional.isPresent())
                    return true;

                IFluidHandlerItem fluidStorage = fluidStorageLazyOptional.orElse(null);
                for(int j = 0;j < fluidStorage.getTanks();j++) {
                    FluidStack fluidStack = fluidStorage.getFluidInTank(j);
                    if(fluidStorage.getTankCapacity(j) > fluidStack.getAmount() && (FluidFillerBlockEntity.this.fluidStorage.isEmpty() ||
                            (fluidStack.isEmpty() && fluidStorage.isFluidValid(j, FluidFillerBlockEntity.this.fluidStorage.getFluid())) ||
                            fluidStack.isFluidEqual(FluidFillerBlockEntity.this.fluidStorage.getFluid())))
                        return false;
                }

                return true;
            }));

    private final LazyOptional<IFluidHandler> lazyFluidStorage;

    protected final ContainerData data;
    private int fluidFillingLeft = -1;
    private int fluidFillingSumPending = 0;

    public FluidFillerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.FLUID_FILLER_ENTITY.get(), blockPos, blockState,

                ModConfigs.COMMON_FLUID_FILLER_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_FILLER_TRANSFER_RATE.getValue(),

                1,

                FluidStorageSingleTankMethods.INSTANCE,
                ModConfigs.COMMON_FLUID_FILLER_FLUID_TANK_CAPACITY.getValue() * 1000,

                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );

        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(FluidFillerBlockEntity.this.fluidFillingLeft, index);
                    case 2, 3 -> ByteUtils.get2Bytes(FluidFillerBlockEntity.this.fluidFillingSumPending, index - 2);
                    case 4 -> redstoneMode.ordinal();
                    case 5 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2, 3 -> {}
                    case 4 -> FluidFillerBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 5 -> FluidFillerBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        };

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
        lazyFluidStorage = LazyOptional.of(() -> fluidStorage);
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
                    return stack.getCapability(Capabilities.FLUID_HANDLER_ITEM).isPresent();

                return super.isItemValid(slot, stack);
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.isSameItem(stack, itemStack) ||
                            (!ItemStack.isSameItemSameTags(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no fluid item
                                    !(stack.getCapability(Capabilities.FLUID_HANDLER_ITEM).isPresent() &&
                                            itemStack.getCapability(Capabilities.FLUID_HANDLER_ITEM).isPresent()))))
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
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.fluid_filler");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);

        return new FluidFillerMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == Capabilities.FLUID_HANDLER) {
            return lazyFluidStorage.cast();
        }else if(cap == Capabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.put("recipe.fluid_filling_left", IntTag.valueOf(fluidFillingLeft));
        nbt.put("recipe.fluid_filling_sum_pending", IntTag.valueOf(fluidFillingSumPending));
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        fluidFillingLeft = nbt.getInt("recipe.fluid_filling_left");
        fluidFillingSumPending = nbt.getInt("recipe.fluid_filling_sum_pending");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, FluidFillerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(FluidFillerBlock.POWERED)))
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.itemHandler.getStackInSlot(0);

            int fluidFillingSum = 0;
            int fluidFillingLeftSum = 0;

            if(blockEntity.fluidStorage.getFluidAmount() - blockEntity.fluidFillingSumPending <= 0)
                return;

            int energyConsumptionPerTick = Math.max(1, (int)Math.ceil(ENERGY_USAGE_PER_TICK *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.energyStorage.getEnergy() < energyConsumptionPerTick)
                return;

            LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = stack.getCapability(Capabilities.FLUID_HANDLER_ITEM);
            if(!fluidStorageLazyOptional.isPresent())
                return;

            IFluidHandlerItem fluidStorage = fluidStorageLazyOptional.orElse(null);
            for(int i = 0;i < fluidStorage.getTanks();i++) {
                FluidStack fluidStack = fluidStorage.getFluidInTank(i);
                if(fluidStorage.getTankCapacity(i) > fluidStack.getAmount() && (blockEntity.fluidStorage.isEmpty() ||
                        (fluidStack.isEmpty() && fluidStorage.isFluidValid(i, blockEntity.fluidStorage.getFluid())) ||
                        fluidStack.isFluidEqual(blockEntity.fluidStorage.getFluid()))) {
                    fluidFillingSum += Math.min(blockEntity.fluidStorage.getFluidAmount() -
                                    blockEntity.fluidFillingSumPending - fluidFillingSum,
                            Math.min(fluidStorage.getTankCapacity(i) - fluidStack.getAmount(),
                                    MAX_FLUID_FILLING_PER_TICK - fluidFillingSum));

                    fluidFillingLeftSum += fluidStorage.getTankCapacity(i) - fluidStack.getAmount();
                }
            }

            if(fluidFillingSum == 0)
                return;

            blockEntity.fluidFillingLeft = fluidFillingLeftSum;
            blockEntity.fluidFillingSumPending += fluidFillingSum;

            blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick);

            int fluidSumFillable = Math.min(blockEntity.fluidStorage.getFluidAmount(),
                    blockEntity.fluidFillingSumPending);

            FluidStack fluidStackToFill = blockEntity.getFluid(0);

            int fluidSumFilled = fluidStorage.fill(new FluidStack(fluidStackToFill.getFluid(), fluidSumFillable,
                    fluidStackToFill.getTag()), IFluidHandler.FluidAction.EXECUTE);

            if(fluidSumFilled <= 0) {
                setChanged(level, blockPos, state);

                return;
            }

            blockEntity.itemHandler.setStackInSlot(0, fluidStorage.getContainer());

            blockEntity.fluidStorage.drain(fluidSumFilled, IFluidHandler.FluidAction.EXECUTE);
            blockEntity.fluidFillingSumPending -= fluidSumFilled;
            blockEntity.fluidFillingLeft = fluidFillingLeftSum - fluidSumFilled;

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
        if(stack.getCapability(Capabilities.FLUID_HANDLER_ITEM).isPresent()) {
            LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = stack.getCapability(Capabilities.FLUID_HANDLER_ITEM);
            if(!fluidStorageLazyOptional.isPresent())
                return false;

            IFluidHandlerItem fluidStorage = fluidStorageLazyOptional.orElse(null);
            for(int i = 0;i < fluidStorage.getTanks();i++) {
                FluidStack fluidStack = fluidStorage.getFluidInTank(i);
                if(fluidStorage.getTankCapacity(i) > fluidStack.getAmount() && (FluidFillerBlockEntity.this.fluidStorage.isEmpty() ||
                        (fluidStack.isEmpty() && fluidStorage.isFluidValid(i, FluidFillerBlockEntity.this.fluidStorage.getFluid())) ||
                        fluidStack.isFluidEqual(FluidFillerBlockEntity.this.fluidStorage.getFluid())))
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