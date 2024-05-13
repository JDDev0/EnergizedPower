package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AssemblingMachineBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.AssemblingMachineRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.AssemblingMachineMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

public class AssemblingMachineBlockEntity extends SimpleRecipeMachineBlockEntity<AssemblingMachineRecipe> {
    final InputOutputItemHandler itemHandlerSidedTopBottom = new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 4, i -> i == 4);
    final InputOutputItemHandler itemHandlerSidedFront = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 3, i -> i == 4);
    final InputOutputItemHandler itemHandlerSidedBack = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 4);
    final InputOutputItemHandler itemHandlerSidedLeft = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i == 4);
    final InputOutputItemHandler itemHandlerSidedRight = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 2, i -> i == 4);

    public AssemblingMachineBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.ASSEMBLING_MACHINE_ENTITY, blockPos, blockState,

                "assembling_machine", AssemblingMachineMenu::new,

                5, ModRecipes.ASSEMBLING_MACHINE_TYPE, ModConfigs.COMMON_ASSEMBLING_MACHINE_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_ASSEMBLING_MACHINE_CAPACITY.getValue(),
                ModConfigs.COMMON_ASSEMBLING_MACHINE_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ASSEMBLING_MACHINE_ENERGY_CONSUMPTION_PER_TICK.getValue(),

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
                    case 0, 1, 2, 3 -> world == null || world.getRecipeManager().
                            listAllOfType(AssemblingMachineRecipe.Type.INSTANCE).stream().
                            map(RecipeEntry::value).map(AssemblingMachineRecipe::getInputs).anyMatch(inputs ->
                                    Arrays.stream(inputs).map(AssemblingMachineRecipe.IngredientWithCount::input).
                                            anyMatch(ingredient -> ingredient.test(stack)));
                    case 4 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 4) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.areItemsAndComponentsEqual(stack, itemStack))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                AssemblingMachineBlockEntity.this.markDirty();
            }
        };
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
    protected void craftItem(RecipeEntry<AssemblingMachineRecipe> recipe) {
        if(world == null || !hasRecipe())
            return;

        AssemblingMachineRecipe.IngredientWithCount[] inputs = recipe.value().getInputs();

        boolean[] usedIndices = new boolean[4];
        for(int i = 0;i < 4;i++)
            usedIndices[i] = itemHandler.getStack(i).isEmpty();

        int len = Math.min(inputs.length, 4);
        for(int i = 0;i < len;i++) {
            AssemblingMachineRecipe.IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 4;j++) {
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

        itemHandler.setStack(4, recipe.value().getResult(world.getRegistryManager()).copyWithCount(
                itemHandler.getStack(4).getCount() +
                        recipe.value().getResult(world.getRegistryManager()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<AssemblingMachineRecipe> recipe) {
        return world != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 4, recipe.value().getResult(world.getRegistryManager()));
    }
}