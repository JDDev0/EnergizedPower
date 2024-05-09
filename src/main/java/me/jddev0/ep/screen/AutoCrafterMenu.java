package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.inventory.PatternResultSlot;
import me.jddev0.ep.inventory.PatternSlot;
import me.jddev0.ep.inventory.*;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AutoCrafterMenu extends ScreenHandler implements EnergyStorageConsumerIndicatorBarMenu {
    private final AutoCrafterBlockEntity blockEntity;
    private final Inventory inv;
    private final World level;
    private final PropertyDelegate data;
    private final UpgradeModuleViewContainerData upgradeModuleViewContainerData;

    private final Inventory patternSlots;

    private final Inventory patternResultSlots;

    public AutoCrafterMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv.player.getWorld().getBlockEntity(pos), inv, new SimpleInventory(18) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return super.isValid(slot, stack) && slot >= 3;
            }
        }, new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), new SimpleInventory(9), new SimpleInventory(1), new ArrayPropertyDelegate(13));
    }

    public AutoCrafterMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                           UpgradeModuleInventory upgradeModuleInventory, Inventory patternSlots,
                           Inventory patternResultSlots, PropertyDelegate data) {
        super(ModMenuTypes.AUTO_CRAFTER_MENU, id);

        this.blockEntity = (AutoCrafterBlockEntity)blockEntity;

        this.patternSlots = patternSlots;
        this.patternResultSlots = patternResultSlots;

        this.inv = inv;
        checkSize(this.inv, 18);
        checkSize(upgradeModuleInventory, 3);
        checkDataCount(data, 13);
        this.level = playerInventory.player.getWorld();
        this.inv.onOpen(playerInventory.player);
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        for(int i = 0;i < 2;i++)
            for(int j = 0;j < 9;j++)
                addSlot(new ConstraintInsertSlot(this.inv, 9 * i + j, 8 + 18 * j, 75 + 18 * i));

        for(int i = 0;i < 3;i++)
            for(int j = 0;j < 3;j++)
                addSlot(new PatternSlot(patternSlots, j + i * 3, 30 + j * 18, 17 + i * 18, () -> true) {
                    @Override
                    public boolean isEnabled() {
                        return super.isEnabled() && !isInUpgradeModuleView();
                    }
                });

        addSlot(new PatternResultSlot(patternResultSlots, 0, 124, 35, () -> true) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });

        for(int i = 0;i < upgradeModuleInventory.size();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 62 + i * 18, 35, this::isInUpgradeModuleView));

        addProperties(this.data);

        upgradeModuleViewContainerData = new UpgradeModuleViewContainerData();
        addProperties(upgradeModuleViewContainerData);
    }

    public Inventory getPatternSlots() {
        return patternSlots;
    }

    @Override
    public boolean isInUpgradeModuleView() {
        return upgradeModuleViewContainerData.isInUpgradeModuleView();
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int index) {
        if(index == 0) {
            upgradeModuleViewContainerData.toggleInUpgradeModuleView();

            sendContentUpdates();
        }

        return false;
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
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1)) > 0 && data.get(8) == 1;
    }

    public int getScaledProgressArrowSize() {
        int progress = ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1));
        int maxProgress = ByteUtils.from2ByteChunks((short)data.get(2), (short)data.get(3));
        int progressArrowSize = 24;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    public boolean isIgnoreNBT() {
        return data.get(9) != 0;
    }

    public boolean isSecondaryExtractMode() {
        return data.get(10) != 0;
    }

    public RedstoneMode getRedstoneMode() {
        return RedstoneMode.fromIndex(data.get(11));
    }

    public ComparatorMode getComparatorMode() {
        return ComparatorMode.fromIndex(data.get(12));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory, Merge into tile inventory
            //"+ 18": Ignore 3x3 crafting grid and result slot
            if(!insertItem(sourceItem, 4 * 9 + 18 + 3*3 + 1, 4 * 9 + 18 + 3*3 + 1 + 3, false) &&
                    !insertItem(sourceItem, 4 * 9 + 3, 4 * 9 + 18, false)) {
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
        }else if(index < 4 * 9 + 18 + 3*3 + 1 + 3) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
            if(!insertItem(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else{
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
