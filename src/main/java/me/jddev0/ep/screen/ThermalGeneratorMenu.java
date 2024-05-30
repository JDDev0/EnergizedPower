package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.ThermalGeneratorBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.inventory.UpgradeModuleSlot;
import me.jddev0.ep.inventory.UpgradeModuleViewContainerData;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.base.AbstractEnergizedPowerScreenHandler;
import me.jddev0.ep.screen.base.ConfigurableMenu;
import me.jddev0.ep.screen.base.EnergyStorageProducerIndicatorBarMenu;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;

public class ThermalGeneratorMenu extends AbstractEnergizedPowerScreenHandler
        implements EnergyStorageProducerIndicatorBarMenu, ConfigurableMenu {
    private final ThermalGeneratorBlockEntity blockEntity;
    private final World level;
    private final PropertyDelegate data;
    private final UpgradeModuleViewContainerData upgradeModuleViewContainerData;

    public ThermalGeneratorMenu(int id, PlayerInventory inv, PacketByteBuf buffer) {
        this(id, inv.player.getWorld().getBlockEntity(buffer.readBlockPos()), inv, new UpgradeModuleInventory(
                UpgradeModuleModifier.ENERGY_CAPACITY
        ), new ArrayPropertyDelegate(6));
    }

    public ThermalGeneratorMenu(int id, BlockEntity blockEntity, PlayerInventory playerInventory,
                                UpgradeModuleInventory upgradeModuleInventory, PropertyDelegate data) {
        super(ModMenuTypes.THERMAL_GENERATOR_MENU, id);

        this.blockEntity = (ThermalGeneratorBlockEntity)blockEntity;

        checkSize(upgradeModuleInventory, 1);
        checkDataCount(data, 6);
        this.level = playerInventory.player.getWorld();
        this.data = data;

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addSlot(new UpgradeModuleSlot(upgradeModuleInventory, 0, 80, 35, this::isInUpgradeModuleView));

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
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1), (short)data.get(2), (short)data.get(3));
    }

    public FluidStack getFluid() {
        return blockEntity.getFluid(0);
    }

    public long getTankCapacity() {
        return blockEntity.getTankCapacity(0);
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return RedstoneMode.fromIndex(data.get(4));
    }

    @Override
    public ComparatorMode getComparatorMode() {
        return ComparatorMode.fromIndex(data.get(5));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot sourceSlot = slots.get(index);
        if(sourceSlot == null || !sourceSlot.hasStack())
            return ItemStack.EMPTY;

        ItemStack sourceItem = sourceSlot.getStack();
        ItemStack sourceItemCopy = sourceItem.copy();

        if(index < 4 * 9) {
            //Player inventory slot -> Merge into upgrade module inventory
            if(!insertMaxCount1Item(sourceItem, 4 * 9, 4 * 9 + 1, false)) {
                return ItemStack.EMPTY;
            }
        }else if(index < 4 * 9 + 1) {
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
        return canUse(ScreenHandlerContext.create(level, blockEntity.getPos()), player, ModBlocks.THERMAL_GENERATOR);
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

    @Override
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
