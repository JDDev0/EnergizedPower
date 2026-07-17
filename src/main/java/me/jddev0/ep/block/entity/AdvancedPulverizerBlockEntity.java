package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.InputOutputFluidStorage;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.PulverizerRecipe;
import me.jddev0.ep.screen.AdvancedPulverizerMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public class AdvancedPulverizerBlockEntity
        extends SimpleRecipeFluidMachineBlockEntity<RecipeInput, PulverizerRecipe> {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(
            1000 * ModConfigs.COMMON_ADVANCED_PULVERIZER_TANK_CAPACITY.getValue());
    public static final long WATER_CONSUMPTION_PER_RECIPE = FluidUtils.convertMilliBucketsToDroplets(
            ModConfigs.COMMON_ADVANCED_PULVERIZER_WATER_USAGE_PER_RECIPE.getValue());

    private static final int ITEM_SLOT_INPUT = 0;
    private static final int ITEM_SLOT_OUTPUT_MAIN = 1;
    private static final int ITEM_SLOT_OUTPUT_SECONDARY = 2;

    private static final int FLUID_SLOT_INPUT = 0;
    private static final int FLUID_SLOT_OUTPUT = 1;

    public AdvancedPulverizerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_PULVERIZER_ENTITY, blockPos, blockState,

                "advanced_pulverizer", AdvancedPulverizerMenu::new,

                3, EPRecipes.PULVERIZER_TYPE, ModConfigs.COMMON_ADVANCED_PULVERIZER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_ADVANCED_PULVERIZER_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_PULVERIZER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ADVANCED_PULVERIZER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                TANK_CAPACITY,

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
            public boolean isValid(int slot, @NotNull ItemVariant resource) {
                ItemStack stack = resource.toStack();

                return switch(slot) {
                    case 0 -> level == null || RecipeUtils.isIngredientOfAny(level, PulverizerRecipe.Type.INSTANCE, stack);
                    case 1, 2 -> false;
                    default -> super.isValid(slot, resource);
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() &&
                            !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress(0);
                }

                setChanged();
            }
        };
    }

    @Override
    protected EnergizedPowerFluidStorage initFluidStorage() {
        return new EnergizedPowerFluidStorage(2, baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncFluidToPlayers(32);
            }

            @Override
            public boolean isValid(int tank, @NotNull FluidVariant resource) {
                if(!super.isValid(tank, resource))
                    return false;

                return switch(tank) {
                    case 0 -> resource.isOf(Fluids.WATER);
                    case 1 -> resource.isOf(EPFluids.DIRTY_WATER);
                    default -> false;
                };
            }
        };
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        return switch(slotType) {
            case ITEM -> List.of(
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
            case FLUID -> List.of(
                    //Input only
                    SlotGroup.of(SlotEntry.ofInput(FLUID_SLOT_INPUT)),

                    //Output only
                    SlotGroup.of(SlotEntry.ofOutput(FLUID_SLOT_OUTPUT)),

                    //Input & Output
                    SlotGroup.of(SlotEntry.ofInput(FLUID_SLOT_INPUT), SlotEntry.ofOutput(FLUID_SLOT_OUTPUT)),

                    //Output all tanks (Every EP fluid tank should support output to allow draining fluids without losing them)
                    SlotGroup.of(SlotEntry.ofOutput(FLUID_SLOT_INPUT), SlotEntry.ofOutput(FLUID_SLOT_OUTPUT))
            );
        };
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        switch(slotType) {
            case ITEM -> {
                for(RelativeDirection direction:RelativeDirection.values())
                    conf.setSlotGroupId(direction, 6);
            }
            case FLUID -> {
                for(RelativeDirection direction:RelativeDirection.values())
                    conf.setSlotGroupId(direction, 2);
            }
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

    public @Nullable Storage<FluidVariant> getFluidHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return fluidStorage;

        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.FLUID);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.FLUID);
        return conf.createSidedFluidHandlerFor(slotGroups, fluidStorage, facing, side);
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
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

        ItemStack[] outputs = recipe.value().generateOutputs(level.random, true);

        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.extract(FluidVariant.of(Fluids.WATER), WATER_CONSUMPTION_PER_RECIPE, transaction);
            fluidStorage.insert(FluidVariant.of(EPFluids.DIRTY_WATER), WATER_CONSUMPTION_PER_RECIPE, transaction);

            transaction.commit();
        }

        itemHandler.extractItem(0, 1);
        if(!outputs[0].isEmpty())
            itemHandler.setStackInSlot(1, outputs[0].
                    copyWithCount(itemHandler.getStackInSlot(1).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStackInSlot(2, outputs[1].
                    copyWithCount(itemHandler.getStackInSlot(2).getCount() + outputs[1].getCount()));

        resetProgress(thread);
    }

    @Override
    protected boolean canCraftRecipe(int thread, SimpleContainer inventory, RecipeHolder<PulverizerRecipe> recipe) {
        ItemStack[] maxOutputs = recipe.value().getMaxOutputCounts(true);

        return level != null &&
                fluidStorage.getAmount(0) >= WATER_CONSUMPTION_PER_RECIPE &&
                fluidStorage.getTankCapacity(1) - fluidStorage.getAmount(1) >= WATER_CONSUMPTION_PER_RECIPE &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, maxOutputs[0]) &&
                (maxOutputs[1].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 2, maxOutputs[1]));
    }
}