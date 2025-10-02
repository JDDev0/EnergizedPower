package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCheckboxC2SPacket;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemConveyorBeltSorterScreen extends EnergizedPowerBaseContainerScreen<ItemConveyorBeltSorterMenu> {
    private final ResourceLocation TEXTURE;

    public ItemConveyorBeltSorterScreen(ItemConveyorBeltSorterMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/item_conveyor_belt_sorter.png");
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();
        if(mouseButton == 0) {
            boolean clicked = false;
            for(int i = 0; i < 3;i++) {
                if(isHovering(136, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                    //Whitelist checkbox [3x]

                    ModMessages.sendToServer(new SetCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), i, !menu.isWhitelist(i)));
                    clicked = true;
                }else if(isHovering(153, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                    //Ignore NBT checkbox [3x]

                    ModMessages.sendToServer(new SetCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), i + 3, !menu.isIgnoreNBT(i)));
                    clicked = true;
                }
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        renderOutputBeltConnectionState(guiGraphics, x, y, mouseX, mouseY);
        renderCheckboxes(guiGraphics, x, y, mouseX, mouseY);
    }

    private void renderOutputBeltConnectionState(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        for(int i = 0;i < 3;i++)
            if(menu.isOutputBeltConnected(i))
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 10, y + 18 + i * 18, 22, 169 + i * 14, 30, 14, 256, 256);
    }
    private void renderCheckboxes(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        for(int i = 0;i < 3;i++) {
            if(menu.isWhitelist(i)) {
                //Whitelist checkbox [3x]

                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 136, y + 19 + i * 18, 13, 150, 13, 13, 256, 256);
            }

            if(menu.isIgnoreNBT(i)) {
                //Ignore NBT checkbox [3x]

                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 153, y + 19 + i * 18, 0, 150, 13, 13, 256, 256);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        for(int i = 0; i < 3;i++) {
            if(isHovering(10, 18 + i * 18, 30, 14, mouseX, mouseY)) {
                //Output connection label [3x]

                List<Component> components = new ArrayList<>(2);
                components.add(Component.translatable("tooltip.energizedpower.item_conveyor_belt_sorter.label.output_connection." +
                        (menu.isOutputBeltConnected(i)?"connection":"no_connection"), i + 1));

                guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
            }else if(isHovering(136, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                //Whitelist checkbox [3x]

                List<Component> components = new ArrayList<>(2);
                components.add(Component.translatable("tooltip.energizedpower.item_conveyor_belt_sorter.cbx." + (menu.isWhitelist(i)?"whitelist":"blacklist")));

                guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
            }else if(isHovering(153, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                //Ignore NBT checkbox [3x]

                List<Component> components = new ArrayList<>(2);
                components.add(Component.translatable("tooltip.energizedpower.item_conveyor_belt_sorter.cbx.ignore_nbt"));

                guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
