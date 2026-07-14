package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.SelectableRecipeFluidMachineBlockEntity;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.screen.StoneSolidifierMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StoneSolidifierBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<RecipeInput, StoneSolidifierRecipe> {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_STONE_SOLIDIFIER_TANK_CAPACITY.getValue();

    private static final int ITEM_SLOT_OUTPUT = 0;

    private static final int FLUID_SLOT_INPUT_1 = 0;
    private static final int FLUID_SLOT_INPUT_2 = 1;

    public StoneSolidifierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.STONE_SOLIDIFIER_ENTITY.get(), blockPos, blockState,

                "stone_solidifier", StoneSolidifierMenu::new,

                1,
                EPRecipes.STONE_SOLIDIFIER_TYPE.get(),
                EPRecipes.STONE_SOLIDIFIER_SERIALIZER.get(),
                ModConfigs.COMMON_STONE_SOLIDIFIER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_STONE_SOLIDIFIER_CAPACITY.getValue(),
                ModConfigs.COMMON_STONE_SOLIDIFIER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_STONE_SOLIDIFIER_CONSUMPTION_PER_TICK.getValue(),

                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR
        );
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemResource resource) {
                return switch(slot) {
                    case 0 -> false;
                    default -> super.isValid(slot, resource);
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
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
            public boolean isValid(int tank, @NotNull FluidResource resource) {
                if(!super.isValid(tank, resource))
                    return false;

                return switch(tank) {
                    case 0 -> resource.matches(new FluidStack(Fluids.WATER, 1));
                    case 1 -> resource.matches(new FluidStack(Fluids.LAVA, 1));
                    default -> false;
                };
            }
        };
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        return switch(slotType) {
            case ITEM -> List.of(
                    //Output only
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_OUTPUT))
            );
            case FLUID -> List.of(
                    //Input 1 only
                    SlotGroup.of(SlotEntry.ofInput(FLUID_SLOT_INPUT_1)),

                    //Input 2 only
                    SlotGroup.of(SlotEntry.ofInput(FLUID_SLOT_INPUT_2)),

                    //All Inputs only
                    SlotGroup.of(SlotEntry.ofInput(FLUID_SLOT_INPUT_1), SlotEntry.ofInput(FLUID_SLOT_INPUT_2)),

                    //Output all tanks (Every EP fluid tank should support output to allow draining fluids without losing them)
                    SlotGroup.of(SlotEntry.ofOutput(FLUID_SLOT_INPUT_1), SlotEntry.ofOutput(FLUID_SLOT_INPUT_2))
            );
        };
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        switch(slotType) {
            case ITEM -> {
                for(RelativeDirection direction:RelativeDirection.values())
                    conf.setSlotGroupId(direction, 0);
            }
            case FLUID -> {
                for(RelativeDirection direction:RelativeDirection.values())
                    conf.setSlotGroupId(direction, 2);
            }
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

    public @Nullable ResourceHandler<FluidResource> getFluidHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return fluidStorage;

        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.FLUID);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.FLUID);
        return conf.createSidedFluidHandlerFor(slotGroups, fluidStorage, facing, side);
    }

    public @Nullable EnergyHandler getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void craftItem(RecipeHolder<StoneSolidifierRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        try(Transaction transaction = Transaction.open(null)) {
            fluidStorage.extract(FluidResource.of(Fluids.WATER), recipe.value().getWaterAmount(), transaction);
            fluidStorage.extract(FluidResource.of(Fluids.LAVA), recipe.value().getLavaAmount(), transaction);

            transaction.commit();
        }

        itemHandler.setStackInSlot(0, recipe.value().assemble(null).
                copyWithCount(itemHandler.getStackInSlot(0).getCount() +
                        recipe.value().assemble(null).getCount()));

        resetProgress(0);
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<StoneSolidifierRecipe> recipe) {
        return level != null &&
                fluidStorage.getFluid(0).getAmount() >= recipe.value().getWaterAmount() &&
                fluidStorage.getFluid(1).getAmount() >= recipe.value().getLavaAmount() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 0, recipe.value().assemble(null));
    }
}