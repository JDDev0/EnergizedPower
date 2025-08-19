package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.block.entity.base.InventoryStorageBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.ItemStackPacketUpdate;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ItemStackSyncS2CPacket;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class ItemConveyorBeltBlockEntity
        extends InventoryStorageBlockEntity<ItemStackHandler>
        implements ItemStackPacketUpdate {
    private final int ticksPerStep;

    private final LazyOptional<IItemHandler> lazyItemHandlerFrontSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 3));
    private final LazyOptional<IItemHandler> lazyItemHandlerOthersSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i == 3));

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
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();

                for(int i = 0;i < getSlots();i++)
                    if(level != null && !level.isClientSide())
                        ModMessages.sendToPlayersWithinXBlocks(
                                new ItemStackSyncS2CPacket(i, getStackInSlot(i), getBlockPos()),
                                getBlockPos(), level.dimension(), 64
                        );
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0, 1 -> true;
                    case 2, 3 -> false;
                    default -> super.isItemValid(slot, stack);
                };
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

    public int getRedstoneOutput() {
        return InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            Direction facing = getBlockState().getValue(ItemConveyorBeltBlock.FACING).getDirection();
            if(side.getOpposite() == facing)
                return lazyItemHandlerFrontSided.cast();

            return lazyItemHandlerOthersSided.cast();
        }

        return super.getCapability(cap, side);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        //Sync item stacks to client every 5 seconds
        if(level.getGameTime() % 100 == 0) //TODO improve
            for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
                if(!level.isClientSide())
                    ModMessages.sendToPlayersWithinXBlocks(
                            new ItemStackSyncS2CPacket(i, blockEntity.itemHandler.getStackInSlot(i), blockPos),
                            blockPos, level.dimension(), 64
                    );

        if(level.getGameTime() % blockEntity.ticksPerStep == 0) {
            int slotCount = blockEntity.itemHandler.getSlots();

            if(!blockEntity.itemHandler.getStackInSlot(slotCount - 1).isEmpty())
                insertItemStackIntoBlockEntity(level, blockPos, state, blockEntity, blockEntity.itemHandler.getStackInSlot(slotCount - 1).copy());

            for(int i = slotCount - 2;i >= 0;i--) {
                ItemStack fromItemStack = blockEntity.itemHandler.getStackInSlot(i);
                if(fromItemStack.isEmpty())
                    continue;

                ItemStack toItemStack = blockEntity.itemHandler.getStackInSlot(i + 1);
                if(!toItemStack.isEmpty())
                    continue;

                blockEntity.itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                blockEntity.itemHandler.setStackInSlot(i + 1, fromItemStack);
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
        LazyOptional<IItemHandler> itemStackStorageLazyOptional = testBlockEntity == null?null:testBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, facingDirection.getOpposite());
        if(itemStackStorageLazyOptional == null || !itemStackStorageLazyOptional.isPresent()) {
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

            itemStackStorageLazyOptional = testBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, facingDirection.getOpposite());
            if(!itemStackStorageLazyOptional.isPresent())
                return;
        }

        IItemHandler itemStackStorage = itemStackStorageLazyOptional.orElseGet(null);
        for(int i = 0;i < itemStackStorage.getSlots();i++) {
            if(itemStackStorage.insertItem(i, itemStackToInsert, false).isEmpty()) {
                blockEntity.itemHandler.setStackInSlot(blockEntity.itemHandler.getSlots() - 1, ItemStack.EMPTY);

                break;
            }
        }
    }

    public int getSlotCount() {
        return itemHandler.getSlots();
    }

    public ItemStack getStack(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    public void setItemStack(int slot, ItemStack itemStack) {
        itemHandler.setStackInSlot(slot, itemStack);
    }
}