package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMultiTankMethods;
import me.jddev0.ep.block.entity.base.SelectableRecipeFluidMachineBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.screen.StoneSolidifierMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoneSolidifierBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<EnergizedPowerFluidStorage, StoneSolidifierRecipe> {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_STONE_SOLIDIFIER_TANK_CAPACITY.getValue();

    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> false, i -> i == 0));

    public StoneSolidifierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.STONE_SOLIDIFIER_ENTITY.get(), blockPos, blockState,

                "stone_solidifier", StoneSolidifierMenu::new,

                1,
                EPRecipes.STONE_SOLIDIFIER_TYPE.get(),
                EPRecipes.STONE_SOLIDIFIER_SERIALIZER.get(),
                ModConfigs.COMMON_STONE_SOLIDIFIER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_STONE_SOLIDIFIER_CAPACITY.getValue(),
                ModConfigs.COMMON_STONE_SOLIDIFIER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_STONE_SOLIDIFIER_CONSUMPTION_PER_TICK.getValue(),

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
                    case 0 -> false;
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
                    case 0 -> stack.isFluidEqual(new FluidStack(Fluids.WATER, 1));
                    case 1 -> stack.isFluidEqual(new FluidStack(Fluids.LAVA, 1));
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
    protected void craftItem(StoneSolidifierRecipe recipe) {
        if(level == null || !hasRecipe())
            return;

        fluidStorage.drain(new FluidStack(Fluids.WATER, recipe.getWaterAmount()), IFluidHandler.FluidAction.EXECUTE);
        fluidStorage.drain(new FluidStack(Fluids.LAVA, recipe.getLavaAmount()), IFluidHandler.FluidAction.EXECUTE);

        itemHandler.setStackInSlot(0, recipe.getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(0).getCount() +
                        recipe.getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, StoneSolidifierRecipe recipe) {
        return level != null &&
                fluidStorage.getFluid(0).getAmount() >= recipe.getWaterAmount() &&
                fluidStorage.getFluid(1).getAmount() >= recipe.getLavaAmount() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 0, recipe.getResultItem(level.registryAccess()));
    }
}