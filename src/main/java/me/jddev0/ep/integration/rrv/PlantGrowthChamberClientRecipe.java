package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.block.entity.PlantGrowthChamberBlockEntity;
import me.jddev0.ep.recipe.OutputItemStackTemplateWithPercentages;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import me.jddev0.ep.recipe.PlantGrowthChamberSoilRecipe;
import me.jddev0.ep.registry.EPRegistries;
import me.jddev0.ep.soil.SoilType;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

import java.util.*;
import java.util.stream.Collectors;

public class PlantGrowthChamberClientRecipe implements EPExtendedReliableClientRecipe {
    private final Identifier id;
    private final SlotContent input, soilType, fluid;
    private final double fluidConsumption;
    private final List<ItemStack> rawOutputs;
    private final List<SlotContent> outputs;
    private final OutputItemStackTemplateWithPercentages[] outputsWithPercentages;
    private final int ticks;
    private final List<ChanceBasedSlotRenderer> slotRenderers;

    public PlantGrowthChamberClientRecipe(RecipeHolder<PlantGrowthChamberRecipe> recipe, Collection<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipes) {
        this.id = recipe.id().identifier();
        this.input = SlotContent.of(recipe.value().getInput());

        List<Holder<SoilType>> soilTypes = recipe.value().getSoilType().getSoilType().map(
                soilType -> soilType.stream().
                        map(st -> ClientRecipeManager.INSTANCE.registryAccess().getOrThrow(st)).
                        collect(Collectors.toUnmodifiableList()),
                soilType -> ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(EPRegistries.SOIL_TYPE).
                            getOrThrow(soilType).stream().toList()
        );
        this.soilType = SlotContent.of(soilRecipes.stream().map(RecipeHolder::value).filter(soil -> soilTypes.stream().
                        anyMatch(soilType -> soilType.is(soil.getSoilType()))).
                map(PlantGrowthChamberSoilRecipe::getInput).
                flatMap(Ingredient::items).
                map(Holder::unwrap).
                map(registryKeyItemEither -> registryKeyItemEither.map(
                        l -> new ItemStack(ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(l)),
                        ItemStack::new
                )).
                collect(Collectors.toList()));

        List<Fluid> rawFluids = recipe.value().getFluid().getFluid().map(
                fluid -> fluid,
                fluid -> ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(BuiltInRegistries.FLUID.key()).
                        getOrThrow(fluid).stream().map(Holder::value).toList()
        );
        this.fluid = SlotContent.ofFluidList(rawFluids.stream().
                map(fluid -> new FluidStack(fluid, 1000)).
                collect(Collectors.toList()));

        this.fluidConsumption = recipe.value().getFluidConsumption() * PlantGrowthChamberBlockEntity.FLUID_CONSUMPTION_MULTIPLIER;

        ItemStack[] outputEntries = recipe.value().getMaxOutputCounts();
        this.rawOutputs = Arrays.stream(outputEntries).map(output -> output.copyWithCount(1)).toList();
        this.outputs = rawOutputs.stream().map(SlotContent::of).toList();
        this.outputsWithPercentages = recipe.value().getOutputs();
        this.ticks = (int)(recipe.value().getTicks() * PlantGrowthChamberBlockEntity.RECIPE_DURATION_MULTIPLIER);

        slotRenderers = new ArrayList<>();
        for(int i = 0;i < outputsWithPercentages.length && i < 4;i++) {
            //TODO support multiple amounts
            double[] percentages = outputsWithPercentages[i].percentages();
            slotRenderers.add(new ChanceBasedSlotRenderer(76 + (i%2) * 18, 4 + (i/2) * 18,
                    (int)Arrays.stream(percentages).filter(p -> p >= 1.0).count(), percentages.length));
        }
    }

    @Override
    public ReliableClientRecipeType getType() {
        return PlantGrowthChamberClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(1, this.soilType);
        slotFillContext.bindSlot(2, this.fluid);

        List<List<ItemStack>> combinedOutputs = new ArrayList<>();
        for(int i = 0;i < 4;i++)
            combinedOutputs.add(new ArrayList<>());

        for(int i = 0;i < rawOutputs.size();i++) {
            List<ItemStack> outputSlot = combinedOutputs.get(i % 4);
            outputSlot.add(rawOutputs.get(i));
        }

        for(int i = 0;i < 4;i++) {
            slotFillContext.bindSlot(3 + i, SlotContent.of(combinedOutputs.get(i)));

            slotFillContext.addAdditionalStackModifier(3 + i, ((stack, tooltip) -> {
                tooltip.add(Component.translatable("recipes.energizedpower.transfer.output_percentages"));

                for(int j = 0;j < outputsWithPercentages.length;j++) {
                    if(ItemStack.isSameItemSameComponents(stack, outputsWithPercentages[j].output())) {
                        double[] percentages = outputsWithPercentages[j].percentages();
                        for(int k = 0;k < percentages.length;k++)
                            tooltip.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", k + 1, 100 * percentages[k])));

                        return;
                    }
                }
            }));
        }
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.input, this.soilType, this.fluid);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.copyOf(this.outputs);
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {
        Font font = context.font();

        Component component = Component.translatable("recipes.energizedpower.info.ticks", ticks);

        context.guiGraphics().text(font, component, 4, 41, 0xFFFFFFFF, true);

        component = Component.translatable("recipes.energizedpower.plant_growth_chamber.fluid_consumption");

        context.guiGraphics().text(font, component, 4, 53, 0xFFFFFFFF, true);

        component = Component.literal("-> " + FluidUtils.getFluidAmountWithPrefixSmallAndLarge(fluidConsumption) + "/t");

        context.guiGraphics().text(font, component, 4, 65, 0xFFFFFFFF, true);
    }

    @Override
    public void energizedpower$renderRecipeOverlay(RecipeScreenContext context) {
        for(ChanceBasedSlotRenderer slotRenderer:slotRenderers)
            slotRenderer.extractRenderState(context.guiGraphics(), context.mouseX(), context.mouseY(), context.partialTicks());
    }
}
