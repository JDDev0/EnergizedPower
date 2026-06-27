package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.UnchargerBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.UnchargerMenu;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.ArrayList;
import java.util.List;

public class UnchargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler> {
    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i != 0)
            return false;

        ItemStack stack = itemHandler.getStackInSlot(i);

        if(!EnergyStorageUtil.isEnergyStorage(stack))
            return true;

        EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                ofSingleSlot(itemHandler.getSlot(i)));
        if(limitingEnergyStorage == null)
            return true;

        if(!limitingEnergyStorage.supportsExtraction())
            return true;

        return limitingEnergyStorage.getAmount() == 0;
    });

    private long energyProductionLeft = -1;

    public UnchargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.UNCHARGER_ENTITY, blockPos, blockState,

                "uncharger",

                ModConfigs.COMMON_UNCHARGER_CAPACITY.getValue(),
                ModConfigs.COMMON_UNCHARGER_TRANSFER_RATE.getValue(),

                1,

                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity) {
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
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, 0, baseEnergyTransferRate) {
            @Override
            public long getMaxExtract() {
                return Math.max(1, (long)Math.ceil(maxExtract * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public long getCapacity(int index, ItemVariant resource) {
                return 1;
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemVariant resource) {
                ItemStack stack = resource.toStack();

                if(slot == 0) {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(limitingEnergyStorage == null)
                        return false;

                    return limitingEnergyStorage.supportsExtraction();
                }

                return super.isValid(slot, resource);
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() && (!ItemStack.isSameItem(stack, previousItemStack) ||
                            (!ItemStack.isSameItemSameComponents(stack, previousItemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(EnergyStorageUtil.isEnergyStorage(stack) && EnergyStorageUtil.isEnergyStorage(previousItemStack)))))
                        resetProgress();
                }

                setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new EnergyValueContainerData(() -> hasRecipe()?getEnergyProductionPerTick():-1, value -> {}),
                new EnergyValueContainerData(() -> energyProductionLeft, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new UnchargerMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.put("recipe.energy_production_left", LongTag.valueOf(energyProductionLeft));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        energyProductionLeft = nbt.getLong("recipe.energy_production_left");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, UnchargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(blockEntity.redstoneMode.isActive(state.getValue(UnchargerBlock.POWERED))) {
            blockEntity.pullItemsFromInputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_PULLING));
            tickRecipe(level, blockPos, state, blockEntity);
            blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));
        }

        transferEnergy(level, blockPos, state, blockEntity);
    }

    protected final long getEnergyProductionPerTick() {
        ItemStack stack = itemHandler.getStackInSlot(0);

        if(!EnergyStorageUtil.isEnergyStorage(stack))
            return -1;

        EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                ofSingleSlot(itemHandler.getSlot(0)));
        if(limitingEnergyStorage == null)
            return -1;

        if(!limitingEnergyStorage.supportsExtraction())
            return -1;

        try(Transaction transaction = Transaction.openOuter()) {
            return limitingEnergyStorage.extract(Math.max(0, Math.min(this.limitingEnergyStorage.getMaxExtract(),
                    this.energyStorage.getCapacity() - this.energyStorage.getAmount())), transaction);
        }
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, UnchargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.itemHandler.getStackInSlot(0);

            if(!EnergyStorageUtil.isEnergyStorage(stack))
                return;

            EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                    ofSingleSlot(blockEntity.itemHandler.getSlot(0)));
            if(limitingEnergyStorage == null)
                return;

            if(!limitingEnergyStorage.supportsExtraction())
                return;

            blockEntity.energyProductionLeft = limitingEnergyStorage.getAmount();

            if(blockEntity.energyProductionLeft < 0) {
                //Reset progress for invalid values

                blockEntity.resetProgress();
                setChanged(level, blockPos, state);

                return;
            }

            blockEntity.energyProductionLeft -= EnergyStorageUtil.move(limitingEnergyStorage, blockEntity.energyStorage,
                    blockEntity.limitingEnergyStorage.getMaxExtract(), null);

            if(blockEntity.energyProductionLeft <= 0)
                blockEntity.resetProgress();

            setChanged(level, blockPos, state);
        }else {
            blockEntity.resetProgress();
            setChanged(level, blockPos, state);
        }
    }

    private static void transferEnergy(Level level, BlockPos blockPos, BlockState state, UnchargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        List<EnergyStorage> consumerItems = new ArrayList<>();
        List<Long> consumerEnergyValues = new ArrayList<>();
        long consumptionSum = 0;
        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            EnergyStorage limitingEnergyStorage = EnergyStorage.SIDED.find(level, testPos, direction.getOpposite());
            if(limitingEnergyStorage == null)
                continue;

            if(!limitingEnergyStorage.supportsInsertion())
                continue;

            try(Transaction transaction = Transaction.openOuter()) {
                long received = limitingEnergyStorage.insert(Math.min(blockEntity.limitingEnergyStorage.getMaxExtract(),
                        blockEntity.energyStorage.getAmount()), transaction);

                if(received <= 0)
                    continue;

                consumptionSum += received;
                consumerItems.add(limitingEnergyStorage);
                consumerEnergyValues.add(received);
            }
        }

        List<Long> consumerEnergyDistributed = new ArrayList<>();
        for(int i = 0;i < consumerItems.size();i++)
            consumerEnergyDistributed.add(0L);

        long consumptionLeft = Math.min(blockEntity.limitingEnergyStorage.getMaxExtract(),
                Math.min(blockEntity.energyStorage.getAmount(), consumptionSum));
        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.energyStorage.extract(consumptionLeft, transaction);
            transaction.commit();
        }

        int divisor = consumerItems.size();
        outer:
        while(consumptionLeft > 0) {
            long consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < consumerEnergyValues.size();i++) {
                long consumptionDistributed = consumerEnergyDistributed.get(i);
                long consumptionOfConsumerLeft = consumerEnergyValues.get(i) - consumptionDistributed;

                long consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                consumerEnergyDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < consumerItems.size();i++) {
            long energy = consumerEnergyDistributed.get(i);
            if(energy > 0) {
                try(Transaction transaction = Transaction.openOuter()) {
                    consumerItems.get(i).insert(energy, transaction);
                    transaction.commit();
                }
            }
        }
    }

    private void resetProgress() {
        energyProductionLeft = -1;
    }

    private boolean hasRecipe() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        return EnergyStorageUtil.isEnergyStorage(stack);
    }
}