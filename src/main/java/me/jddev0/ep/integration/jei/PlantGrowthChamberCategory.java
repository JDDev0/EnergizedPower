package me.jddev0.ep.integration.jei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.PlantGrowthChamberBlockEntity;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import me.jddev0.ep.recipe.PlantGrowthChamberSoilRecipe;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.SoilType;
import me.jddev0.ep.util.FluidUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

import java.util.*;
import java.util.stream.Collectors;

public class PlantGrowthChamberCategory implements IRecipeCategory<RecipeHolder<PlantGrowthChamberRecipe>> {
    public static final RecipeType<RecipeHolder<PlantGrowthChamberRecipe>> TYPE = RecipeType.createFromVanilla(PlantGrowthChamberRecipe.Type.INSTANCE);

    private final IDrawable background;
    private final IDrawable icon;

    public PlantGrowthChamberCategory(IGuiHelper helper) {
        ResourceLocation texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        background = helper.createDrawable(texture, 116, 1, 139, 74);

        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get()));
    }

    @Override
    public RecipeType<RecipeHolder<PlantGrowthChamberRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.plant_growth_chamber");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, RecipeHolder<PlantGrowthChamberRecipe> recipe, IFocusGroup iFocusGroup) {
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 19, 1).addIngredients(recipe.value().getInput());

        List<Holder<SoilType>> soilTypes = recipe.value().getSoilType().map(
                soilType -> soilType.stream().
                        map(st -> Minecraft.getInstance().level.registryAccess().lookupOrThrow(EPRegistries.SOIL_TYPE).
                                getOrThrow(st)).
                        collect(Collectors.toUnmodifiableList()),
                soilType -> Minecraft.getInstance().level.registryAccess().lookupOrThrow(EPRegistries.SOIL_TYPE).
                            getOrThrow(soilType).stream().toList()
        );

        Collection<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipes = Minecraft.getInstance().level.getRecipeManager().
                getAllRecipesFor(PlantGrowthChamberSoilRecipe.Type.INSTANCE);

        IRecipeSlotBuilder soilSlot = iRecipeLayout.addSlot(RecipeIngredientRole.CATALYST, 19, 19);

        soilRecipes.stream().map(RecipeHolder::value).filter(soil -> soilTypes.stream().
                        anyMatch(soilType -> soilType.is(soil.getSoilType()))).
                forEach(soil -> soilSlot.addIngredients(soil.getInput()));

        IRecipeSlotBuilder fluidSlot = iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 10);
        for(Fluid fluid:recipe.value().getFluid())
            fluidSlot.addFluidStack(fluid, 1000);

        List<List<ItemStack>> outputSlotEntries = new ArrayList<>(4);
        for(int i = 0;i < 4;i++)
            outputSlotEntries.add(new ArrayList<>());

        ItemStack[] outputEntries = recipe.value().getMaxOutputCounts();
        for(int i = 0;i < outputEntries.length;i++)
            outputSlotEntries.get(i % 4).add(outputEntries[i].copyWithCount(1));

        IRecipeSlotRichTooltipCallback callback = (view, tooltip) -> {
            if(view.isEmpty())
                return;

            Optional<ItemStack> optionalItemStack = view.getDisplayedItemStack();
            if(optionalItemStack.isEmpty())
                return;

            tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

            OutputItemStackWithPercentages[] outputs = recipe.value().getOutputs();
            for(int i = 0;i < outputs.length;i++) {
                if(ItemStack.isSameItemSameComponents(optionalItemStack.get(), outputs[i].output())) {
                    double[] percentages = outputs[i].percentages();
                    for(int j = 0;j < percentages.length;j++)
                        tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

                    return;
                }
            }
        };

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 1).addItemStacks(outputSlotEntries.get(0)).
                addRichTooltipCallback(callback);
        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 91, 1).addItemStacks(outputSlotEntries.get(1)).
                addRichTooltipCallback(callback);
        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 73, 19).addItemStacks(outputSlotEntries.get(2)).
                addRichTooltipCallback(callback);
        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 91, 19).addItemStacks(outputSlotEntries.get(3)).
                addRichTooltipCallback(callback);
    }

    @Override
    public void draw(RecipeHolder<PlantGrowthChamberRecipe> recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;

        int ticks = (int)(recipe.value().getTicks() * PlantGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);
        Component component = Component.translatable("recipes.energizedpower.info.ticks", ticks);

        guiGraphics.drawString(font, component, 1, 40, 0xFFFFFFFF, true);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.fluid_consumption");

        guiGraphics.drawString(font, component, 1, 50, 0xFFFFFFFF, true);

        double fluidConsumption = recipe.value().getFluidConsumption() * PlantGrowthChamberBlockEntity.FLUID_CONSUMPTION_MULTIPLIER;
        component = Component.literal("-> " + FluidUtils.getFluidAmountWithPrefixSmallAndLarge(fluidConsumption) + "/t");

        guiGraphics.drawString(font, component, 1, 62, 0xFFFFFFFF, true);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<PlantGrowthChamberRecipe> recipe, IFocusGroup focuses) {
        ItemStack[] outputEntries = recipe.value().getMaxOutputCounts();
        for(int i = 0;i < outputEntries.length && i < 4;i++) {
            int x = i == 0 || i == 2?73:91;
            int y = i < 2?1:19;

            //TODO support multiple amounts
            double[] percentages = recipe.value().getOutputs()[i].percentages();
            builder.addWidget(new ChanceBasedSlotWidget(x, y,
                    (int)Arrays.stream(percentages).filter(p -> p >= 1.0).count(), percentages.length));
        }
    }
}
