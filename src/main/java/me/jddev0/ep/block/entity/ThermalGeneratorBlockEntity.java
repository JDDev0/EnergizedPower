package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ThermalGeneratorBlock;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.ThermalGeneratorRecipe;
import me.jddev0.ep.screen.ThermalGeneratorMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ThermalGeneratorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate,
        FluidStoragePacketUpdate, RedstoneModeUpdate, ComparatorModeUpdate {
    private static final long CAPACITY = ModConfigs.COMMON_THERMAL_GENERATOR_CAPACITY.getValue();
    private static final long MAX_EXTRACT = ModConfigs.COMMON_THERMAL_GENERATOR_TRANSFER_RATE.getValue();

    public static final double ENERGY_PRODUCTION_MULTIPLIER = ModConfigs.COMMON_THERMAL_GENERATOR_ENERGY_PRODUCTION_MULTIPLIER.getValue();

    private final UpgradeModuleInventory upgradeModuleInventory = new UpgradeModuleInventory(
            UpgradeModuleModifier.ENERGY_CAPACITY
    );
    private final InventoryChangedListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    final EnergizedPowerLimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;

    final SimpleFluidStorage fluidStorage;

    protected final PropertyDelegate data;

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private @NotNull ComparatorMode comparatorMode = ComparatorMode.FLUID;

    public ThermalGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.THERMAL_GENERATOR_ENTITY, blockPos, blockState);

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
        energyStorage = new EnergizedPowerLimitingEnergyStorage(internalEnergyStorage, 0, MAX_EXTRACT) {
            @Override
            public long getMaxExtract() {
                return Math.max(1, (long)Math.ceil(maxExtract * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };

        fluidStorage = new SimpleFluidStorage(FluidUtils.convertMilliBucketsToDroplets(
                ModConfigs.COMMON_THERMAL_GENERATOR_FLUID_TANK_CAPACITY.getValue() * 1000)) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeInt(0);
                    getFluid().toPacket(buffer);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            ModMessages.FLUID_SYNC_ID, buffer
                    );
                }
            }

            private boolean isFluidValid(FluidVariant variant) {
                if(world == null)
                    return false;

                List<ThermalGeneratorRecipe> recipes = world.getRecipeManager().listAllOfType(ThermalGeneratorRecipe.Type.INSTANCE);

                return recipes.stream().map(ThermalGeneratorRecipe::getInput).
                        anyMatch(inputs -> Arrays.stream(inputs).anyMatch(input -> variant.getFluid() == input));
            }

            @Override
            protected boolean canInsert(FluidVariant variant) {
                return isFluidValid(variant);
            }

            @Override
            protected boolean canExtract(FluidVariant variant) {
                return isFluidValid(variant);
            }
        };

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                if(index == 4)
                    return redstoneMode.ordinal();
                else if(index == 5)
                    return comparatorMode.ordinal();

                if(world == null || index > 3)
                    return 0;

                List<ThermalGeneratorRecipe> recipes = world.getRecipeManager().listAllOfType(ThermalGeneratorRecipe.Type.INSTANCE);

                long rawProduction = 0;
                outer:
                for(ThermalGeneratorRecipe recipe:recipes) {
                    for(Fluid fluid:recipe.getInput()) {
                        if(fluidStorage.getFluid().getFluid() == fluid) {
                            rawProduction = recipe.getEnergyProduction();
                            rawProduction = (long)(rawProduction * ENERGY_PRODUCTION_MULTIPLIER);

                            break outer;
                        }
                    }
                }
                //Calculate real production (raw production is in x E per 1000 mB, use fluid amount without cap)
                long productionLeft = (long)(rawProduction * fluidStorage.getFluid().getMilliBucketsAmount() / 1000.);

                return switch(index) {
                    case 0, 1, 2, 3 -> ByteUtils.get2Bytes(productionLeft, index);
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2, 3 -> {}
                    case 4 -> ThermalGeneratorBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 5 -> ThermalGeneratorBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 6;
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.thermal_generator");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeLong(internalEnergyStorage.getAmount());
        buffer.writeLong(internalEnergyStorage.getCapacity());
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.ENERGY_SYNC_ID, buffer);

        buffer = PacketByteBufs.create();
        buffer.writeInt(0);
        fluidStorage.getFluid().toPacket(buffer);
        buffer.writeLong(fluidStorage.getCapacity());
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.FLUID_SYNC_ID, buffer);

        return new ThermalGeneratorMenu(id, this, inventory, upgradeModuleInventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public int getRedstoneOutput() {
        return switch(comparatorMode) {
            case ITEM -> 0;
            case FLUID -> FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
        };
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        //Save Upgrade Module Inventory first
        nbt.put("upgrade_module_inventory", upgradeModuleInventory.saveToNBT());

        nbt.putLong("energy", internalEnergyStorage.getAmount());
        nbt.put("fluid", fluidStorage.toNBT(new NbtCompound()));

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());

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
        fluidStorage.fromNBT(nbt.getCompound("fluid"));

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = nbt.contains("configuration.comparator_mode")?
                ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode")):ComparatorMode.FLUID;
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, upgradeModuleInventory);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(blockEntity.redstoneMode.isActive(state.get(ThermalGeneratorBlock.POWERED)))
            tickRecipe(level, blockPos, state, blockEntity);

        transferEnergy(level, blockPos, state, blockEntity);
    }

    private static void tickRecipe(World level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClient())
            return;

        List<ThermalGeneratorRecipe> recipes = level.getRecipeManager().listAllOfType(ThermalGeneratorRecipe.Type.INSTANCE);

        long rawProduction = 0;
        outer:
        for(ThermalGeneratorRecipe recipe:recipes) {
            for(Fluid fluid:recipe.getInput()) {
                if(blockEntity.fluidStorage.getFluid().getFluid() == fluid) {
                    rawProduction = recipe.getEnergyProduction();
                    rawProduction = (long)(rawProduction * ENERGY_PRODUCTION_MULTIPLIER);

                    break outer;
                }
            }
        }

        if(rawProduction > 0 && blockEntity.internalEnergyStorage.getAmount() < blockEntity.internalEnergyStorage.getCapacity()) {
            //Calculate real production (raw production is in x E per 1000 mB, 50 mB of fluid can be consumed per tick)
            long production = (long)(rawProduction * (Math.min(
                    blockEntity.fluidStorage.getFluid().getMilliBucketsAmount(), 50) / 1000.));

            //Cap production
            production = Math.min(production, blockEntity.internalEnergyStorage.getCapacity() - blockEntity.internalEnergyStorage.getAmount());

            long fluidAmount = (long)((double)production/rawProduction * 1000);

            //Re-calculate energy production (Prevents draining of not enough fluid)
            production = (long)(rawProduction * fluidAmount / 1000.);

            try(Transaction transaction = Transaction.openOuter()) {
                blockEntity.fluidStorage.extract(blockEntity.fluidStorage.variant,
                        FluidUtils.convertMilliBucketsToDroplets(fluidAmount), transaction);

                blockEntity.internalEnergyStorage.insert(production, transaction);
                transaction.commit();
            }
        }
    }

    private static void transferEnergy(World level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClient())
            return;

        List<EnergyStorage> consumerItems = new LinkedList<>();
        List<Long> consumerEnergyValues = new LinkedList<>();
        long consumptionSum = 0;
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

            try(Transaction transaction = Transaction.openOuter()) {
                long received = energyStorage.insert(Math.min(blockEntity.energyStorage.getMaxExtract(),
                        blockEntity.internalEnergyStorage.getAmount()), transaction);

                if(received <= 0)
                    continue;

                consumptionSum += received;
                consumerItems.add(energyStorage);
                consumerEnergyValues.add(received);
            }
        }

        List<Long> consumerEnergyDistributed = new LinkedList<>();
        for(int i = 0;i < consumerItems.size();i++)
            consumerEnergyDistributed.add(0L);

        long consumptionLeft = Math.min(blockEntity.energyStorage.getMaxExtract(),
                Math.min(blockEntity.internalEnergyStorage.getAmount(), consumptionSum));
        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.internalEnergyStorage.extract(consumptionLeft, transaction);
            transaction.commit();
        }

        int divisor = consumerItems.size();
        outer:
        while(consumptionLeft > 0) {
            long consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < consumerEnergyValues.size();i++) {
                long consumptionDistributed = consumerEnergyDistributed.get(i);
                long consumptionOfConsumerLeft = consumerEnergyValues.get(i) - consumptionDistributed;

                long consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                consumerEnergyDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < consumerItems.size();i++) {
            long energy = consumerEnergyDistributed.get(i);
            if(energy > 0) {
                try(Transaction transaction = Transaction.openOuter()) {
                    consumerItems.get(i).insert(energy, transaction);
                    transaction.commit();
                }
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

    public FluidStack getFluid(int tank) {
        return fluidStorage.getFluid();
    }

    public long getTankCapacity(int tank) {
        return fluidStorage.getCapacity();
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.setAmountWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(long capacity) {
        internalEnergyStorage.setCapacityWithoutUpdate(capacity);
    }

    public long getEnergy() {
        return internalEnergyStorage.getAmount();
    }

    public long getCapacity() {
        return internalEnergyStorage.getCapacity();
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, long capacity) {
        //Does nothing (capacity is final)
    }

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        markDirty();
    }

    @Override
    public void setNextComparatorMode() {
        do {
            comparatorMode = ComparatorMode.fromIndex(comparatorMode.ordinal() + 1);
        }while(comparatorMode == ComparatorMode.ITEM); //Prevent the ITEM comparator mode from being selected
        markDirty();
    }
}