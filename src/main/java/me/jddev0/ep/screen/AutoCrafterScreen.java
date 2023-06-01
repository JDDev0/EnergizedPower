package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AutoCrafterScreen extends AbstractGenericEnergyStorageHandledScreen<AutoCrafterMenu> {
    public AutoCrafterScreen(AutoCrafterMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/auto_crafter.png"),
                8, 17);

        backgroundHeight = 206;
        playerInventoryTitleY = backgroundHeight - 94;
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
                ClientPlayNetworking.send(ModMessages.SET_AUTO_CRAFTER_CHECKBOX_ID, buf);
                clicked = true;
            }else if(isPointWithinBounds(158, 38, 11, 11, mouseX, mouseY)) {
                //Extract mode checkbox

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(handler.getBlockEntity().getPos());
                buf.writeInt(1);
                buf.writeBoolean(!handler.isSecondaryExtractMode());
                ClientPlayNetworking.send(ModMessages.SET_AUTO_CRAFTER_CHECKBOX_ID, buf);
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

        renderProgressArrow(poseStack, x, y);
        renderCheckboxes(poseStack, x, y, mouseX, mouseY);
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
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack poseStack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(poseStack, mouseX, mouseY);

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
        }
    }
}
