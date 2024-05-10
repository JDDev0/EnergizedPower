package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FiltrationPlantBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.fluid.ModFluids;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import me.jddev0.ep.networking.packet.SyncFiltrationPlantCurrentRecipeS2CPacket;
import me.jddev0.ep.recipe.FiltrationPlantRecipe;
import me.jddev0.ep.screen.FiltrationPlantMenu;
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
import net.minecraft.world.ContainerListener;
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

public class FiltrationPlantBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate,
        FluidStoragePacketUpdate, RedstoneModeUpdate, ComparatorModeUpdate {
    public static final int ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_FILTRATION_PLANT_CONSUMPTION_PER_TICK.getValue();
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_FILTRATION_PLANT_TANK_CAPACITY.getValue();
    public static final int DIRTY_WATER_CONSUMPTION_PER_RECIPE = ModConfigs.COMMON_FILTRATION_PLANT_DIRTY_WATER_USAGE_PER_RECIPE.getValue();

    private static final int RECIPE_DURATION = ModConfigs.COMMON_FILTRATION_PLANT_RECIPE_DURATION.getValue();

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch(slot) {
                case 0, 1 -> stack.is(ModItems.CHARCOAL_FILTER.get());
                case 2, 3 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0 || i == 1, i -> i == 2 || i == 3);

    private final UpgradeModuleInventory upgradeModuleInventory = new UpgradeModuleInventory(
            UpgradeModuleModifier.SPEED,
            UpgradeModuleModifier.ENERGY_CONSUMPTION,
            UpgradeModuleModifier.ENERGY_CAPACITY
    );
    private final ContainerListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    private final ReceiveOnlyEnergyStorage energyStorage;

    private final EnergizedPowerFluidStorage fluidStorage;

    protected final ContainerData data;
    private int progress;
    private int maxProgress;
    private int energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    private ResourceLocation currentRecipeIdForLoad = null;
    private RecipeHolder<FiltrationPlantRecipe> currentRecipe = null;

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

    public FiltrationPlantBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.FILTRATION_PLANT_ENTITY.get(), blockPos, blockState);

        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        energyStorage = new ReceiveOnlyEnergyStorage(0, ModConfigs.COMMON_FILTRATION_PLANT_CAPACITY.getValue(),
                ModConfigs.COMMON_FILTRATION_PLANT_TRANSFER_RATE.getValue()) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            public int getMaxReceive() {
                return Math.max(1, (int)Math.ceil(maxReceive * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }

            @Override
            protected void onChange() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToPlayersWithinXBlocks(
                            new EnergySyncS2CPacket(getEnergy(), getCapacity(), getBlockPos()),
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
                    case 0 -> FluidStack.isSameFluid(stack, new FluidStack(ModFluids.DIRTY_WATER.get(), 1));
                    case 1 -> FluidStack.isSameFluid(stack, new FluidStack(Fluids.WATER, 1));
                    default -> false;
                };
            }
        };

        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(FiltrationPlantBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(FiltrationPlantBlockEntity.this.maxProgress, index - 2);
                    case 4, 5 -> ByteUtils.get2Bytes(FiltrationPlantBlockEntity.this.energyConsumptionLeft, index - 4);
                    case 6 -> hasEnoughEnergy?1:0;
                    case 7 -> redstoneMode.ordinal();
                    case 8 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> FiltrationPlantBlockEntity.this.progress = ByteUtils.with2Bytes(
                            FiltrationPlantBlockEntity.this.progress, (short)value, index
                    );
                    case 2, 3 -> FiltrationPlantBlockEntity.this.maxProgress = ByteUtils.with2Bytes(
                            FiltrationPlantBlockEntity.this.maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6 -> {}
                    case 7 -> FiltrationPlantBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 8 -> FiltrationPlantBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
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
        return Component.translatable("container.energizedpower.filtration_plant");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(), getBlockPos()), (ServerPlayer)player);
        for(int i = 0;i < 2;i++)
            ModMessages.sendToPlayer(new FluidSyncS2CPacket(i, fluidStorage.getFluidInTank(i), fluidStorage.getTankCapacity(i), worldPosition), (ServerPlayer)player);

        ModMessages.sendToPlayer(new SyncFiltrationPlantCurrentRecipeS2CPacket(getBlockPos(), currentRecipe), (ServerPlayer)player);

        return new FiltrationPlantMenu(id, inventory, this, upgradeModuleInventory, this.data);
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
        //Save Upgrade Module Inventory first
        nbt.put("upgrade_module_inventory", upgradeModuleInventory.saveToNBT(registries));

        nbt.put("inventory", itemHandler.serializeNBT(registries));
        nbt.put("energy", energyStorage.saveNBT());
        for(int i = 0;i < fluidStorage.getTanks();i++)
            nbt.put("fluid." + i, fluidStorage.getFluid(i).saveOptional(registries));

        if(currentRecipe != null)
            nbt.put("recipe.id", StringTag.valueOf(currentRecipe.id().toString()));

        nbt.put("recipe.progress", IntTag.valueOf(progress));
        nbt.put("recipe.max_progress", IntTag.valueOf(maxProgress));
        nbt.put("recipe.energy_consumption_left", IntTag.valueOf(energyConsumptionLeft));

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());

        super.saveAdditional(nbt, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        //Load Upgrade Module Inventory first
        upgradeModuleInventory.removeListener(updateUpgradeModuleListener);
        upgradeModuleInventory.loadFromNBT(nbt.getCompound("upgrade_module_inventory"), registries);
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

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
        maxProgress = nbt.getInt("recipe.max_progress");
        energyConsumptionLeft = nbt.getInt("recipe.energy_consumption_left");

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode"));
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);

        Containers.dropContents(level, worldPosition, upgradeModuleInventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, FiltrationPlantBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        //Load recipe
        if(blockEntity.currentRecipeIdForLoad != null) {
            List<RecipeHolder<FiltrationPlantRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(FiltrationPlantRecipe.Type.INSTANCE);
            blockEntity.currentRecipe = recipes.stream().
                    filter(recipe -> recipe.id().equals(blockEntity.currentRecipeIdForLoad)).
                    findFirst().orElse(null);

            blockEntity.currentRecipeIdForLoad = null;
        }

        if(!blockEntity.redstoneMode.isActive(state.getValue(FiltrationPlantBlock.POWERED)))
            return;

        if(hasRecipe(blockEntity)) {
            if(blockEntity.currentRecipe == null)
                return;

            if(blockEntity.maxProgress == 0)
                blockEntity.maxProgress = Math.max(1, (int)Math.ceil(RECIPE_DURATION /
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));

            int energyConsumptionPerTick = Math.max(1, (int)Math.ceil(ENERGY_USAGE_PER_TICK *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = energyConsumptionPerTick * blockEntity.maxProgress;

            if(energyConsumptionPerTick <= blockEntity.energyStorage.getEnergy()) {
                blockEntity.hasEnoughEnergy = true;

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    setChanged(level, blockPos, state);

                    return;
                }

                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick);
                blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

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
        maxProgress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = true;
    }

    private void craftItem() {
        if(currentRecipe == null || !hasRecipe(this))
            return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        FiltrationPlantRecipe recipe = currentRecipe.value();

        fluidStorage.drain(new FluidStack(ModFluids.DIRTY_WATER.get(), DIRTY_WATER_CONSUMPTION_PER_RECIPE), IFluidHandler.FluidAction.EXECUTE);
        fluidStorage.fill(new FluidStack(Fluids.WATER, DIRTY_WATER_CONSUMPTION_PER_RECIPE), IFluidHandler.FluidAction.EXECUTE);

        for(int i = 0;i < 2;i++) {
            ItemStack charcoalFilter = itemHandler.getStackInSlot(i).copy();
            if(charcoalFilter.isEmpty() && !charcoalFilter.is(ModItems.CHARCOAL_FILTER.get()))
                continue;

            charcoalFilter.hurtAndBreak(1, level.random, null, () -> charcoalFilter.setCount(0));
            itemHandler.setStackInSlot(i, charcoalFilter);
        }

        ItemStack[] outputs = recipe.generateOutputs(level.random);

        if(!outputs[0].isEmpty())
            itemHandler.setStackInSlot(2, outputs[0].copyWithCount(
                    itemHandler.getStackInSlot(2).getCount() + outputs[0].getCount()));
        if(!outputs[1].isEmpty())
            itemHandler.setStackInSlot(3, outputs[1].copyWithCount(
                    itemHandler.getStackInSlot(3).getCount() + outputs[1].getCount()));

        resetProgress(getBlockPos(), getBlockState());
    }

    private static boolean hasRecipe(FiltrationPlantBlockEntity blockEntity) {
        if(blockEntity.currentRecipe == null)
            return false;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        FiltrationPlantRecipe recipe = blockEntity.currentRecipe.value();

        ItemStack[] maxOutputs = recipe.getMaxOutputCounts();

        return blockEntity.fluidStorage.getFluid(0).getAmount() >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                blockEntity.fluidStorage.getCapacity(1) - blockEntity.fluidStorage.getFluid(1).getAmount() >= DIRTY_WATER_CONSUMPTION_PER_RECIPE &&
                blockEntity.itemHandler.getStackInSlot(0).is(ModItems.CHARCOAL_FILTER.get()) &&
                blockEntity.itemHandler.getStackInSlot(1).is(ModItems.CHARCOAL_FILTER.get()) &&
                (maxOutputs[0].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 2, maxOutputs[0])) &&
                (maxOutputs[1].isEmpty() || InventoryUtils.canInsertItemIntoSlot(inventory, 3, maxOutputs[1]));
    }

    public void changeRecipeIndex(boolean downUp) {
        if(level.isClientSide())
            return;

        List<RecipeHolder<FiltrationPlantRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(FiltrationPlantRecipe.Type.INSTANCE);
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
                new SyncFiltrationPlantCurrentRecipeS2CPacket(getBlockPos(), currentRecipe),
                getBlockPos(), (ServerLevel)level, 32
        );
    }

    public void setCurrentRecipe(@Nullable RecipeHolder<FiltrationPlantRecipe> currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    public @Nullable RecipeHolder<FiltrationPlantRecipe> getCurrentRecipe() {
        return currentRecipe;
    }

    private void updateUpgradeModules() {
        resetProgress(getBlockPos(), getBlockState());
        setChanged();
        if(level != null && !level.isClientSide())
            ModMessages.sendToPlayersWithinXBlocks(
                    new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(), getBlockPos()),
                    getBlockPos(), (ServerLevel)level, 32
            );
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