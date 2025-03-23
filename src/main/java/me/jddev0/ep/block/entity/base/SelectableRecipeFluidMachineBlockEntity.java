package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncCurrentRecipeS2CPacket;
import me.jddev0.ep.recipe.ChangeCurrentRecipeIndexPacketUpdate;
import me.jddev0.ep.recipe.CurrentRecipePacketUpdate;
import me.jddev0.ep.recipe.SetCurrentRecipeIdPacketUpdate;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class SelectableRecipeFluidMachineBlockEntity
        <F extends IFluidHandler, R extends Recipe<Container>>
        extends WorkerFluidMachineBlockEntity<F, R>
        implements ChangeCurrentRecipeIndexPacketUpdate, CurrentRecipePacketUpdate<R>, SetCurrentRecipeIdPacketUpdate {
    protected final UpgradableMenuProvider menuProvider;

    protected final RecipeType<R> recipeType;
    protected final RecipeSerializer<R> recipeSerializer;

    protected ResourceLocation currentRecipeIdForLoad;
    protected R currentRecipe;

    public SelectableRecipeFluidMachineBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                   String machineName, UpgradableMenuProvider menuProvider,
                                                   int slotCount, RecipeType<R> recipeType, RecipeSerializer<R> recipeSerializer,
                                                   int baseRecipeDuration,
                                                   int baseEnergyCapacity, int baseEnergyTransferRate, int baseEnergyConsumptionPerTick,
                                                   FluidStorageMethods<F> fluidStorageMethods, int baseTankCapacity,
                                                   UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, slotCount, baseRecipeDuration, baseEnergyCapacity, baseEnergyTransferRate,
                baseEnergyConsumptionPerTick, fluidStorageMethods, baseTankCapacity, upgradeModifierSlots);

        this.menuProvider = menuProvider;

        this.recipeType = recipeType;
        this.recipeSerializer = recipeSerializer;
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

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        if(currentRecipe != null)
            nbt.put("recipe.id", StringTag.valueOf(currentRecipe.getId().toString()));
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        if(nbt.contains("recipe.id"))
            currentRecipeIdForLoad = ResourceLocation.tryParse(nbt.getString("recipe.id"));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);
        syncCurrentRecipeToPlayer(player);

        return menuProvider.createMenu(id, inventory, this, upgradeModuleInventory, data);
    }

    @Override
    protected final void onTickStart() {
        //Load recipe
        if(currentRecipeIdForLoad != null) {
            List<R> recipes = level.getRecipeManager().getAllRecipesFor(recipeType);
            currentRecipe = recipes.stream().
                    filter(recipe -> recipe.getId().equals(currentRecipeIdForLoad)).
                    findFirst().orElse(null);

            currentRecipeIdForLoad = null;
        }
    }

    @Override
    protected Optional<R> getCurrentWorkData() {
        return Optional.ofNullable(currentRecipe);
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
        if(level == null || currentRecipe == null)
            return false;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        return canCraftRecipe(inventory, currentRecipe);
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

    protected abstract void craftItem(R recipe);

    protected abstract boolean canCraftRecipe(SimpleContainer inventory, R recipe);

    @Override
    public void changeRecipeIndex(boolean downUp) {
        if(level == null || level.isClientSide())
            return;

        List<R> recipes = level.getRecipeManager().getAllRecipesFor(recipeType);
        recipes = recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.getResultItem(level.registryAccess()).
                        getDescriptionId())).
                toList();

        int currentIndex = -1;
        if(currentRecipe != null) {
            for(int i = 0;i < recipes.size();i++) {
                if(currentRecipe.getId().equals(recipes.get(i).getId())) {
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
        setChanged();

        syncCurrentRecipeToPlayers(32);
    }

    @Override
    public void setRecipeId(ResourceLocation recipeId) {
        if(level == null || level.isClientSide())
            return;

        if(recipeId == null) {
            currentRecipe = null;
        }else {
            List<R> recipes = level.getRecipeManager().getAllRecipesFor(recipeType);
            Optional<R> recipe = recipes.stream().filter(r -> r.getId().equals(recipeId)).findFirst();

            currentRecipe = recipe.orElse(null);
        }

        resetProgress();
        setChanged();

        syncCurrentRecipeToPlayers(32);
    }

    protected final void syncCurrentRecipeToPlayer(Player player) {
        ModMessages.sendToPlayer(new SyncCurrentRecipeS2CPacket<>(getBlockPos(), recipeSerializer, currentRecipe),
                (ServerPlayer)player);
    }

    protected final void syncCurrentRecipeToPlayers(int distance) {
        if(level != null && !level.isClientSide())
            ModMessages.sendToPlayersWithinXBlocks(
                    new SyncCurrentRecipeS2CPacket<>(getBlockPos(), recipeSerializer, currentRecipe),
                    getBlockPos(), level.dimension(), distance
            );
    }

    public @Nullable R getCurrentRecipe() {
        return currentRecipe;
    }

    @Override
    public void setCurrentRecipe(@Nullable R currentRecipe) {
        this.currentRecipe = currentRecipe;
    }
}