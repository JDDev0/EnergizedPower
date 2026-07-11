package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ThermalGeneratorBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableFluidEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ThermalGeneratorRecipe;
import me.jddev0.ep.screen.ThermalGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ThermalGeneratorBlockEntity
        extends ConfigurableUpgradableFluidEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, EnergizedPowerFluidStorage> {
    public static final float ENERGY_PRODUCTION_MULTIPLIER = ModConfigs.COMMON_THERMAL_GENERATOR_ENERGY_PRODUCTION_MULTIPLIER.getValue();

    public ThermalGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.THERMAL_GENERATOR_ENTITY.get(), blockPos, blockState,

                "thermal_generator",

                ModConfigs.COMMON_THERMAL_GENERATOR_CAPACITY.getValue(),
                ModConfigs.COMMON_THERMAL_GENERATOR_TRANSFER_RATE.getValue(),

                ModConfigs.COMMON_THERMAL_GENERATOR_FLUID_TANK_CAPACITY.getValue() * 1000,

                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.ENERGY_PRODUCTION
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity) {
            @Override
            public int getCapacity() {
                return Math.max(1, (int)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_CAPACITY)));
            }

            @Override
            protected void onFinalCommit() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, 0, baseEnergyTransferRate) {
            @Override
            public int getMaxExtract() {
                return Math.max(1, (int)Math.ceil(maxExtract * upgradeModuleInventory.getModifierEffectProduct(
                        UpgradeModuleModifier.ENERGY_TRANSFER_RATE)));
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

                List<RecipeHolder<ThermalGeneratorRecipe>> recipes = level.getRecipeManager().
                        getAllRecipesFor(ThermalGeneratorRecipe.Type.INSTANCE);

                return recipes.stream().map(RecipeHolder::value).map(ThermalGeneratorRecipe::getInput).
                        anyMatch(input -> input.test(stack));
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new EnergyValueContainerData(() -> {
                    if(level == null)
                        return 0;

                    List<RecipeHolder<ThermalGeneratorRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ThermalGeneratorRecipe.Type.INSTANCE);

                    int rawProduction = 0;
                    outer:
                    for(RecipeHolder<ThermalGeneratorRecipe> recipe:recipes) {
                        if(recipe.value().getInput().test(ThermalGeneratorBlockEntity.this.fluidStorage.getFluid(0))) {
                            rawProduction = recipe.value().getEnergyProduction();
                            rawProduction = (int)(rawProduction * ENERGY_PRODUCTION_MULTIPLIER *
                                    upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_PRODUCTION));

                            break outer;
                        }
                    }

                    //Calculate real production (raw production is in x FE per 1000 mB, 50 mB of fluid can be consumed per tick)
                    int production = (int)(rawProduction * (Math.min(fluidStorage.getAmount(0), 50) / 1000.f));

                    //Cap production
                    production = Math.max(0, Math.min(production, energyStorage.getCapacity() - energyStorage.getEnergy()));

                    int fluidAmount = (int)((float)production/rawProduction * 1000);

                    //Re-calculate energy production (Prevents draining of not enough fluid)
                    return (int)(rawProduction * fluidAmount / 1000.f);
                }, value -> {}),
                new EnergyValueContainerData(() -> {
                    if(level == null)
                        return 0;

                    List<RecipeHolder<ThermalGeneratorRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ThermalGeneratorRecipe.Type.INSTANCE);

                    int rawProduction = 0;
                    outer:
                    for(RecipeHolder<ThermalGeneratorRecipe> recipe:recipes) {
                        if(recipe.value().getInput().test(ThermalGeneratorBlockEntity.this.fluidStorage.getFluid(0))) {
                            rawProduction = recipe.value().getEnergyProduction();
                            rawProduction = (int)(rawProduction * ENERGY_PRODUCTION_MULTIPLIER *
                                    upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_PRODUCTION));

                            break outer;
                        }
                    }

                    //Calculate real production (raw production is in x FE per 1000 mB, use fluid amount without cap)
                    return (int)(rawProduction * ThermalGeneratorBlockEntity.this.fluidStorage.getAmount(0) / 1000.f);
                }, value -> {}),
                new RedstoneModeValueContainerData(() -> redstoneMode, value -> redstoneMode = value),
                new ComparatorModeValueContainerData(() -> comparatorMode, value -> comparatorMode = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        syncFluidToPlayer(player);

        return new ThermalGeneratorMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    public @Nullable IFluidHandler getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        if(blockEntity.redstoneMode.isActive(state.getValue(ThermalGeneratorBlock.POWERED)))
            tickRecipe(level, blockPos, state, blockEntity);

        blockEntity.pushEnergyToOutputs(Direction.values());
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        List<RecipeHolder<ThermalGeneratorRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(ThermalGeneratorRecipe.Type.INSTANCE);

        int rawProduction = 0;
        outer:
        for(RecipeHolder<ThermalGeneratorRecipe> recipe:recipes) {
            if(recipe.value().getInput().test(blockEntity.fluidStorage.getFluid(0))) {
                rawProduction = recipe.value().getEnergyProduction();
                rawProduction = (int)(rawProduction * ENERGY_PRODUCTION_MULTIPLIER *
                        blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_PRODUCTION));

                break outer;
            }
        }

        if(rawProduction > 0 && blockEntity.energyStorage.getEnergy() < blockEntity.energyStorage.getCapacity()) {
            //Calculate real production (raw production is in x FE per 1000 mB, 50 mB of fluid can be consumed per tick)
            int production = (int)(rawProduction * (Math.min(blockEntity.fluidStorage.getAmount(0), 50) / 1000.f));

            //Cap production
            production = Math.max(0, Math.min(production, blockEntity.energyStorage.getCapacity() - blockEntity.energyStorage.getEnergy()));

            int fluidAmount = (int)((float)production/rawProduction * 1000);

            //Re-calculate energy production (Prevents draining of not enough fluid)
            production = (int)(rawProduction * fluidAmount / 1000.f);

            blockEntity.fluidStorage.drain(fluidAmount, IFluidHandler.FluidAction.EXECUTE);

            blockEntity.energyStorage.setEnergy(Math.min(blockEntity.energyStorage.getCapacity(),
                    blockEntity.energyStorage.getEnergy() + production));
        }
    }
}