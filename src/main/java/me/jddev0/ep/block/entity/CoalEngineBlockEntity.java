package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.CoalEngineBlock;
import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.screen.CoalEngineMenu;
import me.jddev0.ep.util.ByteUtils;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import me.jddev0.ep.util.EnergyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class CoalEngineBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, EnergyStoragePacketUpdate, RedstoneModeUpdate,
        ComparatorModeUpdate {
    public static final long CAPACITY = ModConfigs.COMMON_COAL_ENGINE_CAPACITY.getValue();
    public static final long MAX_EXTRACT = ModConfigs.COMMON_COAL_ENGINE_TRANSFER_RATE.getValue();

    public static final double ENERGY_PRODUCTION_MULTIPLIER = ModConfigs.COMMON_COAL_ENGINE_ENERGY_PRODUCTION_MULTIPLIER.getValue();

    final CachedSidedInventoryStorage<CoalEngineBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    final EnergizedPowerLimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;

    protected final PropertyDelegate data;
    private int progress;
    private int maxProgress;
    private long energyProductionLeft = -1;
    private boolean hasEnoughCapacityForProduction;

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

    public CoalEngineBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.COAL_ENGINE_ENTITY, blockPos, blockState);

        internalInventory = new SimpleInventory(1) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot == 0) {
                    Integer burnTime = FuelRegistry.INSTANCE.get(stack.getItem());
                    return burnTime != null && burnTime > 0;
                }

                return super.isValid(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                CoalEngineBlockEntity.this.markDirty();
            }
        };
        inventory = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 1).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> true, i -> {
            if(i != 0)
                return false;

            //Do not allow extraction of fuel items, allow for non fuel items (Bucket of Lava -> Empty Bucket)
            ItemStack item = internalInventory.getStack(i);
            Integer burnTime = FuelRegistry.INSTANCE.get(item.getItem());
            return burnTime == null || burnTime <= 0;
        });
        cachedSidedInventoryStorage = new CachedSidedInventoryStorage<>(inventory);

        internalEnergyStorage = new EnergizedPowerEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            new EnergySyncS2CPacket(getAmount(), getCapacity(), getPos())
                    );
                }
            }
        };
        energyStorage = new EnergizedPowerLimitingEnergyStorage(internalEnergyStorage, 0, MAX_EXTRACT);
        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(CoalEngineBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(CoalEngineBlockEntity.this.maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(CoalEngineBlockEntity.this.energyProductionLeft, index - 4);
                    case 8 -> hasEnoughCapacityForProduction?1:0;
                    case 9 -> redstoneMode.ordinal();
                    case 10 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> CoalEngineBlockEntity.this.progress = ByteUtils.with2Bytes(
                            CoalEngineBlockEntity.this.progress, (short)value, index
                    );
                    case 2, 3 -> CoalEngineBlockEntity.this.maxProgress = ByteUtils.with2Bytes(
                            CoalEngineBlockEntity.this.maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6, 7, 8 -> {}
                    case 9 -> CoalEngineBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 10 -> CoalEngineBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 11;
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.coal_engine");
    }

    public int getRedstoneOutput() {
        return switch(comparatorMode) {
            case ITEM -> ScreenHandler.calculateComparatorOutput(internalInventory);
            case FLUID -> 0;
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player,
                new EnergySyncS2CPacket(internalEnergyStorage.getAmount(), internalEnergyStorage.getCapacity(), getPos()));
        
        return new CoalEngineMenu(id, this, inventory, internalInventory, this.data);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.heldStacks, registries));
        nbt.putLong("energy", internalEnergyStorage.getAmount());

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.max_progress", NbtInt.of(maxProgress));
        nbt.put("recipe.energy_production_left", NbtLong.of(energyProductionLeft));

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());

        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.heldStacks, registries);
        internalEnergyStorage.setAmountWithoutUpdate(nbt.getLong("energy"));

        progress = nbt.getInt("recipe.progress");
        maxProgress = nbt.getInt("recipe.max_progress");
        energyProductionLeft = nbt.getLong("recipe.energy_production_left");

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode"));
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.heldStacks);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, CoalEngineBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(blockEntity.redstoneMode.isActive(state.get(CoalEngineBlock.POWERED)))
            tickRecipe(level, blockPos, state, blockEntity);

        transferEnergy(level, blockPos, state, blockEntity);
    }


    private static void tickRecipe(World level, BlockPos blockPos, BlockState state, CoalEngineBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(blockEntity.maxProgress > 0 || hasRecipe(blockEntity)) {
            ItemStack item = blockEntity.internalInventory.getStack(0);

            Integer burnTime = FuelRegistry.INSTANCE.get(item.getItem());
            long energyProduction = burnTime == null?-1:burnTime;
            energyProduction = (long)(energyProduction * ENERGY_PRODUCTION_MULTIPLIER);
            if(blockEntity.progress == 0)
                blockEntity.energyProductionLeft = energyProduction;

            //Change max progress if item would output more than max extract
            if(blockEntity.maxProgress == 0) {
                if(energyProduction / 100 <= MAX_EXTRACT)
                    blockEntity.maxProgress = 100;
                else
                    blockEntity.maxProgress = (int)Math.ceil((double)energyProduction / MAX_EXTRACT);
            }

            //TODO improve (alternate values +/- 1 per x recipes instead of changing last energy production tick)
            long energyProductionPerTick = (long)Math.ceil((double)blockEntity.energyProductionLeft / (blockEntity.maxProgress - blockEntity.progress));
            if(blockEntity.progress == blockEntity.maxProgress - 1)
                energyProductionPerTick = blockEntity.energyProductionLeft;

            if(energyProductionPerTick <= blockEntity.energyStorage.getCapacity() - blockEntity.internalEnergyStorage.getAmount()) {
                if(blockEntity.progress == 0) {
                    //Remove item instantly else the item could be removed before finished and energy was cheated

                    if(!item.getRecipeRemainder().isEmpty())
                        blockEntity.internalInventory.setStack(0, item.getRecipeRemainder());
                    else
                        blockEntity.internalInventory.removeStack(0, 1);
                }

                if(!level.getBlockState(blockPos).contains(CoalEngineBlock.LIT) || !level.getBlockState(blockPos).get(CoalEngineBlock.LIT)) {
                    blockEntity.hasEnoughCapacityForProduction = true;
                    level.setBlockState(blockPos, state.with(CoalEngineBlock.LIT, Boolean.TRUE), 3);
                }

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyProductionLeft < 0 ||
                        energyProductionPerTick < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    markDirty(level, blockPos, state);

                    return;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    blockEntity.internalEnergyStorage.insert(energyProductionPerTick, transaction);
                    transaction.commit();
                }

                blockEntity.energyProductionLeft -= energyProductionPerTick;

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    blockEntity.resetProgress(blockPos, state);

                markDirty(level, blockPos, state);
            }else {
                blockEntity.hasEnoughCapacityForProduction = false;
                markDirty(level, blockPos, state);
            }
        }else {
            blockEntity.resetProgress(blockPos, state);
            markDirty(level, blockPos, state);
        }
    }

    private static void transferEnergy(World level, BlockPos blockPos, BlockState state, CoalEngineBlockEntity blockEntity) {
        if(level.isClient())
            return;

        List<EnergyStorage> consumerItems = new LinkedList<>();
        List<Long> consumerEnergyValues = new LinkedList<>();
        int consumptionSum = 0;
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
                long received = energyStorage.insert(Math.min(MAX_EXTRACT, blockEntity.internalEnergyStorage.getAmount()), transaction);

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

        long consumptionLeft = Math.min(MAX_EXTRACT, Math.min(blockEntity.internalEnergyStorage.getAmount(), consumptionSum));
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

    private void resetProgress(BlockPos blockPos, BlockState state) {
        progress = 0;
        maxProgress = 0;
        energyProductionLeft = -1;
        hasEnoughCapacityForProduction = true;

        world.setBlockState(blockPos, state.with(CoalEngineBlock.LIT, false), 3);
    }

    private static boolean hasRecipe(CoalEngineBlockEntity blockEntity) {
        ItemStack item = blockEntity.internalInventory.getStack(0);

        Integer burnTime = FuelRegistry.INSTANCE.get(item.getItem());
        if(burnTime == null || burnTime <= 0)
            return false;

        return item.getRecipeRemainder().isEmpty() || item.getCount() == 1;
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

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        markDirty();
    }

    @Override
    public void setNextComparatorMode() {
        do {
            comparatorMode = ComparatorMode.fromIndex(comparatorMode.ordinal() + 1);
        }while(comparatorMode == ComparatorMode.FLUID); //Prevent the FLUID comparator mode from being selected
        markDirty();
    }
}