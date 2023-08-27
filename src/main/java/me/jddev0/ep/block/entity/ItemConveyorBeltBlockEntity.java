package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ModBlockStateProperties;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.ItemStackPacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ItemStackSyncS2CPacket;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
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

public class ItemConveyorBeltBlockEntity extends BlockEntity implements ItemStackPacketUpdate {
    private static final int TICKS_PER_BLOCK = (int)(1.f / ModConfigs.COMMON_ITEM_CONVEYOR_BELT_SPEED.getValue());

    private final int TICKS_PER_STEP;

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
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
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> lazyItemHandlerFrontSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i >= 0 && i < 4));
    private final LazyOptional<IItemHandler> lazyItemHandlerOthersSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i >= 0 && i < 4));

    public ItemConveyorBeltBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ITEM_CONVEYOR_BELT_ENTITY.get(), blockPos, blockState);

        TICKS_PER_STEP = Math.max(TICKS_PER_BLOCK / itemHandler.getSlots(), 1);
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

    @Override
    public void onLoad() {
        super.onLoad();

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);
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

        if(level.getGameTime() % blockEntity.TICKS_PER_STEP == 0) {
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
        ModBlockStateProperties.ConveyorBeltDirection facing = blockEntity.getBlockState().getValue(ItemConveyorBeltBlock.FACING);
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
            if(!testBlockState.is(ModBlocks.ITEM_CONVEYOR_BELT.get()))
                return;

            ModBlockStateProperties.ConveyorBeltDirection testFacing = testBlockState.getValue(ItemConveyorBeltBlock.FACING);
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