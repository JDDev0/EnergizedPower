package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.PlantGrowthChamberFertilizerRecipe;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import me.jddev0.ep.screen.PlantGrowthChamberMenu;
import me.jddev0.ep.util.ItemStackUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PlantGrowthChamberBlockEntity extends SimpleRecipeMachineBlockEntity<PlantGrowthChamberRecipe> {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_RECIPE_DURATION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSidesSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i > 1 && i < 6);
    final InputOutputItemHandler itemHandlerTopBottomSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i > 1 && i < 6);

    private double fertilizerSpeedMultiplier = 1;
    private double fertilizerEnergyConsumptionMultiplier = 1;

    public PlantGrowthChamberBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.PLANT_GROWTH_CHAMBER_ENTITY, blockPos, blockState,

                "plant_growth_chamber", PlantGrowthChamberMenu::new,

                6, EPRecipes.PLANT_GROWTH_CHAMBER_TYPE, 1,

                ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_CAPACITY.getValue(),
                ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

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
                    case 0 -> world == null || RecipeUtils.isIngredientOfAny(world, PlantGrowthChamberRecipe.Type.INSTANCE, stack);
                    case 1 -> world == null || RecipeUtils.isIngredientOfAny(world, PlantGrowthChamberFertilizerRecipe.Type.INSTANCE, stack);
                    case 2, 3, 4, 5 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.canCombine(stack, itemStack))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                PlantGrowthChamberBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put("recipe.speed_multiplier", NbtDouble.of(fertilizerSpeedMultiplier));
        nbt.put("recipe.energy_consumption_multiplier", NbtDouble.of(fertilizerEnergyConsumptionMultiplier));
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        fertilizerSpeedMultiplier = nbt.getDouble("recipe.speed_multiplier");
        fertilizerEnergyConsumptionMultiplier = nbt.getDouble("recipe.energy_consumption_multiplier");
    }

    @Override
    protected double getRecipeDependentRecipeDuration(PlantGrowthChamberRecipe recipe) {
        return recipe.getTicks() * RECIPE_DURATION_MULTIPLIER / fertilizerSpeedMultiplier;
    }

    @Override
    protected double getRecipeDependentEnergyConsumption(PlantGrowthChamberRecipe recipe) {
        return fertilizerEnergyConsumptionMultiplier;
    }

    @Override
    protected void resetProgress() {
        super.resetProgress();

        fertilizerSpeedMultiplier = 1;
        fertilizerEnergyConsumptionMultiplier = 1;
    }

    @Override
    protected void onStartCrafting(PlantGrowthChamberRecipe recipe) {
        if(world == null)
            return;

        Optional<PlantGrowthChamberFertilizerRecipe> fertilizerRecipe = world.getRecipeManager().
                getFirstMatch(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE, itemHandler, world);

        if(fertilizerRecipe.isPresent()) {
            fertilizerSpeedMultiplier = fertilizerRecipe.get().getSpeedMultiplier();
            fertilizerEnergyConsumptionMultiplier = fertilizerRecipe.get().getEnergyConsumptionMultiplier();

            itemHandler.removeStack(1, 1);
        }
    }

    @Override
    protected void craftItem(PlantGrowthChamberRecipe recipe) {
        if(world == null || !hasRecipe())
            return;

        itemHandler.removeStack(0, 1);

        List<ItemStack> itemStacksInsert = new ArrayList<>(Arrays.asList(recipe.generateOutputs(world.random)));

        List<Integer> emptyIndices = new ArrayList<>(4);
        outer:
        for(ItemStack itemStack:itemStacksInsert) {
            if(itemStack.isEmpty())
                continue;

            for(int i = 2;i < itemHandler.size();i++) {
                ItemStack testItemStack = itemHandler.getStack(i);
                if(emptyIndices.contains(i))
                    continue;

                if(testItemStack.isEmpty()) {
                    emptyIndices.add(i);

                    continue;
                }

                if(ItemStack.areItemsEqual(itemStack, testItemStack) && ItemStack.areNbtEqual(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxCount() - testItemStack.getCount());
                    if(amount > 0) {
                        itemHandler.setStack(i, ItemStackUtils.copyWithCount(itemHandler.getStack(i),
                                testItemStack.getCount() + amount));

                        itemStack.setCount(itemStack.getCount() - amount);

                        if(itemStack.isEmpty())
                            continue outer;
                    }
                }
            }

            //Leftover -> put in empty slot
            if(emptyIndices.isEmpty())
                continue; //Excess items will be vanished

            itemHandler.setStack(emptyIndices.remove(0), itemStack);
        }

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, PlantGrowthChamberRecipe recipe) {
        return world != null &&
                canInsertItemsIntoOutputSlots(inventory, new ArrayList<>(Arrays.asList(recipe.getMaxOutputCounts())));
    }

    private static boolean canInsertItemsIntoOutputSlots(SimpleInventory inventory, List<ItemStack> itemsStacks) {
        List<Integer> checkedIndices = new ArrayList<>(4);
        List<Integer> emptyIndices = new ArrayList<>(4);
        outer:
        for(int i = Math.min(4, itemsStacks.size()) - 1;i >= 0;i--) {
            ItemStack itemStack = itemsStacks.get(i);
            for(int j = 2;j < inventory.size();j++) {
                if(checkedIndices.contains(j) || emptyIndices.contains(j))
                    continue;

                ItemStack testItemStack = inventory.getStack(j);
                if(testItemStack.isEmpty()) {
                    emptyIndices.add(j);

                    continue;
                }

                if(ItemStack.areItemsEqual(itemStack, testItemStack) && ItemStack.areNbtEqual(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxCount() - testItemStack.getCount());

                    if(amount + testItemStack.getCount() == testItemStack.getMaxCount())
                        checkedIndices.add(j);

                    if(amount == itemStack.getCount()) {
                        itemsStacks.remove(i);

                        continue outer;
                    }else {
                        itemStack.decrement(amount);
                    }
                }
            }

            //Leftover -> put in empty slot
            if(emptyIndices.isEmpty())
                return false;

            int index = emptyIndices.remove(0);
            if(itemStack.getCount() == itemStack.getMaxCount())
                checkedIndices.add(index);

            itemsStacks.remove(i);
        }

        return itemsStacks.isEmpty();
    }
}