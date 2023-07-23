package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.EnergizerBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.recipe.EnergizerRecipe;
import me.jddev0.ep.screen.EnergizerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnergizerBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> level == null || RecipeUtils.isIngredientOfAny(level, EnergizerRecipe.Type.INSTANCE, stack);
                case 1 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            if(slot == 0) {
                ItemStack itemStack = getStackInSlot(slot);
                if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameTags(stack, itemStack))
                    resetProgress(worldPosition, level.getBlockState(worldPosition));
            }

            super.setStackInSlot(slot, stack);
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1));

    private final ReceiveOnlyEnergyStorage energyStorage;

    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    protected final ContainerData data;
    private int progress;
    private int maxProgress = 100;
    private int energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    public EnergizerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ENERGIZER_ENTITY.get(), blockPos, blockState);

        energyStorage = new ReceiveOnlyEnergyStorage(0, 65536, 4096) {
            @Override
            protected void onChange() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToAllPlayers(new EnergySyncS2CPacket(energy, capacity, getBlockPos()));
            }
        };
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> EnergizerBlockEntity.this.progress;
                    case 1 -> EnergizerBlockEntity.this.maxProgress;
                    case 2, 3 -> ByteUtils.get2Bytes(EnergizerBlockEntity.this.energyConsumptionLeft, index - 2);
                    case 4 -> hasEnoughEnergy?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> EnergizerBlockEntity.this.progress = value;
                    case 1 -> EnergizerBlockEntity.this.maxProgress = value;
                    case 2, 3, 4 -> {}
                }
            }

            @Override
            public int getCount() {
                return 5;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.energizer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(),
                getBlockPos()), (ServerPlayer)player);

        return new EnergizerMenu(id, inventory, this, this.data);
    }

    public int getRedstoneOutput() {
        return InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyItemHandler.invalidate();
        lazyEnergyStorage.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.put("energy", energyStorage.saveNBT());

        nbt.put("recipe.progress", IntTag.valueOf(progress));
        nbt.put("recipe.energy_consumption_left", IntTag.valueOf(energyConsumptionLeft));

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyStorage.loadNBT(nbt.get("energy"));

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getInt("recipe.energy_consumption_left");
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, EnergizerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(hasRecipe(blockEntity)) {
            SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
            for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
                inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

            Optional<EnergizerRecipe> recipe = level.getRecipeManager().getRecipeFor(EnergizerRecipe.Type.INSTANCE, inventory, level);
            if(recipe.isEmpty())
                return;

            int energyConsumption = recipe.get().getEnergyConsumption();
            if(blockEntity.progress == 0)
                blockEntity.energyConsumptionLeft = energyConsumption;

            //TODO improve (alternate values +/- 1 per x recipes instead of changing last energy consumption tick)
            int energyConsumptionPerTick = (int)Math.ceil((float)energyConsumption / blockEntity.maxProgress);
            if(blockEntity.progress == blockEntity.maxProgress - 1)
                energyConsumptionPerTick = blockEntity.energyConsumptionLeft;

            if(energyConsumptionPerTick <= blockEntity.energyStorage.getEnergy()) {

                if(!level.getBlockState(blockPos).hasProperty(EnergizerBlock.LIT) || !level.getBlockState(blockPos).getValue(EnergizerBlock.LIT)) {
                    blockEntity.hasEnoughEnergy = true;
                    level.setBlock(blockPos, state.setValue(EnergizerBlock.LIT, Boolean.TRUE), 3);
                }

                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick);
                blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

                blockEntity.progress++;
                setChanged(level, blockPos, state);

                if(blockEntity.progress >= blockEntity.maxProgress) {
                    craftItem(blockPos, state, blockEntity);
                }
            }else {
                blockEntity.hasEnoughEnergy = false;
                level.setBlock(blockPos, state.setValue(EnergizerBlock.LIT, false), 3);
            }
        }else {
            blockEntity.resetProgress(blockPos, state);
            setChanged(level, blockPos, state);
        }
    }

    private void resetProgress(BlockPos blockPos, BlockState state) {
        progress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = true;

        level.setBlock(blockPos, state.setValue(EnergizerBlock.LIT, false), 3);
    }

    private static void craftItem(BlockPos blockPos, BlockState state, EnergizerBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        Optional<EnergizerRecipe> recipe = level.getRecipeManager().getRecipeFor(EnergizerRecipe.Type.INSTANCE, inventory, level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.extractItem(0, 1, false);
        blockEntity.itemHandler.setStackInSlot(1, new ItemStack(recipe.get().getResultItem().getItem(),
                blockEntity.itemHandler.getStackInSlot(1).getCount() + recipe.get().getResultItem().getCount()));

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(EnergizerBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        Optional<EnergizerRecipe> recipe = level.getRecipeManager().getRecipeFor(EnergizerRecipe.Type.INSTANCE, inventory, level);

        return recipe.isPresent() && canInsertAmountIntoOutputSlot(inventory, recipe.get().getResultItem().getCount()) &&
                canInsertItemIntoOutputSlot(inventory, recipe.get().getResultItem());
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack itemStack) {
        return inventory.getItem(1).isEmpty() || inventory.getItem(1).getItem() == itemStack.getItem();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory, int count) {
        return inventory.getItem(1).getMaxStackSize() >= inventory.getItem(1).getCount() + count;
    }

    public int getEnergy() {
        return energyStorage.getEnergy();
    }

    public int getCapacity() {
        return energyStorage.getCapacity();
    }

    @Override
    public void setEnergy(int energy) {
        energyStorage.setEnergyWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }
}