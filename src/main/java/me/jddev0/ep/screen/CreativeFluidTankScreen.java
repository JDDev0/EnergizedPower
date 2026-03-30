package me.jddev0.ep.screen;

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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CreativeFluidTankScreen extends EnergizedPowerBaseContainerScreen<CreativeFluidTankMenu> {
    private final Identifier TEXTURE;

    public CreativeFluidTankScreen(CreativeFluidTankMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/creative_fluid_tank.png");
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();

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

        return super.mouseClicked(click, doubled);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float a) {
        super.extractBackground(drawContext, mouseX, mouseY, a);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        drawContext.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        renderFluidMeterContent(drawContext, menu.getFluid(0), menu.getTankCapacity(0), x + 48, y + 17, 80, 52);
        renderFluidMeterOverlay(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(GuiGraphicsExtractor drawContext, int x, int y) {
        drawContext.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 48, y + 17, 36, 0, 80, 52, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor drawContext, int mouseX, int mouseY) {
        super.extractLabels(drawContext, mouseX, mouseY);

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

            drawContext.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
