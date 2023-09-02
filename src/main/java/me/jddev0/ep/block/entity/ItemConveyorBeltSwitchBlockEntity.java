package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltSwitchBlock;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemConveyorBeltSwitchBlockEntity extends BlockEntity {
    private static final int TICKS_PER_ITEM = ModConfigs.COMMON_ITEM_CONVEYOR_BELT_SWITCH_TICKS_PER_ITEM.getValue();

    public ItemConveyorBeltSwitchBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ITEM_CONVEYOR_BELT_SWITCH_ENTITY, blockPos, blockState);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltSwitchBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(level.getTime() % TICKS_PER_ITEM == 0) {
            Direction facing = state.get(ItemConveyorBeltSwitchBlock.FACING);

            BlockPos inputPos = blockPos.offset(facing);
            BlockState inputBlockState = level.getBlockState(inputPos);
            if(!inputBlockState.isOf(ModBlocks.ITEM_CONVEYOR_BELT))
                return;

            //Conveyor belt must face towards Switch and must not be ascending
            ModBlockStateProperties.ConveyorBeltDirection inputBeltFacing = inputBlockState.get(ItemConveyorBeltBlock.FACING);
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

            boolean isPowered = state.get(ItemConveyorBeltSwitchBlock.POWERED);
            Direction outputDirection = isPowered?facing.rotateYCounterclockwise():facing.rotateYClockwise();

            BlockPos outputPos = blockPos.offset(outputDirection);
            BlockState outputBlockState = level.getBlockState(outputPos);
            if(!outputBlockState.isOf(ModBlocks.ITEM_CONVEYOR_BELT))
                return;

            BlockEntity outputBlockEntity = level.getBlockEntity(outputPos);
            if(!(outputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                return;

            Storage<ItemVariant> outputBeltItemStackStorage = ItemStorage.SIDED.find(level, outputPos, outputDirection.getOpposite());
            if(outputBeltItemStackStorage == null || !outputBeltItemStackStorage.supportsInsertion())
                return;

            try(Transaction transaction = Transaction.openOuter()) {
                long amount = outputBeltItemStackStorage.insert(ItemVariant.of(itemStackToSwitch), 1, transaction);
                if(amount > 0)
                    inputBeltItemStackStorage.extract(ItemVariant.of(itemStackToSwitch),1, transaction);

                transaction.commit();
            }
        }
    }
}