package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.screen.StoneSolidifierMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.LimitingEnergyStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StoneSolidifierBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, EnergyStoragePacketUpdate,
        FluidStoragePacketUpdate {
    public static final long CAPACITY = ModConfigs.COMMON_FLUID_DRAINER_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_FLUID_DRAINER_TRANSFER_RATE.getValue();

    public static final long ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_STONE_SOLIDIFIER_CONSUMPTION_PER_TICK.getValue();
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(1000 *
            ModConfigs.COMMON_STONE_SOLIDIFIER_TANK_CAPACITY.getValue());

    final CachedSidedInventoryStorage<UnchargerBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    final LimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;

    final CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage;

    protected final PropertyDelegate data;

    private Identifier currentRecipeIdForLoad = null;
    private StoneSolidifierRecipe currentRecipe = null;
    private int progress;
    private int maxProgress = ModConfigs.COMMON_CSTONE_SOLIDIFIER_RECIPE_DURATION.getValue();
    private long energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    public StoneSolidifierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.STONE_SOLIDIFIER_ENTITY, blockPos, blockState);


        internalInventory = new SimpleInventory(1) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(slot == 0)
                    return false;

                return super.isValid(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                StoneSolidifierBlockEntity.this.markDirty();
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
        }, (i, stack) -> false, i -> i == 0);
        cachedSidedInventoryStorage = new CachedSidedInventoryStorage<>(inventory);

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
        energyStorage = new LimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0);

        fluidStorage = new CombinedStorage<>(List.of(
                new SimpleFluidStorage(TANK_CAPACITY) {
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
                        return variant.isOf(Fluids.WATER);
                    }

                    @Override
                    protected boolean canInsert(FluidVariant variant) {
                        return isFluidValid(variant);
                    }

                    @Override
                    protected boolean canExtract(FluidVariant variant) {
                        return isFluidValid(variant);
                    }
                },
                new SimpleFluidStorage(TANK_CAPACITY) {
                    @Override
                    protected void onFinalCommit() {
                        markDirty();

                        if(world != null && !world.isClient()) {
                            PacketByteBuf buffer = PacketByteBufs.create();
                            buffer.writeInt(1);
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
                        return variant.isOf(Fluids.LAVA);
                    }

                    @Override
                    protected boolean canInsert(FluidVariant variant) {
                        return isFluidValid(variant);
                    }

                    @Override
                    protected boolean canExtract(FluidVariant variant) {
                        return isFluidValid(variant);
                    }
                }
        ));

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(StoneSolidifierBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(StoneSolidifierBlockEntity.this.maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(StoneSolidifierBlockEntity.this.energyConsumptionLeft, index - 4);
                    case 8 -> hasEnoughEnergy?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> StoneSolidifierBlockEntity.this.progress = ByteUtils.with2Bytes(
                            StoneSolidifierBlockEntity.this.progress, (short)value, index
                    );
                    case 2, 3 -> StoneSolidifierBlockEntity.this.maxProgress = ByteUtils.with2Bytes(
                            StoneSolidifierBlockEntity.this.maxProgress, (short)value, index - 2
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
        return Text.translatable("container.energizedpower.stone_solidifier");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeLong(internalEnergyStorage.amount);
        buffer.writeLong(internalEnergyStorage.capacity);
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.ENERGY_SYNC_ID, buffer);
        for(int i = 0;i < 2;i++) {
            buffer = PacketByteBufs.create();
            buffer.writeInt(i);
            fluidStorage.parts.get(i).getFluid().toPacket(buffer);
            buffer.writeLong(fluidStorage.parts.get(i).getCapacity());
            buffer.writeBlockPos(getPos());

            ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.FLUID_SYNC_ID, buffer);
        }

        buffer = PacketByteBufs.create();
        buffer.writeBlockPos(getPos());
        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);
            buffer.writeIdentifier(currentRecipe.getId());
            StoneSolidifierRecipe.Serializer.INSTANCE.write(buffer, currentRecipe);
        }
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.SYNC_STONE_SOLIDIFIER_CURRENT_RECIPE_ID, buffer);

        return new StoneSolidifierMenu(id, this, inventory, internalInventory, this.data);
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
        for(int i = 0;i < fluidStorage.parts.size();i++)
            nbt.put("fluid." + i, fluidStorage.parts.get(i).toNBT(new NbtCompound()));

        if(currentRecipe != null)
            nbt.put("recipe.id", NbtString.of(currentRecipe.getId().toString()));

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.stacks);
        internalEnergyStorage.amount = nbt.getLong("energy");
        for(int i = 0;i < fluidStorage.parts.size();i++)
            fluidStorage.parts.get(i).fromNBT(nbt.getCompound("fluid." + i));

        if(nbt.contains("recipe.id")) {
            NbtElement tag = nbt.get("recipe.id");

            if(!(tag instanceof NbtString stringTag))
                throw new IllegalArgumentException("Tag must be of type StringTag!");

            currentRecipeIdForLoad = Identifier.tryParse(stringTag.asString());
        }

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getInt("recipe.energy_consumption_left");
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.stacks);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, StoneSolidifierBlockEntity blockEntity) {
        if(level.isClient())
            return;

        //Load recipe
        if(blockEntity.currentRecipeIdForLoad != null) {
            List<StoneSolidifierRecipe> recipes = level.getRecipeManager().listAllOfType(StoneSolidifierRecipe.Type.INSTANCE);
            blockEntity.currentRecipe = recipes.stream().
                    filter(recipe -> recipe.getId().equals(blockEntity.currentRecipeIdForLoad)).
                    findFirst().orElse(null);

            blockEntity.currentRecipeIdForLoad = null;
        }

        if(hasRecipe(blockEntity)) {
            if(blockEntity.currentRecipe == null)
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
                    blockEntity.craftItem();

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

    public void craftItem() {
        if(currentRecipe == null || !hasRecipe(this))
            return;

        StoneSolidifierRecipe recipe = currentRecipe;

        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.extract(FluidVariant.of(Fluids.WATER), FluidUtils.convertMilliBucketsToDroplets(
                    recipe.getWaterAmount()), transaction);
            fluidStorage.extract(FluidVariant.of(Fluids.LAVA), FluidUtils.convertMilliBucketsToDroplets(
                    recipe.getLavaAmount()), transaction);

            transaction.commit();
        }

        internalInventory.setStack(0, recipe.getOutput(world.getRegistryManager()).copyWithCount(
                internalInventory.getStack(0).getCount() + recipe.getOutput(world.getRegistryManager()).getCount()));

        resetProgress(getPos(), getCachedState());
    }

    private static boolean hasRecipe(StoneSolidifierBlockEntity blockEntity) {
        if(blockEntity.currentRecipe == null)
            return false;

        World level = blockEntity.world;

        StoneSolidifierRecipe recipe = blockEntity.currentRecipe;

        return blockEntity.fluidStorage.parts.get(0).getAmount() >= FluidUtils.convertMilliBucketsToDroplets(recipe.getWaterAmount()) &&
                blockEntity.fluidStorage.parts.get(1).getAmount() >= FluidUtils.convertMilliBucketsToDroplets(recipe.getLavaAmount()) &&
                canInsertItemIntoOutputSlot(blockEntity.internalInventory, recipe.getOutput(level.getRegistryManager()));
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getStack(0);

        return (inventoryItemStack.isEmpty() || ItemStack.canCombine(inventoryItemStack, itemStack)) &&
                inventoryItemStack.getMaxCount() >= inventoryItemStack.getCount() + itemStack.getCount();
    }

    public void changeRecipeIndex(boolean downUp) {
        if(world.isClient())
            return;

        List<StoneSolidifierRecipe> recipes = world.getRecipeManager().listAllOfType(StoneSolidifierRecipe.Type.INSTANCE);
        recipes = recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.getOutput(world.getRegistryManager()).getTranslationKey())).
                collect(Collectors.toList());

        int currentIndex = -1;
        if(currentRecipe != null) {
            for(int i = 0;i < recipes.size();i++) {
                if(currentRecipe.getId().equals(recipes.get(i).getId())) {
                    currentIndex = i;
                    break;
                }
            }
        }

        currentIndex += downUp?1:-1;
        if(currentIndex < -1)
            currentIndex = recipes.size() - 1;
        else if(currentIndex >= recipes.size())
            currentIndex = -1;

        currentRecipe = currentIndex == -1?null:recipes.get(currentIndex);

        resetProgress(getPos(), getCachedState());

        markDirty(world, getPos(), getCachedState());

        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBlockPos(getPos());
        if(currentRecipe == null) {
            buffer.writeBoolean(false);
        }else {
            buffer.writeBoolean(true);
            buffer.writeIdentifier(currentRecipe.getId());
            StoneSolidifierRecipe.Serializer.INSTANCE.write(buffer, currentRecipe);
        }
        ModMessages.sendServerPacketToPlayersWithinXBlocks(
                getPos(), (ServerWorld)world, 32,
                ModMessages.SYNC_STONE_SOLIDIFIER_CURRENT_RECIPE_ID, buffer
        );
    }

    public void setCurrentRecipe(@Nullable StoneSolidifierRecipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    public @Nullable StoneSolidifierRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public FluidStack getFluid(int tank) {
        return fluidStorage.parts.get(tank).getFluid();
    }

    public long getTankCapacity(int tank) {
        return fluidStorage.parts.get(tank).getCapacity();
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.amount = energy;
    }

    @Override
    public void setCapacity(long capacity) {
        internalEnergyStorage.capacity = capacity;
    }

    public long getEnergy() {
        return internalEnergyStorage.amount;
    }

    public long getCapacity() {
        return internalEnergyStorage.capacity;
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorage.parts.get(tank).setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, long capacity) {
        //Does nothing (capacity is final)
    }
}