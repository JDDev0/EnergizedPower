package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AdvancedUnchargerBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.data.ComparatorModeValueContainerData;
import me.jddev0.ep.inventory.data.EnergyValueContainerData;
import me.jddev0.ep.inventory.data.RedstoneModeValueContainerData;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.AdvancedUnchargerMenu;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.world.SimpleContainer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AdvancedUnchargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler> {
    //Item slot indices are dynamic in the future

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i < 0 || i > 2)
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

                if(slot >= 0 && slot < 3) {
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
                if(slot >= 0 && slot < 3) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() && (!ItemStack.isSameItem(stack, previousItemStack) ||
                            (!ItemStack.isSameItemSameComponents(stack, previousItemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(EnergyStorageUtil.isEnergyStorage(stack) && EnergyStorageUtil.isEnergyStorage(previousItemStack)))))
                        resetProgress(slot);
                }

                setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
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
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncIOConfigurationToPlayer(player);

        return new AdvancedUnchargerMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        //this.wokerThreadCount is not yet assigned when this method gets called
        int workerThreadCount = /* TODO initWorkerThreadCount()*/3;

        if(slotType == SlotType.ITEM) {
            List<SlotEntry> allInputs = IntStream.range(0, workerThreadCount).mapToObj(SlotEntry::ofInput).toList();
            List<SlotEntry> allOutputs = IntStream.range(0, workerThreadCount).mapToObj(SlotEntry::ofOutput).toList();
            List<SlotEntry> allSlots = IntStream.range(0, workerThreadCount).mapToObj(SlotEntry::ofBoth).toList();

            List<SlotGroup> slotGroups = new ArrayList<>();

            //All inputs only
            slotGroups.add(SlotGroup.of(allInputs));

            //All outputs only
            slotGroups.add(SlotGroup.of(allOutputs));

            //All inputs & outputs
            slotGroups.add(SlotGroup.of(allSlots));

            //Only add individual & n - 1 slot groups if more than one thread, otherwise there would be duplicated groups
            if(workerThreadCount > 1) {
                List<SlotEntry> allInputsExceptFirst = IntStream.range(1, workerThreadCount).mapToObj(SlotEntry::ofInput).toList();
                List<SlotEntry> allOutputsExceptFirst  = IntStream.range(1, workerThreadCount).mapToObj(SlotEntry::ofOutput).toList();
                List<SlotEntry> allSlotsExceptFirst  = IntStream.range(1, workerThreadCount).mapToObj(SlotEntry::ofBoth).toList();

                //First input only
                slotGroups.add(SlotGroup.of(SlotEntry.ofInput(0)));

                //First output only
                slotGroups.add(SlotGroup.of(SlotEntry.ofOutput(0)));

                //First input & output
                slotGroups.add(SlotGroup.of(SlotEntry.ofBoth(0)));

                //All except first input only
                slotGroups.add(SlotGroup.of(allInputsExceptFirst));

                //All except first output only
                slotGroups.add(SlotGroup.of(allOutputsExceptFirst));

                //All except first input & output
                slotGroups.add(SlotGroup.of(allSlotsExceptFirst));
            }

            return slotGroups;
        }

        return super.initSlotGroups(slotType);
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        if(slotType == SlotType.ITEM) {
            for(RelativeDirection direction: RelativeDirection.values())
                conf.setSlotGroupId(direction, 2);
        }

        return conf;
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        //itemHandlerSided must be used because of an extra check
        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.ITEM);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.ITEM);
        return conf.createSidedItemHandlerFor(slotGroups, itemHandlerSided, facing, side);
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(nbt, registries);

        for(int i = 0;i < 3;i++)
            nbt.put("recipe.energy_production_left." + i, LongTag.valueOf(energyProductionLeft[i]));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(nbt, registries);

        for(int i = 0;i < 3;i++)
            energyProductionLeft[i] = nbt.getLong("recipe.energy_production_left." + i);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AdvancedUnchargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(blockEntity.redstoneMode.isActive(state.getValue(AdvancedUnchargerBlock.POWERED))) {
            blockEntity.pullItemsFromInputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_PULLING));
            tickRecipe(level, blockPos, state, blockEntity);
            blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));
        }

        blockEntity.pushEnergyToOutputs(Direction.values());
    }

    protected final long getEnergyProductionPerTickSum() {
        final long maxExtractPerSlot = Math.max(0, (long)Math.min(this.limitingEnergyStorage.getMaxExtract() / 3.,
                Math.ceil((this.energyStorage.getCapacity() - this.energyStorage.getAmount()) / 3.)));

        long energyProductionSum = -1;

        for(int i = 0;i < 3;i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);

            if(!EnergyStorageUtil.isEnergyStorage(stack))
                continue;

            EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                    ofSingleSlot(itemHandler.getSlot(i)));
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

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, AdvancedUnchargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        final long maxExtractPerSlot = Math.max(0, (long)Math.min(blockEntity.limitingEnergyStorage.getMaxExtract() / 3.,
                Math.ceil((blockEntity.energyStorage.getCapacity() - blockEntity.energyStorage.getAmount()) / 3.)));

        for(int i = 0;i < 3;i++) {
            if(blockEntity.hasRecipe(i)) {
                ItemStack stack = blockEntity.itemHandler.getStackInSlot(i);

                if(!EnergyStorageUtil.isEnergyStorage(stack))
                    continue;

                EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                        ofSingleSlot(blockEntity.itemHandler.getSlot(i)));
                if(limitingEnergyStorage == null)
                    continue;

                if(!limitingEnergyStorage.supportsExtraction())
                    continue;

                blockEntity.energyProductionLeft[i] = limitingEnergyStorage.getAmount();

                if(blockEntity.energyProductionLeft[i] < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(i);
                    setChanged(level, blockPos, state);

                    continue;
                }

                blockEntity.energyProductionLeft[i] -= EnergyStorageUtil.move(limitingEnergyStorage, blockEntity.energyStorage, maxExtractPerSlot, null);

                if(blockEntity.energyProductionLeft[i] <= 0)
                    blockEntity.resetProgress(i);

                setChanged(level, blockPos, state);
            }else {
                blockEntity.resetProgress(i);
                setChanged(level, blockPos, state);
            }
        }
    }

    private void resetProgress(int index) {
        energyProductionLeft[index] = -1;
    }

    private boolean hasRecipe(int index) {
        ItemStack stack = itemHandler.getStackInSlot(index);
        return EnergyStorageUtil.isEnergyStorage(stack);
    }
}