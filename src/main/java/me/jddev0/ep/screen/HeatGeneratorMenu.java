package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.HeatGeneratorBlockEntity;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.UpgradeModuleViewData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HeatGeneratorMenu extends AbstractContainerMenu implements EnergyStorageMenu {
    private final HeatGeneratorBlockEntity blockEntity;
    private final Level level;
    private final UpgradeModuleViewData upgradeModuleViewData;

    public HeatGeneratorMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY
        ));
    }

    public HeatGeneratorMenu(int id, Inventory inv, BlockEntity blockEntity, UpgradeModuleInventory upgradeModuleInventory) {
        super(ModMenuTypes.HEAT_GENERATOR_MENU.get(), id);

        checkContainerSize(upgradeModuleInventory, 1);
        this.blockEntity = (HeatGeneratorBlockEntity)blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addSlot(new UpgradeModuleSlot(upgradeModuleInventory, 0, 80, 35, this::isInUpgradeModuleView));

        upgradeModuleViewData = new UpgradeModuleViewData();
        addDataSlots(upgradeModuleViewData);
    }

    @Override
    public boolean isInUpgradeModuleView() {
        return upgradeModuleViewData.isInUpgradeModuleView();
    }

    @Override
    public boolean clickMenuButton(Player player, int index) {
        if(index == 0) {
            upgradeModuleViewData.toggleInUpgradeModuleView();

            broadcastChanges();
        }

        return false;
    }

    @Override
    public int getEnergy() {
        return blockEntity.getEnergy();
    }

    @Override
    public int getCapacity() {
        return blockEntity.getCapacity();
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
            if(!moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1) {
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

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.HEAT_GENERATOR.get());
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
