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

public class AssemblingMachineClientRecipeType implements ReliableClientRecipeType {
    protected static final AssemblingMachineClientRecipeType INSTANCE = new AssemblingMachineClientRecipeType();

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.assembling_machine");
    }

    @Override
    public int getDisplayWidth() {
        return 121;
    }

    @Override
    public int getDisplayHeight() {
        return 60;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return EPAPI.id("textures/gui/recipe/rrv/assembling_machine.png");
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        for(int i = 0;i < 4;i++)
            slotDefinition.addItemSlot(i, i == 1?4:(i == 2?40:22), i == 0?4:(i == 3?40:22));

        slotDefinition.addItemSlot(4, 94, 22);
    }

    @Override
    public Identifier getId() {
        return EPAPI.id("assembling_machine");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(EPBlocks.ASSEMBLING_MACHINE_ITEM.get());
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(
                new ItemStack(EPBlocks.ASSEMBLING_MACHINE_ITEM.get())
        );
    }
}
