package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltSplitterBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemConveyorBeltSplitterBlockEntity extends BlockEntity {
    private final int ticksPerItem;

    private int currentOutputIndex;

    private final ConveyorBeltTier tier;

    public ItemConveyorBeltSplitterBlockEntity(BlockPos blockPos, BlockState blockState, ConveyorBeltTier tier) {
        super(tier.getItemConveyorBeltSplitterBlockEntityFromTier(), blockPos, blockState);

        this.tier = tier;

        ticksPerItem = switch(tier) {
            case BASIC -> ModConfigs.COMMON_BASIC_ITEM_CONVEYOR_BELT_SPLITTER_TICKS_PER_ITEM.getValue();
            case FAST -> ModConfigs.COMMON_FAST_ITEM_CONVEYOR_BELT_SPLITTER_TICKS_PER_ITEM.getValue();
            case EXPRESS -> ModConfigs.COMMON_EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER_TICKS_PER_ITEM.getValue();
        };
    }

    public ConveyorBeltTier getTier() {
        return tier;
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, @NotNull RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        nbt.putInt("current_output_index", currentOutputIndex);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        currentOutputIndex = nbt.getInt("current_output_index");
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltSplitterBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(level.getTime() % blockEntity.ticksPerItem == 0) {
            Direction facing = state.get(ItemConveyorBeltSplitterBlock.FACING);

            BlockPos inputPos = blockPos.offset(facing);
            BlockState inputBlockState = level.getBlockState(inputPos);
            if(!(inputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
                return;

            //Conveyor belt must face towards Splitter and must not be ascending
            EPBlockStateProperties.ConveyorBeltDirection inputBeltFacing = inputBlockState.get(ItemConveyorBeltBlock.FACING);
            if(inputBeltFacing.isAscending() || inputBeltFacing.getDirection().getOpposite() != facing)
                return;

            BlockEntity inputBlockEntity = level.getBlockEntity(inputPos);
            if(!(inputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                return;

            Storage<ItemVariant> inputBeltItemStackStorage = ItemStorage.SIDED.find(level, inputPos, facing.getOpposite());
            if(inputBeltItemStackStorage == null || !inputBeltItemStackStorage.supportsExtraction())
                return;

            ItemStack itemStackToSwitch = ItemStack.EMPTY;
            try(Transaction transaction = Transaction.openOuter()) {
                for(StorageView<ItemVariant> itemView:inputBeltItemStackStorage) {
                    if(itemView.isResourceBlank())
                        continue;

                    ItemVariant itemVariant = itemView.getResource();
                    long amount = inputBeltItemStackStorage.extract(itemVariant, 1, transaction);
                    if(amount > 0) {
                        itemStackToSwitch = itemVariant.toStack(1);

                        break;
                    }
                }
            }
            if(itemStackToSwitch.isEmpty())
                return;

            for(int j = 0;j < 3;j++) {
                int index = (blockEntity.currentOutputIndex + j) % 3;
                Direction outputDirection = switch(index) {
                    case 0 -> facing.rotateYClockwise();
                    case 1 -> facing.getOpposite();
                    case 2 -> facing.rotateYCounterclockwise();
                    default -> null;
                };

                if(outputDirection == null)
                    return;

                BlockPos outputPos = blockPos.offset(outputDirection);
                BlockState outputBlockState = level.getBlockState(outputPos);
                if(!(outputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
                    continue;

                BlockEntity outputBlockEntity = level.getBlockEntity(outputPos);
                if(!(outputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                    continue;

                Storage<ItemVariant> outputBeltItemStackStorage = ItemStorage.SIDED.find(level, outputPos, outputDirection.getOpposite());
                if(outputBeltItemStackStorage == null || !outputBeltItemStackStorage.supportsInsertion())
                    continue;

                try(Transaction transaction = Transaction.openOuter()) {
                    long amount = outputBeltItemStackStorage.insert(ItemVariant.of(itemStackToSwitch), 1, transaction);
                    if(amount > 0)
                        inputBeltItemStackStorage.extract(ItemVariant.of(itemStackToSwitch),1, transaction);

                    transaction.commit();

                    if(amount > 0) {
                        blockEntity.currentOutputIndex = (index + 1) % 3;
                        markDirty(level, blockPos, state);

                        return;
                    }
                }
            }
        }
    }
}