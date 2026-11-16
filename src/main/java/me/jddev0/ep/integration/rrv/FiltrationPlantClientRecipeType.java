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

public class FiltrationPlantClientRecipeType implements ReliableClientRecipeType {
    protected static final FiltrationPlantClientRecipeType INSTANCE = new FiltrationPlantClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.filtration_plant");
    }

    @Override
    public int getDisplayWidth() {
        return 118;
    }

    @Override
    public int getDisplayHeight() {
        return 32;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return EPAPI.id("textures/gui/recipe/rrv/filtration_plant.png");
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 4, 8);
        slotDefinition.addItemSlot(1, 67, 8);
        slotDefinition.addItemSlot(2, 95, 8);
    }

    @Override
    public Identifier getId() {
        return EPAPI.id("filtration_plant");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(EPBlocks.FILTRATION_PLANT_ITEM);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(
                new ItemStack(EPBlocks.FILTRATION_PLANT_ITEM)
        );
    }
}
