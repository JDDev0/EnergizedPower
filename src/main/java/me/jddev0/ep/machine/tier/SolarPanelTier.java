package me.jddev0.ep.machine.tier;

import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.block.entity.EPBlockEntities;
import me.jddev0.ep.block.entity.SolarPanelBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.screen.EPMenuTypes;
import me.jddev0.ep.screen.SolarPanelMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public enum SolarPanelTier {
    TIER_1("solar_panel_1", ModConfigs.COMMON_SOLAR_PANEL_1_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_1_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_1_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    TIER_2("solar_panel_2", ModConfigs.COMMON_SOLAR_PANEL_2_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_2_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_2_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    TIER_3("solar_panel_3", ModConfigs.COMMON_SOLAR_PANEL_3_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_3_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_3_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    TIER_4("solar_panel_4", ModConfigs.COMMON_SOLAR_PANEL_4_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_4_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_4_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    TIER_5("solar_panel_5", ModConfigs.COMMON_SOLAR_PANEL_5_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_5_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_5_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL)),
    TIER_6("solar_panel_6", ModConfigs.COMMON_SOLAR_PANEL_6_ENERGY_PEAK_PRODUCTION.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_6_TRANSFER_RATE.getValue(),
            ModConfigs.COMMON_SOLAR_PANEL_6_CAPACITY.getValue(),
            BlockBehaviour.Properties.of().
                    requiresCorrectToolForDrops().strength(4.0f, 5.0f).sound(SoundType.METAL));

    private final String resourceId;
    private final int peakFePerTick;
    private final int maxTransfer;
    private final int capacity;
    private final BlockBehaviour.Properties props;

    SolarPanelTier(String resourceId, int peakFePerTick, int maxTransfer, int capacity, BlockBehaviour.Properties props) {
        this.resourceId = resourceId;
        this.peakFePerTick = peakFePerTick;
        this.maxTransfer = maxTransfer;
        this.capacity = capacity;
        this.props = props;
    }

    public MenuType<SolarPanelMenu> getMenuTypeFromTier() {
        return switch(this) {
            case TIER_1 -> EPMenuTypes.SOLAR_PANEL_MENU_1.get();
            case TIER_2 -> EPMenuTypes.SOLAR_PANEL_MENU_2.get();
            case TIER_3 -> EPMenuTypes.SOLAR_PANEL_MENU_3.get();
            case TIER_4 -> EPMenuTypes.SOLAR_PANEL_MENU_4.get();
            case TIER_5 -> EPMenuTypes.SOLAR_PANEL_MENU_5.get();
            case TIER_6 -> EPMenuTypes.SOLAR_PANEL_MENU_6.get();
        };
    }

    public Block getBlockFromTier() {
        return switch(this) {
            case TIER_1 -> EPBlocks.SOLAR_PANEL_1.get();
            case TIER_2 -> EPBlocks.SOLAR_PANEL_2.get();
            case TIER_3 -> EPBlocks.SOLAR_PANEL_3.get();
            case TIER_4 -> EPBlocks.SOLAR_PANEL_4.get();
            case TIER_5 -> EPBlocks.SOLAR_PANEL_5.get();
            case TIER_6 -> EPBlocks.SOLAR_PANEL_6.get();
        };
    }

    public BlockEntityType<SolarPanelBlockEntity> getEntityTypeFromTier() {
        return switch(this) {
            case TIER_1 -> EPBlockEntities.SOLAR_PANEL_ENTITY_1.get();
            case TIER_2 -> EPBlockEntities.SOLAR_PANEL_ENTITY_2.get();
            case TIER_3 -> EPBlockEntities.SOLAR_PANEL_ENTITY_3.get();
            case TIER_4 -> EPBlockEntities.SOLAR_PANEL_ENTITY_4.get();
            case TIER_5 -> EPBlockEntities.SOLAR_PANEL_ENTITY_5.get();
            case TIER_6 -> EPBlockEntities.SOLAR_PANEL_ENTITY_6.get();
        };
    }

    public String getResourceId() {
        return resourceId;
    }

    public int getPeakFePerTick() {
        return peakFePerTick;
    }

    public int getMaxTransfer() {
        return maxTransfer;
    }

    public int getCapacity() {
        return capacity;
    }

    public BlockBehaviour.Properties getProperties() {
        return props;
    }
}
