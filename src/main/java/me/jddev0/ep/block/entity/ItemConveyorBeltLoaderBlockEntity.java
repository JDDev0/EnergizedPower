package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltLoaderBlock;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.base.MenuInventoryStorageBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.ItemConveyorBeltLoaderMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemConveyorBeltLoaderBlockEntity
        extends MenuInventoryStorageBlockEntity<ItemStackHandler> {
    private static final int TICKS_PER_ITEM = ModConfigs.COMMON_ITEM_CONVEYOR_BELT_LOADER_TICKS_PER_ITEM.getValue();

    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 0));

    public ItemConveyorBeltLoaderBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ITEM_CONVEYOR_BELT_LOADER_ENTITY.get(), blockPos, blockState,

                "item_conveyor_belt_loader",

                1
        );
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ItemConveyorBeltLoaderMenu(id, inventory, this);
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
        }

        return super.getCapability(cap, side);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltLoaderBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(level.getGameTime() % TICKS_PER_ITEM == 0 && state.getValue(ItemConveyorBeltLoaderBlock.ENABLED)) {
            if(!blockEntity.itemHandler.getStackInSlot(0).isEmpty())
                insertItemStackIntoItemConveyorBelt(level, blockPos, state, blockEntity, blockEntity.itemHandler.getStackInSlot(0).copy());

            if(blockEntity.itemHandler.getStackInSlot(0).isEmpty())
                extractItemStackFromBlockEntity(level, blockPos, state, blockEntity);
        }
    }

    private static void insertItemStackIntoItemConveyorBelt(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltLoaderBlockEntity blockEntity,
                                                       ItemStack itemStackToInsert) {
        Direction direction = blockEntity.getBlockState().getValue(ItemConveyorBeltLoaderBlock.FACING);

        BlockPos testPos = blockPos.relative(direction);
        BlockState testBlockState = level.getBlockState(testPos);
        if(!testBlockState.is(EPBlocks.ITEM_CONVEYOR_BELT.get()))
            return;

        BlockEntity testBlockEntity = level.getBlockEntity(testPos);
        if(!(testBlockEntity instanceof ItemConveyorBeltBlockEntity))
            return;

        LazyOptional<IItemHandler> itemStackStorageLazyOptional = testBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite());
        if(!itemStackStorageLazyOptional.isPresent())
            return;

        IItemHandler itemStackStorage = itemStackStorageLazyOptional.orElseGet(null);
        for(int i = 0;i < itemStackStorage.getSlots();i++) {
            if(itemStackStorage.insertItem(i, itemStackToInsert, false).isEmpty()) {
                blockEntity.itemHandler.setStackInSlot(0, ItemStack.EMPTY);

                break;
            }
        }
    }

    private static void extractItemStackFromBlockEntity(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltLoaderBlockEntity blockEntity) {
        Direction direction = blockEntity.getBlockState().getValue(ItemConveyorBeltLoaderBlock.FACING).getOpposite();

        BlockPos testPos = blockPos.relative(direction);
        BlockEntity testBlockEntity = level.getBlockEntity(testPos);
        if(testBlockEntity == null)
            return;

        LazyOptional<IItemHandler> itemStackStorageLazyOptional = testBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite());
        if(!itemStackStorageLazyOptional.isPresent())
            return;

        IItemHandler itemStackStorage = itemStackStorageLazyOptional.orElseGet(null);
        for(int i = 0;i < itemStackStorage.getSlots();i++) {
            ItemStack extracted = itemStackStorage.extractItem(i, 1, false);
            if(!extracted.isEmpty()) {
                blockEntity.itemHandler.setStackInSlot(0, extracted);

                break;
            }
        }
    }
}