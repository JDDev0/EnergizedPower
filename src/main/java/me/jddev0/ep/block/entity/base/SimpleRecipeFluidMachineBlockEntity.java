package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.IngredientPacketUpdate;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SimpleRecipeFluidMachineBlockEntity
        <F extends Storage<FluidVariant>, C extends RecipeInput, R extends Recipe<C>>
        extends WorkerFluidMachineBlockEntity<F, RecipeEntry<R>>
        implements IngredientPacketUpdate {
    protected final UpgradableMenuProvider menuProvider;

    protected final RecipeType<R> recipeType;

    protected List<Ingredient> ingredientsOfRecipes = new ArrayList<>();

    public SimpleRecipeFluidMachineBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                               String machineName, UpgradableMenuProvider menuProvider,
                                               int slotCount, RecipeType<R> recipeType, int baseRecipeDuration,
                                               long baseEnergyCapacity, long baseEnergyTransferRate, long baseEnergyConsumptionPerTick,
                                               FluidStorageMethods<F> fluidStorageMethods, long baseTankCapacity,
                                               UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, slotCount, baseRecipeDuration, baseEnergyCapacity, baseEnergyTransferRate,
                baseEnergyConsumptionPerTick, fluidStorageMethods, baseTankCapacity, upgradeModifierSlots);

        this.menuProvider = menuProvider;

        this.recipeType = recipeType;
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(energyConsumptionLeft, index - 4);
                    case 8 -> hasEnoughEnergy?1:0;
                    case 9 -> redstoneMode.ordinal();
                    case 10 -> comparatorMode.ordinal();
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
                    case 4, 5, 6, 7, 8 -> {}
                    case 9 -> redstoneMode = RedstoneMode.fromIndex(value);
                    case 10 -> comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 11;
            }
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);
        syncIngredientListToPlayer(player);

        return menuProvider.createMenu(id, this, inventory, itemHandler, upgradeModuleInventory, data);
    }

    protected abstract C getRecipeInput(SimpleInventory inventory);

    protected Optional<RecipeEntry<R>> getRecipeFor(SimpleInventory inventory) {
        if(!(world instanceof ServerWorld serverWorld))
            return Optional.empty();

        return serverWorld.getRecipeManager().getFirstMatch(recipeType, getRecipeInput(inventory), world);
    }

    @Override
    protected final Optional<RecipeEntry<R>> getCurrentWorkData() {
        return getRecipeFor(itemHandler);
    }

    @Override
    protected final double getWorkDataDependentWorkDuration(RecipeEntry<R> workData) {
        return getRecipeDependentRecipeDuration(workData);
    }

    protected double getRecipeDependentRecipeDuration(RecipeEntry<R> recipe) {
        return 1;
    }

    @Override
    protected final double getWorkDataDependentEnergyConsumption(RecipeEntry<R> workData) {
        return getRecipeDependentEnergyConsumption(workData);
    }

    protected double getRecipeDependentEnergyConsumption(RecipeEntry<R> recipe) {
        return 1;
    }

    @Override
    protected final boolean hasWork() {
        return hasRecipe();
    }

    protected boolean hasRecipe() {
        if(world == null)
            return false;

        Optional<RecipeEntry<R>> recipe = getRecipeFor(itemHandler);

        return recipe.isPresent() && canCraftRecipe(itemHandler, recipe.get());
    }

    @Override
    protected final void onWorkStarted(RecipeEntry<R> workData) {
        onStartCrafting(workData);
    }

    protected void onStartCrafting(RecipeEntry<R> recipe) {}

    @Override
    protected final void onWorkCompleted(RecipeEntry<R> workData) {
        craftItem(workData);
    }

    protected abstract void craftItem(RecipeEntry<R> recipe);

    protected abstract boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<R> recipe);

    protected void syncIngredientListToPlayer(PlayerEntity player) {
        if(!(world instanceof ServerWorld serverWorld))
            return;

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player,
                new SyncIngredientsS2CPacket(getPos(), 0, RecipeUtils.getIngredientsOf(serverWorld, recipeType)));
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