package me.jddev0.ep.screen.base;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.InsertExtractFluidFromFluidMeterC2SPacket;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class EnergizedPowerBaseContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected final ResourceLocation MACHINE_SPRITES_TEXTURE = EPAPI.id("textures/gui/container/sprites/machine_sprites.png");

    public EnergizedPowerBaseContainerScreen(T menu, Inventory inventory, Component titleComponent) {
        super(menu, inventory, titleComponent);
    }

    protected void renderFluidMeterContent(GuiGraphics drawContext, FluidStack fluidStack, long tankCapacity, int x, int y,
                                           int w, int h) {
        RenderSystem.enableBlend();
        drawContext.pose().pushPose();

        drawContext.pose().translate(x, y, 0);

        renderFluidStack(drawContext, fluidStack, tankCapacity, w, h);

        drawContext.pose().popPose();
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.disableBlend();
    }

    private void renderFluidStack(GuiGraphics drawContext, FluidStack fluidStack, long tankCapacity, int w, int h) {
        if(fluidStack.isEmpty())
            return;

        Fluid fluid = fluidStack.getFluid();
        TextureAtlasSprite stillFluidSprite = FluidVariantRendering.getSprite(fluidStack.getFluidVariant());
        if(stillFluidSprite == null)
            stillFluidSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).
                    apply(MissingTextureAtlasSprite.getLocation());

        int fluidColorTint = FluidVariantRendering.getColor(fluidStack.getFluidVariant());

        int fluidMeterPos = tankCapacity == -1 || (fluidStack.getDropletsAmount() > 0 && fluidStack.getDropletsAmount() == tankCapacity)?
                0:(int)(h - ((fluidStack.getDropletsAmount() <= 0 || tankCapacity == 0)?0:
                (Math.min(fluidStack.getDropletsAmount(), tankCapacity - 1) * h / tankCapacity + 1)));

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor((fluidColorTint >> 16 & 0xFF) / 255.f,
                (fluidColorTint >> 8 & 0xFF) / 255.f, (fluidColorTint & 0xFF) / 255.f,
                (fluidColorTint >> 24 & 0xFF) / 255.f);

        Matrix4f mat = drawContext.pose().last().pose();

        for(int yOffset = h;yOffset > fluidMeterPos;yOffset -= 16) {
            for(int xOffset = 0;xOffset < w;xOffset += 16) {
                int width = Math.min(w - xOffset, 16);
                int height = Math.min(yOffset - fluidMeterPos, 16);

                float u0 = stillFluidSprite.getU0();
                float u1 = stillFluidSprite.getU1();
                float v0 = stillFluidSprite.getV0();
                float v1 = stillFluidSprite.getV1();
                u1 = u1 - ((16 - width) / 16.f * (u1 - u0));
                v0 = v0 - ((16 - height) / 16.f * (v0 - v1));

                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferBuilder.addVertex(mat, xOffset, yOffset, 0).setUv(u0, v1);
                bufferBuilder.addVertex(mat, xOffset + width, yOffset, 0).setUv(u1, v1);
                bufferBuilder.addVertex(mat, xOffset + width, yOffset - height, 0).setUv(u1, v0);
                bufferBuilder.addVertex(mat, xOffset, yOffset - height, 0).setUv(u0, v0);
                BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
            }
        }
    }

    protected void renderFluidMeterContentTooltip(GuiGraphics guiGraphics, FluidStack fluid, long tankCapacity, int mouseX, int mouseY) {
        boolean fluidEmpty = fluid.isEmpty();

        long fluidAmount = fluidEmpty?0:fluid.getMilliBucketsAmount();

        List<Component> tooltipComponents = new ArrayList<>(3);

        Component tooltipComponent = Component.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(FluidUtils.
                        convertDropletsToMilliBuckets(tankCapacity)));

        if(!fluidEmpty) {
            tooltipComponent = Component.translatable(fluid.getTranslationKey()).append(" ").
                    append(tooltipComponent);
        }

        tooltipComponents.add(tooltipComponent);

        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.translatable("tooltip.energizedpower.fluid_meter.insert_fluid.txt").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        tooltipComponents.add(Component.translatable("tooltip.energizedpower.fluid_meter.extract_fluid.txt").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

        guiGraphics.renderTooltip(font, tooltipComponents, Optional.empty(), mouseX, mouseY);
    }

    protected boolean handleFluidMeterClick(BlockPos blockPos, int tank, int mouseButton) {
        ItemStack carriedItemStack = menu.getCarried();

        Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(carriedItemStack, ContainerItemContext.withConstant(carriedItemStack));
        if(fluidStorage != null) {
            boolean insertExtract;
            if(mouseButton == InputConstants.MOUSE_BUTTON_LEFT) {
                insertExtract = true;
            }else if(mouseButton == InputConstants.MOUSE_BUTTON_RIGHT) {
                insertExtract = false;
            }else {
                return false;
            }

            ModMessages.sendToServer(new InsertExtractFluidFromFluidMeterC2SPacket(blockPos, tank, insertExtract));
            return true;
        }

        return false;
    }
}
