package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ChargingStationBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.ChargingStationMenu;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.List;

public class ChargingStationBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate {
    public static final long CAPACITY = ModConfigs.COMMON_CHARGING_STATION_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_CHARGING_STATION_TRANSFER_RATE.getValue();
    public static final int MAX_CHARGING_DISTANCE = ModConfigs.COMMON_CHARGING_STATION_MAX_CHARGING_DISTANCE.getValue();

    private final UpgradeModuleInventory upgradeModuleInventory = new UpgradeModuleInventory(
            UpgradeModuleModifier.ENERGY_CAPACITY,
            UpgradeModuleModifier.RANGE
    );
    private final InventoryChangedListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    final EnergizedPowerLimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;

    public ChargingStationBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CHARGING_STATION_ENTITY, blockPos, blockState);

        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        internalEnergyStorage = new EnergizedPowerEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
            @Override
            public long getCapacity() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(getAmount());
                    buffer.writeLong(getCapacity());
                    buffer.writeBlockPos(getPos());

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            ModMessages.ENERGY_SYNC_ID, buffer
                    );
                }
            }
        };
        energyStorage = new EnergizedPowerLimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0) {
            @Override
            public long getMaxInsert() {
                return Math.max(1, (long)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
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
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeLong(internalEnergyStorage.getAmount());
        buffer.writeLong(internalEnergyStorage.getCapacity());
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.ENERGY_SYNC_ID, buffer);
        
        return new ChargingStationMenu(id, this, inventory, upgradeModuleInventory);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        //Save Upgrade Module Inventory first
        nbt.put("upgrade_module_inventory", upgradeModuleInventory.saveToNBT());

        nbt.putLong("energy", internalEnergyStorage.getAmount());

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        //Load Upgrade Module Inventory first
        upgradeModuleInventory.removeListener(updateUpgradeModuleListener);
        upgradeModuleInventory.loadFromNBT(nbt.getCompound("upgrade_module_inventory"));
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        internalEnergyStorage.setAmountWithoutUpdate(nbt.getLong("energy"));
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, upgradeModuleInventory);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ChargingStationBlockEntity blockEntity) {
        if(level.isClient())
            return;

        int maxChargingDistance = (int)Math.ceil(MAX_CHARGING_DISTANCE *
                blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.RANGE));

        List<PlayerEntity> players = level.getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), Box.from(BlockBox.create(
                new Vec3i(blockPos.getX() - maxChargingDistance, blockPos.getY() - maxChargingDistance,
                        blockPos.getZ() - maxChargingDistance),
                new Vec3i(blockPos.getX() + maxChargingDistance, blockPos.getY() + maxChargingDistance,
                        blockPos.getZ() + maxChargingDistance))), EntityPredicates.EXCEPT_SPECTATOR.
                and(entity -> entity.squaredDistanceTo(blockPos.toCenterPos()) <= maxChargingDistance*maxChargingDistance));

        long energyPerTick = Math.min(blockEntity.energyStorage.getMaxInsert(), blockEntity.internalEnergyStorage.getAmount());
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

    private void updateUpgradeModules() {
        markDirty();
        if(world != null && !world.isClient()) {
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeLong(internalEnergyStorage.getAmount());
            buffer.writeLong(internalEnergyStorage.getCapacity());
            buffer.writeBlockPos(getPos());

            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getPos(), (ServerWorld)world, 32,
                    ModMessages.ENERGY_SYNC_ID, buffer
            );
        }
    }

    public long getEnergy() {
        return internalEnergyStorage.getAmount();
    }

    public long getCapacity() {
        return internalEnergyStorage.getCapacity();
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.setAmountWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(long capacity) {
        internalEnergyStorage.setCapacityWithoutUpdate(capacity);
    }
}