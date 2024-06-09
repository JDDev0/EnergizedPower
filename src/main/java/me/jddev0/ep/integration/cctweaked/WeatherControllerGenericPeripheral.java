package me.jddev0.ep.integration.cctweaked;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.entity.WeatherControllerBlockEntity;
import net.minecraft.server.world.ServerWorld;

public class WeatherControllerGenericPeripheral implements GenericPeripheral {
    @Override
    public String id() {
        return EnergizedPowerMod.MODID + ":weather_controller_peripheral";
    }

    @LuaFunction(mainThread = true)
    public final int getSelectedWeatherType(WeatherControllerBlockEntity weatherController) {
        return weatherController.getSelectedWeatherType();
    }

    @LuaFunction(mainThread = true)
    public final boolean setWeather(WeatherControllerBlockEntity weatherController, int weatherType) throws LuaException {
        if(weatherType != - 1 && weatherType != 0 && weatherType != 1 && weatherType != 2)
            throw new LuaException("Weather type must be one of (-1 for reset, 0 for clear, 1 for rain, or 2 for thunder)");

        if(weatherType == -1 && !weatherController.hasInfiniteWeatherChangedDuration())
            throw new LuaException("Weather type -1 (reset) can only be used if an infinite duration upgrade module is installed");

        if(!(weatherController.getWorld() instanceof ServerWorld level) || !weatherController.hasEnoughEnergy() ||
                weatherController.getSelectedWeatherType() == weatherType)
            return false;

        if(weatherController.hasInfiniteWeatherChangedDuration()) {
            weatherController.setSelectedWeatherType(weatherType);

            if(weatherType == -1)
                return true;
        }else {
            weatherController.clearEnergy();
        }

        int duration = weatherController.getWeatherChangedDuration();

        switch(weatherType) {
            //Clear
            case 0 -> level.setWeather(duration, 0, false, false);
            //Rain
            case 1 -> level.setWeather(0, duration, true, false);
            //Thunder
            case 2 -> level.setWeather(0, duration, true, true);
        }

        return true;
    }
}
