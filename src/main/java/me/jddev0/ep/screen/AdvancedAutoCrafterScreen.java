package me.jddev0.ep.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.ChangeRedstoneModeC2SPacket;
import me.jddev0.ep.networking.packet.CycleAdvancedAutoCrafterRecipeOutputC2SPacket;
import me.jddev0.ep.networking.packet.SetAdvancedAutoCrafterCheckboxC2SPacket;
import me.jddev0.ep.networking.packet.SetAdvancedAutoCrafterRecipeIndexC2SPacket;
import me.jddev0.ep.networking.packet.*;
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
public class AdvancedAutoCrafterScreen extends AbstractGenericEnergyStorageContainerScreen<AdvancedAutoCrafterMenu> {
    private final ResourceLocation CONFIGURATION_ICONS_TEXTURE = new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/machine_configuration/configuration_buttons.png");

    public AdvancedAutoCrafterScreen(AdvancedAutoCrafterMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component,
                "tooltip.energizedpower.recipe.energy_required_to_finish.txt",
                new ResourceLocation(EnergizedPowerMod.MODID, "textures/gui/container/advanced_auto_crafter.png"),
                8, 17);

        imageHeight = 224;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(mouseButton == 0) {
            boolean clicked = false;
            if(isHovering(158, 16, 11, 11, mouseX, mouseY)) {
                //Ignore NBT checkbox

                ModMessages.sendToServer(new SetAdvancedAutoCrafterCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), 0, !menu.isIgnoreNBT()));
                clicked = true;
            }else if(isHovering(158, 38, 11, 11, mouseX, mouseY)) {
                //Extract mode checkbox

                ModMessages.sendToServer(new SetAdvancedAutoCrafterCheckboxC2SPacket(menu.getBlockEntity().getBlockPos(), 1, !menu.isSecondaryExtractMode()));
                clicked = true;
            }else if(isHovering(126, 16, 12, 12, mouseX, mouseY)) {
                //Cycle through recipes

                ModMessages.sendToServer(new CycleAdvancedAutoCrafterRecipeOutputC2SPacket(menu.getBlockEntity().getBlockPos()));
                clicked = true;
            }else if(isHovering(96, 16, 12, 12, mouseX, mouseY)) {
                //Set recipe index

                ModMessages.sendToServer(new SetAdvancedAutoCrafterRecipeIndexC2SPacket(menu.getBlockEntity().getBlockPos(), menu.getRecipeIndex() + 1));
                clicked = true;
            }else if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
                //Redstone Mode

                ModMessages.sendToServer(new ChangeRedstoneModeC2SPacket(menu.getBlockEntity().getBlockPos()));
                clicked = true;
            }else if(isHovering(-22, 26, 20, 20, mouseX, mouseY)) {
                //Comparator Mode

                ModMessages.sendToServer(new ChangeComparatorModeC2SPacket(menu.getBlockEntity().getBlockPos()));
                clicked = true;
            }

            if(clicked)
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.f));
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTick, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderProgressArrow(poseStack, x, y);
        renderCheckboxes(poseStack, x, y, mouseX, mouseY);

        renderConfiguration(poseStack, x, y, mouseX, mouseY);
    }

    private void renderProgressArrow(PoseStack poseStack, int x, int y) {
        if(menu.isCraftingActive())
            blit(poseStack, x + 89, y + 34, 176, 53, menu.getScaledProgressArrowSize(), 17);
    }

    private void renderCheckboxes(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        if(menu.isIgnoreNBT()) {
            //Ignore NBT checkbox

            blit(poseStack, x + 158, y + 16, 176, 70, 11, 11);
        }

        if(menu.isSecondaryExtractMode()) {
            //Extract mode checkbox [2]

            blit(poseStack, x + 158, y + 38, 187, 81, 11, 11);
        }else {
            //Extract mode checkbox [1]

            blit(poseStack, x + 158, y + 38, 176, 81, 11, 11);
        }

        blit(poseStack, x + 96, y + 16, 176 + 11 * menu.getRecipeIndex(), 81, 11, 11);
    }

    private void renderConfiguration(PoseStack poseStack, int x, int y, int mouseX, int mouseY) {
        RedstoneMode redstoneMode = menu.getRedstoneMode();
        int ordinal = redstoneMode.ordinal();

        RenderSystem.setShaderTexture(0, CONFIGURATION_ICONS_TEXTURE);
        if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            blit(poseStack, x - 22, y + 2, 20 * ordinal, 20, 20, 20);
        }else {
            blit(poseStack, x - 22, y + 2, 20 * ordinal, 0, 20, 20);
        }

        ComparatorMode comparatorMode = menu.getComparatorMode();
        ordinal = comparatorMode.ordinal();

        if(isHovering(-22, 26, 20, 20, mouseX, mouseY)) {
            blit(poseStack, x - 22, y + 26, 20 * ordinal, 60, 20, 20);
        }else {
            blit(poseStack, x - 22, y + 26, 20 * ordinal, 40, 20, 20);
        }
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        super.renderTooltip(poseStack, mouseX, mouseY);

        if(isHovering(158, 16, 11, 11, mouseX, mouseY)) {
            //Ignore NBT checkbox

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_crafter.cbx.ignore_nbt"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(158, 38, 11, 11, mouseX, mouseY)) {
            //Extract mode

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_crafter.cbx.extract_mode." + (menu.isSecondaryExtractMode()?"2":"1")));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(126, 16, 12, 12, mouseX, mouseY)) {
            //Cycle through recipes

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_crafter.cycle_through_recipes"));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(96, 16, 12, 12, mouseX, mouseY)) {
            //Set recipe index

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.auto_crafter.recipe_index", menu.getRecipeIndex() + 1));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(-22, 2, 20, 20, mouseX, mouseY)) {
            //Redstone Mode

            RedstoneMode redstoneMode = menu.getRedstoneMode();

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.machine_configuration.redstone_mode." + redstoneMode.getSerializedName()));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }else if(isHovering(-22, 26, 20, 20, mouseX, mouseY)) {
            //Comparator Mode

            ComparatorMode comparatorMode = menu.getComparatorMode();

            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.energizedpower.machine_configuration.comparator_mode." + comparatorMode.getSerializedName()));

            renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY);
        }
    }
}
