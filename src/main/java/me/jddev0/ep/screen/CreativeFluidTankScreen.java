package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCreativeFluidTankFluidStackC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
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

                FluidStack fluidStack = FluidStack.EMPTY;

                ItemStack carriedItemStack = menu.getCarried();

                LazyOptional<IFluidHandlerItem> fluidStorageLazyOptional = carriedItemStack.getCapability(Capabilities.FLUID_HANDLER_ITEM);
                if(fluidStorageLazyOptional.isPresent()) {
                    IFluidHandlerItem fluidStorage = fluidStorageLazyOptional.orElse(null);
                    if(fluidStorage != null && fluidStorage.getTanks() > 0)
                        fluidStack = fluidStorage.getFluidInTank(0);
                }

                ModMessages.sendToServer(new SetCreativeFluidTankFluidStackC2SPacket(menu.getBlockEntity().getBlockPos(), fluidStack));
                clicked = true;
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderFluidMeterContent(guiGraphics, menu.getFluid(0), menu.getTankCapacity(0), x + 48, y + 17, 80, 52);
        renderFluidMeterOverlay(guiGraphics, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(TEXTURE, x + 48, y + 17, 176, 0, 80, 52);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

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

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
