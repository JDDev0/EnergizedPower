package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.inventory.data.CombinedContainerData;
import me.jddev0.ep.inventory.data.EnergyValueContainerData;
import me.jddev0.ep.machine.RedstoneOutput;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.recipe.HeatGeneratorRecipe;
import me.jddev0.ep.screen.HeatGeneratorMenu;
import me.jddev0.ep.util.EnergyUtils;
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
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

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
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new EnergyValueContainerData(() -> {
                    if(!(level instanceof ServerLevel serverLevel))
                        return 0;

                    Collection<RecipeHolder<HeatGeneratorRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverLevel, HeatGeneratorRecipe.Type.INSTANCE);

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

                    return Math.min(productionSum, energyStorage.getCapacityAsInt() - energyStorage.getAmountAsInt());
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

    public @Nullable EnergyHandler getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, HeatGeneratorBlockEntity blockEntity) {
        if(level.isClientSide() || !(level instanceof ServerLevel serverLevel))
            return;

        Collection<RecipeHolder<HeatGeneratorRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverLevel, HeatGeneratorRecipe.Type.INSTANCE);

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

            try(Transaction transaction = Transaction.open(null)) {
                blockEntity.energyStorage.insert(productionSum, transaction);
                transaction.commit();
            }
        }

        blockEntity.pushEnergyToOutputs(Direction.values());
    }
}