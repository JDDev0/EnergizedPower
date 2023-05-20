package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ChargingStationBlock;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.ChargingStationMenu;
import me.jddev0.ep.util.ByteUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.LimitingEnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;

public class ChargingStationBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate {
    public static final long CAPACITY = 262144;
    public static final long MAX_RECEIVE = 16384;
    public static final int MAX_CHARGING_DISTANCE = 7;

    final LimitingEnergyStorage energyStorage;
    private final SimpleEnergyStorage internalEnergyStorage;

    protected  final PropertyDelegate data;

    public ChargingStationBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CHARGING_STATION_ENTITY, blockPos, blockState);

        internalEnergyStorage = new SimpleEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(amount);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.broadcastServerPacket(world.getServer(), ModMessages.ENERGY_SYNC_ID, buffer);
                }
            }
        };
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0);
        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1, 2, 3 -> ByteUtils.get2Bytes(ChargingStationBlockEntity.this.internalEnergyStorage.amount, index);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(ChargingStationBlockEntity.this.internalEnergyStorage.capacity, index - 4);
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2, 3 -> ChargingStationBlockEntity.this.internalEnergyStorage.amount = ByteUtils.with2Bytes(
                            ChargingStationBlockEntity.this.internalEnergyStorage.amount, (short)value, index);
                    case 4, 5, 6, 7 -> {}
                }
            }

            @Override
            public int size() {
                return 8;
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.charging_station");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ChargingStationMenu(id, this, inventory, new SimpleInventory(0), this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putLong("energy", internalEnergyStorage.amount);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        internalEnergyStorage.amount = nbt.getLong("energy");
    }


    public static void tick(World level, BlockPos blockPos, BlockState state, ChargingStationBlockEntity blockEntity) {
        if(level.isClient())
            return;

        List<PlayerEntity> players = level.getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), Box.from(BlockBox.create(
                new Vec3i(blockPos.getX() - MAX_CHARGING_DISTANCE, blockPos.getY() - MAX_CHARGING_DISTANCE,
                        blockPos.getZ() - MAX_CHARGING_DISTANCE),
                new Vec3i(blockPos.getX() + MAX_CHARGING_DISTANCE, blockPos.getY() + MAX_CHARGING_DISTANCE,
                        blockPos.getZ() + MAX_CHARGING_DISTANCE))), EntityPredicates.EXCEPT_SPECTATOR.
                and(entity -> entity.squaredDistanceTo(blockPos.toCenterPos()) <= MAX_CHARGING_DISTANCE*MAX_CHARGING_DISTANCE));

        long energyPerTick = Math.min(MAX_RECEIVE, blockEntity.internalEnergyStorage.amount);
        long energyPerTickLeft = energyPerTick;

        outer:
        for(PlayerEntity player:players) {
            if(player.isDead())
                continue;

            PlayerInventory inventory = player.getInventory();
            for(int i = 0;i < inventory.size();i++) {
                ItemStack itemStack = inventory.getStack(i);

                if(!EnergyStorageUtil.isEnergyStorage(itemStack))
                    continue;

                EnergyStorage energyStorage = EnergyStorage.ITEM.find(itemStack, ContainerItemContext.
                        ofPlayerSlot(inventory.player, InventoryStorage.of(inventory, null).getSlots().get(i)));
                if(energyStorage == null)
                    continue;

                if(!energyStorage.supportsInsertion())
                    continue;

                try(Transaction transaction = Transaction.openOuter()) {
                    energyPerTickLeft -= energyStorage.insert(energyPerTickLeft, transaction);
                    transaction.commit();
                    if(energyPerTickLeft == 0)
                        break outer;
                }
            }
        }

        if(energyPerTickLeft == energyPerTick) {
            if(!level.getBlockState(blockPos).contains(ChargingStationBlock.CHARGING) || level.getBlockState(blockPos).get(ChargingStationBlock.CHARGING))
                level.setBlockState(blockPos, state.with(ChargingStationBlock.CHARGING, false), 3);
        }else {
            if(!level.getBlockState(blockPos).contains(ChargingStationBlock.CHARGING) || !level.getBlockState(blockPos).get(ChargingStationBlock.CHARGING))
                level.setBlockState(blockPos, state.with(ChargingStationBlock.CHARGING, Boolean.TRUE), 3);

            try(Transaction transaction = Transaction.openOuter()) {
                blockEntity.internalEnergyStorage.extract(energyPerTick - energyPerTickLeft, transaction);
                transaction.commit();
            }
        }
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.amount = energy;
    }

    @Override
    public void setCapacity(long capacity) {
        //Does nothing (capacity is final)
    }
}