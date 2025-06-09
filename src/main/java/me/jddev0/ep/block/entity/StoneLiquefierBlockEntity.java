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
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

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
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {

            @Override
            public void markDirty() {
                super.markDirty();

                StoneLiquefierBlockEntity.this.markDirty();
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return slot == 0 && (world == null || RecipeUtils.isIngredientOfAny(world, recipeType, stack));
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.areItemsAndComponentsEqual(stack, itemStack))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }
        };
    }

    @Override
    protected SimpleFluidStorage initFluidStorage() {
        return new SimpleFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                markDirty();
                syncFluidToPlayers(32);
            }

            private boolean isFluidValid(FluidVariant variant) {
                if(world == null)
                    return false;

                return world.getRecipeManager().listAllOfType(recipeType).stream().map(RecipeEntry::value).
                        map(StoneLiquefierRecipe::getOutput).
                        anyMatch(fluidStack -> variant.isOf(fluidStack.getFluid()) &&
                                variant.componentsMatch(fluidStack.getFluidVariant().getComponents()));
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
    protected RecipeInput getRecipeInput(SimpleInventory inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeEntry<StoneLiquefierRecipe> recipe) {
        if(world == null || !hasRecipe())
            return;

        FluidStack output = new FluidStack(recipe.value().getOutput().getFluidVariant().getFluid(),
                recipe.value().getOutput().getFluidVariant().getComponents(), recipe.value().getOutput().getDropletsAmount());


        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.insert(output.getFluidVariant(), output.getDropletsAmount(), transaction);

            transaction.commit();
        }

        itemHandler.removeStack(0, 1);

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<StoneLiquefierRecipe> recipe) {
        long fluidAmountInTank = fluidStorage.getFluid().getDropletsAmount();
        long fluidAmountInRecipe = recipe.value().getOutput().getDropletsAmount();

        return world != null && fluidStorage.getCapacity() - fluidAmountInTank >= fluidAmountInRecipe &&
                (fluidStorage.isEmpty() || (fluidStorage.getResource().isOf(recipe.value().getOutput().getFluid()) &&
                        fluidStorage.getResource().componentsMatch(recipe.value().getOutput().getFluidVariant().getComponents())));
    }
}