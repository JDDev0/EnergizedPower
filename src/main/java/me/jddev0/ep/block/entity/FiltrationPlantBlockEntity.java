package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMultiTankMethods;
import me.jddev0.ep.block.entity.base.SelectableRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.FiltrationPlantMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class FiltrationPlantBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<CombinedStorage<FluidVariant, SimpleFluidStorage>, FiltrationPlantRecipe> {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(
            1000 * ModConfigs.COMMON_FILTRATION_PLANT_TANK_CAPACITY.getValue());
    public static final long DIRTY_WATER_CONSUMPTION_PER_RECIPE = FluidUtils.convertMilliBucketsToDroplets(
            ModConfigs.COMMON_FILTRATION_PLANT_DIRTY_WATER_USAGE_PER_RECIPE.getValue());

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2 || i == 3);

    public FiltrationPlantBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.FILTRATION_PLANT_ENTITY, blockPos, blockState,

                "filtration_plant", FiltrationPlantMenu::new,

                4,
                EPRecipes.FILTRATION_PLANT_TYPE,
                EPRecipes.FILTRATION_PLANT_SERIALIZER,
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
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1 -> stack.isOf(EPItems.CHARCOAL_FILTER);
                    case 2, 3 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void markDirty() {
                super.markDirty();

                FiltrationPlantBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected CombinedStorage<FluidVariant, SimpleFluidStorage> initFluidStorage() {
        return new CombinedStorage<>(List.of(
                new SimpleFluidStorage(baseTankCapacity) {
                    @Override
                    protected void onFinalCommit() {
                        markDirty();
                        syncFluidToPlayers(32);
                    }

                    private boolean isFluidValid(FluidVariant variant) {
                        return variant.isOf(EPFluids.DIRTY_WATER);
                    }

                    @Override
                    protected boolean canInsert(FluidVariant variant) {
                        return isFluidValid(variant);
                    }

                    @Override
                    protected boolean canExtract(FluidVariant variant) {
                        return isFluidValid(variant);
                    }
                },
                new SimpleFluidStorage(baseTankCapacity) {
                    @Override
                    protected void onFinalCommit() {
                        markDirty();
                        syncFluidToPlayers(32);
                    }

                    private boolean isFluidValid(FluidVariant variant) {
                        return variant.isOf(Fluids.WATER);
                    }

                    @Override
                    protected boolean canInsert(FluidVariant variant) {
                        return isFluidValid(variant);
                    }

                    @Override
                    protected boolean canExtract(FluidVariant variant) {
                        return isFluidValid(variant);
                    }
                }
        ));
    }

    @Override
    protected void craftItem(RecipeEntry<FiltrationPlantRecipe> recipe) {
        if(world == null || !hasRecipe())
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.extract(FluidVariant.of(EPFluids.DIRTY_WATER), DIRTY_WATER_CONSUMPTION_PER_RECIPE, transaction);
            fluidStorage.insert(FluidVariant.of(Fluids.WATER), DIRTY_WATER_CONSUMPTION_PER_RECIPE, transaction);

            transaction.commit();
        }

        for(int i = 0;i < 2;i++) {
            ItemStack charcoalFilter = itemHandler.getStack(i).copy();
            if(charcoalFilter.isEmpty() && !charcoalFilter.isOf(EPItems.CHARCOAL_FILTER))
                continue;

            if(charcoalFilter.damage(1, world.random, null))
                itemHandler.setStack(i, ItemStack.EMPTY);
            else
                itemHandler.setStack(i, charcoalFilter);
        }

        ItemStack[] outputs = recipe.value().generateOutputs(world.random);

        if(!outputs[0].isEmpty())
            itemHandler.setStack(2, outputs[0].
                    copyWithCount(itemHandler.getStack(2).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStack(3, outputs[1].
                    copyWithCount(itemHandler.getStack(3).getCount() + outputs[1].getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<FiltrationPlantRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts();

        return world != null &&
                fluidStorage.parts.get(0).getAmount() >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                fluidStorage.parts.get(1).getCapacity() - fluidStorage.parts.get(1).getAmount() >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                itemHandler.getStack(0).isOf(EPItems.CHARCOAL_FILTER) &&
                itemHandler.getStack(1).isOf(EPItems.CHARCOAL_FILTER) &&
                (maxOutputs[0].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 2, maxOutputs[0])) &&
                (maxOutputs[1].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 3, maxOutputs[1]));

    }
}