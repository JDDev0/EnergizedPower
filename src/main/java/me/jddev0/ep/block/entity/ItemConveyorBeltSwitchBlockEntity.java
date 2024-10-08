package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltSwitchBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.config.ModConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemConveyorBeltSwitchBlockEntity extends BlockEntity {
    private static final int TICKS_PER_ITEM = ModConfigs.COMMON_ITEM_CONVEYOR_BELT_SWITCH_TICKS_PER_ITEM.getValue();

    public ItemConveyorBeltSwitchBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EPBlockEntities.ITEM_CONVEYOR_BELT_SWITCH_ENTITY.get(), blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltSwitchBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(level.getGameTime() % TICKS_PER_ITEM == 0) {
            Direction facing = state.getValue(ItemConveyorBeltSwitchBlock.FACING);

            BlockPos inputPos = blockPos.relative(facing);
            BlockState inputBlockState = level.getBlockState(inputPos);
            if(!inputBlockState.is(EPBlocks.ITEM_CONVEYOR_BELT.get()))
                return;

            //Conveyor belt must face towards Switch and must not be ascending
            EPBlockStateProperties.ConveyorBeltDirection inputBeltFacing = inputBlockState.getValue(ItemConveyorBeltBlock.FACING);
            if(inputBeltFacing.isAscending() || inputBeltFacing.getDirection().getOpposite() != facing)
                return;

            BlockEntity inputBlockEntity = level.getBlockEntity(inputPos);
            if(!(inputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                return;

            IItemHandler inputBeltItemStackStorage = level.getCapability(Capabilities.ItemHandler.BLOCK, inputPos,
                    inputBlockState, inputBlockEntity, facing.getOpposite());
            if(inputBeltItemStackStorage == null)
                return;

            ItemStack itemStackToSwitch = inputBeltItemStackStorage.getStackInSlot(inputBeltItemStackStorage.getSlots() - 1);
            if(itemStackToSwitch.isEmpty())
                return;

            boolean isPowered = state.getValue(ItemConveyorBeltSwitchBlock.POWERED);
            Direction outputDirection = isPowered?facing.getCounterClockWise():facing.getClockWise();

            BlockPos outputPos = blockPos.relative(outputDirection);
            BlockState outputBlockState = level.getBlockState(outputPos);
            if(!outputBlockState.is(EPBlocks.ITEM_CONVEYOR_BELT.get()))
                return;

            BlockEntity outputBlockEntity = level.getBlockEntity(outputPos);
            if(!(outputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                return;

            IItemHandler outputBeltItemStackStorage = level.getCapability(Capabilities.ItemHandler.BLOCK, outputPos,
                    outputBlockState, outputBlockEntity, outputDirection.getOpposite());
            if(outputBeltItemStackStorage == null)
                return;

            for(int i = 0;i < outputBeltItemStackStorage.getSlots();i++) {
                if(outputBeltItemStackStorage.insertItem(i, itemStackToSwitch, false).isEmpty()) {
                    inputBeltItemStackStorage.extractItem(inputBeltItemStackStorage.getSlots() - 1, 1, false);

                    return;
                }
            }
        }
    }
}