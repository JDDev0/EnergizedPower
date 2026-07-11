package me.jddev0.ep.block.entity;

import com.mojang.serialization.Codec;
import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.FluidIngredientWithAmount;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.FluidTransposerMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

public class FluidTransposerBlockEntity
        extends SimpleRecipeFluidMachineBlockEntity<RecipeInput, FluidTransposerRecipe>
        implements CheckboxUpdate {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_FLUID_TRANSPOSER_TANK_CAPACITY.getValue();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    private Mode mode = Mode.EMPTYING;

    public FluidTransposerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.FLUID_TRANSPOSER_ENTITY.get(), blockPos, blockState,

                "fluid_transposer", FluidTransposerMenu::new,

                2, EPRecipes.FLUID_TRANSPOSER_TYPE.get(), ModConfigs.COMMON_FLUID_TRANSPOSER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_FLUID_TRANSPOSER_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_TRANSPOSER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_FLUID_TRANSPOSER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ITEM_EJECTOR,
                UpgradeModuleModifier.ITEM_PULLING
        );
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
                new ShortValueContainerData(() -> (short)mode.ordinal(), value -> mode = Mode.fromIndex(value)),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0 -> level == null || RecipeUtils.isIngredientOfAny(level, recipeType, stack);
                    case 1 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                if(slot == 0) {
                    ItemStack stack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !previousItemStack.isEmpty() &&
                            !ItemStack.isSameItemSameComponents(stack, previousItemStack))
                        resetProgress(0);
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

                return level.getRecipeManager().getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).
                        map(FluidTransposerRecipe::getFluid).
                        anyMatch(fluid -> fluid.map(
                                fluidStack -> FluidStack.isSameFluidSameComponents(stack, fluidStack), f -> f.fluid().test(stack)));
            }
        };
    }

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.putInt("mode", mode.ordinal());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        mode = Mode.fromIndex(nbt.getInt("mode"));
    }

    @Override
    protected Optional<RecipeHolder<FluidTransposerRecipe>> getRecipeFor(Container inventory) {
        return level.getRecipeManager().getAllRecipesFor(recipeType).
                stream().filter(recipe -> recipe.value().getMode() == mode).
                filter(recipe -> recipe.value().matches(getRecipeInput(inventory), level)).
                filter(recipe -> (mode == Mode.EMPTYING && fluidStorage.getFluid(0).isEmpty()) ||
                        recipe.value().getFluid().map(fluid -> FluidStack.isSameFluidSameComponents(fluidStorage.getFluid(0), fluid),
                                fluid -> fluid.test(fluidStorage.getFluid(0)))).
                findFirst();
    }

    @Override
    protected RecipeInput getRecipeInput(Container inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(int thread, RecipeHolder<FluidTransposerRecipe> recipe) {
        if(level == null || !hasRecipe(thread))
            return;

        if(mode == Mode.EMPTYING) {
            //fluid of recipe is always a FluidStack in EMPTYING mode
            FluidStack fluid = recipe.value().getFluid().left().get().copyWithAmount(recipe.value().getFluid().left().get().getAmount());

            fluidStorage.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
        }else {
            int amount = recipe.value().getFluid().map(FluidStack::getAmount,
                    FluidIngredientWithAmount::amount);

            //Fluid in tank must be valid at this point
            fluidStorage.drain(amount, IFluidHandler.FluidAction.EXECUTE);
        }

        itemHandler.extractItem(0, 1, false);
        itemHandler.setStackInSlot(1, recipe.value().getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(1).getCount() +
                        recipe.value().getResultItem(level.registryAccess()).getCount()));

        resetProgress(thread);
    }

    @Override
    protected boolean canCraftRecipe(int thread, SimpleContainer inventory, RecipeHolder<FluidTransposerRecipe> recipe) {
        int fluidAmountInTank = fluidStorage.getFluid(0).getAmount();
        int fluidAmountInRecipe = recipe.value().getFluid().map(FluidStack::getAmount,
                FluidIngredientWithAmount::amount);

        return level != null &&
                (mode == Mode.EMPTYING?fluidStorage.getTankCapacity(0) - fluidAmountInTank:fluidAmountInTank) >= fluidAmountInRecipe &&
                (mode != Mode.EMPTYING || fluidStorage.getFluid(0).isEmpty() ||
                        recipe.value().getFluid().map(fluid -> FluidStack.isSameFluidSameComponents(fluidStorage.getFluid(0), fluid),
                                fluid -> fluid.test(fluidStorage.getFluid(0)))) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().getResultItem(level.registryAccess()));
    }

    public void setMode(boolean isFillingMode) {
        this.mode = isFillingMode?Mode.FILLING:Mode.EMPTYING;
        resetProgress(0);
        setChanged(level, getBlockPos(), getBlockState());
    }

    @Override
    public void setCheckbox(int checkboxId, boolean checked) {
        switch(checkboxId) {
            //Mode
            case 0 -> setMode(checked);
        }
    }

    public enum Mode implements StringRepresentable {
        EMPTYING, FILLING;

        public static final Codec<Mode> CODEC = ExtraCodecs.orCompressed(
                Codec.stringResolver(Mode::name, Mode::valueOf),
                ExtraCodecs.idResolverCodec(Mode::ordinal, i -> i >= 0 && i < Mode.values().length?Mode.values()[i]:null, -1)
        );

        /**
         * @return Returns the enum value at index if index is valid otherwise EMPTYING will be returned
         */
        public static @NotNull Mode fromIndex(int index) {
            Mode[] values = values();

            if(index < 0 || index >= values.length)
                return EMPTYING;

            return values[index];
        }

        @Override
        @NotNull
        public String getSerializedName() {
            return name().toLowerCase(Locale.US);
        }
    }
}