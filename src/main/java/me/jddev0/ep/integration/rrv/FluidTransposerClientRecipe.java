package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import me.jddev0.ep.util.FluidStackUtils;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

public class FluidTransposerClientRecipe implements ReliableClientRecipe {
    private final Identifier id;
    private final FluidTransposerBlockEntity.Mode mode;
    private final SlotContent input, output, fluid;

    public FluidTransposerClientRecipe(RecipeHolder<FluidTransposerRecipe> recipe) {
        this.id = recipe.id().identifier();
        this.mode = recipe.value().getMode();
        this.input = SlotContent.of(recipe.value().getInput());
        this.output = SlotContent.of(recipe.value().getOutput());
        List<FluidStack> fluidStacks = new ArrayList<>();
        recipe.value().getFluid().map(f -> {
            fluidStacks.add(FluidStack.fromFluidStack(FluidStackUtils.fromNullableFluidStackTemplate(f)));

            return null;
        }, f -> {
            int amount = f.amount();
            List<Fluid> fluids = f.fluid().getFluid().map(
                    fluid -> fluid,
                    fluid -> ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(BuiltInRegistries.FLUID.key()).
                            getOrThrow(fluid).stream().map(Holder::value).toList()
            );

            for(Fluid fluid:fluids)
                fluidStacks.add(new FluidStack(fluid, amount));

            return null;
        });
        this.fluid = SlotContent.ofFluidList(fluidStacks);
    }

    @Override
    public ReliableClientRecipeType getType() {
        return FluidTransposerClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
        if(this.mode == FluidTransposerBlockEntity.Mode.EMPTYING) {
            //Disable unused slot
            slotFillContext.bindOptionalSlot(1, SlotContent.of(), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);

            slotFillContext.bindSlot(2, this.output);
            slotFillContext.bindSlot(3, this.fluid);
        }else {
            //Disable unused slot
            slotFillContext.bindOptionalSlot(2, SlotContent.of(), RecipeViewMenu.OptionalSlotRenderer.DEFAULT);

            slotFillContext.bindSlot(1, this.fluid);
            slotFillContext.bindSlot(3, this.output);
        }
    }

    @Override
    public List<SlotContent> getIngredients() {
        if(this.mode == FluidTransposerBlockEntity.Mode.EMPTYING) {
            return List.of(this.input);
        }else {
            return List.of(this.input, this.fluid);
        }
    }

    @Override
    public List<SlotContent> getResults() {
        if(this.mode == FluidTransposerBlockEntity.Mode.EMPTYING) {
            return List.of(this.output, this.fluid);
        }else {
            return List.of(this.output);
        }
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {
        ReliableClientRecipeType recipeType = this.getType();
        Identifier backgroundTexture;
        if(this.mode == FluidTransposerBlockEntity.Mode.FILLING) {
            backgroundTexture = EPAPI.id("textures/gui/recipe/rrv/fluid_transposer_filling.png");
        }else {
            backgroundTexture = EPAPI.id("textures/gui/recipe/rrv/fluid_transposer_emptying.png");
        }
        context.guiGraphics().blit(RenderPipelines.GUI_TEXTURED, backgroundTexture, 0, 0, 0, 0,
                recipeType.getDisplayWidth(), recipeType.getDisplayHeight(), recipeType.getDisplayWidth(), recipeType.getDisplayHeight());

        ItemStack output = new ItemStack(this.mode == FluidTransposerBlockEntity.Mode.EMPTYING?Items.BUCKET:Items.WATER_BUCKET);
        context.guiGraphics().pose().pushMatrix();
        context.guiGraphics().pose().translate(0.f, 0.f);

        context.guiGraphics().item(output, 123, 8, 123 + 8 * 143);

        context.guiGraphics().pose().popMatrix();

        int tooltipX = 122;
        int tooltipY = 7;
        if(context.mouseX() >= (double)(tooltipX - 1) && context.mouseX() < (double)(tooltipX + 20 + 1) &&
                context.mouseY() >= (double)(tooltipY - 1) && context.mouseY() < (double)(tooltipY + 20 + 1)) {
            context.setTooltip(Component.translatable("tooltip.energizedpower.fluid_transposer.mode." +
                    this.mode.getSerializedName()));
        }
    }
}
