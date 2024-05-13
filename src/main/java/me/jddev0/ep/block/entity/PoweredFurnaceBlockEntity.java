package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.PoweredFurnaceBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncFurnaceRecipeTypeS2CPacket;
import me.jddev0.ep.recipe.FurnaceRecipeTypePacketUpdate;
import me.jddev0.ep.screen.PoweredFurnaceMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PoweredFurnaceBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage, ItemStackHandler>
        implements MenuProvider, FurnaceRecipeTypePacketUpdate {
    private static final List<@NotNull ResourceLocation> RECIPE_BLACKLIST = ModConfigs.COMMON_POWERED_FURNACE_RECIPE_BLACKLIST.getValue();

    private static final int ENERGY_USAGE_PER_TICK = ModConfigs.COMMON_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_TICK.getValue();

    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1));

    protected final ContainerData data;
    private int progress;
    private int maxProgress;
    private int energyConsumptionLeft = -1;
    private boolean hasEnoughEnergy;

    private @NotNull RecipeType<? extends AbstractCookingRecipe> recipeType = RecipeType.SMELTING;

    public PoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.POWERED_FURNACE_ENTITY.get(), blockPos, blockState,

                ModConfigs.COMMON_POWERED_FURNACE_CAPACITY.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_TRANSFER_RATE.getValue(),

                2,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE
        );

        data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(PoweredFurnaceBlockEntity.this.progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(PoweredFurnaceBlockEntity.this.maxProgress, index - 2);
                    case 4, 5 -> ByteUtils.get2Bytes(PoweredFurnaceBlockEntity.this.energyConsumptionLeft, index - 4);
                    case 6 -> hasEnoughEnergy?1:0;
                    case 7 -> redstoneMode.ordinal();
                    case 8 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> PoweredFurnaceBlockEntity.this.progress = ByteUtils.with2Bytes(
                            PoweredFurnaceBlockEntity.this.progress, (short)value, index
                    );
                    case 2, 3 -> PoweredFurnaceBlockEntity.this.maxProgress = ByteUtils.with2Bytes(
                            PoweredFurnaceBlockEntity.this.maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6 -> {}
                    case 7 -> PoweredFurnaceBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 8 -> PoweredFurnaceBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int getCount() {
                return 9;
            }
        };
    }

    @Override
    protected ReceiveOnlyEnergyStorage initEnergyStorage() {
        return new ReceiveOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
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
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return  new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0 -> level == null || RecipeUtils.isIngredientOfAny(level, getRecipeForFurnaceModeUpgrade(), stack);
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
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.powered_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        ModMessages.sendToPlayer(new SyncFurnaceRecipeTypeS2CPacket(getRecipeForFurnaceModeUpgrade(), getBlockPos()),
                (ServerPlayer)player);

        return new PoweredFurnaceMenu(id, inventory, this, upgradeModuleInventory, this.data);
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
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.put("recipe.progress", IntTag.valueOf(progress));
        nbt.put("recipe.max_progress", IntTag.valueOf(maxProgress));
        nbt.put("recipe.energy_consumption_left", IntTag.valueOf(energyConsumptionLeft));
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        progress = nbt.getInt("recipe.progress");
        maxProgress = nbt.getInt("recipe.max_progress");
        energyConsumptionLeft = nbt.getInt("recipe.energy_consumption_left");
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, PoweredFurnaceBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(PoweredFurnaceBlock.POWERED)))
            return;

        if(hasRecipe(blockEntity)) {
            SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
            for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
                inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

            Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = blockEntity.getRecipeFor(inventory, level);
            if(recipe.isEmpty())
                return;

            int cookingTime = recipe.get().value().getCookingTime();
            if(blockEntity.maxProgress == 0)
                //Default Cooking Time = 200 -> maxProgress = 100 (= 200 / 2)
                blockEntity.maxProgress = Math.max(1, (int)Math.ceil(cookingTime * RECIPE_DURATION_MULTIPLIER / 2.f /
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));

            int energyUsagePerTick = Math.max(1, (int)Math.ceil(ENERGY_USAGE_PER_TICK *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = energyUsagePerTick * blockEntity.maxProgress;

            if(energyUsagePerTick <= blockEntity.energyStorage.getEnergy()) {
                if(!level.getBlockState(blockPos).hasProperty(PoweredFurnaceBlock.LIT) || !level.getBlockState(blockPos).getValue(PoweredFurnaceBlock.LIT)) {
                    blockEntity.hasEnoughEnergy = true;
                    level.setBlock(blockPos, state.setValue(PoweredFurnaceBlock.LIT, Boolean.TRUE), 3);
                }

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(blockPos, state);
                    setChanged(level, blockPos, state);

                    return;
                }

                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyUsagePerTick);
                blockEntity.energyConsumptionLeft -= energyUsagePerTick;

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    craftItem(blockPos, state, blockEntity);

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

        level.setBlock(blockPos, state.setValue(PoweredFurnaceBlock.LIT, false), 3);
    }

    private static void craftItem(BlockPos blockPos, BlockState state, PoweredFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = blockEntity.getRecipeFor(inventory, level);

        if(!hasRecipe(blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.extractItem(0, 1, false);
        blockEntity.itemHandler.setStackInSlot(1, recipe.get().value().getResultItem(level.registryAccess()).copyWithCount(
                blockEntity.itemHandler.getStackInSlot(1).getCount() + recipe.get().value().getResultItem(level.registryAccess()).getCount()));

        blockEntity.resetProgress(blockPos, state);
    }

    private static boolean hasRecipe(PoweredFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = blockEntity.getRecipeFor(inventory, level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.get().value().getResultItem(level.registryAccess()));
    }

    private Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> getRecipeFor(Container container, Level level) {
        return level.getRecipeManager().getAllRecipesFor(getRecipeForFurnaceModeUpgrade()).
                stream().filter(recipe -> !RECIPE_BLACKLIST.contains(recipe.id())).
                filter(recipe -> recipe.value().matches(container, level)).
                findFirst();
    }

    public RecipeType<? extends AbstractCookingRecipe> getRecipeForFurnaceModeUpgrade() {
        if(level != null && level.isClientSide())
            return recipeType;

        double value = upgradeModuleInventory.getUpgradeModuleModifierEffect(3, UpgradeModuleModifier.FURNACE_MODE);
        if(value == 1)
            return RecipeType.BLASTING;
        else if(value == 2)
            return RecipeType.SMOKING;

        return RecipeType.SMELTING;
    }

    @Override
    public void setRecipeType(@NotNull RecipeType<? extends AbstractCookingRecipe> recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    protected void updateUpgradeModules() {
        resetProgress(getBlockPos(), getBlockState());

        super.updateUpgradeModules();

        if(level != null && !level.isClientSide())
            ModMessages.sendToPlayersWithinXBlocks(
                    new SyncFurnaceRecipeTypeS2CPacket(getRecipeForFurnaceModeUpgrade(), getBlockPos()),
                    getBlockPos(), level.dimension(), 32
            );
    }
}