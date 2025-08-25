package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.WorkerMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncFurnaceRecipeTypeS2CPacket;
import me.jddev0.ep.recipe.FurnaceRecipeTypePacketUpdate;
import me.jddev0.ep.screen.PoweredFurnaceMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PoweredFurnaceBlockEntity
        extends WorkerMachineBlockEntity<AbstractCookingRecipe>
        implements FurnaceRecipeTypePacketUpdate {
    private static final List<@NotNull Identifier> RECIPE_BLACKLIST = ModConfigs.COMMON_POWERED_FURNACE_RECIPE_BLACKLIST.getValue();

    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    private @NotNull RecipeType<? extends AbstractCookingRecipe> recipeType = RecipeType.SMELTING;

    public PoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.POWERED_FURNACE_ENTITY, blockPos, blockState,

                "powered_furnace",

                2, 1,

                ModConfigs.COMMON_POWERED_FURNACE_CAPACITY.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE
        );
    }

    @Override
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> world == null || RecipeUtils.isIngredientOfAny(world, getRecipeForFurnaceModeUpgrade(), stack);
                    case 1 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.canCombine(stack, itemStack))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                PoweredFurnaceBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new EnergyValueContainerData(() -> hasWork()?getCurrentWorkData().map(this::getEnergyConsumptionFor).orElse(-1L):-1, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft, value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player,
                new SyncFurnaceRecipeTypeS2CPacket(getRecipeForFurnaceModeUpgrade(), getPos()));

        return new PoweredFurnaceMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    protected void onHasEnoughEnergy() {
        if(world.getBlockState(getPos()).contains(Properties.LIT) &&
                !world.getBlockState(getPos()).get(Properties.LIT)) {
            world.setBlockState(getPos(), getCachedState().with(Properties.LIT, true), 3);
        }
    }

    @Override
    protected void onHasNotEnoughEnergyWithOffTimeout() {
        if(world.getBlockState(getPos()).contains(Properties.LIT) &&
                world.getBlockState(getPos()).get(Properties.LIT)) {
            world.setBlockState(getPos(), getCachedState().with(Properties.LIT, false), 3);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    protected Optional<AbstractCookingRecipe> getCurrentWorkData() {
        return (Optional<AbstractCookingRecipe>)getRecipeFor(itemHandler, world);
    }

    @Override
    protected boolean hasWork() {
        return hasRecipe(this);
    }

    @Override
    protected void onWorkStarted(AbstractCookingRecipe recipe) {}

    @Override
    protected void onWorkCompleted(AbstractCookingRecipe workData) {
        craftItem(getPos(), getCachedState(), this);
    }

    @Override
    protected double getWorkDataDependentWorkDuration(AbstractCookingRecipe recipe) {
        //Default Cooking Time = 200 -> maxProgress = 100 (= 200 / 2)
        return recipe.getCookTime() * RECIPE_DURATION_MULTIPLIER / 2.f;
    }

    private static void craftItem(BlockPos blockPos, BlockState state, PoweredFurnaceBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<? extends AbstractCookingRecipe> recipe = blockEntity.getRecipeFor(blockEntity.itemHandler, level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.removeStack(0, 1);
        blockEntity.itemHandler.setStack(1, recipe.get().getOutput(level.getRegistryManager()).copyWithCount(
                blockEntity.itemHandler.getStack(1).getCount() + recipe.get().getOutput(level.getRegistryManager()).getCount()));

        blockEntity.resetProgress();
    }

    private static boolean hasRecipe(PoweredFurnaceBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<? extends AbstractCookingRecipe> recipe = blockEntity.getRecipeFor(blockEntity.itemHandler, level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(blockEntity.itemHandler, 1, recipe.get().getOutput(level.getRegistryManager()));
    }

    private Optional<? extends AbstractCookingRecipe> getRecipeFor(Inventory container, World level) {
        return level.getRecipeManager().listAllOfType(getRecipeForFurnaceModeUpgrade()).
                stream().filter(recipe -> !RECIPE_BLACKLIST.contains(recipe.getId())).
                filter(recipe -> recipe.matches(container, level)).
                findFirst();
    }

    public RecipeType<? extends AbstractCookingRecipe> getRecipeForFurnaceModeUpgrade() {
        if(world != null && world.isClient())
            return recipeType;

        double value = upgradeModuleInventory.getUpgradeModuleModifierEffect(3, UpgradeModuleModifier.FURNACE_MODE);
        if(value == 1)
            return RecipeType.BLASTING;
        else if(value == 2)
            return RecipeType.SMOKING;

        return RecipeType.SMELTING;
    }

    @Override
    public void setRecipeType(@NotNull RecipeType<? extends AbstractCookingRecipe> recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    protected void updateUpgradeModules() {
        super.updateUpgradeModules();

        if(world != null && !world.isClient()) {
            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getPos(), (ServerWorld)world, 32,
                    new SyncFurnaceRecipeTypeS2CPacket(getRecipeForFurnaceModeUpgrade(), getPos())
            );
        }
    }
}