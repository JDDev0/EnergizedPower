package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ItemConveyorBeltBlock;
import me.jddev0.ep.block.ItemConveyorBeltSwitchBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.tier.ConveyorBeltTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

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

    public static void tick(Level level, BlockPos blockPos, BlockState state, ItemConveyorBeltSwitchBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(level.getGameTime() % blockEntity.ticksPerItem == 0) {
            Direction facing = state.getValue(ItemConveyorBeltSwitchBlock.FACING);

            BlockPos inputPos = blockPos.relative(facing);
            BlockState inputBlockState = level.getBlockState(inputPos);
            if(!(inputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
                return;

            //Conveyor belt must face towards Switch and must not be ascending
            EPBlockStateProperties.ConveyorBeltDirection inputBeltFacing = inputBlockState.getValue(ItemConveyorBeltBlock.FACING);
            if(inputBeltFacing.isAscending() || inputBeltFacing.getDirection().getOpposite() != facing)
                return;

            BlockEntity inputBlockEntity = level.getBlockEntity(inputPos);
            if(!(inputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                return;

            ResourceHandler<ItemResource> inputBeltItemStackStorage = level.getCapability(Capabilities.Item.BLOCK, inputPos,
                    inputBlockState, inputBlockEntity, facing.getOpposite());
            if(inputBeltItemStackStorage == null)
                return;

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
                return;

            boolean isPowered = state.getValue(ItemConveyorBeltSwitchBlock.POWERED);
            Direction outputDirection = isPowered?facing.getCounterClockWise():facing.getClockWise();

            BlockPos outputPos = blockPos.relative(outputDirection);
            BlockState outputBlockState = level.getBlockState(outputPos);
            if(!(outputBlockState.getBlock() instanceof ItemConveyorBeltBlock))
                return;

            BlockEntity outputBlockEntity = level.getBlockEntity(outputPos);
            if(!(outputBlockEntity instanceof ItemConveyorBeltBlockEntity))
                return;

            ResourceHandler<ItemResource> outputBeltItemStackStorage = level.getCapability(Capabilities.Item.BLOCK, outputPos,
                    outputBlockState, outputBlockEntity, outputDirection.getOpposite());
            if(outputBeltItemStackStorage == null)
                return;

            try(Transaction transaction = Transaction.open(null)) {
                long amount = outputBeltItemStackStorage.insert(ItemResource.of(itemStackToSwitch), 1, transaction);
                if(amount > 0)
                    inputBeltItemStackStorage.extract(ItemResource.of(itemStackToSwitch),1, transaction);

                transaction.commit();
            }
        }
    }
}