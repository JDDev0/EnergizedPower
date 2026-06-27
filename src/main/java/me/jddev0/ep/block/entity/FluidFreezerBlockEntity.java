package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.LegacySelectableRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.LegacyInputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.FluidFreezerRecipe;
import me.jddev0.ep.screen.FluidFreezerMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public class FluidFreezerBlockEntity
        extends LegacySelectableRecipeFluidMachineBlockEntity<SimpleFluidStorage, RecipeInput, FluidFreezerRecipe> {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(1000 *
            ModConfigs.COMMON_FLUID_FREEZER_TANK_CAPACITY.getValue());

    final LegacyInputOutputItemHandler itemHandlerSided = new LegacyInputOutputItemHandler(itemHandler, (i, stack) -> false, i -> i == 0);

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

                FluidStorageSingleTankMethods.INSTANCE,
                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR
        );
    }

    @Override
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                if(slot == 0)
                    return false;

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                FluidFreezerBlockEntity.this.setChanged();
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
                    return false;

                return level.getRecipeManager().getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).map(FluidFreezerRecipe::getInput).
                        anyMatch(output -> variant.isOf(output.getFluid()) &&
                                variant.componentsMatch(output.getFluidVariant().getComponents()));
            }

            @Override
            protected boolean canInsert(FluidVariant variant) {
                return isFluidValid(variant);
            }
        };
    }

    @Override
    protected void craftItem(RecipeHolder<FluidFreezerRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;


        FluidStack fluid = new FluidStack(recipe.value().getInput().getFluidVariant().getFluid(),
                recipe.value().getInput().getFluidVariant().getComponents(), recipe.value().getInput().getDropletsAmount());

        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.extract(fluid.getFluidVariant(), fluid.getDropletsAmount(), transaction);

            transaction.commit();
        }

        itemHandler.setItem(0, recipe.value().assemble(null, level.registryAccess()).
                copyWithCount(itemHandler.getItem(0).getCount() +
                        recipe.value().assemble(null, level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<FluidFreezerRecipe> recipe) {
        return level != null &&
                (fluidStorage.getResource().isOf(recipe.value().getInput().getFluid()) &&
                        fluidStorage.getResource().componentsMatch(recipe.value().getInput().getFluidVariant().getComponents())) &&
                fluidStorage.getFluid().getDropletsAmount() >= recipe.value().getInput().getDropletsAmount() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 0, recipe.value().assemble(null, level.registryAccess()));
    }
}