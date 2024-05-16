package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class SimpleRecipeMachineBlockEntity<R extends Recipe<Inventory>>
        extends WorkerMachineBlockEntity<RecipeEntry<R>>
        implements ExtendedScreenHandlerFactory {
    protected final String machineName;
    protected final UpgradableMenuProvider menuProvider;

    protected final RecipeType<R> recipeType;

    protected final PropertyDelegate data;

    public SimpleRecipeMachineBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                          String machineName, UpgradableMenuProvider menuProvider,
                                          int slotCount, RecipeType<R> recipeType, int baseRecipeDuration,
                                          long baseEnergyCapacity, long baseEnergyTransferRate, long baseEnergyConsumptionPerTick,
                                          UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, slotCount, baseRecipeDuration, baseEnergyCapacity, baseEnergyTransferRate,
                baseEnergyConsumptionPerTick, upgradeModifierSlots);

        this.machineName = machineName;
        this.menuProvider = menuProvider;

        this.recipeType = recipeType;

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(energyConsumptionLeft, index - 4);
                    case 8 -> hasEnoughEnergy?1:0;
                    case 9 -> redstoneMode.ordinal();
                    case 10 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> progress = ByteUtils.with2Bytes(
                            progress, (short)value, index
                    );
                    case 2, 3 -> maxProgress = ByteUtils.with2Bytes(
                            maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6, 7, 8 -> {}
                    case 9 -> redstoneMode = RedstoneMode.fromIndex(value);
                    case 10 -> comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 11;
            }
        };
    }

    @Override
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return slot == 0 && (world == null || RecipeUtils.isIngredientOfAny(world, recipeType, stack));
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.canCombine(stack, itemStack))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                SimpleRecipeMachineBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower." + machineName);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);

        return menuProvider.createMenu(id, this, inventory, itemHandler, upgradeModuleInventory, data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected final Optional<RecipeEntry<R>> getCurrentWorkData() {
        return world.getRecipeManager().getFirstMatch(recipeType, itemHandler, world);
    }

    @Override
    protected final double getWorkDataDependentWorkDuration(RecipeEntry<R> workData) {
        return getRecipeDependentRecipeDuration(workData);
    }

    protected double getRecipeDependentRecipeDuration(RecipeEntry<R> recipe) {
        return 1;
    }

    @Override
    protected final double getWorkDataDependentEnergyConsumption(RecipeEntry<R> workData) {
        return getRecipeDependentEnergyConsumption(workData);
    }

    protected double getRecipeDependentEnergyConsumption(RecipeEntry<R> recipe) {
        return 1;
    }

    @Override
    protected final boolean hasWork() {
        return hasRecipe();
    }

    protected boolean hasRecipe() {
        if(world == null)
            return false;

        Optional<RecipeEntry<R>> recipe = world.getRecipeManager().getFirstMatch(recipeType, itemHandler, world);

        return recipe.isPresent() && canCraftRecipe(itemHandler, recipe.get());
    }

    @Override
    protected final void onWorkStarted(RecipeEntry<R> workData) {
        onStartCrafting(workData);
    }

    protected void onStartCrafting(RecipeEntry<R> recipe) {}

    @Override
    protected final void onWorkCompleted(RecipeEntry<R> workData) {
        craftItem(workData);
    }

    protected void craftItem(RecipeEntry<R> recipe) {
        if(world == null || !hasRecipe())
            return;

        itemHandler.removeStack(0, 1);
        itemHandler.setStack(1, recipe.value().getResult(world.getRegistryManager()).
                copyWithCount(itemHandler.getStack(1).getCount() +
                        recipe.value().getResult(world.getRegistryManager()).getCount()));

        resetProgress();
    }

    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<R> recipe) {
        return world != null &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().getResult(world.getRegistryManager()));
    }
}