package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AdvancedUnchargerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.ComparatorModeValueContainerData;
import me.jddev0.ep.inventory.data.EnergyValueContainerData;
import me.jddev0.ep.inventory.data.RedstoneModeValueContainerData;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.AdvancedUnchargerMenu;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtLong;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.ArrayList;
import java.util.List;

public class AdvancedUnchargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleInventory> {
    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i < 0 || i > 2)
            return false;

        ItemStack itemStack = itemHandler.getStack(i);

        if(!EnergyStorageUtil.isEnergyStorage(itemStack))
            return true;

        EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(itemStack, ContainerItemContext.
                ofSingleSlot(InventoryStorage.of(itemHandler, null).getSlots().get(i)));
        if(limitingEnergyStorage == null)
            return true;

        if(!limitingEnergyStorage.supportsExtraction())
            return true;

        return limitingEnergyStorage.getAmount() == 0;
    });

    private long[] energyProductionLeft = new long[] {
            -1, -1, -1
    };

    public AdvancedUnchargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_UNCHARGER_ENTITY, blockPos, blockState,

                "advanced_uncharger",

                ModConfigs.COMMON_ADVANCED_UNCHARGER_CAPACITY_PER_SLOT.getValue() * 3,
                ModConfigs.COMMON_ADVANCED_UNCHARGER_TRANSFER_RATE_PER_SLOT.getValue() * 3,

                3,

                UpgradeModuleModifier.ENERGY_CAPACITY
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
                markDirty();
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
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public int getMaxCountPerStack() {
                return 1;
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(limitingEnergyStorage == null)
                        return false;

                    return limitingEnergyStorage.supportsExtraction();
                }

                return super.isValid(slot, stack);
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    ItemStack itemStack = getStack(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.areItemsEqual(stack, itemStack) ||
                            (!ItemStack.areItemsAndComponentsEqual(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(EnergyStorageUtil.isEnergyStorage(stack) && EnergyStorageUtil.isEnergyStorage(itemStack)))))
                        resetProgress(slot);
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                AdvancedUnchargerBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new CombinedContainerData(
                new EnergyValueContainerData(this::getEnergyProductionPerTickSum, value -> {}),
                new EnergyValueContainerData(() -> energyProductionLeft[0], value -> {}),
                new EnergyValueContainerData(() -> energyProductionLeft[1], value -> {}),
                new EnergyValueContainerData(() -> energyProductionLeft[2], value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);

        return new AdvancedUnchargerMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, RegistryWrapper.@NotNull WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        for(int i = 0;i < 3;i++)
            nbt.put("recipe.energy_production_left." + i, NbtLong.of(energyProductionLeft[i]));
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.@NotNull WrapperLookup registries) {
        super.readNbt(nbt, registries);

        for(int i = 0;i < 3;i++)
            energyProductionLeft[i] = nbt.getLong("recipe.energy_production_left." + i);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AdvancedUnchargerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(blockEntity.redstoneMode.isActive(state.get(AdvancedUnchargerBlock.POWERED)))
            tickRecipe(level, blockPos, state, blockEntity);

        transferEnergy(level, blockPos, state, blockEntity);
    }

    protected final long getEnergyProductionPerTickSum() {
        final long maxExtractPerSlot = Math.max(0, (long)Math.min(this.limitingEnergyStorage.getMaxExtract() / 3.,
                Math.ceil((this.energyStorage.getCapacity() - this.energyStorage.getAmount()) / 3.)));

        long energyProductionSum = -1;

        for(int i = 0;i < 3;i++) {
            ItemStack stack = itemHandler.getStack(i);

            if(!EnergyStorageUtil.isEnergyStorage(stack))
                continue;

            EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                    ofSingleSlot(InventoryStorage.of(this.itemHandler, null).getSlots().get(i)));
            if(limitingEnergyStorage == null)
                continue;

            if(!limitingEnergyStorage.supportsExtraction())
                continue;

            long energyProduction;
            try(Transaction transaction = Transaction.openOuter()) {
                energyProduction = limitingEnergyStorage.extract(Math.max(0, Math.min(maxExtractPerSlot,
                        this.energyStorage.getCapacity() - this.energyStorage.getAmount())), transaction);
            }

            if(energyProductionSum == -1)
                energyProductionSum = energyProduction;
            else
                energyProductionSum += energyProduction;

            if(energyProductionSum < 0)
                energyProductionSum = Long.MAX_VALUE;
        }

        return energyProductionSum;
    }

    public static void tickRecipe(World level, BlockPos blockPos, BlockState state, AdvancedUnchargerBlockEntity blockEntity) {
        final long maxExtractPerSlot = Math.max(0, (long)Math.min(blockEntity.limitingEnergyStorage.getMaxExtract() / 3.,
                Math.ceil((blockEntity.energyStorage.getCapacity() - blockEntity.energyStorage.getAmount()) / 3.)));

        for(int i = 0;i < 3;i++) {
            if(blockEntity.hasRecipe(i)) {
                ItemStack stack = blockEntity.itemHandler.getStack(i);

                if(!EnergyStorageUtil.isEnergyStorage(stack))
                    continue;

                EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                        ofSingleSlot(InventoryStorage.of(blockEntity.itemHandler, null).getSlots().get(i)));
                if(limitingEnergyStorage == null)
                    continue;

                if(!limitingEnergyStorage.supportsExtraction())
                    continue;

                blockEntity.energyProductionLeft[i] = limitingEnergyStorage.getAmount();

                if(blockEntity.energyProductionLeft[i] < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(i);
                    markDirty(level, blockPos, state);

                    continue;
                }

                blockEntity.energyProductionLeft[i] -= EnergyStorageUtil.move(limitingEnergyStorage, blockEntity.energyStorage, maxExtractPerSlot, null);

                if(blockEntity.energyProductionLeft[i] <= 0)
                    blockEntity.resetProgress(i);

                markDirty(level, blockPos, state);
            }else {
                blockEntity.resetProgress(i);
                markDirty(level, blockPos, state);
            }
        }
    }

    private static void transferEnergy(World level, BlockPos blockPos, BlockState state, AdvancedUnchargerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        List<EnergyStorage> consumerItems = new ArrayList<>();
        List<Long> consumerEnergyValues = new ArrayList<>();
        long consumptionSum = 0;
        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.offset(direction);

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

    private void resetProgress(int index) {
        energyProductionLeft[index] = -1;
    }

    private boolean hasRecipe(int index) {
        ItemStack stack = itemHandler.getStack(index);
        return EnergyStorageUtil.isEnergyStorage(stack);
    }

    @Override
    protected void updateUpgradeModules() {
        for(int i = 0;i < 3;i++)
            resetProgress(i);

        super.updateUpgradeModules();
    }
}