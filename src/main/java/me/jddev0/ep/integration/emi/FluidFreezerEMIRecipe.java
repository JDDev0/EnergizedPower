package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.recipe.FluidFreezerRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class FluidFreezerEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/fluid_freezer_front.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.FLUID_FREEZER_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("fluid_freezer"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public FluidFreezerEMIRecipe(RecipeHolder<FluidFreezerRecipe> recipe) {
        this.id = recipe.id();

        List<FluidStack> rawFluids = recipe.value().getInput().map(
                fluid -> List.of(fluid),
                f -> f.fluid().getFluid().map(fluid -> fluid,
                                fluid -> Minecraft.getInstance().level.registryAccess().lookupOrThrow(BuiltInRegistries.FLUID.key()).
                                        getOrThrow(fluid).stream().map(Holder::value).toList()).stream().
                        map(fluid -> new FluidStack(fluid, f.dropletsAmount())).toList()
        );
        List<EmiStack> fluids = new ArrayList<>();
        for(FluidStack fluid:rawFluids)
            fluids.add(EmiStack.of(fluid.getFluid(), fluid.getFluidVariant().getComponents(), fluid.getDropletsAmount()));

        this.input = List.of(
                EmiIngredient.of(fluids)
        );
        this.output = List.of(
                EmiStack.of(recipe.value().getOutput())
        );
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
        return 82;
    }

    @Override
    public int getDisplayHeight() {
        return 25;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 23, 4);

        widgets.addSlot(input.get(0), 0, 4);

        widgets.addSlot(output.get(0), 61, 4).recipeContext(this);
    }
}
