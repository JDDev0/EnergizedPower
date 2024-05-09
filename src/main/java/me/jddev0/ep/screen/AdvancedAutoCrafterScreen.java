package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.networking.ModMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import me.jddev0.ep.networking.packet.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AdvancedAutoCrafterScreen extends AbstractGenericEnergyStorageHandledScreen<AdvancedAutoCrafterMenu> {
    private final Identifier CONFIGURATION_ICONS_TEXTURE =
            new Identifier(EnergizedPowerMod.MODID, "textures/gui/machine_configuration/configuration_buttons.png");
    private final Identifier UPGRADE_VIEW_TEXTURE =
            new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/upgrade_view/advanced_auto_crafter.png");

    public AdvancedAutoCrafterScreen(AdvancedAutoCrafterMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/advanced_auto_crafter.png"),
                8, 17);

        backgroundHeight = 224;
        playerInventoryTitleY = backgroundHeight - 94;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;

            if(!handler.isInUpgradeModuleView()) {
                if(isPointWithinBounds(158, 16, 11, 11, mouseX, mouseY)) {
                    //Ignore NBT checkbox

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(handler.getBlockEntity().getPos());
                    buf.writeInt(0);
                    buf.writeBoolean(!handler.isIgnoreNBT());
                    ClientPlayNetworking.send(ModMessages.SET_CHECKBOX_ID, buf);
                    clicked = true;
                }else if(isPointWithinBounds(158, 38, 11, 11, mouseX, mouseY)) {
                    //Extract mode checkbox

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(handler.getBlockEntity().getPos());
                    buf.writeInt(1);
                    buf.writeBoolean(!handler.isSecondaryExtractMode());
                    ClientPlayNetworking.send(ModMessages.SET_CHECKBOX_ID, buf);
                    clicked = true;
                }else if(isPointWithinBounds(126, 16, 12, 12, mouseX, mouseY)) {
                    //Cycle through recipes

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(handler.getBlockEntity().getPos());
                    ClientPlayNetworking.send(ModMessages.CYCLE_ADVANCED_AUTO_CRAFTER_RECIPE_OUTPUT_ID, buf);
                    clicked = true;
                }else if(isPointWithinBounds(96, 16, 12, 12, mouseX, mouseY)) {
                    //Set recipe index

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(handler.getBlockEntity().getPos());
                    buf.writeInt(handler.getRecipeIndex() + 1);
                    ClientPlayNetworking.send(ModMessages.SET_ADVANCED_AUTO_CRAFTER_RECIPE_INDEX_ID, buf);
                    clicked = true;
                }
            }

            if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
                //Upgrade view

                client.interactionManager.clickButton(handler.syncId, 0);
                clicked = true;
            }else if(isPointWithinBounds(-22, 26, 20, 20, mouseX, mouseY)) {
                //Redstone Mode

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(handler.getBlockEntity().getPos());
                ClientPlayNetworking.send(ModMessages.CHANGE_REDSTONE_MODE_ID, buf);
                clicked = true;
            }else if(isPointWithinBounds(-22, 50, 20, 20, mouseX, mouseY)) {
                //Comparator Mode

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(handler.getBlockEntity().getPos());
                ClientPlayNetworking.send(ModMessages.CHANGE_COMPARATOR_MODE_ID, buf);
                clicked = true;
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawBackground(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.drawBackground(poseStack, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        if(handler.isInUpgradeModuleView()) {
            RenderSystem.setShaderTexture(0, UPGRADE_VIEW_TEXTURE);
            drawTexture(poseStack, x, y, 0, 0, backgroundWidth, backgroundHeight);
            RenderSystem.setShaderTexture(0, TEXTURE);
        }else {
            renderProgressArrow(poseStack, x, y);
            renderCheckboxes(poseStack, x, y, mouseX, mouseY);
        }

        renderConfiguration(poseStack, x, y, mouseX, mouseY);
    }

    private void renderProgressArrow(MatrixStack poseStack, int x, int y) {
        if(handler.isCraftingActive())
            drawTexture(poseStack, x + 89, y + 34, 176, 53, handler.getScaledProgressArrowSize(), 17);
    }

    private void renderCheckboxes(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(handler.isIgnoreNBT()) {
            //Ignore NBT checkbox

            drawTexture(poseStack, x + 158, y + 16, 176, 70, 11, 11);
        }

        if(handler.isSecondaryExtractMode()) {
            //Extract mode checkbox [2]

            drawTexture(poseStack, x + 158, y + 38, 187, 81, 11, 11);
        }else {
            //Extract mode checkbox [1]

            drawTexture(poseStack, x + 158, y + 38, 176, 81, 11, 11);
        }

        drawTexture(poseStack, x + 96, y + 16, 176 + 11 * handler.getRecipeIndex(), 81, 11, 11);
    }

    private void renderConfiguration(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, CONFIGURATION_ICONS_TEXTURE);

        //Upgrade view
        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            drawTexture(poseStack, x - 22, y + 2, 40, 80, 20, 20);
        }else if(handler.isInUpgradeModuleView()) {
            drawTexture(poseStack, x - 22, y + 2, 20, 80, 20, 20);
        }else {
            drawTexture(poseStack, x - 22, y + 2, 0, 80, 20, 20);
        }

        RedstoneMode redstoneMode = handler.getRedstoneMode();
        int ordinal = redstoneMode.ordinal();

        if(isPointWithinBounds(-22, 26, 20, 20, mouseX, mouseY)) {
            drawTexture(poseStack, x - 22, y + 26, 20 * ordinal, 20, 20, 20);
        }else {
            drawTexture(poseStack, x - 22, y + 26, 20 * ordinal, 0, 20, 20);
        }

        ComparatorMode comparatorMode = handler.getComparatorMode();
        ordinal = comparatorMode.ordinal();

        if(isPointWithinBounds(-22, 50, 20, 20, mouseX, mouseY)) {
            drawTexture(poseStack, x - 22, y + 50, 20 * ordinal, 60, 20, 20);
        }else {
            drawTexture(poseStack, x - 22, y + 50, 20 * ordinal, 40, 20, 20);
        }
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack poseStack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(poseStack, mouseX, mouseY);

        if(!handler.isInUpgradeModuleView()) {
            if(isPointWithinBounds(158, 16, 11, 11, mouseX, mouseY)) {
                //Ignore NBT checkbox

                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.auto_crafter.cbx.ignore_nbt"));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }else if(isPointWithinBounds(158, 38, 11, 11, mouseX, mouseY)) {
                //Extract mode

                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.auto_crafter.cbx.extract_mode." + (handler.isSecondaryExtractMode()?"2":"1")));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }else if(isPointWithinBounds(126, 16, 12, 12, mouseX, mouseY)) {
                //Cycle through recipes

                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.auto_crafter.cycle_through_recipes"));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }else if(isPointWithinBounds(96, 16, 12, 12, mouseX, mouseY)) {
                //Set recipe index

                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.auto_crafter.recipe_index", handler.getRecipeIndex() + 1));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }
        }

        if(isPointWithinBounds(-22, 2, 20, 20, mouseX, mouseY)) {
            //Upgrade view

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.upgrade_view.button." +
                    (handler.isInUpgradeModuleView()?"close":"open")));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(-22, 26, 20, 20, mouseX, mouseY)) {
            //Redstone Mode

            RedstoneMode redstoneMode = handler.getRedstoneMode();

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.machine_configuration.redstone_mode." + redstoneMode.asString()));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isPointWithinBounds(-22, 50, 20, 20, mouseX, mouseY)) {
            //Comparator Mode

            ComparatorMode comparatorMode = handler.getComparatorMode();

            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.machine_configuration.comparator_mode." + comparatorMode.asString()));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
