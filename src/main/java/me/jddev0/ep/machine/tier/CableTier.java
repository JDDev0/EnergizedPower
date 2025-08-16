package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.entity.CableBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.config.ModConfigs;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public enum CableTier {
    TIN("tin_cable", ModConfigs.COMMON_TIN_CABLE_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL)),
    COPPER("copper_cable", ModConfigs.COMMON_COPPER_CABLE_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL)),
    GOLD("gold_cable", ModConfigs.COMMON_GOLD_CABLE_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL)),
    ENERGIZED_COPPER("energized_copper_cable", ModConfigs.COMMON_ENERGIZED_COPPER_CABLE_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL)),
    ENERGIZED_GOLD("energized_gold_cable", ModConfigs.COMMON_ENERGIZED_GOLD_CABLE_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL)),
    ENERGIZED_CRYSTAL_MATRIX("energized_crystal_matrix_cable", ModConfigs.COMMON_ENERGIZED_CRYSTAL_MATRIX_CABLE_TRANSFER_RATE.getValue(),
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(.5f).sound(SoundType.WOOL));

    private final String resourceId;
    private final int maxTransfer;
    private final BlockBehaviour.Properties props;

    CableTier(String resourceId, int maxTransfer, BlockBehaviour.Properties props) {
        this.resourceId = resourceId;
        this.maxTransfer = maxTransfer;
        this.props = props;
    }

    public BlockEntityType<CableBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case TIN -> EPBlockEntities.TIN_CABLE_ENTITY.get();
            case COPPER -> EPBlockEntities.COPPER_CABLE_ENTITY.get();
            case GOLD -> EPBlockEntities.GOLD_CABLE_ENTITY.get();
            case ENERGIZED_COPPER -> EPBlockEntities.ENERGIZED_COPPER_CABLE_ENTITY.get();
            case ENERGIZED_GOLD -> EPBlockEntities.ENERGIZED_GOLD_CABLE_ENTITY.get();
            case ENERGIZED_CRYSTAL_MATRIX -> EPBlockEntities.ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY.get();
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }

    public BlockBehaviour.Properties getProperties() {
        return props;
    }
}
