package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AssemblingMachineBlock;
import me.jddev0.ep.block.entity.base.MenuInventoryStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.screen.AlloyFurnaceMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class AlloyFurnaceBlockEntity
        extends MenuInventoryStorageBlockEntity<SimpleInventory> {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_ALLOY_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    private int progress;
    private int maxProgress;
    private int litDuration;
    private int maxLitDuration;

    private final Predicate<Integer> canOutput = i -> {
        if(i == 3) {
            //Do not allow extraction of fuel items, allow for non fuel items (Bucket of Lava -> Empty Bucket)
            ItemStack item = itemHandler.getStack(i);
            Integer burnTime = FuelRegistry.INSTANCE.get(item.getItem());
            return burnTime == null || burnTime <= 0;
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
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2 -> world == null || world.getRecipeManager().
                            listAllOfType(AlloyFurnaceRecipe.Type.INSTANCE).stream().
                            map(RecipeEntry::value).map(AlloyFurnaceRecipe::getInputs).anyMatch(inputs ->
                                    Arrays.stream(inputs).map(IngredientWithCount::input).
                                            anyMatch(ingredient -> ingredient.test(stack)));
                    case 3 -> {
                        Integer burnTime = FuelRegistry.INSTANCE.get(stack.getItem());
                        yield burnTime != null && burnTime > 0;
                    }
                    case 4, 5 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    ItemStack itemStack = getStack(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.areItemsAndComponentsEqual(stack, itemStack))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                AlloyFurnaceBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new ProgressValueContainerData(() -> litDuration, value -> litDuration = value),
                new ProgressValueContainerData(() -> maxLitDuration, value -> maxLitDuration = value)
        );
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new AlloyFurnaceMenu(id, this, inventory, itemHandler, data);
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(itemHandler);
    }

    public Storage<ItemVariant> getInventoryStorageForDirection(Direction side) {
        if(side == null)
            return null;

        Direction facing = getCachedState().get(AssemblingMachineBlock.FACING);

        if(facing == side)
            return itemHandlerSidedFront.apply(side);

        if(facing.getOpposite() == side)
            return itemHandlerSidedBack.apply(side);

        if(facing.rotateYClockwise() == side)
            return itemHandlerSidedLeft.apply(side);

        if(facing.rotateYCounterclockwise() == side)
            return itemHandlerSidedRight.apply(side);

        return itemHandlerSidedTopBottom.apply(side);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, @NotNull RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.max_progress", NbtInt.of(maxProgress));
        nbt.put("recipe.lit_duration", NbtInt.of(litDuration));
        nbt.put("recipe.max_lit_duration", NbtInt.of(maxLitDuration));
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, @NotNull RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        progress = nbt.getInt("recipe.progress");
        maxProgress = nbt.getInt("recipe.max_progress");
        litDuration = nbt.getInt("recipe.lit_duration");
        maxLitDuration = nbt.getInt("recipe.max_lit_duration");
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AlloyFurnaceBlockEntity blockEntity) {
        if(level.isClient)
            return;

        boolean hasNotEnoughFuel = false;

        //Use fuel up regardless if recipe is present
        if(blockEntity.litDuration > 0) {
            blockEntity.litDuration--;

            if(blockEntity.litDuration <= 0) {
                blockEntity.maxLitDuration = 0;
                hasNotEnoughFuel = true;
            }

            markDirty(level, blockPos, state);
        }

        if(blockEntity.hasRecipe()) {
            Optional<RecipeEntry<AlloyFurnaceRecipe>> recipe = blockEntity.getCurrentRecipe();
            if(recipe.isEmpty())
                return;

            //Use next fuel only if recipe is present
            if(blockEntity.litDuration <= 0) {
                ItemStack item = blockEntity.itemHandler.getStack(3);
                Integer burnTime = FuelRegistry.INSTANCE.get(item.getItem());
                blockEntity.litDuration = blockEntity.maxLitDuration = burnTime == null?0:burnTime;
                if(blockEntity.maxLitDuration > 0) {
                    blockEntity.onHasEnoughFuel();
                    hasNotEnoughFuel = false;

                    if(!item.getRecipeRemainder().isEmpty())
                        blockEntity.itemHandler.setStack(3, item.getRecipeRemainder());
                    else
                        blockEntity.itemHandler.removeStack(3, 1);
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
                    markDirty(level, blockPos, state);

                    return;
                }

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    blockEntity.craftItem(recipe.get());

                markDirty(level, blockPos, state);
            }else {
                //Undo recipe progress if no fuel left
                blockEntity.progress = Math.max(blockEntity.progress - 2, 0);

                hasNotEnoughFuel = true;
                markDirty(level, blockPos, state);
            }
        }else {
            blockEntity.resetProgress();
            markDirty(level, blockPos, state);
        }

        if(hasNotEnoughFuel)
            blockEntity.onHasNotEnoughFuel();
    }

    private void onHasEnoughFuel() {
        if(world.getBlockState(getPos()).contains(Properties.LIT) &&
                !world.getBlockState(getPos()).get(Properties.LIT)) {
            world.setBlockState(getPos(), getCachedState().with(Properties.LIT, true), 3);
        }
    }

    private void onHasNotEnoughFuel() {
        if(world.getBlockState(getPos()).contains(Properties.LIT) &&
                world.getBlockState(getPos()).get(Properties.LIT)) {
            world.setBlockState(getPos(), getCachedState().with(Properties.LIT, false), 3);
        }
    }

    private RecipeInput getRecipeInput(Inventory inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    private Optional<RecipeEntry<AlloyFurnaceRecipe>> getRecipeFor(SimpleInventory inventory) {
        return world.getRecipeManager().getFirstMatch(EPRecipes.ALLOY_FURNACE_TYPE, getRecipeInput(inventory), world);
    }

    private Optional<RecipeEntry<AlloyFurnaceRecipe>> getCurrentRecipe() {
        return getRecipeFor(itemHandler);
    }

    private boolean hasRecipe() {
        if(world == null)
            return false;

        Optional<RecipeEntry<AlloyFurnaceRecipe>> recipe = getRecipeFor(itemHandler);

        return recipe.isPresent() && canCraftRecipe(itemHandler, recipe.get());
    }

    protected void craftItem(RecipeEntry<AlloyFurnaceRecipe> recipe) {
        if(world == null || !hasRecipe())
            return;

        IngredientWithCount[] inputs = recipe.value().getInputs();

        boolean[] usedIndices = new boolean[3];
        for(int i = 0;i < 3;i++)
            usedIndices[i] = itemHandler.getStack(i).isEmpty();

        int len = Math.min(inputs.length, 3);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 3;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = itemHandler.getStack(j);

                if((indexMinCount == -1 || item.getCount() < minCount) && input.input().test(item) &&
                        item.getCount() >= input.count()) {
                    indexMinCount = j;
                    minCount = item.getCount();
                }
            }

            if(indexMinCount == -1)
                return; //Should never happen: Ingredient did not match any item

            usedIndices[indexMinCount] = true;

            itemHandler.removeStack(indexMinCount, input.count());
        }

        ItemStack[] outputs = recipe.value().generateOutputs(world.random);

        itemHandler.setStack(4, outputs[0].
                copyWithCount(itemHandler.getStack(4).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStack(5, outputs[1].
                    copyWithCount(itemHandler.getStack(5).getCount() + outputs[1].getCount()));

        resetProgress();
    }

    private boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<AlloyFurnaceRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts();

        return world != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 4, maxOutputs[0]) &&
                (maxOutputs[1].isEmpty() ||
                        InventoryUtils.canInsertItemIntoSlot(inventory, 5, maxOutputs[1]));
    }

    private void resetProgress() {
        progress = 0;
        maxProgress = 0;
    }
}