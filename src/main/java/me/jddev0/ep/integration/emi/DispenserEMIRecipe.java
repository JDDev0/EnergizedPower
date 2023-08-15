package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class DispenserEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = new ResourceLocation("textures/block/dispenser_front.png");
    public static final EmiStack ITEM = EmiStack.of(Items.DISPENSER);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(new ResourceLocation(EnergizedPowerMod.MODID, "dispenser"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public DispenserEMIRecipe(DispenserRecipe recipe) {
        this.id = recipe.id();
        this.input = List.of(EmiIngredient.of(recipe.tool()), EmiIngredient.of(recipe.block()));
        this.output = List.of(EmiStack.of(recipe.output()));
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

    record DispenserRecipe(ResourceLocation id, Ingredient tool, Ingredient block, ItemStack output) {}
}
