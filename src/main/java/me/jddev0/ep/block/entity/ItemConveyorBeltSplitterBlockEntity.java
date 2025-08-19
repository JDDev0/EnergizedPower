package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltSplitterBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
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
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.putInt("current_output_index", currentOutputIndex);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        currentOutputIndex = nbt.getInt("current_output_index");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltSplitterBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(level.getGameTime() % blockEntity.ticksPerItem == 0) {
            Direction facing = state.getValue(ItemConveyorBeltSplitterBlock.FACING);

            BlockPos inputPos = blockPos.relative(facing);
            BlockState inputBlockState = level.getBlockState(inputPos);
            if(!(inputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
                return;

            //Conveyor belt must face towards Splitter and must not be ascending
            EPBlockStateProperties.ConveyorBeltDirection inputBeltFacing = inputBlockState.getValue(ItemConveyorBeltBlock.FACING);
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
                if(!(outputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
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