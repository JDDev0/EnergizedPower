package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AlloyFurnaceBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.inventory.data.SimpleProgressValueContainerData;
import me.jddev0.ep.util.RecipeUtils;
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

public class AlloyFurnaceMenu extends ScreenHandler {
    private final AlloyFurnaceBlockEntity blockEntity;
    private final World level;

    private final SimpleProgressValueContainerData progressData = new SimpleProgressValueContainerData();
    private final SimpleProgressValueContainerData maxProgressData = new SimpleProgressValueContainerData();
    private final SimpleProgressValueContainerData litDurationData = new SimpleProgressValueContainerData();
    private final SimpleProgressValueContainerData maxLitDurationData = new SimpleProgressValueContainerData();

    public AlloyFurnaceMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getEntityWorld().getBlockEntity(pos), inv, new SimpleInventory(6) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2 -> RecipeUtils.isIngredientOfAny(((AlloyFurnaceBlockEntity)inv.player.getEntityWorld().
                            getBlockEntity(pos)).getIngredientsOfRecipes(), stack);
                    case 3 -> inv.player.getEntityWorld().getFuelRegistry().getFuelTicks(stack) > 0;
                    case 4, 5 -> false;
                    default -> super.isValid(slot, stack);
                };
            }
        }, null);
    }

    public AlloyFurnaceMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                            PropertyDelegate data) {
        super(EPMenuTypes.ALLOY_FURNACE_MENU, id);

        checkSize(inv, 6);
        this.blockEntity = (AlloyFurnaceBlockEntity)blockEntity;
        this.level = playerInventory.player.getEntityWorld();

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addSlot(new ConstraintInsertSlot(inv, 0, 14, 20));
        addSlot(new ConstraintInsertSlot(inv, 1, 35, 17));
        addSlot(new ConstraintInsertSlot(inv, 2, 56, 20));
        addSlot(new ConstraintInsertSlot(inv, 3, 35, 53));
        addSlot(new ConstraintInsertSlot(inv, 4, 116, 35));
        addSlot(new ConstraintInsertSlot(inv, 5, 143, 35));

        if(data == null) {
            addProperties(progressData);
            addProperties(maxProgressData);
            addProperties(litDurationData);
            addProperties(maxLitDurationData);
        }else {
            addProperties(data);
        }
    }

    public boolean isCraftingActive() {
        return progressData.getValue() > 0;
    }

    public int getScaledProgressArrowSize() {
        int progress = progressData.getValue();
        int maxProgress = maxProgressData.getValue();
        int progressArrowSize = 24;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    public boolean isBurningFuel() {
        return litDurationData.getValue() > 0;
    }

    public int getScaledProgressFlameSize() {
        int progress = litDurationData.getValue();
        int maxProgress = maxLitDurationData.getValue();
        int progressFlameSize = 14;

        return (maxProgress == 0 || progress == 0)?0:(progress * progressFlameSize / maxProgress);
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
            if(!insertItem(sourceItem, 4 * 9, 4 * 9 + 4, false)) {
                //"+4" instead of "+6": Do not allow adding to output slot
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 6) {
            //Tile inventory slot -> Merge into player inventory
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
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, EPBlocks.ALLOY_FURNACE);
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
