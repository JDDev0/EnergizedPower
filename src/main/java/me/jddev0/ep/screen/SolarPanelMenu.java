package me.jddev0.ep.screen;

import me.jddev0.ep.block.SolarPanelBlock;
import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SolarPanelMenu extends AbstractContainerMenu implements EnergyStorageMenu {
    private final SolarPanelBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public static MenuType<SolarPanelMenu> getMenuTypeFromTier(SolarPanelBlock.Tier tier) {
        return switch(tier) {
            case TIER_1 -> ModMenuTypes.SOLAR_PANEL_MENU_1.get();
            case TIER_2 -> ModMenuTypes.SOLAR_PANEL_MENU_2.get();
            case TIER_3 -> ModMenuTypes.SOLAR_PANEL_MENU_3.get();
            case TIER_4 -> ModMenuTypes.SOLAR_PANEL_MENU_4.get();
            case TIER_5 -> ModMenuTypes.SOLAR_PANEL_MENU_5.get();
        };
    }

    public SolarPanelMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level().getBlockEntity(buffer.readBlockPos()), new SimpleContainerData(4));
    }

    public SolarPanelMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(getMenuTypeFromTier(((SolarPanelBlockEntity)blockEntity).getTier()), id);

        checkContainerSize(inv, 0);
        checkContainerDataCount(data, 4);
        this.blockEntity = (SolarPanelBlockEntity)blockEntity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(this.data);
    }

    public SolarPanelBlock.Tier getTier() {
        return blockEntity.getTier();
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
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, SolarPanelBlock.getBlockFromTier(getTier()));
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
