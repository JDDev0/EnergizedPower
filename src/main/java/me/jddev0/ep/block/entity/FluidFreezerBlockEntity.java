package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.SelectableRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.FluidFreezerRecipe;
import me.jddev0.ep.screen.FluidFreezerMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class FluidFreezerBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<FluidTank, RecipeInput, FluidFreezerRecipe> {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_FLUID_FREEZER_TANK_CAPACITY.getValue();

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> false, i -> i == 0);

    public FluidFreezerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.FLUID_FREEZER_ENTITY.get(), blockPos, blockState,

                "fluid_freezer", FluidFreezerMenu::new,

                1,
                EPRecipes.FLUID_FREEZER_TYPE.get(),
                EPRecipes.FLUID_FREEZER_SERIALIZER.get(),
                ModConfigs.COMMON_FLUID_FREEZER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_FLUID_FREEZER_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_FREEZER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_FLUID_FREEZER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

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
            public boolean isItemValid(int slot, @NotNull ItemStack resource) {
                return switch(slot) {
                    case 0 -> false;
                    default -> super.isItemValid(slot, resource);
                };
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
                        map(FluidFreezerRecipe::getInput).
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
    protected void craftItem(RecipeHolder<FluidFreezerRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        FluidStack fluid = recipe.value().getInput().copyWithAmount(recipe.value().getInput().getAmount());

        fluidStorage.drain(fluid, IFluidHandler.FluidAction.EXECUTE);

        itemHandler.setStackInSlot(0, recipe.value().getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(0).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<FluidFreezerRecipe> recipe) {
        return level != null &&
                FluidStack.isSameFluidSameComponents(fluidStorage.getFluid(), recipe.value().getInput()) &&
                fluidStorage.getFluid().getAmount() >= recipe.value().getInput().getAmount() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 0, recipe.value().assemble(null, level.registryAccess()));
    }
}