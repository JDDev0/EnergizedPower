package me.jddev0.ep.screen;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.CrusherBlockEntity;
import me.jddev0.ep.inventory.ConstraintInsertSlot;
import me.jddev0.ep.recipe.CrusherRecipe;
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
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class CrusherMenu extends UpgradableEnergyStorageMenu<CrusherBlockEntity>
        implements IEnergyStorageConsumerIndicatorBarMenu, IConfigurableMenu {
    private final SimpleProgressValueContainerData progressData = new SimpleProgressValueContainerData();
    private final SimpleProgressValueContainerData maxProgressData = new SimpleProgressValueContainerData();
    private final SimpleEnergyValueContainerData energyConsumptionPerTickData = new SimpleEnergyValueContainerData();
    private final SimpleEnergyValueContainerData energyConsumptionLeftData = new SimpleEnergyValueContainerData();
    private final SimpleBooleanValueContainerData hasEnoughEnergyData = new SimpleBooleanValueContainerData();
    private final SimpleRedstoneModeValueContainerData redstoneModeData = new SimpleRedstoneModeValueContainerData();
    private final SimpleComparatorModeValueContainerData comparatorModeData = new SimpleComparatorModeValueContainerData();

    public CrusherMenu(int id, PlayerInventory inv, PacketByteBuf buf) {
        this(id, inv.player.getWorld().getBlockEntity(buf.readBlockPos()), inv, new SimpleInventory(2) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> RecipeUtils.isIngredientOfAny(inv.player.getWorld(), CrusherRecipe.Type.INSTANCE, stack);
                    case 1 -> false;
                    default -> super.isValid(slot, stack);
                };
            }
        }, new UpgradeModuleInventory(
                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), null);
    }

    public CrusherMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory, Inventory inv,
                       UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data) {
        super(
                EPMenuTypes.CRUSHER_MENU, id,

                playerInventory, blockEntity,
                EPBlocks.CRUSHER,

                upgradeModuleInventory, 3
        );

        checkSize(inv, 2);

        addSlot(new ConstraintInsertSlot(inv, 0, 48, 35) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });
        addSlot(new ConstraintInsertSlot(inv, 1, 124, 35) {
            @Override
            public boolean isEnabled() {
                return super.isEnabled() && !isInUpgradeModuleView();
            }
        });

        for(int i = 0;i < upgradeModuleInventory.size();i++)
            addSlot(new UpgradeModuleSlot(upgradeModuleInventory, i, 62 + i * 18, 35, this::isInUpgradeModuleView));

        if(data == null) {
            addProperties(progressData);
            addProperties(maxProgressData);
            addProperties(energyConsumptionPerTickData);
            addProperties(energyConsumptionLeftData);
            addProperties(hasEnoughEnergyData);
            addProperties(redstoneModeData);
            addProperties(comparatorModeData);
        }else {
            addProperties(data);
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
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory, Merge into tile inventory
            if(!insertMaxCount1Item(sourceItem, 4 * 9 + 2, 4 * 9 + 2 + 3, false) &&
                    !insertItem(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                //"+1" instead of "+2": Do not allow adding to output slot
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 2 + 3) {
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
