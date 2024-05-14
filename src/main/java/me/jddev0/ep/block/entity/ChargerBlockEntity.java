package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ChargerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.screen.ChargerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.Optional;

public class ChargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleInventory>
        implements ExtendedScreenHandlerFactory {
    public static final double CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_CHARGER_CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i != 0)
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

    protected final PropertyDelegate data;
    private long energyConsumptionLeft = -1;

    public ChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.CHARGER_ENTITY, blockPos, blockState,

                ModConfigs.COMMON_CHARGER_CAPACITY.getValue(),
                ModConfigs.COMMON_CHARGER_TRANSFER_RATE.getValue(),

                1,

                UpgradeModuleModifier.ENERGY_CAPACITY
        );

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1, 2, 3 -> ByteUtils.get2Bytes(ChargerBlockEntity.this.energyConsumptionLeft, index);
                    case 4 -> redstoneMode.ordinal();
                    case 5 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1, 2, 3 -> {}
                    case 4 -> ChargerBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 5 -> ChargerBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 6;
            }
        };
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

                if(slot == 0) {
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
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(!stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.areItemsEqual(stack, itemStack) ||
                            (!ItemStack.canCombine(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(EnergyStorageUtil.isEnergyStorage(stack) && EnergyStorageUtil.isEnergyStorage(itemStack)))))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                ChargerBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.charger");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);

        return new ChargerMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        energyConsumptionLeft = nbt.getLong("recipe.energy_consumption_left");
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ChargerBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.redstoneMode.isActive(state.get(ChargerBlock.POWERED)))
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.itemHandler.getStack(0);
            long energyConsumptionPerTick = 0;

            Optional<RecipeEntry<ChargerRecipe>> recipe = level.getRecipeManager().getFirstMatch(ChargerRecipe.Type.INSTANCE, blockEntity.itemHandler, level);
            if(recipe.isPresent()) {
                if(blockEntity.energyConsumptionLeft == -1)
                    blockEntity.energyConsumptionLeft = (long)(recipe.get().value().getEnergyConsumption() * CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER);

                if(blockEntity.energyStorage.getAmount() == 0) {
                    markDirty(level, blockPos, state);

                    return;
                }

                energyConsumptionPerTick = Math.min(blockEntity.energyConsumptionLeft, Math.min(blockEntity.limitingEnergyStorage.getMaxInsert(),
                        blockEntity.energyStorage.getAmount()));
            }else {
                if(!EnergyStorageUtil.isEnergyStorage(stack))
                    return;

                EnergyStorage limitingEnergyStorage = EnergyStorage.ITEM.find(stack, ContainerItemContext.
                        ofSingleSlot(InventoryStorage.of(blockEntity.itemHandler, null).getSlots().get(0)));
                if(limitingEnergyStorage == null)
                    return;

                if(!limitingEnergyStorage.supportsInsertion())
                    return;

                blockEntity.energyConsumptionLeft = limitingEnergyStorage.getCapacity() - limitingEnergyStorage.getAmount();

                if(blockEntity.energyStorage.getAmount() == 0) {
                    markDirty(level, blockPos, state);

                    return;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    energyConsumptionPerTick = limitingEnergyStorage.insert(Math.min(blockEntity.limitingEnergyStorage.getMaxInsert(),
                            blockEntity.energyStorage.getAmount()), transaction);
                    transaction.commit();
                }
            }

            if(blockEntity.energyConsumptionLeft < 0 || energyConsumptionPerTick < 0) {
                //Reset progress for invalid values

                blockEntity.resetProgress();
                markDirty(level, blockPos, state);

                return;
            }

            try(Transaction transaction = Transaction.openOuter()) {
                energyConsumptionPerTick = blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);
                transaction.commit();
            }

            blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

            if(blockEntity.energyConsumptionLeft <= 0) {
                recipe.ifPresent(chargerRecipe ->
                        blockEntity.itemHandler.setStack(0, chargerRecipe.value().getResult(level.getRegistryManager()).copyWithCount(1)));

                blockEntity.resetProgress();
            }

            markDirty(level, blockPos, state);
        }else {
            blockEntity.resetProgress();
            markDirty(level, blockPos, state);
        }
    }

    private void resetProgress() {
        energyConsumptionLeft = -1;
    }

    private boolean hasRecipe() {
        ItemStack stack = itemHandler.getStack(0);

        Optional<RecipeEntry<ChargerRecipe>> recipe = world == null?Optional.empty():
                world.getRecipeManager().getFirstMatch(ChargerRecipe.Type.INSTANCE, itemHandler, world);

        if(recipe.isPresent())
            return true;

        return EnergyStorageUtil.isEnergyStorage(stack);
    }

    @Override
    protected void updateUpgradeModules() {
        resetProgress();

        super.updateUpgradeModules();
    }
}