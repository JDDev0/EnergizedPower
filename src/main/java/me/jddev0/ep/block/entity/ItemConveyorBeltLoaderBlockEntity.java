package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltLoaderBlock;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
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
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class ItemConveyorBeltLoaderBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    private static final int TICKS_PER_ITEM = ModConfigs.COMMON_ITEM_CONVEYOR_BELT_LOADER_TICKS_PER_ITEM.getValue();

    final CachedSidedInventoryStorage<BlockPlacerBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    public ItemConveyorBeltLoaderBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ITEM_CONVEYOR_BELT_LOADER_ENTITY, blockPos, blockState);

        internalInventory = new SimpleInventory(1) {
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
        }, (i, stack) -> true, i -> true);
        cachedSidedInventoryStorage = new CachedSidedInventoryStorage<>(inventory);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.item_conveyor_belt_loader");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ItemConveyorBeltLoaderMenu(id, this, inventory, internalInventory);
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

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.stacks);
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.stacks);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltLoaderBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(level.getTime() % TICKS_PER_ITEM == 0 && state.get(ItemConveyorBeltLoaderBlock.ENABLED)) {
            if(!blockEntity.internalInventory.getStack(0).isEmpty())
                insertItemStackIntoItemConveyorBelt(level, blockPos, state, blockEntity, blockEntity.internalInventory.getStack(0).copy());

            if(blockEntity.internalInventory.getStack(0).isEmpty())
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
                blockEntity.internalInventory.setStack(0, ItemStack.EMPTY);

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
                    blockEntity.internalInventory.setStack(0, itemVariant.toStack(1));

                    break;
                }
            }

            transaction.commit();
        }
    }
}