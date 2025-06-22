package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;

public abstract class UpgradableInventoryFluidEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends SimpleInventory, F extends Storage<FluidVariant>>
        extends MenuInventoryFluidEnergyStorageBlockEntity<E, I, F> {
    protected final UpgradeModuleInventory upgradeModuleInventory;
    protected final InventoryChangedListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    public UpgradableInventoryFluidEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                            String machineName,
                                                            long baseEnergyCapacity, long baseEnergyTransferRate,
                                                            int slotCount,
                                                            FluidStorageMethods<F> fluidStorageMethods, long baseTankCapacity,
                                                            UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate, slotCount, fluidStorageMethods,
                baseTankCapacity);

        this.upgradeModuleInventory = new UpgradeModuleInventory(upgradeModifierSlots);
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);
    }

    @Override
    protected void writeData(WriteView view) {
        //Save Upgrade Module Inventory first
        upgradeModuleInventory.saveData(view.get("upgrade_module_inventory"));

        super.writeData(view);
    }

    @Override
    protected void readData(ReadView view) {
        //Load Upgrade Module Inventory first
        upgradeModuleInventory.removeListener(updateUpgradeModuleListener);
        upgradeModuleInventory.readData(view.getReadView("upgrade_module_inventory"));
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        super.readData(view);
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        super.onBlockReplaced(pos, oldState);

        if(world != null)
            ItemScatterer.spawn(world, pos, upgradeModuleInventory);
    }

    protected void updateUpgradeModules() {
        markDirty();
        syncEnergyToPlayers(32);
    }
}