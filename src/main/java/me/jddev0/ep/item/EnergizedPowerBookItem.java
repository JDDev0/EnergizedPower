package me.jddev0.ep.item;

import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergizedPowerBookItem extends WrittenBookItem {
    public EnergizedPowerBookItem(Item.Properties props) {
        super(props);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        components.add(new TranslatableComponent("book.byAuthor", "JDDev0").withStyle(ChatFormatting.GRAY));
        components.add(new TranslatableComponent("book.generation.0").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(level.isClientSide)
            showBookViewScreen();

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @OnlyIn(Dist.CLIENT)
    private void showBookViewScreen() {
        Minecraft.getInstance().setScreen(new EnergizedPowerBookScreen());
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return false;
    }
}
