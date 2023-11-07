package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pw.octane.manager.utils.Colors;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.profiles.Profile;

import java.util.List;

public class PlayerJoinListener implements Listener {

    private PracticeModule module;
    public PlayerJoinListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = module.getProfileManager().get(player.getUniqueId());

        module.getProfileManager().playerUpdateVisibility();

        profile.setName(player.getName());
        profile.playerUpdate();

        List<String> list = module.getConfig().getStringList("messages.join");
        for(String s : list) {
            player.sendMessage(Colors.get(s));
        }
    }
}
