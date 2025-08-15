package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltMergerBlock;
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
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemConveyorBeltMergerBlockEntity extends BlockEntity {
    private final int ticksPerItem;

    private int currentInputIndex;

    private final ConveyorBeltTier tier;

    public ItemConveyorBeltMergerBlockEntity(BlockPos blockPos, BlockState blockState, ConveyorBeltTier tier) {
        super(tier.getItemConveyorBeltMergerBlockEntityFromTier(), blockPos, blockState);

        this.tier = tier;

        ticksPerItem = switch(tier) {
            case BASIC -> ModConfigs.COMMON_BASIC_ITEM_CONVEYOR_BELT_MERGER_TICKS_PER_ITEM.getValue();
            case FAST -> ModConfigs.COMMON_FAST_ITEM_CONVEYOR_BELT_MERGER_TICKS_PER_ITEM.getValue();
            case EXPRESS -> ModConfigs.COMMON_EXPRESS_ITEM_CONVEYOR_BELT_MERGER_TICKS_PER_ITEM.getValue();
        };
    }

    public ConveyorBeltTier getTier() {
        return tier;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        view.putInt("current_input_index", currentInputIndex);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        currentInputIndex = view.getInt("current_input_index", 0);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltMergerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(level.getTime() % blockEntity.ticksPerItem == 0) {
            Direction facing = state.get(ItemConveyorBeltMergerBlock.FACING);

            BlockPos outputPos = blockPos.offset(facing);
            BlockState outputBlockState = level.getBlockState(outputPos);
            if(!(outputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
                return;

            BlockEntity outputBlockEntity = level.getBlockEntity(outputPos);
            if(!(outputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                return;

            Storage<ItemVariant> outputBeltItemStackStorage = ItemStorage.SIDED.find(level, outputPos, facing.getOpposite());
            if(outputBeltItemStackStorage == null || !outputBeltItemStackStorage.supportsInsertion())
                return;

            for(int j = 0;j < 3;j++) {
                int index = (blockEntity.currentInputIndex + j) % 3;
                Direction inputDirection = switch(index) {
                    case 0 -> facing.rotateYClockwise();
                    case 1 -> facing.getOpposite();
                    case 2 -> facing.rotateYCounterclockwise();
                    default -> null;
                };

                if(inputDirection == null)
                    return;

                BlockPos inputPos = blockPos.offset(inputDirection);
                BlockState inputBlockState = level.getBlockState(inputPos);
                if(!(inputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
                    continue;

                //Conveyor belt must face towards Merger and must not be ascending
                EPBlockStateProperties.ConveyorBeltDirection inputBeltFacing = inputBlockState.get(ItemConveyorBeltBlock.FACING);
                if(inputBeltFacing.isAscending() || inputBeltFacing.getDirection().getOpposite() != inputDirection)
                    continue;

                BlockEntity inputBlockEntity = level.getBlockEntity(inputPos);
                if(!(inputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                    continue;

                Storage<ItemVariant> inputBeltItemStackStorage = ItemStorage.SIDED.find(level, inputPos, inputDirection.getOpposite());
                if(inputBeltItemStackStorage == null || !inputBeltItemStackStorage.supportsExtraction())
                    continue;

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
                    continue;

                try(Transaction transaction = Transaction.openOuter()) {
                    long amount = outputBeltItemStackStorage.insert(ItemVariant.of(itemStackToSwitch), 1, transaction);
                    if(amount > 0)
                        inputBeltItemStackStorage.extract(ItemVariant.of(itemStackToSwitch),1, transaction);

                    transaction.commit();

                    if(amount > 0) {
                        blockEntity.currentInputIndex = (index + 1) % 3;
                        markDirty(level, blockPos, state);

                        return;
                    }
                }
            }
        }
    }
}