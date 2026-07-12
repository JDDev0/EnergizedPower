package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.BasicMenuProvider;
import me.jddev0.ep.block.entity.base.MenuEnergyStorageBlockEntity;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.entity.AbstractMinecartBatteryBox;
import me.jddev0.ep.machine.ItemDrop;
import me.jddev0.ep.machine.RedstoneOutput;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

public abstract class AbstractMinecartChargerBlockEntity extends MenuEnergyStorageBlockEntity<EnergizedPowerEnergyStorage>
        implements RedstoneOutput, ItemDrop {
    protected final BasicMenuProvider menuProvider;

    private boolean hasMinecartOld = true; //Default true (Force first update)
    private boolean hasMinecart = false; //Default false (Force first update)

    public AbstractMinecartChargerBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                              String machineName, BasicMenuProvider menuProvider,
                                              long baseEnergyCapacity, long baseEnergyTransferRate) {
        super(
                type, blockPos, blockState,

                machineName,

                baseEnergyCapacity, baseEnergyTransferRate
        );

        this.menuProvider = menuProvider;
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, 0);
    }


    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return menuProvider.createMenu(id, inventory, this);
    }

    @Override
    public int getRedstoneOutput() {
        BlockPos blockPosFacing = getBlockPos().relative(getBlockState().getValue(BlockStateProperties.FACING));
        List<AbstractMinecartBatteryBox> minecarts = level.getEntities(
                EntityTypeTest.forClass(AbstractMinecartBatteryBox.class),
                new AABB(blockPosFacing.getX(), blockPosFacing.getY(),
                        blockPosFacing.getZ(), blockPosFacing.getX() + 1,
                        blockPosFacing.getY() + 1, blockPosFacing.getZ() + 1),
                EntitySelector.ENTITY_STILL_ALIVE);
        if(minecarts.isEmpty())
            return 0;

        AbstractMinecartBatteryBox minecart = minecarts.get(0);

        long minecartEnergy = minecart.getEnergy();
        boolean isEmptyFlag = minecartEnergy == 0;

        return Math.min(Mth.floor((double)minecartEnergy / minecart.getCapacity() * 14.f) + (isEmptyFlag?0:1), 15);
    }

    public @Nullable EnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AbstractMinecartChargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(blockEntity.hasMinecartOld != blockEntity.hasMinecart)
            setChanged(level, blockPos, state);

        blockEntity.hasMinecartOld = blockEntity.hasMinecart;

        BlockPos blockPosFacing = blockEntity.getBlockPos().relative(blockEntity.getBlockState().getValue(BlockStateProperties.FACING));
        List<AbstractMinecartBatteryBox> minecarts = level.getEntities(
                EntityTypeTest.forClass(AbstractMinecartBatteryBox.class),
                new AABB(blockPosFacing.getX(), blockPosFacing.getY(),
                        blockPosFacing.getZ(), blockPosFacing.getX() + 1,
                        blockPosFacing.getY() + 1, blockPosFacing.getZ() + 1),
                EntitySelector.ENTITY_STILL_ALIVE);
        blockEntity.hasMinecart = !minecarts.isEmpty();
        if(!blockEntity.hasMinecart)
            return;

        AbstractMinecartBatteryBox minecart = minecarts.get(0);
        long transferred = Math.max(0, Math.min(Math.min(blockEntity.limitingEnergyStorage.getAmount(), blockEntity.limitingEnergyStorage.getMaxInsert()),
                Math.min(minecart.getTransferRate(), minecart.getCapacity() - minecart.getEnergy())));
        minecart.setEnergy(minecart.getEnergy() + transferred);

        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.energyStorage.extract(transferred, transaction);
            transaction.commit();
        }
    }

    @Override
    public void drops(Level level, BlockPos worldPosition) {}
}