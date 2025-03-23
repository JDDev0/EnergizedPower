package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EnergizerRecipe;
import me.jddev0.ep.screen.EnergizerMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnergizerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage, ItemStackHandler> {
    public static final float ENERGY_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_ENERGIZER_ENERGY_CONSUMPTION_MULTIPLIER.getValue();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    private int progress;
    private int maxProgress = ModConfigs.COMMON_ENERGIZER_RECIPE_DURATION.getValue();
    private int energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    public EnergizerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ENERGIZER_ENTITY.get(), blockPos, blockState,

                "energizer",

                ModConfigs.COMMON_ENERGIZER_CAPACITY.getValue(),
                ModConfigs.COMMON_ENERGIZER_TRANSFER_RATE.getValue(),

                2,

                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected ReceiveOnlyEnergyStorage initEnergyStorage() {
        return new ReceiveOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            public int getMaxReceive() {
                return Math.max(1, (int)Math.ceil(maxReceive * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }

            @Override
            protected void onChange() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
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
                return switch (slot) {
                    case 0 -> level == null || RecipeUtils.isIngredientOfAny(level, EnergizerRecipe.Type.INSTANCE, stack);
                    case 1 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress(worldPosition, level.getBlockState(worldPosition));
                }

                super.setStackInSlot(slot, stack);
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new EnergyValueContainerData(() -> hasRecipe(this)?getEnergyConsumptionPerTick():-1, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft, value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new EnergizerMenu(id, inventory, this, upgradeModuleInventory, this.data);
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
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.put("recipe.progress", IntTag.valueOf(progress));
        nbt.put("recipe.energy_consumption_left", IntTag.valueOf(energyConsumptionLeft));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getInt("recipe.energy_consumption_left");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, EnergizerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(BlockStateProperties.POWERED)))
            return;

        if(hasRecipe(blockEntity)) {
            SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
            for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
                inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

            Optional<RecipeHolder<EnergizerRecipe>> recipe = level.getRecipeManager().
                    getRecipeFor(EnergizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);
            if(recipe.isEmpty())
                return;

            int energyConsumption = recipe.get().value().getEnergyConsumption();
            energyConsumption = (int)(energyConsumption * ENERGY_CONSUMPTION_MULTIPLIER);
            if(blockEntity.progress == 0)
                blockEntity.energyConsumptionLeft = energyConsumption;

            int energyConsumptionPerTick = blockEntity.getEnergyConsumptionPerTick();
            if(energyConsumptionPerTick <= blockEntity.energyStorage.getEnergy()) {
                blockEntity.hasEnoughEnergy = true;
                if(level.getBlockState(blockPos).hasProperty(BlockStateProperties.LIT) &&
                        !level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                    level.setBlock(blockPos, state.setValue(BlockStateProperties.LIT, true), 3);
                }

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0 ||
                        energyConsumptionPerTick < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    setChanged(level, blockPos, state);

                    return;
                }

                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick);
                blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    craftItem(blockPos, state, blockEntity);

                setChanged(level, blockPos, state);
            }else {
                blockEntity.hasEnoughEnergy = false;
                if(level.getBlockState(blockPos).hasProperty(BlockStateProperties.LIT) &&
                        level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                    level.setBlock(blockPos, state.setValue(BlockStateProperties.LIT, false), 3);
                }
                setChanged(level, blockPos, state);
            }
        }else {
            blockEntity.resetProgress(blockPos, state);
            if(level.getBlockState(blockPos).hasProperty(BlockStateProperties.LIT) &&
                    level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                level.setBlock(blockPos, state.setValue(BlockStateProperties.LIT, false), 3);
            }
            setChanged(level, blockPos, state);
        }
    }
    
    protected final int getEnergyConsumptionPerTick() {
        if(level == null)
            return -1;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<EnergizerRecipe>> recipe = level.getRecipeManager().
                getRecipeFor(EnergizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);
        if(recipe.isEmpty())
            return -1;

        int energyConsumption = recipe.get().value().getEnergyConsumption();
        energyConsumption = (int)(energyConsumption * ENERGY_CONSUMPTION_MULTIPLIER);

        //TODO improve (alternate values +/- 1 per x recipes instead of changing last energy consumption tick)
        int energyConsumptionPerTick = (int)Math.ceil((float)energyConsumption / maxProgress);
        if(progress == maxProgress - 1)
            energyConsumptionPerTick = energyConsumptionLeft;

        return energyConsumptionPerTick;
    }

    private void resetProgress(BlockPos blockPos, BlockState state) {
        progress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = false;
    }

    private static void craftItem(BlockPos blockPos, BlockState state, EnergizerBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<EnergizerRecipe>> recipe = level.getRecipeManager().
                getRecipeFor(EnergizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.extractItem(0, 1, false);
        blockEntity.itemHandler.setStackInSlot(1, recipe.get().value().getResultItem(level.registryAccess()).copyWithCount(
                blockEntity.itemHandler.getStackInSlot(1).getCount() + recipe.get().value().getResultItem(level.registryAccess()).getCount()));

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(EnergizerBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<EnergizerRecipe>> recipe = level.getRecipeManager().
                getRecipeFor(EnergizerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.get().value().getResultItem(level.registryAccess()));
    }

    @Override
    protected void updateUpgradeModules() {
        resetProgress(getBlockPos(), getBlockState());

        super.updateUpgradeModules();
    }
}