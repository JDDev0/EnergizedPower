package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SelectableRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.AutoPressMoldMakerMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoPressMoldMakerBlockEntity
        extends SelectableRecipeMachineBlockEntity<PressMoldMakerRecipe> {
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2));

    public AutoPressMoldMakerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.AUTO_PRESS_MOLD_MAKER_ENTITY.get(), blockPos, blockState,

                "auto_press_mold_maker", AutoPressMoldMakerMenu::new,

                3,
                ModRecipes.PRESS_MOLD_MAKER_TYPE.get(),
                ModRecipes.PRESS_MOLD_MAKER_SERIALIZER.get(),
                ModConfigs.COMMON_AUTO_PRESS_MOLD_MAKER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_AUTO_PRESS_MOLD_MAKER_CAPACITY.getValue(),
                ModConfigs.COMMON_AUTO_PRESS_MOLD_MAKER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_AUTO_PRESS_MOLD_MAKER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0 -> stack.is(Items.CLAY_BALL);
                    case 1 -> stack.is(Tags.Items.TOOLS_SHOVELS);
                    case 2 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }
        };
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
    protected void craftItem(PressMoldMakerRecipe recipe) {
        if(level == null || !hasRecipe())
            return;

        ItemStack shovel = itemHandler.getStackInSlot(1).copy();
        if(shovel.isEmpty() && !shovel.is(Tags.Items.TOOLS_SHOVELS))
            return;

        if(shovel.hurt(1, level.random, null))
            itemHandler.setStackInSlot(1, ItemStack.EMPTY);
        else
            itemHandler.setStackInSlot(1, shovel);

        itemHandler.extractItem(0, recipe.getClayCount(), false);
        itemHandler.setStackInSlot(2, ItemStackUtils.copyWithCount(recipe.getResultItem(),
                itemHandler.getStackInSlot(2).getCount() +
                        recipe.getResultItem().getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, PressMoldMakerRecipe recipe) {
        return level != null &&
                itemHandler.getStackInSlot(0).is(Items.CLAY_BALL) &&
                itemHandler.getStackInSlot(0).getCount() >= recipe.getClayCount() &&
                itemHandler.getStackInSlot(1).is(Tags.Items.TOOLS_SHOVELS) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.getResultItem());
    }
}