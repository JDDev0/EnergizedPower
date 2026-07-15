package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.CrystalGrowthChamberRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.CrystalGrowthChamberMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
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

import java.util.List;

public class CrystalGrowthChamberBlockEntity extends SimpleRecipeMachineBlockEntity<RecipeInput, CrystalGrowthChamberRecipe> {
    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_RECIPE_DURATION_MULTIPLIER.getValue();

    private static final int ITEM_SLOT_INPUT = 0;
    private static final int ITEM_SLOT_OUTPUT = 1;

    public CrystalGrowthChamberBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.CRYSTAL_GROWTH_CHAMBER_ENTITY, blockPos, blockState,

                "crystal_growth_chamber", CrystalGrowthChamberMenu::new,

                2, EPRecipes.CRYSTAL_GROWTH_CHAMBER_TYPE, 1,

                ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_CAPACITY.getValue(),
                ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_CRYSTAL_GROWTH_CHAMBER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemVariant resource) {
                ItemStack stack = resource.toStack();

                return switch(slot) {
                    case 0 -> (level instanceof ServerLevel serverLevel)?
                            RecipeUtils.isIngredientOfAny(serverLevel, recipeType, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack);
                    case 1 -> false;
                    default -> super.isValid(slot, resource);
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0) {
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
    protected double getRecipeDependentRecipeDuration(int thread, RecipeHolder<CrystalGrowthChamberRecipe> recipe) {
        return recipe.value().getTicks() * RECIPE_DURATION_MULTIPLIER;
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(int thread, RecipeHolder<CrystalGrowthChamberRecipe> recipe) {
        if(level == null || !hasRecipe(thread))
            return;

        int startOffset = getSlotStartOffsetFor(thread);
        itemHandler.extractItem(startOffset, recipe.value().getInput().count());

        ItemStack output = recipe.value().generateOutput(level.getRandom());

        if(!output.isEmpty())
            itemHandler.setStackInSlot(startOffset + 1, output.copyWithCount(
                    itemHandler.getStackInSlot(startOffset + 1).getCount() + output.getCount()));

        resetProgress(thread);
    }

    @Override
    protected boolean canCraftRecipe(int thread, SimpleContainer inventory, RecipeHolder<CrystalGrowthChamberRecipe> recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().getMaxOutputCount());
    }
}