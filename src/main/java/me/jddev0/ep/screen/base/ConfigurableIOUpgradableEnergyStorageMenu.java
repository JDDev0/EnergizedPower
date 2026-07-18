package me.jddev0.ep.screen.base;

import me.jddev0.ep.block.entity.base.EnergyStorageBlockEntity;
import me.jddev0.ep.inventory.data.IOConfigurationViewContainerData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.IConfigurableIOMachine;
import me.jddev0.ep.machine.configuration.IOConfiguration;
import me.jddev0.ep.machine.configuration.SlotGroup;
import me.jddev0.ep.machine.configuration.SlotType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class ConfigurableIOUpgradableEnergyStorageMenu<T extends EnergyStorageBlockEntity<?> & IConfigurableIOMachine>
        extends UpgradableEnergyStorageMenu<T>
        implements IConfigurableIOMenu {
    protected final IOConfigurationViewContainerData ioConfigurationViewContainerData;

    public ConfigurableIOUpgradableEnergyStorageMenu(@Nullable MenuType<?> menuType, int id, Inventory playerInventory,
                                                     BlockEntity blockEntity, Block blockType,
                                                     UpgradeModuleInventory upgradeModuleInventory, int upgradeModuleCount) {
        super(menuType, id, playerInventory, blockEntity, blockType, upgradeModuleInventory, upgradeModuleCount);

        this.ioConfigurationViewContainerData = new IOConfigurationViewContainerData();

        addDataSlots(ioConfigurationViewContainerData);
        verifyAndFixDefaultSlotType();
    }

    public ConfigurableIOUpgradableEnergyStorageMenu(@Nullable MenuType<?> menuType, int id, Inventory playerInventory,
                                                     BlockEntity blockEntity, Block blockType,
                                                     int playerInventoryX, int playerInventoryY,
                                                     UpgradeModuleInventory upgradeModuleInventory, int upgradeModuleCount) {
        super(menuType, id, playerInventory, blockEntity, blockType, playerInventoryX, playerInventoryY, upgradeModuleInventory, upgradeModuleCount);

        this.ioConfigurationViewContainerData = new IOConfigurationViewContainerData();

        addDataSlots(ioConfigurationViewContainerData);
        verifyAndFixDefaultSlotType();
    }

    public void verifyAndFixDefaultSlotType() {
        //Support machines without slots: Prevent crash by going to first supported SlotType

        List<SlotType> supportedSlotTypeList = Arrays.asList(blockEntity.getSupportedSlotTypes());

        SlotType slotType = ioConfigurationViewContainerData.getSlotType();
        while(!supportedSlotTypeList.contains(slotType)) {
            slotType = SlotType.fromIndex(slotType.ordinal() + 1);
        }

        ioConfigurationViewContainerData.setSlotType(slotType);
    }

    @Override
    public boolean isInIOConfigurationView() {
        return ioConfigurationViewContainerData.isInIOConfigurationView();
    }

    @Override
    public SlotType getSlotType() {
        return ioConfigurationViewContainerData.getSlotType();
    }

    @Override
    public List<SlotGroup> getSlotGroups() {
        return blockEntity.getSlotGroups(getSlotType());
    }

    @Override
    public IOConfiguration getIOConfiguration() {
        return blockEntity.getIOConfiguration(getSlotType());
    }

    @Override
    public boolean clickMenuButton(Player player, int index) {
        if(index == 0) {
            //Upgrade view will be toggled -> force close IO Configuration view (call before super, because brodcastChanges() is called within super method)
            ioConfigurationViewContainerData.setInIOConfigurationView(false);
        }

        if(super.clickMenuButton(player, index))
            return true;

        if(index == 1) {
            ioConfigurationViewContainerData.toggleInIOConfigurationView();

            //IO Configuration view was toggled -> force close upgrade Configuration view
            upgradeViewContainerData.setInUpgradeModuleView(false);

            broadcastChanges();

            return true;
        }else if(index == 2) {
            SlotType[] supportedSlotTypes = blockEntity.getSupportedSlotTypes();
            if(supportedSlotTypes.length > 1) {
                List<SlotType> supportedSlotTypeList = Arrays.asList(supportedSlotTypes);

                SlotType slotType = ioConfigurationViewContainerData.getSlotType();
                do {
                    slotType = SlotType.fromIndex(slotType.ordinal() + 1);
                }while(!supportedSlotTypeList.contains(slotType));

                ioConfigurationViewContainerData.setSlotType(slotType);
            }

            broadcastChanges();
        }

        return false;
    }

    @Override
    protected void addPlayerInventorySlots(Inventory playerInventory, int x, int y) {
        //Player Inventory
        for(int i = 0;i < 3;i++)
            for(int j = 0;j < 9;j++)
                addSlot(new Slot(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18) {
                    @Override
                    public boolean isActive() {
                        return super.isActive() && !isInIOConfigurationView();
                    }
                });

        //Player Hotbar
        for(int i = 0;i < 9;i++)
            addSlot(new Slot(playerInventory, i, x + i * 18, y + 58) {
                @Override
                public boolean isActive() {
                    return super.isActive() && !isInIOConfigurationView();
                }
            });
    }
}
