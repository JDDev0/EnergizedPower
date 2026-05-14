package me.jddev0.ep.item;

import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

public class EnergizedPowerBookItem extends WrittenBookItem {
    public EnergizedPowerBookItem(Item.Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        tooltip.add(Component.translatable("book.byAuthor", "JDDev0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("book.generation.0").withStyle(ChatFormatting.GRAY));

        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.energizedpower.energized_power_book.txt.shift.1").
                    withStyle(ChatFormatting.GRAY));
        }else {
            tooltip.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(level.isClientSide())
            showBookViewScreen(null);

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos blockPos, Player player) {
        if(!level.isClientSide())
            return false;

        showBookViewScreen(BuiltInRegistries.BLOCK.getKey(state.getBlock()));

        return false;
    }

    @Environment(EnvType.CLIENT)
    private void showBookViewScreen(ResourceLocation openOnPageForBlock) {
        Minecraft.getInstance().setScreen(new EnergizedPowerBookScreen(openOnPageForBlock));
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return false;
    }
}
