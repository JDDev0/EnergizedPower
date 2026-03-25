package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AssemblingMachineBlock;
import me.jddev0.ep.block.entity.base.MenuInventoryStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.screen.AlloyFurnaceMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class AlloyFurnaceBlockEntity
        extends MenuInventoryStorageBlockEntity<SimpleContainer>
        implements IngredientPacketUpdate {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_ALLOY_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    private int progress;
    private int maxProgress;
    private int litDuration;
    private int maxLitDuration;

    protected List<Ingredient> ingredientsOfRecipes = new ArrayList<>();

    private final Predicate<Integer> canOutput = i -> {
        if(i == 3) {
            //Do not allow extraction of fuel items, allow for non fuel items (Bucket of Lava -> Empty Bucket)
            ItemStack item = itemHandler.getItem(i);
            return level != null && level.fuelValues().burnDuration(item) <= 0;
        }

        return i > 3 && i < 6;
    };

    private final InputOutputItemHandler itemHandlerSidedTopBottom = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 3, canOutput);
    private final InputOutputItemHandler itemHandlerSidedFront = new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 3, canOutput);
    private final InputOutputItemHandler itemHandlerSidedBack = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, canOutput);
    private final InputOutputItemHandler itemHandlerSidedLeft = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, canOutput);
    private final InputOutputItemHandler itemHandlerSidedRight = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 2, canOutput);

    public AlloyFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ALLOY_FURNACE_ENTITY, blockPos, blockState,

                "alloy_furnace",

                6
        );
    }

    @Override
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2 -> ((level instanceof ServerLevel serverWorld)?
                            RecipeUtils.isIngredientOfAny(serverWorld, EPRecipes.ALLOY_FURNACE_TYPE, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
                    case 3 -> level != null && level.fuelValues().burnDuration(stack) > 0;
                    case 4, 5 -> false;
                    default -> super.canPlaceItem(slot, stack);
                };
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    ItemStack itemStack = getItem(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress();
                }

                super.setItem(slot, stack);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                AlloyFurnaceBlockEntity.this.setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new ProgressValueContainerData(() -> litDuration, value -> litDuration = value),
                new ProgressValueContainerData(() -> maxLitDuration, value -> maxLitDuration = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncIngredientListToPlayer(player);

        return new AlloyFurnaceMenu(id, this, inventory, itemHandler, data);
    }

    public int getRedstoneOutput() {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(itemHandler);
    }

    public Storage<ItemVariant> getInventoryStorageForDirection(Direction side) {
        if(side == null)
            return null;

        Direction facing = getBlockState().getValue(AssemblingMachineBlock.FACING);

        if(facing == side)
            return itemHandlerSidedFront.apply(side);

        if(facing.getOpposite() == side)
            return itemHandlerSidedBack.apply(side);

        if(facing.getClockWise() == side)
            return itemHandlerSidedLeft.apply(side);

        if(facing.getCounterClockWise() == side)
            return itemHandlerSidedRight.apply(side);

        return itemHandlerSidedTopBottom.apply(side);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("recipe.progress", progress);
        view.putInt("recipe.max_progress", maxProgress);
        view.putInt("recipe.lit_duration", litDuration);
        view.putInt("recipe.max_lit_duration", maxLitDuration);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        progress = view.getIntOr("recipe.progress", 0);
        maxProgress = view.getIntOr("recipe.max_progress", 0);
        litDuration = view.getIntOr("recipe.lit_duration", 0);
        maxLitDuration = view.getIntOr("recipe.max_lit_duration", 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AlloyFurnaceBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        boolean hasNotEnoughFuel = false;

        //Use fuel up regardless if recipe is present
        if(blockEntity.litDuration > 0) {
            blockEntity.litDuration--;

            if(blockEntity.litDuration <= 0) {
                blockEntity.maxLitDuration = 0;
                hasNotEnoughFuel = true;
            }

            setChanged(level, blockPos, state);
        }

        if(blockEntity.hasRecipe()) {
            Optional<RecipeHolder<AlloyFurnaceRecipe>> recipe = blockEntity.getCurrentRecipe();
            if(recipe.isEmpty())
                return;

            //Use next fuel only if recipe is present
            if(blockEntity.litDuration <= 0) {
                ItemStack item = blockEntity.itemHandler.getItem(3);
                blockEntity.litDuration = blockEntity.maxLitDuration = blockEntity.level.fuelValues().burnDuration(item);
                if(blockEntity.maxLitDuration > 0) {
                    blockEntity.onHasEnoughFuel();
                    hasNotEnoughFuel = false;

                    if(!item.getRecipeRemainder().isEmpty())
                        blockEntity.itemHandler.setItem(3, item.getRecipeRemainder());
                    else
                        blockEntity.itemHandler.removeItem(3, 1);
                }
            }

            if(blockEntity.maxProgress == 0)
                blockEntity.maxProgress = Math.max(1, (int)(recipe.get().value().getTicks() * RECIPE_DURATION_MULTIPLIER));

            if(blockEntity.litDuration > 0) {
                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0) {
                    //Reset progress for invalid values

                    if(hasNotEnoughFuel)
                        blockEntity.onHasNotEnoughFuel();

                    blockEntity.resetProgress();
                    setChanged(level, blockPos, state);

                    return;
                }

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    blockEntity.craftItem(recipe.get());

                setChanged(level, blockPos, state);
            }else {
                //Undo recipe progress if no fuel left
                blockEntity.progress = Math.max(blockEntity.progress - 2, 0);

                hasNotEnoughFuel = true;
                setChanged(level, blockPos, state);
            }
        }else {
            blockEntity.resetProgress();
            setChanged(level, blockPos, state);
        }

        if(hasNotEnoughFuel)
            blockEntity.onHasNotEnoughFuel();
    }

    private void onHasEnoughFuel() {
        if(level.getBlockState(getBlockPos()).hasProperty(BlockStateProperties.LIT) &&
                !level.getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT)) {
            level.setBlock(getBlockPos(), getBlockState().setValue(BlockStateProperties.LIT, true), 3);
        }
    }

    private void onHasNotEnoughFuel() {
        if(level.getBlockState(getBlockPos()).hasProperty(BlockStateProperties.LIT) &&
                level.getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT)) {
            level.setBlock(getBlockPos(), getBlockState().setValue(BlockStateProperties.LIT, false), 3);
        }
    }

    private RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    private Optional<RecipeHolder<AlloyFurnaceRecipe>> getRecipeFor(SimpleContainer inventory) {
        if(!(level instanceof ServerLevel serverWorld))
            return Optional.empty();

        return serverWorld.recipeAccess().getRecipeFor(EPRecipes.ALLOY_FURNACE_TYPE, getRecipeInput(inventory), level);
    }

    private Optional<RecipeHolder<AlloyFurnaceRecipe>> getCurrentRecipe() {
        return getRecipeFor(itemHandler);
    }

    private boolean hasRecipe() {
        if(level == null)
            return false;

        Optional<RecipeHolder<AlloyFurnaceRecipe>> recipe = getRecipeFor(itemHandler);

        return recipe.isPresent() && canCraftRecipe(itemHandler, recipe.get());
    }

    protected void craftItem(RecipeHolder<AlloyFurnaceRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        IngredientWithCount[] inputs = recipe.value().getInputs();

        boolean[] usedIndices = new boolean[3];
        for(int i = 0;i < 3;i++)
            usedIndices[i] = itemHandler.getItem(i).isEmpty();

        int len = Math.min(inputs.length, 3);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 3;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = itemHandler.getItem(j);

                if((indexMinCount == -1 || item.getCount() < minCount) && input.input().test(item) &&
                        item.getCount() >= input.count()) {
                    indexMinCount = j;
                    minCount = item.getCount();
                }
            }

            if(indexMinCount == -1)
                return; //Should never happen: Ingredient did not match any item

            usedIndices[indexMinCount] = true;

            itemHandler.removeItem(indexMinCount, input.count());
        }

        ItemStack[] outputs = recipe.value().generateOutputs(level.random);

        itemHandler.setItem(4, outputs[0].
                copyWithCount(itemHandler.getItem(4).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setItem(5, outputs[1].
                    copyWithCount(itemHandler.getItem(5).getCount() + outputs[1].getCount()));

        resetProgress();
    }

    private boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<AlloyFurnaceRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts();

        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 4, maxOutputs[0]) &&
                (maxOutputs[1].isEmpty() ||
                        InventoryUtils.canInsertItemIntoSlot(inventory, 5, maxOutputs[1]));
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 0;
    }

    protected void syncIngredientListToPlayer(Player player) {
        if(!(level instanceof ServerLevel serverWorld))
            return;

        ModMessages.sendServerPacketToPlayer((ServerPlayer)player,
                new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf(serverWorld, EPRecipes.ALLOY_FURNACE_TYPE)));
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