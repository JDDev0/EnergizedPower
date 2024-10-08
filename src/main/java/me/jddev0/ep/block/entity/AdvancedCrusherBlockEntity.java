package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMultiTankMethods;
import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.CrusherRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.AdvancedCrusherMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import me.jddev0.ep.util.*;

import java.util.List;

public class AdvancedCrusherBlockEntity
        extends SimpleRecipeFluidMachineBlockEntity<CombinedStorage<FluidVariant, SimpleFluidStorage>, CrusherRecipe> {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(
            1000 * ModConfigs.COMMON_ADVANCED_CRUSHER_TANK_CAPACITY.getValue());
    public static final long WATER_CONSUMPTION_PER_RECIPE = FluidUtils.convertMilliBucketsToDroplets(
            ModConfigs.COMMON_ADVANCED_CRUSHER_WATER_USAGE_PER_RECIPE.getValue());

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public AdvancedCrusherBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_CRUSHER_ENTITY, blockPos, blockState,

                "advanced_crusher", AdvancedCrusherMenu::new,

                2, EPRecipes.CRUSHER_TYPE, ModConfigs.COMMON_ADVANCED_CRUSHER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_ADVANCED_CRUSHER_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_CRUSHER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_ADVANCED_CRUSHER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                FluidStorageMultiTankMethods.INSTANCE,
                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> world == null || RecipeUtils.isIngredientOfAny(world, CrusherRecipe.Type.INSTANCE, stack);
                    case 1 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.canCombine(stack, itemStack))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                AdvancedCrusherBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected CombinedStorage<FluidVariant, SimpleFluidStorage> initFluidStorage() {
        return new CombinedStorage<>(List.of(
                new SimpleFluidStorage(baseTankCapacity) {
                    @Override
                    protected void onFinalCommit() {
                        markDirty();
                        syncFluidToPlayers(32);
                    }

                    private boolean isFluidValid(FluidVariant variant) {
                        return variant.isOf(Fluids.WATER);
                    }

                    @Override
                    protected boolean canInsert(FluidVariant variant) {
                        return isFluidValid(variant);
                    }

                    @Override
                    protected boolean canExtract(FluidVariant variant) {
                        return isFluidValid(variant);
                    }
                },
                new SimpleFluidStorage(baseTankCapacity) {
                    @Override
                    protected void onFinalCommit() {
                        markDirty();
                        syncFluidToPlayers(32);
                    }

                    private boolean isFluidValid(FluidVariant variant) {
                        return variant.isOf(EPFluids.DIRTY_WATER);
                    }

                    @Override
                    protected boolean canInsert(FluidVariant variant) {
                        return isFluidValid(variant);
                    }

                    @Override
                    protected boolean canExtract(FluidVariant variant) {
                        return isFluidValid(variant);
                    }
                }
        ));
    }

    @Override
    protected void craftItem(CrusherRecipe recipe) {
        if(world == null || !hasRecipe())
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.extract(FluidVariant.of(Fluids.WATER), WATER_CONSUMPTION_PER_RECIPE, transaction);
            fluidStorage.insert(FluidVariant.of(EPFluids.DIRTY_WATER), WATER_CONSUMPTION_PER_RECIPE, transaction);

            transaction.commit();
        }

        itemHandler.removeStack(0, 1);
        itemHandler.setStack(1, recipe.getOutput(world.getRegistryManager()).
                copyWithCount(itemHandler.getStack(1).getCount() +
                        recipe.getOutput(world.getRegistryManager()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, CrusherRecipe recipe) {
        return world != null &&
                fluidStorage.parts.get(0).getAmount() >= WATER_CONSUMPTION_PER_RECIPE &&
                fluidStorage.parts.get(1).getCapacity() - fluidStorage.parts.get(1).getAmount() >= WATER_CONSUMPTION_PER_RECIPE &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.getOutput(world.getRegistryManager()));
    }
}