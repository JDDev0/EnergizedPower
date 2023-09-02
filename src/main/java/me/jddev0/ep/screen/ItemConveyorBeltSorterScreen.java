package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetItemConveyorBeltSorterCheckboxC2SPacket;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class ItemConveyorBeltSorterScreen extends AbstractContainerScreen<ItemConveyorBeltSorterMenu> {
    private final ResourceLocation TEXTURE;

    public ItemConveyorBeltSorterScreen(ItemConveyorBeltSorterMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/item_conveyor_belt_sorter.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        //TODO

        if(mouseButton == 0) {
            boolean clicked = false;
            for(int i = 0; i < 3;i++) {
                if(isHovering(136, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                    //Whitelist checkbox [3x]

                    ModMessages.sendToServer(new SetItemConveyorBeltSorterCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), i, !menu.isWhitelist(i)));
                    clicked = true;
                }else if(isHovering(153, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                    //Ignore NBT checkbox [3x]

                    ModMessages.sendToServer(new SetItemConveyorBeltSorterCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), i + 3, !menu.isIgnoreNBT(i)));
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

        renderOutputBeltConnectionState(poseStack, x, y, mouseX, mouseY);
        renderCheckboxes(poseStack, x, y, mouseX, mouseY);
    }

    private void renderOutputBeltConnectionState(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        for(int i = 0;i < 3;i++)
            if(menu.isOutputBeltConnected(i))
                blit(poseStack, x + 10, y + 18 + i * 18, 176, i * 14, 30, 14);
    }
    private void renderCheckboxes(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        for(int i = 0;i < 3;i++) {
            if(menu.isWhitelist(i)) {
                //Whitelist checkbox [3x]

                blit(poseStack, x + 136, y + 19 + i * 18, 176, 42, 13, 13);
            }

            if(menu.isIgnoreNBT(i)) {
                //Ignore NBT checkbox [3x]

                blit(poseStack, x + 153, y + 19 + i * 18, 176, 55, 13, 13);
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

        for(int i = 0; i < 3;i++) {
            if(isHovering(10, 18 + i * 18, 30, 14, mouseX, mouseY)) {
                //Output connection label [3x]

                List<Component> components = new ArrayList<>(2);
                components.add(Component.translatable("tooltip.energizedpower.item_conveyor_belt_sorter.label.output_connection." +
                        (menu.isOutputBeltConnected(i)?"connection":"no_connection"), i + 1));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }else if(isHovering(136, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                //Whitelist checkbox [3x]

                List<Component> components = new ArrayList<>(2);
                components.add(Component.translatable("tooltip.energizedpower.item_conveyor_belt_sorter.cbx." + (menu.isWhitelist(i)?"whitelist":"blacklist")));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }else if(isHovering(153, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                //Ignore NBT checkbox [3x]

                List<Component> components = new ArrayList<>(2);
                components.add(Component.translatable("tooltip.energizedpower.item_conveyor_belt_sorter.cbx.ignore_nbt"));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
