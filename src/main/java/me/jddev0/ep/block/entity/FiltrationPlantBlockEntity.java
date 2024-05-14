package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMultiTankMethods;
import me.jddev0.ep.block.entity.base.SelectableRecipeFluidMachineBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.ModFluids;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.FiltrationPlantMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FiltrationPlantBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<EnergizedPowerFluidStorage, FiltrationPlantRecipe> {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_FILTRATION_PLANT_TANK_CAPACITY.getValue();
    public static final int DIRTY_WATER_CONSUMPTION_PER_RECIPE = ModConfigs.COMMON_FILTRATION_PLANT_DIRTY_WATER_USAGE_PER_RECIPE.getValue();

    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2 || i == 3));

    private LazyOptional<IFluidHandler> lazyFluidStorage = LazyOptional.empty();

    public FiltrationPlantBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.FILTRATION_PLANT_ENTITY.get(), blockPos, blockState,

                "filtration_plant", FiltrationPlantMenu::new,

                4,
                ModRecipes.FILTRATION_PLANT_TYPE.get(),
                ModRecipes.FILTRATION_PLANT_SERIALIZER.get(),
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
                    case 0, 1 -> stack.is(ModItems.CHARCOAL_FILTER.get());
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
                    case 0 -> stack.isFluidEqual(new FluidStack(ModFluids.DIRTY_WATER.get(), 1));
                    case 1 -> stack.isFluidEqual(new FluidStack(Fluids.WATER, 1));
                    default -> false;
                };
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidStorage.cast();
        }else if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
        lazyFluidStorage = LazyOptional.of(() -> fluidStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyItemHandler.invalidate();
        lazyEnergyStorage.invalidate();
        lazyFluidStorage.invalidate();
    }

    @Override
    protected void craftItem(FiltrationPlantRecipe recipe) {
        if(level == null || !hasRecipe())
            return;

        fluidStorage.drain(new FluidStack(ModFluids.DIRTY_WATER.get(), DIRTY_WATER_CONSUMPTION_PER_RECIPE), IFluidHandler.FluidAction.EXECUTE);
        fluidStorage.fill(new FluidStack(Fluids.WATER, DIRTY_WATER_CONSUMPTION_PER_RECIPE), IFluidHandler.FluidAction.EXECUTE);

        for(int i = 0;i < 2;i++) {
            ItemStack charcoalFilter = itemHandler.getStackInSlot(i).copy();
            if(charcoalFilter.isEmpty() && !charcoalFilter.is(ModItems.CHARCOAL_FILTER.get()))
                continue;

            if(charcoalFilter.hurt(1, level.random, null))
                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
            else
                itemHandler.setStackInSlot(i, charcoalFilter);
        }

        ItemStack[] outputs = recipe.generateOutputs(level.random);

        if(!outputs[0].isEmpty())
            itemHandler.setStackInSlot(2, ItemStackUtils.copyWithCount(outputs[0],
                    itemHandler.getStackInSlot(2).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStackInSlot(3, ItemStackUtils.copyWithCount(outputs[1],
                    itemHandler.getStackInSlot(3).getCount() + outputs[1].getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, FiltrationPlantRecipe recipe) {
        ItemStack[] maxOutputs = recipe.getMaxOutputCounts();

        return level != null &&
                fluidStorage.getFluid(0).getAmount() >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                fluidStorage.getCapacity(1) - fluidStorage.getFluid(1).getAmount() >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                itemHandler.getStackInSlot(0).is(ModItems.CHARCOAL_FILTER.get()) &&
                itemHandler.getStackInSlot(1).is(ModItems.CHARCOAL_FILTER.get()) &&
                (maxOutputs[0].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 2, maxOutputs[0])) &&
                (maxOutputs[1].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 3, maxOutputs[1]));
    }
}