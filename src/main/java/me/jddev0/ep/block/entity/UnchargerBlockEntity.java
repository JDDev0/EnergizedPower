package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.UnchargerBlock;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.UnchargerMenu;
import me.jddev0.ep.util.CapabilityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnchargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, EnergizedPowerItemStackHandler> {
    private static final int ITEM_SLOT_BOTH = 0;

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i != 0)
            return false;

        ItemStack stack = itemHandler.getStackInSlot(i);
        EnergyHandler limitingEnergyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forHandlerIndexStrict(itemHandler, i));
        if(limitingEnergyStorage == null || !CapabilityUtil.canExtract(limitingEnergyStorage))
            return true;

        return limitingEnergyStorage.getAmountAsInt() == 0;
    });

    private int energyProductionLeft = -1;

    public UnchargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.UNCHARGER_ENTITY.get(), blockPos, blockState,

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
            public long getCapacityAsLong() {
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
            public int getCapacity(int index, ItemResource resource) {
                return 1;
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemResource resource) {
                ItemStack stack = resource.toStack();

                if(slot == 0) {
                    EnergyHandler limitingEnergyStorage = CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Energy.ITEM, stack);
                    return limitingEnergyStorage != null && CapabilityUtil.canExtract(limitingEnergyStorage);
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
                                    !(CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Energy.ITEM, stack) != null &&
                                            CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Energy.ITEM, previousItemStack) != null))))
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
        syncIOConfigurationToPlayer(player);

        return new UnchargerMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        if(slotType == SlotType.ITEM) {
            return List.of(
                    //Input only
                    SlotGroup.of(SlotEntry.ofInput(ITEM_SLOT_BOTH)),

                    //Output only
                    SlotGroup.of(SlotEntry.ofOutput(ITEM_SLOT_BOTH)),

                    //Input & Output
                    SlotGroup.of(SlotEntry.ofBoth(ITEM_SLOT_BOTH))
            );
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

    public @Nullable ResourceHandler<ItemResource> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        //itemHandlerSided must be used because of an extra check
        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.ITEM);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.ITEM);
        return conf.createSidedItemHandlerFor(slotGroups, itemHandlerSided, facing, side);
    }

    public @Nullable EnergyHandler getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("recipe.energy_production_left", energyProductionLeft);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        energyProductionLeft = view.getIntOr("recipe.energy_production_left", 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, UnchargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(blockEntity.redstoneMode.isActive(state.getValue(UnchargerBlock.POWERED))) {
            blockEntity.pullItemsFromInputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_PULLING));
            tickRecipe(level, blockPos, state, blockEntity);
            blockEntity.pushItemsToOutputs(blockEntity.upgradeModuleInventory.getModifierEffectSum(UpgradeModuleModifier.ITEM_EJECTOR));
        }

        blockEntity.pushEnergyToOutputs(Direction.values());
    }
    
    protected final int getEnergyProductionPerTick() {
        ItemStack stack = itemHandler.getStackInSlot(0);

        EnergyHandler limitingEnergyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forHandlerIndexStrict(itemHandler, 0));
        if(limitingEnergyStorage == null || !CapabilityUtil.canExtract(limitingEnergyStorage))
            return -1;

        try(Transaction transaction = Transaction.open(null)) {
            return limitingEnergyStorage.extract(Math.max(0, Math.min(this.limitingEnergyStorage.getMaxExtract(),
                    this.energyStorage.getCapacityAsInt() - this.energyStorage.getAmountAsInt())), transaction);
        }
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, UnchargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.itemHandler.getStackInSlot(0);

            EnergyHandler limitingEnergyStorage = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forHandlerIndexStrict(blockEntity.itemHandler, 0));
            if(limitingEnergyStorage == null || !CapabilityUtil.canExtract(limitingEnergyStorage))
                return;

            blockEntity.energyProductionLeft = limitingEnergyStorage.getAmountAsInt();

            blockEntity.energyProductionLeft -= EnergyHandlerUtil.move(limitingEnergyStorage, blockEntity.energyStorage,
                    blockEntity.limitingEnergyStorage.getMaxExtract(), null);

            if(blockEntity.energyProductionLeft <= 0)
                blockEntity.resetProgress();

            setChanged(level, blockPos, state);
        }else {
            blockEntity.resetProgress();
            setChanged(level, blockPos, state);
        }
    }

    private void resetProgress() {
        energyProductionLeft = -1;
    }

    private boolean hasRecipe() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        return CapabilityUtil.getItemCapabilityReadOnly(Capabilities.Energy.ITEM, stack) != null;
    }
}