package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.StoneSolidifierBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import me.jddev0.ep.networking.packet.SyncStoneSolidifierCurrentRecipeS2CPacket;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.screen.StoneSolidifierMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StoneSolidifierBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate,
        FluidStoragePacketUpdate, RedstoneModeUpdate, ComparatorModeUpdate {
    public static final int ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_STONE_SOLIDIFIER_CONSUMPTION_PER_TICK.getValue();
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_STONE_SOLIDIFIER_TANK_CAPACITY.getValue();

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch(slot) {
                case 0 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> false, i -> i == 0);

    private final ReceiveOnlyEnergyStorage energyStorage;

    private final EnergizedPowerFluidStorage fluidStorage;

    protected final ContainerData data;

    private ResourceLocation currentRecipeIdForLoad = null;
    private RecipeHolder<StoneSolidifierRecipe> currentRecipe = null;
    private int progress;
    private int maxProgress = ModConfigs.COMMON_CSTONE_SOLIDIFIER_RECIPE_DURATION.getValue();
    private int energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

    public StoneSolidifierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.STONE_SOLIDIFIER_ENTITY.get(), blockPos, blockState);

        energyStorage = new ReceiveOnlyEnergyStorage(0, ModConfigs.COMMON_STONE_SOLIDIFIER_CAPACITY.getValue(),
                ModConfigs.COMMON_STONE_SOLIDIFIER_TRANSFER_RATE.getValue()) {
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

        fluidStorage = new EnergizedPowerFluidStorage(new int[] {
                TANK_CAPACITY, TANK_CAPACITY
        }) {
            @Override
            protected void onContentsChanged() {
                setChanged();

                if(level != null && !level.isClientSide())
                    for(int i = 0;i < getTanks();i++)
                        ModMessages.sendToPlayersWithinXBlocks(
                                new FluidSyncS2CPacket(i, getFluidInTank(i), getTankCapacity(i), getBlockPos()),
                                getBlockPos(), (ServerLevel)level, 32
                        );
            }

            @Override
            public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
                if(!super.isFluidValid(tank, stack))
                    return false;

                return switch(tank) {
                    case 0 -> FluidStack.isSameFluid(stack, new FluidStack(Fluids.WATER, 1));
                    case 1 -> FluidStack.isSameFluid(stack, new FluidStack(Fluids.LAVA, 1));
                    default -> false;
                };
            }
        };

        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(StoneSolidifierBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(StoneSolidifierBlockEntity.this.maxProgress, index - 2);
                    case 4, 5 -> ByteUtils.get2Bytes(StoneSolidifierBlockEntity.this.energyConsumptionLeft, index - 4);
                    case 6 -> hasEnoughEnergy?1:0;
                    case 7 -> redstoneMode.ordinal();
                    case 8 -> comparatorMode.ordinal();
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
                    case 4, 5, 6 -> {}
                    case 7 -> StoneSolidifierBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 8 -> StoneSolidifierBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int getCount() {
                return 9;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.stone_solidifier");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(), getBlockPos()), (ServerPlayer)player);
        for(int i = 0;i < 2;i++)
            ModMessages.sendToPlayer(new FluidSyncS2CPacket(i, fluidStorage.getFluidInTank(i), fluidStorage.getTankCapacity(i), worldPosition), (ServerPlayer)player);

        ModMessages.sendToPlayer(new SyncStoneSolidifierCurrentRecipeS2CPacket(getBlockPos(), currentRecipe), (ServerPlayer)player);

        return new StoneSolidifierMenu(id, inventory, this, data);
    }

    public int getRedstoneOutput() {
        return switch(comparatorMode) {
            case ITEM -> InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
            case FLUID -> FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
        };
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        nbt.put("inventory", itemHandler.serializeNBT(registries));
        nbt.put("energy", energyStorage.saveNBT());
        for(int i = 0;i < fluidStorage.getTanks();i++)
            nbt.put("fluid." + i, fluidStorage.getFluid(i).saveOptional(registries));

        if(currentRecipe != null)
            nbt.put("recipe.id", StringTag.valueOf(currentRecipe.id().toString()));

        nbt.put("recipe.progress", IntTag.valueOf(progress));
        nbt.put("recipe.energy_consumption_left", IntTag.valueOf(energyConsumptionLeft));

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());

        super.saveAdditional(nbt, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        itemHandler.deserializeNBT(registries, nbt.getCompound("inventory"));
        energyStorage.loadNBT(nbt.get("energy"));
        for(int i = 0;i < fluidStorage.getTanks();i++)
            fluidStorage.setFluid(i, FluidStack.parseOptional(registries, nbt.getCompound("fluid." + i)));

        if(nbt.contains("recipe.id")) {
            Tag tag = nbt.get("recipe.id");

            if(!(tag instanceof StringTag stringTag))
                throw new IllegalArgumentException("Tag must be of type StringTag!");

            currentRecipeIdForLoad = ResourceLocation.tryParse(stringTag.getAsString());
        }

        progress = nbt.getInt("recipe.progress");
        energyConsumptionLeft = nbt.getInt("recipe.energy_consumption_left");

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode"));
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, StoneSolidifierBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        //Load recipe
        if(blockEntity.currentRecipeIdForLoad != null) {
            List<RecipeHolder<StoneSolidifierRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(StoneSolidifierRecipe.Type.INSTANCE);
            blockEntity.currentRecipe = recipes.stream().
                    filter(recipe -> recipe.id().equals(blockEntity.currentRecipeIdForLoad)).
                    findFirst().orElse(null);

            blockEntity.currentRecipeIdForLoad = null;
        }

        if(!blockEntity.redstoneMode.isActive(state.getValue(StoneSolidifierBlock.POWERED)))
            return;

        if(hasRecipe(blockEntity)) {
            if(blockEntity.currentRecipe == null)
                return;

            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = ENERGY_USAGE_PER_TICK * blockEntity.maxProgress;

            if(ENERGY_USAGE_PER_TICK <= blockEntity.energyStorage.getEnergy()) {
                blockEntity.hasEnoughEnergy = true;

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    setChanged(level, blockPos, state);

                    return;
                }

                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - ENERGY_USAGE_PER_TICK);
                blockEntity.energyConsumptionLeft -= ENERGY_USAGE_PER_TICK;

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    blockEntity.craftItem();

                setChanged(level, blockPos, state);
            }else {
                blockEntity.hasEnoughEnergy = false;
                setChanged(level, blockPos, state);
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
    }

    public void craftItem() {
        if(currentRecipe == null || !hasRecipe(this))
            return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        StoneSolidifierRecipe recipe = currentRecipe.value();

        fluidStorage.drain(new FluidStack(Fluids.WATER, recipe.getWaterAmount()), IFluidHandler.FluidAction.EXECUTE);
        fluidStorage.drain(new FluidStack(Fluids.LAVA, recipe.getLavaAmount()), IFluidHandler.FluidAction.EXECUTE);

        itemHandler.setStackInSlot(0, recipe.getResultItem(level.registryAccess()).copyWithCount(
                itemHandler.getStackInSlot(0).getCount() + recipe.getResultItem(level.registryAccess()).getCount()));

        resetProgress(getBlockPos(), getBlockState());
    }

    private static boolean hasRecipe(StoneSolidifierBlockEntity blockEntity) {
        if(blockEntity.currentRecipe == null)
            return false;

        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        StoneSolidifierRecipe recipe = blockEntity.currentRecipe.value();

        return blockEntity.fluidStorage.getFluid(0).getAmount() >= recipe.getWaterAmount() &&
                blockEntity.fluidStorage.getFluid(1).getAmount() >= recipe.getLavaAmount() &&
                canInsertItemIntoOutputSlot(inventory, recipe.getResultItem(level.registryAccess()));
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getItem(0);

        return inventoryItemStack.isEmpty() || (ItemStack.isSameItemSameComponents(inventoryItemStack, itemStack) &&
                inventoryItemStack.getMaxStackSize() >= inventoryItemStack.getCount() + itemStack.getCount());
    }

    public void changeRecipeIndex(boolean downUp) {
        if(level.isClientSide())
            return;

        List<RecipeHolder<StoneSolidifierRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(StoneSolidifierRecipe.Type.INSTANCE);
        recipes = recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.value().getResultItem(level.registryAccess()).getDescriptionId())).
                collect(Collectors.toList());

        int currentIndex = -1;
        if(currentRecipe != null) {
            for(int i = 0;i < recipes.size();i++) {
                if(currentRecipe.id().equals(recipes.get(i).id())) {
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

        resetProgress(getBlockPos(), getBlockState());

        setChanged(level, getBlockPos(), getBlockState());

        ModMessages.sendToPlayersWithinXBlocks(
                new SyncStoneSolidifierCurrentRecipeS2CPacket(getBlockPos(), currentRecipe),
                getBlockPos(), (ServerLevel)level, 32
        );
    }

    public void setCurrentRecipe(@Nullable RecipeHolder<StoneSolidifierRecipe> currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    public @Nullable RecipeHolder<StoneSolidifierRecipe> getCurrentRecipe() {
        return currentRecipe;
    }

    public FluidStack getFluid(int tank) {
        return fluidStorage.getFluid(tank);
    }

    public int getTankCapacity(int tank) {
        return fluidStorage.getCapacity(tank);
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
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(tank, fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, int capacity) {
        fluidStorage.setCapacity(tank, capacity);
    }

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        setChanged();
    }

    @Override
    public void setNextComparatorMode() {
        comparatorMode = ComparatorMode.fromIndex(comparatorMode.ordinal() + 1);
        setChanged();
    }
}