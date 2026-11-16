package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.block.entity.CrystalGrowthChamberBlockEntity;
import me.jddev0.ep.recipe.CrystalGrowthChamberRecipe;
import me.jddev0.ep.recipe.OutputItemStackTemplateWithPercentages;
import net.minecraft.client.gui.Font;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CrystalGrowthChamberClientRecipe implements EPExtendedReliableClientRecipe {
    private final Identifier id;
    private final SlotContent input, output;
    private final OutputItemStackTemplateWithPercentages outputWithPercentages;
    private final int ticks;
    private final List<ChanceBasedSlotRenderer> slotRenderers;

    public CrystalGrowthChamberClientRecipe(RecipeHolder<CrystalGrowthChamberRecipe> recipe) {
        this.id = recipe.id().identifier();
        this.input = SlotContent.of(recipe.value().getInput().input().items().
                map(Holder::unwrap).
                map(registryKeyItemEither -> registryKeyItemEither.map(
                        l -> new ItemStack(ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(l)),
                        ItemStack::new
                )).
                map(itemStack -> itemStack.copyWithCount(recipe.value().getInput().count())).
                collect(Collectors.toList()));
        this.output = SlotContent.of(recipe.value().getMaxOutputCount().copyWithCount(1));
        this.outputWithPercentages = recipe.value().getOutput();
        this.ticks = (int)(recipe.value().getTicks() * CrystalGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);

        slotRenderers = new ArrayList<>();
        {
            double[] percentages = outputWithPercentages.percentages();
            slotRenderers.add(new ChanceBasedSlotRenderer(80, 8, (int)Arrays.stream(percentages).filter(p -> p >= 1.0).count(), percentages.length));
        }
    }

    @Override
    public ReliableClientRecipeType getType() {
        return CrystalGrowthChamberClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(1, this.output);
        {
            double[] percentages = outputWithPercentages.percentages();
            slotFillContext.addAdditionalStackModifier(1, ((stack, tooltip) -> {
                tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                for(int i = 0;i < percentages.length;i++)
                    tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
            }));
        }
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.input);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.output);
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {
        Font font = context.font();
        Component component = Component.translatable("recipes.energizedpower.info.ticks", ticks);
        int textWidth = font.width(component);

        context.guiGraphics().text(font, component, 101 - textWidth, 33, 0xFFFFFFFF, false);
    }

    @Override
    public void energizedpower$renderRecipeOverlay(RecipeScreenContext context) {
        for(ChanceBasedSlotRenderer slotRenderer:slotRenderers)
            slotRenderer.extractRenderState(context.guiGraphics(), context.mouseX(), context.mouseY(), context.partialTicks());
    }
}
