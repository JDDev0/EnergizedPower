package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.item.upgrade.UpgradeModuleItem;
import me.jddev0.ep.machine.ItemDrop;
import me.jddev0.ep.machine.upgrade.IUpgradableMachine;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class UpgradableEnergyStorageBlockEntity<E extends IEnergizedPowerEnergyStorage>
        extends MenuEnergyStorageBlockEntity<E>
        implements ItemDrop, IUpgradableMachine {
    protected final UpgradeModuleInventory upgradeModuleInventory;
    protected final ContainerListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    public UpgradableEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                              String machineName,
                                              int baseEnergyCapacity, int baseEnergyTransferRate,
                                              UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate);

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
        upgradeModuleInventory.loadFromNBT(nbt.getCompound("upgrade_module_inventory"), registries);
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        super.loadAdditional(nbt, registries);
    }

    @Override
    public void drops(Level level, BlockPos worldPosition) {
        Containers.dropContents(level, worldPosition, upgradeModuleInventory);
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