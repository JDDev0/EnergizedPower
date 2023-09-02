package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltMergerBlock;
import me.jddev0.ep.block.ModBlockStateProperties;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.config.ModConfigs;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemConveyorBeltMergerBlockEntity extends BlockEntity {
    private static final int TICKS_PER_ITEM = ModConfigs.COMMON_ITEM_CONVEYOR_BELT_MERGER_TICKS_PER_ITEM.getValue();

    private int currentInputIndex;

    public ItemConveyorBeltMergerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ITEM_CONVEYOR_BELT_MERGER_ENTITY, blockPos, blockState);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("current_input_index", currentInputIndex);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        currentInputIndex = nbt.getInt("current_input_index");
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltMergerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(level.getTime() % TICKS_PER_ITEM == 0) {
            Direction facing = state.get(ItemConveyorBeltMergerBlock.FACING);

            BlockPos outputPos = blockPos.offset(facing);
            BlockState outputBlockState = level.getBlockState(outputPos);
            if(!outputBlockState.isOf(ModBlocks.ITEM_CONVEYOR_BELT))
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
                if(!inputBlockState.isOf(ModBlocks.ITEM_CONVEYOR_BELT))
                    continue;

                //Conveyor belt must face towards Merger and must not be ascending
                ModBlockStateProperties.ConveyorBeltDirection inputBeltFacing = inputBlockState.get(ItemConveyorBeltBlock.FACING);
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