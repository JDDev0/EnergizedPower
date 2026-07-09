package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.LightningGeneratorBlock;
import me.jddev0.ep.block.entity.base.MenuEnergyStorageBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.EnergizedPowerEnergyStorage;
import me.jddev0.ep.energy.EnergizedPowerLimitingEnergyStorage;
import me.jddev0.ep.screen.LightningGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class LightningGeneratorBlockEntity extends MenuEnergyStorageBlockEntity<EnergizedPowerEnergyStorage> {
    public LightningGeneratorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.LIGHTING_GENERATOR_ENTITY.get(), blockPos, blockState,

                "lightning_generator",

                LightningGeneratorBlock.ENERGY_PER_LIGHTNING_STRIKE,
                ModConfigs.COMMON_LIGHTNING_GENERATOR_TRANSFER_RATE.getValue()
        );
    }

    @Override
    protected EnergizedPowerEnergyStorage initEnergyStorage() {
        return new EnergizedPowerEnergyStorage(baseEnergyCapacity) {
            @Override
            protected void onFinalCommit() {
                setChanged();
                syncEnergyToPlayers(32);
            }
        };
    }

    @Override
    protected EnergizedPowerLimitingEnergyStorage initLimitingEnergyStorage() {
        return new EnergizedPowerLimitingEnergyStorage(energyStorage, 0, baseEnergyTransferRate);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncEnergyToPlayer(player);

        return new LightningGeneratorMenu(id, inventory, this);
    }

    public void onLightningStrike() {
        try(Transaction transaction = Transaction.open(null)) {
            energyStorage.insert(LightningGeneratorBlock.ENERGY_PER_LIGHTNING_STRIKE, transaction);
            transaction.commit();
        }
    }

    public @Nullable EnergyHandler getEnergyStorageCapability(@Nullable Direction side) {
        return limitingEnergyStorage;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, LightningGeneratorBlockEntity blockEntity) {
        if(level.isClientSide())
            return;

        blockEntity.pushEnergyToOutputs(Direction.values());
    }
}