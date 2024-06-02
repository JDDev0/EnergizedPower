package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.screen.base.ConfigurableUpgradableEnergyStorageContainerScreen;
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
import net.minecraft.fluid.FluidState;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class FluidPumpScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<FluidPumpMenu> {
    public static final boolean SHOW_RELATIVE_COORDINATES = ModConfigs.CLIENT_FLUID_PUMP_RELATIVE_TARGET_COORDINATES.getValue();

    public FluidPumpScreen(FluidPumpMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.fluid_pump.process_energy_left.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/fluid_pump.png"),
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/upgrade_view/fluid_pump.png"));

        backgroundWidth = 230;
        energyMeterTextureX = 230;
    }

    @Override
    protected void renderBgNormalView(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderFluidMeterContent(poseStack, x, y);
        renderFluidMeterOverlay(poseStack, x, y);

        renderInfoText(poseStack, x, y);
    }

    private void renderFluidMeterContent(MatrixStack poseStack, int x, int y) {
        RenderSystem.enableBlend();
        poseStack.push();

        poseStack.translate(x + 206, y + 17, 0);

        renderFluidStack(poseStack);

        poseStack.pop();
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.disableBlend();
    }

    private void renderFluidStack(MatrixStack poseStack) {
        FluidStack fluidStack = handler.getFluid();
        if(fluidStack.isEmpty())
            return;

        long capacity = handler.getTankCapacity();

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

    private void renderFluidMeterOverlay(MatrixStack poseStack, int x, int y) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        drawTexture(poseStack, x + 206, y + 17, 230, 53, 16, 52);
    }

    private void renderInfoText(MatrixStack poseStack, int x, int y) {
        BlockPos targetPos = handler.getBlockEntity().getPos().add(handler.getTargetOffset());

        Text component;
        if(SHOW_RELATIVE_COORDINATES) {
            component = Text.translatable("tooltip.energizedpower.fluid_pump.target_relative",
                    String.format(Locale.ENGLISH, "%+d", handler.getTargetOffset().getX()),
                    String.format(Locale.ENGLISH, "%+d", handler.getTargetOffset().getY()),
                    String.format(Locale.ENGLISH, "%+d", handler.getTargetOffset().getZ()));
        }else {
            component = Text.translatable("tooltip.energizedpower.fluid_pump.target",
                    targetPos.getX(), targetPos.getY(), targetPos.getZ());
        }

        int componentWidth = textRenderer.getWidth(component);

        textRenderer.draw(poseStack, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 22, 0);


        if(handler.getSlot(4 * 9).getStack().isEmpty()) {
            component = Text.translatable("tooltip.energizedpower.fluid_pump.cobblestone_missing").
                    formatted(Formatting.RED);

            componentWidth = textRenderer.getWidth(component);

            textRenderer.draw(poseStack, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 58, 0);
        }else if(handler.isExtractingFluid()) {
            FluidState targetFluidState = handler.getBlockEntity().getWorld().getFluidState(targetPos);
            if(!targetFluidState.isEmpty()) {
                component = Text.translatable("tooltip.energizedpower.fluid_pump.extracting",
                        Text.translatable(new FluidStack(targetFluidState.getFluid(), 1).getTranslationKey()));

                componentWidth = textRenderer.getWidth(component);

                textRenderer.draw(poseStack, component, (int)(x + 35 + (162 - componentWidth) * .5f), y + 58, 0);
            }
        }
    }

    @Override
    protected void renderTooltipNormalView(MatrixStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        if(isPointWithinBounds(206, 17, 16, 52, mouseX, mouseY)) {
            //Fluid meter

            List<Text> components = new ArrayList<>(2);

            boolean fluidEmpty =  handler.getFluid().isEmpty();

            long fluidAmount = fluidEmpty?0:handler.getFluid().getMilliBucketsAmount();

            Text tooltipComponent = Text.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                    FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(FluidUtils.
                            convertDropletsToMilliBuckets(handler.getTankCapacity())));

            if(!fluidEmpty) {
                tooltipComponent = Text.translatable(handler.getFluid().getTranslationKey()).append(" ").
                        append(tooltipComponent);
            }

            components.add(tooltipComponent);

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
