package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.InductionSmelterBlockEntity;
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
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class InductionSmelterMenu extends UpgradableEnergyStorageMenu<InductionSmelterBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    private final SimpleProgressValueContainerData progressData = new SimpleProgressValueContainerData();
    private final SimpleProgressValueContainerData maxProgressData = new SimpleProgressValueContainerData();
    private final SimpleEnergyValueContainerData energyConsumptionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleEnergyValueContainerData energyConsumptionLeftData = new SimpleEnergyValueContainerData();
    private final SimpleBooleanValueContainerData hasEnoughEnergyData = new SimpleBooleanValueContainerData();
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public InductionSmelterMenu(int id, Inventory inv, BlockPos pos) {
        this(id, inv.player.level().getBlockEntity(pos), inv, new SimpleContainer(5) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2 -> RecipeUtils.isIngredientOfAny(((InductionSmelterBlockEntity)inv.player.level().
                            getBlockEntity(pos)).getIngredientsOfRecipes(), stack);
                    case 3, 4 -> false;
                    default -> false;
                };
            }
        }, new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), null);
    }

    public InductionSmelterMenu(int id, BlockEntity blockEntity, Inventory playerInventory, Container inv,
                                UpgradeModuleInventory upgradeModuleInventory, ContainerData data) {
        super(
                EPMenuTypes.INDUCTION_SMELTER_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.INDUCTION_SMELTER,

                upgradeModuleInventory, 3
        );

        checkContainerSize(inv, 5);

        addSlot(new ConstraintInsertSlot(inv, 0, 36, 37) {
            @Override
            public boolean isActive() {
                return super.isActive() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 1, 57, 34) {
            @Override
            public boolean isActive() {
                return super.isActive() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 2, 78, 37) {
            @Override
            public boolean isActive() {
                return super.isActive() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 3, 141, 25) {
            @Override
            public boolean isActive() {
                return super.isActive() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 4, 141, 49) {
            @Override
            public boolean isActive() {
                return super.isActive() && !isInUpgradeModuleView();
            }
        });

        for(int i = 0;i < upgradeModuleInventory.getContainerSize();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 62 + i * 18, 35, this::isInUpgradeModuleView));

        if(data == null) {
            addDataSlots(progressData);
            addDataSlots(maxProgressData);
            addDataSlots(energyConsumptionPerTickData);
            addDataSlots(energyConsumptionLeftData);
            addDataSlots(hasEnoughEnergyData);
            addDataSlots(redstoneModeData);
            addDataSlots(comparatorModeData);
        }else {
            addDataSlots(data);
        }
    }

    @Override
    public long getEnergyIndicatorBarValue() {
        return energyConsumptionLeftData.getValue();
    }

    @Override
    public long getEnergyPerTickBarValue() {
        return energyConsumptionPerTickData.getValue();
    }

    /**
     * @return Same as isCrafting but energy requirements are ignored
     */
    public boolean isCraftingActive() {
        return progressData.getValue() > 0;
    }

    public boolean isCrafting() {
        return progressData.getValue() > 0 && hasEnoughEnergyData.getValue();
    }

    public int getScaledProgressArrowSize() {
        int progress = progressData.getValue();
        int maxProgress = maxProgressData.getValue();
        int progressArrowSize = 24;

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
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getItem();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory, Merge into tile inventory
            if(!moveItemStackTo(sourceItem, 4 * 9 + 5, 4 * 9 + 5 + 3, false) &&
                    !moveItemStackTo(sourceItem, 4 * 9, 4 * 9 + 3, false)) {
                //"+3" instead of "+5": Do not allow adding to output slot
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 5 + 3) {
            //Tile inventory and upgrade module slot -> Merge into player inventory
            if(!moveItemStackTo(sourceItem, 0, 4 * 9, false)) {
                return ItemStack.EMPTY;
            }
        }else {
            throw new IllegalArgumentException("Invalid slot index");
        }

        if(sourceItem.getCount() == 0)
            sourceSlot.setByPlayer(ItemStack.EMPTY);
        else
            sourceSlot.setChanged();

        sourceSlot.onTake(player, sourceItem);

        return sourceItemCopy;
    }
}
