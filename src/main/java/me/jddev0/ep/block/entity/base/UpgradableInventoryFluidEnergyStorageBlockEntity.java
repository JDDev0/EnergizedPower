package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.fluid.IEnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.ContainerListener;
import me.jddev0.ep.inventory.IEnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.upgrade.IUpgradableMachine;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class UpgradableInventoryFluidEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends IEnergizedPowerItemStackHandler, F extends IEnergizedPowerFluidStorage>
        extends MenuInventoryFluidEnergyStorageBlockEntity<E, I, F>
        implements IUpgradableMachine {
    protected final UpgradeModuleInventory upgradeModuleInventory;
    protected final ContainerListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    public UpgradableInventoryFluidEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                            String machineName,
                                                            int baseEnergyCapacity, int baseEnergyTransferRate,
                                                            int slotCount,
                                                            int baseTankCapacity,
                                                            UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate, slotCount, baseTankCapacity);

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

    @Override
    public ItemStack onShiftClickUpgradeModuleInsertion(Player player, ItemStack sourceItemStack) {
        if(!(sourceItemStack.getItem() instanceof UpgradeModuleItem sourceUpgradeModuleItem)) {
            return sourceItemStack;
        }

        sourceItemStack = sourceItemStack.copy();

        for(int i = 0;i < upgradeModuleInventory.getContainerSize();i++) {
            ItemStack targetItemStack = upgradeModuleInventory.getItem(i);
            if(sourceUpgradeModuleItem.getMainUpgradeModuleModifier() == upgradeModuleInventory.
                    getUpgradeModifierSlots()[i] && sourceItemStack.getCount() > 0 &&
                    !ItemStack.isSameItemSameComponents(sourceItemStack, targetItemStack)) {
                if(targetItemStack.isEmpty()) {
                    upgradeModuleInventory.setItem(i, sourceItemStack.copyWithCount(1));
                    sourceItemStack.shrink(1);
                }else if(targetItemStack.getItem() instanceof UpgradeModuleItem targetUpgradeModuleItem && (
                        targetUpgradeModuleItem.shouldIgnoreTierValueForItemSwapping() ||
                                sourceUpgradeModuleItem.getUpgradeModuleTier() > targetUpgradeModuleItem.getUpgradeModuleTier())) {
                    //Remove item stack completely (Count cannot be > 1 without modifying the block data itself,
                    // but in case it was modified the whole stack should be removed anyway to prevent losing items)
                    player.getInventory().placeItemBackInInventory(targetItemStack.copy());

                    upgradeModuleInventory.setItem(i, sourceItemStack.copyWithCount(1));
                    sourceItemStack.shrink(1);
                }
            }
        }

        return sourceItemStack;
    }
}