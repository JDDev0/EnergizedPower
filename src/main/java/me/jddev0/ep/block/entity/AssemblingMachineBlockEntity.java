package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AssemblingMachineBlock;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.AssemblingMachineRecipe;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.IngredientWithCount;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.AssemblingMachineMenu;
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
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AssemblingMachineBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, AssemblingMachineRecipe> {
    private static final int ITEM_SLOT_INPUT_1 = 0;
    private static final int ITEM_SLOT_INPUT_2 = 1;
    private static final int ITEM_SLOT_INPUT_3 = 2;
    private static final int ITEM_SLOT_INPUT_4 = 3;
    private static final int ITEM_SLOT_OUTPUT = 4;

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
            public boolean isValid(int slot, @NotNull ItemResource resource) {
                ItemStack stack = resource.toStack();

                return switch(slot) {
                    case 0, 1, 2, 3 -> ((level instanceof ServerLevel serverLevel)?
                            RecipeUtils.isIngredientOfAny(serverLevel, recipeType, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
                    case 4 -> false;
                    default -> false;
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot >= 0 && slot < 4) {
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

                    //Input 4 only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_4)),

                    //First 3 inputs only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_1), SlotEntry.ofInput(ITEM_SLOT_INPUT_2), SlotEntry.ofInput(ITEM_SLOT_INPUT_3)),

                    //All inputs
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_1), SlotEntry.ofInput(ITEM_SLOT_INPUT_2), SlotEntry.ofInput(ITEM_SLOT_INPUT_3), SlotEntry.ofInput(ITEM_SLOT_INPUT_4)),

                    //Output only
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_OUTPUT)),

                    //Input 1 & output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_1), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT)),

                    //Input 2 & output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_2), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT)),

                    //Input 3 & output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_3), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT)),

                    //Input 4 & output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_4), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT)),

                    //First 3 inputs & output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_1), SlotEntry.ofInput(ITEM_SLOT_INPUT_2), SlotEntry.ofInput(ITEM_SLOT_INPUT_3),
                            SlotEntry.ofOutput(ITEM_SLOT_OUTPUT)),

                    //Alls Inputs & output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT_1), SlotEntry.ofInput(ITEM_SLOT_INPUT_2), SlotEntry.ofInput(ITEM_SLOT_INPUT_3),
                            SlotEntry.ofInput(ITEM_SLOT_INPUT_4), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT))
            );
        }

        return super.initSlotGroups(slotType);
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        if(slotType == SlotType.ITEM) {
            conf.setSlotGroupId(RelativeDirection.TOP, 12);
            conf.setSlotGroupId(RelativeDirection.BOTTOM, 12);

            conf.setSlotGroupId(RelativeDirection.FRONT, 10);
            conf.setSlotGroupId(RelativeDirection.BACK, 7);
            conf.setSlotGroupId(RelativeDirection.LEFT, 8);
            conf.setSlotGroupId(RelativeDirection.RIGHT, 9);
        }

        return conf;
    }

    public @Nullable ResourceHandler<ItemResource> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.ITEM);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.ITEM);
        return conf.createSidedItemHandlerFor(slotGroups, itemHandler, facing, side);
    }

    public @Nullable EnergyHandler getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(int thread, RecipeHolder<AssemblingMachineRecipe> recipe) {
        if(level == null || !hasRecipe(thread))
            return;

        IngredientWithCount[] inputs = recipe.value().getInputs();

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

            itemHandler.extractItem(indexMinCount, input.count());
        }

        itemHandler.setStackInSlot(4, recipe.value().assemble(null).copyWithCount(
                itemHandler.getStackInSlot(4).getCount() +
                        recipe.value().assemble(null).getCount()));

        resetProgress(thread);
    }
}