package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ModBlockStateProperties;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.ItemStackPacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class ItemConveyorBeltBlockEntity extends BlockEntity implements ItemStackPacketUpdate {
    private static final int TICKS_PER_BLOCK = (int)(1.f / ModConfigs.COMMON_ITEM_CONVEYOR_BELT_SPEED.getValue());

    private final int TICKS_PER_STEP;

    final CachedSidedInventoryStorage<ItemConveyorBeltBlockEntity> cachedSidedInventoryStorageFront;
    final InputOutputItemHandler sidedInventoryFront;

    final CachedSidedInventoryStorage<ItemConveyorBeltBlockEntity> cachedSidedInventoryStorageOthers;
    final InputOutputItemHandler sidedInventoryOthers;
    private final SimpleInventory internalInventory;

    static Storage<ItemVariant> getInventoryStorageForDirection(ItemConveyorBeltBlockEntity entity, Direction side) {
        if(side == null)
            return null;

        Direction facing = entity.getCachedState().get(ItemConveyorBeltBlock.FACING).getDirection();
        if(side.getOpposite() == facing)
            return entity.cachedSidedInventoryStorageFront.apply(side);

        return entity.cachedSidedInventoryStorageOthers.apply(side);
    }

    public ItemConveyorBeltBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ITEM_CONVEYOR_BELT_ENTITY, blockPos, blockState);

        internalInventory = new SimpleInventory(4) {
            @Override
            public int getMaxCountPerStack() {
                return 1;
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0, 1 -> true;
                    case 2, 3 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void markDirty() {
                super.markDirty();

                ItemConveyorBeltBlockEntity.this.markDirty();

                for(int i = 0;i < size();i++)
                    if(world != null && !world.isClient()) {
                        PacketByteBuf buffer = PacketByteBufs.create();
                        buffer.writeInt(i);
                        buffer.writeItemStack(getStack(i));
                        buffer.writeBlockPos(getPos());

                        ModMessages.sendServerPacketToPlayersWithinXBlocks(
                                getPos(), (ServerWorld)world, 64,
                                ModMessages.ITEM_STACK_SYNC_ID, buffer
                        );
                    }
            }
        };
        sidedInventoryFront = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 4).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i == 0, i -> i == 3);
        cachedSidedInventoryStorageFront = new CachedSidedInventoryStorage<>(sidedInventoryFront);
        sidedInventoryOthers = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 4).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i == 1, i -> i == 3);
        cachedSidedInventoryStorageOthers = new CachedSidedInventoryStorage<>(sidedInventoryOthers);

        TICKS_PER_STEP = Math.max(TICKS_PER_BLOCK / internalInventory.size(), 1);
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

    public static void tick(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltBlockEntity blockEntity) {
        if(level.isClient())
            return;

        //Sync item stacks to client every 5 seconds
        if(level.getTime() % 100 == 0) //TODO improve
            for(int i = 0;i < blockEntity.internalInventory.size();i++)
                if(!level.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeInt(i);
                    buffer.writeItemStack(blockEntity.getStack(i));
                    buffer.writeBlockPos(blockPos);

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            blockPos, (ServerWorld)level, 64,
                            ModMessages.ITEM_STACK_SYNC_ID, buffer
                    );
                }

        if(level.getTime() % blockEntity.TICKS_PER_STEP == 0) {
            int slotCount = blockEntity.internalInventory.size();

            if(!blockEntity.internalInventory.getStack(slotCount - 1).isEmpty())
                insertItemStackIntoBlockEntity(level, blockPos, state, blockEntity, blockEntity.internalInventory.getStack(slotCount - 1).copy());

            for(int i = slotCount - 2;i >= 0;i--) {
                ItemStack fromItemStack = blockEntity.internalInventory.getStack(i);
                if(fromItemStack.isEmpty())
                    continue;

                ItemStack toItemStack = blockEntity.internalInventory.getStack(i + 1);
                if(!toItemStack.isEmpty())
                    continue;

                blockEntity.internalInventory.setStack(i, ItemStack.EMPTY);
                blockEntity.internalInventory.setStack(i + 1, fromItemStack);
            }
        }
    }

    private static void insertItemStackIntoBlockEntity(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltBlockEntity blockEntity,
                                                       ItemStack itemStackToInsert) {
        ModBlockStateProperties.ConveyorBeltDirection facing = blockEntity.getCachedState().get(ItemConveyorBeltBlock.FACING);
        Direction facingDirection = facing.getDirection();

        BlockPos testPos = blockPos.offset(facingDirection);
        if(facing.isAscending())
            testPos = testPos.offset(Direction.UP);
        //Descending will insert on same height because it came from one block higher

        BlockEntity testBlockEntity = level.getBlockEntity(testPos);

        Storage<ItemVariant> itemStackStorage = testBlockEntity == null?null:ItemStorage.SIDED.find(level, testPos, facingDirection.getOpposite());
        if(itemStackStorage == null) {
            //Check for descending belt facing the same direction one block lower (Will also work if this belt is ascending)

            testPos = testPos.offset(Direction.DOWN);
            BlockState testBlockState = level.getBlockState(testPos);
            if(!testBlockState.isOf(ModBlocks.ITEM_CONVEYOR_BELT))
                return;

            ModBlockStateProperties.ConveyorBeltDirection testFacing = testBlockState.get(ItemConveyorBeltBlock.FACING);
            if(!testFacing.isDescending() || testFacing.getDirection() != facingDirection)
                return;

            testBlockEntity = level.getBlockEntity(testPos);
            if(!(testBlockEntity instanceof ItemConveyorBeltBlockEntity))
                return;

            itemStackStorage = ItemStorage.SIDED.find(level, testPos, facingDirection.getOpposite());
            if(itemStackStorage == null)
                return;
        }

        if(!itemStackStorage.supportsInsertion())
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            long amount = itemStackStorage.insert(ItemVariant.of(itemStackToInsert), 1, transaction);
            if(amount > 0)
                blockEntity.internalInventory.setStack(blockEntity.internalInventory.size() - 1, ItemStack.EMPTY);

            transaction.commit();
        }
    }

    public int getSlotCount() {
        return internalInventory.size();
    }

    public ItemStack getStack(int slot) {
        return internalInventory.getStack(slot);
    }

    @Override
    public void setItemStack(int slot, ItemStack itemStack) {
        internalInventory.setStack(slot, itemStack);
    }
}