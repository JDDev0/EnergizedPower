package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.SolarPanelBlock;
import me.jddev0.ep.block.entity.base.UpgradableEnergyStorageBlockEntity;
import me.jddev0.ep.energy.ExtractOnlyEnergyStorage;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.screen.SolarPanelMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlockEntity extends UpgradableEnergyStorageBlockEntity<ExtractOnlyEnergyStorage>
        implements MenuProvider {
    private final SolarPanelBlock.Tier tier;

    private final LazyOptional<IEnergyStorage> lazyEnergyStorage;

    public static BlockEntityType<SolarPanelBlockEntity> getEntityTypeFromTier(SolarPanelBlock.Tier tier) {
        return switch(tier) {
            case TIER_1 -> ModBlockEntities.SOLAR_PANEL_ENTITY_1.get();
            case TIER_2 -> ModBlockEntities.SOLAR_PANEL_ENTITY_2.get();
            case TIER_3 -> ModBlockEntities.SOLAR_PANEL_ENTITY_3.get();
            case TIER_4 -> ModBlockEntities.SOLAR_PANEL_ENTITY_4.get();
            case TIER_5 -> ModBlockEntities.SOLAR_PANEL_ENTITY_5.get();
            case TIER_6 -> ModBlockEntities.SOLAR_PANEL_ENTITY_6.get();
        };
    }

    public SolarPanelBlockEntity(BlockPos blockPos, BlockState blockState, SolarPanelBlock.Tier tier) {
        super(
                getEntityTypeFromTier(tier), blockPos, blockState,

                tier.getCapacity(),
                tier.getMaxTransfer(),

                UpgradeModuleModifier.ENERGY_CAPACITY,
                UpgradeModuleModifier.MOON_LIGHT
        );

        this.tier = tier;

        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
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
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower." + tier.getResourceId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new SolarPanelMenu(id, inventory, this, upgradeModuleInventory);
    }

    public SolarPanelBlock.Tier getTier() {
        return tier;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, SolarPanelBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        int i = 4 * (level.getBrightness(LightLayer.SKY, blockPos) - level.getSkyDarken()); //(0 - 15) * 4 => (0 - 60)
        float f = level.getSunAngle(1.0F);
        if(i > 0) {
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);

            f += (f1 - f) * 0.2F;

            i = Math.round((float)i * Mth.cos(f));
        }

        i = Mth.clamp(i, 0, 60);

        int energyProduction = (int)(i/60.f * blockEntity.getTier().getPeakFePerTick());

        double moonLightUpgradeModuleEffect = blockEntity.upgradeModuleInventory.
                getUpgradeModuleModifierEffect(1, UpgradeModuleModifier.MOON_LIGHT);
        if(moonLightUpgradeModuleEffect > 0) {
            i = 15 - (level.getBrightness(LightLayer.SKY, blockPos) - level.getSkyDarken());
            if(i < 14) {
                i = Mth.clamp(i, 0, 15);

                energyProduction += (int)(i/15. * blockEntity.getTier().getPeakFePerTick() * moonLightUpgradeModuleEffect);
            }
        }

        blockEntity.energyStorage.setEnergy(Math.min(blockEntity.energyStorage.getCapacity(),
                blockEntity.energyStorage.getEnergy() + energyProduction));

        transferEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferEnergy(Level level, BlockPos blockPos, BlockState state, SolarPanelBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        BlockPos testPos = blockPos.relative(Direction.DOWN);

        BlockEntity testBlockEntity = level.getBlockEntity(testPos);
        if(testBlockEntity == null)
            return;

        LazyOptional<IEnergyStorage> energyStorageLazyOptional = testBlockEntity.getCapability(Capabilities.ENERGY, Direction.DOWN.getOpposite());
        if(!energyStorageLazyOptional.isPresent())
            return;

        IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
        if(!energyStorage.canReceive())
            return;

        int amount = energyStorage.receiveEnergy(Math.min(blockEntity.energyStorage.getEnergy(), blockEntity.energyStorage.getMaxExtract()), false);
        if(amount > 0)
            blockEntity.energyStorage.extractEnergy(amount, false);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.ENERGY) {
            if(side == null || side == Direction.DOWN)
                return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }
}
