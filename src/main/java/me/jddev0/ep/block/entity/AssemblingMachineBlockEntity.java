package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AssemblingMachineBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.AssemblingMachineRecipe;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.AssemblingMachineMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class AssemblingMachineBlockEntity extends SimpleRecipeMachineBlockEntity<AssemblingMachineRecipe> {
    private final LazyOptional<IItemHandler> lazyItemHandlerSidedTopBottom = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 4, i -> i == 4));
    private final LazyOptional<IItemHandler> lazyItemHandlerSidedFront = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 3, i -> i == 4));
    private final LazyOptional<IItemHandler> lazyItemHandlerSidedBack = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 4));
    private final LazyOptional<IItemHandler> lazyItemHandlerSidedLeft = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i == 4));
    private final LazyOptional<IItemHandler> lazyItemHandlerSidedRight = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 2, i -> i == 4));

    public AssemblingMachineBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ASSEMBLING_MACHINE_ENTITY.get(), blockPos, blockState,

                "assembling_machine", AssemblingMachineMenu::new,

                5, EPRecipes.ASSEMBLING_MACHINE_TYPE.get(), ModConfigs.COMMON_ASSEMBLING_MACHINE_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_ASSEMBLING_MACHINE_CAPACITY.getValue(),
                ModConfigs.COMMON_ASSEMBLING_MACHINE_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ASSEMBLING_MACHINE_ENERGY_CONSUMPTION_PER_TICK.getValue(),

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
                    case 0, 1, 2, 3 -> level == null || level.getRecipeManager().
                            getAllRecipesFor(AssemblingMachineRecipe.Type.INSTANCE).stream().
                            map(AssemblingMachineRecipe::getInputs).anyMatch(inputs ->
                                    Arrays.stream(inputs).map(IngredientWithCount::input).
                                            anyMatch(ingredient -> ingredient.test(stack)));
                    case 4 -> false;
                    default -> false;
                };
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < 4) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameTags(stack, itemStack))
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

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            Direction facing = getBlockState().getValue(AssemblingMachineBlock.FACING);

            if(facing == side)
                return lazyItemHandlerSidedFront.cast();

            if(facing.getOpposite() == side)
                return lazyItemHandlerSidedBack.cast();

            if(facing.getClockWise() == side)
                return lazyItemHandlerSidedLeft.cast();

            if(facing.getCounterClockWise() == side)
                return lazyItemHandlerSidedRight.cast();

            return lazyItemHandlerSidedTopBottom.cast();
        }else if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void craftItem(AssemblingMachineRecipe recipe) {
        if(level == null || !hasRecipe())
            return;

        IngredientWithCount[] inputs = recipe.getInputs();

        boolean[] usedIndices = new boolean[4];
        for(int i = 0;i < 4;i++)
            usedIndices[i] = itemHandler.getStackInSlot(i).isEmpty();

        int len = Math.min(inputs.length, 4);
        for(int i = 0;i < len;i++) {
            IngredientWithCount input = inputs[i];

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

        itemHandler.setStackInSlot(4, recipe.getResultItem(level.registryAccess()).copyWithCount(
                itemHandler.getStackInSlot(4).getCount() +
                        recipe.getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, AssemblingMachineRecipe recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 4, recipe.getResultItem(level.registryAccess()));
    }
}