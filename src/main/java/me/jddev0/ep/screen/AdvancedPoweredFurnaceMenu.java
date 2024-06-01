package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.AdvancedPoweredFurnaceBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.IConfigurableMenu;
import me.jddev0.ep.screen.base.IEnergyStorageConsumerIndicatorBarMenu;
import me.jddev0.ep.screen.base.UpgradableEnergyStorageMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class AdvancedPoweredFurnaceMenu extends UpgradableEnergyStorageMenu<AdvancedPoweredFurnaceBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    private final PropertyDelegate data;

    public AdvancedPoweredFurnaceMenu(int id, PlayerInventory inv, PacketByteBuf buffer) {
        this(id, inv, (AdvancedPoweredFurnaceBlockEntity)inv.player.getWorld().getBlockEntity(buffer.readBlockPos()));
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
        super(
                ModMenuTypes.ADVANCED_POWERED_FURNACE_MENU, id,

                playerInventory, blockEntity,
                ModBlocks.ADVANCED_POWERED_FURNACE,

                upgradeModuleInventory, 4
        );

        checkSize(inv, 6);
        checkDataCount(data, 29);
        this.data = data;

        addSlot(new ConstraintInsertSlot(inv, 0, 44, 17) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 1, 98, 17) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 2, 152, 17) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 3, 44, 53) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 4, 98, 53) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 5, 152, 53) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });

        for(int i = 0;i < upgradeModuleInventory.size();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 53 + i * 18, 35, this::isInUpgradeModuleView));

        addProperties(this.data);
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

    @Override
    public RedstoneMode getRedstoneMode() {
        return RedstoneMode.fromIndex(data.get(27));
    }

    @Override
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
            if(!insertMaxCount1Item(sourceItem, 4 * 9 + 6, 4 * 9 + 6 + 4, false) &&
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
}
