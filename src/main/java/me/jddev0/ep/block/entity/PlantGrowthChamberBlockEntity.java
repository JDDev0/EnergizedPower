package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.screen.PlantGrowthChamberMenu;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
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

    private final InputOutputItemHandler itemHandlerSidesSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i > 1 && i < 6);
    private final InputOutputItemHandler itemHandlerTopSided = new InputOutputItemHandler(itemHandler,
            (i, stack) -> i == 1 || i == 6, i -> i > 1 && i < 7);
    private final InputOutputItemHandler itemHandlerBottomSided = new InputOutputItemHandler(itemHandler,
            (i, stack) -> i == 1 || i == 6, i -> i > 1 && i < 6);

    private double leftoverFluidConsumption = 0;
    private double fertilizerSpeedMultiplier = 1;
    private double fertilizerEnergyConsumptionMultiplier = 1;
    private double fertilizerFluidConsumptionMultiplier = 1;

    protected List<Ingredient> ingredientsOfFertilizerRecipes = new ArrayList<>();
    protected List<Ingredient> ingredientsOfSoilRecipes = new ArrayList<>();

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
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemResource resource) {
                ItemStack stack = resource.toStack();

                return switch(slot) {
                    case 0 -> (level instanceof ServerLevel serverLevel)?
                            RecipeUtils.isIngredientOfAny(serverLevel, recipeType, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack);
                    case 1 -> (level instanceof ServerLevel serverLevel)?
                            RecipeUtils.isIngredientOfAny(serverLevel, EPRecipes.PLANT_GROWTH_CHAMBER_FERTILIZER_TYPE.get(), stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfFertilizerRecipes, stack);
                    case 6 -> (level instanceof ServerLevel serverLevel)?
                            RecipeUtils.isIngredientOfAny(serverLevel, EPRecipes.PLANT_GROWTH_CHAMBER_SOIL_TYPE.get(), stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfSoilRecipes, stack);
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
            public int getCapacity(int index, ItemResource resource) {
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
            public boolean isValid(int index, FluidResource resource) {
                if(!super.isValid(index, resource) || level == null)
                    return false;

                return !(level instanceof ServerLevel serverLevel) || //Always false on client side (Recipes are no longer synced)
                        RecipeUtils.getAllRecipesFor(serverLevel, recipeType).stream().map(RecipeHolder::value).
                        map(PlantGrowthChamberRecipe::getFluid).
                        anyMatch(fluid -> fluid.test(resource));
            }
        };
    }

    public @Nullable ResourceHandler<ItemResource> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        if(side == Direction.UP)
            return itemHandlerTopSided;

        if(side == Direction.DOWN)
            return itemHandlerBottomSided;

        return itemHandlerSidesSided;
    }

    public @Nullable ResourceHandler<FluidResource> getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable EnergyHandler getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncFertilizerAndSoilIngredientListToPlayer(player);

        return super.createMenu(id, inventory, player);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putDouble("recipe.leftover_fluid_consumption", leftoverFluidConsumption);

        view.putDouble("recipe.speed_multiplier", fertilizerSpeedMultiplier);
        view.putDouble("recipe.energy_consumption_multiplier", fertilizerEnergyConsumptionMultiplier);
        view.putDouble("recipe.fluid_consumption_multiplier", fertilizerFluidConsumptionMultiplier);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        leftoverFluidConsumption = view.getDoubleOr("recipe.leftover_fluid_consumption", 0);

        fertilizerSpeedMultiplier = view.getDoubleOr("recipe.speed_multiplier", 1);
        fertilizerEnergyConsumptionMultiplier = view.getDoubleOr("recipe.energy_consumption_multiplier", 1);
        fertilizerFluidConsumptionMultiplier = view.getDoubleOr("recipe.fluid_consumption_multiplier", 1);
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
        if(!(level instanceof ServerLevel serverLevel))
            return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.size());
        for(int i = 0;i < itemHandler.size();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<PlantGrowthChamberFertilizerRecipe>> fertilizerRecipe = serverLevel.recipeAccess().
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
        int fluidConsumptionThisTick = (int)leftoverFluidConsumption;

        //Only keep decimal part
        leftoverFluidConsumption -= fluidConsumptionThisTick;

        //Fluid is always valid, so just use fluid which is in tank
        FluidStack fluid = new FluidStack(fluidStorage.getFluid(0).getFluid(), fluidConsumptionThisTick);
        if(fluid.isEmpty()) {
            return;
        }

        try(Transaction transaction = Transaction.open(null)) {
            fluidStorage.extract(FluidResource.of(fluid), fluid.getAmount(), transaction);

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

        List<ItemStack> itemStacksInsert = new ArrayList<>(Arrays.asList(recipe.value().generateOutputs(level.getRandom())));

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
                fluidStorage.getFluid(0).getAmount() >= (int)Math.ceil(leftoverFluidConsumption +
                        recipe.value().getFluidConsumption() * soilRecipe.get().value().getFluidConsumptionMultiplier() *
                                fertilizerFluidConsumptionMultiplier * FLUID_CONSUMPTION_MULTIPLIER) &&
                recipe.value().getFluid().test(fluidStorage.getFluid(0)) &&
                canInsertItemsIntoOutputSlots(inventory, new ArrayList<>(Arrays.asList(recipe.value().getMaxOutputCounts())));
    }

    private Optional<RecipeHolder<PlantGrowthChamberSoilRecipe>> getSoilRecipe(SimpleContainer inventory) {
        if(!(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }

        return serverLevel.recipeAccess().getRecipeFor(EPRecipes.PLANT_GROWTH_CHAMBER_SOIL_TYPE.get(), getRecipeInput(inventory), serverLevel);
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

    protected void syncFertilizerAndSoilIngredientListToPlayer(Player player) {
        if(!(level instanceof ServerLevel serverLevel))
            return;

        ModMessages.sendToPlayer(
                new SyncIngredientsS2CPacket(getBlockPos(), 1, RecipeUtils.getIngredientsOf(serverLevel, EPRecipes.PLANT_GROWTH_CHAMBER_FERTILIZER_TYPE.get())),
                (ServerPlayer)player
        );

        ModMessages.sendToPlayer(
                new SyncIngredientsS2CPacket(getBlockPos(), 2, RecipeUtils.getIngredientsOf(serverLevel, EPRecipes.PLANT_GROWTH_CHAMBER_SOIL_TYPE.get())),
                (ServerPlayer)player
        );
    }

    public List<Ingredient> getIngredientsOfFertilizerRecipes() {
        return ingredientsOfFertilizerRecipes;
    }

    public List<Ingredient> getIngredientsOfSoilRecipes() {
        return ingredientsOfSoilRecipes;
    }

    @Override
    public void setIngredients(int index, List<Ingredient> ingredients) {
        super.setIngredients(index, ingredients);

        if(index == 1)
            this.ingredientsOfFertilizerRecipes = ingredients;

        if(index == 2)
            this.ingredientsOfSoilRecipes = ingredients;
    }
}