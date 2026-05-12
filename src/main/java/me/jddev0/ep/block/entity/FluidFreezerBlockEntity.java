package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.SelectableRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.FluidFreezerRecipe;
import me.jddev0.ep.screen.FluidFreezerMenu;
import me.jddev0.ep.util.FluidStackUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidFreezerBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<SimpleFluidStorage, RecipeInput, FluidFreezerRecipe> {
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
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemResource resource) {
                return switch(slot) {
                    case 0 -> false;
                    default -> super.isValid(slot, resource);
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                setChanged();
            }
        };
    }

    @Override
    protected SimpleFluidStorage initFluidStorage() {
        return new SimpleFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncFluidToPlayers(32);
            }

            @Override
            public boolean isValid(int index, FluidResource resource) {
                if(!super.isValid(index, resource) || level == null)
                    return false;

                return !(level instanceof ServerLevel serverLevel) || //Always false on client side (Recipes are no longer synced)
                        RecipeUtils.getAllRecipesFor(serverLevel, recipeType).stream().map(RecipeHolder::value).
                        map(FluidFreezerRecipe::getInput).
                        anyMatch(resource::matches);
            }
        };
    }

    public @Nullable ResourceHandler<ItemResource> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable ResourceHandler<FluidResource> getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable EnergyHandler getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void craftItem(RecipeHolder<FluidFreezerRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        FluidStack fluid = FluidStackUtils.fromNullableFluidStackTemplate(recipe.value().getInput()).
                copyWithAmount(FluidStackUtils.fromNullableFluidStackTemplate(recipe.value().getInput()).getAmount());

        try(Transaction transaction = Transaction.open(null)) {
            fluidStorage.extract(FluidResource.of(fluid), fluid.getAmount(), transaction);

            transaction.commit();
        }

        itemHandler.setStackInSlot(0, recipe.value().assemble(null).
                copyWithCount(itemHandler.getStackInSlot(0).getCount() +
                        recipe.value().assemble(null).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<FluidFreezerRecipe> recipe) {
        return level != null &&
                FluidStack.isSameFluidSameComponents(fluidStorage.getFluid(), recipe.value().getInput()) &&
                fluidStorage.getFluid().getAmount() >= recipe.value().getInput().amount() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 0, recipe.value().assemble(null));
    }
}