package me.jddev0.ep.block.entity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class EnergizedPowerTrackedBlockEntity extends BlockEntity {
    protected final List<ServerPlayer> playersTracking = new ArrayList<>();

    public EnergizedPowerTrackedBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    protected void onPlayerStartTracking(ServerPlayer player) {
        playersTracking.add(player);
    }

    public void onPlayerStopTracking(ServerPlayer player) {
        playersTracking.remove(player);
    }
}
