package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SimpleRecipeMachineBlockEntity<C extends RecipeInput, R extends Recipe<C>>
        extends WorkerMachineBlockEntity<RecipeHolder<R>> {
    protected final UpgradableMenuProvider menuProvider;

    protected final RecipeType<R> recipeType;

    protected int slotCountPerRecipeStartOffset = 0;
    protected int slotCountPerRecipe = 2;

    public SimpleRecipeMachineBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                          String machineName, UpgradableMenuProvider menuProvider,
                                          int slotCount, RecipeType<R> recipeType, int baseRecipeDuration,
                                          int baseEnergyCapacity, int baseEnergyTransferRate, int baseEnergyConsumptionPerTick,
                                          UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, slotCount, baseRecipeDuration, baseEnergyCapacity, baseEnergyTransferRate,
                baseEnergyConsumptionPerTick, upgradeModifierSlots);

        this.menuProvider = menuProvider;

        this.recipeType = recipeType;
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                int slotToCheck = slot - slotCountPerRecipeStartOffset;
                boolean isInputSlot = slotToCheck % slotCountPerRecipe == 0;
                return isInputSlot && (level == null || RecipeUtils.isIngredientOfAny(level, recipeType, stack));
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                int slotToCheck = slot - slotCountPerRecipeStartOffset;
                boolean isInputSlot = slotToCheck % slotCountPerRecipe == 0;
                if(isInputSlot) {
                    ItemStack stack = itemHandler.getStackInSlot(slot);
                    if(!stack.isEmpty() && !previousItemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress(slotToCheck / slotCountPerRecipe);
                }

                setChanged();
            }
        };
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

        return menuProvider.createMenu(id, inventory, this, upgradeModuleInventory, data);
    }

    protected final int getSlotStartOffsetFor(int thread) {
        return slotCountPerRecipeStartOffset + thread * slotCountPerRecipe;
    }

    protected SimpleContainer getInventoryForRecipe(int thread) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        int startOffset = getSlotStartOffsetFor(thread);
        for(int i = 0;i < slotCountPerRecipe;i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i + startOffset));

        return inventory;
    }

    protected abstract C getRecipeInput(Container inventory);

    protected Optional<RecipeHolder<R>> getRecipeFor(Container inventory) {
        return level.getRecipeManager().getRecipeFor(recipeType, getRecipeInput(inventory), level);
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

    protected void craftItem(int thread, RecipeHolder<R> recipe) {
        if(level == null || !hasRecipe(thread))
            return;

        int startOffset = getSlotStartOffsetFor(thread);
        itemHandler.extractItem(startOffset, 1, false);
        itemHandler.setStackInSlot(startOffset + slotCountPerRecipe - 1, recipe.value().getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(startOffset + slotCountPerRecipe - 1).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress(thread);
    }

    protected boolean canCraftRecipe(int thread, SimpleContainer inventory, RecipeHolder<R> recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, slotCountPerRecipe - 1, recipe.value().getResultItem(level.registryAccess()));
    }
}