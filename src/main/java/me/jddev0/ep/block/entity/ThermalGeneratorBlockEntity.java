package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.ThermalGeneratorBlock;
import me.jddev0.ep.block.base.HorizontallyOrientableWorkerMachineBlock;
import me.jddev0.ep.block.entity.base.ConfigurableUpgradableFluidEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.fluid.EnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.*;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.ThermalGeneratorRecipe;
import me.jddev0.ep.screen.ThermalGeneratorMenu;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ThermalGeneratorBlockEntity
        extends ConfigurableUpgradableFluidEnergyStorageBlockEntity<EnergizedPowerEnergyStorage, EnergizedPowerFluidStorage> {
    private static final int FLUID_SLOT_INPUT = 0;

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
            public long getCapacityAsLong() {
                return Math.max(1, (long)Math.ceil(capacity * upgradeModuleInventory.getModifierEffectProduct(
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
            public boolean isValid(int tank, FluidResource resource) {
                if(!super.isValid(tank, resource) || !(level instanceof ServerLevel serverLevel))
                    return false;

                Collection<RecipeHolder<ThermalGeneratorRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverLevel, ThermalGeneratorRecipe.Type.INSTANCE);

                return recipes.stream().map(RecipeHolder::value).map(ThermalGeneratorRecipe::getInput).
                        anyMatch(input -> input.test(resource));
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new EnergyValueContainerData(() -> {
                    if(!(level instanceof ServerLevel serverLevel))
                        return 0;

                    Collection<RecipeHolder<ThermalGeneratorRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverLevel, ThermalGeneratorRecipe.Type.INSTANCE);

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
                    int production = (int)(rawProduction * (Math.min(fluidStorage.getAmountAsInt(0), 50) / 1000.f));

                    //Cap production
                    production = Math.max(0, Math.min(production, energyStorage.getCapacityAsInt() - energyStorage.getAmountAsInt()));

                    int fluidAmount = (int)((float)production/rawProduction * 1000);

                    //Re-calculate energy production (Prevents draining of not enough fluid)
                    return (int)(rawProduction * fluidAmount / 1000.f);
                }, value -> {}),
                new EnergyValueContainerData(() -> {
                    if(!(level instanceof ServerLevel serverLevel))
                        return 0;

                    Collection<RecipeHolder<ThermalGeneratorRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverLevel, ThermalGeneratorRecipe.Type.INSTANCE);

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
                    return (int)(rawProduction * ThermalGeneratorBlockEntity.this.fluidStorage.getAmountAsInt(0) / 1000.f);
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
        syncIOConfigurationToPlayer(player);

        return new ThermalGeneratorMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    @Override
    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        if(slotType == SlotType.FLUID) {
            return List.of(
                    //Input only
                    SlotGroup.of(SlotEntry.ofInput(FLUID_SLOT_INPUT)),

                    //Output all tanks (Every EP fluid tank should support output to allow draining fluids without losing them)
                    SlotGroup.of(SlotEntry.ofOutput(FLUID_SLOT_INPUT))
            );
        }

        return super.initSlotGroups(slotType);
    }

    @Override
    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        IOConfiguration conf = new IOConfiguration();

        if(slotType == SlotType.FLUID) {
            for(RelativeDirection direction:RelativeDirection.values())
                conf.setSlotGroupId(direction, 0);
        }

        return conf;
    }

    public @Nullable ResourceHandler<FluidResource> getFluidHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return fluidStorage;

        Direction facing = getBlockState().getValue(HorizontallyOrientableWorkerMachineBlock.FACING);
        IOConfiguration conf = getIOConfiguration(SlotType.FLUID);
        List<SlotGroup> slotGroups = getSlotGroups(SlotType.FLUID);
        return conf.createSidedFluidHandlerFor(slotGroups, fluidStorage, facing, side);
    }

    public @Nullable EnergyHandler getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        if(blockEntity.redstoneMode.isActive(state.getValue(ThermalGeneratorBlock.POWERED)))
            tickRecipe(level, blockPos, state, blockEntity);

        blockEntity.pushEnergyToOutputs(Direction.values());
    }

    private static void tickRecipe(Level level, BlockPos blockPos, BlockState state, ThermalGeneratorBlockEntity blockEntity) {
        if(level.isClientSide() || !(level instanceof ServerLevel serverLevel))
            return;

        Collection<RecipeHolder<ThermalGeneratorRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverLevel, ThermalGeneratorRecipe.Type.INSTANCE);

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

        if(rawProduction > 0 && blockEntity.energyStorage.getAmountAsInt() < blockEntity.energyStorage.getCapacityAsInt()) {
            //Calculate real production (raw production is in x FE per 1000 mB, 50 mB of fluid can be consumed per tick)
            int production = (int)(rawProduction * (Math.min(blockEntity.fluidStorage.getAmountAsInt(0), 50) / 1000.f));

            //Cap production
            production = Math.max(0, Math.min(production, blockEntity.energyStorage.getCapacityAsInt() - blockEntity.energyStorage.getAmountAsInt()));

            int fluidAmount = (int)((float)production/rawProduction * 1000);

            //Re-calculate energy production (Prevents draining of not enough fluid)
            production = (int)(rawProduction * fluidAmount / 1000.f);

            try(Transaction transaction = Transaction.open(null)) {
                blockEntity.fluidStorage.extract(blockEntity.fluidStorage.getResource(0), fluidAmount, transaction);

                blockEntity.energyStorage.insert(production, transaction);
                transaction.commit();
            }
        }
    }
}