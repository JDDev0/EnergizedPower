package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltLoaderBlock;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.ItemConveyorBeltLoaderMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
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

public class ItemConveyorBeltLoaderBlockEntity extends BlockEntity  implements MenuProvider {
    private static final int TICKS_PER_ITEM = ModConfigs.COMMON_ITEM_CONVEYOR_BELT_LOADER_TICKS_PER_ITEM.getValue();

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 0));

    public ItemConveyorBeltLoaderBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ITEM_CONVEYOR_BELT_LOADER_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.item_conveyor_belt_loader");
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

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltLoaderBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(level.getGameTime() % TICKS_PER_ITEM == 0 && state.getValue(ItemConveyorBeltLoaderBlock.ENABLED)) {
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
        if(!testBlockState.is(ModBlocks.ITEM_CONVEYOR_BELT.get()))
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