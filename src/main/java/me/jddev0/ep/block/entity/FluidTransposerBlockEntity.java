package me.jddev0.ep.block.entity;

import com.mojang.serialization.Codec;
import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import me.jddev0.ep.recipe.EPRecipes;
import me.jddev0.ep.screen.FluidTransposerMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

public class FluidTransposerBlockEntity
        extends SimpleRecipeFluidMachineBlockEntity<SimpleFluidStorage, RecipeInput, FluidTransposerRecipe>
        implements CheckboxUpdate {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(
            1000 * ModConfigs.COMMON_FLUID_TRANSPOSER_TANK_CAPACITY.getValue());

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    private Mode mode = Mode.EMPTYING;

    public FluidTransposerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.FLUID_TRANSPOSER_ENTITY, blockPos, blockState,

                "fluid_transposer", FluidTransposerMenu::new,

                2, EPRecipes.FLUID_TRANSPOSER_TYPE, ModConfigs.COMMON_FLUID_TRANSPOSER_RECIPE_DURATION.getValue(),

                ModConfigs.COMMON_FLUID_TRANSPOSER_CAPACITY.getValue(),
                ModConfigs.COMMON_FLUID_TRANSPOSER_TRANSFER_RATE.getValue(),
                ModConfigs.COMMON_FLUID_TRANSPOSER_ENERGY_CONSUMPTION_PER_TICK.getValue(),

                FluidStorageSingleTankMethods.INSTANCE,
                TANK_CAPACITY,

                UpgradeModuleModifier.SPEED,
                UpgradeModuleModifier.ENERGY_CONSUMPTION,
                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new ProgressValueContainerData(() -> progress, value -> progress = value),
                new ProgressValueContainerData(() -> maxProgress, value -> maxProgress = value),
                new EnergyValueContainerData(() -> hasWork()?getCurrentWorkData().map(this::getEnergyConsumptionFor).orElse(-1L):-1, value -> {}),
                new EnergyValueContainerData(() -> energyConsumptionLeft, value -> {}),
                new BooleanValueContainerData(() -> hasEnoughEnergy, value -> {}),
                new ShortValueContainerData(() -> (short)mode.ordinal(), value -> mode = Mode.fromIndex(value)),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Override
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public void setChanged() {
                super.setChanged();

                FluidTransposerBlockEntity.this.setChanged();
            }

            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> ((level instanceof ServerLevel serverWorld)?
                            RecipeUtils.isIngredientOfAny(serverWorld, recipeType, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
                    case 1 -> false;
                    default -> super.canPlaceItem(slot, stack);
                };
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getItem(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.isSameItemSameComponents(stack, itemStack))
                        resetProgress();
                }

                super.setItem(slot, stack);
            }
        };
    }

    @Override
    protected SimpleFluidStorage initFluidStorage() {
        return new SimpleFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncFluidToPlayers(32);
            }

            private boolean isFluidValid(FluidVariant variant) {
                if(level == null)
                    return false;

                return !(level instanceof ServerLevel serverWorld) || //Always false on client side (Recipes are no longer synced)
                    RecipeUtils.getAllRecipesFor(serverWorld, recipeType).stream().map(RecipeHolder::value).
                        map(FluidTransposerRecipe::getFluid).
                        anyMatch(fluidStack -> variant.isOf(fluidStack.getFluid()) &&
                                variant.componentsMatch(fluidStack.getFluidVariant().getComponentsPatch()));
            }

            @Override
            protected boolean canInsert(FluidVariant variant) {
                return isFluidValid(variant);
            }

            @Override
            protected boolean canExtract(FluidVariant variant) {
                return isFluidValid(variant);
            }
        };
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("mode", mode.ordinal());
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        mode = Mode.fromIndex(view.getIntOr("mode", 0));
    }

    @Override
    protected Optional<RecipeHolder<FluidTransposerRecipe>> getRecipeFor(SimpleContainer inventory) {
        if(!(level instanceof ServerLevel serverWorld))
            return Optional.empty();

        return RecipeUtils.getAllRecipesFor(serverWorld, recipeType).
                stream().filter(recipe -> recipe.value().getMode() == mode).
                filter(recipe -> recipe.value().matches(getRecipeInput(inventory), level)).
                filter(recipe -> (mode == Mode.EMPTYING && fluidStorage.isEmpty()) ||
                        (recipe.value().getFluid().getFluidVariant().isOf(fluidStorage.getFluid().getFluid()) &&
                                recipe.value().getFluid().getFluidVariant().componentsMatch(fluidStorage.getFluid().getFluidVariant().getComponentsPatch()))).
                findFirst();
    }

    @Override
    protected RecipeInput getRecipeInput(SimpleContainer inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeHolder<FluidTransposerRecipe> recipe) {
        if(level == null || !hasRecipe())
            return;

        FluidStack fluid = new FluidStack(recipe.value().getFluid().getFluidVariant().getFluid(),
                recipe.value().getFluid().getFluidVariant().getComponentsPatch(), recipe.value().getFluid().getDropletsAmount());

        if(mode == Mode.EMPTYING) {
            try(Transaction transaction = Transaction.openOuter()) {
                fluidStorage.insert(fluid.getFluidVariant(), fluid.getDropletsAmount(), transaction);

                transaction.commit();
            }
        }else {
            try(Transaction transaction = Transaction.openOuter()) {
                fluidStorage.extract(fluid.getFluidVariant(), fluid.getDropletsAmount(), transaction);

                transaction.commit();
            }
        }

        itemHandler.removeItem(0, 1);
        itemHandler.setItem(1, recipe.value().assemble(null).
                copyWithCount(itemHandler.getItem(1).getCount() +
                        recipe.value().assemble(null).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, RecipeHolder<FluidTransposerRecipe> recipe) {
        long fluidAmountInTank = fluidStorage.getFluid().getDropletsAmount();
        long fluidAmountInRecipe = recipe.value().getFluid().getDropletsAmount();

        return level != null &&
                (mode == Mode.EMPTYING?fluidStorage.getCapacity() - fluidAmountInTank:fluidAmountInTank) >= fluidAmountInRecipe &&
                (mode != Mode.EMPTYING || fluidStorage.isEmpty() || (fluidStorage.getResource().isOf(recipe.value().getFluid().getFluid()) &&
                        fluidStorage.getResource().componentsMatch(recipe.value().getFluid().getFluidVariant().getComponentsPatch()))) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().assemble(null));
    }

    public void setMode(boolean isFillingMode) {
        this.mode = isFillingMode?Mode.FILLING:Mode.EMPTYING;
        resetProgress();
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