package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.WorkerMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.IngredientPacketUpdate;
import me.jddev0.ep.screen.PoweredFurnaceMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PoweredFurnaceBlockEntity
        extends WorkerMachineBlockEntity<RecipeHolder<? extends AbstractCookingRecipe>>
        implements IngredientPacketUpdate {
    private static final List<@NotNull Identifier> RECIPE_BLACKLIST = ModConfigs.COMMON_POWERED_FURNACE_RECIPE_BLACKLIST.getValue();

    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    protected List<Ingredient> ingredientsOfRecipes = new ArrayList<>();

    public PoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.POWERED_FURNACE_ENTITY, blockPos, blockState,

                "powered_furnace",

                2, 1,

                ModConfigs.COMMON_POWERED_FURNACE_CAPACITY.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE
        );
    }

    @Override
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> ((level instanceof ServerLevel serverWorld)?
                            RecipeUtils.isIngredientOfAny(serverWorld, getRecipeForFurnaceModeUpgrade(), stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
                    case 1 -> false;
                    default -> super.canPlaceItem(slot, stack);
                };
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getItem(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress();
                }

                super.setItem(slot, stack);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                PoweredFurnaceBlockEntity.this.setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new EnergyValueContainerData(() -> hasWork()?getCurrentWorkData().map(this::getEnergyConsumptionFor).orElse(-1L):-1, value -> {}),
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
        syncIngredientListToPlayer(player);
        
        return new PoweredFurnaceMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    protected void onHasEnoughEnergy() {
        if(level.getBlockState(getBlockPos()).hasProperty(BlockStateProperties.LIT) &&
                !level.getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT)) {
            level.setBlock(getBlockPos(), getBlockState().setValue(BlockStateProperties.LIT, true), 3);
        }
    }

    @Override
    protected void onHasNotEnoughEnergyWithOffTimeout() {
        if(level.getBlockState(getBlockPos()).hasProperty(BlockStateProperties.LIT) &&
                level.getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT)) {
            level.setBlock(getBlockPos(), getBlockState().setValue(BlockStateProperties.LIT, false), 3);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Optional<RecipeHolder<? extends AbstractCookingRecipe>> getCurrentWorkData() {
        return (Optional<RecipeHolder<? extends AbstractCookingRecipe>>)getRecipeFor(itemHandler, level);
    }

    @Override
    protected boolean hasWork() {
        return hasRecipe(this);
    }

    @Override
    protected void onWorkStarted(RecipeHolder<? extends AbstractCookingRecipe> recipe) {}

    @Override
    protected void onWorkCompleted(RecipeHolder<? extends AbstractCookingRecipe> workData) {
        craftItem(getBlockPos(), getBlockState(), this);
    }

    @Override
    protected double getWorkDataDependentWorkDuration(RecipeHolder<? extends AbstractCookingRecipe> recipe) {
        //Default Cooking Time = 200 -> maxProgress = 100 (= 200 / 2)
        return recipe.value().cookingTime() * RECIPE_DURATION_MULTIPLIER / 2.f;
    }

    private static void craftItem(BlockPos blockPos, BlockState state, PoweredFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.level;

        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = blockEntity.getRecipeFor(blockEntity.itemHandler, level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.removeItem(0, 1);
        blockEntity.itemHandler.setItem(1, recipe.get().value().assemble(null).copyWithCount(
                blockEntity.itemHandler.getItem(1).getCount() + recipe.get().value().assemble(null).getCount()));

        blockEntity.resetProgress();
    }

    private static boolean hasRecipe(PoweredFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.level;

        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = blockEntity.getRecipeFor(blockEntity.itemHandler, level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(blockEntity.itemHandler, 1, recipe.get().value().assemble(null));
    }

    private Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> getRecipeFor(Container container, Level level) {
        if(!(level instanceof ServerLevel serverWorld))
            return Optional.empty();

        return RecipeUtils.getAllRecipesFor(serverWorld, getRecipeForFurnaceModeUpgrade()).
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

        if(level != null && !level.isClientSide()) {
            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getBlockPos(), (ServerLevel)level, 32,
                    new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf((ServerLevel)level, getRecipeForFurnaceModeUpgrade()))
            );
        }
    }

    protected void syncIngredientListToPlayer(Player player) {
        if(!(level instanceof ServerLevel serverWorld))
            return;

        ModMessages.sendServerPacketToPlayer((ServerPlayer)player,
                new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf(serverWorld, getRecipeForFurnaceModeUpgrade())));
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