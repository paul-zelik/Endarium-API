package net.endarium.api.minecraft.listeners;

import net.endarium.api.games.GameStatus;
import net.endarium.api.games.servers.CrystaliserAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (CrystaliserAPI.getEndaServer().isGameServer()) {
            if ((GameStatus.isStatus(GameStatus.LOBBY)))
                event.setCancelled(true);
        }
    }
}