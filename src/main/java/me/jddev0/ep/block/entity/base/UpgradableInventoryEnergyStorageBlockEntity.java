package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public abstract class UpgradableInventoryEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends ItemStackHandler>
        extends MenuInventoryEnergyStorageBlockEntity<E, I> {
    protected final UpgradeModuleInventory upgradeModuleInventory;
    protected final ContainerListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    public UpgradableInventoryEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                       String machineName,
                                                       int baseEnergyCapacity, int baseEnergyTransferRate,
                                                       int slotCount,
                                                       UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate, slotCount);

        this.upgradeModuleInventory = new UpgradeModuleInventory(upgradeModifierSlots);
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        //Save Upgrade Module Inventory first
        nbt.put("upgrade_module_inventory", upgradeModuleInventory.saveToNBT(registries));

        super.saveAdditional(nbt, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        //Load Upgrade Module Inventory first
        upgradeModuleInventory.removeListener(updateUpgradeModuleListener);
        upgradeModuleInventory.loadFromNBT(nbt.getCompoundOrEmpty("upgrade_module_inventory"), registries);
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        super.loadAdditional(nbt, registries);
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