package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AdvancedChargerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.screen.AdvancedChargerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
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

import java.util.Optional;

public class AdvancedChargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage, ItemStackHandler> {
    public static final float CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_ADVANCED_CHARGER_CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER.getValue();

    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
                if(i < 0 || i > 2)
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

                return energyStorage.receiveEnergy(AdvancedChargerBlockEntity.this.energyStorage.getMaxReceive() / 3, true) == 0;
            }));

    private int[] energyConsumptionLeft = new int[] {
            -1, -1, -1
    };

    public AdvancedChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_CHARGER_ENTITY.get(), blockPos, blockState,

                "advanced_charger",

                ModConfigs.COMMON_ADVANCED_CHARGER_CAPACITY_PER_SLOT.getValue() * 3,
                ModConfigs.COMMON_ADVANCED_CHARGER_TRANSFER_RATE_PER_SLOT.getValue() * 3,

                3,

                UpgradeModuleModifier.ENERGY_CAPACITY
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
                if(slot >= 0 && slot < 3) {
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
                if(slot >= 0 && slot < 3) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.isSameItem(stack, itemStack) ||
                            (!ItemStack.isSameItemSameTags(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(stack.getCapability(ForgeCapabilities.ENERGY).isPresent() && itemStack.getCapability(ForgeCapabilities.ENERGY).isPresent()))))
                        resetProgress(slot);
                }

                super.setStackInSlot(slot, stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(AdvancedChargerBlockEntity.this.energyConsumptionLeft[0], index);
                    case 2, 3 -> ByteUtils.get2Bytes(AdvancedChargerBlockEntity.this.energyConsumptionLeft[1], index - 2);
                    case 4, 5 -> ByteUtils.get2Bytes(AdvancedChargerBlockEntity.this.energyConsumptionLeft[2], index - 4);
                    case 6 -> redstoneMode.ordinal();
                    case 7 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2, 3, 4, 5 -> {}
                    case 6 -> AdvancedChargerBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 7 -> AdvancedChargerBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int getCount() {
                return 8;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new AdvancedChargerMenu(id, inventory, this, upgradeModuleInventory, this.data);
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
            nbt.put("recipe.energy_consumption_left." + i, IntTag.valueOf(energyConsumptionLeft[i]));
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        for(int i = 0;i < 3;i++)
            energyConsumptionLeft[i] = nbt.getInt("recipe.energy_consumption_left." + i);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, AdvancedChargerBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(AdvancedChargerBlock.POWERED)))
            return;

        final int maxReceivePerSlot = (int)Math.min(blockEntity.energyStorage.getMaxReceive() / 3.,
                Math.ceil(blockEntity.energyStorage.getEnergy() / 3.));

        for(int i = 0;i < 3;i++) {
            if(blockEntity.hasRecipe(i)) {
                ItemStack stack = blockEntity.itemHandler.getStackInSlot(i);
                int energyConsumptionPerTick;

                SimpleContainer inventory = new SimpleContainer(1);
                inventory.setItem(0, blockEntity.itemHandler.getStackInSlot(i));

                Optional<ChargerRecipe> recipe = level.getRecipeManager().getRecipeFor(ChargerRecipe.Type.INSTANCE, inventory, level);
                if(recipe.isPresent()) {
                    if(blockEntity.energyConsumptionLeft[i] == -1)
                        blockEntity.energyConsumptionLeft[i] = (int)(recipe.get().getEnergyConsumption() * CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER);

                    if(blockEntity.energyStorage.getEnergy() == 0) {
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    energyConsumptionPerTick = Math.min(blockEntity.energyConsumptionLeft[i], Math.min(maxReceivePerSlot,
                            blockEntity.energyStorage.getEnergy()));
                }else {
                    LazyOptional<IEnergyStorage> energyStorageLazyOptional = stack.getCapability(ForgeCapabilities.ENERGY);
                    if(!energyStorageLazyOptional.isPresent())
                        continue;

                    IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
                    if(!energyStorage.canReceive())
                        continue;

                    blockEntity.energyConsumptionLeft[i] = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();

                    if(blockEntity.energyStorage.getEnergy() == 0) {
                        setChanged(level, blockPos, state);

                        continue;
                    }

                    energyConsumptionPerTick = energyStorage.receiveEnergy(Math.min(maxReceivePerSlot,
                            blockEntity.energyStorage.getEnergy()), false);
                }

                if(blockEntity.energyConsumptionLeft[i] < 0 || energyConsumptionPerTick < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(i);
                    setChanged(level, blockPos, state);

                    continue;
                }

                blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick);
                blockEntity.energyConsumptionLeft[i] -= energyConsumptionPerTick;

                if(blockEntity.energyConsumptionLeft[i] <= 0) {
                    final int index = i;
                    recipe.ifPresent(chargerRecipe -> blockEntity.itemHandler.setStackInSlot(index,
                            chargerRecipe.getResultItem(level.registryAccess()).copyWithCount(1)));

                    blockEntity.resetProgress(i);
                }
                setChanged(level, blockPos, state);
            }else {
                blockEntity.resetProgress(i);
                setChanged(level, blockPos, state);
            }
        }
    }

    private void resetProgress(int index) {
        energyConsumptionLeft[index] = -1;
    }

    private boolean hasRecipe(int index) {
        ItemStack stack = itemHandler.getStackInSlot(index);
        if(stack.getCapability(ForgeCapabilities.ENERGY).isPresent())
            return true;

        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, itemHandler.getStackInSlot(index));

        Optional<ChargerRecipe> recipe = level.getRecipeManager().getRecipeFor(ChargerRecipe.Type.INSTANCE, inventory, level);

        return recipe.isPresent();
    }

    @Override
    protected void updateUpgradeModules() {
        for(int i = 0;i < 3;i++)
            resetProgress(i);

        super.updateUpgradeModules();
    }
}