package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.recipe.PlantGrowthChamberSoilRecipe;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.SoilType;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class PlantGrowthChamberSoilClientRecipe implements ReliableClientRecipe {
    private final Identifier id;
    private final SlotContent input;
    private final ResourceKey<SoilType> soilType;
    private final double speedMultiplier, fluidConsumptionMultiplier, energyConsumptionMultiplier;

    public PlantGrowthChamberSoilClientRecipe(RecipeHolder<PlantGrowthChamberSoilRecipe> recipe) {
        this.id = recipe.id().identifier();
        this.input = SlotContent.of(recipe.value().getInput());
        this.soilType = recipe.value().getSoilType();
        this.speedMultiplier = recipe.value().getSpeedMultiplier();
        this.fluidConsumptionMultiplier = recipe.value().getFluidConsumptionMultiplier();
        this.energyConsumptionMultiplier = recipe.value().getEnergyConsumptionMultiplier();
    }

    @Override
    public ReliableClientRecipeType getType() {
        return PlantGrowthChamberSoilClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.input);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of();
    }

    @Override
    public boolean isVisualOnly() {
        return true;
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {
        Font font = context.font();

        SoilType soilType = ClientRecipeManager.INSTANCE.registryAccess().
                lookupOrThrow(EPRegistries.SOIL_TYPE).getOrThrow(this.soilType).value();

        Component component = Component.translatable("recipes.energizedpower.plant_growth_chamber.soil_type", "");
        int textWidth = font.width(component);

        context.guiGraphics().text(font, component, 4, 25, 0xFFFFFFFF, true);
        context.guiGraphics().text(font, soilType.displayName(), 4 + textWidth, 26, 0xFFFFFFFF, false);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.speed_multiplier", speedMultiplier);
        context.guiGraphics().text(font, component, 4, 40, 0xFFFFFFFF, true);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.fluid_consumption_multiplier", fluidConsumptionMultiplier);
        context.guiGraphics().text(font, component, 4, 55, 0xFFFFFFFF, true);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.energy_consumption_multiplier", energyConsumptionMultiplier);
        context.guiGraphics().text(font, component, 4, 70, 0xFFFFFFFF, true);
    }
}
