package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltLoaderBlock;
import me.jddev0.ep.block.entity.base.MenuInventoryStorageBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ItemConveyorBeltLoaderBlockEntity
        extends MenuInventoryStorageBlockEntity<ItemStackHandler> {
    private final int ticksPerItem;

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 0);

    private final ConveyorBeltTier tier;

    public ItemConveyorBeltLoaderBlockEntity(BlockPos blockPos, BlockState blockState, ConveyorBeltTier tier) {
        super(
                tier.getItemConveyorBeltLoaderBlockEntityFromTier(), blockPos, blockState,

                switch(tier) {
                    case BASIC -> "item_conveyor_belt_loader";
                    case FAST -> "fast_item_conveyor_belt_loader";
                    case EXPRESS -> "express_item_conveyor_belt_loader";
                },

                1
        );

        this.tier = tier;

        ticksPerItem = switch(tier) {
            case BASIC -> ModConfigs.COMMON_BASIC_ITEM_CONVEYOR_BELT_LOADER_TICKS_PER_ITEM.getValue();
            case FAST -> ModConfigs.COMMON_FAST_ITEM_CONVEYOR_BELT_LOADER_TICKS_PER_ITEM.getValue();
            case EXPRESS -> ModConfigs.COMMON_EXPRESS_ITEM_CONVEYOR_BELT_LOADER_TICKS_PER_ITEM.getValue();
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
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    public ConveyorBeltTier getTier() {
        return tier;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ItemConveyorBeltLoaderMenu(id, inventory, this);
    }

    public int getRedstoneOutput() {
        return InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltLoaderBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(level.getGameTime() % blockEntity.ticksPerItem == 0 && state.getValue(ItemConveyorBeltLoaderBlock.ENABLED)) {
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
        if(!(testBlockState.getBlock() instanceof ItemConveyorBeltBlock))
            return;

        BlockEntity testBlockEntity = level.getBlockEntity(testPos);
        if(!(testBlockEntity instanceof ItemConveyorBeltBlockEntity))
            return;

        IItemHandler itemStackStorage = level.getCapability(Capabilities.ItemHandler.BLOCK, testPos, testBlockState,
                testBlockEntity, direction.getOpposite());
        if(itemStackStorage == null)
            return;

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

        IItemHandler itemStackStorage = level.getCapability(Capabilities.ItemHandler.BLOCK, testPos, level.getBlockState(testPos),
                testBlockEntity, direction.getOpposite());
        if(itemStackStorage == null)
            return;

        for(int i = 0;i < itemStackStorage.getSlots();i++) {
            ItemStack extracted = itemStackStorage.extractItem(i, 1, false);
            if(!extracted.isEmpty()) {
                blockEntity.itemHandler.setStackInSlot(0, extracted);

                break;
            }
        }
    }
}