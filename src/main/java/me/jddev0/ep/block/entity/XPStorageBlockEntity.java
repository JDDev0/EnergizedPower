package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.MenuFluidStorageBlockEntity;
import me.jddev0.ep.fluid.EnergizedPowerXPFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.XPValueContainerData;
import me.jddev0.ep.machine.tier.XPStorageTier;
import me.jddev0.ep.screen.XPStorageMenu;
import me.jddev0.ep.util.XPUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class XPStorageBlockEntity extends MenuFluidStorageBlockEntity<EnergizedPowerXPFluidStorage> {
    private final XPStorageTier tier;

    public XPStorageBlockEntity(BlockPos blockPos, BlockState blockState, XPStorageTier tier) {
        super(
                tier.getEntityTypeFromTier(), blockPos, blockState,

                tier.getResourceId(),

                tier.getXPStorageCapacity()
        );

        this.tier = tier;
    }

    @Override
    protected EnergizedPowerXPFluidStorage initFluidStorage() {
        return new EnergizedPowerXPFluidStorage(XPUtils.getTotalXPFromLevel((int)baseTankCapacity)) {
            @Override
            protected void onFinalCommit() {
                setChanged();
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new XPValueContainerData(fluidStorage::getXPAmount, fluidStorage::setXPAmount),
                new XPValueContainerData(fluidStorage::getXpCapacity, value -> {})
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new XPStorageMenu(id, inventory, this, data);
    }

    public int getRedstoneOutput() {
        double fullnessPercent = (double)fluidStorage.getXPAmount() / fluidStorage.getXpCapacity();
        boolean isEmptyFlag = fluidStorage.getXPAmount() <= 0;

        return Math.min(Mth.floor(fullnessPercent * 14.f) + (isEmptyFlag?0:1), 15);
    }

    public @Nullable Storage<FluidVariant> getFluidHandlerCapability(@Nullable Direction side) {
        return fluidStorage;
    }

    public XPStorageTier getTier() {
        return tier;
    }

    public void onInsertExtractXP(int levels, ServerPlayer player) {
        int xpLevelPlayer = player.experienceLevel;

        if(levels > 0) {
            long xpTotalToInsert;
            if(xpLevelPlayer >= levels)
                xpTotalToInsert = XPUtils.getTotalXPFromLevel(xpLevelPlayer) - XPUtils.getTotalXPFromLevel(Math.max(0, xpLevelPlayer - levels));
            else
                xpTotalToInsert = XPUtils.getTotalXPFromPlayer(player);

            long insertedAmount;
            try(Transaction transaction = Transaction.openOuter()) {
                insertedAmount = fluidStorage.insertXP(xpTotalToInsert, transaction);
                transaction.commit();
            }
            if(insertedAmount > 0) {
                //There is a slight rounding error for certain values in giveExperiencePoints()
                // [e.g. 20 levels - 10 levels => 10 levels - 1 lost xp point], but only for a few xp points
                player.giveExperiencePoints(-(int)insertedAmount);
            }
        }else {
            levels = -levels;

            long xpTotalToExtract;
            if(levels == Integer.MAX_VALUE)
                xpTotalToExtract = Integer.MAX_VALUE;
            else
                xpTotalToExtract = XPUtils.getTotalXPFromLevel(xpLevelPlayer + levels) - XPUtils.getTotalXPFromLevel(xpLevelPlayer);

            long extractedAmount;
            try(Transaction transaction = Transaction.openOuter()) {
                extractedAmount = fluidStorage.extractXP(Math.min(Integer.MAX_VALUE, xpTotalToExtract), transaction);
                transaction.commit();
            }
            if(extractedAmount > 0) {
                //There is a slight rounding error for certain values in giveExperiencePoints()
                // [e.g. 20 levels - 10 levels => 10 levels - 1 lost xp point], but only for a few xp points
                player.giveExperiencePoints((int)extractedAmount);
            }
        }
    }
}