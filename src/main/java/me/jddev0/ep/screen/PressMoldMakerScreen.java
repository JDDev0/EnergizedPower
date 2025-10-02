package me.jddev0.ep.screen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.CraftPressMoldMakerRecipeC2SPacket;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import me.jddev0.ep.screen.base.EnergizedPowerBaseContainerScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class PressMoldMakerScreen extends EnergizedPowerBaseContainerScreen<PressMoldMakerMenu> {
    private final Identifier TEXTURE;

    private int scrollIndexOffset = 0;

    public PressMoldMakerScreen(PressMoldMakerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        TEXTURE = EPAPI.id("textures/gui/container/press_mold_maker.png");
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int mouseButton = click.button();

        if(mouseButton == 0) {
            boolean clicked = false;
            //Recipe buttons
            for(int j = 0; j < 2;j++) {
                for(int i = 0; i < 4;i++) {
                    if(isPointWithinBounds(35 + 30 * i, 18 + 30 * j, 20, 20, mouseX, mouseY)) {
                        int index = scrollIndexOffset + i + 4 * j;

                        if(index < handler.getRecipeList().size() && handler.getRecipeList().get(index).getSecond()) {
                            ModMessages.sendClientPacketToServer(new CraftPressMoldMakerRecipeC2SPacket(handler.getBlockEntity().getPos(),
                                    handler.getRecipeList().get(index).getFirst().id().getValue()));
                            clicked = true;
                        }
                    }
                }
            }

            //Up button
            if(scrollIndexOffset > 0) {
                if(isPointWithinBounds(155, 19, 11, 12, mouseX, mouseY)) {
                    scrollIndexOffset -= 8;
                    clicked = true;
                }
            }

            //Down button
            if(scrollIndexOffset + 8 < handler.getRecipeList().size()) {
                if(isPointWithinBounds(155, 55, 11, 12, mouseX, mouseY)) {
                    scrollIndexOffset += 8;
                    clicked = true;
                }
            }

            if(clicked)
                client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    protected void drawBackground(DrawContext drawContext, float partialTick, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        renderButtons(drawContext, x, y, mouseX, mouseY);
    }

    private void renderButtons(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        //Recipe buttons
        int buttonIndex = 0;
        for(int i = scrollIndexOffset;i < handler.getRecipeList().size() && buttonIndex < 8;i++,buttonIndex++) {
            int btnX = 35 + 30 * (buttonIndex % 4);
            int btnY = 18 + 30 * (buttonIndex / 4);

            if(handler.getRecipeList().get(i).getSecond()) {
                if(isPointWithinBounds(btnX, btnY, 20, 20, mouseX, mouseY)) {
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + btnX, y + btnY, 0, 211, 20, 20, 256, 256);
                }else {
                    drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + btnX, y + btnY, 0, 231, 20, 20, 256, 256);
                }
            }

            ItemStack output = handler.getRecipeList().get(i).getFirst().value().getOutput();
            if(!output.isEmpty()) {
                drawContext.getMatrices().pushMatrix();

                drawContext.drawItem(output, x + btnX + 2, y + btnY + 2, btnX + 2 + (btnY + 2) * this.backgroundWidth);

                drawContext.getMatrices().popMatrix();
            }
        }

        //Up button
        if(scrollIndexOffset > 0) {
            if(isPointWithinBounds(155, 19, 11, 12, mouseX, mouseY)) {
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 155, y + 19, 11, 187, 11, 12, 256, 256);
            }else {
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 155, y + 19, 11, 199, 11, 12, 256, 256);
            }
        }

        //Down button
        if(scrollIndexOffset + 8 < handler.getRecipeList().size()) {
            if(isPointWithinBounds(155, 55, 11, 12, mouseX, mouseY)) {
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 155, y + 55, 0, 187, 11, 12, 256, 256);
            }else {
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, MACHINE_SPRITES_TEXTURE, x + 155, y + 55, 0, 199, 11, 12, 256, 256);
            }
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

        //Recipe buttons
        int buttonIndex = 0;
        for(int i = scrollIndexOffset;i < handler.getRecipeList().size() && buttonIndex < 8;i++,buttonIndex++) {
            int btnX = 35 + 30 * (buttonIndex % 4);
            int btnY = 18 + 30 * (buttonIndex / 4);

            if(isPointWithinBounds(btnX, btnY, 20, 20, mouseX, mouseY)) {
                PressMoldMakerRecipe recipe = handler.getRecipeList().get(i).getFirst().value();
                ItemStack output = recipe.getOutput();
                if(!output.isEmpty()) {
                    List<Text> components = new ArrayList<>(2);
                    components.add(Text.translatable("tooltip.energizedpower.count_with_item.txt", output.getCount(),
                            output.getName()));
                    components.add(Text.translatable("tooltip.energizedpower.press_mold_maker.btn.recipes", recipe.getClayCount(),
                            Items.CLAY_BALL.getName()).formatted(Formatting.ITALIC));

                    drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
                }
            }
        }

        //Up button
        if(isPointWithinBounds(155, 19, 11, 12, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.press_mold_maker.btn.up"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }

        //Down button
        if(isPointWithinBounds(155, 55, 11, 12, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.press_mold_maker.btn.down"));

            drawContext.drawTooltip(textRenderer, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
