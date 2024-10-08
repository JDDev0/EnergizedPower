package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMultiTankMethods;
import me.jddev0.ep.block.entity.base.SelectableRecipeFluidMachineBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.FiltrationPlantMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FiltrationPlantBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<EnergizedPowerFluidStorage, FiltrationPlantRecipe> {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_FILTRATION_PLANT_TANK_CAPACITY.getValue();
    public static final int DIRTY_WATER_CONSUMPTION_PER_RECIPE = ModConfigs.COMMON_FILTRATION_PLANT_DIRTY_WATER_USAGE_PER_RECIPE.getValue();

    private final LazyOptional<IEnergyStorage> lazyEnergyStorage;

    private final LazyOptional<IItemHandler> lazyItemHandler;
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2 || i == 3));

    private final LazyOptional<IFluidHandler> lazyFluidStorage;

    public FiltrationPlantBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.FILTRATION_PLANT_ENTITY.get(), blockPos, blockState,

                "filtration_plant", FiltrationPlantMenu::new,

                4,
                EPRecipes.FILTRATION_PLANT_TYPE.get(),
                EPRecipes.FILTRATION_PLANT_SERIALIZER.get(),
                ModConfigs.COMMON_FILTRATION_PLANT_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_FILTRATION_PLANT_CAPACITY.getValue(),
                ModConfigs.COMMON_FILTRATION_PLANT_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_FILTRATION_PLANT_CONSUMPTION_PER_TICK.getValue(),

                FluidStorageMultiTankMethods.INSTANCE,
                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
        lazyFluidStorage = LazyOptional.of(() -> fluidStorage);
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
                    case 0, 1 -> stack.is(EPItems.CHARCOAL_FILTER.get());
                    case 2, 3 -> false;
                    default -> super.isItemValid(slot, stack);
                };
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
                    case 0 -> stack.isFluidEqual(new FluidStack(EPFluids.DIRTY_WATER.get(), 1));
                    case 1 -> stack.isFluidEqual(new FluidStack(Fluids.WATER, 1));
                    default -> false;
                };
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == Capabilities.FLUID_HANDLER) {
            return lazyFluidStorage.cast();
        }else if(cap == Capabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void craftItem(RecipeHolder<FiltrationPlantRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        fluidStorage.drain(new FluidStack(EPFluids.DIRTY_WATER.get(), DIRTY_WATER_CONSUMPTION_PER_RECIPE), IFluidHandler.FluidAction.EXECUTE);
        fluidStorage.fill(new FluidStack(Fluids.WATER, DIRTY_WATER_CONSUMPTION_PER_RECIPE), IFluidHandler.FluidAction.EXECUTE);

        for(int i = 0;i < 2;i++) {
            ItemStack charcoalFilter = itemHandler.getStackInSlot(i).copy();
            if(charcoalFilter.isEmpty() && !charcoalFilter.is(EPItems.CHARCOAL_FILTER.get()))
                continue;

            if(charcoalFilter.hurt(1, level.random, null))
                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
            else
                itemHandler.setStackInSlot(i, charcoalFilter);
        }

        ItemStack[] outputs = recipe.value().generateOutputs(level.random);

        if(!outputs[0].isEmpty())
            itemHandler.setStackInSlot(2, outputs[0].
                    copyWithCount(itemHandler.getStackInSlot(2).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStackInSlot(3, outputs[1].
                    copyWithCount(itemHandler.getStackInSlot(3).getCount() + outputs[1].getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<FiltrationPlantRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts();

        return level != null &&
                fluidStorage.getFluid(0).getAmount() >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                fluidStorage.getCapacity(1) - fluidStorage.getFluid(1).getAmount() >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                itemHandler.getStackInSlot(0).is(EPItems.CHARCOAL_FILTER.get()) &&
                itemHandler.getStackInSlot(1).is(EPItems.CHARCOAL_FILTER.get()) &&
                (maxOutputs[0].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 2, maxOutputs[0])) &&
                (maxOutputs[1].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 3, maxOutputs[1]));
    }
}