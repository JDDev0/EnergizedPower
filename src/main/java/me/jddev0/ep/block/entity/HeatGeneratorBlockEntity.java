package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.EnergyValueContainerData;
import me.jddev0.ep.machine.RedstoneOutput;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.HeatGeneratorRecipe;
import me.jddev0.ep.screen.HeatGeneratorMenu;
import me.jddev0.ep.util.EnergyUtils;
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
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HeatGeneratorBlockEntity extends UpgradableEnergyStorageBlockEntity<EnergizedPowerEnergyStorage>
        implements RedstoneOutput {
    public static final float ENERGY_PRODUCTION_MULTIPLIER = ModConfigs.COMMON_HEAT_GENERATOR_ENERGY_PRODUCTION_MULTIPLIER.getValue();

    public HeatGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.HEAT_GENERATOR_ENTITY.get(), blockPos, blockState,

                "heat_generator",

                ModConfigs.COMMON_HEAT_GENERATOR_CAPACITY.getValue(),
                ModConfigs.COMMON_HEAT_GENERATOR_TRANSFER_RATE.getValue(),

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
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new EnergyValueContainerData(() -> {
                    if(!(level instanceof ServerLevel))
                        return 0;

                    List<RecipeHolder<HeatGeneratorRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(HeatGeneratorRecipe.Type.INSTANCE);

                    int productionSum = 0;
                    for(Direction direction:Direction.values()) {
                        BlockPos checkPos = getBlockPos().relative(direction);
                        FluidState fluidState = level.getFluidState(checkPos);

                        outer:
                        for(RecipeHolder<HeatGeneratorRecipe> recipe:recipes) {
                            if(recipe.value().getInput().test(fluidState)) {
                                productionSum += recipe.value().getEnergyProduction();

                                break outer;
                            }
                        }
                    }

                    productionSum = (int)(productionSum * ENERGY_PRODUCTION_MULTIPLIER *
                            upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_PRODUCTION));

                    return Math.min(productionSum, energyStorage.getCapacity() - energyStorage.getEnergy());
                }, value -> {})
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new HeatGeneratorMenu(id, inventory, this, upgradeModuleInventory, this.data);
    }

    @Override
    public int getRedstoneOutput() {
        return EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
    }

    public @Nullable IEnergyStorage getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, HeatGeneratorBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        List<RecipeHolder<HeatGeneratorRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(HeatGeneratorRecipe.Type.INSTANCE);

        int productionSum = 0;
        for(Direction direction:Direction.values()) {
            BlockPos checkPos = blockPos.relative(direction);
            FluidState fluidState = level.getFluidState(checkPos);

            outer:
            for(RecipeHolder<HeatGeneratorRecipe> recipe:recipes) {
                if(recipe.value().getInput().test(fluidState)) {
                    productionSum += recipe.value().getEnergyProduction();

                    break outer;
                }
            }
        }

        if(productionSum > 0) {
            productionSum = (int)(productionSum * ENERGY_PRODUCTION_MULTIPLIER *
                    blockEntity.upgradeModuleInventory.getModifierEffectProduct(UpgradeModuleModifier.ENERGY_PRODUCTION));

            blockEntity.energyStorage.setEnergy(Math.min(blockEntity.energyStorage.getCapacity(),
                    blockEntity.energyStorage.getEnergy() + productionSum));
        }

        blockEntity.pushEnergyToOutputs(Direction.values());
    }
}