package me.jddev0.ep.integration.rei;

import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetAdvancedAutoCrafterPatternInputSlotsC2SPacket;
import me.jddev0.ep.screen.AdvancedAutoCrafterMenu;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdvancedAutoCrafterTransferHandler implements TransferHandler {
    @Override
    public Result handle(Context context) {
        if(!(context.getMenu() instanceof AdvancedAutoCrafterMenu container))
            return Result.createNotApplicable();

        Display display = context.getDisplay();
        if(!(display instanceof SimpleGridMenuDisplay simpleGridMenuDisplay))
            return Result.createNotApplicable();

        Optional<Identifier> recipeIdOptional = display.getDisplayLocation();
        if(recipeIdOptional.isEmpty())
            return Result.createNotApplicable();

        if(simpleGridMenuDisplay.getWidth() > 3 || simpleGridMenuDisplay.getHeight() > 3)
            return Result.createFailed(Component.translatable("recipes.energizedpower.transfer.too_large"));

        if(!context.isActuallyCrafting())
            return Result.createSuccessful().blocksFurtherHandling();

        List<ItemStack> itemStacks = new ArrayList<>(9);

        List<InputIngredient<EntryStack<?>>> inputSlots = display.getInputIngredients(null, null);
        int len = Math.min(inputSlots.size(), 9);
        for(int i = 0;i < len;i++) {
            EntryStack<?> entryStack = inputSlots.get(i).get().stream().findAny().orElse(EntryStacks.of(ItemStack.EMPTY));
            if(entryStack.getType() != VanillaEntryTypes.ITEM)
                return Result.createNotApplicable();

            itemStacks.add(entryStack.castValue());
        }

        ModMessages.sendToServer(new SetAdvancedAutoCrafterPatternInputSlotsC2SPacket(container.getBlockEntity().getBlockPos(), itemStacks, recipeIdOptional.get()));

        return Result.createSuccessful().blocksFurtherHandling();
    }
}
