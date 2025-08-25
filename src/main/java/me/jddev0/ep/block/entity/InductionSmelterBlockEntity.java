package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.InductionSmelterBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.InductionSmelterMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

public class InductionSmelterBlockEntity extends SimpleRecipeMachineBlockEntity<AlloyFurnaceRecipe> {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_INDUCTION_SMELTER_RECIPE_DURATION_MULTIPLIER.getValue();

    private final InputOutputItemHandler itemHandlerSidedFrontTopBottom = new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 3, i -> i > 2 && i < 5);
    private final InputOutputItemHandler itemHandlerSidedBack = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i > 2 && i < 5);
    private final InputOutputItemHandler itemHandlerSidedLeft = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i > 2 && i < 5);
    private final InputOutputItemHandler itemHandlerSidedRight = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 2, i -> i > 2 && i < 5);

    public InductionSmelterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.INDUCTION_SMELTER_ENTITY, blockPos, blockState,

                "induction_smelter", InductionSmelterMenu::new,

                5, EPRecipes.ALLOY_FURNACE_TYPE, 1,

                ModConfigs.COMMON_INDUCTION_SMELTER_CAPACITY.getValue(),
                ModConfigs.COMMON_INDUCTION_SMELTER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_INDUCTION_SMELTER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
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
                            map(AlloyFurnaceRecipe::getInputs).anyMatch(inputs ->
                                    Arrays.stream(inputs).map(IngredientWithCount::input).
                                            anyMatch(ingredient -> ingredient.test(stack)));
                    case 3, 4 -> false;
                    default -> false;
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    ItemStack itemStack = getStack(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.canCombine(stack, itemStack))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                InductionSmelterBlockEntity.this.markDirty();
            }
        };
    }

    public Storage<ItemVariant> getInventoryStorageForDirection(Direction side) {
        if(side == null)
            return null;

        Direction facing = getCachedState().get(InductionSmelterBlock.FACING);

        if(facing.getOpposite() == side)
            return itemHandlerSidedBack.apply(side);

        if(facing.rotateYClockwise() == side)
            return itemHandlerSidedLeft.apply(side);

        if(facing.rotateYCounterclockwise() == side)
            return itemHandlerSidedRight.apply(side);

        return itemHandlerSidedFrontTopBottom.apply(side);
    }

    @Override
    protected void onHasEnoughEnergy() {
        if(world.getBlockState(getPos()).contains(Properties.LIT) &&
                !world.getBlockState(getPos()).get(Properties.LIT)) {
            world.setBlockState(getPos(), getCachedState().with(Properties.LIT, true), 3);
        }
    }

    @Override
    protected void onHasNotEnoughEnergyWithOffTimeout() {
        if(world.getBlockState(getPos()).contains(Properties.LIT) &&
                world.getBlockState(getPos()).get(Properties.LIT)) {
            world.setBlockState(getPos(), getCachedState().with(Properties.LIT, false), 3);
        }
    }

    @Override
    protected double getRecipeDependentRecipeDuration(AlloyFurnaceRecipe recipe) {
        return recipe.getTicks() * RECIPE_DURATION_MULTIPLIER / 2.f;
    }

    protected void craftItem(AlloyFurnaceRecipe recipe) {
        if(world == null || !hasRecipe())
            return;

        IngredientWithCount[] inputs = recipe.getInputs();

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

        ItemStack[] outputs = recipe.generateOutputs(world.random);

        itemHandler.setStack(3, outputs[0].
                copyWithCount(itemHandler.getStack(3).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStack(4, outputs[1].
                    copyWithCount(itemHandler.getStack(4).getCount() + outputs[1].getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, AlloyFurnaceRecipe recipe) {
        ItemStack[] maxOutputs = recipe.getMaxOutputCounts();

        return world != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 3, maxOutputs[0]) &&
                (maxOutputs[1].isEmpty() ||
                        InventoryUtils.canInsertItemIntoSlot(inventory, 4, maxOutputs[1]));
    }
}