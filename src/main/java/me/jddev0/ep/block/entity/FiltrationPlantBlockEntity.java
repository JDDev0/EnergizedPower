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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import java.util.List;

public class FiltrationPlantBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<CombinedStorage<FluidVariant, SimpleFluidStorage>, RecipeInput, FiltrationPlantRecipe> {
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
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1 -> stack.is(EPItems.CHARCOAL_FILTER);
                    case 2, 3 -> false;
                    default -> super.canPlaceItem(slot, stack);
                };
            }

            @Override
            public void setChanged() {
                super.setChanged();

                FiltrationPlantBlockEntity.this.setChanged();
            }
        };
    }

    @Override
    protected CombinedStorage<FluidVariant, SimpleFluidStorage> initFluidStorage() {
        return new CombinedStorage<>(List.of(
                new SimpleFluidStorage(baseTankCapacity) {
                    @Override
                    protected void onFinalCommit() {
                        setChanged();
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
                        setChanged();
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
    protected void craftItem(RecipeHolder<FiltrationPlantRecipe> recipe) {
        if(level == null || !hasRecipe() || !(level instanceof ServerLevel serverWorld))
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.extract(FluidVariant.of(EPFluids.DIRTY_WATER), DIRTY_WATER_CONSUMPTION_PER_RECIPE, transaction);
            fluidStorage.insert(FluidVariant.of(Fluids.WATER), DIRTY_WATER_CONSUMPTION_PER_RECIPE, transaction);

            transaction.commit();
        }

        for(int i = 0;i < 2;i++) {
            ItemStack charcoalFilter = itemHandler.getItem(i).copy();
            if(charcoalFilter.isEmpty() && !charcoalFilter.is(EPItems.CHARCOAL_FILTER))
                continue;

            charcoalFilter.hurtAndBreak(1, serverWorld, null, item -> charcoalFilter.setCount(0));
            itemHandler.setItem(i, charcoalFilter);
        }

        ItemStack[] outputs = recipe.value().generateOutputs(level.random);

        if(!outputs[0].isEmpty())
            itemHandler.setItem(2, outputs[0].
                    copyWithCount(itemHandler.getItem(2).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setItem(3, outputs[1].
                    copyWithCount(itemHandler.getItem(3).getCount() + outputs[1].getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<FiltrationPlantRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts();

        return level != null &&
                fluidStorage.parts.get(0).getAmount() >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                fluidStorage.parts.get(1).getCapacity() - fluidStorage.parts.get(1).getAmount() >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                itemHandler.getItem(0).is(EPItems.CHARCOAL_FILTER) &&
                itemHandler.getItem(1).is(EPItems.CHARCOAL_FILTER) &&
                (maxOutputs[0].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 2, maxOutputs[0])) &&
                (maxOutputs[1].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 3, maxOutputs[1]));

    }
}