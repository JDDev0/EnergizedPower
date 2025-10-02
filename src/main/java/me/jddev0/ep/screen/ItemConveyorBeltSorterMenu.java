package me.jddev0.ep.screen;

import me.jddev0.ep.block.entity.ItemConveyorBeltSorterBlockEntity;
import me.jddev0.ep.inventory.PatternSlot;
import me.jddev0.ep.inventory.data.SimpleBooleanValueContainerData;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemConveyorBeltSorterMenu extends ScreenHandler {
    private final ItemConveyorBeltSorterBlockEntity blockEntity;
    private final World level;

    private final Inventory patternSlots;

    private final SimpleBooleanValueContainerData[] outputBeltConnectedData = new SimpleBooleanValueContainerData[] {
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData()
    };
    private final SimpleBooleanValueContainerData[] whitelistData = new SimpleBooleanValueContainerData[] {
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData()
    };
    private final SimpleBooleanValueContainerData[] ignoreNBTData = new SimpleBooleanValueContainerData[] {
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData()
    };

    public ItemConveyorBeltSorterMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv, inv.player.getEntityWorld().getBlockEntity(pos), new SimpleInventory(15), null);
    }

    public ItemConveyorBeltSorterMenu(int id, PlayerInventory playerInventory, BlockEntity blockEntity, Inventory patternSlots, PropertyDelegate data) {
        super(((ItemConveyorBeltSorterBlockEntity)blockEntity).getTier().getItemConveyorBeltSorterMenuTypeFromTier(), id);

        this.patternSlots = patternSlots;

        this.blockEntity = (ItemConveyorBeltSorterBlockEntity)blockEntity;
        this.level = playerInventory.player.getEntityWorld();

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        for(int i = 0;i < 3;i++)
            for(int j = 0;j < 5;j++)
                addSlot(new PatternSlot(patternSlots, j + i * 5, 44 + j * 18, 17 + i * 18, () -> true));

        if(data == null) {
            addProperties(outputBeltConnectedData[0]);
            addProperties(outputBeltConnectedData[1]);
            addProperties(outputBeltConnectedData[2]);
            addProperties(whitelistData[0]);
            addProperties(whitelistData[1]);
            addProperties(whitelistData[2]);
            addProperties(ignoreNBTData[0]);
            addProperties(ignoreNBTData[1]);
            addProperties(ignoreNBTData[2]);
        }else {
            addProperties(data);
        }
    }

    public Inventory getPatternSlots() {
        return patternSlots;
    }

    public boolean isOutputBeltConnected(int index) {
        return outputBeltConnectedData[index].getValue();
    }

    public boolean isWhitelist(int index) {
        return whitelistData[index].getValue();
    }

    public boolean isIgnoreNBT(int index) {
        return ignoreNBTData[index].getValue();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, blockEntity.getTier().getItemConveyorBeltSorterBlockFromTier());
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
