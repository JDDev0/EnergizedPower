package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ChargingStationBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.integration.curios.CuriosCompatUtils;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.screen.ChargingStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChargingStationBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate {
    public static final int MAX_CHARGING_DISTANCE = ModConfigs.COMMON_CHARGING_STATION_MAX_CHARGING_DISTANCE.getValue();

    private final ReceiveOnlyEnergyStorage energyStorage;

    public ChargingStationBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CHARGING_STATION_ENTITY.get(), blockPos, blockState);

        energyStorage = new ReceiveOnlyEnergyStorage(0, ModConfigs.COMMON_CHARGING_STATION_CAPACITY.getValue(),
                ModConfigs.COMMON_CHARGING_STATION_TRANSFER_RATE.getValue()) {
            @Override
            protected void onChange() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToPlayersWithinXBlocks(
                            new EnergySyncS2CPacket(getEnergy(), getCapacity(), getBlockPos()),
                            getBlockPos(), (ServerLevel)level, 32
                    );
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.charging_station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(),
                getBlockPos()), (ServerPlayer)player);

        return new ChargingStationMenu(id, inventory, this);
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        nbt.put("energy", energyStorage.saveNBT());

        super.saveAdditional(nbt, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        energyStorage.loadNBT(nbt.get("energy"));
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ChargingStationBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        List<Player> players = level.getEntities(EntityTypeTest.forClass(Player.class), AABB.of(BoundingBox.fromCorners(
                new Vec3i(blockPos.getX() - MAX_CHARGING_DISTANCE, blockPos.getY() - MAX_CHARGING_DISTANCE,
                        blockPos.getZ() - MAX_CHARGING_DISTANCE),
                new Vec3i(blockPos.getX() + MAX_CHARGING_DISTANCE, blockPos.getY() + MAX_CHARGING_DISTANCE,
                        blockPos.getZ() + MAX_CHARGING_DISTANCE))), EntitySelector.NO_SPECTATORS.
                and(entity -> entity.distanceToSqr(blockPos.getCenter()) <= MAX_CHARGING_DISTANCE*MAX_CHARGING_DISTANCE));

        int energyPerTick = Math.min(blockEntity.energyStorage.getMaxReceive(), blockEntity.energyStorage.getEnergy());
        int energyPerTickLeft = energyPerTick;

        outer:
        for(Player player:players) {
            if(player.isDeadOrDying())
                continue;

            Inventory inventory = player.getInventory();
            for(int i = 0;i < inventory.getContainerSize();i++) {
                ItemStack itemStack = inventory.getItem(i);

                IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
                if(energyStorage == null || !energyStorage.canReceive())
                    continue;

                energyPerTickLeft -= energyStorage.receiveEnergy(energyPerTickLeft, false);
                if(energyPerTickLeft == 0)
                    break outer;
            }

            List<ItemStack> curiosItemStacks = CuriosCompatUtils.getCuriosItemStacks(inventory);
            for(ItemStack itemStack:curiosItemStacks) {
                IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
                if(energyStorage == null || !energyStorage.canReceive())
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

    public int getEnergy() {
        return energyStorage.getEnergy();
    }

    public int getCapacity() {
        return energyStorage.getCapacity();
    }

    @Override
    public void setEnergy(int energy) {
        energyStorage.setEnergyWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }
}