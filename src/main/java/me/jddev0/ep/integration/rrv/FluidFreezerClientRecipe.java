package me.jddev0.ep.integration.rrv;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.extra.FluidStack;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SetCurrentRecipeIdC2SPacket;
import me.jddev0.ep.recipe.FluidFreezerRecipe;
import me.jddev0.ep.screen.FluidFreezerScreen;
import me.jddev0.ep.util.FluidStackUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;

public class FluidFreezerClientRecipe implements EPExtendedReliableClientRecipe {
    private final Identifier id;
    private final SlotContent input, output;

    public FluidFreezerClientRecipe(RecipeHolder<FluidFreezerRecipe> recipe) {
        this.id = recipe.id().identifier();
        List<FluidStack> fluidInputs = new ArrayList<>();
        recipe.value().getInput().map(f -> {
            fluidInputs.add(FluidStack.fromFluidStack(FluidStackUtils.fromNullableFluidStackTemplate(f)));

            return null;
        }, f -> {
            int amount = f.amount();
            List<Fluid> fluids = f.fluid().getFluid().map(
                    fluid -> fluid,
                    fluid -> ClientRecipeManager.INSTANCE.registryAccess().lookupOrThrow(BuiltInRegistries.FLUID.key()).
                            getOrThrow(fluid).stream().map(Holder::value).toList()
            );

            for(Fluid fluid:fluids)
                fluidInputs.add(new FluidStack(fluid, amount));

            return null;
        });
        this.input = SlotContent.ofFluidList(fluidInputs);
        this.output = SlotContent.of(recipe.value().getOutput());
    }

    @Override
    public ReliableClientRecipeType getType() {
        return FluidFreezerClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.input);
        slotFillContext.bindSlot(1, this.output);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.input);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.output);
    }

    @Override
    public boolean supportsItemTransfer() {
        return true;
    }

    @Override
    public List<Class<? extends AbstractContainerScreen<?>>> getTransferClasses() {
        return List.of(FluidFreezerScreen.class);
    }

    @Override
    public void mapRecipeItems(RecipeTransferMap transferMap, AbstractContainerScreen<?> screen) {
        //TODO this method is intended to do something else, but it is called right time to send a SetCurrentRecipeIdC2SPacket
        if(screen instanceof FluidFreezerScreen epScreen) {
            ModMessages.sendToServer(new SetCurrentRecipeIdC2SPacket(epScreen.getMenu().getBlockEntity().getBlockPos(), this.getId()));
        }
    }

    @Override
    public boolean energizedpower$ignoreRecipeRequirementsForTransfer() {
        return true;
    }
}
