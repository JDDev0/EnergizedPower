package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class UpgradableInventoryFluidEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends ItemStackHandler, F extends IFluidHandler>
        extends MenuInventoryFluidEnergyStorageBlockEntity<E, I, F> {
    protected final UpgradeModuleInventory upgradeModuleInventory;
    protected final ContainerListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    public UpgradableInventoryFluidEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                            String machineName,
                                                            int baseEnergyCapacity, int baseEnergyTransferRate,
                                                            int slotCount,
                                                            FluidStorageMethods<F> fluidStorageMethods, int baseTankCapacity,
                                                            UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate, slotCount, fluidStorageMethods,
                baseTankCapacity);

        this.upgradeModuleInventory = new UpgradeModuleInventory(upgradeModifierSlots);
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        //Save Upgrade Module Inventory first
        upgradeModuleInventory.saveData(view.child("upgrade_module_inventory"));

        super.saveAdditional(view);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        //Load Upgrade Module Inventory first
        upgradeModuleInventory.removeListener(updateUpgradeModuleListener);
        upgradeModuleInventory.readData(view.childOrEmpty("upgrade_module_inventory"));
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        super.loadAdditional(view);
    }

    @Override
    public void preRemoveSideEffects(BlockPos worldPosition, BlockState oldState) {
        super.preRemoveSideEffects(worldPosition, oldState);

        if(level != null) {
            Containers.dropContents(level, worldPosition, upgradeModuleInventory);
        }
    }

    protected void updateUpgradeModules() {
        setChanged();
        syncEnergyToPlayers(32);
    }
}