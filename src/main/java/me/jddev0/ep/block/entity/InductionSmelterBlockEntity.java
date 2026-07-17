package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.InductionSmelterBlock;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.AlloyFurnaceRecipe;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.screen.InductionSmelterMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.Arrays;
import java.util.List;

public class InductionSmelterBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, AlloyFurnaceRecipe> {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_INDUCTION_SMELTER_RECIPE_DURATION_MULTIPLIER.getValue();

    private static final int ITEM_SLOT_INPUT_1 = 0;
    private static final int ITEM_SLOT_INPUT_2 = 1;
    private static final int ITEM_SLOT_INPUT_3 = 2;
    private static final int ITEM_SLOT_OUTPUT_MAIN = 3;
    private static final int ITEM_SLOT_OUTPUT_SECONDARY = 4;

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
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );

        slotCountPerRecipe = 5;
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemVariant resource) {
                ItemStack stack = resource.toStack();

                return switch(slot) {
                    case 0, 1, 2 -> level == null || level.getRecipeManager().
                            getAllRecipesFor(AlloyFurnaceRecipe.Type.INSTANCE).stream().
                            map(RecipeHolder::value).map(AlloyFurnaceRecipe::getInputs).anyMatch(inputs ->
                                    Arrays.stream(inputs).map(IngredientWithCount::input).
                                            anyMatch(ingredient -> ingredient.test(stack)));
                    case 3, 4 -> false;
                    default -> false;
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot >= 0 && slot < 3) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress(0);
                }

                setChanged();
            }
        };
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        if(slotType == SlotType.ITEM) {
            return List.of(
                    //Input 1 only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_1)),

                    //Input 2 only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_2)),

                    //Input 3 only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_3)),

                    //All inputs
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_1), SlotEntry.ofInput(ITEM_SLOT_INPUT_2), SlotEntry.ofInput(ITEM_SLOT_INPUT_3)),

                    //Main output only
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_MAIN)),

                    //Secondary output only
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_SECONDARY)),

                    //All outputs
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_MAIN), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_SECONDARY)),

                    //Input 1 & all outputs
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_1), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_MAIN), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_SECONDARY)),

                    //Input 2 & all outputs
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_2), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_MAIN), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_SECONDARY)),

                    //Input 3 & all outputs
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_3), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_MAIN), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_SECONDARY)),

                    //Alls Inputs & Main Output & Secondary Output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_1), SlotEntry.ofInput(ITEM_SLOT_INPUT_2), SlotEntry.ofInput(ITEM_SLOT_INPUT_3),
                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_MAIN), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_SECONDARY))
            );
        }

        return super.initSlotGroups(slotType);
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        if(slotType == SlotType.ITEM) {
            conf.setSlotGroupId(RelativeDirection.FRONT, 10);
            conf.setSlotGroupId(RelativeDirection.TOP, 10);
            conf.setSlotGroupId(RelativeDirection.BOTTOM, 10);

            conf.setSlotGroupId(RelativeDirection.LEFT, 7);
            conf.setSlotGroupId(RelativeDirection.BACK, 8);
            conf.setSlotGroupId(RelativeDirection.RIGHT, 9);
        }

        return conf;
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.ITEM);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.ITEM);
        return conf.createSidedItemHandlerFor(slotGroups, itemHandler, facing, side);
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected double getRecipeDependentRecipeDuration(int thread, RecipeHolder<AlloyFurnaceRecipe> recipe) {
        return recipe.value().getTicks() * RECIPE_DURATION_MULTIPLIER;
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(int thread, RecipeHolder<AlloyFurnaceRecipe> recipe) {
        if(level == null || !hasRecipe(thread))
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

            itemHandler.extractItem(indexMinCount, input.count());
        }

        ItemStack[] outputs = recipe.value().generateOutputs(level.random);

        itemHandler.setStackInSlot(3, outputs[0].
                copyWithCount(itemHandler.getStackInSlot(3).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStackInSlot(4, outputs[1].
                    copyWithCount(itemHandler.getStackInSlot(4).getCount() + outputs[1].getCount()));

        resetProgress(thread);
    }

    @Override
    protected boolean canCraftRecipe(int thread, SimpleContainer inventory, RecipeHolder<AlloyFurnaceRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts();

        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 3, maxOutputs[0]) &&
                (maxOutputs[1].isEmpty() ||
                        InventoryUtils.canInsertItemIntoSlot(inventory, 4, maxOutputs[1]));
    }
}