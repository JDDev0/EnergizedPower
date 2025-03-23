package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ItemConveyorBeltSorterScreen extends EnergizedPowerBaseContainerScreen<ItemConveyorBeltSorterMenu> {
    private final Identifier TEXTURE;

    public ItemConveyorBeltSorterScreen(ItemConveyorBeltSorterMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/item_conveyor_belt_sorter.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            for(int i = 0; i < 3;i++) {
                if(isPointWithinBounds(136, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                    //Whitelist checkbox [3x]

                    ModMessages.sendClientPacketToServer(
                            new SetCheckboxC2SPacket(handler.getBlockEntity().getPos(), i, !handler.isWhitelist(i)));
                    clicked = true;
                }else if(isPointWithinBounds(153, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                    //Ignore NBT checkbox [3x]

                    ModMessages.sendClientPacketToServer(
                            new SetCheckboxC2SPacket(handler.getBlockEntity().getPos(), i + 3, !handler.isIgnoreNBT(i)));
                    clicked = true;
                }
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

        renderOutputBeltConnectionState(drawContext, x, y, mouseX, mouseY);
        renderCheckboxes(drawContext, x, y, mouseX, mouseY);
    }

    private void renderOutputBeltConnectionState(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        for(int i = 0;i < 3;i++)
            if(handler.isOutputBeltConnected(i))
                drawContext.drawTexture(MACHINE_SPRITES_TEXTURE, x + 10, y + 18 + i * 18, 22, 169 + i * 14, 30, 14);
    }
    private void renderCheckboxes(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        for(int i = 0;i < 3;i++) {
            if(handler.isWhitelist(i)) {
                //Whitelist checkbox [3x]

                drawContext.drawTexture(MACHINE_SPRITES_TEXTURE, x + 136, y + 19 + i * 18, 13, 150, 13, 13);
            }

            if(handler.isIgnoreNBT(i)) {
                //Ignore NBT checkbox [3x]

                drawContext.drawTexture(MACHINE_SPRITES_TEXTURE, x + 153, y + 19 + i * 18, 0, 150, 13, 13);
            }
        }
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        renderBackground(drawContext);

        super.render(drawContext, mouseX, mouseY, delta);

        drawMouseoverTooltip(drawContext, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext drawContext, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(drawContext, mouseX, mouseY);

        for(int i = 0; i < 3;i++) {
            if(isPointWithinBounds(10, 18 + i * 18, 30, 14, mouseX, mouseY)) {
                //Output connection label [3x]

                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.item_conveyor_belt_sorter.label.output_connection." +
                        (handler.isOutputBeltConnected(i)?"connection":"no_connection"), i + 1));

                drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
            }else if(isPointWithinBounds(136, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                //Whitelist checkbox [3x]

                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.item_conveyor_belt_sorter.cbx." + (handler.isWhitelist(i)?"whitelist":"blacklist")));

                drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
            }else if(isPointWithinBounds(153, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                //Ignore NBT checkbox [3x]

                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.item_conveyor_belt_sorter.cbx.ignore_nbt"));

                drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
