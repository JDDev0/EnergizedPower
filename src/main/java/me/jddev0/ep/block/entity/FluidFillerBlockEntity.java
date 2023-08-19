package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.FluidFillerMenu;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.LimitingEnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.stream.IntStream;

public class FluidFillerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate,
        FluidStoragePacketUpdate {
    public static final long CAPACITY = ModConfigs.COMMON_FLUID_FILLER_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_FLUID_FILLER_TRANSFER_RATE.getValue();

    /**
     * MAX_FLUID_DRAINING_PER_TICK is in Milli Buckets
     */
    public static final long MAX_FLUID_FILLING_PER_TICK = ModConfigs.COMMON_FLUID_FILLER_FLUID_ITEM_TRANSFER_RATE.getValue();
    public static final long ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_FLUID_FILLER_ENERGY_CONSUMPTION_PER_TICK.getValue();

    final CachedSidedInventoryStorage<UnchargerBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    final LimitingEnergyStorage energyStorage;
    private final SimpleEnergyStorage internalEnergyStorage;

    final SimpleFluidStorage fluidStorage;

    protected final PropertyDelegate data;
    private long fluidFillingLeft = -1;
    private long fluidFillingSumPending = 0;

    private boolean forceAllowStackUpdateFlag = false;

    public FluidFillerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.FLUID_FILLER_ENTITY, blockPos, blockState);

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

                FluidFillerBlockEntity.this.markDirty();
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

            if(!fluidStorage.supportsInsertion())
                return true;

            boolean isFluidValid = false;
            if(!FluidFillerBlockEntity.this.fluidStorage.isEmpty()) {
                FluidFillerBlockEntity.this.forceAllowStackUpdateFlag = true;
                //Get current transaction for simulation only [Transaction is necessary to find out if a fluid variant is valid]
                try(Transaction transaction = Transaction.openNested(Transaction.getCurrentUnsafe())) {
                    long inserted = fluidStorage.insert(FluidFillerBlockEntity.this.fluidStorage.getResource(),
                            Long.MAX_VALUE, transaction);
                    isFluidValid = inserted > 0;

                    transaction.abort();
                }finally {
                    FluidFillerBlockEntity.this.forceAllowStackUpdateFlag = false;
                }
            }

            for(StorageView<FluidVariant> fluidView:fluidStorage) {
                FluidVariant fluidVariant = fluidView.getResource();

                if(fluidView.getCapacity() > fluidView.getAmount() && (FluidFillerBlockEntity.this.fluidStorage.isEmpty() ||
                        (fluidVariant.isBlank() && isFluidValid) ||
                        fluidVariant.equals(FluidFillerBlockEntity.this.fluidStorage.getResource())))
                    return false;
            }

            return true;
        });
        cachedSidedInventoryStorage = new CachedSidedInventoryStorage<>(inventory);

        internalEnergyStorage = new SimpleEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(amount);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            ModMessages.ENERGY_SYNC_ID, buffer
                    );
                }
            }
        };
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0);

        fluidStorage = new SimpleFluidStorage(FluidUtils.convertMilliBucketsToDroplets(
                ModConfigs.COMMON_FLUID_FILLER_FLUID_TANK_CAPACITY.getValue() * 1000)) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
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
                    case 0, 1, 2, 3 -> ByteUtils.get2Bytes(FluidFillerBlockEntity.this.fluidFillingLeft, index);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(FluidFillerBlockEntity.this.fluidFillingSumPending, index - 4);
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2, 3, 4, 5, 6, 7 -> {}
                }
            }

            @Override
            public int size() {
                return 8;
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.fluid_filler");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeLong(internalEnergyStorage.amount);
        buffer.writeLong(internalEnergyStorage.capacity);
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.ENERGY_SYNC_ID, buffer);

        buffer = PacketByteBufs.create();
        fluidStorage.getFluid().toPacket(buffer);
        buffer.writeLong(fluidStorage.getCapacity());
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.FLUID_SYNC_ID, buffer);

        return new FluidFillerMenu(id, this, inventory, internalInventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(internalInventory);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.stacks));
        nbt.putLong("energy", internalEnergyStorage.amount);
        nbt.put("fluid", fluidStorage.toNBT(new NbtCompound()));

        FluidUtils.writeFluidAmountInMilliBucketsWithLeftover(fluidFillingLeft,
                "recipe.fluid_filling_left", "recipe.fluid_filling_left_leftover_droplets", nbt);
        FluidUtils.writeFluidAmountInMilliBucketsWithLeftover(fluidFillingSumPending,
                "recipe.fluid_filling_sum_pending", "recipe.fluid_filling_sum_pending_leftover_droplets", nbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.stacks);
        internalEnergyStorage.amount = nbt.getLong("energy");
        fluidStorage.fromNBT(nbt.getCompound("fluid"));

        fluidFillingLeft = FluidUtils.readFluidAmountInMilliBucketsWithLeftover("recipe.fluid_filling_left",
                "recipe.fluid_filling_left_leftover_droplets", nbt);
        fluidFillingSumPending = FluidUtils.readFluidAmountInMilliBucketsWithLeftover("recipe.fluid_filling_sum_pending",
                "recipe.fluid_filling_sum_pending_leftover_droplets", nbt);
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.stacks);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, FluidFillerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack itemStack = blockEntity.internalInventory.getStack(0);
            long fluidFillingSum = 0;
            long fluidFillingLeftSum = 0;

            if(blockEntity.fluidStorage.getAmount() - blockEntity.fluidFillingSumPending <= 0)
                return;

            if(blockEntity.internalEnergyStorage.amount < ENERGY_USAGE_PER_TICK)
                return;

            if(ContainerItemContext.withConstant(itemStack).find(FluidStorage.ITEM) == null)
                return;

            Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(itemStack, ContainerItemContext.
                    ofSingleSlot(InventoryStorage.of(blockEntity.internalInventory, null).getSlots().get(0)));
            if(fluidStorage == null)
                return;

            boolean isFluidValid = false;
            if(!blockEntity.fluidStorage.isEmpty()) {
                blockEntity.forceAllowStackUpdateFlag = true;
                try(Transaction transaction = Transaction.openOuter()) {
                    long inserted = fluidStorage.insert(blockEntity.fluidStorage.getResource(),
                            Long.MAX_VALUE, transaction);
                    isFluidValid = inserted > 0;

                    transaction.abort();
                }finally {
                    blockEntity.forceAllowStackUpdateFlag = false;
                }
            }

            for(StorageView<FluidVariant> fluidView:fluidStorage) {
                FluidVariant fluidVariant = fluidView.getResource();
                if(fluidView.getCapacity() > fluidView.getAmount() && ((blockEntity.fluidStorage.isEmpty() ||
                        (fluidVariant.isBlank() && isFluidValid)) ||
                        fluidVariant.equals(blockEntity.fluidStorage.getResource()))) {
                    fluidFillingSum += Math.min(blockEntity.fluidStorage.getAmount() -
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

            blockEntity.forceAllowStackUpdateFlag = true;
            try(Transaction transaction = Transaction.openOuter()) {
                blockEntity.internalEnergyStorage.extract(ENERGY_USAGE_PER_TICK, transaction);

                long fluidSumFillable = Math.min(blockEntity.fluidStorage.getAmount(),
                        blockEntity.fluidFillingSumPending);

                FluidVariant fluidVariantToFill = blockEntity.fluidStorage.getResource();

                long fluidSumFilled = fluidStorage.insert(fluidVariantToFill, fluidSumFillable, transaction);

                if(fluidSumFilled > 0) {
                    blockEntity.fluidStorage.extract(fluidVariantToFill, fluidSumFilled, transaction);
                    blockEntity.fluidFillingSumPending -= fluidSumFilled;
                    blockEntity.fluidFillingLeft = fluidFillingLeftSum - fluidSumFilled;
                }

                transaction.commit();
            }finally {
                blockEntity.forceAllowStackUpdateFlag = false;
            }

            markDirty(level, blockPos, state);

            if(blockEntity.fluidFillingLeft <= 0)
                blockEntity.resetProgress();
        }else {
            blockEntity.resetProgress();
            markDirty(level, blockPos, state);
        }
    }

    private void resetProgress() {
        fluidFillingLeft = -1;
        fluidFillingSumPending = 0;
    }

    private boolean hasRecipe() {
        ItemStack itemStack = internalInventory.getStack(0);
        if(ContainerItemContext.withConstant(itemStack).find(FluidStorage.ITEM) != null) {
            Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(itemStack, ContainerItemContext.
                    ofSingleSlot(InventoryStorage.of(internalInventory, null).getSlots().get(0)));
            if(fluidStorage == null)
                return false;

            boolean isFluidValid = false;
            if(!FluidFillerBlockEntity.this.fluidStorage.isEmpty()) {
                forceAllowStackUpdateFlag = true;
                try(Transaction transaction = Transaction.openOuter()) {
                    long inserted = fluidStorage.insert(FluidFillerBlockEntity.this.fluidStorage.getResource(),
                            Long.MAX_VALUE, transaction);
                    isFluidValid = inserted > 0;

                    transaction.abort();
                }finally {
                    forceAllowStackUpdateFlag = false;
                }
            }

            for(StorageView<FluidVariant> fluidView:fluidStorage) {
                FluidVariant fluidVariant = fluidView.getResource();
                if(fluidView.getCapacity() > fluidView.getAmount() && (FluidFillerBlockEntity.this.fluidStorage.isEmpty() ||
                        (fluidVariant.isBlank() && isFluidValid) ||
                        fluidVariant.equals(FluidFillerBlockEntity.this.fluidStorage.getResource())))
                    return true;
            }
        }

        return false;
    }

    public FluidStack getFluid() {
        return fluidStorage.getFluid();
    }

    public long getTankCapacity() {
        return fluidStorage.getCapacity();
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.amount = energy;
    }

    @Override
    public void setCapacity(long capacity) {
        //Does nothing (capacity is final)
    }

    public long getEnergy() {
        return internalEnergyStorage.amount;
    }

    public long getCapacity() {
        return internalEnergyStorage.capacity;
    }

    @Override
    public void setFluid(FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(long capacity) {
        //Does nothing (capacity is final)
    }
}