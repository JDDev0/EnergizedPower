package me.jddev0.ep.item;

import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergizedPowerBookItem extends WrittenBookItem {
    public EnergizedPowerBookItem(FabricItemSettings props) {
        super(props);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("book.byAuthor", "JDDev0").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("book.generation.0").formatted(Formatting.GRAY));
    }

    @Override
    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand interactionHand) {
        ItemStack itemStack = player.getStackInHand(interactionHand);

        if(level.isClient())
            showBookViewScreen();

        return TypedActionResult.success(itemStack, level.isClient());
    }

    @Environment(EnvType.CLIENT)
    private void showBookViewScreen() {
        MinecraftClient.getInstance().setScreen(new EnergizedPowerBookScreen());
    }

    @Override
    public boolean hasGlint(ItemStack itemStack) {
        return false;
    }
}
