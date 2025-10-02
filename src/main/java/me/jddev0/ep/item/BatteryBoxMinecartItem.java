package me.jddev0.ep.item;

import me.jddev0.ep.entity.MinecartBatteryBox;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.function.Consumer;

public class BatteryBoxMinecartItem extends Item {
    private static final ItemDispenserBehavior DISPENSE_ITEM_BEHAVIOR = new ItemDispenserBehavior() {
        private static final ItemDispenserBehavior DEFAULT_ITEM_BEHAVIOR = new ItemDispenserBehavior();

        @Override
        public ItemStack dispenseSilently(BlockPointer blockSource, ItemStack itemStack) {
            Direction direction = blockSource.state().get(DispenserBlock.FACING);
            World level = blockSource.world();
            double xOffset = blockSource.centerPos().getX() + direction.getOffsetX() * 1.125;
            double yOffset = Math.floor(blockSource.centerPos().getY()) + direction.getOffsetY();
            double zOffset = blockSource.centerPos().getZ() + direction.getOffsetZ() * 1.125;
            BlockPos blockPos = blockSource.pos().offset(direction);
            BlockState blockState = level.getBlockState(blockPos);
            RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock?
                    blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty()):
                    RailShape.NORTH_SOUTH;

            double additionalYOffset;
            if(blockState.isIn(BlockTags.RAILS)) {
                additionalYOffset = railShape.isAscending()?.6:.1;
            }else {
                if(!blockState.isAir() || !level.getBlockState(blockPos.down()).isIn(BlockTags.RAILS))
                    return DEFAULT_ITEM_BEHAVIOR.dispense(blockSource, itemStack);

                BlockState blockStateBelow = level.getBlockState(blockPos.down());
                RailShape railShapeBelow = blockStateBelow.getBlock() instanceof AbstractRailBlock?
                        blockStateBelow.get(((AbstractRailBlock)blockStateBelow.getBlock()).getShapeProperty()):
                        RailShape.NORTH_SOUTH;

                additionalYOffset = direction != Direction.DOWN && railShapeBelow.isAscending()?-.4:-.9;
            }

            MinecartBatteryBox minecartBatteryBox = new MinecartBatteryBox(level, xOffset,
                    yOffset + additionalYOffset, zOffset);
            if(itemStack.contains(DataComponentTypes.CUSTOM_NAME))
                minecartBatteryBox.setCustomName(itemStack.getName());

            level.spawnEntity(minecartBatteryBox);
            itemStack.decrement(1);
            return itemStack;
        }
    };

    public BatteryBoxMinecartItem(Item.Settings props) {
        super(props);

        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
        if(MinecraftClient.getInstance().isShiftPressed()) {
            tooltip.accept(Text.translatable("tooltip.energizedpower.capacity.txt",
                            EnergyUtils.getEnergyWithPrefix(MinecartBatteryBox.CAPACITY)).
                    formatted(Formatting.GRAY));
            tooltip.accept(Text.translatable("tooltip.energizedpower.transfer_rate.txt",
                            EnergyUtils.getEnergyWithPrefix(MinecartBatteryBox.MAX_TRANSFER)).
                    formatted(Formatting.GRAY));
        }else {
            tooltip.accept(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext useOnContext) {
        World level = useOnContext.getWorld();
        BlockPos blockPos = useOnContext.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.isIn(BlockTags.RAILS))
            return ActionResult.FAIL;

        ItemStack itemStack = useOnContext.getStack();
        if(!level.isClient()) {
            RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock ?
                    blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty()):
                    RailShape.NORTH_SOUTH;
            double yOffset = railShape.isAscending()?.5:0.;

            MinecartBatteryBox minecartBatteryBox = new MinecartBatteryBox(level, blockPos.getX() + .5,
                    blockPos.getY() + .0625 + yOffset, blockPos.getZ() + .5);
            if(itemStack.contains(DataComponentTypes.CUSTOM_NAME))
                minecartBatteryBox.setCustomName(itemStack.getName());

            level.spawnEntity(minecartBatteryBox);
            level.emitGameEvent(GameEvent.ENTITY_PLACE, blockPos, GameEvent.Emitter.of(useOnContext.getPlayer(),
                    level.getBlockState(blockPos.down())));
        }

        itemStack.decrement(1);
        return level.isClient()?ActionResult.SUCCESS:ActionResult.SUCCESS_SERVER;
    }
}
