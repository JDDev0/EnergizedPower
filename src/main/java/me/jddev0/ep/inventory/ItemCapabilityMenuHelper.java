package me.jddev0.ep.inventory;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.Optional;

public final class ItemCapabilityMenuHelper {
    private ItemCapabilityMenuHelper() {}

    public static Optional<IEnergizedPowerItemStackHandler> getEnergizedPowerItemStackHandlerCapability(Level level, BlockEntity blockEntity) {
        return Optional.ofNullable(level.getCapability(Capabilities.Item.BLOCK, blockEntity.getBlockPos(),
                blockEntity.getBlockState(), blockEntity, null)).flatMap(capability -> {
                    if(capability instanceof IEnergizedPowerItemStackHandler energizedPowerItemStackHandler) {
                        return Optional.of(energizedPowerItemStackHandler);
                    }

                    return Optional.empty();
                });
    }
}
