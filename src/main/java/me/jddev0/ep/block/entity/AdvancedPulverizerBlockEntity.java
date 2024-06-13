package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMultiTankMethods;
import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.ModFluids;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.recipe.PulverizerRecipe;
import me.jddev0.ep.screen.AdvancedPulverizerMenu;
import me.jddev0.ep.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancedPulverizerBlockEntity
        extends SimpleRecipeFluidMachineBlockEntity<EnergizedPowerFluidStorage, RecipeInput, PulverizerRecipe> {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_ADVANCED_PULVERIZER_TANK_CAPACITY.getValue();
    public static final int WATER_CONSUMPTION_PER_RECIPE = ModConfigs.COMMON_ADVANCED_PULVERIZER_WATER_USAGE_PER_RECIPE.getValue();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1 || i == 2);
    public AdvancedPulverizerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.ADVANCED_PULVERIZER_ENTITY.get(), blockPos, blockState,

                "advanced_pulverizer", AdvancedPulverizerMenu::new,

                3, ModRecipes.PULVERIZER_TYPE.get(), ModConfigs.COMMON_ADVANCED_PULVERIZER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_ADVANCED_PULVERIZER_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_PULVERIZER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ADVANCED_PULVERIZER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                FluidStorageMultiTankMethods.INSTANCE,
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
                    case 0 -> level == null || RecipeUtils.isIngredientOfAny(level, PulverizerRecipe.Type.INSTANCE, stack);
                    case 1, 2 -> false;
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
    protected EnergizedPowerFluidStorage initFluidStorage() {
        return new EnergizedPowerFluidStorage(new int[] {
                baseTankCapacity, baseTankCapacity
        }) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                syncFluidToPlayers(32);
            }

            @Override
            public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
                if(!super.isFluidValid(tank, stack))
                    return false;

                return switch(tank) {
                    case 0 -> FluidStack.isSameFluid(stack, new FluidStack(Fluids.WATER, 1));
                    case 1 -> FluidStack.isSameFluid(stack, new FluidStack(ModFluids.DIRTY_WATER.get(), 1));
                    default -> false;
                };
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
    protected void craftItem(RecipeHolder<PulverizerRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        ItemStack[] outputs = recipe.value().generateOutputs(level.random, true);

        fluidStorage.drain(new FluidStack(Fluids.WATER, WATER_CONSUMPTION_PER_RECIPE), IFluidHandler.FluidAction.EXECUTE);
        fluidStorage.fill(new FluidStack(ModFluids.DIRTY_WATER.get(), WATER_CONSUMPTION_PER_RECIPE), IFluidHandler.FluidAction.EXECUTE);

        itemHandler.extractItem(0, 1, false);
        itemHandler.setStackInSlot(1, outputs[0].
                copyWithCount(itemHandler.getStackInSlot(1).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStackInSlot(2, outputs[1].
                    copyWithCount(itemHandler.getStackInSlot(2).getCount() + outputs[1].getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<PulverizerRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts(true);

        return level != null &&
                fluidStorage.getFluid(0).getAmount() >= WATER_CONSUMPTION_PER_RECIPE &&
                fluidStorage.getCapacity(1) - fluidStorage.getFluid(1).getAmount() >= WATER_CONSUMPTION_PER_RECIPE &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, maxOutputs[0]) &&
                (maxOutputs[1].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 2, maxOutputs[1]));
    }
}