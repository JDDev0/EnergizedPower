package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public abstract class MenuInventoryEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends SimpleInventory>
        extends InventoryEnergyStorageBlockEntity<E, I>
        implements ExtendedScreenHandlerFactory {
    protected final String machineName;

    protected final PropertyDelegate data;

    public MenuInventoryEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                       String machineName,
                                       long baseEnergyCapacity, long baseEnergyTransferRate,
                                       int slotCount) {
        super(type, blockPos, blockState, baseEnergyCapacity, baseEnergyTransferRate, slotCount);

        this.machineName = machineName;

        data = initContainerData();
    }

    protected PropertyDelegate initContainerData() {
        return new ArrayPropertyDelegate(0);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower." + machineName);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

}