package me.jddev0.ep.screen;

import me.jddev0.ep.block.SolarPanelBlock;
import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SolarPanelMenu extends UpgradableEnergyStorageMenu<SolarPanelBlockEntity> {
    public static MenuType<SolarPanelMenu> getMenuTypeFromTier(SolarPanelBlock.Tier tier) {
        return switch(tier) {
            case TIER_1 -> EPMenuTypes.SOLAR_PANEL_MENU_1.get();
            case TIER_2 -> EPMenuTypes.SOLAR_PANEL_MENU_2.get();
            case TIER_3 -> EPMenuTypes.SOLAR_PANEL_MENU_3.get();
            case TIER_4 -> EPMenuTypes.SOLAR_PANEL_MENU_4.get();
            case TIER_5 -> EPMenuTypes.SOLAR_PANEL_MENU_5.get();
            case TIER_6 -> EPMenuTypes.SOLAR_PANEL_MENU_6.get();
        };
    }

    public SolarPanelMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.MOON_LIGHT
        ));
    }

    public SolarPanelMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory) {
        super(
                getMenuTypeFromTier(((SolarPanelBlockEntity)blockEntity).getTier()), id,

                inv, blockEntity,
                SolarPanelBlock.getBlockFromTier(((SolarPanelBlockEntity)blockEntity).getTier()),

                upgradeModuleInventory, 2
        );

        for(int i = 0;i < upgradeModuleInventory.getContainerSize();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 71 + i * 18, 35, this::isInUpgradeModuleView));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getItem();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory
            if(!moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 2, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 2) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.set(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceItem);

        return sourceItemCopy;
    }
}
