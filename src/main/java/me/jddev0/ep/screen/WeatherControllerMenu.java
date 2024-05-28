package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.WeatherControllerBlockEntity;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;

public class WeatherControllerMenu extends UpgradableEnergyStorageMenu<WeatherControllerBlockEntity> {
    private final PropertyDelegate data;

    public WeatherControllerMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getWorld().getBlockEntity(pos), inv, new UpgradeModuleInventory(
                UpgradeModuleModifier.DURATION
        ), new ArrayPropertyDelegate(2));
    }

    public WeatherControllerMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory,
                                 UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data) {
        super(
                ModMenuTypes.WEATHER_CONTROLLER_MENU, id,

                playerInventory, blockEntity,
                ModBlocks.WEATHER_CONTROLLER,

                upgradeModuleInventory, 1
        );

        checkDataCount(data, 2);
        this.data = data;

        addSlot(new UpgradeModuleSlot(upgradeModuleInventory, 0, 80, 35, this::isInUpgradeModuleView));

        addProperties(this.data);
    }

    public int getSelectedWeatherType() {
        return data.get(0);
    }

    public boolean hasEnoughEnergy() {
        return data.get(1) != 0;
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
            if(!insertItem(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1) {
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
