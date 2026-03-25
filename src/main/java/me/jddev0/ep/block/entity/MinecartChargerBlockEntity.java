package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.MinecartChargerBlock;
import me.jddev0.ep.block.entity.base.MenuEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.entity.AbstractMinecartBatteryBox;
import me.jddev0.ep.screen.MinecartChargerMenu;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.List;

public class MinecartChargerBlockEntity
        extends MenuEnergyStorageBlockEntity<EnergizedPowerEnergyStorage> {
    public static final long MAX_TRANSFER = ModConfigs.COMMON_MINECART_CHARGER_TRANSFER_RATE.getValue();

    private boolean hasMinecartOld = true; //Default true (Force first update)
    private boolean hasMinecart = false; //Default false (Force first update)

    public MinecartChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.MINECART_CHARGER_ENTITY, blockPos, blockState,

                "minecart_charger",

                ModConfigs.COMMON_MINECART_CHARGER_CAPACITY.getValue(),
                MAX_TRANSFER
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity, baseEnergyCapacity, baseEnergyCapacity) {
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
        
        return new MinecartChargerMenu(id, this, inventory);
    }

    public int getRedstoneOutput() {
        BlockPos blockPosFacing = getBlockPos().relative(getBlockState().getValue(MinecartChargerBlock.FACING));
        List<AbstractMinecartBatteryBox> minecarts = level.getEntities(EntityTypeTest.forClass(AbstractMinecartBatteryBox.class),
                new AABB(blockPosFacing.getX(), blockPosFacing.getY(),
                        blockPosFacing.getZ(), blockPosFacing.getX() + 1,
                        blockPosFacing.getY() + 1, blockPosFacing.getZ() + 1),
                EntitySelector.ENTITY_STILL_ALIVE);
        if(minecarts.isEmpty())
            return 0;

        AbstractMinecartBatteryBox minecart = minecarts.get(0);

        long minecartEnergy = minecart.getEnergy();
        boolean isEmptyFlag = minecartEnergy == 0;

        return Math.min(Mth.floor((float)minecartEnergy / minecart.getCapacity() * 14.f) + (isEmptyFlag?0:1), 15);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, MinecartChargerBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(blockEntity.hasMinecartOld != blockEntity.hasMinecart)
            setChanged(level, blockPos, state);

        blockEntity.hasMinecartOld = blockEntity.hasMinecart;

        BlockPos blockPosFacing = blockEntity.getBlockPos().relative(blockEntity.getBlockState().getValue(MinecartChargerBlock.FACING));
        List<AbstractMinecartBatteryBox> minecarts = level.getEntities(EntityTypeTest.forClass(AbstractMinecartBatteryBox.class),
                new AABB(blockPosFacing.getX(), blockPosFacing.getY(),
                        blockPosFacing.getZ(), blockPosFacing.getX() + 1,
                        blockPosFacing.getY() + 1, blockPosFacing.getZ() + 1),
                EntitySelector.ENTITY_STILL_ALIVE);
        blockEntity.hasMinecart = !minecarts.isEmpty();
        if(!blockEntity.hasMinecart)
            return;

        AbstractMinecartBatteryBox minecart = minecarts.get(0);
        long transferred = Math.max(0, Math.min(Math.min(blockEntity.energyStorage.getAmount(),
                        blockEntity.limitingEnergyStorage.getMaxInsert()),
                Math.min(minecart.getTransferRate(), minecart.getCapacity() - minecart.getEnergy())));
        minecart.setEnergy(minecart.getEnergy() + transferred);

        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.energyStorage.extract(transferred, transaction);
            transaction.commit();
        }
    }
}