package me.jddev0.ep.integration.cctweaked;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.GenericPeripheral;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.WeatherControllerBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

public class WeatherControllerGenericPeripheral implements GenericPeripheral {
    @Override
    @NotNull
    public ResourceLocation id() {
        return EPAPI.id("weather_controller_peripheral");
    }

    @LuaFunction(mainThread = true)
    public static int getSelectedWeatherType(WeatherControllerBlockEntity weatherController) {
        return weatherController.getSelectedWeatherType();
    }

    @LuaFunction(mainThread = true)
    public static boolean setWeather(WeatherControllerBlockEntity weatherController, int weatherType) throws LuaException {
        if(weatherType != - 1 && weatherType != 0 && weatherType != 1 && weatherType != 2)
            throw new LuaException("Weather type must be one of (-1 for reset, 0 for clear, 1 for rain, or 2 for thunder)");

        if(weatherType == -1 && !weatherController.hasInfiniteWeatherChangedDuration())
            throw new LuaException("Weather type -1 (reset) can only be used if an infinite duration upgrade module is installed");

        if(!(weatherController.getLevel() instanceof ServerLevel level) || !weatherController.hasEnoughEnergy() ||
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
            case 0 -> level.setWeatherParameters(duration, 0, false, false);
            //Rain
            case 1 -> level.setWeatherParameters(0, duration, true, false);
            //Thunder
            case 2 -> level.setWeatherParameters(0, duration, true, true);
        }

        return true;
    }
}
