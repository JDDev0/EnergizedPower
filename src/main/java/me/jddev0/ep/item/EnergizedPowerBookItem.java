package me.jddev0.ep.item;

import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergizedPowerBookItem extends WrittenBookItem {
    public EnergizedPowerBookItem(Item.Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        components.add(Component.translatable("book.byAuthor", "JDDev0").withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("book.generation.0").withStyle(ChatFormatting.GRAY));

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.energizedpower.energized_power_book.txt.shift.1").
                    withStyle(ChatFormatting.GRAY));
        }else {
            components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(level.isClientSide)
            showBookViewScreen(null);

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos blockPos, Player player) {
        if(!level.isClientSide)
            return false;

        showBookViewScreen(BuiltInRegistries.BLOCK.getKey(state.getBlock()));

        return false;
    }

    @OnlyIn(Dist.CLIENT)
    private void showBookViewScreen(ResourceLocation openOnPageForBlock) {
        Minecraft.getInstance().setScreen(new EnergizedPowerBookScreen(openOnPageForBlock));
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return false;
    }
}
