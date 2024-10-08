package me.jddev0.ep.screen;

import me.jddev0.ep.block.SolarPanelBlock;
import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class SolarPanelMenu extends UpgradableEnergyStorageMenu<SolarPanelBlockEntity> {
    public static ScreenHandlerType<SolarPanelMenu> getMenuTypeFromTier(SolarPanelBlock.Tier tier) {
        return switch(tier) {
            case TIER_1 -> EPMenuTypes.SOLAR_PANEL_MENU_1;
            case TIER_2 -> EPMenuTypes.SOLAR_PANEL_MENU_2;
            case TIER_3 -> EPMenuTypes.SOLAR_PANEL_MENU_3;
            case TIER_4 -> EPMenuTypes.SOLAR_PANEL_MENU_4;
            case TIER_5 -> EPMenuTypes.SOLAR_PANEL_MENU_5;
            case TIER_6 -> EPMenuTypes.SOLAR_PANEL_MENU_6;
        };
    }

    public SolarPanelMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getWorld().getBlockEntity(pos), inv, new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.MOON_LIGHT
        ));
    }

    public SolarPanelMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory,
                          UpgradeModuleInventory upgradeModuleInventory) {
        super(
                getMenuTypeFromTier(((SolarPanelBlockEntity)blockEntity).getTier()), id,

                playerInventory, blockEntity,
                SolarPanelBlock.getBlockFromTier(((SolarPanelBlockEntity)blockEntity).getTier()),

                upgradeModuleInventory, 2
        );

        for(int i = 0;i < upgradeModuleInventory.size();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 71 + i * 18, 35, this::isInUpgradeModuleView));
    }

    public SolarPanelBlock.Tier getTier() {
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
