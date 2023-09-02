package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.networking.ModMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
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
public class ItemConveyorBeltSorterScreen extends HandledScreen<ItemConveyorBeltSorterMenu> {
    private final Identifier TEXTURE;

    public ItemConveyorBeltSorterScreen(ItemConveyorBeltSorterMenu menu, PlayerInventory inventory, Text component) {
        super(menu, inventory, component);

        TEXTURE = new Identifier(EnergizedPowerMod.MODID, "textures/gui/container/item_conveyor_belt_sorter.png");
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            for(int i = 0; i < 3;i++) {
                if(isPointWithinBounds(136, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                    //Whitelist checkbox [3x]

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(handler.getBlockEntity().getPos());
                    buf.writeInt(i);
                    buf.writeBoolean(!handler.isWhitelist(i));
                    ClientPlayNetworking.send(ModMessages.SET_ITEM_CONVEYOR_BELT_SORTER_CHECKBOX_ID, buf);
                    clicked = true;
                }else if(isPointWithinBounds(153, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                    //Ignore NBT checkbox [3x]

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(handler.getBlockEntity().getPos());
                    buf.writeInt(i + 3);
                    buf.writeBoolean(!handler.isIgnoreNBT(i));
                    ClientPlayNetworking.send(ModMessages.SET_ITEM_CONVEYOR_BELT_SORTER_CHECKBOX_ID, buf);
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
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawTexture(poseStack, x, y, 0, 0, backgroundWidth, backgroundHeight);

        renderOutputBeltConnectionState(poseStack, x, y, mouseX, mouseY);
        renderCheckboxes(poseStack, x, y, mouseX, mouseY);
    }

    private void renderOutputBeltConnectionState(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        for(int i = 0;i < 3;i++)
            if(handler.isOutputBeltConnected(i))
                drawTexture(poseStack, x + 10, y + 18 + i * 18, 176, i * 14, 30, 14);
    }
    private void renderCheckboxes(MatrixStack poseStack, int x, int y, int mouseX, int mouseY) {
        for(int i = 0;i < 3;i++) {
            if(handler.isWhitelist(i)) {
                //Whitelist checkbox [3x]

                drawTexture(poseStack, x + 136, y + 19 + i * 18, 176, 42, 13, 13);
            }

            if(handler.isIgnoreNBT(i)) {
                //Ignore NBT checkbox [3x]

                drawTexture(poseStack, x + 153, y + 19 + i * 18, 176, 55, 13, 13);
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

        for(int i = 0; i < 3;i++) {
            if(isPointWithinBounds(10, 18 + i * 18, 30, 14, mouseX, mouseY)) {
                //Output connection label [3x]

                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.item_conveyor_belt_sorter.label.output_connection." +
                        (handler.isOutputBeltConnected(i)?"connection":"no_connection"), i + 1));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }else if(isPointWithinBounds(136, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                //Whitelist checkbox [3x]

                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.item_conveyor_belt_sorter.cbx." + (handler.isWhitelist(i)?"whitelist":"blacklist")));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }else if(isPointWithinBounds(153, 19 + i * 18, 13, 13, mouseX, mouseY)) {
                //Ignore NBT checkbox [3x]

                List<Text> components = new ArrayList<>(2);
                components.add(Text.translatable("tooltip.energizedpower.item_conveyor_belt_sorter.cbx.ignore_nbt"));

                renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
