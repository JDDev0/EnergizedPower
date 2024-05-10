package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AssemblingMachineBlock;
import me.jddev0.ep.block.entity.base.SingleRecipeTypeMachineBlockEntity;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.AssemblingMachineRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.AssemblingMachineMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class AssemblingMachineBlockEntity extends SingleRecipeTypeMachineBlockEntity<AssemblingMachineRecipe> {
    private final IItemHandler itemHandlerSidedTopBottom = new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 4, i -> i == 4);
    private final IItemHandler itemHandlerSidedFront = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 3, i -> i == 4);
    private final IItemHandler itemHandlerSidedBack = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 4);
    private final IItemHandler itemHandlerSidedLeft = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i == 4);
    private final IItemHandler itemHandlerSidedRight = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 2, i -> i == 4);

    public AssemblingMachineBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.ASSEMBLING_MACHINE_ENTITY.get(), blockPos, blockState,

                "assembling_machine", AssemblingMachineMenu::new,

                5, ModRecipes.ASSEMBLING_MACHINE_TYPE.get(), ModConfigs.COMMON_ASSEMBLING_MACHINE_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_ASSEMBLING_MACHINE_CAPACITY.getValue(),
                ModConfigs.COMMON_ASSEMBLING_MACHINE_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ASSEMBLING_MACHINE_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        Direction facing = getBlockState().getValue(AssemblingMachineBlock.FACING);

        if(facing == side)
            return itemHandlerSidedFront;

        if(facing.getOpposite() == side)
            return itemHandlerSidedBack;

        if(facing.getClockWise() == side)
            return itemHandlerSidedLeft;

        if(facing.getCounterClockWise() == side)
            return itemHandlerSidedRight;

        return itemHandlerSidedTopBottom;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected boolean isItemValid(int slot, ItemStack stack) {
        return switch(slot) {
            case 0, 1, 2, 3 -> level == null || level.getRecipeManager().
                    getAllRecipesFor(AssemblingMachineRecipe.Type.INSTANCE).stream().
                    map(RecipeHolder::value).map(AssemblingMachineRecipe::getInputs).anyMatch(inputs ->
                            Arrays.stream(inputs).map(AssemblingMachineRecipe.IngredientWithCount::input).
                                    anyMatch(ingredient -> ingredient.test(stack)));
            case 4 -> false;
            default -> false;
        };
    }

    @Override
    protected void onSetItemInSlot(int slot, ItemStack stack) {
        if(slot >= 0 && slot < 4) {
            ItemStack itemStack = itemHandler.getStackInSlot(slot);
            if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                resetProgress();
        }
    }

    @Override
    protected void craftItem(RecipeHolder<AssemblingMachineRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        AssemblingMachineRecipe.IngredientWithCount[] inputs = recipe.value().getInputs();

        boolean[] usedIndices = new boolean[4];
        for(int i = 0;i < 4;i++)
            usedIndices[i] = itemHandler.getStackInSlot(i).isEmpty();

        int len = Math.min(inputs.length, 4);
        for(int i = 0;i < len;i++) {
            AssemblingMachineRecipe.IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 4;j++) {
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

        itemHandler.setStackInSlot(4, recipe.value().getResultItem(level.registryAccess()).copyWithCount(
                itemHandler.getStackInSlot(4).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canInsertItemsIntoOutputSlots(SimpleContainer inventory, RecipeHolder<AssemblingMachineRecipe> recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 4, recipe.value().getResultItem(level.registryAccess()));
    }
}