package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.WorkerFluidMachineBlockEntity;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.fluid.InputOutputFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncFurnaceRecipeTypeS2CPacket;
import me.jddev0.ep.recipe.FurnaceRecipeTypePacketUpdate;
import me.jddev0.ep.screen.PoweredFurnaceMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import me.jddev0.ep.util.XPUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PoweredFurnaceBlockEntity
        extends WorkerFluidMachineBlockEntity<RecipeHolder<? extends AbstractCookingRecipe>>
        implements FurnaceRecipeTypePacketUpdate {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_POWERED_FURNACE_FLUID_TANK_CAPACITY.getValue();

    private static final List<@NotNull ResourceLocation> RECIPE_BLACKLIST = ModConfigs.COMMON_POWERED_FURNACE_RECIPE_BLACKLIST.getValue();

    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);
    private final InputOutputFluidStorage fluidStorageSided = new InputOutputFluidStorage(fluidStorage, (i, stack) -> false, i -> true);

    private double leftoverXPAmount = 0;

    private @NotNull RecipeType<? extends AbstractCookingRecipe> recipeType = RecipeType.SMELTING;

    public PoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.POWERED_FURNACE_ENTITY.get(), blockPos, blockState,

                "powered_furnace",

                2, 1,

                ModConfigs.COMMON_POWERED_FURNACE_CAPACITY.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING,
                UpgradeModuleModifier.XP_YIELD
        );
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                boolean isInputSlot = slot < workerThreadCount;
                return isInputSlot && (level == null || RecipeUtils.isIngredientOfAny(level, getRecipeForFurnaceModeUpgrade(), stack));
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                boolean isInputSlot = slot < workerThreadCount;
                if(isInputSlot) {
                    ItemStack stack = itemHandler.getStackInSlot(slot);
                    if(!stack.isEmpty() && !previousItemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress(slot);
                }

                setChanged();
            }
        };
    }

    @Override
    protected EnergizedPowerFluidStorage initFluidStorage() {
        return new EnergizedPowerFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncFluidToPlayers(32);
            }

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                if(!super.isFluidValid(tank, stack) || level == null)
                    return false;

                return stack.is(Tags.Fluids.EXPERIENCE);
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress[0], value -> progress[0] = value),
                new ProgressValueContainerData(() -> maxProgress[0], value -> maxProgress[0] = value),
                new EnergyValueContainerData(() -> hasWork(0)?getCurrentWorkData(0).
                        map(workData -> getEnergyConsumptionFor(0, workData)).orElse(-1):-1, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft[0], value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy[0], value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);
        ModMessages.sendToPlayer(new SyncFurnaceRecipeTypeS2CPacket(getRecipeForFurnaceModeUpgrade(), getBlockPos()),
                (ServerPlayer)player);

        return new PoweredFurnaceMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return fluidStorage;

        return fluidStorageSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.putDouble("recipe.leftover_xp_amount", leftoverXPAmount);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        leftoverXPAmount = nbt.getDouble("recipe.leftover_xp_amount");
    }

    protected SimpleContainer getInventoryForRecipe(int thread) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < 2;i++)
            inventory.setItem(i, itemHandler.getStackInSlot(thread + i * workerThreadCount));

        return inventory;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Optional<RecipeHolder<? extends AbstractCookingRecipe>> getCurrentWorkData(int thread) {
        return (Optional<RecipeHolder<? extends AbstractCookingRecipe>>)getRecipeFor(getInventoryForRecipe(thread), level);
    }

    @Override
    protected final boolean hasWork(int thread) {
        return hasRecipe(thread);
    }

    @Override
    protected void onWorkStarted(int thread, RecipeHolder<? extends AbstractCookingRecipe> recipe) {}

    @Override
    protected void onWorkCompleted(int thread, RecipeHolder<? extends AbstractCookingRecipe> workData) {
        craftItem(thread, workData);
    }

    @Override
    protected double getWorkDataDependentWorkDuration(int thread, RecipeHolder<? extends AbstractCookingRecipe> recipe) {
        return recipe.value().getCookingTime() * RECIPE_DURATION_MULTIPLIER;
    }

    private void craftItem(int thread, RecipeHolder<? extends AbstractCookingRecipe> workData) {
        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = getRecipeFor(getInventoryForRecipe(thread), level);

        if(!hasRecipe(thread) || recipe.isEmpty())
            return;

        itemHandler.extractItem(thread, 1, false);
        itemHandler.setStackInSlot(thread + 1, recipe.get().value().getResultItem(level.registryAccess()).copyWithCount(
                itemHandler.getStackInSlot(thread + 1).getCount() + recipe.get().value().getResultItem(level.registryAccess()).getCount()));

        if(upgradeModuleInventory.getMainUpgradeModuleModifier(6) == UpgradeModuleModifier.XP_YIELD) {
            leftoverXPAmount += recipe.get().value().getExperience() * upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.XP_YIELD);
            int xpYieldThisTick = (int)leftoverXPAmount;

            //Only keep decimal part
            leftoverXPAmount -= xpYieldThisTick;

            //Do not check if overflow -> Extra XP should just vanish
            fluidStorage.fill(new FluidStack(EPFluids.LIQUID_XP, XPUtils.XP_TO_LIQUID_RATIO * xpYieldThisTick), IFluidHandler.FluidAction.EXECUTE);
        }

        resetProgress(thread);
    }

    private boolean hasRecipe(int thread) {
        SimpleContainer inventory = getInventoryForRecipe(thread);

        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = getRecipeFor(inventory, level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.get().value().getResultItem(level.registryAccess()));
    }

    private Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> getRecipeFor(Container container, Level level) {
        return level.getRecipeManager().getAllRecipesFor(getRecipeForFurnaceModeUpgrade()).
                stream().filter(recipe -> !RECIPE_BLACKLIST.contains(recipe.id())).
                filter(recipe -> recipe.value().matches(new SingleRecipeInput(container.getItem(0)), level)).
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
        super.updateUpgradeModules();

        if(level != null && !level.isClientSide())
            ModMessages.sendToPlayersWithinXBlocks(
                    new SyncFurnaceRecipeTypeS2CPacket(getRecipeForFurnaceModeUpgrade(), getBlockPos()),
                    getBlockPos(), (ServerLevel)level, 32
            );
    }
}