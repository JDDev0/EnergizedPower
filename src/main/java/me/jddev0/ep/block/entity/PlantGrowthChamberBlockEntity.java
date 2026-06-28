package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.PlantGrowthChamberFertilizerRecipe;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.screen.PlantGrowthChamberMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PlantGrowthChamberBlockEntity extends SimpleRecipeFluidMachineBlockEntity<RecipeInput, PlantGrowthChamberRecipe> {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_RECIPE_DURATION_MULTIPLIER.getValue();
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(1000 *
            ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_FLUID_TANK_CAPACITY.getValue());
    public static final double FLUID_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_FLUID_CONSUMPTION_MULTIPLIER.getValue();

    private final InputOutputItemHandler itemHandlerSidesSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i > 1 && i < 6);
    private final InputOutputItemHandler itemHandlerTopSided = new InputOutputItemHandler(itemHandler,
            (i, stack) -> i == 1 || i == 6, i -> i > 1 && i < 7);
    private final InputOutputItemHandler itemHandlerBottomSided = new InputOutputItemHandler(itemHandler,
            (i, stack) -> i == 1 || i == 6, i -> i > 1 && i < 6);

    private double leftoverFluidConsumption = 0;
    private double fertilizerSpeedMultiplier = 1;
    private double fertilizerEnergyConsumptionMultiplier = 1;
    private double fertilizerFluidConsumptionMultiplier = 1;

    public PlantGrowthChamberBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.PLANT_GROWTH_CHAMBER_ENTITY, blockPos, blockState,

                "plant_growth_chamber", PlantGrowthChamberMenu::new,

                7, EPRecipes.PLANT_GROWTH_CHAMBER_TYPE, 1,

                ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_CAPACITY.getValue(),
                ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

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
            public boolean isValid(int slot, @NotNull ItemVariant resource) {
                ItemStack stack = resource.toStack();

                return switch(slot) {
                    case 0 -> level == null || RecipeUtils.isIngredientOfAny(level, PlantGrowthChamberRecipe.Type.INSTANCE, stack);
                    case 1 -> level == null || RecipeUtils.isIngredientOfAny(level, PlantGrowthChamberFertilizerRecipe.Type.INSTANCE, stack);
                    case 6 -> level == null || RecipeUtils.isIngredientOfAny(level, PlantGrowthChamberSoilRecipe.Type.INSTANCE, stack);
                    case 2, 3, 4, 5 -> false;
                    default -> super.isValid(slot, resource);
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0 || slot == 6) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() &&
                            !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress();
                }

                setChanged();
            }

            @Override
            public long getCapacity(int index, ItemVariant resource) {
                if(index == 6)
                    return 1;

                return super.getCapacity(index, resource);
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

                return level.getRecipeManager().getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).
                        map(PlantGrowthChamberRecipe::getFluid).
                        anyMatch(fluids -> Arrays.stream(fluids).anyMatch(resource::isOf));
            }
        };
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        if(side == Direction.UP)
            return itemHandlerTopSided;

        if(side == Direction.DOWN)
            return itemHandlerBottomSided;

        return itemHandlerSidesSided;
    }

    public @Nullable Storage<FluidVariant> getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.putDouble("recipe.leftover_fluid_consumption", leftoverFluidConsumption);

        nbt.putDouble("recipe.speed_multiplier", fertilizerSpeedMultiplier);
        nbt.putDouble("recipe.energy_consumption_multiplier", fertilizerEnergyConsumptionMultiplier);
        nbt.putDouble("recipe.fluid_consumption_multiplier", fertilizerFluidConsumptionMultiplier);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        leftoverFluidConsumption = nbt.getDouble("recipe.leftover_fluid_consumption");

        fertilizerSpeedMultiplier = nbt.getDouble("recipe.speed_multiplier");
        fertilizerEnergyConsumptionMultiplier = nbt.getDouble("recipe.energy_consumption_multiplier");
        fertilizerFluidConsumptionMultiplier = nbt.getDouble("recipe.fluid_consumption_multiplier");
    }

    @Override
    protected double getRecipeDependentRecipeDuration(RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.size());
        for(int i = 0;i < itemHandler.size();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipe = getSoilRecipe(inventory);
        if(soilRecipe.isEmpty())
            return 1.0;

        return recipe.value().getTicks() * RECIPE_DURATION_MULTIPLIER / fertilizerSpeedMultiplier / soilRecipe.get().value().getSpeedMultiplier();
    }

    @Override
    protected double getRecipeDependentEnergyConsumption(RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.size());
        for(int i = 0;i < itemHandler.size();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipe = getSoilRecipe(inventory);
        if(soilRecipe.isEmpty())
            return 1.0;

        return fertilizerEnergyConsumptionMultiplier * soilRecipe.get().value().getEnergyConsumptionMultiplier();
    }

    @Override
    protected void resetProgress() {
        super.resetProgress();

        //Do not reset leftoverFluidConsumption

        fertilizerSpeedMultiplier = 1;
        fertilizerEnergyConsumptionMultiplier = 1;
        fertilizerFluidConsumptionMultiplier = 1;
    }

    @Override
    protected void onStartCrafting(RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        if(level == null)
            return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.size());
        for(int i = 0;i < itemHandler.size();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<PlantGrowthChamberFertilizerRecipe>> fertilizerRecipe = level.getRecipeManager().
                getRecipeFor(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);

        if(fertilizerRecipe.isPresent()) {
            fertilizerSpeedMultiplier = fertilizerRecipe.get().value().getSpeedMultiplier();
            fertilizerEnergyConsumptionMultiplier = fertilizerRecipe.get().value().getEnergyConsumptionMultiplier();
            fertilizerFluidConsumptionMultiplier = fertilizerRecipe.get().value().getFluidConsumptionMultiplier();

            itemHandler.extractItem(1, 1);
        }
    }

    @Override
    protected void onCraftingTicked(RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        super.onCraftingTicked(recipe);

        SimpleContainer inventory = new SimpleContainer(itemHandler.size());
        for(int i = 0;i < itemHandler.size();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipe = getSoilRecipe(inventory);
        if(soilRecipe.isEmpty())
            return;

        double fluidConsumptionPerTick = recipe.value().getFluidConsumption() * soilRecipe.get().value().getFluidConsumptionMultiplier() *
                fertilizerFluidConsumptionMultiplier * FLUID_CONSUMPTION_MULTIPLIER;

        leftoverFluidConsumption += fluidConsumptionPerTick;
        long fluidConsumptionThisTick = (long)leftoverFluidConsumption;

        //Only keep decimal part
        leftoverFluidConsumption -= fluidConsumptionThisTick;

        //Fluid is always valid, so just use fluid which is in tank
        FluidStack fluid = new FluidStack(fluidStorage.getFluid(0).getFluid(), FluidUtils.convertMilliBucketsToDroplets(fluidConsumptionThisTick));
        if(fluid.isEmpty()) {
            return;
        }

        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.extract(fluid.getFluidVariant(), fluid.getDropletsAmount(), transaction);

            transaction.commit();
        }
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        itemHandler.extractItem(0, 1);

        List<ItemStack> itemStacksInsert = new ArrayList<>(Arrays.asList(recipe.value().generateOutputs(level.random)));

        List<Integer> emptyIndices = new ArrayList<>(4);
        outer:
        for(ItemStack itemStack:itemStacksInsert) {
            if(itemStack.isEmpty())
                continue;

            for(int i = 2;i < itemHandler.size();i++) {
                ItemStack testItemStack = itemHandler.getStackInSlot(i);
                if(emptyIndices.contains(i))
                    continue;

                if(testItemStack.isEmpty()) {
                    emptyIndices.add(i);

                    continue;
                }

                if(ItemStack.isSameItemSameComponents(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxStackSize() - testItemStack.getCount());
                    if(amount > 0) {
                        itemHandler.setStackInSlot(i, itemHandler.getStackInSlot(i).
                                copyWithCount(testItemStack.getCount() + amount));

                        itemStack.setCount(itemStack.getCount() - amount);

                        if(itemStack.isEmpty())
                            continue outer;
                    }
                }
            }

            //Leftover -> put in empty slot
            if(emptyIndices.isEmpty())
                continue; //Excess items will be vanished

            itemHandler.setStackInSlot(emptyIndices.remove(0), itemStack);
        }

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        Optional<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipe = getSoilRecipe(inventory);
        if(soilRecipe.isEmpty())
            return false;

        return level != null &&
                fluidStorage.getAmount(0) >= (int)Math.ceil(leftoverFluidConsumption +
                        recipe.value().getFluidConsumption() * soilRecipe.get().value().getFluidConsumptionMultiplier() *
                                fertilizerFluidConsumptionMultiplier * FLUID_CONSUMPTION_MULTIPLIER) &&
                Arrays.stream(recipe.value().getFluid()).anyMatch(fluid -> fluidStorage.getFluid(0).getFluid() == fluid) &&
                canInsertItemsIntoOutputSlots(inventory, new ArrayList<>(Arrays.asList(recipe.value().getMaxOutputCounts())));
    }

    private Optional<RecipeHolder<PlantGrowthChamberSoilRecipe>> getSoilRecipe(SimpleContainer inventory) {
        return level.getRecipeManager().getRecipeFor(EPRecipes.PLANT_GROWTH_CHAMBER_SOIL_TYPE, getRecipeInput(inventory), level);
    }

    private static boolean canInsertItemsIntoOutputSlots(SimpleContainer inventory, List<ItemStack> itemsStacks) {
        List<Integer> checkedIndices = new ArrayList<>(4);
        List<Integer> emptyIndices = new ArrayList<>(4);
        outer:
        for(int i = Math.min(4, itemsStacks.size()) - 1;i >= 0;i--) {
            ItemStack itemStack = itemsStacks.get(i);
            for(int j = 2;j < inventory.getContainerSize();j++) {
                if(checkedIndices.contains(j) || emptyIndices.contains(j))
                    continue;

                ItemStack testItemStack = inventory.getItem(j);
                if(testItemStack.isEmpty()) {
                    emptyIndices.add(j);

                    continue;
                }

                if(ItemStack.isSameItemSameComponents(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxStackSize() - testItemStack.getCount());

                    if(amount + testItemStack.getCount() == testItemStack.getMaxStackSize())
                        checkedIndices.add(j);

                    if(amount == itemStack.getCount()) {
                        itemsStacks.remove(i);

                        continue outer;
                    }else {
                        itemStack.shrink(amount);
                    }
                }
            }

            //Leftover -> put in empty slot
            if(emptyIndices.isEmpty())
                return false;

            int index = emptyIndices.remove(0);
            if(itemStack.getCount() == itemStack.getMaxStackSize())
                checkedIndices.add(index);

            itemsStacks.remove(i);
        }

        return itemsStacks.isEmpty();
    }
}