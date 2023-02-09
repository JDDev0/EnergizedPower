package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.screen.ChargerMenu;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
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

public class ChargerBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate {
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if(slot == 0) {
                if(level == null || RecipeUtils.isIngredientOfAny(level, ChargerRecipe.Type.INSTANCE, stack))
                    return true;

                LazyOptional<IEnergyStorage> energyStorageLazyOptional = stack.getCapability(ForgeCapabilities.ENERGY);
                if(!energyStorageLazyOptional.isPresent())
                    return false;

                IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
                return energyStorage.canReceive();
            }

            return super.isItemValid(slot, stack);
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            if(slot == 0) {
                ItemStack itemStack = getStackInSlot(slot);
                if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.isSame(stack, itemStack) ||
                        (!ItemStack.tagMatches(stack, itemStack) &&
                                //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                !(stack.getCapability(ForgeCapabilities.ENERGY).isPresent() && itemStack.getCapability(ForgeCapabilities.ENERGY).isPresent()))))
                    resetProgress();
            }

            super.setStackInSlot(slot, stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
                if(i != 0)
                    return false;

                ItemStack stack = itemHandler.getStackInSlot(i);
                if(level != null && RecipeUtils.isResultOfAny(level, ChargerRecipe.Type.INSTANCE, stack))
                    return true;

                if(level == null || RecipeUtils.isIngredientOfAny(level, ChargerRecipe.Type.INSTANCE, stack))
                    return false;

                LazyOptional<IEnergyStorage> energyStorageLazyOptional = stack.getCapability(ForgeCapabilities.ENERGY);
                if(!energyStorageLazyOptional.isPresent())
                    return true;

                IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
                if(!energyStorage.canReceive())
                    return true;

                return energyStorage.receiveEnergy(ChargerBlockEntity.this.energyStorage.getMaxReceive(), true) == 0;
            }));

    private final ReceiveOnlyEnergyStorage energyStorage;

    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    protected  final ContainerData data;
    private int energyConsumptionLeft = -1;

    public ChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.CHARGER_ENTITY.get(), blockPos, blockState);

        energyStorage = new ReceiveOnlyEnergyStorage(0, 8192, 512) {
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
                    case 0, 1 -> -1;
                    case 2 -> ChargerBlockEntity.this.energyStorage.getEnergy();
                    case 3 -> ChargerBlockEntity.this.energyStorage.getCapacity();
                    case 4 -> ChargerBlockEntity.this.energyConsumptionLeft;
                    case 5 -> 1;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 2 -> ChargerBlockEntity.this.energyStorage.setEnergyWithoutUpdate(value);
                    case 3 -> ChargerBlockEntity.this.energyStorage.setCapacityWithoutUpdate(value);
                    case 0, 1, 4, 5 -> {}
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.charger");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ChargerMenu(id, inventory, this, this.data);
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

        nbt.put("recipe.energy_consumption_left", IntTag.valueOf(energyConsumptionLeft));

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyStorage.loadNBT(nbt.get("energy"));

        energyConsumptionLeft = nbt.getInt("recipe.energy_consumption_left");
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ChargerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        //Fix for players on server
        blockEntity.energyStorage.setCapacity(blockEntity.energyStorage.getCapacity());

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.itemHandler.getStackInSlot(0);
            int energyConsumptionPerTick = 0;

            SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
            for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
                inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

            Optional<ChargerRecipe> recipe = level.getRecipeManager().getRecipeFor(ChargerRecipe.Type.INSTANCE, inventory, level);
            if(recipe.isPresent()) {
                if(blockEntity.energyConsumptionLeft == -1)
                    blockEntity.energyConsumptionLeft = recipe.get().getEnergyConsumption();

                if(blockEntity.energyStorage.getEnergy() == 0)
                    return;

                energyConsumptionPerTick = Math.min(blockEntity.energyConsumptionLeft, Math.min(blockEntity.energyStorage.getMaxReceive(),
                        blockEntity.energyStorage.getEnergy()));
            }else {
                LazyOptional<IEnergyStorage> energyStorageLazyOptional = stack.getCapability(ForgeCapabilities.ENERGY);
                if(!energyStorageLazyOptional.isPresent())
                    return;

                IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
                if(!energyStorage.canReceive())
                    return;

                blockEntity.energyConsumptionLeft = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();

                if(blockEntity.energyStorage.getEnergy() == 0)
                    return;

                energyConsumptionPerTick = energyStorage.receiveEnergy(Math.min(blockEntity.energyStorage.getMaxReceive(),
                        blockEntity.energyStorage.getEnergy()), false);
            }

            blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick);
            blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

            setChanged(level, blockPos, state);

            if(blockEntity.energyConsumptionLeft <= 0) {
                recipe.ifPresent(chargerRecipe ->
                        blockEntity.itemHandler.setStackInSlot(0, new ItemStack(chargerRecipe.getResultItem().getItem())));

                blockEntity.resetProgress();
            }
        }else {
            blockEntity.resetProgress();
            setChanged(level, blockPos, state);
        }
    }

    private void resetProgress() {
        energyConsumptionLeft = -1;
    }

    private boolean hasRecipe() {
        ItemStack stack = itemHandler.getStackInSlot(0);
        if(stack.getCapability(ForgeCapabilities.ENERGY).isPresent())
            return true;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<ChargerRecipe> recipe = level.getRecipeManager().getRecipeFor(ChargerRecipe.Type.INSTANCE, inventory, level);

        return recipe.isPresent();
    }

    @Override
    public void setEnergy(int energy) {
        energyStorage.setEnergyWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }

    public int getCapacity() {
        return energyStorage.getCapacity();
    }
}