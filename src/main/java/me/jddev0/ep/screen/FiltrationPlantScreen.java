package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import me.jddev0.ep.screen.base.SelectableRecipeMachineContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class FiltrationPlantScreen extends SelectableRecipeMachineContainerScreen<FiltrationPlantRecipe, FiltrationPlantMenu> {
    public FiltrationPlantScreen(FiltrationPlantMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/filtration_plant.png"),
                new Identifier(EnergizedPowerMod.MODID,
                        "textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));

        recipeSelectorPosX = 98;

        recipeSelectorTexturePosY = 115;
    }

    @Override
    protected ItemStack getRecipeIcon(FiltrationPlantRecipe currentRecipe) {
        Identifier icon = currentRecipe.getIcon();
        return new ItemStack(Registry.ITEM.get(icon));
    }

    @Override
    protected void renderCurrentRecipeTooltip(MatrixStack poseStack, int mouseX, int mouseY, FiltrationPlantRecipe currentRecipe) {
        List<Text> components = new ArrayList<>(2);

        ItemStack[] maxOutputs = currentRecipe.getMaxOutputCounts();
        for(int i = 0;i < maxOutputs.length;i++) {
            ItemStack output = maxOutputs[i];
            if(output.isEmpty())
                continue;

            components.add(Text.empty().
                    append(output.getName()).
                    append(Text.literal(": ")).
                    append(Text.translatable("recipes.energizedpower.transfer.output_percentages"))
            );

            double[] percentages = (i == 0?currentRecipe.getOutputItem():currentRecipe.getSecondaryOutput()).
                    percentages();
            for(int j = 0;j < percentages.length;j++)
                components.add(Text.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

            components.add(Text.empty());
        }

        //Remove trailing empty line
        if(!components.isEmpty())
            components.remove(components.size() - 1);

        renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
    }

    @Override
    protected void renderBgNormalView(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        for (int i = 0; i < 2; i++) {
            renderFluidMeterContent(i, poseStack, x, y);
            renderFluidMeterOverlay(i, poseStack, x, y);
        }

        renderProgressArrows(poseStack, x, y);
    }

    private void renderFluidMeterContent(int tank, MatrixStack poseStack, int x, int y) {
        RenderSystem.enableBlend();
        poseStack.push();

        poseStack.translate(x + (tank == 0?44:152), y + 17, 0);

        renderFluidStack(tank, poseStack);

        poseStack.pop();
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.disableBlend();
    }

    private void renderFluidStack(int tank, MatrixStack poseStack) {
        FluidStack fluidStack = handler.getFluid(tank);
        if(fluidStack.isEmpty())
            return;

        long capacity = handler.getTankCapacity(tank);

        Fluid fluid = fluidStack.getFluid();
        Sprite stillFluidSprite = FluidVariantRendering.getSprite(fluidStack.getFluidVariant());
        if(stillFluidSprite == null)
            stillFluidSprite = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).
                    apply(MissingSprite.getMissingSpriteId());

        int fluidColorTint = FluidVariantRendering.getColor(fluidStack.getFluidVariant());

        int fluidMeterPos = 52 - (int)((fluidStack.getDropletsAmount() <= 0 || capacity == 0)?0:
                (Math.min(fluidStack.getDropletsAmount(), capacity - 1) * 52 / capacity + 1));

        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor((fluidColorTint >> 16 & 0xFF) / 255.f,
                (fluidColorTint >> 8 & 0xFF) / 255.f, (fluidColorTint & 0xFF) / 255.f,
                (fluidColorTint >> 24 & 0xFF) / 255.f);

        Matrix4f mat = poseStack.peek().getPositionMatrix();

        for(int yOffset = 52;yOffset > fluidMeterPos;yOffset -= 16) {
            int height = Math.min(yOffset - fluidMeterPos, 16);

            float u0 = stillFluidSprite.getMinU();
            float u1 = stillFluidSprite.getMaxU();
            float v0 = stillFluidSprite.getMinV();
            float v1 = stillFluidSprite.getMaxV();
            v0 = v0 - ((16 - height) / 16.f * (v0 - v1));

            Tessellator tesselator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuffer();
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(mat, 0, yOffset, 0).texture(u0, v1).next();
            bufferBuilder.vertex(mat, 16, yOffset, 0).texture(u1, v1).next();
            bufferBuilder.vertex(mat, 16, yOffset - height, 0).texture(u1, v0).next();
            bufferBuilder.vertex(mat, 0, yOffset - height, 0).texture(u0, v0).next();
            tesselator.draw();
        }
    }

    private void renderFluidMeterOverlay(int tank, MatrixStack poseStack, int x, int y) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        drawTexture(poseStack, x + (tank == 0?44:152), y + 17, 176, 53, 16, 52);
    }

    private void renderProgressArrows(MatrixStack poseStack, int x, int y) {
        if(handler.isCraftingActive()) {
            for(int i = 0;i < 2;i++) {
                drawTexture(poseStack, x + 67, y + 34 + 27*i, 176, 106, handler.getScaledProgressArrowSize(), 9);
            }
        }
    }

    @Override
    protected void renderTooltipNormalView(MatrixStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        for(int i = 0;i < 2;i++) {
            //Fluid meter

            if(isPointWithinBounds(i == 0?44:152, 17, 16, 52, mouseX, mouseY)) {
                List<Text> components = new ArrayList<>(2);

                boolean fluidEmpty =  handler.getFluid(i).isEmpty();

                long fluidAmount = fluidEmpty?0:handler.getFluid(i).getMilliBucketsAmount();

                Text tooltipComponent = Text.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                        FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(FluidUtils.
                                convertDropletsToMilliBuckets(handler.getTankCapacity(i))));

                if(!fluidEmpty) {
                    tooltipComponent = Text.translatable(handler.getFluid(i).getTranslationKey()).append(" ").
                            append(tooltipComponent);
                }

                components.add(tooltipComponent);

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }
        }

        //Missing Charcoal Filter
        for(int i = 0;i < 2;i++) {
            if(isPointWithinBounds(62 + 72*i, 44, 16, 16, mouseX, mouseY) &&
                    handler.getSlot(4 * 9 + i).getStack().isEmpty()) {
                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.filtration_plant.charcoal_filter_missing").
                        formatted(Formatting.RED));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
