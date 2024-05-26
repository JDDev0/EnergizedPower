package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.AdvancedChargerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.screen.AdvancedChargerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtLong;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.Optional;

public class AdvancedChargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleInventory> {
    public static final float CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_ADVANCED_CHARGER_CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i < 0 || i > 2)
            return false;

        ItemStack itemStack = itemHandler.getStack(i);
        if(world != null && RecipeUtils.isResultOfAny(world, ChargerRecipe.Type.INSTANCE, itemStack))
            return true;

        if(world == null || RecipeUtils.isIngredientOfAny(world, ChargerRecipe.Type.INSTANCE, itemStack))
            return false;

        if(!EnergyStorageUtil.isEnergyStorage(itemStack))
            return true;

        EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(itemStack, ContainerItemContext.
                ofSingleSlot(InventoryStorage.of(itemHandler, null).getSlots().get(i)));
        if(limitingEnergyStorage == null)
            return true;

        if(!limitingEnergyStorage.supportsInsertion())
            return true;

        return limitingEnergyStorage.getAmount() == limitingEnergyStorage.getCapacity();
    });

    private long[] energyConsumptionLeft = new long[] {
            -1, -1, -1
    };

    public AdvancedChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.ADVANCED_CHARGER_ENTITY, blockPos, blockState,

                "advanced_charger",

                ModConfigs.COMMON_ADVANCED_CHARGER_CAPACITY_PER_SLOT.getValue() * 3,
                ModConfigs.COMMON_ADVANCED_CHARGER_TRANSFER_RATE_PER_SLOT.getValue() * 3,

                3,

                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity, baseEnergyCapacity, baseEnergyCapacity) {
            @Override
            public long getCapacity() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                markDirty();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, baseEnergyTransferRate, 0) {
            @Override
            public long getMaxInsert() {
                return Math.max(1, (long)Math.ceil(maxInsert * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
            }
        };
    }

    @Override
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public int getMaxCountPerStack() {
                return 1;
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                if(world == null || RecipeUtils.isIngredientOfAny(world, ChargerRecipe.Type.INSTANCE, stack))
                    return true;

                if(slot >= 0 && slot < 3) {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        return false;

                    EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
                    if(limitingEnergyStorage == null)
                        return false;

                    return limitingEnergyStorage.supportsInsertion();
                }

                return super.isValid(slot, stack);
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    ItemStack itemStack = getStack(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.areItemsEqual(stack, itemStack) ||
                            (!ItemStack.areItemsAndComponentsEqual(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(EnergyStorageUtil.isEnergyStorage(stack) && EnergyStorageUtil.isEnergyStorage(itemStack)))))
                        resetProgress(slot);
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                AdvancedChargerBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1, 2, 3 -> ByteUtils.get2Bytes(AdvancedChargerBlockEntity.this.energyConsumptionLeft[0], index);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(AdvancedChargerBlockEntity.this.energyConsumptionLeft[1], index - 4);
                    case 8, 9, 10, 11 -> ByteUtils.get2Bytes(AdvancedChargerBlockEntity.this.energyConsumptionLeft[2], index - 8);
                    case 12 -> redstoneMode.ordinal();
                    case 13 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 -> {}
                    case 12 -> AdvancedChargerBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 13 -> AdvancedChargerBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 14;
            }
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);

        return new AdvancedChargerMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, RegistryWrapper.@NotNull WrapperLookup registries) {
        super.writeNbt(nbt, registries);

        for(int i = 0;i < 3;i++)
            nbt.put("recipe.energy_consumption_left." + i, NbtLong.of(energyConsumptionLeft[i]));
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.@NotNull WrapperLookup registries) {
        super.readNbt(nbt, registries);

        for(int i = 0;i < 3;i++)
            energyConsumptionLeft[i] = nbt.getLong("recipe.energy_consumption_left." + i);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AdvancedChargerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.redstoneMode.isActive(state.get(AdvancedChargerBlock.POWERED)))
            return;

        final long maxReceivePerSlot = (long)Math.min(blockEntity.limitingEnergyStorage.getMaxInsert() / 3.,
                Math.ceil(blockEntity.energyStorage.getAmount() / 3.));

        for(int i = 0;i < 3;i++) {
            if(blockEntity.hasRecipe(i)) {
                ItemStack stack = blockEntity.itemHandler.getStack(i);
                long energyConsumptionPerTick;

                SimpleInventory inventory = new SimpleInventory(1);
                inventory.setStack(0, blockEntity.itemHandler.getStack(i));

                Optional<RecipeEntry<ChargerRecipe>> recipe = level.getRecipeManager().getFirstMatch(ChargerRecipe.Type.INSTANCE, inventory, level);
                if(recipe.isPresent()) {
                    if(blockEntity.energyConsumptionLeft[i] == -1)
                        blockEntity.energyConsumptionLeft[i] = (long)(recipe.get().value().getEnergyConsumption() * CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER);

                    if(blockEntity.energyStorage.getAmount() == 0) {
                        markDirty(level, blockPos, state);

                        continue;
                    }

                    energyConsumptionPerTick = Math.min(blockEntity.energyConsumptionLeft[i], Math.min(maxReceivePerSlot,
                            blockEntity.energyStorage.getAmount()));
                }else {
                    if(!EnergyStorageUtil.isEnergyStorage(stack))
                        continue;

                    EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                            ofSingleSlot(InventoryStorage.of(blockEntity.itemHandler, null).getSlots().get(i)));
                    if(limitingEnergyStorage == null)
                        continue;

                    if(!limitingEnergyStorage.supportsInsertion())
                        continue;

                    blockEntity.energyConsumptionLeft[i] = limitingEnergyStorage.getCapacity() - limitingEnergyStorage.getAmount();

                    if(blockEntity.energyStorage.getAmount() == 0) {
                        markDirty(level, blockPos, state);

                        continue;
                    }

                    try(Transaction transaction = Transaction.openOuter()) {
                        energyConsumptionPerTick = limitingEnergyStorage.insert(Math.min(maxReceivePerSlot,
                                blockEntity.energyStorage.getAmount()), transaction);
                        transaction.commit();
                    }
                }

                if(blockEntity.energyConsumptionLeft[i] < 0 || energyConsumptionPerTick < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress(i);
                    markDirty(level, blockPos, state);

                    continue;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    energyConsumptionPerTick = blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);
                    transaction.commit();
                }

                blockEntity.energyConsumptionLeft[i] -= energyConsumptionPerTick;

                if(blockEntity.energyConsumptionLeft[i] <= 0) {
                    final int index = i;
                    recipe.ifPresent(advancedChargerRecipe ->
                            blockEntity.itemHandler.setStack(index, advancedChargerRecipe.value().getResult(level.getRegistryManager()).copyWithCount(1)));

                    blockEntity.resetProgress(i);
                }
                markDirty(level, blockPos, state);
            }else {
                blockEntity.resetProgress(i);
                markDirty(level, blockPos, state);
            }
        }
    }

    private void resetProgress(int index) {
        energyConsumptionLeft[index] = -1;
    }

    private boolean hasRecipe(int index) {
        ItemStack stack = itemHandler.getStack(index);

        SimpleInventory inventory = new SimpleInventory(1);
        inventory.setStack(0, itemHandler.getStack(index));

        Optional<RecipeEntry<ChargerRecipe>> recipe = world == null?Optional.empty():
                world.getRecipeManager().getFirstMatch(ChargerRecipe.Type.INSTANCE, inventory, world);

        if(recipe.isPresent())
            return true;

        return EnergyStorageUtil.isEnergyStorage(stack);
    }

    @Override
    protected void updateUpgradeModules() {
        for(int i = 0;i < 3;i++)
            resetProgress(i);

        super.updateUpgradeModules();
    }
}