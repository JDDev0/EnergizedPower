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

public class EnergizerClientRecipeType implements ReliableClientRecipeType {
    protected static final EnergizerClientRecipeType INSTANCE = new EnergizerClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.energizer");
    }

    @Override
    public int getDisplayWidth() {
        return 120;
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return EPAPI.id("textures/gui/recipe/rrv/energizer.png");
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 20, 20);
        slotDefinition.addItemSlot(1, 96, 20);
    }

    @Override
    public Identifier getId() {
        return EPAPI.id("energizer");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(EPBlocks.ENERGIZER_ITEM.get());
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(
                new ItemStack(EPBlocks.ENERGIZER_ITEM.get())
        );
    }
}
