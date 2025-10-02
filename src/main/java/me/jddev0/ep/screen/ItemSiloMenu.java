package me.jddev0.ep.screen;

import me.jddev0.ep.block.entity.ItemSiloBlockEntity;
import me.jddev0.ep.inventory.SingleItemStackHandler;
import me.jddev0.ep.inventory.ViewOnlySlot;
import me.jddev0.ep.inventory.data.SimpleLongValueContainerData;
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

public class ItemSiloMenu extends ScreenHandler {
    private final SimpleLongValueContainerData countData = new SimpleLongValueContainerData();
    private final SimpleLongValueContainerData maxCountData = new SimpleLongValueContainerData();

    private final ItemSiloBlockEntity blockEntity;
    private final World level;

    public ItemSiloMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getEntityWorld().getBlockEntity(pos), inv, new SingleItemStackHandler(
                ((ItemSiloBlockEntity)inv.player.getEntityWorld().getBlockEntity(pos)).getTier().getItemSiloCapacity()), null);
    }

    public ItemSiloMenu(int id, BlockEntity blockEntity, PlayerInventory inv, SingleItemStackHandler itemStackHandler, PropertyDelegate data) {
        super(((ItemSiloBlockEntity)blockEntity).getTier().getMenuTypeFromTier(), id);

        this.blockEntity = (ItemSiloBlockEntity)blockEntity;
        this.level = inv.player.getEntityWorld();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addSlot(new ViewOnlySlot(itemStackHandler, 0, 80, 35));

        if(data == null) {
            addProperties(countData);
            addProperties(maxCountData);
        }else {
            addProperties(data);
        }
    }

    public long getCount() {
        return countData.getValue();
    }

    public long getMaxCount() {
        return maxCountData.getValue();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, blockEntity.getTier().getBlockFromTier());
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
