package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import me.jddev0.ep.screen.base.SelectableRecipeMachineContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class FiltrationPlantScreen
        extends SelectableRecipeMachineContainerScreen<FiltrationPlantRecipe, FiltrationPlantMenu> {
    public FiltrationPlantScreen(FiltrationPlantMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                EPAPI.id("textures/gui/container/filtration_plant.png"),
                EPAPI.id("textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));

        recipeSelectorPosX = 98;

        recipeSelectorTexturePosY = 115;
    }

    @Override
    protected ItemStack getRecipeIcon(FiltrationPlantRecipe currentRecipe) {
        ResourceLocation icon = currentRecipe.getIcon();
        Item iconItem = ForgeRegistries.ITEMS.getValue(icon);
        return iconItem == null?ItemStack.EMPTY:new ItemStack(iconItem);
    }

    @Override
    protected void renderCurrentRecipeTooltip(PoseStack poseStack, int mouseX, int mouseY, FiltrationPlantRecipe currentRecipe) {
        List<Component> components = new ArrayList<>(2);

        ItemStack[] maxOutputs = currentRecipe.getMaxOutputCounts();
        for(int i = 0;i < maxOutputs.length;i++) {
            ItemStack output = maxOutputs[i];
            if(output.isEmpty())
                continue;

            components.add(Component.empty().
                    append(output.getHoverName()).
                    append(Component.literal(": ")).
                    append(Component.translatable("recipes.energizedpower.transfer.output_percentages"))
            );

            double[] percentages = (i == 0?currentRecipe.getOutput():currentRecipe.getSecondaryOutput()).
                    percentages();
            for(int j = 0;j < percentages.length;j++)
                components.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

            components.add(Component.empty());
        }

        //Remove trailing empty line
        if(!components.isEmpty())
            components.remove(components.size() - 1);

        renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
    }

    @Override
    protected void renderBgNormalView(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        for (int i = 0; i < 2; i++) {
            renderFluidMeterContent(poseStack, menu.getFluid(i), menu.getTankCapacity(i), x + (i == 0?44:152), y + 17, 16, 52);
            renderFluidMeterOverlay(i, poseStack, x, y);
        }

        renderProgressArrows(poseStack, x, y);
    }

    private void renderFluidMeterOverlay(int tank, PoseStack poseStack, int x, int y) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, x + (tank == 0?44:152), y + 17, 176, 53, 16, 52);
    }

    private void renderProgressArrows(PoseStack poseStack, int x, int y) {
        if(menu.isCraftingActive()) {
            for(int i = 0;i < 2;i++) {
                blit(poseStack, x + 67, y + 34 + 27*i, 176, 106, menu.getScaledProgressArrowSize(), 9);
            }
        }
    }

    @Override
    protected void renderTooltipNormalView(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        for(int i = 0;i < 2;i++) {
            //Fluid meter

            if(isHovering(i == 0?44:152, 17, 16, 52, mouseX, mouseY)) {
                List<Component> components = new ArrayList<>(2);

                boolean fluidEmpty =  menu.getFluid(i).isEmpty();

                int fluidAmount = fluidEmpty?0:menu.getFluid(i).getAmount();

                Component tooltipComponent = Component.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                        FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(menu.getTankCapacity(i)));

                if(!fluidEmpty) {
                    tooltipComponent = Component.translatable(menu.getFluid(i).getTranslationKey()).append(" ").
                            append(tooltipComponent);
                }

                components.add(tooltipComponent);

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }
        }

        //Missing Charcoal Filter
        for(int i = 0;i < 2;i++) {
            if(isHovering(62 + 72*i, 44, 16, 16, mouseX, mouseY) &&
                    menu.getSlot(4 * 9 + i).getItem().isEmpty()) {
                List<Component> components = new ArrayList<>(2);
                components.add(Component.translatable("tooltip.energizedpower.filtration_plant.charcoal_filter_missing").
                        withStyle(ChatFormatting.RED));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
