package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.AdvancedPoweredFurnaceBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.UpgradeModuleViewContainerData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.RecipeUtils;
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

public class AdvancedPoweredFurnaceMenu extends ScreenHandler implements EnergyStorageConsumerIndicatorBarMenu {
    private final AdvancedPoweredFurnaceBlockEntity blockEntity;
    private final Inventory inv;
    private final World level;
    private final PropertyDelegate data;
    private final UpgradeModuleViewContainerData upgradeModuleViewContainerData;

    public AdvancedPoweredFurnaceMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv, (AdvancedPoweredFurnaceBlockEntity)inv.player.getWorld().getBlockEntity(pos));
    }
    private AdvancedPoweredFurnaceMenu(int id, PlayerInventory inv, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        this(id, blockEntity, inv, new SimpleInventory(6) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2 -> RecipeUtils.isIngredientOfAny(inv.player.getWorld(),
                            blockEntity.getRecipeForFurnaceModeUpgrade(), stack);
                    case 3, 4, 5 -> false;
                    default -> super.isValid(slot, stack);
                };
            }
        }, new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE
        ), new ArrayPropertyDelegate(29));
    }

    public AdvancedPoweredFurnaceMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                                      UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data) {
        super(ModMenuTypes.ADVANCED_POWERED_FURNACE_MENU, id);

        this.blockEntity = (AdvancedPoweredFurnaceBlockEntity)blockEntity;

        this.inv = inv;
        checkSize(this.inv, 6);
        checkSize(upgradeModuleInventory, 4);
        checkDataCount(data, 29);
        this.level = playerInventory.player.getWorld();
        this.inv.onOpen(playerInventory.player);
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addSlot(new ConstraintInsertSlot(this.inv, 0, 44, 17) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(this.inv, 1, 98, 17) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(this.inv, 2, 152, 17) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(this.inv, 3, 44, 53) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(this.inv, 4, 98, 53) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(this.inv, 5, 152, 53) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });

        for(int i = 0;i < upgradeModuleInventory.size();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 53 + i * 18, 35, this::isInUpgradeModuleView));

        addProperties(this.data);

        upgradeModuleViewContainerData = new UpgradeModuleViewContainerData();
        addProperties(upgradeModuleViewContainerData);
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
        long energyIndicatorBarValueSum = -1;

        for(int i = 0;i < 12;i += 4) {
            long value = ByteUtils.from2ByteChunks((short)data.get(i), (short)data.get(i + 1),
                    (short)data.get(i + 2), (short)data.get(i + 3));

            //Prevent overflow
            if(Math.max(0, energyIndicatorBarValueSum) + Math.max(0, value) < 0)
                return Long.MAX_VALUE;

            if(value > -1) {
                if(energyIndicatorBarValueSum == -1)
                    energyIndicatorBarValueSum++;

                energyIndicatorBarValueSum += value;
            }
        }

        return energyIndicatorBarValueSum;
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive(int index) {
        return ByteUtils.from2ByteChunks((short)data.get(12 + 4 * index), (short)data.get(13 + 4 * index)) > 0;
    }

    public boolean isCrafting(int index) {
        return ByteUtils.from2ByteChunks((short)data.get(14 + 4 * index), (short)data.get(15 + 4 * index)) > 0 && data.get(24 + index) == 1;
    }

    public int getScaledProgressArrowSize(int index) {
        int progress = ByteUtils.from2ByteChunks((short)data.get(12 + 4 * index), (short)data.get(13 + 4 * index));
        int maxProgress = ByteUtils.from2ByteChunks((short)data.get(14 + 4 * index), (short)data.get(15 + 4 * index));
        int progressArrowSize = 17;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    public RedstoneMode getRedstoneMode() {
        return RedstoneMode.fromIndex(data.get(27));
    }

    public ComparatorMode getComparatorMode() {
        return ComparatorMode.fromIndex(data.get(28));
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
            if(!insertItem(sourceItem, 4 * 9 + 6, 4 * 9 + 6 + 4, false) &&
                    !insertItem(sourceItem, 4 * 9, 4 * 9 + 3, false)) {
                //"+3" instead of "+6": Do not allow adding to output slots
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 6 + 4) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
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
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, ModBlocks.ADVANCED_POWERED_FURNACE);
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
