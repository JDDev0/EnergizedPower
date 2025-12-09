package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.machine.tier.SolarPanelTier;
import me.jddev0.ep.screen.SolarPanelMenu;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import net.minecraft.world.attribute.EnvironmentAttributes;
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

    public SolarPanelTier getTier() {
        return tier;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncEnergyToPlayer(player);
        
        return new SolarPanelMenu(id, this, inventory, upgradeModuleInventory);
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, SolarPanelBlockEntity blockEntity) {
        if(level.isClient())
            return;

        int i = 4 * (level.getLightLevel(LightType.SKY, blockPos) - level.getAmbientDarkness()); //(0 - 15) * 4 => (0 - 60)
        float f = level.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.SUN_ANGLE_VISUAL, blockPos) * ((float)Math.PI / 180F);
        if(i > 0) {
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);

            f += (f1 - f) * 0.2F;

            i = Math.round((float)i * MathHelper.cos(f));
        }

        i = MathHelper.clamp(i, 0, 60);

        long energyProduction = (long)(i/60.f * blockEntity.getTier().getPeakFePerTick());

        double moonLightUpgradeModuleEffect = blockEntity.upgradeModuleInventory.
                getUpgradeModuleModifierEffect(1, UpgradeModuleModifier.MOON_LIGHT);
        if(moonLightUpgradeModuleEffect > 0) {
            i = 15 - (level.getLightLevel(LightType.SKY, blockPos) - level.getAmbientDarkness());
            if(i < 14) {
                i = MathHelper.clamp(i, 0, 15);

                energyProduction += (long)(i/15. * blockEntity.getTier().getPeakFePerTick() * moonLightUpgradeModuleEffect);
            }
        }

        try(Transaction transaction = Transaction.openOuter()) {
            blockEntity.energyStorage.insert(energyProduction, transaction);
            transaction.commit();
        }

        transferEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferEnergy(World level, BlockPos blockPos, BlockState state, SolarPanelBlockEntity blockEntity) {
        if(level.isClient())
            return;

        BlockPos testPos = blockPos.offset(Direction.DOWN);

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
