package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.MetalPressRecipe;
import me.jddev0.ep.registry.tags.EnergizedPowerItemTags;
import me.jddev0.ep.screen.MetalPressMenu;
import me.jddev0.ep.util.ByteUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
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
import team.reborn.energy.api.base.LimitingEnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.Optional;
import java.util.stream.IntStream;

public class MetalPressBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate {
    public static final long CAPACITY = ModConfigs.COMMON_METAL_PRESS_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_METAL_PRESS_TRANSFER_RATE.getValue();
    private static final long ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_METAL_PRESS_ENERGY_CONSUMPTION_PER_TICK.getValue();

    final CachedSidedInventoryStorage<MetalPressBlockEntity> cachedSidedInventoryStorageTop;
    final InputOutputItemHandler sidedInventoryTop;

    final CachedSidedInventoryStorage<MetalPressBlockEntity> cachedSidedInventoryStorageOthers;
    final InputOutputItemHandler sidedInventoryOthers;
    private final SimpleInventory internalInventory;

    final LimitingEnergyStorage energyStorage;
    private final SimpleEnergyStorage internalEnergyStorage;

    protected final PropertyDelegate data;
    private int progress;
    private int maxProgress = ModConfigs.COMMON_METAL_PRESS_RECIPE_DURATION.getValue();
    private long energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    public MetalPressBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.METAL_PRESS_ENTITY, blockPos, blockState);

        internalInventory = new SimpleInventory(3) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> world == null || world.getRecipeManager().listAllOfType(MetalPressRecipe.Type.INSTANCE).stream().
                            map(MetalPressRecipe::getInput).anyMatch(ingredient -> ingredient.test(stack));
                    case 1 -> world == null || stack.isIn(EnergizedPowerItemTags.METAL_PRESS_MOLDS) &&
                            (getStack(1).isEmpty() || stack.getCount() == 1);
                    case 2 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0 || slot == 1) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.areItemsEqual(stack, itemStack) ||
                            !ItemStack.canCombine(stack, itemStack)))
                        resetProgress(pos, world.getBlockState(pos));
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                MetalPressBlockEntity.this.markDirty();
            }
        };
        sidedInventoryTop = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 3).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }

            @Override
            public int getMaxCountPerStack() {
                return 1;
            }
        }, (i, stack) -> i == 1, i -> i == 1);
        cachedSidedInventoryStorageTop = new CachedSidedInventoryStorage<>(sidedInventoryTop);
        sidedInventoryOthers = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 3).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i == 0, i -> i == 2);
        cachedSidedInventoryStorageOthers = new CachedSidedInventoryStorage<>(sidedInventoryOthers);

        internalEnergyStorage = new SimpleEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(amount);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            ModMessages.ENERGY_SYNC_ID, buffer
                    );
                }
            }
        };
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0);

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(MetalPressBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(MetalPressBlockEntity.this.maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(MetalPressBlockEntity.this.energyConsumptionLeft, index - 4);
                    case 8 -> hasEnoughEnergy?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> MetalPressBlockEntity.this.progress = ByteUtils.with2Bytes(
                            MetalPressBlockEntity.this.progress, (short)value, index
                    );
                    case 2, 3 -> MetalPressBlockEntity.this.maxProgress = ByteUtils.with2Bytes(
                            MetalPressBlockEntity.this.maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6, 7, 8 -> {}
                }
            }

            @Override
            public int size() {
                return 9;
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.metal_press");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeLong(internalEnergyStorage.amount);
        buffer.writeLong(internalEnergyStorage.capacity);
        buffer.writeBlockPos(getPos());

        return new MetalPressMenu(id, this, inventory, internalInventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(internalInventory);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.stacks));
        nbt.putLong("energy", internalEnergyStorage.amount);

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.stacks);
        internalEnergyStorage.amount = nbt.getLong("energy");

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getLong("recipe.energy_consumption_left");
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.stacks);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, MetalPressBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(hasRecipe(blockEntity)) {
            Optional<MetalPressRecipe> recipe = level.getRecipeManager().getFirstMatch(MetalPressRecipe.Type.INSTANCE, blockEntity.internalInventory, level);
            if(recipe.isEmpty())
                return;

            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = ENERGY_USAGE_PER_TICK * blockEntity.maxProgress;

            if(ENERGY_USAGE_PER_TICK <= blockEntity.internalEnergyStorage.amount) {
                blockEntity.hasEnoughEnergy = true;

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    markDirty(level, blockPos, state);

                    return;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    blockEntity.internalEnergyStorage.extract(ENERGY_USAGE_PER_TICK, transaction);
                    transaction.commit();
                }
                blockEntity.energyConsumptionLeft -= ENERGY_USAGE_PER_TICK;

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    craftItem(blockPos, state, blockEntity);

                markDirty(level, blockPos, state);
            }else {
                blockEntity.hasEnoughEnergy = false;
                markDirty(level, blockPos, state);
            }
        }else {
            blockEntity.resetProgress(blockPos, state);
            markDirty(level, blockPos, state);
        }
    }

    private void resetProgress(BlockPos blockPos, BlockState state) {
        progress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = true;
    }

    private static void craftItem(BlockPos blockPos, BlockState state, MetalPressBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<MetalPressRecipe> recipe = level.getRecipeManager().getFirstMatch(MetalPressRecipe.Type.INSTANCE, blockEntity.internalInventory, level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        blockEntity.internalInventory.removeStack(0, recipe.get().getInputCount());
        blockEntity.internalInventory.setStack(2, new ItemStack(recipe.get().getOutput(level.getRegistryManager()).getItem(),
                blockEntity.internalInventory.getStack(2).getCount() + recipe.get().getOutput(level.getRegistryManager()).getCount()));

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(MetalPressBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<MetalPressRecipe> recipe = level.getRecipeManager().getFirstMatch(MetalPressRecipe.Type.INSTANCE, blockEntity.internalInventory, level);

        return recipe.isPresent() && canInsertItemIntoOutputSlot(blockEntity.internalInventory, recipe.get().getOutput(level.getRegistryManager()));
    }

    private static boolean canInsertItemIntoOutputSlot(Inventory inventory, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getStack(2);

        return (inventoryItemStack.isEmpty() || ItemStack.canCombine(inventoryItemStack, itemStack)) &&
                inventoryItemStack.getMaxCount() >= inventoryItemStack.getCount() + itemStack.getCount();
    }

    public long getEnergy() {
        return internalEnergyStorage.amount;
    }

    public long getCapacity() {
        return internalEnergyStorage.capacity;
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