package me.jddev0.ep.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeleporterMatrixItem extends Item {
    public TeleporterMatrixItem(FabricItemSettings props) {
        super(props);
    }

    public static boolean isLinked(ItemStack itemStack) {
        if(!itemStack.hasNbt())
            return false;

        NbtCompound nbt = itemStack.getNbt();
        return nbt != null && nbt.contains("x", NbtElement.INT_TYPE) && nbt.contains("y", NbtElement.INT_TYPE) &&
                nbt.contains("z", NbtElement.INT_TYPE) && nbt.contains("dim", NbtElement.STRING_TYPE);
    }

    public static BlockPos getBlockPos(World level, ItemStack itemStack) {
        if(level.isClient())
            return null;

        if(!isLinked(itemStack))
            return null;

        NbtCompound nbt = itemStack.getNbt();
        if(nbt == null)
            return null;

        return new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
    }

    public static World getDimension(World level, ItemStack itemStack) {
        if(level.isClient() || !(level instanceof ServerWorld))
            return null;

        if(!isLinked(itemStack))
            return null;

        NbtCompound nbt = itemStack.getNbt();
        if(nbt == null)
            return null;

        Identifier dimensionId = Identifier.tryParse(nbt.getString("dim"));
        if(dimensionId == null)
            return null;

        RegistryKey<World> dimensionKey = RegistryKey.of(RegistryKeys.WORLD, dimensionId);
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

        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt("x", blockPos.getX());
        nbt.putInt("y", blockPos.getY());
        nbt.putInt("z", blockPos.getZ());

        nbt.putString("dim", level.getRegistryKey().getValue().toString());

        //TODO check if block is teleporter block
        if(/*!(block instanceof TeleporterBlock)*/false) {
            if(player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.teleporter_matrix.set.warning").
                                formatted(Formatting.YELLOW)
                ));
            }
        }else {
            if(player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.teleporter_matrix.set").
                                formatted(Formatting.GREEN)
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

        NbtCompound nbt = itemStack.getNbt();
        if(nbt != null) {
            nbt.remove("x");
            nbt.remove("y");
            nbt.remove("z");

            nbt.remove("dim");
        }

        if(player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                    Text.translatable("tooltip.energizedpower.teleporter_matrix.cleared").
                            formatted(Formatting.GREEN)
            ));
        }

        return TypedActionResult.success(itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound nbt = stack.getNbt();
        boolean linked = isLinked(stack);


        tooltip.add(Text.translatable("tooltip.energizedpower.teleporter_matrix.status").formatted(Formatting.GRAY).
                append(Text.translatable("tooltip.energizedpower.teleporter_matrix.status." +
                        (linked?"linked":"unlinked")).formatted(linked?Formatting.GREEN:Formatting.RED)));

        if(linked) {
            tooltip.add(Text.empty());

            tooltip.add(Text.translatable("tooltip.energizedpower.teleporter_matrix.location").
                   append(Text.literal(nbt.getInt("x") + " " + nbt.getInt("y") +
                           " " + nbt.getInt("z"))));
            tooltip.add(Text.translatable("tooltip.energizedpower.teleporter_matrix.dimension").
                   append(Text.literal(nbt.getString("dim"))));
        }

        tooltip.add(Text.empty());

        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.energizedpower.teleporter_matrix.txt.shift").
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
