package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class FluidTransposerScreen
        extends ConfigurableUpgradableEnergyStorageContainerScreen<FluidTransposerMenu> {
    public FluidTransposerScreen(FluidTransposerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/fluid_transposer.png"),
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png"));
    }

    @Override
    protected boolean mouseClickedNormalView(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedNormalView(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            if(isPointWithinBounds(114, 47, 20, 20, mouseX, mouseY)) {
                //Mode button

                ModMessages.sendClientPacketToServer(new SetCheckboxC2SPacket(handler.getBlockEntity().getPos(), 0,
                        handler.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void renderBgNormalView(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBgNormalView(poseStack, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        renderFluidMeterContent(poseStack, x, y);
        renderFluidMeterOverlay(poseStack, x, y);

        renderButtons(poseStack, x, y, mouseX, mouseY);
        renderProgressArrow(poseStack, x, y);
    }

    private void renderFluidMeterContent(MatrixStack poseStack, int x, int y) {
        RenderSystem.enableBlend();
        poseStack.push();

        poseStack.translate(x + 152, y + 17, 0);

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
        drawTexture(poseStack, x + 152, y + 17, 176, 53, 16, 52);
    }

    private void renderButtons(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(isPointWithinBounds(114, 47, 20, 20, mouseX, mouseY))
            drawTexture(poseStack, x + 114, y + 47, 176, 135, 20, 20);

        ItemStack output = new ItemStack(handler.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?Items.BUCKET:Items.WATER_BUCKET);
        poseStack.push();
        poseStack.translate(0.f, 0.f, 100.f);

        itemRenderer.renderInGuiWithOverrides(output, x + 116, y + 49, 116 + 49 * this.backgroundWidth);

        poseStack.pop();

        RenderSystem.setShaderTexture(0, TEXTURE);
    }

    private void renderProgressArrow(MatrixStack poseStack, int x, int y) {
        int arrowPosY = handler.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING?106:120;

        drawTexture(poseStack, x + 114, y + 19, 176, arrowPosY, 20, 14);

        if(handler.isCraftingActive()) {
            if(handler.getMode() == FluidTransposerBlockEntity.Mode.EMPTYING)
                drawTexture(poseStack, x + 114, y + 19, 196, arrowPosY, handler.getScaledProgressArrowSize(), 14);
            else
                drawTexture(poseStack, x + 134 - handler.getScaledProgressArrowSize(), y + 19,
                        216 - handler.getScaledProgressArrowSize(), arrowPosY, handler.getScaledProgressArrowSize(), 14);
        }
    }

    @Override
    protected void renderTooltipNormalView(MatrixStack poseStack, int mouseX, int mouseY) {
        super.renderTooltipNormalView(poseStack, mouseX, mouseY);

        if(isPointWithinBounds(152, 17, 16, 52, mouseX, mouseY)) {
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

        if(isPointWithinBounds(114, 47, 20, 20, mouseX, mouseY)) {
            //Mode button

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.fluid_transposer.mode." +
                    handler.getMode().asString()));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
