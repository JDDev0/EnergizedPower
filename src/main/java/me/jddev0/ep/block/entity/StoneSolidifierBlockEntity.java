package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageMultiTankMethods;
import me.jddev0.ep.block.entity.base.SelectableRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.screen.StoneSolidifierMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class StoneSolidifierBlockEntity
        extends SelectableRecipeFluidMachineBlockEntity<CombinedStorage<FluidVariant, SimpleFluidStorage>, StoneSolidifierRecipe> {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(1000 *
            ModConfigs.COMMON_STONE_SOLIDIFIER_TANK_CAPACITY.getValue());

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> false, i -> i == 0);

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
                if(slot == 0)
                    return false;

                return super.isValid(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                StoneSolidifierBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected CombinedStorage<FluidVariant, SimpleFluidStorage> initFluidStorage() {
        return  new CombinedStorage<>(List.of(
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
                        return variant.isOf(Fluids.LAVA);
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
    protected void craftItem(StoneSolidifierRecipe recipe) {
        if(world == null || !hasRecipe())
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.extract(FluidVariant.of(Fluids.WATER), FluidUtils.convertMilliBucketsToDroplets(
                    recipe.getWaterAmount()), transaction);
            fluidStorage.extract(FluidVariant.of(Fluids.LAVA), FluidUtils.convertMilliBucketsToDroplets(
                    recipe.getLavaAmount()), transaction);

            transaction.commit();
        }

        itemHandler.setStack(0, ItemStackUtils.copyWithCount(recipe.getOutput(),
                itemHandler.getStack(0).getCount() +
                        recipe.getOutput().getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, StoneSolidifierRecipe recipe) {
        return world != null &&
                fluidStorage.parts.get(0).getAmount() >= FluidUtils.convertMilliBucketsToDroplets(recipe.getWaterAmount()) &&
                fluidStorage.parts.get(1).getAmount() >= FluidUtils.convertMilliBucketsToDroplets(recipe.getLavaAmount()) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 0, recipe.getOutput());
    }
}