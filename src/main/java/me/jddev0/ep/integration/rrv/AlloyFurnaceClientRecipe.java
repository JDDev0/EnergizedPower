package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.block.entity.AlloyFurnaceBlockEntity;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
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

public class AlloyFurnaceClientRecipe implements EPExtendedReliableClientRecipe {
    private final Identifier id;
    private final List<SlotContent> inputs;
    private final SlotContent output, secondaryOutput;
    private final OutputItemStackTemplateWithPercentages secondaryOutputWithPercentages;
    private final int ticks;
    private final List<ChanceBasedSlotRenderer> slotRenderers;

    public AlloyFurnaceClientRecipe(RecipeHolder<AlloyFurnaceRecipe> recipe) {
        this.id = recipe.id().identifier();
        this.inputs = Arrays.stream(recipe.value().getInputs()).map(input ->
                SlotContent.of(input.input().items().
                        map(Holder::unwrap).
                        map(registryKeyItemEither -> registryKeyItemEither.map(
                                l -> new ItemStack(ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(l)),
                                ItemStack::new
                        )).
                        map(itemStack -> itemStack.copyWithCount(input.count())).
                        collect(Collectors.toList()))).collect(Collectors.toList());
        ItemStack[] outputEntries = recipe.value().getMaxOutputCounts();
        this.output = SlotContent.of(outputEntries[0]);
        this.secondaryOutput = outputEntries[1].isEmpty()?SlotContent.of():SlotContent.of(outputEntries[1].copyWithCount(1));
        this.secondaryOutputWithPercentages = recipe.value().getSecondaryOutput();
        this.ticks = (int)(recipe.value().getTicks() * AlloyFurnaceBlockEntity.RECIPE_DURATION_MULTIPLIER);

        slotRenderers = new ArrayList<>();
        if(!outputEntries[1].isEmpty()) {
            double[] percentages = secondaryOutputWithPercentages.percentages();
            slotRenderers.add(new ChanceBasedSlotRenderer(129, 8, (int)Arrays.stream(percentages).filter(p -> p >= 1.0).count(), percentages.length));
        }
    }

    @Override
    public ReliableClientRecipeType getType() {
        return AlloyFurnaceClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        for(int i = 0;i < this.inputs.size();i++)
            slotFillContext.bindSlot(i, this.inputs.get(i));

        slotFillContext.bindSlot(3, this.output);
        slotFillContext.bindSlot(4, this.secondaryOutput);
        {
            double[] percentages = secondaryOutputWithPercentages.percentages();
            slotFillContext.addAdditionalStackModifier(4, ((stack, tooltip) -> {
                tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                for(int i = 0;i < percentages.length;i++)
                    tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", i + 1, 100 * percentages[i])));
            }));
        }
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.copyOf(this.inputs);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.output, this.secondaryOutput);
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {
        Font font = context.font();
        Component component = Component.translatable("recipes.energizedpower.info.ticks", ticks);
        int textWidth = font.width(component);

        context.guiGraphics().text(font, component, 150 - textWidth, 32, 0xFFFFFFFF, false);
    }

    @Override
    public void energizedpower$renderRecipeOverlay(RecipeScreenContext context) {
        for(ChanceBasedSlotRenderer slotRenderer:slotRenderers)
            slotRenderer.extractRenderState(context.guiGraphics(), context.mouseX(), context.mouseY(), context.partialTicks());
    }
}
