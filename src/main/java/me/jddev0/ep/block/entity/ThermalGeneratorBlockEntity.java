package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ThermalGeneratorBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableFluidEnergyStorageBlockEntity;
import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.ComparatorModeValueContainerData;
import me.jddev0.ep.inventory.data.EnergyValueContainerData;
import me.jddev0.ep.inventory.data.RedstoneModeValueContainerData;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ThermalGeneratorRecipe;
import me.jddev0.ep.screen.ThermalGeneratorMenu;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

import java.util.*;

public class ThermalGeneratorBlockEntity
        extends ConfigurableUpgradableFluidEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, SimpleFluidStorage> {
    public static final double ENERGY_PRODUCTION_MULTIPLIER = ModConfigs.COMMON_THERMAL_GENERATOR_ENERGY_PRODUCTION_MULTIPLIER.getValue();

    public ThermalGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.THERMAL_GENERATOR_ENTITY, blockPos, blockState,

                "thermal_generator",

                ModConfigs.COMMON_THERMAL_GENERATOR_CAPACITY.getValue(),
                ModConfigs.COMMON_THERMAL_GENERATOR_TRANSFER_RATE.getValue(),

                FluidStorageSingleTankMethods.INSTANCE,
                FluidUtils.convertMilliBucketsToDroplets(ModConfigs.COMMON_THERMAL_GENERATOR_FLUID_TANK_CAPACITY.getValue() * 1000),

                UpgradeModuleModifier.ENERGY_CAPACITY
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity, baseEnergyCapacity, baseEnergyCapacity) {
            @Override
            public long getCapacity() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                markDirty();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, 0, baseEnergyTransferRate) {
            @Override
            public long getMaxExtract() {
                return Math.max(1, (long)Math.ceil(maxExtract * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
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
                if(!(world instanceof ServerWorld serverWorld))
                    return false;

                Collection<RecipeEntry<ThermalGeneratorRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverWorld, ThermalGeneratorRecipe.Type.INSTANCE);

                return recipes.stream().map(RecipeEntry::value).map(ThermalGeneratorRecipe::getInput).
                        anyMatch(inputs -> Arrays.stream(inputs).anyMatch(input -> variant.getFluid() == input));
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
    protected PropertyDelegate initContainerData() {
        return new CombinedContainerData(
                new EnergyValueContainerData(() -> {
                    if(!(world instanceof ServerWorld serverWorld))
                        return 0L;

                    Collection<RecipeEntry<ThermalGeneratorRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverWorld, ThermalGeneratorRecipe.Type.INSTANCE);

                    long rawProduction = 0;
                    outer:
                    for(RecipeEntry<ThermalGeneratorRecipe> recipe:recipes) {
                        for(Fluid fluid:recipe.value().getInput()) {
                            if(fluidStorage.getFluid().getFluid() == fluid) {
                                rawProduction = recipe.value().getEnergyProduction();
                                rawProduction = (long)(rawProduction * ENERGY_PRODUCTION_MULTIPLIER);

                                break outer;
                            }
                        }
                    }

                    //Calculate real production (raw production is in x FE per 1000 mB, 50 mB of fluid can be consumed per tick)
                    long production = (long)(rawProduction * (Math.min(fluidStorage.getFluid().getMilliBucketsAmount(), 50) / 1000.f));

                    //Cap production
                    production = Math.max(0, Math.min(production, energyStorage.getCapacity() - energyStorage.getAmount()));

                    long fluidAmount = (long)((float)production/rawProduction * 1000);

                    //Re-calculate energy production (Prevents draining of not enough fluid)
                    return (long)(rawProduction * fluidAmount / 1000.f);
                }, value -> {}),
                new EnergyValueContainerData(() -> {
                    if(!(world instanceof ServerWorld serverWorld))
                        return 0L;

                    Collection<RecipeEntry<ThermalGeneratorRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverWorld, ThermalGeneratorRecipe.Type.INSTANCE);

                    long rawProduction = 0;
                    outer:
                    for(RecipeEntry<ThermalGeneratorRecipe> recipe:recipes) {
                        for(Fluid fluid:recipe.value().getInput()) {
                            if(ThermalGeneratorBlockEntity.this.fluidStorage.getFluid().getFluid() == fluid) {
                                rawProduction = recipe.value().getEnergyProduction();
                                rawProduction = (long)(rawProduction * ENERGY_PRODUCTION_MULTIPLIER);

                                break outer;
                            }
                        }
                    }

                    //Calculate real production (raw production is in x E per 1000 mB, use fluid amount without cap)
                    return (long)(rawProduction * fluidStorage.getFluid().getMilliBucketsAmount() / 1000.);
                }, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);

        return new ThermalGeneratorMenu(id, this, inventory, upgradeModuleInventory, this.data);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClient())
            return;

        if(blockEntity.redstoneMode.isActive(state.get(ThermalGeneratorBlock.POWERED)))
            tickRecipe(level, blockPos, state, blockEntity);

        transferEnergy(level, blockPos, state, blockEntity);
    }

    private static void tickRecipe(World level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClient() || !(level instanceof ServerWorld serverWorld))
            return;

        Collection<RecipeEntry<ThermalGeneratorRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverWorld, ThermalGeneratorRecipe.Type.INSTANCE);

        long rawProduction = 0;
        outer:
        for(RecipeEntry<ThermalGeneratorRecipe> recipe:recipes) {
            for(Fluid fluid:recipe.value().getInput()) {
                if(blockEntity.fluidStorage.getFluid().getFluid() == fluid) {
                    rawProduction = recipe.value().getEnergyProduction();
                    rawProduction = (long)(rawProduction * ENERGY_PRODUCTION_MULTIPLIER);

                    break outer;
                }
            }
        }

        if(rawProduction > 0 && blockEntity.energyStorage.getAmount() < blockEntity.energyStorage.getCapacity()) {
            //Calculate real production (raw production is in x E per 1000 mB, 50 mB of fluid can be consumed per tick)
            long production = (long)(rawProduction * (Math.min(
                    blockEntity.fluidStorage.getFluid().getMilliBucketsAmount(), 50) / 1000.));

            //Cap production
            production = Math.max(0, Math.min(production, blockEntity.energyStorage.getCapacity() - blockEntity.energyStorage.getAmount()));

            long fluidAmount = (long)((double)production/rawProduction * 1000);

            //Re-calculate energy production (Prevents draining of not enough fluid)
            production = (long)(rawProduction * fluidAmount / 1000.);

            try(Transaction transaction = Transaction.openOuter()) {
                blockEntity.fluidStorage.extract(blockEntity.fluidStorage.variant,
                        FluidUtils.convertMilliBucketsToDroplets(fluidAmount), transaction);

                blockEntity.energyStorage.insert(production, transaction);
                transaction.commit();
            }
        }
    }

    private static void transferEnergy(World level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClient())
            return;

        List<EnergyStorage> consumerItems = new ArrayList<>();
        List<Long> consumerEnergyValues = new ArrayList<>();
        long consumptionSum = 0;
        for(Direction direction:Direction.values()) {
            BlockPos testPos = blockPos.offset(direction);

            BlockEntity testBlockEntity = level.getBlockEntity(testPos);
            if(testBlockEntity == null)
                continue;

            EnergyStorage limitingEnergyStorage = EnergyStorage.SIDED.find(level, testPos, direction.getOpposite());
            if(limitingEnergyStorage == null)
                continue;

            if(!limitingEnergyStorage.supportsInsertion())
                continue;

            try(Transaction transaction = Transaction.openOuter()) {
                long received = limitingEnergyStorage.insert(Math.min(blockEntity.limitingEnergyStorage.getMaxExtract(),
                        blockEntity.energyStorage.getAmount()), transaction);

                if(received <= 0)
                    continue;

                consumptionSum += received;
                consumerItems.add(limitingEnergyStorage);
                consumerEnergyValues.add(received);
            }
        }

        List<Long> consumerEnergyDistributed = new ArrayList<>();
        for(int i = 0;i < consumerItems.size();i++)
            consumerEnergyDistributed.add(0L);

        long consumptionLeft = Math.min(blockEntity.limitingEnergyStorage.getMaxExtract(),
                Math.min(blockEntity.energyStorage.getAmount(), consumptionSum));
        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.energyStorage.extract(consumptionLeft, transaction);
            transaction.commit();
        }

        int divisor = consumerItems.size();
        outer:
        while(consumptionLeft > 0) {
            long consumptionPerConsumer = consumptionLeft / divisor;
            if(consumptionPerConsumer == 0) {
                divisor = Math.max(1, divisor - 1);
                consumptionPerConsumer = consumptionLeft / divisor;
            }

            for(int i = 0;i < consumerEnergyValues.size();i++) {
                long consumptionDistributed = consumerEnergyDistributed.get(i);
                long consumptionOfConsumerLeft = consumerEnergyValues.get(i) - consumptionDistributed;

                long consumptionDistributedNew = Math.min(consumptionOfConsumerLeft, Math.min(consumptionPerConsumer, consumptionLeft));
                consumerEnergyDistributed.set(i, consumptionDistributed + consumptionDistributedNew);
                consumptionLeft -= consumptionDistributedNew;
                if(consumptionLeft == 0)
                    break outer;
            }
        }

        for(int i = 0;i < consumerItems.size();i++) {
            long energy = consumerEnergyDistributed.get(i);
            if(energy > 0) {
                try(Transaction transaction = Transaction.openOuter()) {
                    consumerItems.get(i).insert(energy, transaction);
                    transaction.commit();
                }
            }
        }
    }
}