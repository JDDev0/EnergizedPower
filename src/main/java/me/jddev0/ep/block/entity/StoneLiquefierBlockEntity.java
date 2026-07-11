package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.InputOutputFluidStorage;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.StoneLiquefierRecipe;
import me.jddev0.ep.screen.StoneLiquefierMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoneLiquefierBlockEntity
        extends SimpleRecipeFluidMachineBlockEntity<RecipeInput, StoneLiquefierRecipe> {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_STONE_LIQUEFIER_TANK_CAPACITY.getValue();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> false);
    private final InputOutputFluidStorage fluidStorageSided = new InputOutputFluidStorage(fluidStorage, (i, stack) -> false, i -> true);

    public StoneLiquefierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.STONE_LIQUEFIER_ENTITY.get(), blockPos, blockState,

                "stone_liquefier", StoneLiquefierMenu::new,

                1, EPRecipes.STONE_LIQUEFIER_TYPE.get(), ModConfigs.COMMON_STONE_LIQUEFIER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_STONE_LIQUEFIER_CAPACITY.getValue(),
                ModConfigs.COMMON_STONE_LIQUEFIER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_STONE_LIQUEFIER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_PULLING
        );

        slotCountPerRecipe = 1;
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0 -> level == null || level.getRecipeManager().getAllRecipesFor(recipeType).stream().
                            map(RecipeHolder::value).map(StoneLiquefierRecipe::getInput).anyMatch(ingredient -> ingredient.test(stack));
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() &&
                            !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress(0);
                }

                setChanged();
            }
        };
    }

    @Override
    protected EnergizedPowerFluidStorage initFluidStorage() {
        return new EnergizedPowerFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncFluidToPlayers(32);
            }

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                if(!super.isFluidValid(tank, stack) || level == null)
                    return false;

                return level.getRecipeManager().getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).
                        map(StoneLiquefierRecipe::getOutput).
                        anyMatch(fluidStack -> FluidStack.isSameFluidSameComponents(stack, fluidStack));
            }
        };
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return fluidStorage;

        return fluidStorageSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(int thread, RecipeHolder<StoneLiquefierRecipe> recipe) {
        if(level == null || !hasRecipe(thread))
            return;

        FluidStack output = recipe.value().getOutput().copyWithAmount(recipe.value().getOutput().getAmount());

        fluidStorage.fill(output, IFluidHandler.FluidAction.EXECUTE);

        itemHandler.extractItem(0, 1, false);

        resetProgress(thread);
    }

    @Override
    protected boolean canCraftRecipe(int thread, SimpleContainer inventory, RecipeHolder<StoneLiquefierRecipe> recipe) {
        int fluidAmountInTank = fluidStorage.getFluid(0).getAmount();
        int fluidAmountInRecipe = recipe.value().getOutput().getAmount();

        return level != null && fluidStorage.getTankCapacity(0) - fluidAmountInTank >= fluidAmountInRecipe &&
                (fluidStorage.getFluid(0).isEmpty() || FluidStack.isSameFluidSameComponents(fluidStorage.getFluid(0), recipe.value().getOutput()));
    }
}