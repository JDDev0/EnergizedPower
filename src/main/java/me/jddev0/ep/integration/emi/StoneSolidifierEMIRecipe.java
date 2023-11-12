package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class StoneSolidifierEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/block/stone_solidifier_front.png");
    public static final EmiStack ITEM = EmiStack.of(ModBlocks.STONE_SOLIDIFIER_ITEM.get());
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(new ResourceLocation(EnergizedPowerMod.MODID, "stone_solidifier"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public StoneSolidifierEMIRecipe(RecipeHolder<StoneSolidifierRecipe> recipe) {
        this.id = recipe.id();
        this.input = List.of(
                EmiStack.of(Fluids.WATER, recipe.value().getWaterAmount()),
                EmiStack.of(Fluids.LAVA, recipe.value().getLavaAmount())
        );
        this.output = List.of(EmiStack.of(recipe.value().getOutput()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }

    @Override
    public int getDisplayWidth() {
        return 100;
    }

    @Override
    public int getDisplayHeight() {
        return 25;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 41, 4);

        widgets.addSlot(input.get(0), 0, 4);

        widgets.addSlot(input.get(1), 18, 4);

        widgets.addSlot(output.get(0), 79, 4).recipeContext(this);
    }
}
