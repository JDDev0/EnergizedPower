package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncFurnaceRecipeTypeS2CPacket;
import me.jddev0.ep.recipe.FurnaceRecipeTypePacketUpdate;
import me.jddev0.ep.screen.AdvancedPoweredFurnaceMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.ItemStackUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.List;
import java.util.Optional;

public class AdvancedPoweredFurnaceBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleInventory>
        implements FurnaceRecipeTypePacketUpdate {
    private static final List<@NotNull Identifier> RECIPE_BLACKLIST = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_RECIPE_BLACKLIST.getValue();

    private static final long ENERGY_USAGE_PER_INPUT_PER_TICK = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_INPUT_PER_TICK.getValue();

    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_ADVANCED_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i >= 0 && i < 3, i -> i >= 3 && i < 6);

    private int[] progress = new int[] {
            0, 0, 0
    };
    private int[] maxProgress = new int[] {
            0, 0, 0
    };
    private long[] energyConsumptionLeft = new long[] {
            -1, -1, -1
    };
    private boolean[] hasEnoughEnergy = new boolean[] {
            false, false, false
    };

    private @NotNull RecipeType<? extends AbstractCookingRecipe> recipeType = RecipeType.SMELTING;

    public AdvancedPoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.ADVANCED_POWERED_FURNACE_ENTITY, blockPos, blockState,

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
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0, 1, 2 -> world == null || RecipeUtils.isIngredientOfAny(world, getRecipeForFurnaceModeUpgrade(), stack);
                    case 3, 4, 5 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot >= 0 && slot < 3) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.canCombine(stack, itemStack))
                        resetProgress(slot, pos, world.getBlockState(pos));
                }

                super.setStack(slot, stack);
            }

            @Override
            public void markDirty() {
                super.markDirty();

                AdvancedPoweredFurnaceBlockEntity.this.markDirty();
            }
        };
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1, 2, 3 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.energyConsumptionLeft[0], index);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.energyConsumptionLeft[1], index - 4);
                    case 8, 9, 10, 11 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.energyConsumptionLeft[2], index - 8);
                    case 12, 13 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.progress[0], index - 12);
                    case 14, 15 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.maxProgress[0], index - 14);
                    case 16, 17 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.progress[1], index - 16);
                    case 18, 19 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.maxProgress[1], index - 18);
                    case 20, 21 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.progress[2], index - 20);
                    case 22, 23 -> ByteUtils.get2Bytes(AdvancedPoweredFurnaceBlockEntity.this.maxProgress[2], index - 22);
                    case 24 -> AdvancedPoweredFurnaceBlockEntity.this.hasEnoughEnergy[0]?0:1;
                    case 25 -> AdvancedPoweredFurnaceBlockEntity.this.hasEnoughEnergy[1]?0:1;
                    case 26 -> AdvancedPoweredFurnaceBlockEntity.this.hasEnoughEnergy[2]?0:1;
                    case 27 -> redstoneMode.ordinal();
                    case 28 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 12, 13 -> AdvancedPoweredFurnaceBlockEntity.this.progress[0] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.progress[0], (short)value, index - 12
                    );
                    case 14, 15 -> AdvancedPoweredFurnaceBlockEntity.this.maxProgress[0] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.maxProgress[0], (short)value, index - 14
                    );
                    case 16, 17 -> AdvancedPoweredFurnaceBlockEntity.this.progress[1] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.progress[1], (short)value, index - 16
                    );
                    case 18, 19 -> AdvancedPoweredFurnaceBlockEntity.this.maxProgress[1] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.maxProgress[1], (short)value, index - 18
                    );
                    case 20, 21 -> AdvancedPoweredFurnaceBlockEntity.this.progress[2] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.progress[2], (short)value, index - 20
                    );
                    case 22, 23 -> AdvancedPoweredFurnaceBlockEntity.this.maxProgress[2] = ByteUtils.with2Bytes(
                            AdvancedPoweredFurnaceBlockEntity.this.maxProgress[2], (short)value, index - 22
                    );
                    case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 24, 25, 26 -> {}
                    case 27 -> AdvancedPoweredFurnaceBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 28 -> AdvancedPoweredFurnaceBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 29;
            }
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player,
                new SyncFurnaceRecipeTypeS2CPacket(getRecipeForFurnaceModeUpgrade(), getPos()));

        return new AdvancedPoweredFurnaceMenu(id, this, inventory, itemHandler, upgradeModuleInventory, this.data);
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        for(int i = 0;i < 3;i++)
            nbt.put("recipe.progress." + i, NbtInt.of(progress[i]));
        for(int i = 0;i < 3;i++)
            nbt.put("recipe.max_progress." + i, NbtInt.of(maxProgress[i]));
        for(int i = 0;i < 3;i++)
            nbt.put("recipe.energy_consumption_left." + i, NbtLong.of(energyConsumptionLeft[i]));
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        for(int i = 0;i < 3;i++)
            progress[i] = nbt.getInt("recipe.progress." + i);
        for(int i = 0;i < 3;i++)
            maxProgress[i] = nbt.getInt("recipe.max_progress." + i);
        for(int i = 0;i < 3;i++)
            energyConsumptionLeft[i] = nbt.getLong("recipe.energy_consumption_left." + i);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(!blockEntity.redstoneMode.isActive(state.get(Properties.POWERED)))
            return;

        boolean hasNoRecipe = true;
        int hasNotEnoughEnergyCount = 0;
        for(int i = 0;i < 3;i++) {
            if(hasRecipe(i, blockEntity)) {
                hasNoRecipe = false;
                SimpleInventory inventory = new SimpleInventory(2);
                inventory.setStack(0, blockEntity.itemHandler.getStack(i));
                inventory.setStack(1, blockEntity.itemHandler.getStack(3 + i));

                Optional<? extends AbstractCookingRecipe> recipe = blockEntity.getRecipeFor(inventory, level);
                if(recipe.isEmpty())
                    continue;

                int cookingTime = recipe.get().getCookTime();
                if(blockEntity.maxProgress[i] == 0)
                    //Default Cooking Time = 200 -> maxProgress = 34 (= 200 / 6)
                    blockEntity.maxProgress[i] = Math.max(1, (int)Math.ceil(cookingTime * RECIPE_DURATION_MULTIPLIER / 6.f /
                            blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));

                long energyUsagePerInputPerTick = Math.max(1, (long)Math.ceil(ENERGY_USAGE_PER_INPUT_PER_TICK *
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

                if(blockEntity.energyConsumptionLeft[i] < 0)
                    blockEntity.energyConsumptionLeft[i] = energyUsagePerInputPerTick * blockEntity.maxProgress[i];

                if(energyUsagePerInputPerTick <= blockEntity.energyStorage.getAmount()) {
                    blockEntity.hasEnoughEnergy[i] = true;
                    if(level.getBlockState(blockPos).contains(Properties.LIT) &&
                            !level.getBlockState(blockPos).get(Properties.LIT)) {
                        level.setBlockState(blockPos, state.with(Properties.LIT, true), 3);
                    }

                    if(blockEntity.progress[i] < 0 || blockEntity.maxProgress[i] < 0 || blockEntity.energyConsumptionLeft[i] < 0) {
                        //Reset progress for invalid values

                        blockEntity.resetProgress(i, blockPos, state);
                        markDirty(level, blockPos, state);

                        continue;
                    }

                    try(Transaction transaction = Transaction.openOuter()) {
                        blockEntity.energyStorage.extract(energyUsagePerInputPerTick, transaction);
                        transaction.commit();
                    }
                    blockEntity.energyConsumptionLeft[i] -= energyUsagePerInputPerTick;

                    blockEntity.progress[i]++;
                    if(blockEntity.progress[i] >= blockEntity.maxProgress[i])
                        craftItem(i, blockPos, state, blockEntity);

                    markDirty(level, blockPos, state);
                }else {
                    blockEntity.hasEnoughEnergy[i] = false;
                    hasNotEnoughEnergyCount++;
                    markDirty(level, blockPos, state);
                }
            }else {
                blockEntity.resetProgress(i, blockPos, state);
                hasNotEnoughEnergyCount++;
                markDirty(level, blockPos, state);
            }
        }

        //Unlit if nothing is being smelted
        if(hasNoRecipe || hasNotEnoughEnergyCount == 3) {
            if(level.getBlockState(blockPos).contains(Properties.LIT) &&
                    level.getBlockState(blockPos).get(Properties.LIT)) {
                level.setBlockState(blockPos, state.with(Properties.LIT, false), 3);
            }
        }
    }

    private void resetProgress(int index, BlockPos blockPos, BlockState state) {
        progress[index] = 0;
        maxProgress[index] = 0;
        energyConsumptionLeft[index] = -1;
        hasEnoughEnergy[index] = false;
    }

    private static void craftItem(int index, BlockPos blockPos, BlockState state, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        World level = blockEntity.world;

        SimpleInventory inventory = new SimpleInventory(2);
        inventory.setStack(0, blockEntity.itemHandler.getStack(index));
        inventory.setStack(1, blockEntity.itemHandler.getStack(3 + index));

        Optional<? extends AbstractCookingRecipe> recipe = blockEntity.getRecipeFor(inventory, level);

        if(!hasRecipe(index, blockEntity) || recipe.isEmpty())
            return;

        blockEntity.itemHandler.removeStack(index, 1);
        blockEntity.itemHandler.setStack(3 + index, ItemStackUtils.copyWithCount(recipe.get().getOutput(),
                blockEntity.itemHandler.getStack(3 + index).getCount() + recipe.get().getOutput().getCount()));

        blockEntity.resetProgress(index, blockPos, state);
    }

    private static boolean hasRecipe(int index, AdvancedPoweredFurnaceBlockEntity blockEntity) {
        World level = blockEntity.world;

        SimpleInventory inventory = new SimpleInventory(2);
        inventory.setStack(0, blockEntity.itemHandler.getStack(index));
        inventory.setStack(1, blockEntity.itemHandler.getStack(3 + index));

        Optional<? extends AbstractCookingRecipe> recipe = blockEntity.getRecipeFor(inventory, level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(blockEntity.itemHandler, 3 + index, recipe.get().getOutput());
    }

    private Optional<? extends AbstractCookingRecipe> getRecipeFor(Inventory container, World level) {
        return level.getRecipeManager().listAllOfType(getRecipeForFurnaceModeUpgrade()).
                stream().filter(recipe -> !RECIPE_BLACKLIST.contains(recipe.getId())).
                filter(recipe -> recipe.matches(container, level)).
                findFirst();
    }

    public RecipeType<? extends AbstractCookingRecipe> getRecipeForFurnaceModeUpgrade() {
        if(world != null && world.isClient())
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
            resetProgress(i, getPos(), getCachedState());

        super.updateUpgradeModules();

        if(world != null && !world.isClient()) {
            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getPos(), (ServerWorld)world, 32,
                    new SyncFurnaceRecipeTypeS2CPacket(getRecipeForFurnaceModeUpgrade(), getPos())
            );
        }
    }
}