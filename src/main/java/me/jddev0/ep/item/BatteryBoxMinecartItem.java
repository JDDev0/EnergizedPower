package me.jddev0.ep.item;

import me.jddev0.ep.entity.MinecartBatteryBox;
import net.minecraft.world.level.Level;

public class BatteryBoxMinecartItem extends AbstractBatteryBoxMinecartItem<MinecartBatteryBox> {
    public BatteryBoxMinecartItem(Properties props) {
        super(props, MinecartBatteryBox.CAPACITY, MinecartBatteryBox.MAX_TRANSFER);
    }

    @Override
    protected MinecartBatteryBox crateMinecartEntity(Level level, double x, double y, double z) {
        return new MinecartBatteryBox(level, x, y, z);
    }
}
