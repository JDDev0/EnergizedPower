package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class FluidTankScreen extends HandledScreen<FluidTankMenu> {
    private final Identifier TEXTURE;

    public FluidTankScreen(FluidTankMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        TEXTURE = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/fluid_tank.png");
    }

    public FluidTankBlock.Tier getTier() {
        return handler.getTier();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isPointWithinBounds(158, 16, 11, 11, mouseX, mouseY)) {
                //Ignore NBT checkbox
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(handler.getBlockEntity().getPos());
                buf.writeInt(0);
                buf.writeBoolean(!handler.isIgnoreNBT());
                ClientPlayNetworking.send(ModMessages.SET_CHECKBOX_ID, buf);
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

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(handler.getBlockEntity().getPos());
                fluidFilter.toPacket(buf);
                ClientPlayNetworking.send(ModMessages.SET_FLUID_TANK_FILTER_ID, buf);
                clicked = true;
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawContext.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        for(int i = 0;i < 2;i++) {
            renderFluidMeterContent(drawContext, x, y, i);
            renderFluidMeterOverlay(drawContext, x, y, i);
        }

        renderCheckboxes(drawContext, x, y, mouseX, mouseY);
    }

    private void renderFluidMeterContent(DrawContext drawContext, int x, int y, int tank) {
        RenderSystem.enableBlend();
        drawContext.getMatrices().push();

        if(tank == 0)
            drawContext.getMatrices().translate(x + 80, y + 17, 0);
        else if(tank == 1)
            drawContext.getMatrices().translate(x + 152, y + 19, 0);

        renderFluidStack(drawContext, tank);

        drawContext.getMatrices().pop();
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.disableBlend();
    }

    private void renderFluidStack(DrawContext drawContext, int tank) {
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

        int fluidMeterPos = switch(tank) {
            case 0 ->  52 - (int)((fluidStack.getDropletsAmount() <= 0 || capacity == 0)?0:
                    (Math.min(fluidStack.getDropletsAmount(), capacity - 1) * 52 / capacity + 1));
            case 1 -> 16;
            default -> 0;
        };

        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((fluidColorTint >> 16 & 0xFF) / 255.f,
                (fluidColorTint >> 8 & 0xFF) / 255.f, (fluidColorTint & 0xFF) / 255.f,
                (fluidColorTint >> 24 & 0xFF) / 255.f);

        Matrix4f mat = drawContext.getMatrices().peek().getPositionMatrix();

        for(int yOffset = tank == 0?52:32;yOffset > fluidMeterPos;yOffset -= 16) {
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

    private void renderFluidMeterOverlay(DrawContext drawContext, int x, int y, int tank) {
        if(tank == 0)
            drawContext.drawTexture(TEXTURE, x + 80, y + 17, 176, 0, 16, 52);
        else if(tank == 1)
            drawContext.drawTexture(TEXTURE, x + 152, y + 35, 176, 64, 16, 16);
    }

    private void renderCheckboxes(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        if(handler.isIgnoreNBT()) {
            //Ignore NBT checkbox

            drawContext.drawTexture(TEXTURE, x + 158, y + 16, 176, 53, 11, 11);
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(drawContext, mouseX, mouseY);

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

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }

        if(isPointWithinBounds(158, 16, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.fluid_tanks.cbx.ignore_nbt"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
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

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
