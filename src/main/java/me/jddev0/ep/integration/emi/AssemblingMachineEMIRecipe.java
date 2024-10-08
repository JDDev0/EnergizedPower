package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.recipe.AssemblingMachineRecipe;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AssemblingMachineEMIRecipe implements EmiRecipe {
    public static final Identifier SIMPLIFIED_TEXTURE = EPAPI.id("textures/block/assembling_machine_front.png");
    public static final EmiStack ITEM = EmiStack.of(ModBlocks.ASSEMBLING_MACHINE_ITEM);
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(EPAPI.id("assembling_machine"),
            ITEM, new EmiTexture(SIMPLIFIED_TEXTURE, 0, 0, 16, 16, 16, 16, 16, 16));

    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public AssemblingMachineEMIRecipe(AssemblingMachineRecipe recipe) {
        this.id = recipe.getId();
        this.input = Arrays.stream(recipe.getInputs()).map(input ->
                EmiIngredient.of(input.input(), input.count())).collect(Collectors.toList());
        this.output = List.of(EmiStack.of(recipe.getOutput()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return CATEGORY;
    }

    @Override
    public Identifier getId() {
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
        return 115;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        Identifier texture = EPAPI.id("textures/gui/container/assembling_machine.png");
        widgets.addTexture(texture, 0, 0, 115, 54, 43, 18);

        int len = Math.min(input.size(), 4);
        for(int i = 0;i < len;i++) {
            widgets.addSlot(input.get(i), i == 1?0:(i == 2?36:18), i == 0?0:(i == 3?36:18)).drawBack(false);
        }

        widgets.addSlot(output.get(0), 90, 18).drawBack(false).recipeContext(this);
    }
}
