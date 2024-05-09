package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.StoneSolidifierBlock;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.upgrade.UpgradeModuleInventory;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.ComparatorModeUpdate;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.RedstoneModeUpdate;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import me.jddev0.ep.networking.packet.SyncStoneSolidifierCurrentRecipeS2CPacket;
import me.jddev0.ep.recipe.StoneSolidifierRecipe;
import me.jddev0.ep.screen.StoneSolidifierMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.FluidUtils;
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
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
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
import me.jddev0.ep.util.EnergyUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StoneSolidifierBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, EnergyStoragePacketUpdate,
        FluidStoragePacketUpdate, RedstoneModeUpdate, ComparatorModeUpdate {
    public static final long CAPACITY = ModConfigs.COMMON_STONE_SOLIDIFIER_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_STONE_SOLIDIFIER_TRANSFER_RATE.getValue();

    private static final int RECIPE_DURATION = ModConfigs.COMMON_CSTONE_SOLIDIFIER_RECIPE_DURATION.getValue();

    public static final long ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_STONE_SOLIDIFIER_CONSUMPTION_PER_TICK.getValue();
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(1000 *
            ModConfigs.COMMON_STONE_SOLIDIFIER_TANK_CAPACITY.getValue());

    final CachedSidedInventoryStorage<UnchargerBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    private final UpgradeModuleInventory upgradeModuleInventory = new UpgradeModuleInventory(
            UpgradeModuleModifier.SPEED,
            UpgradeModuleModifier.ENERGY_CONSUMPTION,
            UpgradeModuleModifier.ENERGY_CAPACITY
    );
    private final InventoryChangedListener updateUpgradeModuleListener = container -> updateUpgradeModules();

    final EnergizedPowerLimitingEnergyStorage energyStorage;
    private final EnergizedPowerEnergyStorage internalEnergyStorage;

    final CombinedStorage<FluidVariant, SimpleFluidStorage> fluidStorage;

    protected final PropertyDelegate data;

    private Identifier currentRecipeIdForLoad = null;
    private RecipeEntry<StoneSolidifierRecipe> currentRecipe = null;
    private int progress;
    private int maxProgress;
    private long energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    private @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

    public StoneSolidifierBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.STONE_SOLIDIFIER_ENTITY, blockPos, blockState);

        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

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
            public long getCapacity() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            new EnergySyncS2CPacket(getAmount(), getCapacity(), getPos())
                    );
                }
            }
        };
        energyStorage = new EnergizedPowerLimitingEnergyStorage(internalEnergyStorage, MAX_RECEIVE, 0) {
            @Override
            public long getMaxInsert() {
                return Math.max(1, (long)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };

        fluidStorage = new CombinedStorage<>(List.of(
                new SimpleFluidStorage(TANK_CAPACITY) {
                    @Override
                    protected void onFinalCommit() {
                        markDirty();

                        if(world != null && !world.isClient()) {
                            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                                    getPos(), (ServerWorld)world, 32,
                                    new FluidSyncS2CPacket(0, getFluid(), capacity, getPos())
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
                            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                                    getPos(), (ServerWorld)world, 32,
                                    new FluidSyncS2CPacket(1, getFluid(), capacity, getPos())
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
                    case 9 -> redstoneMode.ordinal();
                    case 10 -> comparatorMode.ordinal();
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
                    case 9 -> StoneSolidifierBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 10 -> StoneSolidifierBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 11;
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
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, new EnergySyncS2CPacket(internalEnergyStorage.getAmount(), internalEnergyStorage.getCapacity(), getPos()));
        for(int i = 0;i < 2;i++)
            ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, new FluidSyncS2CPacket(i,
                    fluidStorage.parts.get(i).getFluid(), fluidStorage.parts.get(i).getCapacity(), getPos()));

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, new SyncStoneSolidifierCurrentRecipeS2CPacket(getPos(), currentRecipe));

        return new StoneSolidifierMenu(id, this, inventory, internalInventory, upgradeModuleInventory, this.data);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }
    public int getRedstoneOutput() {
        return switch(comparatorMode) {
            case ITEM -> ScreenHandler.calculateComparatorOutput(internalInventory);
            case FLUID -> FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(internalEnergyStorage);
        };
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        //Save Upgrade Module Inventory first
        nbt.put("upgrade_module_inventory", upgradeModuleInventory.saveToNBT(registries));

        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.heldStacks, registries));
        nbt.putLong("energy", internalEnergyStorage.getAmount());
        for(int i = 0;i < fluidStorage.parts.size();i++)
            nbt.put("fluid." + i, fluidStorage.parts.get(i).getFluid().toNBT(new NbtCompound(), registries));

        if(currentRecipe != null)
            nbt.put("recipe.id", NbtString.of(currentRecipe.id().toString()));

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.max_progress", NbtInt.of(maxProgress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));

        nbt.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        nbt.putInt("configuration.comparator_mode", comparatorMode.ordinal());

        super.writeNbt(nbt, registries);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);

        //Load Upgrade Module Inventory first
        upgradeModuleInventory.removeListener(updateUpgradeModuleListener);
        upgradeModuleInventory.loadFromNBT(nbt.getCompound("upgrade_module_inventory"), registries);
        upgradeModuleInventory.addListener(updateUpgradeModuleListener);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.heldStacks, registries);
        internalEnergyStorage.setAmountWithoutUpdate(nbt.getLong("energy"));
        for(int i = 0;i < fluidStorage.parts.size();i++)
            fluidStorage.parts.get(i).setFluid(FluidStack.fromNbt(nbt.getCompound("fluid." + i), registries));

        if(nbt.contains("recipe.id")) {
            NbtElement tag = nbt.get("recipe.id");

            if(!(tag instanceof NbtString stringTag))
                throw new IllegalArgumentException("Tag must be of type StringTag!");

            currentRecipeIdForLoad = Identifier.tryParse(stringTag.asString());
        }

        progress = nbt.getInt("recipe.progress");
        maxProgress = nbt.getInt("recipe.max_progress");
        energyConsumptionLeft = nbt.getInt("recipe.energy_consumption_left");

        redstoneMode = RedstoneMode.fromIndex(nbt.getInt("configuration.redstone_mode"));
        comparatorMode = ComparatorMode.fromIndex(nbt.getInt("configuration.comparator_mode"));
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory);
        ItemScatterer.spawn(level, worldPosition, upgradeModuleInventory);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, StoneSolidifierBlockEntity blockEntity) {
        if(level.isClient())
            return;

        //Load recipe
        if(blockEntity.currentRecipeIdForLoad != null) {
            List<RecipeEntry<StoneSolidifierRecipe>> recipes = level.getRecipeManager().listAllOfType(StoneSolidifierRecipe.Type.INSTANCE);
            blockEntity.currentRecipe = recipes.stream().
                    filter(recipe -> recipe.id().equals(blockEntity.currentRecipeIdForLoad)).
                    findFirst().orElse(null);

            blockEntity.currentRecipeIdForLoad = null;
        }

        if(!blockEntity.redstoneMode.isActive(state.get(StoneSolidifierBlock.POWERED)))
            return;

        if(hasRecipe(blockEntity)) {
            if(blockEntity.currentRecipe == null)
                return;

            if(blockEntity.maxProgress == 0)
                blockEntity.maxProgress = Math.max(1, (int)Math.ceil(RECIPE_DURATION /
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));

            long energyConsumptionPerTick = Math.max(1, (long)Math.ceil(ENERGY_USAGE_PER_TICK *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = energyConsumptionPerTick * blockEntity.maxProgress;

            if(energyConsumptionPerTick <= blockEntity.internalEnergyStorage.getAmount()) {
                blockEntity.hasEnoughEnergy = true;

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    markDirty(level, blockPos, state);

                    return;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    blockEntity.internalEnergyStorage.extract(energyConsumptionPerTick, transaction);
                    transaction.commit();
                }
                blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

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
        maxProgress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = true;
    }

    public void craftItem() {
        if(currentRecipe == null || !hasRecipe(this))
            return;

        StoneSolidifierRecipe recipe = currentRecipe.value();

        try(Transaction transaction = Transaction.openOuter()) {
            fluidStorage.extract(FluidVariant.of(Fluids.WATER), FluidUtils.convertMilliBucketsToDroplets(
                    recipe.getWaterAmount()), transaction);
            fluidStorage.extract(FluidVariant.of(Fluids.LAVA), FluidUtils.convertMilliBucketsToDroplets(
                    recipe.getLavaAmount()), transaction);

            transaction.commit();
        }

        internalInventory.setStack(0, recipe.getResult(world.getRegistryManager()).copyWithCount(
                internalInventory.getStack(0).getCount() + recipe.getResult(world.getRegistryManager()).getCount()));

        resetProgress(getPos(), getCachedState());
    }

    private static boolean hasRecipe(StoneSolidifierBlockEntity blockEntity) {
        if(blockEntity.currentRecipe == null)
            return false;

        World level = blockEntity.world;

        StoneSolidifierRecipe recipe = blockEntity.currentRecipe.value();

        return blockEntity.fluidStorage.parts.get(0).getAmount() >= FluidUtils.convertMilliBucketsToDroplets(recipe.getWaterAmount()) &&
                blockEntity.fluidStorage.parts.get(1).getAmount() >= FluidUtils.convertMilliBucketsToDroplets(recipe.getLavaAmount()) &&
                canInsertItemIntoOutputSlot(blockEntity.internalInventory, recipe.getResult(level.getRegistryManager()));
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getStack(0);

        return inventoryItemStack.isEmpty() || (ItemStack.areItemsAndComponentsEqual(inventoryItemStack, itemStack) &&
                inventoryItemStack.getMaxCount() >= inventoryItemStack.getCount() + itemStack.getCount());
    }

    public void changeRecipeIndex(boolean downUp) {
        if(world.isClient())
            return;

        List<RecipeEntry<StoneSolidifierRecipe>> recipes = world.getRecipeManager().listAllOfType(StoneSolidifierRecipe.Type.INSTANCE);
        recipes = recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.value().getResult(world.getRegistryManager()).getTranslationKey())).
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

        resetProgress(getPos(), getCachedState());

        markDirty(world, getPos(), getCachedState());

        ModMessages.sendServerPacketToPlayersWithinXBlocks(
                getPos(), (ServerWorld)world, 32,
                new SyncStoneSolidifierCurrentRecipeS2CPacket(getPos(), currentRecipe)
        );
    }

    public void setCurrentRecipe(@Nullable RecipeEntry<StoneSolidifierRecipe> currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    public @Nullable RecipeEntry<StoneSolidifierRecipe> getCurrentRecipe() {
        return currentRecipe;
    }

    private void updateUpgradeModules() {
        resetProgress(getPos(), getCachedState());
        markDirty();
        if(world != null && !world.isClient()) {
            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getPos(), (ServerWorld)world, 32,
                    new EnergySyncS2CPacket(internalEnergyStorage.getAmount(), internalEnergyStorage.getCapacity(), getPos())
            );
        }
    }

    public FluidStack getFluid(int tank) {
        return fluidStorage.parts.get(tank).getFluid();
    }

    public long getTankCapacity(int tank) {
        return fluidStorage.parts.get(tank).getCapacity();
    }

    @Override
    public void setEnergy(long energy) {
        internalEnergyStorage.setAmountWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(long capacity) {
        internalEnergyStorage.setCapacityWithoutUpdate(capacity);
    }

    public long getEnergy() {
        return internalEnergyStorage.getAmount();
    }

    public long getCapacity() {
        return internalEnergyStorage.getCapacity();
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorage.parts.get(tank).setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, long capacity) {
        //Does nothing (capacity is final)
    }

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        markDirty();
    }

    @Override
    public void setNextComparatorMode() {
        comparatorMode = ComparatorMode.fromIndex(comparatorMode.ordinal() + 1);
        markDirty();
    }
}