package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeComparatorModeC2SPacket;
import me.jddev0.ep.networking.packet.ChangeCurrentRecipeIndexC2SPacket;
import me.jddev0.ep.networking.packet.ChangeRedstoneModeC2SPacket;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class FiltrationPlantScreen extends AbstractGenericEnergyStorageContainerScreen<FiltrationPlantMenu> {
    private final ResourceLocation CONFIGURATION_ICONS_TEXTURE =
            new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/machine_configuration/configuration_buttons.png");
    private final ResourceLocation UPGRADE_VIEW_TEXTURE =
            new ResourceLocation(EnergizedPowerMod.MODID,
                    "textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png");

    public FiltrationPlantScreen(FiltrationPlantMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/filtration_plant.png"), 8, 17);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;

            if(!menu.isInUpgradeModuleView()) {
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
                    ModMessages.sendToServer(new ChangeCurrentRecipeIndexC2SPacket(menu.getBlockEntity().getBlockPos(),
                            diff == 1));
                }
            }

            if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
                //Upgrade view

                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                clicked = true;
            }else if(isHovering(-22, 26, 20, 20, mouseX, mouseY)) {
                //Redstone Mode

                ModMessages.sendToServer(new ChangeRedstoneModeC2SPacket(menu.getBlockEntity().getBlockPos()));
                clicked = true;
            }else if(isHovering(-22, 50, 20, 20, mouseX, mouseY)) {
                //Comparator Mode

                ModMessages.sendToServer(new ChangeComparatorModeC2SPacket(menu.getBlockEntity().getBlockPos()));
                clicked = true;
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        if(menu.isInUpgradeModuleView()) {
            RenderSystem.setShaderTexture(0, UPGRADE_VIEW_TEXTURE);
            blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);
            RenderSystem.setShaderTexture(0, TEXTURE);
        }else {
            for(int i = 0;i < 2;i++) {
                renderFluidMeterContent(i, poseStack, x, y);
                renderFluidMeterOverlay(i, poseStack, x, y);
            }

            renderCurrentRecipeOutput(poseStack, x, y);

            renderButtons(poseStack, x, y, mouseX, mouseY);

            renderProgressArrows(poseStack, x, y);
        }

        renderConfiguration(poseStack, x, y, mouseX, mouseY);
    }

    private void renderFluidMeterContent(int tank, PoseStack poseStack, int x, int y) {
        RenderSystem.enableBlend();
        poseStack.pushPose();

        poseStack.translate(x + (tank == 0?44:152), y + 17, 0);

        renderFluidStack(tank, poseStack);

        poseStack.popPose();
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.disableBlend();
    }

    private void renderFluidStack(int tank, PoseStack poseStack) {
        FluidStack fluidStack = menu.getFluid(tank);
        if(fluidStack.isEmpty())
            return;

        int capacity = menu.getTankCapacity(tank);

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation stillFluidImageId = fluidTypeExtensions.getStillTexture(fluidStack);
        if(stillFluidImageId == null)
            stillFluidImageId = new ResourceLocation("air");
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

        Matrix4f mat = poseStack.last().pose();

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

    private void renderFluidMeterOverlay(int tank, PoseStack poseStack, int x, int y) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(poseStack, x + (tank == 0?44:152), y + 17, 176, 53, 16, 52);
    }

    private void renderCurrentRecipeOutput(PoseStack poseStack, int x, int y) {
        FiltrationPlantRecipe currentRecipe = menu.getCurrentRecipe();
        if(currentRecipe == null)
            return;

        ResourceLocation icon = currentRecipe.getIcon();
        ItemStack itemStackIcon = new ItemStack(ForgeRegistries.ITEMS.getValue(icon));
        if(!itemStackIcon.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.f, 0.f, 100.f);

            itemRenderer.renderAndDecorateItem(itemStackIcon, x + 98, y + 17, 98 + 17 * this.imageWidth);

            poseStack.popPose();

            RenderSystem.setShaderTexture(0, TEXTURE);
        }
    }

    private void renderButtons(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);

        //Up button
        if(isHovering(85, 19, 11, 12, mouseX, mouseY)) {
            blit(poseStack, x + 85, y + 19, 176, 135, 11, 12);
        }

        //Down button
        if(isHovering(116, 19, 11, 12, mouseX, mouseY)) {
            blit(poseStack, x + 116, y + 19, 187, 135, 11, 12);
        }
    }

    private void renderProgressArrows(PoseStack poseStack, int x, int y) {
        if(menu.isCraftingActive()) {
            for(int i = 0;i < 2;i++) {
                blit(poseStack, x + 67, y + 34 + 27*i, 176, 106, menu.getScaledProgressArrowSize(), 9);
            }
        }
    }

    private void renderConfiguration(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, CONFIGURATION_ICONS_TEXTURE);

        //Upgrade view
        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            blit(poseStack, x - 22, y + 2, 40, 80, 20, 20);
        }else if(menu.isInUpgradeModuleView()) {
            blit(poseStack, x - 22, y + 2, 20, 80, 20, 20);
        }else {
            blit(poseStack, x - 22, y + 2, 0, 80, 20, 20);
        }

        RedstoneMode redstoneMode = menu.getRedstoneMode();
        int ordinal = redstoneMode.ordinal();

        if(isHovering(-22, 26, 20, 20, mouseX, mouseY)) {
            blit(poseStack, x - 22, y + 26, 20 * ordinal, 20, 20, 20);
        }else {
            blit(poseStack, x - 22, y + 26, 20 * ordinal, 0, 20, 20);
        }

        ComparatorMode comparatorMode = menu.getComparatorMode();
        ordinal = comparatorMode.ordinal();

        if(isHovering(-22, 50, 20, 20, mouseX, mouseY)) {
            blit(poseStack, x - 22, y + 50, 20 * ordinal, 60, 20, 20);
        }else {
            blit(poseStack, x - 22, y + 50, 20 * ordinal, 40, 20, 20);
        }
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);

        if(!menu.isInUpgradeModuleView()) {
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

                    renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
                }
            }

            //Current recipe
            FiltrationPlantRecipe currentRecipe = menu.getCurrentRecipe();
            if(currentRecipe != null && isHovering(98, 17, 16, 16, mouseX, mouseY)) {
                List<Component> components = new ArrayList<>(2);

                ItemStack[] maxOutputs = currentRecipe.getMaxOutputCounts();
                for(int i = 0;i < maxOutputs.length;i++) {
                    ItemStack output = maxOutputs[i];
                    if(output.isEmpty())
                        continue;

                    components.add(Component.empty().
                            append(output.getHoverName()).
                            append(Component.literal(": ")).
                            append(Component.translatable("recipes.energizedpower.transfer.output_percentages"))
                    );

                    double[] percentages = (i == 0?currentRecipe.getOutput():currentRecipe.getSecondaryOutput()).
                            percentages();
                    for(int j = 0;j < percentages.length;j++)
                        components.add(Component.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

                    renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
                }

                //Remove trailing empty line
                if(!components.isEmpty())
                    components.remove(components.size() - 1);

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }

            //Up button
            if(isHovering(85, 19, 11, 12, mouseX, mouseY)) {
                List<Component> components = new ArrayList<>(2);
                components.add(Component.translatable("tooltip.energizedpower.filtration_plant.btn.up"));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }

            //Down button
            if(isHovering(116, 19, 11, 12, mouseX, mouseY)) {
                List<Component> components = new ArrayList<>(2);
                components.add(Component.translatable("tooltip.energizedpower.filtration_plant.btn.down"));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }

            //Missing Charcoal Filter
            for(int i = 0;i < 2;i++) {
                if(isHovering(62 + 72*i, 44, 16, 16, mouseX, mouseY) &&
                        menu.getSlot(4 * 9 + i).getItem().isEmpty()) {
                    List<Component> components = new ArrayList<>(2);
                    components.add(Component.translatable("tooltip.energizedpower.filtration_plant.charcoal_filter_missing").
                            withStyle(ChatFormatting.RED));

                    renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
                }
            }
        }

        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            //Upgrade view

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.upgrade_view.button." +
                    (menu.isInUpgradeModuleView()?"close":"open")));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(-22, 26, 20, 20, mouseX, mouseY)) {
            //Redstone Mode

            RedstoneMode redstoneMode = menu.getRedstoneMode();

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.machine_configuration.redstone_mode." + redstoneMode.getSerializedName()));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(-22, 50, 20, 20, mouseX, mouseY)) {
            //Comparator Mode

            ComparatorMode comparatorMode = menu.getComparatorMode();

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.machine_configuration.comparator_mode." + comparatorMode.getSerializedName()));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
