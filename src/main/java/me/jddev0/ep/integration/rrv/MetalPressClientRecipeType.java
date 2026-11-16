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

public class MetalPressClientRecipeType implements ReliableClientRecipeType {
    protected static final MetalPressClientRecipeType INSTANCE = new MetalPressClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.metal_press");
    }

    @Override
    public int getDisplayWidth() {
        return 104;
    }

    @Override
    public int getDisplayHeight() {
        return 40;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return EPAPI.id("textures/gui/recipe/rrv/metal_press.png");
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 4, 16);
        slotDefinition.addItemSlot(1, 40, 4);
        slotDefinition.addItemSlot(2, 80, 16);
    }

    @Override
    public Identifier getId() {
        return EPAPI.id("metal_press");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(EPBlocks.METAL_PRESS_ITEM.get());
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(
                new ItemStack(EPBlocks.METAL_PRESS_ITEM.get())
        );
    }
}
