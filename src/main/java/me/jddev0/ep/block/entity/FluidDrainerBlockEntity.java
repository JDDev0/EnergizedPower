package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.FluidDrainerBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.FluidDrainerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.FluidUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.stream.IntStream;

public class FluidDrainerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate,
        FluidStoragePacketUpdate, RedstoneModeUpdate, ComparatorModeUpdate {
    public static final long CAPACITY = ModConfigs.COMMON_FLUID_DRAINER_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_FLUID_DRAINER_TRANSFER_RATE.getValue();

    /**
     * MAX_FLUID_DRAINING_PER_TICK is in Milli Buckets
     */
    public static final long MAX_FLUID_DRAINING_PER_TICK = ModConfigs.COMMON_FLUID_DRAINER_FLUID_ITEM_TRANSFER_RATE.getValue();
    public static final long ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_FLUID_DRAINER_ENERGY_CONSUMPTION_PER_TICK.getValue();

    final CachedSidedInventoryStorage<UnchargerBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    private final UpgradeModuleInventory upgradeModuleInventory = new UpgradeModuleInventory(
            UpgradeModuleModifier.ENERGY_CONSUMPTION,
            UpgradeModuleModifier.ENERGY_CAPACITY
    );
    private final InventoryChangedListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    final EnergizedPowerLimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;

    final SimpleFluidStorage fluidStorage;

    protected final PropertyDelegate data;

    private long fluidDrainingLeft = -1;
    private long fluidDrainingSumPending = 0;

    private boolean forceAllowStackUpdateFlag = false;

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

    public FluidDrainerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.FLUID_DRAINER_ENTITY, blockPos, blockState);

        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        internalInventory = new SimpleInventory(1) {
            @Override
            public int getMaxCountPerStack() {
                return 1;
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot == 0)
                    return ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null;

                return super.isValid(slot, stack);
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(!forceAllowStackUpdateFlag && !stack.isEmpty() && !itemStack.isEmpty() &&
                            (!ItemStack.areItemsEqual(stack, itemStack) || (!ItemStack.canCombine(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no fluid item
                                    !(ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null &&
                                            ContainerItemContext.withConstant(itemStack).find(FluidStorage.ITEM) != null))))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                FluidDrainerBlockEntity.this.markDirty();
            }
        };
        inventory = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 1).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> true, i -> {
            if(i != 0)
                return false;

            ItemStack itemStack = internalInventory.getStack(i);

            if(ContainerItemContext.withConstant(itemStack).find(FluidStorage.ITEM) == null)
                return true;

            Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(itemStack, ContainerItemContext.
                    ofSingleSlot(InventoryStorage.of(internalInventory, null).getSlots().get(i)));
            if(fluidStorage == null)
                return true;

            if(!fluidStorage.supportsExtraction())
                return true;

            for(StorageView<FluidVariant> fluidView:fluidStorage) {
                FluidVariant fluidVariant = fluidView.getResource();
                if(!fluidVariant.isBlank() && (FluidDrainerBlockEntity.this.fluidStorage.isEmpty() ||
                        fluidVariant.equals(FluidDrainerBlockEntity.this.fluidStorage.getResource())))
                    return false;
            }

            return true;
        });
        cachedSidedInventoryStorage = new CachedSidedInventoryStorage<>(inventory);

        internalEnergyStorage = new EnergizedPowerEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
            @Override
            public long getCapacity() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(getAmount());
                    buffer.writeLong(getCapacity());
                    buffer.writeBlockPos(getPos());

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            ModMessages.ENERGY_SYNC_ID, buffer
                    );
                }
            }
        };
        energyStorage = new EnergizedPowerLimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0) {
            @Override
            public long getMaxInsert() {
                return Math.max(1, (long)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };

        fluidStorage = new SimpleFluidStorage(FluidUtils.convertMilliBucketsToDroplets(
                ModConfigs.COMMON_FLUID_DRAINER_FLUID_TANK_CAPACITY.getValue() * 1000)) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeInt(0);
                    getFluid().toPacket(buffer);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            ModMessages.FLUID_SYNC_ID, buffer
                    );
                }
            }
        };

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1, 2, 3 -> ByteUtils.get2Bytes(FluidDrainerBlockEntity.this.fluidDrainingLeft, index);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(FluidDrainerBlockEntity.this.fluidDrainingSumPending, index - 4);
                    case 8 -> redstoneMode.ordinal();
                    case 9 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2, 3, 4, 5, 6, 7 -> {}
                    case 8 -> FluidDrainerBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 9 -> FluidDrainerBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 10;
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.fluid_drainer");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeLong(internalEnergyStorage.getAmount());
        buffer.writeLong(internalEnergyStorage.getCapacity());
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.ENERGY_SYNC_ID, buffer);

        buffer = PacketByteBufs.create();
        buffer.writeInt(0);
        fluidStorage.getFluid().toPacket(buffer);
        buffer.writeLong(fluidStorage.getCapacity());
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.FLUID_SYNC_ID, buffer);

        return new FluidDrainerMenu(id, this, inventory, internalInventory, upgradeModuleInventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public int getRedstoneOutput() {
        return switch(comparatorMode) {
            case ITEM -> ScreenHandler.calculateComparatorOutput(internalInventory);
            case FLUID -> FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
        };
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        //Save Upgrade Module Inventory first
        nbt.put("upgrade_module_inventory", upgradeModuleInventory.saveToNBT());

        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.stacks));
        nbt.putLong("energy", internalEnergyStorage.getAmount());
        nbt.put("fluid", fluidStorage.toNBT(new NbtCompound()));

        FluidUtils.writeFluidAmountInMilliBucketsWithLeftover(fluidDrainingLeft,
                "recipe.fluid_draining_left", "recipe.fluid_draining_left_leftover_droplets", nbt);
        FluidUtils.writeFluidAmountInMilliBucketsWithLeftover(fluidDrainingSumPending,
                "recipe.fluid_draining_sum_pending", "recipe.fluid_draining_sum_pending_leftover_droplets", nbt);

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        //Load Upgrade Module Inventory first
        upgradeModuleInventory.removeListener(updateUpgradeModuleListener);
        upgradeModuleInventory.loadFromNBT(nbt.getCompound("upgrade_module_inventory"));
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.stacks);
        internalEnergyStorage.setAmountWithoutUpdate(nbt.getLong("energy"));
        fluidStorage.fromNBT(nbt.getCompound("fluid"));

        fluidDrainingLeft = FluidUtils.readFluidAmountInMilliBucketsWithLeftover("recipe.fluid_draining_left",
                "recipe.fluid_draining_left_leftover_droplets", nbt);
        fluidDrainingSumPending = FluidUtils.readFluidAmountInMilliBucketsWithLeftover("recipe.fluid_draining_sum_pending",
                "recipe.fluid_draining_sum_pending_leftover_droplets", nbt);

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode"));
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory);
        ItemScatterer.spawn(level, worldPosition, upgradeModuleInventory);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, FluidDrainerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.redstoneMode.isActive(state.get(FluidDrainerBlock.POWERED)))
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack itemStack = blockEntity.internalInventory.getStack(0);
            long fluidDrainingSum = 0;
            long fluidDrainingLeftSum = 0;

            if(blockEntity.fluidStorage.getCapacity() - blockEntity.fluidStorage.getAmount() - blockEntity.fluidDrainingSumPending <= 0)
                return;

            long energyConsumptionPerTick = Math.max(1, (long)Math.ceil(ENERGY_USAGE_PER_TICK *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.internalEnergyStorage.getAmount() < energyConsumptionPerTick)
                return;

            if(ContainerItemContext.withConstant(itemStack).find(FluidStorage.ITEM) == null)
                return;

            Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(itemStack, ContainerItemContext.
                    ofSingleSlot(InventoryStorage.of(blockEntity.internalInventory, null).getSlots().get(0)));
            if(fluidStorage == null)
                return;

            FluidVariant firstNonEmptyFluidVariant = FluidVariant.blank();
            for(StorageView<FluidVariant> fluidView:fluidStorage) {
                FluidVariant fluidVariant = fluidView.getResource();
                if(!fluidVariant.isBlank() && firstNonEmptyFluidVariant.isBlank())
                    firstNonEmptyFluidVariant = fluidVariant;
                if(!fluidVariant.isBlank() && ((blockEntity.fluidStorage.isEmpty() &&
                        fluidVariant.equals(firstNonEmptyFluidVariant) ||
                        fluidVariant.equals(blockEntity.fluidStorage.getResource())))) {
                    fluidDrainingSum += Math.min(blockEntity.fluidStorage.getCapacity() -
                                    blockEntity.fluidStorage.getAmount() - blockEntity.fluidDrainingSumPending - fluidDrainingSum,
                            Math.min(fluidView.getAmount(), FluidUtils.convertMilliBucketsToDroplets(MAX_FLUID_DRAINING_PER_TICK) -
                                    fluidDrainingSum));

                    fluidDrainingLeftSum += fluidView.getAmount();
                }
            }

            if(firstNonEmptyFluidVariant.isBlank() || fluidDrainingSum == 0)
                return;

            blockEntity.fluidDrainingLeft = fluidDrainingLeftSum;
            blockEntity.fluidDrainingSumPending += fluidDrainingSum;

            blockEntity.forceAllowStackUpdateFlag = true;
            try(Transaction transaction = Transaction.openOuter()) {
                blockEntity.internalEnergyStorage.extract(energyConsumptionPerTick, transaction);

                long fluidSumDrainable = Math.min(blockEntity.fluidStorage.getCapacity() - blockEntity.fluidStorage.getAmount(),
                        blockEntity.fluidDrainingSumPending);

                FluidVariant fluidVariantToDrain = blockEntity.fluidStorage.isEmpty()?firstNonEmptyFluidVariant:
                        blockEntity.fluidStorage.getResource();

                long fluidSumDrained = fluidStorage.extract(fluidVariantToDrain, fluidSumDrainable, transaction);

                if(fluidSumDrained > 0) {
                    blockEntity.fluidStorage.insert(fluidVariantToDrain, fluidSumDrained, transaction);
                    blockEntity.fluidDrainingSumPending -= fluidSumDrained;
                    blockEntity.fluidDrainingLeft = fluidDrainingLeftSum - fluidSumDrained;
                }

                transaction.commit();
            }finally {
                blockEntity.forceAllowStackUpdateFlag = false;
            }

            if(blockEntity.fluidDrainingLeft <= 0)
                blockEntity.resetProgress();

            markDirty(level, blockPos, state);
        }else {
            blockEntity.resetProgress();
            markDirty(level, blockPos, state);
        }
    }

    private void resetProgress() {
        fluidDrainingLeft = -1;
        fluidDrainingSumPending = 0;
    }

    private boolean hasRecipe() {
        ItemStack itemStack = internalInventory.getStack(0);
        if(ContainerItemContext.withConstant(itemStack).find(FluidStorage.ITEM) != null) {
            Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(itemStack, ContainerItemContext.
                    ofSingleSlot(InventoryStorage.of(internalInventory, null).getSlots().get(0)));
            if(fluidStorage == null)
                return false;
            for(StorageView<FluidVariant> fluidView:fluidStorage) {
                FluidVariant fluidVariant = fluidView.getResource();
                if(!fluidVariant.isBlank() && (FluidDrainerBlockEntity.this.fluidStorage.isEmpty() ||
                        fluidVariant.equals(FluidDrainerBlockEntity.this.fluidStorage.getResource())))
                    return true;
            }
        }

        return false;
    }

    private void updateUpgradeModules() {
        resetProgress();
        markDirty();
        if(world != null && !world.isClient()) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeLong(internalEnergyStorage.getAmount());
            buffer.writeLong(internalEnergyStorage.getCapacity());
            buffer.writeBlockPos(getPos());

            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getPos(), (ServerWorld)world, 32,
                    ModMessages.ENERGY_SYNC_ID, buffer
            );
        }
    }

    public FluidStack getFluid(int tank) {
        return fluidStorage.getFluid();
    }

    public long getTankCapacity(int tank) {
        return fluidStorage.getCapacity();
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.setAmountWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(long capacity) {
        internalEnergyStorage.setCapacityWithoutUpdate(capacity);
    }

    public long getEnergy() {
        return internalEnergyStorage.getAmount();
    }

    public long getCapacity() {
        return internalEnergyStorage.getCapacity();
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, long capacity) {
        //Does nothing (capacity is final)
    }

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        markDirty();
    }

    @Override
    public void setNextComparatorMode() {
        comparatorMode = ComparatorMode.fromIndex(comparatorMode.ordinal() + 1);
        markDirty();
    }
}