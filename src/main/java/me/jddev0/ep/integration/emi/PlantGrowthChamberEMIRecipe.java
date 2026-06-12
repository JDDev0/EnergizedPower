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
import me.jddev0.ep.block.entity.PlantGrowthChamberBlockEntity;
import me.jddev0.ep.recipe.OutputItemStackWithPercentages;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import me.jddev0.ep.recipe.PlantGrowthChamberSoilRecipe;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.SoilType;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

import java.util.*;
import java.util.stream.Collectors;

public class PlantGrowthChamberEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/plant_growth_chamber_front.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get());
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("plant_growth_chamber"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final Either<List<ResourceKey<SoilType>>, TagKey<SoilType>> soilType;
    private final Fluid[] fluid;
    private final double fluidConsumption;
    private final List<EmiStack> output;
    private final OutputItemStackWithPercentages[] outputsWithPercentages;
    private final int ticks;

    public PlantGrowthChamberEMIRecipe(RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        this.id = recipe.id();
        this.input = List.of(EmiIngredient.of(recipe.value().getInput()));
        this.soilType = recipe.value().getSoilType();
        this.output = Arrays.stream(recipe.value().getMaxOutputCounts()).
                map(item -> EmiStack.of(item, 1)).toList();
        this.outputsWithPercentages = recipe.value().getOutputs();
        this.fluid = recipe.value().getFluid();
        this.fluidConsumption = recipe.value().getFluidConsumption() * PlantGrowthChamberBlockEntity.FLUID_CONSUMPTION_MULTIPLIER;
        this.ticks = (int)(recipe.value().getTicks() * PlantGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);
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
        return 139;
    }

    @Override
    public int getDisplayHeight() {
        return 74;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        widgets.addTexture(texture, 0, 0, 139, 74, 116, 1);

        widgets.addSlot(input.get(0), 18, 0).drawBack(false);

        List<Holder<SoilType>> soilTypes = soilType.map(
                soilType -> soilType.stream().
                        map(st -> Minecraft.getInstance().level.registryAccess().lookupOrThrow(EPRegistries.SOIL_TYPE).
                                getOrThrow(st)).
                        collect(Collectors.toUnmodifiableList()),
                soilType -> Minecraft.getInstance().level.registryAccess().lookupOrThrow(EPRegistries.SOIL_TYPE).
                        getOrThrow(soilType).stream().toList()
        );

        Collection<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipes = Minecraft.getInstance().level.getRecipeManager().
                getAllRecipesFor(PlantGrowthChamberSoilRecipe.Type.INSTANCE);

        List<EmiIngredient> soils = new ArrayList<>();
        soilRecipes.stream().map(RecipeHolder::value).filter(soil -> soilTypes.stream().
                        anyMatch(soilType -> soilType.is(soil.getSoilType()))).
                forEach(soil -> soils.add(EmiIngredient.of(soil.getInput())));

        widgets.addSlot(EmiIngredient.of(soils), 18, 18).drawBack(false);

        List<EmiStack> fluids = new ArrayList<>();
        for(Fluid fluid:fluid)
            fluids.add(EmiStack.of(fluid, 1000));

        widgets.addSlot(EmiIngredient.of(fluids), 0, 9).drawBack(false);

        List<List<EmiStack>> outputSlotEntries = new ArrayList<>(4);
        for(int i = 0;i < 4;i++)
            outputSlotEntries.add(new ArrayList<>());

        for(int i = 0;i < output.size();i++)
            outputSlotEntries.get(i % 4).add(output.get(i));

        ChanceBasedSlotWidget[] outputSlots = new ChanceBasedSlotWidget[4];

        outputSlots[0] = widgets.add(new ChanceBasedSlotWidget(EmiIngredient.of(outputSlotEntries.get(0)), 72, 0)).drawBack(false).recipeContext(this);
        outputSlots[1] = widgets.add(new ChanceBasedSlotWidget(EmiIngredient.of(outputSlotEntries.get(1)), 90, 0)).drawBack(false).recipeContext(this);
        outputSlots[2] = widgets.add(new ChanceBasedSlotWidget(EmiIngredient.of(outputSlotEntries.get(2)), 72, 18)).drawBack(false).recipeContext(this);
        outputSlots[3] = widgets.add(new ChanceBasedSlotWidget(EmiIngredient.of(outputSlotEntries.get(3)), 90, 18)).drawBack(false).recipeContext(this);

        for(int i = 0;i < outputsWithPercentages.length;i++) {
            ChanceBasedSlotWidget outputSlot = outputSlots[i % 4];

            Component oddsText = Component.translatable("recipes.energizedpower.transfer.output_percentages");

            if(i >= 4 || i + 4 < outputsWithPercentages.length) {
                outputSlot.appendTooltip(Component.translatable(outputsWithPercentages[i].output().getDescriptionId()).
                        append(Component.literal(": ").append(oddsText)));
            }else {
                outputSlot.appendTooltip(oddsText);

                //TODO support multiple amounts
                outputSlot.setMinAmount((int)Arrays.stream(outputsWithPercentages[i].percentages()).filter(p -> p >= 1.0).count());
                outputSlot.setMaxAmount(outputsWithPercentages[i].percentages().length);
            }

            double[] percentages = outputsWithPercentages[i].percentages();
            for(int j = 0;j < percentages.length;j++)
                outputSlot.appendTooltip(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

            if(i + 4 < outputsWithPercentages.length) {
                outputSlot.appendTooltip(Component.empty());
            }
        }

        Font font = Minecraft.getInstance().font;

        Component component = Component.translatable("recipes.energizedpower.info.ticks", ticks);

        widgets.addText(component, 1, 40, 0xFFFFFFFF, true);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.fluid_consumption");

        widgets.addText(component, 1, 50, 0xFFFFFFFF, true);

        component = Component.literal("-> " + FluidUtils.getFluidAmountWithPrefixSmallAndLarge(fluidConsumption) + "/t");

        widgets.addText(component, 1, 62, 0xFFFFFFFF, true);
    }
}
