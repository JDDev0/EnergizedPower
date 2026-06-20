package me.jddev0.ep.integration.rei;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.PlantGrowthChamberBlockEntity;
import me.jddev0.ep.recipe.PlantGrowthChamberSoilRecipe;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.SoilType;
import me.jddev0.ep.util.FluidUtils;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

import java.util.*;
import java.util.stream.Collectors;

public class PlantGrowthChamberCategory implements DisplayCategory<PlantGrowthChamberDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends PlantGrowthChamberDisplay> getCategoryIdentifier() {
        return PlantGrowthChamberDisplay.CATEGORY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.energizedpower.plant_growth_chamber");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM);
    }

    @Override
    public List<Widget> setupDisplay(PlantGrowthChamberDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING;

        Identifier texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        widgets.add(Widgets.createTexturedWidget(texture, x, y, 116, 1, 139, 74));

        widgets.add(Widgets.createSlot(new Point(x + 19, y + 1)).disableBackground().markInput().
                entries(display.getInputEntries().get(0)));

        List<Holder<SoilType>> soilTypes = display.recipe().value().getSoilType().map(
                soilType -> soilType.stream().
                        map(st -> Minecraft.getInstance().level.registryAccess().lookupOrThrow(EPRegistries.SOIL_TYPE).
                                getOrThrow(st)).
                        collect(Collectors.toUnmodifiableList()),
                soilType -> Minecraft.getInstance().level.registryAccess().lookupOrThrow(EPRegistries.SOIL_TYPE).
                        getOrThrow(soilType).stream().toList()
        );

        Collection<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipes = Minecraft.getInstance().level.recipeAccess().getSynchronizedRecipes().
                getAllOfType(PlantGrowthChamberSoilRecipe.Type.INSTANCE);

        List<EntryStack<?>> soils = new ArrayList<>();
        soilRecipes.stream().map(RecipeHolder::value).filter(soil -> soilTypes.stream().
                        anyMatch(soilType -> soilType.is(soil.getSoilType()))).
                forEach(soil -> soils.addAll(EntryIngredients.ofIngredient(soil.getInput())));

        widgets.add(Widgets.createSlot(new Point(x + 19, y + 19)).disableBackground().markInput().
                entries(soils));

        List<EntryStack<?>> fluids = new ArrayList<>();
        for(Fluid fluid:display.recipe().value().getFluid())
            fluids.addAll(EntryIngredients.of(fluid, FluidUtils.convertMilliBucketsToDroplets(1000)));

        widgets.add(Widgets.createSlot(new Point(x + 1, y + 10)).disableBackground().markInput().
                entries(fluids));

        List<List<EntryStack<?>>> outputSlotEntries = new ArrayList<>(4);
        for(int i = 0;i < 4;i++)
            outputSlotEntries.add(new ArrayList<>());

        List<EntryIngredient> outputEntries = display.getOutputEntries();
        for(int i = 0;i < outputEntries.size();i++) {
            int index = i;
            outputSlotEntries.get(i % 4).addAll(outputEntries.get(i).map(stack -> {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                double[] percentages = display.recipe().value().getOutputs()[index].percentages();
                for(int j = 0;j < percentages.length;j++)
                    tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

                return stack.tooltip(tooltip);
            }));
        }

        widgets.add(Widgets.createSlot(new Point(x + 73, y + 1)).disableBackground().markOutput().
                entries(outputSlotEntries.get(0)));
        widgets.add(Widgets.createSlot(new Point(x + 91, y + 1)).disableBackground().markOutput().
                entries(outputSlotEntries.get(1)));
        widgets.add(Widgets.createSlot(new Point(x + 73, y + 19)).disableBackground().markOutput().
                entries(outputSlotEntries.get(2)));
        widgets.add(Widgets.createSlot(new Point(x + 91, y + 19)).disableBackground().markOutput().
                entries(outputSlotEntries.get(3)));

        for(int i = 0;i < outputEntries.size() && i < 4;i++) {
            int xOffset = i == 0 || i == 2?73:91;
            int yOffset = i < 2?1:19;

            //TODO support multiple amounts
            double[] percentages = display.recipe().value().getOutputs()[i].percentages();
            widgets.add(Widgets.wrapRenderer(new Rectangle(x + xOffset, y + yOffset, 18, 18),
                    new ChanceBasedSlotRenderer((int)Arrays.stream(percentages).filter(p -> p >= 1.0).count(), percentages.length)));
        }

        int ticks = (int)(display.recipe().value().getTicks() * PlantGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);
        Component component = Component.translatable("recipes.energizedpower.info.ticks", ticks);

        widgets.add(Widgets.createLabel(new Point(x + 1, y + 40), component).leftAligned());

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.fluid_consumption");

        widgets.add(Widgets.createLabel(new Point(x + 1, y + 50), component).leftAligned());

        double fluidConsumption = display.recipe().value().getFluidConsumption() * PlantGrowthChamberBlockEntity.FLUID_CONSUMPTION_MULTIPLIER;
        component = Component.literal("-> " + FluidUtils.getFluidAmountWithPrefixSmallAndLarge(fluidConsumption) + "/t");

        widgets.add(Widgets.createLabel(new Point(x + 1, y + 62), component).leftAligned());

        return widgets;
    }

    @Override
    public int getDisplayWidth(PlantGrowthChamberDisplay display) {
        return 139 + 2*PADDING;
    }

    @Override
    public int getDisplayHeight() {
        return 74 + 2*PADDING;
    }
}
