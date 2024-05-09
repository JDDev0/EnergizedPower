package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.ChargerBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.screen.ChargerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.ItemStackUtils;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtLong;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.Optional;
import java.util.stream.IntStream;

public class ChargerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate, RedstoneModeUpdate,
        ComparatorModeUpdate {
    public static final long CAPACITY = ModConfigs.COMMON_CHARGER_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_CHARGER_TRANSFER_RATE.getValue();

    public static final double CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_CHARGER_CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER.getValue();

    final CachedSidedInventoryStorage<ChargerBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    final EnergizedPowerLimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;

    protected final PropertyDelegate data;
    private long energyConsumptionLeft = -1;

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

    public ChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CHARGER_ENTITY, blockPos, blockState);

        internalInventory = new SimpleInventory(1) {
            @Override
            public int getMaxCountPerStack() {
                return 1;
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(world == null || RecipeUtils.isIngredientOfAny(world, ChargerRecipe.Type.INSTANCE, stack))
                    return true;

                if(slot == 0) {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(energyStorage == null)
                        return false;

                    return energyStorage.supportsInsertion();
                }

                return super.isValid(slot, stack);
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.areItemsEqual(stack, itemStack) ||
                            (!ItemStack.areNbtEqual(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(EnergyStorageUtil.isEnergyStorage(stack) && EnergyStorageUtil.isEnergyStorage(itemStack)))))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                ChargerBlockEntity.this.markDirty();
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

            ItemStack itemStack = internalInventory.getStack(i);
            if(world != null && RecipeUtils.isResultOfAny(world, ChargerRecipe.Type.INSTANCE, itemStack))
                return true;

            if(world == null || RecipeUtils.isIngredientOfAny(world, ChargerRecipe.Type.INSTANCE, itemStack))
                return false;

            if(!EnergyStorageUtil.isEnergyStorage(itemStack))
                return true;

            EnergyStorage energyStorage = EnergyStorage.ITEM.find(itemStack, ContainerItemContext.
                    ofSingleSlot(InventoryStorage.of(internalInventory, null).getSlots().get(i)));
            if(energyStorage == null)
                return true;

            if(!energyStorage.supportsInsertion())
                return true;

            return energyStorage.getAmount() == energyStorage.getCapacity();
        });
        cachedSidedInventoryStorage = new CachedSidedInventoryStorage<>(inventory);

        internalEnergyStorage = new EnergizedPowerEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
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
        energyStorage = new EnergizedPowerLimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0);

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1, 2, 3 -> ByteUtils.get2Bytes(ChargerBlockEntity.this.energyConsumptionLeft, index);
                    case 4 -> redstoneMode.ordinal();
                    case 5 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2, 3 -> {}
                    case 4 -> ChargerBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 5 -> ChargerBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
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
        return Text.translatable("container.energizedpower.charger");
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
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeLong(internalEnergyStorage.getAmount());
        buffer.writeLong(internalEnergyStorage.getCapacity());
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.ENERGY_SYNC_ID, buffer);
        
        return new ChargerMenu(id, this, inventory, internalInventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.stacks));
        nbt.putLong("energy", internalEnergyStorage.getAmount());

        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.stacks);
        internalEnergyStorage.setAmountWithoutUpdate(nbt.getLong("energy"));

        energyConsumptionLeft = nbt.getLong("recipe.energy_consumption_left");

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode"));
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.stacks);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ChargerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.redstoneMode.isActive(state.get(ChargerBlock.POWERED)))
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.internalInventory.getStack(0);
            long energyConsumptionPerTick = 0;

            Optional<ChargerRecipe> recipe = level.getRecipeManager().getFirstMatch(ChargerRecipe.Type.INSTANCE, blockEntity.internalInventory, level);
            if(recipe.isPresent()) {
                if(blockEntity.energyConsumptionLeft == -1)
                    blockEntity.energyConsumptionLeft = (long)(recipe.get().getEnergyConsumption() * CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER);

                if(blockEntity.internalEnergyStorage.getAmount() == 0) {
                    markDirty(level, blockPos, state);

                    return;
                }

                energyConsumptionPerTick = Math.min(blockEntity.energyConsumptionLeft, Math.min(MAX_RECEIVE,
                        blockEntity.internalEnergyStorage.getAmount()));
            }else {
                if(!EnergyStorageUtil.isEnergyStorage(stack))
                    return;

                EnergyStorage energyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                        ofSingleSlot(InventoryStorage.of(blockEntity.internalInventory, null).getSlots().get(0)));
                if(energyStorage == null)
                    return;

                if(!energyStorage.supportsInsertion())
                    return;

                blockEntity.energyConsumptionLeft = energyStorage.getCapacity() - energyStorage.getAmount();

                if(blockEntity.internalEnergyStorage.getAmount() == 0) {
                    markDirty(level, blockPos, state);

                    return;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    energyConsumptionPerTick = energyStorage.insert(Math.min(MAX_RECEIVE,
                            blockEntity.internalEnergyStorage.getAmount()), transaction);
                    transaction.commit();
                }
            }

            if(blockEntity.energyConsumptionLeft < 0 || energyConsumptionPerTick < 0) {
                //Reset progress for invalid values

                blockEntity.resetProgress();
                markDirty(level, blockPos, state);

                return;
            }

            try(Transaction transaction = Transaction.openOuter()) {
                energyConsumptionPerTick = blockEntity.internalEnergyStorage.extract(energyConsumptionPerTick, transaction);
                transaction.commit();
            }

            blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

            if(blockEntity.energyConsumptionLeft <= 0) {
                recipe.ifPresent(chargerRecipe ->
                        blockEntity.internalInventory.setStack(0, ItemStackUtils.copyWithCount(chargerRecipe.getOutput(), 1)));

                blockEntity.resetProgress();
            }

            markDirty(level, blockPos, state);
        }else {
            blockEntity.resetProgress();
            markDirty(level, blockPos, state);
        }
    }

    private void resetProgress() {
        energyConsumptionLeft = -1;
    }

    private boolean hasRecipe() {
        ItemStack stack = internalInventory.getStack(0);

        Optional<ChargerRecipe> recipe = world == null?Optional.empty():
                world.getRecipeManager().getFirstMatch(ChargerRecipe.Type.INSTANCE, internalInventory, world);

        if(recipe.isPresent())
            return true;

        return EnergyStorageUtil.isEnergyStorage(stack);
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