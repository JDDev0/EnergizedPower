package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ChargerBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableInventoryEnergyStorageBlockEntity;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncIngredientsS2CPacket;
import me.jddev0.ep.recipe.ChargerRecipe;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.recipe.IngredientPacketUpdate;
import me.jddev0.ep.screen.ChargerMenu;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChargerBlockEntity
        extends ConfigurableUpgradableInventoryEnergyStorageBlockEntity<ReceiveOnlyEnergyStorage, ItemStackHandler>
        implements IngredientPacketUpdate {
    public static final float CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER = ModConfigs.COMMON_CHARGER_CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER.getValue();

    protected List<Ingredient> ingredientsOfRecipes = new ArrayList<>();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> true, i -> {
        if(i != 0)
            return false;

        ItemStack stack = itemHandler.getStackInSlot(i);
        if(level instanceof ServerLevel serverLevel && RecipeUtils.isResultOfAny(serverLevel, ChargerRecipe.Type.INSTANCE, stack))
            return true;

        if(!(level instanceof ServerLevel serverLevel) || RecipeUtils.isIngredientOfAny(serverLevel, ChargerRecipe.Type.INSTANCE, stack))
            return false;

        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyStorage == null || !energyStorage.canReceive())
            return true;

        return energyStorage.receiveEnergy(ChargerBlockEntity.this.energyStorage.getMaxReceive(), true) == 0;
    });

    private int energyConsumptionLeft = -1;

    public ChargerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.CHARGER_ENTITY.get(), blockPos, blockState,

                "charger",

                ModConfigs.COMMON_CHARGER_CAPACITY.getValue(),
                ModConfigs.COMMON_CHARGER_TRANSFER_RATE.getValue(),

                1,

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
                if(slot == 0) {
                    if(((level instanceof ServerLevel serverLevel)?
                            RecipeUtils.isIngredientOfAny(serverLevel, EPRecipes.CHARGER_TYPE.get(), stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack)))
                        return true;

                    IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                    return energyStorage != null && energyStorage.canReceive();
                }

                return super.isItemValid(slot, stack);
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() && (!ItemStack.isSameItem(stack, itemStack) ||
                            (!ItemStack.isSameItemSameComponents(stack, itemStack) &&
                                    //Only check if NBT data is equal if one of stack or itemStack is no energy item
                                    !(stack.getCapability(Capabilities.EnergyStorage.ITEM) != null && itemStack.getCapability(Capabilities.EnergyStorage.ITEM) != null))))
                        resetProgress();
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
        return new CombinedContainerData(
                new EnergyValueContainerData(() -> hasRecipe()?getEnergyConsumptionPerTick():-1, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncIngredientListToPlayer(player);

        return new ChargerMenu(id, inventory, this, upgradeModuleInventory, this.data);
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
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.put("recipe.energy_consumption_left", IntTag.valueOf(energyConsumptionLeft));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        energyConsumptionLeft = nbt.getIntOr("recipe.energy_consumption_left", 0);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ChargerBlockEntity blockEntity) {
        if(!(level instanceof ServerLevel serverLevel))
            return;

        if(!blockEntity.redstoneMode.isActive(state.getValue(ChargerBlock.POWERED)))
            return;

        if(blockEntity.hasRecipe()) {
            ItemStack stack = blockEntity.itemHandler.getStackInSlot(0);
            int energyConsumptionPerTick;

            SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
            for(int i = 0;i < blockEntity.itemHandler.getSlots();i++)
                inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));

            Optional<RecipeHolder<ChargerRecipe>> recipe = serverLevel.recipeAccess().
                    getRecipeFor(ChargerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);
            if(recipe.isPresent()) {
                if(blockEntity.energyConsumptionLeft == -1)
                    blockEntity.energyConsumptionLeft = (int)(recipe.get().value().getEnergyConsumption() * CHARGER_RECIPE_ENERGY_CONSUMPTION_MULTIPLIER);

                if(blockEntity.energyStorage.getEnergy() == 0) {
                    setChanged(level, blockPos, state);

                    return;
                }

                energyConsumptionPerTick = Math.min(blockEntity.energyConsumptionLeft, Math.min(blockEntity.energyStorage.getMaxReceive(),
                        blockEntity.energyStorage.getEnergy()));
            }else {
                IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
                if(energyStorage == null || !energyStorage.canReceive())
                    return;

                blockEntity.energyConsumptionLeft = Math.max(0, energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());

                if(blockEntity.energyStorage.getEnergy() == 0) {
                    setChanged(level, blockPos, state);

                    return;
                }

                energyConsumptionPerTick = energyStorage.receiveEnergy(Math.min(blockEntity.energyStorage.getMaxReceive(),
                        blockEntity.energyStorage.getEnergy()), false);
            }

            if(blockEntity.energyConsumptionLeft < 0 || energyConsumptionPerTick < 0) {
                //Reset progress for invalid values

                blockEntity.resetProgress();
                setChanged(level, blockPos, state);

                return;
            }

            blockEntity.energyStorage.setEnergy(blockEntity.energyStorage.getEnergy() - energyConsumptionPerTick);
            blockEntity.energyConsumptionLeft -= energyConsumptionPerTick;

            if(blockEntity.energyConsumptionLeft <= 0) {
                recipe.ifPresent(chargerRecipe ->
                        blockEntity.itemHandler.setStackInSlot(0, chargerRecipe.value().assemble(null, level.registryAccess()).copyWithCount(1)));

                blockEntity.resetProgress();
            }
            setChanged(level, blockPos, state);
        }else {
            blockEntity.resetProgress();
            setChanged(level, blockPos, state);
        }
    }
    
    protected final int getEnergyConsumptionPerTick() {
        if(!(level instanceof ServerLevel serverLevel))
            return -1;

        ItemStack stack = itemHandler.getStackInSlot(0);

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<ChargerRecipe>> recipe = serverLevel.recipeAccess().
                getRecipeFor(ChargerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);
        if(recipe.isPresent()) {
            return Math.min(energyConsumptionLeft, Math.min(energyStorage.getMaxReceive(), energyStorage.getEnergy()));
        }else {
            IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
            if(energyStorage == null || !energyStorage.canReceive())
                return -1;

            return energyStorage.receiveEnergy(Math.min(this.energyStorage.getMaxReceive(), this.energyStorage.getEnergy()), true);
        }
    }

    private void resetProgress() {
        energyConsumptionLeft = -1;
    }

    private boolean hasRecipe() {
        if(!(level instanceof ServerLevel serverLevel))
            return false;

        ItemStack stack = itemHandler.getStackInSlot(0);
        if(stack.getCapability(Capabilities.EnergyStorage.ITEM) != null)
            return true;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Optional<RecipeHolder<ChargerRecipe>> recipe = serverLevel.recipeAccess().
                getRecipeFor(ChargerRecipe.Type.INSTANCE, new ContainerRecipeInputWrapper(inventory), level);

        return recipe.isPresent();
    }

    @Override
    protected void updateUpgradeModules() {
        resetProgress();

        super.updateUpgradeModules();
    }

    protected void syncIngredientListToPlayer(Player player) {
        if(!(level instanceof ServerLevel serverLevel))
            return;

        ModMessages.sendToPlayer(new SyncIngredientsS2CPacket(getBlockPos(), 0, RecipeUtils.getIngredientsOf(serverLevel, EPRecipes.CHARGER_TYPE.get())), (ServerPlayer)player);
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