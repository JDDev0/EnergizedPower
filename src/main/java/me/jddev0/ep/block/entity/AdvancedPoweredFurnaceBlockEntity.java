package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AdvancedPoweredFurnaceBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.screen.AdvancedPoweredFurnaceMenu;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
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

public class AdvancedPoweredFurnaceBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate {
    private static final int ENERGY_USAGE_PER_INPUT_PER_TICK = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_INPUT_PER_TICK.getValue();

    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    private final ItemStackHandler itemHandler = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch(slot) {
                case 0, 1, 2 -> level == null || RecipeUtils.isIngredientOfAny(level, RecipeType.SMELTING, stack);
                case 3, 4, 5 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            if(slot >= 0 && slot < 3) {
                ItemStack itemStack = getStackInSlot(slot);
                if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameTags(stack, itemStack))
                    resetProgress(slot, worldPosition, level.getBlockState(worldPosition));
            }

            super.setStackInSlot(slot, stack);
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 3, i -> i >= 3 && i < 6));

    private final ReceiveOnlyEnergyStorage energyStorage;

    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    protected final ContainerData data;
    private int[] progress = new int[] {
            0, 0, 0
    };
    private int[] maxProgress = new int[] {
            0, 0, 0
    };
    private int[] energyConsumptionLeft = new int[] {
            -1, -1, -1
    };
    private boolean[] hasEnoughEnergy = new boolean[] {
            false, false, false
    };

    public AdvancedPoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ADVANCED_POWERED_FURNACE_ENTITY.get(), blockPos, blockState);

        energyStorage = new ReceiveOnlyEnergyStorage(0, ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_TRANSFER_RATE.getValue()) {
            @Override
            protected void onChange() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToPlayersWithinXBlocks(
                            new EnergySyncS2CPacket(energy, capacity, getBlockPos()),
                            getBlockPos(), level.dimension(), 32
                    );
            }
        };
        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.energyConsumptionLeft[0], index);
                    case 2, 3 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.energyConsumptionLeft[1], index - 2);
                    case 4, 5 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.energyConsumptionLeft[2], index - 4);
                    case 6, 7 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.progress[0], index - 6);
                    case 8, 9 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.maxProgress[0], index - 8);
                    case 10, 11 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.progress[1], index - 10);
                    case 12, 13 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.maxProgress[1], index - 12);
                    case 14, 15 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.progress[2], index - 14);
                    case 16, 17 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.maxProgress[2], index - 16);
                    case 18 -> AdvancedPoweredFurnaceBlockEntity.this.hasEnoughEnergy[0]?0:1;
                    case 19 -> AdvancedPoweredFurnaceBlockEntity.this.hasEnoughEnergy[1]?0:1;
                    case 20 -> AdvancedPoweredFurnaceBlockEntity.this.hasEnoughEnergy[2]?0:1;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 6, 7 -> AdvancedPoweredFurnaceBlockEntity.this.progress[0] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.progress[0], (short)value, index - 6
                    );
                    case 8, 9 -> AdvancedPoweredFurnaceBlockEntity.this.maxProgress[0] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.maxProgress[0], (short)value, index - 8
                    );
                    case 10, 11 -> AdvancedPoweredFurnaceBlockEntity.this.progress[1] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.progress[1], (short)value, index - 10
                    );
                    case 12, 13 -> AdvancedPoweredFurnaceBlockEntity.this.maxProgress[1] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.maxProgress[1], (short)value, index - 12
                    );
                    case 14, 15 -> AdvancedPoweredFurnaceBlockEntity.this.progress[2] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.progress[2], (short)value, index - 14
                    );
                    case 16, 17 -> AdvancedPoweredFurnaceBlockEntity.this.maxProgress[2] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.maxProgress[2], (short)value, index - 16
                    );
                    case 0, 1, 2, 3, 4, 5, 18, 19, 20 -> {}
                }
            }

            @Override
            public int getCount() {
                return 21;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.advanced_powered_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(),
                getBlockPos()), (ServerPlayer)player);

        return new AdvancedPoweredFurnaceMenu(id, inventory, this, this.data);
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

        for(int i = 0;i < 3;i++)
            nbt.put("recipe.progress." + i, IntTag.valueOf(progress[i]));
        for(int i = 0;i < 3;i++)
            nbt.put("recipe.max_progress." + i, IntTag.valueOf(maxProgress[i]));
        for(int i = 0;i < 3;i++)
            nbt.put("recipe.energy_consumption_left." + i, IntTag.valueOf(energyConsumptionLeft[i]));

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyStorage.loadNBT(nbt.get("energy"));

        for(int i = 0;i < 3;i++)
            progress[i] = nbt.getInt("recipe.progress." + i);
        for(int i = 0;i < 3;i++)
            maxProgress[i] = nbt.getInt("recipe.max_progress." + i);
        for(int i = 0;i < 3;i++)
            energyConsumptionLeft[i] = nbt.getInt("recipe.energy_consumption_left." + i);
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        for(int i = 0;i < 3;i++) {
            if(hasRecipe(i, blockEntity)) {
                SimpleContainer inventory = new SimpleContainer(2);
                inventory.setItem(0, blockEntity.itemHandler.getStackInSlot(i));
                inventory.setItem(1, blockEntity.itemHandler.getStackInSlot(3 + i));

                Optional<SmeltingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, inventory, level);
                if(recipe.isEmpty())
                    continue;

                int cookingTime = recipe.get().getCookingTime();
                if(blockEntity.maxProgress[i] == 0)
                    blockEntity.maxProgress[i] = (int)Math.ceil(cookingTime * RECIPE_DURATION_MULTIPLIER / 12.f); //Default Cooking Time = 200 -> maxProgress = 16 (= 200 / 12)

                if(blockEntity.energyConsumptionLeft[i] < 0)
                    blockEntity.energyConsumptionLeft[i] = ENERGY_USAGE_PER_INPUT_PER_TICK * blockEntity.maxProgress[i];

                if(ENERGY_USAGE_PER_INPUT_PER_TICK <= blockEntity.energyStorage.getEnergy()) {
                    if(!level.getBlockState(blockPos).hasProperty(AdvancedPoweredFurnaceBlock.LIT) || !level.getBlockState(blockPos).getValue(AdvancedPoweredFurnaceBlock.LIT)) {
                        blockEntity.hasEnoughEnergy[i] = true;
                        level.setBlock(blockPos, state.setValue(AdvancedPoweredFurnaceBlock.LIT, Boolean.TRUE), 3);
                    }

                    if(blockEntity.progress[i] < 0 || blockEntity.maxProgress[i] < 0 || blockEntity.energyConsumptionLeft[i] < 0) {
                        //Reset progress for invalid values

                        blockEntity.resetProgress(i, blockPos, state);
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - ENERGY_USAGE_PER_INPUT_PER_TICK);
                    blockEntity.energyConsumptionLeft[i] -= ENERGY_USAGE_PER_INPUT_PER_TICK;

                    blockEntity.progress[i]++;
                    setChanged(level, blockPos, state);

                    if(blockEntity.progress[i] >= blockEntity.maxProgress[i]) {
                        craftItem(i, blockPos, state, blockEntity);
                    }
                }else {
                    blockEntity.hasEnoughEnergy[i] = false;
                    //Do not unlit block (Would flicker if energy is not inserted at the consumption rate or greater)
                }
            }else {
                blockEntity.resetProgress(i, blockPos, state);
                setChanged(level, blockPos, state);
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

        level.setBlock(blockPos, state.setValue(AdvancedPoweredFurnaceBlock.LIT, false), 3);
    }

    private static void craftItem(int index, BlockPos blockPos, BlockState state, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, blockEntity.itemHandler.getStackInSlot(index));
        inventory.setItem(1, blockEntity.itemHandler.getStackInSlot(3 + index));

        Optional<SmeltingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, inventory, level);

        if(!hasRecipe(index, blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.extractItem(index, 1, false);
        blockEntity.itemHandler.setStackInSlot(3 + index, new ItemStack(recipe.get().getResultItem().getItem(),
                blockEntity.itemHandler.getStackInSlot(3 + index).getCount() + recipe.get().getResultItem().getCount()));

        blockEntity.resetProgress(index, blockPos, state);
    }

    private static boolean hasRecipe(int index, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, blockEntity.itemHandler.getStackInSlot(index));
        inventory.setItem(1, blockEntity.itemHandler.getStackInSlot(3 + index));

        Optional<SmeltingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, inventory, level);

        inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        return recipe.isPresent() && canInsertAmountIntoOutputSlot(index, inventory, recipe.get().getResultItem().getCount()) &&
                canInsertItemIntoOutputSlot(index, inventory, recipe.get().getResultItem());
    }

    private static boolean canInsertItemIntoOutputSlot(int index, SimpleContainer inventory, ItemStack itemStack) {
        return inventory.getItem(3 + index).isEmpty() || inventory.getItem(3 + index).getItem() == itemStack.getItem();
    }

    private static boolean canInsertAmountIntoOutputSlot(int index, SimpleContainer inventory, int count) {
        return inventory.getItem(3 + index).getMaxStackSize() >= inventory.getItem(3 + index).getCount() + count;
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