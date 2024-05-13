package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltLoaderBlock;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.base.InventoryStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.screen.ItemConveyorBeltLoaderMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemConveyorBeltLoaderBlockEntity
        extends InventoryStorageBlockEntity<SimpleInventory>
        implements ExtendedScreenHandlerFactory<BlockPos> {
    private static final int TICKS_PER_ITEM = ModConfigs.COMMON_ITEM_CONVEYOR_BELT_LOADER_TICKS_PER_ITEM.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 0);

    public ItemConveyorBeltLoaderBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.ITEM_CONVEYOR_BELT_LOADER_ENTITY, blockPos, blockState,

                1
        );
    }

    @Override
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public int getMaxCountPerStack() {
                return 1;
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot == 0) {
                    return true;
                }

                return super.isValid(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                ItemConveyorBeltLoaderBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.item_conveyor_belt_loader");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ItemConveyorBeltLoaderMenu(id, this, inventory, itemHandler);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(itemHandler);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltLoaderBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(level.getTime() % TICKS_PER_ITEM == 0 && state.get(ItemConveyorBeltLoaderBlock.ENABLED)) {
            if(!blockEntity.itemHandler.getStack(0).isEmpty())
                insertItemStackIntoItemConveyorBelt(level, blockPos, state, blockEntity, blockEntity.itemHandler.getStack(0).copy());

            if(blockEntity.itemHandler.getStack(0).isEmpty())
                extractItemStackFromBlockEntity(level, blockPos, state, blockEntity);
        }
    }

    private static void insertItemStackIntoItemConveyorBelt(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltLoaderBlockEntity blockEntity,
                                                       ItemStack itemStackToInsert) {
        Direction direction = blockEntity.getCachedState().get(ItemConveyorBeltLoaderBlock.FACING);

        BlockPos testPos = blockPos.offset(direction);
        BlockState testBlockState = level.getBlockState(testPos);
        if(!testBlockState.isOf(ModBlocks.ITEM_CONVEYOR_BELT))
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
                blockEntity.itemHandler.setStack(0, ItemStack.EMPTY);

            transaction.commit();
        }
    }

    private static void extractItemStackFromBlockEntity(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltLoaderBlockEntity blockEntity) {
        Direction direction = blockEntity.getCachedState().get(ItemConveyorBeltLoaderBlock.FACING).getOpposite();

        BlockPos testPos = blockPos.offset(direction);
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
                    blockEntity.itemHandler.setStack(0, itemVariant.toStack(1));

                    break;
                }
            }

            transaction.commit();
        }
    }
}