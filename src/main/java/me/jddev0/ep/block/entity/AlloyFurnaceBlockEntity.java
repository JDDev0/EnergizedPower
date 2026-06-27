package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AlloyFurnaceBlock;
import me.jddev0.ep.block.EPBlockStateProperties;
import me.jddev0.ep.block.entity.base.MenuLegacyItemContainerStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.LegacyInputOutputItemHandler;
import me.jddev0.ep.inventory.data.ProgressValueContainerData;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.machine.RedstoneOutput;
import me.jddev0.ep.screen.AlloyFurnaceMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class AlloyFurnaceBlockEntity
        extends MenuLegacyItemContainerStorageBlockEntity<SimpleContainer>
        implements RedstoneOutput {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_ALLOY_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    private int progress;
    private int maxProgress;
    private int litDuration;
    private int maxLitDuration;

    private final Predicate<Integer> canOutput = i -> {
        if(i == 3) {
            //Do not allow extraction of fuel items, allow for non fuel items (Bucket of Lava -> Empty Bucket)
            ItemStack item = itemHandler.getItem(i);
            Integer burnTime = FuelRegistry.INSTANCE.get(item.getItem());
            return burnTime == null || burnTime <= 0;
        }

        return i > 3 && i < 6;
    };

    private final LegacyInputOutputItemHandler itemHandlerSidedTopBottom = new LegacyInputOutputItemHandler(itemHandler, (i, stack) -> i == 3, canOutput);
    private final LegacyInputOutputItemHandler itemHandlerSidedFront = new LegacyInputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 3, canOutput);
    private final LegacyInputOutputItemHandler itemHandlerSidedBack = new LegacyInputOutputItemHandler(itemHandler, (i, stack) -> i == 1, canOutput);
    private final LegacyInputOutputItemHandler itemHandlerSidedLeft = new LegacyInputOutputItemHandler(itemHandler, (i, stack) -> i == 0, canOutput);
    private final LegacyInputOutputItemHandler itemHandlerSidedRight = new LegacyInputOutputItemHandler(itemHandler, (i, stack) -> i == 2, canOutput);

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
                    case 0, 1, 2 -> level == null || level.getRecipeManager().
                            getAllRecipesFor(AlloyFurnaceRecipe.Type.INSTANCE).stream().
                            map(RecipeHolder::value).map(AlloyFurnaceRecipe::getInputs).anyMatch(inputs ->
                                    Arrays.stream(inputs).map(IngredientWithCount::input).
                                            anyMatch(ingredient -> ingredient.test(stack)));
                    case 3 -> {
                        Integer burnTime = FuelRegistry.INSTANCE.get(stack.getItem());
                        yield burnTime != null && burnTime > 0;
                    }
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
        return new AlloyFurnaceMenu(id, this, inventory, itemHandler, data);
    }

    @Override
    public int getRedstoneOutput() {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(itemHandler);
    }

    public Storage<ItemVariant> getInventoryStorageForDirection(Direction side) {
        if(side == null)
            return null;

        Direction facing = getBlockState().getValue(AlloyFurnaceBlock.FACING);

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
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.put("recipe.progress", IntTag.valueOf(progress));
        nbt.put("recipe.max_progress", IntTag.valueOf(maxProgress));
        nbt.put("recipe.lit_duration", IntTag.valueOf(litDuration));
        nbt.put("recipe.max_lit_duration", IntTag.valueOf(maxLitDuration));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        progress = nbt.getInt("recipe.progress");
        maxProgress = nbt.getInt("recipe.max_progress");
        litDuration = nbt.getInt("recipe.lit_duration");
        maxLitDuration = nbt.getInt("recipe.max_lit_duration");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AlloyFurnaceBlockEntity blockEntity) {
        if(level.isClientSide)
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
                Integer burnTime = FuelRegistry.INSTANCE.get(item.getItem());
                blockEntity.litDuration = blockEntity.maxLitDuration = burnTime == null?0:burnTime;
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
        if(level.getBlockState(getBlockPos()).hasProperty(EPBlockStateProperties.WORKING) &&
                !level.getBlockState(getBlockPos()).getValue(EPBlockStateProperties.WORKING)) {
            level.setBlock(getBlockPos(), getBlockState().setValue(EPBlockStateProperties.WORKING, true), 3);
        }
    }

    private void onHasNotEnoughFuel() {
        if(level.getBlockState(getBlockPos()).hasProperty(EPBlockStateProperties.WORKING) &&
                level.getBlockState(getBlockPos()).getValue(EPBlockStateProperties.WORKING)) {
            level.setBlock(getBlockPos(), getBlockState().setValue(EPBlockStateProperties.WORKING, false), 3);
        }
    }

    private RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    private Optional<RecipeHolder<AlloyFurnaceRecipe>> getRecipeFor(SimpleContainer inventory) {
        return level.getRecipeManager().getRecipeFor(EPRecipes.ALLOY_FURNACE_TYPE, getRecipeInput(inventory), level);
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
}