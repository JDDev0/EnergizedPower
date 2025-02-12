package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.IngredientPacketUpdate;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.InventoryUtils;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SimpleRecipeMachineBlockEntity<C extends RecipeInput, R extends Recipe<C>>
        extends WorkerMachineBlockEntity<RecipeHolder<R>>
        implements IngredientPacketUpdate {
    protected final UpgradableMenuProvider menuProvider;

    protected final RecipeType<R> recipeType;

    protected List<Ingredient> ingredientsOfRecipes = new ArrayList<>();

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
                return slot == 0 && ((level instanceof ServerLevel serverLevel)?
                        RecipeUtils.isIngredientOfAny(serverLevel, recipeType, stack):
                        RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = itemHandler.getStackInSlot(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
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
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(maxProgress, index - 2);
                    case 4, 5 -> ByteUtils.get2Bytes(energyConsumptionLeft, index - 4);
                    case 6 -> hasEnoughEnergy?1:0;
                    case 7 -> redstoneMode.ordinal();
                    case 8 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> progress = ByteUtils.with2Bytes(
                            progress, (short)value, index
                    );
                    case 2, 3 -> maxProgress = ByteUtils.with2Bytes(
                            maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6 -> {}
                    case 7 -> redstoneMode = RedstoneMode.fromIndex(value);
                    case 8 -> comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int getCount() {
                return 9;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncIngredientListToPlayer(player);

        return menuProvider.createMenu(id, inventory, this, upgradeModuleInventory, data);
    }

    protected abstract C getRecipeInput(Container inventory);

    protected Optional<RecipeHolder<R>> getRecipeFor(Container inventory) {
        if(!(level instanceof ServerLevel serverLevel))
            return Optional.empty();

        return serverLevel.recipeAccess().getRecipeFor(recipeType, getRecipeInput(inventory), level);
    }

    @Override
    protected final Optional<RecipeHolder<R>> getCurrentWorkData() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        return getRecipeFor(inventory);
    }

    @Override
    protected final double getWorkDataDependentWorkDuration(RecipeHolder<R> workData) {
        return getRecipeDependentRecipeDuration(workData);
    }

    protected double getRecipeDependentRecipeDuration(RecipeHolder<R> recipe) {
        return 1;
    }

    @Override
    protected final double getWorkDataDependentEnergyConsumption(RecipeHolder<R> workData) {
        return getRecipeDependentEnergyConsumption(workData);
    }

    protected double getRecipeDependentEnergyConsumption(RecipeHolder<R> recipe) {
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

        Optional<RecipeHolder<R>> recipe = getRecipeFor(inventory);

        return recipe.isPresent() && canCraftRecipe(inventory, recipe.get());
    }

    @Override
    protected final void onWorkStarted(RecipeHolder<R> workData) {
        onStartCrafting(workData);
    }

    protected void onStartCrafting(RecipeHolder<R> recipe) {}

    @Override
    protected final void onWorkCompleted(RecipeHolder<R> workData) {
        craftItem(workData);
    }

    protected void craftItem(RecipeHolder<R> recipe) {
        if(level == null || !hasRecipe())
            return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        itemHandler.extractItem(0, 1, false);
        itemHandler.setStackInSlot(1, recipe.value().assemble(getRecipeInput(inventory), level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(1).getCount() +
                        recipe.value().assemble(getRecipeInput(inventory), level.registryAccess()).getCount()));

        resetProgress();
    }

    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<R> recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().assemble(getRecipeInput(inventory), level.registryAccess()));
    }

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