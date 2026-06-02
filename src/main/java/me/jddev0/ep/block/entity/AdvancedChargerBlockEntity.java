package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AdvancedChargerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.ComparatorModeValueContainerData;
import me.jddev0.ep.inventory.data.EnergyValueContainerData;
import me.jddev0.ep.inventory.data.RedstoneModeValueContainerData;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.screen.AdvancedChargerMenu;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.Optional;

public class AdvancedChargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleContainer> {
    public static final float CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_ADVANCED_CHARGER_CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i < 0 || i > 2)
            return false;

        ItemStack itemStack = itemHandler.getItem(i);
        if(level != null && RecipeUtils.isResultOfAny(level, ChargerRecipe.Type.INSTANCE, itemStack))
            return true;

        if(level == null || RecipeUtils.isIngredientOfAny(level, ChargerRecipe.Type.INSTANCE, itemStack))
            return false;

        if(!EnergyStorageUtil.isEnergyStorage(itemStack))
            return true;

        EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(itemStack, ContainerItemContext.
                ofSingleSlot(InventoryStorage.of(itemHandler, null).getSlots().get(i)));
        if(limitingEnergyStorage == null)
            return true;

        if(!limitingEnergyStorage.supportsInsertion())
            return true;

        return limitingEnergyStorage.getAmount() == limitingEnergyStorage.getCapacity();
    });

    private long[] energyConsumptionLeft = new long[] {
            -1, -1, -1
    };

    public AdvancedChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_CHARGER_ENTITY, blockPos, blockState,

                "advanced_charger",

                ModConfigs.COMMON_ADVANCED_CHARGER_CAPACITY_PER_SLOT.getValue() * 3,
                ModConfigs.COMMON_ADVANCED_CHARGER_TRANSFER_RATE_PER_SLOT.getValue() * 3,

                3,

                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity, baseEnergyCapacity, baseEnergyCapacity) {
            @Override
            public long getCapacity() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, 0) {
            @Override
            public long getMaxInsert() {
                return Math.max(1, (long)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };
    }

    @Override
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                if(level == null || RecipeUtils.isIngredientOfAny(level, ChargerRecipe.Type.INSTANCE, stack))
                    return true;

                if(slot >= 0 && slot < 3) {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(limitingEnergyStorage == null)
                        return false;

                    return limitingEnergyStorage.supportsInsertion();
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    ItemStack itemStack = getItem(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.isSameItem(stack, itemStack) ||
                            (!ItemStack.isSameItemSameComponents(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(EnergyStorageUtil.isEnergyStorage(stack) && EnergyStorageUtil.isEnergyStorage(itemStack)))))
                        resetProgress(slot);
                }

                super.setItem(slot, stack);
            }

            @Override
            public void setChanged() {
                super.setChanged();

                AdvancedChargerBlockEntity.this.setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new EnergyValueContainerData(this::getEnergyConsumptionPerTickSum, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[0], value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[1], value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[2], value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new AdvancedChargerMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(nbt, registries);

        for(int i = 0;i < 3;i++)
            nbt.put("recipe.energy_consumption_left." + i, LongTag.valueOf(energyConsumptionLeft[i]));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(nbt, registries);

        for(int i = 0;i < 3;i++)
            energyConsumptionLeft[i] = nbt.getLong("recipe.energy_consumption_left." + i);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AdvancedChargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(AdvancedChargerBlock.POWERED)))
            return;

        blockEntity.pullItemsFromInputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_PULLING));

        tickRecipe(level, blockPos, state, blockEntity);

        blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, AdvancedChargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        final long maxReceivePerSlot = (long)Math.min(blockEntity.limitingEnergyStorage.getMaxInsert() / 3.,
                Math.ceil(blockEntity.energyStorage.getAmount() / 3.));

        for(int i = 0;i < 3;i++) {
            if(blockEntity.hasRecipe(i)) {
                ItemStack stack = blockEntity.itemHandler.getItem(i);
                long energyConsumptionPerTick;

                SimpleContainer inventory = new SimpleContainer(1);
                inventory.setItem(0, blockEntity.itemHandler.getItem(i));

                Optional<RecipeHolder<ChargerRecipe>> recipe = level.getRecipeManager().
                        getRecipeFor(ChargerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);
                if(recipe.isPresent()) {
                    if(blockEntity.energyConsumptionLeft[i] == -1)
                        blockEntity.energyConsumptionLeft[i] = (long)(recipe.get().value().getEnergyConsumption() * CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER);

                    if(blockEntity.energyStorage.getAmount() == 0) {
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    energyConsumptionPerTick = Math.min(blockEntity.energyConsumptionLeft[i], Math.min(maxReceivePerSlot,
                            blockEntity.energyStorage.getAmount()));
                }else {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        continue;

                    EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                            ofSingleSlot(InventoryStorage.of(blockEntity.itemHandler, null).getSlots().get(i)));
                    if(limitingEnergyStorage == null)
                        continue;

                    if(!limitingEnergyStorage.supportsInsertion())
                        continue;

                    blockEntity.energyConsumptionLeft[i] = Math.max(0, limitingEnergyStorage.getCapacity() - limitingEnergyStorage.getAmount());

                    if(blockEntity.energyStorage.getAmount() == 0) {
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    try(Transaction transaction = Transaction.openOuter()) {
                        energyConsumptionPerTick = limitingEnergyStorage.insert(Math.min(maxReceivePerSlot,
                                blockEntity.energyStorage.getAmount()), transaction);
                        transaction.commit();
                    }
                }

                if(blockEntity.energyConsumptionLeft[i] < 0 || energyConsumptionPerTick < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(i);
                    setChanged(level, blockPos, state);

                    continue;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    energyConsumptionPerTick = blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);
                    transaction.commit();
                }

                blockEntity.energyConsumptionLeft[i] -= energyConsumptionPerTick;

                if(blockEntity.energyConsumptionLeft[i] <= 0) {
                    final int index = i;
                    recipe.ifPresent(advancedChargerRecipe ->
                            blockEntity.itemHandler.setItem(index, advancedChargerRecipe.value().getResultItem(level.registryAccess()).copyWithCount(1)));

                    blockEntity.resetProgress(i);
                }
                setChanged(level, blockPos, state);
            }else {
                blockEntity.resetProgress(i);
                setChanged(level, blockPos, state);
            }
        }
    }

    protected final long getEnergyConsumptionPerTickSum() {
        if(level == null)
            return -1;

        final long maxReceivePerSlot = (long)Math.min(this.limitingEnergyStorage.getMaxInsert() / 3.,
                Math.ceil(this.energyStorage.getAmount() / 3.));

        long energyConsumptionSum = -1;

        for(int i = 0;i < 3;i++) {
            ItemStack stack = itemHandler.getItem(i);
            long energyConsumption;

            SimpleContainer inventory = new SimpleContainer(1);
            inventory.setItem(0, this.itemHandler.getItem(i));

            Optional<RecipeHolder<ChargerRecipe>> recipe = level.getRecipeManager().
                    getRecipeFor(ChargerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);

            if(recipe.isPresent()) {
                energyConsumption = Math.min(energyConsumptionLeft[i], Math.min(maxReceivePerSlot, energyStorage.getAmount()));
            }else {
                if(!EnergyStorageUtil.isEnergyStorage(stack))
                    continue;

                EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                        ofSingleSlot(InventoryStorage.of(this.itemHandler, null).getSlots().get(i)));
                if(limitingEnergyStorage == null)
                    continue;

                if(!limitingEnergyStorage.supportsInsertion())
                    continue;

                try(Transaction transaction = Transaction.openOuter()) {
                    energyConsumption = limitingEnergyStorage.insert(Math.min(maxReceivePerSlot,
                            this.energyStorage.getAmount()), transaction);
                }
            }

            if(energyConsumptionSum == -1)
                energyConsumptionSum = energyConsumption;
            else
                energyConsumptionSum += energyConsumption;

            if(energyConsumptionSum < 0)
                energyConsumptionSum = Long.MAX_VALUE;
        }

        return energyConsumptionSum;
    }

    private void resetProgress(int index) {
        energyConsumptionLeft[index] = -1;
    }

    private boolean hasRecipe(int index) {
        ItemStack stack = itemHandler.getItem(index);

        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, itemHandler.getItem(index));

        Optional<RecipeHolder<ChargerRecipe>> recipe = level == null?Optional.empty():level.getRecipeManager().
                getRecipeFor(ChargerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);

        if(recipe.isPresent())
            return true;

        return EnergyStorageUtil.isEnergyStorage(stack);
    }
}