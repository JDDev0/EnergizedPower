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

public class FluidTransposerClientRecipeType implements ReliableClientRecipeType {
    protected static final FluidTransposerClientRecipeType INSTANCE = new FluidTransposerClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.fluid_transposer");
    }

    @Override
    public int getDisplayWidth() {
        return 149;
    }

    @Override
    public int getDisplayHeight() {
        return 32;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        //Texture depends on recipe mode
        return null;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 4, 8);
        slotDefinition.addItemSlot(1, 22, 8);
        slotDefinition.addItemSlot(2, 67, 8);
        slotDefinition.addItemSlot(3, 93, 8);
    }

    @Override
    public Identifier getId() {
        return EPAPI.id("fluid_transposer");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(EPBlocks.FLUID_TRANSPOSER_ITEM);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(
                new ItemStack(EPBlocks.FLUID_TRANSPOSER_ITEM)
        );
    }
}
