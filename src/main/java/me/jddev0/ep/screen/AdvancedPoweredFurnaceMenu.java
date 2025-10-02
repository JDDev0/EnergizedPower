package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.AdvancedPoweredFurnaceBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.data.*;
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
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class AdvancedPoweredFurnaceMenu extends UpgradableEnergyStorageMenu<AdvancedPoweredFurnaceBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    private final SimpleProgressValueContainerData[] progressData = new SimpleProgressValueContainerData[] {
            new SimpleProgressValueContainerData(),
            new SimpleProgressValueContainerData(),
            new SimpleProgressValueContainerData()
    };
    private final SimpleProgressValueContainerData[] maxProgressData = new SimpleProgressValueContainerData[] {
            new SimpleProgressValueContainerData(),
            new SimpleProgressValueContainerData(),
            new SimpleProgressValueContainerData()
    };
    private final SimpleEnergyValueContainerData energyConsumptionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleEnergyValueContainerData[] energyConsumptionLeftData = new SimpleEnergyValueContainerData[] {
            new SimpleEnergyValueContainerData(),
            new SimpleEnergyValueContainerData(),
            new SimpleEnergyValueContainerData()
    };
    private final SimpleBooleanValueContainerData[] hasEnoughEnergyData = new SimpleBooleanValueContainerData[] {
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData(),
            new SimpleBooleanValueContainerData()
    };
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public AdvancedPoweredFurnaceMenu(int id, PlayerInventory inv, BlockPos pos) {
        this(id, inv, (AdvancedPoweredFurnaceBlockEntity)inv.player.getEntityWorld().getBlockEntity(pos));
    }
    private AdvancedPoweredFurnaceMenu(int id, PlayerInventory inv, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        this(id, blockEntity, inv, new SimpleInventory(6) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2 -> RecipeUtils.isIngredientOfAny(blockEntity.getIngredientsOfRecipes(), stack);
                    case 3, 4, 5 -> false;
                    default -> super.isValid(slot, stack);
                };
            }
        }, new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE
        ), null);
    }

    public AdvancedPoweredFurnaceMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                                      UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data) {
        super(
                EPMenuTypes.ADVANCED_POWERED_FURNACE_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.ADVANCED_POWERED_FURNACE,

                upgradeModuleInventory, 4
        );

        checkSize(inv, 6);

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

        if(data == null) {
            addProperties(progressData[0]);
            addProperties(progressData[1]);
            addProperties(progressData[2]);
            addProperties(maxProgressData[0]);
            addProperties(maxProgressData[1]);
            addProperties(maxProgressData[2]);
            addProperties(energyConsumptionPerTickData);
            addProperties(energyConsumptionLeftData[0]);
            addProperties(energyConsumptionLeftData[1]);
            addProperties(energyConsumptionLeftData[2]);
            addProperties(hasEnoughEnergyData[0]);
            addProperties(hasEnoughEnergyData[1]);
            addProperties(hasEnoughEnergyData[2]);
            addProperties(redstoneModeData);
            addProperties(comparatorModeData);
        }else {
            addProperties(data);
        }
    }

    @Override
    public long getEnergyIndicatorBarValue() {
        long energyIndicatorBarValueSum = -1;

        for(SimpleEnergyValueContainerData ele:energyConsumptionLeftData) {
            long value = ele.getValue();

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

    @Override
    public long getEnergyPerTickBarValue() {
        return energyConsumptionPerTickData.getValue();
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive(int index) {
        return progressData[index].getValue() > 0;
    }

    public boolean isCrafting(int index) {
        return progressData[index].getValue() > 0 && hasEnoughEnergyData[index].getValue();
    }

    public int getScaledProgressArrowSize(int index) {
        int progress = progressData[index].getValue();
        int maxProgress = maxProgressData[index].getValue();
        int progressArrowSize = 17;

        return (maxProgress == 0 || progress == 0)?0:progress * progressArrowSize / maxProgress;
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneModeData.getValue();
    }

    @Override
    public ComparatorMode getComparatorMode() {
        return comparatorModeData.getValue();
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
}
