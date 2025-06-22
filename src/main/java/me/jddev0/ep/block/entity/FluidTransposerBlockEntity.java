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
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
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
    protected PropertyDelegate initContainerData() {
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
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public void markDirty() {
                super.markDirty();

                FluidTransposerBlockEntity.this.markDirty();
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> ((world instanceof ServerWorld serverWorld)?
                            RecipeUtils.isIngredientOfAny(serverWorld, recipeType, stack):
                            RecipeUtils.isIngredientOfAny(ingredientsOfRecipes, stack));
                    case 1 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.areItemsAndComponentsEqual(stack, itemStack))
                        resetProgress();
                }

                super.setStack(slot, stack);
            }
        };
    }

    @Override
    protected SimpleFluidStorage initFluidStorage() {
        return new SimpleFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                markDirty();
                syncFluidToPlayers(32);
            }

            private boolean isFluidValid(FluidVariant variant) {
                if(world == null)
                    return false;

                return !(world instanceof ServerWorld serverWorld) || //Always false on client side (Recipes are no longer synced)
                    RecipeUtils.getAllRecipesFor(serverWorld, recipeType).stream().map(RecipeEntry::value).
                        map(FluidTransposerRecipe::getFluid).
                        anyMatch(fluidStack -> variant.isOf(fluidStack.getFluid()) &&
                                variant.componentsMatch(fluidStack.getFluidVariant().getComponents()));
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
    protected void writeData(WriteView view) {
        super.writeData(view);

        view.putInt("mode", mode.ordinal());
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        mode = Mode.fromIndex(view.getInt("mode", 0));
    }

    @Override
    protected Optional<RecipeEntry<FluidTransposerRecipe>> getRecipeFor(SimpleInventory inventory) {
        if(!(world instanceof ServerWorld serverWorld))
            return Optional.empty();

        return RecipeUtils.getAllRecipesFor(serverWorld, recipeType).
                stream().filter(recipe -> recipe.value().getMode() == mode).
                filter(recipe -> recipe.value().matches(getRecipeInput(inventory), world)).
                filter(recipe -> (mode == Mode.EMPTYING && fluidStorage.isEmpty()) ||
                        (recipe.value().getFluid().getFluidVariant().isOf(fluidStorage.getFluid().getFluid()) &&
                                recipe.value().getFluid().getFluidVariant().componentsMatch(fluidStorage.getFluid().getFluidVariant().getComponents()))).
                findFirst();
    }

    @Override
    protected RecipeInput getRecipeInput(SimpleInventory inventory) {
        return new ContainerRecipeInputWrapper(inventory);
    }

    @Override
    protected void craftItem(RecipeEntry<FluidTransposerRecipe> recipe) {
        if(world == null || !hasRecipe())
            return;

        FluidStack fluid = new FluidStack(recipe.value().getFluid().getFluidVariant().getFluid(),
                recipe.value().getFluid().getFluidVariant().getComponents(), recipe.value().getFluid().getDropletsAmount());

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

        itemHandler.removeStack(0, 1);
        itemHandler.setStack(1, recipe.value().craft(null, world.getRegistryManager()).
                copyWithCount(itemHandler.getStack(1).getCount() +
                        recipe.value().craft(null, world.getRegistryManager()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<FluidTransposerRecipe> recipe) {
        long fluidAmountInTank = fluidStorage.getFluid().getDropletsAmount();
        long fluidAmountInRecipe = recipe.value().getFluid().getDropletsAmount();

        return world != null &&
                (mode == Mode.EMPTYING?fluidStorage.getCapacity() - fluidAmountInTank:fluidAmountInTank) >= fluidAmountInRecipe &&
                (mode != Mode.EMPTYING || fluidStorage.isEmpty() || (fluidStorage.getResource().isOf(recipe.value().getFluid().getFluid()) &&
                        fluidStorage.getResource().componentsMatch(recipe.value().getFluid().getFluidVariant().getComponents()))) &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().craft(null, world.getRegistryManager()));
    }

    public void setMode(boolean isFillingMode) {
        this.mode = isFillingMode?Mode.FILLING:Mode.EMPTYING;
        resetProgress();
        markDirty(world, getPos(), getCachedState());
    }

    @Override
    public void setCheckbox(int checkboxId, boolean checked) {
        switch(checkboxId) {
            //Mode
            case 0 -> setMode(checked);
        }
    }

    public enum Mode implements StringIdentifiable {
        EMPTYING, FILLING;

        public static final Codec<Mode> CODEC = Codecs.orCompressed(
                Codec.stringResolver(Mode::name, Mode::valueOf),
                Codecs.rawIdChecked(Mode::ordinal, i -> i >= 0 && i < Mode.values().length?Mode.values()[i]:null, -1)
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
        public String asString() {
            return name().toLowerCase(Locale.US);
        }
    }
}