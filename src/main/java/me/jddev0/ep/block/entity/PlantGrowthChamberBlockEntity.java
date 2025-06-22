package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.PlantGrowthChamberFertilizerRecipe;
import me.jddev0.ep.recipe.PlantGrowthChamberRecipe;
import me.jddev0.ep.screen.PlantGrowthChamberMenu;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PlantGrowthChamberBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, PlantGrowthChamberRecipe> {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_PLANT_GROWTH_CHAMBER_RECIPE_DURATION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSidesSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i > 1 && i < 6);
    final InputOutputItemHandler itemHandlerTopBottomSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i > 1 && i < 6);

    private double fertilizerSpeedMultiplier = 1;
    private double fertilizerEnergyConsumptionMultiplier = 1;

    protected List<Ingredient> ingredientsOfFertilizerRecipes = new ArrayList<>();

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
                    case 0 -> ((world instanceof ServerWorld serverWorld)?
                            RecipeUtils.isIngredientOfAny(serverWorld, recipeType, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
                    case 1 -> ((world instanceof ServerWorld serverWorld)?
                            RecipeUtils.isIngredientOfAny(serverWorld, EPRecipes.PLANT_GROWTH_CHAMBER_FERTILIZER_TYPE, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfFertilizerRecipes, stack));
                    case 2, 3, 4, 5 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.areItemsAndComponentsEqual(stack, itemStack))
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
    public @Nullable ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncFertilizerIngredientListToPlayer(player);

        return super.createMenu(id, inventory, player);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        view.putDouble("recipe.speed_multiplier", fertilizerSpeedMultiplier);
        view.putDouble("recipe.energy_consumption_multiplier", fertilizerEnergyConsumptionMultiplier);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        fertilizerSpeedMultiplier = view.getDouble("recipe.speed_multiplier", 0);
        fertilizerEnergyConsumptionMultiplier = view.getDouble("recipe.energy_consumption_multiplier", 0);
    }

    @Override
    protected double getRecipeDependentRecipeDuration(RecipeEntry<PlantGrowthChamberRecipe> recipe) {
        return recipe.value().getTicks() * RECIPE_DURATION_MULTIPLIER / fertilizerSpeedMultiplier;
    }

    @Override
    protected double getRecipeDependentEnergyConsumption(RecipeEntry<PlantGrowthChamberRecipe> recipe) {
        return fertilizerEnergyConsumptionMultiplier;
    }

    @Override
    protected void resetProgress() {
        super.resetProgress();

        fertilizerSpeedMultiplier = 1;
        fertilizerEnergyConsumptionMultiplier = 1;
    }

    @Override
    protected void onStartCrafting(RecipeEntry<PlantGrowthChamberRecipe> recipe) {
        if(!(world instanceof ServerWorld serverWorld))
            return;

        Optional<RecipeEntry<PlantGrowthChamberFertilizerRecipe>> fertilizerRecipe = serverWorld.getRecipeManager().
                getFirstMatch(PlantGrowthChamberFertilizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(itemHandler), world);

        if(fertilizerRecipe.isPresent()) {
            fertilizerSpeedMultiplier = fertilizerRecipe.get().value().getSpeedMultiplier();
            fertilizerEnergyConsumptionMultiplier = fertilizerRecipe.get().value().getEnergyConsumptionMultiplier();

            itemHandler.removeStack(1, 1);
        }
    }

    @Override
    protected RecipeInput getRecipeInput(SimpleInventory inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeEntry<PlantGrowthChamberRecipe> recipe) {
        if(world == null || !hasRecipe())
            return;

        itemHandler.removeStack(0, 1);

        List<ItemStack> itemStacksInsert = new ArrayList<>(Arrays.asList(recipe.value().generateOutputs(world.random)));

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

                if(ItemStack.areItemsAndComponentsEqual(itemStack, testItemStack)) {
                    int amount = Math.min(itemStack.getCount(), testItemStack.getMaxCount() - testItemStack.getCount());
                    if(amount > 0) {
                        itemHandler.setStack(i, itemHandler.getStack(i).
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

            itemHandler.setStack(emptyIndices.remove(0), itemStack);
        }

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<PlantGrowthChamberRecipe> recipe) {
        return world != null &&
                canInsertItemsIntoOutputSlots(inventory, new ArrayList<>(Arrays.asList(recipe.value().getMaxOutputCounts())));
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

                if(ItemStack.areItemsAndComponentsEqual(itemStack, testItemStack)) {
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

    protected void syncFertilizerIngredientListToPlayer(PlayerEntity player) {
        if(!(world instanceof ServerWorld serverWorld))
            return;

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player,
                new SyncIngredientsS2CPacket(getPos(), 1, RecipeUtils.getIngredientsOf(serverWorld, EPRecipes.PLANT_GROWTH_CHAMBER_FERTILIZER_TYPE)));
    }

    public List<Ingredient> getIngredientsOfFertilizerRecipes() {
        return ingredientsOfFertilizerRecipes;
    }

    @Override
    public void setIngredients(int index, List<Ingredient> ingredients) {
        super.setIngredients(index, ingredients);

        if(index == 1)
            this.ingredientsOfFertilizerRecipes = ingredients;
    }
}