package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
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
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoneLiquefierBlockEntity
        extends SimpleRecipeFluidMachineBlockEntity<FluidTank, RecipeInput, StoneLiquefierRecipe> {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_STONE_LIQUEFIER_TANK_CAPACITY.getValue();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> false);

    public StoneLiquefierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.STONE_LIQUEFIER_ENTITY.get(), blockPos, blockState,

                "stone_liquefier", StoneLiquefierMenu::new,

                1, EPRecipes.STONE_LIQUEFIER_TYPE.get(), ModConfigs.COMMON_STONE_LIQUEFIER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_STONE_LIQUEFIER_CAPACITY.getValue(),
                ModConfigs.COMMON_STONE_LIQUEFIER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_STONE_LIQUEFIER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                FluidStorageSingleTankMethods.INSTANCE,
                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0 -> level == null || level.getRecipeManager().getAllRecipesFor(recipeType).stream().
                            map(RecipeHolder::value).map(StoneLiquefierRecipe::getInput).anyMatch(ingredient -> ingredient.test(stack));
                    default -> super.isItemValid(slot, stack);
                };
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress();
                }

                super.setStackInSlot(slot, stack);
            }
        };
    }

    @Override
    protected FluidTank initFluidStorage() {
        return new FluidTank(baseTankCapacity) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                syncFluidToPlayers(32);
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                if(!super.isFluidValid(stack) || level == null)
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
        return fluidStorage;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeHolder<StoneLiquefierRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        FluidStack output = recipe.value().getOutput().copyWithAmount(recipe.value().getOutput().getAmount());

        fluidStorage.fill(output, IFluidHandler.FluidAction.EXECUTE);

        itemHandler.extractItem(0, 1, false);

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<StoneLiquefierRecipe> recipe) {
        int fluidAmountInTank = fluidStorage.getFluid().getAmount();
        int fluidAmountInRecipe = recipe.value().getOutput().getAmount();

        return level != null && fluidStorage.getCapacity() - fluidAmountInTank >= fluidAmountInRecipe &&
                (fluidStorage.isEmpty() || FluidStack.isSameFluidSameComponents(fluidStorage.getFluid(), recipe.value().getOutput()));
    }
}