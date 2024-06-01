package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.recipe.SawmillRecipe;
import me.jddev0.ep.screen.SawmillMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SawmillBlockEntity extends SimpleRecipeMachineBlockEntity<SawmillRecipe> {
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1 || i == 2));

    public SawmillBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.SAWMILL_ENTITY.get(), blockPos, blockState,

                "sawmill", SawmillMenu::new,

                3, ModRecipes.SAWMILL_TYPE.get(), ModConfigs.COMMON_SAWMILL_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_SAWMILL_CAPACITY.getValue(),
                ModConfigs.COMMON_SAWMILL_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_SAWMILL_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
       );
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void craftItem(SawmillRecipe recipe) {
        if(level == null || !hasRecipe())
            return;

        itemHandler.extractItem(0, 1, false);
        itemHandler.setStackInSlot(1, ItemStackUtils.copyWithCount(recipe.getResultItem(),
                itemHandler.getStackInSlot(1).getCount() +
                        recipe.getResultItem().getCount()));

        if(!recipe.getSecondaryOutput().isEmpty())
            itemHandler.setStackInSlot(2, ItemStackUtils.copyWithCount(recipe.getSecondaryOutput(),
                    itemHandler.getStackInSlot(2).getCount() +
                            recipe.getSecondaryOutput().getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, SawmillRecipe recipe) {
        return level != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.getResultItem()) &&
                (recipe.getSecondaryOutput().isEmpty() ||
                        InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.getSecondaryOutput()));
    }
}