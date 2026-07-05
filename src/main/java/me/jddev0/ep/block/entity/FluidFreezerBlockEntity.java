package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SelectableRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.FluidFreezerRecipe;
import me.jddev0.ep.recipe.FluidIngredientWithAmount;
import me.jddev0.ep.screen.FluidFreezerMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class FluidFreezerBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<RecipeInput, FluidFreezerRecipe> {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(1000 *
            ModConfigs.COMMON_FLUID_FREEZER_TANK_CAPACITY.getValue());

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> false, i -> i == 0);

    public FluidFreezerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.FLUID_FREEZER_ENTITY, blockPos, blockState,

                "fluid_freezer", FluidFreezerMenu::new,

                1,
                EPRecipes.FLUID_FREEZER_TYPE,
                EPRecipes.FLUID_FREEZER_SERIALIZER,
                ModConfigs.COMMON_FLUID_FREEZER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_FLUID_FREEZER_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_FREEZER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_FLUID_FREEZER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR
        );
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemVariant resource) {
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
    protected EnergizedPowerFluidStorage initFluidStorage() {
        return new EnergizedPowerFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncFluidToPlayers(32);
            }

            @Override
            public boolean isValid(int index, FluidVariant resource) {
                if(!super.isValid(index, resource) || level == null)
                    return false;

                return !(level instanceof ServerLevel serverLevel) || //Always false on client side (Recipes are no longer synced)
                        RecipeUtils.getAllRecipesFor(serverLevel, recipeType).stream().map(RecipeHolder::value).
                        map(FluidFreezerRecipe::getInput).
                        anyMatch(fluid -> fluid.map(
                                output -> resource.isOf(output.getFluid()) &&
                                        resource.componentsMatch(output.getFluidVariant().getComponentsPatch()),
                                f -> f.fluid().test(resource)));
            }
        };
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable Storage<FluidVariant> getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void craftItem(RecipeHolder<FluidFreezerRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            long amount = recipe.value().getInput().map(FluidStack::getDropletsAmount,
                    FluidIngredientWithAmount::dropletsAmount);

            //Fluid in tank must be valid at this point
            fluidStorage.extract(FluidVariant.of(fluidStorage.getFluid(0).getFluid(),
                    fluidStorage.getFluid(0).getFluidVariant().getComponentsPatch()), amount, transaction);

            transaction.commit();
        }

        itemHandler.setStackInSlot(0, recipe.value().assemble(null).
                copyWithCount(itemHandler.getStackInSlot(0).getCount() +
                        recipe.value().assemble(null).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<FluidFreezerRecipe> recipe) {
        long fluidAmountInRecipe = recipe.value().getInput().map(FluidStack::getDropletsAmount,
                FluidIngredientWithAmount::dropletsAmount);

        return level != null &&
                recipe.value().getInput().map(fluid -> fluidStorage.getResource(0).isOf(fluid.getFluid()) &&
                        fluidStorage.getResource(0).componentsMatch(fluid.getFluidVariant().getComponentsPatch()),
                        fluid -> fluid.test(fluidStorage.getFluid(0))) &&
                fluidStorage.getFluid(0).getDropletsAmount() >= fluidAmountInRecipe &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 0, recipe.value().assemble(null));
    }
}