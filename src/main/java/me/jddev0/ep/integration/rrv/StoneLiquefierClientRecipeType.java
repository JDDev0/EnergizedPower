package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class StoneLiquefierClientRecipeType implements ReliableClientRecipeType {
    protected static final StoneLiquefierClientRecipeType INSTANCE = new StoneLiquefierClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.stone_liquefier");
    }

    @Override
    public int getDisplayWidth() {
        return 91;
    }

    @Override
    public int getDisplayHeight() {
        return 32;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return EPAPI.id("textures/gui/recipe/rrv/stone_liquefier.png");
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 4, 8);
        slotDefinition.addItemSlot(1, 67, 8);
    }

    @Override
    public Identifier getId() {
        return EPAPI.id("stone_liquefier");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(EPBlocks.STONE_LIQUEFIER_ITEM.get());
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(
                new ItemStack(EPBlocks.STONE_LIQUEFIER_ITEM.get())
        );
    }
}
