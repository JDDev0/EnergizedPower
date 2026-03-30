package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AssemblingMachineBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.AssemblingMachineRecipe;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.AssemblingMachineMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public class AssemblingMachineBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, AssemblingMachineRecipe> {
    final InputOutputItemHandler itemHandlerSidedTopBottom = new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 4, i -> i == 4);
    final InputOutputItemHandler itemHandlerSidedFront = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 3, i -> i == 4);
    final InputOutputItemHandler itemHandlerSidedBack = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 4);
    final InputOutputItemHandler itemHandlerSidedLeft = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i == 4);
    final InputOutputItemHandler itemHandlerSidedRight = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 2, i -> i == 4);

    public AssemblingMachineBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ASSEMBLING_MACHINE_ENTITY, blockPos, blockState,

                "assembling_machine", AssemblingMachineMenu::new,

                5, EPRecipes.ASSEMBLING_MACHINE_TYPE, ModConfigs.COMMON_ASSEMBLING_MACHINE_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_ASSEMBLING_MACHINE_CAPACITY.getValue(),
                ModConfigs.COMMON_ASSEMBLING_MACHINE_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ASSEMBLING_MACHINE_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2, 3 -> ((level instanceof ServerLevel serverWorld)?
                            RecipeUtils.isIngredientOfAny(serverWorld, recipeType, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
                    case 4 -> false;
                    default -> super.canPlaceItem(slot, stack);
                };
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 4) {
                    ItemStack itemStack = getItem(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress();
                }

                super.setItem(slot, stack);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                AssemblingMachineBlockEntity.this.setChanged();
            }
        };
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
    protected RecipeInput getRecipeInput(SimpleContainer inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeHolder<AssemblingMachineRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        IngredientWithCount[] inputs = recipe.value().getInputs();

        boolean[] usedIndices = new boolean[4];
        for(int i = 0;i < 4;i++)
            usedIndices[i] = itemHandler.getItem(i).isEmpty();

        int len = Math.min(inputs.length, 4);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 4;j++) {
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

        itemHandler.setItem(4, recipe.value().assemble(null).copyWithCount(
                itemHandler.getItem(4).getCount() +
                        recipe.value().assemble(null).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<AssemblingMachineRecipe> recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 4, recipe.value().assemble(null));
    }
}