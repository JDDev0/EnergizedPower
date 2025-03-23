package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.CreativeBatteryBoxBlockEntity;
import me.jddev0.ep.inventory.data.SimpleBooleanValueContainerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CreativeBatteryBoxMenu extends AbstractContainerMenu {
    private final CreativeBatteryBoxBlockEntity blockEntity;
    private final Level level;

    private final SimpleBooleanValueContainerData energyProductionData = new SimpleBooleanValueContainerData();
    private final SimpleBooleanValueContainerData energyConsumptionData = new SimpleBooleanValueContainerData();

    public CreativeBatteryBoxMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), null);
    }

    public CreativeBatteryBoxMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(EPMenuTypes.CREATIVE_BATTERY_BOX_MENU.get(), id);

        this.blockEntity = (CreativeBatteryBoxBlockEntity)blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        if(data == null) {
            addDataSlots(energyProductionData);
            addDataSlots(energyConsumptionData);
        }else {
            addDataSlots(data);
        }
    }

    public boolean isEnergyProduction() {
        return energyProductionData.getValue();
    }

    public boolean isEnergyConsumption() {
        return energyConsumptionData.getValue();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, EPBlocks.CREATIVE_BATTERY_BOX.get());
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
