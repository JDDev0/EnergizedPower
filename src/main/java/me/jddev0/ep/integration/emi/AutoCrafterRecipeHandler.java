package me.jddev0.ep.integration.emi;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetAutoCrafterPatternInputSlotsC2SPacket;
import me.jddev0.ep.screen.AutoCrafterMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AutoCrafterRecipeHandler implements EmiRecipeHandler<AutoCrafterMenu> {
    @Override
    public EmiPlayerInventory getInventory(HandledScreen<AutoCrafterMenu> screen) {
        return new EmiPlayerInventory(new ArrayList<>());
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING;
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<AutoCrafterMenu> context) {
        return recipe.getInputs().size() <= 9;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<AutoCrafterMenu> context) {
        if(!canCraft(recipe, context))
            return false;

        List<ItemStack> itemStacks = new ArrayList<>(9);

        List<EmiIngredient> inputSlots = recipe.getInputs();
        int len = Math.min(inputSlots.size(), 9);
        for(int i = 0;i < len;i++) {
            EmiStack entryStack = inputSlots.get(i).getEmiStacks().stream().findAny().orElse(EmiStack.of(ItemStack.EMPTY));

            itemStacks.add(entryStack.getItemStack());
        }

        while(itemStacks.size() < 9)
            itemStacks.add(ItemStack.EMPTY);

        MinecraftClient.getInstance().setScreen(context.getScreen());

        ModMessages.sendClientPacketToServer(
                new SetAutoCrafterPatternInputSlotsC2SPacket(context.getScreenHandler().getBlockEntity().getPos(),
                        itemStacks, recipe.getId()));

        return true;
    }
}
