package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.networking.packet.SetFluidTankFilterC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
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
public class FluidTankScreen extends EnergizedPowerBaseContainerScreen<FluidTankMenu> {
    private final Identifier TEXTURE;

    public FluidTankScreen(FluidTankMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        TEXTURE = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/fluid_tank.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isPointWithinBounds(158, 16, 11, 11, mouseX, mouseY)) {
                //Ignore NBT checkbox
                ModMessages.sendClientPacketToServer(
                        new SetCheckboxC2SPacket(handler.getBlockEntity().getPos(), 0, !handler.isIgnoreNBT()));

                clicked = true;
            }

            if(isPointWithinBounds(151, 34, 18, 18, mouseX, mouseY)) {
                //Fluid Filter

                FluidVariant fluidFilterVariant = FluidVariant.blank();

                ItemStack carriedItemStack = handler.getCursorStack();

                Storage<FluidVariant> fluidStorage = FluidStorage.ITEM.find(carriedItemStack,
                        ContainerItemContext.withConstant(carriedItemStack));
                if(fluidStorage != null) {
                    for(StorageView<FluidVariant> fluidView:fluidStorage) {
                        fluidFilterVariant = fluidView.getResource();

                        break;
                    }
                }

                FluidStack fluidFilter = new FluidStack(fluidFilterVariant, 1);
                ModMessages.sendClientPacketToServer(
                        new SetFluidTankFilterC2SPacket(handler.getBlockEntity().getPos(), fluidFilter));
                clicked = true;
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawBackground(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawTexture(poseStack, x, y, 0, 0, backgroundWidth, backgroundHeight);

        for(int i = 0;i < 2;i++) {
            if(i == 0)
                renderFluidMeterContent(poseStack, handler.getFluid(0), handler.getTankCapacity(0), x + 80, y + 17, 16, 52);
            else
                renderFluidMeterContent(poseStack, handler.getFluid(1), -1, x + 152, y + 35, 16, 16);

            renderFluidMeterOverlay(poseStack, x, y, i);
        }

        renderCheckboxes(poseStack, x, y, mouseX, mouseY);
    }

    private void renderFluidMeterOverlay(MatrixStack poseStack, int x, int y, int tank) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        if(tank == 0)
            drawTexture(poseStack, x + 80, y + 17, 176, 0, 16, 52);
        else if(tank == 1)
            drawTexture(poseStack, x + 152, y + 35, 176, 64, 16, 16);
    }

    private void renderCheckboxes(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(handler.isIgnoreNBT()) {
            //Ignore NBT checkbox

            drawTexture(poseStack, x + 158, y + 16, 176, 53, 11, 11);
        }
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);

        super.render(poseStack, mouseX, mouseY, delta);

        drawMouseoverTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack poseStack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(poseStack, mouseX, mouseY);

        if(isPointWithinBounds(80, 17, 16, 52, mouseX, mouseY)) {
            //Fluid meter

            List<Text> components = new ArrayList<>(2);

            boolean fluidEmpty =  handler.getFluid(0).isEmpty();

            long fluidAmount = fluidEmpty?0:handler.getFluid(0).getMilliBucketsAmount();

            Text tooltipComponent = Text.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                    FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(FluidUtils.
                            convertDropletsToMilliBuckets(handler.getTankCapacity(0))));

            if(!fluidEmpty) {
                tooltipComponent = Text.translatable(handler.getFluid(0).getTranslationKey()).append(" ").
                        append(tooltipComponent);
            }

            components.add(tooltipComponent);

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }

        if(isPointWithinBounds(158, 16, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.fluid_tanks.cbx.ignore_nbt"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }

        if(isPointWithinBounds(151, 34, 18, 18, mouseX, mouseY)) {
            //Fluid Filter

            List<Text> components = new ArrayList<>(2);

            FluidStack fluidFilter = handler.getFluid(1);

            if(fluidFilter.isEmpty())
                components.add(Text.translatable("tooltip.energizedpower.fluid_tanks.fluid_filter.no_filter_set"));
            else
                components.add(Text.translatable("tooltip.energizedpower.fluid_tanks.fluid_filter.filter_set",
                        Text.translatable(fluidFilter.getTranslationKey())));

            components.add(Text.empty());

            components.add(Text.translatable("tooltip.energizedpower.fluid_tanks.fluid_filter.txt.1").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
            components.add(Text.translatable("tooltip.energizedpower.fluid_tanks.fluid_filter.txt.2").
                    formatted(Formatting.GRAY, Formatting.ITALIC));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
