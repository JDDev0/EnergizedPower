package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncCurrentRecipeS2CPacket;
import me.jddev0.ep.recipe.ChangeCurrentRecipeIndexPacketUpdate;
import me.jddev0.ep.recipe.CurrentRecipePacketUpdate;
import me.jddev0.ep.util.ByteUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

public abstract class SelectableRecipeFluidMachineBlockEntity
        <F extends Storage<FluidVariant>, R extends Recipe<Inventory>>
        extends ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity
        <EnergizedPowerEnergyStorage, SimpleInventory, F>
        implements ExtendedScreenHandlerFactory, ChangeCurrentRecipeIndexPacketUpdate, CurrentRecipePacketUpdate<R> {
    protected final String machineName;
    protected final UpgradableMenuProvider menuProvider;

    protected final RecipeType<R> recipeType;
    protected final RecipeSerializer<R> recipeSerializer;

    protected final long baseEnergyConsumptionPerTick;
    protected final int baseRecipeDuration;

    protected final PropertyDelegate data;

    protected int progress;
    protected int maxProgress;
    protected long energyConsumptionLeft = -1;
    protected boolean hasEnoughEnergy;

    protected Identifier currentRecipeIdForLoad;
    protected R currentRecipe;

    public SelectableRecipeFluidMachineBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                   String machineName, UpgradableMenuProvider menuProvider,
                                                   int slotCount, RecipeType<R> recipeType, RecipeSerializer<R> recipeSerializer,
                                                   int baseRecipeDuration,
                                                   long baseEnergyCapacity, long baseEnergyTransferRate, long baseEnergyConsumptionPerTick,
                                                   FluidStorageMethods<F> fluidStorageMethods, long baseTankCapacity,
                                                   UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, baseEnergyCapacity, baseEnergyTransferRate, slotCount, fluidStorageMethods,
                baseTankCapacity, upgradeModifierSlots);

        this.machineName = machineName;
        this.menuProvider = menuProvider;

        this.recipeType = recipeType;
        this.recipeSerializer = recipeSerializer;

        this.baseEnergyConsumptionPerTick = baseEnergyConsumptionPerTick;
        this.baseRecipeDuration = baseRecipeDuration;

        data = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(energyConsumptionLeft, index - 4);
                    case 8 -> hasEnoughEnergy?1:0;
                    case 9 -> redstoneMode.ordinal();
                    case 10 -> comparatorMode.ordinal();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> progress = ByteUtils.with2Bytes(
                            progress, (short)value, index
                    );
                    case 2, 3 -> maxProgress = ByteUtils.with2Bytes(
                            maxProgress, (short)value, index - 2
                    );
                    case 4, 5, 6, 7, 8 -> {}
                    case 9 -> redstoneMode = RedstoneMode.fromIndex(value);
                    case 10 -> comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 11;
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
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        if(currentRecipe != null)
            nbt.put("recipe.id", NbtString.of(currentRecipe.getId().toString()));

        nbt.put("recipe.progress", NbtInt.of(progress));
        nbt.put("recipe.max_progress", NbtInt.of(maxProgress));
        nbt.put("recipe.energy_consumption_left", NbtLong.of(energyConsumptionLeft));
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        if(nbt.contains("recipe.id"))
            currentRecipeIdForLoad = Identifier.tryParse(nbt.getString("recipe.id"));

        progress = nbt.getInt("recipe.progress");
        maxProgress = nbt.getInt("recipe.max_progress");
        energyConsumptionLeft = nbt.getInt("recipe.energy_consumption_left");
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower." + machineName);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);
        syncCurrentRecipeToPlayer(player);

        return menuProvider.createMenu(id, this, inventory, itemHandler, upgradeModuleInventory, data);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public static <F extends Storage<FluidVariant>, R extends Recipe<Inventory>> void tick(
            World level, BlockPos blockPos, BlockState state, SelectableRecipeFluidMachineBlockEntity<F, R> blockEntity) {
        if(level.isClient)
            return;

        //Load recipe
        if(blockEntity.currentRecipeIdForLoad != null) {
            List<R> recipes = level.getRecipeManager().listAllOfType(blockEntity.recipeType);
            blockEntity.currentRecipe = recipes.stream().
                    filter(recipe -> recipe.getId().equals(blockEntity.currentRecipeIdForLoad)).
                    findFirst().orElse(null);

            blockEntity.currentRecipeIdForLoad = null;
        }

        if(!blockEntity.redstoneMode.isActive(state.get(Properties.POWERED)))
            return;

        if(blockEntity.hasRecipe()) {
            if(blockEntity.currentRecipe == null)
                return;

            if(blockEntity.maxProgress == 0) {
                blockEntity.onStartCrafting(blockEntity.currentRecipe);

                blockEntity.maxProgress = Math.max(1, (int)Math.ceil(blockEntity.baseRecipeDuration *
                        blockEntity.getRecipeDependentRecipeDuration(blockEntity.currentRecipe) /
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.SPEED)));
            }

            long energyConsumptionPerTick = Math.max(1, (long)Math.ceil(blockEntity.baseEnergyConsumptionPerTick *
                    blockEntity.getRecipeDependentEnergyConsumption(blockEntity.currentRecipe) *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_CONSUMPTION)));

            if(blockEntity.energyConsumptionLeft < 0)
                blockEntity.energyConsumptionLeft = energyConsumptionPerTick * blockEntity.maxProgress;

            if(energyConsumptionPerTick <= blockEntity.energyStorage.getAmount()) {
                blockEntity.hasEnoughEnergy = true;

                if(blockEntity.progress < 0 || blockEntity.maxProgress < 0 || blockEntity.energyConsumptionLeft < 0) {
                    //Reset progress for invalid values

                    blockEntity.resetProgress();
                    markDirty(level, blockPos, state);

                    return;
                }

                try(Transaction transaction = Transaction.openOuter()) {
                    blockEntity.energyStorage.extract(energyConsumptionPerTick, transaction);
                    transaction.commit();
                }
                blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

                blockEntity.progress++;
                if(blockEntity.progress >= blockEntity.maxProgress)
                    blockEntity.craftItem(blockEntity.currentRecipe);

                markDirty(level, blockPos, state);
            }else {
                blockEntity.hasEnoughEnergy = false;
                markDirty(level, blockPos, state);
            }
        }else {
            blockEntity.resetProgress();
            markDirty(level, blockPos, state);
        }
    }

    protected double getRecipeDependentRecipeDuration(R recipe) {
        return 1;
    }

    protected double getRecipeDependentEnergyConsumption(R recipe) {
        return 1;
    }

    protected void resetProgress() {
        progress = 0;
        maxProgress = 0;
        energyConsumptionLeft = -1;
        hasEnoughEnergy = true;
    }

    protected boolean hasRecipe() {
        if(world == null || currentRecipe == null)
            return false;

        return canCraftRecipe(itemHandler, currentRecipe);
    }

    protected void onStartCrafting(R recipe) {}

    protected abstract void craftItem(R recipe);

    protected abstract boolean canCraftRecipe(SimpleInventory inventory, R recipe);

    @Override
    public void changeRecipeIndex(boolean downUp) {
        if(world == null || world.isClient())
            return;

        List<R> recipes = world.getRecipeManager().listAllOfType(recipeType);
        recipes = recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.getOutput(world.getRegistryManager()).
                        getTranslationKey())).
                toList();

        int currentIndex = -1;
        if(currentRecipe != null) {
            for(int i = 0;i < recipes.size();i++) {
                if(currentRecipe.getId().equals(recipes.get(i).getId())) {
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

        resetProgress();
        markDirty();

        syncCurrentRecipeToPlayers(32);
    }

    protected final void syncCurrentRecipeToPlayer(PlayerEntity player) {
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player,
                new SyncCurrentRecipeS2CPacket<>(getPos(), recipeSerializer, currentRecipe));
    }

    protected final void syncCurrentRecipeToPlayers(int distance) {
        if(world != null && !world.isClient())
            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    getPos(), (ServerWorld)world, distance,
                    new SyncCurrentRecipeS2CPacket<>(getPos(), recipeSerializer, currentRecipe)
            );
    }

    public @Nullable R getCurrentRecipe() {
        return currentRecipe;
    }

    @Override
    public void setCurrentRecipe(@Nullable R currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    @Override
    protected void updateUpgradeModules() {
        resetProgress();

        super.updateUpgradeModules();
    }
}