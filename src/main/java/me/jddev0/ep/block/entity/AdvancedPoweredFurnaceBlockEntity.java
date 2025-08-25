package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncFurnaceRecipeTypeS2CPacket;
import me.jddev0.ep.recipe.FurnaceRecipeTypePacketUpdate;
import me.jddev0.ep.screen.AdvancedPoweredFurnaceMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AdvancedPoweredFurnaceBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage, ItemStackHandler>
        implements FurnaceRecipeTypePacketUpdate {
    private static final List<@NotNull ResourceLocation> RECIPE_BLACKLIST = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_RECIPE_BLACKLIST.getValue();

    private static final int ENERGY_USAGE_PER_INPUT_PER_TICK = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_INPUT_PER_TICK.getValue();

    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 3, i -> i >= 3 && i < 6));

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

    private @NotNull RecipeType<? extends AbstractCookingRecipe> recipeType = RecipeType.SMELTING;

    private int timeoutOffState;

    public AdvancedPoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_POWERED_FURNACE_ENTITY.get(), blockPos, blockState,

                "advanced_powered_furnace",

                ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_CAPACITY.getValue(),
                ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_TRANSFER_RATE.getValue(),

                6,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE
        );
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
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2 -> level == null || RecipeUtils.isIngredientOfAny(level, getRecipeForFurnaceModeUpgrade(), stack);
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
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress[0], value -> progress[0] = value),
                new ProgressValueContainerData(() -> progress[1], value -> progress[1] = value),
                new ProgressValueContainerData(() -> progress[2], value -> progress[2] = value),
                new ProgressValueContainerData(() -> maxProgress[0], value -> maxProgress[0] = value),
                new ProgressValueContainerData(() -> maxProgress[1], value -> maxProgress[1] = value),
                new ProgressValueContainerData(() -> maxProgress[2], value -> maxProgress[2] = value),
                new EnergyValueContainerData(this::getEnergyConsumptionPerTickSum, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[0], value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[1], value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[2], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[0], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[1], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[2], value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        ModMessages.sendToPlayer(new SyncFurnaceRecipeTypeS2CPacket(getRecipeForFurnaceModeUpgrade(), getBlockPos()),
                (ServerPlayer)player);

        return new AdvancedPoweredFurnaceMenu(id, inventory, this, upgradeModuleInventory, this.data);
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

    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        for(int i = 0;i < 3;i++)
            nbt.put("recipe.progress." + i, IntTag.valueOf(progress[i]));
        for(int i = 0;i < 3;i++)
            nbt.put("recipe.max_progress." + i, IntTag.valueOf(maxProgress[i]));
        for(int i = 0;i < 3;i++)
            nbt.put("recipe.energy_consumption_left." + i, IntTag.valueOf(energyConsumptionLeft[i]));
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        for(int i = 0;i < 3;i++)
            progress[i] = nbt.getInt("recipe.progress." + i);
        for(int i = 0;i < 3;i++)
            maxProgress[i] = nbt.getInt("recipe.max_progress." + i);
        for(int i = 0;i < 3;i++)
            energyConsumptionLeft[i] = nbt.getInt("recipe.energy_consumption_left." + i);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(blockEntity.timeoutOffState > 0) {
            blockEntity.timeoutOffState--;

            if(blockEntity.timeoutOffState == 0 && level.getBlockState(blockPos).hasProperty(BlockStateProperties.LIT) &&
                    level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                level.setBlock(blockPos, state.setValue(BlockStateProperties.LIT, false), 3);
            }
        }

        if(!blockEntity.redstoneMode.isActive(state.getValue(BlockStateProperties.POWERED)))
            return;

        boolean hasNoRecipe = true;
        int hasNotEnoughEnergyCount = 0;
        for(int i = 0;i < 3;i++) {
            if(hasRecipe(i, blockEntity)) {
                hasNoRecipe = false;
                SimpleContainer inventory = new SimpleContainer(2);
                inventory.setItem(0, blockEntity.itemHandler.getStackInSlot(i));
                inventory.setItem(1, blockEntity.itemHandler.getStackInSlot(3 + i));

                Optional<? extends AbstractCookingRecipe> recipe = blockEntity.getRecipeFor(inventory, level);
                if(recipe.isEmpty())
                    continue;

                int cookingTime = recipe.get().getCookingTime();
                if(blockEntity.maxProgress[i] == 0)
                    //Default Cooking Time = 200 -> maxProgress = 34 (= 200 / 6)
                    blockEntity.maxProgress[i] = Math.max(1, (int)Math.ceil(cookingTime * RECIPE_DURATION_MULTIPLIER / 6.f /
                            blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));

                int energyUsagePerInputPerTick = Math.max(1, (int)Math.ceil(ENERGY_USAGE_PER_INPUT_PER_TICK *
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

                if(blockEntity.energyConsumptionLeft[i] < 0)
                    blockEntity.energyConsumptionLeft[i] = energyUsagePerInputPerTick * blockEntity.maxProgress[i];

                if(energyUsagePerInputPerTick <= blockEntity.energyStorage.getEnergy()) {
                    blockEntity.hasEnoughEnergy[i] = true;
                    blockEntity.timeoutOffState = 0;
                    if(level.getBlockState(blockPos).hasProperty(BlockStateProperties.LIT) &&
                            !level.getBlockState(blockPos).getValue(BlockStateProperties.LIT)) {
                        level.setBlock(blockPos, state.setValue(BlockStateProperties.LIT, true), 3);
                    }

                    if(blockEntity.progress[i] < 0 || blockEntity.maxProgress[i] < 0 || blockEntity.energyConsumptionLeft[i] < 0) {
                        //Reset progress for invalid values

                        blockEntity.resetProgress(i, blockPos, state);
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyUsagePerInputPerTick);
                    blockEntity.energyConsumptionLeft[i] -= energyUsagePerInputPerTick;

                    blockEntity.progress[i]++;
                    if(blockEntity.progress[i] >= blockEntity.maxProgress[i])
                        craftItem(i, blockPos, state, blockEntity);

                    setChanged(level, blockPos, state);
                }else {
                    blockEntity.hasEnoughEnergy[i] = false;
                    hasNotEnoughEnergyCount++;
                    setChanged(level, blockPos, state);
                }
            }else {
                blockEntity.resetProgress(i, blockPos, state);
                hasNotEnoughEnergyCount++;
                setChanged(level, blockPos, state);
            }
        }

        //Unlit if nothing is being smelted
        if(hasNoRecipe || hasNotEnoughEnergyCount == 3) {
            if(blockEntity.timeoutOffState == 0) {
                blockEntity.timeoutOffState = ModConfigs.COMMON_OFF_STATE_TIMEOUT.getValue();
            }
        }
    }
    
    protected final int getEnergyConsumptionPerTickSum() {
        int energyConsumptionSum = -1;

        for(int i = 0;i < 3;i++) {
            if(!hasRecipe(i, this))
                continue;

            SimpleContainer inventory = new SimpleContainer(2);
            inventory.setItem(0, itemHandler.getStackInSlot(i));
            inventory.setItem(1, itemHandler.getStackInSlot(3 + i));

            Optional<? extends AbstractCookingRecipe> recipe = getRecipeFor(inventory, level);
            if(recipe.isEmpty())
                continue;

            int energyConsumption = Math.max(1, (int)Math.ceil(ENERGY_USAGE_PER_INPUT_PER_TICK *
                    upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(energyConsumptionSum == -1)
                energyConsumptionSum = energyConsumption;
            else
                energyConsumptionSum += energyConsumption;

            if(energyConsumptionSum < 0)
                energyConsumptionSum = Integer.MAX_VALUE;
        }

        return energyConsumptionSum;
    }

    private void resetProgress(int index, BlockPos blockPos, BlockState state) {
        progress[index] = 0;
        maxProgress[index] = 0;
        energyConsumptionLeft[index] = -1;
        hasEnoughEnergy[index] = false;
    }

    private static void craftItem(int index, BlockPos blockPos, BlockState state, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, blockEntity.itemHandler.getStackInSlot(index));
        inventory.setItem(1, blockEntity.itemHandler.getStackInSlot(3 + index));

        Optional<? extends AbstractCookingRecipe> recipe = blockEntity.getRecipeFor(inventory, level);

        if(!hasRecipe(index, blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.extractItem(index, 1, false);
        blockEntity.itemHandler.setStackInSlot(3 + index, recipe.get().getResultItem(level.registryAccess()).copyWithCount(
                blockEntity.itemHandler.getStackInSlot(3 + index).getCount() + recipe.get().getResultItem(level.registryAccess()).getCount()));

        blockEntity.resetProgress(index, blockPos, state);
    }

    private static boolean hasRecipe(int index, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(2);
        inventory.setItem(0, blockEntity.itemHandler.getStackInSlot(index));
        inventory.setItem(1, blockEntity.itemHandler.getStackInSlot(3 + index));

        Optional<? extends AbstractCookingRecipe> recipe = blockEntity.getRecipeFor(inventory, level);

        inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 3 + index, recipe.get().getResultItem(level.registryAccess()));
    }

    private Optional<? extends AbstractCookingRecipe> getRecipeFor(Container container, Level level) {
        return level.getRecipeManager().getAllRecipesFor(getRecipeForFurnaceModeUpgrade()).
                stream().filter(recipe -> !RECIPE_BLACKLIST.contains(recipe.getId())).
                filter(recipe -> recipe.matches(container, level)).
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
    public void setRecipeType(RecipeType<? extends AbstractCookingRecipe> recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    protected void updateUpgradeModules() {
        for(int i = 0;i < 3;i++)
            resetProgress(i, getBlockPos(), getBlockState());

        super.updateUpgradeModules();

        if(level != null && !level.isClientSide())
            ModMessages.sendToPlayersWithinXBlocks(
                    new SyncFurnaceRecipeTypeS2CPacket(getRecipeForFurnaceModeUpgrade(), getBlockPos()),
                    getBlockPos(), level.dimension(), 32
            );
    }
}