package me.jddev0.ep.integration.emi;

import com.mojang.datafixers.util.Either;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.recipe.FluidIngredientWithAmount;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class FluidTransposerEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/fluid_transposer_front.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.FLUID_TRANSPOSER_ITEM.get());
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("fluid_transposer"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final FluidTransposerBlockEntity.Mode mode;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final Either<FluidStack, FluidIngredientWithAmount> fluid;

    public FluidTransposerEMIRecipe(RecipeHolder<FluidTransposerRecipe> recipe) {
        this.id = recipe.id();

        this.mode = recipe.value().getMode();

        if(mode == FluidTransposerBlockEntity.Mode.EMPTYING) {
            this.input = List.of(
                    EmiIngredient.of(recipe.value().getInput())
            );
            this.output = List.of(
                    EmiStack.of(recipe.value().getOutput())
            );
        }else {
            this.input = List.of(
                    EmiIngredient.of(recipe.value().getInput())
            );
            this.output = List.of(
                    EmiStack.of(recipe.value().getOutput())
            );
        }

        this.fluid = recipe.value().getFluid();
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
        return 143;
    }

    @Override
    public int getDisplayHeight() {
        return 26;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        List<FluidStack> rawFluids = fluid.map(
                fluid -> List.of(fluid),
                f -> f.fluid().getFluid().map(fluid -> fluid,
                        fluid -> Minecraft.getInstance().level.registryAccess().lookupOrThrow(BuiltInRegistries.FLUID.key()).
                                getOrThrow(fluid).stream().map(Holder::value).toList()).stream().
                        map(fluid -> new FluidStack(fluid, f.amount())).toList()
        );
        List<EmiStack> fluids = new ArrayList<>();
        for(FluidStack fluid:rawFluids)
            fluids.add(EmiStack.of(fluid.getFluid(), fluid.getComponentsPatch(), fluid.getAmount()));

        if(mode == FluidTransposerBlockEntity.Mode.EMPTYING) {
            widgets.addTexture(EPAPI.id("textures/gui/recipe/misc_gui.png"),
                    0, 0, 143, 26, 1, 133);

            widgets.addSlot(input.get(0), 0, 4).drawBack(false);

            widgets.addSlot(output.get(0), 63, 4).drawBack(false).recipeContext(this);
            widgets.addSlot(EmiIngredient.of(fluids), 89, 4).drawBack(false).recipeContext(this);

            widgets.addTexture(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/bucket.png"),
                    120, 5, 16, 16, 0, 0, 16, 16, 16, 16);
        }else {
            widgets.addTexture(EPAPI.id("textures/gui/recipe/misc_gui.png"),
                    0, 0, 143, 26, 1, 161);

            widgets.addSlot(input.get(0), 0, 4).drawBack(false);
            widgets.addSlot(EmiIngredient.of(fluids), 18, 4).drawBack(false);

            widgets.addSlot(output.get(0), 89, 4).drawBack(false).recipeContext(this);

            widgets.addTexture(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/item/water_bucket.png"),
                    120, 5, 16, 16, 0, 0, 16, 16, 16, 16);
        }

        widgets.addTooltipText(List.of(Component.translatable("tooltip.energizedpower.fluid_transposer.mode." + mode.getSerializedName())),
                118, 3, 20, 20);
    }
}
