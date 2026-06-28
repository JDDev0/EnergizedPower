package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SelectableRecipeFluidMachineBlockEntity;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.screen.StoneSolidifierMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class StoneSolidifierBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<RecipeInput, StoneSolidifierRecipe> {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(1000 *
            ModConfigs.COMMON_STONE_SOLIDIFIER_TANK_CAPACITY.getValue());

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> false, i -> i == 0);

    public StoneSolidifierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.STONE_SOLIDIFIER_ENTITY, blockPos, blockState,

                "stone_solidifier", StoneSolidifierMenu::new,

                1,
                EPRecipes.STONE_SOLIDIFIER_TYPE,
                EPRecipes.STONE_SOLIDIFIER_SERIALIZER,
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
            public boolean isValid(int slot, @NotNull ItemVariant resource) {
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
            public boolean isValid(int tank, @NotNull FluidVariant resource) {
                if(!super.isValid(tank, resource))
                    return false;

                return switch(tank) {
                    case 0 -> resource.isOf(Fluids.WATER);
                    case 1 -> resource.isOf(Fluids.LAVA);
                    default -> false;
                };
            }
        };
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable Storage<FluidVariant> getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void craftItem(RecipeHolder<StoneSolidifierRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.extract(FluidVariant.of(Fluids.WATER), FluidUtils.convertMilliBucketsToDroplets(
                    recipe.value().getWaterAmount()), transaction);
            fluidStorage.extract(FluidVariant.of(Fluids.LAVA), FluidUtils.convertMilliBucketsToDroplets(
                    recipe.value().getLavaAmount()), transaction);

            transaction.commit();
        }

        itemHandler.setStackInSlot(0, recipe.value().getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(0).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<StoneSolidifierRecipe> recipe) {
        return level != null &&
                fluidStorage.getAmount(0) >= FluidUtils.convertMilliBucketsToDroplets(recipe.value().getWaterAmount()) &&
                fluidStorage.getAmount(1) >= FluidUtils.convertMilliBucketsToDroplets(recipe.value().getLavaAmount()) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 0, recipe.value().getResultItem(level.registryAccess()));
    }
}