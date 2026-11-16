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

public class PlantGrowthChamberFertilizerClientRecipeType implements ReliableClientRecipeType {
    protected static final PlantGrowthChamberFertilizerClientRecipeType INSTANCE = new PlantGrowthChamberFertilizerClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("recipes.energizedpower.plant_growth_chamber_fertilizer");
    }

    @Override
    public int getDisplayWidth() {
        return 150;
    }

    @Override
    public int getDisplayHeight() {
        return 66;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return EPAPI.id("textures/gui/recipe/rrv/plant_growth_chamber_fertilizer.png");
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 4, 4);
    }

    @Override
    public Identifier getId() {
        return EPAPI.id("plant_growth_chamber_fertilizer");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(
                new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM)
        );
    }
}
