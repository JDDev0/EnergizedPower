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

public class AlloyFurnaceClientRecipeType implements ReliableClientRecipeType {
    protected static final AlloyFurnaceClientRecipeType INSTANCE = new AlloyFurnaceClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.alloy_furnace");
    }

    @Override
    public int getDisplayWidth() {
        return 153;
    }

    @Override
    public int getDisplayHeight() {
        return 43;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return EPAPI.id("textures/gui/recipe/rrv/alloy_furnace.png");
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        for(int i = 0;i < 3;i++)
            slotDefinition.addItemSlot(i, 4 + 18 * i, 8);

        slotDefinition.addItemSlot(3, 103, 8);
        slotDefinition.addItemSlot(4, 129, 8);
    }

    @Override
    public Identifier getId() {
        return EPAPI.id("alloy_furnace");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(EPBlocks.ALLOY_FURNACE_ITEM);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(
                new ItemStack(EPBlocks.ALLOY_FURNACE_ITEM),
                new ItemStack(EPBlocks.INDUCTION_SMELTER_ITEM)
        );
    }
}
