package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.MetalPressBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.recipe.MetalPressRecipe;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class MetalPressMenu extends ScreenHandler implements EnergyStorageConsumerIndicatorBarMenu {
    private final MetalPressBlockEntity blockEntity;
    private final Inventory inv;
    private final World level;
    private final PropertyDelegate data;

    public MetalPressMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv, new SimpleInventory(3) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> inv.player.getWorld().getRecipeManager().listAllOfType(MetalPressRecipe.Type.INSTANCE).stream().
                            map(RecipeEntry::value).map(MetalPressRecipe::getInput).anyMatch(ingredient -> ingredient.test(stack));
                    case 1 -> stack.isIn(EnergizedPowerItemTags.METAL_PRESS_MOLDS) &&
                            (getStack(1).isEmpty() || stack.getCount() == 1);
                    case 2 -> false;
                    default -> super.isValid(slot, stack);
                };
            }
        }, new ArrayPropertyDelegate(10));
    }

    public MetalPressMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                          PropertyDelegate data) {
        super(ModMenuTypes.METAL_PRESS_MENU, id);

        this.blockEntity = (MetalPressBlockEntity)blockEntity;

        this.inv = inv;
        checkSize(this.inv, 3);
        checkDataCount(data, 10);
        this.level = playerInventory.player.getWorld();
        this.inv.onOpen(playerInventory.player);
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addSlot(new ConstraintInsertSlot(this.inv, 0, 48, 35));
        addSlot(new ConstraintInsertSlot(this.inv, 1, 84, 23) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        addSlot(new ConstraintInsertSlot(this.inv, 2, 124, 35));

        addProperties(this.data);
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
        return ByteUtils.from2ByteChunks((short)data.get(4), (short)data.get(5), (short)data.get(6), (short)data.get(7));
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1)) > 0;
    }

    public boolean isCrafting() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1)) > 0 && data.get(6) == 1;
    }

    public int getScaledProgressArrowSize() {
        int progress = ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1));
        int maxProgress = ByteUtils.from2ByteChunks((short)data.get(2), (short)data.get(3));
        int progressArrowSize = 24;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    public RedstoneMode getRedstoneMode() {
        return RedstoneMode.fromIndex(data.get(9));
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
            //Input slot
            //Press mold slot [Max Count 1]
            if(!insertItem(sourceItem, 4 * 9, 4 * 9 + 1, false) &&
                    (slots.get(4 * 9 + 1).hasStack() || !insertItem(sourceItem, 4 * 9 + 1, 4 * 9 + 2, false))) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 3) {
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
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, ModBlocks.METAL_PRESS);
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
