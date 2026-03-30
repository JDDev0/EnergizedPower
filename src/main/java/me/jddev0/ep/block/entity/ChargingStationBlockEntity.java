package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ChargingStationBlock;
import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.ChargingStationMenu;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.List;

public class ChargingStationBlockEntity extends UpgradableEnergyStorageBlockEntity<EnergizedPowerEnergyStorage> {
    public static final int MAX_CHARGING_DISTANCE = ModConfigs.COMMON_CHARGING_STATION_MAX_CHARGING_DISTANCE.getValue();

    public ChargingStationBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.CHARGING_STATION_ENTITY, blockPos, blockState,

                "charging_station",

                ModConfigs.COMMON_CHARGING_STATION_CAPACITY.getValue(),
                ModConfigs.COMMON_CHARGING_STATION_TRANSFER_RATE.getValue(),

                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.RANGE
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity, baseEnergyCapacity, baseEnergyCapacity) {
            @Override
            public long getCapacity() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, 0) {
            @Override
            public long getMaxInsert() {
                return Math.max(1, (long)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        
        return new ChargingStationMenu(id, this, inventory, upgradeModuleInventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ChargingStationBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        int maxChargingDistance = (int)Math.ceil(MAX_CHARGING_DISTANCE *
                blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.RANGE));

        List<Player> players = level.getEntities(EntityTypeTest.forClass(Player.class), AABB.of(BoundingBox.fromCorners(
                new Vec3i(blockPos.getX() - maxChargingDistance, blockPos.getY() - maxChargingDistance,
                        blockPos.getZ() - maxChargingDistance),
                new Vec3i(blockPos.getX() + maxChargingDistance, blockPos.getY() + maxChargingDistance,
                        blockPos.getZ() + maxChargingDistance))), EntitySelector.NO_SPECTATORS.
                and(entity -> entity.distanceToSqr(blockPos.getCenter()) <= maxChargingDistance*maxChargingDistance));

        long energyPerTick = Math.min(blockEntity.limitingEnergyStorage.getMaxInsert(), blockEntity.energyStorage.getAmount());
        long energyPerTickLeft = energyPerTick;

        outer:
        for(Player player:players) {
            if(player.isDeadOrDying())
                continue;

            Inventory inventory = player.getInventory();
            for(int i = 0;i < inventory.getContainerSize();i++) {
                ItemStack itemStack = inventory.getItem(i);

                if(!EnergyStorageUtil.isEnergyStorage(itemStack))
                    continue;

                EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(itemStack, ContainerItemContext.
                        ofPlayerSlot(inventory.player, ContainerStorage.of(inventory, null).getSlots().get(i)));
                if(limitingEnergyStorage == null)
                    continue;

                if(!limitingEnergyStorage.supportsInsertion())
                    continue;

                try(Transaction transaction = Transaction.openOuter()) {
                    energyPerTickLeft -= limitingEnergyStorage.insert(energyPerTickLeft, transaction);
                    transaction.commit();
                    if(energyPerTickLeft == 0)
                        break outer;
                }
            }
        }

        if(energyPerTickLeft == energyPerTick) {
            if(!level.getBlockState(blockPos).hasProperty(ChargingStationBlock.CHARGING) || level.getBlockState(blockPos).getValue(ChargingStationBlock.CHARGING))
                level.setBlock(blockPos, state.setValue(ChargingStationBlock.CHARGING, false), 3);
        }else {
            if(!level.getBlockState(blockPos).hasProperty(ChargingStationBlock.CHARGING) || !level.getBlockState(blockPos).getValue(ChargingStationBlock.CHARGING))
                level.setBlock(blockPos, state.setValue(ChargingStationBlock.CHARGING, Boolean.TRUE), 3);

            try(Transaction transaction = Transaction.openOuter()) {
                blockEntity.energyStorage.extract(energyPerTick - energyPerTickLeft, transaction);
                transaction.commit();
            }
        }
    }
}