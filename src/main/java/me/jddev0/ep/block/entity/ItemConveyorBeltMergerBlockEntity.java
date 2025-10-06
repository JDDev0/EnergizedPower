package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltMergerBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

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
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("current_input_index", currentInputIndex);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        currentInputIndex = view.getIntOr("current_input_index", 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltMergerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(level.getGameTime() % blockEntity.ticksPerItem == 0) {
            Direction facing = state.getValue(ItemConveyorBeltMergerBlock.FACING);

            BlockPos outputPos = blockPos.relative(facing);
            BlockState outputBlockState = level.getBlockState(outputPos);
            if(!(outputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
                return;

            BlockEntity outputBlockEntity = level.getBlockEntity(outputPos);
            if(!(outputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                return;

            ResourceHandler<ItemResource> outputBeltItemStackStorage = level.getCapability(Capabilities.Item.BLOCK, outputPos,
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
                if(!(inputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
                    continue;

                //Conveyor belt must face towards Merger and must not be ascending
                EPBlockStateProperties.ConveyorBeltDirection inputBeltFacing = inputBlockState.getValue(ItemConveyorBeltBlock.FACING);
                if(inputBeltFacing.isAscending() || inputBeltFacing.getDirection().getOpposite() != inputDirection)
                    continue;

                BlockEntity inputBlockEntity = level.getBlockEntity(inputPos);
                if(!(inputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                    continue;

                ResourceHandler<ItemResource> inputBeltItemStackStorage = level.getCapability(Capabilities.Item.BLOCK, inputPos,
                        inputBlockState, inputBlockEntity, inputDirection.getOpposite());
                if(inputBeltItemStackStorage == null)
                    continue;

                ItemStack itemStackToSwitch = ItemStack.EMPTY;
                try(Transaction transaction = Transaction.open(null)) {
                    for(int i = 0;i < inputBeltItemStackStorage.size();i++) {
                        if(inputBeltItemStackStorage.getResource(i).isEmpty())
                            continue;

                        ItemResource itemResource = inputBeltItemStackStorage.getResource(i);
                        int amount = inputBeltItemStackStorage.extract(itemResource, 1, transaction);
                        if(amount > 0) {
                            itemStackToSwitch = itemResource.toStack(1);

                            break;
                        }
                    }
                }
                if(itemStackToSwitch.isEmpty())
                    continue;

                try(Transaction transaction = Transaction.open(null)) {
                    int amount = outputBeltItemStackStorage.insert(ItemResource.of(itemStackToSwitch), 1, transaction);
                    if(amount > 0)
                        inputBeltItemStackStorage.extract(ItemResource.of(itemStackToSwitch), 1, transaction);

                    transaction.commit();

                    if(amount > 0) {
                        blockEntity.currentInputIndex = (index + 1) % 3;
                        setChanged(level, blockPos, state);

                        return;
                    }
                }
            }
        }
    }
}