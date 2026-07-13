package me.jddev0.ep.screen.base;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetIOConfigurationC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class ConfigurableIOUpgradableEnergyStorageContainerScreen
        <T extends AbstractContainerMenu & IEnergyStorageMenu & IConfigurableMenu & IConfigurableIOMenu>
        extends ConfigurableUpgradableEnergyStorageContainerScreen<T> {
    protected Identifier ioConfigurationViewTexture = EPAPI.id("textures/gui/container/io_configuration_view/default.png");
    protected Component ioConfigurationViewLabel = Component.translatable("tooltip.energizedpower.io_configuration_view.label");
    protected int ioConfigurationViewX = 6;
    protected int ioConfigurationViewY = 83;
    protected int ioConfigurationViewWidth = 166;
    protected int ioConfigurationViewHeight = 83;

    public ConfigurableIOUpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                                Identifier texture,
                                                                Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, texture, upgradeViewTexture);
    }

    public ConfigurableIOUpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                                String energyIndicatorBarTooltipComponentID,
                                                                Identifier texture,
                                                                Identifier upgradeViewTexture) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture,
                upgradeViewTexture);
    }

    public ConfigurableIOUpgradableEnergyStorageContainerScreen(T menu, Inventory inventory, Component titleComponent,
                                                                String energyIndicatorBarTooltipComponentID,
                                                                Identifier texture,
                                                                Identifier upgradeViewTexture,
                                                                int imageWidth, int imageHeight) {
        super(menu, inventory, titleComponent, energyIndicatorBarTooltipComponentID, texture,
                upgradeViewTexture, imageWidth, imageHeight);
    }

    /**
     * Will be replaced with Tank class
     */
    @Deprecated
    protected record Rect(int x, int y, int w, int h) {
        public Rect(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
    /**
     * Will be replaced with Tank class
     */
    @Deprecated
    protected Rect getTankCords(int tank) {
        return new Rect(0, 0, 0, 0);
    }

    @Override
    protected boolean mouseClickedConfiguration(double mouseX, double mouseY, int mouseButton) {
        if(super.mouseClickedConfiguration(mouseX, mouseY, mouseButton))
            return true;

        if(mouseButton == 0) {
            if(isHovering(-22, 74, 20, 20, mouseX, mouseY)) {
                //IO Configuration View

                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
                return true;
            }
        }

        if(menu.isInIOConfigurationView())
            return mouseClickedIOConfigurationView(mouseX, mouseY, mouseButton);

        return false;
    }

    protected boolean mouseClickedIOConfigurationView(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            if(isHovering(ioConfigurationViewX, ioConfigurationViewY, 20, 20, mouseX, mouseY)) {
                //Toggle slot type mode

                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 2);
                return true;
            }

            for(int i = 0;i < 6;i++) {
                int xOffset = i == 0?72:(51 + ((i - 1) % 3) * 21);
                int yOffset = 7 + ((i + 2) / 3) * 21;

                RelativeDirection direction = switch(i) {
                    case 0 -> RelativeDirection.TOP;
                    case 1 -> RelativeDirection.LEFT;
                    case 2 -> RelativeDirection.FRONT;
                    case 3 -> RelativeDirection.RIGHT;
                    case 4 -> RelativeDirection.BACK;
                    case 5 -> RelativeDirection.BOTTOM;
                    default -> throw new IllegalStateException("Unexpected value: " + i);
                };

                int slotGroupId = menu.getIOConfiguration().getSlotGroupId(direction);
                int slotGroupCount = menu.getSlotGroups().size();

                if(isHovering(ioConfigurationViewX + xOffset, ioConfigurationViewY + yOffset, 20, 20, mouseX, mouseY)) {
                    boolean hasShiftDown = Minecraft.getInstance().hasShiftDown();

                    int nextSlotGroupId = slotGroupId + (hasShiftDown?-1:1);
                    if(nextSlotGroupId >= slotGroupCount)
                        nextSlotGroupId = -1;
                    if(nextSlotGroupId < -1)
                        nextSlotGroupId = slotGroupCount - 1;

                    ModMessages.sendToServer(new SetIOConfigurationC2SPacket(
                            menu.getBlockEntity().getBlockPos(), menu.getSlotType(), direction, nextSlotGroupId
                    ));

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void renderConfiguration(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY) {
        super.renderConfiguration(guiGraphics, x, y, mouseX, mouseY);

        //IO Configuration view
        if(isHovering(-22, 74, 20, 20, mouseX, mouseY)) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 74, 40, 100, 20, 20, 256, 256);
        }else if(menu.isInIOConfigurationView()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 74, 20, 100, 20, 20, 256, 256);
        }else {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x - 22, y + 74, 0, 100, 20, 20, 256, 256);
        }

        if(menu.isInIOConfigurationView())
            renderIOConfigurationView(guiGraphics, x, y, mouseX, mouseY);
    }

    protected void renderIOConfigurationView(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ioConfigurationViewTexture, x + ioConfigurationViewX, y + ioConfigurationViewY, 0, 0,
                ioConfigurationViewWidth, ioConfigurationViewHeight, 256, 256);

        SlotType slotType = menu.getSlotType();
        int ordinal = slotType.ordinal();

        if(isHovering(ioConfigurationViewX, ioConfigurationViewY, 20, 20, mouseX, mouseY)) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x + ioConfigurationViewX, y + ioConfigurationViewY, 20 * ordinal, 140,
                    20, 20, 256, 256);
        }else {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE, x + ioConfigurationViewX, y + ioConfigurationViewY, 20 * ordinal, 120,
                    20, 20, 256, 256);
        }

        SlotGroup mouseOverSlotGroup = null;
        for(int i = 0;i < 6;i++) {
            int xOffset = i == 0?72:(51 + ((i - 1) % 3) * 21);
            int yOffset = 7 + ((i + 2) / 3) * 21;

            RelativeDirection direction = switch(i) {
                case 0 -> RelativeDirection.TOP;
                case 1 -> RelativeDirection.LEFT;
                case 2 -> RelativeDirection.FRONT;
                case 3 -> RelativeDirection.RIGHT;
                case 4 -> RelativeDirection.BACK;
                case 5 -> RelativeDirection.BOTTOM;
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };

            int slotGroupId = menu.getIOConfiguration().getSlotGroupId(direction);
            SlotGroup slotGroup = slotGroupId == -1?null:menu.getSlotGroups().get(slotGroupId);

            int modeButtonIndex = slotGroup == null?0:slotGroup.getMode().ordinal() + 1;
            int buttonVPos = 176 + 20 * modeButtonIndex;
            if(isHovering(ioConfigurationViewX + xOffset, ioConfigurationViewY + yOffset, 20, 20, mouseX, mouseY)) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE,
                        x + ioConfigurationViewX + xOffset, y + ioConfigurationViewY + yOffset, 20, buttonVPos,
                        20, 20, 256, 256);

                mouseOverSlotGroup = slotGroup;
            }else {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONFIGURATION_ICONS_TEXTURE,
                        x + ioConfigurationViewX + xOffset, y + ioConfigurationViewY + yOffset, 0, buttonVPos,
                        20, 20, 256, 256);
            }
        }

        if(mouseOverSlotGroup != null) {
            for(SlotEntry slotEntry:mouseOverSlotGroup.getSlots()) {
                int color = slotEntry.mode().getHighlightColorRGB() | 0x7F000000;

                switch(menu.getSlotType()) {
                    case ITEM -> {
                        Slot slot = menu.getSlot(4 * 9 + slotEntry.index());
                        guiGraphics.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 18 - 1, y + slot.y + 18 - 1, color);
                    }
                    case FLUID -> {
                        Rect rect = getTankCords(slotEntry.index());
                        guiGraphics.fill(x + rect.x - 1, y + rect.y - 1, x + rect.x + rect.w + 1, y + rect.y + rect.h + 1, color);
                    }
                }
            }
        }
    }

    @Override
    protected void extractTooltipConfiguration(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.extractTooltipConfiguration(guiGraphics, mouseX, mouseY);

        if(isHovering(-22, 74, 20, 20, mouseX, mouseY)) {
            //IO Configuration view

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.io_configuration_view.button." +
                    (menu.isInIOConfigurationView()?"close":"open")));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }

        if(menu.isInIOConfigurationView())
            extractTooltipIOConfigurationView(guiGraphics, mouseX, mouseY);
    }

    protected void extractTooltipIOConfigurationView(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if(isHovering(ioConfigurationViewX, ioConfigurationViewY, 20, 20, mouseX, mouseY)) {
            //Toggle slot type mode

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.machine_configuration.slot_type." + menu.getSlotType().getSerializedName()));

            guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
        }

        for(int i = 0;i < 6;i++) {
            int xOffset = i == 0?72:(51 + ((i - 1) % 3) * 21);
            int yOffset = 7 + ((i + 2) / 3) * 21;

            RelativeDirection direction = switch(i) {
                case 0 -> RelativeDirection.TOP;
                case 1 -> RelativeDirection.LEFT;
                case 2 -> RelativeDirection.FRONT;
                case 3 -> RelativeDirection.RIGHT;
                case 4 -> RelativeDirection.BACK;
                case 5 -> RelativeDirection.BOTTOM;
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };

            int slotGroupId = menu.getIOConfiguration().getSlotGroupId(direction);
            SlotGroup slotGroup = slotGroupId == -1?null:menu.getSlotGroups().get(slotGroupId);

            if(isHovering(ioConfigurationViewX + xOffset, ioConfigurationViewY + yOffset, 20, 20, mouseX, mouseY)) {
                List<Component> components = new ArrayList<>(2);
                components.add(Component.translatable("tooltip.energizedpower.relative_direction." + direction.getSerializedName()));
                if(slotGroup == null) {
                    components.add(Component.translatable("tooltip.energizedpower.machine_configuration.not_configured"));
                }else {
                    components.add(Component.translatable("tooltip.energizedpower.slot_io_mode." + slotGroup.getMode().getSerializedName()).
                            append(" (").
                            append(Component.translatable("tooltip.energizedpower.machine_configuration.slot_group")).
                            append(": " + (slotGroupId + 1) + ")"));
                }

                guiGraphics.setTooltipForNextFrame(font, components, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        //Do not call super method to prevent the inventory label from being drawn

        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
        graphics.text(this.font, menu.isInIOConfigurationView()?this.ioConfigurationViewLabel:this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);
    }
}
