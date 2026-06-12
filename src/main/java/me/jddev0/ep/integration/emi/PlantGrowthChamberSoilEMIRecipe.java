package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.recipe.PlantGrowthChamberSoilRecipe;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.SoilType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class PlantGrowthChamberSoilEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/plant_growth_chamber_side.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get());
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("plant_growth_chamber_soil"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final ResourceKey<SoilType> soilType;
    private final double speedMultiplier;
    private final double fluidConsumptionMultiplier;
    private final double energyConsumptionMultiplier;

    public PlantGrowthChamberSoilEMIRecipe(RecipeHolder<PlantGrowthChamberSoilRecipe> recipe) {
        this.id = recipe.id();
        this.input = List.of(EmiIngredient.of(recipe.value().getInput()));
        this.soilType = recipe.value().getSoilType();
        this.speedMultiplier = recipe.value().getSpeedMultiplier();
        this.fluidConsumptionMultiplier = recipe.value().getFluidConsumptionMultiplier();
        this.energyConsumptionMultiplier = recipe.value().getEnergyConsumptionMultiplier();
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
        return List.of();
    }

    @Override
    public int getDisplayWidth() {
        return 144;
    }

    @Override
    public int getDisplayHeight() {
        return 80;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation texture = EPAPI.id("textures/gui/recipe/misc_gui.png");
        widgets.addTexture(texture, 0, 0, 18, 18, 116, 10);

        widgets.addSlot(input.get(0), 0, 0).drawBack(false);

        Font font = Minecraft.getInstance().font;

        SoilType soilType = Minecraft.getInstance().level.registryAccess().
                lookupOrThrow(EPRegistries.SOIL_TYPE).getOrThrow(this.soilType).value();

        Component component = Component.translatable("recipes.energizedpower.plant_growth_chamber.soil_type", "");
        int textWidth = font.width(component);

        widgets.addText(component.getVisualOrderText(), 1, 22, 0xFFFFFFFF, true);
        widgets.addText(soilType.displayName(), 1 + textWidth, 23, 0xFFFFFFFF, false);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.speed_multiplier", speedMultiplier);
        widgets.addText(component, 1, 37, 0xFFFFFFFF, true);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.fluid_consumption_multiplier", fluidConsumptionMultiplier);
        widgets.addText(component, 1, 52, 0xFFFFFFFF, true);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.energy_consumption_multiplier", energyConsumptionMultiplier);
        widgets.addText(component, 1, 67, 0xFFFFFFFF, true);
    }
}
