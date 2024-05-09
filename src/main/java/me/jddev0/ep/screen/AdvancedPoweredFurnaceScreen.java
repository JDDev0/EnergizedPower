package me.jddev0.ep.screen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.networking.ModMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AdvancedPoweredFurnaceScreen extends AbstractGenericEnergyStorageHandledScreen<AdvancedPoweredFurnaceMenu> {
    private final Identifier CONFIGURATION_ICONS_TEXTURE =
            new Identifier(EnergizedPowerMod.MODID, "textures/gui/machine_configuration/configuration_buttons.png");
    private final Identifier UPGRADE_VIEW_TEXTURE =
            new Identifier(EnergizedPowerMod.MODID,
                    "textures/gui/container/upgrade_view/1_speed_1_energy_efficiency_1_energy_capacity_1_furnace_mode.png");

    public AdvancedPoweredFurnaceScreen(AdvancedPoweredFurnaceMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.advanced_powered_furnace.energy_required_to_finish.txt",
                new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/advanced_powered_furnace.png"),
                8, 17);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
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

    @Override    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        super.drawBackground(drawContext, partialTick, mouseX, mouseY);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        if(handler.isInUpgradeModuleView()) {
            drawContext.drawTexture(UPGRADE_VIEW_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        }else {
            renderProgressArrows(drawContext, x, y);
        }

        renderConfiguration(drawContext, x, y, mouseX, mouseY);
    }

    private void renderProgressArrows(DrawContext drawContext, int x, int y) {
        for(int i = 0;i < 3;i++)
            if(handler.isCraftingActive(i))
                drawContext.drawTexture(TEXTURE, x + 45 + 54 * i, y + 35, 176, 53, 12, handler.getScaledProgressArrowSize(i));
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
