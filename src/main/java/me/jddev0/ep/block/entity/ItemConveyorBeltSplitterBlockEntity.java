package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltSwitchBlock;
import me.jddev0.ep.block.ModBlockStateProperties;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.config.ModConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class ItemConveyorBeltSplitterBlockEntity extends BlockEntity {
    private static final int TICKS_PER_ITEM = ModConfigs.COMMON_ITEM_CONVEYOR_BELT_SPLITTER_TICKS_PER_ITEM.getValue();

    private int currentOutputIndex;

    public ItemConveyorBeltSplitterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ITEM_CONVEYOR_BELT_SPLITTER_ENTITY.get(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putInt("current_output_index", currentOutputIndex);

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        currentOutputIndex = nbt.getInt("current_output_index");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltSplitterBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(level.getGameTime() % TICKS_PER_ITEM == 0) {
            Direction facing = state.getValue(ItemConveyorBeltSwitchBlock.FACING);

            BlockPos inputPos = blockPos.relative(facing);
            BlockState inputBlockState = level.getBlockState(inputPos);
            if(!inputBlockState.is(ModBlocks.ITEM_CONVEYOR_BELT.get()))
                return;

            //Conveyor belt must face towards Splitter and must not be ascending
            ModBlockStateProperties.ConveyorBeltDirection inputBeltFacing = inputBlockState.getValue(ItemConveyorBeltBlock.FACING);
            if(inputBeltFacing.isAscending() || inputBeltFacing.getDirection().getOpposite() != facing)
                return;

            BlockEntity inputBlockEntity = level.getBlockEntity(inputPos);
            if(!(inputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                return;

            LazyOptional<IItemHandler> inputItemStackStorageLazyOptional = inputBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, facing.getOpposite());
            if(!inputItemStackStorageLazyOptional.isPresent())
                return;

            IItemHandler inputBeltItemStackStorage = inputItemStackStorageLazyOptional.orElseGet(null);
            ItemStack itemStackToSwitch = inputBeltItemStackStorage.getStackInSlot(inputBeltItemStackStorage.getSlots() - 1);
            if(itemStackToSwitch.isEmpty())
                return;

            for(int j = 0;j < 3;j++) {
                int index = (blockEntity.currentOutputIndex + j) % 3;
                Direction outputDirection = switch(index) {
                    case 0 -> facing.getClockWise();
                    case 1 -> facing.getOpposite();
                    case 2 -> facing.getCounterClockWise();
                    default -> null;
                };

                if(outputDirection == null)
                    return;

                BlockPos outputPos = blockPos.relative(outputDirection);
                BlockState outputBlockState = level.getBlockState(outputPos);
                if(!outputBlockState.is(ModBlocks.ITEM_CONVEYOR_BELT.get()))
                    continue;

                BlockEntity outputBlockEntity = level.getBlockEntity(outputPos);
                if(!(outputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                    continue;

                LazyOptional<IItemHandler> outputItemStackStorageLazyOptional = outputBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, outputDirection.getOpposite());
                if(!outputItemStackStorageLazyOptional.isPresent())
                    continue;

                IItemHandler outputBeltItemStackStorage = outputItemStackStorageLazyOptional.orElse(null);
                for(int i = 0;i < outputBeltItemStackStorage.getSlots();i++) {
                    if(outputBeltItemStackStorage.insertItem(i, itemStackToSwitch, false).isEmpty()) {
                        inputBeltItemStackStorage.extractItem(inputBeltItemStackStorage.getSlots() - 1, 1, false);

                        blockEntity.currentOutputIndex = (index + 1) % 3;
                        setChanged(level, blockPos, state);

                        return;
                    }
                }
            }
        }
    }
}