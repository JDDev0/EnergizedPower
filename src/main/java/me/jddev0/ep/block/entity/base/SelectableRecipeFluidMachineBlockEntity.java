package me.jddev0.ep.block.entity.base;

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
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class SelectableRecipeFluidMachineBlockEntity
        <F extends Storage<FluidVariant>, R extends Recipe<Inventory>>
        extends WorkerFluidMachineBlockEntity<F, R>
        implements ExtendedScreenHandlerFactory, ChangeCurrentRecipeIndexPacketUpdate, CurrentRecipePacketUpdate<R> {
    protected final String machineName;
    protected final UpgradableMenuProvider menuProvider;

    protected final RecipeType<R> recipeType;
    protected final RecipeSerializer<R> recipeSerializer;

    protected final PropertyDelegate data;

    protected Identifier currentRecipeIdForLoad;
    protected R currentRecipe;

    public SelectableRecipeFluidMachineBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                   String machineName, UpgradableMenuProvider menuProvider,
                                                   int slotCount, RecipeType<R> recipeType, RecipeSerializer<R> recipeSerializer,
                                                   int baseRecipeDuration,
                                                   long baseEnergyCapacity, long baseEnergyTransferRate, long baseEnergyConsumptionPerTick,
                                                   FluidStorageMethods<F> fluidStorageMethods, long baseTankCapacity,
                                                   UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, slotCount, baseRecipeDuration, baseEnergyCapacity, baseEnergyTransferRate,
                baseEnergyConsumptionPerTick, fluidStorageMethods, baseTankCapacity, upgradeModifierSlots);

        this.machineName = machineName;
        this.menuProvider = menuProvider;

        this.recipeType = recipeType;
        this.recipeSerializer = recipeSerializer;

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
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        if(currentRecipe != null)
            nbt.put("recipe.id", NbtString.of(currentRecipe.getId().toString()));
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        if(nbt.contains("recipe.id"))
            currentRecipeIdForLoad = Identifier.tryParse(nbt.getString("recipe.id"));
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

    @Override
    protected final void onTickStart() {
        //Load recipe
        if(currentRecipeIdForLoad != null) {
            List<R> recipes = world.getRecipeManager().listAllOfType(recipeType);
            currentRecipe = recipes.stream().
                    filter(recipe -> recipe.getId().equals(currentRecipeIdForLoad)).
                    findFirst().orElse(null);

            currentRecipeIdForLoad = null;
        }
    }

    @Override
    protected Optional<R> getCurrentWorkData() {
        return Optional.ofNullable(currentRecipe);
    }

    @Override
    protected final double getWorkDataDependentWorkDuration(R workData) {
        return getRecipeDependentRecipeDuration(workData);
    }

    protected double getRecipeDependentRecipeDuration(R recipe) {
        return 1;
    }

    @Override
    protected final double getWorkDataDependentEnergyConsumption(R workData) {
        return getRecipeDependentEnergyConsumption(workData);
    }

    protected double getRecipeDependentEnergyConsumption(R recipe) {
        return 1;
    }

    @Override
    protected final boolean hasWork() {
        return hasRecipe();
    }

    protected boolean hasRecipe() {
        if(world == null || currentRecipe == null)
            return false;

        return canCraftRecipe(itemHandler, currentRecipe);
    }

    @Override
    protected final void onWorkStarted(R workData) {
        onStartCrafting(workData);
    }

    protected void onStartCrafting(R recipe) {}

    @Override
    protected final void onWorkCompleted(R workData) {
        craftItem(workData);
    }

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
}