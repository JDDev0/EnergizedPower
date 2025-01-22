package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EnergizerBlockEntity;
import me.jddev0.ep.recipe.EnergizerRecipe;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class EnergizerEMIRecipe implements EmiRecipe {
    public static final ResourceLocation SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/energizer_front.png");
    public static final EmiStack ITEM = EmiStack.of(EPBlocks.ENERGIZER_ITEM.get());
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("energizer"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final ResourceLocation id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;
    private final int energyConsumption;

    public EnergizerEMIRecipe(RecipeHolder<EnergizerRecipe> recipe) {
        this.id = recipe.id().location();
        this.input = List.of(EmiIngredient.of(recipe.value().getInput()));
        this.output = List.of(EmiStack.of(recipe.value().getOutput()));
        this.energyConsumption = (int)(recipe.value().getEnergyConsumption() * EnergizerBlockEntity.ENERGY_CONSUMPTION_MULTIPLIER);
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
        return 114;
    }

    @Override
    public int getDisplayHeight() {
        return 50;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation texture = EPAPI.id("textures/gui/container/energizer.png");
        widgets.addTexture(texture, 0, 0, 114, 50, 31, 18);

        widgets.addSlot(input.get(0), 16, 16).drawBack(false);
        widgets.addSlot(output.get(0), 92, 16).drawBack(false).recipeContext(this);

        Component energyConsumptionText = Component.literal(EnergyUtils.getEnergyWithPrefix(energyConsumption));
        widgets.addText(energyConsumptionText.getVisualOrderText(),
                widgets.getWidth() - Minecraft.getInstance().font.width(energyConsumptionText),
                widgets.getHeight() - Minecraft.getInstance().font.lineHeight, ChatFormatting.YELLOW.getColor(), false);
    }
}
