package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.ChargerBlockEntity;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ChargerEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/block/charger_side.png");
    public static final EmiStack ITEM = EmiStack.of(ModBlocks.CHARGER_ITEM.get());
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(new ResourceLocation(EnergizedPowerMod.MODID, "charger"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final int energyConsumption;

    public ChargerEMIRecipe(ChargerRecipe recipe) {
        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(recipe.getInput()));
        this.output = List.of(EmiStack.of(recipe.getOutput()));
        this.energyConsumption = (int)(recipe.getEnergyConsumption() * ChargerBlockEntity.CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER);
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
        return 113;
    }

    @Override
    public int getDisplayHeight() {
        return 46;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation texture = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/charger.png");
        widgets.addTexture(texture, 0, 0, 46, 46, 65, 20);

        widgets.addTexture(EmiTexture.EMPTY_ARROW, 53, 14);

        widgets.addSlot(input.get(0), 14, 14).drawBack(false);
        widgets.addSlot(output.get(0), 91, 14).recipeContext(this);

        Component energyConsumptionText = Component.literal(EnergyUtils.getEnergyWithPrefix(energyConsumption));
        widgets.addText(energyConsumptionText.getVisualOrderText(),
                widgets.getWidth() - Minecraft.getInstance().font.width(energyConsumptionText),
                widgets.getHeight() - Minecraft.getInstance().font.lineHeight, ChatFormatting.YELLOW.getColor(), false);
    }
}
