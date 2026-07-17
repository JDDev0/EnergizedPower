package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.CompressorRecipe;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.CompressorMenu;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class CompressorBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, CompressorRecipe> {
    private static final int ITEM_SLOT_INPUT = 0;
    private static final int ITEM_SLOT_OUTPUT = 1;

    public CompressorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.COMPRESSOR_ENTITY, blockPos, blockState,

                "compressor", CompressorMenu::new,

                2, EPRecipes.COMPRESSOR_TYPE, ModConfigs.COMMON_COMPRESSOR_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_COMPRESSOR_CAPACITY.getValue(),
                ModConfigs.COMMON_COMPRESSOR_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_COMPRESSOR_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        if(slotType == SlotType.ITEM) {
            return List.of(
                    //Input only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT)),

                    //Output only
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_OUTPUT)),

                    //Input & Output
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT), SlotEntry.ofOutput(ITEM_SLOT_OUTPUT))
            );
        }

        return super.initSlotGroups(slotType);
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        if(slotType == SlotType.ITEM) {
            for(RelativeDirection direction:RelativeDirection.values())
                conf.setSlotGroupId(direction, 2);
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
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(int thread, RecipeHolder<CompressorRecipe> recipe) {
        if(level == null || !hasRecipe(thread))
            return;

        int startOffset = getSlotStartOffsetFor(thread);
        itemHandler.extractItem(startOffset, recipe.value().getInputCount());
        itemHandler.setStackInSlot(startOffset + 1, recipe.value().getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(startOffset + 1).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress(thread);
    }
}