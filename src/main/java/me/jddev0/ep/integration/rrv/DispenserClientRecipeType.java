package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import me.jddev0.ep.api.EPAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class DispenserClientRecipeType implements ReliableClientRecipeType {
    protected static final DispenserClientRecipeType INSTANCE = new DispenserClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.dispenser");
    }

    @Override
    public int getDisplayWidth() {
        return 109;
    }

    @Override
    public int getDisplayHeight() {
        return 32;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return EPAPI.id("textures/gui/recipe/rrv/dispenser.png");
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 4, 8);
        slotDefinition.addItemSlot(1, 22, 8);
        slotDefinition.addItemSlot(2, 85, 8);
    }

    @Override
    public Identifier getId() {
        return EPAPI.id("dispenser");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.DISPENSER);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(
                new ItemStack(Items.DISPENSER)
        );
    }
}
