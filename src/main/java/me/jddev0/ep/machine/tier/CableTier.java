package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.entity.CableBlockEntity;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.config.ModConfigs;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;

public enum CableTier {
    TIN("tin_cable", ModConfigs.COMMON_TIN_CABLE_TRANSFER_RATE.getValue(),
            FabricBlockSettings.create().mapColor(MapColor.GRAY).strength(.5f).sounds(BlockSoundGroup.WOOL)),
    COPPER("copper_cable", ModConfigs.COMMON_COPPER_CABLE_TRANSFER_RATE.getValue(),
            FabricBlockSettings.create().mapColor(MapColor.GRAY).strength(.5f).sounds(BlockSoundGroup.WOOL)),
    GOLD("gold_cable", ModConfigs.COMMON_GOLD_CABLE_TRANSFER_RATE.getValue(),
            FabricBlockSettings.create().mapColor(MapColor.GRAY).strength(.5f).sounds(BlockSoundGroup.WOOL)),
    ENERGIZED_COPPER("energized_copper_cable", ModConfigs.COMMON_ENERGIZED_COPPER_CABLE_TRANSFER_RATE.getValue(),
            FabricBlockSettings.create().mapColor(MapColor.GRAY).strength(.5f).sounds(BlockSoundGroup.WOOL)),
    ENERGIZED_GOLD("energized_gold_cable", ModConfigs.COMMON_ENERGIZED_GOLD_CABLE_TRANSFER_RATE.getValue(),
            FabricBlockSettings.create().mapColor(MapColor.GRAY).strength(.5f).sounds(BlockSoundGroup.WOOL)),
    ENERGIZED_CRYSTAL_MATRIX("energized_crystal_matrix_cable", ModConfigs.COMMON_ENERGIZED_CRYSTAL_MATRIX_CABLE_TRANSFER_RATE.getValue(),
            FabricBlockSettings.create().mapColor(MapColor.GRAY).strength(.5f).sounds(BlockSoundGroup.WOOL));

    private final String resourceId;
    private final long maxTransfer;
    private final FabricBlockSettings props;

    CableTier(String resourceId, long maxTransfer, FabricBlockSettings props) {
        this.resourceId = resourceId;
        this.maxTransfer = maxTransfer;
        this.props = props;
    }

    public BlockEntityType<CableBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case TIN -> EPBlockEntities.TIN_CABLE_ENTITY;
            case COPPER -> EPBlockEntities.COPPER_CABLE_ENTITY;
            case GOLD -> EPBlockEntities.GOLD_CABLE_ENTITY;
            case ENERGIZED_COPPER -> EPBlockEntities.ENERGIZED_COPPER_CABLE_ENTITY;
            case ENERGIZED_GOLD -> EPBlockEntities.ENERGIZED_GOLD_CABLE_ENTITY;
            case ENERGIZED_CRYSTAL_MATRIX -> EPBlockEntities.ENERGIZED_CRYSTAL_MATRIX_CABLE_ENTITY;
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public long getMaxTransfer() {
        return maxTransfer;
    }

    public FabricBlockSettings getProperties() {
        return props;
    }
}
