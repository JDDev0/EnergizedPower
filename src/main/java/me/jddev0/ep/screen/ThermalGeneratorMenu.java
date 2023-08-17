package me.jddev0.ep.screen;

import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.ThermalGeneratorBlockEntity;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public class ThermalGeneratorMenu extends AbstractContainerMenu implements EnergyStorageProducerIndicatorBarMenu {
    private final ThermalGeneratorBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public ThermalGeneratorMenu(int id, Inventory inv, FriendlyByteBuf buffer) {
        this(id, inv, inv.player.level.getBlockEntity(buffer.readBlockPos()), new SimpleContainerData(2));
    }

    public ThermalGeneratorMenu(int id, Inventory inv, BlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.THERMAL_GENERATOR_MENU.get(), id);

        checkContainerDataCount(data, 2);
        this.blockEntity = (ThermalGeneratorBlockEntity)blockEntity;
        this.level = inv.player.level;
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(this.data);
    }

    @Override
    public int getEnergy() {
        return blockEntity.getEnergy();
    }

    @Override
    public int getCapacity() {
        return blockEntity.getCapacity();
    }

    @Override
    public int getEnergyIndicatorBarValue() {
        return ByteUtils.from2ByteChunks((short)data.get(0), (short)data.get(1));
    }

    public FluidStack getFluid() {
        return blockEntity.getFluid();
    }

    public int getTankCapacity() {
        return blockEntity.getTankCapacity();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, ModBlocks.THERMAL_GENERATOR.get());
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
