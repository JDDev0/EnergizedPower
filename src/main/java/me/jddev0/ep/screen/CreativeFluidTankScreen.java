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
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class CreativeFluidTankScreen extends EnergizedPowerBaseContainerScreen<CreativeFluidTankMenu> {
    private final Identifier TEXTURE;

    public CreativeFluidTankScreen(CreativeFluidTankMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/creative_fluid_tank.png");
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();

        if(mouseButton == 0) {
            boolean clicked = false;

            if(isPointWithinBounds(48, 17, 80, 52, mouseX, mouseY)) {
                //Fluid Tank

                FluidVariant fluidVariant = FluidVariant.blank();

                ItemStack carriedItemStack = handler.getCursorStack();

                Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(carriedItemStack,
                        ContainerItemContext.withConstant(carriedItemStack));
                if(fluidStorage != null) {
                    for(StorageView<FluidVariant> fluidView:fluidStorage) {
                        fluidVariant = fluidView.getResource();

                        break;
                    }
                }

                FluidStack fluidStack = new FluidStack(fluidVariant, 1);

                ModMessages.sendClientPacketToServer(new SetCreativeFluidTankFluidStackC2SPacket(handler.getBlockEntity().getPos(), fluidStack));
                clicked = true;
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        renderFluidMeterContent(drawContext, handler.getFluid(0), handler.getTankCapacity(0), x + 48, y + 17, 80, 52);
        renderFluidMeterOverlay(drawContext, x, y);
    }

    private void renderFluidMeterOverlay(DrawContext drawContext, int x, int y) {
        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 48, y + 17, 36, 0, 80, 52, 256, 256);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(drawContext, mouseX, mouseY);

        if(isPointWithinBounds(48, 17, 80, 52, mouseX, mouseY)) {
            //Fluid meter

            List<Text> components = new ArrayList<>(2);

            boolean fluidEmpty = handler.getFluid(0).isEmpty();

            Text tooltipComponent;
            if(fluidEmpty) {
                tooltipComponent = Text.literal("0 / ").append(Text.translatable("tooltip.energizedpower.infinite.txt").
                        formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC));
            }else {
                tooltipComponent = Text.translatable(handler.getFluid(0).getTranslationKey()).append(" ").
                        append(Text.translatable("tooltip.energizedpower.infinite.txt").
                                formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC)).append(" / ").
                        append(Text.translatable("tooltip.energizedpower.infinite.txt").
                                formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC));
            }

            components.add(tooltipComponent);

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
