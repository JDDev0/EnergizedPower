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

public class CrystalGrowthChamberClientRecipeType implements ReliableClientRecipeType {
    protected static final CrystalGrowthChamberClientRecipeType INSTANCE = new CrystalGrowthChamberClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.crystal_growth_chamber");
    }

    @Override
    public int getDisplayWidth() {
        return 104;
    }

    @Override
    public int getDisplayHeight() {
        return 44;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return EPAPI.id("textures/gui/recipe/rrv/crystal_growth_chamber.png");
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 4, 8);
        slotDefinition.addItemSlot(1, 80, 8);
    }

    @Override
    public Identifier getId() {
        return EPAPI.id("crystal_growth_chamber");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM.get());
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(
                new ItemStack(EPBlocks.CRYSTAL_GROWTH_CHAMBER_ITEM.get())
        );
    }
}
