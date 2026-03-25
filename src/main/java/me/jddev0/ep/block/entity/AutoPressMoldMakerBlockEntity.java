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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public class AutoPressMoldMakerBlockEntity
        extends SelectableRecipeMachineBlockEntity<RecipeInput, PressMoldMakerRecipe> {
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
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> stack.is(Items.CLAY_BALL);
                    case 1 -> stack.is(ItemTags.SHOVELS);
                    case 2 -> false;
                    default -> super.canPlaceItem(slot, stack);
                };
            }

            @Override
            public void setChanged() {
                super.setChanged();

                AutoPressMoldMakerBlockEntity.this.setChanged();
            }
        };
    }

    @Override
    protected void craftItem(RecipeHolder<PressMoldMakerRecipe> recipe) {
        if(level == null || !hasRecipe() || !(level instanceof ServerLevel serverWorld))
            return;

        ItemStack shovel = itemHandler.getItem(1).copy();
        if(shovel.isEmpty() && !shovel.is(ItemTags.SHOVELS))
            return;

        shovel.hurtAndBreak(1, serverWorld, null, item -> shovel.setCount(0));
        itemHandler.setItem(1, shovel);

        itemHandler.removeItem(0, recipe.value().getClayCount());
        itemHandler.setItem(2, recipe.value().assemble(null, level.registryAccess()).
                copyWithCount(itemHandler.getItem(2).getCount() +
                        recipe.value().assemble(null, level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<PressMoldMakerRecipe> recipe) {
        return level != null &&
                itemHandler.getItem(0).is(Items.CLAY_BALL) &&
                itemHandler.getItem(0).getCount() >= recipe.value().getClayCount() &&
                itemHandler.getItem(1).is(ItemTags.SHOVELS) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 2, recipe.value().assemble(null, level.registryAccess()));
    }
}