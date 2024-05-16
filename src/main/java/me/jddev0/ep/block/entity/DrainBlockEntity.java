package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageBlockEntity;
import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.screen.DrainMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.CauldronFluidContent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class DrainBlockEntity extends FluidStorageBlockEntity<SimpleFluidStorage> implements ExtendedScreenHandlerFactory<BlockPos> {
    protected final PropertyDelegate data;
    private int progress;
    private int maxProgress = ModConfigs.COMMON_DRAIN_DRAIN_DURATION.getValue();

    public DrainBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.DRAIN_ENTITY, blockPos, blockState,

                FluidStorageSingleTankMethods.INSTANCE,
                FluidUtils.convertMilliBucketsToDroplets(ModConfigs.COMMON_DRAIN_FLUID_TANK_CAPACITY.getValue() * 1000)
        );

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
    protected SimpleFluidStorage initFluidStorage() {
        return new SimpleFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                markDirty();
                syncFluidToPlayers(32);
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
        syncFluidToPlayer(player);

        return new DrainMenu(id, this, inventory, this.data);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    public int getRedstoneOutput() {
        return FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, RegistryWrapper.@NotNull WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        nbt.put("drain.progress", NbtInt.of(progress));
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.@NotNull WrapperLookup registries) {
        super.readNbt(nbt, registries);

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
                    ItemStack bucketItemStack = aboveBlock.tryDrainFluid(null, level, aboveBlockPos, aboveBlockState);
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
                }else {
                    CauldronFluidContent cauldronFluidContent = CauldronFluidContent.getForBlock(aboveBlockState.getBlock());
                    if(cauldronFluidContent != null) {
                        Fluid cauldronFluid = cauldronFluidContent.fluid;
                        if(cauldronFluid != Fluids.EMPTY && (blockEntity.fluidStorage.isEmpty() ||
                                blockEntity.fluidStorage.getFluid().getFluid() == cauldronFluid)) {
                            long cauldronAmountPerLevel = cauldronFluidContent.amountPerLevel;
                            IntProperty cauldronLevelProp = cauldronFluidContent.levelProperty;

                            long amountInsertable = blockEntity.fluidStorage.capacity - blockEntity.fluidStorage.amount;

                            long cauldronAmount;
                            if(cauldronLevelProp != null && aboveBlockState.contains(cauldronLevelProp))
                                cauldronAmount = cauldronAmountPerLevel * aboveBlockState.get(cauldronLevelProp);
                            else
                                cauldronAmount = cauldronAmountPerLevel;

                            if(cauldronAmount > 0 && cauldronAmount <= amountInsertable) {
                                long inserted;
                                try(Transaction transaction = Transaction.openOuter()) {
                                    inserted = blockEntity.fluidStorage.insert(FluidVariant.of(cauldronFluid), cauldronAmount, transaction);
                                    if(inserted == cauldronAmount) {
                                        transaction.commit();
                                    }
                                }

                                if(inserted == cauldronAmount) {
                                    level.setBlockState(aboveBlockPos, Blocks.CAULDRON.getDefaultState(), 3);
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

        if((aboveBlockState.getBlock() instanceof FluidDrainable)) {
            FluidState fluidState = level.getFluidState(aboveBlockPos);
            if(!fluidState.isEmpty()) {
                try(Transaction transaction = Transaction.openOuter()) {
                    return blockEntity.fluidStorage.insert(FluidVariant.of(fluidState.getFluid()), FluidConstants.BLOCK,
                            transaction) == FluidConstants.BLOCK;
                }
            }
        }

        CauldronFluidContent cauldronFluidContent = CauldronFluidContent.getForBlock(aboveBlockState.getBlock());
        if(cauldronFluidContent == null)
            return false;

        Fluid cauldronFluid = cauldronFluidContent.fluid;
        if(cauldronFluid == Fluids.EMPTY ||
                (!blockEntity.fluidStorage.isEmpty() && blockEntity.fluidStorage.getFluid().getFluid() != cauldronFluid))
            return false;

        long cauldronAmountPerLevel = cauldronFluidContent.amountPerLevel;
        IntProperty cauldronLevelProp = cauldronFluidContent.levelProperty;

        long amountInsertable = blockEntity.fluidStorage.capacity - blockEntity.fluidStorage.amount;

        long cauldronAmount;
        if(cauldronLevelProp != null && aboveBlockState.contains(cauldronLevelProp))
            cauldronAmount = cauldronAmountPerLevel * aboveBlockState.get(cauldronLevelProp);
        else
            cauldronAmount = cauldronAmountPerLevel;

        return cauldronAmount > 0 && cauldronAmount <= amountInsertable;
    }
}