package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.occupations.Occupation;
import pw.octane.practice.profiles.Profile;

public class PlayerDeathListener implements Listener {

    private PracticeModule module;
    public PlayerDeathListener(PracticeModule plugin) {
        this.module = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity();
        Profile profile = module.getProfileManager().get(player.getUniqueId());
        Occupation occupation = profile.getOccupation();
        if(occupation != null && occupation.getAlivePlayers().contains(player)) {
            occupation.eliminate(player);
            player.spigot().respawn();
            player.teleport(player.getLocation());
            event.getDrops().clear();
        }
    }
}
