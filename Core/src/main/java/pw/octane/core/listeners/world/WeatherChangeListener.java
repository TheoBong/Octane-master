package pw.octane.core.listeners.world;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import pw.octane.core.CoreModule;

public class WeatherChangeListener implements Listener {

    private CoreModule module;
    public WeatherChangeListener(CoreModule module) {
        this.module = module;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
    }
}
