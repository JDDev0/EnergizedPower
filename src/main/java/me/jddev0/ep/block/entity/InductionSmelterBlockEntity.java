package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.InductionSmelterBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.InductionSmelterMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InductionSmelterBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, AlloyFurnaceRecipe> {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_INDUCTION_SMELTER_RECIPE_DURATION_MULTIPLIER.getValue();

    private final IItemHandler itemHandlerSidedFrontTopBottom = new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 3, i -> i > 2 && i < 5);
    private final IItemHandler itemHandlerSidedBack = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i > 2 && i < 5);
    private final IItemHandler itemHandlerSidedLeft = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i > 2 && i < 5);
    private final IItemHandler itemHandlerSidedRight = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 2, i -> i > 2 && i < 5);

    public InductionSmelterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.INDUCTION_SMELTER_ENTITY.get(), blockPos, blockState,

                "induction_smelter", InductionSmelterMenu::new,

                5, EPRecipes.ALLOY_FURNACE_TYPE.get(), 1,

                ModConfigs.COMMON_INDUCTION_SMELTER_CAPACITY.getValue(),
                ModConfigs.COMMON_INDUCTION_SMELTER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_INDUCTION_SMELTER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2 -> (level instanceof ServerLevel serverLevel)?
                            RecipeUtils.isIngredientOfAny(serverLevel, recipeType, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack);
                    case 3, 4 -> false;
                    default -> false;
                };
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
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

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        Direction facing = getBlockState().getValue(InductionSmelterBlock.FACING);

        if(facing.getOpposite() == side)
            return itemHandlerSidedBack;

        if(facing.getClockWise() == side)
            return itemHandlerSidedLeft;

        if(facing.getCounterClockWise() == side)
            return itemHandlerSidedRight;

        return itemHandlerSidedFrontTopBottom;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
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
    protected double getRecipeDependentRecipeDuration(RecipeHolder<AlloyFurnaceRecipe> recipe) {
        return recipe.value().getTicks() * RECIPE_DURATION_MULTIPLIER / 2.f;
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeHolder<AlloyFurnaceRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        IngredientWithCount[] inputs = recipe.value().getInputs();

        boolean[] usedIndices = new boolean[3];
        for(int i = 0;i < 3;i++)
            usedIndices[i] = itemHandler.getStackInSlot(i).isEmpty();

        int len = Math.min(inputs.length, 3);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 3;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = itemHandler.getStackInSlot(j);

                if((indexMinCount == -1 || item.getCount() < minCount) && input.input().test(item) &&
                        item.getCount() >= input.count()) {
                    indexMinCount = j;
                    minCount = item.getCount();
                }
            }

            if(indexMinCount == -1)
                return; //Should never happen: Ingredient did not match any item

            usedIndices[indexMinCount] = true;

            itemHandler.extractItem(indexMinCount, input.count(), false);
        }

        ItemStack[] outputs = recipe.value().generateOutputs(level.random);

        itemHandler.setStackInSlot(3, outputs[0].
                copyWithCount(itemHandler.getStackInSlot(3).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStackInSlot(4, outputs[1].
                    copyWithCount(itemHandler.getStackInSlot(4).getCount() + outputs[1].getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<AlloyFurnaceRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts();

        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 3, maxOutputs[0]) &&
                (maxOutputs[1].isEmpty() ||
                        InventoryUtils.canInsertItemIntoSlot(inventory, 4, maxOutputs[1]));
    }
}