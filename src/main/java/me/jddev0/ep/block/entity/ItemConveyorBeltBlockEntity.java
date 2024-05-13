package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ModBlockStateProperties;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.base.InventoryStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.ItemStackPacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ItemStackSyncS2CPacket;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemConveyorBeltBlockEntity
        extends InventoryStorageBlockEntity<SimpleInventory>
        implements ItemStackPacketUpdate {
    private static final int TICKS_PER_STEP = ModConfigs.COMMON_ITEM_CONVEYOR_BELT_TICKS_PER_STEP.getValue();

    final InputOutputItemHandler itemHandlerFrontSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 3);
    final InputOutputItemHandler itemHandlerOthersSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i == 3);

    public ItemConveyorBeltBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.ITEM_CONVEYOR_BELT_ENTITY, blockPos, blockState,

                4
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
                        ModMessages.sendServerPacketToPlayersWithinXBlocks(
                                getPos(), (ServerWorld)world, 64,
                                new ItemStackSyncS2CPacket(i, getStack(i), getPos())
                        );
                    }
            }
        };
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(itemHandler);
    }

    static Storage<ItemVariant> getInventoryStorageForDirection(ItemConveyorBeltBlockEntity entity, Direction side) {
        if(side == null)
            return null;

        Direction facing = entity.getCachedState().get(ItemConveyorBeltBlock.FACING).getDirection();
        if(side.getOpposite() == facing)
            return entity.itemHandlerFrontSided.apply(side);

        return entity.itemHandlerOthersSided.apply(side);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltBlockEntity blockEntity) {
        if(level.isClient())
            return;

        //Sync item stacks to client every 5 seconds
        if(level.getTime() % 100 == 0) //TODO improve
            for(int i = 0;i < blockEntity.itemHandler.size();i++)
                if(!level.isClient())
                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            blockPos, (ServerWorld)level, 64,
                            new ItemStackSyncS2CPacket(i, blockEntity.getStack(i), blockPos)
                    );

        if(level.getTime() % blockEntity.TICKS_PER_STEP == 0) {
            int slotCount = blockEntity.itemHandler.size();

            if(!blockEntity.itemHandler.getStack(slotCount - 1).isEmpty())
                insertItemStackIntoBlockEntity(level, blockPos, state, blockEntity, blockEntity.itemHandler.getStack(slotCount - 1).copy());

            for(int i = slotCount - 2;i >= 0;i--) {
                ItemStack fromItemStack = blockEntity.itemHandler.getStack(i);
                if(fromItemStack.isEmpty())
                    continue;

                ItemStack toItemStack = blockEntity.itemHandler.getStack(i + 1);
                if(!toItemStack.isEmpty())
                    continue;

                blockEntity.itemHandler.setStack(i, ItemStack.EMPTY);
                blockEntity.itemHandler.setStack(i + 1, fromItemStack);
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
                blockEntity.itemHandler.setStack(blockEntity.itemHandler.size() - 1, ItemStack.EMPTY);

            transaction.commit();
        }
    }

    public int getSlotCount() {
        return itemHandler.size();
    }

    public ItemStack getStack(int slot) {
        return itemHandler.getStack(slot);
    }

    @Override
    public void setItemStack(int slot, ItemStack itemStack) {
        itemHandler.setStack(slot, itemStack);
    }
}