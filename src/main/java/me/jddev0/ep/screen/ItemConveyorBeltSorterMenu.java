package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.ItemConveyorBeltSorterBlockEntity;
import me.jddev0.ep.inventory.PatternSlot;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class ItemConveyorBeltSorterMenu extends ScreenHandler {
    private final ItemConveyorBeltSorterBlockEntity blockEntity;
    private final World level;
    private final PropertyDelegate data;

    private final Inventory patternSlots;

    public ItemConveyorBeltSorterMenu(int id, PlayerInventory inv, PacketByteBuf buffer) {
        this(id, inv, inv.player.getWorld().getBlockEntity(buffer.readBlockPos()), new SimpleInventory(15), new ArrayPropertyDelegate(9));
    }

    public ItemConveyorBeltSorterMenu(int id, PlayerInventory playerInventory, BlockEntity blockEntity, Inventory patternSlots, PropertyDelegate data) {
        super(ModMenuTypes.ITEM_CONVEYOR_BELT_SORTER_MENU, id);

        this.patternSlots = patternSlots;

        checkDataCount(data, 9);
        this.blockEntity = (ItemConveyorBeltSorterBlockEntity)blockEntity;
        this.level = playerInventory.player.getWorld();
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        for(int i = 0;i < 3;i++)
            for(int j = 0;j < 5;j++)
                addSlot(new PatternSlot(patternSlots, j + i * 5, 44 + j * 18, 17 + i * 18, () -> true));

        addProperties(this.data);
    }

    public Inventory getPatternSlots() {
        return patternSlots;
    }

    public boolean isOutputBeltConnected(int index) {
        return data.get(index) != 0;
    }

    public boolean isWhitelist(int index) {
        return data.get(index + 3) != 0;
    }

    public boolean isIgnoreNBT(int index) {
        return data.get(index + 6) != 0;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, ModBlocks.ITEM_CONVEYOR_BELT_SORTER);
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
