package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import me.jddev0.ep.screen.FluidFillerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidFillerBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate,
        FluidStoragePacketUpdate {
    public static final int MAX_FLUID_FILLING_PER_TICK = 100;
    public static final int ENERGY_USAGE_PER_TICK = 64;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
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
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
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
                    if(fluidStorage.getTankCapacity(j) > fluidStack.getAmount() && (FluidFillerBlockEntity.this.fluidStorage.isEmpty() ||
                            (fluidStack.isEmpty() && fluidStorage.isFluidValid(j, FluidFillerBlockEntity.this.fluidStorage.getFluid())) ||
                            fluidStack.isFluidEqual(FluidFillerBlockEntity.this.fluidStorage.getFluid())))
                        return false;
                }

                return true;
            }));

    private final ReceiveOnlyEnergyStorage energyStorage;

    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    private final FluidTank fluidStorage;
    private LazyOptional<IFluidHandler> lazyFluidStorage = LazyOptional.empty();

    protected final ContainerData data;
    private int fluidFillingLeft = -1;
    private int fluidFillingSumPending = 0;

    public FluidFillerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.FLUID_FILLER_ENTITY.get(), blockPos, blockState);

        energyStorage = new ReceiveOnlyEnergyStorage(0, 2048, 128) {
            @Override
            protected void onChange() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToAllPlayers(new EnergySyncS2CPacket(energy, capacity, getBlockPos()));
            }
        };
        fluidStorage = new FluidTank(8000) {
            @Override
            protected void onContentsChanged() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToAllPlayers(new FluidSyncS2CPacket(fluid, capacity, getBlockPos()));
            }
        };
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(FluidFillerBlockEntity.this.fluidFillingLeft, index);
                    case 2, 3 -> ByteUtils.get2Bytes(FluidFillerBlockEntity.this.fluidFillingSumPending, index - 2);
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2, 3 -> {}
                }
            }

            @Override
            public int getCount() {
                return 4;
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
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(), getBlockPos()), (ServerPlayer)player);
        ModMessages.sendToPlayer(new FluidSyncS2CPacket(fluidStorage.getFluid(), fluidStorage.getCapacity(), worldPosition), (ServerPlayer)player);

        return new FluidFillerMenu(id, inventory, this, this.data);
    }

    public int getRedstoneOutput() {
        return InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
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
    public void onLoad() {
        super.onLoad();

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
        lazyFluidStorage = LazyOptional.of(() -> fluidStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyItemHandler.invalidate();
        lazyEnergyStorage.invalidate();
        lazyFluidStorage.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.put("energy", energyStorage.saveNBT());
        nbt.put("fluid", fluidStorage.writeToNBT(new CompoundTag()));

        nbt.put("recipe.fluid_filling_left", IntTag.valueOf(fluidFillingLeft));
        nbt.put("recipe.fluid_filling_sum_pending", IntTag.valueOf(fluidFillingSumPending));

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyStorage.loadNBT(nbt.get("energy"));
        fluidStorage.readFromNBT(nbt.getCompound("fluid"));

        fluidFillingLeft = nbt.getInt("recipe.fluid_filling_left");
        fluidFillingSumPending = nbt.getInt("recipe.fluid_filling_sum_pending");
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, FluidFillerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.itemHandler.getStackInSlot(0);

            int fluidFillingSum = 0;
            int fluidFillingLeftSum = 0;

            if(blockEntity.fluidStorage.getFluidAmount() - blockEntity.fluidFillingSumPending <= 0)
                return;

            if(blockEntity.energyStorage.getEnergy() < ENERGY_USAGE_PER_TICK)
                return;

            LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
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

            blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - ENERGY_USAGE_PER_TICK);

            int fluidSumFillable = Math.min(blockEntity.fluidStorage.getFluidAmount(),
                    blockEntity.fluidFillingSumPending);

            FluidStack fluidStackToFill = blockEntity.getFluid();

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

            setChanged(level, blockPos, state);

            if(blockEntity.fluidFillingLeft <= 0)
                blockEntity.resetProgress();
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
        if(stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()) {
            LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
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

    public FluidStack getFluid() {
        return fluidStorage.getFluid();
    }

    public int getTankCapacity() {
        return fluidStorage.getCapacity();
    }

    public int getEnergy() {
        return energyStorage.getEnergy();
    }

    public int getCapacity() {
        return energyStorage.getCapacity();
    }

    @Override
    public void setEnergy(int energy) {
        energyStorage.setEnergyWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }

    @Override
    public void setFluid(FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(int capacity) {
        fluidStorage.setCapacity(capacity);
    }
}