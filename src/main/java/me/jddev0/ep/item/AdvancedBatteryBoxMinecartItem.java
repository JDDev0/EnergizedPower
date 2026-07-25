package me.jddev0.ep.item;

import me.jddev0.ep.entity.MinecartAdvancedBatteryBox;

import net.minecraft.world.level.Level;

public class AdvancedBatteryBoxMinecartItem extends AbstractBatteryBoxMinecartItem<MinecartAdvancedBatteryBox> {
    public AdvancedBatteryBoxMinecartItem(Properties props) {
        super(props, MinecartAdvancedBatteryBox.CAPACITY, MinecartAdvancedBatteryBox.MAX_TRANSFER);
    }

    @Override
    protected MinecartAdvancedBatteryBox crateMinecartEntity(Level level, double x, double y, double z) {
        return new MinecartAdvancedBatteryBox(level, x, y, z);
    }
}
