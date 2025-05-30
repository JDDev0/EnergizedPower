package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltMergerBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.config.ModConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class ItemConveyorBeltMergerBlockEntity extends BlockEntity {
    private static final int TICKS_PER_ITEM = ModConfigs.COMMON_ITEM_CONVEYOR_BELT_MERGER_TICKS_PER_ITEM.getValue();

    private int currentInputIndex;

    public ItemConveyorBeltMergerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EPBlockEntities.ITEM_CONVEYOR_BELT_MERGER_ENTITY.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.putInt("current_input_index", currentInputIndex);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        currentInputIndex = nbt.getIntOr("current_input_index", 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltMergerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(level.getGameTime() % TICKS_PER_ITEM == 0) {
            Direction facing = state.getValue(ItemConveyorBeltMergerBlock.FACING);

            BlockPos outputPos = blockPos.relative(facing);
            BlockState outputBlockState = level.getBlockState(outputPos);
            if(!outputBlockState.is(EPBlocks.ITEM_CONVEYOR_BELT.get()))
                return;

            BlockEntity outputBlockEntity = level.getBlockEntity(outputPos);
            if(!(outputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                return;

            IItemHandler outputBeltItemStackStorage = level.getCapability(Capabilities.ItemHandler.BLOCK, outputPos,
                    outputBlockState, outputBlockEntity, facing.getOpposite());
            if(outputBeltItemStackStorage == null)
                return;

            for(int j = 0;j < 3;j++) {
                int index = (blockEntity.currentInputIndex + j) % 3;
                Direction inputDirection = switch(index) {
                    case 0 -> facing.getClockWise();
                    case 1 -> facing.getOpposite();
                    case 2 -> facing.getCounterClockWise();
                    default -> null;
                };

                if(inputDirection == null)
                    return;

                BlockPos inputPos = blockPos.relative(inputDirection);
                BlockState inputBlockState = level.getBlockState(inputPos);
                if(!inputBlockState.is(EPBlocks.ITEM_CONVEYOR_BELT.get()))
                    continue;

                //Conveyor belt must face towards Merger and must not be ascending
                EPBlockStateProperties.ConveyorBeltDirection inputBeltFacing = inputBlockState.getValue(ItemConveyorBeltBlock.FACING);
                if(inputBeltFacing.isAscending() || inputBeltFacing.getDirection().getOpposite() != inputDirection)
                    continue;

                BlockEntity inputBlockEntity = level.getBlockEntity(inputPos);
                if(!(inputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                    continue;

                IItemHandler inputBeltItemStackStorage = level.getCapability(Capabilities.ItemHandler.BLOCK, inputPos,
                        inputBlockState, inputBlockEntity, inputDirection.getOpposite());
                if(inputBeltItemStackStorage == null)
                    continue;

                ItemStack itemStackToSwitch = inputBeltItemStackStorage.getStackInSlot(inputBeltItemStackStorage.getSlots() - 1);
                if(itemStackToSwitch.isEmpty())
                    continue;

                for(int i = 0;i < outputBeltItemStackStorage.getSlots();i++) {
                    if(outputBeltItemStackStorage.insertItem(i, itemStackToSwitch, false).isEmpty()) {
                        inputBeltItemStackStorage.extractItem(inputBeltItemStackStorage.getSlots() - 1, 1, false);

                        blockEntity.currentInputIndex = (index + 1) % 3;
                        setChanged(level, blockPos, state);

                        return;
                    }
                }
            }
        }
    }
}