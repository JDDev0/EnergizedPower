package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.PulverizerRecipe;
import me.jddev0.ep.screen.PulverizerMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PulverizerBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, PulverizerRecipe> {
    private static final int ITEM_SLOT_INPUT = 0;
    private static final int ITEM_SLOT_OUTPUT_MAIN = 1;
    private static final int ITEM_SLOT_OUTPUT_SECONDARY = 2;

    public PulverizerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.PULVERIZER_ENTITY.get(), blockPos, blockState,

                "pulverizer", PulverizerMenu::new,

                3, EPRecipes.PULVERIZER_TYPE.get(), ModConfigs.COMMON_PULVERIZER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_PULVERIZER_CAPACITY.getValue(),
                ModConfigs.COMMON_PULVERIZER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_PULVERIZER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );

        slotCountPerRecipe = 3;
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        if(slotType == SlotType.ITEM) {
            return List.of(
                    //Input only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT)),

                    //Main output only
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_MAIN)),

                    //Secondary output only
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_SECONDARY)),

                    //All outputs
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_MAIN), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_SECONDARY)),

                    //Input & Main Output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_MAIN)),

                    //Input & Secondary Output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_SECONDARY)),

                    //Input & Main Output & Secondary Output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_MAIN), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT_SECONDARY))
            );
        }

        return super.initSlotGroups(slotType);
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        if(slotType == SlotType.ITEM) {
            for(RelativeDirection direction:RelativeDirection.values())
                conf.setSlotGroupId(direction, 6);
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
    protected void craftItem(int thread, RecipeHolder<PulverizerRecipe> recipe) {
        if(level == null || !hasRecipe(thread))
            return;

        int startOffset = getSlotStartOffsetFor(thread);
        ItemStack[] outputs = recipe.value().generateOutputs(level.getRandom(), false);

        itemHandler.extractItem(startOffset, 1);
        if(!outputs[0].isEmpty())
            itemHandler.setStackInSlot(startOffset + 1, outputs[0].
                    copyWithCount(itemHandler.getStackInSlot(startOffset + 1).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStackInSlot(startOffset + 2, outputs[1].
                    copyWithCount(itemHandler.getStackInSlot(startOffset + 2).getCount() + outputs[1].getCount()));

        resetProgress(thread);
    }

    @Override
    protected boolean canCraftRecipe(int thread, SimpleContainer inventory, RecipeHolder<PulverizerRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts(false);

        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, maxOutputs[0]) &&
                (maxOutputs[1].isEmpty() ||
                        InventoryUtils.canInsertItemIntoSlot(inventory, 2, maxOutputs[1]));
    }
}