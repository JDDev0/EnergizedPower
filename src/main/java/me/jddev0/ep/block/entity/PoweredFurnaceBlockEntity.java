package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.WorkerMachineBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.IngredientPacketUpdate;
import me.jddev0.ep.screen.PoweredFurnaceMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PoweredFurnaceBlockEntity
        extends WorkerMachineBlockEntity<RecipeHolder<? extends AbstractCookingRecipe>>
        implements IngredientPacketUpdate {
    private static final List<@NotNull ResourceLocation> RECIPE_BLACKLIST = ModConfigs.COMMON_POWERED_FURNACE_RECIPE_BLACKLIST.getValue();

    public static final float RECIPE_DURATION_MULTIPLIER = ModConfigs.COMMON_POWERED_FURNACE_RECIPE_DURATION_MULTIPLIER.getValue();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    protected List<Ingredient> ingredientsOfRecipes = new ArrayList<>();

    public PoweredFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.POWERED_FURNACE_ENTITY.get(), blockPos, blockState,

                "powered_furnace",

                2, 1,

                ModConfigs.COMMON_POWERED_FURNACE_CAPACITY.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_POWERED_FURNACE_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.FURNACE_MODE
        );
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
                    case 0 -> (level instanceof ServerLevel serverLevel)?
                            RecipeUtils.isIngredientOfAny(serverLevel, getRecipeForFurnaceModeUpgrade(), stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack);
                    case 1 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress();
                }

                super.setStackInSlot(slot, stack);
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new ContainerData() {
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

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncIngredientListToPlayer(player);

        return new PoweredFurnaceMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return energyStorage;
    }

    @Override
    protected void onHasEnoughEnergy() {
        if(level.getBlockState(getBlockPos()).hasProperty(BlockStateProperties.LIT) &&
                !level.getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT)) {
            level.setBlock(getBlockPos(), getBlockState().setValue(BlockStateProperties.LIT, true), 3);
        }
    }

    @Override
    protected void onHasNotEnoughEnergy() {
        if(level.getBlockState(getBlockPos()).hasProperty(BlockStateProperties.LIT) &&
                level.getBlockState(getBlockPos()).getValue(BlockStateProperties.LIT)) {
            level.setBlock(getBlockPos(), getBlockState().setValue(BlockStateProperties.LIT, false), 3);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Optional<RecipeHolder<? extends AbstractCookingRecipe>> getCurrentWorkData() {

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        return (Optional<RecipeHolder<? extends AbstractCookingRecipe>>)getRecipeFor(inventory, level);
    }

    @Override
    protected boolean hasWork() {
        return hasRecipe(this);
    }

    @Override
    protected void onWorkStarted(RecipeHolder<? extends AbstractCookingRecipe> recipe) {}

    @Override
    protected void onWorkCompleted(RecipeHolder<? extends AbstractCookingRecipe> workData) {
        craftItem(getBlockPos(), getBlockState(), this);
    }

    @Override
    protected double getWorkDataDependentWorkDuration(RecipeHolder<? extends AbstractCookingRecipe> recipe) {
        //Default Cooking Time = 200 -> maxProgress = 100 (= 200 / 2)
        return recipe.value().cookingTime() * RECIPE_DURATION_MULTIPLIER / 2.f;
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
        blockEntity.itemHandler.setStackInSlot(1, recipe.get().value().assemble(null, level.registryAccess()).copyWithCount(
                blockEntity.itemHandler.getStackInSlot(1).getCount() + recipe.get().value().assemble(null, level.registryAccess()).getCount()));

        blockEntity.resetProgress();
    }

    private static boolean hasRecipe(PoweredFurnaceBlockEntity blockEntity) {
        Level level = blockEntity.level;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

        Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> recipe = blockEntity.getRecipeFor(inventory, level);

        return recipe.isPresent() &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.get().value().assemble(null, level.registryAccess()));
    }

    private Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> getRecipeFor(Container container, Level level) {
        if(!(level instanceof ServerLevel serverLevel))
            return Optional.empty();

        return RecipeUtils.getAllRecipesFor(serverLevel, getRecipeForFurnaceModeUpgrade()).
                stream().filter(recipe -> !RECIPE_BLACKLIST.contains(recipe.id().location())).
                filter(recipe -> recipe.value().matches(new SingleRecipeInput(container.getItem(0)), level)).
                findFirst();
    }

    public RecipeType<? extends AbstractCookingRecipe> getRecipeForFurnaceModeUpgrade() {
        double value = upgradeModuleInventory.getUpgradeModuleModifierEffect(3, UpgradeModuleModifier.FURNACE_MODE);
        if(value == 1)
            return RecipeType.BLASTING;
        else if(value == 2)
            return RecipeType.SMOKING;

        return RecipeType.SMELTING;
    }

    @Override
    protected void updateUpgradeModules() {
        super.updateUpgradeModules();

        if(level != null && !level.isClientSide())
            ModMessages.sendToPlayersWithinXBlocks(
                    new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf((ServerLevel)level, getRecipeForFurnaceModeUpgrade())),
                    getBlockPos(), (ServerLevel)level, 32
            );
    }

    protected void syncIngredientListToPlayer(Player player) {
        if(!(level instanceof ServerLevel serverLevel))
            return;

        ModMessages.sendToPlayer(
                new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf(serverLevel, getRecipeForFurnaceModeUpgrade())),
                (ServerPlayer)player
        );
    }

    public List<Ingredient> getIngredientsOfRecipes() {
        return new ArrayList<>(ingredientsOfRecipes);
    }

    @Override
    public void setIngredients(int index, List<Ingredient> ingredients) {
        if(index == 0)
            this.ingredientsOfRecipes = ingredients;
    }
}