package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AssemblingMachineBlock;
import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.AssemblingMachineRecipe;
import me.jddev0.ep.screen.AssemblingMachineMenu;
import me.jddev0.ep.util.ByteUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
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

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

public class AssemblingMachineBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate, RedstoneModeUpdate {
    private static final long CAPACITY = ModConfigs.COMMON_ASSEMBLING_MACHINE_CAPACITY.getValue();

    private static final long ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_ASSEMBLING_MACHINE_ENERGY_CONSUMPTION_PER_TICK.getValue();

    final CachedSidedInventoryStorage<AssemblingMachineBlockEntity> cachedSidedInventoryStorageTopBottom;
    final InputOutputItemHandler sidedInventoryTopBottom;

    final CachedSidedInventoryStorage<AssemblingMachineBlockEntity> cachedSidedInventoryStorageFront;
    final InputOutputItemHandler sidedInventoryFront;

    final CachedSidedInventoryStorage<AssemblingMachineBlockEntity> cachedSidedInventoryStorageBack;
    final InputOutputItemHandler sidedInventoryBack;

    final CachedSidedInventoryStorage<AssemblingMachineBlockEntity> cachedSidedInventoryStorageLeft;
    final InputOutputItemHandler sidedInventoryLeft;

    final CachedSidedInventoryStorage<AssemblingMachineBlockEntity> cachedSidedInventoryStorageRight;
    final InputOutputItemHandler sidedInventoryRight;

    private final SimpleInventory internalInventory;

    final LimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;

    protected final PropertyDelegate data;
    private int progress;
    private int maxProgress = ModConfigs.COMMON_ASSEMBLING_MACHINE_RECIPE_DURATION.getValue();
    private long energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    public AssemblingMachineBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ASSEMBLING_MACHINE_ENTITY, blockPos, blockState);

        internalInventory = new SimpleInventory(5) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2, 3 -> world == null || world.getRecipeManager().
                            listAllOfType(AssemblingMachineRecipe.Type.INSTANCE).stream().
                            map(RecipeEntry::value).map(AssemblingMachineRecipe::getInputs).anyMatch(inputs ->
                                    Arrays.stream(inputs).map(AssemblingMachineRecipe.IngredientWithCount::input).
                                            anyMatch(ingredient -> ingredient.test(stack)));
                    case 4 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 4) {
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

                AssemblingMachineBlockEntity.this.markDirty();
            }
        };
        sidedInventoryTopBottom = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 5).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i >= 0 && i < 4, i -> i == 4);
        cachedSidedInventoryStorageTopBottom = new CachedSidedInventoryStorage<>(sidedInventoryTopBottom);
        sidedInventoryFront = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 5).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i == 3, i -> i == 4);
        cachedSidedInventoryStorageFront = new CachedSidedInventoryStorage<>(sidedInventoryFront);
        sidedInventoryBack = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 5).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i == 0, i -> i == 4);
        cachedSidedInventoryStorageBack = new CachedSidedInventoryStorage<>(sidedInventoryBack);
        sidedInventoryLeft = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 5).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i == 1, i -> i == 4);
        cachedSidedInventoryStorageLeft = new CachedSidedInventoryStorage<>(sidedInventoryLeft);
        sidedInventoryRight = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 5).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i == 2, i -> i == 4);
        cachedSidedInventoryStorageRight = new CachedSidedInventoryStorage<>(sidedInventoryRight);

        internalEnergyStorage = new EnergizedPowerEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
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
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, ModConfigs.COMMON_ASSEMBLING_MACHINE_TRANSFER_RATE.getValue(), 0);

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(AssemblingMachineBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(AssemblingMachineBlockEntity.this.maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(AssemblingMachineBlockEntity.this.energyConsumptionLeft, index - 4);
                    case 8 -> hasEnoughEnergy?1:0;
                    case 9 -> redstoneMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> AssemblingMachineBlockEntity.this.progress = ByteUtils.with2Bytes(
                            AssemblingMachineBlockEntity.this.progress, (short)value, index
                    );
                    case 2, 3 -> AssemblingMachineBlockEntity.this.maxProgress = ByteUtils.with2Bytes(
                            AssemblingMachineBlockEntity.this.maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6, 7, 8 -> {}
                    case 9 -> AssemblingMachineBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 10;
            }
        };
    }

    public Storage<ItemVariant> getInventoryStorageForDirection(Direction side) {
        if(side == null)
            return null;

        Direction facing = getCachedState().get(AssemblingMachineBlock.FACING);

        if(facing == side)
            return cachedSidedInventoryStorageFront.apply(side);

        if(facing.getOpposite() == side)
            return cachedSidedInventoryStorageBack.apply(side);

        if(facing.rotateYClockwise() == side)
            return cachedSidedInventoryStorageLeft.apply(side);

        if(facing.rotateYCounterclockwise() == side)
            return cachedSidedInventoryStorageRight.apply(side);

        return cachedSidedInventoryStorageTopBottom.apply(side);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.assembling_machine");
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(internalInventory);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeLong(internalEnergyStorage.amount);
        buffer.writeLong(internalEnergyStorage.capacity);
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.ENERGY_SYNC_ID, buffer);

        return new AssemblingMachineMenu(id, this, inventory, internalInventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.heldStacks));
        nbt.putLong("energy", internalEnergyStorage.amount);

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.heldStacks);
        internalEnergyStorage.amount = nbt.getLong("energy");

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getLong("recipe.energy_consumption_left");

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.heldStacks);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AssemblingMachineBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.redstoneMode.isActive(state.get(AssemblingMachineBlock.POWERED)))
            return;

        if(hasRecipe(blockEntity)) {
            Optional<RecipeEntry<AssemblingMachineRecipe>> recipe = level.getRecipeManager().
                    getFirstMatch(AssemblingMachineRecipe.Type.INSTANCE, blockEntity.internalInventory, level);
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

    private static void craftItem(BlockPos blockPos, BlockState state, AssemblingMachineBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<RecipeEntry<AssemblingMachineRecipe>> recipe = level.getRecipeManager().
                getFirstMatch(AssemblingMachineRecipe.Type.INSTANCE, blockEntity.internalInventory, level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        AssemblingMachineRecipe.IngredientWithCount[] inputs = recipe.get().value().getInputs();

        boolean[] usedIndices = new boolean[4];
        for(int i = 0;i < 4;i++)
            usedIndices[i] = blockEntity.internalInventory.getStack(i).isEmpty();

        int len = Math.min(inputs.length, 4);
        for(int i = 0;i < len;i++) {
            AssemblingMachineRecipe.IngredientWithCount input = inputs[i];

            int indexMinCount = -1;
            int minCount = Integer.MAX_VALUE;

            for(int j = 0;j < 4;j++) {
                if(usedIndices[j])
                    continue;

                ItemStack item = blockEntity.internalInventory.getStack(j);

                if((indexMinCount == -1 || item.getCount() < minCount) && input.input().test(item) &&
                        item.getCount() >= input.count()) {
                    indexMinCount = j;
                    minCount = item.getCount();
                }
            }

            if(indexMinCount == -1)
                return; //Should never happen: Ingredient did not match any item

            usedIndices[indexMinCount] = true;

            blockEntity.internalInventory.removeStack(indexMinCount, input.count());
        }

        blockEntity.internalInventory.setStack(4, recipe.get().value().getResult(level.getRegistryManager()).copyWithCount(
                blockEntity.internalInventory.getStack(4).getCount() + recipe.get().value().getResult(level.getRegistryManager()).getCount()));

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(AssemblingMachineBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<RecipeEntry<AssemblingMachineRecipe>> recipe = level.getRecipeManager().
                getFirstMatch(AssemblingMachineRecipe.Type.INSTANCE, blockEntity.internalInventory, level);

        return recipe.isPresent() && canInsertItemIntoOutputSlot(blockEntity.internalInventory,
                recipe.get().value().getResult(level.getRegistryManager()));
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getStack(4);

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
        internalEnergyStorage.capacity = capacity;
    }

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        markDirty();
    }
}