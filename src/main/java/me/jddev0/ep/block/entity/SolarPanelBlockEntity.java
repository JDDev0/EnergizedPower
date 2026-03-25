package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.machine.tier.SolarPanelTier;
import me.jddev0.ep.screen.SolarPanelMenu;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;

public class SolarPanelBlockEntity
        extends UpgradableEnergyStorageBlockEntity<EnergizedPowerEnergyStorage> {
    private final SolarPanelTier tier;

    public SolarPanelBlockEntity(BlockPos blockPos, BlockState blockState, SolarPanelTier tier) {
        super(
                tier.getEntityTypeFromTier(), blockPos, blockState,

                tier.getResourceId(),

                tier.getCapacity(),
                tier.getMaxTransfer(),

                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.MOON_LIGHT
        );

        this.tier = tier;
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
                setChanged();
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

    public SolarPanelTier getTier() {
        return tier;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);
        
        return new SolarPanelMenu(id, this, inventory, upgradeModuleInventory);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, SolarPanelBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        int i = 4 * (level.getBrightness(LightLayer.SKY, blockPos) - level.getSkyDarken()); //(0 - 15) * 4 => (0 - 60)
        float f = level.environmentAttributes().getValue(EnvironmentAttributes.SUN_ANGLE, blockPos) * ((float)Math.PI / 180F);
        if(i > 0) {
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);

            f += (f1 - f) * 0.2F;

            i = Math.round((float)i * Mth.cos(f));
        }

        i = Mth.clamp(i, 0, 60);

        long energyProduction = (long)(i/60.f * blockEntity.getTier().getPeakFePerTick());

        double moonLightUpgradeModuleEffect = blockEntity.upgradeModuleInventory.
                getUpgradeModuleModifierEffect(1, UpgradeModuleModifier.MOON_LIGHT);
        if(moonLightUpgradeModuleEffect > 0) {
            i = 15 - (level.getBrightness(LightLayer.SKY, blockPos) - level.getSkyDarken());
            if(i < 14) {
                i = Mth.clamp(i, 0, 15);

                energyProduction += (long)(i/15. * blockEntity.getTier().getPeakFePerTick() * moonLightUpgradeModuleEffect);
            }
        }

        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.energyStorage.insert(energyProduction, transaction);
            transaction.commit();
        }

        transferEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferEnergy(Level level, BlockPos blockPos, BlockState state, SolarPanelBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        BlockPos testPos = blockPos.relative(Direction.DOWN);

        BlockEntity testBlockEntity = level.getBlockEntity(testPos);
        if(testBlockEntity == null)
            return;

        EnergyStorage limitingEnergyStorage = EnergyStorage.SIDED.find(level, testPos, Direction.DOWN.getOpposite());
        if(limitingEnergyStorage == null)
            return;

        if(!limitingEnergyStorage.supportsInsertion())
            return;

        try(Transaction transaction = Transaction.openOuter()) {
            long amount = limitingEnergyStorage.insert(Math.min(blockEntity.energyStorage.getAmount(),
                    blockEntity.limitingEnergyStorage.getMaxExtract()), transaction);
            blockEntity.limitingEnergyStorage.extract(amount, transaction);
            transaction.commit();
        }
    }
}
