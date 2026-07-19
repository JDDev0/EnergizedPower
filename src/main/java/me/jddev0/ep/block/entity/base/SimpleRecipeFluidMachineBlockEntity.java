package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.inventory.data.CombinedContainerData;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.IngredientPacketUpdate;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SimpleRecipeFluidMachineBlockEntity
        <C extends RecipeInput, R extends Recipe<C>>
        extends WorkerFluidMachineBlockEntity<RecipeHolder<R>>
        implements IngredientPacketUpdate {
    protected final UpgradableMenuProvider menuProvider;

    protected final RecipeType<R> recipeType;

    protected List<Ingredient> ingredientsOfRecipes = new ArrayList<>();

    protected int slotCountPerRecipeStartOffset = 0;
    protected int slotCountPerRecipe = 2;

    public SimpleRecipeFluidMachineBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                               String machineName, UpgradableMenuProvider menuProvider,
                                               int slotCount, RecipeType<R> recipeType, int baseRecipeDuration,
                                               int baseEnergyCapacity, int baseEnergyTransferRate, int baseEnergyConsumptionPerTick,
                                               int baseTankCapacity,
                                               UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, slotCount, baseRecipeDuration, baseEnergyCapacity, baseEnergyTransferRate,
                baseEnergyConsumptionPerTick, baseTankCapacity, upgradeModifierSlots);

        this.menuProvider = menuProvider;

        this.recipeType = recipeType;
    }

    @Override
    protected ContainerData initContainerData() {
        //this.wokerThreadCount is not yet assigned when this method gets called
        int workerThreadCount = initWorkerThreadCount();

        List<ContainerData> combinedContainerDataList = new ArrayList<>(4 * workerThreadCount + 3);
        for(int i = 0;i < workerThreadCount;i++) {
            final int thread = i;

            combinedContainerDataList.add(new ProgressValueContainerData(() -> progress[thread], value -> progress[thread] = value));
            combinedContainerDataList.add(new ProgressValueContainerData(() -> maxProgress[thread], value -> maxProgress[thread] = value));
            combinedContainerDataList.add(new EnergyValueContainerData(() -> energyConsumptionLeft[thread], value -> {}));
            combinedContainerDataList.add(new BooleanValueContainerData(() -> hasEnoughEnergy[thread], value -> {}));
        }

        combinedContainerDataList.add(new EnergyValueContainerData(this::getEnergyConsumptionPerTickSum, value -> {}));

        combinedContainerDataList.add(new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value));
        combinedContainerDataList.add(new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value));

        return new CombinedContainerData(combinedContainerDataList.toArray(ContainerData[]::new));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);
        syncIngredientListToPlayer(player);
        syncIOConfigurationToPlayer(player);

        return menuProvider.createMenu(id, inventory, this, upgradeModuleInventory, data);
    }

    protected final int getSlotStartOffsetFor(int thread) {
        return slotCountPerRecipeStartOffset + thread * slotCountPerRecipe;
    }

    protected SimpleContainer getInventoryForRecipe(int thread) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.size());
        int startOffset = getSlotStartOffsetFor(thread);
        for(int i = 0;i < slotCountPerRecipe;i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i + startOffset));

        return inventory;
    }

    protected abstract C getRecipeInput(Container inventory);

    protected Optional<RecipeHolder<R>> getRecipeFor(Container inventory) {
        if(!(level instanceof ServerLevel serverLevel))
            return Optional.empty();

        return serverLevel.recipeAccess().getRecipeFor(recipeType, getRecipeInput(inventory), level);
    }

    @Override
    protected final Optional<RecipeHolder<R>> getCurrentWorkData(int thread) {
        return getRecipeFor(getInventoryForRecipe(thread));
    }

    @Override
    protected final double getWorkDataDependentWorkDuration(int thread, RecipeHolder<R> workData) {
        return getRecipeDependentRecipeDuration(thread, workData);
    }

    protected double getRecipeDependentRecipeDuration(int thread, RecipeHolder<R> recipe) {
        return 1;
    }

    @Override
    protected final double getWorkDataDependentEnergyConsumption(int thread, RecipeHolder<R> workData) {
        return getRecipeDependentEnergyConsumption(thread, workData);
    }

    protected double getRecipeDependentEnergyConsumption(int thread, RecipeHolder<R> recipe) {
        return 1;
    }

    @Override
    protected final boolean hasWork(int thread) {
        return hasRecipe(thread);
    }

    protected boolean hasRecipe(int thread) {
        if(level == null)
            return false;

        SimpleContainer inventory = getInventoryForRecipe(thread);

        Optional<RecipeHolder<R>> recipe = getRecipeFor(inventory);

        return recipe.isPresent() && canCraftRecipe(thread, inventory, recipe.get());
    }

    @Override
    protected final void onWorkStarted(int thread, RecipeHolder<R> workData) {
        onStartCrafting(thread, workData);
    }

    protected void onStartCrafting(int thread, RecipeHolder<R> recipe) {}

    @Override
    protected final void onWorkTicked(int thread, RecipeHolder<R> workData) {
        onCraftingTicked(thread, workData);
    }

    protected void onCraftingTicked(int thread, RecipeHolder<R> recipe) {}

    @Override
    protected final void onWorkCompleted(int thread, RecipeHolder<R> workData) {
        craftItem(thread, workData);
    }

    protected abstract void craftItem(int thread, RecipeHolder<R> recipe);

    protected abstract boolean canCraftRecipe(int thread, SimpleContainer inventory, RecipeHolder<R> recipe);

    protected void syncIngredientListToPlayer(Player player) {
        if(!(level instanceof ServerLevel serverLevel))
            return;

        ModMessages.sendToPlayer(new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf(serverLevel, recipeType)), (ServerPlayer)player);
    }

    public List<Ingredient> getIngredientsOfRecipes() {
        return new ArrayList<>(ingredientsOfRecipes);
    }

    @Override
    public void setIngredients(int index, List<Ingredient> ingredients) {
        if(index == 0)
            this.ingredientsOfRecipes = ingredients;
    }
}