package me.jddev0.ep.screen.base;

import com.mojang.blaze3d.platform.InputConstants;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.client.rendering.FluidTankRenderState;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.util.FluidRenderUtils;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.InsertExtractFluidFromFluidMeterC2SPacket;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.joml.Matrix3x2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public abstract class EnergizedPowerBaseContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    protected final Identifier MACHINE_SPRITES_TEXTURE = EPAPI.id("textures/gui/container/sprites/machine_sprites.png");

    public EnergizedPowerBaseContainerScreen(T menu, Inventory inventory, Component titleComponent) {
        super(menu, inventory, titleComponent);
    }

    public EnergizedPowerBaseContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                             int imageWidth, int imageHeight) {
        super(menu, inventory, titleComponent, imageWidth, imageHeight);
    }

    protected void renderFluidMeterContent(GuiGraphicsExtractor drawContext, FluidStack fluidStack, long tankCapacity, int x, int y,
                                           int w, int h) {
        drawContext.pose().pushMatrix();

        drawContext.pose().translate(x, y);

        renderFluidStack(drawContext, fluidStack, tankCapacity, w, h);

        drawContext.pose().popMatrix();
    }

    private void renderFluidStack(GuiGraphicsExtractor drawContext, FluidStack fluidStack, long tankCapacity, int w, int h) {
        if(fluidStack.isEmpty())
            return;

        Fluid fluid = fluidStack.getFluid();
        TextureAtlasSprite stillFluidSprite = FluidRenderUtils.getStillSprite(fluidStack.getFluidVariant());

        int fluidColorTint = FluidRenderUtils.getTintColor(fluidStack.getFluidVariant());

        int fluidMeterPos = tankCapacity == -1 || (fluidStack.getDropletsAmount() > 0 && fluidStack.getDropletsAmount() == tankCapacity)?
                0:(int)(h - ((fluidStack.getDropletsAmount() <= 0 || tankCapacity == 0)?0:
                (Math.min(fluidStack.getDropletsAmount(), tankCapacity - 1) * h / tankCapacity + 1)));

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

                AbstractTexture abstractTexture = this.minecraft.getTextureManager().getTexture(stillFluidSprite.atlasLocation());
                drawContext.guiRenderState.addGuiElement(new FluidTankRenderState(
                        RenderPipelines.GUI_TEXTURED, TextureSetup.singleTexture(abstractTexture.getTextureView(), abstractTexture.getSampler()),
                        new Matrix3x2f(drawContext.pose()),
                        xOffset, yOffset, width, height,
                        u0, u1, v0, v1, fluidColorTint,
                        drawContext.scissorStack.peek()
                ));
            }
        }
    }

    protected void renderFluidMeterContentTooltip(GuiGraphicsExtractor guiGraphics, FluidStack fluid, long tankCapacity, int mouseX, int mouseY) {
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

        guiGraphics.setTooltipForNextFrame(font, tooltipComponents, Optional.empty(), mouseX, mouseY);
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
