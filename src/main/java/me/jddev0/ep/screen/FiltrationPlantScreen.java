package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.networking.packet.ChangeComparatorModeC2SPacket;
import me.jddev0.ep.networking.packet.ChangeCurrentRecipeIndexC2SPacket;
import me.jddev0.ep.networking.packet.ChangeRedstoneModeC2SPacket;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class FiltrationPlantScreen extends AbstractGenericEnergyStorageHandledScreen<FiltrationPlantMenu> {
    private final Identifier CONFIGURATION_ICONS_TEXTURE =
            new Identifier(EnergizedPowerMod.MODID, "textures/gui/machine_configuration/configuration_buttons.png");
    private final Identifier UPGRADE_VIEW_TEXTURE =
            new Identifier(EnergizedPowerMod.MODID,
                    "textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity.png");

    public FiltrationPlantScreen(FiltrationPlantMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/filtration_plant.png"), 8, 17);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;

            if(!handler.isInUpgradeModuleView()) {
                int diff = 0;

                //Up button
                if(isPointWithinBounds(85, 19, 11, 12, mouseX, mouseY)) {
                    diff = 1;
                    clicked = true;
                }

                //Down button
                if(isPointWithinBounds(116, 19, 11, 12, mouseX, mouseY)) {
                    diff = -1;
                    clicked = true;
                }

                if(diff != 0) {
                    ModMessages.sendClientPacketToServer(
                            new ChangeCurrentRecipeIndexC2SPacket(handler.getBlockEntity().getPos(), diff == 1));
                }
            }

            if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
                //Upgrade view

                client.interactionManager.clickButton(handler.syncId, 0);
                clicked = true;
            }else if(isPointWithinBounds(-22, 26, 20, 20, mouseX, mouseY)) {
                //Redstone Mode

                ModMessages.sendClientPacketToServer(new ChangeRedstoneModeC2SPacket(handler.getBlockEntity().getPos()));
                clicked = true;
            }else if(isPointWithinBounds(-22, 50, 20, 20, mouseX, mouseY)) {
                //Comparator Mode

                ModMessages.sendClientPacketToServer(new ChangeComparatorModeC2SPacket(handler.getBlockEntity().getPos()));
                clicked = true;
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.drawBackground(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        if(handler.isInUpgradeModuleView()) {
            drawContext.drawTexture(UPGRADE_VIEW_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        }else {
            for (int i = 0; i < 2; i++) {
                renderFluidMeterContent(i, drawContext, x, y);
                renderFluidMeterOverlay(i, drawContext, x, y);
            }

            renderCurrentRecipeOutput(drawContext, x, y);

            renderButtons(drawContext, x, y, mouseX, mouseY);

            renderProgressArrows(drawContext, x, y);
        }

        renderConfiguration(drawContext, x, y, mouseX, mouseY);
    }

    private void renderFluidMeterContent(int tank, DrawContext drawContext, int x, int y) {
        RenderSystem.enableBlend();
        drawContext.getMatrices().push();

        drawContext.getMatrices().translate(x + (tank == 0?44:152), y + 17, 0);

        renderFluidStack(tank, drawContext);

        drawContext.getMatrices().pop();
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.disableBlend();
    }

    private void renderFluidStack(int tank, DrawContext drawContext) {
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

        int fluidMeterPos = 52 - (int)((fluidStack.getDropletsAmount() <= 0 || capacity == 0)?0:
                (Math.min(fluidStack.getDropletsAmount(), capacity - 1) * 52 / capacity + 1));

        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((fluidColorTint >> 16 & 0xFF) / 255.f,
                (fluidColorTint >> 8 & 0xFF) / 255.f, (fluidColorTint & 0xFF) / 255.f,
                (fluidColorTint >> 24 & 0xFF) / 255.f);

        Matrix4f mat = drawContext.getMatrices().peek().getPositionMatrix();

        for(int yOffset = 52;yOffset > fluidMeterPos;yOffset -= 16) {
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

    private void renderFluidMeterOverlay(int tank, DrawContext drawContext, int x, int y) {
        drawContext.drawTexture(TEXTURE, x + (tank == 0?44:152), y + 17, 176, 53, 16, 52);
    }

    private void renderCurrentRecipeOutput(DrawContext drawContext, int x, int y) {
        FiltrationPlantRecipe currentRecipe = handler.getCurrentRecipe();
        if(currentRecipe == null)
            return;

        Identifier icon = currentRecipe.getIcon();
        ItemStack itemStackIcon = new ItemStack(Registries.ITEM.get(icon));
        if(!itemStackIcon.isEmpty()) {
            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0.f, 0.f, 100.f);

            drawContext.drawItem(itemStackIcon, x + 98, y + 17, 98 + 17 * this.backgroundWidth);

            drawContext.getMatrices().pop();
        }
    }

    private void renderButtons(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        //Up button
        if(isPointWithinBounds(85, 19, 11, 12, mouseX, mouseY)) {
            drawContext.drawTexture(TEXTURE, x + 85, y + 19, 176, 135, 11, 12);
        }

        //Down button
        if(isPointWithinBounds(116, 19, 11, 12, mouseX, mouseY)) {
            drawContext.drawTexture(TEXTURE, x + 116, y + 19, 187, 135, 11, 12);
        }
    }

    private void renderProgressArrows(DrawContext drawContext, int x, int y) {
        if(handler.isCraftingActive()) {
            for(int i = 0;i < 2;i++) {
                drawContext.drawTexture(TEXTURE, x + 67, y + 34 + 27*i, 176, 106, handler.getScaledProgressArrowSize(), 9);
            }
        }
    }

    private void renderConfiguration(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        //Upgrade view
        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 40, 80, 20, 20);
        }else if(handler.isInUpgradeModuleView()) {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 20, 80, 20, 20);
        }else {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 2, 0, 80, 20, 20);
        }

        RedstoneMode redstoneMode = handler.getRedstoneMode();
        int ordinal = redstoneMode.ordinal();

        if(isPointWithinBounds(-22, 26, 20, 20, mouseX, mouseY)) {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 26, 20 * ordinal, 20, 20, 20);
        }else {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 26, 20 * ordinal, 0, 20, 20);
        }

        ComparatorMode comparatorMode = handler.getComparatorMode();
        ordinal = comparatorMode.ordinal();

        if(isPointWithinBounds(-22, 50, 20, 20, mouseX, mouseY)) {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 50, 20 * ordinal, 60, 20, 20);
        }else {
            drawContext.drawTexture(CONFIGURATION_ICONS_TEXTURE, x - 22, y + 50, 20 * ordinal, 40, 20, 20);
        }
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(drawContext, mouseX, mouseY);

        if(!handler.isInUpgradeModuleView()) {
            for(int i = 0;i < 2;i++) {
                //Fluid meter

                if(isPointWithinBounds(i == 0?44:152, 17, 16, 52, mouseX, mouseY)) {
                    List<Text> components = new ArrayList<>(2);

                    boolean fluidEmpty =  handler.getFluid(i).isEmpty();

                    long fluidAmount = fluidEmpty?0:handler.getFluid(i).getMilliBucketsAmount();

                    Text tooltipComponent = Text.translatable("tooltip.energizedpower.fluid_meter.content_amount.txt",
                            FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(FluidUtils.
                                    convertDropletsToMilliBuckets(handler.getTankCapacity(i))));

                    if(!fluidEmpty) {
                        tooltipComponent = Text.translatable(handler.getFluid(i).getTranslationKey()).append(" ").
                                append(tooltipComponent);
                    }

                    components.add(tooltipComponent);

                    drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
                }
            }

            //Current recipe
            FiltrationPlantRecipe currentRecipe = handler.getCurrentRecipe();
            if(currentRecipe != null && isPointWithinBounds(98, 17, 16, 16, mouseX, mouseY)) {
                List<Text> components = new ArrayList<>(2);

                ItemStack[] maxOutputs = currentRecipe.getMaxOutputCounts();
                for(int i = 0;i < maxOutputs.length;i++) {
                    ItemStack output = maxOutputs[i];
                    if(output.isEmpty())
                        continue;

                    components.add(Text.empty().
                            append(output.getName()).
                            append(Text.literal(": ")).
                            append(Text.translatable("recipes.energizedpower.transfer.output_percentages"))
                    );

                    double[] percentages = (i == 0?currentRecipe.getOutput():currentRecipe.getSecondaryOutput()).
                            percentages();
                    for(int j = 0;j < percentages.length;j++)
                        components.add(Text.literal(String.format(Locale.ENGLISH, "%2d • %.2f %%", j + 1, 100 * percentages[j])));

                    components.add(Text.empty());
                }

                //Remove trailing empty line
                if(!components.isEmpty())
                    components.remove(components.size() - 1);

                drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
            }

            //Up button
            if(isPointWithinBounds(85, 19, 11, 12, mouseX, mouseY)) {
                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.recipe.selector.next_recipe"));

                drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
            }

            //Down button
            if(isPointWithinBounds(116, 19, 11, 12, mouseX, mouseY)) {
                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.recipe.selector.prev_recipe"));

                drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
            }

            //Missing Charcoal Filter
            for(int i = 0;i < 2;i++) {
                if(isPointWithinBounds(62 + 72*i, 44, 16, 16, mouseX, mouseY) &&
                        handler.getSlot(4 * 9 + i).getStack().isEmpty()) {
                    List<Text> components = new ArrayList<>(2);
                    components.add(Text.translatable("tooltip.energizedpower.filtration_plant.charcoal_filter_missing").
                            formatted(Formatting.RED));

                    drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
                }
            }
        }

        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            //Upgrade view

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.upgrade_view.button." +
                    (handler.isInUpgradeModuleView()?"close":"open")));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(-22, 26, 20, 20, mouseX, mouseY)) {
            //Redstone Mode

            RedstoneMode redstoneMode = handler.getRedstoneMode();

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.machine_configuration.redstone_mode." + redstoneMode.asString()));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(-22, 50, 20, 20, mouseX, mouseY)) {
            //Comparator Mode

            ComparatorMode comparatorMode = handler.getComparatorMode();

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.machine_configuration.comparator_mode." + comparatorMode.asString()));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
