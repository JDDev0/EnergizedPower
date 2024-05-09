package me.jddev0.ep.screen;

import me.jddev0.ep.block.SolarPanelBlock;
import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.UpgradeModuleViewContainerData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class SolarPanelMenu extends AbstractEnergizedPowerScreenHandler
        implements EnergyStorageMenu {
    private final SolarPanelBlockEntity blockEntity;
    private final World level;
    private final UpgradeModuleViewContainerData upgradeModuleViewContainerData;

    public static ScreenHandlerType<SolarPanelMenu> getMenuTypeFromTier(SolarPanelBlock.Tier tier) {
        return switch(tier) {
            case TIER_1 -> ModMenuTypes.SOLAR_PANEL_MENU_1;
            case TIER_2 -> ModMenuTypes.SOLAR_PANEL_MENU_2;
            case TIER_3 -> ModMenuTypes.SOLAR_PANEL_MENU_3;
            case TIER_4 -> ModMenuTypes.SOLAR_PANEL_MENU_4;
            case TIER_5 -> ModMenuTypes.SOLAR_PANEL_MENU_5;
            case TIER_6 -> ModMenuTypes.SOLAR_PANEL_MENU_6;
        };
    }

    public SolarPanelMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv, new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.MOON_LIGHT
        ));
    }

    public SolarPanelMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory,
                          UpgradeModuleInventory upgradeModuleInventory) {
        super(getMenuTypeFromTier(((SolarPanelBlockEntity)blockEntity).getTier()), id);

        this.blockEntity = (SolarPanelBlockEntity)blockEntity;

        checkSize(upgradeModuleInventory, 2);
        this.level = playerInventory.player.getWorld();

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        for(int i = 0;i < upgradeModuleInventory.size();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 71 + i * 18, 35, this::isInUpgradeModuleView));

        upgradeModuleViewContainerData = new UpgradeModuleViewContainerData();
        addProperties(upgradeModuleViewContainerData);
    }

    public SolarPanelBlock.Tier getTier() {
        return blockEntity.getTier();
    }

    @Override
    public boolean isInUpgradeModuleView() {
        return upgradeModuleViewContainerData.isInUpgradeModuleView();
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int index) {
        if(index == 0) {
            upgradeModuleViewContainerData.toggleInUpgradeModuleView();

            sendContentUpdates();
        }

        return false;
    }

    @Override
    public long getEnergy() {
        return blockEntity.getEnergy();
    }

    @Override
    public long getCapacity() {
        return blockEntity.getCapacity();
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
            if(!insertMaxCount1Item(sourceItem, 4 * 9, 4 * 9 + 2, false)) {
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

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, SolarPanelBlock.getBlockFromTier(getTier()));
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for(int i = 0;i < 3;i++) {
            for(int j = 0;j < 9;j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for(int i = 0;i < 9;i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
