package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeRedstoneModeC2SPacket;
import me.jddev0.ep.networking.packet.ChangeStoneSolidifierRecipeIndexC2SPacket;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class StoneSolidifierScreen extends AbstractGenericEnergyStorageContainerScreen<StoneSolidifierMenu> {
    private final ResourceLocation CONFIGURATION_ICONS_TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/machine_configuration/configuration_buttons.png");

    public StoneSolidifierScreen(StoneSolidifierMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/stone_solidifier.png"), 8, 17);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;

            int diff = 0;

            //Up button
            if(isHovering(85, 19, 11, 12, mouseX, mouseY)) {
                diff = 1;
                clicked = true;
            }

            //Down button
            if(isHovering(116, 19, 11, 12, mouseX, mouseY)) {
                diff = -1;
                clicked = true;
            }

            if(diff != 0) {
                ModMessages.sendToServer(new ChangeStoneSolidifierRecipeIndexC2SPacket(menu.getBlockEntity().getBlockPos(),
                        diff == 1));
            }

            if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
                //Redstone Mode

                ModMessages.sendToServer(new ChangeRedstoneModeC2SPacket(menu.getBlockEntity().getBlockPos()));
                clicked = true;
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        for(int i = 0;i < 2;i++) {
            renderFluidMeterContent(i, guiGraphics, x, y);
            renderFluidMeterOverlay(i, guiGraphics, x, y);
        }

        renderCurrentRecipeOuput(guiGraphics, x, y);

        renderButtons(guiGraphics, x, y, mouseX, mouseY);

        renderProgressArrows(guiGraphics, x, y);

        renderConfiguration(guiGraphics, x, y, mouseX, mouseY);
    }

    private void renderFluidMeterContent(int tank, GuiGraphics guiGraphics, int x, int y) {
        RenderSystem.enableBlend();
        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(x + (tank == 0?44:152), y + 17, 0);

        renderFluidStack(tank, guiGraphics);

        guiGraphics.pose().popPose();
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.disableBlend();
    }

    private void renderFluidStack(int tank, GuiGraphics guiGraphics) {
        FluidStack fluidStack = menu.getFluid(tank);
        if(fluidStack.isEmpty())
            return;

        int capacity = menu.getTankCapacity(tank);

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation stillFluidImageId = fluidTypeExtensions.getStillTexture(fluidStack);
        TextureAtlasSprite stillFluidSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).
                apply(stillFluidImageId);

        int fluidColorTint = fluidTypeExtensions.getTintColor(fluidStack);

        int fluidMeterPos = 52 - ((fluidStack.getAmount() <= 0 || capacity == 0)?0:
                (Math.min(fluidStack.getAmount(), capacity - 1) * 52 / capacity + 1));

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor((fluidColorTint >> 16 & 0xFF) / 255.f,
                (fluidColorTint >> 8 & 0xFF) / 255.f, (fluidColorTint & 0xFF) / 255.f,
                (fluidColorTint >> 24 & 0xFF) / 255.f);

        Matrix4f mat = guiGraphics.pose().last().pose();

        for(int yOffset = 52;yOffset > fluidMeterPos;yOffset -= 16) {
            int height = Math.min(yOffset - fluidMeterPos, 16);

            float u0 = stillFluidSprite.getU0();
            float u1 = stillFluidSprite.getU1();
            float v0 = stillFluidSprite.getV0();
            float v1 = stillFluidSprite.getV1();
            v0 = v0 - ((16 - height) / 16.f * (v0 - v1));

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferBuilder.vertex(mat, 0, yOffset, 0).uv(u0, v1).endVertex();
            bufferBuilder.vertex(mat, 16, yOffset, 0).uv(u1, v1).endVertex();
            bufferBuilder.vertex(mat, 16, yOffset - height, 0).uv(u1, v0).endVertex();
            bufferBuilder.vertex(mat, 0, yOffset - height, 0).uv(u0, v0).endVertex();
            tesselator.end();
        }
    }

    private void renderFluidMeterOverlay(int tank, GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(TEXTURE, x + (tank == 0?44:152), y + 17, 176, 53, 16, 52);
    }

    private void renderCurrentRecipeOuput(GuiGraphics guiGraphics, int x, int y) {
        RecipeHolder<StoneSolidifierRecipe> currentRecipe = menu.getCurrentRecipe();
        if(currentRecipe == null)
            return;

        ItemStack output = currentRecipe.value().getOutput();
        if(!output.isEmpty()) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.f, 0.f, 100.f);

            guiGraphics.renderItem(output, x + 98, y + 17, 98 + 17 * this.imageWidth);

            guiGraphics.pose().popPose();
        }
    }

    private void renderButtons(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        //Up button
        if(isHovering(85, 19, 11, 12, mouseX, mouseY)) {
            guiGraphics.blit(TEXTURE, x + 85, y + 19, 176, 135, 11, 12);
        }

        //Down button
        if(isHovering(116, 19, 11, 12, mouseX, mouseY)) {
            guiGraphics.blit(TEXTURE, x + 116, y + 19, 187, 135, 11, 12);
        }
    }

    private void renderProgressArrows(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCraftingActive()) {
            guiGraphics.blit(TEXTURE, x + 69, y + 45, 176, 106, menu.getScaledProgressArrowSize(), 14);
            guiGraphics.blit(TEXTURE, x + 143 - menu.getScaledProgressArrowSize(), y + 45,
                    196 - menu.getScaledProgressArrowSize(), 120, menu.getScaledProgressArrowSize(), 14);
        }
    }

    private void renderConfiguration(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        RedstoneMode redstoneMode = menu.getRedstoneMode();
        int ordinal = redstoneMode.ordinal();

        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            guiGraphics.blit(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 20 * ordinal, 20, 20, 20);
        }else {
            guiGraphics.blit(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 20 * ordinal, 0, 20, 20);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        for(int i = 0;i < 2;i++) {
            //Fluid meter

            if(isHovering(i == 0?44:152, 17, 16, 52, mouseX, mouseY)) {
                List<Component> components = new ArrayList<>(2);

                boolean fluidEmpty =  menu.getFluid(i).isEmpty();

                int fluidAmount = fluidEmpty?0:menu.getFluid(i).getAmount();

                Component tooltipComponent = Component.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                        FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(menu.getTankCapacity(i)));

                if(!fluidEmpty) {
                    tooltipComponent = Component.translatable(menu.getFluid(i).getTranslationKey()).append(" ").
                            append(tooltipComponent);
                }

                components.add(tooltipComponent);

                guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
            }
        }

        //Current recipe
        RecipeHolder<StoneSolidifierRecipe> currentRecipe = menu.getCurrentRecipe();
        if(currentRecipe != null) {
            if(isHovering(98, 17, 16, 16, mouseX, mouseY)) {
                ItemStack output = currentRecipe.value().getOutput();
                if(!output.isEmpty()) {
                    List<Component> components = new ArrayList<>(2);
                    components.add(Component.translatable("tooltip.energizedpower.count_with_item.txt", output.getCount(),
                            output.getHoverName()));

                    guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
                }
            }
        }

        //Up button
        if(isHovering(85, 19, 11, 12, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.stone_solidifier.btn.up"));

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }

        //Down button
        if(isHovering(116, 19, 11, 12, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.stone_solidifier.btn.down"));

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }

        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            //Redstone Mode

            RedstoneMode redstoneMode = menu.getRedstoneMode();

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.machine_configuration.redstone_mode." + redstoneMode.getSerializedName()));

            guiGraphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
