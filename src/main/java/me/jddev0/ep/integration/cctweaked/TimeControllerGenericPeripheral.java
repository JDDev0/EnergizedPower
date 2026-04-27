package me.jddev0.ep.integration.cctweaked;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.TimeControllerBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Optional;

public class TimeControllerGenericPeripheral implements GenericPeripheral {
    @Override
    public String id() {
        return EPAPI.MOD_ID + ":time_controller_peripheral";
    }

    @LuaFunction(mainThread = true)
    public final boolean setTime(TimeControllerBlockEntity timeController, int time) throws LuaException {
        //TODO dynamically get days timeline
        long ticksPerDay = 24000;

        if(time < 0 || time >= ticksPerDay)
            throw new LuaException("Time must be >= 0 and < " + ticksPerDay);

        if(!(timeController.getLevel() instanceof ServerLevel level) || timeController.getEnergy() < TimeControllerBlockEntity.CAPACITY)
            return false;

        timeController.clearEnergy();

        Holder<DimensionType> dimensionType = level.dimensionTypeRegistration();
        Optional<Holder<WorldClock>> defaultClockOptional = dimensionType.value().defaultClock();
        if(defaultClockOptional.isEmpty()) {
            //TODO send error to player (There is no time in dimension ...)

            return false;
        }

        Holder<WorldClock> defaultClock = defaultClockOptional.get();
        ServerClockManager clockManager = level.clockManager();

        long currentTime = clockManager.getTotalTicks(defaultClock);

        int currentDayTime = (int)(currentTime % ticksPerDay);

        if(currentDayTime <= time)
            clockManager.setTotalTicks(defaultClock, currentTime - currentDayTime + time);
        else
            clockManager.setTotalTicks(defaultClock, currentTime + ticksPerDay - currentDayTime + time);

        return true;
    }
}
