package pw.octane.practice.listeners.bukkit.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import pw.octane.practice.PracticeModule;
import pw.octane.practice.profiles.Profile;

import java.util.UUID;

public class AsyncPlayerPreLoginListener implements Listener {

    private PracticeModule module;
    public AsyncPlayerPreLoginListener(PracticeModule module) {
        this.module = module;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        Profile profile = module.getProfileManager().find(uuid, true);
        if(profile == null) {
            profile = module.getProfileManager().createProfile(uuid);
        }
    }
}
