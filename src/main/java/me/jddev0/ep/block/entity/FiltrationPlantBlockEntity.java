package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SelectableRecipeFluidMachineBlockEntity;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.InputOutputFluidStorage;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.item.EPItems;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.FiltrationPlantMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
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
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class FiltrationPlantBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<RecipeInput, FiltrationPlantRecipe> {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(
            1000 * ModConfigs.COMMON_FILTRATION_PLANT_TANK_CAPACITY.getValue());
    public static final long DIRTY_WATER_CONSUMPTION_PER_RECIPE = FluidUtils.convertMilliBucketsToDroplets(
            ModConfigs.COMMON_FILTRATION_PLANT_DIRTY_WATER_USAGE_PER_RECIPE.getValue());

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2 || i == 3);
    private final InputOutputFluidStorage fluidStorageSided = new InputOutputFluidStorage(fluidStorage, (i, stack) -> i == 0, i -> true);

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

                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemVariant stack) {
                return switch(slot) {
                    case 0, 1 -> stack.is(EPItems.CHARCOAL_FILTER);
                    case 2, 3 -> false;
                    default -> super.isValid(slot, stack);
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
        return new EnergizedPowerFluidStorage(2, baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncFluidToPlayers(32);
            }

            @Override
            public boolean isValid(int tank, @NotNull FluidVariant stack) {
                if(!super.isValid(tank, stack))
                    return false;

                return switch(tank) {
                    case 0 -> stack.isOf(EPFluids.DIRTY_WATER);
                    case 1 -> stack.isOf(Fluids.WATER);
                    default -> false;
                };
            }
        };
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable Storage<FluidVariant> getFluidHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return fluidStorage;

        return fluidStorageSided;
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void craftItem(RecipeHolder<FiltrationPlantRecipe> recipe) {
        if(level == null || !hasRecipe() || !(level instanceof ServerLevel serverLevel))
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.extract(FluidVariant.of(EPFluids.DIRTY_WATER), DIRTY_WATER_CONSUMPTION_PER_RECIPE, transaction);
            fluidStorage.insert(FluidVariant.of(Fluids.WATER), DIRTY_WATER_CONSUMPTION_PER_RECIPE, transaction);

            transaction.commit();
        }

        for(int i = 0;i < 2;i++) {
            ItemStack charcoalFilter = itemHandler.getStackInSlot(i).copy();
            if(charcoalFilter.isEmpty() && !charcoalFilter.is(EPItems.CHARCOAL_FILTER))
                continue;

            charcoalFilter.hurtAndBreak(1, serverLevel, null, item -> charcoalFilter.setCount(0));
            itemHandler.setStackInSlot(i, charcoalFilter);
        }

        ItemStack[] outputs = recipe.value().generateOutputs(level.getRandom());

        if(!outputs[0].isEmpty())
            itemHandler.setStackInSlot(2, outputs[0].
                    copyWithCount(itemHandler.getStackInSlot(2).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStackInSlot(3, outputs[1].
                    copyWithCount(itemHandler.getStackInSlot(3).getCount() + outputs[1].getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<FiltrationPlantRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts();

        return level != null &&
                fluidStorage.getAmount(0) >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                fluidStorage.getTankCapacity(1) - fluidStorage.getAmount(1) >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                itemHandler.getStackInSlot(0).is(EPItems.CHARCOAL_FILTER) &&
                itemHandler.getStackInSlot(1).is(EPItems.CHARCOAL_FILTER) &&
                (maxOutputs[0].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 2, maxOutputs[0])) &&
                (maxOutputs[1].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 3, maxOutputs[1]));
    }
}