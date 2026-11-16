package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class PlantGrowthChamberClientRecipeType implements ReliableClientRecipeType {
    protected static final PlantGrowthChamberClientRecipeType INSTANCE = new PlantGrowthChamberClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.plant_growth_chamber");
    }

    @Override
    public int getDisplayWidth() {
        return 145;
    }

    @Override
    public int getDisplayHeight() {
        return 80;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return EPAPI.id("textures/gui/recipe/rrv/plant_growth_chamber.png");
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 22, 4);
        slotDefinition.addItemSlot(1, 22, 22);
        slotDefinition.addItemSlot(2, 4, 13);

        slotDefinition.addItemSlot(3, 76, 4);
        slotDefinition.addItemSlot(4, 94, 4);
        slotDefinition.addItemSlot(5, 76, 22);
        slotDefinition.addItemSlot(6, 94, 22);
    }

    @Override
    public Identifier getId() {
        return EPAPI.id("plant_growth_chamber");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get());
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(
                new ItemStack(EPBlocks.PLANT_GROWTH_CHAMBER_ITEM.get())
        );
    }
}
