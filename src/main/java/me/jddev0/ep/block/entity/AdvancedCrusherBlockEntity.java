package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMultiTankMethods;
import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.ModFluids;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.CrusherRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.AdvancedCrusherMenu;
import me.jddev0.ep.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
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

public class AdvancedCrusherBlockEntity
        extends SimpleRecipeFluidMachineBlockEntity<EnergizedPowerFluidStorage, CrusherRecipe> {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_ADVANCED_CRUSHER_TANK_CAPACITY.getValue();
    public static final int WATER_CONSUMPTION_PER_RECIPE = ModConfigs.COMMON_ADVANCED_CRUSHER_WATER_USAGE_PER_RECIPE.getValue();

    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1));

    private LazyOptional<IFluidHandler> lazyFluidStorage = LazyOptional.empty();

    public AdvancedCrusherBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.ADVANCED_CRUSHER_ENTITY.get(), blockPos, blockState,

                "advanced_crusher", AdvancedCrusherMenu::new,

                2, ModRecipes.CRUSHER_TYPE.get(), ModConfigs.COMMON_ADVANCED_CRUSHER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_ADVANCED_CRUSHER_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_CRUSHER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ADVANCED_CRUSHER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

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
                    case 0 -> level == null || RecipeUtils.isIngredientOfAny(level, CrusherRecipe.Type.INSTANCE, stack);
                    case 1 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.isSameItemSameTags(stack, itemStack))
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
                    case 0 -> stack.isFluidEqual(new FluidStack(Fluids.WATER, 1));
                    case 1 -> stack.isFluidEqual(new FluidStack(ModFluids.DIRTY_WATER.get(), 1));
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
    protected void craftItem(RecipeHolder<CrusherRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        fluidStorage.drain(new FluidStack(Fluids.WATER, WATER_CONSUMPTION_PER_RECIPE), IFluidHandler.FluidAction.EXECUTE);
        fluidStorage.fill(new FluidStack(ModFluids.DIRTY_WATER.get(), WATER_CONSUMPTION_PER_RECIPE), IFluidHandler.FluidAction.EXECUTE);

        itemHandler.extractItem(0, 1, false);
        itemHandler.setStackInSlot(1, recipe.value().getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(1).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<CrusherRecipe> recipe) {
        return level != null &&
                fluidStorage.getFluid(0).getAmount() >= WATER_CONSUMPTION_PER_RECIPE &&
                fluidStorage.getCapacity(1) - fluidStorage.getFluid(1).getAmount() >= WATER_CONSUMPTION_PER_RECIPE &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().getResultItem(level.registryAccess()));
    }
}