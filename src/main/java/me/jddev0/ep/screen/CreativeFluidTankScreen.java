package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCreativeFluidTankFluidStackC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CreativeFluidTankScreen extends EnergizedPowerBaseContainerScreen<CreativeFluidTankMenu> {
    private final ResourceLocation TEXTURE;

    public CreativeFluidTankScreen(CreativeFluidTankMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/creative_fluid_tank.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;

            if(isHovering(48, 17, 80, 52, mouseX, mouseY)) {
                //Fluid Tank

                FluidVariant fluidVariant = FluidVariant.blank();

                ItemStack carriedItemStack = menu.getCarried();

                Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(carriedItemStack,
                        ContainerItemContext.withConstant(carriedItemStack));
                if(fluidStorage != null) {
                    for(StorageView<FluidVariant> fluidView:fluidStorage) {
                        fluidVariant = fluidView.getResource();

                        break;
                    }
                }

                FluidStack fluidStack = new FluidStack(fluidVariant, 1);

                ModMessages.sendClientPacketToServer(new SetCreativeFluidTankFluidStackC2SPacket(menu.getBlockEntity().getBlockPos(), fluidStack));
                clicked = true;
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(GuiGraphics drawContext, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        drawContext.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderFluidMeterContent(drawContext, menu.getFluid(0), menu.getTankCapacity(0), x + 48, y + 17, 80, 52);
        renderFluidMeterOverlay(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphics drawContext, int x, int y) {
        drawContext.blit(MACHINE_SPRITES_TEXTURE, x + 48, y + 17, 36, 0, 80, 52);
    }

    @Override
    public void render(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        renderTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics drawContext, int mouseX, int mouseY) {
        super.renderTooltip(drawContext, mouseX, mouseY);

        if(isHovering(48, 17, 80, 52, mouseX, mouseY)) {
            //Fluid meter

            List<Component> components = new ArrayList<>(2);

            boolean fluidEmpty = menu.getFluid(0).isEmpty();

            Component tooltipComponent;
            if(fluidEmpty) {
                tooltipComponent = Component.literal("0 / ").append(Component.translatable("tooltip.energizedpower.infinite.txt").
                        withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
            }else {
                tooltipComponent = Component.translatable(menu.getFluid(0).getTranslationKey()).append(" ").
                        append(Component.translatable("tooltip.energizedpower.infinite.txt").
                                withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC)).append(" / ").
                        append(Component.translatable("tooltip.energizedpower.infinite.txt").
                                withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.ITALIC));
            }

            components.add(tooltipComponent);

            drawContext.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
