package me.jddev0.ep.block.entity;

import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.block.entity.base.MenuEnergyStorageBlockEntity;
import me.jddev0.ep.energy.InfinityEnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.screen.CreativeBatteryBoxMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class CreativeBatteryBoxBlockEntity extends MenuEnergyStorageBlockEntity<InfinityEnergyStorage>
        implements CheckboxUpdate {
    private boolean energyProduction = true;
    private boolean energyConsumption;

    public CreativeBatteryBoxBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.CREATIVE_BATTERY_BOX_ENTITY, blockPos, blockState,

                "creative_battery_box",

                Long.MAX_VALUE, Long.MAX_VALUE
        );
    }

    @Override
    protected InfinityEnergyStorage initEnergyStorage() {
        return new InfinityEnergyStorage() {
            @Override
            public long extract(long maxAmount, TransactionContext transaction) {
                return energyProduction?super.extract(maxAmount, transaction):0;
            }

            @Override
            public long insert(long maxAmount, TransactionContext transaction) {
                return energyConsumption?super.insert(maxAmount, transaction):0;
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, Long.MAX_VALUE, Long.MAX_VALUE);
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> energyProduction?1:0;
                    case 1 -> energyConsumption?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> CreativeBatteryBoxBlockEntity.this.energyProduction = value != 0;
                    case 1 -> CreativeBatteryBoxBlockEntity.this.energyConsumption = value != 0;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new CreativeBatteryBoxMenu(id, this, inventory, data);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putBoolean("energy_production", energyProduction);
        nbt.putBoolean("energy_consumption", energyConsumption);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        energyProduction = !nbt.contains("energy_production") || nbt.getBoolean("energy_production");
        energyConsumption = nbt.getBoolean("energy_consumption");
    }

    public void setEnergyProduction(boolean energyProduction) {
        this.energyProduction = energyProduction;
        markDirty(world, getPos(), getCachedState());
    }

    public void setEnergyConsumption(boolean energyConsumption) {
        this.energyConsumption = energyConsumption;
        markDirty(world, getPos(), getCachedState());
    }

    @Override
    public void setCheckbox(int checkboxId, boolean checked) {
        switch(checkboxId) {
            //Energy Production
            case 0 -> setEnergyProduction(checked);

            //Energy Consumption
            case 1 -> setEnergyConsumption(checked);
        }
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, CreativeBatteryBoxBlockEntity blockEntity) {
        if(level.isClient())
            return;

        transferInfiniteEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferInfiniteEnergy(World level, BlockPos blockPos, BlockState state, CreativeBatteryBoxBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.energyProduction)
            return;

        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.offset(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            EnergyStorage limitingEnergyStorage = EnergyStorage.SIDED.find(level, testPos, direction.getOpposite());
            if(limitingEnergyStorage == null)
                continue;

            if(!limitingEnergyStorage.supportsInsertion())
                continue;

            long received;
            try(Transaction transaction = Transaction.openOuter()) {
                received = limitingEnergyStorage.insert(limitingEnergyStorage.getCapacity(), transaction);
            }

            try(Transaction transaction = Transaction.openOuter()) {
                limitingEnergyStorage.insert(received, transaction);
                transaction.commit();
            }
        }
    }
}