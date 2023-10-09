package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class PressMoldMakerScreen extends HandledScreen<PressMoldMakerMenu> {
    private final Identifier TEXTURE;

    private int scrollIndexOffset = 0;

    public PressMoldMakerScreen(PressMoldMakerMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        TEXTURE = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/press_mold_maker.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            //Recipe buttons
            for(int j = 0; j < 2;j++) {
                for(int i = 0; i < 4;i++) {
                    if(isPointWithinBounds(35 + 30 * i, 18 + 30 * j, 20, 20, mouseX, mouseY)) {
                        int index = scrollIndexOffset + i + 4 * j;

                        if(index < handler.getRecipeList().size() && handler.getRecipeList().get(index).getSecond()) {
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeBlockPos(handler.getBlockEntity().getPos());
                            buf.writeIdentifier(handler.getRecipeList().get(index).getFirst().getId());
                            ClientPlayNetworking.send(ModMessages.CRAFT_PRESS_MOLD_MAKER_RECIPE_ID, buf);
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

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawBackground(MatrixStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawTexture(poseStack, x, y, 0, 0, backgroundWidth, backgroundHeight);

        renderButtons(poseStack, x, y, mouseX, mouseY);
    }

    private void renderButtons(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        //Recipe buttons
        int buttonIndex = 0;
        for(int i = scrollIndexOffset;i < handler.getRecipeList().size() && buttonIndex < 8;i++,buttonIndex++) {
            int btnX = 35 + 30 * (buttonIndex % 4);
            int btnY = 18 + 30 * (buttonIndex / 4);

            if(handler.getRecipeList().get(i).getSecond()) {
                if(isPointWithinBounds(btnX, btnY, 20, 20, mouseX, mouseY)) {
                    drawTexture(poseStack, x + btnX, y + btnY, 176, 20, 20, 20);
                }else {
                    drawTexture(poseStack, x + btnX, y + btnY, 176, 0, 20, 20);
                }
            }

            ItemStack output = handler.getRecipeList().get(i).getFirst().getOutputItem();
            if(!output.isEmpty()) {
                poseStack.push();
                poseStack.translate(0.f, 0.f, 100.f);

                itemRenderer.renderInGuiWithOverrides(output, x + btnX + 2, y + btnY + 2, btnX + 2 + (btnY + 2) * this.backgroundWidth);

                poseStack.pop();
            }
        }

        //Up button
        if(scrollIndexOffset > 0) {
            if(isPointWithinBounds(155, 19, 11, 12, mouseX, mouseY)) {
                drawTexture(poseStack, x + 155, y + 19, 187, 40, 11, 12);
            }else {
                drawTexture(poseStack, x + 155, y + 19, 176, 40, 11, 12);
            }
        }

        //Down button
        if(scrollIndexOffset + 8 < handler.getRecipeList().size()) {
            if(isPointWithinBounds(155, 55, 11, 12, mouseX, mouseY)) {
                drawTexture(poseStack, x + 155, y + 55, 187, 52, 11, 12);
            }else {
                drawTexture(poseStack, x + 155, y + 55, 176, 52, 11, 12);
            }
        }
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);

        super.render(poseStack, mouseX, mouseY, delta);

        drawMouseoverTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(MatrixStack poseStack, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(poseStack, mouseX, mouseY);

        //Recipe buttons
        int buttonIndex = 0;
        for(int i = scrollIndexOffset;i < handler.getRecipeList().size() && buttonIndex < 8;i++,buttonIndex++) {
            int btnX = 35 + 30 * (buttonIndex % 4);
            int btnY = 18 + 30 * (buttonIndex / 4);

            if(isPointWithinBounds(btnX, btnY, 20, 20, mouseX, mouseY)) {
                PressMoldMakerRecipe recipe = handler.getRecipeList().get(i).getFirst();
                ItemStack output = recipe.getOutputItem();
                if(!output.isEmpty()) {
                    List<Text> components = new ArrayList<>(2);
                    components.add(Text.translatable("tooltip.energizedpower.press_mold_maker.btn.recipes.1", output.getCount(),
                            output.getName()));
                    components.add(Text.translatable("tooltip.energizedpower.press_mold_maker.btn.recipes.2", recipe.getClayCount(),
                            Text.translatable(Items.CLAY_BALL.getTranslationKey())).formatted(Formatting.ITALIC));

                    renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
                }
            }
        }

        //Up button
        if(isPointWithinBounds(155, 19, 11, 12, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.press_mold_maker.btn.up"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }

        //Down button
        if(isPointWithinBounds(155, 55, 11, 12, mouseX, mouseY)) {
            List<Text> components = new ArrayList<>(2);
            components.add(Text.translatable("tooltip.energizedpower.press_mold_maker.btn.down"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
