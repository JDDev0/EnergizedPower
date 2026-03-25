package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltLoaderBlock;
import me.jddev0.ep.block.entity.base.MenuInventoryStorageBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
import me.jddev0.ep.screen.ItemConveyorBeltLoaderMenu;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ItemConveyorBeltLoaderBlockEntity
        extends MenuInventoryStorageBlockEntity<SimpleContainer> {
    private final int ticksPerItem;

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 0);

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
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                if(slot == 0) {
                    return true;
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                ItemConveyorBeltLoaderBlockEntity.this.setChanged();
            }
        };
    }

    public ConveyorBeltTier getTier() {
        return tier;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ItemConveyorBeltLoaderMenu(id, this, inventory, itemHandler);
    }

    public int getRedstoneOutput() {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(itemHandler);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltLoaderBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(level.getGameTime() % blockEntity.ticksPerItem == 0 && state.getValue(ItemConveyorBeltLoaderBlock.ENABLED)) {
            if(!blockEntity.itemHandler.getItem(0).isEmpty())
                insertItemStackIntoItemConveyorBelt(level, blockPos, state, blockEntity, blockEntity.itemHandler.getItem(0).copy());

            if(blockEntity.itemHandler.getItem(0).isEmpty())
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

        Storage<ItemVariant> itemStackStorage = ItemStorage.SIDED.find(level, testPos, direction.getOpposite());
        if(itemStackStorage == null || !itemStackStorage.supportsInsertion())
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            long amount = itemStackStorage.insert(ItemVariant.of(itemStackToInsert), 1, transaction);
            if(amount > 0)
                blockEntity.itemHandler.setItem(0, ItemStack.EMPTY);

            transaction.commit();
        }
    }

    private static void extractItemStackFromBlockEntity(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltLoaderBlockEntity blockEntity) {
        Direction direction = blockEntity.getBlockState().getValue(ItemConveyorBeltLoaderBlock.FACING).getOpposite();

        BlockPos testPos = blockPos.relative(direction);
        BlockEntity testBlockEntity = level.getBlockEntity(testPos);
        if(testBlockEntity == null)
            return;

        Storage<ItemVariant> itemStackStorage = ItemStorage.SIDED.find(level, testPos, direction.getOpposite());
        if(itemStackStorage == null || !itemStackStorage.supportsExtraction())
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            for(StorageView<ItemVariant> itemView:itemStackStorage) {
                if(itemView.isResourceBlank())
                    continue;

                ItemVariant itemVariant = itemView.getResource();
                long amount = itemStackStorage.extract(itemVariant, 1, transaction);
                if(amount > 0) {
                    blockEntity.itemHandler.setItem(0, itemVariant.toStack(1));

                    break;
                }
            }

            transaction.commit();
        }
    }
}