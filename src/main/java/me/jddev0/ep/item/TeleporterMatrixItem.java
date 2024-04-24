package me.jddev0.ep.item;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.component.DimensionalPositionComponent;
import me.jddev0.ep.component.ModDataComponentTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
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

public class TeleporterMatrixItem extends Item {
    public TeleporterMatrixItem(Item.Settings props) {
        super(props);
    }

    public static boolean isLinked(ItemStack itemStack) {
        return itemStack.contains(ModDataComponentTypes.DIMENSIONAL_POSITION);
    }

    public static BlockPos getBlockPos(World level, ItemStack itemStack) {
        if(level.isClient())
            return null;

        if(!isLinked(itemStack))
            return null;

        DimensionalPositionComponent dimPos = itemStack.get(ModDataComponentTypes.DIMENSIONAL_POSITION);
        if(dimPos == null)
            return null;

        return new BlockPos(dimPos.x(), dimPos.y(), dimPos.z());
    }

    public static World getDimension(World level, ItemStack itemStack) {
        if(level.isClient() || !(level instanceof ServerWorld))
            return null;

        if(!isLinked(itemStack))
            return null;

        DimensionalPositionComponent dimPos = itemStack.get(ModDataComponentTypes.DIMENSIONAL_POSITION);
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
        itemStack.set(ModDataComponentTypes.DIMENSIONAL_POSITION,
                new DimensionalPositionComponent(blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                        level.getRegistryKey().getValue()));

        if(state.isOf(ModBlocks.TELEPORTER)) {
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
    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand interactionHand) {
        ItemStack itemStack = player.getStackInHand(interactionHand);

        if(level.isClient())
            return TypedActionResult.success(itemStack);

        if(itemStack.contains(ModDataComponentTypes.DIMENSIONAL_POSITION))
            itemStack.remove(ModDataComponentTypes.DIMENSIONAL_POSITION);

        if(player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter_matrix.cleared").
                            formatted(Formatting.GREEN)
            ));
        }

        return TypedActionResult.success(itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        DimensionalPositionComponent dimPos = stack.get(ModDataComponentTypes.DIMENSIONAL_POSITION);
        boolean linked = isLinked(stack) && dimPos != null;


        tooltip.add(Text.translatable("tooltip.energizedpower.teleporter_matrix.status").formatted(Formatting.GRAY).
                append(Text.translatable("tooltip.energizedpower.teleporter_matrix.status." +
                        (linked?"linked":"unlinked")).formatted(linked?Formatting.GREEN:Formatting.RED)));

        if(linked) {
            tooltip.add(Text.empty());

            tooltip.add(Text.translatable("tooltip.energizedpower.teleporter_matrix.location").
                   append(Text.literal(dimPos.x() + " " + dimPos.y() + " " + dimPos.z())));
            tooltip.add(Text.translatable("tooltip.energizedpower.teleporter_matrix.dimension").
                   append(Text.literal(dimPos.dimensionId().toString())));
        }

        tooltip.add(Text.empty());

        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.energizedpower.teleporter_matrix.txt.shift.1").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
            tooltip.add(Text.translatable("tooltip.energizedpower.teleporter_matrix.txt.shift.2").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
        }else {
            tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    @Override
    public String getTranslationKey(ItemStack itemStack) {
        return getTranslationKey() + "." + (isLinked(itemStack)?"linked":"unlinked");
    }
}
