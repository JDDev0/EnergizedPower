package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltSwitchBlock;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ItemConveyorBeltSwitchBlockEntity extends BlockEntity {
    private final int ticksPerItem;

    private final ConveyorBeltTier tier;

    public ItemConveyorBeltSwitchBlockEntity(BlockPos blockPos, BlockState blockState, ConveyorBeltTier tier) {
        super(tier.getItemConveyorBeltSwitchBlockEntityFromTier(), blockPos, blockState);

        this.tier = tier;

        ticksPerItem = switch(tier) {
            case BASIC -> ModConfigs.COMMON_BASIC_ITEM_CONVEYOR_BELT_SWITCH_TICKS_PER_ITEM.getValue();
            case FAST -> ModConfigs.COMMON_FAST_ITEM_CONVEYOR_BELT_SWITCH_TICKS_PER_ITEM.getValue();
            case EXPRESS -> ModConfigs.COMMON_EXPRESS_ITEM_CONVEYOR_BELT_SWITCH_TICKS_PER_ITEM.getValue();
        };
    }

    public ConveyorBeltTier getTier() {
        return tier;
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ItemConveyorBeltSwitchBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(level.getTime() % blockEntity.ticksPerItem == 0) {
            Direction facing = state.get(ItemConveyorBeltSwitchBlock.FACING);

            BlockPos inputPos = blockPos.offset(facing);
            BlockState inputBlockState = level.getBlockState(inputPos);
            if(!(inputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
                return;

            //Conveyor belt must face towards Switch and must not be ascending
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

            boolean isPowered = state.get(ItemConveyorBeltSwitchBlock.POWERED);
            Direction outputDirection = isPowered?facing.rotateYCounterclockwise():facing.rotateYClockwise();

            BlockPos outputPos = blockPos.offset(outputDirection);
            BlockState outputBlockState = level.getBlockState(outputPos);
            if(!(outputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
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