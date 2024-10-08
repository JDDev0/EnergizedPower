package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ChargingStationBlock;
import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.integration.curios.CuriosCompatUtils;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.ChargingStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChargingStationBlockEntity extends UpgradableEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage> {
    public static final int MAX_CHARGING_DISTANCE = ModConfigs.COMMON_CHARGING_STATION_MAX_CHARGING_DISTANCE.getValue();

    public ChargingStationBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.CHARGING_STATION_ENTITY.get(), blockPos, blockState,

                "charging_station",

                ModConfigs.COMMON_CHARGING_STATION_CAPACITY.getValue(),
                ModConfigs.COMMON_CHARGING_STATION_TRANSFER_RATE.getValue(),

                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.RANGE
        );
    }

    @Override
    protected ReceiveOnlyEnergyStorage initEnergyStorage() {
        return new ReceiveOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            public int getMaxReceive() {
                return Math.max(1, (int)Math.ceil(maxReceive * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }

            @Override
            protected void onChange() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new ChargingStationMenu(id, inventory, this, upgradeModuleInventory);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ChargingStationBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        int maxChargingDistance = (int)Math.ceil(MAX_CHARGING_DISTANCE *
                blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.RANGE));

        List<Player> players = level.getEntities(EntityTypeTest.forClass(Player.class), AABB.of(BoundingBox.fromCorners(
                new Vec3i(blockPos.getX() - maxChargingDistance, blockPos.getY() - maxChargingDistance,
                        blockPos.getZ() - maxChargingDistance),
                new Vec3i(blockPos.getX() + maxChargingDistance, blockPos.getY() + maxChargingDistance,
                        blockPos.getZ() + maxChargingDistance))), EntitySelector.NO_SPECTATORS.
                and(entity -> entity.distanceToSqr(blockPos.getCenter()) <= maxChargingDistance*maxChargingDistance));

        int energyPerTick = Math.min(blockEntity.energyStorage.getMaxReceive(), blockEntity.energyStorage.getEnergy());
        int energyPerTickLeft = energyPerTick;

        outer:
        for(Player player:players) {
            if(player.isDeadOrDying())
                continue;

            Inventory inventory = player.getInventory();
            for(int i = 0;i < inventory.getContainerSize();i++) {
                ItemStack itemStack = inventory.getItem(i);

                LazyOptional<IEnergyStorage> energyStorageLazyOptional = itemStack.getCapability(ForgeCapabilities.ENERGY);
                if(!energyStorageLazyOptional.isPresent())
                    continue;

                IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
                if(!energyStorage.canReceive())
                    continue;

                energyPerTickLeft -= energyStorage.receiveEnergy(energyPerTickLeft, false);
                if(energyPerTickLeft == 0)
                    break outer;
            }

            List<ItemStack> curiosItemStacks = CuriosCompatUtils.getCuriosItemStacks(inventory);
            for(ItemStack itemStack:curiosItemStacks) {
                LazyOptional<IEnergyStorage> energyStorageLazyOptional = itemStack.getCapability(ForgeCapabilities.ENERGY);
                if(!energyStorageLazyOptional.isPresent())
                    continue;

                IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
                if(!energyStorage.canReceive())
                    continue;

                energyPerTickLeft -= energyStorage.receiveEnergy(energyPerTickLeft, false);
                if(energyPerTickLeft == 0)
                    break outer;
            }
        }

        if(energyPerTickLeft == energyPerTick) {
            if(!level.getBlockState(blockPos).hasProperty(ChargingStationBlock.CHARGING) || level.getBlockState(blockPos).getValue(ChargingStationBlock.CHARGING))
                level.setBlock(blockPos, state.setValue(ChargingStationBlock.CHARGING, false), 3);
        }else {
            if(!level.getBlockState(blockPos).hasProperty(ChargingStationBlock.CHARGING) || !level.getBlockState(blockPos).getValue(ChargingStationBlock.CHARGING))
                level.setBlock(blockPos, state.setValue(ChargingStationBlock.CHARGING, Boolean.TRUE), 3);

            blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyPerTick + energyPerTickLeft);
        }
    }
}