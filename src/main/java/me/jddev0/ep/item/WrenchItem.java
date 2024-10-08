package me.jddev0.ep.item;

import me.jddev0.ep.component.EPDataComponentTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import me.jddev0.ep.block.*;

import java.util.List;

public class WrenchItem extends Item {
    public WrenchItem(Item.Settings props) {
        super(props);
    }

    public static Direction getCurrentFace(ItemStack itemStack) {
        return itemStack.getOrDefault(EPDataComponentTypes.CURRENT_FACE, Direction.DOWN);
    }

    public static void cycleCurrentFace(ItemStack itemStack, ServerPlayerEntity player) {
        int diff = player.isSneaking() ? -1 : 1;
        Direction currentFace = getCurrentFace(itemStack);
        currentFace = Direction.values()[(currentFace.ordinal() + diff + Direction.values().length) %
                Direction.values().length];

        itemStack.set(EPDataComponentTypes.CURRENT_FACE, currentFace);

        player.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                Text.translatable("tooltip.energizedpower.wrench.select_face",
                        Text.translatable("tooltip.energizedpower.direction." + currentFace.asString()).
                                formatted(Formatting.WHITE, Formatting.BOLD)
                ).formatted(Formatting.GRAY)
        ));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext useOnContext) {
        World level = useOnContext.getWorld();
        if(level.isClient())
            return ActionResult.SUCCESS;

        PlayerEntity player = useOnContext.getPlayer();

        BlockPos blockPos = useOnContext.getBlockPos();
        BlockState state = level.getBlockState(blockPos);
        Block block = state.getBlock();
        if(!(block instanceof WrenchConfigurable wrenchConfigurableBlock)) {
            if(player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(
                        Text.translatable("tooltip.energizedpower.wrench.not_configurable").formatted(Formatting.RED)
                ));
            }

            return ActionResult.SUCCESS;
        }

        ItemStack itemStack = useOnContext.getStack();
        Direction currentFace = getCurrentFace(itemStack);

        return wrenchConfigurableBlock.onUseWrench(useOnContext, currentFace, player != null && player.isSneaking());
    }

    @Override
    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand interactionHand) {
        ItemStack itemStack = player.getStackInHand(interactionHand);

        if(level.isClient())
            return TypedActionResult.success(itemStack);

       cycleCurrentFace(itemStack, (ServerPlayerEntity)player);

        return TypedActionResult.success(itemStack);
    }

    @Override
    public float getMiningSpeed(ItemStack itemStack, BlockState blockState) {
        //Allow current face swap in survival in a reasonable amount of time
        return 1000.f;
    }

    @Override
    public boolean canMine(BlockState state, World level, BlockPos blockPos, PlayerEntity player) {
        if(level.isClient() || !(player instanceof ServerPlayerEntity))
            return false;

        ItemStack itemStack = player.getMainHandStack();

        if(itemStack.contains(EPDataComponentTypes.ACTION_COOLDOWN))
            return false;

        cycleCurrentFace(itemStack, (ServerPlayerEntity)player);

        itemStack.set(EPDataComponentTypes.ACTION_COOLDOWN, 5);

        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        Direction currentFace = getCurrentFace(stack);
        tooltip.add(Text.translatable("tooltip.energizedpower.wrench.select_face",
                Text.translatable("tooltip.energizedpower.direction." + currentFace.asString()).
                        formatted(Formatting.WHITE, Formatting.BOLD)
        ).formatted(Formatting.GRAY));

        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.energizedpower.wrench.txt.shift").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
        }else {
            tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemStack, level, entity, slot, selected);

        if(level.isClient())
            return;

        if(!(entity instanceof PlayerEntity))
            return;

        if(itemStack.contains(EPDataComponentTypes.ACTION_COOLDOWN)) {
            int attackingCycleCooldown = itemStack.getOrDefault(EPDataComponentTypes.ACTION_COOLDOWN, 0) - 1;
            if(attackingCycleCooldown <= 0)
                itemStack.remove(EPDataComponentTypes.ACTION_COOLDOWN);
            else
                itemStack.set(EPDataComponentTypes.ACTION_COOLDOWN, attackingCycleCooldown);
        }
    }
}
