package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.SimpleRecipeFluidMachineBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.FluidTransposerRecipe;
import me.jddev0.ep.recipe.ModRecipes;
import me.jddev0.ep.screen.FluidTransposerMenu;
import me.jddev0.ep.util.ByteUtils;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

public class FluidTransposerBlockEntity
        extends SimpleRecipeFluidMachineBlockEntity<FluidTank, FluidTransposerRecipe>
        implements CheckboxUpdate {
    public static final int TANK_CAPACITY = 1000 * ModConfigs.COMMON_FLUID_TRANSPOSER_TANK_CAPACITY.getValue();

    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1));

    private Mode mode = Mode.EMPTYING;

    public FluidTransposerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.FLUID_TRANSPOSER_ENTITY.get(), blockPos, blockState,

                "fluid_transposer", FluidTransposerMenu::new,

                2, ModRecipes.FLUID_TRANSPOSER_TYPE.get(), ModConfigs.COMMON_FLUID_TRANSPOSER_RECIPE_DURATION.getValue(),

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
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(progress, index);
                    case 2, 3 -> ByteUtils.get2Bytes(maxProgress, index - 2);
                    case 4, 5 -> ByteUtils.get2Bytes(energyConsumptionLeft, index - 4);
                    case 6 -> hasEnoughEnergy?1:0;
                    case 7 -> mode.ordinal();
                    case 8 -> redstoneMode.ordinal();
                    case 9 -> comparatorMode.ordinal();
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
                    case 4, 5, 6 -> {}
                    case 7 -> mode = Mode.fromIndex(value);
                    case 8 -> redstoneMode = RedstoneMode.fromIndex(value);
                    case 9 -> comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int getCount() {
                return 10;
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
                return switch(slot) {
                    case 0 -> level == null || RecipeUtils.isIngredientOfAny(level, recipeType, stack);
                    case 1 -> false;
                    default -> super.isItemValid(slot, stack);
                };
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                if(slot == 0) {
                    ItemStack itemStack = getStackInSlot(slot);
                    if(level != null && !stack.isEmpty() && !itemStack.isEmpty() &&
                            !ItemStack.isSameItemSameTags(stack, itemStack))
                        resetProgress();
                }

                super.setStackInSlot(slot, stack);
            }
        };
    }

    @Override
    protected FluidTank initFluidStorage() {
        return new FluidTank(baseTankCapacity) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                syncFluidToPlayers(32);
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                if(!super.isFluidValid(stack) || level == null)
                    return false;

                return level.getRecipeManager().getAllRecipesFor(recipeType).stream().
                        map(FluidTransposerRecipe::getFluid).
                        anyMatch(fluidStack -> stack.isFluidEqual(fluidStack));
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }else if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidStorage.cast();
        }else if(cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.putInt("mode", mode.ordinal());
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        mode = Mode.fromIndex(nbt.getInt("mode"));
    }

    @Override
    protected Optional<FluidTransposerRecipe> getRecipeFor(Container inventory) {
        return level.getRecipeManager().getAllRecipesFor(recipeType).
                stream().filter(recipe -> recipe.getMode() == mode).
                filter(recipe -> recipe.matches(inventory, level)).
                filter(recipe -> (mode == Mode.EMPTYING && fluidStorage.isEmpty()) ||
                        recipe.getFluid().isFluidEqual(fluidStorage.getFluid())).
                findFirst();
    }

    @Override
    protected void craftItem(FluidTransposerRecipe recipe) {
        if(level == null || !hasRecipe())
            return;

        FluidStack fluid = recipe.getFluid().copy();

        if(mode == Mode.EMPTYING)
            fluidStorage.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
        else
            fluidStorage.drain(fluid, IFluidHandler.FluidAction.EXECUTE);

        itemHandler.extractItem(0, 1, false);
        itemHandler.setStackInSlot(1, recipe.getResultItem(level.registryAccess()).
                copyWithCount(itemHandler.getStackInSlot(1).getCount() +
                        recipe.getResultItem(level.registryAccess()).getCount()));

        resetProgress();
    }

    @Override
    protected boolean canCraftRecipe(SimpleContainer inventory, FluidTransposerRecipe recipe) {
        int fluidAmountInTank = fluidStorage.getFluid().getAmount();
        int fluidAmountInRecipe = recipe.getFluid().getAmount();

        return level != null &&
                (mode == Mode.EMPTYING?fluidStorage.getCapacity() - fluidAmountInTank:fluidAmountInTank) >= fluidAmountInRecipe &&
                InventoryUtils.canInsertItemIntoSlot(inventory, 1, recipe.getResultItem(level.registryAccess()));
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