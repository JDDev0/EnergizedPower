package me.jddev0.ep.block.entity;

import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.InfinityEnergyStorage;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.screen.CreativeBatteryBoxMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public class CreativeBatteryBoxBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, EnergyStoragePacketUpdate,
        CheckboxUpdate {
    final InfinityEnergyStorage energyStorage;

    protected final PropertyDelegate data;

    private boolean energyProduction = true;
    private boolean energyConsumption;

    public CreativeBatteryBoxBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CREATIVE_BATTERY_BOX_ENTITY, blockPos, blockState);

        energyStorage = new InfinityEnergyStorage() {
            @Override
            public long extract(long maxAmount, TransactionContext transaction) {
                if(energyProduction)
                    return super.extract(maxAmount, transaction);

                return 0;
            }

            @Override
            public long insert(long maxAmount, TransactionContext transaction) {
                if(energyConsumption)
                    return super.insert(maxAmount, transaction);

                return 0;
            }
        };
        data = new PropertyDelegate() {
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

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.creative_battery_box");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new CreativeBatteryBoxMenu(id, this, inventory, data);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.put("energy", new NbtCompound());

        nbt.putBoolean("energy_production", energyProduction);
        nbt.putBoolean("energy_consumption", energyConsumption);

        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        if(!(nbt.get("energy") instanceof NbtCompound))
            throw new IllegalArgumentException("Tag must be of type IntTag!");

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

            EnergyStorage energyStorage = EnergyStorage.SIDED.find(level, testPos, direction.getOpposite());
            if(energyStorage == null)
                continue;

            if(!energyStorage.supportsInsertion())
                continue;

            long received;
            try(Transaction transaction = Transaction.openOuter()) {
                received = energyStorage.insert(energyStorage.getCapacity(), transaction);
            }

            try(Transaction transaction = Transaction.openOuter()) {
                energyStorage.insert(received, transaction);
                transaction.commit();
            }
        }
    }

    public long getEnergy() {
        return Long.MAX_VALUE;
    }

    public long getCapacity() {
        return Long.MAX_VALUE;
    }

    @Override
    public void setEnergy(long energy) {
        //Does nothing (energy is final)
    }

    @Override
    public void setCapacity(long capacity) {
        //Does nothing (capacity is final)
    }
}