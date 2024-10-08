package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AdvancedMinecartChargerBlock;
import me.jddev0.ep.block.entity.base.MenuEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.entity.AbstractMinecartBatteryBox;
import me.jddev0.ep.screen.AdvancedMinecartChargerMenu;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.List;

public class AdvancedMinecartChargerBlockEntity extends MenuEnergyStorageBlockEntity<EnergizedPowerEnergyStorage> {
    public static final long MAX_TRANSFER = ModConfigs.COMMON_ADVANCED_MINECART_CHARGER_TRANSFER_RATE.getValue();

    private boolean hasMinecartOld = true; //Default true (Force first update)
    private boolean hasMinecart = false; //Default false (Force first update)

    public AdvancedMinecartChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_MINECART_CHARGER_ENTITY, blockPos, blockState,

                "advanced_minecart_charger",

                ModConfigs.COMMON_ADVANCED_MINECART_CHARGER_CAPACITY.getValue(),
                MAX_TRANSFER
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity, baseEnergyCapacity, baseEnergyCapacity) {
            @Override
            protected void onFinalCommit() {
                markDirty();
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
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);

        return new AdvancedMinecartChargerMenu(id, this, inventory);
    }

    public int getRedstoneOutput() {
        BlockPos blockPosFacing = getPos().offset(getCachedState().get(AdvancedMinecartChargerBlock.FACING));
        List<AbstractMinecartBatteryBox> minecarts = world.getEntitiesByType(TypeFilter.instanceOf(AbstractMinecartBatteryBox.class),
                new Box(blockPosFacing.getX(), blockPosFacing.getY(),
                        blockPosFacing.getZ(), blockPosFacing.getX() + 1,
                        blockPosFacing.getY() + 1, blockPosFacing.getZ() + 1),
                EntityPredicates.VALID_ENTITY);
        if(minecarts.isEmpty())
            return 0;

        AbstractMinecartBatteryBox minecart = minecarts.get(0);

        long minecartEnergy = minecart.getEnergy();
        boolean isEmptyFlag = minecartEnergy == 0;

        return Math.min(MathHelper.floor((float)minecartEnergy / minecart.getCapacity() * 14.f) + (isEmptyFlag?0:1), 15);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AdvancedMinecartChargerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(blockEntity.hasMinecartOld != blockEntity.hasMinecart)
            markDirty(level, blockPos, state);

        blockEntity.hasMinecartOld = blockEntity.hasMinecart;

        BlockPos blockPosFacing = blockEntity.getPos().offset(blockEntity.getCachedState().get(AdvancedMinecartChargerBlock.FACING));
        List<AbstractMinecartBatteryBox> minecarts = level.getEntitiesByType(TypeFilter.instanceOf(AbstractMinecartBatteryBox.class),
                new Box(blockPosFacing.getX(), blockPosFacing.getY(),
                        blockPosFacing.getZ(), blockPosFacing.getX() + 1,
                        blockPosFacing.getY() + 1, blockPosFacing.getZ() + 1),
                EntityPredicates.VALID_ENTITY);
        blockEntity.hasMinecart = !minecarts.isEmpty();
        if(!blockEntity.hasMinecart)
            return;

        AbstractMinecartBatteryBox minecart = minecarts.get(0);
        long transferred = Math.min(Math.min(blockEntity.limitingEnergyStorage.getAmount(),
                        blockEntity.limitingEnergyStorage.getMaxInsert()),
                Math.min(minecart.getTransferRate(), minecart.getCapacity() - minecart.getEnergy()));
        minecart.setEnergy(minecart.getEnergy() + transferred);

        if(transferred < 0)
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.energyStorage.extract(transferred, transaction);
            transaction.commit();
        }
    }
}