package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SelectableRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.AutoPressMoldMakerMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoPressMoldMakerBlockEntity
        extends SelectableRecipeMachineBlockEntity<RecipeInput, PressMoldMakerRecipe> {
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2);

    public AutoPressMoldMakerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.AUTO_PRESS_MOLD_MAKER_ENTITY.get(), blockPos, blockState,

                "auto_press_mold_maker", AutoPressMoldMakerMenu::new,

                3,
                EPRecipes.PRESS_MOLD_MAKER_TYPE.get(),
                EPRecipes.PRESS_MOLD_MAKER_SERIALIZER.get(),
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
                    case 1 -> stack.is(ItemTags.SHOVELS);
                    case 2 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }
        };
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void craftItem(RecipeHolder<PressMoldMakerRecipe> recipe) {
        if(level == null || !hasRecipe() || !(level instanceof ServerLevel serverLevel))
            return;

        ItemStack shovel = itemHandler.getStackInSlot(1).copy();
        if(shovel.isEmpty() && !shovel.is(ItemTags.SHOVELS))
            return;

        shovel.hurtAndBreak(1, serverLevel, null, item -> shovel.setCount(0));
        itemHandler.setStackInSlot(1, shovel);

        itemHandler.extractItem(0, recipe.value().getClayCount(), false);
        itemHandler.setStackInSlot(2, recipe.value().assemble(null, level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(2).getCount() +
                        recipe.value().assemble(null, level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<PressMoldMakerRecipe> recipe) {
        return level != null &&
                itemHandler.getStackInSlot(0).is(Items.CLAY_BALL) &&
                itemHandler.getStackInSlot(0).getCount() >= recipe.value().getClayCount() &&
                itemHandler.getStackInSlot(1).is(ItemTags.SHOVELS) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.value().assemble(null, level.registryAccess()));
    }
}