package me.jddev0.ep.screen;

import me.jddev0.ep.block.SolarPanelBlock;
import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.energy.EnergyStorageMenuPacketUpdate;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class SolarPanelMenu extends ScreenHandler implements EnergyStorageMenu, EnergyStorageMenuPacketUpdate {
    private final SolarPanelBlockEntity blockEntity;
    private final Inventory inv;
    private final World level;
    private final PropertyDelegate data;

    public static ScreenHandlerType<SolarPanelMenu> getMenuTypeFromTier(SolarPanelBlock.Tier tier) {
        return switch(tier) {
            case TIER_1 -> ModMenuTypes.SOLAR_PANEL_MENU_1;
            case TIER_2 -> ModMenuTypes.SOLAR_PANEL_MENU_2;
            case TIER_3 -> ModMenuTypes.SOLAR_PANEL_MENU_3;
            case TIER_4 -> ModMenuTypes.SOLAR_PANEL_MENU_4;
            case TIER_5 -> ModMenuTypes.SOLAR_PANEL_MENU_5;
        };
    }

    public SolarPanelMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv, new SimpleInventory(0),
                new ArrayPropertyDelegate(8));
    }

    public SolarPanelMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv, PropertyDelegate data) {
        super(getMenuTypeFromTier(((SolarPanelBlockEntity)blockEntity).getTier()), id);

        this.blockEntity = (SolarPanelBlockEntity)blockEntity;

        this.inv = inv;
        checkSize(this.inv, 0);
        checkDataCount(data, 8);
        this.level = playerInventory.player.world;
        this.inv.onOpen(playerInventory.player);
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(this.data);
    }

    public SolarPanelBlock.Tier getTier() {
        return blockEntity.getTier();
    }

    @Override
    public long getEnergy() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1), (short)data.get(2), (short)data.get(3));
    }

    @Override
    public long getCapacity() {
        return ByteUtils.from2ByteChunks((short)data.get(4), (short)data.get(5), (short)data.get(6), (short)data.get(7));
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
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

    @Override
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public void setEnergy(long energy) {
        for(int i = 0;i < 4;i++)
            data.set(i, ByteUtils.get2Bytes(energy, i));
    }

    @Override
    public void setCapacity(long capacity) {
        for(int i = 0;i < 4;i++)
            data.set(i + 4, ByteUtils.get2Bytes(capacity, i));
    }
}
