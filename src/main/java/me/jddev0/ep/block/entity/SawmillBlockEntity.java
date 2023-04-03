package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryBlockEntityWrapper;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.mixin.inventory.SimpleInventoryStacksGetterSetter;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.SawmillRecipe;
import me.jddev0.ep.screen.SawmillMenu;
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
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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

public class SawmillBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate, SidedInventoryBlockEntityWrapper {
    public static final long CAPACITY = 2048;
    public static final long MAX_RECEIVE = 128;
    private static final long ENERGY_USAGE_PER_TICK = 8;

    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    final LimitingEnergyStorage energyStorage;
    private final SimpleEnergyStorage internalEnergyStorage;

    protected  final PropertyDelegate data;
    private int progress;
    private int maxProgress = 100;
    private long energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    public SawmillBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.SAWMILL_ENTITY, blockPos, blockState);

        internalInventory = new SimpleInventory(3) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> world == null || RecipeUtils.isIngredientOfAny(world, SawmillRecipe.Type.INSTANCE, stack);
                    case 1, 2 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.areItemsEqual(stack, itemStack) ||
                            !ItemStack.areNbtEqual(stack, itemStack)))
                        resetProgress(pos, world.getBlockState(pos));
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                SawmillBlockEntity.this.markDirty();
            }
        };
        inventory = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
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
        }, (i, stack) -> i == 0, i -> i == 1 || i == 2);

        internalEnergyStorage = new SimpleEnergyStorage(CAPACITY, CAPACITY, CAPACITY) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeLong(amount);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.broadcastServerPacket(world.getServer(), ModMessages.ENERGY_SYNC_ID, buffer);
                }
            }
        };
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0);

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> SawmillBlockEntity.this.progress;
                    case 1 -> SawmillBlockEntity.this.maxProgress;
                    case 2, 3, 4, 5 -> ByteUtils.get2Bytes(SawmillBlockEntity.this.internalEnergyStorage.amount, index - 2);
                    case 6, 7, 8, 9 -> ByteUtils.get2Bytes(SawmillBlockEntity.this.internalEnergyStorage.capacity, index - 6);
                    case 10, 11, 12, 13 -> ByteUtils.get2Bytes(SawmillBlockEntity.this.energyConsumptionLeft, index - 10);
                    case 14 -> hasEnoughEnergy?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> SawmillBlockEntity.this.progress = value;
                    case 1 -> SawmillBlockEntity.this.maxProgress = value;
                    case 2, 3, 4, 5 -> SawmillBlockEntity.this.internalEnergyStorage.amount = ByteUtils.with2Bytes(
                            SawmillBlockEntity.this.internalEnergyStorage.amount, (short)value, index - 2);
                    case 6, 7, 8, 9, 10, 11, 12, 13, 14 -> {}
                }
            }

            @Override
            public int size() {
                return 15;
            }
        };
    }

    @Override
    public SidedInventory getHandler() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("container.energizedpower.sawmill");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new SawmillMenu(id, this, inventory, internalInventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), ((SimpleInventoryStacksGetterSetter)internalInventory).getStacks()));
        nbt.putLong("energy", internalEnergyStorage.amount);

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), ((SimpleInventoryStacksGetterSetter)internalInventory).getStacks());
        internalEnergyStorage.amount = nbt.getLong("energy");

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getLong("recipe.energy_consumption_left");
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, ((SimpleInventoryStacksGetterSetter)internalInventory).getStacks());
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, SawmillBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(hasRecipe(blockEntity)) {
            Optional<SawmillRecipe> recipe = level.getRecipeManager().getFirstMatch(SawmillRecipe.Type.INSTANCE, blockEntity.internalInventory, level);
            if(recipe.isEmpty())
                return;

            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = ENERGY_USAGE_PER_TICK * blockEntity.maxProgress;

            if(ENERGY_USAGE_PER_TICK <= blockEntity.internalEnergyStorage.amount) {
                blockEntity.hasEnoughEnergy = true;

                try(Transaction transaction = Transaction.openOuter()) {
                    blockEntity.internalEnergyStorage.extract(ENERGY_USAGE_PER_TICK, transaction);
                    transaction.commit();
                }
                blockEntity.energyConsumptionLeft -= ENERGY_USAGE_PER_TICK;

                blockEntity.progress++;
                markDirty(level, blockPos, state);

                if(blockEntity.progress >= blockEntity.maxProgress) {
                    craftItem(blockPos, state, blockEntity);
                }
            }else {
                blockEntity.hasEnoughEnergy = false;
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

    private static void craftItem(BlockPos blockPos, BlockState state, SawmillBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<SawmillRecipe> recipe = level.getRecipeManager().getFirstMatch(SawmillRecipe.Type.INSTANCE, blockEntity.internalInventory, level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        blockEntity.internalInventory.removeStack(0, 1);
        blockEntity.internalInventory.setStack(1, new ItemStack(recipe.get().getOutput().getItem(),
                blockEntity.internalInventory.getStack(1).getCount() + recipe.get().getOutput().getCount()));
        if(!recipe.get().getSecondaryOutput().isEmpty())
            blockEntity.internalInventory.setStack(2, new ItemStack(recipe.get().getSecondaryOutput().getItem(),
                    blockEntity.internalInventory.getStack(2).getCount() + recipe.get().getSecondaryOutput().getCount()));

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(SawmillBlockEntity blockEntity) {
        World level = blockEntity.world;

        Optional<SawmillRecipe> recipe = level.getRecipeManager().getFirstMatch(SawmillRecipe.Type.INSTANCE, blockEntity.internalInventory, level);

        return recipe.isPresent() && canInsertAmountIntoOutputSlot(blockEntity.internalInventory, recipe.get().getOutput().getCount()) &&
                canInsertItemIntoOutputSlot(blockEntity.internalInventory, recipe.get().getOutput()) &&
                (recipe.get().getSecondaryOutput().isEmpty() || (canInsertAmountIntoSecondaryOutputSlot(blockEntity.internalInventory, recipe.get().getSecondaryOutput().getCount()) &&
                        canInsertItemIntoSecondaryOutputSlot(blockEntity.internalInventory, recipe.get().getSecondaryOutput())));
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, ItemStack itemStack) {
        return inventory.getStack(1).isEmpty() || inventory.getStack(1).getItem() == itemStack.getItem();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleInventory inventory, int count) {
        return inventory.getStack(1).getMaxCount() >= inventory.getStack(1).getCount() + count;
    }

    private static boolean canInsertItemIntoSecondaryOutputSlot(SimpleInventory inventory, ItemStack itemStack) {
        return inventory.getStack(2).isEmpty() || inventory.getStack(2).getItem() == itemStack.getItem();
    }

    private static boolean canInsertAmountIntoSecondaryOutputSlot(SimpleInventory inventory, int count) {
        return inventory.getStack(2).getMaxCount() >= inventory.getStack(2).getCount() + count;
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.amount = energy;
    }

    @Override
    public void setCapacity(long capacity) {
        //Does nothing (capacity is final)
    }

    public long getCapacity() {
        return internalEnergyStorage.capacity;
    }
}