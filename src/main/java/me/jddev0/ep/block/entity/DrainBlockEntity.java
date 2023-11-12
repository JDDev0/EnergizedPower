package me.jddev0.ep.block.entity;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.DrainMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class DrainBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, FluidStoragePacketUpdate {
    final SimpleFluidStorage fluidStorage;

    protected final PropertyDelegate data;
    private int progress;
    private int maxProgress = ModConfigs.COMMON_DRAIN_DRAIN_DURATION.getValue();

    public DrainBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.DRAIN_ENTITY, blockPos, blockState);

        fluidStorage = new SimpleFluidStorage(FluidUtils.convertMilliBucketsToDroplets(
                ModConfigs.COMMON_DRAIN_FLUID_TANK_CAPACITY.getValue() * 1000)) {
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
                    case 0, 1 -> ByteUtils.get2Bytes(DrainBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(DrainBlockEntity.this.maxProgress, index - 2);
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> DrainBlockEntity.this.progress = ByteUtils.with2Bytes(
                            DrainBlockEntity.this.progress, (short)value, index
                    );
                    case 2, 3 -> DrainBlockEntity.this.maxProgress = ByteUtils.with2Bytes(
                            DrainBlockEntity.this.maxProgress, (short)value, index - 2
                    );
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.drain");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(0);
        fluidStorage.getFluid().toPacket(buffer);
        buffer.writeLong(fluidStorage.getCapacity());
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.FLUID_SYNC_ID, buffer);

        return new DrainMenu(id, this, inventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public int getRedstoneOutput() {
        float fullnessPercent = 0;
        boolean isEmptyFlag = true;

        FluidStack fluid = fluidStorage.getFluid();
        if(!fluidStorage.isEmpty()) {
            fullnessPercent = (float)fluid.getDropletsAmount() / fluidStorage.getCapacity();
            isEmptyFlag = false;
        }

        return Math.min(MathHelper.floor(fullnessPercent * 14.f) + (isEmptyFlag?0:1), 15);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("fluid", fluidStorage.toNBT(new NbtCompound()));

        nbt.put("drain.progress", NbtInt.of(progress));

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        fluidStorage.fromNBT(nbt.getCompound("fluid"));

        progress = nbt.getInt("drain.progress");
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, DrainBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(hasRecipe(blockEntity)) {
            if(blockEntity.progress < 0 || blockEntity.maxProgress < 0) {
                //Reset progress for invalid values

                blockEntity.resetProgress(blockPos, state);
                markDirty(level, blockPos, state);

                return;
            }

            if(blockEntity.progress < blockEntity.maxProgress)
                blockEntity.progress++;

            if(blockEntity.progress >= blockEntity.maxProgress) {
                BlockPos aboveBlockPos = blockPos.up();
                BlockState aboveBlockState = level.getBlockState(aboveBlockPos);
                if(aboveBlockState.getBlock() instanceof FluidDrainable aboveBlock) {
                    ItemStack bucketItemStack = aboveBlock.tryDrainFluid(level, aboveBlockPos, aboveBlockState);
                    if(!bucketItemStack.isEmpty()) {
                        level.emitGameEvent(null, GameEvent.FLUID_PICKUP, aboveBlockPos);

                        Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(bucketItemStack, ContainerItemContext.withConstant(bucketItemStack));
                        if(fluidStorage != null) {
                            Iterator<StorageView<FluidVariant>> iterator = fluidStorage.iterator();
                            if(iterator.hasNext()) {
                                StorageView<FluidVariant> fluidView = iterator.next();
                                if(!iterator.hasNext()) {
                                    FluidVariant fluidVariant = fluidView.getResource();
                                    if(!fluidVariant.isBlank()) {
                                        try(Transaction transaction = Transaction.openOuter()) {
                                            blockEntity.fluidStorage.insert(fluidVariant, fluidView.getAmount(), transaction);
                                            transaction.commit();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                blockEntity.resetProgress(blockPos, state);
            }

            markDirty(level, blockPos, state);
        }else {
            blockEntity.resetProgress(blockPos, state);
            markDirty(level, blockPos, state);
        }
    }

    private void resetProgress(BlockPos blockPos, BlockState state) {
        progress = 0;
    }

    private static boolean hasRecipe(DrainBlockEntity blockEntity) {
        World level = blockEntity.getWorld();
        BlockPos blockPos = blockEntity.getPos();

        BlockPos aboveBlockPos = blockPos.up();
        BlockState aboveBlockState = level.getBlockState(aboveBlockPos);

        if(!(aboveBlockState.getBlock() instanceof FluidDrainable))
            return false;

        FluidState fluidState = level.getFluidState(aboveBlockPos);
        if(fluidState.isEmpty())
            return false;

        try(Transaction transaction = Transaction.openOuter()) {
            return blockEntity.fluidStorage.insert(FluidVariant.of(fluidState.getFluid()), FluidConstants.BLOCK,
                    transaction) == FluidConstants.BLOCK;
        }
    }

    public FluidStack getFluid(int tank) {
        return fluidStorage.getFluid();
    }

    public long getTankCapacity(int tank) {
        return fluidStorage.getCapacity();
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, long capacity) {
        //Does nothing (capacity is final)
    }
}