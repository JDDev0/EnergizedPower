package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.CoalEngineBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ExtractOnlyEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.screen.CoalEngineMenu;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoalEngineBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate {
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch(slot) {
                case 0 -> true;
                default -> super.isItemValid(slot, stack);
            };
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, i -> true, i -> {
                if(i != 0)
                    return false;

                //Do not allow extraction of fuel items, allow for non fuel items (Bucket of Lava -> Empty Bucket)
                ItemStack item = itemHandler.getStackInSlot(i);
                return ForgeHooks.getBurnTime(item, null) <= 0;
            }));

    private final ExtractOnlyEnergyStorage energyStorage;
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    protected  final ContainerData data;
    private int progress;
    private int maxProgress;
    private int energyProductionLeft = -1;
    private boolean hasEnoughCapacityForProduction;

    public CoalEngineBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.COAL_ENGINE_ENTITY.get(), blockPos, blockState);

        energyStorage = new ExtractOnlyEnergyStorage(0, 2048, 256) {
            @Override
            protected void onChange() {
                setChanged();

                ModMessages.sendToAllPlayers(new EnergySyncS2CPacket(energy, capacity, getBlockPos()));
            }
        };
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> CoalEngineBlockEntity.this.progress;
                    case 1 -> CoalEngineBlockEntity.this.maxProgress;
                    case 2 -> CoalEngineBlockEntity.this.energyStorage.getEnergy();
                    case 3 -> CoalEngineBlockEntity.this.energyStorage.getCapacity();
                    case 4 -> CoalEngineBlockEntity.this.energyProductionLeft;
                    case 5 -> hasEnoughCapacityForProduction?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> CoalEngineBlockEntity.this.progress = value;
                    case 1 -> CoalEngineBlockEntity.this.maxProgress = value;
                    case 2 -> CoalEngineBlockEntity.this.energyStorage.setEnergyWithoutUpdate(value);
                    case 3 -> CoalEngineBlockEntity.this.energyStorage.setCapacityWithoutUpdate(value);
                    case 4, 5 -> {}
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
        return Component.translatable("container.energizedpower.coal_engine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new CoalEngineMenu(id, inventory, this, this.data);
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
        nbt.put("recipe.max_progress", IntTag.valueOf(maxProgress));
        nbt.put("recipe.energy_production_left", IntTag.valueOf(energyProductionLeft));

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyStorage.loadNBT(nbt.get("energy"));

        progress = nbt.getInt("recipe.progress");
        maxProgress = nbt.getInt("recipe.max_progress");
        energyProductionLeft = nbt.getInt("recipe.energy_production_left");
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, CoalEngineBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        //Fix for players on server
        blockEntity.energyStorage.setCapacity(blockEntity.energyStorage.getCapacity());

        if(blockEntity.maxProgress > 0 || hasRecipe(blockEntity)) {
            SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
            for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
                inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

            ItemStack item = inventory.getItem(0);

            int energyProduction = ForgeHooks.getBurnTime(item, null);
            if(blockEntity.progress == 0)
                blockEntity.energyProductionLeft = energyProduction;

            //Change max progress if item would output more than max extract
            if(blockEntity.maxProgress == 0) {
                if(energyProduction / 100 <= blockEntity.energyStorage.getMaxExtract())
                    blockEntity.maxProgress = 100;
                else
                    blockEntity.maxProgress = (int)Math.ceil((float)energyProduction / blockEntity.energyStorage.getMaxExtract());
            }

            //TODO improve (alternate values +/- 1 per x recipes instead of changing last energy production tick)
            int energyProductionPerTick = (int)Math.ceil((float)blockEntity.energyProductionLeft / (blockEntity.maxProgress - blockEntity.progress));
            if(blockEntity.progress == blockEntity.maxProgress - 1)
                energyProductionPerTick = blockEntity.energyProductionLeft;

            if(energyProductionPerTick <= blockEntity.energyStorage.getCapacity() - blockEntity.energyStorage.getEnergy()) {
                if(blockEntity.progress == 0) {
                    //Remove item instantly else the item could be removed before finished and energy was cheated

                    if(item.hasCraftingRemainingItem())
                        blockEntity.itemHandler.setStackInSlot(0, item.getCraftingRemainingItem());
                    else
                        blockEntity.itemHandler.extractItem(0, 1, false);
                }

                if(!level.getBlockState(blockPos).hasProperty(CoalEngineBlock.LIT) || !level.getBlockState(blockPos).getValue(CoalEngineBlock.LIT)) {
                    blockEntity.hasEnoughCapacityForProduction = true;
                    level.setBlock(blockPos, state.setValue(CoalEngineBlock.LIT, Boolean.TRUE), 3);
                }

                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() + energyProductionPerTick);
                blockEntity.energyProductionLeft -= energyProductionPerTick;

                blockEntity.progress++;
                setChanged(level, blockPos, state);

                if(blockEntity.progress >= blockEntity.maxProgress) {
                    blockEntity.resetProgress(blockPos, state);
                }
            }else {
                blockEntity.hasEnoughCapacityForProduction = false;
                //Do not unlit block (Would flicker if energy is not extracted at the production rate or greater)
            }
        }else {
            blockEntity.resetProgress(blockPos, state);
            setChanged(level, blockPos, state);
        }
    }

    private void resetProgress(BlockPos blockPos, BlockState state) {
        progress = 0;
        maxProgress = 0;
        energyProductionLeft = -1;
        hasEnoughCapacityForProduction = true;

        level.setBlock(blockPos, state.setValue(CoalEngineBlock.LIT, Boolean.FALSE), 3);
    }

    private static boolean hasRecipe(CoalEngineBlockEntity blockEntity) {
        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        ItemStack item = inventory.getItem(0);

        if(ForgeHooks.getBurnTime(item, null) <= 0)
            return false;

        return !item.hasCraftingRemainingItem() || item.getCount() == 1;
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