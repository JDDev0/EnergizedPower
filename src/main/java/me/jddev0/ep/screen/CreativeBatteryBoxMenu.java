package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.CreativeBatteryBoxBlockEntity;
import me.jddev0.ep.inventory.data.SimpleBooleanValueContainerData;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CreativeBatteryBoxMenu extends ScreenHandler {
    private final CreativeBatteryBoxBlockEntity blockEntity;
    private final World level;

    private final SimpleBooleanValueContainerData energyProductionData = new SimpleBooleanValueContainerData();
    private final SimpleBooleanValueContainerData energyConsumptionData = new SimpleBooleanValueContainerData();

    public CreativeBatteryBoxMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getEntityWorld().getBlockEntity(pos), inv, null);
    }

    public CreativeBatteryBoxMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, PropertyDelegate data) {
        super(EPMenuTypes.CREATIVE_BATTERY_BOX_MENU, id);

        this.blockEntity = (CreativeBatteryBoxBlockEntity)blockEntity;
        this.level = playerInventory.player.getEntityWorld();

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        if(data == null) {
            addProperties(energyProductionData);
            addProperties(energyConsumptionData);
        }else {
            addProperties(data);
        }
    }

    public boolean isEnergyProduction() {
        return energyProductionData.getValue();
    }

    public boolean isEnergyConsumption() {
        return energyConsumptionData.getValue();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, EPBlocks.CREATIVE_BATTERY_BOX);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for(int i = 0;i < 3;i++) {
            for(int j = 0;j < 9;j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for(int i = 0;i < 9;i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
