package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.inventory.PatternResultSlot;
import me.jddev0.ep.inventory.PatternSlot;
import me.jddev0.ep.util.ByteUtils;
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

public class AutoCrafterMenu extends ScreenHandler implements EnergyStorageConsumerIndicatorBarMenu {
    private final AutoCrafterBlockEntity blockEntity;
    private final Inventory inv;
    private final World level;
    private final PropertyDelegate data;

    private final Inventory patternSlots;

    private final Inventory patternResultSlots;

    public AutoCrafterMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv, new SimpleInventory(18),
                new SimpleInventory(9), new SimpleInventory(1), new ArrayPropertyDelegate(17));
    }

    public AutoCrafterMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv, Inventory patternSlots,
                           Inventory patternResultSlots, PropertyDelegate data) {
        super(ModMenuTypes.AUTO_CRAFTER_MENU, id);

        this.blockEntity = (AutoCrafterBlockEntity)blockEntity;

        this.patternSlots = patternSlots;
        this.patternResultSlots = patternResultSlots;

        this.inv = inv;
        checkSize(this.inv, 18);
        checkDataCount(data, 17);
        this.level = playerInventory.player.getWorld();
        this.inv.onOpen(playerInventory.player);
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addSlot(new ConstraintInsertSlot(this.inv, 0, 8, 75));
        addSlot(new ConstraintInsertSlot(this.inv, 1, 26, 75));
        addSlot(new ConstraintInsertSlot(this.inv, 2, 44, 75));
        addSlot(new ConstraintInsertSlot(this.inv, 3, 62, 75));
        addSlot(new ConstraintInsertSlot(this.inv, 4, 80, 75));
        addSlot(new ConstraintInsertSlot(this.inv, 5, 98, 75));
        addSlot(new ConstraintInsertSlot(this.inv, 6, 116, 75));
        addSlot(new ConstraintInsertSlot(this.inv, 7, 134, 75));
        addSlot(new ConstraintInsertSlot(this.inv, 8, 152, 75));
        addSlot(new ConstraintInsertSlot(this.inv, 9, 8, 93));
        addSlot(new ConstraintInsertSlot(this.inv, 10, 26, 93));
        addSlot(new ConstraintInsertSlot(this.inv, 11, 44, 93));
        addSlot(new ConstraintInsertSlot(this.inv, 12, 62, 93));
        addSlot(new ConstraintInsertSlot(this.inv, 13, 80, 93));
        addSlot(new ConstraintInsertSlot(this.inv, 14, 98, 93));
        addSlot(new ConstraintInsertSlot(this.inv, 15, 116, 93));
        addSlot(new ConstraintInsertSlot(this.inv, 16, 134, 93));
        addSlot(new ConstraintInsertSlot(this.inv, 17, 152, 93));

        for(int i = 0;i < 3;i++)
            for(int j = 0;j < 3;j++)
                addSlot(new PatternSlot(patternSlots, j + i * 3, 30 + j * 18, 17 + i * 18, () -> true));

        addSlot(new PatternResultSlot(patternResultSlots, 0, 124, 35, () -> true));

        addProperties(this.data);
    }

    public Inventory getPatternSlots() {
        return patternSlots;
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
    public long getEnergyIndicatorBarValue() {
        return ByteUtils.from2ByteChunks((short)data.get(10), (short)data.get(11), (short)data.get(12), (short)data.get(13));
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive() {
        return data.get(0) > 0;
    }

    public boolean isCrafting() {
        return data.get(0) > 0 && data.get(14) == 1;
    }

    public int getScaledProgressArrowSize() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        int progressArrowSize = 24;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    public boolean isIgnoreNBT() {
        return data.get(15) != 0;
    }

    public boolean isSecondaryExtractMode() {
        return data.get(16) != 0;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into tile inventory
            //"+ 18": Ignore 3x3 crafting grid and result slot
            if(!insertItem(sourceItem, 4 * 9 + 3, 4 * 9 + 18, false)) {
                //"+3" instead of nothing: Do not allow adding to first 3 output item only slot
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 18) {
            //Tile inventory slot -> Merge into player inventory
            if(!insertItem(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 18 + 3*3 + 1) {
            return ItemStack.EMPTY;
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
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, ModBlocks.AUTO_CRAFTER);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for(int i = 0;i < 3;i++) {
            for(int j = 0;j < 9;j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 124 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for(int i = 0;i < 9;i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 182));
        }
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
