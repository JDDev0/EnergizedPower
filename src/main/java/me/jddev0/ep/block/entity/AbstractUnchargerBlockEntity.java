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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public abstract class AbstractUnchargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler> {
    protected final UpgradableMenuProvider menuProvider;

    protected final int[] energyProductionLeft;

    //Item slot indices are dynamic

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i < 0 || i >= slotCount)
            return false;

        ItemStack stack = itemHandler.getStackInSlot(i);
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyStorage == null || !energyStorage.canExtract())
            return true;

        return energyStorage.extractEnergy(AbstractUnchargerBlockEntity.this.limitingEnergyStorage.getMaxExtract() / slotCount, true) == 0;
    });

    public AbstractUnchargerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                        String machineName, UpgradableMenuProvider menuProvider,
                                        int slotCount,
                                        int baseEnergyCapacityPerSlot, int baseEnergyTransferRatePerSlot) {
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

        energyProductionLeft = new int[slotCount];
        for(int i = 0;i < slotCount;i++)
            energyProductionLeft[i] = -1;
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
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
            public int getMaxExtract() {
                return Math.max(1, (int)Math.ceil(maxExtract * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public int getCapacity(int index) {
                return 1;
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < slotCount) {
                    IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                    return energyStorage != null && energyStorage.canExtract();
                }

                return super.isValid(slot, stack);
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot >= 0 && slot < slotCount) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() && (!ItemStack.isSameItem(stack, previousItemStack) ||
                            (!ItemStack.isSameItemSameComponents(stack, previousItemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(stack.getCapability(Capabilities.EnergyStorage.ITEM) != null &&
                                            previousItemStack.getCapability(Capabilities.EnergyStorage.ITEM) != null))))
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

            //Only add individual & n - 1 slot groups if more than one thread, otherwise there would be duplicated groups
            if(slotCount > 1) {
                List<SlotEntry> allInputsExceptFirst = IntStream.range(1, slotCount).mapToObj(SlotEntry::ofInput).toList();
                List<SlotEntry> allOutputsExceptFirst  = IntStream.range(1, slotCount).mapToObj(SlotEntry::ofOutput).toList();
                List<SlotEntry> allSlotsExceptFirst  = IntStream.range(1, slotCount).mapToObj(SlotEntry::ofBoth).toList();

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

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        //itemHandlerSided must be used because of an extra check
        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.ITEM);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.ITEM);
        return conf.createSidedItemHandlerFor(slotGroups, itemHandlerSided, facing, side);
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        for(int i = 0;i < slotCount;i++)
            nbt.putInt("recipe.energy_production_left." + i, energyProductionLeft[i]);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        for(int i = 0;i < slotCount;i++)
            energyProductionLeft[i] = nbt.getInt("recipe.energy_production_left." + i);

        //Ensure compatibility with older versions: Override recipe progress data for slot 0
        if(nbt.contains("recipe.energy_production_left"))
            energyProductionLeft[0] = nbt.getInt("recipe.energy_production_left");
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

    protected final int getEnergyProductionPerTickSum() {
        final int maxExtractPerSlot = Math.max(0, (int)Math.min(this.limitingEnergyStorage.getMaxExtract() / (double)slotCount,
                Math.ceil((this.energyStorage.getMaxEnergyStored() - this.energyStorage.getEnergy()) / (double)slotCount)));

        int energyProductionSum = -1;

        for(int i = 0;i < slotCount;i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if(energyStorage == null || !energyStorage.canExtract())
                continue;

            int energyProduction = energyStorage.extractEnergy(Math.max(0, Math.min(maxExtractPerSlot,
                    this.energyStorage.getCapacity() - this.energyStorage.getEnergy())), true);

            if(energyProductionSum == -1)
                energyProductionSum = energyProduction;
            else
                energyProductionSum += energyProduction;

            if(energyProductionSum < 0)
                energyProductionSum = Integer.MAX_VALUE;
        }

        return energyProductionSum;
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, AbstractUnchargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        final int maxExtractPerSlot = Math.max(0, (int)Math.min(blockEntity.limitingEnergyStorage.getMaxExtract() / (double)blockEntity.slotCount,
                Math.ceil((blockEntity.energyStorage.getMaxEnergyStored() - blockEntity.energyStorage.getEnergy()) / (double)blockEntity.slotCount)));

        for(int i = 0;i < blockEntity.slotCount;i++) {
            if(blockEntity.hasRecipe(i)) {
                ItemStack stack = blockEntity.itemHandler.getStackInSlot(i);

                IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                if(energyStorage == null || !energyStorage.canExtract())
                    continue;

                blockEntity.energyProductionLeft[i] = energyStorage.getEnergyStored();

                int energyProductionPerTick = energyStorage.extractEnergy(Math.max(0, Math.min(maxExtractPerSlot,
                        blockEntity.energyStorage.getCapacity() - blockEntity.energyStorage.getEnergy())), false);

                if(blockEntity.energyProductionLeft[i] < 0 || energyProductionPerTick < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(i);
                    setChanged(level, blockPos, state);

                    continue;
                }

                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() + energyProductionPerTick);
                blockEntity.energyProductionLeft[i] -= energyProductionPerTick;

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
        return stack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
    }
}