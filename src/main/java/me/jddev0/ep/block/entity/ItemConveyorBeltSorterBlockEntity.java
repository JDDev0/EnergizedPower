package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltSorterBlock;
import me.jddev0.ep.block.ModBlockStateProperties;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.ItemConveyorBeltSorterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemConveyorBeltSorterBlockEntity extends BlockEntity implements MenuProvider {
    private static final int TICKS_PER_ITEM = ModConfigs.COMMON_ITEM_CONVEYOR_BELT_SORTER_TICKS_PER_ITEM.getValue();

    private static final int PATTERN_SLOTS_PER_OUTPUT = 5;

    private final SimpleContainer patternSlots = new SimpleContainer(3 * PATTERN_SLOTS_PER_OUTPUT) {
        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };
    private final ContainerListener updatePatternListener = container -> ItemConveyorBeltSorterBlockEntity.this.setChanged();

    protected final ContainerData data;
    private boolean[] outputBeltConnected = new boolean[] {
            false, false, false
    };
    private boolean[] whitelist = new boolean[] {
            true, true, true
    };
    private boolean[] ignoreNBT = new boolean[] {
            false, false, false
    };

    private boolean loaded;

    public ItemConveyorBeltSorterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ITEM_CONVEYOR_BELT_SORTER_ENTITY.get(), blockPos, blockState);

        patternSlots.addListener(updatePatternListener);

        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1, 2 -> ItemConveyorBeltSorterBlockEntity.this.outputBeltConnected[index]?1:0;
                    case 3, 4, 5 -> ItemConveyorBeltSorterBlockEntity.this.whitelist[index - 3]?1:0;
                    case 6, 7, 8 -> ItemConveyorBeltSorterBlockEntity.this.ignoreNBT[index - 6]?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2 -> ItemConveyorBeltSorterBlockEntity.this.outputBeltConnected[index] = value != 0;
                    case 3, 4, 5 -> ItemConveyorBeltSorterBlockEntity.this.whitelist[index - 3] = value != 0;
                    case 6, 7, 8 -> ItemConveyorBeltSorterBlockEntity.this.ignoreNBT[index - 6] = value != 0;
                }
            }

            @Override
            public int getCount() {
                return 9;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.item_conveyor_belt_sorter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ItemConveyorBeltSorterMenu(id, inventory, this, patternSlots, data);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("pattern", savePatternContainer());

        for(int i = 0;i < 3;i++)
            nbt.putBoolean("recipe.whitelist." + i, whitelist[i]);
        for(int i = 0;i < 3;i++)
            nbt.putBoolean("recipe.ignore_nbt." + i, ignoreNBT[i]);

        super.saveAdditional(nbt);
    }

    private Tag savePatternContainer() {
        NonNullList<ItemStack> items = NonNullList.withSize(patternSlots.getContainerSize(), ItemStack.EMPTY);
        for(int i = 0;i < patternSlots.getContainerSize();i++)
            items.set(i, patternSlots.getItem(i));

        return ContainerHelper.saveAllItems(new CompoundTag(), items);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        loadPatternContainer(nbt.getCompound("pattern"));

        for(int i = 0;i < 3;i++)
            whitelist[i] = nbt.getBoolean("recipe.whitelist." + i);
        for(int i = 0;i < 3;i++)
            ignoreNBT[i] = nbt.getBoolean("recipe.ignore_nbt." + i);
    }

    private void loadPatternContainer(CompoundTag tag) {
        patternSlots.removeListener(updatePatternListener);

        NonNullList<ItemStack> items = NonNullList.withSize(patternSlots.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
        for(int i = 0;i < patternSlots.getContainerSize();i++)
            patternSlots.setItem(i, items.get(i));

        patternSlots.addListener(updatePatternListener);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltSorterBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(!blockEntity.loaded) {
            for(int i = 0;i < 3;i++) {
                Direction facing = state.getValue(ItemConveyorBeltSorterBlock.FACING);

                Direction outputBeltDirection = switch(i) {
                    case 0 -> facing.getClockWise();
                    case 1 -> facing.getOpposite();
                    case 2 -> facing.getCounterClockWise();
                    default -> null;
                };

                BlockState outputBeltState = level.getBlockState(blockPos.relative(outputBeltDirection));
                blockEntity.setOutputBeltConnected(i, outputBeltState.is(ModBlocks.ITEM_CONVEYOR_BELT.get()));
            }

            blockEntity.loaded = true;
        }

        if(level.getGameTime() % TICKS_PER_ITEM == 0) {
            Direction facing = state.getValue(ItemConveyorBeltSorterBlock.FACING);

            BlockPos inputPos = blockPos.relative(facing);
            BlockState inputBlockState = level.getBlockState(inputPos);
            if(!inputBlockState.is(ModBlocks.ITEM_CONVEYOR_BELT.get())) {
                updatePoweredState(level, blockPos, state, blockEntity, false);

                return;
            }

            //Conveyor belt must face towards sorter and must not be ascending
            ModBlockStateProperties.ConveyorBeltDirection inputBeltFacing = inputBlockState.getValue(ItemConveyorBeltBlock.FACING);
            if(inputBeltFacing.isAscending() || inputBeltFacing.getDirection().getOpposite() != facing) {
                updatePoweredState(level, blockPos, state, blockEntity, false);

                return;
            }

            BlockEntity inputBlockEntity = level.getBlockEntity(inputPos);
            if(!(inputBlockEntity instanceof ItemConveyorBeltBlockEntity)) {
                updatePoweredState(level, blockPos, state, blockEntity, false);

                return;
            }

            LazyOptional<IItemHandler> itemStackStorageLazyOptional = inputBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, facing.getOpposite());
            if(!itemStackStorageLazyOptional.isPresent()) {
                updatePoweredState(level, blockPos, state, blockEntity, false);

                return;
            }

            IItemHandler inputBeltItemStackStorage = itemStackStorageLazyOptional.orElseGet(null);
            ItemStack itemStackToSort = inputBeltItemStackStorage.getStackInSlot(inputBeltItemStackStorage.getSlots() - 1);
            if(itemStackToSort.isEmpty()) {
                updatePoweredState(level, blockPos, state, blockEntity, false);

                return;
            }

            for(int i = 0;i < 3;i++) {
                if(filterMatches(blockEntity, i, itemStackToSort)) {
                    IItemHandler outputBeltItemStackStorage = getOutputBeltItemStackStorage(level, blockPos, state, blockEntity, i);
                    if(outputBeltItemStackStorage == null) {
                        //Filter matched but output belt is not present

                        updatePoweredState(level, blockPos, state, blockEntity, false);

                        return;
                    }

                    tryInsertItemStackIntoOutputBelt(itemStackToSort, inputBeltItemStackStorage, outputBeltItemStackStorage);

                    updatePoweredState(level, blockPos, state, blockEntity, false);

                    return;
                }
            }

            //No filter matched
            updatePoweredState(level, blockPos, state, blockEntity, true);
        }
    }

    private static void updatePoweredState(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltSorterBlockEntity blockEntity,
                                          boolean powered) {
        if(powered != state.getValue(ItemConveyorBeltSorterBlock.POWERED)) {
            level.setBlock(blockPos, state.setValue(ItemConveyorBeltSorterBlock.POWERED, powered), 3);

            setChanged(level, blockPos, state);
        }
    }

    private static boolean filterMatches(ItemConveyorBeltSorterBlockEntity blockEntity, int index, ItemStack itemStackToSort) {
        for(int i = 0;i < PATTERN_SLOTS_PER_OUTPUT;i++) {
            ItemStack patternItemStack = blockEntity.patternSlots.getItem(3 * index + i);
            if(patternItemStack.isEmpty())
                continue;

            if(blockEntity.ignoreNBT[index]?ItemStack.isSame(itemStackToSort, patternItemStack):
                    ItemStack.isSameItemSameTags(itemStackToSort, patternItemStack))
                return blockEntity.whitelist[index];
        }

        return !blockEntity.whitelist[index];
    }

    private static IItemHandler getOutputBeltItemStackStorage(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltSorterBlockEntity blockEntity,
                                        int index) {
        Direction direction = state.getValue(ItemConveyorBeltSorterBlock.FACING);
        direction = switch(index) {
            case 0 -> direction.getClockWise();
            case 1 -> direction.getOpposite();
            case 2 -> direction.getCounterClockWise();
            default -> null;
        };
        if(direction == null)
            return null;

        BlockPos outputPos = blockPos.relative(direction);
        BlockState outputBlockState = level.getBlockState(outputPos);
        if(!outputBlockState.is(ModBlocks.ITEM_CONVEYOR_BELT.get()))
            return null;

        BlockEntity outputBlockEntity = level.getBlockEntity(outputPos);
        if(!(outputBlockEntity instanceof ItemConveyorBeltBlockEntity))
            return null;

        LazyOptional<IItemHandler> itemStackStorageLazyOptional = outputBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite());
        if(!itemStackStorageLazyOptional.isPresent())
            return null;

        return itemStackStorageLazyOptional.orElseGet(null);
    }

    private static boolean tryInsertItemStackIntoOutputBelt(ItemStack itemStackToSort, IItemHandler inputBeltItemStackStorage, IItemHandler outputBeltItemStackStorage) {
        for(int i = 0;i < outputBeltItemStackStorage.getSlots();i++) {
            if(outputBeltItemStackStorage.insertItem(i, itemStackToSort, false).isEmpty()) {
                inputBeltItemStackStorage.extractItem(inputBeltItemStackStorage.getSlots() - 1, 1, false);

                return true;
            }
        }

        return false;
    }

    public void setOutputBeltConnected(int index, boolean outputBeltConnected) {
        this.outputBeltConnected[index] = outputBeltConnected;
        setChanged(level, getBlockPos(), getBlockState());
    }

    public void setWhitelist(int index, boolean whitelist) {
        this.whitelist[index] = whitelist;
        setChanged(level, getBlockPos(), getBlockState());
    }

    public void setIgnoreNBT(int index, boolean ignoreNBT) {
        this.ignoreNBT[index] = ignoreNBT;
        setChanged(level, getBlockPos(), getBlockState());
    }
}