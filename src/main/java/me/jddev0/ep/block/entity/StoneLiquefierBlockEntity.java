package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.InputOutputFluidStorage;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.StoneLiquefierRecipe;
import me.jddev0.ep.screen.StoneLiquefierMenu;
import me.jddev0.ep.util.FluidStackUtils;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StoneLiquefierBlockEntity
        extends SimpleRecipeFluidMachineBlockEntity<RecipeInput, StoneLiquefierRecipe> {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_STONE_LIQUEFIER_TANK_CAPACITY.getValue();

    private static final int ITEM_SLOT_INPUT = 0;

    private static final int FLUID_SLOT_OUTPUT = 0;

    public StoneLiquefierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.STONE_LIQUEFIER_ENTITY.get(), blockPos, blockState,

                "stone_liquefier", StoneLiquefierMenu::new,

                1, EPRecipes.STONE_LIQUEFIER_TYPE.get(), ModConfigs.COMMON_STONE_LIQUEFIER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_STONE_LIQUEFIER_CAPACITY.getValue(),
                ModConfigs.COMMON_STONE_LIQUEFIER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_STONE_LIQUEFIER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_PULLING
        );

        slotCountPerRecipe = 1;
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemResource resource) {
                ItemStack stack = resource.toStack();

                return switch(slot) {
                    case 0 -> (level instanceof ServerLevel serverLevel)?
                            RecipeUtils.isIngredientOfAny(serverLevel, recipeType, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack);
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
        return new EnergizedPowerFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncFluidToPlayers(32);
            }

            @Override
            public boolean isValid(int index, FluidResource resource) {
                if(!super.isValid(index, resource) || level == null)
                    return false;

                return !(level instanceof ServerLevel serverLevel) || //Always false on client side (Recipes are no longer synced)
                        RecipeUtils.getAllRecipesFor(serverLevel, recipeType).stream().map(RecipeHolder::value).
                        map(StoneLiquefierRecipe::getOutput).
                        anyMatch(resource::matches);
            }
        };
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        return switch(slotType) {
            case ITEM -> List.of(
                    //Input only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_INPUT))
            );
            case FLUID -> List.of(
                    //Output only
                    SlotGroup.of(SlotEntry.ofOutput(FLUID_SLOT_OUTPUT))
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
                    conf.setSlotGroupId(direction, 0);
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
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(int thread, RecipeHolder<StoneLiquefierRecipe> recipe) {
        if(level == null || !hasRecipe(thread))
            return;

        FluidStack output = FluidStackUtils.fromNullableFluidStackTemplate(recipe.value().getOutput()).
                copyWithAmount(FluidStackUtils.fromNullableFluidStackTemplate(recipe.value().getOutput()).getAmount());

        try(Transaction transaction = Transaction.open(null)) {
            fluidStorage.insert(FluidResource.of(output), output.getAmount(), transaction);

            transaction.commit();
        }

        itemHandler.extractItem(0, 1);

        resetProgress(thread);
    }

    @Override
    protected boolean canCraftRecipe(int thread, SimpleContainer inventory, RecipeHolder<StoneLiquefierRecipe> recipe) {
        int fluidAmountInTank = fluidStorage.getFluid(0).getAmount();
        int fluidAmountInRecipe = FluidStackUtils.fromNullableFluidStackTemplate(recipe.value().getOutput()).getAmount();

        return level != null && fluidStorage.getTankCapacity(0) - fluidAmountInTank >= fluidAmountInRecipe &&
                (fluidStorage.getFluid(0).isEmpty() || FluidStack.isSameFluidSameComponents(fluidStorage.getFluid(0), recipe.value().getOutput()));
    }
}