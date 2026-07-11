package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.WorkerFluidMachineBlockEntity;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.InputOutputFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.IngredientPacketUpdate;
import me.jddev0.ep.screen.PoweredFurnaceMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import me.jddev0.ep.util.XPUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PoweredFurnaceBlockEntity
        extends WorkerFluidMachineBlockEntity<RecipeHolder<? extends AbstractCookingRecipe>>
        implements IngredientPacketUpdate {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(1000 * ModConfigs.COMMON_POWERED_FURNACE_FLUID_TANK_CAPACITY.getValue());

    private static final List<@NotNull Identifier> RECIPE_BLACKLIST = ModConfigs.COMMON_POWERED_FURNACE_RECIPE_BLACKLIST.getValue();

    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);
    private final InputOutputFluidStorage fluidStorageSided = new InputOutputFluidStorage(fluidStorage, (i, stack) -> false, i -> true);

    private double leftoverXPAmount = 0;

    protected List<Ingredient> ingredientsOfRecipes = new ArrayList<>();

    public PoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.POWERED_FURNACE_ENTITY, blockPos, blockState,

                "powered_furnace",

                2, 1,

                ModConfigs.COMMON_POWERED_FURNACE_CAPACITY.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING,
                UpgradeModuleModifier.XP_YIELD
        );
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemVariant resource) {
                ItemStack stack = resource.toStack();

                boolean isInputSlot = slot < workerThreadCount;
                return isInputSlot && ((level instanceof ServerLevel serverLevel)?
                        RecipeUtils.isIngredientOfAny(serverLevel, getRecipeForFurnaceModeUpgrade(), stack):
                        RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                boolean isInputSlot = slot < workerThreadCount;
                if(isInputSlot) {
                    ItemStack stack = itemHandler.getStackInSlot(slot);
                    if(!stack.isEmpty() && !previousItemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress(slot);
                }

                setChanged();
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
            public boolean isValid(int index, FluidVariant resource) {
                if(!super.isValid(index, resource) || level == null)
                    return false;

                return resource.is(ConventionalFluidTags.EXPERIENCE);
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress[0], value -> progress[0] = value),
                new ProgressValueContainerData(() -> maxProgress[0], value -> maxProgress[0] = value),
                new EnergyValueContainerData(() -> hasWork(0)?getCurrentWorkData(0).
                        map(workData -> getEnergyConsumptionFor(0, workData)).orElse(-1L):-1, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[0], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[0], value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);
        syncIngredientListToPlayer(player);

        return new PoweredFurnaceMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable Storage<FluidVariant> getFluidHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return fluidStorage;

        return fluidStorageSided;
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putDouble("recipe.leftover_xp_amount", leftoverXPAmount);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        leftoverXPAmount = view.getDoubleOr("recipe.leftover_xp_amount", 0.);
    }

    protected SimpleContainer getInventoryForRecipe(int thread) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.size());
        for(int i = 0;i < 2;i++)
            inventory.setItem(i, itemHandler.getStackInSlot(thread + i * workerThreadCount));

        return inventory;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Optional<RecipeHolder<? extends AbstractCookingRecipe>> getCurrentWorkData(int thread) {
        return (Optional<RecipeHolder<? extends AbstractCookingRecipe>>)getRecipeFor(getInventoryForRecipe(thread), level);
    }

    @Override
    protected final boolean hasWork(int thread) {
        return hasRecipe(thread);
    }

    @Override
    protected void onWorkStarted(int thread, RecipeHolder<? extends AbstractCookingRecipe> recipe) {}

    @Override
    protected void onWorkCompleted(int thread, RecipeHolder<? extends AbstractCookingRecipe> workData) {
        craftItem(thread, workData);
    }

    @Override
    protected double getWorkDataDependentWorkDuration(int thread, RecipeHolder<? extends AbstractCookingRecipe> recipe) {
        return recipe.value().cookingTime() * RECIPE_DURATION_MULTIPLIER;
    }

    private void craftItem(int thread, RecipeHolder<? extends AbstractCookingRecipe> workData) {
        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = getRecipeFor(getInventoryForRecipe(thread), level);

        if(!hasRecipe(thread) || recipe.isEmpty())
            return;

        itemHandler.extractItem(thread, 1);
        itemHandler.setStackInSlot(thread + 1, recipe.get().value().assemble(null).copyWithCount(
                itemHandler.getStackInSlot(thread + 1).getCount() + recipe.get().value().assemble(null).getCount()));

        if(upgradeModuleInventory.getMainUpgradeModuleModifier(6) == UpgradeModuleModifier.XP_YIELD) {
            leftoverXPAmount += recipe.get().value().experience() * upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.XP_YIELD);
            int xpYieldThisTick = (int)leftoverXPAmount;

            //Only keep decimal part
            leftoverXPAmount -= xpYieldThisTick;

            try(Transaction transaction = Transaction.openOuter()) {
                //Do not check if overflow -> Extra XP should just vanish
                fluidStorage.insert(FluidVariant.of(EPFluids.LIQUID_XP), XPUtils.XP_TO_LIQUID_RATIO * xpYieldThisTick, transaction);

                transaction.commit();
            }
        }

        resetProgress(thread);
    }

    private boolean hasRecipe(int thread) {
        SimpleContainer inventory = getInventoryForRecipe(thread);

        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = getRecipeFor(inventory, level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.get().value().assemble(null));
    }

    private Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> getRecipeFor(Container container, Level level) {
        if(!(level instanceof ServerLevel serverLevel))
            return Optional.empty();

        return RecipeUtils.getAllRecipesFor(serverLevel, getRecipeForFurnaceModeUpgrade()).
                stream().filter(recipe -> !RECIPE_BLACKLIST.contains(recipe.id().identifier())).
                filter(recipe -> recipe.value().matches(new SingleRecipeInput(container.getItem(0)), level)).
                findFirst();
    }

    public RecipeType<? extends AbstractCookingRecipe> getRecipeForFurnaceModeUpgrade() {
        double value = upgradeModuleInventory.getUpgradeModuleModifierEffect(3, UpgradeModuleModifier.FURNACE_MODE);
        if(value == 1)
            return RecipeType.BLASTING;
        else if(value == 2)
            return RecipeType.SMOKING;

        return RecipeType.SMELTING;
    }

    @Override
    protected void updateUpgradeModules() {
        super.updateUpgradeModules();

        if(level != null && !level.isClientSide())
            ModMessages.sendToPlayersWithinXBlocks(
                    new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf((ServerLevel)level, getRecipeForFurnaceModeUpgrade())),
                    getBlockPos(), (ServerLevel)level, 32
            );
    }

    protected void syncIngredientListToPlayer(Player player) {
        if(!(level instanceof ServerLevel serverLevel))
            return;

        ModMessages.sendToPlayer(
                new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf(serverLevel, getRecipeForFurnaceModeUpgrade())),
                (ServerPlayer)player
        );
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