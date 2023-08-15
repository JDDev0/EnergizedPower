package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

import java.util.List;

public class InWorldEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = new Identifier("textures/block/grass_block_side.png");
    public static final EmiStack ITEM = EmiStack.of(Items.GRASS_BLOCK);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(new Identifier(EnergizedPowerMod.MODID, "in_world_crafting"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public InWorldEMIRecipe(InWorldRecipe recipe) {
        this.id = recipe.id();
        this.input = List.of(EmiIngredient.of(recipe.tool()), EmiIngredient.of(recipe.block()));
        this.output = List.of(EmiStack.of(recipe.output()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public Identifier getId() {
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

    record InWorldRecipe(Identifier id, Ingredient tool, Ingredient block, ItemStack output) {}
}
