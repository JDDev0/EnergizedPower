package me.jddev0.ep.item;

import me.jddev0.ep.client.util.EnergizedPowerBookClientHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class EnergizedPowerBookItem extends WrittenBookItem {
    public EnergizedPowerBookItem(Item.Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
        components.accept(Component.translatable("book.byAuthor", "JDDev0").withStyle(ChatFormatting.GRAY));
        components.accept(Component.translatable("book.generation.0").withStyle(ChatFormatting.GRAY));

        if(Minecraft.getInstance().hasShiftDown()) {
            components.accept(Component.translatable("tooltip.energizedpower.energized_power_book.txt.shift.1").
                    withStyle(ChatFormatting.GRAY));
        }else {
            components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(level.isClientSide())
            EnergizedPowerBookClientHelper.showBookViewScreen((Identifier)null);

        return (level.isClientSide()?InteractionResult.SUCCESS:InteractionResult.SUCCESS_SERVER).heldItemTransformedTo(itemStack);
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos blockPos, LivingEntity player) {
        if(!level.isClientSide())
            return false;

        EnergizedPowerBookClientHelper.showBookViewScreen(BuiltInRegistries.BLOCK.getKey(state.getBlock()));

        return false;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return false;
    }
}
