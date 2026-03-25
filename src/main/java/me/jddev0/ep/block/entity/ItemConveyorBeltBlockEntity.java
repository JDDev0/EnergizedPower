package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.block.entity.base.InventoryStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.ItemStackPacketUpdate;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ItemStackSyncS2CPacket;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ItemConveyorBeltBlockEntity
        extends InventoryStorageBlockEntity<SimpleContainer>
        implements ItemStackPacketUpdate {
    private final int ticksPerStep;

    final InputOutputItemHandler itemHandlerFrontSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 3);
    final InputOutputItemHandler itemHandlerOthersSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i == 3);

    private final ConveyorBeltTier tier;

    public ItemConveyorBeltBlockEntity(BlockPos blockPos, BlockState blockState, ConveyorBeltTier tier) {
        super(
                tier.getItemConveyorBeltBlockEntityFromTier(), blockPos, blockState,

                4
        );

        this.tier = tier;

        ticksPerStep = switch(tier) {
            case BASIC -> ModConfigs.COMMON_BASIC_ITEM_CONVEYOR_BELT_TICKS_PER_STEP.getValue();
            case FAST -> ModConfigs.COMMON_FAST_ITEM_CONVEYOR_BELT_TICKS_PER_STEP.getValue();
            case EXPRESS -> ModConfigs.COMMON_EXPRESS_ITEM_CONVEYOR_BELT_TICKS_PER_STEP.getValue();
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
            public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0, 1 -> true;
                    case 2, 3 -> false;
                    default -> super.canPlaceItem(slot, stack);
                };
            }

            @Override
            public void setChanged() {
                super.setChanged();

                ItemConveyorBeltBlockEntity.this.setChanged();

                for(int i = 0;i < getContainerSize();i++)
                    if(level != null && !level.isClientSide()) {
                        ModMessages.sendServerPacketToPlayersWithinXBlocks(
                                getBlockPos(), (ServerLevel)level, 64,
                                new ItemStackSyncS2CPacket(i, getItem(i), getBlockPos())
                        );
                    }
            }
        };
    }

    public ConveyorBeltTier getTier() {
        return tier;
    }

    public int getRedstoneOutput() {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(itemHandler);
    }

    static Storage<ItemVariant> getInventoryStorageForDirection(ItemConveyorBeltBlockEntity entity, Direction side) {
        if(side == null)
            return null;

        Direction facing = entity.getBlockState().getValue(ItemConveyorBeltBlock.FACING).getDirection();
        if(side.getOpposite() == facing)
            return entity.itemHandlerFrontSided.apply(side);

        return entity.itemHandlerOthersSided.apply(side);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        //Sync item stacks to client every 5 seconds
        if(level.getGameTime() % 100 == 0) //TODO improve
            for(int i = 0;i < blockEntity.itemHandler.getContainerSize();i++)
                if(!level.isClientSide())
                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            blockPos, (ServerLevel)level, 64,
                            new ItemStackSyncS2CPacket(i, blockEntity.getStack(i), blockPos)
                    );

        if(level.getGameTime() % blockEntity.ticksPerStep == 0) {
            int slotCount = blockEntity.itemHandler.getContainerSize();

            if(!blockEntity.itemHandler.getItem(slotCount - 1).isEmpty())
                insertItemStackIntoBlockEntity(level, blockPos, state, blockEntity, blockEntity.itemHandler.getItem(slotCount - 1).copy());

            for(int i = slotCount - 2;i >= 0;i--) {
                ItemStack fromItemStack = blockEntity.itemHandler.getItem(i);
                if(fromItemStack.isEmpty())
                    continue;

                ItemStack toItemStack = blockEntity.itemHandler.getItem(i + 1);
                if(!toItemStack.isEmpty())
                    continue;

                blockEntity.itemHandler.setItem(i, ItemStack.EMPTY);
                blockEntity.itemHandler.setItem(i + 1, fromItemStack);
            }
        }
    }

    private static void insertItemStackIntoBlockEntity(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltBlockEntity blockEntity,
                                                       ItemStack itemStackToInsert) {
        EPBlockStateProperties.ConveyorBeltDirection facing = blockEntity.getBlockState().getValue(ItemConveyorBeltBlock.FACING);
        Direction facingDirection = facing.getDirection();

        BlockPos testPos = blockPos.relative(facingDirection);
        if(facing.isAscending())
            testPos = testPos.relative(Direction.UP);
        //Descending will insert on same height because it came from one block higher

        BlockEntity testBlockEntity = level.getBlockEntity(testPos);

        Storage<ItemVariant> itemStackStorage = testBlockEntity == null?null:ItemStorage.SIDED.find(level, testPos, facingDirection.getOpposite());
        if(itemStackStorage == null) {
            //Check for descending belt facing the same direction one block lower (Will also work if this belt is ascending)

            testPos = testPos.relative(Direction.DOWN);
            BlockState testBlockState = level.getBlockState(testPos);
            if(!(testBlockState.getBlock() instanceof ItemConveyorBeltBlock))
                return;

            EPBlockStateProperties.ConveyorBeltDirection testFacing = testBlockState.getValue(ItemConveyorBeltBlock.FACING);
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
                blockEntity.itemHandler.setItem(blockEntity.itemHandler.getContainerSize() - 1, ItemStack.EMPTY);

            transaction.commit();
        }
    }

    public int getSlotCount() {
        return itemHandler.getContainerSize();
    }

    public ItemStack getStack(int slot) {
        return itemHandler.getItem(slot);
    }

    @Override
    public void setItemStack(int slot, ItemStack itemStack) {
        itemHandler.setItem(slot, itemStack);
    }
}