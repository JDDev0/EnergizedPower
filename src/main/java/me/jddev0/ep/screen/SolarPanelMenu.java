package me.jddev0.ep.screen;

import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.data.SimpleEnergyValueContainerData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.tier.SolarPanelTier;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.IEnergyStorageProducerIndicatorBarMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class SolarPanelMenu extends UpgradableEnergyStorageMenu<SolarPanelBlockEntity>
        implements IEnergyStorageProducerIndicatorBarMenu {
    private final SimpleEnergyValueContainerData energyProductionPerTickData = new SimpleEnergyValueContainerData();

    public SolarPanelMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getWorld().getBlockEntity(pos), inv, new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.MOON_LIGHT
        ), null);
    }

    public SolarPanelMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory,
                          UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data) {
        super(
                ((SolarPanelBlockEntity)blockEntity).getTier().getMenuTypeFromTier(), id,

                playerInventory, blockEntity,
                ((SolarPanelBlockEntity)blockEntity).getTier().getBlockFromTier(),

                upgradeModuleInventory, 2
        );

        for(int i = 0;i < upgradeModuleInventory.size();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 71 + i * 18, 35, this::isInUpgradeModuleView));

        if(data == null) {
            addProperties(energyProductionPerTickData);
        }else {
            addProperties(data);
        }
    }

    @Override
    public long getEnergyIndicatorBarValue() {
        return 0;
    }

    @Override
    public long getEnergyPerTickBarValue() {
        return energyProductionPerTickData.getValue();
    }

    public SolarPanelTier getTier() {
        return blockEntity.getTier();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory
            if(!insertItem(sourceItem, 4 * 9, 4 * 9 + 2, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 2) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
            if(!insertItem(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.setStack(ItemStack.EMPTY);
        else
            sourceSlot.markDirty();

        sourceSlot.onTakeItem(player, sourceItem);

        return sourceItemCopy;
    }
}
