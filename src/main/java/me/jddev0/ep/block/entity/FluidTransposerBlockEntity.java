package me.jddev0.ep.block.entity;

import com.mojang.serialization.Codec;
import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.FluidTransposerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

public class FluidTransposerBlockEntity
        extends SimpleRecipeFluidMachineBlockEntity<SimpleFluidStorage, FluidTransposerRecipe>
        implements CheckboxUpdate {
    public static final long TANK_CAPACITY = FluidUtils.convertMilliBucketsToDroplets(
            1000 * ModConfigs.COMMON_FLUID_TRANSPOSER_TANK_CAPACITY.getValue());

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    private Mode mode = Mode.EMPTYING;

    public FluidTransposerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.FLUID_TRANSPOSER_ENTITY, blockPos, blockState,

                "fluid_transposer", FluidTransposerMenu::new,

                2, ModRecipes.FLUID_TRANSPOSER_TYPE, ModConfigs.COMMON_FLUID_TRANSPOSER_RECIPE_DURATION.getValue(),

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
        return new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(maxProgress, index - 2);
                    case 4, 5, 6, 7 -> ByteUtils.get2Bytes(energyConsumptionLeft, index - 4);
                    case 8 -> hasEnoughEnergy?1:0;
                    case 9 -> mode.ordinal();
                    case 10 -> redstoneMode.ordinal();
                    case 11 -> comparatorMode.ordinal();
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
                    case 9 -> mode = Mode.fromIndex(value);
                    case 10 -> redstoneMode = RedstoneMode.fromIndex(value);
                    case 11 -> comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int size() {
                return 12;
            }
        };
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
                    case 0 -> world == null || RecipeUtils.isIngredientOfAny(world, recipeType, stack);
                    case 1 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStack(slot);
                    if(world != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.canCombine(stack, itemStack))
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

                return world.getRecipeManager().listAllOfType(recipeType).stream().map(RecipeEntry::value).
                        map(FluidTransposerRecipe::getFluid).
                        anyMatch(fluidStack -> variant.isOf(fluidStack.getFluid()) &&
                                variant.nbtMatches(fluidStack.getFluidVariant().getNbt()));
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
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putInt("mode", mode.ordinal());
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        mode = Mode.fromIndex(nbt.getInt("mode"));
    }

    @Override
    protected Optional<RecipeEntry<FluidTransposerRecipe>> getRecipeFor(SimpleInventory inventory) {
        return world.getRecipeManager().listAllOfType(recipeType).
                stream().filter(recipe -> recipe.value().getMode() == mode).
                filter(recipe -> recipe.value().matches(inventory, world)).
                filter(recipe -> (mode == Mode.EMPTYING && fluidStorage.isEmpty()) ||
                        (recipe.value().getFluid().getFluidVariant().isOf(fluidStorage.getFluid().getFluid()) &&
                                recipe.value().getFluid().getFluidVariant().nbtMatches(fluidStorage.getFluid().getFluidVariant().getNbt()))).
                findFirst();
    }

    @Override
    protected void craftItem(RecipeEntry<FluidTransposerRecipe> recipe) {
        if(world == null || !hasRecipe())
            return;

        NbtCompound nbt = recipe.value().getFluid().getFluidVariant().getNbt();
        FluidStack fluid = new FluidStack(recipe.value().getFluid().getFluidVariant().getFluid(),
                nbt == null?null:nbt.copy(), recipe.value().getFluid().getDropletsAmount());

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
        itemHandler.setStack(1, recipe.value().getResult(world.getRegistryManager()).
                copyWithCount(itemHandler.getStack(1).getCount() +
                        recipe.value().getResult(world.getRegistryManager()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleInventory inventory, RecipeEntry<FluidTransposerRecipe> recipe) {
        long fluidAmountInTank = fluidStorage.getFluid().getDropletsAmount();
        long fluidAmountInRecipe = recipe.value().getFluid().getDropletsAmount();

        return world != null &&
                (mode == Mode.EMPTYING?fluidStorage.getCapacity() - fluidAmountInTank:fluidAmountInTank) >= fluidAmountInRecipe &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.value().getResult(world.getRegistryManager()));
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
                Codecs.idChecked(Mode::name, Mode::valueOf),
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