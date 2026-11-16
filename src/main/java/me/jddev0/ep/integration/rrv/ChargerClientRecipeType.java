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

public class ChargerClientRecipeType implements ReliableClientRecipeType {
    protected static final ChargerClientRecipeType INSTANCE = new ChargerClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.charger");
    }

    @Override
    public int getDisplayWidth() {
        return 119;
    }

    @Override
    public int getDisplayHeight() {
        return 52;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return EPAPI.id("textures/gui/recipe/rrv/charger.png");
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 18, 18);
        slotDefinition.addItemSlot(1, 95, 18);
    }

    @Override
    public Identifier getId() {
        return EPAPI.id("charger");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(EPBlocks.CHARGER_ITEM.get());
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(
                new ItemStack(EPBlocks.CHARGER_ITEM.get()),
                new ItemStack(EPBlocks.ADVANCED_CHARGER_ITEM.get()),
                new ItemStack(EPBlocks.ELITE_CHARGER_ITEM.get())
        );
    }
}
