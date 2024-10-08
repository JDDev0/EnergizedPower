package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SelectableRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.AutoPressMoldMakerMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPos;

public class AutoPressMoldMakerBlockEntity
        extends SelectableRecipeMachineBlockEntity<PressMoldMakerRecipe> {
    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2);

    public AutoPressMoldMakerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.AUTO_PRESS_MOLD_MAKER_ENTITY, blockPos, blockState,

                "auto_press_mold_maker", AutoPressMoldMakerMenu::new,

                3,
                EPRecipes.PRESS_MOLD_MAKER_TYPE,
                EPRecipes.PRESS_MOLD_MAKER_SERIALIZER,
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
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> stack.isOf(Items.CLAY_BALL);
                    case 1 -> stack.isIn(ItemTags.SHOVELS);
                    case 2 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void markDirty() {
                super.markDirty();
            }
        };
    }

    @Override
    protected void craftItem(RecipeEntry<PressMoldMakerRecipe> recipe) {
        if(world == null || !hasRecipe())
            return;

        ItemStack shovel = itemHandler.getStack(1).copy();
        if(shovel.isEmpty() && !shovel.isIn(ItemTags.SHOVELS))
            return;

        if(shovel.damage(1, world.random, null))
            itemHandler.setStack(1, ItemStack.EMPTY);
        else
            itemHandler.setStack(1, shovel);

        itemHandler.removeStack(0, recipe.value().getClayCount());
        itemHandler.setStack(2, recipe.value().getResult(world.getRegistryManager()).
                copyWithCount(itemHandler.getStack(2).getCount() +
                        recipe.value().getResult(world.getRegistryManager()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<PressMoldMakerRecipe> recipe) {
        return world != null &&
                itemHandler.getStack(0).isOf(Items.CLAY_BALL) &&
                itemHandler.getStack(0).getCount() >= recipe.value().getClayCount() &&
                itemHandler.getStack(1).isIn(ItemTags.SHOVELS) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.value().getResult(world.getRegistryManager()));
    }
}