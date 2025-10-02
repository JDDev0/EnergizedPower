package me.jddev0.ep.item;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.component.DimensionalPositionComponent;
import me.jddev0.ep.component.EPDataComponentTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class TeleporterMatrixItem extends Item {
    public TeleporterMatrixItem(Item.Settings props) {
        super(props);
    }

    public static boolean isLinked(ItemStack itemStack) {
        return itemStack.contains(EPDataComponentTypes.DIMENSIONAL_POSITION);
    }

    public static BlockPos getBlockPos(World level, ItemStack itemStack) {
        if(level.isClient())
            return null;

        if(!isLinked(itemStack))
            return null;

        DimensionalPositionComponent dimPos = itemStack.get(EPDataComponentTypes.DIMENSIONAL_POSITION);
        if(dimPos == null)
            return null;

        return new BlockPos(dimPos.x(), dimPos.y(), dimPos.z());
    }

    public static World getDimension(World level, ItemStack itemStack) {
        if(level.isClient() || !(level instanceof ServerWorld))
            return null;

        if(!isLinked(itemStack))
            return null;

        DimensionalPositionComponent dimPos = itemStack.get(EPDataComponentTypes.DIMENSIONAL_POSITION);
        if(dimPos == null)
            return null;

        RegistryKey<World> dimensionKey = RegistryKey.of(RegistryKeys.WORLD, dimPos.dimensionId());
        return level.getServer().getWorld(dimensionKey);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext useOnContext) {
        World level = useOnContext.getWorld();
        if(level.isClient() || !(level instanceof ServerWorld))
            return ActionResult.SUCCESS;

        PlayerEntity player = useOnContext.getPlayer();

        BlockPos blockPos = useOnContext.getBlockPos();
        BlockState state = level.getBlockState(blockPos);
        Block block = state.getBlock();

        ItemStack itemStack = useOnContext.getStack();
        itemStack.set(EPDataComponentTypes.DIMENSIONAL_POSITION,
                new DimensionalPositionComponent(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                        level.getRegistryKey().getValue()));

        if(state.isOf(EPBlocks.TELEPORTER)) {
            if(player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.teleporter_matrix.set").
                                formatted(Formatting.GREEN)
                ));
            }
        }else {
            if(player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.teleporter_matrix.set.warning").
                                formatted(Formatting.YELLOW)
                ));
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult use(World level, PlayerEntity player, Hand interactionHand) {
        ItemStack itemStack = player.getStackInHand(interactionHand);

        if(level.isClient())
            return ActionResult.SUCCESS.withNewHandStack(itemStack);

        if(itemStack.contains(EPDataComponentTypes.DIMENSIONAL_POSITION))
            itemStack.remove(EPDataComponentTypes.DIMENSIONAL_POSITION);

        if(player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter_matrix.cleared").
                            formatted(Formatting.GREEN)
            ));
        }

        return ActionResult.SUCCESS.withNewHandStack(itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
        DimensionalPositionComponent dimPos = stack.get(EPDataComponentTypes.DIMENSIONAL_POSITION);
        boolean linked = isLinked(stack) && dimPos != null;


        tooltip.accept(Text.translatable("tooltip.energizedpower.teleporter_matrix.status").formatted(Formatting.GRAY).
                append(Text.translatable("tooltip.energizedpower.teleporter_matrix.status." +
                        (linked?"linked":"unlinked")).formatted(linked?Formatting.GREEN:Formatting.RED)));

        if(linked) {
            tooltip.accept(Text.empty());

            tooltip.accept(Text.translatable("tooltip.energizedpower.teleporter_matrix.location").
                   append(Text.literal(dimPos.x() + " " + dimPos.y() + " " + dimPos.z())));
            tooltip.accept(Text.translatable("tooltip.energizedpower.teleporter_matrix.dimension").
                   append(Text.literal(dimPos.dimensionId().toString())));
        }

        tooltip.accept(Text.empty());

        if(MinecraftClient.getInstance().isShiftPressed()) {
            tooltip.accept(Text.translatable("tooltip.energizedpower.teleporter_matrix.txt.shift.1").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
            tooltip.accept(Text.translatable("tooltip.energizedpower.teleporter_matrix.txt.shift.2").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
        }else {
            tooltip.accept(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    @Override
    public Text getName(ItemStack itemStack) {
        return Text.translatable(translationKey + "." + (isLinked(itemStack)?"linked":"unlinked"));
    }
}
