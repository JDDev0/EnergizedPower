package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncCurrentRecipeS2CPacket;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.ChangeCurrentRecipeIndexPacketUpdate;
import me.jddev0.ep.recipe.CurrentRecipePacketUpdate;
import me.jddev0.ep.recipe.IngredientPacketUpdate;
import me.jddev0.ep.recipe.SetCurrentRecipeIdPacketUpdate;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class SelectableRecipeMachineBlockEntity<C extends RecipeInput, R extends Recipe<C>>
        extends WorkerMachineBlockEntity<RecipeEntry<R>>
        implements ChangeCurrentRecipeIndexPacketUpdate, CurrentRecipePacketUpdate<R>, SetCurrentRecipeIdPacketUpdate,
        IngredientPacketUpdate {
    protected final UpgradableMenuProvider menuProvider;

    protected final RecipeType<R> recipeType;
    protected final RecipeSerializer<R> recipeSerializer;

    protected Identifier currentRecipeIdForLoad;
    protected RecipeEntry<R> currentRecipe;

    protected List<Ingredient> ingredientsOfRecipes = new ArrayList<>();

    public SelectableRecipeMachineBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                              String machineName, UpgradableMenuProvider menuProvider,
                                              int slotCount, RecipeType<R> recipeType, RecipeSerializer<R> recipeSerializer,
                                              int baseRecipeDuration,
                                              long baseEnergyCapacity, long baseEnergyTransferRate, long baseEnergyConsumptionPerTick,
                                              UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, slotCount, baseRecipeDuration, baseEnergyCapacity, baseEnergyTransferRate,
                baseEnergyConsumptionPerTick, upgradeModifierSlots);

        this.menuProvider = menuProvider;

        this.recipeType = recipeType;
        this.recipeSerializer = recipeSerializer;
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

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, @NotNull RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        if(currentRecipe != null)
            nbt.put("recipe.id", NbtString.of(currentRecipe.id().getValue().toString()));
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, @NotNull RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        if(nbt.contains("recipe.id"))
            currentRecipeIdForLoad = Identifier.tryParse(nbt.getString("recipe.id"));
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        syncCurrentRecipeToPlayer(player);
        syncIngredientListToPlayer(player);

        return menuProvider.createMenu(id, this, inventory, itemHandler, upgradeModuleInventory, data);
    }

    @Override
    protected final void onTickStart() {
        //Load recipe
        if(currentRecipeIdForLoad != null && world instanceof ServerWorld serverWorld) {
            Collection<RecipeEntry<R>> recipes = RecipeUtils.getAllRecipesFor(serverWorld, recipeType);
            currentRecipe = recipes.stream().
                    filter(recipe -> recipe.id().getValue().equals(currentRecipeIdForLoad)).
                    findFirst().orElse(null);

            currentRecipeIdForLoad = null;
        }
    }

    @Override
    protected Optional<RecipeEntry<R>> getCurrentWorkData() {
        return Optional.ofNullable(currentRecipe);
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
        if(world == null || currentRecipe == null)
            return false;

        return canCraftRecipe(itemHandler, currentRecipe);
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

    @Override
    public void changeRecipeIndex(boolean downUp) {
        if(!(world instanceof ServerWorld serverWorld))
            return;

        List<RecipeEntry<R>> recipes = new ArrayList<>(RecipeUtils.getAllRecipesFor(serverWorld, recipeType));
        recipes = recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.id().getValue())).
                toList();

        int currentIndex = -1;
        if(currentRecipe != null) {
            for(int i = 0;i < recipes.size();i++) {
                if(currentRecipe.id().getValue().equals(recipes.get(i).id().getValue())) {
                    currentIndex = i;
                    break;
                }
            }
        }

        currentIndex += downUp?1:-1;
        if(currentIndex < -1)
            currentIndex = recipes.size() - 1;
        else if(currentIndex >= recipes.size())
            currentIndex = -1;

        currentRecipe = currentIndex == -1?null:recipes.get(currentIndex);

        resetProgress();
        markDirty();

        syncCurrentRecipeToPlayers(32);
    }

    @Override
    public void setRecipeId(Identifier recipeId) {
        if(!(world instanceof ServerWorld serverWorld))
            return;

        if(recipeId == null) {
            currentRecipe = null;
        }else {
            Collection<RecipeEntry<R>> recipes = RecipeUtils.getAllRecipesFor(serverWorld, recipeType);
            Optional<RecipeEntry<R>> recipe = recipes.stream().filter(r -> r.id().getValue().equals(recipeId)).findFirst();

            currentRecipe = recipe.orElse(null);
        }

        resetProgress();
        markDirty();

        syncCurrentRecipeToPlayers(32);
    }

    protected final void syncCurrentRecipeToPlayer(PlayerEntity player) {
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player,
                new SyncCurrentRecipeS2CPacket<>(getPos(), recipeSerializer, currentRecipe));
    }

    protected final void syncCurrentRecipeToPlayers(int distance) {
        if(world != null && !world.isClient())
            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getPos(), (ServerWorld)world, distance,
                    new SyncCurrentRecipeS2CPacket<>(getPos(), recipeSerializer, currentRecipe)
            );
    }

    public @Nullable RecipeEntry<R> getCurrentRecipe() {
        return currentRecipe;
    }

    @Override
    public void setCurrentRecipe(@Nullable RecipeEntry<R> currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

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