package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.MetalPressRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import me.jddev0.ep.screen.MetalPressMenu;
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

public class MetalPressBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, MetalPressRecipe> {
    private static final int ITEM_SLOT_INPUT = 0;
    private static final int ITEM_SLOT_PRESS_MOLD = 1;
    private static final int ITEM_SLOT_OUTPUT = 2;

    public MetalPressBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.METAL_PRESS_ENTITY.get(), blockPos, blockState,

                "metal_press", MetalPressMenu::new,

                3, EPRecipes.METAL_PRESS_TYPE.get(), ModConfigs.COMMON_METAL_PRESS_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_METAL_PRESS_CAPACITY.getValue(),
                ModConfigs.COMMON_METAL_PRESS_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_METAL_PRESS_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );

        slotCountPerRecipe = 3;
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public int getCapacity(int index, ItemResource resource) {
                if(index == 1)
                    return 1;

                return super.getCapacity(index, resource);
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemResource resource) {
                ItemStack stack = resource.toStack();

                return switch(slot) {
                    case 0 -> (level instanceof ServerLevel serverLevel)?
                            RecipeUtils.isIngredientOfAny(serverLevel, recipeType, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack);
                    case 1 -> level == null || stack.is(EnergizedPowerItemTags.METAL_PRESS_MOLDS);
                    case 2 -> false;
                    default -> super.isValid(slot, resource);
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0 || slot == 1) {
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
                    //Input only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT)),
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_PRESS_MOLD)),
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT), SlotEntry.ofInput(ITEM_SLOT_PRESS_MOLD)),

                    //Output only
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_OUTPUT)),

                    //Input & Output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT)),

                    //Input & Press Mold input & Output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT), SlotEntry.ofInput(ITEM_SLOT_PRESS_MOLD), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT)),

                    //Input & Press Mold input/output & Output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT), SlotEntry.ofBoth(ITEM_SLOT_PRESS_MOLD), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT)),

                    //Special: Press Mold replacement automation
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_PRESS_MOLD)),
                    SlotGroup.of(SlotEntry.ofBoth(ITEM_SLOT_PRESS_MOLD))
            );
        }

        return super.initSlotGroups(slotType);
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        if(slotType == SlotType.ITEM) {
            for(RelativeDirection direction:RelativeDirection.values())
                conf.setSlotGroupId(direction, 4);

            conf.setSlotGroupId(RelativeDirection.TOP, 8);
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
    protected void craftItem(int thread, RecipeHolder<MetalPressRecipe> recipe) {
        if(level == null || !hasRecipe(thread) || !(level instanceof ServerLevel serverLevel))
            return;

        int startOffset = getSlotStartOffsetFor(thread);
        ItemStack pressMold = itemHandler.getStackInSlot(startOffset + 1).copy();
        if(pressMold.isEmpty() && !pressMold.is(EnergizedPowerItemTags.METAL_PRESS_MOLDS))
            return;

        pressMold.hurtAndBreak(1, serverLevel, null, item -> pressMold.setCount(0));
        itemHandler.setStackInSlot(startOffset + 1, pressMold);
        
        itemHandler.extractItem(startOffset, recipe.value().getInput().count());
        itemHandler.setStackInSlot(startOffset + 2, recipe.value().assemble(null).
                copyWithCount(itemHandler.getStackInSlot(startOffset + 2).getCount() +
                        recipe.value().assemble(null).getCount()));

        resetProgress(thread);
    }
}