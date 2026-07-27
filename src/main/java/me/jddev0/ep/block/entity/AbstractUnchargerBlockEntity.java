package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.base.WorkerMachineBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.block.entity.base.UpgradableMenuProvider;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.CombinedContainerData;
import me.jddev0.ep.inventory.data.ComparatorModeValueContainerData;
import me.jddev0.ep.inventory.data.EnergyValueContainerData;
import me.jddev0.ep.inventory.data.RedstoneModeValueContainerData;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public abstract class AbstractUnchargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler> {
    protected final UpgradableMenuProvider menuProvider;

    protected final long[] energyProductionLeft;

    //Item slot indices are dynamic

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i < 0 || i >= slotCount)
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

    public AbstractUnchargerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                        String machineName, UpgradableMenuProvider menuProvider,
                                        int slotCount,
                                        long baseEnergyCapacityPerSlot, long baseEnergyTransferRatePerSlot) {
        super(
                type, blockPos, blockState,

                machineName,

                baseEnergyCapacityPerSlot * slotCount,
                baseEnergyTransferRatePerSlot * slotCount,

                slotCount,

                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );

        this.menuProvider = menuProvider;

        energyProductionLeft = new long[slotCount];
        for(int i = 0;i < slotCount;i++)
            energyProductionLeft[i] = -1;
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

                if(slot >= 0 && slot < slotCount) {
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
                if(slot >= 0 && slot < slotCount) {
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
        List<ContainerData> combinedContainerDataList = new ArrayList<>(slotCount + 3);
        combinedContainerDataList.add(new EnergyValueContainerData(this::getEnergyProductionPerTickSum, value -> {}));

        for(int i = 0;i < slotCount;i++) {
            final int slot = i;

            combinedContainerDataList.add(new EnergyValueContainerData(() -> energyProductionLeft[slot], value -> {}));
        }


        combinedContainerDataList.add(new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value));
        combinedContainerDataList.add(new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value));

        return new CombinedContainerData(combinedContainerDataList.toArray(ContainerData[]::new));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncIOConfigurationToPlayer(player);

        return menuProvider.createMenu(id, inventory, this, upgradeModuleInventory, data);
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        if(slotType == SlotType.ITEM) {
            List<SlotEntry> allInputs = IntStream.range(0, slotCount).mapToObj(SlotEntry::ofInput).toList();
            List<SlotEntry> allOutputs = IntStream.range(0, slotCount).mapToObj(SlotEntry::ofOutput).toList();
            List<SlotEntry> allSlots = IntStream.range(0, slotCount).mapToObj(SlotEntry::ofBoth).toList();

            List<SlotGroup> slotGroups = new ArrayList<>();

            //All inputs only
            slotGroups.add(SlotGroup.of(allInputs));

            //All outputs only
            slotGroups.add(SlotGroup.of(allOutputs));

            //All inputs & outputs
            slotGroups.add(SlotGroup.of(allSlots));

            //Only add 0 to n/2 - 1 & n/2 to n - 1 slot groups if more than one thread, otherwise there would be duplicated groups
            if(slotCount > 1) {
                int firstHalfSlotCount = slotCount/2;

                List<SlotEntry> allInputsFirstHalf = IntStream.range(0, firstHalfSlotCount).mapToObj(SlotEntry::ofInput).toList();
                List<SlotEntry> allOutputsFirstHalf  = IntStream.range(0, firstHalfSlotCount).mapToObj(SlotEntry::ofOutput).toList();
                List<SlotEntry> allSlotsFirstHalf  = IntStream.range(0, firstHalfSlotCount).mapToObj(SlotEntry::ofBoth).toList();

                List<SlotEntry> allInputsSecondHalf = IntStream.range(firstHalfSlotCount, slotCount).mapToObj(SlotEntry::ofInput).toList();
                List<SlotEntry> allOutputsSecondHalf  = IntStream.range(firstHalfSlotCount, slotCount).mapToObj(SlotEntry::ofOutput).toList();
                List<SlotEntry> allSlotsSecondHalf  = IntStream.range(firstHalfSlotCount, slotCount).mapToObj(SlotEntry::ofBoth).toList();

                //First half input only
                slotGroups.add(SlotGroup.of(allInputsFirstHalf));

                //First half output only
                slotGroups.add(SlotGroup.of(allOutputsFirstHalf));

                //First half input & output
                slotGroups.add(SlotGroup.of(allSlotsFirstHalf));

                //Second half input only
                slotGroups.add(SlotGroup.of(allInputsSecondHalf));

                //Second half output only
                slotGroups.add(SlotGroup.of(allOutputsSecondHalf));

                //Second half input & output
                slotGroups.add(SlotGroup.of(allSlotsSecondHalf));
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
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        for(int i = 0;i < slotCount;i++)
            view.putLong("recipe.energy_production_left." + i, energyProductionLeft[i]);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        for(int i = 0;i < slotCount;i++)
            energyProductionLeft[i] = view.getLongOr("recipe.energy_production_left." + i, 0);

        //Ensure compatibility with older versions: Override recipe progress data for slot 0
        if(view.getLong("recipe.energy_production_left").isPresent())
            energyProductionLeft[0] = view.getLongOr("recipe.energy_production_left", 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AbstractUnchargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(blockEntity.redstoneMode.isActive(state.getValue(WorkerMachineBlock.POWERED))) {
            blockEntity.pullItemsFromInputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_PULLING));
            tickRecipe(level, blockPos, state, blockEntity);
            blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));
        }

        blockEntity.pushEnergyToOutputs(Direction.values());
    }

    protected final long getEnergyProductionPerTickSum() {
        final long maxExtractPerSlot = Math.max(0, (long)Math.min(this.limitingEnergyStorage.getMaxExtract() / (double)slotCount,
                Math.ceil((this.energyStorage.getCapacity() - this.energyStorage.getAmount()) / (double)slotCount)));

        long energyProductionSum = -1;

        for(int i = 0;i < slotCount;i++) {
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

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, AbstractUnchargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        final long maxExtractPerSlot = Math.max(0, (long)Math.min(blockEntity.limitingEnergyStorage.getMaxExtract() / (double)blockEntity.slotCount,
                Math.ceil((blockEntity.energyStorage.getCapacity() - blockEntity.energyStorage.getAmount()) / (double)blockEntity.slotCount)));

        for(int i = 0;i < blockEntity.slotCount;i++) {
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