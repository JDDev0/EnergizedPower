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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("current_output_index", currentOutputIndex);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        currentOutputIndex = view.getIntOr("current_output_index", 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltSplitterBlockEntity blockEntity) {
        if(level.isClientSide())
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
                        setChanged(level, blockPos, state);

                        return;
                    }
                }
            }
        }
    }
}