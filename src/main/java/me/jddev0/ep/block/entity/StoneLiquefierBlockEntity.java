package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.StoneLiquefierRecipe;
import me.jddev0.ep.screen.StoneLiquefierMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Collection;

public class StoneLiquefierBlockEntity
        extends SimpleRecipeFluidMachineBlockEntity<SimpleFluidStorage, RecipeInput, StoneLiquefierRecipe> {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(1000 * ModConfigs.COMMON_STONE_LIQUEFIER_TANK_CAPACITY.getValue());

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> false);

    public StoneLiquefierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.STONE_LIQUEFIER_ENTITY, blockPos, blockState,

                "stone_liquefier", StoneLiquefierMenu::new,

                1, EPRecipes.STONE_LIQUEFIER_TYPE, ModConfigs.COMMON_STONE_LIQUEFIER_RECIPE_DURATION.getValue(),

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
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {

            @Override
            public void setChanged() {
                super.setChanged();

                StoneLiquefierBlockEntity.this.setChanged();
            }

            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return slot == 0 && ((level instanceof ServerLevel serverWorld)?
                        RecipeUtils.isIngredientOfAny(serverWorld, recipeType, stack):
                        RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getItem(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress();
                }

                super.setItem(slot, stack);
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

            private boolean isFluidValid(FluidVariant variant) {
                if(!(level instanceof ServerLevel serverWorld))
                    return false; //Always false on client side (Recipes are no longer synced)

                Collection<RecipeHolder<StoneLiquefierRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverWorld, recipeType);

                return recipes.stream().map(RecipeHolder::value).map(StoneLiquefierRecipe::getOutput).
                        anyMatch(output -> variant.isOf(output.getFluid()) &&
                                variant.componentsMatch(output.getFluidVariant().getComponents()));
            }

            @Override
            protected boolean canInsert(FluidVariant variant) {
                return isFluidValid(variant);
            }

            @Override
            protected boolean canExtract(FluidVariant variant) {
                return isFluidValid(variant);
            }
        };
    }

    @Override
    protected RecipeInput getRecipeInput(SimpleContainer inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeHolder<StoneLiquefierRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        FluidStack output = new FluidStack(recipe.value().getOutput().getFluidVariant().getFluid(),
                recipe.value().getOutput().getFluidVariant().getComponents(), recipe.value().getOutput().getDropletsAmount());


        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.insert(output.getFluidVariant(), output.getDropletsAmount(), transaction);

            transaction.commit();
        }

        itemHandler.removeItem(0, 1);

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<StoneLiquefierRecipe> recipe) {
        long fluidAmountInTank = fluidStorage.getFluid().getDropletsAmount();
        long fluidAmountInRecipe = recipe.value().getOutput().getDropletsAmount();

        return level != null && fluidStorage.getCapacity() - fluidAmountInTank >= fluidAmountInRecipe &&
                (fluidStorage.isEmpty() || (fluidStorage.getResource().isOf(recipe.value().getOutput().getFluid()) &&
                        fluidStorage.getResource().componentsMatch(recipe.value().getOutput().getFluidVariant().getComponents())));
    }
}