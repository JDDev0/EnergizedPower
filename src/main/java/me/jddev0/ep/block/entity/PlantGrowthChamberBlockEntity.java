package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.screen.PlantGrowthChamberMenu;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PlantGrowthChamberBlockEntity extends SimpleRecipeFluidMachineBlockEntity<RecipeInput, PlantGrowthChamberRecipe> {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_RECIPE_DURATION_MULTIPLIER.getValue();
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_FLUID_TANK_CAPACITY.getValue();
    public static final double FLUID_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_FLUID_CONSUMPTION_MULTIPLIER.getValue();

    private static final int ITEM_SLOT_SEED = 0;
    private static final int ITEM_SLOT_FERTILIZER = 1;
    private static final int ITEM_SLOT_OUTPUT_1 = 2;
    private static final int ITEM_SLOT_OUTPUT_2 = 3;
    private static final int ITEM_SLOT_OUTPUT_3 = 4;
    private static final int ITEM_SLOT_OUTPUT_4 = 5;
    private static final int ITEM_SLOT_SOIL = 6;

    private static final int FLUID_SLOT_INPUT = 0;

    private double leftoverFluidConsumption = 0;
    private double fertilizerSpeedMultiplier = 1;
    private double fertilizerEnergyConsumptionMultiplier = 1;
    private double fertilizerFluidConsumptionMultiplier = 1;

    public PlantGrowthChamberBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.PLANT_GROWTH_CHAMBER_ENTITY.get(), blockPos, blockState,

                "plant_growth_chamber", PlantGrowthChamberMenu::new,

                7, EPRecipes.PLANT_GROWTH_CHAMBER_TYPE.get(), 1,

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

        slotCountPerRecipe = 7;
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0 -> level == null || RecipeUtils.isIngredientOfAny(level, PlantGrowthChamberRecipe.Type.INSTANCE, stack);
                    case 1 -> level == null || RecipeUtils.isIngredientOfAny(level, PlantGrowthChamberFertilizerRecipe.Type.INSTANCE, stack);
                    case 6 -> level == null || RecipeUtils.isIngredientOfAny(level, PlantGrowthChamberSoilRecipe.Type.INSTANCE, stack);
                    case 2, 3, 4, 5 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0 || slot == 6) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() &&
                            !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress(0);
                }

                setChanged();
            }

            @Override
            public int getCapacity(int slot) {
                if(slot == 6)
                    return 1;

                return super.getCapacity(slot);
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
            public boolean isFluidValid(int tank, FluidStack stack) {
                if(!super.isFluidValid(tank, stack) || level == null)
                    return false;

                return level.getRecipeManager().getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).
                        map(PlantGrowthChamberRecipe::getFluid).
                        anyMatch(fluid -> fluid.test(stack));
            }
        };
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        return switch(slotType) {
            case ITEM -> List.of(
                    //Input only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_SEED)),
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_FERTILIZER), SlotEntry.ofInput(ITEM_SLOT_SOIL)),
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_SEED), SlotEntry.ofInput(ITEM_SLOT_FERTILIZER), SlotEntry.ofInput(ITEM_SLOT_SOIL)),

                    //Output only
                    SlotGroup.of(
                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_1), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_2),
                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_3), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_4)
                    ),

                    //Seed input & output
                    SlotGroup.of(
                            SlotEntry.ofInput(ITEM_SLOT_SEED),

                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_1), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_2),
                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_3), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_4)
                    ),

                    //All input & output (except soil output)
                    SlotGroup.of(
                            SlotEntry.ofInput(ITEM_SLOT_SEED), SlotEntry.ofInput(ITEM_SLOT_FERTILIZER), SlotEntry.ofInput(ITEM_SLOT_SOIL),

                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_1), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_2),
                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_3), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_4)
                    ),

                    //All input & output
                    SlotGroup.of(
                            SlotEntry.ofInput(ITEM_SLOT_SEED), SlotEntry.ofInput(ITEM_SLOT_FERTILIZER), SlotEntry.ofBoth(ITEM_SLOT_SOIL),

                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_1), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_2),
                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_3), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_4)
                    ),

                    //Legacy default configuration: Fertilizer + Soil input and outputs
                    SlotGroup.of(
                            SlotEntry.ofInput(ITEM_SLOT_FERTILIZER), SlotEntry.ofInput(ITEM_SLOT_SOIL),

                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_1), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_2),
                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_3), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_4)
                    ),

                    //Legacy default configuration: Fertilizer + Soil input and outputs + soil output
                    SlotGroup.of(
                            SlotEntry.ofInput(ITEM_SLOT_FERTILIZER), SlotEntry.ofBoth(ITEM_SLOT_SOIL),

                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_1), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_2),
                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_3), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_4)
                    ),

                    //Special: Soil replacement automation
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_SOIL)),
                    SlotGroup.of(SlotEntry.ofBoth(ITEM_SLOT_SOIL))
            );
            case FLUID -> List.of(
                    SlotGroup.of(SlotEntry.ofInput(FLUID_SLOT_INPUT)),

                    //Every EP fluid tank should support output to allow draining fluids without losing them
                    SlotGroup.of(SlotEntry.ofOutput(FLUID_SLOT_INPUT)),
                    SlotGroup.of(SlotEntry.ofBoth(FLUID_SLOT_INPUT))
            );
        };
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        switch(slotType) {
            case ITEM -> {
                for(RelativeDirection direction:RelativeDirection.sidesOnlyValues())
                    conf.setSlotGroupId(direction, 4);

                conf.setSlotGroupId(RelativeDirection.TOP, 8);
                conf.setSlotGroupId(RelativeDirection.BOTTOM, 7);
            }
            case FLUID -> {
                for(RelativeDirection direction:RelativeDirection.values())
                    conf.setSlotGroupId(direction, 2);
            }
        }

        return conf;
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.ITEM);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.ITEM);
        return conf.createSidedItemHandlerFor(slotGroups, itemHandler, facing, side);
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return fluidStorage;

        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.FLUID);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.FLUID);
        return conf.createSidedFluidHandlerFor(slotGroups, fluidStorage, facing, side);
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
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
        if(nbt.contains("inventory") && nbt.getCompound("inventory").getInt("Size") == 6) {
            //Upgrade slot count from 6 to 7
            nbt = nbt.copy(); //Clone, do not modify the original nbt data
            nbt.getCompound("inventory").putInt("Size", 7);
        }

        super.loadAdditional(nbt, registries);

        leftoverFluidConsumption = nbt.getDouble("recipe.leftover_fluid_consumption");

        fertilizerSpeedMultiplier = nbt.getDouble("recipe.speed_multiplier");
        fertilizerEnergyConsumptionMultiplier = nbt.getDouble("recipe.energy_consumption_multiplier");
        fertilizerFluidConsumptionMultiplier = nbt.getDouble("recipe.fluid_consumption_multiplier");
    }

    @Override
    protected double getRecipeDependentRecipeDuration(int thread, RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipe = getSoilRecipe(inventory);
        if(soilRecipe.isEmpty())
            return 1.0;

        return recipe.value().getTicks() * RECIPE_DURATION_MULTIPLIER / fertilizerSpeedMultiplier / soilRecipe.get().value().getSpeedMultiplier();
    }

    @Override
    protected double getRecipeDependentEnergyConsumption(int thread, RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipe = getSoilRecipe(inventory);
        if(soilRecipe.isEmpty())
            return 1.0;

        return fertilizerEnergyConsumptionMultiplier * soilRecipe.get().value().getEnergyConsumptionMultiplier();
    }

    @Override
    protected void resetProgress(int thread) {
        super.resetProgress(thread);

        //Do not reset leftoverFluidConsumption

        fertilizerSpeedMultiplier = 1;
        fertilizerEnergyConsumptionMultiplier = 1;
        fertilizerFluidConsumptionMultiplier = 1;
    }

    @Override
    protected void onStartCrafting(int thread, RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        if(level == null)
            return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<PlantGrowthChamberFertilizerRecipe>> fertilizerRecipe = level.getRecipeManager().
                getRecipeFor(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);

        if(fertilizerRecipe.isPresent()) {
            fertilizerSpeedMultiplier = fertilizerRecipe.get().value().getSpeedMultiplier();
            fertilizerEnergyConsumptionMultiplier = fertilizerRecipe.get().value().getEnergyConsumptionMultiplier();
            fertilizerFluidConsumptionMultiplier = fertilizerRecipe.get().value().getFluidConsumptionMultiplier();

            itemHandler.extractItem(1, 1, false);
        }
    }

    @Override
    protected void onCraftingTicked(int thread, RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        super.onCraftingTicked(thread, recipe);

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipe = getSoilRecipe(inventory);
        if(soilRecipe.isEmpty())
            return;

        double fluidConsumptionPerTick = recipe.value().getFluidConsumption() * soilRecipe.get().value().getFluidConsumptionMultiplier() *
                fertilizerFluidConsumptionMultiplier * FLUID_CONSUMPTION_MULTIPLIER;

        leftoverFluidConsumption += fluidConsumptionPerTick;
        int fluidConsumptionThisTick = (int)leftoverFluidConsumption;

        //Only keep decimal part
        leftoverFluidConsumption -= fluidConsumptionThisTick;

        //Fluid is always valid, so just use fluid which is in tank
        FluidStack fluid = new FluidStack(fluidStorage.getFluid(0).getFluid(), fluidConsumptionThisTick);
        if(fluid.isEmpty()) {
            return;
        }

        fluidStorage.drain(fluid, IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(int thread, RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        if(level == null || !hasRecipe(thread))
            return;

        itemHandler.extractItem(0, 1, false);

        List<ItemStack> itemStacksInsert = new ArrayList<>(Arrays.asList(recipe.value().generateOutputs(level.random)));

        List<Integer> emptyIndices = new ArrayList<>(4);
        outer:
        for(ItemStack itemStack:itemStacksInsert) {
            if(itemStack.isEmpty())
                continue;

            for(int i = 2;i < itemHandler.getSlots();i++) {
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

        resetProgress(thread);
    }

    @Override
    protected boolean canCraftRecipe(int thread, SimpleContainer inventory, RecipeHolder<PlantGrowthChamberRecipe> recipe) {
        Optional<RecipeHolder<PlantGrowthChamberSoilRecipe>> soilRecipe = getSoilRecipe(inventory);
        if(soilRecipe.isEmpty())
            return false;

        return level != null &&
                fluidStorage.getFluid(0).getAmount() >= (int)Math.ceil(leftoverFluidConsumption +
                        recipe.value().getFluidConsumption() * soilRecipe.get().value().getFluidConsumptionMultiplier() *
                                fertilizerFluidConsumptionMultiplier * FLUID_CONSUMPTION_MULTIPLIER) &&
                recipe.value().getFluid().test(fluidStorage.getFluid(0)) &&
                canInsertItemsIntoOutputSlots(inventory, new ArrayList<>(Arrays.asList(recipe.value().getMaxOutputCounts())));
    }

    private Optional<RecipeHolder<PlantGrowthChamberSoilRecipe>> getSoilRecipe(SimpleContainer inventory) {
        return level.getRecipeManager().getRecipeFor(EPRecipes.PLANT_GROWTH_CHAMBER_SOIL_TYPE.get(), getRecipeInput(inventory), level);
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