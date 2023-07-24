package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.fluid.SimpleFluidStorage;
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
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
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

public class FluidDrainerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate,
        FluidStoragePacketUpdate {
    public static final long CAPACITY = 2048;
    public static final long MAX_RECEIVE = 128;

    /**
     * MAX_FLUID_DRAINING_PER_TICK is in Milli Buckets
     */
    public static final int MAX_FLUID_DRAINING_PER_TICK = 100;
    public static final int ENERGY_USAGE_PER_TICK = 64;

    final CachedSidedInventoryStorage<UnchargerBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    final LimitingEnergyStorage energyStorage;
    private final SimpleEnergyStorage internalEnergyStorage;

    final SimpleFluidStorage fluidStorage;

    protected final PropertyDelegate data;

    private long fluidDrainingLeft = -1;
    private long fluidDrainingSumPending = -1;

    public FluidDrainerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.FLUID_DRAINER_ENTITY, blockPos, blockState);

        internalInventory = new SimpleInventory(1) {
            @Override
            public int getMaxCountPerStack() {
                return 1;
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(stack.getCount() != 1)
                    return false;

                if(slot == 0)
                    return ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM) != null;

                return super.isValid(slot, stack);
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.areItemsEqual(stack, itemStack) ||
                            (!ItemStack.canCombine(stack, itemStack) &&
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

        internalEnergyStorage = new SimpleEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(amount);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.broadcastServerPacket(world.getServer(), ModMessages.ENERGY_SYNC_ID, buffer);
                }
            }
        };
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0);

        fluidStorage = new SimpleFluidStorage(FluidUtils.convertMilliBucketsToDroplets(8000)) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    getFluid().toPacket(buffer);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.broadcastServerPacket(world.getServer(), ModMessages.FLUID_SYNC_ID, buffer);
                }
            }
        };

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1, 2, 3 -> ByteUtils.get2Bytes(FluidDrainerBlockEntity.this.fluidDrainingLeft, index);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(FluidDrainerBlockEntity.this.fluidDrainingSumPending, index - 4);
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
        return Text.translatable("container.energizedpower.fluid_drainer");
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

        return new FluidDrainerMenu(id, this, inventory, internalInventory, this.data);
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

        FluidUtils.writeFluidAmountInMilliBucketsWithLeftover(fluidDrainingLeft,
                "recipe.fluid_draining_left", "recipe.fluid_draining_left_leftover_droplets", nbt);
        FluidUtils.writeFluidAmountInMilliBucketsWithLeftover(fluidDrainingSumPending,
                "recipe.fluid_draining_sum_pending", "recipe.fluid_draining_sum_pending_leftover_droplets", nbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.stacks);
        internalEnergyStorage.amount = nbt.getLong("energy");
        fluidStorage.fromNBT(nbt.getCompound("fluid"));

        fluidDrainingLeft = FluidUtils.readFluidAmountInMilliBucketsWithLeftover("recipe.fluid_draining_left",
                "recipe.fluid_draining_left_leftover_droplets", nbt);
        fluidDrainingSumPending = FluidUtils.readFluidAmountInMilliBucketsWithLeftover("recipe.fluid_draining_sum_pending",
                "recipe.fluid_draining_sum_pending_leftover_droplets", nbt);
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.stacks);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, FluidDrainerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack itemStack = blockEntity.internalInventory.getStack(0);
            long fluidDrainingSum = 0;
            long fluidDrainingLeftSum = 0;

            if(blockEntity.fluidStorage.getCapacity() - blockEntity.fluidStorage.getAmount() - blockEntity.fluidDrainingSumPending <= 0)
                return;

            if(blockEntity.internalEnergyStorage.amount < ENERGY_USAGE_PER_TICK)
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

            try(Transaction transaction = Transaction.openOuter()) {
                blockEntity.internalEnergyStorage.extract(ENERGY_USAGE_PER_TICK, transaction);

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
            }

            markDirty(level, blockPos, state);

            if(blockEntity.fluidDrainingLeft <= 0)
                blockEntity.resetProgress();
        }else {
            blockEntity.resetProgress();
            markDirty(level, blockPos, state);
        }
    }

    private void resetProgress() {
        fluidDrainingLeft = -1;
        fluidDrainingSumPending = -1;
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