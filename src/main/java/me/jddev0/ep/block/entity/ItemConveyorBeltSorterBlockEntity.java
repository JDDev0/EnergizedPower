package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltSorterBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
import me.jddev0.ep.screen.ItemConveyorBeltSorterMenu;
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
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemConveyorBeltSorterBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, CheckboxUpdate {
    private final int ticksPerItem;

    private static final int PATTERN_SLOTS_PER_OUTPUT = 5;

    private final SimpleInventory patternSlots = new SimpleInventory(3 * PATTERN_SLOTS_PER_OUTPUT) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    private final InventoryChangedListener updatePatternListener = container -> ItemConveyorBeltSorterBlockEntity.this.markDirty();

    protected final PropertyDelegate data;
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

    private final ConveyorBeltTier tier;

    public ItemConveyorBeltSorterBlockEntity(BlockPos blockPos, BlockState blockState, ConveyorBeltTier tier) {
        super(tier.getItemConveyorBeltSorterBlockEntityFromTier(), blockPos, blockState);

        this.tier = tier;

        patternSlots.addListener(updatePatternListener);

        data = new CombinedContainerData(
                new BooleanValueContainerData(() -> outputBeltConnected[0], value -> outputBeltConnected[0] = value),
                new BooleanValueContainerData(() -> outputBeltConnected[1], value -> outputBeltConnected[1] = value),
                new BooleanValueContainerData(() -> outputBeltConnected[2], value -> outputBeltConnected[2] = value),
                new BooleanValueContainerData(() -> whitelist[0], value -> whitelist[0] = value),
                new BooleanValueContainerData(() -> whitelist[1], value -> whitelist[1] = value),
                new BooleanValueContainerData(() -> whitelist[2], value -> whitelist[2] = value),
                new BooleanValueContainerData(() -> ignoreNBT[0], value -> ignoreNBT[0] = value),
                new BooleanValueContainerData(() -> ignoreNBT[1], value -> ignoreNBT[1] = value),
                new BooleanValueContainerData(() -> ignoreNBT[2], value -> ignoreNBT[2] = value)
        );

        ticksPerItem = switch(tier) {
            case BASIC -> ModConfigs.COMMON_BASIC_ITEM_CONVEYOR_BELT_SORTER_TICKS_PER_ITEM.getValue();
            case FAST -> ModConfigs.COMMON_FAST_ITEM_CONVEYOR_BELT_SORTER_TICKS_PER_ITEM.getValue();
            case EXPRESS -> ModConfigs.COMMON_EXPRESS_ITEM_CONVEYOR_BELT_SORTER_TICKS_PER_ITEM.getValue();
        };
    }

    public ConveyorBeltTier getTier() {
        return tier;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower." + switch(tier) {
            case BASIC -> "item_conveyor_belt_sorter";
            case FAST -> "fast_item_conveyor_belt_sorter";
            case EXPRESS -> "express_item_conveyor_belt_sorter";
        });
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ItemConveyorBeltSorterMenu(id, inventory, this, patternSlots, data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put("pattern", Inventories.writeNbt(new NbtCompound(), patternSlots.stacks));

        for(int i = 0;i < 3;i++)
            nbt.putBoolean("recipe.whitelist." + i, whitelist[i]);
        for(int i = 0;i < 3;i++)
            nbt.putBoolean("recipe.ignore_nbt." + i, ignoreNBT[i]);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("pattern"), patternSlots.stacks);

        for(int i = 0;i < 3;i++)
            whitelist[i] = nbt.getBoolean("recipe.whitelist." + i);
        for(int i = 0;i < 3;i++)
            ignoreNBT[i] = nbt.getBoolean("recipe.ignore_nbt." + i);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltSorterBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.loaded) {
            for(int i = 0;i < 3;i++) {
                Direction facing = state.get(ItemConveyorBeltSorterBlock.FACING);

                Direction outputBeltDirection = switch(i) {
                    case 0 -> facing.rotateYClockwise();
                    case 1 -> facing.getOpposite();
                    case 2 -> facing.rotateYCounterclockwise();
                    default -> null;
                };

                BlockState outputBeltState = level.getBlockState(blockPos.offset(outputBeltDirection));
                blockEntity.setOutputBeltConnected(i, outputBeltState.getBlock() instanceof ItemConveyorBeltBlock);
            }

            blockEntity.loaded = true;
        }

        if(level.getTime() % blockEntity.ticksPerItem == 0) {
            Direction facing = state.get(ItemConveyorBeltSorterBlock.FACING);

            BlockPos inputPos = blockPos.offset(facing);
            BlockState inputBlockState = level.getBlockState(inputPos);
            if(!(inputBlockState.getBlock() instanceof ItemConveyorBeltBlock)) {
                updatePoweredState(level, blockPos, state, blockEntity, false);

                return;
            }

            //Conveyor belt must face towards sorter and must not be ascending
            EPBlockStateProperties.ConveyorBeltDirection inputBeltFacing = inputBlockState.get(ItemConveyorBeltBlock.FACING);
            if(inputBeltFacing.isAscending() || inputBeltFacing.getDirection().getOpposite() != facing) {
                updatePoweredState(level, blockPos, state, blockEntity, false);

                return;
            }

            BlockEntity inputBlockEntity = level.getBlockEntity(inputPos);
            if(!(inputBlockEntity instanceof ItemConveyorBeltBlockEntity)) {
                updatePoweredState(level, blockPos, state, blockEntity, false);

                return;
            }

            Storage<ItemVariant> inputBeltItemStackStorage = ItemStorage.SIDED.find(level, inputPos, facing.getOpposite());
            if(inputBeltItemStackStorage == null || !inputBeltItemStackStorage.supportsExtraction()) {
                updatePoweredState(level, blockPos, state, blockEntity, false);

                return;
            }

            ItemStack itemStackToSort = ItemStack.EMPTY;
            try(Transaction transaction = Transaction.openOuter()) {
                for(StorageView<ItemVariant> itemView:inputBeltItemStackStorage) {
                    if(itemView.isResourceBlank())
                        continue;

                    ItemVariant itemVariant = itemView.getResource();
                    long amount = inputBeltItemStackStorage.extract(itemVariant, 1, transaction);
                    if(amount > 0) {
                        itemStackToSort = itemVariant.toStack(1);

                        break;
                    }
                }
            }
            if(itemStackToSort.isEmpty()) {
                updatePoweredState(level, blockPos, state, blockEntity, false);

                return;
            }

            for(int i = 0;i < 3;i++) {
                if(filterMatches(blockEntity, i, itemStackToSort)) {
                    Storage<ItemVariant> outputBeltItemStackStorage = getOutputBeltItemStackStorage(level, blockPos, state, blockEntity, i);
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

    private static void updatePoweredState(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltSorterBlockEntity blockEntity,
                                          boolean powered) {
        if(powered != state.get(ItemConveyorBeltSorterBlock.POWERED)) {
            level.setBlockState(blockPos, state.with(ItemConveyorBeltSorterBlock.POWERED, powered), 3);

            markDirty(level, blockPos, state);
        }
    }

    private static boolean filterMatches(ItemConveyorBeltSorterBlockEntity blockEntity, int index, ItemStack itemStackToSort) {
        for(int i = 0;i < PATTERN_SLOTS_PER_OUTPUT;i++) {
            ItemStack patternItemStack = blockEntity.patternSlots.getStack(PATTERN_SLOTS_PER_OUTPUT * index + i);
            if(patternItemStack.isEmpty())
                continue;

            if(blockEntity.ignoreNBT[index]?ItemStack.areItemsEqual(itemStackToSort, patternItemStack):
                    ItemStack.canCombine(itemStackToSort, patternItemStack))
                return blockEntity.whitelist[index];
        }

        return !blockEntity.whitelist[index];
    }

    private static Storage<ItemVariant> getOutputBeltItemStackStorage(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltSorterBlockEntity blockEntity,
                                        int index) {
        Direction direction = state.get(ItemConveyorBeltSorterBlock.FACING);
        direction = switch(index) {
            case 0 -> direction.rotateYClockwise();
            case 1 -> direction.getOpposite();
            case 2 -> direction.rotateYCounterclockwise();
            default -> null;
        };
        if(direction == null)
            return null;

        BlockPos outputPos = blockPos.offset(direction);
        BlockState outputBlockState = level.getBlockState(outputPos);
        if(!(outputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
            return null;

        BlockEntity outputBlockEntity = level.getBlockEntity(outputPos);
        if(!(outputBlockEntity instanceof ItemConveyorBeltBlockEntity))
            return null;

        Storage<ItemVariant> itemStackStorage = ItemStorage.SIDED.find(level, outputPos, direction.getOpposite());
        if(itemStackStorage == null || !itemStackStorage.supportsInsertion())
            return null;

        return itemStackStorage;
    }

    private static boolean tryInsertItemStackIntoOutputBelt(ItemStack itemStackToSort, Storage<ItemVariant> inputBeltItemStackStorage, Storage<ItemVariant> outputBeltItemStackStorage) {
        try(Transaction transaction = Transaction.openOuter()) {
            long amount = outputBeltItemStackStorage.insert(ItemVariant.of(itemStackToSort), 1, transaction);
            if(amount > 0)
                inputBeltItemStackStorage.extract(ItemVariant.of(itemStackToSort),1, transaction);

            transaction.commit();

            if(amount > 0)
                return true;
        }

        return false;
    }

    public void setOutputBeltConnected(int index, boolean outputBeltConnected) {
        this.outputBeltConnected[index] = outputBeltConnected;
        markDirty(world, getPos(), getCachedState());
    }

    public void setWhitelist(int index, boolean whitelist) {
        this.whitelist[index] = whitelist;
        markDirty(world, getPos(), getCachedState());
    }

    public void setIgnoreNBT(int index, boolean ignoreNBT) {
        this.ignoreNBT[index] = ignoreNBT;
        markDirty(world, getPos(), getCachedState());
    }

    @Override
    public void setCheckbox(int checkboxId, boolean checked) {
        switch(checkboxId) {
            //Whitelist [3x]
            case 0, 1, 2 -> setWhitelist(checkboxId, checked);

            //Ignore NBT [3x]
            case 3, 4, 5 -> setIgnoreNBT(checkboxId - 3, checked);
        }
    }
}