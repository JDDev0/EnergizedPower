package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AdvancedPoweredFurnaceBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.screen.AdvancedPoweredFurnaceMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AdvancedPoweredFurnaceBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate, RedstoneModeUpdate,
        ComparatorModeUpdate {
    private static final List<@NotNull ResourceLocation> RECIPE_BLACKLIST = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_RECIPE_BLACKLIST.getValue();

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
                if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                    resetProgress(slot, worldPosition, level.getBlockState(worldPosition));
            }

            super.setStackInSlot(slot, stack);
        }
    };
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 3, i -> i >= 3 && i < 6);

    private final ReceiveOnlyEnergyStorage energyStorage;

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

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

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
                            getBlockPos(), (ServerLevel)level, 32
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
                    case 21 -> redstoneMode.ordinal();
                    case 22 -> comparatorMode.ordinal();
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
                    case 21 -> AdvancedPoweredFurnaceBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 22 -> AdvancedPoweredFurnaceBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int getCount() {
                return 23;
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
        return switch(comparatorMode) {
            case ITEM -> InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
            case FLUID -> 0;
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
        };
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        nbt.put("inventory", itemHandler.serializeNBT(registries));
        nbt.put("energy", energyStorage.saveNBT());

        for(int i = 0;i < 3;i++)
            nbt.put("recipe.progress." + i, IntTag.valueOf(progress[i]));
        for(int i = 0;i < 3;i++)
            nbt.put("recipe.max_progress." + i, IntTag.valueOf(maxProgress[i]));
        for(int i = 0;i < 3;i++)
            nbt.put("recipe.energy_consumption_left." + i, IntTag.valueOf(energyConsumptionLeft[i]));

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());

        super.saveAdditional(nbt, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        itemHandler.deserializeNBT(registries, nbt.getCompound("inventory"));
        energyStorage.loadNBT(nbt.get("energy"));

        for(int i = 0;i < 3;i++)
            progress[i] = nbt.getInt("recipe.progress." + i);
        for(int i = 0;i < 3;i++)
            maxProgress[i] = nbt.getInt("recipe.max_progress." + i);
        for(int i = 0;i < 3;i++)
            energyConsumptionLeft[i] = nbt.getInt("recipe.energy_consumption_left." + i);

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode"));
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

        if(!blockEntity.redstoneMode.isActive(state.getValue(AdvancedPoweredFurnaceBlock.POWERED)))
            return;

        for(int i = 0;i < 3;i++) {
            if(hasRecipe(i, blockEntity)) {
                SimpleContainer inventory = new SimpleContainer(2);
                inventory.setItem(0, blockEntity.itemHandler.getStackInSlot(i));
                inventory.setItem(1, blockEntity.itemHandler.getStackInSlot(3 + i));

                Optional<RecipeHolder<SmeltingRecipe>> recipe = blockEntity.getRecipeFor(inventory, level);
                if(recipe.isEmpty())
                    continue;

                int cookingTime = recipe.get().value().getCookingTime();
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
                    if(blockEntity.progress[i] >= blockEntity.maxProgress[i])
                        craftItem(i, blockPos, state, blockEntity);

                    setChanged(level, blockPos, state);
                }else {
                    blockEntity.hasEnoughEnergy[i] = false;
                    setChanged(level, blockPos, state);
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

        Optional<RecipeHolder<SmeltingRecipe>> recipe = blockEntity.getRecipeFor(inventory, level);

        if(!hasRecipe(index, blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.extractItem(index, 1, false);
        blockEntity.itemHandler.setStackInSlot(3 + index, recipe.get().value().getResultItem(level.registryAccess()).copyWithCount(
                blockEntity.itemHandler.getStackInSlot(3 + index).getCount() + recipe.get().value().getResultItem(level.registryAccess()).getCount()));

        blockEntity.resetProgress(index, blockPos, state);
    }

    private static boolean hasRecipe(int index, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, blockEntity.itemHandler.getStackInSlot(index));
        inventory.setItem(1, blockEntity.itemHandler.getStackInSlot(3 + index));

        Optional<RecipeHolder<SmeltingRecipe>> recipe = blockEntity.getRecipeFor(inventory, level);

        inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        return recipe.isPresent() && canInsertItemIntoOutputSlot(index, inventory, recipe.get().value().getResultItem(level.registryAccess()));
    }

    private static boolean canInsertItemIntoOutputSlot(int index, SimpleContainer inventory, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getItem(3 + index);

        return inventoryItemStack.isEmpty() || (ItemStack.isSameItemSameComponents(inventoryItemStack, itemStack) &&
                inventoryItemStack.getMaxStackSize() >= inventoryItemStack.getCount() + itemStack.getCount());
    }

    private Optional<RecipeHolder<SmeltingRecipe>> getRecipeFor(Container container, Level level) {
        return level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING).
                stream().filter(recipe -> !RECIPE_BLACKLIST.contains(recipe.id())).
                filter(recipe -> recipe.value().matches(container, level)).
                findFirst();
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

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        setChanged();
    }

    @Override
    public void setNextComparatorMode() {
        do {
            comparatorMode = ComparatorMode.fromIndex(comparatorMode.ordinal() + 1);
        }while(comparatorMode == ComparatorMode.FLUID); //Prevent the FLUID comparator mode from being selected
        setChanged();
    }
}