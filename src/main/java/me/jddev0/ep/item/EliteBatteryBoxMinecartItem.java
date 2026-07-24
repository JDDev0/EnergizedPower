package me.jddev0.ep.item;

import me.jddev0.ep.entity.MinecartEliteBatteryBox;
import net.minecraft.world.level.Level;

public class EliteBatteryBoxMinecartItem extends AbstractBatteryBoxMinecartItem<MinecartEliteBatteryBox> {
    public EliteBatteryBoxMinecartItem(Properties props) {
        super(props, MinecartEliteBatteryBox.CAPACITY, MinecartEliteBatteryBox.MAX_TRANSFER);
    }

    @Override
    protected MinecartEliteBatteryBox crateMinecartEntity(Level level, double x, double y, double z) {
        return new MinecartEliteBatteryBox(level, x, y, z);
    }
}
