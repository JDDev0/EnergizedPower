package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AdvancedPoweredFurnaceBlock;
import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.AdvancedPoweredFurnaceMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
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

public class AdvancedPoweredFurnaceBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate {
    public static final long CAPACITY = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_TRANSFER_RATE.getValue();
    private static final long ENERGY_USAGE_PER_INPUT_PER_TICK = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_INPUT_PER_TICK.getValue();

    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    final CachedSidedInventoryStorage<AdvancedPoweredFurnaceBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    final LimitingEnergyStorage energyStorage;
    private final SimpleEnergyStorage internalEnergyStorage;

    protected final PropertyDelegate data;
    private int[] progress = new int[] {
            0, 0, 0
    };
    private int[] maxProgress = new int[] {
            0, 0, 0
    };
    private long[] energyConsumptionLeft = new long[] {
            -1, -1, -1
    };
    private boolean[] hasEnoughEnergy = new boolean[] {
            false, false, false
    };

    public AdvancedPoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ADVANCED_POWERED_FURNACE_ENTITY, blockPos, blockState);

        internalInventory = new SimpleInventory(6) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2 -> world == null || RecipeUtils.isIngredientOfAny(world, RecipeType.SMELTING, stack);
                    case 3, 4, 5 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.areItemsEqual(stack, itemStack) ||
                            !ItemStack.canCombine(stack, itemStack)))
                        resetProgress(slot, pos, world.getBlockState(pos));
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                AdvancedPoweredFurnaceBlockEntity.this.markDirty();
            }
        };
        inventory = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 6).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i >= 0 && i < 3, i -> i >= 3 && i < 6);
        cachedSidedInventoryStorage = new CachedSidedInventoryStorage<>(inventory);

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
                    case 0, 1, 2, 3 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.energyConsumptionLeft[0], index);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.energyConsumptionLeft[1], index - 4);
                    case 8, 9, 10, 11 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.energyConsumptionLeft[2], index - 8);
                    case 12, 13 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.progress[0], index - 12);
                    case 14, 15 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.maxProgress[0], index - 14);
                    case 16, 17 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.progress[1], index - 16);
                    case 18, 19 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.maxProgress[1], index - 18);
                    case 20, 21 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.progress[2], index - 20);
                    case 22, 23 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.maxProgress[2], index - 22);
                    case 24 -> AdvancedPoweredFurnaceBlockEntity.this.hasEnoughEnergy[0]?0:1;
                    case 25 -> AdvancedPoweredFurnaceBlockEntity.this.hasEnoughEnergy[1]?0:1;
                    case 26 -> AdvancedPoweredFurnaceBlockEntity.this.hasEnoughEnergy[2]?0:1;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 12, 13 -> AdvancedPoweredFurnaceBlockEntity.this.progress[0] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.progress[0], (short)value, index - 12
                    );
                    case 14, 15 -> AdvancedPoweredFurnaceBlockEntity.this.maxProgress[0] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.maxProgress[0], (short)value, index - 14
                    );
                    case 16, 17 -> AdvancedPoweredFurnaceBlockEntity.this.progress[1] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.progress[1], (short)value, index - 16
                    );
                    case 18, 19 -> AdvancedPoweredFurnaceBlockEntity.this.maxProgress[1] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.maxProgress[1], (short)value, index - 18
                    );
                    case 20, 21 -> AdvancedPoweredFurnaceBlockEntity.this.progress[2] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.progress[2], (short)value, index - 20
                    );
                    case 22, 23 -> AdvancedPoweredFurnaceBlockEntity.this.maxProgress[2] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.maxProgress[2], (short)value, index - 22
                    );
                    case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 24, 25, 26 -> {}
                }
            }

            @Override
            public int size() {
                return 27;
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.advanced_powered_furnace");
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

        return new AdvancedPoweredFurnaceMenu(id, this, inventory, internalInventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.stacks));
        nbt.putLong("energy", internalEnergyStorage.amount);

        for(int i = 0;i < 3;i++)
            nbt.put("recipe.progress." + i, NbtInt.of(progress[i]));
        for(int i = 0;i < 3;i++)
            nbt.put("recipe.max_progress." + i, NbtInt.of(maxProgress[i]));
        for(int i = 0;i < 3;i++)
            nbt.put("recipe.energy_consumption_left." + i, NbtLong.of(energyConsumptionLeft[i]));

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.stacks);
        internalEnergyStorage.amount = nbt.getLong("energy");

        for(int i = 0;i < 3;i++)
            progress[i] = nbt.getInt("recipe.progress." + i);
        for(int i = 0;i < 3;i++)
            maxProgress[i] = nbt.getInt("recipe.max_progress." + i);
        for(int i = 0;i < 3;i++)
            energyConsumptionLeft[i] = nbt.getLong("recipe.energy_consumption_left." + i);
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.stacks);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        if(level.isClient())
            return;

        for(int i = 0;i < 3;i++) {
            if(hasRecipe(i, blockEntity)) {
                SimpleInventory inventory = new SimpleInventory(2);
                inventory.setStack(0, blockEntity.internalInventory.getStack(i));
                inventory.setStack(1, blockEntity.internalInventory.getStack(3 + i));

                Optional<SmeltingRecipe> recipe = level.getRecipeManager().getFirstMatch(RecipeType.SMELTING, inventory, level);
                if(recipe.isEmpty())
                    continue;

                int cookingTime = recipe.get().getCookTime();
                if(blockEntity.maxProgress[i] == 0)
                    blockEntity.maxProgress[i] = (int)Math.ceil(cookingTime * RECIPE_DURATION_MULTIPLIER / 12.f); //Default Cooking Time = 200 -> maxProgress = 16 (= 200 / 12)

                if(blockEntity.energyConsumptionLeft[i] < 0)
                    blockEntity.energyConsumptionLeft[i] = ENERGY_USAGE_PER_INPUT_PER_TICK * blockEntity.maxProgress[i];

                if(ENERGY_USAGE_PER_INPUT_PER_TICK <= blockEntity.internalEnergyStorage.amount) {
                    if(!level.getBlockState(blockPos).contains(AdvancedPoweredFurnaceBlock.LIT) || !level.getBlockState(blockPos).get(AdvancedPoweredFurnaceBlock.LIT)) {
                        blockEntity.hasEnoughEnergy[i] = true;
                        level.setBlockState(blockPos, state.with(AdvancedPoweredFurnaceBlock.LIT, Boolean.TRUE), 3);
                    }

                    if(blockEntity.progress[i] < 0 || blockEntity.maxProgress[i] < 0 || blockEntity.energyConsumptionLeft[i] < 0) {
                        //Reset progress for invalid values

                        blockEntity.resetProgress(i, blockPos, state);
                        markDirty(level, blockPos, state);

                        continue;
                    }

                    try(Transaction transaction = Transaction.openOuter()) {
                        blockEntity.internalEnergyStorage.extract(ENERGY_USAGE_PER_INPUT_PER_TICK, transaction);
                        transaction.commit();
                    }
                    blockEntity.energyConsumptionLeft[i] -= ENERGY_USAGE_PER_INPUT_PER_TICK;

                    blockEntity.progress[i]++;
                    if(blockEntity.progress[i] >= blockEntity.maxProgress[i])
                        craftItem(i, blockPos, state, blockEntity);

                    markDirty(level, blockPos, state);
                }else {
                    blockEntity.hasEnoughEnergy[i] = false;
                    markDirty(level, blockPos, state);
                }
            }else {
                blockEntity.resetProgress(i, blockPos, state);
                markDirty(level, blockPos, state);
            }
        }
    }

    private void resetProgress(int index, BlockPos blockPos, BlockState state) {
        progress[index] = 0;
        maxProgress[index] = 0;
        energyConsumptionLeft[index] = -1;
        hasEnoughEnergy[index] = true;

        //Unlit if nothing is being smelted
        for(int i = 0;i < energyConsumptionLeft.length;i++)
            if(energyConsumptionLeft[0] > -1)
                return;

        world.setBlockState(blockPos, state.with(AdvancedPoweredFurnaceBlock.LIT, false), 3);
    }

    private static void craftItem(int index, BlockPos blockPos, BlockState state, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        World level = blockEntity.world;

        SimpleInventory inventory = new SimpleInventory(2);
        inventory.setStack(0, blockEntity.internalInventory.getStack(index));
        inventory.setStack(1, blockEntity.internalInventory.getStack(3 + index));

        Optional<SmeltingRecipe> recipe = level.getRecipeManager().getFirstMatch(RecipeType.SMELTING, inventory, level);

        if(!hasRecipe(index, blockEntity) || recipe.isEmpty())
            return;

        blockEntity.internalInventory.removeStack(index, 1);
        blockEntity.internalInventory.setStack(3 + index, new ItemStack(recipe.get().getOutput().getItem(),
                blockEntity.internalInventory.getStack(3 + index).getCount() + recipe.get().getOutput().getCount()));

        blockEntity.resetProgress(index, blockPos, state);
    }

    private static boolean hasRecipe(int index, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        World level = blockEntity.world;

        SimpleInventory inventory = new SimpleInventory(2);
        inventory.setStack(0, blockEntity.internalInventory.getStack(index));
        inventory.setStack(1, blockEntity.internalInventory.getStack(3 + index));

        Optional<SmeltingRecipe> recipe = level.getRecipeManager().getFirstMatch(RecipeType.SMELTING, inventory, level);

        return recipe.isPresent() && canInsertItemIntoOutputSlot(index, blockEntity.internalInventory, recipe.get().getOutput());
    }

    private static boolean canInsertItemIntoOutputSlot(int index, SimpleInventory inventory, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getStack(3 + index);

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