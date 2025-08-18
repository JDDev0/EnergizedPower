package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.EPMenuTypes;
import me.jddev0.ep.screen.SolarPanelMenu;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;

public enum SolarPanelTier {
    TIER_1("solar_panel_1", ModConfigs.COMMON_SOLAR_PANEL_1_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_1_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_1_CAPACITY.getValue(),
            AbstractBlock.Settings.create().
                    requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
    TIER_2("solar_panel_2", ModConfigs.COMMON_SOLAR_PANEL_2_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_2_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_2_CAPACITY.getValue(),
            AbstractBlock.Settings.create().
                    requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
    TIER_3("solar_panel_3", ModConfigs.COMMON_SOLAR_PANEL_3_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_3_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_3_CAPACITY.getValue(),
            AbstractBlock.Settings.create().
                    requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
    TIER_4("solar_panel_4", ModConfigs.COMMON_SOLAR_PANEL_4_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_4_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_4_CAPACITY.getValue(),
            AbstractBlock.Settings.create().
                    requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
    TIER_5("solar_panel_5", ModConfigs.COMMON_SOLAR_PANEL_5_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_5_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_5_CAPACITY.getValue(),
            AbstractBlock.Settings.create().
                    requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL)),
    TIER_6("solar_panel_6", ModConfigs.COMMON_SOLAR_PANEL_6_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_6_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_6_CAPACITY.getValue(),
            AbstractBlock.Settings.create().
                    requiresTool().strength(4.0f, 5.0f).sounds(BlockSoundGroup.METAL));

    private final String resourceId;
    private final long peakFePerTick;
    private final long maxTransfer;
    private final long capacity;
    private final AbstractBlock.Settings props;

    SolarPanelTier(String resourceId, long peakFePerTick, long maxTransfer, long capacity, AbstractBlock.Settings props) {
        this.resourceId = resourceId;
        this.peakFePerTick = peakFePerTick;
        this.maxTransfer = maxTransfer;
        this.capacity = capacity;
        this.props = props;
    }

    public ScreenHandlerType<SolarPanelMenu> getMenuTypeFromTier() {
        return switch(this) {
            case TIER_1 -> EPMenuTypes.SOLAR_PANEL_MENU_1;
            case TIER_2 -> EPMenuTypes.SOLAR_PANEL_MENU_2;
            case TIER_3 -> EPMenuTypes.SOLAR_PANEL_MENU_3;
            case TIER_4 -> EPMenuTypes.SOLAR_PANEL_MENU_4;
            case TIER_5 -> EPMenuTypes.SOLAR_PANEL_MENU_5;
            case TIER_6 -> EPMenuTypes.SOLAR_PANEL_MENU_6;
        };
    }

    public Block getBlockFromTier() {
        return switch(this) {
            case TIER_1 -> EPBlocks.SOLAR_PANEL_1;
            case TIER_2 -> EPBlocks.SOLAR_PANEL_2;
            case TIER_3 -> EPBlocks.SOLAR_PANEL_3;
            case TIER_4 -> EPBlocks.SOLAR_PANEL_4;
            case TIER_5 -> EPBlocks.SOLAR_PANEL_5;
            case TIER_6 -> EPBlocks.SOLAR_PANEL_6;
        };
    }

    public BlockEntityType<SolarPanelBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case TIER_1 -> EPBlockEntities.SOLAR_PANEL_ENTITY_1;
            case TIER_2 -> EPBlockEntities.SOLAR_PANEL_ENTITY_2;
            case TIER_3 -> EPBlockEntities.SOLAR_PANEL_ENTITY_3;
            case TIER_4 -> EPBlockEntities.SOLAR_PANEL_ENTITY_4;
            case TIER_5 -> EPBlockEntities.SOLAR_PANEL_ENTITY_5;
            case TIER_6 -> EPBlockEntities.SOLAR_PANEL_ENTITY_6;
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public long getPeakFePerTick() {
        return peakFePerTick;
    }

    public long getMaxTransfer() {
        return maxTransfer;
    }

    public long getCapacity() {
        return capacity;
    }

    public AbstractBlock.Settings getProperties() {
        return props;
    }
}
