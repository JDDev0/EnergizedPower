package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.CraftPressMoldMakerRecipeC2SPacket;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class PressMoldMakerScreen extends AbstractContainerScreen<PressMoldMakerMenu> {
    private final ResourceLocation TEXTURE;

    private int scrollIndexOffset = 0;

    public PressMoldMakerScreen(PressMoldMakerMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/press_mold_maker.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            //Recipe buttons
            for(int j = 0; j < 2;j++) {
                for(int i = 0; i < 4;i++) {
                    if(isHovering(35 + 30 * i, 18 + 30 * j, 20, 20, mouseX, mouseY)) {
                        int index = scrollIndexOffset + i + 4 * j;

                        if(index < menu.getRecipeList().size() && menu.getRecipeList().get(index).getSecond()) {
                            ModMessages.sendToServer(new CraftPressMoldMakerRecipeC2SPacket(menu.getBlockEntity().getBlockPos(),
                                    menu.getRecipeList().get(index).getFirst().getId()));
                            clicked = true;
                        }
                    }
                }
            }

            //Up button
            if(scrollIndexOffset > 0) {
                if(isHovering(155, 19, 11, 12, mouseX, mouseY)) {
                    scrollIndexOffset -= 8;
                    clicked = true;
                }
            }

            //Down button
            if(scrollIndexOffset + 8 < menu.getRecipeList().size()) {
                if(isHovering(155, 55, 11, 12, mouseX, mouseY)) {
                    scrollIndexOffset += 8;
                    clicked = true;
                }
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

        renderButtons(poseStack, x, y, mouseX, mouseY);
    }

    private void renderButtons(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        //Recipe buttons
        int buttonIndex = 0;
        for(int i = scrollIndexOffset;i < menu.getRecipeList().size() && buttonIndex < 8;i++,buttonIndex++) {
            int btnX = 35 + 30 * (buttonIndex % 4);
            int btnY = 18 + 30 * (buttonIndex / 4);

            if(menu.getRecipeList().get(i).getSecond()) {
                if(isHovering(btnX, btnY, 20, 20, mouseX, mouseY)) {
                    blit(poseStack, x + btnX, y + btnY, 176, 20, 20, 20);
                }else {
                    blit(poseStack, x + btnX, y + btnY, 176, 0, 20, 20);
                }
            }

            ItemStack output = menu.getRecipeList().get(i).getFirst().getOutput();
            if(!output.isEmpty()) {
                poseStack.pushPose();
                poseStack.translate(0.f, 0.f, 100.f);

                itemRenderer.renderAndDecorateItem(poseStack, output, x + btnX + 2, y + btnY + 2, btnX + 2 + (btnY + 2) * this.imageWidth);

                poseStack.popPose();

                RenderSystem.setShaderTexture(0, TEXTURE);
            }
        }

        //Up button
        if(scrollIndexOffset > 0) {
            if(isHovering(155, 19, 11, 12, mouseX, mouseY)) {
                blit(poseStack, x + 155, y + 19, 187, 40, 11, 12);
            }else {
                blit(poseStack, x + 155, y + 19, 176, 40, 11, 12);
            }
        }

        //Down button
        if(scrollIndexOffset + 8 < menu.getRecipeList().size()) {
            if(isHovering(155, 55, 11, 12, mouseX, mouseY)) {
                blit(poseStack, x + 155, y + 55, 187, 52, 11, 12);
            }else {
                blit(poseStack, x + 155, y + 55, 176, 52, 11, 12);
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);

        super.render(poseStack, mouseX, mouseY, delta);

        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);

        //Recipe buttons
        int buttonIndex = 0;
        for(int i = scrollIndexOffset;i < menu.getRecipeList().size() && buttonIndex < 8;i++,buttonIndex++) {
            int btnX = 35 + 30 * (buttonIndex % 4);
            int btnY = 18 + 30 * (buttonIndex / 4);

            if(isHovering(btnX, btnY, 20, 20, mouseX, mouseY)) {
                PressMoldMakerRecipe recipe = menu.getRecipeList().get(i).getFirst();
                ItemStack output = recipe.getOutput();
                if(!output.isEmpty()) {
                    List<Component> components = new ArrayList<>(2);
                    components.add(Component.translatable("tooltip.energizedpower.press_mold_maker.btn.recipes.1", output.getCount(),
                            output.getHoverName()));
                    components.add(Component.translatable("tooltip.energizedpower.press_mold_maker.btn.recipes.2", recipe.getClayCount(),
                            Component.translatable(Items.CLAY_BALL.getDescriptionId())).withStyle(ChatFormatting.ITALIC));

                    renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
                }
            }
        }

        //Up button
        if(isHovering(155, 19, 11, 12, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.press_mold_maker.btn.up"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }

        //Down button
        if(isHovering(155, 55, 11, 12, mouseX, mouseY)) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.press_mold_maker.btn.down"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
