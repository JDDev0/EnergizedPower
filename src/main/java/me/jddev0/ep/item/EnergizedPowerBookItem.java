package me.jddev0.ep.item;

import me.jddev0.ep.screen.EnergizedPowerBookScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EnergizedPowerBookItem extends WrittenBookItem {
    public EnergizedPowerBookItem(Item.Settings props) {
        super(props);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("book.byAuthor", "JDDev0").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("book.generation.0").formatted(Formatting.GRAY));

        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.energizedpower.energized_power_book.txt.shift.1").
                    formatted(Formatting.GRAY));
        }else {
            tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand interactionHand) {
        ItemStack itemStack = player.getStackInHand(interactionHand);

        if(level.isClient())
            showBookViewScreen(null);

        return TypedActionResult.success(itemStack, level.isClient());
    }

    @Override
    public float getMiningSpeed(ItemStack itemStack, BlockState blockState) {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public boolean canMine(BlockState state, World level, BlockPos blockPos, PlayerEntity player) {
        if(!level.isClient())
            return false;

        showBookViewScreen(Registries.BLOCK.getId(state.getBlock()));

        return false;
    }

    @Environment(EnvType.CLIENT)
    private void showBookViewScreen(Identifier openOnPageForBlock) {
        MinecraftClient.getInstance().setScreen(new EnergizedPowerBookScreen(openOnPageForBlock));
    }

    @Override
    public boolean hasGlint(ItemStack itemStack) {
        return false;
    }
}
