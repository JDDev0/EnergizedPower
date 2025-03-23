package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.inventory.CombinedContainerData;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class SimpleRecipeMachineBlockEntity<R extends Recipe<Container>>
        extends WorkerMachineBlockEntity<R> {
    protected final UpgradableMenuProvider menuProvider;

    protected final RecipeType<R> recipeType;

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
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return slot == 0 && (level == null || RecipeUtils.isIngredientOfAny(level, recipeType, stack));
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = itemHandler.getStackInSlot(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameTags(stack, itemStack))
                        resetProgress();
                }

                super.setStackInSlot(slot, stack);
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new EnergyValueContainerData(() -> energyConsumptionLeft, value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return menuProvider.createMenu(id, inventory, this, upgradeModuleInventory, data);
    }

    protected Optional<R> getRecipeFor(Container inventory) {
        return level.getRecipeManager().getRecipeFor(recipeType, inventory, level);
    }

    @Override
    protected final Optional<R> getCurrentWorkData() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        return getRecipeFor(inventory);
    }

    @Override
    protected final double getWorkDataDependentWorkDuration(R workData) {
        return getRecipeDependentRecipeDuration(workData);
    }

    protected double getRecipeDependentRecipeDuration(R recipe) {
        return 1;
    }

    @Override
    protected final double getWorkDataDependentEnergyConsumption(R workData) {
        return getRecipeDependentEnergyConsumption(workData);
    }

    protected double getRecipeDependentEnergyConsumption(R recipe) {
        return 1;
    }

    @Override
    protected final boolean hasWork() {
        return hasRecipe();
    }

    protected boolean hasRecipe() {
        if(level == null)
            return false;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<R> recipe = getRecipeFor(inventory);

        return recipe.isPresent() && canCraftRecipe(inventory, recipe.get());
    }

    @Override
    protected final void onWorkStarted(R workData) {
        onStartCrafting(workData);
    }

    protected void onStartCrafting(R recipe) {}

    @Override
    protected final void onWorkCompleted(R workData) {
        craftItem(workData);
    }

    protected void craftItem(R recipe) {
        if(level == null || !hasRecipe())
            return;

        itemHandler.extractItem(0, 1, false);
        itemHandler.setStackInSlot(1, recipe.getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(1).getCount() +
                        recipe.getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }

    protected boolean canCraftRecipe(SimpleContainer inventory, R recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.getResultItem(level.registryAccess()));
    }
}