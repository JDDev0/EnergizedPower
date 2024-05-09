package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FluidDrainerBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import me.jddev0.ep.screen.FluidDrainerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerListener;
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

public class FluidDrainerBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate,
        FluidStoragePacketUpdate, RedstoneModeUpdate, ComparatorModeUpdate {
    public static final int MAX_FLUID_DRAINING_PER_TICK = ModConfigs.COMMON_FLUID_DRAINER_FLUID_ITEM_TRANSFER_RATE.getValue();
    public static final int ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_FLUID_DRAINER_ENERGY_CONSUMPTION_PER_TICK.getValue();

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
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
                    if(!fluidStack.isEmpty() && ((FluidDrainerBlockEntity.this.fluidStorage.isEmpty() &&
                            FluidDrainerBlockEntity.this.fluidStorage.isFluidValid(fluidStack)) ||
                            fluidStack.isFluidEqual(FluidDrainerBlockEntity.this.fluidStorage.getFluid())))
                        return false;
                }

                return true;
            }));

    private final UpgradeModuleInventory upgradeModuleInventory = new UpgradeModuleInventory(
            UpgradeModuleModifier.ENERGY_CONSUMPTION,
            UpgradeModuleModifier.ENERGY_CAPACITY
    );
    private final ContainerListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    private final ReceiveOnlyEnergyStorage energyStorage;

    private final LazyOptional<IEnergyStorage> lazyEnergyStorage;

    private final FluidTank fluidStorage;
    private final LazyOptional<IFluidHandler> lazyFluidStorage;

    protected final ContainerData data;
    private int fluidDrainingLeft = -1;
    private int fluidDrainingSumPending = 0;

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

    public FluidDrainerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.FLUID_DRAINER_ENTITY.get(), blockPos, blockState);

        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        energyStorage = new ReceiveOnlyEnergyStorage(0, ModConfigs.COMMON_FLUID_DRAINER_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_DRAINER_TRANSFER_RATE.getValue()) {
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

                if(level != null && !level.isClientSide())
                    ModMessages.sendToPlayersWithinXBlocks(
                            new EnergySyncS2CPacket(getEnergy(), getCapacity(), getBlockPos()),
                            getBlockPos(), level.dimension(), 32
                    );
            }
        };
        fluidStorage = new FluidTank(ModConfigs.COMMON_FLUID_DRAINER_FLUID_TANK_CAPACITY.getValue() * 1000) {
            @Override
            protected void onContentsChanged() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToPlayersWithinXBlocks(
                            new FluidSyncS2CPacket(0, fluid, capacity, getBlockPos()),
                            getBlockPos(), level.dimension(), 32
                    );
            }
        };
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(FluidDrainerBlockEntity.this.fluidDrainingLeft, index);
                    case 2, 3 -> ByteUtils.get2Bytes(FluidDrainerBlockEntity.this.fluidDrainingSumPending, index - 2);
                    case 4 -> redstoneMode.ordinal();
                    case 5 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2, 3 -> {}
                    case 4 -> FluidDrainerBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 5 -> FluidDrainerBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
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
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.fluid_drainer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(), getBlockPos()), (ServerPlayer)player);
        ModMessages.sendToPlayer(new FluidSyncS2CPacket(0, fluidStorage.getFluid(), fluidStorage.getCapacity(), worldPosition), (ServerPlayer)player);

        return new FluidDrainerMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public int getRedstoneOutput() {
        return switch(comparatorMode) {
            case ITEM -> InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
            case FLUID -> FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
        };
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
    protected void saveAdditional(CompoundTag nbt) {
        //Save Upgrade Module Inventory first
        nbt.put("upgrade_module_inventory", upgradeModuleInventory.saveToNBT());

        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.put("energy", energyStorage.saveNBT());
        nbt.put("fluid", fluidStorage.writeToNBT(new CompoundTag()));

        nbt.put("recipe.fluid_draining_left", IntTag.valueOf(fluidDrainingLeft));
        nbt.put("recipe.fluid_draining_sum_pending", IntTag.valueOf(fluidDrainingSumPending));

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        //Load Upgrade Module Inventory first
        upgradeModuleInventory.removeListener(updateUpgradeModuleListener);
        upgradeModuleInventory.loadFromNBT(nbt.getCompound("upgrade_module_inventory"));
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyStorage.loadNBT(nbt.get("energy"));
        fluidStorage.readFromNBT(nbt.getCompound("fluid"));

        fluidDrainingLeft = nbt.getInt("recipe.fluid_draining_left");
        fluidDrainingSumPending = nbt.getInt("recipe.fluid_draining_sum_pending");

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode"));
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);

        Containers.dropContents(level, worldPosition, upgradeModuleInventory);
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

            LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = stack.getCapability(Capabilities.FLUID_HANDLER_ITEM);
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
        if(stack.getCapability(Capabilities.FLUID_HANDLER_ITEM).isPresent()) {
            LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = stack.getCapability(Capabilities.FLUID_HANDLER_ITEM);
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

    private void updateUpgradeModules() {
        resetProgress();
        setChanged();
        if(level != null && !level.isClientSide())
            ModMessages.sendToPlayersWithinXBlocks(
                    new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(), getBlockPos()),
                    getBlockPos(), level.dimension(), 32
            );
    }

    public FluidStack getFluid(int tank) {
        return fluidStorage.getFluid();
    }

    public int getTankCapacity(int tank) {
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
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, int capacity) {
        fluidStorage.setCapacity(capacity);
    }

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        setChanged();
    }

    @Override
    public void setNextComparatorMode() {
        comparatorMode = ComparatorMode.fromIndex(comparatorMode.ordinal() + 1);
        setChanged();
    }
}