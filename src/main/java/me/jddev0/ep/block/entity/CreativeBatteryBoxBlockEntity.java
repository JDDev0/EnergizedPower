package me.jddev0.ep.block.entity;

import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.block.entity.base.MenuEnergyStorageBlockEntity;
import me.jddev0.ep.energy.InfinityEnergyStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.BooleanValueContainerData;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.screen.CreativeBatteryBoxMenu;
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
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new BooleanValueContainerData(() -> energyProduction, value -> energyProduction = value),
                new BooleanValueContainerData(() -> energyConsumption, value -> energyConsumption = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new CreativeBatteryBoxMenu(id, this, inventory, data);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putBoolean("energy_production", energyProduction);
        view.putBoolean("energy_consumption", energyConsumption);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        energyProduction = view.getBooleanOr("energy_production", true);
        energyConsumption = view.getBooleanOr("energy_consumption", false);
    }

    public void setEnergyProduction(boolean energyProduction) {
        this.energyProduction = energyProduction;
        setChanged(level, getBlockPos(), getBlockState());
    }

    public void setEnergyConsumption(boolean energyConsumption) {
        this.energyConsumption = energyConsumption;
        setChanged(level, getBlockPos(), getBlockState());
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

    public static void tick(Level level, BlockPos blockPos, BlockState state, CreativeBatteryBoxBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        transferInfiniteEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferInfiniteEnergy(Level level, BlockPos blockPos, BlockState state, CreativeBatteryBoxBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(!blockEntity.energyProduction)
            return;

        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.relative(direction);

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