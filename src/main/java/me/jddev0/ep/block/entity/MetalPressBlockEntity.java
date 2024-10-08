package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.SimpleRecipeMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.MetalPressRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import me.jddev0.ep.screen.MetalPressMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.math.BlockPos;

public class MetalPressBlockEntity extends SimpleRecipeMachineBlockEntity<MetalPressRecipe> {
    final InputOutputItemHandler itemHandlerTopSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 1, i -> i == 1) {
        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
    };
    final InputOutputItemHandler itemHandlerOthersSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 2);

    public MetalPressBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.METAL_PRESS_ENTITY, blockPos, blockState,

                "metal_press", MetalPressMenu::new,

                3, EPRecipes.METAL_PRESS_TYPE, ModConfigs.COMMON_METAL_PRESS_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_METAL_PRESS_CAPACITY.getValue(),
                ModConfigs.COMMON_METAL_PRESS_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_METAL_PRESS_ENERGY_CONSUMPTION_PER_TICK.getValue(),

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
                    case 0 -> world == null || world.getRecipeManager().listAllOfType(MetalPressRecipe.Type.INSTANCE).stream().
                            map(RecipeEntry::value).map(MetalPressRecipe::getInput).anyMatch(ingredient -> ingredient.test(stack));
                    case 1 -> world == null || stack.isIn(EnergizedPowerItemTags.METAL_PRESS_MOLDS) &&
                            (getStack(1).isEmpty() || stack.getCount() == 1);
                    case 2 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0 || slot == 1) {
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

                MetalPressBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected void craftItem(RecipeEntry<MetalPressRecipe> recipe) {
        if(world == null || !hasRecipe())
            return;

         ItemStack pressMold = itemHandler.getStack(1).copy();
        if(pressMold.isEmpty() && !pressMold.isIn(EnergizedPowerItemTags.METAL_PRESS_MOLDS))
            return;


        if(pressMold.damage(1, world.random, null))
            itemHandler.setStack(1, ItemStack.EMPTY);
        else
            itemHandler.setStack(1, pressMold);

        itemHandler.removeStack(0, recipe.value().getInputCount());
        itemHandler.setStack(2, recipe.value().getResult(world.getRegistryManager()).
                copyWithCount(itemHandler.getStack(2).getCount() +
                        recipe.value().getResult(world.getRegistryManager()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<MetalPressRecipe> recipe) {
        return world != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.value().getResult(world.getRegistryManager()));
    }
}