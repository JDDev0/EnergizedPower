package me.jddev0.ep.item;

import me.jddev0.ep.block.EPBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeleporterMatrixItem extends Item {
    public TeleporterMatrixItem(Properties props) {
        super(props);
    }

    public static boolean isLinked(ItemStack itemStack) {
        if(!itemStack.hasTag())
            return false;

        CompoundTag nbt = itemStack.getTag();
        return nbt != null && nbt.contains("x", Tag.TAG_INT) && nbt.contains("y", Tag.TAG_INT) &&
                nbt.contains("z", Tag.TAG_INT) && nbt.contains("dim", Tag.TAG_STRING);
    }

    public static BlockPos getBlockPos(Level level, ItemStack itemStack) {
        if(level.isClientSide)
            return null;

        if(!isLinked(itemStack))
            return null;

        CompoundTag nbt = itemStack.getTag();
        if(nbt == null)
            return null;

        return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }

    public static Level getDimension(Level level, ItemStack itemStack) {
        if(level.isClientSide || !(level instanceof ServerLevel))
            return null;

        if(!isLinked(itemStack))
            return null;

        CompoundTag nbt = itemStack.getTag();
        if(nbt == null)
            return null;

        ResourceLocation dimensionId = ResourceLocation.tryParse(nbt.getString("dim"));
        if(dimensionId == null)
            return null;

        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, dimensionId);
        return level.getServer().getLevel(dimensionKey);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if(level.isClientSide || !(level instanceof ServerLevel))
            return InteractionResult.SUCCESS;

        Player player = useOnContext.getPlayer();

        BlockPos blockPos = useOnContext.getClickedPos();
        BlockState state = level.getBlockState(blockPos);

        ItemStack itemStack = useOnContext.getItemInHand();

        CompoundTag nbt = itemStack.getOrCreateTag();
        nbt.putInt("x", blockPos.getX());
        nbt.putInt("y", blockPos.getY());
        nbt.putInt("z", blockPos.getZ());

        nbt.putString("dim", level.dimension().location().toString());

        if(state.is(EPBlocks.TELEPORTER.get())) {
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.teleporter_matrix.set").
                                withStyle(ChatFormatting.GREEN)
                ));
            }
        }else {
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.teleporter_matrix.set.warning").
                                withStyle(ChatFormatting.YELLOW)
                ));
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(level.isClientSide)
            return InteractionResultHolder.success(itemStack);

        CompoundTag nbt = itemStack.getTag();
        if(nbt != null) {
            nbt.remove("x");
            nbt.remove("y");
            nbt.remove("z");

            nbt.remove("dim");
        }

        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                    Component.translatable("tooltip.energizedpower.teleporter_matrix.cleared").
                            withStyle(ChatFormatting.GREEN)
            ));
        }

        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        CompoundTag nbt = itemStack.getTag();
        boolean linked = isLinked(itemStack);


        components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.status").withStyle(ChatFormatting.GRAY).
                append(Component.translatable("tooltip.energizedpower.teleporter_matrix.status." +
                        (linked?"linked":"unlinked")).withStyle(linked?ChatFormatting.GREEN:ChatFormatting.RED)));

        if(linked) {
            components.add(Component.empty());

           components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.location").
                   append(Component.literal(nbt.getInt("x") + " " + nbt.getInt("y") +
                           " " + nbt.getInt("z"))));
           components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.dimension").
                   append(Component.literal(nbt.getString("dim"))));
        }

        components.add(Component.empty());

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.txt.shift.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.txt.shift.2").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }else {
            components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        return getDescriptionId() + "." + (isLinked(itemStack)?"linked":"unlinked");
    }
}
