package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ThermalGeneratorBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableFluidEnergyStorageBlockEntity;
import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ExtractOnlyEnergyStorage;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ThermalGeneratorRecipe;
import me.jddev0.ep.screen.ThermalGeneratorMenu;
import me.jddev0.ep.util.ByteUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ThermalGeneratorBlockEntity
        extends ConfigurableUpgradableFluidEnergyStorageBlockEntity<ExtractOnlyEnergyStorage, FluidTank>
        implements MenuProvider {
    public static final float ENERGY_PRODUCTION_MULTIPLIER = ModConfigs.COMMON_THERMAL_GENERATOR_ENERGY_PRODUCTION_MULTIPLIER.getValue();

    private final LazyOptional<IEnergyStorage> lazyEnergyStorage;

    private final LazyOptional<IFluidHandler> lazyFluidStorage;

    protected final ContainerData data;

    public ThermalGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                ModBlockEntities.THERMAL_GENERATOR_ENTITY.get(), blockPos, blockState,

                ModConfigs.COMMON_THERMAL_GENERATOR_CAPACITY.getValue(),
                ModConfigs.COMMON_THERMAL_GENERATOR_TRANSFER_RATE.getValue(),

                FluidStorageSingleTankMethods.INSTANCE,
                ModConfigs.COMMON_THERMAL_GENERATOR_FLUID_TANK_CAPACITY.getValue() * 1000,

                UpgradeModuleModifier.ENERGY_CAPACITY
        );

        data = new ContainerData() {
            @Override
            public int get(int index) {
                if(index == 2)
                    return redstoneMode.ordinal();
                else if(index == 3)
                    return comparatorMode.ordinal();

                if(level == null || index > 1)
                    return 0;

                List<RecipeHolder<ThermalGeneratorRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ThermalGeneratorRecipe.Type.INSTANCE);

                int rawProduction = 0;
                outer:
                for(RecipeHolder<ThermalGeneratorRecipe> recipe:recipes) {
                    for(Fluid fluid:recipe.value().getInput()) {
                        if(ThermalGeneratorBlockEntity.this.fluidStorage.getFluid().getFluid() == fluid) {
                            rawProduction = recipe.value().getEnergyProduction();
                            rawProduction = (int)(rawProduction * ENERGY_PRODUCTION_MULTIPLIER);

                            break outer;
                        }
                    }
                }

                //Calculate real production (raw production is in x FE per 1000 mB, use fluid amount without cap)
                int productionLeft = (int)(rawProduction * ThermalGeneratorBlockEntity.this.fluidStorage.getFluidAmount() / 1000.f);

                return switch(index) {
                    case 0, 1 -> ByteUtils.get2Bytes(productionLeft, index);
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0, 1 -> {}
                    case 2 -> ThermalGeneratorBlockEntity.this.redstoneMode = RedstoneMode.fromIndex(value);
                    case 3 -> ThermalGeneratorBlockEntity.this.comparatorMode = ComparatorMode.fromIndex(value);
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };

        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
        lazyFluidStorage = LazyOptional.of(() -> fluidStorage);
    }

    @Override
    protected ExtractOnlyEnergyStorage initEnergyStorage() {
        return new ExtractOnlyEnergyStorage(0, baseEnergyCapacity, baseEnergyTransferRate) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            public int getMaxExtract() {
                return Math.max(1, (int)Math.ceil(maxExtract * upgradeModuleInventory.getModifierEffectProduct(
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

                List<RecipeHolder<ThermalGeneratorRecipe>> recipes = level.getRecipeManager().
                        getAllRecipesFor(ThermalGeneratorRecipe.Type.INSTANCE);
                return recipes.stream().map(RecipeHolder::value).map(ThermalGeneratorRecipe::getInput).
                        anyMatch(inputs -> Arrays.stream(inputs).anyMatch(input -> stack.getFluid() == input));
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.ENERGY) {
            return lazyEnergyStorage.cast();
        }else if(cap == Capabilities.FLUID_HANDLER) {
            return lazyFluidStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.thermal_generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);

        return new ThermalGeneratorMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(blockEntity.redstoneMode.isActive(state.getValue(ThermalGeneratorBlock.POWERED)))
            tickRecipe(level, blockPos, state, blockEntity);

        transferEnergy(level, blockPos, state, blockEntity);
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        List<RecipeHolder<ThermalGeneratorRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ThermalGeneratorRecipe.Type.INSTANCE);

        int rawProduction = 0;
        outer:
        for(RecipeHolder<ThermalGeneratorRecipe> recipe:recipes) {
            for(Fluid fluid:recipe.value().getInput()) {
                if(blockEntity.fluidStorage.getFluid().getFluid() == fluid) {
                    rawProduction = recipe.value().getEnergyProduction();
                    rawProduction = (int)(rawProduction * ENERGY_PRODUCTION_MULTIPLIER);

                    break outer;
                }
            }
        }

        if(rawProduction > 0 && blockEntity.energyStorage.getEnergy() < blockEntity.energyStorage.getCapacity()) {
            //Calculate real production (raw production is in x FE per 1000 mB, 50 mB of fluid can be consumed per tick)
            int production = (int)(rawProduction * (Math.min(blockEntity.fluidStorage.getFluidAmount(), 50) / 1000.f));

            //Cap production
            production = Math.min(production, blockEntity.energyStorage.getCapacity() - blockEntity.energyStorage.getEnergy());

            int fluidAmount = (int)((float)production/rawProduction * 1000);

            //Re-calculate energy production (Prevents draining of not enough fluid)
            production = (int)(rawProduction * fluidAmount / 1000.f);

            blockEntity.fluidStorage.drain(fluidAmount, IFluidHandler.FluidAction.EXECUTE);

            blockEntity.energyStorage.setEnergy(Math.min(blockEntity.energyStorage.getCapacity(),
                    blockEntity.energyStorage.getEnergy() + production));
        }
    }

    private static void transferEnergy(Level level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        List<IEnergyStorage> consumerItems = new LinkedList<>();
        List<Integer> consumerEnergyValues = new LinkedList<>();
        int consumptionSum = 0;
        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.relative(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            LazyOptional<IEnergyStorage> energyStorageLazyOptional = testBlockEntity.getCapability(Capabilities.ENERGY, direction.getOpposite());
            if(!energyStorageLazyOptional.isPresent())
                continue;

            IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
            if(!energyStorage.canReceive())
                continue;

            int received = energyStorage.receiveEnergy(Math.min(blockEntity.energyStorage.getMaxExtract(),
                    blockEntity.energyStorage.getEnergy()), true);
            if(received <= 0)
                continue;

            consumptionSum += received;
            consumerItems.add(energyStorage);
            consumerEnergyValues.add(received);
        }

        List<Integer> consumerEnergyDistributed = new LinkedList<>();
        for(int i = 0;i < consumerItems.size();i++)
            consumerEnergyDistributed.add(0);

        int consumptionLeft = Math.min(blockEntity.energyStorage.getMaxExtract(),
                Math.min(blockEntity.energyStorage.getEnergy(), consumptionSum));
        blockEntity.energyStorage.extractEnergy(consumptionLeft, false);

        int divisor = consumerItems.size();
        outer:
        while(consumptionLeft > 0) {
            int consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < consumerEnergyValues.size();i++) {
                int consumptionDistributed = consumerEnergyDistributed.get(i);
                int consumptionOfConsumerLeft = consumerEnergyValues.get(i) - consumptionDistributed;

                int consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                consumerEnergyDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < consumerItems.size();i++) {
            int energy = consumerEnergyDistributed.get(i);
            if(energy > 0)
                consumerItems.get(i).receiveEnergy(energy, false);
        }
    }
}